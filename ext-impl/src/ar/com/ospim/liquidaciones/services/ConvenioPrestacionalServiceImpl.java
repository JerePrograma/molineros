package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.prestadores.NoSuchConvenioPrestacionalEntryException;
import ar.com.ospim.prestadores.beans.BusquedaConvenioPrestacionalFiltro;
import ar.com.ospim.prestadores.beans.ConvenioPrestacional;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle.ESTADOS;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * servicio test que nos da acceso a los datos de la aplicaci�n (BD).
 * 
 */
public class ConvenioPrestacionalServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ConvenioPrestacionalServiceImpl.class);

	/**
	 * Metodo que obtiene un convenio prestacional a partir de la clave primaria, en caso de
	 * que est� dado de baja o de no encontrarlo retorna null
	 * 
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static ConvenioPrestacional getConvenioPrestacional(int idConvenioPrest) throws SystemException,
			NoSuchConvenioPrestacionalEntryException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ConvenioPrestacional convenioPrestacional = null;

		try {
			String sql = "{call convenio_prest.buscar_convenio_prestacional_cab(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvenioPrest);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				convenioPrestacional = ConvenioPrestacional.getMapping(rs, "convprest_");
			}
		} catch (Exception e) {
			_log.error("Error al obtener convenio prestacional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return convenioPrestacional;
	}

	/**
	 * Metodo que obtiene la lista de detalles a partir de la clave primaria del
	 * conv.prestacional, en caso de no encontrarla arroja excepci�n
	 * 
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static List<ConvenioPrestacionalDetalle> getConvePrestDetalles(int idConvPrest)
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> detalles = new ArrayList<ConvenioPrestacionalDetalle>();
		try {			
			String sql = "{call convenio_prest.buscar_convenio_prestacional_det(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvPrest);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle cpDet = ConvenioPrestacionalDetalle
						.getMapping(rs, "convprestdet_");
				detalles.add(cpDet);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener detalles convenio", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return detalles;
	}

	/**
	 * actualiza un convenio prest. y sus items en estados ALTA; MODIF; BAJA
	 * 
	 * @throws NoSuchConvenioPrestacionalEntryException
	 * @throws SystemException
	 */
	public void actualizarConvenioPrestacional(ConvenioPrestacional convPrest, String userName) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt2 = null, stmt3 = null, stmt4 = null;
		try {
			String sql = "{call convenio_prest.actualizar_convenio_cab(?,?,?,?,?,?,?,?,?)}";
			String sql2 = "{call convenio_prest.insertar_convenio_det(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql3 = "{call convenio_prest.actualizar_convenio_det(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql4 = "{call convenio_prest.eliminar_convenio_det(?, ?)}";
    
			con = ConnectionHelper.getConnectionForTransaction();
				 
			//*** CONVENIOS PREST. CAB ***//
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, convPrest.getId());
			stmt.setInt(2, convPrest.getPrestador().getId_prestador());
			stmt.setInt(3, convPrest.getEstado().getIntValue());
			stmt.setInt(4, convPrest.getDiaRecepcion());
			stmt.setString(5, convPrest.getCondicionDePago());
			stmt.setInt(6, convPrest.getTipoPago().getId());
			stmt.setDate(7, new java.sql.Date(convPrest.getVigencia().getTime()));
			if(convPrest.getVencimiento()!=null){
				stmt.setDate(8, new java.sql.Date(convPrest.getVencimiento().getTime()));
			}else{
				stmt.setNull(8, Types.DATE);
			}
			stmt.setString(9, userName);
			
			stmt.executeUpdate();
			
			//*** CONVENIOS PREST. DETALLES ***//
			for (Iterator<ConvenioPrestacionalDetalle> iterator = convPrest.getConvenioPrestDetalle().iterator(); iterator.hasNext();) {
				ConvenioPrestacionalDetalle cpDet = iterator.next();
				
				if(cpDet.getEstado() != null && cpDet.getEstado().equals(ESTADOS.NUEVO)){
					
					stmt2 = con.prepareCall(sql2.toString());
					stmt2.setInt(1, convPrest.getId());
					if (null != cpDet.getFechaDesde()) {
						stmt2.setDate(2, new java.sql.Date(cpDet.getFechaDesde().getTime()));
					} else {
						stmt2.setNull(2, Types.DATE);
					}
					if (null != cpDet.getFechaHasta()) {
						stmt2.setDate(3, new java.sql.Date(cpDet.getFechaHasta().getTime()));
					} else {
						stmt2.setNull(3, Types.DATE);
					}	
					stmt2.setInt(4, cpDet.getTipoNomenclador().getId_tipo_nomenclador());
					stmt2.setInt(5, cpDet.getPrestacionDesde().getId());
					stmt2.setString(6, cpDet.getCodigoDesde());
					stmt2.setInt(7, cpDet.getPrestacionHasta().getId());
					stmt2.setString(8, cpDet.getCodigoHasta());	
					stmt2.setInt(9, cpDet.getIdPlan());
					stmt2.setBigDecimal(10, cpDet.getCoseguro());
					stmt2.setString(11, cpDet.getTipoValorizacion());
					stmt2.setBigDecimal(12, cpDet.getImporte());
					stmt2.setBigDecimal(13, cpDet.getPorcentaje());
					stmt2.setString(14, cpDet.getServicio());
					stmt2.setString(15, userName);

					stmt2.executeUpdate();
					
				}
				
				if(cpDet.getEstado() != null && cpDet.getEstado().equals(ESTADOS.MODIF)){
 
					stmt3 = con.prepareCall(sql3.toString());
					stmt3.setInt(1, cpDet.getId());
					if (null != cpDet.getFechaDesde()) {
						stmt3.setDate(2, new java.sql.Date(cpDet.getFechaDesde().getTime()));
					} else {
						stmt3.setNull(2, Types.DATE);
					}
					if (null != cpDet.getFechaHasta()) {
						stmt3.setDate(3, new java.sql.Date(cpDet.getFechaHasta().getTime()));
					} else {
						stmt3.setNull(3, Types.DATE);
					}	
					stmt3.setInt(4, cpDet.getTipoNomenclador().getId_tipo_nomenclador());
					stmt3.setInt(5, cpDet.getPrestacionDesde().getId());
					stmt3.setString(6, cpDet.getCodigoDesde());
					stmt3.setInt(7, cpDet.getPrestacionHasta().getId());
					stmt3.setString(8, cpDet.getCodigoHasta());	
					stmt3.setInt(9, cpDet.getIdPlan());
					stmt3.setBigDecimal(10, cpDet.getCoseguro());
					stmt3.setString(11, cpDet.getTipoValorizacion());
					stmt3.setBigDecimal(12, cpDet.getImporte());
					stmt3.setBigDecimal(13, cpDet.getPorcentaje());
					stmt3.setString(14, cpDet.getServicio());
					stmt3.setString(15, userName);

					stmt3.executeUpdate();
					
				}
				
				if(cpDet.getEstado() != null && cpDet.getEstado().equals(ESTADOS.BAJA)){
					 
					stmt4 = con.prepareCall(sql4.toString());
					stmt4.setInt(1, cpDet.getId());
					stmt4.setString(2, userName);

					stmt4.executeUpdate();
				}	
				
			}
			con.commit();
			
		} catch (SQLException e) {
			_log.error("Error al actualizar convenio prest. cab", e);
			ConnectionHelper.rollback(con);
		} catch (Exception e) {
			_log.error("Error al actualizar convenio prest.", e);
			ConnectionHelper.rollback(con);
			throw new SystemException(e);
			
		} finally {
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt4);
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo que aplica borrado l�gico de un convenio prestacional a partir de la clave
	 * primaria, no borra el reintegro convenio prestacional, solo lo da de baja
	 * 
	 * @throws NoSuchConvenioPrestacionalEntryException
	 * @throws SystemException
	 */
	public void eliminarConvenioPrestacional(int idconvenioPrest, String userName)
			throws NoSuchConvenioPrestacionalEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call convenio_prest.eliminar_convenio_prestacional(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idconvenioPrest);
			stmt.setString(2, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al dar de baja el convenio prestacional", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchConvenioPrestacionalEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al dar de baja el convenio prestacional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	public List<ConvenioPrestacional> buscarConveniosPrestacionales(BusquedaConvenioPrestacionalFiltro filtro) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacional> listaCoveniosPrest = new ArrayList<ConvenioPrestacional>();
		try {
			String sql = "{call convenio_prest.buscar_convenios_prestacionales_cab(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getIdPrestador()==null){
				stmt.setNull(1, Types.INTEGER);
			}else{
				stmt.setInt(1, filtro.getIdPrestador());
			}
			stmt.setString(2, filtro.getCuit());
			stmt.setString(3, filtro.getDescripcion());
			stmt.setInt(4, filtro.getEstado());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ConvenioPrestacional convPrest = ConvenioPrestacional.getMapping(rs, "convprest_");
				listaCoveniosPrest.add(convPrest);
				// ContratoDetalle contratoDetalle =
				// ContratoDetalle.getMapping(rs, "cd_");
				// int indexOf = listaContratos.indexOf(contrato);
				// if (indexOf == -1) {
				// listaContratos.add(contrato);
				// } else {
				// contrato = listaContratos.get(indexOf);
				// }
				// List<ContratoDetalle> listaContratoDetalle = contrato
				// .getContratoDetalle();
				// if (listaContratoDetalle == null) {
				// listaContratoDetalle = new ArrayList<ContratoDetalle>();
				// }
				// listaContratoDetalle.add(contratoDetalle);
				// contrato.setDetalleContrato(listaContratoDetalle);
			}
		} catch (Exception e) {
			_log.error("Error al traer convenios prestacionales", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCoveniosPrest;
	}

	/**
	 * Metodo que actualiza un reintegro, le cambia el estado a un estado dado,
	 * como estado auditado
	 * 
	 * @throws NoSuchContratoPrestacionEntryException
	 * @throws SystemException
	 */
	public void cambiarEstadoConvenioPrestacional(int idConvenioPrest, int estado,
			String userName) throws NoSuchConvenioPrestacionalEntryException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call convenio_prest.cambio_estado_convenio_prest(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvenioPrest);
			stmt.setInt(2, estado);
			stmt.setString(3, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchConvenioPrestacionalEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}
	
	public List<ConvenioPrestacionalDetalle> validarDetalleExistente(int id_contrato, Date fechaDesde, Date fechaHasta, 
			String codigo_desde, String codigo_hasta, int plan, String servicio) throws SystemException{
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> contratoDetalle = new ArrayList<ConvenioPrestacionalDetalle>();
		try {
			String sql = "{call busca_contrato_detalle_existente(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_contrato);

			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
								
			stmt.setString(4, codigo_desde);			
			stmt.setString(5, codigo_hasta);			

			stmt.setInt(6, plan);
			stmt.setString(7, servicio);	
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle contratoDetalleItem = ConvenioPrestacionalDetalle
						.getMapping(rs, "cd_");
				contratoDetalle.add(contratoDetalleItem);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener detalle contrato", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return contratoDetalle;
	}
	
	public int insertarConvenioPrestacional(ConvenioPrestacional convPrest, String screenName) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt2 = null;
		
		int idConvenioPrest=0;
		
		try {
			
			String sql  = "{call convenio_prest.insertar_convenio_cab(?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql2 = "{call convenio_prest.insertar_convenio_det(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			
			con = ConnectionHelper.getConnectionForTransaction();
			
//			*** CONVENIO PREST CAB ***
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, convPrest.getPrestador().getId_prestador());
			stmt.setInt(2,convPrest.getEstado().getIntValue());
			stmt.setInt(3, convPrest.getDiaRecepcion());
			stmt.setString(4, convPrest.getCondicionDePago());
			stmt.setInt(5, convPrest.getTipoPago().getId());
			if (null != convPrest.getVigencia()) {
				stmt.setDate(6, new java.sql.Date(convPrest.getVigencia().getTime()));
			} else {
				stmt.setNull(6, Types.DATE);
			}	
			if (null != convPrest.getVencimiento()) {
				stmt.setDate(7, new java.sql.Date(convPrest.getVencimiento().getTime()));
			} else {
				stmt.setNull(7, Types.DATE);
			}
			stmt.setString(8, screenName);

			//me devuelve el id del convenio prest.
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idConvenioPrest=rs.getInt(1);
			}	
			
//			*** CONVENIO PREST DET ***	
			if(convPrest.getConvenioPrestDetalle()!=null && convPrest.getConvenioPrestDetalle().size() > 0){
				for (ConvenioPrestacionalDetalle cpDet : convPrest.getConvenioPrestDetalle()) {
					stmt2 = con.prepareCall(sql2.toString());
					stmt2.setInt(1, idConvenioPrest);
					if (null != cpDet.getFechaDesde()) {
						stmt2.setDate(2, new java.sql.Date(cpDet.getFechaDesde().getTime()));
					} else {
						stmt2.setNull(2, Types.DATE);
					}
					if (null != cpDet.getFechaHasta()) {
						stmt2.setDate(3, new java.sql.Date(cpDet.getFechaHasta().getTime()));
					} else {
						stmt2.setNull(3, Types.DATE);
					}	
					stmt2.setInt(4, cpDet.getTipoNomenclador().getId_tipo_nomenclador());
					stmt2.setInt(5, cpDet.getPrestacionDesde().getId());
					stmt2.setString(6, cpDet.getCodigoDesde());
					stmt2.setInt(7, cpDet.getPrestacionHasta().getId());
					stmt2.setString(8, cpDet.getCodigoHasta());	
					stmt2.setInt(9, cpDet.getIdPlan());
					stmt2.setBigDecimal(10, cpDet.getCoseguro());
					stmt2.setString(11, cpDet.getTipoValorizacion());
					stmt2.setBigDecimal(12, cpDet.getImporte());
					stmt2.setBigDecimal(13, cpDet.getPorcentaje());
					stmt2.setString(14, cpDet.getServicio());
					stmt2.setString(15, screenName);

					stmt2.executeUpdate();
				}
			}	
			
			con.commit();
		} catch (SQLException e) {
			_log.error("Error al insertar convenio prestacional", e);
			ConnectionHelper.rollback(con);
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt2, con);
		}
		return idConvenioPrest;
			
	}		
	
	/**
	 * Metodo que obtiene la lista de prestaciones por detalle del c�digo a partir de la clave primaria del
	 * conv.prestacional, en caso de no encontrarla arroja excepci�n
	 * 
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public List<ConvenioPrestacionalDetalle> getPrestacionesDetallesPorCodigo(int idConvPrest)
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> detalles = new ArrayList<ConvenioPrestacionalDetalle>();
		try {			
			String sql = "{call convenio_prest.buscar_convenio_prestacional_det_desglosado(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idConvPrest);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle convPrestDet = ConvenioPrestacionalDetalle
						.getMapping(rs, "convprestdet_");
				convPrestDet.setCodigoDesde(convPrestDet.getCodigoDesde()+"-"+convPrestDet.getPrestacionDesde().getDescripcion());
				detalles.add(convPrestDet);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones detalle por c�digo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return detalles;
	}
	
	/**
	 * Metodo que obtiene un convenio prestacional de un prestador, en caso de
	 * que est� dado de baja o de no encontrarlo retorna null
	 * 
	 * @throws SystemException
	 * @throws NoSuchConvenioPrestacionalEntryException
	 */
	public static ConvenioPrestacional getConvenioPrestacionalPorPrestador(int idPrestador) throws SystemException,
			NoSuchConvenioPrestacionalEntryException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ConvenioPrestacional convenioPrestacional = null;

		try {
			String sql = "{call convenio_prest.buscar_convenio_prestacional_cab_por_prestador(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestador);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				convenioPrestacional = ConvenioPrestacional.getMapping(rs, "convprest_");
			}
		} catch (Exception e) {
			_log.error("Error al obtener convenio prestacional por prestador", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return convenioPrestacional;
	}

	public List<ConvenioPrestacionalDetalle> detalleValorizarTratamiento(int id_prestador, Date fechaDesde, Date fechaHasta, String 	codigo,int plan) throws SystemException{
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> contratoDetalle = new ArrayList<ConvenioPrestacionalDetalle>();
		try {
			String sql = "{call convenio_prest.busca_contrato_detalle_existente_para_valorizar_tratamiento(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestador);

			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
								
			stmt.setString(4, codigo);
			if(plan!=0){
			  stmt.setInt(5, plan);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle cpDetalle = ConvenioPrestacionalDetalle
						.getMapping(rs, "cd_");
				contratoDetalle.add(cpDetalle);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener detalle convenio prestacional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return contratoDetalle;
	}
	
    public List<ConvenioPrestacionalDetalle> detalleValorizarTratamientoV01(int id_prestador, Date fechaDesde, Date fechaHasta, String 	codigo,int plan) throws SystemException{
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ConvenioPrestacionalDetalle> contratoDetalle = new ArrayList<ConvenioPrestacionalDetalle>();
		try {
			String sql = "{call convenio_prest.busca_contrato_detalle_existente_para_valorizar_tratamiento_v01(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestador);

			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
								
			stmt.setString(4, codigo);
			if(plan!=0){
			  stmt.setInt(5, plan);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ConvenioPrestacionalDetalle cpDetalle = ConvenioPrestacionalDetalle
						.getMapping(rs, "convprestdet_");
				contratoDetalle.add(cpDetalle);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener detalle convenio prestacional", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return contratoDetalle;
	}
}	
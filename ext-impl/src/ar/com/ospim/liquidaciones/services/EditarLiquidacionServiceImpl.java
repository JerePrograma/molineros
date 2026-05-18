package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.DuplicateLiquidacionIdException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionEntryException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionPrestacionEntryException;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.PlanPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * servicio test que nos da acceso a los datos de la aplicación (BD).
 * 
 */
public class EditarLiquidacionServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(EditarLiquidacionServiceImpl.class);

	/**
	 * Metodo que obtiene una liquidacion a partir de la clave primaria, en caso
	 * de que está dado de baja o de no encontrarlo retorna null
	 * 
	 * @throws SystemException
	 * @throws NoSuchLiquidacionEntryException
	 */
	public Liquidacion getLiquidacionEntry(int id_liquidacion)
			throws SystemException, NoSuchLiquidacionEntryException {
		Connection con = null;
		CallableStatement stmt = null;
		Liquidacion liquidacion = null;
		PrestadorLugarAtencion pla = null;
		Prestador prestador = null;
		try {
			String sql = "{call busca_liquidacion_header_por_id(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				liquidacion = Liquidacion.getMapping(rs, "l_");
				pla = PrestadorLugarAtencion.getMappingSimple(rs, "pla_");
				prestador = Prestador.getMapping(rs, "pd_");
				prestador.setId_prestador(liquidacion.getId_prestador());
				pla.setPrestador(prestador);
				liquidacion.setPrestador_lugar_atencion(pla);
				liquidacion.setId_domicilio(pla.getId_domicilio());
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return liquidacion;
	}

	/**
	 * Metodo que obtiene la lista de prestaciones a partir de la clave primaria
	 * de la liquidacion, en caso de no encontrarla arroja excepción
	 * 
	 * @throws SystemException
	 * @throws NoSuchLiquidacionEntryException
	 */
	public List<LiquidacionPrestacion> getPrestacionesLiquidacionEntry(
			int id_liquidacion) throws SystemException,
			NoSuchLiquidacionEntryException {
		Connection con = null;
		CallableStatement stmt = null;

		Prestacion prestacion = null;
		Domicilio afiDomicilio = null;
		Afiliado afiliado = null;
		Seccional seccional = null;
		List<LiquidacionPrestacion> liquidacionPrestaciones = new ArrayList<LiquidacionPrestacion>();
		try {
			String sql = "{call busca_prestaciones_liquidacion_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				LiquidacionPrestacion liquidacionPrestacion = LiquidacionPrestacion
						.getMapping(rs, "lp_");
				afiliado = Afiliado.getMapping(rs, "a_");
				afiDomicilio = Domicilio.getMappingAfiDomicilio(rs, "ad_");
				afiliado.setDomicilioDefault(afiDomicilio);
				seccional = Seccional.getMappingSeccionalParaReintegros(rs,
						"s_");
				afiliado.setSeccional(seccional);
				liquidacionPrestacion.setAfiliado(afiliado);
				liquidacionPrestacion.setCuil_titular(afiliado
						.getCuil_titular());
				liquidacionPrestacion.setInte(afiliado.getInte());
				prestacion = Prestacion.getMapping(rs, "n_");
				liquidacionPrestacion.setPrestacion(prestacion);
				liquidacionPrestaciones.add(liquidacionPrestacion);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return liquidacionPrestaciones;
	}

	/**
	 * metodo que carga un nuevo afiliado a partir de los parámetros")); si no
	 * lo puede insertar retorna null
	 * 
	 * @throws DuplicateLiquidacionIdException
	 * @throws SystemException
	 * @throws DuplicateLiquidacionIdException
	 */

	public int cargaLiquidacionEntry(Date fecha, Date fechaE, Date fechaR,
			Date fechaV, Date periodo, String entidad, int id_prestador,
			int id_domicilio, String compro_a_debitar_tipo,
			String compro_a_debitar_letra, int sucu,
			String compro_a_debitar_numero, String tipo_liquidacion,
			int estado, String userName, Date dado_baja, String usr_baja,
			BigDecimal importe, BigDecimal debitado, String nroOC, 
			String observaciones, String tercerizado, String cuit_prestador ,
			BigDecimal cargoOspim, BigDecimal cargoPS, BigDecimal cargoOmint,
			BigDecimal cargoEnSalud,BigDecimal cargoCemic,BigDecimal cargoImesa,BigDecimal cargoCes) throws SystemException,
			DuplicateLiquidacionIdException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_liquidacion = 0;
		try {
			String sql = "{call inserta_liquidacion (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestador);
			stmt.setInt(2, id_domicilio);
			stmt.setDate(3, new java.sql.Date(fecha.getTime()));
			stmt.setDate(4, new java.sql.Date(periodo.getTime()));
			stmt.setInt(5, estado);
			stmt.setString(6, entidad);
			stmt.setString(7, compro_a_debitar_tipo);
			stmt.setString(8, compro_a_debitar_letra);
			stmt.setInt(9, sucu);
			stmt.setString(10, compro_a_debitar_numero);
			stmt.setDate(11, new java.sql.Date(fechaE.getTime()));
			stmt.setDate(12, new java.sql.Date(fechaR.getTime()));
			stmt.setDate(13, new java.sql.Date(fechaV.getTime()));
			stmt.setString(14, userName);
			stmt.setString(15, tipo_liquidacion);
			stmt.setBigDecimal(16, importe);
			stmt.setBigDecimal(17, debitado);
			stmt.setInt(18, StringUtils.checkEmpty(nroOC)?0:Integer.parseInt(nroOC) );
			stmt.setString(19, observaciones);
			stmt.setString(20, tercerizado);
			stmt.setString(21, cuit_prestador);
			stmt.setBigDecimal(22, cargoOspim);
			stmt.setBigDecimal(23, cargoPS);
			stmt.setBigDecimal(24, cargoOmint);
			stmt.setBigDecimal(25, cargoEnSalud);
			stmt.setBigDecimal(26, cargoCemic);
			stmt.setBigDecimal(27, cargoImesa);
			stmt.setBigDecimal(28, cargoCes);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_liquidacion = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateLiquidacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_liquidacion;
	}

	/**
	 * metodo que carga una nueva prestacion a partir de los parámetros si no lo
	 * puede insertar retorna null
	 * 
	 * @throws DuplicateLiquidacionIdException
	 * @throws SystemException
	 * @throws DuplicateLiquidacionPrestacionIdException
	 */

	public int cargaLiquidacionPrestacionEntry(int id_liquidacion,
			String cuil_titular, int inte, int id_prestacion,
			Date prestacionFecha, BigDecimal cantidad, BigDecimal importe,
			String servicio, BigDecimal solicitado, BigDecimal debitado,
			BigDecimal resultado, String tercerizado, String usuario,
			Date periodo, int motivoAltaDiscapacidad  , int[] idLiquidacionPrestacion , BigDecimal cargoOspim, BigDecimal cargoPrestadora,
			BigDecimal cargoImesa, String idTercerizadora) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int orden = 1;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call inserta_prestacion (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			stmt.setString(2, cuil_titular);
			stmt.setInt(3, inte);
			stmt.setInt(4, id_prestacion);
			stmt.setDate(5, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setBigDecimal(6, cantidad);
			stmt.setBigDecimal(7, importe);
			stmt.setString(8, servicio);
			stmt.setBigDecimal(9, solicitado);
			stmt.setBigDecimal(10, debitado);
			stmt.setBigDecimal(11, resultado);
			stmt.setString(12, tercerizado);
			stmt.setString(13, usuario);
			stmt.setDate(14, new java.sql.Date(periodo.getTime()));
			stmt.setInt(15, motivoAltaDiscapacidad);
			stmt.setBigDecimal(16, cargoOspim);
			stmt.setBigDecimal(17, cargoPrestadora);
			stmt.setBigDecimal(18, cargoImesa);
			if (!StringUtils.checkEmpty(idTercerizadora)) {
			    stmt.setString(19, idTercerizadora);
			} else {
			    stmt.setNull(19, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				orden = rs.getInt(1);
				idLiquidacionPrestacion[0] = rs.getInt(2);
			}
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return orden;
	}

	
	
	public void grabaDatosDelReclamoPrestacionaldelaLiquidacion(int id_liquidacion,int id_prestacion,int idReclamoPrestacional 
			,int idPrestacionReclamo ,	String userName ) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call inserta_datos_reclamo_liquidacion (?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			stmt.setInt(2, id_prestacion);
			stmt.setInt(3, idReclamoPrestacional);
			stmt.setInt(4, idPrestacionReclamo);
			stmt.setString(5, userName);
			stmt.executeQuery();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ;
	}


	
	/**
	 * metodo que actualiza un afiliado a partir de los parámetros , si no lo
	 * puede actualizar retorna null
	 * 
	 * @throws NoSuchLiquidacionEntryException
	 * @throws SystemException
	 */

	public void actualizaLiquidacionEntry(int id_liquidacion, Date fecha,
			Date fechaE, Date fechaR, Date fechaV, Date periodo,
			int id_prestador, int id_domicilio, String compro_a_debitar_tipo,
			String letra_compro, int sucu, String compro_a_debitar_numero,
			BigDecimal importe_total, BigDecimal debitado_total, String nroOC,
			String observaciones, String tercerizado_cab, String userName,
			String cuit_prestador , BigDecimal cargoOspim, BigDecimal cargoPS, 
			BigDecimal cargoOmint, BigDecimal cargoEnSalud,BigDecimal cargoCemic,BigDecimal cargoImesa,BigDecimal cargoCes) throws NoSuchLiquidacionEntryException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_liquidacion_fecha (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setDate(3, new java.sql.Date(periodo.getTime()));
			stmt.setInt(4, id_prestador);
			stmt.setInt(5, id_domicilio);
			stmt.setString(6, compro_a_debitar_tipo);
			stmt.setString(7, letra_compro);
			stmt.setInt(8, sucu);
			stmt.setString(9, compro_a_debitar_numero);
			stmt.setDate(10, new java.sql.Date(fechaE.getTime()));
			stmt.setDate(11, new java.sql.Date(fechaR.getTime()));
			stmt.setDate(12, new java.sql.Date(fechaV.getTime()));
			stmt.setString(13, userName);
			stmt.setBigDecimal(14, importe_total);
			stmt.setBigDecimal(15, debitado_total);
			stmt.setInt(16, StringUtils.checkEmpty(nroOC)?0:Integer.parseInt(nroOC) );
			stmt.setString(17, observaciones);
			stmt.setString(18, tercerizado_cab);
			stmt.setString(19, cuit_prestador);
			stmt.setBigDecimal(20, cargoOspim);
			stmt.setBigDecimal(21, cargoPS);
			stmt.setBigDecimal(22, cargoOmint);
			stmt.setBigDecimal(23, cargoEnSalud);
			stmt.setBigDecimal(24, cargoCemic);
			stmt.setBigDecimal(25, cargoImesa);
			stmt.setBigDecimal(26, cargoCes);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchLiquidacionEntryException(e);
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

	/**
	 * Metodo que aplica borrado lógico de un liquidacion a partir de la clave
	 * primaria, no borra el liquidacion físicamente, solo lo da de baja
	 * 
	 * @throws NoSuchLiquidacionEntryException
	 * @throws SystemException
	 */
	public void borraLiquidacionEntry(int id_liquidacion, String userName)
			throws NoSuchLiquidacionEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_liquidacion(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			stmt.setString(2, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchLiquidacionEntryException(e);
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

	/**
	 * Metodo que aplica borrado lógico de un liquidacion prestacion a partir de
	 * la clave primaria, borra la prestacion físicamente
	 * 
	 * @throws NoSuchLiquidacionPrestacionEntryException
	 * @throws SystemException
	 */
	public void borraLiquidacionPrestacionEntry(int id_liquidacion, int orden,
			String userName) throws NoSuchLiquidacionPrestacionEntryException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_liquidacion_prestacion(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			stmt.setInt(2, orden);
			stmt.setString(3, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchLiquidacionPrestacionEntryException(e);
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

	/**
	 * Metodo que cierra una liquedacíon, le cambia el estado a estado cerrado
	 * 
	 * @throws NoSuchLiquidacionPrestacionEntryException
	 * @throws SystemException
	 */
	public void cambiarEstadoLiquidacionEntry(int id_liquidacion, int estado,
			String userName) throws NoSuchLiquidacionEntryException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call cambio_estado_liquidacion(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			stmt.setInt(2, estado);
			stmt.setString(3, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchLiquidacionEntryException(e);
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

	public void actualizaLiquidacionPrestacionEntry(int numero, int orden,
			Date prestacionFecha, String servicio, String cuil_titular,
			int inte, int id_prestacion, BigDecimal cantidad, BigDecimal importe,
			String tercerizado, String userName, Date periodoPrestacion, int motivoAltaDiscapacidad, String idTercerizadora)
			throws NoSuchLiquidacionPrestacionEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_prestacion_liquidacion (?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setInt(2, orden);
			stmt.setDate(3, new java.sql.Date(prestacionFecha.getTime()));
			stmt.setString(4, servicio);
			stmt.setString(5, cuil_titular);
			stmt.setInt(6, inte);
			stmt.setInt(7, id_prestacion);
			stmt.setBigDecimal(8, cantidad);
			stmt.setBigDecimal(9, importe);
			stmt.setString(10, tercerizado);
			stmt.setString(11, userName);
			stmt.setDate(12, new java.sql.Date(periodoPrestacion.getTime()));
			stmt.setInt(13, motivoAltaDiscapacidad);
			if (!StringUtils.checkEmpty(idTercerizadora)) {
			    stmt.setString(14, idTercerizadora);
			} else {
			    stmt.setNull(14, Types.VARCHAR);
			}
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar prestacion para liquidacion", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new NoSuchLiquidacionPrestacionEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al cargar prestacion para liquidacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}
	
	public Liquidacion traeResumenOP(Liquidacion liquidacion, int id_liquidacion) {
		Connection con = null;
		CallableStatement stmt = null;		
		try {
			String sql = "{call buscar_resumen_op_liquidacion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
			    liquidacion.setIdOP(rs.getInt("l__id_orden_pago")); 
			    liquidacion.setChequeOP(rs.getBigDecimal("l__nro_cheque") != null ? rs.getBigDecimal("l__nro_cheque").toBigInteger() : null); 
			    liquidacion.setFechaOP(rs.getDate("l__fecha_op"));									    						    
			}
		} catch (Exception e) {
			_log.error("Error al traer los datos de la OP para la liquidacion ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return liquidacion;
	}

	public List<LiquidacionPrestacion> getComprobantesLiquidaciones(
			int idPrestacion,String comproTipo,String comproLetra,String comproNro,Integer idPrestador,String cuilTitular,int inte,Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		PlanPrestacion planPrestacion = null;
		List<LiquidacionPrestacion> liquidacionPrestaciones = new ArrayList<LiquidacionPrestacion>();
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			String sql = "{call busca_prestaciones_liquidacion_por_comprobante(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestacion);
			stmt.setString(2,comproTipo);
			stmt.setString(3,comproLetra);
			stmt.setString(4, comproNro);
			stmt.setInt(5,idPrestador);
			stmt.setString(6,cuilTitular);
			stmt.setInt(7,inte);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				LiquidacionPrestacion liquidacionPrestacion = new LiquidacionPrestacion();
				
				liquidacionPrestacion.setId_prestacion(rs.getInt("id_prestacion"));
				liquidacionPrestacion.setOrden(rs.getInt("orden"));
				Liquidacion liquidacion = new Liquidacion();
				liquidacion.setCompro_a_debitar_letra(rs.getString("compro_letra"));
				liquidacion.setCompro_a_debitar_numero(rs.getString("compro_nro"));
				liquidacion.setCompro_a_debitar_tipo(rs.getString("compro_tipo"));
				liquidacion.setId_prestador(rs.getInt("id_prestador"));
				liquidacion.setId_liquidacion(rs.getInt("id_liquidacion"));
				liquidacionPrestacion.setLiquidacion(liquidacion);
				
				liquidacionPrestacion.setCuil_titular(rs.getString("cuil_titular"));
				liquidacionPrestacion.setInte(rs.getInt("inte"));
				liquidacionPrestacion.setPeriodo(rs.getDate("periodo"));
				liquidacionPrestacion.setFecha_prestacion(rs.getDate("fecha_prestacion"));
				
				liquidacionPrestaciones.add(liquidacionPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener liquidacion prestacion", e);
			throw new SystemException(e);
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return liquidacionPrestaciones;
	}	
	
	public void actualizaCargosTotal(int id_liquidacion,BigDecimal cargoOspim, BigDecimal cargoPrestadora, 
			BigDecimal cargoImesa) throws NoSuchLiquidacionEntryException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_liquidacion_cargos_total (?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			stmt.setBigDecimal(2, cargoOspim);
			stmt.setBigDecimal(3, cargoPrestadora);
			stmt.setBigDecimal(4, cargoImesa);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.debug(e.getMessage());
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchLiquidacionEntryException(e);
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

	
}
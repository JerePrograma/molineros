package ar.com.ospim.farmacia.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.farmacia.beans.ReporteOrdenPagoReintegrosFarmacia;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.AfiliadoSinPlanException;
import ar.com.ospim.liquidaciones.DuplicateReintegroIdException;
import ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.NoSuchReintegroPrestacionEntryException;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

public class ReintegroFarmaciaServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(ReintegroFarmaciaServiceImpl.class);
	
	/**
	 * Metodo que obtiene un reintegro de farmacia a partir de la clave primaria, en caso de
	 * que está dado de baja o de no encontrarlo retorna null
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public ReintegroMedicamento getReintegroEntry(int id_reintegro)
	throws SystemException, NoSuchReintegroEntryException {
		Connection con = null;
		CallableStatement stmt = null;
		ReintegroMedicamento reintegro = null;
		Domicilio afiDomicilio = null;
		Afiliado afiliado = null;
		Seccional seccional = null;

		try {
			String sql = "{call busca_reintegro_farmacia_header_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {				
				reintegro = new ReintegroMedicamento();
				reintegro = ReintegroMedicamento.getMapping(rs, "r_");
				afiliado = Afiliado.getMapping(rs, "a_");
				afiDomicilio = Domicilio.getMappingAfiDomicilio(rs, "ad_");				
				afiliado.setDomicilioDefault(afiDomicilio);
				seccional = Seccional.getMappingSeccionalParaReintegros(rs,
						"s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);
			}
		} catch (Exception e) {
			_log.error("Error al obtener reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegro;
	}	
	
	/**
	 * Metodo que obtiene la lista de medicamentos a partir de la clave primaria
	 * del reintegro, en caso de no encontrarla arroja excepción
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public List<ReintegroMedicamentoItem> getMedicamentosReintegroEntry(
			int id_reintegro) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Medicamento medicamento = null;		
		List<ReintegroMedicamentoItem> reintegroItems = new ArrayList<ReintegroMedicamentoItem>();
		try {
			String sql = "{call busca_medicamentos_reintegro_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ReintegroMedicamentoItem reintegroPrestacion = ReintegroMedicamentoItem
						.getMapping(rs, "rp_");				
				medicamento = Medicamento.getMapping(rs, "m_");
				reintegroPrestacion.setMedicamento(medicamento);
				reintegroItems.add(reintegroPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegroItems;
	}
	
	public List<ReintegroMedicamento> buscarReintegros(String entidad, Date fechaDesde,
			Date fechaHasta, Date periodoDesde, Date periodoHasta,
			String codPrestad, int nroAfi,
			int inte, String cuil_titular, int seccional, int numero, int estado, String alta_usr,
			int id_medicamento, int receta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReintegroMedicamento> listaReintegros = new ArrayList<ReintegroMedicamento>();
		try {
			String sql = "{call buscar_reintegros_farmacia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numero);
			stmt.setDate(2, fechaDesde == null ? null : new java.sql.Date(
					fechaDesde.getTime()));
			stmt.setDate(3, fechaHasta == null ? null : new java.sql.Date(
					fechaHasta.getTime()));
			stmt.setDate(4, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(5, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));
			stmt.setInt(6, seccional);
			stmt.setString(7, codPrestad);
			stmt.setString(8, entidad);
			if (nroAfi == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, nroAfi);
			}
			if (inte == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, inte);
			}
			stmt.setString(11, cuil_titular);	
			stmt.setString(12, alta_usr);
			stmt.setInt(13, estado);
			stmt.setInt(14, id_medicamento);
			stmt.setInt(15, receta);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroMedicamento reintegro = ReintegroMedicamento.getMapping(rs, "r_");
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Domicilio afiDomicilio = Domicilio.getMappingAfiDomicilio(rs, "ad_");
				afiliado.setDomicilioDefault(afiDomicilio);
				Seccional secc = Seccional.getMappingSeccionalParaReintegros(rs,"s_");
				reintegro.setSeccional(secc);
				reintegro.setAfiliado(afiliado);
				

				reintegro.setCbu(rs.getString("rp__cbu") != null ? rs.getString("rp__cbu") : "");
				reintegro.setCuilCuenta(rs.getString("rp__cuil_cuenta") != null ? rs.getString("rp__cuil_cuenta") : "");
				reintegro.setEmailCuenta(rs.getString("rp__email_cuenta") != null ? rs.getString("rp__email_cuenta") : "");
				reintegro.setApellidoCuenta(rs.getString("rp__apellido_cuenta") != null ? rs.getString("rp__apellido_cuenta") : "");
				reintegro.setNombreCuenta(rs.getString("rp__nombre_cuenta") != null ? rs.getString("rp__nombre_cuenta") : "");

				
				
				reintegro.setChequeOP(rs.getBigDecimal("r__nro_cheque") != null ? rs.getBigDecimal("r__nro_cheque").toBigInteger(): null);
				reintegro.setFechaOP(rs.getDate("r__fecha_op"));
				reintegro.setBajaFechaOP(rs.getDate("r__baja_fecha_op"));

				reintegro.setIdOP(rs.getInt("r__id_orden_pago"));																
				if (reintegro.getIdOP() == 0 && rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setIdOP(-1); //menos uno significa que esta en lista pero que no ha sido pago aún
				}
				
				if ( rs.getInt("opor_id_lista_reintegro_pago") != 0) {
					reintegro.setId_lista_reintegro(rs.getInt("opor_id_lista_reintegro_pago"));
				}
				
				int indexOf = listaReintegros.indexOf(reintegro);
				if (indexOf == -1) {
					listaReintegros.add(reintegro);
				} else {
					reintegro = listaReintegros.get(indexOf);
				}
				ReintegroMedicamentoItem reintegroPrestacion = ReintegroMedicamentoItem.getMapping(rs, "rp_");
				//el troquel viene en el medicamento
				Medicamento medicamento = Medicamento.getMapping(rs, "m_");
				reintegroPrestacion.setMedicamento(medicamento);
				List<ReintegroMedicamentoItem> listaReintegrosPrest = reintegro.getMedicamentos();				
				if (listaReintegrosPrest == null) {
					listaReintegrosPrest = new ArrayList<ReintegroMedicamentoItem>();
				}
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setMedicamentos(listaReintegrosPrest);
			}
		} catch (Exception e) {
			_log.error("Error al traer reintegros", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaReintegros;
	}
	
	/**
	 * metodo que carga un nuevo afiliado a partir de los parámetros")); si no
	 * lo puede insertar retorna null
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 */
	public int cargaReintegroFarmaciaEntry(Date fecha, Date periodo,
			String cuil_titular, int inte, int seccional,
			ArrayList<ReintegroMedicamentoItem> medicamentos, String userName ,
			String cbu, String cuilCuenta,String emailCuenta , 
			String apellidoCuenta, String nombreCuenta, String idTecerizadora)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_reintegro = 0;		
		int id_prestacionfarmacia =0;
		try {
			String sql = "{call insertar_reintegro_farmacia (?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			stmt.setDate(2, new java.sql.Date(periodo.getTime()));
			stmt.setInt(3, seccional);
			stmt.setString(4, cuil_titular);
			stmt.setInt(5, inte);
			stmt.setString(6, userName);
			stmt.setString(7, cbu);
			stmt.setString(8, cuilCuenta);
			stmt.setString(9, emailCuenta);
			stmt.setString(10, apellidoCuenta);
			stmt.setString(11, nombreCuenta);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_reintegro = rs.getInt(1);
			}
			for (ReintegroMedicamentoItem med : medicamentos) {
				if (med.getMedicamento().getNombre().equals("TOTAL") || med.isDelete()) {
					continue;
				}				
				sql = "{call inserta_medicamento_reintegro_farmacia (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, id_reintegro);
				stmt.setInt(2, med.getMedicamento().getId_medicamento());
				stmt.setDate(3, new java.sql.Date(periodo.getTime()));
				stmt.setInt(4, med.getNumeroReceta());
				stmt.setString(5, ""); //no estamos insertando profesional por ahora
				stmt.setDouble(6, med.getCantidad());
				stmt.setInt(7, med.getMedicamento().getTroquel());
				stmt.setBigDecimal(8, med.getMedicamento().getCober_sssalud());
				stmt.setBigDecimal(9, med.getMedicamento().getCober_amtima());
				stmt.setBigDecimal(10, med.getMedicamento().getCober_ospim());
				stmt.setBigDecimal(11, med.getImporteCoberturaOspim());
				stmt.setBigDecimal(12, med.getImporteCoberturaAmtima());
				stmt.setBigDecimal(13, med.getImporteCoberturaPrestadora());
				stmt.setBigDecimal(14, med.getPrecio_al_publico());
				stmt.setBigDecimal(15, med.getMedicamento().getPrecio_ospim());				
				stmt.setBigDecimal(16, med.getTotalCobertura());
				stmt.setBigDecimal(17, med.getTotal());
				stmt.setBigDecimal(18, med.getMedicamento().getTotal_medicamento());
				if(med.getFechaReceta() != null){
					stmt.setDate(19, new java.sql.Date(med.getFechaReceta().getTime()));
				}else{
					stmt.setNull(19,Types.DATE);
				}
				stmt.setString(20, userName);
				
				
				if(med.getFechaComprobante() != null){
					stmt.setDate(21, new java.sql.Date(med.getFechaComprobante().getTime()));
				}else{
					stmt.setNull(21,Types.DATE);
				}
				
				stmt.setString(22, med.getCuitEntidad());
				stmt.setString(23, med.getSucursalEntidad());
				stmt.setString(24, med.getComproaDebitarTipo());
				stmt.setString(25, med.getComproaDebitarLetra());
				stmt.setString(26, med.getComproaDebitarSucursal());
				stmt.setString(27, med.getComproaDebitarNumero());
				stmt.setBigDecimal(28, med.getImporteComprobante());

				if(med.getFechaPrestacion() != null){
					stmt.setDate(29, new java.sql.Date(med.getFechaPrestacion().getTime()));
				}else{
					stmt.setNull(29,Types.DATE);
				}
				
				stmt.setBigDecimal(30, med.getImporteCoberturaImesa());
				if (!StringUtils.checkEmpty(idTecerizadora)) {
				    stmt.setString(31, idTecerizadora);
				} else {
				    stmt.setNull(31, Types.VARCHAR);
				}
				
				ResultSet rs1 = stmt.executeQuery();
				while (rs1.next()) {
					id_prestacionfarmacia = rs1.getInt(1);
				}
				
				if (med.getIdReclamoPrestacional() >0 && med.getIdPrestacionReclamo()>0   )  {
					// graba datos del reclamo del medicamento si existen 
					sql = "{call inserta_datos_reclamo_reintegro_farmacia (?,?,?,?,?)}";
					stmt = con.prepareCall(sql.toString());
					stmt.setInt(1, id_reintegro);
					stmt.setInt(2, id_prestacionfarmacia );
					stmt.setInt(3, med.getIdReclamoPrestacional() );
					stmt.setInt(4, med.getIdPrestacionReclamo() );
					stmt.setString(5, userName);
					stmt.executeQuery();
				}
				
				
			}
			con.commit();

		} catch (SQLException e) {
			ConnectionHelper.rollback(con);
			_log.error("Error al guardar reintegro de farmacia", e);
			throw new SystemException(e);
		} catch (Exception e) {
			ConnectionHelper.rollback(con);
			_log.error("Error al guardar reintegro de farmacia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_reintegro;
	}
	
	/**
	 * metodo que carga una nueva prestacion a partir de los parámetros si no lo
	 * puede insertar retorna null
	 * 
	 * @param periodo
	 * 
	 * @throws DuplicateReintegroIdException
	 * @throws SystemException
	 * @throws DuplicateReintegroPrestacionIdException
	 * @throws AfiliadoSinPlanException
	 */

	public void actualizaMedicamentoReintegroPrestacionEntry(int id_reintegro,
			ArrayList<ReintegroMedicamentoItem> medicamentos, String userName, String idTercerizadora) throws SystemException,
			DuplicateReintegroPrestacionIdException, AfiliadoSinPlanException {

		Connection con = null;
		CallableStatement stmt = null;
		
		int id_prestacionfarmacia =0;
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
		for (ReintegroMedicamentoItem item : medicamentos) {
			if (item.getMedicamento().getNombre().equals("TOTAL")) {
				continue;
			}		
			if (item.isDelete() && item.getId() > 0) {
				
				String sql = "{call borra_prestacion_farmacia (?,?)}";
				stmt = con.prepareCall(sql.toString());										
				stmt.setInt(1, item.getId());
				stmt.setString(2, userName);
				stmt.executeUpdate();
				stmt.close();
				
			} else if (item.isEdit() && item.getId() > 0) {
				
				String sql = "{call actualiza_prestacion_farmacia (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, item.getId());	
				stmt.setInt(2, item.getMedicamento().getId_medicamento());
				stmt.setDouble(3, item.getCantidad());
				stmt.setInt(4, item.getMedicamento().getTroquel());
				stmt.setBigDecimal(5, item.getImporteCoberturaOspim());
				stmt.setBigDecimal(6, item.getImporteCoberturaAmtima());
				stmt.setBigDecimal(7, item.getImporteCoberturaPrestadora());
				stmt.setBigDecimal(8, item.getPrecio_al_publico());				
				stmt.setBigDecimal(9, item.getTotalCobertura());
				stmt.setString(10, userName);
								
				if(item.getFechaComprobante() != null){
					stmt.setDate(11, new java.sql.Date(item.getFechaComprobante().getTime()));
				}else{
					stmt.setNull(11,Types.DATE);
				}
				
				stmt.setString(12, item.getCuitEntidad());
				stmt.setString(13, item.getSucursalEntidad());
				stmt.setString(14, item.getComproaDebitarTipo());
				stmt.setString(15, item.getComproaDebitarLetra());
				stmt.setString(16, item.getComproaDebitarSucursal());
				stmt.setString(17, item.getComproaDebitarNumero());
				stmt.setBigDecimal(18, item.getImporteComprobante());
				
				if(item.getFechaPrestacion() != null){
					stmt.setDate(19, new java.sql.Date(item.getFechaPrestacion().getTime()));
				}else{
					stmt.setNull(19,Types.DATE);
				}
				
				stmt.setBigDecimal(20, item.getImporteCoberturaImesa());
				
				if (!StringUtils.checkEmpty(idTercerizadora)) {
				    stmt.setString(21, idTercerizadora);
				} else {
				    stmt.setNull(21, Types.VARCHAR);
				}
				
				stmt.executeUpdate();
				stmt.close();
				
			} else if (item.getId() < 0) {
				
				String sql = "{call inserta_medicamento_reintegro_farmacia (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, id_reintegro);
				stmt.setInt(2, item.getMedicamento().getId_medicamento());
				stmt.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
				stmt.setInt(4, item.getNumeroReceta());
				stmt.setString(5, ""); //no estamos insertando profesional por ahora
				stmt.setDouble(6, item.getCantidad());
				stmt.setInt(7, item.getMedicamento().getTroquel());
				stmt.setBigDecimal(8, item.getMedicamento().getCober_sssalud());
				stmt.setBigDecimal(9, item.getMedicamento().getCober_amtima());
				stmt.setBigDecimal(10, item.getMedicamento().getCober_ospim());
				stmt.setBigDecimal(11, item.getImporteCoberturaOspim());
				stmt.setBigDecimal(12, item.getImporteCoberturaAmtima());
				stmt.setBigDecimal(13, item.getImporteCoberturaPrestadora());
				stmt.setBigDecimal(14, item.getPrecio_al_publico());
				stmt.setBigDecimal(15, item.getMedicamento().getPrecio_ospim());				
				stmt.setBigDecimal(16, item.getTotalCobertura());
				stmt.setBigDecimal(17, item.getMedicamento().getTotal());
				stmt.setBigDecimal(18, item.getMedicamento().getTotal_medicamento());
				if(item.getFechaReceta() != null){
					stmt.setDate(19, new java.sql.Date(item.getFechaReceta().getTime()));
				}else{
					stmt.setNull(19,Types.DATE);
				}
				stmt.setString(20, userName);
				
				if(item.getFechaComprobante() != null){
					stmt.setDate(21, new java.sql.Date(item.getFechaComprobante().getTime()));
				}else{
					stmt.setNull(21,Types.DATE);
				}
				
				stmt.setString(22, item.getCuitEntidad());
				stmt.setString(23, item.getSucursalEntidad());
				stmt.setString(24, item.getComproaDebitarTipo());
				stmt.setString(25, item.getComproaDebitarLetra());
				stmt.setString(26, item.getComproaDebitarSucursal());
				stmt.setString(27, item.getComproaDebitarNumero());
				stmt.setBigDecimal(28, item.getImporteComprobante());
				
				if(item.getFechaPrestacion() != null){
					stmt.setDate(29, new java.sql.Date(item.getFechaPrestacion().getTime()));
				}else{
					stmt.setNull(29,Types.DATE);
				}	
				
				stmt.setBigDecimal(30, item.getImporteCoberturaImesa());
				
				if (!StringUtils.checkEmpty(idTercerizadora)) {
				    stmt.setString(31, idTercerizadora);
				} else {
				    stmt.setNull(31, Types.VARCHAR);
				}
				
				ResultSet rs1 = stmt.executeQuery();
				while (rs1.next()) {
					id_prestacionfarmacia = rs1.getInt(1);
				}
				if (item.getIdReclamoPrestacional()  >0 && item.getIdPrestacionReclamo()>0   )  {
					// graba datos del reclamo del medicamento si existen 
					sql = "{call inserta_datos_reclamo_reintegro_farmacia (?,?,?,?,?)}";
					stmt = con.prepareCall(sql.toString());
					stmt.setInt(1, id_reintegro);
					stmt.setInt(2, id_prestacionfarmacia  ); 
					stmt.setInt(3, item.getIdReclamoPrestacional() );
					stmt.setInt(4, item.getIdPrestacionReclamo() );
					stmt.setString(5, userName);
					stmt.executeQuery();
				}
				
				stmt.close();
			}
		}
		con.commit();
	} catch (SQLException e) {
		_log.error("Error al guardar reintegro de farmacia", e);
		ConnectionHelper.rollback(con);
		throw new SystemException(e);
	} catch (Exception e) {
		_log.error("Error al guardar reintegro de farmacia", e);
		ConnectionHelper.rollback(con);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(con);
	}
	return;
}
	
	/**
	 * metodo que actualiza un afiliado a partir de los parámetros , si no lo
	 * puede actualizar retorna null
	 * 
	 * @throws NoSuchReintegroEntryException
	 * @throws SystemException
	 */
	public void actualizaReintegroEntry(int id_reintegro, Date fecha,
			String userName, int id_seccional, String obs) throws NoSuchReintegroEntryException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_reintegro_farmacia_fecha (?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setString(3, userName);
			stmt.setInt(4, id_seccional);
			stmt.setString(5, obs);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al actualizar reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo que aplica borrado lógico de un reintegro a partir de la clave
	 * primaria, no borra el reintegro físicamente, solo lo da de baja
	 * 
	 * @throws NoSuchReintegroEntryException
	 * @throws SystemException
	 */
	public void borraReintegroEntry(int id_reintegro, String userName)
			throws NoSuchReintegroEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_reintegro_farmacia(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setString(2, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar reintegro", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	/**
	 * Metodo que aplica borrado lógico de un reintegro prestacion a partir de
	 * la clave primaria, borra la prestacion físicamente
	 * 
	 * @throws NoSuchReintegroPrestacionEntryException
	 * @throws SystemException
	 */ 
	public void borraReintegroPrestacionEntry(int id_reintegro,
			int id_prestacion, Date alta_fecha, int id_plan,
			String tipo_compro, String nro_compro, String userName)
			throws NoSuchReintegroPrestacionEntryException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_prestacion_farmacia(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);			
			stmt.setString(2, userName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al borrar reintegro prestacion", e);
			if (e.getSQLState().equals(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroPrestacionEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al borrar reintegro prestacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return;
	}

	public int actualizaReintegroFarmaciaEntry(int id_reintegro, Date fecha, Date periodo,
		String cuilTitular, int inte, int seccional,
		ArrayList<ReintegroMedicamentoItem> medicamentos, String userName) throws NoSuchReintegroEntryException,
		SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			//no permite actualizar afiliado ni periodo
			String sql = "{call actualiza_reintegro_farmacia (?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_reintegro);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setString(3, userName);
			stmt.setInt(4, seccional);
			stmt.setString(5, null);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar reintegro", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_ROW_NOT_FOUND_UPDATE)) {
				throw new NoSuchReintegroEntryException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al actualizar reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_reintegro;
	}

	public List<ReintegroMedicamentoItem> getMedicamentosReintegroEntryPorNumeroReceta(
			int numReceta) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Medicamento medicamento = null;		
		List<ReintegroMedicamentoItem> reintegroItems = new ArrayList<ReintegroMedicamentoItem>();
		try {
			String sql = "{call busca_medicamentos_reintegro_por_num_receta(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, numReceta);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ReintegroMedicamentoItem reintegroPrestacion = ReintegroMedicamentoItem
						.getMapping(rs, "rp_");				
				medicamento = Medicamento.getMapping(rs, "m_");
				reintegroPrestacion.setMedicamento(medicamento);
				reintegroItems.add(reintegroPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener prestaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reintegroItems;			
	}
	
	public List<ReporteOrdenPagoReintegrosFarmacia> getReintegros(int opId,
			List<ReporteOrdenPagoReintegrosFarmacia> list) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		try {
			String sql = "{call buscar_reporte_reintegros_farmacia(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				ReintegroMedicamento reintegro = ReintegroMedicamento.getMapping(rs, "r_");
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Domicilio afiDomicilio = Domicilio.getMappingAfiDomicilio(rs, "ad_");
				afiliado.setDomicilioDefault(afiDomicilio);
				Seccional secc = Seccional.getMappingSeccionalParaReintegros(rs,
						"s_");
				reintegro.setSeccional(secc);
				reintegro.setAfiliado(afiliado);

				ReintegroMedicamentoItem reintegroPrestacion = ReintegroMedicamentoItem.getMapping(rs, "rp_");
				//el troquel viene en el medicamento
				Medicamento medicamento = Medicamento.getMapping(rs, "m_");
				reintegroPrestacion.setMedicamento(medicamento);
				List<ReintegroMedicamentoItem> listaReintegrosPrest = new ArrayList<ReintegroMedicamentoItem>();				
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setMedicamentos(listaReintegrosPrest);
				
				ReporteOrdenPagoReintegrosFarmacia repo = new ReporteOrdenPagoReintegrosFarmacia(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegrosFarmacia repoOriginal = list.get(indexOf);
					repoOriginal.getReintegro().getMedicamentos().add(
							reintegroPrestacion);
				
					Collections.sort(repoOriginal.getReintegro()
							.getMedicamentos(),
							new Comparator<ReintegroMedicamentoItem>() {
								public int compare(ReintegroMedicamentoItem o1,
										ReintegroMedicamentoItem o2) {
									return o1.getIdAsString().compareTo(
											o2.getIdAsString());
								}
							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de op", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	
	public String  validarComprobantesDuplicados(String cuilTitular, int inte ,ReintegroMedicamentoItem  medicamento) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		String respuesta = "";
		List<ReintegroMedicamentoItem> reintegroItems = new ArrayList<ReintegroMedicamentoItem>();
		try {
			String sql = "{call busca_prestaciones_reintegro_por_comprobante(?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, medicamento.getId_medicamento());
			stmt.setString(2, medicamento.getComproaDebitarTipo());
			stmt.setString(3, medicamento.getComproaDebitarLetra());
			stmt.setString(4, medicamento.getComproaDebitarSucursal());
			stmt.setString(5, medicamento.getComproaDebitarNumero());
			stmt.setString(6, medicamento.getCuitEntidad());
			stmt.setString(7, medicamento.getSucursalEntidad());
			stmt.setString(8, cuilTitular);
			stmt.setInt(9, inte);
			
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ReintegroMedicamentoItem reintegroPrestacion = ReintegroMedicamentoItem.getMapping(rs, "rp_");				
				reintegroItems.add(reintegroPrestacion);
			}
		} catch (Exception e) {
			_log.debug("Error al obtener busca_prestaciones_reintegro_por_comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return respuesta;			
	}
	
	
}


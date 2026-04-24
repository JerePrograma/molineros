package ar.com.ospim.autorizaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import ar.com.ospim.autorizaciones.beans.CuentasInterbaking;
import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDR;
import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDS;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.OrdenPagoConError;
import ar.com.ospim.autorizaciones.beans.PagosInterbanking;
import ar.com.ospim.autorizaciones.beans.ReglaValidacion;
import ar.com.ospim.autorizaciones.beans.ReglaValidacionParametros;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.util.ConnectionHelper;

public class IntegracionServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(IntegracionServiceImpl.class);
	
	
	public Integer insertaIntegracionCabezaDS(IntegracionCabeceraDS cab, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_lote = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.inserta_integracion_ds_cabecera(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cab.getEntidad());
			stmt.setInt(2,cab.getPeriodo());
			if(cab.getFecha() ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (cab.getFecha().getTime()));
			}
			
			stmt.setString(4, screenName);
						
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_lote = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Integracion Cabecera DS", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_lote;
	}
	
	
	public Integer insertaIntegracionDetalleDS(Integer loteId,IntegracionDetalleDS det,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_detalle = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.inserta_integracion_ds_detalle(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,loteId);
			stmt.setInt(2,det.getIdObraSocial());
			stmt.setString(3,det.getCuil());
			stmt.setString(4,det.getCertificadoCodigo());
			if(det.getCertificadoVencimiento() ==null){
				  stmt.setNull(5, Types.DATE );	
			}else{
				  stmt.setDate(5, new java.sql.Date (det.getCertificadoVencimiento().getTime()));
			}
			stmt.setInt(6,det.getPeriodoPrestacion());
			stmt.setString(7,det.getCuitPrestador());
			stmt.setInt(8,det.getComprobanteTipo());
			stmt.setString(9, det.getComprobanteTipoEmision());
			if(det.getComprobanteFechaEmision() ==null){
				  stmt.setNull(10, Types.DATE );	
			}else{
				  stmt.setDate(10, new java.sql.Date (det.getComprobanteFechaEmision().getTime()));
			}
			stmt.setString(11,det.getComprobanteCAECAI());
			stmt.setInt(12,det.getComprobantePtoVta());
			stmt.setInt(13,det.getComprobanteNro());
			stmt.setDouble(14, det.getComprobanteImporte());
			stmt.setDouble(15, det.getImporteSolicitado());
			stmt.setString(16,det.getPrestacionCodigo());
			stmt.setInt(17, det.getPrestacionCantidad());
			stmt.setInt(18, det.getProvincia());
			stmt.setString(19,det.getDependencia());
			stmt.setString(20, det.getError());
			stmt.setString(21, screenName);
			stmt.setString(22,det.getTipoArchivo());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_detalle = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar integracion - detalle ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_detalle;
	}

	
	public List<IntegracionCabeceraDS> lotesProcesados()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionCabeceraDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_lotes_procesados()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionCabeceraDS>();
			while (rs.next()) {
				IntegracionCabeceraDS archivo = IntegracionCabeceraDS.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Lotes Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public boolean getValidaDuplicado(IntegracionDetalleDS det) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean validaDuplicado = false;
		try {
			String sql = "{call autorizaciones.integracion_valida_duplicado(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,det.getCuil());
			
			//if(det.getCertificadoCodigo()!=null && !"".equalsIgnoreCase(det.getCertificadoCodigo())) {
			stmt.setString(2, det.getCertificadoCodigo());
			//}else {
			//   stmt.setNull(2,Types.VARCHAR);	
			//}
			stmt.setInt(3, det.getPeriodoPrestacion());
			stmt.setString(4,det.getCuitPrestador());
			stmt.setInt(5,det.getComprobanteTipo());
			stmt.setString(6,det.getComprobanteTipoEmision());
			stmt.setString(7,det.getComprobanteCAECAI());
			stmt.setInt(8, det.getComprobantePtoVta());
			stmt.setInt(9,det.getComprobanteNro());
			stmt.setDouble(10, det.getComprobanteImporte());
			stmt.setString(11,det.getPrestacionCodigo());
			stmt.setInt(12, det.getPrestacionCantidad());
			stmt.setInt(13,det.getProvincia());
			stmt.setString(14,det.getDependencia());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				validaDuplicado = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al validar duplicado archivo integracion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return validaDuplicado;
	}
	
	public void eliminaLote(Integer id) throws Exception{
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.integracion_elimina_lote(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
		} catch (Exception e) {
			_log.error("Error al eliminar Lote", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	
	public List<IntegracionDetalleDS> detalleDSByIdLote(Integer idLote)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_ds_detalle_por_idlote(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = IntegracionDetalleDS.getMapping(rs) ;
			    try {	
				archivo.setDescripcionPrestador(rs.getString("prestador"));
				   archivo.setAfiliado(rs.getString("afiliado"));
				   archivo.setEntidad(rs.getString("entidad"));
			    }catch(Exception e) {
				   archivo.setDescripcionPrestador("");
				   archivo.setAfiliado(rs.getString(""));
				   archivo.setEntidad("");
			    }
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Detalle Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public Integer updateErrorDetalleDS(IntegracionDetalleDS det,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_detalle = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.update_integracion_ds_detalle_error(?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,det.getId());
			stmt.setString(2,det.getError());
			stmt.setString(3, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_detalle = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update codigo error integracion - detalle ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_detalle;
	}

	public List<IntegracionDetalleDS> detalleDSByPeriodo(Integer periodo )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_ds_detalle_por_periodo(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,periodo);
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = IntegracionDetalleDS.getMapping(rs);
				archivo.setEnviadoSSS(rs.getDate("enviado_sss"));
				archivo.setTercerizadora(rs.getString("entidad"));
				archivo.setPrestacionDescripcion(rs.getString("prestacion_descripcion"));
				archivo.setPeriodo(rs.getInt("periodo"));
				archivo.setImporteSolicitadoSSS(rs.getDouble("importe_solicitado_sss"));
				archivo.setOrdenPago(rs.getInt("ordenpago_id"));
				archivo.setLiquidacion(rs.getInt("liquidacion_id"));
				archivo.setCbu(rs.getString("cbu"));
				archivo.setErrorSSS(rs.getString("cod_error_sss"));
				archivo.setImporteSubsidiado(rs.getDouble("importe_subsidio"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Detalle Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public Nomenclador buscaNomencladorSSSById(Integer id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		Nomenclador nomenclador = new Nomenclador();
		try {
			String sql = "{call autorizaciones.nomenclador_sss_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				nomenclador.setId_prestacion(rs.getInt("codigo"));
				nomenclador.setDescripcion(rs.getString("descripcion"));
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nomenclador SSS ", e);
			
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return nomenclador;
		
	}
	
	public Integer updateInformadoFTPDS(Integer periodo,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_detalle = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.update_integracion_ds_cabecera_enviado_sss(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,periodo);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_detalle = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update enviado sss integracion - cabecera ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_detalle;
	}
	
	public List<IntegracionCabeceraDS> lotesSSS()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionCabeceraDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_lotes_sss()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionCabeceraDS>();
			while (rs.next()) {
				IntegracionCabeceraDS archivo = IntegracionCabeceraDS.getMappingSSS(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Lotes Integracion SSS ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public Integer updateFTPDS_OK(IntegracionDetalleDS det,Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Integer nro = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			String sql = "{call autorizaciones.update_integracion_ds_detalle_sss_ok(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,det.getCuil());
			if(det.getCertificadoCodigo()!=null) {
			   stmt.setString(2, det.getCertificadoCodigo().trim());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			stmt.setInt(3, det.getPeriodoPrestacion());
			stmt.setString(4,det.getCuitPrestador());
			stmt.setInt(5,det.getComprobanteTipo());
			stmt.setString(6,det.getComprobanteTipoEmision());
			stmt.setString(7,det.getComprobanteCAECAI().trim());
			stmt.setInt(8, det.getComprobantePtoVta());
			stmt.setInt(9,det.getComprobanteNro());
			stmt.setDouble(10, det.getComprobanteImporte());
			stmt.setString(11,det.getPrestacionCodigo());
			stmt.setInt(12, det.getPrestacionCantidad());
			stmt.setInt(13,det.getProvincia());
			stmt.setString(14,det.getDependencia());
			stmt.setDouble(15, det.getImporteSolicitado());
			if(det.getError()!=null) {
			   stmt.setString(16,det.getError());
			}else {
			   stmt.setNull(16, Types.VARCHAR);	
			}
			stmt.setString(17,det.getTipoArchivo());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				nro = rs.getInt(1);
			}
			_log.debug("Nro id ds detalle FTP  "+nro);
		} catch (Exception e) {
			_log.error("Error al update archivo integracion sss ok", e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return nro;
	}
	

	public boolean getValidaFTPProcesado(Integer periodo,String tipoArchivo) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean valida = false;
		try {
			String sql = "{call autorizaciones.integracion_valida_respuesta_procesada_sss(?)}";
			if("ERR".equalsIgnoreCase(tipoArchivo)) {
				sql= "{call autorizaciones.integracion_valida_respuesta_procesada_error_sss(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, periodo);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				valida = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al validar FTP procesado", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return valida;
	}
	
	public List<IntegracionDetalleDS> detalleDSByIdLoteSSS(Integer idLote)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_ds_detalle_por_idlote_sss(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = IntegracionDetalleDS.getMapping(rs);
				try {
					archivo.setDescripcionPrestador(rs.getString("prestador"));
					archivo.setAfiliado(rs.getString("afiliado"));
					archivo.setEntidad(rs.getString("entidad"));
				}catch(Exception e) {
					archivo.setDescripcionPrestador("");
					archivo.setAfiliado(rs.getString(""));
					archivo.setEntidad("");
				}
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Detalle Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public boolean liquidarLoteSSS(Integer idLote) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.genera_liquidacion_integracion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al liquidar lote integracion", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public boolean liquidarLoteSSSCab(Integer idLote) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.genera_liquidacion_integracion_by_cabecera(?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al liquidar lote integracion cabecera", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<IntegracionDetalleDS> detalleLiquidacionByIdLote(Integer idLote)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_ds_detalle_liquidacion_por_idlote_sss(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = new IntegracionDetalleDS();
				archivo.setCuitPrestador(rs.getString("cuit_prestador"));
				archivo.setDescripcionPrestador(rs.getString("descripcion_prestador"));
				archivo.setLiquidacion(rs.getInt("liquidacion_id"));
				archivo.setOrdenPago(rs.getInt("ordenpago_id"));
				archivo.setComprobanteString(rs.getString("comprobante"));
//				archivo.setComprobanteFechaEmision(rs.getDate("fecha_emision"));
				archivo.setComprobanteImporte(rs.getDouble("total"));
				archivo.setCbu(rs.getString("cbu"));
				archivo.setTercerizadora(rs.getString("tercerizadora"));
				archivo.setTercerizadoraId(rs.getString("tercerizadora_id"));
				archivo.setOpImporte(rs.getDouble("importe_op"));
				archivo.setOpFecha(rs.getString("fecha_op"));
				archivo.setFechaAvisoTransferencia(rs.getDate("fecha_aviso_transferencia"));
				archivo.setNroRecibo(rs.getString("nro_recibo"));
				archivo.setFechaExportacionInterbanking(rs.getDate("fecha_exportacion_interbanking"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Detalle Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public List<IntegracionDetalleDS> detalleLiquidacionByIdCab(Integer idLote)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_ds_detalle_liquidacion_por_idcab_sss(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = new IntegracionDetalleDS();
				archivo.setCuitPrestador(rs.getString("cuit_prestador"));
				archivo.setDescripcionPrestador(rs.getString("descripcion_prestador"));
				archivo.setLiquidacion(rs.getInt("liquidacion_id"));
				archivo.setOrdenPago(rs.getInt("ordenpago_id"));
				archivo.setComprobanteString(rs.getString("comprobante"));
//				archivo.setComprobanteFechaEmision(rs.getDate("fecha_emision"));
				archivo.setComprobanteImporte(rs.getDouble("total"));
				archivo.setCbu(rs.getString("cbu"));
				archivo.setTercerizadora(rs.getString("tercerizadora"));
				archivo.setTercerizadoraId(rs.getString("tercerizadora_id"));
				archivo.setOpImporte(rs.getDouble("importe_op"));
				archivo.setOpFecha(rs.getString("fecha_op"));
				archivo.setFechaAvisoTransferencia(rs.getDate("fecha_aviso_transferencia"));
				archivo.setNroRecibo(rs.getString("nro_recibo"));
				archivo.setFechaExportacionInterbanking(rs.getDate("fecha_exportacion_interbanking"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Detalle Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	/**
	 * Devuelve el detalle liquidacion ordenado por Op asc
	 * @param idLote
	 * @return
	 * @throws SystemException
	 */
	public List<IntegracionDetalleDS> detalleLiquidacionByIdLotePorOp(Integer idLote)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_ds_detalle_liq_por_idlote_sss_ordenado_por_op(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = new IntegracionDetalleDS();
				archivo.setCuitPrestador(rs.getString("cuit_prestador"));
				archivo.setDescripcionPrestador(rs.getString("descripcion_prestador"));
				archivo.setLiquidacion(rs.getInt("liquidacion_id"));
				archivo.setOrdenPago(rs.getInt("ordenpago_id"));
				archivo.setComprobanteString(rs.getString("comprobante"));
//				archivo.setComprobanteFechaEmision(rs.getDate("fecha_emision"));
				archivo.setComprobanteImporte(rs.getDouble("total"));
				archivo.setCbu(rs.getString("cbu"));
				archivo.setTercerizadora(rs.getString("tercerizadora"));
				archivo.setTercerizadoraId(rs.getString("tercerizadora_id"));
				archivo.setOpImporte(rs.getDouble("importe_op"));
				archivo.setOpFecha(rs.getString("fecha_op"));
				archivo.setFechaAvisoTransferencia(rs.getDate("fecha_aviso_transferencia"));
				archivo.setNroRecibo(rs.getString("nro_recibo"));
				archivo.setFechaExportacionInterbanking(rs.getDate("fecha_exportacion_interbanking"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Detalle Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public boolean cerrarLoteSSS(Integer idLote) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.cerrar_lote_integracion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idLote);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al cerrar lote integracion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public Integer historicoLoteSSS(Integer idLote,String usr) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret = 0;
		try {
			String sql = "{call autorizaciones.insert_integracion_ds_historico(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idLote);
			stmt.setString(2,usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al pasar a historico lote integracion", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public boolean existeMovimientoTransferencia(Date fechaMov,Date fechaValor,Double monto,String cuit,String referencia) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.existe_integracion_extracto_transferencia_movimiento(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			
			stmt.setDate(1, new java.sql.Date (fechaMov.getTime()));
			stmt.setDate(2, new java.sql.Date (fechaValor.getTime()));
			stmt.setDouble(3,monto);
			stmt.setString(4, cuit);
			stmt.setString(5, referencia);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al buscar movimiento transferencia integracion", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public List<OrdenPagoOspim> proponeOrdenPagoTransferenciaBancaria(String cuit,Date fecha,Double importe)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> list = null;
		try {
			String sql = "{call autorizaciones.integracion_orden_pago_sin_transferencia(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,cuit);
			stmt.setDate(2, new java.sql.Date (fecha.getTime()));
			stmt.setDouble(3,importe);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<OrdenPagoOspim>();
			while (rs.next()) {
				OrdenPagoOspim archivo = new OrdenPagoOspim();
				archivo.setId(rs.getInt("ordenpago_id"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar OP para transferencia ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public Integer ultimoLoteTransferenciaProcesado() throws SystemException   {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_lote = 0;
		try {
			
			con = ConnectionHelper.getConnection();
			String sql = "{call autorizaciones.integracion_ultimo_lote_transferencia()}";
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_lote = rs.getInt("ultimo_lote");
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Integracion Cabecera DS", e);
			throw new SystemException(e);
		} finally {
			
				ConnectionHelper.cerrar(stmt, con);
			
		}
		return id_lote;
	}
	
	
	public  Integer insertaIntegracionTranferencia(
			  Integer nro_lote_p,
			  Date fecha_valor_p,
			  Date fecha_mov_p,
			  Double monto_p ,
			  String referencia_p,
			  String concepto_p,
			  String cuit_p,
			  Integer ordenpago_id_p,
			  String alta_usr_p,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_detalle = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.insert_integracion_transferencia_registro(?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,nro_lote_p);
			if(fecha_valor_p ==null){
				  stmt.setNull(2, Types.DATE );	
			}else{
				  stmt.setDate(2, new java.sql.Date (fecha_valor_p.getTime()));
			}
			if(fecha_mov_p ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (fecha_mov_p.getTime()));
			}
			
			stmt.setDouble(4, monto_p);
			stmt.setString(5,referencia_p);
			stmt.setString(6,concepto_p);
			stmt.setString(7,cuit_p);
			
			if(ordenpago_id_p ==null){
				  stmt.setNull(8, Types.INTEGER);	
			}else{
				stmt.setInt(8,ordenpago_id_p);
			}
			stmt.setString(9,alta_usr_p);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_detalle = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar integracion - transferencia registro ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_detalle;
	}

	public List<IntegracionCabeceraDS> lotesTransferenciasExtractos()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionCabeceraDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_lotes_transferencias()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionCabeceraDS>();
			while (rs.next()) {
				IntegracionCabeceraDS archivo = new IntegracionCabeceraDS();
				archivo.setLoteSSS(rs.getInt("nro_lote"));
				archivo.setFecha(rs.getDate("alta_fecha"));
				archivo.setEntidad(rs.getString("alta_usr"));
				archivo.setDetalleProcesadosTOTAL(rs.getInt("cantidad"));
				archivo.setDetalleProcesadosOK(rs.getInt("cantidadOP"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Lotes Integracion Transferencias Extracto ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public boolean avisoTransferenciaOP(Integer idOp) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.integracion_aviso_transferencia(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idOp);
			ResultSet rs = stmt.executeQuery();
			/*
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
			*/
		} catch (Exception e) {
			_log.error("Error al enviar aviso de transferencia", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public boolean marcaAvisoTransferencia(Integer idOp) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.marca_aviso_transferencia(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idOp);
			ResultSet rs = stmt.executeQuery();
		
		} catch (Exception e) {
			_log.error("Error al enviar aviso de transferencia", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return true;
	}
	
	public boolean existeAvisoTransferencia(Integer op) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.integracion_existe_aviso_transferencia(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, op);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al buscar aviso transferencia integracion", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}	
		
	public boolean asociarRecibo(Integer idOp,String nroRecibo) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.integracion_asociar_recibo(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idOp);
			stmt.setString(2, nroRecibo);
			ResultSet rs = stmt.executeQuery();
			/*
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
			*/
		} catch (Exception e) {
			_log.error("Error al asociar recibo integracion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public List<IntegracionDetalleDS> inconsistenciasExtractosBancariosByIdLote(Integer idLote)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_inconsistencias_extractos_idlote_sss(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(idLote!=null) {
			   stmt.setInt(1,idLote);
			}else {
				stmt.setNull(1,Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = new IntegracionDetalleDS();
				archivo.setCuitPrestador(rs.getString("cuit_prestador"));
				archivo.setDescripcionPrestador(rs.getString("descripcion_prestador"));
				archivo.setOrdenPago(rs.getInt("ordenpago_id"));
				archivo.setTercerizadora(rs.getString("tercerizadora"));
				archivo.setOpImporte(rs.getDouble("importe_op"));
				archivo.setOpFecha(rs.getString("fecha_op"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Inconsistencia Extractos Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public Integer updateFTPDS_Subsidio(IntegracionDetalleDS det,Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Integer nro = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			String sql = "{call autorizaciones.update_integracion_ds_detalle_sss_subsidio(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,det.getCuil());
			if(det.getCertificadoCodigo()!=null) {
			   stmt.setString(2, det.getCertificadoCodigo().trim());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			stmt.setInt(3, det.getPeriodoPrestacion());
			stmt.setString(4,det.getCuitPrestador());
			stmt.setInt(5,det.getComprobanteTipo());
			stmt.setString(6,det.getComprobanteTipoEmision());
			stmt.setString(7,det.getComprobanteCAECAI().trim());
			stmt.setInt(8, det.getComprobantePtoVta());
			stmt.setInt(9,det.getComprobanteNro());
			stmt.setDouble(10, det.getComprobanteImporte());
			stmt.setString(11,det.getPrestacionCodigo());
			stmt.setInt(12, det.getPrestacionCantidad());
			stmt.setInt(13,det.getProvincia());
			stmt.setString(14,det.getDependencia());
			stmt.setDouble(15, det.getImporteSolicitado());
			stmt.setDouble(16, det.getImporteSubsidiado());
			stmt.setString(17,det.getNroLiquidacionSSS());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				nro = rs.getInt(1);
			}
			_log.debug("Nro id ds detalle FTP Subsidio  "+nro);
		} catch (Exception e) {
			_log.error("Error al update archivo integracion sss subsidio", e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return nro;
	}

	
	public List<ReglaValidacion> getReglasValidacion()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		CallableStatement stmt1 = null;
		List<ReglaValidacion> list = null;
		try {
			String sql = "{call autorizaciones.integracion_traer_reglas_validacion()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReglaValidacion>();
			while (rs.next()) {
				ReglaValidacion archivo = ReglaValidacion.getMapping(rs) ;
				
				sql = "{call autorizaciones.integracion_traer_reglas_validacion_parametros(?)}";
				stmt1 = con.prepareCall(sql.toString());
				stmt1.setString(1,archivo.getId());
				ResultSet rs1 = stmt1.executeQuery();
				while (rs1.next()) {
					ReglaValidacionParametros p =ReglaValidacionParametros.getMapping(rs1);
					archivo.getParametros().add(p);
				}
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Lotes Integracion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<IntegracionDetalleDS> detalleDSByCuilPeriodo(String cuil,Integer periodo,String prestacionesIncluye,String prestacionesExcluye)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_ds_detalle_por_cuil_periodo(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,cuil);
			stmt.setInt(2,periodo);
			
			if(prestacionesIncluye!=null) {
			  stmt.setString(3, prestacionesIncluye);	
			}else {
			  stmt.setNull(3,Types.VARCHAR);	
			}
			
			if(prestacionesExcluye!=null) {
			  stmt.setString(4, prestacionesExcluye);	
			}else {
			  stmt.setNull(4,Types.VARCHAR);	
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDS>();
			while (rs.next()) {
				IntegracionDetalleDS archivo = IntegracionDetalleDS.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Detalle Integracion Cuil y Periodo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<CuentasInterbaking> obtenerCuentasExportarInterbanking(String opDesde, String opHasta, String in )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentasInterbaking> list = null;
		try {
			String sql = "{call autorizaciones.altas_cuentas_interbanking(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,opDesde == null ? "0" : opDesde);
			stmt.setString(2,opHasta == null ? "0" : opHasta);
			stmt.setString(3,in == null ? "0" : in);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<CuentasInterbaking>();
			while (rs.next()) {
				CuentasInterbaking archivo = CuentasInterbaking.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error para exportar cuentas a interbanking", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	
	
	
	public OrdenesPagoInterbanking  exportacionPagosInterbanking(String opDesde, String opHasta, String in)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<PagosInterbanking> listPagos = null;
		List<OrdenPagoConError> odenConError;
        OrdenesPagoInterbanking ordenes = new OrdenesPagoInterbanking();
		
		try {
			String sql = "{call autorizaciones.exportacion_Pagos_Interbanking(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,opDesde == null ? "0" : opDesde);
			stmt.setString(2,opHasta == null ? "0": opHasta);
			stmt.setString(3,in == null ? "0" : in);
			
			ResultSet rs = stmt.executeQuery();
			listPagos = new ArrayList<PagosInterbanking>();
			odenConError = new ArrayList<OrdenPagoConError>();
			while (rs.next()) {
				PagosInterbanking archivo = PagosInterbanking.getMapping(rs) ;
				if (archivo.getNumeroCBU() != null && archivo.getNumeroCBU().length() >= 22) {
					listPagos.add(archivo);
				}else {//seteo errores 
					OrdenPagoConError ordenPago = OrdenPagoConError.getMapping(rs) ;
					odenConError.add(ordenPago);
					listPagos.remove(listPagos.size() - 1);//borro devito invalido
				}	
			}
			ordenes.setListaPagos(listPagos);
			ordenes.setOdenConError(odenConError);
		} catch (Exception e) {
			_log.error("Error para exportar cuentas a interbanking", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ordenes;
	}
	
	
	public OrdenesPagoInterbanking  exportacionPagosInterbankingOPS(String opDesde, String opHasta, String in,Integer ctaBcria)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<PagosInterbanking> listPagos = null;
		List<OrdenPagoConError> odenConError;
        OrdenesPagoInterbanking ordenes = new OrdenesPagoInterbanking();
		
		try {
			String sql = "{call autorizaciones.exportacion_Pagos_Interbanking_OPS(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,opDesde == null ? "0" : opDesde);
			stmt.setString(2,opHasta == null ? "0": opHasta);
			stmt.setString(3,in == null ? "0" : in);
			stmt.setInt(4,ctaBcria);
			
			ResultSet rs = stmt.executeQuery();
			listPagos = new ArrayList<PagosInterbanking>();
			odenConError = new ArrayList<OrdenPagoConError>();
			while (rs.next()) {
				PagosInterbanking archivo = PagosInterbanking.getMapping(rs) ;
				if (archivo.getNumeroCBU() != null && archivo.getNumeroCBU().length() >= 22) {
					listPagos.add(archivo);
				}else {//seteo errores 
					OrdenPagoConError ordenPago = OrdenPagoConError.getMapping(rs) ;
					odenConError.add(ordenPago);
					listPagos.remove(listPagos.size() - 1);//borro devito invalido
				}	
			}
			ordenes.setListaPagos(listPagos);
			ordenes.setOdenConError(odenConError);
		} catch (Exception e) {
			_log.error("Error para exportar cuentas a interbanking", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ordenes;
	}
	
	
	public Integer  registrarOrdenesInterbankingOPS(Integer op)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<PagosInterbanking> listPagos = null;
		List<OrdenPagoConError> odenConError;
        OrdenesPagoInterbanking ordenes = new OrdenesPagoInterbanking();
		
		try {
			String sql = "{call autorizaciones.registrar_orden_pago_interbanking_OPS(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,op);
			
			ResultSet rs = stmt.executeQuery();
			
		} catch (Exception e) {
			_log.error("Error al registrar OP interbanking", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	
	public Integer  registrarOrdenesInterbanking(Integer op)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<PagosInterbanking> listPagos = null;
		List<OrdenPagoConError> odenConError;
        OrdenesPagoInterbanking ordenes = new OrdenesPagoInterbanking();
		
		try {
			String sql = "{call autorizaciones.registrar_orden_pago_interbanking(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,op);
			
			ResultSet rs = stmt.executeQuery();
			
		} catch (Exception e) {
			_log.error("Error al registrar OP interbanking Integracion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public boolean agregarDebito(Integer idCpte,Double debito, String motivo) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.integracion_debito_add(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idCpte);
			stmt.setDouble(2,debito);
			stmt.setString(3, motivo);
			ResultSet rs = stmt.executeQuery();
		} catch (Exception e) {
			_log.error("Error al agregar débito", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public boolean eliminarDebito(Integer idCpte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.integracion_debito_add(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idCpte);
			stmt.setNull(2, Types.DOUBLE);
			stmt.setNull(3, Types.VARCHAR);
			
			ResultSet rs = stmt.executeQuery();
		} catch (Exception e) {
			_log.error("Error al eliminar débito", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<OrdenPagoOspim>  getOrdenesPagoSinAvisoTransferencia()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> list = null;
		try {
			String sql = "{call autorizaciones.integracion_orden_pago_sin_aviso_transferencia_interbanking()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<OrdenPagoOspim>();
			while (rs.next()) {
				OrdenPagoOspim archivo = new OrdenPagoOspim();
				archivo.setId(rs.getInt("ordenpago_id"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar OP para aviso transferencia ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	

	public List<OrdenPagoOspim>  getOrdenesPagoSinAvisoTransferenciaPagoAfiliado()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> list = null;
		try {
			String sql = "{call autorizaciones.orden_pago_sin_aviso_seccional_transferencia_interbanking()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<OrdenPagoOspim>();
			while (rs.next()) {
				OrdenPagoOspim archivo = new OrdenPagoOspim();
				archivo.setId(rs.getInt("ordenpago_id"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar OP para aviso transferencia ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	
	public List<IntegracionCabeceraDR> lotesRendicion()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionCabeceraDR> list = null;
		try {
			String sql = "{call autorizaciones.integracion_dr_envio_lotes()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionCabeceraDR>();
			while (rs.next()) {
				IntegracionCabeceraDR archivo = IntegracionCabeceraDR.getMappingSSS(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Lotes Rendicion Integracion SSS ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	
	
	public Integer inserta_DR_Envio_Cabecera(IntegracionCabeceraDR cab, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		Integer id_lote = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.inserta_integracion_dr_cabecera_envio(?,?,?,?)}";
			
			 
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,cab.getPeriodo());
			stmt.setDouble(2,cab.getImporteSolicitado());
			stmt.setDouble(3,cab.getImporteLiquidado());
			stmt.setString(4, screenName);
						
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_lote = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Integracion Cabecera DR Envio", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_lote;
	}
	
	public Integer inserta_DR_Envio_Detalle(IntegracionDetalleDR det,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_detalle = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.inserta_integracion_dr_detalle_envio(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,det.getClave());
			stmt.setInt(2,det.getIdObraSocial());
			stmt.setString(3,det.getTipoArchivo());
			stmt.setInt(4,det.getPeriodoPresentacion());
			stmt.setInt(5,det.getPeriodoPrestacion());
			stmt.setString(6,det.getCuil());
			stmt.setInt(7,det.getPrestacionCodigo());
			stmt.setDouble(8, det.getImporteLiquidado() );
			stmt.setDouble(9, det.getImporteSolicitado());
			stmt.setString(10,det.getCuitPrestador());
			stmt.setInt(11,det.getComprobanteTipo());
			stmt.setInt(12,det.getComprobanteNro());
			stmt.setInt(13,det.getComprobantePtoVta());
			stmt.setInt(14,det.getNroEnvioAfip());
			stmt.setString(15, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_detalle = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar integracion - detalle DR envio", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_detalle;
	}
	
	public boolean generarDevolucion(Integer periodo,String screenName) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.genera_devolucion_integracion(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,periodo);
			stmt.setString(2, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al generar devolucion integracion", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
    
	public List<IntegracionDetalleDR>traeListaDetalleDR(Integer offset,IntegracionDetalleDR filtro)
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionDetalleDR> list = null;
		try {
			String sql = "{call autorizaciones.trae_integracion_dr_detalle_envio(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getPeriodoPresentacion()!=null) {
				stmt.setInt(1,filtro.getPeriodoPresentacion());	
			}else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if(filtro.getId()!=null) {
			  stmt.setInt(2,filtro.getId());
			}else {
				stmt.setNull(2, Types.INTEGER);	
			}
			
		    if(offset!=null) {
		    	stmt.setInt(3, offset);
		    }else {
			   stmt.setNull(3, Types.INTEGER);	
		    }
		    
		    if(filtro.getClave()!=null) {
		    	stmt.setString(4,filtro.getClave());
		    }else {
		    	stmt.setNull(4,Types.VARCHAR);
		    }
		    if(filtro.getCuitPrestador()!=null) {
		    	stmt.setString(5,filtro.getCuitPrestador());
		    }else {
		    	stmt.setNull(5,Types.VARCHAR);
		    }
		    
		    if(filtro.getPrestacionCodigo()!=null) {
		    	stmt.setInt(6, filtro.getPrestacionCodigo());
		    }else {
			   stmt.setNull(6, Types.INTEGER);	
		    }
		    
		    if(filtro.getCuil()!=null) {
		    	stmt.setString(7,filtro.getCuil());
		    }else {
		    	stmt.setNull(7,Types.VARCHAR);
		    }
		    
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionDetalleDR>();
			while (rs.next()) {
				IntegracionDetalleDR archivo = IntegracionDetalleDR.getMapping(rs);
				list.add(archivo);
			}
			if(filtro.isSoloErrores()) {
				List<IntegracionDetalleDR> listA = new ArrayList<IntegracionDetalleDR>();
				for(IntegracionDetalleDR d :list) {
					if(d.isConProblema()) {
						listA.add(d);
					}
				}
				list= listA;
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Detalle devolucion Integracion SSS ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public Integer updateDetalleDR(IntegracionDetalleDR det,String screenName,boolean soloError,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_detalle = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.update_integracion_dr_detalle_devolucion(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			if(det.getId()!=null) {
			   stmt.setInt(1,det.getId());
			}else {
			   stmt.setNull(1,Types.INTEGER);
			}
			stmt.setBoolean(2, soloError);
			
			if(det.getPeriodoPresentacion()!=null) {
			    stmt.setInt(3, det.getPeriodoPresentacion());
			} else {
				stmt.setNull(3,Types.INTEGER);
			}
			
			if(det.getClave()!=null) {
			    stmt.setString(4, det.getClave());
			} else {
				stmt.setNull(4,Types.VARCHAR);
			}
			
			if(det.getCbu()!=null) {
			    stmt.setString(5, det.getCbu());
			} else {
				stmt.setNull(5,Types.VARCHAR);
			}
			
			if(det.getOrdenPagoI()!=null) {
				stmt.setInt(6, det.getOrdenPagoI());
			}else {
				stmt.setNull(6, Types.INTEGER);
			}
			
			if(det.getOrdenPagoII()!=null) {
				stmt.setInt(7, det.getOrdenPagoII());
			}else {
				stmt.setNull(7, Types.INTEGER);
			}
			
			if(det.getFechaTransferenciaI()!=null) {
				stmt.setDate(8, new java.sql.Date (det.getFechaTransferenciaI().getTime()));
			}else {
			    stmt.setNull(8, Types.DATE);	
			}
			
			if(det.getFechaTransferenciaII()!=null) {
				stmt.setDate(9, new java.sql.Date (det.getFechaTransferenciaII().getTime()));
			}else {
			    stmt.setNull(9, Types.DATE);	
			}
			
			if(det.getCheque()!=null) {
				stmt.setString(10,det.getCheque());
			}else {
				stmt.setNull(10,Types.VARCHAR);
			}
			
			if(det.getImporteTransferido()!=null) {
				stmt.setDouble(11,det.getImporteTransferido());
			}else {
				stmt.setNull(11,Types.DOUBLE);
			}
			
			if(det.getRetencionGanancias()!=null) {
				stmt.setDouble(12,det.getRetencionGanancias());
			}else {
				stmt.setDouble(12, Types.DOUBLE);
			}
			
			if(det.getRetencionIIBB()!=null) {
				stmt.setDouble(13, det.getRetencionIIBB());
			}else {
				stmt.setDouble(13,Types.DOUBLE);
			}
			
			if(det.getOtrasRetenciones()!=null) {
				stmt.setDouble(14,det.getOtrasRetenciones());
			}else {
				stmt.setDouble(14,Types.DOUBLE);
			}
			
			if(det.getImporteAplicado()!=null) {
			   stmt.setDouble(15, det.getImporteAplicado());	
			}else {
			   stmt.setNull(15, Types.DOUBLE);	
			}
			
			if(det.getFondosPropiosDiscapacidad()!=null) {
				stmt.setDouble(16,det.getFondosPropiosDiscapacidad());
			}else {
				  stmt.setNull(16, Types.DOUBLE);
			}
			
			if(det.getFondosPropiosOtraCuenta()!=null) {
				stmt.setDouble(17,det.getFondosPropiosOtraCuenta());
			}else {
				 stmt.setNull(17, Types.DOUBLE);
			}
			
			if(det.getNroRecibo()!=null) {
				stmt.setInt(18, det.getNroRecibo());
			}else {
				stmt.setNull(18, Types.INTEGER);
			}
			
			if(det.getImporteTrasladado() !=null) {
				stmt.setDouble(19,det.getImporteTrasladado());
			}else {
				 stmt.setNull(19, Types.DOUBLE);
			}
			
			if(det.getImporteDevuelto() !=null) {
				stmt.setDouble(20,det.getImporteDevuelto());
			}else {
				 stmt.setNull(20, Types.DOUBLE);
			}
						
			if(det.getSaldoNoAplicado()!=null) {
				stmt.setDouble(21,det.getSaldoNoAplicado());
			}else {
				 stmt.setNull(21, Types.DOUBLE);
			}
			
			if(det.getRecuperoFondosPropios()!=null) {
				stmt.setDouble(22,det.getRecuperoFondosPropios());
			}else {
				 stmt.setNull(22, Types.DOUBLE);
			}
			
			if(det.getObservaciones()!=null) {
				stmt.setString(23,det.getObservaciones());
			}else {
				stmt.setNull(23,Types.VARCHAR);
			}
			
			stmt.setString(24,det.getError());
			stmt.setString(25, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_detalle = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al hacer update codigo error integracion DR - detalle ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_detalle;
	}
	
	public String getDescripcionError(Integer codError) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		String ret = "";
		try {
			String sql = "{call autorizaciones.trae_integracion_descripcion_error(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,codError);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getString(1);
			}
		} catch (Exception e) {
			_log.error("Error al buscar codigo error integracion", e);
			 throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public Integer eliminarRendicionPeriodo(Integer periodo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret = 0;
		try {
			String sql = "{call autorizaciones.delete_integracion_dr_rendicion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,periodo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al eliminar periodo rendicion integracion", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public Integer cerrarRendicionPeriodo(Integer periodo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret = 0;
		try {
			String sql = "{call autorizaciones.cerrar_integracion_dr_rendicion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,periodo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al eliminar periodo rendicion integracion", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<CuentasInterbaking>cuentasExportarInterbanking( String in,String entidad )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentasInterbaking> list = null;
		try {
			
			String sql = "";
			if("UOMA".equals(entidad)){
				sql = "{call uoma.altas_cuentas_interbanking(?)}";
			}else if("AMTIMA".equals(entidad)) {
				sql = "{call altas_cuentas_interbanking_amtima(?)}";
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,in == null ? "0" : in);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<CuentasInterbaking>();
			while (rs.next()) {
				CuentasInterbaking archivo = CuentasInterbaking.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error para exportar cuentas a interbanking UOMA-AMTIMA", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public OrdenesPagoInterbanking  getPagosInterbankingOPS(String in,Integer ctaBcria,String entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<PagosInterbanking> listPagos = null;
		List<OrdenPagoConError> odenConError;
        OrdenesPagoInterbanking ordenes = new OrdenesPagoInterbanking();
		try {
			String sql = "";
			if("UOMA".equals(entidad)){
				sql = "{call uoma.exportacion_Pagos_Interbanking_OPS(?,?)}";
			}else if("AMTIMA".equals(entidad)) {
				sql = "{call exportacion_Pagos_Interbanking_OPS_amtima(?,?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,in == null ? "0" : in);
			stmt.setInt(2,ctaBcria);
			
			ResultSet rs = stmt.executeQuery();
			listPagos = new ArrayList<PagosInterbanking>();
			odenConError = new ArrayList<OrdenPagoConError>();
			while (rs.next()) {
				PagosInterbanking archivo = PagosInterbanking.getMapping(rs) ;
				if (archivo.getNumeroCBU() != null && archivo.getNumeroCBU().length() >= 22) {
					listPagos.add(archivo);
				}else {//seteo errores 
					OrdenPagoConError ordenPago = OrdenPagoConError.getMapping(rs) ;
					odenConError.add(ordenPago);
//					listPagos.remove(listPagos.size() - 1);//borro debito invalido
				}	
			}
			ordenes.setListaPagos(listPagos);
			ordenes.setOdenConError(odenConError);
		} catch (Exception e) {
			_log.error("Error para exportar cuentas a interbanking UOMA-AMTIMA", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ordenes;
	}
	 
	
	public List<IntegracionCabeceraDS> lotesSSSCabecera()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<IntegracionCabeceraDS> list = null;
		try {
			String sql = "{call autorizaciones.integracion_lotes_sss_cabecera()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<IntegracionCabeceraDS>();
			while (rs.next()) {
				IntegracionCabeceraDS archivo = IntegracionCabeceraDS.getMappingSSSCab(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Lotes Integracion SSSCabecera", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public boolean excluirLiquidacion(Integer idCpte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.integracion_excluir_liquidacion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idCpte);
			
			ResultSet rs = stmt.executeQuery();
		} catch (Exception e) {
			_log.error("Error al excluir liquidación integracion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public boolean incluirLiquidacion(Integer idCpte) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		boolean ret = false;
		try {
			String sql = "{call autorizaciones.integracion_incluir_liquidacion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,idCpte);
			
			ResultSet rs = stmt.executeQuery();
		} catch (Exception e) {
			_log.error("Error al incluir liquidación integracion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public List<CuentasInterbaking> obtenerCuentasExportarInterbankingEmail(String opDesde, String opHasta, String in )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentasInterbaking> list = null;
		try {
			String sql = "{call autorizaciones.altas_cuentas_interbanking_con_email(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,opDesde == null ? "0" : opDesde);
			stmt.setString(2,opHasta == null ? "0" : opHasta);
			stmt.setString(3,in == null ? "0" : in);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<CuentasInterbaking>();
			while (rs.next()) {
				CuentasInterbaking archivo = CuentasInterbaking.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error para exportar cuentas a interbanking", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<CuentasInterbaking>cuentasExportarInterbankingEmail( String in,String entidad )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentasInterbaking> list = null;
		try {
			
			String sql = "";
			if("UOMA".equals(entidad)){
				sql = "{call uoma.altas_cuentas_interbanking_con_email(?)}";
			}else if("AMTIMA".equals(entidad)) {
				sql = "{call altas_cuentas_interbanking_amtima_con_email(?)}";
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,in == null ? "0" : in);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<CuentasInterbaking>();
			while (rs.next()) {
				CuentasInterbaking archivo = CuentasInterbaking.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error para exportar cuentas a interbanking UOMA-AMTIMA", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public List<OrdenPagoOspim>  getOrdenesPagoGRALSinAvisoTransferencia()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> list = null;
		try {
			
			String sql = "{call autorizaciones.orden_pago_gral_sin_aviso_transferencia_interbanking()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<OrdenPagoOspim>();
			while (rs.next()) {
				OrdenPagoOspim archivo = new OrdenPagoOspim();
				archivo.setId(rs.getInt("ordenpago_id"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar OP para aviso transferencia GRAL", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
}

package ar.com.ospim.autorizaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl;

import ar.com.ospim.autorizaciones.beans.BusquedaSeguimientoSurFiltro;
import ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad;
import ar.com.ospim.autorizaciones.beans.DrogaPatologia;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurEstado;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurLoteProcesado;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurPrestador;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class SeguimientoSurServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(SeguimientoSurServiceImpl.class);

	@Deprecated
	public List<SeguimientoSur> getListaSeguimientoSur(int anio, int bimestre,int tipoExpediente,int autorizaOmint,String nroSolicitud,
			String codigoPresentado,String descripcionPresentado,String nroExpediente,String cuil,
			String inte,Date fechaDesde,Date fechaHasta,Boolean incluyeBajas,String estadoExpediente,String clase,String usuarioAlta,String estadoSSS
			, int claseNro ,Date fechaCorresDesde,Date fechaCorresHasta , int tipoExpedienteTercerizadora ,String nroCorrespondencia , 
			String convenioTercerizadora,Date fechaEstadoDesde,Date fechaEstadoHasta , String estadoHisSSS, Date fechaDdeSur , Date fechaHtaSur)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSur> list = null;
		try {
			String sql = "{call autorizaciones.busca_seguimiento_sur(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (anio>0) {
				stmt.setInt(1, anio);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (bimestre>0) {
				stmt.setInt(2, bimestre);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			if (tipoExpediente>0) {
				stmt.setInt(3, tipoExpediente);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (autorizaOmint>0) {
				stmt.setInt(4, autorizaOmint);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			if (null != nroSolicitud && nroSolicitud.trim().length() > 0) {
				stmt.setString(5, nroSolicitud);
			} else {
				stmt.setNull(5, Types.VARCHAR);
			}
			
			if (null != codigoPresentado && codigoPresentado.trim().length() > 0) {
				stmt.setString(6, codigoPresentado);
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			if (null != descripcionPresentado && descripcionPresentado.trim().length() > 0) {
				stmt.setString(7, descripcionPresentado);
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (null != nroExpediente && nroExpediente.trim().length() > 0) {
				stmt.setString(8, nroExpediente);
			} else {
				stmt.setNull(8, Types.VARCHAR);
			}
			
			if (null != cuil && cuil.trim().length() > 0) {
				stmt.setString(9, cuil);
			} else {
				stmt.setNull(9, Types.VARCHAR);
			}
			
			if (null != inte && inte.trim().length() > 0) {
				stmt.setInt(10, Integer.parseInt(inte));
			} else {
				stmt.setNull(10, Types.INTEGER);
			}
			
			if (null != fechaDesde) {
				stmt.setDate(11,  new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(11, Types.DATE);
			}
			
			if (null != fechaHasta) {
				stmt.setDate(12, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(12, Types.DATE);
			}
			
			if(incluyeBajas==true){
				stmt.setBoolean(13,true);
			}else{
				stmt.setNull(13,Types.BOOLEAN);
			}
			
			if (null != estadoExpediente && estadoExpediente.trim().length() > 0) {
				stmt.setString(14, estadoExpediente);
			} else {
				stmt.setNull(14, Types.VARCHAR);
			}
			
			if (null != clase && clase.trim().length() > 0) {
				stmt.setString(15, clase);
			} else {
				stmt.setNull(15, Types.VARCHAR);
			}
			
			if (null != usuarioAlta && usuarioAlta.trim().length() > 0) {
				stmt.setString(16, usuarioAlta);
			} else {
				stmt.setNull(16, Types.VARCHAR);
			}
			
			if (null != estadoSSS && estadoSSS.trim().length() > 0) {
				stmt.setString(17, estadoSSS);
			} else {
				stmt.setNull(17, Types.VARCHAR);
			}
			
			if ( claseNro >0 ) {
				stmt.setInt(18, claseNro );
			} else {
				stmt.setNull(18, Types.INTEGER);
			}
			
			if (null != fechaCorresDesde) {
				stmt.setDate(19,  new java.sql.Date(fechaCorresDesde.getTime()));
			} else {
				stmt.setNull(19, Types.DATE);
			}
			
			if (null != fechaCorresHasta) {
				stmt.setDate(20, new java.sql.Date(fechaCorresHasta.getTime()));
			} else {
				stmt.setNull(20, Types.DATE);
			}
			
			if ( tipoExpedienteTercerizadora  >0 ) {
				stmt.setInt(21, tipoExpedienteTercerizadora );
			} else {
				stmt.setNull(21, Types.INTEGER);
			}
		
			if (null != nroCorrespondencia && nroCorrespondencia.trim().length() > 0) {
				stmt.setString(22, nroCorrespondencia);
			} else {
				stmt.setNull(22, Types.VARCHAR);
			}
			
			if (null != convenioTercerizadora && convenioTercerizadora.trim().length() > 0) {
				stmt.setString(23, convenioTercerizadora );
			} else {
				stmt.setNull(23, Types.VARCHAR);
			}
			
			if (null != fechaEstadoDesde) {
				stmt.setDate(24,  new java.sql.Date(fechaEstadoDesde.getTime()));
			} else {
				stmt.setNull(24, Types.DATE);
			}
			
			if (null != fechaEstadoHasta) {
				stmt.setDate(25, new java.sql.Date(fechaEstadoHasta.getTime()));
			} else {
				stmt.setNull(25, Types.DATE);
			}
			
			
			if (null != estadoHisSSS && estadoHisSSS.trim().length() > 0) {
				stmt.setString(26, estadoHisSSS);
			} else {
				stmt.setNull(26, Types.VARCHAR);
			}
			
			if (null != fechaDdeSur) {
				stmt.setDate(27,  new java.sql.Date(fechaDdeSur.getTime()));
			} else {
				stmt.setNull(27, Types.DATE);
			}
			
			if (null != fechaHtaSur) {
				stmt.setDate(28, new java.sql.Date(fechaHtaSur.getTime()));
			} else {
				stmt.setNull(28, Types.DATE);
			}
			
//			if (null != filtro.getDdjj() && filtro.getDdjj()>0) {
//				stmt.setInt(29,filtro.getDdjj());
//			} else {
				stmt.setNull(29, Types.INTEGER);
//			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSur>();
			while (rs.next()) {
				SeguimientoSur archivo = SeguimientoSur.getMapping(rs);
				
//				archivo = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId(archivo.getId());
				
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Seguimiento Sur", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<SeguimientoSur> getListaSeguimientoSur(BusquedaSeguimientoSurFiltro filtro) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSur> list = null;
		
		try {
			String sql = "{call autorizaciones.busca_seguimiento_sur(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (filtro.getAnio()>0) {
				stmt.setInt(1, filtro.getAnio());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (filtro.getBimestre()>0) {
				stmt.setInt(2, filtro.getBimestre());
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			if (filtro.getTipoExpediente()>0) {
				stmt.setInt(3, filtro.getTipoExpediente());
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (filtro.getAutorizaOmint()>0) {
				stmt.setInt(4, filtro.getAutorizaOmint());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			if (StringUtils.checkNotEmpty(filtro.getNroSolicitud())) {
				stmt.setString(5, filtro.getNroSolicitud());
			} else {
				stmt.setNull(5, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getCodigoPresentado())) {
				stmt.setString(6, filtro.getCodigoPresentado());
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getDescripcionPresentado())) {
				stmt.setString(7, filtro.getDescripcionPresentado());
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getNroExpediente())) {
				stmt.setString(8, filtro.getNroExpediente());
			} else {
				stmt.setNull(8, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getCuil())) {
				stmt.setString(9, filtro.getCuil());
			} else {
				stmt.setNull(9, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getInte())) {
				stmt.setInt(10, Integer.parseInt(filtro.getInte()));
			} else {
				stmt.setNull(10, Types.INTEGER);
			}
			
			if (null != filtro.getFechaDde() /*fechaDesde*/) {
				stmt.setDate(11,  new java.sql.Date(/*fechaDesde.getTime()*/ filtro.getFechaDde().getTime()));
			} else {
				stmt.setNull(11, Types.DATE);
			}
			
			if (null != filtro.getFechaHta() /*fechaHasta*/) {
				stmt.setDate(12, new java.sql.Date(/*fechaHasta.getTime()*/ filtro.getFechaHta().getTime() ));
			} else {
				stmt.setNull(12, Types.DATE);
			}
			
			if(filtro.isIncluyeBajas() /*incluyeBajas==true*/){
				stmt.setBoolean(13, filtro.isIncluyeBajas() /*true*/);
			}else{
				stmt.setNull(13,Types.BOOLEAN);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getEstadoExpediente())) {
				stmt.setString(14, filtro.getEstadoExpediente());
			} else {
				stmt.setNull(14, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getClase())) {
				stmt.setString(15, filtro.getClase());
			} else {
				stmt.setNull(15, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getUsuarioAlta())) {
				stmt.setString(16, filtro.getUsuarioAlta());
			} else {
				stmt.setNull(16, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getEstadoSSS())) {
				stmt.setString(17, filtro.getEstadoSSS());
			} else {
				stmt.setNull(17, Types.VARCHAR);
			}
			
			if ( filtro.getClaseNro() >0 ) {
				stmt.setInt(18, filtro.getClaseNro() );
			} else {
				stmt.setNull(18, Types.INTEGER);
			}
			
			if (null != filtro.getFechaCorresDde()) {
				stmt.setDate(19,  new java.sql.Date(filtro.getFechaCorresDde().getTime()));
			} else {
				stmt.setNull(19, Types.DATE);
			}
			
			if (null != filtro.getFechaCorresHta()) {
				stmt.setDate(20, new java.sql.Date(filtro.getFechaCorresHta().getTime()));
			} else {
				stmt.setNull(20, Types.DATE);
			}
			
			if (filtro.getTipoTercerizadora() /*tipoExpedienteTercerizadora*/  >0 ) {
				stmt.setInt(21, filtro.getTipoTercerizadora() /*tipoExpedienteTercerizadora*/ );
			} else {
				stmt.setNull(21, Types.INTEGER);
			}
		
			if (StringUtils.checkNotEmpty(filtro.getNroCorrespondencia())) {
				stmt.setString(22, filtro.getNroCorrespondencia());
			} else {
				stmt.setNull(22, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getConvenioTercerizadora())) {
				stmt.setString(23, filtro.getConvenioTercerizadora() );
			} else {
				stmt.setNull(23, Types.VARCHAR);
			}
			
			if (null != filtro.getFechaEstadoDde() /*fechaEstadoDesde*/) {
				stmt.setDate(24,  new java.sql.Date(/*fechaEstadoDesde*/filtro.getFechaEstadoDde().getTime()));
			} else {
				stmt.setNull(24, Types.DATE);
			}
			
			if (null != filtro.getFechaEstadoHta() /*fechaEstadoHasta*/) {
				stmt.setDate(25, new java.sql.Date(/*fechaEstadoHasta*/filtro.getFechaEstadoHta().getTime()));
			} else {
				stmt.setNull(25, Types.DATE);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getEstadoHisSSS())) {
				stmt.setString(26, filtro.getEstadoHisSSS());
			} else {
				stmt.setNull(26, Types.VARCHAR);
			}
			
			if (null != filtro.getFechaDdeSur() /*fechaDdeSur*/) {
				stmt.setDate(27,  new java.sql.Date(/*fechaDdeSur*/filtro.getFechaDdeSur().getTime()));
			} else {
				stmt.setNull(27, Types.DATE);
			}
			
			if (null != filtro.getFechaHtaSur() /*fechaHtaSur*/ ) {
				stmt.setDate(28, new java.sql.Date(/*fechaHtaSur*/filtro.getFechaHtaSur().getTime()));
			} else {
				stmt.setNull(28, Types.DATE);
			}
			
			if (null != filtro.getDdjj() && filtro.getDdjj()>0) {
				stmt.setInt(29,filtro.getDdjj());
			} else {
				stmt.setNull(29, Types.INTEGER);
			}
			if (StringUtils.checkNotEmpty(filtro.getCodigoHIV())) {
				stmt.setString(30, filtro.getCodigoHIV());
			} else {
				stmt.setNull(30, Types.VARCHAR);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSur>();
			while (rs.next()) {
				SeguimientoSur archivo = SeguimientoSur.getMapping(rs);
							
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Seguimiento Sur", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<SeguimientoSur> getListaSeguimientoSurXls(BusquedaSeguimientoSurFiltro filtro) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSur> list = null;
		
		try {
			String sql = "{call autorizaciones.busca_seguimiento_sur_xls(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (filtro.getAnio()>0) {
				stmt.setInt(1, filtro.getAnio());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (filtro.getBimestre()>0) {
				stmt.setInt(2, filtro.getBimestre());
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			if (filtro.getTipoExpediente()>0) {
				stmt.setInt(3, filtro.getTipoExpediente());
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (filtro.getAutorizaOmint()>0) {
				stmt.setInt(4, filtro.getAutorizaOmint());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			if (StringUtils.checkNotEmpty(filtro.getNroSolicitud())) {
				stmt.setString(5, filtro.getNroSolicitud());
			} else {
				stmt.setNull(5, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getCodigoPresentado())) {
				stmt.setString(6, filtro.getCodigoPresentado());
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getDescripcionPresentado())) {
				stmt.setString(7, filtro.getDescripcionPresentado());
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getNroExpediente())) {
				stmt.setString(8, filtro.getNroExpediente());
			} else {
				stmt.setNull(8, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getCuil())) {
				stmt.setString(9, filtro.getCuil());
			} else {
				stmt.setNull(9, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getInte())) {
				stmt.setInt(10, Integer.parseInt(filtro.getInte()));
			} else {
				stmt.setNull(10, Types.INTEGER);
			}
			
			if (null != filtro.getFechaDde() /*fechaDesde*/) {
				stmt.setDate(11,  new java.sql.Date(/*fechaDesde.getTime()*/ filtro.getFechaDde().getTime()));
			} else {
				stmt.setNull(11, Types.DATE);
			}
			
			if (null != filtro.getFechaHta() /*fechaHasta*/) {
				stmt.setDate(12, new java.sql.Date(/*fechaHasta.getTime()*/ filtro.getFechaHta().getTime() ));
			} else {
				stmt.setNull(12, Types.DATE);
			}
			
			if(filtro.isIncluyeBajas() /*incluyeBajas==true*/){
				stmt.setBoolean(13, filtro.isIncluyeBajas() /*true*/);
			}else{
				stmt.setNull(13,Types.BOOLEAN);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getEstadoExpediente())) {
				stmt.setString(14, filtro.getEstadoExpediente());
			} else {
				stmt.setNull(14, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getClase())) {
				stmt.setString(15, filtro.getClase());
			} else {
				stmt.setNull(15, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getUsuarioAlta())) {
				stmt.setString(16, filtro.getUsuarioAlta());
			} else {
				stmt.setNull(16, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getEstadoSSS())) {
				stmt.setString(17, filtro.getEstadoSSS());
			} else {
				stmt.setNull(17, Types.VARCHAR);
			}
			
			if ( filtro.getClaseNro() >0 ) {
				stmt.setInt(18, filtro.getClaseNro() );
			} else {
				stmt.setNull(18, Types.INTEGER);
			}
			
			if (null != filtro.getFechaCorresDde()) {
				stmt.setDate(19,  new java.sql.Date(filtro.getFechaCorresDde().getTime()));
			} else {
				stmt.setNull(19, Types.DATE);
			}
			
			if (null != filtro.getFechaCorresHta()) {
				stmt.setDate(20, new java.sql.Date(filtro.getFechaCorresHta().getTime()));
			} else {
				stmt.setNull(20, Types.DATE);
			}
			
			if (filtro.getTipoTercerizadora() /*tipoExpedienteTercerizadora*/  >0 ) {
				stmt.setInt(21, filtro.getTipoTercerizadora() /*tipoExpedienteTercerizadora*/ );
			} else {
				stmt.setNull(21, Types.INTEGER);
			}
		
			if (StringUtils.checkNotEmpty(filtro.getNroCorrespondencia())) {
				stmt.setString(22, filtro.getNroCorrespondencia());
			} else {
				stmt.setNull(22, Types.VARCHAR);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getConvenioTercerizadora())) {
				stmt.setString(23, filtro.getConvenioTercerizadora() );
			} else {
				stmt.setNull(23, Types.VARCHAR);
			}
			
			if (null != filtro.getFechaEstadoDde() /*fechaEstadoDesde*/) {
				stmt.setDate(24,  new java.sql.Date(/*fechaEstadoDesde*/filtro.getFechaEstadoDde().getTime()));
			} else {
				stmt.setNull(24, Types.DATE);
			}
			
			if (null != filtro.getFechaEstadoHta() /*fechaEstadoHasta*/) {
				stmt.setDate(25, new java.sql.Date(/*fechaEstadoHasta*/filtro.getFechaEstadoHta().getTime()));
			} else {
				stmt.setNull(25, Types.DATE);
			}
			
			if (StringUtils.checkNotEmpty(filtro.getEstadoHisSSS())) {
				stmt.setString(26, filtro.getEstadoHisSSS());
			} else {
				stmt.setNull(26, Types.VARCHAR);
			}
			
			if (null != filtro.getFechaDdeSur() /*fechaDdeSur*/) {
				stmt.setDate(27,  new java.sql.Date(/*fechaDdeSur*/filtro.getFechaDdeSur().getTime()));
			} else {
				stmt.setNull(27, Types.DATE);
			}
			
			if (null != filtro.getFechaHtaSur() /*fechaHtaSur*/ ) {
				stmt.setDate(28, new java.sql.Date(/*fechaHtaSur*/filtro.getFechaHtaSur().getTime()));
			} else {
				stmt.setNull(28, Types.DATE);
			}
			
			if (null != filtro.getDdjj() && filtro.getDdjj()>0) {
				stmt.setInt(29,filtro.getDdjj());
			} else {
				stmt.setNull(29, Types.INTEGER);
			}
			if (StringUtils.checkNotEmpty(filtro.getCodigoHIV())) {
				stmt.setString(30, filtro.getCodigoHIV());
			} else {
				stmt.setNull(30, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSur>();
			while (rs.next()) {
				SeguimientoSur archivo = SeguimientoSur.getMapping(rs);
				archivo.setUltimoEstadoAltaFecha(rs.getDate("estado_alta_fecha"));
				archivo.setComprobanteNumero(rs.getString("array_comprobante"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Seguimiento Sur XLS", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public long insertaSeguimiento(SeguimientoSur seguimiento, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.inserta_seguimientosur(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, seguimiento.getAnio());
			stmt.setInt(2, seguimiento.getId_bimestre());
			stmt.setInt(3, seguimiento.getId_tipo_expediente());
			stmt.setInt(4, seguimiento.getId_autoriza_omint() );
			stmt.setString(5, seguimiento.getNro_solicitud_sur());
			if(seguimiento.getId_codigo_presentado()==null){
				stmt.setNull(6, Types.INTEGER);
			}else{
			   stmt.setInt(6,seguimiento.getId_codigo_presentado());
			}   
			stmt.setString(7,seguimiento.getNro_expediente());	
			stmt.setString(8, seguimiento.getCuilTitular());
			stmt.setInt(9,seguimiento.getIntegrante());
			stmt.setString(10, screenName);
			
			if(seguimiento.getTipoNomencladorId()==null){
				stmt.setNull(11, Types.INTEGER);
			}else{
			    stmt.setInt(11, seguimiento.getTipoNomencladorId());
			}    
			stmt.setDate(12, new java.sql.Date (seguimiento.getPresentacion_fecha().getTime()));
			stmt.setDouble(13,seguimiento.getImportePresentado());
			
			stmt.setString(14,seguimiento.getClaseExpediente());
			stmt.setInt(15,seguimiento.getNorma());
			stmt.setInt(16,seguimiento.getPatologia());
			stmt.setBoolean(17,seguimiento.getTutelaje());
			
			if(seguimiento.getId_tipo_expediente_tercerizadora() ==null || seguimiento.getId_tipo_expediente_tercerizadora()==-1 || seguimiento.getId_tipo_expediente() !=2){
				stmt.setNull(18, Types.INTEGER);
			}else{
			   stmt.setInt(18,seguimiento.getId_tipo_expediente_tercerizadora());
			}  
			
			if(seguimiento.getMesaEntrada_fecha()==null){
				  stmt.setNull(19, Types.DATE );	
			}else{
				  stmt.setDate(19, new java.sql.Date (seguimiento.getMesaEntrada_fecha().getTime()));
			}
			
			stmt.setString(20,seguimiento.getNro_correspondencia_sur() );
			
			if(seguimiento.getTutelaje_fecha()==null){
				  stmt.setNull(21, Types.DATE );	
			}else{
				  stmt.setDate(21, new java.sql.Date (seguimiento.getTutelaje_fecha().getTime()));
			}
			
			stmt.setString(22,seguimiento.getTutelaje_observaciones());
			
			stmt.setDouble(23,seguimiento.getTopeRecupero());
			
			if(seguimiento.getValorUnitario()==null){
			  stmt.setNull(24, Types.DOUBLE);	
			}else{
			   stmt.setDouble(24, seguimiento.getValorUnitario());
			}   
			stmt.setString(25, seguimiento.getUnidadMedidaDiagnostico());
			
			if(seguimiento.getCantidadMesesTratamiento()==null){
			   stmt.setNull(26,Types.INTEGER);
			} else {
				stmt.setInt(26,seguimiento.getCantidadMesesTratamiento());	
			}
			
			if(seguimiento.getDiagnostico_fecha() ==null){
				  stmt.setNull(27, Types.DATE );	
			}else{
				  stmt.setDate(27, new java.sql.Date (seguimiento.getDiagnostico_fecha().getTime()));
			}
			if(seguimiento.getFinTratamiento_fecha() ==null){
				  stmt.setNull(28, Types.DATE );	
			}else{
				  stmt.setDate(28, new java.sql.Date (seguimiento.getFinTratamiento_fecha().getTime()));
			}
			
			stmt.setString(29,seguimiento.getObservaciones());
			
			if(seguimiento.getCantidadAfiliados()==null){
				   stmt.setNull(30,Types.INTEGER);
			} else {
					stmt.setInt(30,seguimiento.getCantidadAfiliados());	
			}
			stmt.setDouble(31,seguimiento.getImporteReconocido());
			
			stmt.setString(32,seguimiento.getPeriodicidadHemofilia());
			
			if(seguimiento.getFecha_ingreso_area_sur() ==null){
				  stmt.setNull(33, Types.DATE );	
			}else{
				  stmt.setDate(33, new java.sql.Date (seguimiento.getFecha_ingreso_area_sur().getTime()));
			}
			
			stmt.setString(34,seguimiento.getCodigoHIV());
			
			stmt.setDouble(35,seguimiento.getProporcionalAdelantado());
			
	
			
			stmt.setDouble(36,seguimiento.getImporteOmint() );
			stmt.setDouble(37,seguimiento.getImporteOspim() );
			stmt.setDouble(38,seguimiento.getImportePrevencion() );
			stmt.setDouble(39,seguimiento.getImporteEnSalud());
			stmt.setDouble(40,seguimiento.getImporteCemic());

			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimientoSUR", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	public SeguimientoSur buscarSeguimientoSurPorId(
			int id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		
		SeguimientoSur seguimiento = null;
		try {
			String sql = "{ call autorizaciones.busca_seguimientosur_por_id(?) }";
							
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				seguimiento = SeguimientoSur.getMapping(rs);
			}
			
			sql = "{ call autorizaciones.busca_seguimientosur_tratamientos_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rst = stmt.executeQuery();
			while (rst.next()) {
				Integer idTratamiento= rst.getInt("id_tratamiento");
				TratamientoDiscapacidad td = (TratamientoDiscapacidad) TratamientoDiscapacidadServiceUtil.getTratamientoDiscapacidad(idTratamiento);
				TratamientoDiscapacidadSeguimiento tds = new TratamientoDiscapacidadSeguimiento();
				tds=tds.clonar(td);
				seguimiento.getTratamientos().add(tds);
			}
			

			sql = "{ call autorizaciones.busca_seguimientosur_detalles_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rsd = stmt.executeQuery();
			while (rsd.next()) {
				SeguimientoSurDetalle detalle = SeguimientoSurDetalle.getMapping(rsd);
				seguimiento.getDetalles().add(detalle);
			}
			
			sql = "{ call autorizaciones.busca_seguimientosur_comprobantes_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rsc = stmt.executeQuery();
			while (rsc.next()) {
				
				ComprobanteTratamientoDiscapacidad comprobante=ComprobanteTratamientoDiscapacidad.getMapping(rsc);
				
				for(TratamientoDiscapacidadSeguimiento td:seguimiento.getTratamientos()){
				    if(comprobante.getTratamientoId()==td.getId_tratamiento()){
				    	td.getComprobantes().add(comprobante);
				    }
				}
			}
			
			sql = "{ call autorizaciones.busca_seguimientosur_liquidaciones_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rsl = stmt.executeQuery();
			while (rsl.next()) {
				Medicamento medicamento = new Medicamento(); 
                ComprobanteTratamientoDiscapacidad comprobante=ComprobanteTratamientoDiscapacidad.getMapping(rsl);
                if("ME".equalsIgnoreCase(seguimiento.getClaseExpediente() )){
                   try{	
                	 if( rsl.getInt("troquel")!=0){
                       List <Medicamento> medicamentos = NomencladorServiceUtil.getBusquedaMedicamentos(rsl.getInt("troquel"), "");
                       if(medicamentos.size()>0){
                	     medicamento=medicamentos.get(0);
                       }
                	 }else{
                		Nomenclador nomenclador = NomencladorServiceUtil.buscarNomencladorPorId(rsl.getInt("id_prestacion"));
                     	medicamento.setDroga("");
                     	medicamento.setNombre(nomenclador.getDescripcion());
                     	medicamento.setTroquel(Integer.parseInt(nomenclador.getCodigo())); 
                	 }
                   }catch(Exception e){
                	   Nomenclador nomenclador = NomencladorServiceUtil.buscarNomencladorPorId(rsl.getInt("id_prestacion"));
                       medicamento.setDroga("");
                       medicamento.setNombre(nomenclador.getDescripcion());
                       medicamento.setTroquel(Integer.parseInt(nomenclador.getCodigo())); 
                   }
                }   
                if("DI".equalsIgnoreCase(seguimiento.getClaseExpediente()) || //Agregado por cambio de Comportamieto de Discapacidad
                   "PR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
                   "OT".equalsIgnoreCase(seguimiento.getClaseExpediente()) || 
                   "HI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
    			   "HE".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
    			   "DR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
    			   "FE".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
    			   "DB".equalsIgnoreCase(seguimiento.getClaseExpediente())){
                	Nomenclador nomenclador = NomencladorServiceUtil.buscarNomencladorPorId(rsl.getInt("id_prestacion"));
                	medicamento.setDroga("");
                	medicamento.setNombre(nomenclador.getDescripcion());
                	medicamento.setTroquel(Integer.parseInt(nomenclador.getCodigo()));
                }
                Liquidacion l = EditarLiquidacionServiceUtil.getLiquidacionEntry(comprobante.getLiquidacionPrestacion().getId_liquidacion());
                comprobante.getLiquidacionPrestacion().setLiquidacion(l);
                comprobante.setMedicamento(medicamento);
                
				seguimiento.getLiquidaciones().add(comprobante);
				
			}
			
			sql = "{ call autorizaciones.busca_seguimientosur_estados_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rse = stmt.executeQuery();
			while (rse.next()) {
				SeguimientoSurEstado detalle = SeguimientoSurEstado.getMapping(rse);
				seguimiento.getEstados().add(detalle);
			}
			
			
			sql = "{ call autorizaciones.busca_seguimientosur_prestadores_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rsp = stmt.executeQuery();
			while (rsp.next()) {
				SeguimientoSurPrestador detalle = SeguimientoSurPrestador.getMapping(rsp);
				seguimiento.getPrestadores().add(detalle);
			}
			
			
			sql = "{ call autorizaciones.busca_seguimientosur_prestaciones_nomenclador_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rsn = stmt.executeQuery();
			while (rsn.next()) {
				Nomenclador detalle = NomencladorServiceUtil.buscarNomencladorPorId(rsn.getInt("id_prestacion"));
				seguimiento.getCodigosPresentados().add(detalle);
			}
			
			
			sql = "{ call autorizaciones.busca_seguimientosur_comprobantes_liquidados_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rscl = stmt.executeQuery();
			while (rscl.next()) {
				SeguimientoSurComprobante detalle = SeguimientoSurComprobante.getMapping(rscl);
				seguimiento.getComprobantes().add(detalle);
			}
			
		} catch (Exception e) {
			_log.error("error al buscar Seguimiento SUR por Id", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return seguimiento;
	}
	
	
	public long insertaSeguimientoTratamiento(Integer seguimientoId,TratamientoDiscapacidadSeguimiento tratamiento,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			String sql = "{call autorizaciones.inserta_seguimientosur_tratamiento(?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2, tratamiento.getId_tratamiento());
			stmt.setString(3, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - tratamiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_seguimiento;
	}

	public long insertaSeguimientoDetalle(Integer seguimientoId,SeguimientoSurDetalle detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		int id_seguimiento = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.inserta_seguimientosur_detalle(?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			
			if(detalle.getFechaCarga() != null){
				stmt.setDate(2,new java.sql.Date(detalle.getFechaCarga().getTime()));
			}else{
				stmt.setNull(2, Types.DATE);
			}
			
			if(detalle.getEstadoId()  != null){
			    stmt.setInt(3, detalle.getEstadoId()); 
			}else{
				stmt.setNull(3, Types.INTEGER);
			}
			
			if(detalle.getFechaNotificacion() != null){
			    stmt.setDate(4,new java.sql.Date(detalle.getFechaNotificacion().getTime()));
			}else{
				stmt.setNull(4, Types.DATE);
			}
			
			if(detalle.getObservaciones()!=null){
			  stmt.setString(5,detalle.getObservaciones());
			}else{
			  stmt.setNull(5, Types.VARCHAR);	
			}
			
			stmt.setString(6, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - detalle ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_seguimiento;
	}

	
	public int updateSeguimiento(SeguimientoSur seguimiento,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = seguimiento.getId();
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
	
			String sql = "{call autorizaciones.update_seguimientosur(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimiento.getId());
			stmt.setInt(2,seguimiento.getAnio());
			stmt.setInt(3,seguimiento.getId_bimestre());
			stmt.setInt(4,seguimiento.getId_tipo_expediente());
			stmt.setInt(5,seguimiento.getId_autoriza_omint());
			stmt.setString(6,seguimiento.getNro_solicitud_sur());
			if(seguimiento.getId_codigo_presentado()==null){
				stmt.setNull(7, Types.INTEGER);
			}else{
			   stmt.setInt(7,seguimiento.getId_codigo_presentado());
			} 
			stmt.setString(8, seguimiento.getNro_expediente());
			stmt.setString(9,seguimiento.getCuilTitular());
			stmt.setInt(10, seguimiento.getIntegrante());
			if(seguimiento.getTipoNomencladorId()==null){
				stmt.setNull(11, Types.INTEGER);
			}else{
			    stmt.setInt(11, seguimiento.getTipoNomencladorId());
			} 
			stmt.setString(12, screenName);
			stmt.setDate(13, new java.sql.Date (seguimiento.getPresentacion_fecha().getTime()));
			stmt.setDouble(14,seguimiento.getImportePresentado());
			stmt.setString(15,seguimiento.getClaseExpediente());
			stmt.setInt(16,seguimiento.getNorma());
			stmt.setInt(17,seguimiento.getPatologia());
			stmt.setBoolean(18,seguimiento.getTutelaje());	
			if(seguimiento.getId_tipo_expediente_tercerizadora()==null || seguimiento.getId_tipo_expediente() !=2){
				stmt.setNull(19, Types.INTEGER);
			}else{
			   stmt.setInt(19,seguimiento.getId_tipo_expediente_tercerizadora());
			} 
			
			if(seguimiento.getMesaEntrada_fecha()==null){
			  stmt.setNull(20, Types.DATE );	
			}else{
			  stmt.setDate(20, new java.sql.Date (seguimiento.getMesaEntrada_fecha().getTime()));
			}  
			
			stmt.setString(21, seguimiento.getNro_correspondencia_sur());
			
			
			if(seguimiento.getTutelaje_fecha()==null){
				  stmt.setNull(22, Types.DATE );	
			}else{
				  stmt.setDate(22, new java.sql.Date (seguimiento.getTutelaje_fecha().getTime()));
			}  
			
			stmt.setString(23, seguimiento.getTutelaje_observaciones());
			
			stmt.setDouble(24,seguimiento.getTopeRecupero());
			
			if(seguimiento.getValorUnitario()==null){
			  stmt.setNull(25, Types.DOUBLE);	
			}else{
			  stmt.setDouble(25, seguimiento.getValorUnitario());
			}   
			stmt.setString(26, seguimiento.getUnidadMedidaDiagnostico());
				
			if(seguimiento.getCantidadMesesTratamiento()==null){
			   stmt.setNull(27,Types.INTEGER);
			} else {
				stmt.setInt(27,seguimiento.getCantidadMesesTratamiento());	
			}

			if(seguimiento.getDiagnostico_fecha() ==null){
				  stmt.setNull(28, Types.DATE );	
			}else{
				  stmt.setDate(28, new java.sql.Date (seguimiento.getDiagnostico_fecha().getTime()));
			}
			if(seguimiento.getFinTratamiento_fecha() ==null){
				  stmt.setNull(29, Types.DATE );	
			}else{
				  stmt.setDate(29, new java.sql.Date (seguimiento.getFinTratamiento_fecha().getTime()));
			}
			
			stmt.setString(30, seguimiento.getObservaciones());
			
			if(seguimiento.getCantidadAfiliados()==null){
				   stmt.setNull(31,Types.INTEGER);
			} else {
					stmt.setInt(31,seguimiento.getCantidadAfiliados());	
			}
			stmt.setDouble(32,seguimiento.getImporteReconocido());
			
			stmt.setString(33, seguimiento.getPeriodicidadHemofilia());
			
			if(seguimiento.getFecha_ingreso_area_sur() ==null){
				  stmt.setNull(34, Types.DATE );	
			}else{
				  stmt.setDate(34, new java.sql.Date (seguimiento.getFecha_ingreso_area_sur().getTime()));
			}
			
			stmt.setString(35, seguimiento.getCodigoHIV());
			stmt.setDouble(36,seguimiento.getProporcionalAdelantado());
			stmt.setDouble(37,seguimiento.getImporteOmint() );
			stmt.setDouble(38,seguimiento.getImporteOspim() );
			stmt.setDouble(39,seguimiento.getImportePrevencion() );
			stmt.setDouble(40,seguimiento.getImporteEnSalud() );
			stmt.setDouble(41,seguimiento.getImporteCemic() );
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimientoSur", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

	
	public long eliminaSeguimientoTratamiento(int idseguimiento,TratamientoDiscapacidad trat,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.elimina_seguimientosur_tratamientodiscapacidad(?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idseguimiento);
			stmt.setInt(2, trat.getId_tratamiento());
			stmt.setString(3, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar tratamiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

	public long updateSeguimientoDetalle(Integer seguimientoId,SeguimientoSurDetalle detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.update_seguimientosur_detalle(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			if(detalle.getFechaCarga() != null){
				stmt.setDate(2,new java.sql.Date(detalle.getFechaCarga().getTime()));
			}else{
				stmt.setNull(2, Types.DATE);
			}
			
			if(detalle.getFechaEnvio() != null){
			    stmt.setDate(3,new java.sql.Date(detalle.getFechaEnvio().getTime()));
			}else{
				stmt.setNull(3, Types.DATE);
			}
			
			if(detalle.getFechaNotificacion() != null){
			    stmt.setDate(4,new java.sql.Date(detalle.getFechaNotificacion().getTime()));
			}else{
				stmt.setNull(4, Types.DATE);
			}
			
			if(detalle.getFechaRespuesta() != null){
			   stmt.setDate(5,new java.sql.Date(detalle.getFechaRespuesta().getTime()));
			}else{
			   stmt.setNull(5, Types.DATE);	
			}
			
			if(detalle.getObservaciones()!=null){
			  stmt.setString(6,detalle.getObservaciones());
			}else{
			  stmt.setNull(6, Types.VARCHAR);	
			}
			stmt.setString(7, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar seguimiento sur - detalle ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	public long eliminaSeguimientoDetalle(int idDetalle,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.elimina_seguimientosur_detalle(?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idDetalle);
			stmt.setString(2, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Seguimiento SUR detalle ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	
	public boolean existeSeguimientoSURBimestre(String cuil_titular, String inte, String bimestre)
			throws SystemException {

		_log.debug("existeSeguimientoSURBimestre- cuil_titular: "+cuil_titular + " inte: " + inte + " bimestre: " +bimestre);
		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call autorizaciones.busca_seguimientosur_por_cuil_inte_bimestre(?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, Integer.valueOf(inte));
			stmt.setInt(3, Integer.valueOf(bimestre));
			
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar duplicado Seguimiento SUR Bimestre", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}

	
	public long eliminaSeguimiento(int idSeguimiento,String screenName,String motivo,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.elimina_seguimientosur(?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idSeguimiento);
			stmt.setString(2, screenName);
			stmt.setString(3, motivo);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar seguimiento", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

	public long recuperaSeguimiento(int idSeguimiento,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
				
			String sql = "{call autorizaciones.recupera_seguimientosur(?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idSeguimiento);
			stmt.setString(2,screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al recuperar seguimiento", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	
	public List<ComprobanteTratamientoDiscapacidad> recuperaComprobantesTratamientos(String cuil,int inte,int prestacion,Date periodo_dde,Date periodo_hta,String cuitPrestador,String descPrestador,Integer idDroga)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteTratamientoDiscapacidad> list = null;
		try {
			String sql = "{call autorizaciones.busca_comprobantes_seguimiento_sur(?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (!"".equalsIgnoreCase(cuil)) {
				stmt.setString(1, cuil);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (inte>=0) {
				stmt.setInt(2, inte);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			
			if (prestacion>0) {
				stmt.setInt(3, prestacion);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			
			
			if (periodo_dde!=null) {
				  stmt.setDate(4, new java.sql.Date(periodo_dde.getTime() ));
			} else {
				stmt.setNull(4, Types.DATE);
			}
			
			if (periodo_hta!=null) {
				stmt.setDate(5, new java.sql.Date(periodo_hta.getTime()));
			} else {
				stmt.setNull(5, Types.DATE);
			}
			
			if (!"".equalsIgnoreCase(cuitPrestador)) {
				stmt.setString(6, cuitPrestador);
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			if (!"".equalsIgnoreCase(descPrestador)) {
				stmt.setString(7, descPrestador); ;
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (idDroga!=null && idDroga!=0) {
				stmt.setInt(8, idDroga); ;
			} else {
				stmt.setNull(8, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ComprobanteTratamientoDiscapacidad>();
			while (rs.next()) {
				LiquidacionPrestacion lp = LiquidacionPrestacion.getMapping(rs,"");
				try{
				 Liquidacion l = EditarLiquidacionServiceUtil.getLiquidacionEntry(lp.getId_liquidacion());
				 lp.setLiquidacion(l);
				}catch(Exception e){
				 continue;
				}
				
				Prestacion prest= new Prestacion();
				prest.setDescripcion("");
				if(lp.getId_prestacion()!=0){
					Nomenclador nomenclador = NomencladorServiceUtil.buscarNomencladorPorId(lp.getId_prestacion());
					if(nomenclador!=null){
						prest.setDescripcion(nomenclador.getDescripcion());
					}
				}
				if(lp.getPrestacion()==null){
					lp.setPrestacion(prest);
				}
				
				ComprobanteTratamientoDiscapacidad archivo = new ComprobanteTratamientoDiscapacidad();
				archivo.setLiquidacionPrestacion(lp);
				
				Prestador p = new Prestador(rs.getString("prestadorcuit"),rs.getInt("id_prestador"),rs.getString("prestadordescripcion"));
				archivo.setPrestador(p);
				archivo.setSeguimientoId(rs.getInt("id_seguimiento"));
				
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Tratamiento", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public ComprobanteTratamientoDiscapacidad recuperaLiquidacionPrestacion(int idLiquidacion,int prestacion,int orden)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		ComprobanteTratamientoDiscapacidad tdc= new ComprobanteTratamientoDiscapacidad();
		LiquidacionPrestacion lp = new LiquidacionPrestacion();
		Prestador p =new Prestador();
		try {
			String sql = "{call autorizaciones.busca_liquidacion_prestacion(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (idLiquidacion>0) {
				stmt.setInt(1, idLiquidacion);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (prestacion>0) {
				stmt.setInt(2, prestacion);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			
			if (orden>=0) {
				stmt.setInt(3, orden);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				lp = LiquidacionPrestacion.getMapping(rs,"");
				Liquidacion l = EditarLiquidacionServiceUtil.getLiquidacionEntry(lp.getId_liquidacion());
				lp.setLiquidacion(l);
                p.setDescripcion(rs.getString("prestadordescripcion"));
                p.setCuit(rs.getString("prestadorcuit"));
                p.setId_prestador(rs.getInt("id_prestador"));
				tdc.setLiquidacionPrestacion(lp);
				tdc.setPrestador(p);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Tratamiento", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tdc;
	}
	
	public long insertaSeguimientoTratamientoComprobante(int seguimientoId,SeguimientoSur seguimiento,ComprobanteTratamientoDiscapacidad comprobante,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			String sql = "{call autorizaciones.inserta_seguimientosur_tratamiento_comprobante(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2, comprobante.getTratamientoId());
			stmt.setInt(3, comprobante.getLiquidacionPrestacion().getId_liquidacion());
			stmt.setInt(4, comprobante.getLiquidacionPrestacion().getOrden());
			stmt.setString(5,seguimiento.getCuilTitular());
			stmt.setInt(6,seguimiento.getIntegrante());
			stmt.setString(7, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - tratamiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	public long cierraSeguimiento(int idSeguimiento,Date fechaCierre,String motivoCierre,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			String sql = "{call autorizaciones.cierra_seguimientosur(?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idSeguimiento);
			stmt.setDate(2, new java.sql.Date(fechaCierre.getTime()));
			stmt.setString(3, motivoCierre);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al cerrar seguimiento", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	public boolean existeMovimientoBancoSeguimientoSUR(String nroExpediente)
			throws SystemException {

		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call autorizaciones.busca_seguimientosur_mov_bancario(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, nroExpediente);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar movimiento bancario Seguimiento SUR ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}

	public MovimientoBancario traeMovimientoBancoSeguimientoSUR(String nroExpediente)
			throws SystemException {

		MovimientoBancario result = new MovimientoBancario();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call autorizaciones.busca_seguimientosur_mov_bancario(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, nroExpediente);
			ResultSet rs = stmt.executeQuery();

			
			if (rs.next()) {
				
				result.setFecha_movimiento(rs.getDate("fecha_movimiento"));
				result.setNro_comprobante(rs.getString("nro_compro"));
				result.setImporte(rs.getBigDecimal("importe_movimiento"));
				
				CuentaBancaria cb =new CuentaBancaria(rs.getInt("id_cta_bcria"),rs.getInt("nro_cuenta"), 
						rs.getInt("sucursal"),"",rs.getInt("banco_id"),rs.getString("banco_descripcion"),"" );
				result.setCta_bcria(cb);
				
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar movimiento bancario Seguimiento SUR ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}

	
	public Date[] traeFechasBimestreSeguimientoSur(Integer idBimestre)
			throws SystemException {

		Date[] result = {null,null};
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call autorizaciones.busca_bimestre_por_id(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idBimestre);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				result[0]=rs.getDate("fechainicio");
				result[1]=rs.getDate("fechafin");
			}
			
		} catch (Exception e) {
			_log.error("Error al traer fecha de tabla bimestre ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}

	public long insertaSeguimientoMedicamentoLiquidacion(Integer seguimientoId,ComprobanteTratamientoDiscapacidad comprobante,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.inserta_seguimientosur_medicamentoliquidacion(?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2,comprobante.getLiquidacionPrestacion().getId_liquidacion());
			stmt.setInt(3,comprobante.getLiquidacionPrestacion().getId_prestacion());
			stmt.setInt(4,comprobante.getLiquidacionPrestacion().getOrden());
			stmt.setString(5, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - medicamento liquidacion ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	
	public long eliminaSeguimientoMedicamentoLiquidacion(int idseguimiento,ComprobanteTratamientoDiscapacidad trat,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			String sql = "{call autorizaciones.elimina_seguimientosur_medicamentoliquidacion(?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idseguimiento);
			stmt.setInt(2, trat.getLiquidacionPrestacion().getId_liquidacion() );
			stmt.setInt(3, trat.getLiquidacionPrestacion().getId_prestacion());
			stmt.setInt(4, trat.getLiquidacionPrestacion().getOrden());
			stmt.setString(5, screenName);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar tratamiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

	public List<Medicamento> getBusquedaProtesisYOtros(int troquel, String nombre) {
		Connection con = null;
		CallableStatement stmt=null;
		List<Medicamento> listaMedicamentos= null;
		try {
			String sql = "{call autorizaciones.buscar_nomenclador_protesis_y_otros(?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(troquel!=0){
				stmt.setInt(1, troquel);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			stmt.setString(2, (null!=nombre&&nombre.trim().equals(""))?null:nombre);
			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<Medicamento>();
			while (rs.next()) {
				Medicamento bp = new Medicamento();
				bp.setId_medicamento(rs.getInt("id_prestacion"));
				bp.setTroquel(rs.getInt("troquel"));
				bp.setNombre(rs.getString("descripcion"));
				bp.setPresentacion(rs.getString("descripcion"));
				bp.setDroga("");
				bp.setCod_barra("");
				bp.setPrecio(BigDecimal.ZERO);
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}
	
	public List<DrogaPatologia> traeDrogasPatologia(Integer id) {
		Connection con = null;
		CallableStatement stmt=null;
		List<DrogaPatologia> listaDrogas= null;
		try {
			String sql="";
			sql = "{call autorizaciones.buscar_drogas_patologia(?)}";	
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(id!=null && id!=0){
				stmt.setInt(1, id);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			listaDrogas = new ArrayList<DrogaPatologia>();
			while (rs.next()) {
				DrogaPatologia bp = DrogaPatologia.getMapping(rs);
				listaDrogas.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDrogas;
	}
	
	public List<DrogaPatologia> traePatologias(Integer id) {
		Connection con = null;
		CallableStatement stmt=null;
		List<DrogaPatologia> listaDrogas= null;
		try {
			String sql="";
			sql = "{call autorizaciones.buscar_patologia(?)}";	

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(id!=null && id!=0){
				stmt.setInt(1, id);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			listaDrogas = new ArrayList<DrogaPatologia>();
			while (rs.next()) {
				DrogaPatologia bp =new DrogaPatologia();
				bp.setId(rs.getInt("id_patologia"));
				bp.setPatologia(rs.getString("descripcion"));		
				listaDrogas.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDrogas;
	}
	
	public List<DrogaPatologia> traeNormasSeguimientoSur(Integer id) {
		Connection con = null;
		CallableStatement stmt=null;
		List<DrogaPatologia> listaNormas= null;
		try {
			String sql = "{call autorizaciones.buscar_normas_seguimiento_sur(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(id!=null && id!=0){
				stmt.setInt(1, id);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			listaNormas = new ArrayList<DrogaPatologia>();
			while (rs.next()) {
				DrogaPatologia bp = new DrogaPatologia();
				bp.setId(rs.getInt("id"));
				bp.setDrogaDescripcion(rs.getString("descripcion"));
				listaNormas.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaNormas;
	}
	
	public boolean existeSeguimientoSURNroExpediente(String nroExpediente, Integer idSeguimiento)
			throws SystemException {

		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call autorizaciones.valida_seguimientosur_por_nro_expediente(?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, nroExpediente);
			if(idSeguimiento!=null){
				stmt.setInt(2, idSeguimiento);
			}else{
				stmt.setNull(2, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar Nro Expediente Seguimiento SUR", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}
	
	public int updateComprobanteSeguimiento(SeguimientoSur seguimiento,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = seguimiento.getId();
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.update_comprobante_seguimientosur(?,?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimiento.getId());
			stmt.setString(2, seguimiento.getComprobanteTipo());
			stmt.setString(3,seguimiento.getComprobanteLetra());
			stmt.setInt(4,seguimiento.getComprobanteSucursal());
			stmt.setString(5,seguimiento.getComprobanteNumero());
			stmt.setDate(6, new java.sql.Date (seguimiento.getComprobanteFecha().getTime()));
			stmt.setDouble(7,seguimiento.getComprobanteImporte());
			stmt.setString(8, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al grabar comprobante tercerizadora seguimientoSur", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	
	public long insertaSeguimientoEstado(Integer seguimientoId,SeguimientoSurEstado detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.inserta_seguimientosur_estado(?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2,detalle.getIdEstado());
			
			if(detalle.getFechaEstado() != null){
			    stmt.setDate(3,new java.sql.Date(detalle.getFechaEstado().getTime()));
			}else{
				stmt.setNull(3, Types.DATE);
			}
			
			if(detalle.getObservaciones()!=null){
			  stmt.setString(4,detalle.getObservaciones());
			}else{
			  stmt.setNull(4, Types.VARCHAR);	
			}
			
			stmt.setString(5,detalle.getUsuario());
			
			stmt.setInt(6,detalle.getIdMotivo() );
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - estado ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}	
	public long insertaSeguimientoPrestador(Integer seguimientoId,SeguimientoSurPrestador detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
			
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.inserta_seguimientosur_prestador(?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2,detalle.getIdPrestador());
			
			if(detalle.getFechaEstado() != null){
			    stmt.setDate(3,new java.sql.Date(detalle.getFechaEstado().getTime()));
			}else{
				stmt.setNull(3, Types.DATE);
			}
			
			if(detalle.getObservaciones()!=null){
			  stmt.setString(4,detalle.getObservaciones());
			}else{
			  stmt.setNull(4, Types.VARCHAR);	
			}
			
			stmt.setString(5,detalle.getUsuario());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - prestador ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}


//DS
	 
	public long updateSeguimientoEstado(Integer seguimientoId,SeguimientoSurEstado detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.update_seguimientosur_estado(?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2,detalle.getIdEstado());
			
			if(detalle.getFechaEstado() != null){
				stmt.setDate(3,new java.sql.Date(detalle.getFechaEstado().getTime()));
			}else{
				stmt.setNull(3, Types.DATE);
			}
			
			
			if(detalle.getUsuario() != null){
			    stmt.setString(4,detalle.getUsuario());
			}else{
				stmt.setNull(4, Types.DATE);
			}
			
			if(detalle.getObservaciones()!=null){
			  stmt.setString(5,detalle.getObservaciones());
			}else{
			  stmt.setNull(5, Types.VARCHAR);	
			}
			stmt.setString(6, screenName);
			
			stmt.setInt(7,detalle.getIdMotivo());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar seguimiento sur - estado ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	public long eliminaSeguimientoEstado(int idDetalle,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.elimina_seguimientosur_estado(?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idDetalle);
			stmt.setString(2, screenName);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Seguimiento SUR estado ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

//////
//////
	
	public long updateSeguimientoPrestador(Integer seguimientoId,SeguimientoSurPrestador detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			String sql = "{call autorizaciones.update_seguimientosur_prestador(?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2,detalle.getIdPrestador());
			
			if(detalle.getFechaEstado() != null){
				stmt.setDate(3,new java.sql.Date(detalle.getFechaEstado().getTime()));
			}else{
				stmt.setNull(3, Types.DATE);
			}
			
			
			if(detalle.getUsuario() != null){
			    stmt.setString(4,detalle.getUsuario());
			}else{
				stmt.setNull(4, Types.DATE);
			}
			
			if(detalle.getObservaciones()!=null){
			  stmt.setString(5,detalle.getObservaciones());
			}else{
			  stmt.setNull(5, Types.VARCHAR);	
			}
			stmt.setString(6, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar seguimiento sur - prestador ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	public long eliminaSeguimientoPrestador(int idDetalle,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.elimina_seguimientosur_prestador(?,?)}";
			
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idDetalle);
			stmt.setString(2, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Seguimiento SUR prestador ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_seguimiento;
	}

//DS	
	public boolean realizaBajaSeguimientoSUR(int idEstado,Connection connectionParameter)
			throws SystemException, SQLException {

		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.busca_estado_seguimiento_sur(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idEstado);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = rs.getBoolean(1) ;
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Estado Seguimiento Sur ", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return result;
	}
	
	public List<Medicamento> getBusquedaDrogadependencia(int troquel, String nombre) {
		Connection con = null;
		CallableStatement stmt=null;
		List<Medicamento> listaMedicamentos= null;
		try {
			String sql = "{call autorizaciones.buscar_nomenclador_drogadependencia(?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(troquel!=0){
				stmt.setInt(1, troquel);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			stmt.setString(2, (null!=nombre&&nombre.trim().equals(""))?null:nombre);
			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<Medicamento>();
			while (rs.next()) {
				Medicamento bp = new Medicamento();
				bp.setId_medicamento(rs.getInt("id_prestacion"));
				bp.setTroquel(rs.getInt("troquel"));
				bp.setNombre(rs.getString("descripcion"));
				bp.setPresentacion(rs.getString("descripcion"));
				bp.setDroga("");
				bp.setCod_barra("");
				bp.setPrecio(BigDecimal.ZERO);
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}
	
	public long eliminaSeguimientoTratamientoComprobante(int seguimientoId,SeguimientoSur seguimiento,ComprobanteTratamientoDiscapacidad comprobante,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.elimina_seguimientosur_tratamiento_comprobante(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2, comprobante.getTratamientoId());
			stmt.setInt(3, comprobante.getLiquidacionPrestacion().getId_liquidacion());
			stmt.setInt(4, comprobante.getLiquidacionPrestacion().getOrden());
			stmt.setString(5,seguimiento.getCuilTitular());
			stmt.setInt(6,seguimiento.getIntegrante());
			stmt.setString(7, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - tratamiento ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

	public Map<String,Object> datosEstadoSeguimientoSUR(int idEstado,Connection connectionParameter)
			throws SystemException, SQLException {

		Map<String,Object> result = new HashMap<String,Object>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.busca_estado_seguimiento_sur(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idEstado);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result.put("enviaemail", rs.getBoolean("enviaemail"));
				result.put("destinatarioemail",rs.getString("destinoemail"));
				result.put("asuntoemail", rs.getString("asuntoemail"));
				result.put("mensajeemail", rs.getString("mensajeemail"));
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Estado Seguimiento Sur ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
			
		}
		
		return result;
	}

	
	public SeguimientoSurEstado ultimoEstadoSeguimientoSUR(int idSeguimiento,Connection connectionParameter)
			throws SystemException, SQLException {

		SeguimientoSurEstado result = new SeguimientoSurEstado();
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{ call autorizaciones.busca_seguimientosur_ultimo_estado_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idSeguimiento);
			ResultSet rse = stmt.executeQuery();
			while (rse.next()) {
				result = SeguimientoSurEstado.getMapping(rse);
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar Estado Seguimiento Sur ", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return result;
	}
	
	public long insertaSeguimientoCodigoPresentado(Integer seguimientoId,Nomenclador detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
			
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.inserta_seguimientosur_codigo_nomenclador(?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2,detalle.getId_prestacion());
			stmt.setString(3,screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - prestador ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

	public long eliminaSeguimientoCodigoPresentado(int idSeguimiento, Nomenclador nomenclador,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.elimina_seguimientosur_codigo_presentado(?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idSeguimiento);
			stmt.setInt(2, nomenclador.getId_prestacion());
			stmt.setString(3, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Seguimiento SUR codigo presentado ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_seguimiento;
	}
	
	
	public List<SeguimientoSurComprobante> recuperaComprobantesLiquidados(
			Integer idPrestador,
			String cuit,
			String razonSocial,
			String tipo,
			String letra,
			Integer ptoVta,
			String nro,
			Date fechaEmision,
			Date fechaRecibido,
            Date fechaVencimiento)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSurComprobante> list = null;
		try {
			String sql = "{call autorizaciones.busca_comprobantes_liquidados_seguimiento_sur(?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (idPrestador>0) {
				stmt.setInt(1, idPrestador);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (!"".equalsIgnoreCase(cuit)) {
				stmt.setString(2, cuit);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if (!"".equalsIgnoreCase(razonSocial)) {
				stmt.setString(3, razonSocial); ;
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			
			if (!"".equalsIgnoreCase(tipo)) {
				stmt.setString(4,tipo); ;
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			
			if (!"".equalsIgnoreCase(letra)) {
				stmt.setString(5,letra); ;
			} else {
				stmt.setNull(5, Types.VARCHAR);
			}
			
			if (ptoVta>0) {
				stmt.setInt(6, ptoVta);
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			
			if (!"".equalsIgnoreCase(nro)) {
				stmt.setString(7, nro); ;
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (fechaEmision!=null) {
				  stmt.setDate(8, new java.sql.Date(fechaEmision.getTime() ));
			} else {
				stmt.setNull(8, Types.DATE);
			}
			
			if (fechaRecibido!=null) {
				  stmt.setDate(9, new java.sql.Date(fechaRecibido.getTime() ));
			} else {
				stmt.setNull(9, Types.DATE);
			}
			
			if (fechaVencimiento!=null) {
				  stmt.setDate(10, new java.sql.Date(fechaVencimiento.getTime() ));
			} else {
				stmt.setNull(10, Types.DATE);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSurComprobante>();
			while (rs.next()) {
				
				SeguimientoSurComprobante comprobante = new SeguimientoSurComprobante(rs.getInt("id_punto_venta"),
						rs.getString("compro_tipo"),
						rs.getString("compro_nro"),
						rs.getString("cuit"),
						rs.getDate("fecha_emision"),
						rs.getDate("fecha_recibido"),
						rs.getBigDecimal("importe_original"),
						rs.getString("compro_letra"), 
						rs.getInt("compro_sucu"), 
						rs.getDate("fecha_vencimiento"),
						rs.getInt("id_seguimiento"));
				
				Empresa empresa= new Empresa(rs.getString("cuit"),rs.getString("id_prestador"),rs.getString("razon_social"));
				comprobante.setAcreedorEmpresa(empresa);
				
				list.add(comprobante);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Comprobantes Liquidados Seguimiento SUR", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public Comprobante getComprobante(Comprobante comp, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_comprobante(?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_comprobante_amtima(?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_comprobante_uoma(?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comprobante = Comprobante.getMapping(rs);
				comprobante.setPagado(rs.getBoolean("pagado"));
				comprobante.setNroAnticipo(rs.getInt("nro_anticipo"));
				if (entidad == WebKeysGlobal.UOMA) {
					comprobante.setCantCuotas(rs.getInt("cant_cuotas"));
				}
				return comprobante;
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public long insertaSeguimientoComprobante(Integer seguimientoId,SeguimientoSurComprobante detalle,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
			
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.inserta_seguimientosur_comprobante_liquidado(?,?,?,?,?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,seguimientoId);
			stmt.setInt(2,Integer.parseInt(detalle.getAcreedorEmpresa().getSucursal()));
			stmt.setString(3,detalle.getCuit());
			stmt.setInt(4, detalle.getPtoVenta());
			stmt.setString(5, detalle.getTipoComprobante());
			stmt.setString(6,detalle.getLetraComprobante());
			stmt.setString(7, detalle.getNroComprobante());
			stmt.setInt(8,detalle.getSucuComprobante());
			
			if (detalle.getFechaEmision()!=null) {
				  stmt.setDate(9, new java.sql.Date(detalle.getFechaEmision().getTime() ));
			} else {
				stmt.setNull(9, Types.DATE);
			}
			
			if (detalle.getFechaRecepcion()!=null) {
				  stmt.setDate(10, new java.sql.Date(detalle.getFechaRecepcion().getTime() ));
			} else {
				stmt.setNull(10, Types.DATE);
			}
			
			if (detalle.getFechaVencimiento()!=null) {
				  stmt.setDate(11, new java.sql.Date(detalle.getFechaVencimiento().getTime() ));
			} else {
				stmt.setNull(11, Types.DATE);
			}
			
			stmt.setBigDecimal(12,detalle.getImporte());
			
			stmt.setString(13,screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - comprobante liquidado ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}

	public long eliminaSeguimientoComprobante(int idSeguimiento, SeguimientoSurComprobante comprobante,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.elimina_seguimientosur_comprobante_liquidado(?,?,?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idSeguimiento);
			stmt.setInt(2, Integer.parseInt(comprobante.getAcreedorEmpresa().getSucursal()) );
			stmt.setString(3, comprobante.getCuit());
			stmt.setInt(4, comprobante.getPtoVenta());
			stmt.setString(5,comprobante.getTipoComprobante());
			stmt.setString(6, comprobante.getLetraComprobante());
			stmt.setString(7, comprobante.getNroComprobante());
			stmt.setInt(8,comprobante.getSucuComprobante());
			stmt.setString(9, screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar Seguimiento SUR comprobante liquidado ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_seguimiento;
	}

	public boolean existePatologiaSur(String descripcion)
			throws SystemException {

		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call autorizaciones.busca_patologia_sur(?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, descripcion);
			ResultSet rs = stmt.executeQuery();

			if(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar duplicado Patologia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}

	public long insertaPatologia(String descripcion,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
			
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call autorizaciones.inserta_patologia_sur(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,descripcion);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimiento sur - patologia ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}
	
	public List<DLFileEntryImpl> getImagenesSeguimientoSur(String titulo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<DLFileEntryImpl> list = null;
		try {
			String sql = "{call autorizaciones.busca_imagenes_seguimiento_sur(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, titulo);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<DLFileEntryImpl>();
			while (rs.next()) {
				DLFileEntryImpl a = new DLFileEntryImpl();
				DLFolder f = new DLFolderImpl();
				a.setFolderId(rs.getLong("folderId"));
				a.setName(rs.getString("fileName"));
				a.setDescription(rs.getString("fileDescription"));
				a.setTitle(rs.getString("fileTitle"));
				list.add(a);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Imagenes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	
	public List<SeguimientoSurComprobante> buscarComprobantesLiquidadosSeguimientoSurPorId(
			int id,Connection connectionParameter) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSurComprobante>list=new ArrayList<SeguimientoSurComprobante>();
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connectionParameter;
		}
		
		try {
						
			String sql = "{ call autorizaciones.busca_seguimientosur_comprobantes_liquidados_por_idseguimiento(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rsl = stmt.executeQuery();
			while (rsl.next()) {
				SeguimientoSurComprobante comprobante = new SeguimientoSurComprobante();
				comprobante.setCuit(rsl.getString("cuit"));
				comprobante.setTipoComprobante(rsl.getString("compro_tipo"));
				comprobante.setLetraComprobante(rsl.getString("compro_letra"));
				comprobante.setSucuComprobante(rsl.getInt("compro_sucu"));
				comprobante.setPtoVenta(rsl.getInt("id_punto_venta"));
				comprobante.setNroComprobante(rsl.getString("compro_nro"));
				
				list.add(comprobante);
			}
			
				
		} catch (Exception e) {
			_log.error("error al buscar Comprobantes Liquidados Seguimiento SUR por Id", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	
	
	public long buscarIdSeguimientoByNroExpediente(String nroExpdte,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		int id_seguimiento = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.busca_id_seguimientosur_por_nro_expediente(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,nroExpdte);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar seguimiento sur por nro Expdte ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_seguimiento;
	}

	public Integer proximoNroLotePago(Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		int id_lote = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.seguimiento_sur_proximo_lote_pago()}";

			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_lote = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar seguimiento sur por nro Expdte ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return id_lote;
	}
		

	
		
	public static long imputaPagoSeguimiento(SeguimientoSur seguimiento,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			
			String sql = "{call autorizaciones.seguimiento_sur_imputa_pago(?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,seguimiento.getId());
			stmt.setDouble(2,seguimiento.getProporcionalAdelantado());
			stmt.setString(3, screenName);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al imputar seguimiento", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;

	}
	
	public void guardarLoteNovedades(Integer nroLote, String tipoArchivo, SeguimientoSur seguimiento, String screenName, 
			Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;

		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
					
			String sql = "{call autorizaciones.seguimientosur_inserta_lote_pago_imputado(?,?,?,?,?,?,?,?,?,?,?)}";
											   
			stmt = con.prepareCall(sql.toString());
			if(seguimiento.getId()!=null){
			   stmt.setInt(1,seguimiento.getId());
			}else{
			   stmt.setNull(1,Types.INTEGER);	
			}
			stmt.setInt(2,nroLote);
			if (tipoArchivo.equals("CAMBIO_MASIVO_ESTADOS")){
				if (null != seguimiento.getEstados().get(0).getFechaEstado()) {
					stmt.setDate(3, new java.sql.Date(seguimiento.getEstados().get(0).getFechaEstado().getTime()));
				} else {
					stmt.setNull(3, Types.DATE);
				}
			}else if (tipoArchivo.equals("ENANALISISSUR")){	
				stmt.setDate(3, new java.sql.Date(DateUtils.getCalendarGMTMenos3().getTimeInMillis()) );
			}else{
				stmt.setDate(3, new java.sql.Date(seguimiento.getBaja_fecha().getTime()));	
			}
			stmt.setString(4,seguimiento.getNro_solicitud_sur());
			stmt.setString(5,seguimiento.getNro_expediente());
			stmt.setString(6,seguimiento.getCuilTitular());
			stmt.setString(7,seguimiento.getAfiliadoNombre());
			stmt.setString(8,seguimiento.getTipoRegistro());
			
			if(seguimiento.getProporcionalAdelantado()!=null){
			   stmt.setDouble(9,seguimiento.getProporcionalAdelantado());
			}else{
               stmt.setNull(9, Types.DOUBLE);				
			}
			stmt.setString(10,screenName);
			stmt.setString(11,tipoArchivo);
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al guardar novedades proceso archivos SUR ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
	public List<SeguimientoSur> traePorUltimoEstado(String estadoSSS,Date fechaDesde,Date fechaHasta)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSur> list = null;
		try {
			String sql = "{call autorizaciones.busca_seguimiento_sur_por_id_estado(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (null != estadoSSS && estadoSSS.trim().length() > 0) {
				stmt.setString(1, estadoSSS);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if (null != fechaDesde) {
				stmt.setDate(2,  new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			
			if (null != fechaHasta) {
				stmt.setDate(3, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSur>();
			while (rs.next()) {
				SeguimientoSur archivo = new SeguimientoSur();
				archivo.setId(rs.getInt("id"));
				archivo.setCuilTitular(rs.getString("cuil_titular"));
				archivo.setAfiliadoNombre(rs.getString("nombre_afiliado"));
				archivo.setNro_expediente(rs.getString("nro_expediente"));
				archivo.setNro_solicitud_sur(rs.getString("nro_solicitud_sur"));
				archivo.setProporcionalAdelantado(rs.getDouble("proporcional_adelantado"));
				archivo.setBaja_fecha(rs.getDate("fecha_estado"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Seguimiento Sur por estado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<SeguimientoSurLoteProcesado> lotesProcesadosAdelantos()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSurLoteProcesado> list = null;
		try {
			String sql = "{call autorizaciones.busca_seguimiento_sur_lotes_procesados_pagos()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSurLoteProcesado>();
			while (rs.next()) {
				SeguimientoSurLoteProcesado archivo = SeguimientoSurLoteProcesado.getMapping(rs) ;
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Lotes Pagados Seguimiento Sur ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public List<SeguimientoSur> lotesProcesadosAdelantosDetalle(Integer nroLote,String tipo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSur> list = null;
		try {
			String sql = "{call autorizaciones.busca_seguimiento_sur_lotes_procesados_pagos_detalle(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (null != nroLote) {
				stmt.setInt(1, nroLote);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (null != tipo) {
				stmt.setString(2, tipo);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSur>();
			while (rs.next()) {
				SeguimientoSur archivo = new SeguimientoSur();
				archivo.setId(rs.getInt("id_seguimiento"));
				archivo.setAfiliadoNombre(rs.getString("nombre"));
				archivo.setNro_expediente(rs.getString("nro_expediente"));
				archivo.setNro_solicitud_sur(rs.getString("nro_solicitud"));
				archivo.setProporcionalAdelantado(rs.getDouble("importe"));
				archivo.setBaja_fecha(rs.getDate("fecha"));
				archivo.setCuilTitular(rs.getString("cuil"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Seguimiento Sur Lote detalle", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public static void actualizaPagoProporcional(SeguimientoSur seguimiento, String screenName, int idEstado,
			Connection connectionParameter) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			
			String sql = "{call autorizaciones.seguimiento_sur_actualiza_pago_prop(?,?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,seguimiento.getId());
			if(seguimiento.getFecha_ingreso_area_sur() != null){
				stmt.setDate(2,new java.sql.Date(seguimiento.getFecha_ingreso_area_sur().getTime()));
			}else{
				stmt.setNull(2, Types.DATE);
			}
			
			if(seguimiento.getProporcionalAdelantado()!=null) {
				stmt.setDouble(3,seguimiento.getProporcionalAdelantado());
			}else {
				stmt.setDouble(3, new Double(0));
			}
			if(seguimiento.getFechaProporcionalAdelantado() != null){
				stmt.setDate(4,new java.sql.Date(seguimiento.getFechaProporcionalAdelantado().getTime()));
			}else{
				stmt.setNull(4, Types.DATE);
			}
			stmt.setInt(5, idEstado);
			
			stmt.setString(6,seguimiento.getNro_expediente());
			
			stmt.setString(7,seguimiento.getCodigoHIV());
			
			stmt.setString(8, screenName);
			
			if(seguimiento.getImporteReconocido()!=null) {
				stmt.setDouble(9,seguimiento.getImporteReconocido());
			}else {
				stmt.setDouble(9, new Double(0));
			}
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al actualizar pago prop. seguimiento", e);
			throw new SystemException(e);
		} catch (Exception e) {
			_log.error("Error al actualizar pago prop. seguimiento", e);
			throw new SystemException(e);	
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
	public static void actualizaPagoImputado(SeguimientoSur seguimiento, String screenName, int idEstado,
			Connection connectionParameter) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.seguimiento_sur_actualiza_pago_imputado(?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			_log.debug("id " + seguimiento.getId());	
			stmt.setInt(1,seguimiento.getId());
			stmt.setDate(2,new java.sql.Date(seguimiento.getEstados().get(0).getFechaEstado().getTime())); //es la fecha de pago
			stmt.setInt(3, idEstado);
			stmt.setString(4, screenName);
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al actualizar pago imputado seguimiento", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<SeguimientoSur> getListaSeguimientoSurImputados(int anio, int bimestre,int tipoExpediente,int autorizaOmint,String nroSolicitud,
			String codigoPresentado,String descripcionPresentado,String nroExpediente,String cuil,
			String inte,Date fechaDesde,Date fechaHasta,Boolean incluyeBajas,String estadoExpediente,String clase,String usuarioAlta,
			String estadoSSS, Date fechaDesdePago,Date fechaHastaPago)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<SeguimientoSur> list = null;
		try {
			String sql = "{call autorizaciones.busca_seguimiento_sur_imputados(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (anio>0) {
				stmt.setInt(1, anio);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (bimestre>0) {
				stmt.setInt(2, bimestre);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			if (tipoExpediente>0) {
				stmt.setInt(3, tipoExpediente);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (autorizaOmint>0) {
				stmt.setInt(4, autorizaOmint);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			if (null != nroSolicitud && nroSolicitud.trim().length() > 0) {
				stmt.setString(5, nroSolicitud);
			} else {
				stmt.setNull(5, Types.VARCHAR);
			}
			
			if (null != codigoPresentado && codigoPresentado.trim().length() > 0) {
				stmt.setString(6, codigoPresentado);
			} else {
				stmt.setNull(6, Types.VARCHAR);
			}
			
			if (null != descripcionPresentado && descripcionPresentado.trim().length() > 0) {
				stmt.setString(7, descripcionPresentado);
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			if (null != nroExpediente && nroExpediente.trim().length() > 0) {
				stmt.setString(8, nroExpediente);
			} else {
				stmt.setNull(8, Types.VARCHAR);
			}
			
			if (null != cuil && cuil.trim().length() > 0) {
				stmt.setString(9, cuil);
			} else {
				stmt.setNull(9, Types.VARCHAR);
			}
			
			if (null != inte && inte.trim().length() > 0) {
				stmt.setInt(10, Integer.parseInt(inte));
			} else {
				stmt.setNull(10, Types.INTEGER);
			}
			
			if (null != fechaDesde) {
				stmt.setDate(11,  new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(11, Types.DATE);
			}
			
			if (null != fechaHasta) {
				stmt.setDate(12, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(12, Types.DATE);
			}
			
			if(incluyeBajas==true){
				stmt.setBoolean(13,true);
			}else{
				stmt.setNull(13,Types.BOOLEAN);
			}
			
			if (null != estadoExpediente && estadoExpediente.trim().length() > 0) {
				stmt.setString(14, estadoExpediente);
			} else {
				stmt.setNull(14, Types.VARCHAR);
			}
			
			if (null != clase && clase.trim().length() > 0) {
				stmt.setString(15, clase);
			} else {
				stmt.setNull(15, Types.VARCHAR);
			}
			
			if (null != usuarioAlta && usuarioAlta.trim().length() > 0) {
				stmt.setString(16, usuarioAlta);
			} else {
				stmt.setNull(16, Types.VARCHAR);
			}
			
			if (null != estadoSSS && estadoSSS.trim().length() > 0) {
				stmt.setString(17, estadoSSS);
			} else {
				stmt.setNull(17, Types.VARCHAR);
			}
			if (null != fechaDesdePago) {
				stmt.setDate(18,  new java.sql.Date(fechaDesdePago.getTime()));
			} else {
				stmt.setNull(18, Types.DATE);
			}
			
			if (null != fechaHastaPago) {
				stmt.setDate(19, new java.sql.Date(fechaHastaPago.getTime()));
			} else {
				stmt.setNull(19, Types.DATE);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<SeguimientoSur>();
			while (rs.next()) {
				SeguimientoSur archivo = SeguimientoSur.getMapping(rs);
				
//				archivo = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId(archivo.getId());
				
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Seguimiento Sur Imputados", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
}

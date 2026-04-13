package ar.com.ospim.novedades.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadas;
import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadasCab;
import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadasDet;
import ar.com.ospim.novedades.beans.AfiliadoCambioCuil;
import ar.com.ospim.novedades.beans.ArchivoNovedad;
import ar.com.ospim.novedades.beans.Novedad;
import ar.com.ospim.novedades.beans.NovedadEmpleadorTotal;
import ar.com.ospim.novedades.beans.NovedadTotal;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class NovedadesServiceImpl {

	private static Log logger = LogFactoryUtil.getLog(NovedadesServiceImpl.class);

	public List<ArchivoNovedad> getArchivosNovedades(Date fechaDesde) throws SystemException{
		
		List<ArchivoNovedad> archivosNove = new ArrayList<ArchivoNovedad>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.trae_archivos_novedades(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (fechaDesde == null)
				stmt.setNull(1, Types.DATE);
			else
				stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()) );
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ArchivoNovedad an = ArchivoNovedad.getMapping(rs);
				archivosNove.add(an);
			}

		} catch (Exception e) {
			logger.error("error al buscar archivos de novedades", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return archivosNove;
	}
	
	public List<NovedadTotal> getNovedades(String cuil_titular, String cuil, String tdoc, String nrodoc, 
			String apellido, String nombre, String tipoNov, String tipoOri, Date fechaProc, int pagina_sel) throws SystemException{
		
		List<NovedadTotal> novedades = new ArrayList<NovedadTotal>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.buscar_novedades_detalle(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
					
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (cuil_titular == null){
				stmt.setNull(1, Types.VARCHAR);
			}else{
				stmt.setString(1, cuil_titular);
			}
			if (cuil == null){
				stmt.setNull(2, Types.VARCHAR);
			}else{
				stmt.setString(2, cuil);
			}
			if (tdoc == null){
				stmt.setNull(3, Types.VARCHAR);
			}else{
				stmt.setString(3, tdoc);
			}
			if (nrodoc == null){
				stmt.setNull(4, Types.INTEGER);
			}else{
				stmt.setInt(4, Integer.parseInt(nrodoc));
			}
			if (apellido == null && nombre == null){
				stmt.setNull(5, Types.VARCHAR);
			}else if (apellido != null && nombre != null){
//				String apeNom = "%'"+apellido + "'% || '%' || %'" +nombre+"'%";
				String apeNom = apellido + " " + nombre; 
//				logger.debug(apeNom);
				stmt.setString(5, apeNom); //'%'|| 'oO' || '%' || 'NNA' ||'%'
			}else if (apellido != null && nombre == null){
				stmt.setString(5, apellido);
			}else if (apellido == null && nombre != null){
				stmt.setString(5, nombre);
			}
			if (tipoNov == null){
				stmt.setNull(6, Types.VARCHAR);
			}else{
				stmt.setString(6, tipoNov);
			}
			if (tipoOri == null){
				stmt.setNull(7, Types.VARCHAR);
			}else{
				stmt.setString(7, tipoOri);
			}
			if (fechaProc == null){
				stmt.setNull(8, Types.DATE);
			}else{
				stmt.setDate(8, new java.sql.Date(fechaProc.getTime()));
			}
			stmt.setInt(9, pagina_sel);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				NovedadTotal n = NovedadTotal.getMapping("", rs);
				novedades.add(n);
			}

		} catch (Exception e) {
			logger.error("error al buscar novedades detalle", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return novedades;
		
	}
	
	public List<NovedadTotal> getNovedadesXls(String cuil_titular, String cuil, String tdoc, String nrodoc, 
			String apellido, String nombre, String tipoNov, String tipoOri, Date fechaProc/*, int pagina_sel*/) throws SystemException{
		
		List<NovedadTotal> novedades = new ArrayList<NovedadTotal>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.buscar_novedades_detalle_xls(?, ?, ?, ?, ?, ?, ?, ?)}";
					
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (cuil_titular == null){
				stmt.setNull(1, Types.VARCHAR);
			}else{
				stmt.setString(1, cuil_titular);
			}
			if (cuil == null){
				stmt.setNull(2, Types.VARCHAR);
			}else{
				stmt.setString(2, cuil);
			}
			if (tdoc == null){
				stmt.setNull(3, Types.VARCHAR);
			}else{
				stmt.setString(3, tdoc);
			}
			if (nrodoc == null){
				stmt.setNull(4, Types.INTEGER);
			}else{
				stmt.setInt(4, Integer.parseInt(nrodoc));
			}
			if (apellido == null && nombre == null){
				stmt.setNull(5, Types.VARCHAR);
			}else if (apellido != null && nombre != null){
//				String apeNom = "%'"+apellido + "'% || '%' || %'" +nombre+"'%";
				String apeNom = apellido + " " + nombre; 
//				logger.debug(apeNom);
				stmt.setString(5, apeNom); //'%'|| 'oO' || '%' || 'NNA' ||'%'
			}else if (apellido != null && nombre == null){
				stmt.setString(5, apellido);
			}else if (apellido == null && nombre != null){
				stmt.setString(5, nombre);
			}
			if (tipoNov == null){
				stmt.setNull(6, Types.VARCHAR);
			}else{
				stmt.setString(6, tipoNov);
			}
			if (tipoNov == null){
				stmt.setNull(6, Types.VARCHAR);
			}else{
				stmt.setString(6, tipoNov);
			}
			if (tipoOri == null){
				stmt.setNull(7, Types.VARCHAR);
			}else{
				stmt.setString(7, tipoOri);
			}
			if (fechaProc == null){
				stmt.setNull(8, Types.DATE);
			}else{
				stmt.setDate(8, new java.sql.Date(fechaProc.getTime()));
			}
//			stmt.setInt(9, pagina_sel);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				NovedadTotal n = NovedadTotal.getMapping("", rs);
				novedades.add(n);
			}

		} catch (Exception e) {
			logger.error("error al buscar novedades detalle xls", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return novedades;
		
	}
	
//	TODO Hacer paginacion; limitadas a 50 x pagina, solo primer pagina
	public List<NovedadEmpleadorTotal> getNovedadesEmpleadores(Date fechaHasta, String tipoNovedadEmpl, int pagina_sel) throws SystemException{
		
		Calendar fechaDesde = Calendar.getInstance();
		
		fechaDesde.setTime(fechaHasta);
//		quedamos que se solicitan periodos de 3 meses
		fechaDesde.set(Calendar.DATE, 1);
		fechaDesde.add(Calendar.MONTH, -3);
		
		String sql = null, tipoNovDetalle=null;
		List<NovedadEmpleadorTotal> novedades = new ArrayList<NovedadEmpleadorTotal>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
//			String sql = "{call novedades_sss.buscar_novedades_empleadores(?, ?, ?)}";
			if(tipoNovedadEmpl.equalsIgnoreCase("ALTAS")){
				sql = "{call novedades_sss.buscar_novedades_empleadores_altas(?, ?)}";
				tipoNovDetalle = "ALTA DE PLAN y ALTA DE AFILIADO";
			}else if(tipoNovedadEmpl.equalsIgnoreCase("CAMBIOSPLAN")){
				sql = "{call novedades_sss.buscar_novedades_empleadores_cambio_plan(?, ?)}";
				tipoNovDetalle = "CAMBIO DE PLAN";
			}else if(tipoNovedadEmpl.equalsIgnoreCase("BAJAS")){
				sql = "{call novedades_sss.buscar_novedades_empleadores_bajas(?, ?)}";
				tipoNovDetalle = "BAJA SUGERIDA";
			}

					
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, new java.sql.Date(fechaDesde.getTimeInMillis()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
//			stmt.setInt(3, pagina_sel);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				NovedadEmpleadorTotal n = NovedadEmpleadorTotal.getMapping("", rs);
				n.setNovedad_desc(tipoNovDetalle);
				novedades.add(n);
			}

		} catch (Exception e) {
			logger.error("error al buscar novedades empleadores detalle", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return novedades;
		
	}
//	TODO unificar con el metodo anterior cuando este paginado, este manda todo sin limit...
	public List<NovedadEmpleadorTotal> getNovedadesEmpleadoresXls(Date fechaHasta, String tipoNovedadEmpl, int pagina_sel) throws SystemException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar fechaDesde = Calendar.getInstance();
		
		fechaDesde.setTime(fechaHasta);
//		quedamos que se solicitan periodos de 3 meses
		fechaDesde.set(Calendar.DATE, 1);
		fechaDesde.add(Calendar.MONTH, -3);
		
		logger.debug("Solicitando novedades empleadores " + tipoNovedadEmpl);
		logger.debug("Fecha Desde: " + sdf.format(fechaDesde.getTime()));
		logger.debug("Fecha Hasta: " + sdf.format(fechaHasta.getTime()));
		
		String sql = null, tipoNovDetalle=null;
		List<NovedadEmpleadorTotal> novedades = new ArrayList<NovedadEmpleadorTotal>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
//			String sql = "{call novedades_sss.buscar_novedades_empleadores(?, ?, ?)}";
			if(tipoNovedadEmpl.equalsIgnoreCase("ALTAS")){
				sql = "{call novedades_sss.buscar_novedades_empleadores_altas_xls(?, ?)}";
				tipoNovDetalle = "ALTA DE PLAN y ALTA DE AFILIADO";
			}else if(tipoNovedadEmpl.equalsIgnoreCase("CAMBIOSPLAN")){
				sql = "{call novedades_sss.buscar_novedades_empleadores_cambio_plan_xls(?, ?)}";
				tipoNovDetalle = "CAMBIO DE PLAN";
			}else if(tipoNovedadEmpl.equalsIgnoreCase("BAJAS")){
				sql = "{call novedades_sss.buscar_novedades_empleadores_bajas_xls(?, ?)}";
				tipoNovDetalle = "BAJA SUGERIDA";
			}

					
			con = ConnectionHelper.getReportesOspimConnection(); //ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, new java.sql.Date(fechaDesde.getTimeInMillis()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
//			stmt.setInt(3, pagina_sel);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				NovedadEmpleadorTotal n = NovedadEmpleadorTotal.getMapping("", rs);
				n.setNovedad_desc(tipoNovDetalle);
				novedades.add(n);
			}

		} catch (Exception e) {
			logger.error("error al buscar novedades empleadores detalle", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return novedades;
		
	}
	
	public Novedad getNovedadById(int idNovedad) throws SystemException{
		
		Novedad nove = null;
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.buscar_novedad_por_id(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, idNovedad);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				nove = NovedadTotal.getMapping("", rs);
			}

		} catch (Exception e) {
			logger.error("error al buscar novedad pr Id", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return nove;
	}
		
	public boolean cambiaCuil(AfiliadoCambioCuil cambioCuil, String user) throws SystemException{
		
		int result= 0;
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.cambia_cuil_afiliado(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
					  
			stmt.setString(1, cambioCuil.getCuil_titular());
			stmt.setInt(2, cambioCuil.getInte());
			stmt.setString(3, cambioCuil.getCuil());
			stmt.setString(4, cambioCuil.getDocumento_tipo());
			stmt.setString(5, cambioCuil.getDocumento_numero());
			stmt.setDate(6, new java.sql.Date(cambioCuil.getVigen_fecha().getTime()) );
			stmt.setString(7, cambioCuil.getCuil_titular_anterior());
			stmt.setInt(8, cambioCuil.getInte_anterior());
			stmt.setString(9, cambioCuil.getCuil_anterior());
			stmt.setString(10, cambioCuil.getDocumento_tipo_anterior());
			stmt.setString(11, cambioCuil.getDocumento_numero_anterior());
			stmt.setString(12, user);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			logger.error("error al realizar cambio de CUIL", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result == 1;
	}
	
	public static List<ReporteNovedadesSSSProcesadas> getReportesNovedadesSSSProcesadas() throws SystemException {
		
		logger.debug("Buscando Reportes de Novedades de la SSS procesadas por periodo");
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteNovedadesSSSProcesadas> ret = null;
		ReporteNovedadesSSSProcesadas rnp = null;
		ReporteNovedadesSSSProcesadasCab cab = null;
		ReporteNovedadesSSSProcesadasDet det = null;
		
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call informes.buscar_reportes_novedades_sss_procesadas() }";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ReporteNovedadesSSSProcesadas>();
			while (rs.next()) {
				cab = ReporteNovedadesSSSProcesadasCab.getMapping("cab_",rs);
				det = ReporteNovedadesSSSProcesadasDet.getMapping("det_",rs);
				rnp = new ReporteNovedadesSSSProcesadas();
				rnp.setCabecera(cab);
				rnp.setDetalle(det);
				
				ret.add(rnp);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return ret;
	}
	
	public static List<ReporteNovedadesSSSProcesadas> getEstadisticaNovedadesSSSProcesadas() throws SystemException {
		
		logger.debug("Generando Estadistica de Novedades de la SSS procesadas por periodo");
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteNovedadesSSSProcesadas> ret = null;
		ReporteNovedadesSSSProcesadas rnp = null;
		ReporteNovedadesSSSProcesadasCab cab = null;
		ReporteNovedadesSSSProcesadasDet det = null;
		
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call informes.calcular_estadistica_novedades_sss_procesadas() }";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ReporteNovedadesSSSProcesadas>();
			while (rs.next()) {
				cab = ReporteNovedadesSSSProcesadasCab.getMapping("cab_",rs);
				det = ReporteNovedadesSSSProcesadasDet.getMapping("det_",rs);
				rnp = new ReporteNovedadesSSSProcesadas();
				rnp.setCabecera(cab);
				rnp.setDetalle(det);
				
				ret.add(rnp);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return ret;
	}
}

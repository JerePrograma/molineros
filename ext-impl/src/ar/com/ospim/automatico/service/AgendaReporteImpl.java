package ar.com.ospim.automatico.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadas;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaListado;
import ar.com.ospim.automatico.beans.MensajeEnvioyRespuestaWSOmint;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.webservice.service.AfiliadoOpe;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AgendaReporteImpl{

	private static Log logger = LogFactoryUtil.getLog(AgendaReporteImpl.class);
	
	public static int horaCorridaDiferida = 3; //19
	
//	agenda una corrida de stored_procedure para el mismo dia a las horaCorridaDiferida por unica vez  
	public int agendarReporte(String reporteDescripcion, String csvParameteres) throws SystemException{
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, horaCorridaDiferida);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, 1);
		
		int idReportesAutom=0;
		String storedProcedure = "";
		
		if(reporteDescripcion.equalsIgnoreCase(ReporteDeudaEmpresaListado.REPORTE_DEUDA_EMPRESAS_PERIODO)){
//			storedProcedure = "informes.reporte_deuda_empresas_periodo_batch(periodo_desde date, periodo_hasta date, 
//			sin_deuda boolean, ramo_desde_p integer, ramo_hasta_p integer, usuario_p varchar, fecha_solicitado_p timestamp without time zone)";
			
			storedProcedure = "informes.reporte_deuda_empresas_periodo_batch"; //(?, ?, ?, ?, ?, ?, ?)

		}else if(reporteDescripcion.equalsIgnoreCase(ReporteNovedadesSSSProcesadas.REPORTE_NOVEDADES_SSS_PROCESADAS)){

			storedProcedure = "novedades_sss.generar_estadistica_novedades_sss"; 
		}	
		
		logger.debug("Agendando " + storedProcedure);
		Connection con = null;
		CallableStatement stmt = null;

		try {		
			String sql = "{call inserta_repo_autom(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) }";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, reporteDescripcion);
			stmt.setString(2, storedProcedure);
			stmt.setString(3, csvParameteres);
			stmt.setInt(4, horaCorridaDiferida);
			stmt.setBoolean(5, false);
			stmt.setBoolean(6, false);
			stmt.setInt(7, 0);
			stmt.setInt(8, cal.get(Calendar.DAY_OF_MONTH));
			stmt.setDate(9, new java.sql.Date(cal.getTimeInMillis()) );
			stmt.setString(10, "svalentini@ospim.org.ar,dsulfaro@uoma.org.ar");
			stmt.setDate(11, null);
			stmt.setInt(12, 0);
			stmt.setInt(13, 1);
			stmt.setString(14, null);
	
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idReportesAutom = rs.getInt(1);
			}
			
		} catch (Exception e) {
			logger.error("Error al agendarReporte ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return idReportesAutom;
	}
	
	public List<MensajeEnvioyRespuestaWSOmint> getNovedadesProcesadas(Date fechaProceso) throws SystemException{
		
		logger.debug("Buscando novedades procesadas");
		
		Connection con = null;
		CallableStatement stmt = null;
		List<MensajeEnvioyRespuestaWSOmint> novedades = new ArrayList<MensajeEnvioyRespuestaWSOmint>();
		MensajeEnvioyRespuestaWSOmint nov;
		
		try {
			con = ConnectionHelper.getConnection();
//			con = ConnectionHelper.getConnectionFromJavaApplication() ;

			String sql = "{call informes.buscar_novedades_procesadas_ws_omint(?) }";

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaProceso.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				nov = MensajeEnvioyRespuestaWSOmint.getMapping(rs);
				
				novedades.add(nov);
			}
			
		} catch (Exception e) {
			logger.error("Error al novedades procesadas ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return novedades;
	}
	
	public List<AfiliadoOpe> getNovedadesProcesadasPrevencion(Date fechaProceso) throws SystemException{
		
		logger.debug("Buscando novedades procesadas para Prevención");
		
		Connection con = null;
		CallableStatement stmt = null;
		List<AfiliadoOpe> novedades = new ArrayList<AfiliadoOpe>();
		AfiliadoOpe nov;
		
		try {
			con = ConnectionHelper.getConnection();
//			con = ConnectionHelper.getConnectionFromJavaApplication() ;

			String sql = "{call informes.buscar_novedades_procesadas_ws(?) }";

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaProceso.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				nov = AfiliadoOpe.getMapping3(rs);
				
				novedades.add(nov);
			}
			
		} catch (Exception e) {
			logger.error("Error al novedades procesadas prevencion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return novedades;
	}
}

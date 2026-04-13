package ar.com.ospim.afiliados.reportes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReportesBonosServiceImpl {
	private static Log logger = LogFactoryUtil.getLog(ReportesBonosServiceImpl.class);
	
	public List<ReporteCantBonosSeccional> getReporteCantBonosSeccional()throws Exception{		
		Connection con = null;
		CallableStatement stmt = null;		
		
		List<ReporteCantBonosSeccional> result=null;//new ArrayList<ReportePosiblesInconsistenciasResult>();
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  reporte_cant_bonos_seccional()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<ReporteCantBonosSeccional>();
			while (rs.next()) {
				ReporteCantBonosSeccional deuda = new ReporteCantBonosSeccional(rs.getString("seccional"), rs.getInt("total_beneficiarios"), rs.getInt("cant_bonos_1"), rs.getInt("cant_bonos_2"), rs.getInt("cant_bonos_3"));
				result.add(deuda);
			}
		
		} finally {
			ConnectionHelper.cerrar(stmt, con);			
		}		
		return result;		
	}

	
	public List<ReporteAfiliadosAnses> getReporteAfiliadosAnses(){		
		Connection con = null;
		CallableStatement stmt = null;		
		
		List<ReporteAfiliadosAnses> result=null;		
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();			
			String sql = "{call reporte_afiliados_jubilados_info_afip()}";
			stmt = con.prepareCall(sql.toString());			
									
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<ReporteAfiliadosAnses>();
			while (rs.next()) {	
				ReporteAfiliadosAnses jubiladoafip =ReporteAfiliadosAnses.getMapping("", rs);
				result.add(jubiladoafip );
			}
		}catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);			
		}		
		return result;		
	}
	

	
	public List<ReporteCantBonosSeccionalVent> getReporteCantBonosSeccionalVent(int tipoBono, int seccional){		
		Connection con = null;
		CallableStatement stmt = null;		
		
		List<ReporteCantBonosSeccionalVent> result=null;		
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();			
			
			String sql = "{call  reporte_cant_bonos_seccional_con_anulados(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,tipoBono); 
			stmt.setInt(2, seccional); 
						
			ResultSet rs = stmt.executeQuery();
			
			result = new ArrayList<ReporteCantBonosSeccionalVent>();
			
			while (rs.next()) {	
				ReporteCantBonosSeccionalVent bono = ReporteCantBonosSeccionalVent.getMapping("", rs);
				result.add(bono);
			}
			
		}catch (Exception e) {
			logger.error(e);	
		} finally {
			ConnectionHelper.cerrar(stmt, con);			
		}		
		return result;		
	}
	
}

package ar.com.ospim.automatico.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import ar.com.global.beans.Destinatario;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.beans.ResultadoReporteAutomatico;
import ar.com.ospim.automatico.beans.ResultadoReporteAutomatico.ItemResultadoReporteAutomatico;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SchedulerServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(ReportesServiceImpl.class);
	
	public static int BASE_DEVMOLINEROS=1;
	public static int BASE_PORTALEMPLEADORES=2;

	private static SchedulerServiceImpl instance = null;

	public static SchedulerServiceImpl getInstance() {
		if (null == instance) {
			instance = new SchedulerServiceImpl();
		}
		return instance;
	}

	public void run(Integer idJobs) throws SystemException { // Vuelto a escribir para que corra en molineres porque murio 12.1.1.5  -- 2025-09-09
		logger.debug("Run now - Jobs");
		Connection conRun = null;
		CallableStatement stmtRun = null;
		try {
			String fn=getFunctionName(idJobs);
			
			conRun = ConnectionHelper.getReportesOspimConnection();
			String sql  = "{call " + fn  +"()}";
			stmtRun = conRun.prepareCall(sql.toString());
			ResultSet rs = stmtRun.executeQuery();
			
		} catch (Exception e) {
			logger.error("Error Run now - Jobs ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmtRun, conRun);
		}
	}
	
	
/*	
	public void run(Integer idJobs) throws SystemException {
		logger.debug("Run now - Jobs");
		Connection con = null;
		Statement stmt = null;
		try {
			con = ConnectionHelper.getConnectionPostgres();
			String sql  = String.format("UPDATE pgagent.pga_job SET jobnextrun = now() WHERE jobid = %s",idJobs);
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			logger.error("Error Run now - Jobs ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
*/
	public void addParameters(String codigo,Integer idJobs,String parametros) throws SystemException {
		logger.debug("Add parameters - Jobs");
		Connection con = null;
		Statement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql  = String.format("UPDATE util.parametros_para_scheduler_pgagent SET parametros = '%s' WHERE idjob = %s",parametros,idJobs);
			stmt = con.createStatement();
			int result = stmt.executeUpdate(sql);
			if(result==0){
				sql  = String.format("insert into util.parametros_para_scheduler_pgagent(codigo,idjob,parametros) "
						+ " values('%s',%s,'%s')",codigo,idJobs,parametros);
				stmt = con.createStatement();
				result = stmt.executeUpdate(sql);
			}
		} catch (Exception e) {
			logger.error("Error Add parameters - Jobs ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	
	public List<String> getParameters(String codigo) throws SystemException {
		logger.debug("Add parameters - Jobs");
		Connection con = null;
		Statement stmt = null;
		List<String> rta = new ArrayList<String>();
		try {
			con = ConnectionHelper.getConnection();
			String sql  = String.format("select * from util.parametros_para_scheduler_pgagent  WHERE codigo = '%s'",codigo);
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql) ;
			String params ="";
			while (rs.next()) {
				params= rs.getString("parametros");
			}
			if(params.length()>0){
				String[] vParams=params.split(";");
				for(int i=0;i<vParams.length;i++){
					rta.add(vParams[i]);
				}
			}
			return rta;
		} catch (Exception e) {
			logger.error("Error Get parameters - Jobs ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public List<String> status(Integer idJobs) throws SystemException {
		logger.debug("Run now - Jobs");
		Connection con = null;
		Statement stmt = null;
		List result = new ArrayList<String>(); 
		try {
			
			String fn = getFunctionName(idJobs);
			if(fn!=null && !"".equals(fn)) {
			  con = ConnectionHelper.getConnection();
			  String sql  = 
			  "SELECT 'r' as jlgstatus, query_start as jlgstart FROM pg_stat_activity " + 
			  "WHERE state <> 'idle' and pid <> pg_backend_pid() and query like '%" + fn + "%'";		  
			  //String.format("select jlgstatus,to_char(jlgstart,'dd/MM/yyyy HH24:MI:SS') as jlgstart from pgagent.pga_joblog where jlgjobid=%s and jlgid in(select max(jlgid) from pgagent.pga_joblog where jlgjobid=%s)",idJobs,idJobs);
			  stmt = con.createStatement();
			  ResultSet rs = stmt.executeQuery(sql);
			  while (rs.next()) {
				result.add(rs.getString("jlgstatus"));
				result.add(rs.getString("jlgstart"));
			  }
			}  
			return result;
			
		} catch (Exception e) {
			logger.error("Error Run now - Jobs ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	/*
	public List<String> status(Integer idJobs) throws SystemException {
		logger.debug("Run now - Jobs");
		Connection con = null;
		Statement stmt = null;
		List result = new ArrayList<String>(); 
		try {
			con = ConnectionHelper.getConnectionPostgres();
			String sql  = String.format("select jlgstatus,to_char(jlgstart,'dd/MM/yyyy HH24:MI:SS') as jlgstart from pgagent.pga_joblog where jlgjobid=%s and jlgid in(select max(jlgid) from pgagent.pga_joblog where jlgjobid=%s)",idJobs,idJobs);
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(rs.getString("jlgstatus"));
				result.add(rs.getString("jlgstart"));
			}
			return result;
			
		} catch (Exception e) {
			logger.error("Error Run now - Jobs ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
    */
	
	public String getFunctionName(Integer idjob) throws SystemException {
		logger.debug("Add parameters - Jobs");
		Connection con = null;
		Statement stmt = null;
		String rta = "";
		try {
			con = ConnectionHelper.getConnection();
			String sql  = String.format("select function_name from util.parametros_para_scheduler_pgagent  WHERE idjob = '%s'",idjob);
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql) ;
			//String params ="";
			while (rs.next()) {
				rta= rs.getString("function_name");
			}
			return rta;
		} catch (Exception e) {
			logger.error("Error Get FunctionName - Jobs ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
}

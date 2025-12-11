package ar.com.ospim.automatico.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
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

public class ReportesServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(ReportesServiceImpl.class);
	
	public static int BASE_DEVMOLINEROS=1;
	public static int BASE_PORTALEMPLEADORES=2;

	private static ReportesServiceImpl instance = null;

	public static ReportesServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReportesServiceImpl();
		}
		return instance;
	}

	public List<ReporteAutomatico> getReportesACorrer() throws SystemException {
		logger.debug("Buscando reportes a correr");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAutomatico> ret = null;
		try {
//			con = ConnectionHelper.getConnection();
			con = ConnectionHelper.getReportesOspimConnection();

//			String sql = "select * from reportes_automaticos where stored_procedure is not null";
			String sql  = "{call buscar_reportes_agendados() }";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ReporteAutomatico>();
			while (rs.next()) {
				ret.add(ReporteAutomatico.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("Error al getReportesACorrer", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public List<ReporteAutomatico> getReportesACorrerUrgentes() throws SystemException {
		logger.debug("Buscando reportes a correr urgentes");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAutomatico> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql  = "{call buscar_reportes_agendados_urgente() }";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ReporteAutomatico>();
			while (rs.next()) {
				ret.add(ReporteAutomatico.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("Error al getReportesACorrer Urgentes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public ResultadoReporteAutomatico correrReporte(ReporteAutomatico ra)
			throws SystemException {
		logger.debug("Buscando reporte automatico " +ra.getStoredProcedure()!=null? ra.getStoredProcedure() : ra.getJava());
		Connection con = null;
		CallableStatement stmt = null;
		ResultadoReporteAutomatico ret = new ResultadoReporteAutomatico();
		try {
			if(ra.getBase()==2){
				con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			}else{
//				con = ConnectionHelper.getConnection();
				con = ConnectionHelper.getReportesOspimConnection();
			}		
			String sql = ra.getLlamadaStoredProcedure();
			stmt = con.prepareCall(sql.toString());
			ra.generateStatementParameters(stmt);
			ResultSet rs = stmt.executeQuery();
			ret.setInitialInfo(rs.getMetaData());
			while (rs.next()) {
				ret.addItem(ItemResultadoReporteAutomatico.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("Error al correr reporte automatico "
					+ ra.getStoredProcedure(), e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<Destinatario> correrReporteDifusion(ReporteAutomatico ra)
			throws SystemException {
		logger.debug("Buscando reporte automatico " + ra.getStoredProcedure());
		Connection con = null;
		CallableStatement stmt = null;
		ResultadoReporteAutomatico ret = new ResultadoReporteAutomatico();
		List<Destinatario> listaDestinatarios=new ArrayList<Destinatario>();
		try {
			if(ra.getBase()==2){
				con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			}else{
				con = ConnectionHelper.getConnection();
			}
			String sql = ra.getLlamadaStoredProcedure();
			stmt = con.prepareCall(sql.toString());
			ra.generateStatementParameters(stmt);
			ResultSet rs = stmt.executeQuery();
			ret.setInitialInfo(rs.getMetaData());
			while (rs.next()) {
				listaDestinatarios.add(Destinatario.getMapping(rs));				
			}
		} catch (Exception e) {
			logger.error("Error al correr reporte automatico "
					+ ra.getStoredProcedure(), e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDestinatarios;
	}

	public void reporteEjecutado(ReporteAutomatico ra) throws SystemException {
		
		logger.debug("Actualizando fecha de repo automatico: " + ra.getStoredProcedure()!=null? ra.getStoredProcedure() : ra.getJava());
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "update reportes_automaticos set ultima_ejecucion= ? where id = ?";
			stmt = con.prepareCall(sql.toString());
			stmt.setTimestamp(1, new Timestamp(ra.getUltimaEjecucion().getTime()));
			stmt.setInt(2, ra.getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error("Error al actualizar fecha de reporte automatico", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void save(ReporteAutomatico ra) throws SystemException {
		logger.debug("insertando repo automatico " + ra.getStoredProcedure()!=null? ra.getStoredProcedure() : ra.getJava());
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "insert into reportes_automaticos (titulo, stored_procedure, csv_parameteres, hora, diario, incluir_fin_de_semana, "
					+ "dia_de_la_semana, dia_del_mes, fecha_unica_vez , mails_destino, difusion, base, java)";
			sql += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, ra.getTitulo());
			stmt.setString(2, ra.getStoredProcedure());
			stmt.setString(3, ra.getCsvParameteres());
			stmt.setInt(4, ra.getHora());
			stmt.setBoolean(5, ra.isDiario());
			stmt.setBoolean(6, ra.isIncluirFinDeSemana());
			stmt.setInt(7, ra.getDiaDeLaSemana());
			stmt.setInt(8, ra.getDiaDelMes());
			
			if (ra.getFechaUnicaVez() == null) {
				stmt.setDate(9, null);
			} else {
				stmt.setDate(9, new java.sql.Date(ra.getFechaUnicaVez()
						.getTime()));
			}
			stmt.setString(10, ra.getEmails());
			stmt.setInt(11, ra.getDifusion());
			stmt.setInt(12, ra.getBase());
			stmt.setString(13, ra.getJava());
			
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error("Error al insertar reportes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public void update(ReporteAutomatico ra) throws SystemException {
		logger.debug("Actualizando repo automatico " + ra.getStoredProcedure()!=null? ra.getStoredProcedure() : ra.getJava());
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "update reportes_automaticos set titulo = ?, stored_procedure = ?, csv_parameteres = ?, hora = ?, diario = ?, "+
					" incluir_fin_de_semana = ?, dia_de_la_semana = ?, dia_del_mes = ?, fecha_unica_vez = ?, mails_destino = ?, " +
					" difusion = ?, base = ?, java = ?" +
					" where id = ?";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, ra.getTitulo());
			stmt.setString(2, ra.getStoredProcedure());
			stmt.setString(3, ra.getCsvParameteres());
			stmt.setInt(4, ra.getHora());
			stmt.setBoolean(5, ra.isDiario());
			stmt.setBoolean(6, ra.isIncluirFinDeSemana());
			stmt.setInt(7, ra.getDiaDeLaSemana());
			stmt.setInt(8, ra.getDiaDelMes());
			if (ra.getFechaUnicaVez() == null) {
				stmt.setDate(9, null);
			} else {
				stmt.setDate(9, new java.sql.Date(ra.getFechaUnicaVez()
						.getTime()));
			}
			stmt.setString(10, ra.getEmails());
			stmt.setInt(11, ra.getDifusion());
			stmt.setInt(12, ra.getBase());
			stmt.setString(13, ra.getJava());
			stmt.setInt(14, ra.getId());
			
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error("Error al actualizar reportes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void borrar(ReporteAutomatico ra) throws SystemException {
		logger.debug("Borrando reporte automatico");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "delete from reportes_automaticos where id = ?";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, ra.getId());
			
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error("Error al borrar reporte automatico", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public ReporteAutomatico get(int id) throws SystemException {
		logger.debug("Buscando reporte");
		Connection con = null;
		CallableStatement stmt = null;
		ReporteAutomatico ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "select * from reportes_automaticos where id = ?";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = ReporteAutomatico.getMapping(rs);
			}
		} catch (Exception e) {
			logger.error("Error al get", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;

	}

	public ReportesAutomaticosConfiguracion getConfiguracion()
			throws SystemException {
		logger.debug("Buscando getConfiguracion");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "select * from reportes_automaticos_configuracion";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return ReportesAutomaticosConfiguracion.getMapping(rs);
			}
		} catch (Exception e) {
			logger.error("Error al getConfiguracion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public void update(ReportesAutomaticosConfiguracion configuracion)
			throws SystemException {
		logger.debug("Updating config");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "update reportes_automaticos_configuracion set mail_from = ?,"
					+ " pass = ?, mails_en_caso_de_error = ?";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, configuracion.getMailFrom());
			stmt.setString(2, configuracion.getPass());
			stmt.setString(3, configuracion.getMailsDeError());
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error("Error al updatear config", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public List<ReporteAutomatico> getJavaAgendadosACorrer() throws SystemException {
		logger.debug("Buscando clases a correr");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAutomatico> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_java_agendados() }";
			
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ReporteAutomatico>();
			while (rs.next()) {
				ret.add(ReporteAutomatico.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("Error al getJavaAgendadosACorrer", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<ReporteDeudaEmpresaCab> getReportesDeudaEmpPeriodo() throws SystemException {
		
		logger.debug("Buscando Reportes Deuda Empresa Periodo");
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDeudaEmpresaCab> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call informes.buscar_reportes_deuda_empresas_periodo() }";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ReporteDeudaEmpresaCab>();
			while (rs.next()) {
				ret.add(ReporteDeudaEmpresaCab.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return ret;
	}
	
	public List<ReporteAutomatico> getAlertasVencimientoContactosCRM() throws SystemException {
		logger.debug("Buscando Alertas Vencimiento CRM a correr");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAutomatico> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_crm_alertas_schedule() }";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ReporteAutomatico>();
			while (rs.next()) {
				ret.add(ReporteAutomatico.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("Error al getAlertasVencimientoContactosCRM", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	public List<ContactoCRM> getContactosCRMaVencer(ReporteAutomatico ra)
			throws SystemException {
		
		logger.debug("Corriendo reporte automático " +ra.getTitulo() + " - " + ra.getStoredProcedure()!=null?ra.getStoredProcedure():ra.getJava());

		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		TreeMap<Integer,ContactoCRM> mapaContactos = new TreeMap<Integer,ContactoCRM>();
		
		List<ContactoCRM> contactos = new ArrayList<ContactoCRM>();
		try {
			if(ra.getBase()==2){
				con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			}else{
				con = ConnectionHelper.getConnection();
			}		
			String sql = ra.getLlamadaStoredProcedure();
			stmt = con.prepareCall(sql.toString());
			ra.generateStatementParameters(stmt);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				contacto = ContactoCRM.getMappingConSeguimiento("con_", rs);
				
				if(!mapaContactos.containsKey(contacto.getIdContacto())){
					mapaContactos.put(contacto.getIdContacto(), contacto);
				}else{
					ContactoCRM contac = mapaContactos.get(contacto.getIdContacto());
					if(contacto.getSeguimiento() != null && contacto.getSeguimiento().size() > 0){
						contac.getSeguimiento().add(contacto.getSeguimiento().get(0));
					}
					mapaContactos.put(contacto.getIdContacto(), contac);

				}
			}
		} catch (Exception e) {
			logger.error("Error al correr reporte automatico de alertas crm "
					+ ra.getStoredProcedure(), e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		Set<Integer> keys = mapaContactos.descendingKeySet(); //mapaContactos.keySet();
		
		for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
			Integer key = iterator.next();
			contacto = mapaContactos.get(key);
			contactos.add(contacto);
		}
		
		return contactos;
	}
	
	public List<ContactoCRM> getContactosCRMaVencerUrgente(ReporteAutomatico ra)
			throws SystemException {
		
		logger.debug("Corriendo reporte automático " +ra.getTitulo() + " - " + ra.getStoredProcedure()!=null?ra.getStoredProcedure():ra.getJava());

		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		TreeMap<Integer,ContactoCRM> mapaContactos = new TreeMap<Integer,ContactoCRM>();
		
		List<ContactoCRM> contactos = new ArrayList<ContactoCRM>();
		try {
//			if(ra.getBase()==2){
//				con = ConnectionHelper.getConnectionPortalEmpleadores();
//			}else{
				con = ConnectionHelper.getConnection();
//			}
			String sql = "{call crm.buscar_contactos_a_vencer_urgente() }" ;
//			String sql = ra.getLlamadaStoredProcedure();
			stmt = con.prepareCall(sql.toString());
//			ra.generateStatementParameters(stmt);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				contacto = ContactoCRM.getMappingConSeguimiento("con_", rs);
				
				if(!mapaContactos.containsKey(contacto.getIdContacto())){
					mapaContactos.put(contacto.getIdContacto(), contacto);
				}else{
					ContactoCRM contac = mapaContactos.get(contacto.getIdContacto());
					if(contacto.getSeguimiento() != null && contacto.getSeguimiento().size() > 0){
						contac.getSeguimiento().add(contacto.getSeguimiento().get(0));
					}
					mapaContactos.put(contacto.getIdContacto(), contac);

				}
			}
		} catch (Exception e) {
			logger.error("Error al correr reporte automatico de alertas crm "
					+ ra.getStoredProcedure(), e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		Set<Integer> keys = mapaContactos.descendingKeySet(); //mapaContactos.keySet();
		
		for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
			Integer key = iterator.next();
			contacto = mapaContactos.get(key);
			contactos.add(contacto);
		}
		
		return contactos;
	}
	
	public List<ContactoCRM> getResumenContactosCRMsinCerrar(ReporteAutomatico ra)
			throws SystemException {
		
		logger.debug("Corriendo reporte automático " +ra.getTitulo() + " - " + ra.getStoredProcedure()!=null?ra.getStoredProcedure():ra.getJava());

		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		TreeMap<Integer,ContactoCRM> mapaContactos = new TreeMap<Integer,ContactoCRM>();
		
		List<ContactoCRM> contactos = new ArrayList<ContactoCRM>();
		try {

			con = ConnectionHelper.getConnection();
			String sql = "{call crm.resumen_crm_sin_cerrar() }" ;
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				contacto = ContactoCRM.getMappingConSeguimiento("con_", rs);
				
				if(!mapaContactos.containsKey(contacto.getIdContacto())){
					mapaContactos.put(contacto.getIdContacto(), contacto);
				}else{
					ContactoCRM contac = mapaContactos.get(contacto.getIdContacto());
					if(contacto.getSeguimiento() != null && contacto.getSeguimiento().size() > 0){
						contac.getSeguimiento().add(contacto.getSeguimiento().get(0));
					}
					mapaContactos.put(contacto.getIdContacto(), contac);

				}
			}
		} catch (Exception e) {
			logger.error("Error al correr reporte automatico de resumen crm "
					+ ra.getStoredProcedure(), e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		Set<Integer> keys = mapaContactos.descendingKeySet(); //mapaContactos.keySet();
		
		for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
			Integer key = iterator.next();
			contacto = mapaContactos.get(key);
			contactos.add(contacto);
		}
		
		return contactos;
	}
	
	public List<ContactoCRM> getResumenContactosCRMCerrados(ReporteAutomatico ra)
			throws SystemException {
		
		logger.debug("Corriendo reporte automático " +ra.getTitulo() + " - " + ra.getStoredProcedure()!=null?ra.getStoredProcedure():ra.getJava());

		Connection con = null;
		CallableStatement stmt = null;
		ContactoCRM contacto = null;
		TreeMap<Integer,ContactoCRM> mapaContactos = new TreeMap<Integer,ContactoCRM>();
		
		List<ContactoCRM> contactos = new ArrayList<ContactoCRM>();
		try {

			con = ConnectionHelper.getConnection();
			String sql = "{call crm.resumen_crm_cerrados() }" ;
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				contacto = ContactoCRM.getMappingConSeguimiento("con_", rs);
				
				if(!mapaContactos.containsKey(contacto.getIdContacto())){
					mapaContactos.put(contacto.getIdContacto(), contacto);
				}else{
					ContactoCRM contac = mapaContactos.get(contacto.getIdContacto());
					if(contacto.getSeguimiento() != null && contacto.getSeguimiento().size() > 0){
						contac.getSeguimiento().add(contacto.getSeguimiento().get(0));
					}
					mapaContactos.put(contacto.getIdContacto(), contac);

				}
			}
		} catch (Exception e) {
			logger.error("Error al correr reporte automatico de resumen crm cerrados"
					+ ra.getStoredProcedure(), e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		Set<Integer> keys = mapaContactos.descendingKeySet(); //mapaContactos.keySet();
		
		for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
			Integer key = iterator.next();
			contacto = mapaContactos.get(key);
			contactos.add(contacto);
		}
		
		return contactos;
	}
	
}

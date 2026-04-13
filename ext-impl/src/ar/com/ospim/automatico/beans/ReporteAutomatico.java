package ar.com.ospim.automatico.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import ar.com.global.beans.Destinatario;
import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;

public class ReporteAutomatico {
	
	private int id;
	private String titulo;
	private int hora;
	private boolean diario;
	private boolean incluirFinDeSemana;
	private int diaDeLaSemana;
	private int diaDelMes;
	private Date fechaUnicaVez;
	private String storedProcedure;
	private String csvParameteres;
	private String emails;
	private Date ultimaEjecucion;
	private int difusion;
	private int base;
	private String java;
	
	private static final String ALERTA_VENCIMIENTO_CRM_CONTACTO = "CRM Alerta Vencimiento";

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setStoredProcedure(String storedrocedure) {
		this.storedProcedure = storedrocedure;
	}

	public String getStoredProcedure() {
		return storedProcedure;
	}

	public void setCsvParameteres(String csvParameteres) {
		this.csvParameteres = csvParameteres;
	}

	public String getCsvParameteres() {
		return csvParameteres;
	}

	public String getUltimaEjecucionAsString() {
		return null != ultimaEjecucion ? DateUtils.format(ultimaEjecucion,
				DateUtils.SHORT) : "";
	}

	public String getDiaDeLaSemanaString() {
		String dia = "";
		switch (diaDeLaSemana) {
		case 0:
			dia = "Todos";
			break;
		case 1:
			dia = "Domingo";
			break;
		case 2:
			dia = "Lunes";
			break;
		case 3:
			dia = "Martes";
			break;
		case 4:
			dia = "Miercoles";
			break;
		case 5:
			dia = "Jueves";
			break;
		case 6:
			dia = "Viernes";
			break;
		case 7:
			dia = "Sabado";
			break;
		}
		return dia;
	}

	public ResultadoReporteAutomatico execute(Calendar calendar)
			throws SystemException {
		
		if (ejecutar(calendar)) {
			
			if(this.getTitulo().equalsIgnoreCase(ALERTA_VENCIMIENTO_CRM_CONTACTO)){
				
				List<ContactoCRM> contactosCRMaVencer = ReportesServiceUtil.getContactosCRMparaAlertas(this);
				
				this.agendarAlertasContactosCRM(contactosCRMaVencer);
				
				/*Hago asi porque no devuelvo resultados en Xls ni Destinatarios p avisar de la corrida.*/
				this.setUltimaEjecucion(DateUtils.getCalendarGMTMenos3().getTime());
				ReportesServiceUtil.reporteEjecutado(this);
				
				return null;
			}// fin ALERTA_VENCIMIENTO_CRM_CONTACTO
			
			return ReportesServiceUtil.correrReporte(this);
		}
		return null;
	}

	private void agendarAlertasContactosCRM(
			List<ContactoCRM> contactosCRMaVencer) throws SystemException {
		
		List<Feriado> feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
		
		for (Iterator<ContactoCRM> iterator = contactosCRMaVencer.iterator(); iterator.hasNext();) {
			
			ContactoCRM cont = iterator.next();
			
			String usuNotif = null, sectorNotif = null, emailsNotificaAlerta = null; 
			
			if(cont.getEstado().equals(ContactoCRM.ESTADOS.DERIVADO)){
				usuNotif = cont.getDerivacion().getUsuario();
				sectorNotif = cont.getDerivacion().getGrupo() ;
			}else{
				usuNotif = cont.getAltaUsr();
			}	
			
			emailsNotificaAlerta = this.armarListaDestinatariosAlerta(usuNotif, sectorNotif);
			
			int horario = 0, diasHabilesAlerta = 2; // 48hs
			Calendar fechaAltaContacto = DateUtils.getCalendarGMTMenos3(); //Calendar.getInstance();
			Calendar fechaVencimContacto = DateUtils.getCalendarGMTMenos3(); //Calendar.getInstance();
			
			fechaVencimContacto.setTime(cont.getAltaFecha());
			
			while(diasHabilesAlerta > 0){
				fechaVencimContacto.add(Calendar.DATE, 1);
				diasHabilesAlerta--;
				while(DateUtils.esFeriadoOFinde(fechaVencimContacto.getTime(), feriados)){
					fechaVencimContacto.add(Calendar.DATE, 1);
				}	
			}
			
			fechaAltaContacto.setTime(cont.getAltaFecha());
			horario = fechaAltaContacto.get(Calendar.HOUR_OF_DAY);
			horario = horario -3; // 3 horas previas de alerta 
			if(horario < 9){
				horario = 9;
			}
			
			ReporteAutomatico raAux = new ReporteAutomatico();
//			raAux.setFechaUnicaVez(new Date()); //antes fecha de hoy, porque asi es como busque los que se iban a vencer...
			raAux.setFechaUnicaVez(fechaVencimContacto.getTime()); // ahora pega fecha pero sin tener encuenta los fines de sem ni feriados
			raAux.setHora(horario);
			raAux.setTitulo("Alerta Vencimiento CRM Contacto N°: " + cont.getIdContacto());
			raAux.setJava("ar.com.ospim.crm.beans.AvisoVencimiento");
			raAux.setEmails(emailsNotificaAlerta);
			raAux.setBase(1);
			raAux.setCsvParameteres(null);
			raAux.setDiaDeLaSemana(0);
			raAux.setDiaDelMes(0);
			raAux.setDiario(false); // true
			raAux.setDifusion(0);
			raAux.setIncluirFinDeSemana(false);
			raAux.setStoredProcedure(null);
			raAux.setUltimaEjecucion(null);
			
			ReportesServiceUtil.save(raAux);
		}
	}
	
	public List<Destinatario> executeDifusion(Calendar calendar)
			throws SystemException {
		if (ejecutar(calendar)) {
			return ReportesServiceUtil.correrReporteDifusion(this);
		}
		return null;
	}

	public boolean ejecutarJava(Calendar calendar, ReporteAutomatico ra) {
		
		/*Seteamos si corre o no con un archivo de propiedades */
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "liferay_schedulers.properties");
		InputStream stream;
		Boolean debeCorrer = false;
		String contenido = null;
		try {
			stream = new FileInputStream(configFile);
		
			Properties props = new Properties();
			props.load(stream);
			// WS Client de Prevención 
			//PROD 331 y 391
			//QA 302
			if(ra.getId()==331 || ra.getId()==391 || ra.getId()==302){   // WS Client de Prevención
				contenido = props.getProperty("corre_ws_client");
			}else{
				contenido = props.getProperty("corre_java_agendado");
			}
			
			if(contenido != null){
				debeCorrer = Boolean.valueOf(contenido);	
			}		

		} catch (FileNotFoundException e1) {
			debeCorrer = false;
		} catch (IOException e2) {
			debeCorrer = false;
		} 
		return debeCorrer && ejecutar(calendar);
		/* */
		
	}
	
	public boolean ejecutarJavaCiclico(Calendar calendar) {
		
		boolean cumpleDia = false;
//		if (yaFueEjecutado(calendar)) {
//			return false;
//		}

		int dia = calendar.get(Calendar.DAY_OF_WEEK);

		// Diario
		if ((diario && incluirFinDeSemana)
				|| (diario && !incluirFinDeSemana && !(dia == Calendar.SATURDAY || dia == Calendar.SUNDAY))) {
			cumpleDia = true;
		}

		// Dia de la semana
		if (diaDeLaSemana != 0
				&& calendar.get(Calendar.DAY_OF_WEEK) == diaDeLaSemana) {
			cumpleDia = true;
		}
		
		// Dia del mes
		if (diaDelMes != 0 && calendar.get(Calendar.DAY_OF_MONTH) == diaDelMes) {
			cumpleDia = true;
		}
//		1 sola corrida diaria
		if (cumpleDia && calendar.get(Calendar.HOUR_OF_DAY) == hora) {
			return true;
		}
//		varias corridas diarias
		if (cumpleDia && hora == 99) {
			return true;
		}
		return false;
		
	}

	public List<ContactoCRM> ejecutarAlertaVencimientoCRM(Calendar calendar)
			throws SystemException {
		if (ejecutar(calendar)) {
			return ReportesServiceUtil.getContactosCRMparaAlertas(this);
		}
		return null;
	}
	
	public boolean ejecutar(Calendar calendar) {
		boolean cumpleDia = false;
		if (yaFueEjecutado(calendar)) {
			return false;
		}

		int dia = calendar.get(Calendar.DAY_OF_WEEK);

		// Diario
		if ((diario && incluirFinDeSemana)
				|| (diario && !incluirFinDeSemana && !(dia == Calendar.SATURDAY || dia == Calendar.SUNDAY))) {
			cumpleDia = true;
		}

		// Dia de la semana
		if (diaDeLaSemana != 0
				&& calendar.get(Calendar.DAY_OF_WEEK) == diaDeLaSemana) {
			cumpleDia = true;
		}

		// Dia del mes
		if (diaDelMes != 0 && calendar.get(Calendar.DAY_OF_MONTH) == diaDelMes) {
			cumpleDia = true;
		}

		// Unico dia
		if (fechaUnicaVez != null) {
//			Calendar fechaUnica = Calendar.getInstance();
			Calendar fechaUnica = DateUtils.getCalendarGMTMenos3();
			
			fechaUnica.setTime(fechaUnicaVez);
			if (fechaUnica.get(Calendar.DAY_OF_MONTH) == calendar
					.get(Calendar.DAY_OF_MONTH)
					&& fechaUnica.get(Calendar.MONTH) == calendar
							.get(Calendar.MONTH)
					&& fechaUnica.get(Calendar.YEAR) == calendar
							.get(Calendar.YEAR))
				cumpleDia = true;

		}

		if (cumpleDia && (calendar.get(Calendar.HOUR_OF_DAY) == hora || 99 == hora)) {
			return true;
		}

		return false;
	}

	private boolean yaFueEjecutado(Calendar calendar) {
		if (ultimaEjecucion != null) {
//			Calendar ultima = Calendar.getInstance(TimeZone.getTimeZone(DateUtils.TIME_ZONE_AR));
			Calendar ultima = DateUtils.getCalendarGMTMenos3();
			
			ultima.setTime(ultimaEjecucion);
			long tiempo = calendar.getTimeInMillis() - ultima.getTimeInMillis();
			// si el tiempo transcurrido es menor a una hora
			if (ultima.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY)
					&& tiempo < 3600000) {
				return true;
			}
		}
		return false;
	}

	public void setDiaDeLaSemana(int diaDeLaSemana) {
		this.diaDeLaSemana = diaDeLaSemana;
	}

	public int getDiaDeLaSemana() {
		return diaDeLaSemana;
	}

	public void setHora(int hora) {
		this.hora = hora;
	}

	public int getHora() {
		return hora;
	}

	public void setIncluirFinDeSemana(boolean incluirFinDeSemana) {
		this.incluirFinDeSemana = incluirFinDeSemana;
	}

	public boolean isIncluirFinDeSemana() {
		return incluirFinDeSemana;
	}

	public void generateStatementParameters(CallableStatement stmt)
			throws NumberFormatException, SQLException, ParseException {
		if (StringUtils.checkEmpty(csvParameteres)) {
			return;
		}
		int i = 0;
		for (String param : csvParameteres.split(",")) {
			i++;
			String valor = param.trim().split("=")[0].trim();
			String tipo = param.trim().split("=")[1].trim();
			if (tipo.equalsIgnoreCase("Int")
					|| tipo.equalsIgnoreCase("Integer")) {
				if (valor.equalsIgnoreCase("null")) {
					stmt.setNull(i, Types.INTEGER);
				} else {
					stmt.setInt(i, Integer.valueOf(valor));
				}
			} else if (tipo.equalsIgnoreCase("String")) {
				if (valor.equalsIgnoreCase("null")) {
					stmt.setString(i, null);
				} else {
					stmt.setString(i, valor);
				}
			} else if (tipo.equalsIgnoreCase("Boolean")) {
				if (valor.equalsIgnoreCase("null")) {
					stmt.setNull(i, Types.BOOLEAN);
				} else {
					stmt.setBoolean(i, Boolean.valueOf(valor));
				}
			} else if (tipo.equalsIgnoreCase("Date")) {
				if (valor.equalsIgnoreCase("null")) {
					stmt.setDate(i, null);
				} else {
					Date fecha = DateUtils.parse(valor, DateUtils.LONG_SEC);
					stmt.setDate(i, new java.sql.Date(fecha.getTime()));
				}
			}
		}
	}

	public String getLlamadaStoredProcedure() {
		StringBuilder signos = new StringBuilder();
		String signosStr = "";
		if (StringUtils.checkNotEmpty(csvParameteres)) {
			for (int i = 0; i < csvParameteres.split(",").length; i++) {
				signos.append("?,");
			}
			signosStr = signos.substring(0, signos.length() - 1);
		}

		String sql = "{call " + storedProcedure + "(" + signosStr + ")}";
		return sql;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getEmails() {
		return emails;
	}

	public void setUltimaEjecucion(Date ultimaEjecucion) {
		this.ultimaEjecucion = ultimaEjecucion;
	}

	public Date getUltimaEjecucion() {
		return ultimaEjecucion;
	}

	@SuppressWarnings("unchecked")
	public List<String> getEmailsAsList() {
		if (emails != null) {
			return Arrays.asList(emails.split(","));
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setDiario(boolean diario) {
		this.diario = diario;
	}

	public boolean isDiario() {
		return diario;
	}

	public void setDiaDelMes(int diaDelMes) {
		this.diaDelMes = diaDelMes;
	}

	public int getDiaDelMes() {
		return diaDelMes;
	}

	public void setFechaUnicaVez(Date fechaUnicaVez) {
		this.fechaUnicaVez = fechaUnicaVez;
	}

	public Date getFechaUnicaVez() {
		return fechaUnicaVez;
	}

	public String getFechaUnicaVezAsString() {
		return null != fechaUnicaVez ? DateUtils.format(fechaUnicaVez,
				DateUtils.SHORT) : "";
	}	
	
	public int getDifusion() {
		return difusion;
	}

	public void setDifusion(int difusion) {
		this.difusion = difusion;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public static ReporteAutomatico getMapping(ResultSet rs)
			throws SQLException {
		
		ReporteAutomatico ra = new ReporteAutomatico();
		ra.setId(rs.getInt("id"));
		ra.setTitulo(rs.getString("titulo"));
		ra.setStoredProcedure(rs.getString("stored_procedure"));
		ra.setCsvParameteres(rs.getString("csv_parameteres"));
		ra.setHora(rs.getInt("hora"));
		ra.setEmails(rs.getString("mails_destino"));
		ra.setUltimaEjecucion(rs.getTimestamp("ultima_ejecucion"));
		ra.setDiario(rs.getBoolean("diario"));
		ra.setIncluirFinDeSemana(rs.getBoolean("incluir_fin_de_semana"));
		ra.setDiaDeLaSemana(rs.getInt("dia_de_la_semana"));
		ra.setDiaDelMes(rs.getInt("dia_del_mes"));
		ra.setFechaUnicaVez(rs.getDate("fecha_unica_vez"));
		ra.setDifusion(rs.getInt("difusion"));
		ra.setBase(rs.getInt("base"));
		ra.setJava(rs.getString("java"));
		
		return ra;
	}

	public String getJava() {
		return java;
	}

	public void setJava(String java) {
		this.java = java;
	}
	
	private String armarListaDestinatariosAlerta(String usuNotif, String sectorNotif){

		DerivacionNotificacion dn = null;
		List<DerivacionNotificacion> destinatarios = new ArrayList<DerivacionNotificacion>();
		
		String emailsNotificaAlerta = "";
		boolean estanResponsables = false;
		
		try {
			if(usuNotif!=null && usuNotif.equalsIgnoreCase("TODOS")){ // derivacion todo el sector
				destinatarios = (ArrayList<DerivacionNotificacion>) CrmServiceUtil.getNotificacionDerivacionSector(sectorNotif);
			}else{   // un solo usuario derivado
				dn = CrmServiceUtil.getNotificacionDerivacion(usuNotif);
				if(dn != null){
					destinatarios.add(dn);
				}
			}
		} catch (SystemException e) {
//			nada
		}
			
		for (Iterator<DerivacionNotificacion> iterator = destinatarios.iterator(); iterator.hasNext();) {
			DerivacionNotificacion derivNotif = iterator.next();
			
			if(!estanResponsables){
				
				String[] auxEmails = derivNotif.getResponsableEmail().split(";");
				
				for (int i = 0; i < auxEmails.length; i++) {
					if(emailsNotificaAlerta.length()==0){
						emailsNotificaAlerta = auxEmails[i];
					}else{
						emailsNotificaAlerta = emailsNotificaAlerta + "," + auxEmails[i];
					}
				}
				estanResponsables = true;
			}
			
			emailsNotificaAlerta = emailsNotificaAlerta + "," + derivNotif.getDerivacionEmail();
		}

		if(emailsNotificaAlerta==null || emailsNotificaAlerta.length() == 0){
			emailsNotificaAlerta = "info@ospim.org.ar";
		}
		
		return emailsNotificaAlerta;
	}
}

package ar.com.ospim.automatico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compass.core.util.backport.java.util.Arrays;

import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.beans.EnvioInfoAdicionalPS;
import ar.com.ospim.autorizaciones.beans.SolicitudAutorizacionPS;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.job.IntervalJob;
import com.liferay.portal.kernel.job.JobExecutionContext;
import com.liferay.portal.kernel.job.JobExecutionException;
import com.liferay.portal.kernel.job.JobSchedulerUtil;
import com.liferay.portal.kernel.job.Scheduler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Time;

public class AgendadoJavaScheduler implements Scheduler {
	private static Log logger = LogFactoryUtil.getLog(AgendadoJavaScheduler.class);

	public void schedule() {
		JobSchedulerUtil.schedule(miJavaJob);
	}

	public void unschedule() {
		JobSchedulerUtil.unschedule(miJavaJob);
	}

	private IntervalJob miJavaJob = new AgendadoJob();

	public static class AgendadoJob implements IntervalJob {

		public void execute(JobExecutionContext jobExecutionContext)
				throws JobExecutionException {
			ejecutarReportes();
		}

		public void ejecutarReportes() {
			
			logger.debug("Ejecutando AgendadoJob");
			
			List<ReporteAutomatico> reportesACorrer = null;
			ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
			List<String> em = new ArrayList<String>();
			try {
				reportesACorrer = ReportesServiceUtil.getJavaAgendadosACorrer(); 
			} catch (Exception e) {
				logger.error("Error al buscar java reportes automaticos", e);
			}

			if (reportesACorrer != null) {
//				logger.debug("Java's a correr " + reportesACorrer.size());
				
//				Calendar calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone(DateUtils.TIME_ZONE_AR));
				Calendar calendar = DateUtils.getCalendarGMTMenos3();
				for (ReporteAutomatico ra : reportesACorrer) {
					
					if(ra.ejecutarJava(calendar, ra)){ // evalua si corresponde ejecutar...						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						logger.debug("Se comienza a correr: "+ ra.getTitulo() );
						logger.debug("en horario: "+ sdf.format(calendar.getTimeInMillis()) );
						try {
							Class miclase = null;
							AgendadoJava javaJob = null;
	//						Class miclase = Class.forName ("ObjetoMiClase"); 
	//						utilizamos reflextion
	//						http://www.todoexpertos.com/categorias/tecnologia-e-internet/programacion/java/respuestas/1211780/crear-objetos-a-partir-de-una-cadena-recibida
								if(ra.getJava() != null && !ra.getJava().isEmpty()){							
									miclase = Class.forName (ra.getJava());
									javaJob = (AgendadoJava) miclase.newInstance();								
								}
	
							// obtener configuracion del reporte_automatico
							rac = ReportesServiceUtil.getConfiguracion();
	
							if(rac == null || javaJob == null){
								throw new Exception();
							}	
							// correr al Java
							javaJob.correrAgendado(ra);

							//Necesito que para los WS de PS para autorizaciones se maneje la fecha de ultima ejecucion, local en la clase 
							if(!miclase.equals(SolicitudAutorizacionPS.class) 
									&& !miclase.equals(EnvioInfoAdicionalPS.class)  ) {
							// proponer fecha ultima ejecucion
								ra.setUltimaEjecucion(calendar.getTime());
							
							// actualizar fecha ultima ejecucion
								ReportesServiceUtil.reporteEjecutado(ra);
							}
							HSSFWorkbook wb =  javaJob.getResultados();
							
								if(wb != null){
									String asunto = ra.getTitulo()+ " " + DateUtils.format(calendar.getTime(),DateUtils.LONG);
									String mensaje = "Reporte de novedades de padrón capitados enviados por Ospim a Prevención. "+											
											"Cualquier aclaración por favor comunicarse con: Sandra Querin (5238-3915) "+
											"o Sergio Valentini (5238-3907) .";
											
                                    for(String strE:ra.getEmailsAsList()) {
                                      em.clear();
                                      em.add(strE);
									  EnviaEmailsThread.enviarMailDesatendido(asunto, mensaje, em, wb, "novedadesWS.xls");
                                    }  
								}
							
							}catch (Exception e) {
								logger.error("Error de ejecucion Agendado Java");
								logger.error(e);
								
								String asunto = "Error! - "+ ra.getTitulo()+ " - "+ DateUtils.format(calendar.getTime(),DateUtils.LONG);
								String mensaje = e.getMessage()!=null?e.getMessage():"";

//								MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), 
//										rac.getPass(), 
//										rac.getMailsErrorAsList(), 
//										"Error! - "+ ra.getTitulo()+ " - "+ DateUtils.format(calendar.getTime(),DateUtils.LONG),
//										e.getMessage()!=null?e.getMessage():"",
//										null);
								for(String strE:rac.getMailsErrorAsList()) {
                                    em.clear();
                                    em.add(strE);
								    EnviaEmailsThread.enviarMailDesatendido(asunto, mensaje, em, 1);
								}    
							}	
						/*Fin try*/
					}
					/*Fin if*/
				}
				/* Fin for*/
			}
			/* Fin if*/
		}
		/*Fin ejecutarReportes*/
		
		public long getInterval() {
			return Time.MINUTE * 60;
//			return Time.MINUTE * 1;
//			return Time.MINUTE * 2;
		}

	}

	public static class ReportesAutomaticosConfiguracion {
		private String mailFrom;
		private String pass;
		private String mailsDeError;

		public void setMailFrom(String mailFrom) {
			this.mailFrom = mailFrom;
		}

		public String getMailFrom() {
			return mailFrom;
		}

		public void setPass(String pass) {
			this.pass = pass;
		}

		public String getPass() {
			return pass;
		}

		public void setMailsDeError(String mailsDeError) {
			this.mailsDeError = mailsDeError;
		}

		public String getMailsDeError() {
			return mailsDeError;
		}

		@SuppressWarnings("unchecked")
		public List<String> getMailsErrorAsList() {
			return Arrays.asList(mailsDeError.split(","));
		}

		public static ReportesAutomaticosConfiguracion getMapping(ResultSet rs)
				throws SQLException {
			ReportesAutomaticosConfiguracion rac = new ReportesAutomaticosConfiguracion();
			rac.setMailFrom(rs.getString("mail_from"));
			rac.setMailsDeError(rs.getString("mails_en_caso_de_error"));
			rac.setPass(rs.getString("pass"));
			return rac;
		}
	}
}

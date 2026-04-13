package ar.com.ospim.automatico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compass.core.util.backport.java.util.Arrays;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Destinatario;
import ar.com.global.services.OpenemmClient;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.beans.ResultadoReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.job.IntervalJob;
import com.liferay.portal.kernel.job.JobExecutionContext;
import com.liferay.portal.kernel.job.JobExecutionException;
import com.liferay.portal.kernel.job.JobSchedulerUtil;
import com.liferay.portal.kernel.job.Scheduler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Time;

public class ReportesScheduler implements Scheduler {
	private static Log logger = LogFactoryUtil.getLog(ReportesScheduler.class);

	public void schedule() {
		JobSchedulerUtil.schedule(_testJob);
	}

	public void unschedule() {
		JobSchedulerUtil.unschedule(_testJob);
	}

	private IntervalJob _testJob = new ReportesJob();

	public static class ReportesJob implements IntervalJob {

		public void execute(JobExecutionContext jobExecutionContext)
				throws JobExecutionException {
			ejecutarReportes();
		}

		public void ejecutarReportes() {
			List<ReporteAutomatico> reportesACorrer = null;
			ReportesAutomaticosConfiguracion rac = null;
			List<Destinatario> destinatarios=null;
			ResultadoReporteAutomatico res=null;
			List<String>em = new ArrayList<String>();
			try {
				reportesACorrer = ReportesServiceUtil.getReportesACorrer();
			} catch (Exception e) {
				logger.error("Error al buscar reportes automaticos", e);
			}

			if (reportesACorrer != null) {
//				Calendar calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone(DateUtils.TIME_ZONE_AR));
				Calendar calendar = DateUtils.getCalendarGMTMenos3();
				
				for (ReporteAutomatico ra : reportesACorrer) {
					
					try {
						if (ra.getDifusion() > 0) {			
							destinatarios = ra.executeDifusion(calendar);
						}else{
							res = ra.execute(calendar);
						}
						if (res != null || (destinatarios!=null && destinatarios.size()>0)) {
							if (rac == null) {
								rac = ReportesServiceUtil.getConfiguracion();
							}							

							if (ra.getDifusion() > 0) {						
								SimpleDateFormat sdf=new SimpleDateFormat("ddMMyyyy");
								Boletin boletin=new Boletin();
								boletin.setAsunto("UOMA - PORTAL EMPLEADORES");
								boletin.setNombre(ra.getStoredProcedure()+sdf.format(new Date()));
								boletin.setObservaciones("REPORTE AUTOMATICO DIFUSION");								
								int id_lista=OpenemmClient.crearLista(boletin);
								OpenemmClient.insertSubscriberList(destinatarios, id_lista);
								boletin.setIdListaBoletin(id_lista);
								boletin.setDifusion(true);
								
								int id_mailing=OpenemmClient.crearMail(boletin);
								boletin.setIdBoletin(id_mailing);
										
								//OpenemmClient.insertarContenido(boletin);		
								int result=OpenemmClient.enviarMail(boletin, "TODOS");
								if(result>0){
									ra.setUltimaEjecucion(calendar.getTime());
								}

							} else {
								HSSFWorkbook wb = ReporteAutomaticoXls.obtenerXls(res, ra);
								
								String asunto = ra.getTitulo();
								String mensaje = ra.getTitulo()+ " " + DateUtils.format(calendar.getTime(),DateUtils.LONG);
								
								if(wb != null){
									for(String strE:ra.getEmailsAsList()) {
									   em.clear();
									   em.add(strE);
									   EnviaEmailsThread.enviarMailDesatendido(asunto, mensaje, em, wb, ra.getTitulo() + ".xls");
									}	   
								}
								ra.setUltimaEjecucion(calendar.getTime());
							}
							
							ReportesServiceUtil.reporteEjecutado(ra);
						}	
					} catch (Exception e) {
						logger.error("Error al ejecutar reporte automatico", e);
						try {
							if (rac == null) {
								rac = ReportesServiceUtil.getConfiguracion();
							}
							String asunto = "Error! - "+ ra.getTitulo()+ " - "+ DateUtils.format(calendar.getTime() ,DateUtils.LONG);
							String mensaje = e.getMessage();
							for(String strE:rac.getMailsErrorAsList()) {
								   em.clear();
								   em.add(strE);
							       EnviaEmailsThread.enviarMailDesatendido(asunto, mensaje, rac.getMailsErrorAsList(), 1);
							}       
						} catch (SystemException e1) {
							logger.fatal("Error al tratar de enviar mail de error de mail automatico",e1);
						}
					}
					/*Fin try*/
				}
				/* Fin for*/
			}
		}

		public long getInterval() {
			return Time.MINUTE * 60;
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

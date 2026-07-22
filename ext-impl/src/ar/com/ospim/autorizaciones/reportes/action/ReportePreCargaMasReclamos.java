package ar.com.ospim.autorizaciones.reportes.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.crm.beans.ReportePreCargaMasReclamosExcel;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

public class ReportePreCargaMasReclamos extends AgendadoJava {


	
	private static Log _log = LogFactoryUtil
			.getLog(ReportePreCargaMasReclamos.class);
	private HSSFWorkbook wb;
	private HSSFWorkbook wbSURGE;
	
	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		_log.debug("ReportePreCargaMasReclamos");
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		
		String fecha = null;
		
		fecha = DateUtils.getDateString(calendar.getTime(), "dd-MM-yyyy");
		
		
		wb = new HSSFWorkbook();	
		wb= ReportePreCargaMasReclamosExcel.generaReporte();
		enviarReporte(fecha, wb);
		
        wbSURGE = new HSSFWorkbook();	
		wbSURGE= ReportePreCargaMasReclamosExcel.generaReportePendientesSURGE();
		enviarReporteSURGE(fecha, wbSURGE);
		
			
	}



	private void enviarReporte(String fecha, HSSFWorkbook wb) {
		
		// obtener configuracion del reporte_automatico
		ReportesAutomaticosConfiguracion rac = null;
      	try {
	         rac = ReportesServiceUtil.getConfiguracion();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		List<String> emails;
		String destinos;
		
		emails = new ArrayList<String>();
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_PRE_CARGA_PENDIENTE");
		Integer delay=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("EMAIL_DELAY"));
		
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails = new ArrayList<String>(); //DS Agregado 28-10-2022 por problema en envia de email. A efectos de generar una prueba
			emails.add(to);

//Agregado 28-10-2022			
			MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
					emails, "Reclamos prestacionales en estado pre-carga y pendiente " + fecha,
					"Reclamos prestacionales en estado pre-carga y pendiente " + fecha, wb, "Reclamos prestacionales en estado pre-carga y pendiente " + fecha + ".xls");
			try {
				Thread.sleep(delay *1000);
			} catch (InterruptedException e) {}
//Fin Agregado			
			
		}
	}
	
	
	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void enviarReporteSURGE(String fecha, HSSFWorkbook wb) {
		
		// obtener configuracion del reporte_automatico
		ReportesAutomaticosConfiguracion rac = null;
      	try {
	         rac = ReportesServiceUtil.getConfiguracion();Integer delay=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("EMAIL_DELAY"));
		
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		List<String> emails;
		String destinos;
		
		emails = new ArrayList<String>();
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_RECLAMO_PENDIENTE_SURGE");
		Integer delay=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("EMAIL_DELAY"));
		
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails = new ArrayList<String>(); //DS Agregado 28-10-2022 por problema en envia de email. A efectos de generar una prueba
			emails.add(to);

			MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
					emails, "Reclamos prestacionales en estado pendiente y recuperables SURGE " + fecha,
					"Reclamos prestacionales en estado pendiente y recuperables SURGE" + fecha, wb, "Reclamos prestacionales en estado pendiente y recuperables SURGE " + fecha + ".xls");
			try {
				Thread.sleep(delay *1000);
			} catch (InterruptedException e) {}
			
		}
		
	}
	
}

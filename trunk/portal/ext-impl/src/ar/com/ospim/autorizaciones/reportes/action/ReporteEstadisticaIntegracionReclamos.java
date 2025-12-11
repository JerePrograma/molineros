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
import ar.com.ospim.crm.beans.ReporteEstadisticaIntegracionReclamosExcel;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

public class ReporteEstadisticaIntegracionReclamos extends AgendadoJava {


	
	private static Log _log = LogFactoryUtil
			.getLog(ReporteEstadisticaIntegracionReclamos.class);
	private HSSFWorkbook wb;
	
	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		_log.debug("ReporteEstadísticaIntegracionReclamos");
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		
		String fecha = null;
		
		fecha = DateUtils.getDateString(calendar.getTime(), "dd-MM-yyyy");
		
		
		wb = new HSSFWorkbook();	
	
		wb= ReporteEstadisticaIntegracionReclamosExcel.generaReporte();

	
		enviarReporte(fecha, wb);
		
	
		
			
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
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_ESTADISTICA_INTEGRACION_RECLAMOS");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.clear();
			emails.add(to);
			MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
					emails, "Estadística integración reclamos " + fecha,
					"Estadística integración reclamos " + fecha, wb, "Estadística integración reclamos " + fecha + ".xls");
		}
		
		
		
	}
	
	
	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}

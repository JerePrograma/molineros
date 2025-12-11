package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.reportes.action.ReportePreautorizacionEstadosExcel;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteEstadisticasPreautorizaciones extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = 1565553655773919229L;
	private static Log logger = LogFactoryUtil.getLog(ReporteEstadisticasPreautorizaciones.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		HSSFWorkbook wb = new HSSFWorkbook();		
		try{
			rac = ReportesServiceUtil.getConfiguracion();
			
			Date fechaDde=null;
			Date fechaHta=null;
			Calendar calendar = Calendar.getInstance();
			Integer dia = calendar.get(Calendar.DAY_OF_MONTH);
			ArrayList<String> emails = new ArrayList<String>();
			
			if(dia==1 || dia==8 || dia== 16 || dia==24 
//					|| dia==5
			 ) {
			   fechaHta=DateUtils.quitaDias(calendar.getTime(), 1);
//DS - Borrar			   
//			   Calendar cumpleCal = Calendar.getInstance();
//			   cumpleCal.set(2017,10,30); //mes -1 
//			   fechaHta=cumpleCal.getTime();
//DS - Borrar			   
			   fechaDde=DateUtils.getFirstDateOfMonth(fechaHta, true);
			   
			   List<Estado> list = new ArrayList<Estado>();
			   List<PreAutorizacion> listP = new ArrayList<PreAutorizacion>();
			   try {
					 list = PreAutorizacionServiceUtil.getEstadisticoEstados(fechaDde,fechaHta);
					 wb= ReportePreautorizacionEstadosExcel.generaReporte(wb,list,fechaDde,fechaHta);
			   } catch (SystemException e) {
				   logger.debug("Error al generar Estadistico de Estados Preautorizaciones(Agendado)");
			   }
			   
			   try {
					 listP = PreAutorizacionServiceUtil.getEstadisticoPorDia(fechaDde,fechaHta);
					 wb=ReportePreautorizacionEstadosExcel.generaReportePorDia(wb,listP,fechaDde,fechaHta);
				} catch (SystemException e) {
					logger.debug("Error al generar Estadistico de Dias Preautorizaciones(Agendado)");
				}
				
				try {
					 listP = PreAutorizacionServiceUtil.getEstadisticoPorSeccional(fechaDde,fechaHta);
					 wb=ReportePreautorizacionEstadosExcel.generaReportePorSeccional(wb,listP,fechaDde,fechaHta);
				} catch (SystemException e) {
					logger.debug("Error al generar Estadistico por Seccional Preautorizaciones(Agendado)");
				}
				
				
				try {
					 Calendar c1 = Calendar.getInstance();
			         c1.setTime(fechaHta);
			         c1.add(Calendar.MONTH, -13);
					 list = PreAutorizacionServiceUtil.getEstadisticoPorMes(c1.getTime(),fechaHta);
					 wb=ReportePreautorizacionEstadosExcel.generaReportePorMes(wb,list,fechaDde,fechaHta);
				} catch (SystemException e) {
					logger.debug("Error al generar Estadistico Mensual Preautorizaciones(Agendado)");
				}
				String to="";
				to=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_ESTADISTICA_EMAIL");
				String[] vTo = to.split(";");
				
				for(int i=0;i<vTo.length;i++){
					emails.clear();
					emails.add(vTo[i]);
//					   emails.add("dsulfaro@uoma.org.ar");
					MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), emails, "Informes Estadisticas Preautorizaciones ",
							  "Informe de Estadisticas de Preautorizaciones", wb, "EstadisticasPreautorizacion.xls");
				}
	
			

			
			   
			
			}
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Estadisticas de Preautorizaciones");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		
		
		
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

    
}

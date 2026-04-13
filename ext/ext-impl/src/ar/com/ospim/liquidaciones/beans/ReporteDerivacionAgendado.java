package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.reportes.action.ReporteEstadisticaGastoMedicoExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteReclamosPrestacionales;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.tesoreria.reportes.action.ReporteDerivacionTercerizadorasExcel;
import ar.com.ospim.util.DateUtils;

public class ReporteDerivacionAgendado extends AgendadoJava implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6588104686569260525L;
	private static Log logger = LogFactoryUtil.getLog(ReporteDerivacionAgendado.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		HSSFWorkbook wb = new HSSFWorkbook();		
		try{
			rac = ReportesServiceUtil.getConfiguracion();
			
			Date fechaDde=null;
			Date fechaHta=null;
			Calendar calendar = Calendar.getInstance();
			List<String> emails = new ArrayList<String>();
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM");
			
			Date fecha = DateUtils.getPreceedingMonth(calendar.getTime());
			fechaDde=DateUtils.getFirstDateOfMonth(fecha, true);
			fechaHta=DateUtils.getLastDateOfMonth(fecha, true);
			   
			wb=ReporteDerivacionTercerizadorasExcel.generaReporte(null,null);
						  
		    emails=ra.getEmailsAsList();	
			
//			   emails.add("dsulfaro@uoma.org.ar");
			   
			   String nbeArchivo="Reporte_Derivacion _"+ sdf.format(fechaDde)+".xls";
			
			   MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(), emails, "Reporte Derivación Tercerizadora ",
						  "Reporte Derivación Tercerizadora", wb, nbeArchivo);
			
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Reporte de Derivación");
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

package ar.com.ospim.crm.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.reportes.action.ReporteEstadisticaEmpleadores;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.ospim.util.DateUtils;

public class EstadisticaEmpleadores extends AgendadoJava {
	private static Log logger = LogFactoryUtil.getLog(EstadisticaEmpleadores.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		logger.debug("Inicio  EstadisticaEmpleadores ");
		
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		calendar.add(Calendar.MONTH, -1);
				
		String periodo = null;
		
		periodo = DateUtils.getDateString(calendar.getTime(), "YYYY-MM");
		
		Date fechaDesde = DateUtils.getFirstDateOfMonth(calendar.getTime(), true);
		Date fechaHasta = DateUtils.getLastDateOfMonth(calendar.getTime(), true);
		
		List<FichaBoletaPortal> fichasBoletas = PortalEmpleadoresServiceUtil.getBoletaCapitalInteresPortal(fechaDesde, fechaHasta);
		List<FichaBoletaPortal> fichasSinDDJJ = PortalEmpleadoresServiceUtil.getBoletaCapitalSinDDJJ(fechaDesde, fechaHasta);
		FichaBoletaPortal cantDDJJFinale = PortalEmpleadoresServiceUtil.getReporteCantDDJJFinales(fechaDesde, fechaHasta);
		FichaBoletaPortal cantDDJJ = PortalEmpleadoresServiceUtil.getReporteCantDDJJ(fechaDesde, fechaHasta);
		FichaBoletaPortal empresasActiva = PortalEmpleadoresServiceUtil.getReporteEmpresasActiva(fechaDesde, fechaHasta);
		int cantDDJJFinaleVal = cantDDJJFinale.getReporteCantDDJJFinales(); 
		int cantDDJJVal = cantDDJJ.getReporteCantDDJJ(); 
		int empresasActivaVal = empresasActiva.getEmpresasActivas();
		
		HSSFWorkbook wb = new HSSFWorkbook();	
		try {
			 wb= ReporteEstadisticaEmpleadores.generaReporte(fichasBoletas,fichasSinDDJJ,cantDDJJFinaleVal,cantDDJJVal,empresasActivaVal,periodo);
	    } catch (Exception e) {
		   logger.debug("Error al generar EstadisticaEmpleadores");
	    }
		
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
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_BOLETA_ESTADISTICA_EMPLEADORES");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.clear();
			emails.add(to);
			MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
					emails, "DETALLE DE BOLETAS " + periodo,
					"DETALLE DE BOLETAS PERIODO " + periodo, wb, "Estadística Empleadores " + periodo + ".xls");
		}
		
		
		
	
		
		ra.setUltimaEjecucion(new Date());
		
		try {
			ReportesServiceUtil.reporteEjecutado(ra);
		} catch (SystemException e) {
			logger.debug(e);
		}
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}

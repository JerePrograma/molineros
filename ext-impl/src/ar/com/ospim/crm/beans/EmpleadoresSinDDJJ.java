package ar.com.ospim.crm.beans;

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
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.reportes.action.ReporteEstadisticaEmpleadores;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.ospim.util.DateUtils;

public class EmpleadoresSinDDJJ extends AgendadoJava {
	private static Log logger = LogFactoryUtil.getLog(EmpleadoresSinDDJJ.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		logger.debug("Inicio Empleadores Sin DDJJ");
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		List<FichaBoletaPortal> fichasBoletas = PortalEmpleadoresServiceUtil.getEmpresasSinDDJJ();
		
		HSSFWorkbook wb = new HSSFWorkbook();	
		try {
			 wb= ReporteEstadisticaEmpleadores.generaReporteEmpresasSinDDJJ(fichasBoletas);
	    } catch (Exception e) {
		   logger.debug("Error al generar  Empleadores Sin DDJJ");
		   logger.debug(e);
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
		destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_EMPLEADORES_SIN_DDJJ");
		String[] auxDestinos = destinos.split(";");
		for (String to : auxDestinos) {
			emails.clear();
			emails.add(to);
			MailUtils.enviarMailGmailconXls(rac.getMailFrom(), rac.getPass(),
					emails, "EMPRESAS SIN DDJJ AL " + sdf.format(hoy) ,
					"EMPRESAS SIN DDJJ AL " + sdf.format(hoy), wb, "EMPRESAS_SIN_DDJJ" + ".xls");
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

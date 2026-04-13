package ar.com.ospim.automatico.service;

import java.util.ArrayList;
import java.util.List;

import ar.com.global.beans.Destinatario;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.beans.ResultadoReporteAutomatico;
import ar.com.ospim.crm.beans.ContactoCRM;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReportesServiceUtil {
	
	private static Log logger = LogFactoryUtil.getLog(ReportesServiceUtil.class);

	public static List<ReporteAutomatico> getReportesACorrer()
			throws SystemException {
		return ReportesServiceImpl.getInstance().getReportesACorrer();
	}

	public static List<ReporteAutomatico> getReportesACorrerUrgentes()
			throws SystemException {
		return ReportesServiceImpl.getInstance().getReportesACorrerUrgentes();
	}
	
	public static ResultadoReporteAutomatico correrReporte(ReporteAutomatico ra)
			throws SystemException {
//		logger.debug("correrReporte " + ra.getStoredProcedure());
		return ReportesServiceImpl.getInstance().correrReporte(ra);
	}
	
	public static List<Destinatario> correrReporteDifusion(ReporteAutomatico ra)
			throws SystemException {
//		logger.debug("correrReporte " + ra.getStoredProcedure());
		return ReportesServiceImpl.getInstance().correrReporteDifusion(ra);
	}

	public static void reporteEjecutado(ReporteAutomatico ra)
			throws SystemException {
//		logger.debug("reporteEjecutado " + ra.getStoredProcedure());
		ReportesServiceImpl.getInstance().reporteEjecutado(ra);
	}

	public static void save(ReporteAutomatico ra) throws SystemException {
//		logger.debug("reporteEjecutado " + ra.getStoredProcedure());
		ReportesServiceImpl.getInstance().save(ra);
	}

	public static void update(ReporteAutomatico ra) throws SystemException {
//		logger.debug("reporteEjecutado " + ra.getStoredProcedure());
		ReportesServiceImpl.getInstance().update(ra);
	}

	public static void borrar(ReporteAutomatico ra) throws SystemException {
//		logger.debug("reporteEjecutado " + ra.getStoredProcedure());
		ReportesServiceImpl.getInstance().borrar(ra);

	}

	public static ReporteAutomatico get(int id) throws SystemException {
		return ReportesServiceImpl.getInstance().get(id);
	}

	public static ReportesAutomaticosConfiguracion getConfiguracion()
			throws SystemException {
		return ReportesServiceImpl.getInstance().getConfiguracion();
	}

	public static void update(ReportesAutomaticosConfiguracion configuracion)
			throws SystemException {
		ReportesServiceImpl.getInstance().update(configuracion);
	}
	
	public static List<ReporteAutomatico> getJavaAgendadosACorrer()
			throws SystemException {
//		logger.debug("Buscando agenda java");
		return ReportesServiceImpl.getInstance().getJavaAgendadosACorrer();
//		List<ReporteAutomatico> dummyList = new ArrayList<ReporteAutomatico>();
//		ReporteAutomatico dummyReport = new ReporteAutomatico();
//		dummyReport.setBase(1);
//		dummyReport.setCsvParameteres("");
//		dummyReport.setDiaDeLaSemana(0);
//		dummyReport.setDiaDelMes(0);
//		dummyReport.setDiario(true);
//		dummyReport.setDifusion(0);
//		dummyReport.setEmails("svalentini@ospim.org.ar");
//		dummyReport.setFechaUnicaVez(null);
//		dummyReport.setHora(16);
//		dummyReport.setId(17);
//		dummyReport.setIncluirFinDeSemana(false);
//		dummyReport.setJava("ar.com.ospim.webservice.OmintWSClient");
//		dummyReport.setStoredProcedure("");
//		dummyReport.setTitulo("Mi Dummy de Java");
////		dummyReport.setUltimaEjecucion(Calendar.getInstance().getTime());		
//		dummyList.add(dummyReport);
//		return dummyList;
	}
	
	public List<ReporteDeudaEmpresaCab> getReportesDeudaEmpPeriodo() throws SystemException {
		
		List<ReporteDeudaEmpresaCab> reportes = new ArrayList<ReporteDeudaEmpresaCab>();
		
		reportes = ReportesServiceImpl.getInstance().getReportesDeudaEmpPeriodo();
		
		return reportes;
	}
	
	public static List<ReporteAutomatico> getAlertasVencimientoContactosCRM() throws SystemException {
		
		return ReportesServiceImpl.getInstance().getAlertasVencimientoContactosCRM();
		
	}

	public static List<ContactoCRM> getContactosCRMparaAlertas(ReporteAutomatico ra)
			throws SystemException {
		
		return ReportesServiceImpl.getInstance().getContactosCRMaVencer(ra);
				
	}
	
	public static List<ContactoCRM> getContactosCRMparaAlertasUrgente(ReporteAutomatico ra)
			throws SystemException {
		
		return ReportesServiceImpl.getInstance().getContactosCRMaVencerUrgente(ra);
				
	}
	
	public static List<ContactoCRM> getResumenContactosCRMsinCerrar(ReporteAutomatico ra)
			throws SystemException {
		
		return ReportesServiceImpl.getInstance().getResumenContactosCRMsinCerrar(ra);
				
	}
	
	public static List<ContactoCRM> getResumenContactosCRMCerrados(ReporteAutomatico ra)
			throws SystemException {
		
		return ReportesServiceImpl.getInstance().getResumenContactosCRMCerrados(ra);
				
	}
}

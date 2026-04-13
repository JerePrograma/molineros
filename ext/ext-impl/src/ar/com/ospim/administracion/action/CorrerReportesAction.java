package ar.com.ospim.administracion.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.administracion.WebKeysAdministracion;
import ar.com.ospim.automatico.AgendadoJavaScheduler.AgendadoJob;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.ReportesScheduler.ReportesJob;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.webservice.PrevencionWSClient;

import com.liferay.portal.struts.PortletAction;

public class CorrerReportesAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {

//		ReportesJob repo = new ReportesJob();
//		repo.ejecutarReportes();
//
//		AgendadoJob agendaJava = new AgendadoJob();
//		agendaJava.ejecutarReportes();
//	
//		
//		List<ReporteAutomatico> reportesACorrer = ReportesServiceUtil
//				.getReportesACorrer();
//		req.setAttribute(WebKeysAdministracion.REPORTES_AUTOMATICOS,
//				reportesACorrer);
//
//		ReportesAutomaticosConfiguracion configuracion = ReportesServiceUtil
//				.getConfiguracion();
//		req.setAttribute(
//				WebKeysAdministracion.REPORTES_AUTOMATICOS_CONFIGURACION,
//				configuracion);
		
		PrevencionWSClient wsClient = new PrevencionWSClient();
		
		ReporteAutomatico ra = new ReporteAutomatico();
		ra.setId(331);
		ra.setEmails("svalentini@ospim.org.ar");
		
		wsClient.correrAgendado(ra);
		
		return mapping.findForward(getForward(req,
				"portlet.administracion.view"));
	}
}

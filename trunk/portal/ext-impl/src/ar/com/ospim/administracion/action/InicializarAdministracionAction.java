package ar.com.ospim.administracion.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.administracion.WebKeysAdministracion;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;

import com.liferay.portal.struts.PortletAction;

public class InicializarAdministracionAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		List<ReporteAutomatico> reportesACorrer = ReportesServiceUtil
				.getReportesACorrer();
		ReportesAutomaticosConfiguracion configuracion = ReportesServiceUtil
				.getConfiguracion();

		renderRequest.setAttribute(WebKeysAdministracion.REPORTES_AUTOMATICOS,
				reportesACorrer);
		renderRequest.setAttribute(
				WebKeysAdministracion.REPORTES_AUTOMATICOS_CONFIGURACION,
				configuracion);
		return mapping.findForward(getForward(renderRequest,
				"portlet.administracion.view"));

	}
}

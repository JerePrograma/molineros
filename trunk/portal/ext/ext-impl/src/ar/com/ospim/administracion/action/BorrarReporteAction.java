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

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BorrarReporteAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {

		int id = ParamUtil.getInteger(req, "id");
		ReporteAutomatico ra = new ReporteAutomatico();
		ra.setId(id);
		ReportesServiceUtil.borrar(ra);

		List<ReporteAutomatico> reportesACorrer = ReportesServiceUtil
				.getReportesACorrer();
		req.setAttribute(WebKeysAdministracion.REPORTES_AUTOMATICOS,
				reportesACorrer);

		ReportesAutomaticosConfiguracion configuracion = ReportesServiceUtil
				.getConfiguracion();
		req.setAttribute(
				WebKeysAdministracion.REPORTES_AUTOMATICOS_CONFIGURACION,
				configuracion);
		
		return mapping.findForward(getForward(req,
				"portlet.administracion.view"));
	}

}

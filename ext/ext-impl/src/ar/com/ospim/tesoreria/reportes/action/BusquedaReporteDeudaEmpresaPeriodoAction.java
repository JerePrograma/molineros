package ar.com.ospim.tesoreria.reportes.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.automatico.service.ReportesServiceImpl;

import com.liferay.portal.struts.PortletAction;

public class BusquedaReporteDeudaEmpresaPeriodoAction  extends PortletAction{

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		List<ReporteDeudaEmpresaCab> lista = ReportesServiceImpl.getInstance().getReportesDeudaEmpPeriodo();
		
		renderRequest.setAttribute("reportesDeuEmpPeriodo", lista);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.deuda.empresas.periodo"));
	}



}

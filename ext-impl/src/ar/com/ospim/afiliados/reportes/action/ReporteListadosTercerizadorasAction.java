package ar.com.ospim.afiliados.reportes.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.reportes.ReportesAfiliadoServiceImpl;
import ar.com.ospim.afiliados.reportes.beans.PadronInformado;

import com.liferay.portal.struts.PortletAction;

public class ReporteListadosTercerizadorasAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		ReportesAfiliadoServiceImpl repo= new ReportesAfiliadoServiceImpl();
		List<PadronInformado> list=repo.getUltimoPadronInformado();
		renderRequest.setAttribute("ultimoPadron", list);
		return mapping.findForward("portlet.afiliados.reportes.listados_tercerizadoras");									 
	}
}
package ar.com.ospim.liquidaciones.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroList;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class GenerarReporteReintegros extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(GenerarReporteReintegros.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		try {
			User user = PortalUtil.getUser(actionRequest);
			ReintegroList reintegrosList = getReintegroListFromRequest(actionRequest);
			int id = OrdenPagoServiceUtil.saveReintegroListParaReporte(
					reintegrosList, user);
			actionRequest.setAttribute("reporteId", id);
		} catch (Exception e) {
			_log.error("Error al crear reporte de reintegros", e);
			throw e;
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping
				.findForward("portlet.liquidaciones.view");
	}

	@SuppressWarnings("unchecked")
	private ReintegroList getReintegroListFromRequest(
			ActionRequest actionRequest) {
		ReintegroList rList = new ReintegroList();
		List<Reintegro> reintegrosList = new ArrayList<Reintegro>();
		PortletSession portletSession = actionRequest.getPortletSession();
		reintegrosList= (ArrayList<Reintegro>) portletSession.getAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
		for (Reintegro reintegro : reintegrosList) {
			reintegro.setImporteTotal();
		}
		rList.setReintegros(reintegrosList);
		int idSeccional = 0;
		idSeccional = Integer.parseInt(actionRequest.getParameter("seccional_op"));
		rList.setSeccional(new Seccional(idSeccional, ""));
		
		return rList;
	}
}
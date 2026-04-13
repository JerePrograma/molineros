package ar.com.ospim.farmacia.action;

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
import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.beans.ReintegroFarmaciaList;

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
			ReintegroFarmaciaList reintegrosList = getReintegroListFromRequest(actionRequest);
			int id = OrdenPagoServiceUtil.saveReintegroFarmaciaListParaReporte(
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
	private ReintegroFarmaciaList getReintegroListFromRequest(
			ActionRequest actionRequest) {
		ReintegroFarmaciaList rList = new ReintegroFarmaciaList();
		List<ReintegroMedicamento> reintegrosList = new ArrayList<ReintegroMedicamento>();
		PortletSession portletSession = actionRequest.getPortletSession();														  
		reintegrosList= (ArrayList<ReintegroMedicamento>) portletSession.getAttribute(WebKeysFarmacia.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
		for (ReintegroMedicamento reintegro : reintegrosList) {
			reintegro.getImporteTotal();
		}
		rList.setReintegros(reintegrosList);
		int idSeccional = 0;
		idSeccional = Integer.parseInt(actionRequest.getParameter("seccional_op"));
		rList.setSeccional(new Seccional(idSeccional, ""));
		
		return rList;
	}
}
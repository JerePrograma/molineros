package ar.com.ospim.tesoreria.actas.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Inspector;
import ar.com.ospim.tesoreria.service.InspectorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarInspectorAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(AgregarInspectorAction.class);

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");
		List<Inspector> inspectoresFullList = InspectorServiceUtil
				.getInspectores(renderRequest);

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		List<InspectorWrapper> inspectores = (ArrayList<InspectorWrapper>) session
				.getAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);

		if (inspectores == null) {
			inspectores = new ArrayList<InspectorWrapper>();
		}
		if (renderRequest.getParameter("esEdicion")!=null){
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		String idString = renderRequest.getParameter("inspector");
		if (idString != null) {
			Inspector inspector = new Inspector(Integer.parseInt(idString), "");
			if (!inspectores.contains(new InspectorWrapper(inspector))) {
				InspectorWrapper inspectorWrapper = new InspectorWrapper(
						inspectoresFullList.get(inspectoresFullList
								.indexOf(inspector)));
				inspectorWrapper.setRecienAgregado(true);
				inspectorWrapper.setBorradoLogico(false);
				inspectores.add(inspectorWrapper);
			}
		}

		session.setAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS,
				inspectores);
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.tesoreria.actas.inspectores.result.search");
	}

	public static List<InspectorWrapper> getInspectorWrapperList(
			List<Inspector> inspectores) {
		List<InspectorWrapper> inspW = new ArrayList<InspectorWrapper>();
		if (inspectores != null) {
			for (Inspector ins : inspectores) {
				InspectorWrapper inspectorWrapper = new InspectorWrapper(ins);
				inspectorWrapper.setRecienAgregado(false);
				inspW.add(inspectorWrapper);
			}
		}

		return inspW;
	}
}

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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarInspectorAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(SacarInspectorAction.class);

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		
		if (renderRequest.getParameter("esEdicion")!=null){
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}
		
		List<InspectorWrapper> inspectores = (ArrayList<InspectorWrapper>) session
				.getAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);

		if (inspectores == null) {
			inspectores = new ArrayList<InspectorWrapper>();
		}

		String idString = renderRequest.getParameter("inspector");
		if (idString != null) {
			InspectorWrapper inspectorWrapper = new InspectorWrapper(
					new Inspector(Integer.parseInt(idString), ""));
			InspectorWrapper inspABorrar = inspectores.get(inspectores
					.indexOf(inspectorWrapper));
			if (inspABorrar.isRecienAgregado()) {
				inspectores.remove(inspABorrar);
			} else {
				inspABorrar.setBorradoLogico(true);
			}
		}

		session.setAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS,
				inspectores);
		_log.debug("Saliendo de reder");
		return mapping
				.findForward("portlet.tesoreria.actas.inspectores.result.search");
	}

}

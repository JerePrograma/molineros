package ar.com.ospim.liquidaciones.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="CambiarEstadoCuotaAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * cambia estado reintegro cuota ortopedia ortodoncia
 * 
 * @author Carlos Rivas
 * 
 */
public class CambiarEstadoCuotaAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(CambiarEstadoCuotaAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.liquidaciones.catastro.result");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		User user = PortalUtil.getUser(renderRequest);

		int id_reintegro = ParamUtil.getInteger(renderRequest, "id_reitnegro");
		int cuota = ParamUtil.getInteger(renderRequest, "cuota");
		int estado = ParamUtil.getInteger(renderRequest, "estado");

		try {
			ReintegroServiceUtil.cambiarEstadoCuota(id_reintegro, cuota, estado, user.getScreenName());

		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "");
		}		
		return mapping.findForward("portlet.liquidaciones.cuota.result");
	}

}
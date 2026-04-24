package ar.com.ospim.prestadores.action;

import java.sql.SQLException;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.ImposibleBorrarPrestadorException;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class BorrarPrestadorAction extends PrestadoresBaseAction {
	private static Log logger = LogFactoryUtil
			.getLog(BorrarPrestadorAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String id = null;

		if (null != renderRequest.getParameter("prestador_id")) {
			id = renderRequest.getParameter("prestador_id").trim().length() > 0 ? renderRequest
					.getParameter("prestador_id")
					: null;
		}

		User user = PortalUtil.getUser(PortalUtil
				.getHttpServletRequest(renderRequest));
		try {
			PrestadorServiceUtil.borrar(Integer.valueOf(id), user);
		} catch (ImposibleBorrarPrestadorException e){
			logger.debug("No se pudo borrar prestador", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.afiliados.error");
		} catch (SQLException e) {
			logger.error("No se pudo borrar empleador", e);
			return mapping.findForward("portlet.afiliados.error");
		}
		
		return mapping
				.findForward("portlet.liquidaciones.view");
	}
}

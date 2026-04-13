package ar.com.ospim.tesoreria.convenios.action;

import java.sql.SQLException;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.estudioisidro.action.BuscarSeguimientoEmpresaAction;
import ar.com.ospim.tesoreria.ImposibleBorrarConvenioException;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BorrarConvenioAction extends PortletAction {
	private static Log logger = LogFactoryUtil.getLog(BorrarConvenioAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int id = 0;

		id = ParamUtil.getInteger(renderRequest, "id");
		User user = PortalUtil.getUser(PortalUtil
				.getHttpServletRequest(renderRequest));
		renderRequest.setAttribute("tabs1", "convenios");
		try {
			ConvenioServiceUtil.borrar(id, user);
		} catch (ImposibleBorrarConvenioException e) {
			logger.debug("No se pudo borrar convnenio", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.tesoreria.error");
		} catch (SQLException e) {
			logger.error("No se pudo borrar convenio", e);
			return mapping.findForward("portlet.tesoreria.error");
		}
		
		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}
		if (renderResponse != null
				&& renderResponse.getNamespace() != null
				&& renderResponse.getNamespace().equals("_EST_1_")) {
			renderRequest.setAttribute("popupConvenio", "true");
			BuscarSeguimientoEmpresaAction buscar = new BuscarSeguimientoEmpresaAction();
			buscar.buscarConvenios(renderRequest);
			return mapping
					.findForward("portlet.estudio_isidro.seguimiento_empresa_result");
		}else{
			return mapping.findForward("portlet.tesoreria.view");
		}
	}
}

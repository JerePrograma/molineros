package ar.com.uoma.conveniosNoOS.action;

import java.sql.SQLException;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.ImposibleBorrarConvenioException;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BorrarConvenioNoOSAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(BorrarConvenioNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int id = 0;

		id = ParamUtil.getInteger(renderRequest, "id");
		User user = PortalUtil.getUser(PortalUtil
				.getHttpServletRequest(renderRequest));
		renderRequest.setAttribute("tabs1", "convenios");
		String path = mapping.getPath();
		renderRequest.setAttribute("convenio_id", id);
		boolean reactivar = ParamUtil.getBoolean(renderRequest, "reactivar");

		if (path.contains("borrar_convenios_fecha")) {
			return mapping.findForward("portlet.uoma.borrar.convenio");
		}

		try {
			if (reactivar) {
				ConvenioNoOSServiceUtil.reactivar(id, user);
			} else {
				Calendar fechaBaja = Calendar.getInstance();
				fechaBaja.set(Calendar.DATE,
						ParamUtil.getInteger(renderRequest, "fechaBajaDia"));
				fechaBaja.set(Calendar.MONTH,
						ParamUtil.getInteger(renderRequest, "fechaBajaMes"));
				fechaBaja.set(Calendar.YEAR,
						ParamUtil.getInteger(renderRequest, "fechaBajaAnio"));
				ConvenioNoOSServiceUtil.borrar(id, fechaBaja.getTime(), user);
			}
		} catch (ImposibleBorrarConvenioException e) {
			logger.debug("No se pudo borrar convnenio", e);
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.estudio_isidro.error");
		} catch (SQLException e) {
			logger.error("No se pudo borrar convenio", e);
			return mapping.findForward("portlet.estudio_isidro.error");
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}
		return mapping.findForward("portlet.uoma.borrar.convenio");
	}
}

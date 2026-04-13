package ar.com.ospim.tesoreria.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.beans.FechaCierre;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EliminarFechasCierreContableAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		try {
			String fecha = renderRequest.getParameter("fecha");
			FechaCierre fechaCierre = new FechaCierre();
			fechaCierre.setFechaString(fecha);
			User user = PortalUtil.getUser(renderRequest);
			ContabilidadServiceUtil.eliminarFechaCierreContableGestion(
					fechaCierre, user, entidad);
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}
		List<FechaCierre> cierreGestion = ContabilidadServiceUtil
				.getFechasCierreContable(entidad);
		List<FechaCierre> cierreAsientos = ContabilidadServiceUtil
				.getFechasCierreAsientos(entidad);

		renderRequest
				.setAttribute("fechasCierreContableGestion", cierreGestion);
		renderRequest.setAttribute("fechasCierreContableAsientos",
				cierreAsientos);

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.fechas_cierre_contable"));
	}
}

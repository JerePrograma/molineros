package ar.com.ospim.tesoreria.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
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

public class GuardarFechasCierreContableAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		try {
			String fecha = actionRequest.getParameter("fecha");
			String descripcion = actionRequest.getParameter("descripcion");
			FechaCierre fechacierre = new FechaCierre();
			fechacierre.setFechaString(fecha);
			fechacierre.setObservacion(descripcion);
			User user = PortalUtil.getUser(actionRequest);
			ContabilidadServiceUtil.guardarFechaCierreContableGestion(
					fechacierre, user, entidad);
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
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

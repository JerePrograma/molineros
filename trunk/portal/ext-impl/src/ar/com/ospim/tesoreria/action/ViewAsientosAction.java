package ar.com.ospim.tesoreria.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;

import com.liferay.portal.struts.PortletAction;

public class ViewAsientosAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		Asiento asiento = null;
		String asientoId = renderRequest.getParameter("asiento_id");
		asiento = AsientoServiceUtil.buscarAsiento(new Asiento(Integer
				.parseInt(asientoId)),entidad);
		renderRequest.setAttribute("asiento", asiento);

		renderRequest.setAttribute("planCuentas",
				TraeListasServiceUtil.getPlanCuentas(asiento.getFecha(), entidad));
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.view_asientos"));
	}
}

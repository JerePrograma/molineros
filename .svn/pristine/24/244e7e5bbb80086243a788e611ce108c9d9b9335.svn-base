package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PlanCuentasSSS;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;

public class PlanCuentasSSSAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		PortletSession portletSession = renderRequest.getPortletSession();
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		
		String numero = renderRequest.getParameter("numero");
		String descripcion = renderRequest.getParameter("descripcion");

		List<PlanCuentasSSS> planCuentas = TraeListasServiceUtil
				.getPlanCuentasSSS(entidad,null,numero,descripcion,null);
		
		renderRequest.setAttribute("planCuentasSSS", planCuentas);
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.plan_cuentas_sss"));
	}
}

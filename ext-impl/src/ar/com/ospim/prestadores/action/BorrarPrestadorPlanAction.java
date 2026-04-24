package ar.com.ospim.prestadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.PrestadorPlan;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class BorrarPrestadorPlanAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BorrarPrestadorPlanAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int idPrestPlan = ParamUtil.getInteger(renderRequest, "idPrestPlan");
		
		PrestadorPlan prestPlan = new PrestadorPlan();
		prestPlan.setId(idPrestPlan);
		
		_log.debug("Borrando PrestPlan id: " + idPrestPlan);
		
		List<PrestadorPlan> listaPrestadorPlanes = (ArrayList<PrestadorPlan>) session.getAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
		
		int pos = listaPrestadorPlanes.indexOf(prestPlan);
//		reemplazo por el objeto de la lista
		prestPlan = listaPrestadorPlanes.get(pos);
		if(prestPlan.getEstado()==null){ // esta prest-plan esta en BD
			prestPlan.setEstado(PrestadorPlan.ESTADOS.BAJA);
		}else{
			listaPrestadorPlanes.remove(pos);
		}	
		session.removeAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION, listaPrestadorPlanes);

		return mapping.findForward("portlet.liquidaciones.plan.prestador");
	}
		
}
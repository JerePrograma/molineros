package ar.com.ospim.liquidaciones.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.NoSuchLiquidacionEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="ViewLiquidacionEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class ViewLiquidacionEntryAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			LiquidacionActionUtil.getLiquidacionEntry(renderRequest);
			renderRequest.setAttribute(WebKeysLiquidaciones.VIEW_LIQUIDACION, WebKeysLiquidaciones.VIEW_LIQUIDACION);
		} catch (Exception e) {
			if (e instanceof NoSuchLiquidacionEntryException
					|| e instanceof PrincipalException) {
				SessionErrors.add(renderRequest, e.getClass().getName());
				return mapping.findForward("portlet.liquidaciones.error");
			} else {
				throw e;
			}
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.editar_liquidacion_entry"));
	}
}
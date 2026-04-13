package ar.com.ospim.farmacia.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="ViewReintegroEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class ViewReintegroEntryAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			ReintegroActionUtil.getReintegroEntry(renderRequest);
			renderRequest.setAttribute(WebKeysFarmacia.VIEW_REINTEGRO, WebKeysFarmacia.VIEW_REINTEGRO);
		} catch (Exception e) {
			if (e instanceof NoSuchReintegroEntryException
					|| e instanceof PrincipalException) {
				SessionErrors.add(renderRequest, e.getClass().getName());
				return mapping.findForward("portlet.farmacia.error");
			} else {
				throw e;
			}
		}
		return mapping.findForward(getForward(renderRequest,
				"portlet.farmacia.editar_reintegro_entry"));
	}
}
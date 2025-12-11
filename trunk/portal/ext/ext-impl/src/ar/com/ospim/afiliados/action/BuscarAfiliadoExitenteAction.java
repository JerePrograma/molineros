/**
 */

package ar.com.ospim.afiliados.action;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <a href="BuscarAfiliadoExitenteAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class BuscarAfiliadoExitenteAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		try {			
			
			ActionUtil.getAfiliadoEntryInclusoDadoBaja(renderRequest);
			
			Afiliado afiliado = (Afiliado) renderRequest
					.getAttribute(WebKeysAfiliados.AFILIADO_EXISTENTE);
			
			

			if (afiliado == null) {
				throw new NoSuchAfiliadoEntryException();
			}
		} catch (Exception e) {
			if (e instanceof NoSuchAfiliadoEntryException
					|| e instanceof PrincipalException) {

				SessionErrors.add(renderRequest, e.getClass().getName());

				return mapping.findForward("portlet.afiliados.error");
			} else {
				throw e;
			}
		}
		return mapping
				.findForward("portlet.afiliados.view_afiliado_entry");
	}

}
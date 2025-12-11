/**
 */

package ar.com.ospim.cai.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarAfiliadosAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de afiliados según parámetros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class CAIBuscarPersonalSeccionalAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(CAIBuscarPersonalSeccionalAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.cai.personal.seccional.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		try {
			String seccional = null;
			String seccional_nombre=null;
			int seccional_int = 0;
			String nombre = null;
			
			if (null != renderRequest.getParameter("seccional")) {
				seccional = renderRequest.getParameter("seccional").trim()
						.length() > 0 ? renderRequest.getParameter("seccional")
						: null;
			}
			if (null != renderRequest.getParameter("seccional_nombre")) {
				seccional_nombre = renderRequest.getParameter("seccional_nombre").trim()
						.length() > 0 ? renderRequest.getParameter("seccional_nombre")
						: null;
			}			
			
			if (null != seccional) {
				try {
					seccional_int = Integer.parseInt(seccional);
				} catch (NumberFormatException e) {
					seccional_int = 0;
				}
			}
			if (null != renderRequest.getParameter("nombre")) {
				nombre = renderRequest.getParameter("nombre").trim().length() > 0 ? renderRequest
						.getParameter("nombre") : null;
			}
			List<Contacto> busqueda=new ArrayList<Contacto>();
			
			busqueda=SeccionalServiceUtil.buscarContactosPersonalesSeccional(seccional_int,nombre);
			renderRequest.getPortletSession().removeAttribute("PERSONAL_SECCIONAL");
			renderRequest.getPortletSession().setAttribute("PERSONAL_SECCIONAL", busqueda);
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return mapping.findForward("portlet.cai.personal.seccional.result.search");
		
	}

}
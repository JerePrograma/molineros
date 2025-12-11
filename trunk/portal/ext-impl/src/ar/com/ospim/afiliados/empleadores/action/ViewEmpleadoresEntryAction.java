package ar.com.ospim.afiliados.empleadores.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.empleadores.NoSuchEmpresaEntryException;
import ar.com.ospim.afiliados.empleadores.WebKeysEmpleadores;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ViewAfiliadoEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class ViewEmpleadoresEntryAction extends EmpleadoresBaseAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		TraeListasServiceUtil.getSeccionales(renderRequest);
		TraeListasServiceUtil.getRamosEmpresa(renderRequest);
		TraeListasServiceUtil.getPosicionesIva(renderRequest);
		TraeListasServiceUtil.getEntidadesCamaraEmpresa(renderRequest);

		try {
			Empresa empresa = null;
			String cambioSolapa = renderRequest.getParameter("cambioSolapa");
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(
					renderRequest);
			if (cambioSolapa == null) {
				httpServletRequest.getSession()
						.removeAttribute(WebKeysEmpleadores.EMPRESA_EN_EDICION);
				empresa = getEmpleadorEntry(httpServletRequest);
			} else {
				empresa = (Empresa) httpServletRequest.getSession().getAttribute(
						WebKeysEmpleadores.EMPRESA_EN_EDICION);
			}
			
			if (empresa == null) {
				throw new NoSuchEmpresaEntryException();
			}
			
			httpServletRequest.getSession().setAttribute(WebKeysEmpleadores.EMPRESA_EN_EDICION, empresa);
			
		} catch (NoSuchEmpresaEntryException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.afiliados.error");
		} catch (PrincipalException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.afiliados.error");
		}
		return mapping
				.findForward("portlet.afiliados.empleadores.view_empleadores_entry");
	}

}
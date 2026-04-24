package ar.com.uoma.actasNoOS.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.NoSuchPrestadorEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.actas.action.AgregarInspectorAction;
import ar.com.ospim.tesoreria.actas.action.InspectorWrapper;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.service.InspectorServiceUtil;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ViewActasEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class ViewActasNoOSEntryAction extends ActasNoOSBaseAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		TraeListasServiceUtil.getBancos(renderRequest);
		InspectorServiceUtil.getInspectores(renderRequest);
		try {
			Acta acta = null;
			String cambioSolapa = renderRequest.getParameter("cambioSolapa");
			HttpServletRequest httpServletRequest = PortalUtil
					.getHttpServletRequest(renderRequest);
			if (cambioSolapa == null) {
				httpServletRequest.getSession().removeAttribute(
						WebKeysLiquidaciones.PRESTADOR_EN_EDICION);
				httpServletRequest.getSession().removeAttribute(
						WebKeysTesoreria.INSPECTORES_AGREGADOS);
				acta = getActaEntry(httpServletRequest);
				if (acta != null && acta.getInspectoresFirmantes() != null) {
					
					List<InspectorWrapper> inspectorWrapperList = AgregarInspectorAction
					.getInspectorWrapperList(acta.getInspectoresFirmantes());
			
					PortalUtil.getHttpServletRequest(renderRequest)
							.getSession().setAttribute(
									WebKeysTesoreria.INSPECTORES_AGREGADOS,
									inspectorWrapperList);
				}
			} else {
				acta = (Acta) httpServletRequest.getSession().getAttribute(
						WebKeysTesoreria.ACTA_EN_EDICION);
			}

			if (acta == null) {
				throw new NoSuchPrestadorEntryException();
			}

			httpServletRequest.getSession().setAttribute(
					WebKeysTesoreria.ACTA_EN_EDICION, acta);

		} catch (NoSuchPrestadorEntryException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.afiliados.error");
		} catch (PrincipalException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
			return mapping.findForward("portlet.afiliados.error");
		}
		return mapping.findForward("portlet.estudio_isidro.actas.view_actas_entry");
	}

}
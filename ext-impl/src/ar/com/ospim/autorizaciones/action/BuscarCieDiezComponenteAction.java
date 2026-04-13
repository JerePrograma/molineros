
package ar.com.ospim.autorizaciones.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.CieDiez;
import ar.com.ospim.autorizaciones.services.BusquedaCieDiezServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;


public class BuscarCieDiezComponenteAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(CieDiez.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.cie.diez.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		String popup = null;

				
		try {
			String codigoCie = null;
			String detalleCie = null;
			

			if (null != renderRequest.getParameter("codigoCie")) {
				codigoCie= renderRequest.getParameter("codigoCie").trim().length() > 0 ? renderRequest
						.getParameter("codigoCie")
						: null;
			}
			if (null != renderRequest.getParameter("detalleCie")) {
				detalleCie= renderRequest.getParameter("detalleCie").trim().length() > 0 ? renderRequest
						.getParameter("detalleCie")
						: null;
			}
			
			BusquedaCieDiezServiceUtil.getInstance();
			
			List<CieDiez> busqueda ;
			
					busqueda = BusquedaCieDiezServiceUtil.getBusquedaCieDiez(codigoCie, detalleCie);
					renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_CIEDIEZ);
			     	renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_CIEDIEZ,	busqueda);			
			
						
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		popup = ParamUtil.getString(renderRequest, "popup");			
		
		String origen= ParamUtil.getString(renderRequest, "origen");
		
		renderRequest.setAttribute("origen", origen);		

		//if (null != popup && !popup.trim().equals("")) {
			return mapping.findForward("portlet.ciediez.result.search.popup");
		//} else {
		//	return mapping.findForward("portlet.cie.diez.result.search");
		//}
	}

}
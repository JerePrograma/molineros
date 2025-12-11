package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarAutorizacionPmiAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Busqueda Autorizaciones Recetas PMI
 * 
 * @author Gustavo Fernandez
 * 
 */

public class BuscarAutorizacionPmiAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarAutorizacionPmiAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		try {
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

			String fechaRecetaDia = ParamUtil.getString(renderRequest,"fechaRecetaDia");
			String fechaRecetaMes = ParamUtil.getString(renderRequest,"fechaRecetaMes");
			String fechaRecetaAnio = ParamUtil.getString(renderRequest,"fechaRecetaAnio");
			Date fechaReceta = null;
			try {
				fechaReceta = formatoDePeriodo.parse(fechaRecetaDia + "/"
						+ (Integer.parseInt(fechaRecetaMes) + 1) + "/"
						+ fechaRecetaAnio);
			} catch (Exception e) {
				fechaReceta = null;
			}
			
			int inte = ParamUtil.getInteger(renderRequest, "inte");
			String cuil = ParamUtil.getString(renderRequest, "cuil");
			int numReceta = ParamUtil.getInteger(renderRequest, "receta");
						
				List<AutorizacionesPmi> autorizaciones = new ArrayList<AutorizacionesPmi>();
				autorizaciones = AutorizacionesServiceUtil.getListaAutorizacionesPmi(fechaReceta, cuil, 
								inte, numReceta);
				
				renderRequest.removeAttribute("AutorizacionesPmi");
				renderRequest.setAttribute("AutorizacionesPmi", autorizaciones);
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return mapping.findForward("portlet.autorizaciones.buscar_autorizacion_pmi");

	}

}
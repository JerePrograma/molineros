package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AporteAfiliado;
import ar.com.ospim.afiliados.services.AporteServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="FiltrarBoletasAction.java.html"><b><i>View Source</i></b></a>
 * <p> filtra boletas
 * @author Gustavo Fernandez
 *
 */

public class FiltrarBoletasAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(FiltrarBoletasAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		try {
					
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String[] periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
				"periodoDesdeMesAnio").split("_");
		Date periodoDesde = null;
		try {
			periodoDesde = formatoDePeriodos.parse(Integer
					.parseInt(periodoDesdeMesAnio[0])
					+ 1 + "/" + periodoDesdeMesAnio[1]);
		} catch (Exception e) {
			periodoDesde = null;
		}
		if (periodoDesde == null) {
			periodoDesde = formatoDePeriodos.parse(Integer
					.parseInt("01")+"/"+(calendar.get(Calendar.YEAR)-1)); 
		}
		
		boolean cuota_amtima = ParamUtil.getBoolean(renderRequest, "cuota_amtima");
		boolean cuota_usufructo = ParamUtil.getBoolean(renderRequest, "cuota_usufructo");
		boolean art_46 = ParamUtil.getBoolean(renderRequest, "art_46");
		boolean cuota_social_uoma = ParamUtil.getBoolean(renderRequest, "cuota_social_uoma");
		boolean aporte_solidario_uoma = ParamUtil.getBoolean(renderRequest, "aporte_solidario_uoma");
		boolean aporte_afip_ospim = ParamUtil.getBoolean(renderRequest, "aporte_afip_ospim");
		boolean boleta_blanca_ospim = ParamUtil.getBoolean(renderRequest, "boleta_blanca_ospim");
		boolean boleta_blanca_uoma = ParamUtil.getBoolean(renderRequest, "boleta_blanca_uoma");
		boolean boleta_blanca_amtima = ParamUtil.getBoolean(renderRequest, "boleta_blanca_amtima");
		
		HashMap<Integer, Boolean> tBoletas = new HashMap<Integer, Boolean>();	
		
		tBoletas.put(1,cuota_amtima);
		tBoletas.put(2,cuota_usufructo);
		tBoletas.put(3,art_46);
		tBoletas.put(4,cuota_social_uoma);
		tBoletas.put(5,aporte_solidario_uoma);
		tBoletas.put(6,aporte_afip_ospim);
		tBoletas.put(7,boleta_blanca_ospim);
		tBoletas.put(8,boleta_blanca_uoma);
		tBoletas.put(9,boleta_blanca_amtima);
		
			PortletSession portletSession=renderRequest.getPortletSession();
			
			//almaceno el hashMap de los tipos de boleta en sesion 
			portletSession.removeAttribute(WebKeysAfiliados.TBOLETA_SESSION,PortletSession.APPLICATION_SCOPE);
			portletSession.setAttribute(WebKeysAfiliados.TBOLETA_SESSION,tBoletas,PortletSession.APPLICATION_SCOPE);
		
			@SuppressWarnings("unchecked")
			List<AporteAfiliado> afiliadosList= (List<AporteAfiliado>)portletSession.getAttribute(WebKeysAfiliados.APORTES_AFIP,
					PortletSession.APPLICATION_SCOPE);
			
			portletSession.removeAttribute(WebKeysAfiliados.APORTES_AFIP,PortletSession.APPLICATION_SCOPE);
			portletSession.setAttribute(WebKeysAfiliados.APORTES_AFIP,AporteServiceUtil.filtrarLista(afiliadosList,cuota_amtima,cuota_usufructo,
					art_46,cuota_social_uoma,aporte_solidario_uoma, aporte_afip_ospim, boleta_blanca_ospim,boleta_blanca_uoma, boleta_blanca_amtima),
					PortletSession.APPLICATION_SCOPE);
		} 
		
		catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest,Exception.class.getName());
			//setForward(renderRequest, "portlet.afiliados.error");
		}		
		return mapping.findForward("portlet.ver.aportes.filtrados");
	}
	
	
}
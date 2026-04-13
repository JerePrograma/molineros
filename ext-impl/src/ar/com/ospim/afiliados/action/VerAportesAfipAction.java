package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.AporteAfiliado;
import ar.com.ospim.afiliados.services.AporteServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="VerAportesAfipAction.java.html"><b><i>View Source</i></b></a>
 * <p> muestraAportes
 * @author Federico Brachi
 *
 */
public class VerAportesAfipAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(VerAportesAfipAction.class);

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {		
		
		setForward(actionRequest,"portlet.ver.aportes");

	}
	
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		PortletSession portletSession = renderRequest.getPortletSession();
		
		String cuil_titular=ParamUtil.getString(renderRequest, "cuil");	
		
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
		
		HashMap<Integer, Boolean> boletas= (HashMap<Integer, Boolean>)portletSession.getAttribute(WebKeysAfiliados.TBOLETA_SESSION,
				PortletSession.APPLICATION_SCOPE);
		
		boolean cuota_amtima = false;
		boolean cuota_usufructo = false;
		boolean art_46 = false;
		boolean cuota_social_uoma = false;
		boolean aporte_solidario_uoma = false;
		boolean aporte_afip_ospim = false;
		boolean boleta_blanca_ospim = false;
		boolean boleta_blanca_uoma = false;
		boolean boleta_blanca_amtima = false;
		
		cuota_amtima = ParamUtil.getBoolean(renderRequest, "cuota_amtima");
		cuota_usufructo = ParamUtil.getBoolean(renderRequest, "cuota_usufructo");
		art_46 = ParamUtil.getBoolean(renderRequest, "art_46");
		cuota_social_uoma = ParamUtil.getBoolean(renderRequest, "cuota_social_uoma");
		aporte_solidario_uoma = ParamUtil.getBoolean(renderRequest, "aporte_solidario_uoma");
		aporte_afip_ospim = ParamUtil.getBoolean(renderRequest, "aporte_afip_ospim");
		boleta_blanca_ospim = ParamUtil.getBoolean(renderRequest, "boleta_blanca_ospim");
		boleta_blanca_uoma = ParamUtil.getBoolean(renderRequest, "boleta_blanca_uoma");
		boleta_blanca_amtima = ParamUtil.getBoolean(renderRequest, "boleta_blanca_amtima");
		
		if ( boletas!=null && boletas.size()>0 ) {
			cuota_amtima=boletas.get(1);
			cuota_usufructo=boletas.get(2);
			art_46=boletas.get(3);
			cuota_social_uoma=boletas.get(4);
			aporte_solidario_uoma=boletas.get(5);
			aporte_afip_ospim=boletas.get(6);
			} 
		
		try {
			
			List<AporteAfiliado> afiAportes=null;
			if(cuota_amtima==false && cuota_usufructo==false && art_46==false && cuota_social_uoma==false && aporte_solidario_uoma==false &&
					aporte_afip_ospim==false && boleta_blanca_ospim==false && boleta_blanca_uoma==false && boleta_blanca_amtima==false){
				aporte_afip_ospim=true;
			}
			afiAportes= AporteServiceUtil.buscaAportesAfipYEmpleadoresAfiliado(cuil_titular, periodoDesde, cuota_amtima, cuota_usufructo,
					art_46, cuota_social_uoma, aporte_solidario_uoma, aporte_afip_ospim, boleta_blanca_ospim, boleta_blanca_uoma,
					boleta_blanca_amtima);		
			
			afiAportes = AporteServiceUtil.filtrarLista(afiAportes,
					cuota_amtima, cuota_usufructo, art_46, cuota_social_uoma,
					aporte_solidario_uoma, aporte_afip_ospim,
					boleta_blanca_ospim, boleta_blanca_uoma,
					boleta_blanca_amtima);
			
			/*Regla para evitar que se vena aportes Ospim antes del 01/01/2013*/
			User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(renderRequest));
			boolean permiteVerAportesOOSSdesde2011 = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_VER_APORTES_OSPIM);
			
			if(!permiteVerAportesOOSSdesde2011){

				Calendar fechaRestriccion = Calendar.getInstance();
				fechaRestriccion.set(Calendar.YEAR, 2013);
				fechaRestriccion.set(Calendar.MONTH, 0);
				fechaRestriccion.set(Calendar.DATE, 1);
				fechaRestriccion.set(Calendar.HOUR_OF_DAY, 0);
				fechaRestriccion.set(Calendar.MINUTE, 0);
				fechaRestriccion.set(Calendar.SECOND, 0);
				fechaRestriccion.set(Calendar.MILLISECOND, 0);

				List<AporteAfiliado> auxAportesRestringidos = new ArrayList<AporteAfiliado>();
				
				for (Iterator<AporteAfiliado> iterator = afiAportes.iterator(); iterator.hasNext();) {
					
					AporteAfiliado aa = iterator.next();
					if(aa.getTipoAporte() == WebKeysGlobal.TIPO_BOLETA_OS && (aa.getPeriodo().getTime() < fechaRestriccion.getTimeInMillis())){ // aporte_os
//						aa.setMostrar(false);
						auxAportesRestringidos.add(aa);
					}
				}
				afiAportes.removeAll(auxAportesRestringidos);
			}
			/*fin regla*/
			
			//almaceno la lista en sesion
			renderRequest.getPortletSession().removeAttribute(WebKeysAfiliados.APORTES_AFIP, PortletSession.APPLICATION_SCOPE);
			renderRequest.getPortletSession().setAttribute(WebKeysAfiliados.APORTES_AFIP, afiAportes , PortletSession.APPLICATION_SCOPE);
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest,Exception.class.getName());
			//setForward(renderRequest, "portlet.afiliados.error");
		}		
		return mapping.findForward("portlet.ver.aportes");
	}
	



}
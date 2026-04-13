package ar.com.ospim.autorizaciones.action;

import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class PreautorizacionesVerificaPrestacionesRequierenAutorizacionJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean noRequiere=true;	
		String mensaje="";
		try {
		
			HttpSession session = req.getSession();
			PreAutorizacion m = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			if(m.getCodigosPresentados()!=null && !m.getCodigosPresentados().isEmpty()) {
			 for(PreAutorizacionPrestacion p: m.getCodigosPresentados()){
			  if(p.getFechaBaja()==null){
				  
				Nomenclador n = NomencladorServiceUtil.buscarNomencladorPorId(p.getNomenclador().getId_prestacion());
				
				
				if( n.getRequiereAutorizacion() ){
					noRequiere=false;
//					mensaje="Las prestaciones requieren Autorización, no puede seleccionar el estado NO REQUIERE AUTORIZACIÓN";
					break;
				}
				
				
				
			  }	
			 }
			}else {
				noRequiere=false;
			}
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		return "{ \"norequiere\" : \"" + noRequiere +
				 "\",\"mensaje\" : \"" + mensaje +
				"\"}";
	}
	
}

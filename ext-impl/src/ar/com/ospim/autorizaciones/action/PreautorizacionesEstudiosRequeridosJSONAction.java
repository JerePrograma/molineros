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
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class PreautorizacionesEstudiosRequeridosJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean requiereHistoriaClinica=false;
		boolean requiereEstudiosComplementarios=false;
		boolean requiereBiopsia=false;
		boolean requiereAnatomiaPatologica=false;
		boolean recuperaSUR=false;
		try {
		
			HttpSession session = req.getSession();
			PreAutorizacion m = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			
			requiereHistoriaClinica=m.getExisteHistoriaClinica();
			requiereEstudiosComplementarios=m.getExisteEstudiosComplementarios();
			requiereBiopsia=m.getExisteBiopsia();
			requiereAnatomiaPatologica=m.getExisteAnatomiaPatologica();
			
			for(PreAutorizacionPrestacion p: m.getCodigosPresentados()) {
                 Nomenclador n= NomencladorServiceUtil.buscarNomencladorPorId( p.getNomenclador().getId_prestacion());
                if(p.getFechaBaja()==null) { 
                   if(n.getRecuperaSUR()) {
                	  recuperaSUR=true;
                	  break;
                   }
                } 
			}
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		
		return "{ \"requierehistoriaclinica\" : \"" + requiereHistoriaClinica +
				 "\",\"requiereestudioscomplementarios\" : \"" + requiereEstudiosComplementarios +
				 "\",\"requierebiopsia\" : \"" + requiereBiopsia +
				 "\",\"requiereanatomiapatologica\" : \"" + requiereAnatomiaPatologica +
				 "\",\"recuperasur\" : \"" + recuperaSUR +
				"\"}";
		
	}
	
}

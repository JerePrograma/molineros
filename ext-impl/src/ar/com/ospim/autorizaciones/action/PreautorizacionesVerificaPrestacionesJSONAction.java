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

public class PreautorizacionesVerificaPrestacionesJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		Integer marcaRein =Integer.parseInt(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_DISCAPACIDAD_MARCA_REINLIQ"));
		boolean esDiscapacidad =ParamUtil.getBoolean(req, "discapacidad");
		String estado=ParamUtil.getString(req,"estado");
		boolean inconsistencia=false;	
		String mensaje="";
		try {
		
			HttpSession session = req.getSession();
			PreAutorizacion m = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			String strTipo = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_TIPOS_NOMENCLADOR_VALIDOS");
			
			boolean primerElemento=true;
			boolean esSupraAnt=false;
			
			if( m.getCodigosPresentados().isEmpty()) {
				inconsistencia=true;
				mensaje="Debe Ingresar alguna prestación";
			}
			
			for(PreAutorizacionPrestacion p: m.getCodigosPresentados()){
			  if(p.getFechaBaja()==null){
				  
				Nomenclador n = NomencladorServiceUtil.buscarNomencladorPorId(p.getNomenclador().getId_prestacion());
				
				String tipo = n.getId_tipo_nomenclador_string().trim();
				Integer marcaR =n.getMarcaReintegroLiquidacion();
				int resultado = strTipo.indexOf(tipo);
				if(esDiscapacidad &&  !marcaR.equals(marcaRein)){
				   inconsistencia=true;
				   mensaje="Las prestaciones cargadas deben pertenecer al nomenclador de Discapacidad";
				   break;
				}else if(!esDiscapacidad && resultado==-1){
				   inconsistencia=true;
				   mensaje="Las prestaciones cargadas no deben pertenecer al nomenclador de Discapacidad";
				   break;	
				}
				
				if("NR".equalsIgnoreCase(estado) && n.getRequiereAutorizacion() ){
					inconsistencia=true;
					mensaje="Las prestaciones requieren Autorización, no puede seleccionar el estado NO REQUIERE AUTORIZACIÓN";
					break;
				}
				
				if(primerElemento) {
				  esSupraAnt=p.getNomenclador().isSupra();
				  primerElemento=false;
				}
				
				if(esSupraAnt != p.getNomenclador().isSupra() ) {
					inconsistencia=true;
					mensaje="Todas las prestaciones deben tener la marca de SUPRA, o ninguna de ellas deben tenerla";
					break;
				}
				
			  }	
			}
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		return "{ \"inconsistencia\" : \"" + inconsistencia +
				 "\",\"mensaje\" : \"" + mensaje +
				"\"}";
	}
	
}

package ar.com.ospim.autorizaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

public class PreautorizacionesVerificaMedicamentosJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
//		Integer marcaRein =Integer.parseInt(TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_DISCAPACIDAD_MARCA_REINLIQ"));
		boolean esDiscapacidad =ParamUtil.getBoolean(req, "discapacidad");
		String estado=ParamUtil.getString(req,"estado");
		boolean inconsistencia=false;	
		String mensaje="";
		try {
		
			HttpSession session = req.getSession();
			PreAutorizacion preAut = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			
			if( preAut.getMedicamentosPresentados().isEmpty()) {
				inconsistencia=true;
				mensaje="Debe ingresar algún medicamento";
			}
			
			for(PreAutorizacionMedicamento med: preAut.getMedicamentosPresentados()){
			  if(med.getFechaBaja()==null){
				
				if("NR".equalsIgnoreCase(estado)){
					inconsistencia=true;
					mensaje="Los medicamentos requieren Autorización, no puede seleccionar el estado NO REQUIERE AUTORIZACIÓN";
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

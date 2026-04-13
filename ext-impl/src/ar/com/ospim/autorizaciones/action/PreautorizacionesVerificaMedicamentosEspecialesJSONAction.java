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
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class PreautorizacionesVerificaMedicamentosEspecialesJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String afiliados =TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_AFILIADOS_MEDICACION_ESPECIAL");
		String nroDoc=ParamUtil.getString(req,"nrodoc");
		boolean inconsistencia=false;	
		String mensaje="";
		String gestionospim="NO";
		try {
			 int intIndex = afiliados.indexOf(nroDoc);
		     if(intIndex != - 1){
		       gestionospim="SI";
		     }
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		return "{ \"gestionospim\" : \"" + gestionospim +
				 "\",\"mensaje\" : \"" + mensaje +
				"\"}";
	}
	
}

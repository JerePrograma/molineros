package ar.com.ospim.autorizaciones.action; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;


public class ValidaNroCorrespondenciaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		int   nroCorrespondencia= ParamUtil.getInteger(req,"nrocorrespondencia");
		String extra ="";
		boolean respuesta = false;
	
		CabeceraCorrespondencia cabecera = CorrespondenciaServiceImpl.buscarCabeceraCorrespondenciaPorId(nroCorrespondencia);
		if ( cabecera ==null){
			respuesta=false;
		}else{
			respuesta=true;
		}
		
        String resultado = "{}";
	    resultado = "{ \"nroCorrespondenciaExiste\" : \"" 			    + respuesta 		    + "\",\"extra\" : \"" 	        + extra + "\" }";
		return resultado;		
	}

}
package ar.com.ospim.afiliados.action;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.CredencialesServiceUtil;

/**
 * Este Servicio valida si hay que imprimir credencial y registra en el legajo generaLoteAImprimir
 * @author Pablo
 *
 */

public class ValidarCredencialExentoCoPago extends JSONAction {
	private static Log _log = LogFactoryUtil.getLog(ValidarCredencialExentoCoPago.class);
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
	    String resultado = "{}";
	
		User user = PortalUtil.getUser(req);
	    
		String cuil_titular= ParamUtil.getString(req, "cuil_titular");
		int inte= ParamUtil.getInteger(req, "inte");
		
	
		Boolean existe=false;
		
			
		_log.debug("Ingresa a ValidarCredencialExentoCoPago " + cuil_titular + " inte " + inte);
			
		  try {
				if(CredencialesServiceUtil.validarExisteExentoCopago(cuil_titular,inte)==1 ){
					existe=true;
					
					List<Afiliado>  afi =  BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(cuil_titular,String.valueOf(inte),"", 
							 																		"", 0,"", "", "", 0, 0, null); 
					CredencialesServiceUtil.generaLoteAImprimir(afi, user);
				 }
		  }catch (Exception e) {
			_log.error(e);
		  }		  
			  
		  resultado = "{ \"existe\" : \"" 
					    + existe 
				        + "\" }";
			 		
		
		return resultado;
	}
	
		
}
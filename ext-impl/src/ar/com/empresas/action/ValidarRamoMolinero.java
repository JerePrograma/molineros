package ar.com.empresas.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.util.StringUtils;

/**
 * Este Servicio consulta si es ramo molinero o no
 * @author Pablo
 *
 */

public class ValidarRamoMolinero extends JSONAction {
	private static Log _log = LogFactoryUtil.getLog(ValidarRamoMolinero.class);
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
	    String resultado = "{}";
		
	    Empresa emp =  null;
		String cuit= ParamUtil.getString(req, "nroCuitEmpresa");
				
		Boolean existe=false, noExiste=true;
		
		// Consulto si la Empresa es Molinera o no por el ramo
		if(StringUtils.checkNotEmpty(cuit)){	
			
		   _log.debug("Ingresa a ValidarRamoMolinero " + cuit);
			
		  try {
		  	emp=EmpresaServiceUtil.getEmpleadorCompleto(cuit, "000");
		  }catch (Exception e) {
			_log.error(e);
		  }		  
		  if(emp !=null) {
			  existe = emp.isMolinera();
			  
			  resultado = "{ \"existe\" : \"" 
					    + existe 
				        + "\" }";
			  
		  }else {
			  resultado = "{ \"noexiste\" : \"" 
					    + noExiste 
				        + "\" }";
		  }
		 
		}			
		
		
		return resultado;
	}
	
		
}
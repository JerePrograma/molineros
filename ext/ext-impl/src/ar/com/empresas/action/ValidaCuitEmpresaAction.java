package ar.com.empresas.action; 

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.global.beans.Empresa;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.StringUtils;

public class ValidaCuitEmpresaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String  nroCuitEmpresa = ParamUtil.getString(req,"nroCuitEmpresa");
		String nombreEmpresa="";
		boolean respuesta = false;
	
		List<Empresa> busqueda = new ArrayList<Empresa>();
		
		if(StringUtils.checkNotEmpty(nroCuitEmpresa)) {
			busqueda = TraeListasServiceUtil.getEmpleadores(nroCuitEmpresa , "", 0, 0);
				
			if (busqueda !=null && busqueda.size()>0 ){
				respuesta=true; 
				nombreEmpresa =busqueda.get(0).getRazon_soc();
			}
		}	
        String resultado = "{}";
	    resultado = "{ \"nroCuitExisteEnAfip\" : \"" 			    + respuesta 		    + "\",\"nombreEmpresa\" : \"" 	        + nombreEmpresa + "\" }";
		return resultado;
		
	}

}
package ar.com.ospim.farmaciaOspim.action; 

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class ValidaCuitFarmaciaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String  nroCuitFarmacia = ParamUtil.getString(req,"nroCuitFarmacia");
		int idFarmacia =0;
		boolean respuesta = false;
	
		List<Farmacia> busqueda = TraeListasServiceUtil.getEmpresasFarmacia(nroCuitFarmacia);
		if (busqueda !=null && busqueda.size()>0 ){
			respuesta=true; 
			idFarmacia  =busqueda.get(0).getId_farmacia() ;			
		}
        String resultado = "{}";
	    resultado = "{ \"nroCuitExisteEnAfip\" : \"" 			    + respuesta 		    + "\",\"idFarmacia\" : \"" 	        + String.valueOf(idFarmacia)   + "\" }";
		return resultado;
		
	}

}
package ar.com.ospim.farmaciaOspim.action;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.farmaciaOspim.services.BusquedaColegioFarmaciaServiceUtil;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class ActualizaColegioFarmaciaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		
		String descripcion = ParamUtil.getString(req,"descripcion");
		Long idColegio =0L;
		if( !BusquedaColegioFarmaciaServiceUtil.existeColegioFarmacia(descripcion) ){
			idColegio =BusquedaColegioFarmaciaServiceUtil.insertaColegioFarmacia(descripcion, "") ;
			 
		}
		String resultado = "";
		resultado = "{ \"id\" : \"" 
		    				    + idColegio 
		    			        + "\",\"descripcion\" : \""
		    			        + descripcion+ "\" }";
		    				
		return resultado;
		
	}

}
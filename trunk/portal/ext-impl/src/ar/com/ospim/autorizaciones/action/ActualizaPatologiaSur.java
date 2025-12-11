package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class ActualizaPatologiaSur extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		
		String descripcion = ParamUtil.getString(req,"descripcion");
		Long idPatologia=0L;
		if( !SeguimientoSurServiceUtil.existePatologiaSur(descripcion) ){
			idPatologia=SeguimientoSurServiceUtil.insertaPatologia(descripcion,"");
		            		
		}
		String resultado = "";
		resultado = "{ \"id\" : \"" 
		    				    + idPatologia 
		    			        + "\",\"descripcion\" : \""
		    			        + descripcion+ "\" }";
		    				
		return resultado;
		
	}

}
package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.struts.JSONAction;

public class BuscarAfiliadoFechaVtoDiscapacidad extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String cuilTitular = req.getParameter("cuil_titular");
		String inte  = req.getParameter("inte");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		Afiliado afiliado =EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuilTitular, Integer.parseInt(inte)) ;
		String resultado = "";
		try{
		  resultado =sdf.format(afiliado.getFechaVtoDocDiscap());
		}catch(Exception e){}
		if(afiliado != null)
	     resultado = "{ \"fechaVto\" : \"" 
				    + resultado 
			        + "\",\"discapacitado\" : \""
			        + afiliado.getDiscapacitado()+ "\" }";
				
		return resultado;
	}

}
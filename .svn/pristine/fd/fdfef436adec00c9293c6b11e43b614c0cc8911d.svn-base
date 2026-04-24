package ar.com.ospim.rrhh.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.rrhh.services.TarjetasServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class TarjetaLegajoValidacion extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {		
		int nroTarjeta =Integer.parseInt(req.getParameter("nroTarjeta"));
		int legajoPersona =Integer.parseInt(req.getParameter("legajoPersona"));
		int idTarjeta =Integer.parseInt(req.getParameter("idTarjeta"));
		int respTarjeta=0 ; 
		TarjetaAcceso  tarjeta = TarjetasServiceUtil.getTarjetaxNroTarjetaoLegajo(nroTarjeta , legajoPersona, idTarjeta );
		if (tarjeta !=null){
			if (tarjeta.getId_tarjeta_acceso()==nroTarjeta  ){  
				respTarjeta =1;
			}		
		}
		String resultado = "";
			   resultado = "{ \"tarjetaValidacion\" : \""
					        + respTarjeta + "\" }";
		return resultado;
	}

}
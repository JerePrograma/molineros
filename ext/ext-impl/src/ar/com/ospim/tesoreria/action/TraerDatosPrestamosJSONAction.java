package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class TraerDatosPrestamosJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		SimpleDateFormat fdp = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String cuil = req.getParameter("cuil");
		String prestamoStr =req.getParameter("prestamo_id");
		
		Long prestamoId=Long.parseLong(prestamoStr);
		
	   
		Prestamo prestamo  =HotelesServiceUtil.getPrestamoById(prestamoId);
				
		
		String afiliado ="";
		Double total =0D;
		
		Boolean inexistente=true;
		Boolean otroAfiliado=false;
		
		
		if(prestamo!=null) {
			inexistente=false;
			if(!cuil.equalsIgnoreCase(prestamo.getAfiliado().getCuil_titular())) {
				otroAfiliado=true;
			}
			total=prestamo.getTotal();
		}
		
		String resultado = "{ \"inexistente\" :\""  
				    + inexistente 
				    + "\",\"otroAfiliado\" : \"" 
				    + otroAfiliado 
				    + "\",\"total\" : \""
			        + total+ "\" }";
		
		return resultado;
	}

}
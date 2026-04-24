package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ValidarReclamoAfiliadoPrestaciones extends JSONAction  {
	private static Log _log = LogFactoryUtil.getLog(ValidarReclamoAfiliadoPrestaciones.class);


	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
	
	    String resultado = "{}";
		String codError = "0";
	
		_log.debug("ValidarReclamoAfiliadoPrestaciones ");
		
		HttpSession session = (HttpSession) req.getSession();
		List<PrestacionesReclamo> prestaciones= (List<PrestacionesReclamo>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
		
		
        String baja = ParamUtil.getString(req, "baja");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date bajaFecha =null;
		try {
		  bajaFecha= sdf.parse(baja);
		}catch(Exception e) {
			bajaFecha=null;
		}
		
		boolean fechaBaja =false;
		if(bajaFecha !=null) {
		   for(PrestacionesReclamo p:prestaciones) {
			fechaBaja=DateUtils.esMayor(p.getFechaPrestacion(),bajaFecha);
			if(fechaBaja) break;
		   }
		}   
		
		if (fechaBaja == true){
			codError = "6";
		}
		
		return  resultado = "{ \"codError\" : \"" 
								    + codError 
							        + "\" }";
		  
	}
			
}
package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.PrecioPlanSuperador;
import ar.com.ospim.tesoreria.service.LiquidacionPlanesSuperadoresServiceUtil;

public class PrecioPlanSuperadorJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		boolean inconsistencia=false;	
		List<String> mensaje=new ArrayList<String>();
		try {
		
			HttpSession session = req.getSession();
			PrecioPlanSuperador m = (PrecioPlanSuperador) session.getAttribute(WebKeysTesoreria.PRECIO_EN_SESSION);
			
			String fechaDia = ParamUtil.getString(req,"ddeDia");
			String fechaMes = ParamUtil.getString(req,"ddeMes");
			String fechaAnio = ParamUtil.getString(req,"ddeAnio");
			
			String fechaDiaH = ParamUtil.getString(req,"htaDia");
			String fechaMesH = ParamUtil.getString(req,"htaMes");
			String fechaAnioH = ParamUtil.getString(req,"htaAnio");
			
			Date fechaD = null;
			try {
				fechaD = formatoDeFechas.parse(fechaDia + "/"
						+ (Integer.parseInt(fechaMes) + 1) + "/"
						+ fechaAnio);
			} catch (Exception e) {
				fechaD = null;
			}
			
			Date fechaH = null;
			try {
				fechaH = formatoDeFechas.parse(fechaDiaH + "/"
						+ (Integer.parseInt(fechaMesH) + 1) + "/"
						+ fechaAnioH);
			} catch (Exception e) {
				fechaH = null;
			}
			
			Integer edadDde = ParamUtil.getInteger(req, "edadDde");
			Integer edadHta = ParamUtil.getInteger(req, "edadHta");
			m.setEdadDesde(edadDde);
			m.setEdadHasta(edadHta);
			m.setFechaDesde(fechaD);
			m.setFechaHasta(fechaH);
			
			
			/*
			List<PrecioPlanSuperador> list = new ArrayList<PrecioPlanSuperador>();
			PrecioPlanSuperador filtro= new PrecioPlanSuperador();
			filtro.setFechaDesde(fechaD);
			filtro.setFechaHasta(fechaH);
			Boolean conProvincia=false;
			Boolean conParentesco=false;
			Boolean conPlanes=false;
			for (Plan pl : m.getPlanes()) {
				  conPlanes=true;
			      filtro.setPlanes(new ArrayList<Plan>());
				  filtro.getPlanes().add(pl);
				  for(Parentesco pa:m.getParentescos()) {
					 conParentesco=true; 
					filtro.setParentescos(new ArrayList<Parentesco>());  
					filtro.getParentescos().add(pa);  
					for(Provincia pr:m.getProvincias()) {
						conProvincia=true;
						filtro.getProvincias().add(pr);
						mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
						if(!mensaje.isEmpty()) {
							inconsistencia=true;
							break;
						}
					}
					if(inconsistencia || conProvincia) break;
					mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
					if(!mensaje.isEmpty()) {
						inconsistencia=true;
						break;
					}
				  }
				  if(inconsistencia || conParentesco) break;
				  mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
				  if(!mensaje.isEmpty()) {
					 inconsistencia=true;
					 break;
				  }
			}
		    if(!conPlanes) {
		    	 mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
				 if(!mensaje.isEmpty() ) inconsistencia=true;
		    }
			*/
		    
			mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m);
		    if(!mensaje.isEmpty() ) inconsistencia=true;
		    
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		return "{ \"inconsistencia\" : \"" + inconsistencia +
				 "\",\"mensaje\" : \"" + mensaje.toString() +
				"\"}";
	}
	
}

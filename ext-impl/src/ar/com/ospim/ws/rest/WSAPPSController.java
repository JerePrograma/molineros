package ar.com.ospim.ws.rest;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;

@Controller
public class WSAPPSController {

	private static Log _log = LogFactoryUtil.getLog(WSAPPSController.class);
	
	@RequestMapping(value = "/AFILIADO_SEARCH_INTEGRAL", method = RequestMethod.GET)
	protected ModelAndView validaAfiliado(@RequestParam("tipoDoc") String tipoDoc,
			@RequestParam("nroDoc") String nroDoc,
			HttpServletRequest request)
			throws Exception {
		
		Map<String, String> model = new HashMap<String, String>();
		Afiliado afiliado = null;

		String ip = request.getRemoteAddr();
		_log.info("APPS CONSULTA IP: " + ip);
		String credencial="";
		
		/*
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "ingreso_portal.properties");
		InputStream stream = new FileInputStream(configFile);
		Properties props = new Properties();
		props.load(stream);
		StringTokenizer ips_seguras = new StringTokenizer(
				props.getProperty("ip_seguras_ws"), ",");
		boolean ipSegura=false;
		while (ips_seguras.hasMoreElements()) {
			String ip_segura = ips_seguras.nextToken();
			if (ip.contains(ip_segura)) {
				ipSegura= true;
			}
		}
		if(!ipSegura){
			model.put("estado", "IP DEL CLIENTE NO SEGURA");
			return new ModelAndView("jsonView", model);
		}
        */
		
		_log.debug("Documento:  " + tipoDoc + " " +nroDoc);
		
		if (!validarIngreso( tipoDoc, nroDoc,null)) {
			model.put("estado", "DATOS INGRESADOS NO VALIDOS");
		} else {
			
			try {
				
				List<Afiliado> afs =BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponenteCredencialUOMA(null, null, tipoDoc, nroDoc, 
						0,null,null,null, 0, 0, null);
						
				if(afs!=null && afs.size()>0) {
				   afiliado = afs.get(0);
				   if (null != afiliado) {
					if (null != afiliado.getUltimo_plan().getDescripcion() && ("INTEGRAL".equals(afiliado.getUltimo_plan().getDescripcion()) ||
							"OSPIM INTEGRAL".equals(afiliado.getUltimo_plan().getDescripcion()) )
							) {
						credencial =StringUtil.valueOf(afiliado.getId_ospim()) +"-"+StringUtil.valueOf(afiliado.getInte());
						model.put("estado", "HABILITADO");
						model.put("credencial", credencial);
					} else {
						model.put("estado", "INHABILITADO");
						model.put("credencial", "");
					}
				   } else {
					model.put("estado", "INHABILITADO");
					model.put("credencial", "");
				   }
			  }else {
					  model.put("estado", "INHABILITADO");
					  model.put("credencial", "");
			  }   
				
			} catch (Exception e) {
				_log.error("APPS CONSULTA ERROR");
				_log.error(e);
				model.put("estado", "ERROR AL CONSULTAR AFILIADO");
			}
			
		}

		return new ModelAndView("jsonView", model);
	}
	
	@RequestMapping(value = "/AFILIADO_SEARCH_PLAN", method = RequestMethod.GET)
	protected ModelAndView validaAfiliadoSearchPlan(
	        @RequestParam("tipoDoc") String tipoDoc,
	        @RequestParam("nroDoc") String nroDoc,
	        HttpServletRequest request) throws Exception {

	    Map<String, String> model = new HashMap<String, String>();

	    String credencial = "";

	    if (!validarIngresoPlan(tipoDoc, nroDoc)) {
	        model.put("estado", "DATOS INGRESADOS NO VALIDOS");
	        model.put("credencial", "");
	        return new ModelAndView("jsonView", model);
	    }

	    try {

	        credencial = BusquedaAfiliadoServiceUtil.getCredencialAfiliadoPlan(tipoDoc, nroDoc);

	        if (credencial != null && credencial.trim().length() > 0) {
	            model.put("estado", "HABILITADO");
	            model.put("credencial", credencial);
	        } else {
	            model.put("estado", "INHABILITADO");
	            model.put("credencial", "");
	        }

	    } catch (Exception e) {
	        _log.error("AFILIADO_PLAN ERROR");
	        _log.error(e);
	        model.put("estado", "ERROR AL CONSULTAR AFILIADO");
	        model.put("credencial", "");
	    }

	    return new ModelAndView("jsonView", model);
	}
	
	public boolean validarIngresoPlan(String tipoDoc, String nroDoc) {

	    if (tipoDoc == null || tipoDoc.trim().length() == 0 || "null".equalsIgnoreCase(tipoDoc.trim())) {
	        return false;
	    }

	    if (nroDoc == null || nroDoc.trim().length() == 0 || "null".equalsIgnoreCase(nroDoc.trim())) {
	        return false;
	    }

	    return true;
	}
	
	public boolean validarIngreso(String tipoDoc,
			String nroDoc,String fecha) {
		/*
		if ((ar.com.ospim.util.StringUtils.checkEmpty(nroCredencial)
				|| "null".equals(nroCredencial.trim()) || null == ar.com.ospim.util.StringUtils
				.getLongOrNull(nroCredencial))
				&& (ar.com.ospim.util.StringUtils.checkEmpty(nroDoc)
						|| "null".equals(nroDoc.trim()) || null == ar.com.ospim.util.StringUtils
						.getIntegerOrNull(nroDoc))) {
			return false;
		}
		*/
		return true;
	}

}

package ar.com.ospim.ws.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.webservice.service.SimuladorSalarialServiceUtil;

@Controller
public class WSSimuladorSalarialController {

	@RequestMapping(value = "/ubicacionsimulador", method = RequestMethod.GET)
	protected ModelAndView validaAfiliado(@RequestParam("provincia") Integer provincia,
			@RequestParam("localidad") Integer localidad)
			throws Exception {
		Map<String, String> model = new HashMap<String, String>();
		
		if(provincia!=null && localidad!=null){
			try{
			   SimuladorSalarialServiceUtil.registraUbicacionConsulta(provincia, localidad);
			   model.put("estado", "OK");
			}catch(Exception e){
				model.put("estado", "ERROR");	
			}
		}else{
			model.put("estado", "PARAMETROS NULOS");
		}
		
		
		return new ModelAndView("jsonView", model);
	}

	

}

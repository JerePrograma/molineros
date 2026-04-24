package ar.com.ospim.ws.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.experimental.theories.ParametersSuppliedBy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WSEjemploController {

	@RequestMapping(value = "/ejemplo", method = RequestMethod.GET)
	protected ModelAndView ejemploWS(@RequestParam("param1") String param1) throws Exception {
		Map<String,String> model = new HashMap<String,String>();
		model.put("firstname", param1);
		model.put("secondname", "Schmitt");

		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value = "/ejemplo2", method = RequestMethod.GET)
	protected ModelAndView ejemploWS2() throws Exception {
		Map model = new HashMap();
		model.put("informe", "Peter");
		
		List<Map<String,String>> lista = new ArrayList<Map<String,String>>();
		
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("firstname", "Peter");
		map1.put("secondname", "Schmitt");
		lista.add(map1);
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("firstname", "Peter2");
		map2.put("secondname", "Schmitt2");
		lista.add(map2);
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("firstname", "Peter3");
		map3.put("secondname", "Schmitt3");
		lista.add(map3);
		
		model.put("detalle", lista);
		
		return new ModelAndView("jsonView", model);
	}
	
	@RequestMapping(value = "/ejemplo3", method = RequestMethod.GET)
	protected ModelAndView ejemploWS3() throws Exception {
		Map model = new HashMap();
		model.put("informe", "Peter");
		
		List<Map<String,String>> lista = new ArrayList<Map<String,String>>();
		
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("firstname", "Peter");
		map1.put("secondname", "Schmitt");
		lista.add(map1);
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("firstname", "Peter2");
		map2.put("secondname", "Schmitt2");
		lista.add(map2);
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("firstname", "Peter3");
		map3.put("secondname", "Schmitt3");
		lista.add(map3);
		
		model.put("detalle", lista.toArray());
		
		return new ModelAndView("jsonView", model);
	}
}

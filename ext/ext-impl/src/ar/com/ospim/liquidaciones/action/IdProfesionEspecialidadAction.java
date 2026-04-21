package ar.com.ospim.liquidaciones.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ar.com.ospim.prestadores.WebKeysPrestadores;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class IdProfesionEspecialidadAction extends JSONAction {

	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int iProfesionInt = ParamUtil.getInteger(req, "idProfesion");
		
		@SuppressWarnings("unchecked")
		List<EspecialidadPrestador> lista = (List<EspecialidadPrestador>) req.getSession()
				.getAttribute(WebKeysPrestadores.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION);
		
		Collections.sort(lista, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				EspecialidadPrestador o11 = (EspecialidadPrestador) o1;
				EspecialidadPrestador o22 = (EspecialidadPrestador) o2;
				
				return o11.getDescripcion().compareTo(o22.getDescripcion()) ;
			}
		});
		
		String json = "{\"listaFiltrada\": ["; 
		for(int i = 0; i < lista.size(); i++) {
			int idProf = lista.get(i).getIdProfesion();
			if(idProf == iProfesionInt) { 
				json += "\""+lista.get(i).getIdEspecialidad()+"|"+lista.get(i).getDescripcion()+"\"" + ",";			
			}
		}	
		int count = json.length();
		String especialidad = json.substring(0, count/*-1*/);
		especialidad += "]}";
		return especialidad;
	}
}
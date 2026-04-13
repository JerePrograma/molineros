package ar.com.ospim.liquidaciones.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.SubEspecialidadPrestador;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class IdEspecialidadSubespecialidadAction extends JSONAction {

	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int iEspecialidadInt = ParamUtil.getInteger(req, "idEspecialidad");
		
		@SuppressWarnings("unchecked")
		List<SubEspecialidadPrestador> lista = (List<SubEspecialidadPrestador>) req.getSession()
				.getAttribute(WebKeysLiquidaciones.LISTAS_DE_SUB_ESPECIALIDAD_PRESTADOR_EN_SESSION );
		
		Collections.sort(lista, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				SubEspecialidadPrestador o11 = (SubEspecialidadPrestador) o1;
				SubEspecialidadPrestador o22 = (SubEspecialidadPrestador) o2;
				
				return o11.getDescripcion().compareTo(o22.getDescripcion()) ;
			}
		});
		
		String json = "{\"listaFiltrada\": ["; 
		for(int i = 0; i < lista.size(); i++) {
			int idEsp = lista.get(i).getIdEspecialidad();
			if(idEsp == iEspecialidadInt) { 
				json += "\""+lista.get(i).getId()+"|"+lista.get(i).getDescripcion()+"\"" + ",";			
			}
		}	
		int count = json.length();
		String subespecialidad = json.substring(0, count/*-1*/);
		subespecialidad += "]}";
		return subespecialidad;
	}
}
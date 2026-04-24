package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;

public class ValidaPlanTercerizadoraAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		Integer idPlan = ParamUtil.getInteger(req,"id_plan");
		
		ArrayList<TercerizadoraServicio> tercerizadorasPlan=(ArrayList<TercerizadoraServicio>) TercerizadoraServiceUtil.getInstance().getTercerizadoraPlan(idPlan);

//		viene ordenado de la BD
//		Collections.sort(tercerizadorasPlan, new Comparator() {
//			@Override
//			public int compare(Object o1, Object o2) {
//				TercerizadoraServicio o11 = (TercerizadoraServicio) o1;
//				TercerizadoraServicio o22 = (TercerizadoraServicio) o2;
//				
//				return o11.getDescripcion().compareTo(o22.getDescripcion()) ;
//			}
//		});
		
		String json = "{\"listaFiltrada\": ["; 
		json += "\""+""+"|"+"Seleccione una tercerizadora"+"\"" + ",";

		for (Iterator<TercerizadoraServicio> iterator = tercerizadorasPlan.iterator(); iterator.hasNext();) {
			TercerizadoraServicio ts = iterator.next();
			
			json += "\""+ts.getId_tercerizadora()+"|"+ts.getDescripcion()+"\"" + ",";
			
		}

		int count = json.length();
		String result = json.substring(0, count/*-1*/);
		result += "]}";
		return result;
	}
	
}
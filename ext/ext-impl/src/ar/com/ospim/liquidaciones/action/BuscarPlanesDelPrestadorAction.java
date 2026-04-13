package ar.com.ospim.liquidaciones.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.PrestadorPlan;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class BuscarPlanesDelPrestadorAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Plan> listaPlanes = new ArrayList<Plan>();
		
		int idPrestador = ParamUtil.getInteger(req, "idPrestador");
		
		List<PrestadorPlan> lista = PrestadorServiceUtil.getPlanesDelPrestador(idPrestador);
		
		for (Iterator<PrestadorPlan> iterator = lista.iterator(); iterator.hasNext();) {
			PrestadorPlan pp = iterator.next();
			listaPlanes.add(pp.getPlan());
		}		
		req.getSession().removeAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
		
		String json = "{\"listaFiltrada\": ["; 
		if(listaPlanes!= null && listaPlanes.size() > 0){
			json += "\"0|TODOS los planes del Prestador\",";
		}else{
			json += "\"-1|Debe cargar planes al Prestador\",";
		}
		
		for(int i = 0; i < listaPlanes.size(); i++) {
			json += "\""+listaPlanes.get(i).getId()+"|"+listaPlanes.get(i).getDescripcion()+"\"" + ",";			
		}	
		int count = json.length();
		String planes = json.substring(0, count-1);
		planes += "]}";
		
		req.getSession().setAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION, listaPlanes);
		
		return planes;
	}
}
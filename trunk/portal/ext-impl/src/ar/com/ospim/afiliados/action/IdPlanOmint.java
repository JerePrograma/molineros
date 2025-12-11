package ar.com.ospim.afiliados.action;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.novedades.service.PreAfiliadoServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class IdPlanOmint extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String idPlan = req.getParameter("idPlan");
		List<Plan> lista = TraeListasServiceUtil.getPlanesOmint(req.getSession());
		int idPlanInt = Integer.parseInt(idPlan);
		
		String resultado = "";
		
		for (Iterator<Plan> iterator = lista.iterator(); iterator.hasNext();) {
			Plan plan = iterator.next();
			
			if(plan.getId() == idPlanInt){
				 resultado = "{ \"planOmint\" : \"" 
						    + plan.getId_plan_omint() 
						    + "\",\"descripcionOmint\" : \""
					        + plan.getDescripcionOmint() 
						    + "\",\"descripcionPrevencion\" : \""
					        + plan.getDescripcionPrevencion()  
					        + "\",\"farmaciaPrevencion\" : \""
					        + plan.getFarmaciaPrevencion()+ "\" }";
				
				break;
			}
			
		}
		return resultado;
	}
	
//	@Override
//	public String getJSON(ActionMapping arg0, ActionForm arg1,
//			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
//		String idPlan = req.getParameter("idPlan");
//		List<Plan> lista = TraeListasServiceUtil.getPlanesOmint(req.getSession());
//		int idPlanInt = Integer.parseInt(idPlan);
//		String json = "{ \"planOmint\": [";
//		int countJson = json.length();
//		int i = 0;
//		while (i < lista.size()) {
//			if (lista.get(i).getId() == idPlanInt) {
//				json += "\"" + lista.get(i).getId_plan_omint() + "|"
//						+ lista.get(i).getDescripcionOmint() + "\"" + ",";
//			}
//			i++;
//		}
//		int count = json.length();
//		String plan_omint = json.substring(0, count - 1);
//		if (countJson == count) {
//			plan_omint += "\"" + idPlanInt + "|" + "0" + "\"";
//		} else {
//			plan_omint += "] ";
//		}
//		plan_omint += "}";
//		return plan_omint;
//	}
}
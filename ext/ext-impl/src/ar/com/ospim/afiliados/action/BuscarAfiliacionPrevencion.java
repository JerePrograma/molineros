package ar.com.ospim.afiliados.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.struts.JSONAction;

public class BuscarAfiliacionPrevencion extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String cuilTitular = req.getParameter("cuil_titular");
		String inte  = req.getParameter("inte");
		
		AfiliacionPrevencionDTO prevenDTO = PlanServiceUtil.getInstance().buscarAfiliacionPrevencion(cuilTitular, Integer.parseInt(inte));
		
		String resultado = "{}";
		
		if(prevenDTO != null)
	    resultado = "{ \"nroSocioPrev\" : \"" 
			    + prevenDTO.getNroSocio() 
		        + "\",\"credencialPrevencion\" : \""
		        + prevenDTO.getNroCredencial()+ "\" }";
				
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
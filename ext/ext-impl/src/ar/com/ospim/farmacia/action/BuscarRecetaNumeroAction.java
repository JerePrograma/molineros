package ar.com.ospim.farmacia.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.services.ReintegroFarmaciaServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class BuscarRecetaNumeroAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		String recetaNum = req.getParameter("numero_receta");
		String id_reintegro = req.getParameter("id_reintegro");
		String receta = null;
		ReintegroMedicamento reintegroMedicamento = ReintegroFarmaciaServiceUtil.getReintegroPorNumeroReceta(Integer.valueOf(recetaNum));

		if (Integer.valueOf(id_reintegro) == 0) {
			if (reintegroMedicamento != null && reintegroMedicamento.getMedicamentos().size() > 0) {				
				receta = recetaNum;
			} else {
				receta = "0";
			}
		} else {
			if (reintegroMedicamento != null && 
					reintegroMedicamento.getId_reintegro() != Integer.valueOf(id_reintegro) && reintegroMedicamento.getMedicamentos().size() > 0 ) {				
				receta = recetaNum;
			} else {
				receta = "0";
			}	
		}						
		return "{ \"receta\" : \"" + receta + "\"}";
	}
}

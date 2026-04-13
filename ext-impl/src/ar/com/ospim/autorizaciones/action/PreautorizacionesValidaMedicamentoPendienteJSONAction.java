package ar.com.ospim.autorizaciones.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;

public class PreautorizacionesValidaMedicamentoPendienteJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean existeMedicamentoPendiente=false;
		String mensaje="Ya se encuentra este medicamento en la Preautorización nro ";
		
		try {
			
			String cuil_titu= ParamUtil.getString(req, "cuil_titu");
			int inte =ParamUtil.getInteger(req, "inte");
			Integer idMedicamento = ParamUtil.getInteger(req, "idmedicamento");
			Integer idPreautorizacion = ParamUtil.getInteger(req, "idpreautorizacion");
			
		    List<PreAutorizacion>list = PreAutorizacionServiceUtil.getExisteMedicamentoPendiente(idMedicamento, cuil_titu, inte);
			if(list==null || list.size()==0){
				existeMedicamentoPendiente=false;
			}else{
				if(list.size()==1) {
					if(!list.get(0).getId().equals(idPreautorizacion)){
					  existeMedicamentoPendiente=true;
					  mensaje += list.get(0).getId();
					}  
				}else{
				   for(PreAutorizacion p:list){
					   if(!p.getId().equals(idPreautorizacion)){
						   existeMedicamentoPendiente=true;
						   mensaje += p.getId(); 
						   break;
					   }
				   }	   
				}
			}
			
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		
		return "{ \"existemedicamentopendiente\" : \"" + existeMedicamentoPendiente +
				 "\",\"mensaje\" : \"" + mensaje +
				 "\"}";
		
	}
	
}

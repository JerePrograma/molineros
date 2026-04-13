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

public class PreautorizacionesValidaPrestacionPendienteJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean existePrestacionPendiente=false;
		String mensaje="Ya se encuentra esta prestación en la Preautorización nro ";
		
		boolean requiereEstudiosComplementarios=false;
		boolean requiereBiopsia=false;
		boolean requiereAnatomiaPatologica=false;
		try {
			
			String cuil_titu= ParamUtil.getString(req, "cuil_titu");
			int inte =ParamUtil.getInteger(req, "inte");
			Integer idPrestacion = ParamUtil.getInteger(req, "idprestacion");
			Integer idPreautorizacion = ParamUtil.getInteger(req, "idpreautorizacion");
			
		    List<PreAutorizacion>list = PreAutorizacionServiceUtil.getExistePrestacionPendiente(idPrestacion, cuil_titu, inte);
			if(list==null || list.size()==0){
				existePrestacionPendiente=false;
			}else{
				if(list.size()==1) {
					if(!list.get(0).getId().equals(idPreautorizacion)){
					  existePrestacionPendiente=true;
					  mensaje += list.get(0).getId();
					}  
				}else{
				   for(PreAutorizacion p:list){
					   if(!p.getId().equals(idPreautorizacion)){
						   existePrestacionPendiente=true;
						   mensaje += p.getId(); 
						   break;
					   }
				   }	   
				}
			}
			
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		
		return "{ \"existeprestacionpendiente\" : \"" + existePrestacionPendiente +
				 "\",\"mensaje\" : \"" + mensaje +
				 "\"}";
		
	}
	
}

package ar.com.global.action;



import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

/**
 * Trae todos las cuentas que tengan validez en el ejercicio de la fecha
 * 
 * @author DSU
 * 
 */
public class SchedulerVerificaEstadoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String reporte = ParamUtil.getString(req,"reporte", null);
		
		Integer idJob =Integer.parseInt(TraeListasServiceUtil.getSystemConfig(reporte));
		try {
			List<String> estado = SchedulerServiceUtil.status(idJob);
			return getStatusJSON(estado);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	protected String getStatusJSON(List<String> estado) {
		String resultado = "";
		resultado = "{ \"status\" : \"" 
			    + estado.get(0)
		        + "\",\"descripcion\" : \""
		        + estado.get(1)+ "\" }";
		
		return resultado;
	}
}

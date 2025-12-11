package ar.com.ospim.tesoreria.action;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

/**
 * Trae todos las cuentas que tengan validez en el ejercicio de la fecha
 * 
 * @author martin
 * 
 */
public class AportesPagoRamoVerificaEstadoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		Integer idJob =Integer.parseInt(TraeListasServiceUtil.getSystemConfig("reporte.aportes_pago_ramo"));
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

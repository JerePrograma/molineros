package ar.com.ospim.autorizaciones.action;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
public class TraeEstadoMotivoSeguimientoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		Integer idEstado=ParamUtil.getInteger(req, "idEstado");	
		try {
			List<ModalidadAtencion> motivos = TraeListasServiceUtil.getMotivosEstadoSur(idEstado) ;
			return getMotivosJSON(motivos);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	protected String getMotivosJSON(List<ModalidadAtencion> conceptos) {
		boolean primero = true;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"motivos\":[");
		for (ModalidadAtencion c : conceptos) {
			if (!primero) {
				sb.append(",");
			}
			
			sb.append("{\"id\":\"" + c.getId() + "\",\"descripcion\":\""
					+ c.getDescripcion() + "\"");
			sb.append("}");
			primero = false;
		}
		sb.append("]}");
		return sb.toString();
	}
}

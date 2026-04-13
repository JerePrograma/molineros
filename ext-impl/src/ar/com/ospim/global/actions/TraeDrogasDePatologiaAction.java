package ar.com.ospim.global.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.DrogaPatologia;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.PlanCuentas;
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
public class TraeDrogasDePatologiaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		Integer patologia=ParamUtil.getInteger(req, "patologia");	
		try {
			List<DrogaPatologia> drogas = TraeListasServiceUtil.getDrogasPorPatologia(patologia) ;
			return getDrogasJSON(drogas);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	protected String getDrogasJSON(List<DrogaPatologia> conceptos) {
		boolean primero = true;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"drogas\":[");
		for (DrogaPatologia c : conceptos) {
			if (!primero) {
				sb.append(",");
			}
			
			sb.append("{\"id\":\"" + c.getDrogaId() + "\",\"descripcion\":\""
					+ c.getDrogaDescripcion() + "\"");
			sb.append("}");
			primero = false;
		}
		sb.append("]}");
		return sb.toString();
	}
}

package ar.com.ospim.global.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
public class TraeBimestresDeEjercicioAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String ejercicio=ParamUtil.getString(req, "ejercicio");	
		String clase=ParamUtil.getString(req, "clase");
        SimpleDateFormat fm= new SimpleDateFormat("dd/MM/yyyy"); 
		try {
			Date fechaDesde = fm.parse("01/01/"+ejercicio);
			Date fechaHasta = fm.parse("31/12/"+ejercicio);

			List<String> bimestres = TraeListasServiceUtil.getBimestresPorAnio(fechaDesde, fechaHasta,clase);
			return getBimestresJSON(bimestres);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	protected String getBimestresJSON(List<String> conceptos) {
		boolean primero = true;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"bimestres\":[");
		for (String c : conceptos) {
			if (!primero) {
				sb.append(",");
			}
			String[] xx = c.split("\\|");
			
			sb.append("{\"id\":\"" + xx[0] + "\",\"descripcion\":\""
					+ xx[1] + "\"");
			sb.append("}");
			primero = false;
		}
		sb.append("]}");
		return sb.toString();
	}
}

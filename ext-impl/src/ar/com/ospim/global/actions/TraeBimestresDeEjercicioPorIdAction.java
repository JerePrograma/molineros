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
public class TraeBimestresDeEjercicioPorIdAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		Integer id=ParamUtil.getInteger(req, "bimestreid");
		try {
			String bimestre = TraeListasServiceUtil.getBimestresPorId(id);
			return getBimestresJSON(bimestre);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	protected String getBimestresJSON(String c) {
		boolean primero = true;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"bimestres\":[");
			String[] xx = c.split("\\|");
			
			sb.append("{\"id\":\"" + xx[0] + "\",\"descripcion\":\""
					+ xx[1]+ "\",\"fechainicio\":\""
							+ xx[2] + "\",\"fechafin\":\""
									+ xx[3]
					+ "\"");
			sb.append("}");
		sb.append("]}");
		return sb.toString();
	}
}

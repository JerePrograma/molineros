package ar.com.ospim.global.actions;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
public class TraePlanCuentasDeEjercicioAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int entidad=ParamUtil.getInteger(req, "entidad");		

		try {
			Date fechaDesde = DateUtils.getDesdeEjercicio(req, entidad).getTime();

			List<PlanCuentas> cuentas = TraeListasServiceUtil
					.getPlanCuentasImputables(fechaDesde, entidad);
			return getPlanCuentasJSON(cuentas);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	protected String getPlanCuentasJSON(List<PlanCuentas> planCuentas) {
		boolean primero = true;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"cuentas\":[");
		for (PlanCuentas c : planCuentas) {
			if (!primero) {
				sb.append(",");
			}
			sb.append("{\"id\":\"" + c.getId() + "\",\"numero\":\""
					+ c.getNumero() + "\"");
			sb.append(",\"cuenta\":\"" + c.getCuenta() + "\"");
			sb.append("}");
			primero = false;
		}
		sb.append("]}");
		return sb.toString();
	}
}

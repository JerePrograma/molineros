package ar.com.ospim.autorizaciones.action;



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
public class TraeValoresDefectoExpedienteSURAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String clase=ParamUtil.getString(req, "clase");
        SimpleDateFormat fm= new SimpleDateFormat("dd/MM/yyyy"); 
		try {
			String norma = TraeListasServiceUtil.getSystemConfig("NORMA_SUR_"+clase) ;
			String patologia = TraeListasServiceUtil.getSystemConfig("PATOLOGIA_SUR_"+clase) ;
			return getValoresJSON(norma,patologia);
		} catch (Exception e) {
   			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	protected String getValoresJSON(String norma,String patologia) {
		boolean primero = true;
		StringBuilder sb = new StringBuilder();
		sb.append("{\"norma\":[");
		sb.append("{\"id\":\"" + norma + "\"");
		sb.append("}");
		sb.append("],");
		
		String[] vPatologia = patologia.split(";");
	
		sb.append("\"patologia\":[");
		if(vPatologia.length>1){
		   sb.append("{\"id\":\"" + vPatologia[0] + "\"");
		   sb.append("}");
		   sb.append(",{\"descripcion\":\"" + vPatologia[1] + "\"");
		   sb.append("}");
		}
		sb.append("]}");
		return sb.toString();
	}
}

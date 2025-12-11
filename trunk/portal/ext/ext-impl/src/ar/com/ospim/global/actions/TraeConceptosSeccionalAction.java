package ar.com.ospim.global.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
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
public class TraeConceptosSeccionalAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		Integer seccional=ParamUtil.getInteger(req, "seccional");
		int entidad=ParamUtil.getInteger(req, "entidad");
		
		try {
					
			List<Concepto> conceptosAux = TraeListasServiceUtil.getConceptosConSeccional(DateUtils.getDesdeEjercicioActual().getTime(), entidad);
			
			if(entidad==WebKeysGlobal.OSPIM) seccional=0; 
			
			boolean primero = true;
			StringBuilder sb = new StringBuilder();
			sb.append("{\"conceptos\":[");
			
			for(Concepto c:conceptosAux){
				if(seccional == 0 || c.getIdSeccional()==seccional	){
					if (!primero) {
						sb.append(",");
					}
					
					sb.append("{\"id\":\"" + c.getId()  + "\",\"descripcion\":\""
							+ c.getDescripcion() + "\"");
					sb.append("}");
					primero = false;					

				}
				
			}
			
			String conceptoStr = TraeListasServiceUtil.getSystemConfig("caja_chica_concepto_anticipo_"+entidad);
			if(conceptoStr.length()>0){
				String[] concepto = conceptoStr.split(";");
				if (!primero) {
					sb.append(",");
				}
				
				sb.append("{\"id\":\"" + concepto[0]  + "\",\"descripcion\":\""
						+ concepto[1] + "\"");
				sb.append("}");
			}
			
			
			sb.append("]}");
			
			return sb.toString();
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}

	
}

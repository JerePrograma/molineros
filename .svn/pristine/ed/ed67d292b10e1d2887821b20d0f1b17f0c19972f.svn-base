package ar.com.uoma.centro_costo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.uoma.beans.CentroCosto;


public class CentroCostoContableJSONAction extends JSONAction {
	private static Log _log = LogFactoryUtil.getLog(CentroCostoContableJSONAction.class);

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		String entidad = req.getParameter("entidad");
		String ejercicio = req.getParameter("ejercicio");
		String ddString="";
		if (StringUtils.isNotBlank(ejercicio) && Integer.parseInt(entidad)!=WebKeysGlobal.AMTIMA) {
			ddString = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
		}
		if (StringUtils.isNotBlank(ejercicio) && Integer.parseInt(entidad)==WebKeysGlobal.AMTIMA) {
			ddString = "01/07/" + Integer.valueOf(ejercicio.split("-")[0]);
		}
		
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaUtil = format.parse(ddString);
		
		List<CentroCosto> centros = CentroCostoServiceUtil.getContables(fechaUtil, Integer.parseInt(entidad));
		
		String json = "{\"listaFiltrada\": [";
		if(!centros.isEmpty()) {
		  json+=  "\""+"<option selected value='0'>Seleccione un Centro de Costo</option>" +"\"" + ",";
		}else {
		  json+=  "\""+"<option selected value='0'>Seleccione un Centro de Costo</option>" +"\"" + "";	
		}
		for(CentroCosto c:centros) {
		 	json +=  "\""+ "<option value='" +c.getId()+"'> "+c.getDescripcion() +
        			"</option>"+"\""+",";
		}	
        int count = json.length();
		String centrosc = json.substring(0, count-1);
		centrosc += "]}";
		return centrosc;
	}	
}

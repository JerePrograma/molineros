package ar.com.uoma.paritarias.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.Paritaria;
import ar.com.uoma.paritarias.services.ParitariaServiceUtil;

/**
 * Este Servicio Valida si existe la paritaria
 * @author Pablo
 *
 */

public class ValidarParitariaExistente extends JSONAction {
	private static Log _log = LogFactoryUtil.getLog(ValidarParitariaExistente.class);
	
	Date periodoDate = null;
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		_log.debug("  ValidarParitariaExistente ");
	    String resultado = "{}";
		int result = 0;
	    Paritaria paritaria = new Paritaria(); 
	    
	    String camara= ParamUtil.getString(req, "nombre_camara");
	    String fechaDesdeMes= ParamUtil.getString(req, "fechaDesdeMes");
	    String fechaDesdeAnio= ParamUtil.getString(req, "fechaDesdeAnio");
	    
		Calendar calendar =  DateUtils.getCalendarGMTMenos3(); 
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	
		Date fechaParitaria= null;
		try {
			fechaParitaria = formatoDeFecha.parse("01" + "/"
					+ (Integer.parseInt(fechaDesdeMes) )  + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaParitaria = null;
		}
			
		if (fechaParitaria != null) {			
			calendar.add(Calendar.MONTH, -1);
			periodoDate =DateUtils.getLastDateOfMonth(fechaParitaria, false);
		}		
		paritaria.setCamara(camara);
		paritaria.setFechaAltaParitaria(fechaParitaria);
		
		Boolean existe=false;
		
		result=ParitariaServiceUtil.validarParitariaExistente(paritaria);
		if(result == 0) {
			resultado = "{ \"existe\" : \"" 
						+ existe 
				        + "\" }";
		}else {
			resultado = "{ \"existe\" : \"" 
					+ true 
			        + "\" }";
		}
		return resultado;
	}
	
		
}
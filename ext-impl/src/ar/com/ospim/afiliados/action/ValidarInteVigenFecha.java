package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;

public class ValidarInteVigenFecha extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int result=0;
				
		Afiliado afiliado = null;
		
		String cuil = req.getParameter("cuil_titular");
	
		
		String vigenteFechaDia = req.getParameter("vigenteFechaDia");
		String vigenteFechaMes = req.getParameter("vigenteFechaMes");
		String vigenteFechaAnio = req.getParameter("vigenteFechaAnio");
		
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");
		Date vigenFecha = null;
		try {
			vigenFecha = formatoDeFechaV.parse(vigenteFechaDia + "/"
						+ (Integer.parseInt(vigenteFechaMes) ) + "/"
						+ vigenteFechaAnio);
		} catch (Exception e) {
			vigenFecha = null;
		}
		
		afiliado = EditarAfiliadoServiceUtil.getInstance().getAfiliadoEntryInclusoDadoBaja(cuil,0, null);
		
		if (afiliado.getVigen_fecha().after(vigenFecha) ) {
			result = 1;
			return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
		}
		
		
		return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
	}
}
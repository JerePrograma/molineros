package ar.com.ospim.afiliados.action;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class ValidaFechaCertificaOpcionSSSAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		int respuesta = 0;
		Date fechaElec = ParamUtil.getDate(req, "fecha_eleccion", DateFormat.getDateInstance(DateFormat.SHORT));
		Date fechaCertif = ParamUtil.getDate(req, "fecha_certi", DateFormat.getDateInstance(DateFormat.SHORT));
		
		respuesta =  DateUtils.calculaDiasHabilesEntreFechas(fechaCertif, fechaElec, true, null); 

		return "{ \"validado\" : \"" + String.valueOf(respuesta) + "\"}";
	}
}
package ar.com.ospim.correspondencia.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;

import com.liferay.portal.struts.JSONAction;

public class VerificaFCPrestadorDuplicadaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		boolean result = false;
		int respuesta = 0;

		String cuitPrestador = req.getParameter("cuitPrestador");
		String idPtoVenta = req.getParameter("idPtoVenta");
		String tipoComprobante = req.getParameter("tipoComp");
		String letraComprobante = req.getParameter("letraComp");
		String nroComprobante = req.getParameter("nroComp");
		String sucuComprobante = req.getParameter("sucuComp");

		
		result = CorrespondenciaServiceUtil.buscarFCPrestadorDuplicado(cuitPrestador, Integer.parseInt(idPtoVenta), tipoComprobante, 
				letraComprobante, nroComprobante, Integer.parseInt(sucuComprobante));
		
		if(result){
			respuesta=1;
		}
	
		return "{ \"verificado\" : \"" + String.valueOf(respuesta) + "\"}";
	}
}
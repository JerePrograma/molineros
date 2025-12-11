package ar.com.ospim.liquidaciones.comprobantes.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class BuscarUltimoComprobanteAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		int entidad= ParamUtil.getInteger(req, "entidad");		
			
		String tipo = req.getParameter("tipo");
		String cuit = req.getParameter("cuit");
		String sucu = req.getParameter("sucursal");

		return "{ \"numero\" : \"" + obtenerNumero(tipo, cuit, sucu, entidad)
				+ "\"}";
	}

	public static String obtenerNumero(String tipo, String cuit, String sucu,
			int entidad) throws SystemException {

		String numero = ComprobanteServiceUtil.getUltimoNumeroComprobante(tipo,
				cuit, sucu, entidad);

		return numero;
	}

}

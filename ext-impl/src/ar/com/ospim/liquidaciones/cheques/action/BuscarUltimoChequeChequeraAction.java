package ar.com.ospim.liquidaciones.cheques.action;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.Pago;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.beans.Chequera;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class BuscarUltimoChequeChequeraAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		int entidad=ParamUtil.getInteger(req, "entidad");
		List<Chequera> chequeras=ChequeServiceUtil.getUltimasChequeras(entidad);
		
		String id = req.getParameter("id_cta_bcria");
		Integer idCta= Integer.parseInt(id);
		String numero = null;
		for(Chequera c:chequeras) {
		   if( 	c.getId_cuenta()==idCta) {
			  numero= String.valueOf(c.getNroHasta());
			  break;
		   }
		}
		
		if (numero == null) {
			numero = "0";
		}
		return "{ \"numero\" : \"" + numero + "\"}";
	}

}

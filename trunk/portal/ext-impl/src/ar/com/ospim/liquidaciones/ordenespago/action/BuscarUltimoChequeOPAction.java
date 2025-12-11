package ar.com.ospim.liquidaciones.ordenespago.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.Pago;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class BuscarUltimoChequeOPAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		
		
		int entidad=ParamUtil.getInteger(req, "entidad");
		
		String id = req.getParameter("id_cta_bcria");

		OrdenPago op = (OrdenPago) req.getSession().getAttribute(
				WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		String numero = null;
		int idInt = Integer.parseInt(id);
		if (op != null && op.getFormaPago() != null && op.getFormaPago().size() > 0) {
			for (OrdenPago.FormaPago fp : op.getFormaPago()) {
				Pago p = fp.getPago();
				if ((p instanceof Cheque) && p.getCuentaBancaria() != null) {
					if (p.getCuentaBancaria().getId_cuenta_bcria() == idInt) {
						numero = p.getNumeroStr();
					}
				}
			}
		}
		if (numero == null) {
			numero = OrdenPagoServiceUtil.getUltimoNumeroChequeOP(idInt, entidad)
					.toString();
		}
		return "{ \"numero\" : \"" + numero + "\"}";
	}

}

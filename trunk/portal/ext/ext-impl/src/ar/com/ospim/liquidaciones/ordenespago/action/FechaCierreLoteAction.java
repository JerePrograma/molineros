package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class FechaCierreLoteAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int dia_baja = ParamUtil.getInteger(renderRequest, "baja_dia");
		int mes_baja = ParamUtil.getInteger(renderRequest, "baja_mes");
		int anio_baja = ParamUtil.getInteger(renderRequest, "baja_anio");
		
		Date baja_fecha=null;
		if (0 != dia_baja && 0 != anio_baja) {
			baja_fecha = new GregorianCalendar(anio_baja, mes_baja, dia_baja).getTime();			
		}
		
		User user = PortalUtil.getUser(renderRequest);
				
		OrdenPagoServiceUtil.setFechaFirmaLoteOrdenPago(baja_fecha, user.getScreenName());

		return mapping.findForward(getForward(renderRequest,"portlet.liquidaciones.divlote"));
	}

	private List<Cheque> getChequesParaReutilizar(RenderRequest renderRequest,
			List<CuentaBancaria> ctasBcrias, int cantidad_cheques) {
		List<Cheque> cheques = new ArrayList<Cheque>();
		String key = "utilizar_cheque_";
		for (int i = 0; i < cantidad_cheques; i++) {
			if (renderRequest.getParameter(key + i) != null) {
				String aBorrar = renderRequest.getParameter(key + i);
				String numeroCheque = aBorrar.substring(16,
						aBorrar.indexOf("_", 16));
				String idCta = aBorrar.substring(aBorrar.indexOf("_", 16) + 1,
						aBorrar.length());
				int indexOf = ctasBcrias.indexOf(new CuentaBancaria(Integer
						.valueOf(idCta)));
				cheques.add(new Cheque(new BigDecimal(numeroCheque), ctasBcrias
						.get(indexOf).getBanco().getId_banco()));
			}
		}
		return cheques;
	}

}

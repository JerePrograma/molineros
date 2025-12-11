package ar.com.uoma.facturacion.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarReciboAdelantoFacturaAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarReciboAdelantoFacturaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando Recibo a factura");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Factura factura = (Factura) session.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
		
		String sucursal= ParamUtil.getString(renderRequest, "sucursal");
		String numero= ParamUtil.getString(renderRequest, "nro");
		List<Recibo>recibos = new ArrayList<Recibo>();
		for(Recibo r:factura.getRecibosAdelantos()) {
			if(r.getSucursal()!=sucursal && r.getNumero()!=Long.parseLong(numero)) {
				recibos.add(r);
			}
		}
		factura.setRecibosAdelantos(recibos);
		session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION , factura);
				
		logger.debug("Saliendo de sacar Recibo a factura");
		return mapping.findForward("portlet.uoma.facturacion_adelantos");
	}

	
}
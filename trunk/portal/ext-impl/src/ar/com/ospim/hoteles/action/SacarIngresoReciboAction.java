package ar.com.ospim.hoteles.action;

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
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarIngresoReciboAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarIngresoReciboAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
//		logger.debug("Sacando cheque a factura");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Recibo recibo = (Recibo) session.getAttribute(WebKeysHoteles.RECIBO_EN_EDICION);

		List<FacturaIngreso> list = recibo.getIngresos();
		if (list == null) {
			list = new ArrayList<FacturaIngreso>();
		}

		removeIngreso(renderRequest, recibo);

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		Double total=recibo.totalIngresos();
		recibo.setTotal(total);
		session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION, recibo);
		
//		logger.debug("Saliendo de sacar cheque a factura");
		return mapping.findForward("portlet.hoteles.recibo.ingresos.view");
	}

	private void removeIngreso(RenderRequest renderRequest, Recibo recibo) {
		String nro = renderRequest.getParameter("nro");
		if(nro.trim().equals("")){
			nro=null;
		}
		String tipo = renderRequest.getParameter("tipo");
		String idBanco = renderRequest.getParameter("id_banco");
		String importeStr = renderRequest.getParameter("importe");
		String idCtaBcria = renderRequest.getParameter("id_cta_bcria");
		
		BigDecimal importe = new BigDecimal(importeStr);

		if (tipo.equals(Cheque.class.getName())) {
			Banco b = new Banco(Integer.parseInt(idBanco));
			CuentaBancaria cb = new  CuentaBancaria(Integer.parseInt(idCtaBcria));
			Cheque cheque = new Cheque(new BigDecimal(nro), Integer.parseInt(idBanco));
			cb.setBanco(b);
			cheque.setCuentaBancaria(cb);
			recibo.getIngresos().remove(new FacturaIngreso(cheque));
			
		} else if (tipo.equals(DepositoBancario.class.getName())) {
			List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil.getCtasBcrias(renderRequest);
			int index = ctasBcrias.indexOf(new CuentaBancaria(Integer.parseInt(idCtaBcria)));
			DepositoBancario deposito = new DepositoBancario(nro, ctasBcrias.get(index));
			recibo.getIngresos().remove(new FacturaIngreso(deposito));
			
		} else if (tipo.equals(Efectivo.class.getName())) {
			recibo.getIngresos().remove(new FacturaIngreso(new Efectivo(importe)));
			
		} else if (tipo.equals(Pagare.class.getName())) {
			recibo.getIngresos().remove(new FacturaIngreso(new Pagare(importe)));
		} else if (tipo.equals(TarjetaDebitoCredito.class.getName())) {
			Banco b = new Banco(Integer.parseInt(idBanco));
			TarjetaDebitoCredito tarjeta = new TarjetaDebitoCredito();
			tarjeta.setBanco(b);
			tarjeta.setImporte(importe);
			recibo.getIngresos().remove(new FacturaIngreso(tarjeta));
		}

	}
}
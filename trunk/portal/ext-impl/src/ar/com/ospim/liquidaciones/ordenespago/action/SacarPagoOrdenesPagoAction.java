package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.Caja;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.PagoBancario;
import ar.com.ospim.global.beans.PagoSinSalidaDeFondos;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.beans.RetencionIIBB;
import ar.com.ospim.global.beans.RetencionIVA;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarPagoOrdenesPagoAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		OrdenPago ordenPago = (OrdenPago) session
				.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);

		String importe = renderRequest.getParameter("importe_pago");
		String tipo_ingreso = renderRequest.getParameter("tipo");
		String idCtaBcria = renderRequest.getParameter("id_cta_bcria");
		String nro = renderRequest.getParameter("nro");
		String cuit = renderRequest.getParameter("cuit");
		String jurisdiccion = renderRequest.getParameter("jurisdiccion");

		BigDecimal importeBigD = null;
		if (StringUtils.checkNotEmpty(importe)) {
			importeBigD = new BigDecimal(importe);
		}

		CuentaBancaria cta = new CuentaBancaria(Integer.parseInt(idCtaBcria));
		int indexOf = ctasBcrias.indexOf(cta);
		if (indexOf != -1) {
			cta = ctasBcrias.get(indexOf);
		}

		if (tipo_ingreso.equals(Cheque.class.getSimpleName())) {
//			Cheque cheque = new Cheque(new BigDecimal(nro), cta.getBanco()
//					.getId_banco());
			if(cuit!=null && "null".equals(cuit) ) cuit=null;
			
			Cheque cheque = new Cheque(cuit, new BigDecimal(nro), cta, cta.getBanco());
			
			list.remove(new OrdenPago.FormaPago(cheque));
		} else if (tipo_ingreso
				.equals(RetencionGanancias.class.getSimpleName())) {
			RetencionGanancias ret = new RetencionGanancias();
			ret.setCuentaBancaria(cta);
			ret.setImporte(importeBigD);
			list.remove(new OrdenPago.FormaPago(ret));
		} else if (tipo_ingreso.equals(Anticipo.class.getSimpleName())) {
			Iterator<OrdenPago.FormaPago> it = list.iterator();
			while (it.hasNext()) {
				OrdenPago.FormaPago fp = it.next();
				if (fp.getNumeroStr().equals(nro)) {
					it.remove();
				}
			}
		} else if (tipo_ingreso.startsWith(PagoBancario.class.getSimpleName())) {
			PagoBancario pago = new PagoBancario();
			pago.setCuentaBancaria(cta);
			pago.setImporte(importeBigD);
			if (StringUtils.checkNotEmpty(nro)) {
				pago.setNumero(nro);
			}
			pago.setTipo_pago(Integer.parseInt(tipo_ingreso.replace(
					PagoBancario.class.getSimpleName(), "")));
			list.remove(new OrdenPago.FormaPago(pago));
		} else if(tipo_ingreso.startsWith(Caja.class.getSimpleName())){
			Iterator<OrdenPago.FormaPago> it = list.iterator();
			while (it.hasNext()) {
				OrdenPago.FormaPago fp = it.next();
				if (fp.getImporte().equals(importeBigD)) {
					it.remove();
				}
			}
			
		} else if (tipo_ingreso
				.equals(RetencionIIBB.class.getSimpleName())) {
			RetencionIIBB ret = new RetencionIIBB();
			ret.setCuentaBancaria(cta);
			ret.setImporte(importeBigD);
			try {
			  ret.setJurisdiccion(Integer.parseInt(jurisdiccion));
			} catch(Exception e) {
				ret.setJurisdiccion(0); 	
			}
			list.remove(new OrdenPago.FormaPago(ret));
		}else if (tipo_ingreso
				.equals(RetencionIVA.class.getSimpleName())) {
			RetencionIVA ret = new RetencionIVA();
			ret.setCuentaBancaria(cta);
			ret.setImporte(importeBigD);
			list.remove(new OrdenPago.FormaPago(ret));
		}else if(tipo_ingreso.startsWith(PagoSinSalidaDeFondos.class.getSimpleName())){
			Iterator<OrdenPago.FormaPago> it = list.iterator();
			while (it.hasNext()) {
				OrdenPago.FormaPago fp = it.next();
				if (fp.getImporte().equals(importeBigD) && fp.getTipo().equals(PagoSinSalidaDeFondos.class.getSimpleName())  ) {
					it.remove();
				}
			}
			
		}
		
		if (tipo_ingreso != null
				&& tipo_ingreso.equals(Anticipo.class.getSimpleName())) {
			return mapping
					.findForward(getForward(renderRequest,
							"portlet.liquidaciones.ordenes_pago.anticipos.result.search"));
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.ordenes_pago.pagos.result.search"));
	}

}

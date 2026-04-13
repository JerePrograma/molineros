package ar.com.ospim.tesoreria.actas.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.tesoreria.ErrorAlSacarChequeException;
import ar.com.ospim.tesoreria.EstadoChequeInvalidoException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarIngresoActaAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(SacarIngresoActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando ingreso a acta");
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

		List<ActaPago> list = acta.getPagos();
		if (list == null) {
			list = new ArrayList<ActaPago>();
		}

		String cuitEmisor = renderRequest.getParameter("cuitEmisor");
		String idCtaBancaria = renderRequest.getParameter("idCtaBcria");
		String nroCheque = renderRequest.getParameter("cheque_nro");
		String idBanco = renderRequest.getParameter("id_banco");
		String idActaPago = renderRequest.getParameter("id_acta_pago");
		String tipo = renderRequest.getParameter("forma");
		String importeStr = renderRequest.getParameter("importe");
		BigDecimal importe = new BigDecimal(importeStr);
		String fechaStr = renderRequest.getParameter("fecha");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = formatter.parse(fechaStr);

		ActaPago ap = new ActaPago();

		try {

			if (tipo.equals(Cheque.class.getName())) {
				Banco b = null;
				b = new Banco(Integer.parseInt(idBanco));
				CuentaBancaria cb = new CuentaBancaria(Integer.parseInt(idCtaBancaria), "");
				cb.setBanco(b);
				
				Cheque cheque = new Cheque();
				cheque.setNumero(new BigDecimal(nroCheque));
				cheque.setCuit(cuitEmisor);
				cheque.setCuentaBancaria(cb);

				ap.setIngreso(cheque);
				if (!StringUtils.isBlank(idActaPago)) {
					ap.setId(Integer.parseInt(idActaPago));
				}
				Cheque cheques = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cheque, entidad);
				if (cheques == null && acta !=null && acta.getId() > 0) {
					throw new ErrorAlSacarChequeException();
				}
				if (cheque.getEstado()!=null && cheque.getEstado().getId() != Cheque.Estado.CARGADO) {
					throw new EstadoChequeInvalidoException();
				}
			} else if (tipo.equals(DepositoBancario.class.getName())) {
				DepositoBancario depo = new DepositoBancario();
				depo.setImporte(importe);
				depo.setFecha(fecha);
				ap.setIngreso(depo);
			} else if (tipo.equals(Efectivo.class.getName())) {
				Efectivo efectivo = new Efectivo(importe);
				efectivo.setFecha(fecha);
				ap.setIngreso(efectivo);
			}

			removeActaPagoFromList(list, ap);

		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		
		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		logger.debug("Saliendo de sacar ingreso a acta");
		return mapping.findForward("portlet.tesoreria.actas.cheques.view");
	}

	private void removeActaPagoFromList(List<ActaPago> list, ActaPago ap) {
		Iterator<ActaPago> it = list.iterator();
		while (it.hasNext()) {
			ActaPago aPagoEnLista = it.next();
			if (ap.getIngreso() != null && aPagoEnLista.getIngreso() != null) {
				if ((ap.getIngreso() instanceof Cheque)) {
					if (aPagoEnLista.getIngreso().equals(ap.getIngreso())) {
						remove(it, aPagoEnLista);
					}
				} else {
					Ingreso ing = aPagoEnLista.getIngreso();
					if (ing.getFecha().equals(ap.getIngreso().getFecha())
							&& ing.getImporte().equals(
									ap.getIngreso().getImporte())) {
						remove(it, aPagoEnLista);
					}
				}
			}
		}
	}

	private void remove(Iterator<ActaPago> it, ActaPago aPagoEnLista) {
		if (aPagoEnLista.getId() != 0) {
			aPagoEnLista.setBorradoLogico(true);
		} else {
			it.remove();
		}
	}
}

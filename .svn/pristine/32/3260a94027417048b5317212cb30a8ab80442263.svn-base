package ar.com.ospim.tesoreria.recibos.action;

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

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboActa;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ABMReciboActasAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ABMReciboActasAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");

		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

		if (recibo == null) {
			recibo = new Recibo();
		}
		if (recibo.getActas() == null) {
			recibo.setActas(new ArrayList<ReciboActa>());
		}

		if (recibo.getIngresos() == null) {
			recibo.setIngresos(new ArrayList<ReciboIngreso>());
		}

		String borrar = renderRequest.getParameter("borrar");
		if (borrar != null && borrar.equals("borrar")) {
			borrarActa(renderRequest, recibo, entidad);
		} else {
			agregarActa(renderRequest, recibo, entidad);
		}

		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		return mapping
				.findForward("portlet.tesoreria.recibos.recibo_actas.result.search");
	}

	private void agregarActa(RenderRequest renderRequest, Recibo recibo,
			int entidad) throws Exception {
		obtenerDatosActas(renderRequest, recibo);

		String cuit = ParamUtil.getString(renderRequest, "cuit");
		List<Acta> actas = null;
		if (entidad != WebKeysGlobal.OSPIM) {
			actas = ActaNoOSServiceUtil.getActasSinRecibo(cuit, entidad);
		} else {
			actas = ActaServiceUtil.getActasSinRecibo(cuit);
		}
		if (actas != null) {
			for (Acta acta : actas) {
				if (!recibo.getActas().contains(new ReciboActa(acta))) {
					Acta actaConInfo = null;
					if (entidad != WebKeysGlobal.OSPIM) {
						actaConInfo = ActaNoOSServiceUtil.getActa(acta.getId(), recibo.getId());
					} else {
						actaConInfo = ActaServiceUtil.getActa(acta.getId(),recibo.getId());
					}

					for (ActaPago ap : actaConInfo.getPagos()) {
						Ingreso ingreso = ap.getIngreso();
						if (ap.getTipo().equals(ActaPago.Tipo.PAGO)
								&& ingreso != null
								&& ((ingreso instanceof Cheque) || (ingreso instanceof Pagare))
								&& ap.getRecibo() == null) {
							ingreso.setActaId(ap.getActaId());
							if (ingreso instanceof Cheque
									&& (null == actaConInfo
											.getPagosIngresados() || !actaConInfo
											.containsChequeIngresado((Cheque) ingreso))) {
								((Cheque) ingreso)
										.setEstado(TraeListasServiceUtil
												.getEstadoChequeRecibido(renderRequest));
								ReciboIngreso ring = new ReciboIngreso((Cheque)ingreso);								
								recibo.getIngresos().add(ring);
							} else if (!(ingreso instanceof Cheque)) {
								recibo.getIngresos().add(
										new ReciboIngreso(ingreso));
							}

						}
					}
					recibo.getActas().add(
							new ReciboActa(actaConInfo, actaConInfo
									.getTotalActaPagosChequeNoIngresados()));
				}
			}
		}
		recibo.setEmpresa(new Empresa(cuit));
	}

	private void borrarActa(RenderRequest renderRequest, Recibo recibo,
			int entidad) {
		obtenerDatosActas(renderRequest, recibo);

		int indexOf = recibo.getActas().indexOf(
				new ReciboActa(new Acta(Integer.parseInt(renderRequest
						.getParameter("acta_id")))));
		ReciboActa reciboActa = recibo.getActas().get(indexOf);
		Acta acta = reciboActa.getActa();
		if (acta.getPagos() != null) {
			for (ActaPago ap : acta.getPagos()) {
				if (ap.getTipo().equals(ActaPago.Tipo.PAGO)
						&& ap.getIngreso() != null
						&& (ap.getIngreso() instanceof Cheque)) {
					recibo.getIngresos().remove(
							new ReciboIngreso(ap.getIngreso()));
				}
			}
		}
		recibo.getActas().remove(reciboActa);
	}

	private void obtenerDatosActas(RenderRequest renderRequest, Recibo recibo) {
		if (recibo.getActas() != null) {
			for (ReciboActa reciboActa : recibo.getActas()) {
				String adicionalStr = renderRequest.getParameter("acta_"
						+ reciboActa.getActa().getId());
				BigDecimal adicional = BigDecimal.ZERO;
				if (StringUtils.checkNotEmpty(adicionalStr)) {
					adicional = new BigDecimal(adicionalStr);
				}
				reciboActa.setImporteAdicional(adicional);
			}
		}
	}
}

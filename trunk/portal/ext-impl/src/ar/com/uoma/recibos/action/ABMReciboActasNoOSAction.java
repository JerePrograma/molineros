package ar.com.uoma.recibos.action;

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
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Ingreso;
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

public class ABMReciboActasNoOSAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ABMReciboActasNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");

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
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}		
		
		if (borrar != null && borrar.equals("borrar")) {
			borrarActa(renderRequest, recibo);
		} else {
			agregarActa(renderRequest, recibo, entidad);
		}

		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		return mapping
				.findForward("portlet.estudio_isidro.recibos_no_os.recibo_actas.result.search");
	}

	private void agregarActa(RenderRequest renderRequest, Recibo recibo, int entidad) throws Exception {
		obtenerDatosActas(renderRequest, recibo);
		List<Acta> actas = null;
		String cuit = ParamUtil.getString(renderRequest, "cuit");
		if(entidad==WebKeysGlobal.OSPIM){
			actas = ActaServiceUtil.getActasSinRecibo(cuit);
		}else{
			actas = ActaNoOSServiceUtil.getActasSinRecibo(cuit, entidad);
		}
		if (actas != null) {
			for (Acta acta : actas) {
				if (!recibo.getActas().contains(new ReciboActa(acta))) {
					Acta actaConInfo =null;
					if(entidad==WebKeysGlobal.OSPIM){
						actaConInfo = ActaServiceUtil.getActa(acta.getId(),recibo.getId());
					}else{
						actaConInfo = ActaNoOSServiceUtil.getActa(acta.getId(), recibo.getId());
					}
					for (ActaPago ap : actaConInfo.getPagos()) {
						Ingreso ingreso = ap.getIngreso();
						if (ap.getTipo().equals(ActaPago.Tipo.PAGO)
								&& ingreso != null
								&& (ingreso instanceof Cheque)
								&& ap.getRecibo() == null) {
							((Cheque) ingreso).setEstado(TraeListasServiceUtil
									.getEstadoChequeRecibido(renderRequest));
							recibo.getIngresos()
									.add(new ReciboIngreso(ingreso));
						}else if(ap.getTipo().equals(ActaPago.Tipo.PAGO) && ingreso != null&& ap.getRecibo() == null && !(ingreso instanceof DepositoBancario)){
							recibo.getIngresos().add(new ReciboIngreso(ingreso));
						}
					}
					recibo.getActas().add(
							new ReciboActa(actaConInfo, actaConInfo
									.getTotalActaPagosIngresados()));
				}
			}
		}
		recibo.setEmpresa(new Empresa(cuit));
	}

	private void borrarActa(RenderRequest renderRequest, Recibo recibo) {
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

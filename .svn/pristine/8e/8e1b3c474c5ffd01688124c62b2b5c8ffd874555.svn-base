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
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboConvenio;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ABMReciboNoOSConveniosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMReciboNoOSConveniosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
		
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}		

		if (recibo == null) {
			recibo = new Recibo();
		}
		if (recibo.getConvenios() == null) {
			recibo.setConvenios(new ArrayList<ReciboConvenio>());
		}

		if (recibo.getIngresos() == null) {
			recibo.setIngresos(new ArrayList<ReciboIngreso>());
		}

		String borrar = renderRequest.getParameter("borrar");
		

		if (borrar != null && borrar.equals("borrar")) {
			borrarConvenio(renderRequest, recibo);
		} else {
			agregarConvenio(renderRequest, recibo, entidad);
		}

		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);

		return mapping
				.findForward("portlet.estudio_isidro.recibos_no_os.recibo_convenios.result.search");
	}

	private void borrarConvenio(RenderRequest renderRequest, Recibo recibo) {
		obtenerDatosConvenios(renderRequest, recibo);

		int indexOf = recibo.getConvenios().indexOf(
				new ReciboConvenio(new Convenio(Integer.parseInt(renderRequest
						.getParameter("convenio_id")))));
		ReciboConvenio reciboConvenio = recibo.getConvenios().get(indexOf);
		recibo.getConvenios().remove(reciboConvenio);

		if (recibo.getIngresos() != null) {
			if (reciboConvenio.getConvenio().getPagos() != null) {
				for (ConvenioPago ap : reciboConvenio.getConvenio().getPagos()) {
					if (ap.getTipo().equals(ConvenioPago.Tipo.PAGO)
							&& ap.getCheque() != null) {
						recibo.getIngresos().remove(
								new ReciboIngreso(ap.getCheque()));
					}
				}
			}
		}
	}

	private void agregarConvenio(RenderRequest renderRequest, Recibo recibo,
			int entidad) {
		obtenerDatosConvenios(renderRequest, recibo);

		String cuit = ParamUtil.getString(renderRequest, "cuit");
		List<Convenio> convenios = null;
		if (entidad==WebKeysGlobal.OSPIM) {
			convenios = ConvenioServiceUtil.getConveniosSinRecibo(cuit);
		} else {
			convenios = ConvenioNoOSServiceUtil.getConveniosSinRecibo(cuit,
					entidad);
		}
		List<ReciboConvenio> conveniosRecibo = recibo.getConvenios();
		if (convenios != null) {
			for (Convenio convenio : convenios) {
				Convenio covenioConInfo = null;
				if (entidad==WebKeysGlobal.OSPIM) {
					covenioConInfo = ConvenioServiceUtil.getConvenio(convenio
							.getId(), recibo.getId());
				} else {
					covenioConInfo = ConvenioNoOSServiceUtil
							.getConvenio(convenio.getId(), recibo.getId(), entidad);
				}
				if (!conveniosRecibo.contains(new ReciboConvenio(convenio))) {
					conveniosRecibo
							.add(new ReciboConvenio(
									covenioConInfo,
									covenioConInfo
											.getTotalConvenioPagosChequeNoIngresados()));
				}
				for (ConvenioPago cp : covenioConInfo.getPagos()) {
					if (cp.getTipo().equals(ConvenioPago.Tipo.PAGO)
							&& cp.getRecibo() == null) {
						Cheque cheque = cp.getCheque();
						if (cheque != null
								&& !recibo.getIngresos().contains(cheque)) {
							cheque.setEstado(TraeListasServiceUtil
									.getEstadoChequeRecibido(renderRequest));
							recibo.getIngresos().add(new ReciboIngreso(cheque));
						}
					}
				}
			}
		}
		recibo.setEmpresa(new Empresa(cuit));
	}

	private void obtenerDatosConvenios(RenderRequest renderRequest,
			Recibo recibo) {
		if (recibo.getConvenios() != null) {
			for (ReciboConvenio reciboConvenio : recibo.getConvenios()) {
				String adicionalStr = renderRequest.getParameter("convenio_"
						+ reciboConvenio.getConvenio().getId());
				BigDecimal adicional = BigDecimal.ZERO;
				if (StringUtils.checkNotEmpty(adicionalStr)) {
					adicional = new BigDecimal(adicionalStr);
				}
				reciboConvenio.setImporteAdicional(adicional);
			}
		}
	}

}

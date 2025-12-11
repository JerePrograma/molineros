package ar.com.uoma.conveniosNoOS.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarPagoConvenioNoOSAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarPagoConvenioNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		logger.debug("Agregando cheque a convenio no os");

//		String portlet_name = null;
		int entidad = 0;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
//			portlet_name = "farmacia";
			entidad = WebKeysGlobal.AMTIMA;
		}else if (renderResponse.getNamespace().equals("_UOM_1_")) {
//			portlet_name = "uoma";
			entidad = WebKeysGlobal.UOMA;
		}else if (renderResponse.getNamespace().equals("_EST_1_")) {
//			portlet_name = "estudio";
			entidad = WebKeysGlobal.ESTUDIO;
		}
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Convenio convenio = (Convenio) session
				.getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		if (convenio == null) {
			convenio = new Convenio();
			session.setAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION, convenio);
		}

		List<ConvenioPago> list = convenio.getPagos();
		if (list == null) {
			list = new ArrayList<ConvenioPago>();
			convenio.setPagos(list);
		}

		ConvenioPago convenioPago = getConvenioPago(renderRequest, entidad);
		String tipo_pago = renderRequest.getParameter("tipo_pago");
		if (tipo_pago.equals("cheque") && !existeCuota(list, convenioPago)) {
			list.add(convenioPago);
		} else if (tipo_pago.equals("pagare") && !existeCuotaDeposito(list, convenioPago)){
			list.add(convenioPago);
		} else if (tipo_pago.equals("depostio")
				&& !existeCuotaDeposito(list, convenioPago)) {
			list.add(convenioPago);
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		Collections.sort(list, new Comparator<ConvenioPago>() {

			public int compare(ConvenioPago o1, ConvenioPago o2) {
				if (o1.getNroCuota() < o2.getNroCuota()) {
					return -1;
				} else if (o1.getNroCuota() > o2.getNroCuota()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		logger.debug("Saliendo de agregar cheque a convenio no os");
		return mapping.findForward("portlet.estudio_isidro.convenios_no_os.pagos.view");
	}

	private boolean existeCuota(List<ConvenioPago> list,
			ConvenioPago convenioPago) {
		for (ConvenioPago cp : list) {
			if (cp.getNroCuota() == convenioPago.getNroCuota()
					|| (cp.getCheque() != null && cp.getCheque().getNumero()
							.equals(convenioPago.getCheque().getNumero()))) {
				return true;
			}
		}
		return false;
	}

	private ConvenioPago getConvenioPago(RenderRequest renderRequest, int entidad)
			throws ParseException, DuplicateNumeroChequeException, SystemException {
		
		ConvenioPago convenioPago = new ConvenioPago();
		convenioPago.setTipo(ConvenioPago.Tipo.PAGO);

		int cuotaNro = ParamUtil.getInteger(renderRequest, "cuota_nro_cheque");
		String capital = renderRequest.getParameter("capital_cheque");
		String tipo_pago = renderRequest.getParameter("tipo_pago");
		String interes = renderRequest.getParameter("interes_cheque")!=null?renderRequest.getParameter("interes_cheque"):"";
		String nroCheque = renderRequest.getParameter("cheque_nro");
		String idBanco = renderRequest.getParameter("id_banco");
		String cuitEmisor = renderRequest.getParameter("cuitEmisor");
		String idCtaBancaria = renderRequest.getParameter("idCtaBcria");
		String ctaBancariaNueva = renderRequest.getParameter("ctaBcriaNueva");
		String ctaBancaria = renderRequest.getParameter("ctaBcria");
		String cuitTerceros = renderRequest.getParameter("cuitTerceros");
		String chequeTerceros = renderRequest.getParameter("esChequeTerceros");
		boolean esChequeTerceros = Boolean.parseBoolean(chequeTerceros); 
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaPagoDia = ParamUtil.getString(renderRequest,
				"fechaPagoDiaCheque");
		String fechaPagoMes = ParamUtil.getString(renderRequest,
				"fechaPagoMesCheque");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
		String fechaPagoAnio = ParamUtil.getString(renderRequest,
				"fechaPagoAnioCheque");
		Date fechaPago = format.parse(fechaPagoDia + "-" + fechaPagoMes + "-"
				+ fechaPagoAnio);

		BigDecimal importeBigD = new BigDecimal(capital);
		BigDecimal interesBigD = new BigDecimal(interes);
		if (tipo_pago.equals("cheque")) {
//			Cheque cheque = new Cheque(new BigDecimal(nroCheque),
//					Integer.parseInt(idBanco));
//			cheque.setImporte(importeBigD.add(interesBigD));
//			cheque.setEstado(TraeListasServiceUtil
//					.getEstadoChequeCargado(renderRequest));
//			cheque.setFecha(fechaPago);
//			convenioPago.setCheque(cheque);
			Banco b = null;
			CuentaBancaria cb = null;
			Cheque cheque = new Cheque(new BigDecimal(nroCheque),Integer.parseInt(idBanco));
			cheque.setImporte(importeBigD.add(interesBigD));
			cheque.setEstado(TraeListasServiceUtil.getEstadoChequeCargado(renderRequest));
			cheque.setDebitoCredito(Cheque.Tipo.CREDITO);
			cheque.setFecha(fechaPago);
			cheque.setCuit(esChequeTerceros?cuitTerceros:cuitEmisor);
			b = new Banco(Integer.parseInt(idBanco));
			
			if(!esChequeTerceros){
				cb = new CuentaBancaria(Integer.parseInt(idCtaBancaria), ctaBancaria);
			}else{
				cb = new CuentaBancaria(-1, ctaBancariaNueva);
			}
			cb.setBanco(b);
			cheque.setCuentaBancaria(cb);
			Cheque chequeDuplicado = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cheque, entidad);
			if (chequeDuplicado != null) {
				throw new DuplicateNumeroChequeException(chequeDuplicado);
			}
			convenioPago.setCheque(cheque);
		} else if (tipo_pago.equals("pagare")) {
			Pagare pagare = new Pagare(new BigDecimal("1"));
			pagare.setImporte(importeBigD.add(interesBigD));
			pagare.setEstado(TraeListasServiceUtil
					.getEstadoPagareCargado(renderRequest));
			pagare.setFecha(fechaPago);
			convenioPago.setPagare(pagare);
		}

		convenioPago.setImporte(importeBigD);
		convenioPago.setInteres(interesBigD);
		convenioPago.setFechaPago(fechaPago);
		convenioPago.setNroCuota(cuotaNro);
		return convenioPago;
	}

	private boolean existeCuotaDeposito(List<ConvenioPago> list,
			ConvenioPago convenioPago) {
		for (ConvenioPago cp : list) {
			if (cp.getNroCuota() == convenioPago.getNroCuota()) {
				return true;
			}
		}
		return false;
	}

}

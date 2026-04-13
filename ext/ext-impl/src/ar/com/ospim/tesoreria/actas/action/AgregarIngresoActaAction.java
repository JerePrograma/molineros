package ar.com.ospim.tesoreria.actas.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarIngresoActaAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarIngresoActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando ingreso a acta");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		List<ActaPago> list = acta.getPagos();
		if (list == null) {
			list = new ArrayList<ActaPago>();
			acta.setPagos(list);
		}
		try {
			list.add(getActaPago(renderRequest,renderResponse));
		} catch (DuplicateNumeroChequeException e) {
			renderRequest.setAttribute(EditarActasEntryAction.CHEQUE_DUPLICADO,
					e.getCheque());
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		logger.debug("Saliendo de agregar ingreso a acta");
		return mapping.findForward("portlet.tesoreria.actas.cheques.view");
	}

	private ActaPago getActaPago(RenderRequest renderRequest, RenderResponse renderResponse)
			throws ParseException, DuplicateNumeroChequeException,
			SystemException {
		
		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		
		ActaPago actaPago = new ActaPago();
		actaPago.setTipo(ActaPago.Tipo.PAGO);
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

		String importe = renderRequest.getParameter("importe");
		String nroCheque = renderRequest.getParameter("cheque_nro");
		String idBanco = renderRequest.getParameter("id_banco");
		String cuitEmisor = renderRequest.getParameter("cuitEmisor");
		String idCtaBancaria = renderRequest.getParameter("idCtaBcria");
		String ctaBancariaNueva = renderRequest.getParameter("ctaBcriaNueva");
		String ctaBancaria = renderRequest.getParameter("ctaBcria");
		String cuitTerceros = renderRequest.getParameter("cuitTerceros");
		String chequeTerceros = renderRequest.getParameter("esChequeTerceros");
		boolean esChequeTerceros = Boolean.parseBoolean(chequeTerceros); 

		actaPago.setFechaPago(fechaPago);
		String forma = renderRequest.getParameter("forma");
		BigDecimal importeBigD = new BigDecimal(importe);
		actaPago.setImporte(importeBigD);
		
		
		if (forma.equals(Cheque.class.getName())) {
			actaPago.setImporte(importeBigD);
			Cheque cheque = null;
			Banco b = null;
			CuentaBancaria cb = null;
			cheque = new Cheque(new BigDecimal(nroCheque),Integer.parseInt(idBanco));
			cheque.setImporte(importeBigD);
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
//			List<Cheque> cheques = ChequeServiceUtil.getCheques(cheque, WebKeysGlobal.OSPIM);
//			if (cheques != null && cheques.size() > 0) {
//				throw new DuplicateNumeroChequeException(cheque);
//			}
			Cheque chequeDuplicado = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cheque, entidad);
			if (chequeDuplicado != null) {
				throw new DuplicateNumeroChequeException(chequeDuplicado);
			}
			actaPago.setIngreso(cheque);
		} else if (forma.equals(DepositoBancario.class.getName())) {
			DepositoBancario depo = new DepositoBancario();
			depo.setImporte(importeBigD);
			depo.setFecha(fechaPago);
			actaPago.setIngreso(depo);
		} else if (forma.equals(Efectivo.class.getName())) {
			Efectivo ef = new Efectivo();
			ef.setImporte(importeBigD);
			ef.setFecha(fechaPago);
			actaPago.setIngreso(ef);
		}else if (forma.equals(Pagare.class.getName())) {
			Pagare ef = new Pagare();
			ef.setImporte(importeBigD);
			ef.setFecha(fechaPago);
			actaPago.setIngreso(ef);
		}
		return actaPago;
	}

}

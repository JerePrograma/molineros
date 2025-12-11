package ar.com.ospim.tesoreria.recibos.action;

import java.math.BigDecimal;
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
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.ReciboAnticipo;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarIngresoReciboAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarIngresoReciboAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando ingreso a recibo");
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		if (renderRequest.getParameter("reload") != null
				&& renderRequest.getParameter("reload").equals("reload")) {
			renderRequest.setAttribute("esEdicion", "true");
			return mapping
					.findForward("portlet.tesoreria.recibos.ingresos.view");
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
		if (recibo == null) {
			recibo = new Recibo();
			session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", renderRequest
					.getParameter("esEdicion"));
		}
		String ret = "portlet.tesoreria.recibos.ingresos.view";
		try {
			ret = getIngreso(renderRequest, recibo, entidad);
		} catch (DuplicateNumeroChequeException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		logger.debug("Saliendo de agregar ingreso a recibo");
		return mapping.findForward(ret);
	}

	private String getIngreso(RenderRequest renderRequest, Recibo recibo, int entidad)
			throws Exception {

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);

		String tipoIngreso = renderRequest.getParameter("tipo");
	
		logger.debug("tipo ingreso " + tipoIngreso);

		List<ReciboIngreso> list = recibo.getIngresos();
		if (list == null) {
			list = new ArrayList<ReciboIngreso>();
			recibo.setIngresos(list);
		}

		if (tipoIngreso.equals("Anticipo")) {
			String cuit = ParamUtil.getString(renderRequest, "cuit_empleador");
			String sucu = ParamUtil.getString(renderRequest, "sucursal_empleador");
			Empresa emp = new Empresa();
			emp.setCuit(cuit);
			emp.setSucursal(sucu);
			List<ReciboIngreso> ris = ReciboServiceUtil
					.getAnticiposParaAplicar(emp, entidad);
			if (ris != null) {
				for (ReciboIngreso ri : ris) {
					((ReciboAnticipo) ri.getIngreso())
							.setEstado(new Efectivo.Estado(
									Efectivo.Estado.ANTICIPO));
				}
			}
			list.addAll(ris);

			return "portlet.tesoreria.recibos.anticipos.view";
		}

		
		String aplicarA= ParamUtil.getString(renderRequest,"aplicar_a");
		int convenioId=0;
		int actaId=0;
		if(aplicarA!=null && !aplicarA.trim().equals("")){
			String[] acuerdos=aplicarA.split("_");
			if(acuerdos[1].equals("a")){
				actaId=Integer.valueOf(acuerdos[0]);
			}else if(acuerdos[1].equals("c")){
				convenioId=Integer.valueOf(acuerdos[0]);
			}
		}
		
		String importe = renderRequest.getParameter("importe");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaPagoDia = ParamUtil
				.getString(renderRequest, "fechaPagoDia");
		String fechaPagoMes = ParamUtil
				.getString(renderRequest, "fechaPagoMes");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
		String fechaPagoAnio = ParamUtil.getString(renderRequest,
				"fechaPagoAnio");
		Date fechaPago = format.parse(fechaPagoDia + "-" + fechaPagoMes + "-"
				+ fechaPagoAnio);
		if (StringUtils.checkEmpty(importe)) {
			importe = "0";
		}
		BigDecimal importeBigD = new BigDecimal(importe);

		if (tipoIngreso.equals(Cheque.class.getName())) {
			String nro = renderRequest.getParameter("nro");
			String idBanco = renderRequest.getParameter("id_banco");
			String idCtaBancaria = renderRequest.getParameter("idCtaBcriaCh");
			String ctaBancariaNueva = renderRequest.getParameter("ctaBcriaNuevaCh");
			String ctaBancaria = renderRequest.getParameter("ctaBcriaCh");
			String cuitEmisor = renderRequest.getParameter("cuitEmisor");
			String cuitTerceros = renderRequest.getParameter("cuitTerceros");
			String chequeTerceros = renderRequest.getParameter("esChequeTerceros");
			boolean esChequeTerceros = Boolean.parseBoolean(chequeTerceros); 
			
			Banco b = null;
			CuentaBancaria cb = null;
			Cheque cheque = new Cheque(new BigDecimal(nro), Integer.parseInt(idBanco));
			cheque.setImporte(importeBigD);
			cheque.setEstado(TraeListasServiceUtil.getEstadoChequeRecibido(renderRequest));
			cheque.setFecha(fechaPago);
			cheque.setDebitoCredito(Cheque.Tipo.CREDITO);
			cheque.setCuit(esChequeTerceros?cuitTerceros:cuitEmisor);
			b = new Banco(Integer.parseInt(idBanco));
			
			if(!esChequeTerceros){
				cb = new CuentaBancaria(Integer.parseInt(idCtaBancaria), ctaBancaria);
			}else{
				cb = new CuentaBancaria(-1, ctaBancariaNueva);
			}
			cb.setBanco(b);
			cheque.setCuentaBancaria(cb);
			
			if (!list.contains(cheque)) {
				Date baja_fecha = verificarCheque(cheque, entidad);
				cheque.setBaja_fecha(baja_fecha);
				list.add(new ReciboIngreso(cheque));
			}
		} else if (tipoIngreso.equals(Pagare.class.getName())) {
			String nro = renderRequest.getParameter("nro");		
			if(null==nro || nro.trim().equals("")){
				nro="1";
			}
			Pagare pagare = new Pagare(new BigDecimal(nro));
			pagare.setImporte(importeBigD);
			pagare.setFecha(fechaPago);
			if (!list.contains(pagare)) {				
				list.add(new ReciboIngreso(pagare));
			}
		} else if (tipoIngreso.equals(Efectivo.class.getName())
				|| tipoIngreso.equals("Redondeo")
				|| tipoIngreso.equals("AFIP") || tipoIngreso.equals("Quitas") || tipoIngreso.equals("Manuales")) {
			Efectivo ef = new Efectivo(importeBigD);
			ef.setFecha(fechaPago);
			if (tipoIngreso.equals(Efectivo.class.getName())) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.RECIBIDO));
			} else if (tipoIngreso.equals("Redondeo")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.REDONDEO));
			} else if (tipoIngreso.equals("AFIP")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.AFIP));
			} else if (tipoIngreso.equals("Quitas")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.QUITAS));
			} else if (tipoIngreso.equals("Manuales")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.MANUALES));
			}
			if (!list.contains(ef)) {
				list.add(new ReciboIngreso(ef));
			}
		} else if (tipoIngreso.startsWith("Deposito_Bancario_")) {
			int tipo = Integer.valueOf(tipoIngreso.substring(18));
			int sucuNacion=0;
			String nro = renderRequest.getParameter("nro");
			if(entidad==WebKeysGlobal.UOMA){
				sucuNacion= ParamUtil.getInteger(renderRequest,"sucursal_dpto");
			}
			String id_cta_bcria = renderRequest.getParameter("id_cta_bcria");
			int index = ctasBcrias.indexOf(new CuentaBancaria(Integer.parseInt(id_cta_bcria)));
			DepositoBancario depo = null;
			if(entidad==WebKeysGlobal.UOMA){
				depo =new DepositoBancario(nro, sucuNacion, ctasBcrias.get(index));
			}else{
				depo =new DepositoBancario(nro, ctasBcrias.get(index));
			}
			depo.setImporte(importeBigD);
			depo.setFecha(fechaPago);
			depo.setTipoDeposito(tipo);
			if (!list.contains(depo)) {
				list.add(new ReciboIngreso(depo));
			}
			if(convenioId>0){
				list.get(list.size()-1).setConvenioId(convenioId);
			}
			if(actaId>0){
				list.get(list.size()-1).setActaId(actaId);
			}
		}
		return "portlet.tesoreria.recibos.ingresos.view";

	}

	private Date verificarCheque(Cheque cheque, int entidad) throws SystemException,
			DuplicateNumeroChequeException {
		List<Cheque> cheques = ChequeServiceUtil.getCheques(cheque, entidad);
		if (cheques != null && cheques.size() > 0) {
			for (Cheque ch : cheques) {
				if (ch.getBaja_fecha() != null
						&& ch.getDebitoCredito().equals(Cheque.Tipo.CREDITO)) {
					return ch.getBaja_fecha();
				}/* else { POR AHORA NO SE VALIDA MAS SI EXISTE EL CHEQUE
					throw new DuplicateNumeroChequeException();
				}*/
			}
		}
		return null;
	}

}

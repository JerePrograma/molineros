package ar.com.ospim.hoteles.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaIngreso;

public class AgregarIngresoReciboAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarIngresoReciboAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
//		logger.debug("Agregando ingreso a Recibo");
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_HOT_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		if (renderRequest.getParameter("reload") != null
				&& renderRequest.getParameter("reload").equals("reload")) {
			renderRequest.setAttribute("esEdicion", "true");
			return mapping
					.findForward("portlet.hoteles.recibo.ingresos.view");
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Recibo recibo = (Recibo) session.getAttribute(WebKeysHoteles.RECIBO_EN_EDICION);

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", renderRequest.getParameter("esEdicion"));
		}
		String ret = "portlet.hoteles.recibo.ingresos.view";
		try {
			ret = getIngreso(renderRequest, recibo, entidad);
		} catch (DuplicateNumeroChequeException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		
		
//		logger.debug("Saliendo de agregar ingreso a recibo");
		return mapping.findForward(ret);
	}

	private String getIngreso(RenderRequest renderRequest, Recibo recibo, int entidad)
			throws Exception {

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);

		String tipo_ingreso = renderRequest.getParameter("tipo");
		

		List<FacturaIngreso> list = recibo.getIngresos();
		if (list == null) {
			list = new ArrayList<FacturaIngreso>();
			recibo.setIngresos(list);
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
		importeBigD.setScale(2, RoundingMode.HALF_EVEN);

		if (tipo_ingreso.equals(Cheque.class.getName())) {
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
				list.add(new FacturaIngreso(cheque));
			}
		} else if (tipo_ingreso.equals(Pagare.class.getName())) {
			String nro = renderRequest.getParameter("nro");		
			if(null==nro || nro.trim().equals("")){
				nro="1";
			}
			Pagare pagare = new Pagare(new BigDecimal(nro));
			pagare.setImporte(importeBigD);
			pagare.setFecha(fechaPago);
			if (!list.contains(pagare)) {				
				list.add(new FacturaIngreso(pagare));
			}
		} else if (tipo_ingreso.equals(Efectivo.class.getName())
				|| tipo_ingreso.equals("Redondeo")
				|| tipo_ingreso.equals("AFIP") || tipo_ingreso.equals("Quitas") || tipo_ingreso.equals("Manuales")) {
			Efectivo ef = new Efectivo(importeBigD);
			ef.setFecha(fechaPago);
			if (tipo_ingreso.equals(Efectivo.class.getName())) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.RECIBIDO));
			} else if (tipo_ingreso.equals("Redondeo")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.REDONDEO));
			} else if (tipo_ingreso.equals("AFIP")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.AFIP));
			} else if (tipo_ingreso.equals("Quitas")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.QUITAS));
			} else if (tipo_ingreso.equals("Manuales")) {
				ef.setEstado(new Efectivo.Estado(Efectivo.Estado.MANUALES));
			}
			if (!list.contains(ef)) {
				list.add(new FacturaIngreso(ef));
			}
		} else if (tipo_ingreso.startsWith("Deposito_Bancario_")) {
			int tipo = Integer.valueOf(tipo_ingreso.substring(18));
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
				list.add(new FacturaIngreso(depo));
			}
			if(convenioId>0){
				list.get(list.size()-1).setConvenioId(convenioId);
			}
			if(actaId>0){
				list.get(list.size()-1).setActaId(actaId);
			}
		} else if (tipo_ingreso.startsWith(TarjetaDebitoCredito.class.getName())) {
			int tipo = Integer.valueOf(tipo_ingreso.replace(TarjetaDebitoCredito.class.getName(),""));
			int sucuNacion=0;
			
			String idBanco = renderRequest.getParameter("id_banco");
			String nro = renderRequest.getParameter("nro");
			String idEmisor = renderRequest.getParameter("id_emisor_tarjeta");
			String desEmisor = renderRequest.getParameter("des_emisor_tarjeta");
			String cuotas = renderRequest.getParameter("cuotas_tarjeta");
			TarjetaDebitoCredito tarjeta = null;
			tarjeta = new TarjetaDebitoCredito(Integer.parseInt(idEmisor), new Banco(Integer.parseInt(idBanco)),fechaPago,nro,
					Integer.parseInt(cuotas),importeBigD);
			tarjeta.setEmisorDescripcion(desEmisor);
			tarjeta.setTipo(tipo);
			if (!list.contains(tarjeta)) {
				list.add(new FacturaIngreso(tarjeta));
			}
					 
			 
		}
		
		Double total=recibo.totalIngresos();
		recibo.setTotal(total);
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION, recibo);
		
		return "portlet.hoteles.recibo.ingresos.view";

	}

	private Date verificarCheque(Cheque cheque, int entidad) throws SystemException,
			DuplicateNumeroChequeException {
		List<Cheque> cheques = ChequeServiceUtil.getCheques(cheque, entidad);
		if (cheques != null && cheques.size() > 0) {
			for (Cheque ch : cheques) {
				if (ch.getBaja_fecha() != null
						&& ch.getDebitoCredito().equals(Cheque.Tipo.CREDITO)) {
					return ch.getBaja_fecha();
				}
			}
		}
		return null;
	}

}

package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.actions.BuscarComprobanteEmbebidoAction;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.action.LiquidacionActionUtil;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarOrdenPagoOspimFromLiquidacion extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EditarOrdenPagoOspimFromLiquidacion.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		try {
			int entidad = WebKeysGlobal.OSPIM;
			if (actionResponse.getNamespace().equals("_FAR_1_")) {
				entidad = WebKeysGlobal.AMTIMA;
			} else if (actionResponse.getNamespace().equals("_UOM_1_")) {
				entidad = WebKeysGlobal.UOMA;
			}
			HttpSession session = PortalUtil.getHttpServletRequest(
					actionRequest).getSession();

			PortalUtil
					.getHttpServletRequest(actionRequest)
					.getSession()
					.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

			List<Liquidacion> liquidacionesList = new ArrayList<Liquidacion>();
			Enumeration<String> parameters = actionRequest.getParameterNames();
			BigDecimal total = new BigDecimal(0);
			BigDecimal totDebitos = new BigDecimal(0);
			Liquidacion liquidacion = null;

			List<Comprobante> comprobantes = new ArrayList<Comprobante>();
			String cuit_prestador = null;
			String sucu_prestador = "000";
			
			while (parameters.hasMoreElements()) {
				String paramName = parameters.nextElement();
				if (paramName.indexOf("pagarLiqui") != -1) {
					int id_liquidacion = ParamUtil.getInteger(actionRequest,
							paramName);
					liquidacion = LiquidacionActionUtil
							.getLiquidacionEntry(id_liquidacion);

					Comprobante compDeb = ComprobanteServiceUtil
							.getComprobanteDebitoLiquidacionPorId(id_liquidacion);
					liquidacionesList.add(liquidacion);

					cuit_prestador = liquidacion.getPrestador_lugar_atencion()
							.getPrestador().getCuit() != null ? liquidacion
							.getPrestador_lugar_atencion().getPrestador()
							.getCuit() : "";

					/*List<Empresa> busqueda = EmpresaServiceUtil.getEmpleadores(
							cuit_prestador,
							null,
							String.valueOf(liquidacion
									.getPrestador_lugar_atencion()
									.getPrestador().getId_prestador()), 0);*/
							
					List<Empresa> busqueda = TraeListasServiceUtil.getEmpleadores(
							cuit_prestador,
							null,
							String.valueOf(liquidacion
									.getPrestador_lugar_atencion()
									.getPrestador().getId_prestador()));

					sucu_prestador = busqueda.size() == 1 ? String
							.valueOf(liquidacion.getPrestador_lugar_atencion()
									.getPrestador().getId_prestador()) : "000";

					Comprobante comp = new Comprobante(
							liquidacion.getSucu(),
							liquidacion.getCompro_a_debitar_tipo(),
							liquidacion.getCompro_a_debitar_numero(),
							liquidacion.getPrestador_lugar_atencion()
									.getPrestador().getCuit() != null ? liquidacion
									.getPrestador_lugar_atencion()
									.getPrestador().getCuit()
									: "", liquidacion.getFecha_emitido(),
							liquidacion.getFecha_recibido(),
							liquidacion.getImporte(),
							liquidacion.getCompro_a_debitar_letra(),
							liquidacion.getSucu(),
							liquidacion.getFecha_vencimiento());
					comp.setAlta_fecha(new Date());
					comprobantes.add(comp);
					comp.setAcreedorEmpresa(new Empresa(cuit_prestador,
							sucu_prestador, null));
					comp.setPeriodoPrestacion(liquidacion.getPeriodo());
					comp.setConceptos(ComprobanteServiceUtil.getConceptos(comp,
							entidad));

					if (compDeb != null) {
						comprobantes.add(compDeb);
						// 11/02/2015 SACO EL CONCEPTO PARA LIQ CON PRESTACIONES
						if (null != liquidacion.getLiquidacionPrestacion()
								&& liquidacion.getLiquidacionPrestacion()
										.size() == 0) {
							compDeb.setConceptos(ComprobanteServiceUtil
									.getConceptos(compDeb, entidad));
						} else {
							boolean existe = false;
							for (LiquidacionPrestacion liq : liquidacion
									.getLiquidacionPrestacion()) {
								if (null != liq.getLiquidacion()) {
									if (liq.getLiquidacion().getCompro_a_debitar_letra().equals(compDeb.getLetraComprobante())
											&& liq.getLiquidacion().getCompro_a_debitar_numero().equals(compDeb.getNroComprobante())
											&& liq.getLiquidacion().getCompro_a_debitar_tipo().equals(compDeb.getTipoComprobante())) {
										existe = true;
									}
								}
							}
							if (!existe) {
								compDeb.setConceptos(ComprobanteServiceUtil
										.getConceptos(compDeb, entidad));
							}
						}

					}
				}
			}

			OrdenPagoOspim op = new OrdenPagoOspim();

			try {
				int proximoIdOP= OrdenPagoServiceUtil.obtenerProximoIdOrdenPago();
				actionRequest.setAttribute("PROXIMOIDORDENPAGO",proximoIdOP);
				
			} catch (SystemException e) {
				_log.error(e);
			}
			
			PortalUtil
					.getHttpServletRequest(actionRequest)
					.getSession()
					.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
							op);
			actionRequest.setAttribute(
					WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
			actionRequest.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION,
					WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
			PortalUtil.getHttpServletRequest(actionRequest).getSession()
					.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);

			op.setLiquidacionesList(liquidacionesList);
			op.setComprobantes(comprobantes);			
			

			op.setAcreedor(new Empresa(cuit_prestador, sucu_prestador, null));

			op.setObservaciones("LIQUIDACIÓN - PRESTAC. MEDICAS: "
					+ liquidacion.getPrestador_lugar_atencion().getPrestador()
							.getDescripcion());
			session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,
					comprobantes);

			op.setImporte(total.subtract(totDebitos));

			List<OrdenPago.FormaPago> list = op.getFormaPago();
			if (list == null) {
				list = new ArrayList<OrdenPago.FormaPago>();
				op.setFormaPago(list);
			}
			
			//ESTO ES PARA RETENCIONES
			Empresa proveedor = null;
			BigDecimal importesCompro=BigDecimal.ZERO;
			if (comprobantes != null && comprobantes.size()>0) {				
				importesCompro=BuscarComprobanteEmbebidoAction.sumaImportesOrden(comprobantes);
				proveedor = TraeListasServiceUtil.getEmpleadores(comprobantes.get(0).getCuit(),
						null, null).get(0);
				if(proveedor!=null&&proveedor.getImpGanancias()!=null && proveedor.getImpGanancias().equals("AC")){
					//1ro BUSCO TODOS LOS COMPROBANTES DEL MES EN CURSO				
					Calendar periodoCalendar=Calendar.getInstance();
					periodoCalendar.set(Calendar.DAY_OF_MONTH, 1);		
//					TODO parece que aca no tomo la fecha de alta :P
					BigDecimal retencion=AfipServiceUtil.getRetencionGanancias(proveedor.getCuit(),importesCompro, periodoCalendar.getTime(), entidad);
					if(retencion.compareTo(BigDecimal.ZERO)>0){
						OrdenPago ordenPago = (OrdenPago) session
								.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);						
						RetencionGanancias ret = new RetencionGanancias();
						//CUENTAS BCRIAS POR DEFECTO SEGUN ENTIDAD
						int nroCta=entidad==WebKeysGlobal.OSPIM?2:entidad==WebKeysGlobal.UOMA?8:5;
						CuentaBancaria cta = new CuentaBancaria(nroCta);					
						ret.setImporte(retencion);
						ret.setCuentaBancaria(cta);
						if (!list.contains(new OrdenPago.FormaPago(ret))) {
							list.add(new OrdenPago.FormaPago(ret));
						}
						ordenPago.setPagos(list);
						session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
								ordenPago);
						
					}else if(retencion.compareTo(new BigDecimal(-1))==0){
						// Importe de retenciones = -1 es porque no está configurado el codigo de régimen
						SessionErrors.add(actionRequest, "regimenError");
						actionRequest.setAttribute("msgError2", LanguageUtil.get(defaultLocale, "exception-ret-ganancia-regimen"));
					}else if(retencion.compareTo(new BigDecimal(-2))==0){
						// Importe de retenciones = -2 es porque no está importada la tabla de excenciones
						SessionErrors.add(actionRequest, "exencionError");
						actionRequest.setAttribute("msgError3", LanguageUtil.get(defaultLocale, "exception-ret-ganancia-exencion"));
						SessionErrors.add(actionRequest, "exencionUrlError");
						actionRequest.setAttribute("msgError4", LanguageUtil.get(defaultLocale, "exception-ret-ganancia-exencion-url"));
					}
				}	
			}
			
			List<OrdenPago.FormaPago> comp = ComprobanteServiceUtil
					.getAnticipoARendir(cuit_prestador, sucu_prestador, null,
							entidad);
			// BUSCO DESTINO Y RAZON SOC CHEQUE
			String[] razonDestino = OrdenPagoServiceUtil
					.getUltimaRazonSocialChequeYDestinoOP(op.getCuit(),
							sucu_prestador, 0, WebKeysGlobal.OSPIM);
			op.setDestino(razonDestino[OrdenPagoServiceUtil.DESTINO_POS]);
			op.setAFavorDe(razonDestino[OrdenPagoServiceUtil.A_NOMBRE_DE_POS]);
			op.setCBU(razonDestino[OrdenPagoServiceUtil.CBU_POS]);
			op.getAcreedor().setRazon_soc(razonDestino[OrdenPagoServiceUtil.RAZON_SOC_POS]);			

			if (comp != null) {
				list.addAll(comp);
			}

			actionRequest.setAttribute("cheque_a_favor_de", proveedor.getPortaCheque());
		} catch (NoSuchLiquidacionEntryException e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (Exception e) {
			_log.error("Error al crear op de liquidacion ", e);
			throw e;
		}
		if (!SessionErrors.isEmpty(actionRequest)) {
			setForward(actionRequest, "portlet.liquidaciones.view");
			actionRequest.setAttribute("tabs1", "ordenes-pago-ospim");
		}

		actionRequest.setAttribute(
				WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES,
				WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		TraeListasServiceUtil.getCtasBcrias(renderRequest);

		renderRequest.setAttribute(WebKeysLiquidaciones.FROM_LIQUIDACION,
				WebKeysLiquidaciones.FROM_LIQUIDACION);
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.editar_orden_pago_ospim_entry"));
	}

}
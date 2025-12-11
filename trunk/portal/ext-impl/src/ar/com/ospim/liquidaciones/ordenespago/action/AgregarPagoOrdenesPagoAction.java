package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.ComprobanteInexistenteException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.Caja;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.PagoBancario;
import ar.com.ospim.global.beans.PagoSinSalidaDeFondos;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.beans.RetencionIIBB;
import ar.com.ospim.global.beans.RetencionIVA;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ChequeSinChequeraException;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroList;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.StringUtils;

public class AgregarPagoOrdenesPagoAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(AgregarPagoOrdenesPagoAction.class);
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String addFormaPago = ParamUtil.getString(renderRequest, Constants.ACTION);

		
		int entidad = WebKeysGlobal.OSPIM;
		if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		}
		String vacio = renderRequest.getParameter("vacio");
		String esEdicion = renderRequest.getParameter("esEdicion");
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		OrdenPago ordenPago = (OrdenPago) session.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		
		//Lista pago por tranferencia
		List<ReintegroList> reintegrosAll  =  (List<ReintegroList>) session.getAttribute(WebKeysLiquidaciones.LISTA_ORDEN_PAGO_EDICION);
		
		
		if (null == vacio || !vacio.equals("si")) {

			if (renderRequest.getParameter("esEdicion") != null) {
				renderRequest.setAttribute("esEdicion",renderRequest.getParameter("esEdicion"));
			}

			try {
				
				if(StringUtils.checkNotEmpty(addFormaPago) 
						&& WebKeysLiquidaciones.ADD_FORMA_PAGO.equalsIgnoreCase(addFormaPago)) {
				if (reintegrosAll != null && !reintegrosAll.isEmpty()){
					
					List<Reintegro>  reintegros = null;
					for (ReintegroList reintegroList : reintegrosAll) {
						
						reintegros = reintegroList.getReintegros();
						
						for (Reintegro reintegro : reintegros) {
							//getPago(renderRequest, "500", "100", 50);
							if (reintegro.importeTotal().compareTo(BigDecimal.ZERO) != 0){								
								try{									
									getPago(renderRequest, reintegro, reintegro.importeTotal().toString(), ordenPago);
								}catch (Exception e) {
									_log.debug(e.getMessage());
								}
							}
						}
						
					}
				
					session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

					session.removeAttribute(WebKeysLiquidaciones.LISTA_ORDEN_PAGO_EDICION);
					session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,ordenPago);
					// ver
					return mapping.findForward(getForward(renderRequest,"portlet.liquidaciones.ordenes_pago.pagos.result.search"));
				}

			}
				
				
				String buscar_anticipos = renderRequest
						.getParameter("buscar_anticipos");
				if (buscar_anticipos != null && buscar_anticipos.equals("buscar_anticipos")) {
					String cuitEntidad = renderRequest
							.getParameter("cuit_entidad");
					String sucuEntidad = renderRequest
							.getParameter("sucu_entidad");
					String idSeccional = renderRequest
							.getParameter("id_seccional");

					List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
					if (list == null) {
						list = new ArrayList<OrdenPago.FormaPago>();
						ordenPago.setFormaPago(list);
					}
					
					//Agregado 22/12/2016 para que no traiga lista de Anticipos a Rendir cuando es una consulta de OP
					if("true".equalsIgnoreCase(esEdicion)){
					   sacarAnticipos(list);
					   List<OrdenPago.FormaPago> comp = ComprobanteServiceUtil
							.getAnticipoARendir(cuitEntidad, sucuEntidad,
									idSeccional, entidad);
					   if (comp != null) {
						   for(OrdenPago.FormaPago p:comp) {
							   if(!list.contains(p)) {
								   list.add(p);
							   }
						   }
//						  list.addAll(comp);
					   }
					}// Fin Agregado
					
				} else if (buscar_anticipos != null
						&& buscar_anticipos.equals("modificar_anticipo")) {
					List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
					StringTokenizer anticipoAmodif = new StringTokenizer(
							renderRequest.getParameter("anticipo"), "_");
					String importeStr = renderRequest
							.getParameter("importeAnticipo");
					BigDecimal importe = new BigDecimal(importeStr != null
							&& !"".equals(importeStr.trim()) ? importeStr : "0");
					modificarAnticipo(list, anticipoAmodif.nextToken(), importe);

				} else {
					getPago(renderRequest, ordenPago, entidad);

				}
				
			
			} catch (DuplicateNumeroChequeException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			} catch (ChequeSinChequeraException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			} catch (ComprobanteInexistenteException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			} catch (Exception e) {
				_log.debug(e.getMessage());
			}

			String buscar_anticipos = renderRequest
					.getParameter("buscar_anticipos");
			String tipo_ingreso = renderRequest.getParameter("tipo");
			if ((buscar_anticipos != null && (buscar_anticipos
					.equals("buscar_anticipos") || buscar_anticipos
					.equals("modificar_anticipo")))
					|| (tipo_ingreso != null && tipo_ingreso
							.equals(Anticipo.class.getName()))) {
				return mapping.findForward(getForward(renderRequest,"portlet.liquidaciones.ordenes_pago.anticipos.result.search"));
			}
		}

		session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
				ordenPago);

		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.ordenes_pago.pagos.result.search"));
	}

	private void sacarAnticipos(List<OrdenPago.FormaPago> list) {
		if (list != null) {
			Iterator<OrdenPago.FormaPago> it = list.iterator();
			while (it.hasNext()) {
				OrdenPago.FormaPago p = it.next();
				if (p.getPago() instanceof Anticipo) {
					it.remove();
				}
			}
		}
	}

	private void modificarAnticipo(List<OrdenPago.FormaPago> list,
			String nroAntic, BigDecimal importe) {
		if (list != null) {
			Iterator<OrdenPago.FormaPago> it = list.iterator();
			while (it.hasNext()) {
				OrdenPago.FormaPago p = it.next();
				if (p.getPago() instanceof Anticipo
						&& p.getPago().getNumeroStr().equals(nroAntic)) {
					((Anticipo) p.getPago()).setImporteOriginal(((Anticipo) p
							.getPago()).getImporte());
					// NO PUEDE SER MAYOR AL ANTICIPO
					if (importe
							.compareTo(((Anticipo) p.getPago()).getAnticipo().getImporteComprobanteOriginal()) > 0) {
						((Anticipo) p.getPago()).setImporte(((Anticipo) p
								.getPago()).getImporte());						
						((Anticipo) p.getPago()).getAnticipo().getConceptos()
								.get(0).setImporte(((Anticipo) p
										.getPago()).getImporte());
					} else {
						((Anticipo) p.getPago()).setImporte(importe);
						((Anticipo) p.getPago()).getAnticipo().getConceptos()
								.get(0).setImporte(importe);
					}

					
				}
			}
		}
	}
	
	private void getPago(RenderRequest renderRequest, Reintegro reintegro /*String nroCBU*/, String importe  ,OrdenPago ordenPago) throws DuplicateNumeroChequeException,
	ChequeSinChequeraException, SystemException, ParseException,
			ComprobanteInexistenteException {

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil.getCtasBcrias(renderRequest);

		CuentaBancaria cta = new CuentaBancaria(10);
		int indexOf = ctasBcrias.indexOf(cta);
		if (indexOf != -1) {
			cta = ctasBcrias.get(indexOf);
		}

		String tipo_ingreso = "3";

		BigDecimal importeBigD = null;
		if (StringUtils.checkNotEmpty(importe)) {
			importeBigD = new BigDecimal(importe);
		}
		List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
		if (list == null) {
			list = new ArrayList<OrdenPago.FormaPago>();
			ordenPago.setFormaPago(list);
		}

		PagoBancario pago = new PagoBancario();
		pago.setCuentaBancaria(cta);
		pago.setImporte(importeBigD);
		pago.setNumero(reintegro.getCbu());
		pago.setCuilCuenta(reintegro.getCuilCuenta());
		pago.setEmailCuenta(reintegro.getEmailCuenta());
		pago.setApellidoCuenta(reintegro.getApellidoCuenta());
		pago.setNombreCuenta(reintegro.getNombreCuenta());

		
		
		if (!list.contains(new OrdenPago.FormaPago(pago))) {
			list.add(new OrdenPago.FormaPago(pago));
		}

		pago.setTipo_pago(3);

		//cambiarCtaRetencion(list, cta);

	}

	private void getPago(RenderRequest renderRequest, OrdenPago ordenPago,
			int entidad) throws DuplicateNumeroChequeException,
			ChequeSinChequeraException, SystemException, ParseException,
			ComprobanteInexistenteException {

		List<CuentaBancaria> ctasBcrias = TraeListasServiceUtil
				.getCtasBcrias(renderRequest);

		String aFavorDe = StringUtils.getValueOrNull(renderRequest
				.getParameter("aFavorDe"));
		String importe = renderRequest.getParameter("importe_pago");
		String tipo_ingreso = renderRequest.getParameter("tipo");
		String idCtaBcria = renderRequest.getParameter("id_cta_bcria");
		String modificaFormaPago = renderRequest.getParameter("modificaFormaPago");

		BigDecimal importeBigD = null;
		if (StringUtils.checkNotEmpty(importe)) {
			importeBigD = new BigDecimal(importe);
		}
		List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
		if (list == null) {
			list = new ArrayList<OrdenPago.FormaPago>();
			ordenPago.setFormaPago(list);
		}

		CuentaBancaria cta = new CuentaBancaria(Integer.parseInt(idCtaBcria));
		int indexOf = ctasBcrias.indexOf(cta);
		if (indexOf != -1) {
			cta = ctasBcrias.get(indexOf);
		}

		String nro = renderRequest.getParameter("nro");
		if (tipo_ingreso.equals(Cheque.class.getName())) {
			Cheque cheque = new Cheque(new BigDecimal(nro), cta.getBanco()
					.getId_banco());

			cheque.setImporte(importeBigD);
			cheque.setEstado(TraeListasServiceUtil
					.getEstadoChequeEmitido(renderRequest));
			cheque.setDebitoCredito(Cheque.Tipo.DEBITO);
			cheque.setCuentaBancaria(cta);
			cheque.setANombreDe(aFavorDe);
			if (!list.contains(new OrdenPago.FormaPago(cheque))) {
				if(!"true".equalsIgnoreCase(modificaFormaPago)) {
				  validarCheque(cheque, entidad);
				}  

				list.add(new OrdenPago.FormaPago(cheque));
			}
			cheque.setFecha(new Date());
		} else if (tipo_ingreso.equals(RetencionGanancias.class.getName())) {
			RetencionGanancias ret = new RetencionGanancias();
			ret.setCuentaBancaria(cta);
			ret.setImporte(importeBigD);
			if (!list.contains(new OrdenPago.FormaPago(ret))) {
				list.add(new OrdenPago.FormaPago(ret));
			}
		} else if (tipo_ingreso.equals(Anticipo.class.getName())) {
			Comprobante c = new Comprobante();
			c.setNroComprobante(nro);
			c.setTipoComprobante("ANT");
			c.setPtoVenta(1);

			List<Comprobante> comps = ComprobanteServiceUtil
					.getAnticiposARendir(c, entidad);
			if (comps == null || comps.size() == 0) {
				throw new ComprobanteInexistenteException();
			}
			Anticipo ant = new Anticipo();
			ant.setAnticipo(comps.get(0));
			if (!list.contains(new OrdenPago.FormaPago(ant))) {
				list.add(new OrdenPago.FormaPago(ant));
			}
		} else if (tipo_ingreso.startsWith(PagoBancario.class.getName())) {
			PagoBancario pago = new PagoBancario();
			pago.setCuentaBancaria(cta);
			pago.setImporte(importeBigD);
			if (StringUtils.checkNotEmpty(nro)) {
				pago.setNumero(nro);
			}
			if (!list.contains(new OrdenPago.FormaPago(pago))) {
				list.add(new OrdenPago.FormaPago(pago));
			}

			pago.setTipo_pago(Integer.parseInt(tipo_ingreso.replace(
					PagoBancario.class.getName(), "")));
		} else if (tipo_ingreso.startsWith(Caja.class.getName())) {
			Caja efectivo = new Caja();
			efectivo.setImporte(importeBigD);
			efectivo.setTipo_pago(Integer.parseInt(tipo_ingreso.replace(
					Caja.class.getName(), "")));
			if (!list.contains(new OrdenPago.FormaPago(efectivo))) {
				list.add(new OrdenPago.FormaPago(efectivo));
			}
		}else if (tipo_ingreso.startsWith(RetencionIIBB.class.getName())) {
			RetencionIIBB rIB = new RetencionIIBB();
			rIB.setCuentaBancaria(cta);
			rIB.setImporte(importeBigD);
			rIB.setJurisdiccion(Integer.parseInt(tipo_ingreso.replace(
					RetencionIIBB.class.getName(), "")));
			if (!list.contains(new OrdenPago.FormaPago(rIB))) {
				list.add(new OrdenPago.FormaPago(rIB));
			}
		} else if (tipo_ingreso.equals(RetencionIVA.class.getName())) {
			RetencionIVA ret = new RetencionIVA();
			ret.setCuentaBancaria(cta);
			ret.setImporte(importeBigD);
			if (!list.contains(new OrdenPago.FormaPago(ret))) {
				list.add(new OrdenPago.FormaPago(ret));
			}
		} else if (tipo_ingreso.startsWith(PagoSinSalidaDeFondos.class.getName())) {
			PagoSinSalidaDeFondos e = new PagoSinSalidaDeFondos();
			e.setImporte(importeBigD);
			e.setTipo_pago(Integer.parseInt(tipo_ingreso.replace(
					PagoSinSalidaDeFondos.class.getName(), "")));
			if (!list.contains(new OrdenPago.FormaPago(e))) {
				list.add(new OrdenPago.FormaPago(e));
			}
		}
		
		
		if(!tipo_ingreso.equals(RetencionGanancias.class.getName())
				&& !tipo_ingreso.startsWith(Caja.class.getName())
				&& !tipo_ingreso.startsWith(Anticipo.class.getName())){
			cambiarCtaRetencion(list,cta);
		}

	}
	
	private void cambiarCtaRetencion(List<OrdenPago.FormaPago> list, CuentaBancaria cta){
		for(OrdenPago.FormaPago fp:list){
			if(fp.getTipo().contains(RetencionGanancias.class.getSimpleName())){				
				((RetencionGanancias)fp.getPago()).setCuentaBancaria(cta);		
			}
		}		
	}

	private boolean validarCheque(Cheque cheque, int entidad)
			throws SystemException, DuplicateNumeroChequeException,
			ChequeSinChequeraException {
		return ChequeServiceUtil.validarCheque(cheque, entidad);
	}

}

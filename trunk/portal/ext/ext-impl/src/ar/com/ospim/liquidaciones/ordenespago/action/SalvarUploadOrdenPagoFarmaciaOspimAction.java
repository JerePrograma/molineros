package ar.com.ospim.liquidaciones.ordenespago.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Tipo;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.PagoBancario;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.AnticipoSuperaImporteOPException;
import ar.com.ospim.liquidaciones.ChequeSinChequeraException;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.OrdenPagoOspimCreacionNuevoAnticipoException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import edu.emory.mathcs.backport.java.util.Collections;

public class SalvarUploadOrdenPagoFarmaciaOspimAction extends PortletAction {

	@SuppressWarnings("unchecked")
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;

		if (actionResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (actionResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		int nro_lote = ParamUtil.getInteger(actionRequest, "nro_lote");

		User user = PortalUtil.getUser(actionRequest);
		List<OrdenPago> ordenes = (ArrayList<OrdenPago>) PortalUtil
				.getHttpServletRequest(actionRequest).getSession()
				.getAttribute(WebKeysLiquidaciones.ORDENES_PAGO);

		Collections.sort(ordenes, new Comparator<OrdenPagoOspim>() {
			public int compare(OrdenPagoOspim o1, OrdenPagoOspim o2) {
				if (o1.getItems().isEmpty() || o2.getItems().isEmpty()) {
					return 0;
				}
				ItemOrdenPago item1 = o1.getItems().get(0);
				ItemOrdenPago item2 = o2.getItems().get(0);
				if (item1.getPeriodo().equals(item2.getPeriodo())) {
					return item1.getCodigoPrestador().compareTo(
							item2.getCodigoPrestador());
				} else {
					return item1.getPeriodo().compareTo(item2.getPeriodo());
				}

			}
		});
		
		try {
			if (ordenes != null) {
				int nro = 0;
				Comprobante ultimoComprobanteNDFOspimAutomatico = ComprobanteServiceUtil
						.getUltimoComprobanteNDFOspimAutomatico();

				if (ultimoComprobanteNDFOspimAutomatico != null) {
					nro = Integer.valueOf(ultimoComprobanteNDFOspimAutomatico
							.getNroComprobante());
				}
				for (OrdenPago opGenerica : ordenes) {
					nro++;
					OrdenPagoOspim op = (OrdenPagoOspim) opGenerica;
					op.setFarmacia(true);
					op.setIdLote(nro_lote);
					String nro_cheque = ParamUtil.getString(actionRequest,
							"cheque_nro_" + op.getId());
					String dcto = ParamUtil.getString(actionRequest, "dcto_"
							+ op.getId());
					String dctoDrog = ParamUtil.getString(actionRequest,
							"dcto_drog_" + op.getId());
					String a_favor_de = ParamUtil.getString(actionRequest,
							"a_favor_de_" + op.getId());
					String cuit_de = ParamUtil.getString(actionRequest,
							"cuit_de_" + op.getId());
					String destino = ParamUtil.getString(actionRequest,
							"destino_" + op.getId());
					String obs_interna = ParamUtil.getString(actionRequest,
							"obs_int_" + op.getId());
					String anticipoString = ParamUtil.getString(actionRequest,
							"ant_" + op.getId());
					String tipoPagoString = ParamUtil.getString(actionRequest,
							"tipo_pago_" + op.getId());
					String emailCBU=ParamUtil.getString(actionRequest,
							"email_cbu_" + op.getId());
					
					
					int idCtaBcria= ParamUtil.getInteger(actionRequest,
							"id_cta_bcria_" + op.getId());
					
					Banco banco= TraeListasServiceUtil.getBancoPorIdCtaBcria(actionRequest, idCtaBcria);
					
					BigDecimal anticipo = new BigDecimal(anticipoString);
					op.setDescuento(new BigDecimal(dcto).setScale(2,
							BigDecimal.ROUND_DOWN));
					op.setDescuentoDrogueria(new BigDecimal(dctoDrog).setScale(2,
							BigDecimal.ROUND_DOWN));
					op.setDestino(destino);
					op.setObsInterna(obs_interna);
					op.setEmailCBU(emailCBU);
					Comprobante compNd = null;
					if (op.getDescuento().doubleValue() > 0
							|| op.getDescuentoDrogueria().doubleValue() > 0) {

						List<Comprobante> comprobantesOP = op.getComprobantes();
						BigDecimal totalDescuentoPorc = BigDecimal.ZERO;
						if (op.getBaseDescuentoFarmacia().equals("PVP")) {
							totalDescuentoPorc = op.getImporteDeItemsPVP()
									.multiply(op.getDescuento())
									.divide(new BigDecimal(100)).setScale(2,
											BigDecimal.ROUND_DOWN);;
						} else {
							totalDescuentoPorc = op.getImporteDeItems()
									.multiply(op.getDescuento())
									.divide(new BigDecimal(100)).setScale(2,
											BigDecimal.ROUND_DOWN);;
						}

						BigDecimal totalDescuento = totalDescuentoPorc.add(
								op.getDescuentoDrogueria()).setScale(2,
								BigDecimal.ROUND_DOWN);

						compNd = new Comprobante(2,
								WebKeysGlobal.COMPROBANTE_NOTA_DEBITO_FARMACIA,
								String.valueOf(nro), WebKeysGlobal.CUIT_OSPIM,
								new Date(), new Date(), totalDescuento, "", 2,
								null, new Empresa(op.getCuit(), op
										.getAcreedor().getSucursal(), ""), null);

						// crea conceptos asociados a la nota débito que se está
						// insertando
						List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
						ComprobanteConcepto comprobanteConcepto = new ComprobanteConcepto(
								new Concepto(236), // VER
								totalDescuento);
						conceptos.add(comprobanteConcepto);
						compNd.setConceptos(conceptos);
						comprobantesOP.add(compNd);

						op.setComprobantes(comprobantesOP);
					}
					BigDecimal sumaAnticipos = BigDecimal.ZERO;
					if (op.getFormaPago() != null) {

						for (int p = 0; p < op.getFormaPago().size(); p++) {
							if (op.getFormaPago().get(p).getPago() instanceof Cheque) {
								Cheque cheque = (Cheque) op.getFormaPago()
										.get(p).getPago();
								cheque.setNumero(new BigDecimal(nro_cheque));
								cheque.setImporte(op
										.getImporteDeItemsConDescuento().setScale(2,
												BigDecimal.ROUND_DOWN));
								cheque.setCuentaBancaria(new CuentaBancaria(
										idCtaBcria));
								cheque.setBanco(banco);
								cheque.setFecha(new Date());
								cheque.setCuit(cuit_de);
								cheque.setANombreDe(a_favor_de);
								cheque.setDebitoCredito(Tipo.DEBITO);
								cheque.setEstado(TraeListasServiceUtil
										.getEstadoChequeEmitido(actionRequest));
							}
							if (op.getFormaPago().get(p).isAnticipo()) {								
								if (anticipo.compareTo(BigDecimal.ZERO) > 0) {
									sumaAnticipos.add(op.getFormaPago().get(p).getImporte().setScale(2,
											BigDecimal.ROUND_DOWN));
								}
								if (op.getImporteDeItemsConDescuento()
										.compareTo(anticipo) < 0) {
									throw new AnticipoSuperaImporteOPException(
											op.getCuit());
								}
							}

						}
					} else {						
						op.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
						//List<Comprobante> comprobantes = getComprobantes(op);
						//ANTICIPOS
						if (anticipo.compareTo(BigDecimal.ZERO) > 0) {
							//EL ANTICIPO SUPERA EL IMPORTE SALIMOS
							if (op.getImporteDeItemsConDescuento()
									.compareTo(anticipo) < 0) {
								throw new AnticipoSuperaImporteOPException(
										op.getCuit());
							}
							
							BigDecimal totalAnti=BigDecimal.ZERO;
							List<Anticipo> anticipos = ComprobanteServiceUtil
									.getInstance().getAnticiposARendir(
											new Empresa(op.getAcreedor().getCuit(),
													"000"), 0, WebKeysGlobal.OSPIM);
							
							if (anticipos != null) {								
								for (Anticipo ant : anticipos) {
									
										op.getFormaPago().add(new OrdenPago.FormaPago(ant));
										List<ComprobanteConcepto> conc = ComprobanteServiceUtil
												.getConceptos(ant.getAnticipo(),
														WebKeysGlobal.OSPIM);
										ant.getAnticipo().setConceptos(conc);
										totalAnti = totalAnti.add(ant.getImporte());
										
										//comprobantes.add(ant.getAnticipo());
									
								}
								if(anticipo.compareTo(totalAnti)>0){
									throw new AnticipoSuperaImporteOPException();
								}
							}

						}
						//ACA VERIFICAR FORMA DE PAGO
						if(tipoPagoString.equals(PagoBancario.class.getName()+PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA)){
							PagoBancario pago=new PagoBancario();
							pago.setNumero(nro_cheque);
							pago.setImporte(op.getImporteDeItemsConDescuento().subtract(anticipo).setScale(2,
									BigDecimal.ROUND_DOWN));
							pago.setCuentaBancaria(new CuentaBancaria(
									idCtaBcria));
							pago.setTipo_pago(PagoBancario.ID_PAGO_TRANSFERENCIA_BANCARIA);			
							op.getFormaPago().add(
									new OrdenPago.FormaPago(pago));
						}else{
							Cheque cheque =new Cheque();
							cheque.setNumero(new BigDecimal(nro_cheque));
							cheque.setImporte(op.getImporteDeItemsConDescuento().subtract(anticipo).setScale(2,
									BigDecimal.ROUND_DOWN));
							cheque.setCuentaBancaria(new CuentaBancaria(
									idCtaBcria));
							cheque.setBanco(banco);
							cheque.setFecha(new Date());
							cheque.setCuit(cuit_de);
							cheque.setANombreDe(a_favor_de);
							cheque.setDebitoCredito(Tipo.DEBITO);
							cheque.setEstado(TraeListasServiceUtil
									.getEstadoChequeEmitido(actionRequest));
							op.getFormaPago().add(
									new OrdenPago.FormaPago(cheque));
						}
						
					}
					op.setImporte(op.getImporteDeItemsConDescuento().setScale(2,
							BigDecimal.ROUND_DOWN));
					op.setTotalAnticipos(anticipo);
					try {
						if(anticipo.compareTo(BigDecimal.ZERO)>0){
							op.validarAnticipoParcial(entidad, anticipo);
						}
					} catch (OrdenPagoOspimCreacionNuevoAnticipoException e) {
						///comprobantes.add(new Comprobante())
					}

				}
				// TODO definir tema clave primaria de farmacia y actualizar
				// farmacia si es diferente
				
				OrdenPagoServiceUtil.save(ordenes, user, entidad);

				PortalUtil
						.getHttpServletRequest(actionRequest)
						.getSession()
						.setAttribute(WebKeysLiquidaciones.ORDENES_PAGO,
								ordenes);
			}
		} catch (AnticipoSuperaImporteOPException e) {
			if (e.getRazonSoc() != null) {
				actionRequest.setAttribute("op_superada", e.getRazonSoc());
			}
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (DuplicateNumeroChequeException e) {
			if (e.getCheque() != null) {
				actionRequest.setAttribute("Cheques_Duplicados", e.getCheque());
			}
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (ChequeSinChequeraException e) {
			if (e.getCheque() != null) {
				actionRequest.setAttribute("Cheques_Duplicados", e.getCheque());
			}
			SessionErrors.add(actionRequest, e.getClass().getName());
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			//todo bien, envio mails de Transferencia....
			enviaMailsTransferencias(ordenes);
			
			setForward(actionRequest,
					"portlet.farmacia.editar_uploaded_orden_pago_entry");
			actionRequest.setAttribute("chequeIniId", ordenes.get(0)
					.getFormaPago().get(0).isAnticipo() ? ordenes.get(0)
					.getFormaPago().get(1).getPago().getNumeroStr() : ordenes
					.get(0).getFormaPago().get(0).getPago().getNumeroStr());
			actionRequest.setAttribute("chequeFinId",
					ordenes.get(ordenes.size() - 1).getFormaPago().get(0)
							.isAnticipo() ? ordenes.get(ordenes.size() - 1)
							.getFormaPago().get(1).getPago().getNumeroStr()
							: ordenes.get(ordenes.size() - 1).getFormaPago()
									.get(0).getPago().getNumeroStr());
			actionRequest.setAttribute("ordenIniId", ordenes.get(0).getId());
			actionRequest.setAttribute("ordenFinId", ordenes.get(ordenes.size() - 1).getId());
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
			PortalUtil.getHttpServletRequest(actionRequest).getSession()
					.setAttribute(WebKeysLiquidaciones.ORDENES_PAGO, ordenes);
		} else {
			int i = -1;
			for (OrdenPago op : ordenes) {
				op.setId(i);
				i--;
			}
		}
	}

	public void enviaMailsTransferencias(List<OrdenPago> ordenes){
		for(OrdenPago op: ordenes){
			String cbu=op.getCBUTransferencia();
			String email = op.getEmailCBU();
			if(null!=cbu){
				ArrayList<byte[]> pdfs = new ArrayList<byte[]>();
				PdfServlet pdfServlet=new PdfServlet();
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("ID_ORDEN_PAGO", String.valueOf(op.getId()));		
				byte[] pdfOp=pdfServlet.crearPdfComoAdjunto(PdfServlet.ORDEN_PAGO_OSPIM, hm, PdfServlet.ORDEN_PAGO_OSPIM_PDF_FILENAME);
				pdfs.add(pdfOp);
				hm=new HashMap<String, String>();
				hm.put("id_op_p", String.valueOf(op.getId()));		
				hm.put("entidad_p", String.valueOf(WebKeysGlobal.OSPIM));		
				pdfOp=pdfServlet.crearPdfComoAdjunto(PdfServlet.COMPROBANTE_RETEN_GANANCIAS, hm, PdfServlet.COMPROBANTE_RETEN_GANANCIAS_PDF_FILENAME);
				if(null!=pdfOp && pdfOp.length>902){
					pdfs.add(pdfOp);
				}
				OrdenPago.enviarMailTransferencia(cbu, email, pdfs);
			}
		}
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest,
				"portlet.farmacia.editar_uploaded_orden_pago_entry"));
	}
	
	/*private List<Comprobante> getComprobantes(OrdenPagoOspim op) {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Comprobante comprobante = new Comprobante();
		comprobante.setCuit(op.getAcreedor().getCuit());
		comprobante.setTipoComprobante("LIQ");
		comprobante.setAcreedorEmpresa(op.getAcreedor());
		comprobante.setNroComprobante(String.valueOf(0));
		comprobante.setFechaRecepcion(new Date());
		comprobante.setPtoVenta(1);
		comprobante.setSucuComprobante(1);
		comprobante.setLetraComprobante("");
		comprobante.setImporteComprobante(op.getImporteDeItems());
		comprobantes.add(comprobante);
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		ComprobanteConcepto cc = new ComprobanteConcepto(new Concepto(
				UploadOrdenPagoOspimLiqAction.ID_CONCEPTO_LIQ_FARMACIA));
		cc.setImporte(op.getImporteDeItems());
		conceptos.add(cc);
		comprobante.setConceptos(conceptos);
		return comprobantes;
	}*/

}

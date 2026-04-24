package ar.com.ospim.liquidaciones.ordenespago.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class UploadOrdenPagoOspimLiqAction extends PortletAction {

	public static int ID_CONCEPTO_LIQ_FARMACIA = 236;

	private static Log logger = LogFactoryUtil
			.getLog(UploadOrdenPagoOspimLiqAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		Comprobante ultimoComprobanteOspimAutomatico = ComprobanteServiceUtil
				.getUltimoComprobanteOspimAutomatico();

		List<Farmacia> farmacias = TraeListasServiceUtil
				.getFarmaciasLiq(actionRequest);
		PortalUtil.getHttpServletRequest(actionRequest).getSession()
				.removeAttribute(WebKeysLiquidaciones.ORDENES_PAGO);
		PortalUtil
				.getHttpServletRequest(actionRequest)
				.getSession()
				.removeAttribute(
						WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_FALLAS);
		PortalUtil
				.getHttpServletRequest(actionRequest)
				.getSession()
				.removeAttribute(
						WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_DUPLICADOS);

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);
		if (uploadReq.getFileName("zipFile") != null) {
			String caja = uploadReq.getFileName("zipFile");
			actionRequest.setAttribute("cajaFarmacia", caja);
			File zip = uploadReq.getFile("zipFile");

			try {
				ZipInputStream in = new ZipInputStream(new FileInputStream(zip));
				ZipEntry entry = in.getNextEntry();
				List<OrdenPagoOspim> ordenes = new ArrayList<OrdenPagoOspim>();
				List<String> archivosFallidos = new ArrayList<String>();
				List<String> archivosDuplicados = new ArrayList<String>();
				List<String> archivosSinCuit = new ArrayList<String>();
				while (entry != null) {
					String name = entry.getName();
					logger.debug(name);
					if (name.toLowerCase().endsWith(".txt")) {
						processEntry(in, entry, ordenes, archivosSinCuit,
								archivosFallidos, archivosDuplicados,
								farmacias, caja);
					}
					entry = in.getNextEntry();
				}
				in.close();

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
							return item1.getPeriodo().compareTo(
									item2.getPeriodo());
						}

					}
				});

				int nro = 0;
				if (ultimoComprobanteOspimAutomatico != null) {
					nro = Integer.valueOf(ultimoComprobanteOspimAutomatico
							.getNroComprobante());
				}

				nro++;
				int i = 0;
				for (OrdenPagoOspim o : ordenes) {
					i--;
					o.setId(i);
					o.getComprobantes().get(0)
							.setNroComprobante(String.valueOf(nro));
					nro++;
				}

				PortalUtil
						.getHttpServletRequest(actionRequest)
						.getSession()
						.setAttribute(WebKeysLiquidaciones.ORDENES_PAGO,
								ordenes);

				actionRequest.setAttribute(
						WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_FALLAS,
						archivosFallidos);
				actionRequest.setAttribute(
						WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_DUPLICADOS,
						archivosDuplicados);
				actionRequest.setAttribute(
						WebKeysLiquidaciones.ORDENES_PAGO_ARCHIVOS_SIN_CUIT,
						archivosSinCuit);
			} catch (IOException e) {
				SessionErrors.add(actionRequest, e.getClass().getName());
			}
		}

	}

	private void processEntry(ZipInputStream in, ZipEntry entry,
			List<OrdenPagoOspim> ordenes, List<String> archivosSinCuit,
			List<String> archivosFallidos, List<String> archivosDuplicados,
			List<Farmacia> farmacias, String caja)
			throws UnsupportedEncodingException, IOException, SystemException {

		boolean error = false;
		OrdenPagoOspim op = new OrdenPagoOspim();
		List<ItemOrdenPago> items = new ArrayList<ItemOrdenPago>();
		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));
		int i=0;//PARA DEBUGUEAR
		while ((line = reader.readLine()) != null) {
			try {
				i++;
				ItemOrdenPago item = new ItemOrdenPago(line);
				op.setAFavorDe(item.getPrestador());
				op.setFechaDesde(item.getPeriodo());
				op.setFechaHasta(item.getPeriodo());
				op.setObservaciones(OrdenPagoOspim.CONCEPTO_FARMACIA);
				item.setArchivo(entry.getName());
				item.setCajaFarmacia(caja);
				items.add(item);				
			} catch (Exception e) {
				logger.error("ERRROR EN LINEA: "+i+" linea: "+line);
				error = true;
				archivosFallidos.add(entry.getName());
			}
		}

		if (items.size() == 0) {
			archivosSinCuit.add(entry.getName());
		} else {
			// siempre busco la primer farmacia para el codigo de prestador dado
			Farmacia farmacia = new Farmacia(items.get(0).getCodigoPrestador(),
					"1");

			int indexOf = farmacias.indexOf(farmacia);
			if (indexOf == -1
					|| farmacias.get(indexOf).getEmpresa() == null
					|| StringUtils.checkEmpty(farmacias.get(indexOf)
							.getEmpresa().getCuit())
					|| StringUtils.checkEmpty(farmacias.get(indexOf)
							.getEmpresa().getSucursal())) {
				error = true;
				archivosSinCuit.add(entry.getName());
			} else {
				op.setAcreedor(farmacias.get(indexOf).getEmpresa());
				op.setDescuento(farmacias.get(indexOf).getPorcDescuento());
				op.setAFavorDe(farmacias.get(indexOf).getCheque_a_nombre_de() == null ? ""
						: farmacias.get(indexOf).getCheque_a_nombre_de());
				op.setBaseDescuentoFarmacia(farmacias.get(indexOf).getBaseDto());
				op.setDestino(farmacias.get(indexOf).getDestino());
				op.setCBU(farmacias.get(indexOf).getCBU());
				op.setEmailCBU(farmacias.get(indexOf).getEmailCBU());
			}
		}
		if (!error) {
			op.setItems(items);
			logger.debug(op.getAFavorDe());
			if (op.getItems() != null && !op.getItems().isEmpty()) {
				if (OrdenPagoServiceUtil.existeOPFarmacia(op.getItems().get(0)
						.getPeriodo(), op.getItems().get(0)
						.getCodigoPrestador())) {
					archivosDuplicados.add(entry.getName());
				} else {
					List<Comprobante> comprobantes = getComprobantes(op);

					// ANTICIPOS
					List<Anticipo> anticipos = ComprobanteServiceUtil
							.getInstance().getAnticiposARendir(
									new Empresa(op.getAcreedor().getCuit(),
											"000"), 0, WebKeysGlobal.OSPIM);
					BigDecimal totalAnti = BigDecimal.ZERO;

										
					if (anticipos != null) {
						boolean anticipoTomado=false;
						for (Anticipo ant : anticipos) {
							if (!anticipoTomado || totalAnti.compareTo(op.getImporteDeItems()) < 0) {
								//ESTO SACO pagos.add(new OrdenPago.FormaPago(ant));
								List<ComprobanteConcepto> conc = ComprobanteServiceUtil
										.getConceptos(ant.getAnticipo(),
												WebKeysGlobal.OSPIM);
								ant.getAnticipo().setConceptos(conc);
								totalAnti = totalAnti.add(ant.getImporte());
								anticipoTomado=true;
								// comprobantes.add(ant.getAnticipo());
							}
						}
					}

					op.setComprobantes(comprobantes);
					op.setTotalAnticipos(totalAnti);
					
					ordenes.add(op);
				}
			}
		}
	}

	private List<Comprobante> getComprobantes(OrdenPagoOspim op) {
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
				ID_CONCEPTO_LIQ_FARMACIA));
		cc.setImporte(op.getImporteDeItems());
		conceptos.add(cc);
		comprobante.setConceptos(conceptos);
		return comprobantes;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		TraeListasServiceUtil.getCtasBcrias(renderRequest);

		return mapping
				.findForward("portlet.farmacia.editar_uploaded_orden_pago_entry");
	}

}

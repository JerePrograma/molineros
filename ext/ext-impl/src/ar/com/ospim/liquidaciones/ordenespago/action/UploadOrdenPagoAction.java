package ar.com.ospim.liquidaciones.ordenespago.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
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

public class UploadOrdenPagoAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadOrdenPagoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		Comprobante ultimoComprobanteAmtimaAutomatico = ComprobanteServiceUtil
				.getUltimoComprobanteAmtimaAutomatico();

		List<Farmacia> farmacias = TraeListasServiceUtil
				.getFarmacias(actionRequest);
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
			File zip = uploadReq.getFile("zipFile");

			try {
				ZipInputStream in = new ZipInputStream(new FileInputStream(zip));
				ZipEntry entry = in.getNextEntry();
				List<OrdenPagoAmtima> ordenes = new ArrayList<OrdenPagoAmtima>();
				List<String> archivosFallidos = new ArrayList<String>();
				List<String> archivosDuplicados = new ArrayList<String>();
				List<String> archivosSinCuit = new ArrayList<String>();
				while (entry != null) {
					String name = entry.getName();
					logger.debug(name);
					if (name.toLowerCase().endsWith(".txt")) {
						processEntry(in, entry, ordenes, archivosSinCuit,
								archivosFallidos, archivosDuplicados, farmacias);
					}
					entry = in.getNextEntry();
				}
				in.close();

				Collections.sort(ordenes, new Comparator<OrdenPagoAmtima>() {
					public int compare(OrdenPagoAmtima o1, OrdenPagoAmtima o2) {
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
				if (ultimoComprobanteAmtimaAutomatico != null) {
					nro = Integer.valueOf(ultimoComprobanteAmtimaAutomatico
							.getNroComprobante());
				}

				nro++;
				int i = 0;
				for (OrdenPagoAmtima o : ordenes) {
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
			List<OrdenPagoAmtima> ordenes, List<String> archivosSinCuit,
			List<String> archivosFallidos, List<String> archivosDuplicados,
			List<Farmacia> farmacias) throws UnsupportedEncodingException,
			IOException, SystemException {

		boolean error = false;
		OrdenPagoAmtima op = new OrdenPagoAmtima();
		List<ItemOrdenPago> items = new ArrayList<ItemOrdenPago>();
		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));
		while ((line = reader.readLine()) != null) {
			try {
				ItemOrdenPago item = new ItemOrdenPago(line);
				op.setAFavorDe(item.getPrestador());
				op.setFechaDesde(item.getPeriodo());
				op.setFechaHasta(item.getPeriodo());
				op.setObservaciones(OrdenPagoAmtima.CONCEPTO_FARMACIA);
				items.add(item);
			} catch (Exception e) {
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
			}
		}
		if (!error) {
			op.setItems(items);
			logger.debug(op.getAFavorDe());
			if (op.getItems() != null && !op.getItems().isEmpty()) {
				if (OrdenPagoServiceUtil.existeOPAmtima(op.getItems().get(0)
						.getPeriodo(), op.getItems().get(0)
						.getCodigoPrestador())) {
					archivosDuplicados.add(entry.getName());
				} else {
					List<Comprobante> comprobantes = getComprobantes(op);
					op.setComprobantes(comprobantes);
					ordenes.add(op);
				}
			} else {
				logger.debug("Esta empty");
			}
		}
	}

	private List<Comprobante> getComprobantes(OrdenPagoAmtima op) {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Comprobante comprobante = new Comprobante();
		comprobante.setCuit(op.getAcreedor().getCuit());		
		comprobante.setTipoComprobante("LIQ");
		comprobante.setNroComprobante(String.valueOf(0));
		comprobante.setFechaRecepcion(new Date());
		comprobante.setPtoVenta(1);
		comprobante.setSucuComprobante(1);
		comprobante.setLetraComprobante("");
		comprobante.setImporteComprobante(op.getImporteDeItems());
		comprobantes.add(comprobante);
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		ComprobanteConcepto cc = new ComprobanteConcepto(new Concepto(1));
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

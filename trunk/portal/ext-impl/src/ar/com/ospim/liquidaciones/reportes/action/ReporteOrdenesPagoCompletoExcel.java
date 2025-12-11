package ar.com.ospim.liquidaciones.reportes.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteOrdenesPagoCompletoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteOrdenesPagoCompletoExcel.class);

	static HSSFCellStyle styleDateLeft = null;
	static HSSFCellStyle styleAllLeft = null;
	static HSSFCellStyle styleBoldLeft = null;
	static HSSFCellStyle styleMoneyLeft = null;
	static HSSFCellStyle styleHeaderRight = null;
	static HSSFCellStyle styleDateRight = null;
	static HSSFCellStyle styleAllRight = null;
	static HSSFCellStyle styleAllRightBottom = null;
	static HSSFCellStyle styleBoldRight = null;
	static HSSFCellStyle styleMoneyRight = null;
	static HSSFCellStyle styleHeader = null;
	static HSSFCellStyle styleAll = null;
	static HSSFCellStyle styleTotalesL = null;
	static HSSFCellStyle styleTotalesR = null;
	static HSSFCellStyle styleTotales = null;
	static HSSFCellStyle styleTotalesMoneyR = null;
	static HSSFCellStyle styleMoneyRightTop = null;
	static HSSFCellStyle styleAllLeftTop = null;
	static HSSFCellStyle styleAllRightTop = null;
	static HSSFCellStyle styleDateTop = null;
	static HSSFCellStyle styleAllTop = null;

	public static HSSFWorkbook generaReporteOPs(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		List<Seccional> seccionales = TraeListasServiceUtil.getSeccionales();

		int id_prestador = ParamUtil.getInteger(req, "id_prestador");
		String cuit = ParamUtil.getString(req, "cuit_prestador");
		// String nombre_prestador = ParamUtil.getString(req,
		// "nombre_prestador");
		String compro_tipo = ParamUtil.getString(req, "compro_tipo");
		String compro_letra = ParamUtil.getString(req, "compro_letra");
		int compro_sucur = ParamUtil.getInteger(req, "sucu");
		String compro_nro = ParamUtil.getString(req, "compro_nro");
		String compro_sucu = ParamUtil.getString(req, "sucur_prestador");
		int entidad = ParamUtil.getInteger(req, "entidad");

		int cta = ParamUtil.getInteger(req, "cta_bancaria");
		int nro_lote = ParamUtil.getInteger(req, "nro_lote");

		boolean soloDeBaja = ParamUtil.getBoolean(req, "solo_de_baja");
		boolean incluirTotales = ParamUtil.getBoolean(req, "incluir_totales");
		boolean formatoRecep = ParamUtil.getBoolean(req, "formato_recepcion");

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			List<OrdenPago> reporte = null;

			if (entidad != WebKeysGlobal.OSPIM) {
				reporte = OrdenPagoServiceUtil.reporteOrdenPagoCompleto(
						fechaIni, fechaFin, id_prestador, cuit, compro_sucu,
						compro_tipo, compro_nro, compro_sucur, compro_letra,
						entidad);

			} else {
				reporte = OrdenPagoServiceUtil.reporteOrdenPagoOspimCompleto(
						fechaIni, fechaFin, id_prestador, cuit, compro_sucu,
						compro_tipo, compro_nro, compro_sucur, compro_letra,
						nro_lote);
			}

			Collections.sort(reporte, new Comparator<ItemSubdiarioEgreso>() {
				public int compare(ItemSubdiarioEgreso arg0,
						ItemSubdiarioEgreso arg1) {
					int compareTo = arg0.getNumeroOP().compareTo(
							arg1.getNumeroOP());
					if (compareTo == 0) {
						if (arg0.getBaja_fecha() != null
								&& arg1.getBaja_fecha() != null) {
							compareTo = arg0.getBaja_fecha().compareTo(
									arg1.getBaja_fecha());
						} else if (arg0.getBaja_fecha() != null
								&& arg1.getBaja_fecha() == null) {
							compareTo = 1;
						} else if (arg0.getBaja_fecha() == null
								&& arg1.getBaja_fecha() != null) {
							compareTo = -1;
						}
					}
					return compareTo;
				}

			});

			return generarReporte(fechaIni, fechaFin, reporte, seccionales,
					cta, soloDeBaja, incluirTotales, formatoRecep, entidad);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<OrdenPago> reporte, List<Seccional> seccionales, int cta,
			boolean soloDeBaja, boolean incluirTotales, boolean formatoRecep,
			int entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();

		if (formatoRecep) {

			styleTotales = getStyleAllWithBorder(wb, 10);
			styleTotalesMoneyR = getStyleMoneyWithBorder(wb, 10);
			styleMoneyRightTop = getStyleMoneyWithBorder(wb, 10);
			styleAllLeftTop = getStyleAllWithBorder(wb, 10);
			styleAllRightTop = getStyleAllWithBorder(wb, 10);
			styleDateTop = getStyleDateWithBorder(wb, 10);
			styleAllTop = getStyleAllWithBorder(wb, 10);
			styleTotalesR = getStyleMoneyBoldWithBorder(wb, 10);
			styleTotalesL = getStyleBoldWithBorder(wb, 10);
			styleAll = getStyleAllWithBorder(wb, 10);
			styleDateLeft = getStyleDateWithBorder(wb, 10);
			styleAllLeft = getStyleAllWithBorder(wb, 10);
			styleBoldLeft = getStyleBoldWithBorder(wb, 10);
			styleMoneyLeft = getStyleMoneyWithBorder(wb, 10);
			styleHeaderRight = getStyleHeaderWithBorder(wb, 10);
			styleDateRight = getStyleDateWithBorder(wb, 10);
			styleAllRight = getStyleAllWithBorder(wb, 10);
			styleBoldRight = getStyleBoldWithBorder(wb, 10);
			styleMoneyRight = getStyleMoneyWithBorder(wb, 10);
			styleHeader = getStyleHeaderWithBorder(wb, 10);
		} else {
			styleDateLeft = getStyleDate(wb, 10);
			styleDateLeft.setBorderLeft(BorderStyle.THIN);
			styleAllLeft = getStyleAll(wb, 10);
			styleAllLeft.setBorderLeft(BorderStyle.THIN);
			styleBoldLeft = getStyleBold(wb, 10);
			styleBoldLeft.setBorderLeft(BorderStyle.THIN);
			styleMoneyLeft = getStyleMoney(wb, 10);
			styleMoneyLeft.setBorderLeft(BorderStyle.THIN);
			styleHeaderRight = getStyleHeader(wb, 10);
			styleHeaderRight.setBorderRight(BorderStyle.THIN);
			styleHeaderRight.setBorderTop(BorderStyle.THIN);
			styleDateRight = getStyleDate(wb, 10);
			styleDateRight.setBorderRight(BorderStyle.THIN);
			styleAllRight = getStyleAll(wb, 10);
			styleAllRight.setBorderRight(BorderStyle.THIN);
			styleBoldRight = getStyleBold(wb, 10);
			styleAllRightBottom = getStyleAll(wb, 10);
			styleAllRightBottom
					.setBorderRight(BorderStyle.THIN);
			styleAllRightBottom
					.setBorderBottom(BorderStyle.THIN);
			styleBoldRight.setBorderRight(BorderStyle.THIN);
			styleMoneyRight = getStyleMoney(wb, 10);
			styleMoneyRight.setBorderRight(BorderStyle.THIN);
			styleHeader = getStyleHeader(wb, 10);
			styleHeader.setBorderTop(BorderStyle.THIN);
			styleAll = getStyleAll(wb, 10);
			styleTotalesL = getStyleBold(wb, 10);
			styleTotalesL.setBorderLeft(BorderStyle.THIN);
			styleTotalesL.setBorderBottom(BorderStyle.THIN);
			styleTotalesR = getStyleMoneyBold(wb, 10);
			styleTotalesR.setBorderRight(BorderStyle.THIN);
			styleTotalesR.setBorderBottom(BorderStyle.THIN);
			styleTotales = getStyleAll(wb, 10);
			styleTotales.setBorderBottom(BorderStyle.THIN);
			styleTotalesMoneyR = getStyleMoney(wb, 10);
			styleTotalesMoneyR.setBorderRight(BorderStyle.THIN);
			styleTotalesMoneyR
					.setBorderBottom(BorderStyle.THIN);

			styleMoneyRightTop = getStyleMoney(wb, 10);
			styleMoneyRightTop.setBorderRight(BorderStyle.THIN);
			styleMoneyRightTop.setBorderTop(BorderStyle.THIN);

			styleAllLeftTop = getStyleAll(wb, 10);
			styleAllLeftTop.setBorderLeft(BorderStyle.THIN);
			styleAllLeftTop.setBorderTop(BorderStyle.THIN);

			styleAllRightTop = getStyleAll(wb, 10);
			styleAllRightTop.setBorderRight(BorderStyle.THIN);
			styleAllRightTop.setBorderTop(BorderStyle.THIN);

			styleDateTop = getStyleDate(wb, 10);
			styleDateTop.setBorderTop(BorderStyle.THIN);

			styleAllTop = getStyleAll(wb, 10);
			styleAllTop.setBorderTop(BorderStyle.THIN);
		}

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 0.2);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Ordenes de Pago - Desde:"
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " - Hasta:"
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
		sheet.getFooter().setRight(
				HSSFFooter.page() + "/" + HSSFFooter.numPages() + " - "
						+ DateUtils.format(new Date(), DateUtils.SHORT));

		Totales totales = new Totales();

		int i = 1;
		i = generarHeader(sheet, i, wb, formatoRecep, entidad);
		for (OrdenPago repo : reporte) {
			if (!soloDeBaja || repo.getBaja_fecha() != null) {
				if (tienePagosACta(repo, cta)) {
					i = generarDatos(sheet, i, repo, wb, seccionales, totales,
							cta, incluirTotales, formatoRecep, entidad);
				}
			}
		}

		HSSFCellStyle styleAllTop = getStyleAll(wb);
		styleAllTop.setBorderTop(BorderStyle.THIN);

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleAllTop);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));

		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		i++;
		HSSFRow rowTotales1 = sheet.createRow(i);
		HSSFCell createCell = rowTotales1.createCell(0);
		createCell.setCellValue(new HSSFRichTextString("Total comprobantes"));
		createCell.setCellStyle(styleBold);

		HSSFCell createCell1 = rowTotales1.createCell(2);
		createCell1.setCellValue(totales.getTotalComprobantes().doubleValue());
		createCell1.setCellStyle(styleMoney);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 3));

		i++;
		HSSFRow rowTotales2 = sheet.createRow(i);
		HSSFCell createCel2 = rowTotales2.createCell(0);
		createCel2.setCellValue(new HSSFRichTextString("Total conceptos"));
		createCel2.setCellStyle(styleBold);

		HSSFCell createCell2 = rowTotales2.createCell(2);
		createCell2.setCellValue(totales.getTotalConceptos().doubleValue());
		createCell2.setCellStyle(styleMoney);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 3));

		i++;
		HSSFRow rowTotales3 = sheet.createRow(i);
		HSSFCell createCel3 = rowTotales3.createCell(0);
		createCel3.setCellValue(new HSSFRichTextString("Total formas de pago"));
		createCel3.setCellStyle(styleBold);

		HSSFCell createCell3 = rowTotales3.createCell(2);
		createCell3.setCellValue(totales.getTotalPagos().doubleValue());
		createCell3.setCellStyle(styleMoney);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 3));

		sheet.setColumnWidth(0, 1792);
		sheet.setColumnWidth(1, 3000);
		sheet.setColumnWidth(2, 6000);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 6000);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);

		return wb;
	}

	private static boolean tienePagosACta(OrdenPago repo, int cta) {
		if (cta == 0) {
			return true;
		}
		int cantCuentaIncorrecta = 0;
		int cantCuentaCorrecta = 0;
		if (repo.getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : repo.getFormaPago()) {
				if (fp.getCuentaBancaria() != null
						&& fp.getCuentaBancaria().getId_cuenta_bcria() > 0
						&& fp.getImporte() != null
						&& fp.getImporte().doubleValue() > 0) {
//					if (!(fp.getPago() instanceof RetencionGanancias) && 
//						fp.getCuentaBancaria().getId_cuenta_bcria() != cta) {
//						cantCuentaIncorrecta++;
//					} else {
//						cantCuentaCorrecta++;
//					}
					if (fp.getCuentaBancaria().getId_cuenta_bcria() == cta) {
						cantCuentaCorrecta++;
					} else {
						cantCuentaIncorrecta++;
					}
				}
			}
			return /*cantCuentaIncorrecta == 0 &&*/ cantCuentaCorrecta > 0;
		} else {
			return false;
		}
	}

	private static int generarDatos(HSSFSheet sheet, int i, OrdenPago repo,
			HSSFWorkbook wb, List<Seccional> seccionales, Totales totales,
			int ctaFiltro, boolean incluirTotales, boolean formatoRecep,
			int entidad) {

		int cont = 0;
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell1 = row.createCell(cont++);
		cell1.setCellValue(repo.getId());
		cell1.setCellStyle(styleAllLeftTop);
		boolean anulado = false;
		if (repo.getBaja_fecha() != null) {
			anulado = true;
		}

		if (null != repo.getObservaciones()
				&& repo.getObservaciones().equals("ANULADAMISMODIA")) {
			HSSFCell cell = row.createCell(cont++);
			cell.setCellValue(new HSSFRichTextString("ANULADA"));
			// COMPLETO EL RECUADRO FINAL
			HSSFCell createCell11 = row.createCell(11);
			createCell11.setCellValue(new HSSFRichTextString(" "));
			createCell11.setCellStyle(styleAllRightBottom);

			return i + 1;
		} else {
			HSSFCell cell = row.createCell(cont++);
			cell.setCellValue(repo.getAlta_fecha());
			cell.setCellStyle(styleDateTop);

			HSSFCell cellAcre = row.createCell(cont++);
			String cuit = repo.getAcreedor().getCuit();
			String sucursal = repo.getAcreedor().getSucursal();
			String acreedor = repo.getAcreedor().getRazon_soc();
			if (repo.getSeccional() != null && repo.getSeccional().getId() != 0) {
				sucursal = String.valueOf(repo.getSeccional().getId());
				acreedor = seccionales.get(
						seccionales.indexOf(repo.getSeccional()))
						.getDescripcion();
			}
			cellAcre.setCellValue(new HSSFRichTextString((cuit != null ? cuit
					: "") + (sucursal != null ? "-" + sucursal : "")));
			cellAcre.setCellStyle(styleAllTop);

			if (!formatoRecep) {
				HSSFCell cellAcreRZ = row.createCell(cont++);
				if (repo.getBaja_fecha() == null) {
					cellAcreRZ.setCellValue(new HSSFRichTextString(
							acreedor != null ? acreedor : ""));
				} else {
					cellAcreRZ.setCellValue(new HSSFRichTextString(
							acreedor != null ? "ANULACIÓN - " + acreedor
									: "ANULACIÓN"));
				}
				cellAcreRZ.setCellStyle(styleAllRightTop);
			}

			HSSFCell cellaFav = row.createCell(cont++);
			cellaFav.setCellValue(new HSSFRichTextString(repo.getAFavorDe()));
			cellaFav.setCellStyle(styleAllRightTop);

			List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
			// COMPROBANTES
			int indexComprobantes = i;
			BigDecimal totalCompr = BigDecimal.ZERO;
			int contC = cont;
			
			if(entidad == WebKeysGlobal.UOMA){
				try {
					if(repo.getComprobantes() != null){
					  if("VAR".equalsIgnoreCase(repo.getComprobantes().get(0).getTipoComprobante())){	
					     List<ComprobanteCajaChica>cch =CajaChicaServiceUtil.comprobantesPorOP(entidad, repo.getId());
					     if(cch.size()>0){
					    	for(ComprobanteCajaChica cc:cch){ 
						      Comprobante c = new Comprobante();
						      c.setTipoComprobante(cc.getTipoComprobante());
						      c.setPtoVenta(cc.getPtoVenta());
						      c.setNroComprobante(cc.getNroComprobante());
						      c.setSucuComprobante(cc.getSucuComprobante());
						      c.setFechaRecepcion(cc.getFechaEmision());
						      c.setImporteComprobante(BigDecimal.ZERO);
						      c.setCuit(cc.getCuit());
						      c.setLetraComprobante(cc.getLetraComprobante());
						      c.setDebitoParaEgreso(repo.getComprobantes().get(0).isDebitoParaEgreso());
						      c.setCuitEmisor(repo.getComprobantes().get(0).getCuitEmisor());
						      repo.getComprobantes().add(c);
					    	}  
					     }
					  }   
					}  
				} catch (SystemException e) {}
			}
			
			
			if (repo.getComprobantes() != null) {
				for (Comprobante c : repo.getComprobantes()) {
					cont = contC;
					HSSFRow rowComp = null;
					if (indexComprobantes == i) {
						rowComp = row;
					} else {
						rowComp = sheet.createRow(indexComprobantes);
					}
					indexComprobantes++;

					HSSFCell cellCompro = rowComp.createCell(cont++);
					cellCompro
							.setCellValue(new HSSFRichTextString(c.toString()));
					if (indexComprobantes - 1 == i) {
						cellCompro.setCellStyle(styleAllLeftTop);
					} else {
						cellCompro.setCellStyle(styleAllLeft);
					}
					if (!formatoRecep) {
						HSSFCell cellImpCompro = rowComp.createCell(cont++);
						if (anulado || c.isDebitoParaEgreso()) {
							cellImpCompro.setCellValue(c
									.getImporteComprobante().negate()
									.doubleValue());
						} else {
							cellImpCompro.setCellValue(c
									.getImporteComprobante().doubleValue());
						}
						if (indexComprobantes - 1 == i) {
							cellImpCompro.setCellStyle(styleMoneyRightTop);
						} else {
							cellImpCompro.setCellStyle(styleMoneyRight);
						}
					}

					if (c.isDebitoParaEgreso()) {
						totalCompr = totalCompr.subtract(c
								.getImporteComprobante());
					} else {
						totalCompr = totalCompr.add(c.getImporteComprobante());
					}
					if (c.getConceptos() != null) {
						for (ComprobanteConcepto cc : c.getConceptos()) {
							int indexOf = conceptos.indexOf(cc);
							BigDecimal importe = cc.getImporte();
							// si es NDB que resta, o un anticipo a rendir
							if (c.isDebitoParaEgreso()
									|| (c.getTipoComprobante().equals("ANT") && c
											.getImporteComprobante().compareTo(
													BigDecimal.ZERO) < 0)) {

								importe = importe.negate();
							}
							if (indexOf != -1) {
								ComprobanteConcepto comprobanteConcepto = conceptos
										.get(indexOf);
								BigDecimal add = comprobanteConcepto
										.getImporte().add(importe);
								comprobanteConcepto.setImporte(add);
							} else {
								conceptos.add(new ComprobanteConcepto(cc
										.getConceptoComprobante(), importe));
							}
						}
					}
				}
			} else if (repo.getComprobantes() == null) {
				HSSFCell cellCompro = row.createCell(cont++);
				cellCompro.setCellValue(new HSSFRichTextString(" "));
				cellCompro.setCellStyle(styleAllLeftTop);

				HSSFCell cellImpCompro = row.createCell(cont++);
				cellImpCompro.setCellValue(new HSSFRichTextString(" "));
				cellImpCompro.setCellStyle(styleAllRightTop);

			}

			// CONCEPTOS
			int indexConceptos = i;
			BigDecimal totalConceptos = BigDecimal.ZERO;
			contC = cont;
			for (ComprobanteConcepto cc : conceptos) {
				cont = contC;
				HSSFRow rowConcepto = null;
				if (indexConceptos == i) {
					rowConcepto = row;
				} else {
					rowConcepto = sheet.getRow(indexConceptos);
					if (rowConcepto == null) {
						rowConcepto = sheet.createRow(indexConceptos);
					}
				}
				indexConceptos++;
				HSSFCell cellConc = rowConcepto.createCell(cont++);
				cellConc.setCellValue(new HSSFRichTextString(cc
						.getConceptoComprobante().getDescripcion()));
				if (indexConceptos - 1 == i) {
					cellConc.setCellStyle(styleAllLeftTop);
				} else {
					cellConc.setCellStyle(styleAllLeft);
				}
				if (!formatoRecep) {

					HSSFCell cellImpConc = rowConcepto.createCell(cont++);
					if (anulado) {
						cellImpConc.setCellValue(cc.getImporte().negate()
								.doubleValue());
					} else {
						cellImpConc.setCellValue(cc.getImporte().doubleValue());
					}
					if (indexConceptos - 1 == i) {
						cellImpConc.setCellStyle(styleMoneyRightTop);
					} else {
						cellImpConc.setCellStyle(styleMoneyRight);
					}
				}

				totalConceptos = totalConceptos.add(cc.getImporte());
			}
			if (conceptos.size() == 0) {
				HSSFCell cellConc = row.createCell(cont++);
				cellConc.setCellValue(new HSSFRichTextString(" "));
				cellConc.setCellStyle(styleAllLeftTop);

				HSSFCell cellImpConc = row.createCell(cont++);
				cellImpConc.setCellValue(new HSSFRichTextString(" "));
				cellImpConc.setCellStyle(styleAllRightTop);

			}

			// PAGOS
			int indexPagos = i;
			BigDecimal totalPagos = BigDecimal.ZERO;
			contC = cont;
			if (repo.getFormaPago() != null) {
				for (OrdenPago.FormaPago fp : repo.getFormaPago()) {
					cont = contC;
					if (fp.getTipo().equals("Anticipo")
							&& entidad == WebKeysGlobal.OSPIM) {						
						cont=cont+3;
						continue;
					}
					if (ctaFiltro != 0
							&& (fp.getCuentaBancaria() == null || (fp
									.getCuentaBancaria() != null && fp
									.getCuentaBancaria().getId_cuenta_bcria() != ctaFiltro &&
									!(fp.getPago() instanceof RetencionGanancias)))) {
						continue;
					}
					HSSFRow rowPago = null;
					if (indexPagos == i) {
						rowPago = row;
					} else {
						rowPago = sheet.getRow(indexPagos);
						if (rowPago == null) {
							rowPago = sheet.createRow(indexPagos);
						}
					}
					indexPagos++;
					HSSFCell cellForma = rowPago.createCell(cont++);
					CuentaBancaria cuentaBancaria = fp.getCuentaBancaria();
					String cta = "";
					if (cuentaBancaria != null
							&& cuentaBancaria.getDescripcion() != null
							&& cuentaBancaria.getId_cuenta_bcria() != 99) {
						cta = " " + cuentaBancaria.getDescripcion() + " "
								+ cuentaBancaria.getNro_cuenta() + "/"
								+ cuentaBancaria.getSucursal();
					}
					if (anulado && fp.getPago().getBaja_fecha() != null) {
						cellForma.setCellValue(new HSSFRichTextString(
								"ANULADO-" + fp.getTipo() + cta));
					} else {
						cellForma.setCellValue(new HSSFRichTextString(fp
								.getTipo() + cta));
					}

					if (indexPagos - 1 == i) {
						cellForma.setCellStyle(styleAllLeftTop);
					} else {
						cellForma.setCellStyle(styleAllLeft);
					}

					HSSFCell cellNro = rowPago.createCell(cont++);
					String numeroStr = fp.getNumeroStr();
					if (StringUtils.checkEmpty(numeroStr)) {
						numeroStr = "xxxxxxxx";
					}
					cellNro.setCellValue(new HSSFRichTextString(numeroStr));
					if (indexPagos - 1 == i) {
						cellNro.setCellStyle(styleAllTop);
					} else {
						cellNro.setCellStyle(styleAll);
					}
					BigDecimal importePago = BigDecimal.ZERO;
					HSSFCell cellImpoPago = rowPago.createCell(cont++);
					if (fp.getImporte() != null) {
						if (anulado) {
							if (fp.getTipo().equals("Anticipo")) {
								cellImpoPago.setCellValue(fp.getImporte()
										.doubleValue());
								importePago = importePago.add(fp.getImporte()
										.negate());
							} else {
								cellImpoPago.setCellValue(fp.getImporte()
										.negate().doubleValue());
								importePago = importePago.add(fp.getImporte());
							}
						} else {

							if (fp.getTipo().equals("Anticipo")) {
								importePago = fp.getImporte().negate();
							} else {
								importePago = fp.getImporte();
							}
							cellImpoPago
									.setCellValue(importePago.doubleValue());
						}
						totalPagos = totalPagos.add(importePago);
					}

					cellImpoPago.setCellStyle(styleMoneyRightTop);

				}
			} else if (repo.getFormaPago() == null) {
				HSSFCell cellForma = row.createCell(cont++);
				cellForma.setCellValue(new HSSFRichTextString(" "));
//				cellForma.setCellStyle(styleAllLeftTop);
				cellForma.setCellStyle(styleAll);
								
				HSSFCell cellImpoPago = row.createCell(++cont);
				cellImpoPago.setCellValue(new HSSFRichTextString(" "));
//				cellImpoPago.setCellStyle(styleAllRightTop);
				cellImpoPago.setCellStyle(styleAll);
				
				sheet.addMergedRegion(new CellRangeAddress(i, i, cont, ++cont));
			}

			if (entidad == WebKeysGlobal.OSPIM) {
				if (!formatoRecep) {
					HSSFCell cellObsInterna = row.createCell(cont++);
					cellObsInterna.setCellValue(new HSSFRichTextString(repo
							.getObsInterna()));
				}
				HSSFCell cellDestino = row.createCell(cont++);
				cellDestino.setCellValue(new HSSFRichTextString(repo
						.getDestino()));
				cellDestino.setCellStyle(styleAll);

				HSSFCell cellIdLote = row.createCell(cont++);
				cellIdLote.setCellValue(repo.getIdLote());
				cellIdLote.setCellStyle(styleAll);

				if (!formatoRecep) {
					HSSFCell cellfechaFirma = row.createCell(cont++);
					cellfechaFirma.setCellStyle(styleDateTop);

					if (null != repo.getFechaFirma()) {
						cellfechaFirma.setCellValue(repo.getFechaFirma());
					} else {
						cellfechaFirma.setCellValue(new HSSFRichTextString(""));
					}
				}
			}
			// TOTALES (alineados)
			if (anulado) {
				totales.setTotalComprobantes(totales.getTotalComprobantes()
						.subtract(totalCompr));
				totales.setTotalConceptos(totales.getTotalConceptos().subtract(
						totalConceptos));
				totales.setTotalPagos(totales.getTotalPagos().subtract(
						totalPagos));
			} else {
				totales.setTotalComprobantes(totalCompr.add(totales
						.getTotalComprobantes()));
				totales.setTotalConceptos(totalConceptos.add(totales
						.getTotalConceptos()));
				totales.setTotalPagos(totalPagos.add(totales.getTotalPagos()));
			}

			int max = Math.max(indexComprobantes, indexConceptos);
			max = Math.max(max, indexPagos);

			max = completarFilasVacias(sheet, wb, i, indexComprobantes,
					indexConceptos, indexPagos);

			cont = 0;
			if (incluirTotales && (max - i > 1)) {
				HSSFRow rowTotales = sheet.createRow(max);

				HSSFCell createCell = rowTotales.createCell(cont++);
				createCell.setCellValue(new HSSFRichTextString(" "));
				createCell.setCellStyle(styleTotalesL);

				HSSFCell createCell1 = rowTotales.createCell(cont++);
				createCell1.setCellValue(new HSSFRichTextString(" "));
				createCell1.setCellStyle(styleTotales);

				HSSFCell createCell2 = rowTotales.createCell(cont++);
				createCell2.setCellValue(new HSSFRichTextString(" "));
				createCell2.setCellStyle(styleTotales);

				if (!formatoRecep) {
					HSSFCell createCell3 = rowTotales.createCell(cont++);
					createCell3.setCellValue(new HSSFRichTextString(" "));
					createCell3.setCellStyle(styleTotales);
				}

				HSSFCell createCell31 = rowTotales.createCell(cont++);
				createCell31.setCellValue(new HSSFRichTextString(" "));
				createCell31.setCellStyle(styleTotales);

				HSSFCell cellCompro = rowTotales.createCell(cont++);
				cellCompro.setCellValue(new HSSFRichTextString("Totales"));
				cellCompro.setCellStyle(styleTotalesL);
				if (!formatoRecep) {
					HSSFCell cellImpCompro = rowTotales.createCell(cont++);
					if (anulado) {
						cellImpCompro.setCellValue(totalCompr.negate()
								.doubleValue());
					} else {
						cellImpCompro.setCellValue(totalCompr.doubleValue());

					}
					cellImpCompro.setCellStyle(styleTotalesR);
				}
				HSSFCell cellConc = rowTotales.createCell(cont++);
				cellConc.setCellValue(new HSSFRichTextString("Totales"));
				cellConc.setCellStyle(styleTotalesL);

				HSSFCell cellImpConc = rowTotales.createCell(cont++);
				if (anulado) {
					cellImpConc.setCellValue(totalConceptos.negate()
							.doubleValue());
				} else {
					cellImpConc.setCellValue(totalConceptos.doubleValue());
				}
				cellImpConc.setCellStyle(styleTotalesMoneyR);

				HSSFCell cellForma = rowTotales.createCell(cont++);
				cellForma.setCellValue(new HSSFRichTextString("Totales"));
				cellForma.setCellStyle(styleTotalesL);
				//sheet.addMergedRegion(new CellRangeAddress(max, max, cont,
				//		++cont));

				++cont;
				HSSFCell cellImpoPago = rowTotales.createCell(cont++);
				if (anulado) {
					cellImpoPago
							.setCellValue(totalPagos.negate().doubleValue());
				} else {
					cellImpoPago.setCellValue(totalPagos.doubleValue());
				}
				cellImpoPago.setCellStyle(styleTotalesMoneyR);
				max++;
			}

			if (max == i) {
				max++;
			}
			return max;
		}

	}

	private static int completarFilasVacias(HSSFSheet sheet, HSSFWorkbook wb,
			int primerFila, int indexComprobantes, int indexConceptos,
			int indexPagos) {


		int max = Math.max(indexComprobantes, indexConceptos);
		max = Math.max(max, indexPagos);
		int fin = max - 1;

		if (indexComprobantes <= fin) {
			for (int i = indexComprobantes; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(4) == null) {
					HSSFCell createCell = row.createCell(4);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllLeft);
				}
				if (row.getCell(5) == null) {
					HSSFCell createCell2 = row.createCell(5);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleMoneyRight);
				}
			}
		}
		if (indexConceptos <= fin) {
			for (int i = indexConceptos; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(6) == null) {
					HSSFCell createCell = row.createCell(6);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllLeft);
					HSSFCell createCell2 = row.createCell(7);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleMoneyRight);
				}
			}
		}
		if (indexPagos <= fin) {
			for (int i = indexPagos; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(8) == null) {
					HSSFCell createCell = row.createCell(8);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllRight);
					if (row.getCell(9) == null) {
						HSSFCell createCell2 = row.createCell(9);
						createCell2.setCellValue(new HSSFRichTextString(" "));
						createCell2.setCellStyle(styleAllLeft);
					}
					if (row.getCell(10) == null) {
						HSSFCell createCell3 = row.createCell(10);
						createCell3.setCellValue(new HSSFRichTextString(" "));
					}

				}
			}
		}

		// primerFila+1 porque en la primera siempre escribo algo, asiq debo
		// completar de ahi en adelante
		if ((primerFila + 1) <= fin) {
			for (int i = (primerFila + 1); i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(0) == null) {
					HSSFCell createCell = row.createCell(0);
					createCell.setCellStyle(styleAllLeft);
					if (row.getCell(4) == null) {
						HSSFCell createCell4 = row.createCell(4);
						createCell4.setCellStyle(styleAllLeft);
					}
					if (row.getCell(5) == null) {
						HSSFCell createCell5 = row.createCell(5);
						createCell5.setCellStyle(styleAllLeft);
					}
					if (row.getCell(11) == null) {
						HSSFCell createCell11 = row.createCell(11);
						createCell11.setCellValue(new HSSFRichTextString(" "));
						createCell11.setCellStyle(styleAllRight);
					}

				}
			}
		}
		return (fin + 1);
	}

	private static int generarHeader(HSSFSheet sheet, int i, HSSFWorkbook wb,
			boolean formatoRecep, int entidad) {

		HSSFCellStyle styleAllBorder = getStyleAllWithBorder(wb, 10);

		int col = 0;

		HSSFRow row = sheet.createRow(i);

		HSSFCell cell1 = row.createCell(col++);
		cell1.setCellValue(new HSSFRichTextString("OP"));
		cell1.setCellStyle(styleAllBorder);

		HSSFCell cell = row.createCell(col++);
		cell.setCellValue(new HSSFRichTextString("Fecha"));
		cell.setCellStyle(styleAllBorder);

		HSSFCell cellAcreed = row.createCell(col++);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleAllBorder);

		if (!formatoRecep) {
			HSSFCell cellRaz = row.createCell(col++);
			cellRaz.setCellValue(new HSSFRichTextString("Razon Social"));
			cellRaz.setCellStyle(styleAllBorder);
		}

		HSSFCell cellAfav = row.createCell(col++);
		cellAfav.setCellValue(new HSSFRichTextString("A FAVOR DE"));
		cellAfav.setCellStyle(styleAllBorder);

		HSSFCell cell3 = row.createCell(col++);
		cell3.setCellValue(new HSSFRichTextString("Comprobante"));
		cell3.setCellStyle(styleAllBorder);

		if (!formatoRecep) {
			HSSFCell cellBajaOP = row.createCell(col++);
			cellBajaOP.setCellValue(new HSSFRichTextString("Importe Comp"));
			cellBajaOP.setCellStyle(styleAllBorder);
		}

		HSSFCell cellConcepto = row.createCell(col++);
		cellConcepto.setCellValue(new HSSFRichTextString("Concepto"));
		cellConcepto.setCellStyle(styleAllBorder);

		if (!formatoRecep) {
			HSSFCell cellImpConc = row.createCell(col++);
			cellImpConc.setCellValue(new HSSFRichTextString("Importe Conc."));
			cellImpConc.setCellStyle(styleAllBorder);
		}
		HSSFCell cellFP = row.createCell(col++);
		cellFP.setCellValue(new HSSFRichTextString("Forma de Pago"));
		cellFP.setCellStyle(styleAllBorder);

		HSSFCell cellVacia = row.createCell(col++);
		cellVacia.setCellValue(new HSSFRichTextString("Nro.FP"));
		cellVacia.setCellStyle(styleAllBorder);

		HSSFCell cellImporteFP = row.createCell(col++);
		cellImporteFP.setCellValue(new HSSFRichTextString("Importe"));
		cellImporteFP.setCellStyle(styleAllBorder);

		if (entidad == WebKeysGlobal.OSPIM) {
			if (!formatoRecep) {
				HSSFCell obsInterna = row.createCell(col++);
				obsInterna.setCellValue(new HSSFRichTextString("Obs. Interna"));
				obsInterna.setCellStyle(styleAllBorder);
			}
			HSSFCell destino = row.createCell(col++);
			destino.setCellValue(new HSSFRichTextString("Destino"));
			destino.setCellStyle(styleAllBorder);

			HSSFCell cellIdLote = row.createCell(col++);
			cellIdLote.setCellValue(new HSSFRichTextString("Nro. Lote"));
			cellIdLote.setCellStyle(styleAllBorder);
			if (!formatoRecep) {
				HSSFCell cellfechaFirna = row.createCell(col++);
				cellfechaFirna.setCellValue(new HSSFRichTextString(
						"Fecha Firma"));
				cellfechaFirna.setCellStyle(styleAllBorder);
			}
		}

		//wb.setRepeatingRowsAndColumns(0, 0, col, i, i);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return ++i;
	}

	private static class Totales {
		private BigDecimal totalComprobantes = BigDecimal.ZERO;
		private BigDecimal totalConceptos = BigDecimal.ZERO;
		private BigDecimal totalPagos = BigDecimal.ZERO;

		public void setTotalComprobantes(BigDecimal totalComprobantes) {
			this.totalComprobantes = totalComprobantes;
		}

		public BigDecimal getTotalComprobantes() {
			return totalComprobantes;
		}

		public void setTotalConceptos(BigDecimal totalConceptos) {
			this.totalConceptos = totalConceptos;
		}

		public BigDecimal getTotalConceptos() {
			return totalConceptos;
		}

		public void setTotalPagos(BigDecimal totalPagos) {
			this.totalPagos = totalPagos;
		}

		public BigDecimal getTotalPagos() {
			return totalPagos;
		}

	}
}

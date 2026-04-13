package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.SubdiarioComprobante;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteSubdiario;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteSubdiarioEgresoExcel extends ReporteSubdiario {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSubdiarioEgresoExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		List<Seccional> seccionales = TraeListasServiceUtil.getSeccionales();

		boolean incluirTotales = ParamUtil.getBoolean(req, "incluir_totales");
		boolean incluirCuadroEgresos = ParamUtil.getBoolean(req,
				"incluir_cuadro");

		boolean incluirReintegros = ParamUtil.getBoolean(req,
				"incluir_reintegros");
		boolean incluirLiquidaciones = ParamUtil.getBoolean(req,
				"incluir_liquidaciones");
		boolean incluirProveedores = ParamUtil.getBoolean(req,
				"incluir_proveedores");
		boolean incluirMovBcrios = ParamUtil.getBoolean(req,
				"incluir_movimientosbancarios");
		boolean incluirObsComprob = ParamUtil.getBoolean(req,
				"incluir_obs_comp");
		int entidad = ParamUtil.getInteger(req, "entidad");

		boolean exportar = ParamUtil.getBoolean(req, "exportar");

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			Date fechaImpre = null;

			try {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				String fechaImpreDia = ParamUtil.getString(req,
						"fechaImpresionDia");
				String fechaImpreMes = ParamUtil.getString(req,
						"fechaImpresionMes");
				fechaImpreMes = String
						.valueOf(Integer.valueOf(fechaImpreMes) + 1);
				String fechaImpreAnio = ParamUtil.getString(req,
						"fechaImpresionAnio");

				fechaImpre = format.parse(fechaImpreDia + "-" + fechaImpreMes
						+ "-" + fechaImpreAnio);

			} catch (NumberFormatException nfe) {
				fechaImpre = new Date();
			}

			List<ItemSubdiarioEgreso> lista = new ArrayList<ItemSubdiarioEgreso>();
			List<? extends ItemSubdiarioEgreso> reporte = null;
			// busca ordenes de pago
			if (entidad != WebKeysGlobal.OSPIM &&  incluirProveedores) {
				reporte = OrdenPagoServiceUtil
						.reporteOrdenPagoCompletoParaSubdiario(fechaIni,
								fechaFin, true, false, incluirReintegros, false,
								entidad, 0);

			} else {
				reporte = OrdenPagoServiceUtil
						.reporteOrdenPagoOspimCompletoParaSubdiario(fechaIni,
								fechaFin, incluirProveedores,
								incluirLiquidaciones, incluirReintegros);
			}
			lista.addAll(reporte);
			// busca movimientos bancarios.
			if (incluirMovBcrios) {
				List<? extends ItemSubdiarioEgreso> reporteParaSubdiario = MovimientoBancarioServiceUtil
						.reporteParaSubdiario(fechaIni, fechaFin, entidad);
				lista.addAll(reporteParaSubdiario);
			}

			Collections.sort(lista, new Comparator<ItemSubdiarioEgreso>() {
				public int compare(ItemSubdiarioEgreso arg0,
						ItemSubdiarioEgreso arg1) {
					// Primero por fecha OP
					int compareTo = arg0.getFecha().compareTo(arg1.getFecha());
					/*
					 * int compareTo = arg0.getNumeroOP().compareTo(
					 * arg1.getNumeroOP());
					 */
					if (compareTo == 0) {
						compareTo = arg0.getNumeroOP().compareTo(
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
					}
					return compareTo;
				}

			});
			return generarReporte(fechaIni, fechaFin, lista, seccionales,
					incluirTotales, incluirCuadroEgresos, entidad, exportar,
					fechaImpre, incluirObsComprob);
		} catch (Exception e) {
			_log.error("Error al generar subdiario egresos", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<? extends ItemSubdiarioEgreso> reporte,
			List<Seccional> seccionales, boolean incluirTotales,
			boolean incluirCuadroEgresos, int entidad, boolean exportar,
			Date fechaImpresion, boolean incluirObsCompr) throws Exception {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);
		// styleHeaderLeft.setBorderLeft(BorderStyle.THIN);
		// styleHeaderLeft.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleDateLeft = getStyleDate(wb);
		// styleDateLeft.setBorderLeft(BorderStyle.THIN);
		HSSFCellStyle styleAllLeft = getStyleAll(wb);
		// styleAllLeft.setBorderLeft(BorderStyle.THIN);
		HSSFCellStyle styleBoldLeft = getStyleBold(wb);
		// styleBoldLeft.setBorderLeft(BorderStyle.THIN);
		HSSFCellStyle styleMoneyLeft = getStyleMoney(wb);
		// styleMoneyLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);
		// styleHeaderRight.setBorderRight(BorderStyle.THIN);
		// styleHeaderRight.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleDateRight = getStyleDate(wb);
		// styleDateRight.setBorderRight(BorderStyle.THIN);
		HSSFCellStyle styleAllRight = getStyleAll(wb);
		// styleAllRight.setBorderRight(BorderStyle.THIN);
		HSSFCellStyle styleBoldRight = getStyleBold(wb);
		// styleBoldRight.setBorderRight(BorderStyle.THIN);
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		// styleMoneyRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
		// styleHeader.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleTotalesL = getStyleBold(wb);
		// styleTotalesL.setBorderLeft(BorderStyle.THIN);
		// styleTotalesL.setBorderBottom(BorderStyle.THIN);
		HSSFCellStyle styleTotalesR = getStyleMoneyBold(wb);
		// styleTotalesR.setBorderRight(BorderStyle.THIN);
		// styleTotalesR.setBorderBottom(BorderStyle.THIN);

		HSSFCellStyle styleTotales = getStyleAll(wb);
		// styleTotales.setBorderBottom(BorderStyle.THIN);

		HSSFCellStyle styleTotalesMoneyR = getStyleMoneyBold(wb);
		// styleTotalesMoneyR.setBorderRight(BorderStyle.THIN);
		// styleTotalesMoneyR.setBorderBottom(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
		// styleMoneyRightTop.setBorderRight(BorderStyle.THIN);
		// styleMoneyRightTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllTop = getStyleAll(wb);
		// styleAllTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllLeftTop = getStyleAll(wb);
		// styleAllLeftTop.setBorderLeft(BorderStyle.THIN);
		// styleAllLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllRightTop = getStyleAll(wb);
		// styleAllRightTop.setBorderRight(BorderStyle.THIN);
		// styleAllRightTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleDateLeftTop = getStyleDate(wb);
		// styleAllLeftTop.setBorderLeft(BorderStyle.THIN);
		// styleDateLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleTotalesMoneyL = getStyleMoneyBold(wb);
		// styleTotalesMoneyL.setBorderLeft(BorderStyle.THIN);
		// styleTotalesMoneyL.setBorderBottom(BorderStyle.THIN);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.LEGAL_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		int i = 0;

		if (entidad == WebKeysGlobal.OSPIM) {
			i = crearHeaderPrincipal(wb, sheet, 11, entidad);
		}
		if (entidad == WebKeysGlobal.UOMA) {
			i = crearHeaderPrincipalUoma(wb, sheet, 11, fechaImpresion);
		}

		i = createTitulosHeader(wb, sheet, i, entidad, fechaIni, fechaFin);

		Totales totales = new Totales();

		if (entidad == WebKeysGlobal.OSPIM) {
			i = generarHeader(wb, sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight);
		} else {
			i = generarHeaderUoma(wb, sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, incluirObsCompr);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 11, 0, i - 1);
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		try {
			for (ItemSubdiarioEgreso repo : reporte) {
				_log.debug("OP " + repo.getNumeroOP());
				i = generarDatos(sheet, i, repo, styleDateLeft, styleDateRight,
						styleAllLeft, styleAllRight, styleBoldLeft,
						styleBoldRight, styleMoneyLeft, styleMoneyRight,
						styleAll, styleDate, styleTotalesL, styleTotalesR,
						styleTotales, styleTotalesMoneyR, styleMoneyRightTop,
						styleAllTop, styleAllLeftTop, styleAllRightTop,
						styleDateLeftTop, styleTotalesMoneyL, seccionales,
						incluirTotales, totales, exportar, entidad, incluirObsCompr);
			}
		} catch (Exception e) {
			throw e;
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleAllTop);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 11));

		// TOTALES
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		i++;
		HSSFRow row0 = sheet.createRow(i);
		HSSFCell cellSubt = row0.createCell(4);
		cellSubt.setCellValue(new HSSFRichTextString("SubTotal"));
		cellSubt.setCellStyle(styleBold);

		HSSFCell cellSubtotalComp = row0.createCell(5);
		cellSubtotalComp.setCellValue(totales.getTotalComprobantes()
				.doubleValue());
		cellSubtotalComp.setCellStyle(styleMoneyBold);

		i++;
		HSSFRow rowAnticipos = sheet.createRow(i);
		HSSFCell cellAnt = rowAnticipos.createCell(4);
		cellAnt.setCellValue(new HSSFRichTextString("Anticipos Rendidos"));
		cellAnt.setCellStyle(styleBold);

		HSSFCell cellAntImporte = rowAnticipos.createCell(5);
		cellAntImporte.setCellValue(totales.getTotalAnticipos().doubleValue());
		cellAntImporte.setCellStyle(styleMoneyBold);

		i++;
		HSSFRow row1 = sheet.createRow(i);
		HSSFCell cell4 = row1.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Total"));
		cell4.setCellStyle(styleBold);

		HSSFCell cell5 = row1.createCell(5);
		cell5.setCellValue(totales.getTotalComprobantes()
				.add(totales.getTotalAnticipos()).doubleValue());
		cell5.setCellStyle(styleMoneyBold);

		HSSFCell cell6 = row1.createCell(7);
		cell6.setCellValue(new HSSFRichTextString("Total"));
		cell6.setCellStyle(styleBold);

		HSSFCell cell8 = row1.createCell(8);
		cell8.setCellValue(totales.getTotalDesde().doubleValue());
		cell8.setCellStyle(styleMoneyBold);

		HSSFCell cell10 = row1.createCell(10);
		cell10.setCellValue(new HSSFRichTextString("Total"));
		cell10.setCellStyle(styleBold);

		HSSFCell cell11 = row1.createCell(11);
		cell11.setCellValue(totales.getTotalHacia().doubleValue());
		cell11.setCellStyle(styleMoneyBold);
		i++;

		if (incluirCuadroEgresos) {
			incluirCuadro(sheet, i, totales.getResumenDesde(),
					totales.getResumenHasta(), styleAll, styleMoneyRight,
					styleBold, styleTotalesMoneyR, styleAllLeft, styleAllTop,
					styleAllRightTop, styleAllLeftTop, fechaIni, entidad);
		}

		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 1792);
		sheet.autoSizeColumn((short) 2);
		sheet.setColumnWidth(3, 5200);
		sheet.setColumnWidth(4, 5200);
		sheet.autoSizeColumn((short) 5);
		if(!incluirObsCompr){
			sheet.autoSizeColumn((short) 6);
		}
		sheet.setColumnWidth(7, 5200);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.setColumnWidth(10, 5200);
		sheet.autoSizeColumn((short) 11);
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ItemSubdiarioEgreso repo, HSSFCellStyle styleDateLeft,
			HSSFCellStyle styleDateRight, HSSFCellStyle styleAllLeft,
			HSSFCellStyle styleAllRight, HSSFCellStyle styleBoldLeft,
			HSSFCellStyle styleBoldRight, HSSFCellStyle styleMoneyLeft,
			HSSFCellStyle styleMoneyRight, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate, HSSFCellStyle styleTotalesL,
			HSSFCellStyle styleTotalesR, HSSFCellStyle styleTotales,
			HSSFCellStyle styleTotalesMoneyR, HSSFCellStyle styleMoneyRightTop,
			HSSFCellStyle styleAllTop, HSSFCellStyle styleAllLeftTop,
			HSSFCellStyle styleAllRightTop, HSSFCellStyle styleDateLeftTop,
			HSSFCellStyle styleTotalesMoneyL, List<Seccional> seccionales,
			boolean incluirTotales, Totales totales, boolean exportar,
			int entidad, boolean incluirObsComp) throws Exception {

		int max = 0;

		try {
			HSSFRow row = sheet.createRow(i);
			HSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(repo.getFecha());
			cell1.setCellStyle(styleDateLeftTop);

			if (exportar
					&& (null == repo.getNumeroOP() || repo.getNumeroOP().trim()
							.length() == 0)) {
				HSSFCell cell = row.createCell(1);
				cell.setCellValue(new HSSFRichTextString("---"));
				cell.setCellStyle(styleAllTop);

			} else {
				HSSFCell cell = row.createCell(1);
				cell.setCellValue(new HSSFRichTextString(repo.getNumeroOP()));
				cell.setCellStyle(styleAllTop);
			}

			if (null!=repo.getObservaciones()&&repo.getObservaciones().equals("ANULADAMISMODIA")) {
				HSSFCell cell = row.createCell(2);
				cell.setCellValue(new HSSFRichTextString("ANULADA"));
				cell.setCellStyle(styleAllTop);
				max=i+1;
			} else {

				if (exportar
						&& (null == repo.getCuit() || repo.getCuit().trim()
								.length() == 0)) {
					HSSFCell cellAcre = row.createCell(2);
					cellAcre.setCellValue(new HSSFRichTextString("---"));
					cellAcre.setCellStyle(styleAllTop);
				} else {
					HSSFCell cellAcre = row.createCell(2);
					cellAcre.setCellValue(new HSSFRichTextString(repo.getCuit()));
					cellAcre.setCellStyle(styleAllTop);
				}
				String nombre = repo.getRazonSocial();
				if (repo.getId_seccional() != 0) {
					int ind = seccionales.indexOf(new Seccional(repo
							.getId_seccional(), null));
					nombre = seccionales.get(ind).getDescripcion();
				}
				HSSFCell cellAcreRZ = row.createCell(3);
				if (exportar
						&& (null == repo.getCuit() || repo.getCuit().trim()
								.length() == 0)) {

					cellAcreRZ.setCellValue(new HSSFRichTextString("---"));
					cellAcreRZ.setCellStyle(styleAllTop);
				} else {
					if (repo.getBaja_fecha() == null) {
						cellAcreRZ.setCellValue(new HSSFRichTextString(nombre));
					} else {
						cellAcreRZ.setCellValue(new HSSFRichTextString(
								"ANULACION - " + nombre));
					}
				}
				cellAcreRZ.setCellStyle(styleAllRightTop);
				Date fecha = null;
				String op = null;
				String cuit = null;
				String razon = null;
				String comprobante = null;
				// boolean mostrarComprobante = true;

				// COMPROBANTES
				int indexComprobantes = i;
				int agregarColObs=0;
				BigDecimal totalCompr = BigDecimal.ZERO;
				if (repo.getComprobantesSubdiario() != null) {
					for (SubdiarioComprobante c : repo
							.getComprobantesSubdiario()) {
						HSSFRow rowComp = null;
						if (indexComprobantes == i) {
							rowComp = row;
						} else if (!repo.isMostrarComprobantesEnSubdiario()) {
							continue;
						} else {
							rowComp = sheet.createRow(indexComprobantes);
						}
						indexComprobantes++;
						if (exportar) {
							HSSFCell cell11 = rowComp.createCell(0);
							cell11.setCellValue(repo.getFecha());
							cell11.setCellStyle(styleDateLeftTop);
							fecha = repo.getFecha();

							HSSFCell cell12 = rowComp.createCell(1);
							cell12.setCellValue(new HSSFRichTextString(
									repo.getNumeroOP() != null
											&& repo.getNumeroOP().trim()
													.length() > 0 ? repo
											.getNumeroOP() : "----"));
							cell12.setCellStyle(styleAllTop);
							op = repo.getNumeroOP();

							HSSFCell cellAcre12 = rowComp.createCell(2);
							cellAcre12
									.setCellValue(new HSSFRichTextString(
											repo.getCuit() != null
													&& repo.getCuit().trim()
															.length() > 0 ? repo
													.getCuit() : "----"));
							cellAcre12.setCellStyle(styleAllTop);
							cuit = repo.getCuit();

							HSSFCell cellAcreRZ12 = rowComp.createCell(3);
							if (repo.getBaja_fecha() == null) {
								cellAcreRZ12
										.setCellValue(new HSSFRichTextString(
												nombre != null
														&& nombre.trim()
																.length() > 0 ? nombre
														: "----"));
							} else {
								nombre = "ANULACION - " + nombre;
								cellAcreRZ12
										.setCellValue(new HSSFRichTextString(
												nombre));
							}
							cellAcreRZ12.setCellStyle(styleAllRightTop);
							razon = nombre;

						}
						if (repo.isMostrarComprobantesEnSubdiario()) {
							HSSFCell cellCompro = rowComp.createCell(4);
							cellCompro.setCellValue(new HSSFRichTextString(c
									.getDescripcion()));
							if (indexComprobantes - 1 == i) {
								cellCompro.setCellStyle(styleAllLeftTop);
							} else {
								cellCompro.setCellStyle(styleAllLeft);
							}
							comprobante = c.getDescripcion();

							HSSFCell cellImpCompro = rowComp.createCell(5);
							if (c.isDebitoParaEgreso()) {
								cellImpCompro.setCellValue(c.getImporte()
										.negate().doubleValue());
							} else {
								cellImpCompro.setCellValue(c.getImporte()
										.doubleValue());
							}
							if (indexComprobantes - 1 == i) {
								cellImpCompro.setCellStyle(styleMoneyRightTop);
							} else {
								cellImpCompro.setCellStyle(styleMoneyRight);
							}

							if (c.isDebitoParaEgreso()) {
								totalCompr = totalCompr.subtract(c.getImporte());
							} else {
								totalCompr = totalCompr.add(c.getImporte());
							}
							
							if (incluirObsComp) {
								agregarColObs++;
								HSSFCell cellObs = rowComp.createCell(6);
								cellObs.setCellValue(new HSSFRichTextString(c.getObservaciones()));
							}
						}
					}
				}
				List<? extends SubdiarioEgresoColumna> desde = null;
				List<? extends SubdiarioEgresoColumna> hta = null;
				try{
					// DEBE
					desde = repo.getDesde();
					// HABER
					hta = repo.getHacia();
				}catch(Exception e){
					_log.error("ERROR EN SUBD EGRESO OP: "+repo.getNumeroOP());
					
				}
				
				Resultado resDD = reportar(i, desde, row, sheet,
						styleAllLeftTop, styleAllLeft, styleAllTop, styleAll,
						styleMoneyRightTop, styleMoneyRight, 6+agregarColObs, 7+agregarColObs, 8+agregarColObs,
						totales.getResumenDesde(),
						totales.getResumenDesdeAnulados(),
						repo.isMostrarEnCuadro(), entidad);

				Resultado resHta = reportar(i, hta, row, sheet,
						styleAllLeftTop, styleAllLeft, styleAllTop, styleAll,
						styleMoneyRightTop, styleMoneyRight, 9+agregarColObs, 10+agregarColObs, 11+agregarColObs,
						totales.getResumenHasta(),
						totales.getResumenHastaAnulados(),
						repo.isMostrarEnCuadro(), entidad);

				int indexDD = resDD.getI();
				int indexHta = resHta.getI();

				max = Math.max(indexComprobantes, indexDD);
				max = Math.max(max, indexHta);

				max = completarFilasVacias(sheet, i, indexComprobantes,
						indexDD, indexHta, styleDateLeft, styleDateRight,
						styleAllLeft, styleAllRight, styleBoldLeft,
						styleBoldRight, styleMoneyLeft, styleMoneyRight,
						styleAll, styleDate, fecha, op, cuit, razon,
						comprobante, exportar);

				boolean debug = true;
				if (debug && resHta.getTotal().compareTo(resDD.getTotal()) != 0) {
					for (int ind = i; ind < max; ind++) {
						HSSFCell cellDebug = sheet.getRow(ind).createCell(12);
						cellDebug.setCellValue(new HSSFRichTextString(repo
								.getNumeroOP()));
					}
				}
				if (incluirTotales && (max - i > 1)) {
					HSSFRow rowTotales = sheet.createRow(max);

					HSSFCell createCell = rowTotales.createCell(0);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleTotalesL);

					HSSFCell createCell1 = rowTotales.createCell(1);
					createCell1.setCellValue(new HSSFRichTextString(" "));
					createCell1.setCellStyle(styleTotales);

					HSSFCell createCell2 = rowTotales.createCell(2);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleTotales);

					HSSFCell createCell3 = rowTotales.createCell(3);
					createCell3.setCellValue(new HSSFRichTextString(" "));
					createCell3.setCellStyle(styleTotales);

					HSSFCell cellCompro = rowTotales.createCell(4);
					cellCompro.setCellValue(new HSSFRichTextString("Totales"));
					cellCompro.setCellStyle(styleTotalesMoneyL);

					HSSFCell cellImpCompro = rowTotales.createCell(5);
					cellImpCompro.setCellValue(totalCompr!=null?totalCompr.doubleValue():0);

					cellImpCompro.setCellStyle(styleTotalesR);

					HSSFCell cellConc = rowTotales.createCell(7);
					cellConc.setCellValue(new HSSFRichTextString(" "));

					HSSFCell cellImpDD = rowTotales.createCell(8);
					cellImpDD.setCellValue(resDD.getTotal().doubleValue());
					cellImpDD.setCellStyle(styleTotalesMoneyR);

					HSSFCell cellHta = rowTotales.createCell(10);
					cellHta.setCellValue(new HSSFRichTextString(" "));

					HSSFCell cellImpoHta = rowTotales.createCell(11);
					cellImpoHta.setCellValue(resHta.getTotal().doubleValue());
					cellImpoHta.setCellStyle(styleTotalesMoneyR);

					if (debug
							&& resHta.getTotal().compareTo(resDD.getTotal()) != 0) {
						HSSFCell cellDebug = rowTotales.createCell(12);
						cellDebug.setCellValue(new HSSFRichTextString(repo
								.getNumeroOP()));
					}

					max++;
				}

				if (repo.getComprobantesSubdiario() == null) {
					HSSFCell cellCompro = row.createCell(4);
					cellCompro.setCellValue(new HSSFRichTextString(" "));
					cellCompro.setCellStyle(styleAllLeftTop);

					HSSFCell cellImpCompro = row.createCell(5);
					cellImpCompro.setCellValue(new HSSFRichTextString(" "));
					cellImpCompro.setCellStyle(styleAllRightTop);

				}

				if (max == i) {
					max++;
				}

				totales.setTotalHacia(totales.getTotalHacia().add(
						resHta.getTotal()));
				totales.setTotalDesde(totales.getTotalDesde().add(
						resDD.getTotal()));
				totales.setTotalAnticipos(totales.getTotalAnticipos()
						.add(resHta.getTotalAnticipos())
						.add(resDD.getTotalAnticipos()));
				totales.setTotalComprobantes(totales.getTotalComprobantes()
						.add(totalCompr));
			}
		} catch (Exception e) {
			_log.error(e);
			throw e;
		}

		return max;
	}

	private static Resultado reportar(int i,
			List<? extends SubdiarioEgresoColumna> columna, HSSFRow row,
			HSSFSheet sheet, HSSFCellStyle styleAllLeftTop,
			HSSFCellStyle styleAllLeft, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRightTop,
			HSSFCellStyle styleMoneyRight, int col1, int col2, int col3,
			Map<String, BigDecimal> resumen,
			Map<String, BigDecimal> resumenAnulados, boolean agregarAResumen,
			int entidad) {

		BigDecimal totalPagos = BigDecimal.ZERO;
		BigDecimal totalPagosAnulados = BigDecimal.ZERO;
		BigDecimal totalAnticipos = BigDecimal.ZERO;
		int index = i;
		boolean seCreoAlgo = false;

		if (columna != null) {
			for (SubdiarioEgresoColumna fp : columna) {

				if (fp!=null && fp.getImporte()!=null && fp.getImporte().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				if (fp.isAnticipo()) {
					totalAnticipos = totalAnticipos.add(fp.getImporte());
				}
				seCreoAlgo = true;
				HSSFRow rowPago = null;
				if (index == i) {
					rowPago = row;
				} else {
					rowPago = sheet.getRow(index);
					if (rowPago == null) {
						rowPago = sheet.createRow(index);
					}
				}
				index++;

				HSSFCell cellForma = rowPago.createCell(col1);
				cellForma.setCellValue(new HSSFRichTextString(fp
						.getCuenta(entidad)));

				if (index - 1 == i) {
					cellForma.setCellStyle(styleAllLeftTop);
				} else {
					cellForma.setCellStyle(styleAllLeft);
				}

				HSSFCell cellNro = rowPago.createCell(col2);
				String numeroStr = fp.getDescripcionPAraSubdiario();
				if (StringUtils.checkEmpty(numeroStr)) {
					numeroStr = "xxxxxxxx";
				}
				cellNro.setCellValue(new HSSFRichTextString(numeroStr));
				if (index - 1 == i) {
					cellNro.setCellStyle(styleAllTop);
				} else {
					cellNro.setCellStyle(styleAll);
				}

				HSSFCell cellImpoPago = rowPago.createCell(col3);
				if (fp.getImporte() != null) {
					cellImpoPago.setCellValue(fp.getImporte().doubleValue());
					totalPagos = totalPagos.add(fp.getImporte());

					// if (agregarAResumen && fp.getCuenta() != null) {
					String cuenta = fp.getCuenta(entidad);
					if (StringUtils.checkEmpty(cuenta)) {
						cuenta = "xxx";
					}
					if (resumen.get(cuenta) != null) {
						resumen.put(fp.getCuenta(entidad), resumen.get(cuenta)
								.add(fp.getImporte()));
					} else {
						resumen.put(cuenta, fp.getImporte());
					}
					// } else {
					// totalPagosAnulados = totalPagosAnulados.add(fp
					// .getImporte());
					// if (resumenAnulados.get(fp.getCuenta()) != null) {
					// resumenAnulados.put(fp.getCuenta(), resumenAnulados
					// .get(fp.getCuenta()).add(fp.getImporte()));
					// } else {
					// resumenAnulados
					// .put(fp.getCuenta(), fp.getImporte());
					// }
					// }
				}

				if (index - 1 == i) {
					cellImpoPago.setCellStyle(styleMoneyRightTop);
				} else {
					cellImpoPago.setCellStyle(styleMoneyRight);
				}
			}
		}
		if (!seCreoAlgo) {
			HSSFCell cell1 = row.createCell(col1);
			cell1.setCellValue(new HSSFRichTextString(" "));
			cell1.setCellStyle(styleAllLeftTop);

			HSSFCell cell2 = row.createCell(col2);
			cell2.setCellValue(new HSSFRichTextString(" "));
			cell2.setCellStyle(styleAllTop);

			HSSFCell cell3 = row.createCell(col3);
			cell3.setCellValue(new HSSFRichTextString(" "));
			cell3.setCellStyle(styleMoneyRightTop);
			index++;
		}

		return new Resultado(index, totalPagos, totalPagosAnulados,
				totalAnticipos);
	}

	private static int completarFilasVacias(HSSFSheet sheet, int primerFila,
			int indexComprobantes, int indexDD, int indexHta,
			HSSFCellStyle styleDateLeft, HSSFCellStyle styleDateRight,
			HSSFCellStyle styleAllLeft, HSSFCellStyle styleAllRight,
			HSSFCellStyle styleBoldLeft, HSSFCellStyle styleBoldRight,
			HSSFCellStyle styleMoneyLeft, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate, Date fecha,
			String op, String cuit, String razon, String compro,
			boolean exportar) {
		int max = Math.max(indexComprobantes, indexDD);
		max = Math.max(max, indexHta);
		int fin = max - 1;

		if (indexComprobantes <= fin) {
			for (int i = indexComprobantes; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (/* !exportar && */row.getCell(4) == null) {
					HSSFCell createCell = row.createCell(4);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllLeft);
					HSSFCell createCell2 = row.createCell(5);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleMoneyRight);
				}/*
				 * else if(exportar){ HSSFCell createCell = row.createCell(4);
				 * createCell.setCellValue(new HSSFRichTextString(compro));
				 * createCell.setCellStyle(styleAllLeft);
				 * 
				 * }
				 */
			}
		}
		if (indexDD <= fin) {
			for (int i = indexDD; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(6) == null) {
					HSSFCell createCell = row.createCell(6);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllLeft);
					HSSFCell createCell2 = row.createCell(7);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleAll);
					HSSFCell createCell3 = row.createCell(8);
					createCell3.setCellValue(new HSSFRichTextString(" "));
					createCell3.setCellStyle(styleMoneyRight);
				}
			}
		}
		if (indexHta <= fin) {
			for (int i = indexHta; i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row.getCell(9) == null) {
					HSSFCell createCell = row.createCell(9);
					createCell.setCellValue(new HSSFRichTextString(" "));
					createCell.setCellStyle(styleAllLeft);
					HSSFCell createCell2 = row.createCell(10);
					createCell2.setCellValue(new HSSFRichTextString(" "));
					createCell2.setCellStyle(styleAll);
					HSSFCell createCell3 = row.createCell(11);
					createCell3.setCellValue(new HSSFRichTextString(" "));
					createCell3.setCellStyle(styleMoneyRight);
				}
			}
		}

		// primerFila+1 porque en la primera siempre escribo algo, asiq debo
		// completar de ahi en adelante
		if ((primerFila + 1) <= fin) {
			for (int i = (primerFila + 1); i <= fin; i++) {
				HSSFRow row = sheet.getRow(i);
				if (!exportar && row.getCell(0) == null) {
					HSSFCell createCell = row.createCell(0);
					createCell.setCellStyle(styleAllLeft);
				} else if (exportar) {
					HSSFCell createCell = row.createCell(0);
					if (null != fecha) {
						createCell.setCellValue(fecha);
					}
					createCell.setCellStyle(styleDateLeft);

					HSSFCell createCell1 = row.createCell(1);
					createCell1.setCellValue(new HSSFRichTextString(op));
					createCell1.setCellStyle(styleAllLeft);

					HSSFCell createCell12 = row.createCell(2);
					createCell12.setCellValue(new HSSFRichTextString(cuit));
					createCell12.setCellStyle(styleAllLeft);

					HSSFCell createCell13 = row.createCell(3);
					createCell13.setCellValue(new HSSFRichTextString(razon));
					createCell13.setCellStyle(styleAllLeft);
					if (row.getCell(4) == null
							|| row.getCell(4).getRichStringCellValue()
									.equals(new HSSFRichTextString(" "))) {
						HSSFCell createCell14 = row.createCell(4);
						createCell14
								.setCellValue(new HSSFRichTextString(compro));
						createCell14.setCellStyle(styleAllLeft);
					}
				}
			}
		}
		return (fin + 1);
	}

	private static int generarHeader(HSSFWorkbook wb, HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("OP"));
		cell1.setCellStyle(styleHeaderL);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Razon Social"));
		cellRaz.setCellStyle(styleHeaderR);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Comprobante"));
		cell4.setCellStyle(styleHeaderL);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Importe Comp"));
		cell5.setCellStyle(styleHeaderR);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Cta Bco"));
		cell6.setCellStyle(styleHeaderL);

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Descripcion"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(new HSSFRichTextString("Importe"));
		cell8.setCellStyle(styleHeaderR);

		HSSFCell cell9 = row.createCell(9);
		cell9.setCellValue(new HSSFRichTextString("Cuenta"));
		cell9.setCellStyle(styleHeader);

		HSSFCell cell10 = row.createCell(10);
		cell10.setCellValue(new HSSFRichTextString("Descripcion"));
		cell10.setCellStyle(styleHeader);

		HSSFCell cell11 = row.createCell(11);
		cell11.setCellValue(new HSSFRichTextString("Importe"));
		cell11.setCellStyle(styleHeaderR);

		return ++i;
	}

	private static int generarHeaderUoma(HSSFWorkbook wb, HSSFSheet sheet,
			int i, HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, boolean incluirObsCompro) {
		
		int cellPos = 0;
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(cellPos++);
		cell0.setCellValue(new HSSFRichTextString("FECHA"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(cellPos++);
		cell1.setCellValue(new HSSFRichTextString("OP"));
		cell1.setCellStyle(styleHeaderL);

		HSSFCell cellAcreed = row.createCell(cellPos++);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(cellPos++);
		cellRaz.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		cellRaz.setCellStyle(styleHeaderL);

		HSSFCell cell4 = row.createCell(cellPos++);
		cell4.setCellValue(new HSSFRichTextString("COMPROBANTE"));
		cell4.setCellStyle(styleHeaderL);

		HSSFCell cell5 = row.createCell(cellPos++);
		cell5.setCellValue(new HSSFRichTextString("IMPORTE COMPROB"));
		cell5.setCellStyle(styleHeaderR);

		if(incluirObsCompro){
			HSSFCell cell12 = row.createCell(cellPos++);
			cell12.setCellValue(new HSSFRichTextString("OBS. COMPROBANTES"));
			cell12.setCellStyle(styleHeaderL);
			
		}
		HSSFCell cell6 = row.createCell(cellPos++);
		cell6.setCellValue(new HSSFRichTextString("CTA BCO"));
		cell6.setCellStyle(styleHeaderL);

		HSSFCell cell7 = row.createCell(cellPos++);
		cell7.setCellValue(new HSSFRichTextString("DESCRIPCION"));
		cell7.setCellStyle(styleHeaderL);

		HSSFCell cell8 = row.createCell(cellPos++);
		cell8.setCellValue(new HSSFRichTextString("IMPORTE"));
		cell8.setCellStyle(styleHeaderR);

		HSSFCell cell9 = row.createCell(cellPos++);
		cell9.setCellValue(new HSSFRichTextString("CUENTA"));
		cell9.setCellStyle(styleHeaderR);

		HSSFCell cell10 = row.createCell(cellPos++);
		cell10.setCellValue(new HSSFRichTextString("CONTABLE"));
		cell10.setCellStyle(styleHeaderL);

		HSSFCell cell11 = row.createCell(cellPos++);
		cell11.setCellValue(new HSSFRichTextString("IMPORTE"));
		cell11.setCellStyle(styleHeaderR);

		
		return ++i;
	}

	private static class Resultado {
		private int i;
		private BigDecimal total;
		private BigDecimal totalAnulado;
		private final BigDecimal totalAnticipos;

		public Resultado(int index, BigDecimal total, BigDecimal totalAnulado,
				BigDecimal totalAnticipos) {
			this.i = index;
			this.totalAnulado = totalAnulado;
			this.total = total;
			this.totalAnticipos = totalAnticipos;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public int getI() {
			return i;
		}

		public BigDecimal getTotalAnulado() {
			return totalAnulado;
		}

		public void setTotalAnulado(BigDecimal totalAnulado) {
			this.totalAnulado = totalAnulado;
		}

		public BigDecimal getTotalAnticipos() {
			return totalAnticipos;
		}

	}

	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, int entidad, Date fechaIni, Date fechaFin) {

		String tituloReporte = "Subdiario de Egresos";

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		if (entidad == WebKeysGlobal.UOMA) {
			cell.setCellValue(new HSSFRichTextString(tituloReporte
					.toUpperCase()));
			cell.setCellStyle(getStyleBoldUnderlinedHeader(wb, 12));
		} else {
			cell.setCellValue(new HSSFRichTextString(tituloReporte));
			cell.setCellStyle(getStyleBoldUnderlined(wb));
		}

		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;

		HSSFRow rowTitulo2 = sheet.createRow(fila);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;

		return fila;
	}

}

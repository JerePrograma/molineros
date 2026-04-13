package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
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
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteSubdiario;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteSubdiarioIngresoExcel extends ReporteSubdiario {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSubdiarioIngresoExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		int entidad = ParamUtil.getInteger(req, "entidad");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);

		Empresa empresa = new Empresa(cuit, sucu, "");
		empresa.setId_seccional(seccional);

		boolean incluirTotales = ParamUtil.getBoolean(req, "incluir_totales");
		boolean incluirCuadroEgresos = ParamUtil.getBoolean(req,
				"incluir_cuadro");

		boolean incluirBcrios = ParamUtil.getBoolean(req, "incluir_bcrios");
		boolean incluirRecibos = ParamUtil.getBoolean(req, "incluir_recibos");
		boolean incluirAfip = ParamUtil.getBoolean(req, "incluir_afip");
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

			} catch (Exception e) {
				fechaImpre = new Date();
			}

			List<ItemSubdiarioIngreso> reporte = ContabilidadServiceUtil
					.subdiarioIngresos(fechaIni, fechaFin, empresa,
							incluirBcrios, incluirRecibos, incluirAfip, false, entidad);

			Collections.sort(reporte, new Comparator<ItemSubdiarioIngreso>() {
				public int compare(ItemSubdiarioIngreso arg0,
						ItemSubdiarioIngreso arg1) {
					int compareTo = arg0.getFecha().compareTo(arg1.getFecha());
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

			return generarReporte(fechaIni, fechaFin, reporte, incluirTotales,
					incluirCuadroEgresos, entidad, exportar, fechaImpre);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ItemSubdiarioIngreso> reporte, boolean incluirTotales,
			boolean incluirCuadroEgresos, int entidad, boolean exportar,
			Date fechaImpresion) {
		
		_log.debug("iterando reporte...");
		for (Iterator iterator = reporte.iterator(); iterator.hasNext();) {
			ItemSubdiarioIngreso isi = (ItemSubdiarioIngreso) iterator.next();
//			_log.debug(isi.toString());
		}
		
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);
		// styleHeaderLeft.setBorderLeft(BorderStyle.THIN);
		// styleHeaderLeft.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);

		// styleHeaderRight.setBorderRight(BorderStyle.THIN);
		// styleHeaderRight.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
		// styleHeader.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllTop = getStyleAll(wb);
		// styleAllTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
		// styleFechaLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		// styleMoneyRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
		// styleFechaLeftTop.setBorderLeft(BorderStyle.THIN);
		// styleFechaLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
		// styleMoneyRightTop.setBorderRight(BorderStyle.THIN);
		// styleMoneyRightTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightBold = getStyleMoneyBold(wb);
		// styleMoneyRightBold.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);

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
			i = crearHeaderPrincipal(wb, sheet, 7, entidad);
		}
		if (entidad == WebKeysGlobal.UOMA) {
			i = crearHeaderPrincipalUoma(wb, sheet, 7, fechaImpresion);
		}

		// HSSFRow rowTituloPpal = sheet.createRow(i);
		// HSSFCell cellPpal = rowTituloPpal.createCell(0);
		// cellPpal.setCellValue(new
		// HSSFRichTextString("Subdiario de Ingresos"));
		// cellPpal.setCellStyle(getStyleBoldUnderlined(wb));
		// sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
		// i++;
		//
		// HSSFRow rowTituloPpal2 = sheet.createRow(i);
		// HSSFCell cellPpal2 = rowTituloPpal2.createCell(0);
		// cellPpal2.setCellValue(new HSSFRichTextString("Desde "
		// + DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
		// + DateUtils.format(fechaFin, DateUtils.SHORT)));
		// cellPpal2.setCellStyle(getStyleAllCenter(wb));
		// sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
		// i++;

		i = createTitulosHeader(wb, sheet, i, entidad, fechaIni, fechaFin);

		Totales totales = new Totales();

		if (entidad == WebKeysGlobal.OSPIM) {
			i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		} else {
			i = generarHeaderUoma(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		}

		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		boolean mostrarFecha = true;
		Date auxDate = null;
		BigDecimal totalParcial = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		String comprobante = new String();
		for (ItemSubdiarioIngreso repo : reporte) {
			if (auxDate == null || auxDate.compareTo(repo.getFecha()) != 0) {
				if (auxDate != null && incluirTotales) {
					i = mostrarTotalesParciales(styleAll, styleMoneyRightBold,
							sheet, i, totalParcial);
				}
				auxDate = repo.getFecha();
				mostrarFecha = true;
				totalParcial = BigDecimal.ZERO;
			} else {
				mostrarFecha = false;
			}
			if (exportar) {
				if (!comprobante.equals(repo.getComprobante())
						&& repo.getComprobante().trim().length() > 0
						&& repo.getComprobante() != null) {
					comprobante = repo.getComprobante();
				}
				repo.setComprobante(comprobante);
			}
			if (entidad == WebKeysGlobal.OSPIM) {
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop, mostrarFecha, totales, exportar);
			} else {
				i = generarDatosUoma(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop, mostrarFecha, totales, exportar);
			}

			totalParcial = totalParcial.add(repo.getImporte());
			total = total.add(repo.getImporte());
		}

		if (incluirTotales) {
			i = mostrarTotalesParciales(styleAll, styleMoneyRightBold, sheet,
					i, totalParcial);
		}
		i++;
		if (incluirTotales) {
			i = mostrarTotalesParciales(styleAll, styleMoneyBold, sheet, i,
					total);
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleAllTop);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 7));

		i += 2;
		if (incluirCuadroEgresos) {
			HSSFCellStyle styleBold = getStyleBold(wb);

			HSSFCellStyle styleTotalesMoneyR = getStyleMoneyBold(wb);
			// styleTotalesMoneyR.setBorderRight(BorderStyle.THIN);
			// styleTotalesMoneyR
			// .setBorderBottom(BorderStyle.THIN);

			HSSFCellStyle styleAllLeft = getStyleAll(wb);
			// styleAllLeft.setBorderLeft(BorderStyle.THIN);

			HSSFCellStyle styleAllRightTop = getStyleAll(wb);
			// styleAllRightTop.setBorderRight(BorderStyle.THIN);
			// styleAllRightTop.setBorderTop(BorderStyle.THIN);

			HSSFCellStyle styleAllLeftTop = getStyleAll(wb);
			// styleAllLeftTop.setBorderLeft(BorderStyle.THIN);
			// styleAllLeftTop.setBorderTop(BorderStyle.THIN);

			incluirCuadro(sheet, i, totales.getResumenDesde(),
					totales.getResumenHasta(), styleAll, styleMoneyRight,
					styleBold, styleTotalesMoneyR, styleAllLeft, styleAllTop,
					styleAllRightTop, styleAllLeftTop, fechaIni, entidad);
		}

		if (entidad == WebKeysGlobal.OSPIM) {
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.setColumnWidth(3, 10200);
			sheet.setColumnWidth(4, 10200);
			sheet.autoSizeColumn((short) 5);
			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);
		} else {
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.setColumnWidth(3, 10200);
			sheet.autoSizeColumn((short) 4);
			sheet.setColumnWidth(5, 10200);
			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);
		}
		return wb;
	}

	private static int mostrarTotalesParciales(HSSFCellStyle styleAll,
			HSSFCellStyle styleMoneyRightBold, HSSFSheet sheet, int i,
			BigDecimal totalParcial) {
		HSSFRow row = sheet.createRow(i);
		HSSFCell createCell = row.createCell(6);
		createCell.setCellValue(new HSSFRichTextString("Total del ingreso:"));
		createCell.setCellStyle(styleAll);

		HSSFCell createCell2 = row.createCell(7);
		createCell2.setCellValue(totalParcial.doubleValue());
		createCell2.setCellStyle(styleMoneyRightBold);
		i++;
		return i;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ItemSubdiarioIngreso repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop, boolean mostrarFecha,
			Totales totales, boolean exportar) {

		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0 = row.createCell(0);
		if (mostrarFecha || exportar) {
			cell0.setCellValue(repo.getFecha());
		}
		if (mostrarFecha || exportar) {
			cell0.setCellStyle(styleFechaLeftTop);
		} else {
			cell0.setCellStyle(stylefechaLeft);
		}

		HSSFCell cell1 = row.createCell(1);
		String anulado = "";
		if (repo.getBaja_fecha() != null) {
			anulado = "ANULACION - ";
		}
		cell1.setCellValue(new HSSFRichTextString(anulado
				+ repo.getComprobante()));
		if (mostrarFecha || exportar) {
			cell1.setCellStyle(styleAllTop);
		} else {
			cell1.setCellStyle(styleAll);
		}

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(repo.getCuit()));
		if (mostrarFecha || exportar) {
			cell2.setCellStyle(styleAllTop);
		} else {
			cell2.setCellStyle(styleAll);
		}

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
		if (mostrarFecha || exportar) {
			cell3.setCellStyle(styleAllTop);
		} else {
			cell3.setCellStyle(styleAll);
		}

		String ddeCuenta = repo.getCuenta();
		String ddeNro = repo.getNumeroCuenta();
		// String htaCuenta = repo.getCuentaFormaPago();
		String htaNro = repo.getFormaPago();

		Map<String, BigDecimal> resumenDesde = totales.getResumenDesde();
		Map<String, BigDecimal> resumenHasta = totales.getResumenHasta();

		if (resumenDesde.get(ddeNro) != null) {
			resumenDesde.put(ddeNro,
					resumenDesde.get(ddeNro).add(repo.getImporte()));
		} else {
			resumenDesde.put(ddeNro, repo.getImporte());
		}

		if (resumenHasta.get(htaNro) != null) {
			resumenHasta.put(htaNro,
					resumenHasta.get(htaNro).add(repo.getImporte()));
		} else {
			resumenHasta.put(htaNro, repo.getImporte());
		}

		HSSFCell cell4 = row.createCell(4);
		if (repo.getBaja_fecha() != null) {
			cell4.setCellValue(new HSSFRichTextString("ANULACION - "
					+ repo.getCuentaBaja()));
		} else {
			cell4.setCellValue(new HSSFRichTextString(ddeCuenta));
		}
		if (mostrarFecha || exportar) {
			cell4.setCellStyle(styleAllTop);
		} else {
			cell4.setCellStyle(styleAll);
		}

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(ddeNro));
		if (mostrarFecha || exportar) {
			cell5.setCellStyle(styleAllTop);
		} else {
			cell5.setCellStyle(styleAll);
		}

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString(htaNro));
		if (mostrarFecha || exportar) {
			cell6.setCellStyle(styleAllTop);
		} else {
			cell6.setCellStyle(styleAll);
		}

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(repo.getImporte().doubleValue());

		if (mostrarFecha || exportar) {
			cell7.setCellStyle(styleMoneyRightTop);
		} else {
			cell7.setCellStyle(styleMoneyRight);
		}

		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Comprobante"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Razon Social"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Concepto"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Cuenta"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Forma de Pago"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Importe"));
		cell7.setCellStyle(styleHeaderL);

		return ++i;
	}

	private static int generarHeaderUoma(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("FECHA"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		// cell1.setCellValue(new HSSFRichTextString("COMPROBANTE"));
		cell1.setCellValue(new HSSFRichTextString(" "));
		cell1.setCellStyle(styleHeaderL);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		cellRaz.setCellStyle(styleHeaderL);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("CUENTA"));
		cell4.setCellStyle(styleHeaderR);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("CONTABLE"));
		cell5.setCellStyle(styleHeaderL);

		HSSFCell cell6 = row.createCell(6);
		// cell6.setCellValue(new HSSFRichTextString("FORMA DE PAGO"));
		cell6.setCellValue(new HSSFRichTextString(" "));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("IMPORTE"));
		cell7.setCellStyle(styleHeaderL);

		return ++i;
	}

	private static int generarDatosUoma(HSSFSheet sheet, int i,
			ItemSubdiarioIngreso repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop, boolean mostrarFecha,
			Totales totales, boolean exportar) {

		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0 = row.createCell(0);
		stylefechaLeft.setAlignment(HorizontalAlignment.CENTER);
		styleFechaLeftTop.setAlignment(HorizontalAlignment.CENTER);
		if (mostrarFecha || exportar) {
			cell0.setCellValue(repo.getFecha());
		}
		if (mostrarFecha || exportar) {
			cell0.setCellStyle(styleFechaLeftTop);
		} else {
			cell0.setCellStyle(stylefechaLeft);
		}
		/*
		 * Se solicta cambiar la forma de ver recibos, y la forma de mostrar
		 * recaudacion, para no tocar la BD, lo chancheo por aca... SVA
		 */
		HSSFCell cell1 = row.createCell(1);
		String anulado = "";
		String comprobanteEditado = "";
		comprobanteEditado = repo.getComprobante();
		if (repo.getComprobante().startsWith("RECAUDACION")) {
			comprobanteEditado = repo.getComprobante().substring(11,
					repo.getComprobante().length());
		} else if (repo.getComprobante().startsWith("Rec.")) {
			comprobanteEditado = repo.getComprobante().substring(5, 9)
					+ " "
					+ repo.getComprobante().substring(9,
							repo.getComprobante().length());
		} else {
			comprobanteEditado = repo.getComprobante();
		}
		if (repo.getRazonSocial().equals("ANULADAMISMODIA")) {
			cell1.setCellValue(new HSSFRichTextString(comprobanteEditado));
			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(new HSSFRichTextString("ANULADA"));
			if (mostrarFecha || exportar) {
				cell2.setCellStyle(styleAllTop);
			} else {
				cell2.setCellStyle(styleAll);
			}

		} else {
			if (repo.getBaja_fecha() != null) {
				anulado = "ANULACION - ";
			}
			cell1.setCellValue(new HSSFRichTextString(anulado
			// + repo.getComprobante()));
					+ comprobanteEditado));
			if (mostrarFecha || exportar) {
				cell1.setCellStyle(styleAllTop);
			} else {
				cell1.setCellStyle(styleAll);
			}

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(new HSSFRichTextString(repo.getCuit()));
			if (mostrarFecha || exportar) {
				cell2.setCellStyle(styleAllTop);
			} else {
				cell2.setCellStyle(styleAll);
			}

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
			if (mostrarFecha || exportar) {
				cell3.setCellStyle(styleAllTop);
			} else {
				cell3.setCellStyle(styleAll);
			}

			String ddeCuenta = repo.getCuenta();
			String ddeNro = repo.getNumeroCuenta();
			// String htaCuenta = repo.getCuentaFormaPago();
			String htaNro = repo.getFormaPago();

			Map<String, BigDecimal> resumenDesde = totales.getResumenDesde();
			Map<String, BigDecimal> resumenHasta = totales.getResumenHasta();

			if (resumenDesde.get(ddeNro) != null) {
				resumenDesde.put(ddeNro,
						resumenDesde.get(ddeNro).add(repo.getImporte()));
			} else {
				resumenDesde.put(ddeNro, repo.getImporte());
			}

			if (resumenHasta.get(htaNro) != null) {
				resumenHasta.put(htaNro,
						resumenHasta.get(htaNro).add(repo.getImporte()));
			} else {
				resumenHasta.put(htaNro, repo.getImporte());
			}

			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(new HSSFRichTextString(ddeNro));
			if (mostrarFecha || exportar) {
				cell4.setCellStyle(styleAllTop);
				// HSSFCellStyle styleAllR = styleAllTop;
				// styleAllR.setAlignment(HorizontalAlignment.RIGHT);
				// cell4.setCellStyle(styleAllR);
			} else {
				cell4.setCellStyle(styleAll);
				// HSSFCellStyle styleAllR = styleAll;
				// styleAllR.setAlignment(HorizontalAlignment.RIGHT);
				// cell4.setCellStyle(styleAllR);
			}

			HSSFCell cell5 = row.createCell(5);
			if (repo.getBaja_fecha() != null) {
				cell5.setCellValue(new HSSFRichTextString("ANULACION - "
						+ repo.getCuentaBaja()));
			} else {
				cell5.setCellValue(new HSSFRichTextString(ddeCuenta));
			}
			if (mostrarFecha || exportar) {
				cell5.setCellStyle(styleAllTop);
			} else {
				cell5.setCellStyle(styleAll);
			}

			HSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(new HSSFRichTextString(htaNro));
			if (mostrarFecha || exportar) {
				cell6.setCellStyle(styleAllTop);
			} else {
				cell6.setCellStyle(styleAll);
			}

			HSSFCell cell7 = row.createCell(7);
			cell7.setCellValue(repo.getImporte().doubleValue());

			if (mostrarFecha || exportar) {
				cell7.setCellStyle(styleMoneyRightTop);
			} else {
				cell7.setCellStyle(styleMoneyRight);
			}
		}

		return ++i;
	}

	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, int entidad, Date fechaIni, Date fechaFin) {

		String tituloReporte = "Subdiario de Ingresos";

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

package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.SubdiarioComprobante;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.MovimientoBancarioSubdiarioEgreso;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteEgresosPorConceptosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteEgresosPorConceptosExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		String conceptos = ParamUtil.getString(req, "conceptos");

		int entidad = ParamUtil.getInteger(req, "entidad");

		String cuit = ParamUtil.getString(req, "cuit");
		String sucursal = ParamUtil.getString(req, "sucursal");
		int idSeccional = ParamUtil.getInteger(req, "id_seccional");
		boolean incluirMovsBcrios = ParamUtil.getBoolean(req,
				"incluir_mov_bcrios");

		List<Seccional> seccionales = TraeListasServiceUtil.getSeccionales();

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			List<ItemSubdiarioEgreso> lista = new ArrayList<ItemSubdiarioEgreso>();
			List<? extends ItemSubdiarioEgreso> reporte = null;
			if (entidad != WebKeysGlobal.OSPIM) {
				reporte = OrdenPagoServiceUtil
						.reporteOrdenPagoCompletoParaSubdiario(fechaIni,
								fechaFin, true, true, true, cuit, sucursal,
								idSeccional, false, entidad);
				lista.addAll(reporte);
				// busca movimientos bancarios. PEDIDO POR BUG 688 UOMA/AMTIMA
				// SIN SENTIDO.
				if (incluirMovsBcrios) {
					List<? extends ItemSubdiarioEgreso> movsBcrios = MovimientoBancarioServiceUtil
							.reporteParaSubdiario(fechaIni, fechaFin, entidad);
					lista.addAll(movsBcrios);
				}
			} else {
				reporte = OrdenPagoServiceUtil
						.reporteOrdenPagoOspimCompletoParaSubdiario(fechaIni,
								fechaFin, true, true, true);
				lista.addAll(reporte);
			}

			Collections.sort(lista, new Comparator<ItemSubdiarioEgreso>() {
				public int compare(ItemSubdiarioEgreso arg0,
						ItemSubdiarioEgreso arg1) {
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

			List<Concepto> conceptoEgresos = TraeListasServiceUtil
					.getConceptosEgresoValidosDentroDe(fechaIni, fechaFin,
							entidad);

			return generarReporte(fechaIni, fechaFin, lista, seccionales,
					conceptos != null && conceptos.split(",").length > 0
							&& !conceptos.equals("") ? conceptos.split(",")
							: null, conceptoEgresos, entidad);
		} catch (Exception e) {
			_log.error("Error al generar egresos por concepto", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<? extends ItemSubdiarioEgreso> reporte,
			List<Seccional> seccionales, String[] conceptos,
			List<Concepto> maestroConcepto, int entidad) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleHeader = getStyleHeader(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);
		int i = 0;

		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Egresos por concepto"));
		cell.setCellStyle(getStyleHeader(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
		i++;

		StringBuilder sb = new StringBuilder("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT));

		sb.append(" - Conceptos: ");

		List<Concepto> conceptosArray = new ArrayList<Concepto>();

		if (null != conceptos && !conceptos[0].equals("null")) {
			for (String conc : conceptos) {
				StringTokenizer st = new StringTokenizer(conc, "|");
				int conceptoId = Integer.parseInt(st.nextToken());
				int idSeccional = 0;
				try {
					idSeccional = Integer.parseInt(st.nextToken());
				} catch (NumberFormatException e) {
				} catch (NoSuchElementException e) {
				}

				_log.debug("Concepto ID " + conceptoId);
				
				Concepto concepto = new Concepto(conceptoId);
				if (idSeccional != 0) {
					concepto.setIdSeccional(idSeccional);
				}
				conceptosArray.add(concepto);
				int indexOf = maestroConcepto.indexOf(concepto);
				sb.append(maestroConcepto.get(indexOf).getDescripcion() + " - ");
			}
		} else {
			sb.append("Todos");
		}

		HSSFRow rowTitulo2 = sheet.createRow(i);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString(sb.toString()));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));
		i++;

		Totales totales = new Totales();

		i = generarHeader(wb, sheet, i, styleHeader);
		// List<String> conceptosList = (List<String>) Arrays.asList(conceptos);
		for (ItemSubdiarioEgreso repo : reporte) {
			if ((null != repo.getObservaciones() && !repo.getObservaciones()
					.equals("ANULADAMISMODIA"))
					|| null == repo.getObservaciones()) {
				i = generarDatos(sheet, i, repo, styleDate, styleMoney,
						styleAll, seccionales, totales, conceptosArray);
			}
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleAll);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 10));

		// TOTALES
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		i++;
		HSSFRow row0 = sheet.createRow(i);
		HSSFCell cellSubt = row0.createCell(0);
		cellSubt.setCellValue(new HSSFRichTextString("Total"));
		cellSubt.setCellStyle(styleBold);

		HSSFCell cellSubtotalComp = row0.createCell(1);
		cellSubtotalComp
				.setCellValue(totales.getTotalConceptos().doubleValue());
		cellSubtotalComp.setCellStyle(styleMoneyBold);

		List<Concepto> keys = new ArrayList<Concepto>();
		keys.addAll(totales.getResumen().keySet());

		Collections.sort(keys, new Comparator<Concepto>() {
			public int compare(Concepto o1, Concepto o2) {
				return o1.getDescripcion().compareTo(o2.getDescripcion());
			}
		});

		i += 2;
		HSSFRow rowHeader = sheet.createRow(i);
		HSSFCell cellConcepto = rowHeader.createCell(0);
		cellConcepto.setCellValue(new HSSFRichTextString("Concepto"));
		cellConcepto.setCellStyle(styleBold);

		HSSFCell cellImporte = rowHeader.createCell(1);
		cellImporte.setCellValue(new HSSFRichTextString("Importe"));
		cellImporte.setCellStyle(styleBold);
		i++;
		for (Concepto key : keys) {
			HSSFRow rowResumen = sheet.createRow(i);
			BigDecimal importe = totales.getResumen().get(key);

			HSSFCell cellConceptoValue = rowResumen.createCell(0);
			cellConceptoValue.setCellValue(new HSSFRichTextString(key
					.getDescripcion()));
			cellConceptoValue.setCellStyle(styleAll);

			HSSFCell cellImporteValue = rowResumen.createCell(1);
			cellImporteValue.setCellValue(importe.doubleValue());
			cellImporteValue.setCellStyle(styleMoney);
			i++;
		}
		HSSFRow rowTotal = sheet.createRow(i);
		HSSFCell cellTotal = rowTotal.createCell(0);
		cellTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTotal.setCellStyle(styleBold);

		HSSFCell cellTotalValue = rowTotal.createCell(1);
		cellTotalValue.setCellValue(totales.getTotalConceptos().doubleValue());
		cellTotalValue.setCellStyle(styleMoneyBold);

		sheet.setColumnWidth(0, 10360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.setColumnWidth(5, 10360);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);

		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ItemSubdiarioEgreso repo, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleAll,
			List<Seccional> seccionales, Totales totales,
			List<Concepto> conceptosList) throws Exception {

		boolean anulado = false;
		if (repo.getBaja_fecha() != null) {
			anulado = true;
		}

		// COMPROBANTES
		BigDecimal totalCompr = BigDecimal.ZERO;
		if (repo.getComprobantesSubdiario() != null) {
			for (SubdiarioComprobante c : repo.getComprobantesSubdiario()) {
				Map<Concepto, BigDecimal> importes = new HashMap<Concepto, BigDecimal>();
				// agrupo segun concepto (los reintegros y liquidaciones tienen
				// muchas prestaciones que se mapean a conceptos, por lo que
				// termino con varios conceptos iguales para el mismo
				// comprobante)
				if (c instanceof MovimientoBancarioSubdiarioEgreso.ColumnaComprobante) {
					if (conceptosList != null && conceptosList.size() != 0
							&& !conceptosList.get(0).equals("null")
							&& !conceptosList.contains(c.getDescripcion())) {
						// &&
						// !conceptosList.contains(String.valueOf(cc.getConceptoComprobante().getId())))
						// {
						continue;
					}
					BigDecimal importeConcepto = c.getImporte();
					if (c.getImporte().compareTo(BigDecimal.ZERO) < 0) {
						// la rendicion del anticipo me llega con el importe del
						// comprobante < 0, pero el concepto > 0, entonces hago
						// esto
						importeConcepto = importeConcepto.negate();
					}
					// si es debito para egreso lo niego
					if (c.isDebitoParaEgreso()) {
						importeConcepto = importeConcepto.negate();
					}
					// si es anulado lo vuelvo a negar (es independiente del if
					// anterior, o sea: NO juntar la condicion usando un "or")
					if (anulado) {
						importeConcepto = importeConcepto.negate();
					}
					Concepto conc = new Concepto();
					conc.setDescripcion(c.getTipoMovDescripcion());
					conc.setId(c.getIdTipoMov());
					agregarAResumen(importes, conc, importeConcepto);

				} else {
					List<ComprobanteConcepto> conceptos = null;
					try {
						conceptos = c.getConceptos();
					} catch (Exception e) {
						_log.error("ERROR comprobante: "
								+ c.getTipoComprobante() + " "
								+ c.getNroComprobante());
						if (!(c.getTipoComprobante().equals("ANT") && c
								.getNroComprobante().contains("|"))) {
							throw e;
						}
					}
					if (conceptos != null) {
						for (ComprobanteConcepto cc : conceptos) {
							if (conceptosList != null
									&& conceptosList.size() != 0
									&& !conceptosList.get(0).equals("null")
									&& !conceptosList.contains(cc
											.getConceptoComprobante())) {
								// &&
								// !conceptosList.contains(String.valueOf(cc.getConceptoComprobante().getId())))
								// {
								continue;
							}
							BigDecimal importeConcepto = cc.getImporte();
							if (c.getImporte().compareTo(BigDecimal.ZERO) < 0) {
								// la rendicion del anticipo me llega con el
								// importe
								// del
								// comprobante < 0, pero el concepto > 0,
								// entonces
								// hago
								// esto
								importeConcepto = importeConcepto.negate();
							}
							// si es debito para egreso lo niego
							if (c.isDebitoParaEgreso()) {
								importeConcepto = importeConcepto.negate();
							}
							// si es anulado lo vuelvo a negar (es independiente
							// del
							// if
							// anterior, o sea: NO juntar la condicion usando un
							// "or")
							if (anulado) {
								importeConcepto = importeConcepto.negate();
							}

							
// Excepción agregada para tratar comprobantes VAR negativos. 12/09/2019 - DS							
							if (c.getImporte().compareTo(BigDecimal.ZERO) < 0 && !c.isDebitoParaEgreso() && !anulado &&
									c.getTipoComprobante().equals("VAR")) {
								importeConcepto = cc.getImporte();
							}
							
							agregarAResumen(importes,
									cc.getConceptoComprobante(),
									importeConcepto);
						}
					}
				}

				// recorro la agrupacion de conceptos para hacer el reporte
				for (Concepto cc : importes.keySet()) {
					int cont = 0;
					HSSFRow rowComp = sheet.createRow(i);
					i++;

					HSSFCell cellConcepto = rowComp.createCell(cont++);
					cellConcepto.setCellValue(new HSSFRichTextString(cc
							.getDescripcion()));
					cellConcepto.setCellStyle(styleAll);

					HSSFCell cellImporteConcepto = rowComp.createCell(cont++);

					BigDecimal importe = importes.get(cc);
					agregarAResumen(totales.getResumen(), cc, importe);
					cellImporteConcepto.setCellValue(importe.doubleValue());
					cellImporteConcepto.setCellStyle(styleMoney);

					HSSFCell cellCompro = rowComp.createCell(cont++);
					cellCompro
							.setCellValue(new HSSFRichTextString(c
									.getTipoComprobante()
									+ "-"
									+ c.getNroComprobante()));
					cellCompro.setCellStyle(styleAll);

					HSSFCell cellFechaRecep = rowComp.createCell(cont++);
					if (null != c.getFechaEmision()) {
						cellFechaRecep.setCellValue(c.getFechaEmision());
					}
					cellFechaRecep.setCellStyle(styleDate);

					HSSFCell cellImpCompro = rowComp.createCell(cont++);
					if ((c.isDebitoParaEgreso() && !anulado)
							|| (!c.isDebitoParaEgreso() && anulado)) {
						cellImpCompro.setCellValue(c.getImporte().negate()
								.doubleValue());
						totalCompr = totalCompr.subtract(c.getImporte());
					} else {
						cellImpCompro
								.setCellValue(c.getImporte().doubleValue());
						totalCompr = totalCompr.add(c.getImporte());
					}
					cellImpCompro.setCellStyle(styleMoney);

					HSSFCell cellObser = rowComp.createCell(cont++);
					cellObser.setCellValue(new HSSFRichTextString(c
							.getObservaciones()));
					cellObser.setCellStyle(styleAll);

					totales.setTotalConceptos(totales.getTotalConceptos().add(
							importe));

					reportarDatosOpEmpresa(repo, c.getFechaRecepcion(),
							c.getPeriodoPrestacion(), styleAll, styleDate,
							seccionales, rowComp, cont);
				}
			}
		}

		return i;
	}

	private static void agregarAResumen(Map<Concepto, BigDecimal> importes,
			Concepto cc, BigDecimal importeConcepto) {
		if (importes.get(cc) == null) {
			importes.put(cc, importeConcepto);
		} else {
			importes.put(cc, importes.get(cc).add(importeConcepto));
		}
	}

	private static void reportarDatosOpEmpresa(ItemSubdiarioEgreso repo,
			Date recepcion, Date periodo, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate, List<Seccional> seccionales, HSSFRow row,
			int cont) {

		HSSFCell cellAcre = row.createCell(cont++);
		cellAcre.setCellValue(new HSSFRichTextString(repo.getCuit()));
		cellAcre.setCellStyle(styleAll);

		String nombre = repo.getRazonSocial();
		if (repo.getId_seccional() != 0) {
			int ind = seccionales.indexOf(new Seccional(repo.getId_seccional(),
					null));
			nombre = seccionales.get(ind).getDescripcion();
		}
		HSSFCell cellAcreRZ = row.createCell(cont++);
		if (repo.getBaja_fecha() == null) {
			cellAcreRZ.setCellValue(new HSSFRichTextString(nombre));
		} else {
			cellAcreRZ.setCellValue(new HSSFRichTextString("ANULACIÓN - "
					+ nombre));
		}
		cellAcreRZ.setCellStyle(styleAll);

		HSSFCell cellRecepcion = row.createCell(cont++);
		cellRecepcion.setCellValue(recepcion);
		cellRecepcion.setCellStyle(styleDate);

		HSSFCell cellPeriodo = row.createCell(cont++);
		if (periodo != null) {
			cellPeriodo.setCellValue(periodo);
			cellPeriodo.setCellStyle(styleDate);
		}

		HSSFCell cell1 = row.createCell(cont++);
		cell1.setCellValue(repo.getFecha());
		cell1.setCellStyle(styleDate);

		HSSFCell cell = row.createCell(cont++);
		cell.setCellValue(new HSSFRichTextString(repo.getNumeroOP()));
		cell.setCellStyle(styleAll);

	}

	private static int generarHeader(HSSFWorkbook wb, HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader) {
		int cont = 0;
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell = row.createCell(cont++);
		cell.setCellValue(new HSSFRichTextString("Concepto"));
		cell.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(cont++);
		cell1.setCellValue(new HSSFRichTextString("Importe del Concepto"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell2 = row.createCell(cont++);
		cell2.setCellValue(new HSSFRichTextString("Comprobante"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell21 = row.createCell(cont++);
		cell21.setCellValue(new HSSFRichTextString("Fecha Emisión"));
		cell21.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(cont++);
		cell3.setCellValue(new HSSFRichTextString("Importe Comprobante"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell31 = row.createCell(cont++);
		cell31.setCellValue(new HSSFRichTextString("Observación Compro."));
		cell31.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(cont++);
		cell4.setCellValue(new HSSFRichTextString("Cuit"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(cont++);
		cell5.setCellValue(new HSSFRichTextString("Razon Social"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(cont++);
		cell6.setCellValue(new HSSFRichTextString("Recepcion"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(cont++);
		cell7.setCellValue(new HSSFRichTextString("Período"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(cont++);
		cell8.setCellValue(new HSSFRichTextString("Fecha del egreso"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(cont++);
		cell9.setCellValue(new HSSFRichTextString("OP"));
		cell9.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, cont, i, i);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return ++i;
	}

	public static class Totales {
		private BigDecimal totalConceptos = BigDecimal.ZERO;
		private Map<Concepto, BigDecimal> resumen = new HashMap<Concepto, BigDecimal>();

		public BigDecimal getTotalConceptos() {
			return totalConceptos;
		}

		public void setTotalConceptos(BigDecimal totalConceptos) {
			this.totalConceptos = totalConceptos;
		}

		public Map<Concepto, BigDecimal> getResumen() {
			return resumen;
		}

		public void setResumen(Map<Concepto, BigDecimal> resumen) {
			this.resumen = resumen;
		}
	}
}

package ar.com.ospim.tesoreria.reportes.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.EgresoLiquidacion;
import ar.com.ospim.tesoreria.service.ReporteEgresosLiquidacionesServiceImpl;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteEgresosLiquidacionesExcel extends ReporteXLS {

	private static Log _log = LogFactoryUtil
			.getLog(ReporteEgresosLiquidacionesExcel.class);

	private static BigDecimal totalDebitadoOmintLiquidaciones=BigDecimal.ZERO;
	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");

		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			_log.info("Comienzo Reporte Egresos...");
			_log.info("Comienzo Reporte Egresos...LiquidacionesConcepto");
			ReporteEgresosLiquidacionesServiceImpl repo = new ReporteEgresosLiquidacionesServiceImpl();
			List<EgresoLiquidacion> libro = repo.getLiquidacionesConcepto(
					fechaIni, fechaFin);
			_log.info("Comienzo Reporte Egresos...LiquidacionesConceptoAgrupado");
			List<EgresoLiquidacion> libroAgrupado = repo
					.getLiquidacionesConceptoAgrupado(fechaIni, fechaFin);
			//ESTO NO LO USAN POR AHORA. LO SACO PORQUE ES LO QUE RALENTIZA EL REPORTE
			_log.info("Comienzo Reporte Egresos...EgresosPrestacion");
			List<EgresoLiquidacion> libroPrestacion = repo
					.getEgresosPrestacion(fechaIni, fechaFin);
			_log.info("Comienzo Reporte Egresos...libroPrestacionOS");
			List<EgresoLiquidacion> libroPrestacionOS = repo
					.getEgresosPrestacionOS(fechaIni, fechaFin);
			_log.info("Comienzo Reporte Egresos...libroOtrosReintegrosOS");
			List<EgresoLiquidacion> libroOtrosReintegrosOS = repo
					.getOtrosReintegrosPrestacionOS(fechaIni, fechaFin);
			_log.info("Fin Reporte Egresos...");
			_log.info("Reporte Egresos...armando Excel");
			HSSFWorkbook reporte = new HSSFWorkbook();
			reporte = generarReporteConceptoAgrupado(reporte, fechaIni,
					fechaFin, libroAgrupado);
			reporte = generarReporteConcepto(reporte, fechaIni, fechaFin, libro);
			reporte = generarReporteEgresoPrestacion(reporte, fechaIni,
					fechaFin, libroPrestacion);
			reporte = generarReporteEgresoPrestacionOS(reporte, fechaIni,
					fechaFin, libroPrestacionOS);
			reporte = generarReporteOtrosReintegrosPrestacionOS(reporte, fechaIni,
					fechaFin, libroOtrosReintegrosOS);
			_log.info("Reporte Egresos...FIN armando Excel");
			return reporte;
		} catch (Exception e) {
			_log.error("Error al generar listado estado comprobantes", e);
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private static HSSFWorkbook generarReporteOtrosReintegrosPrestacionOS(
			HSSFWorkbook wb, Date fechaIni, Date fechaFin,
			List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb
				.createSheet("Otros reintegros prestacionales abonados por la OS");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer(
				"Listado de egresos abonados por la OS - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		// cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		createHeaderOtrosReintegrosPrestacionOS(wb, sheet, styleHeader);

		BigDecimal total = BigDecimal.ZERO;
		
		int i = 3;

		for (EgresoLiquidacion l : libro) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell2 = row.createCell(0);
			cell2.setCellValue(new HSSFRichTextString(l.getCodPrestacion()));
			cell2.setCellStyle(styleAll);

			HSSFCell cell3 = row.createCell(1);
			cell3.setCellValue(new HSSFRichTextString(l.getDescripcion()));
			cell3.setCellStyle(styleAll);

			
			BigDecimal importe=l.getImporte();
			total = total.add(importe);
			HSSFCell cell6 = row.createCell(2);
			cell6.setCellValue(importe.doubleValue());
			cell6.setCellStyle(styleMoney);

			i++;
		}

		HSSFRow rowTotal = sheet.createRow(i);

		HSSFCell cellTotal = rowTotal.createCell(1);
		cellTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTotal.setCellStyle(styleBold);

		HSSFCell cellTotalValue = rowTotal.createCell(2);
		cellTotalValue.setCellValue(total.doubleValue());
		cellTotalValue.setCellStyle(styleMoneyBold);

		sheet.setColumnWidth(0, 10360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		return wb;
	}

	@SuppressWarnings("unused")
	private static HSSFWorkbook generarReporteEgresoPrestacionOS(
			HSSFWorkbook wb, Date fechaIni, Date fechaFin,
			List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb
				.createSheet("Monto abonado por la OS por prestaciones");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer(
				"Listado de egresos abonados por la OS - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		// cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		createHeaderEgresosPrestacionOS(wb, sheet, styleHeader);
		
		BigDecimal totalDiscapacidad = BigDecimal.ZERO;
		BigDecimal totalNoDiscapacidad = BigDecimal.ZERO;
		BigDecimal totalDebitadoDiscapacidad = BigDecimal.ZERO;
		BigDecimal totalDebitadoNoDiscapacidad = BigDecimal.ZERO;

		int i = 3;

		for (EgresoLiquidacion l : libro) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell2 = row.createCell(0);
			cell2.setCellValue(new HSSFRichTextString(l.getCodPrestacion()));
			cell2.setCellStyle(styleAll);

			HSSFCell cell3 = row.createCell(1);
			cell3.setCellValue(new HSSFRichTextString(l.getDescripcion()));
			cell3.setCellStyle(styleAll);

			
			BigDecimal importeNoDisca=BigDecimal.ZERO;
			BigDecimal importeDebitado=BigDecimal.ZERO;
			BigDecimal importeDisca=BigDecimal.ZERO;
			BigDecimal importeDebitadoDisca=BigDecimal.ZERO;
			
			if(l.getDescripcion().trim().equals("LIQUIDACIONES")){
				importeNoDisca = null!=l.getNoDiscapacidad()?l.getNoDiscapacidad().add(totalDebitadoOmintLiquidaciones):new BigDecimal(0);
				totalNoDiscapacidad = totalNoDiscapacidad.add(importeNoDisca);
				HSSFCell cell4 = row.createCell(2);
				cell4.setCellValue(importeNoDisca.doubleValue());
				cell4.setCellStyle(styleMoney);
				
				importeDebitado = totalDebitadoOmintLiquidaciones;
				totalDebitadoNoDiscapacidad = totalDebitadoNoDiscapacidad.add(importeDebitado);
				HSSFCell cell5 = row.createCell(3);
				cell5.setCellValue(importeDebitado.doubleValue());
				cell5.setCellStyle(styleMoney);
												
				HSSFCell cell51 = row.createCell(4);
				cell51.setCellValue(0);
				cell51.setCellStyle(styleMoney);
				
				HSSFCell cell52 = row.createCell(5);
				cell52.setCellValue(0);
				cell52.setCellStyle(styleMoney);
				
				/*BigDecimal totalLiq = l.getNoDiscapacidad().subtract(totalDebitadoOmintLiquidaciones);
				//totalDebitado = totalDebitado.add(importeDebitado);
				HSSFCell cell53 = row.createCell(6);
				cell53.setCellValue(totalLiq.doubleValue());
				cell53.setCellStyle(styleMoney);*/
			}else{
				importeNoDisca = l.getNoDiscapacidad()!=null?l.getNoDiscapacidad():BigDecimal.ZERO;
				totalNoDiscapacidad = totalNoDiscapacidad.add(importeNoDisca);
				HSSFCell cell4 = row.createCell(2);
				cell4.setCellValue(importeNoDisca.doubleValue());
				cell4.setCellStyle(styleMoney);
				
				importeDebitado = l.getDebitadoNoDiscapacidad()!=null?l.getDebitadoNoDiscapacidad():BigDecimal.ZERO;;
				totalDebitadoNoDiscapacidad = totalDebitadoNoDiscapacidad.add(importeDebitado);
				HSSFCell cell5 = row.createCell(3);
				cell5.setCellValue(importeDebitado.doubleValue());
				cell5.setCellStyle(styleMoney);
				
				importeDisca = l.getDiscapacidad()!=null?l.getDiscapacidad():BigDecimal.ZERO;;
				totalDiscapacidad = totalDiscapacidad.add(importeDisca!=null?importeDisca:BigDecimal.ZERO);
				HSSFCell cell51 = row.createCell(4);
				cell51.setCellValue(importeDisca!=null?importeDisca.doubleValue():BigDecimal.ZERO.doubleValue());
				cell51.setCellStyle(styleMoney);
				
				importeDebitadoDisca = l.getDebitadoDiscapacidad()!=null?l.getDebitadoDiscapacidad():BigDecimal.ZERO;;
				totalDebitadoDiscapacidad = totalDebitadoDiscapacidad.add(importeDebitadoDisca!=null?importeDebitadoDisca:BigDecimal.ZERO);
				HSSFCell cell6 = row.createCell(5);
				cell6.setCellValue(importeDebitadoDisca!=null?importeDebitadoDisca.doubleValue():BigDecimal.ZERO.doubleValue());
				cell6.setCellStyle(styleMoney);
			}

			BigDecimal totalOS = importeDisca.add(importeNoDisca)
						         .subtract(importeDebitado)
						         .subtract(importeDebitadoDisca);
			HSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(totalOS.doubleValue());
			cell6.setCellStyle(styleMoney);

			i++;
		}

		HSSFRow rowTotal = sheet.createRow(i);

		HSSFCell cellTotal = rowTotal.createCell(1);
		cellTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTotal.setCellStyle(styleBold);

		HSSFCell cellTotalValue = rowTotal.createCell(2);
		cellTotalValue.setCellValue(totalNoDiscapacidad.doubleValue());
		cellTotalValue.setCellStyle(styleMoneyBold);

		HSSFCell cellTotalValue2 = rowTotal.createCell(3);
		cellTotalValue2.setCellValue(totalDebitadoNoDiscapacidad.doubleValue());
		cellTotalValue2.setCellStyle(styleMoneyBold);
		
		HSSFCell cellTotalValue3 = rowTotal.createCell(4);
		cellTotalValue3.setCellValue(totalDiscapacidad.doubleValue());
		cellTotalValue3.setCellStyle(styleMoneyBold);
		
		HSSFCell cellTotalValue4 = rowTotal.createCell(5);
		cellTotalValue4.setCellValue(totalDebitadoDiscapacidad.doubleValue());
		cellTotalValue4.setCellStyle(styleMoneyBold);
		
		BigDecimal totalOSFinal = BigDecimal.ZERO;
		
		totalOSFinal = totalNoDiscapacidad.add(totalDiscapacidad).subtract(totalDebitadoNoDiscapacidad).subtract(totalDebitadoDiscapacidad);
		HSSFCell cellTotalValue5 = rowTotal.createCell(6);
		cellTotalValue5.setCellValue(totalOSFinal.doubleValue());
		cellTotalValue5.setCellStyle(styleMoneyBold);
		
		i=i+2;
		HSSFRow rowTotalFinal = sheet.createRow(i);

		HSSFCell cellTotalAbonaXOS = rowTotalFinal.createCell(1);
		cellTotalAbonaXOS.setCellValue(new HSSFRichTextString("Abonado x OS No Discapacidad"));
		cellTotalAbonaXOS.setCellStyle(styleBold);
		
		BigDecimal totalNoDiscaFinal=totalNoDiscapacidad.subtract(totalDebitadoNoDiscapacidad);
		HSSFCell cellTotalAbonaXOS2 = rowTotalFinal.createCell(2);
		cellTotalAbonaXOS2.setCellValue(totalNoDiscaFinal.doubleValue());
		cellTotalAbonaXOS2.setCellStyle(styleMoneyBold);
		
		i++;
		
		HSSFRow rowTotalFinal2 = sheet.createRow(i);

		HSSFCell cellTotalAbonaXOS3 = rowTotalFinal2.createCell(1);
		cellTotalAbonaXOS3.setCellValue(new HSSFRichTextString("Abonado x OS Discapacidad"));
		cellTotalAbonaXOS3.setCellStyle(styleBold);
				
		BigDecimal abonadoXOSDiscaFinal=totalDiscapacidad.subtract(totalDebitadoDiscapacidad);
		HSSFCell cellTotalOSDiscaFinal= rowTotalFinal2.createCell(2);
		cellTotalOSDiscaFinal.setCellValue(abonadoXOSDiscaFinal.doubleValue());
		cellTotalOSDiscaFinal.setCellStyle(styleMoneyBold);
		
		i++;		
		HSSFRow rowTotalFinal3 = sheet.createRow(i);
		
		HSSFCell cellTotalAbonaXOS4 = rowTotalFinal3.createCell(1);
		cellTotalAbonaXOS4.setCellValue(new HSSFRichTextString("Abonado x OS Total"));
		cellTotalAbonaXOS4.setCellStyle(styleBold);
		
		BigDecimal abonadoXOSTotal=totalNoDiscaFinal.add(abonadoXOSDiscaFinal);
		HSSFCell cellTotalFinal= rowTotalFinal3.createCell(2);
		cellTotalFinal.setCellValue(abonadoXOSTotal.doubleValue());
		cellTotalFinal.setCellStyle(styleMoneyBold);
		
		
		sheet.setColumnWidth(0, 10360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		return wb;
	}

	@SuppressWarnings("unused")
	private static HSSFWorkbook generarReporteEgresoPrestacion(HSSFWorkbook wb,
			Date fechaIni, Date fechaFin, List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Egresos por Prestación");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer(
				"Listado de Egresos por Prestaciones, Cta. Nación 79848/46 y 800352/44 OP - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		// cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		createHeaderEgresosPrestacion(wb, sheet, styleHeader);

		BigDecimal total = BigDecimal.ZERO;
		BigDecimal totalNoDisca = BigDecimal.ZERO;
		BigDecimal totalDisca = BigDecimal.ZERO;
		BigDecimal totalOtrosReintegrosCompro = BigDecimal.ZERO;
		BigDecimal totalAmtimaRestoMed = BigDecimal.ZERO;
		BigDecimal totalDebitadoOmintReintegros = BigDecimal.ZERO;
		BigDecimal totalliqConveniosGlobales = BigDecimal.ZERO;
		BigDecimal totalliqNoDiscrimina= BigDecimal.ZERO;

		boolean tieneOtraCuenta = false;

		int i = 3;

		for (EgresoLiquidacion l : libro) {
			if ( l.getCuenta()!=null && (l.getCuenta().equals("79848/46") || l.getCuenta().equals("800352/44"))
					&& !l.getCodPrestacion().equals("000000")) {
				HSSFRow row = sheet.createRow(i);

				HSSFCell cell2 = row.createCell(0);
				cell2.setCellValue(new HSSFRichTextString(l.getCodPrestacion()));
				cell2.setCellStyle(styleAll);

				HSSFCell cell3 = row.createCell(1);
				cell3.setCellValue(new HSSFRichTextString(l.getCuenta()));
				cell3.setCellStyle(styleAll);

				HSSFCell cell4 = row.createCell(2);
				cell4.setCellValue(new HSSFRichTextString(l.getDescripcion()));
				cell4.setCellStyle(styleAll);

				HSSFCell cell5 = row.createCell(3);
				cell5.setCellValue(l.getNoDiscapacidad().doubleValue());
				cell5.setCellStyle(styleMoney);

				HSSFCell cell6 = row.createCell(4);
				cell6.setCellValue(l.getDiscapacidad().doubleValue());
				cell6.setCellStyle(styleMoney);

				if (l.getDiscapacidad().equals(BigDecimal.ZERO)						
						&& l.getNoDiscapacidad().equals(BigDecimal.ZERO)
						&& l.getImporte() != null && !l.getImporte().equals(BigDecimal.ZERO)) {
					BigDecimal importe= l.getImporte();
					totalliqNoDiscrimina= totalliqNoDiscrimina.add(importe);
					HSSFCell cell7 = row.createCell(5);
					cell7.setCellValue(l.getImporte().doubleValue());
					cell7.setCellStyle(styleMoney);				

				}else{
					HSSFCell cell7 = row.createCell(5);
					cell7.setCellValue(0);
					cell7.setCellStyle(styleMoney);
				}

				HSSFCell cell7 = row.createCell(6);
				cell7.setCellValue(l.getImporte().doubleValue());
				cell7.setCellStyle(styleMoney);

				BigDecimal importe = l.getImporte();
				total = total.add(importe);
				BigDecimal importeNoDisca = l.getNoDiscapacidad();
				totalNoDisca = totalNoDisca.add(importeNoDisca);
				BigDecimal importeDisca = l.getDiscapacidad();
				totalDisca = totalDisca.add(importeDisca);
				if (l.getDescripcion().trim()
						.equals("OTROS REINTEGROS (X COMPROBANTES)")) {
					BigDecimal aux = l.getImporte();
					totalOtrosReintegrosCompro = totalOtrosReintegrosCompro
							.add(aux);

					HSSFCell cellA = row.createCell(7);
					cellA.setCellValue(new HSSFRichTextString("A"));
				}
				if (l.getDescripcion().trim()
						.equals("AMTIMA - RESTO MEDICAMENTOS - CONSULTORIO")) {
					BigDecimal aux = l.getImporte();
					totalAmtimaRestoMed = totalAmtimaRestoMed.add(aux);
					HSSFCell cellD = row.createCell(7);
					cellD.setCellValue(new HSSFRichTextString("D"));
				}
				if (l.getDescripcion().trim()
						.equals("LIQUIDACIONES CONVENIOS GLOBALES")) {
					BigDecimal aux = l.getImporte();
					totalliqConveniosGlobales = totalliqConveniosGlobales
							.add(aux);
				}
				i++;
			} else if (!l.getCodPrestacion().equals("000000")) {
				tieneOtraCuenta = true;
			} else if (l.getCodPrestacion().equals("000000")) {
				BigDecimal aux = l.getImporte();
				totalDebitadoOmintReintegros = totalDebitadoOmintReintegros
						.add(aux);
			}

		}

		HSSFRow rowTotal = sheet.createRow(i);
		HSSFCell cellTotal = rowTotal.createCell(2);
		cellTotal.setCellValue(new HSSFRichTextString(
				"SUBTOTAL CUENTA 79848/46 y 800352/44"));
		cellTotal.setCellStyle(styleBold);

		HSSFCell cellTotalValue = rowTotal.createCell(3);
		cellTotalValue.setCellValue(totalNoDisca.doubleValue());
		cellTotalValue.setCellStyle(styleMoneyBold);

		HSSFCell cellTotalNoValue = rowTotal.createCell(4);
		cellTotalNoValue.setCellValue(totalDisca.doubleValue());
		cellTotalNoValue.setCellStyle(styleMoneyBold);
		
		HSSFCell cellTotalNoDiscriValue = rowTotal.createCell(5);
		cellTotalNoDiscriValue.setCellValue(totalliqNoDiscrimina.doubleValue());
		cellTotalNoDiscriValue.setCellStyle(styleMoneyBold);

		HSSFCell cellTotalDiscaValue = rowTotal.createCell(6);
		cellTotalDiscaValue.setCellValue(total.doubleValue());
		cellTotalDiscaValue.setCellStyle(styleMoneyBold);

		HSSFCell cellB = rowTotal.createCell(7);
		cellB.setCellValue(new HSSFRichTextString("B"));

		i++;

		BigDecimal totalC = BigDecimal.ZERO;
		BigDecimal totalNoDiscaC = BigDecimal.ZERO;
		BigDecimal totalDiscaC = BigDecimal.ZERO;

		if (tieneOtraCuenta) {
			for (EgresoLiquidacion l : libro) {
				if ( l.getCuenta()!= null && !(l.getCuenta().equals("79848/46") || l.getCuenta().equals("800352/44"))
						&& !l.getCodPrestacion().equals("000000")) {
					HSSFRow row = sheet.createRow(i);

					HSSFCell cell2 = row.createCell(0);
					cell2.setCellValue(new HSSFRichTextString(l
							.getCodPrestacion()));
					cell2.setCellStyle(styleAll);

					HSSFCell cell3 = row.createCell(1);
					cell3.setCellValue(new HSSFRichTextString(l.getCuenta()));
					cell3.setCellStyle(styleAll);

					HSSFCell cell4 = row.createCell(2);
					cell4.setCellValue(new HSSFRichTextString(l
							.getDescripcion()));
					cell4.setCellStyle(styleAll);

					HSSFCell cell5 = row.createCell(3);
					cell5.setCellValue(l.getNoDiscapacidad().doubleValue());
					cell5.setCellStyle(styleMoney);

					HSSFCell cell6 = row.createCell(4);
					cell6.setCellValue(l.getDiscapacidad().doubleValue());
					cell6.setCellStyle(styleMoney);
					
					if (l.getDiscapacidad() == null
							&& l.getDiscapacidad().equals(BigDecimal.ZERO)
							&& l.getNoDiscapacidad() == null
							&& l.getNoDiscapacidad().equals(BigDecimal.ZERO)
							&& l.getImporte() != null && !l.getImporte().equals(BigDecimal.ZERO)) {
						BigDecimal importe= l.getImporte();
						totalliqNoDiscrimina= totalliqNoDiscrimina.add(importe);
						HSSFCell cell7 = row.createCell(5);
						cell7.setCellValue(l.getImporte().doubleValue());
						cell7.setCellStyle(styleMoney);				

					}else{
						HSSFCell cell7 = row.createCell(5);
						cell7.setCellValue(0);
						cell7.setCellStyle(styleMoney);
					}

					HSSFCell cell7 = row.createCell(6);
					cell7.setCellValue(l.getImporte().doubleValue());
					cell7.setCellStyle(styleMoney);

					BigDecimal importe = l.getImporte();
					totalC = totalC.add(importe);
					BigDecimal importeNoDisca = l.getNoDiscapacidad();
					totalNoDiscaC = totalNoDiscaC.add(importeNoDisca);
					BigDecimal importeDisca = l.getDiscapacidad();
					totalDiscaC = totalDiscaC.add(importeDisca);

					if (l.getDescripcion().trim()
							.equals("OTROS REINTEGROS (X COMPROBANTES)")) {
						BigDecimal aux = l.getImporte();
						totalOtrosReintegrosCompro = totalOtrosReintegrosCompro
								.add(aux);
					}
					if (l.getDescripcion()
							.trim()
							.equals("AMTIMA - RESTO MEDICAMENTOS - CONSULTORIO")) {
						BigDecimal aux = l.getImporte();
						totalAmtimaRestoMed = totalAmtimaRestoMed.add(aux);
						HSSFCell cellD = row.createCell(6);
						cellD.setCellValue(new HSSFRichTextString("D"));
					}
					if (l.getDescripcion().trim()
							.equals("LIQUIDACIONES CONVENIOS GLOBALES")) {
						BigDecimal aux = l.getImporte();
						totalliqConveniosGlobales = totalliqConveniosGlobales
								.add(aux);
					}
					i++;
				}
//				i++;
			}

			HSSFRow rowTotalC = sheet.createRow(i);
			HSSFCell cellTotalC = rowTotalC.createCell(2);
			cellTotalC.setCellValue(new HSSFRichTextString(
					"SUBTOTAL OTRAS CUENTAS"));
			cellTotalC.setCellStyle(styleBold);

			HSSFCell cellTotalValueC = rowTotalC.createCell(3);
			cellTotalValueC.setCellValue(totalNoDiscaC.doubleValue());
			cellTotalValueC.setCellStyle(styleMoneyBold);

			HSSFCell cellTotalNoValueC = rowTotalC.createCell(4);
			cellTotalNoValueC.setCellValue(totalDiscaC.doubleValue());
			cellTotalNoValueC.setCellStyle(styleMoneyBold);
			
			HSSFCell cellTotalNoDiscriminaC = rowTotalC.createCell(5);
			cellTotalNoDiscriminaC.setCellValue(totalliqNoDiscrimina.doubleValue());
			cellTotalNoDiscriminaC.setCellStyle(styleMoneyBold);

			HSSFCell cellTotalDiscaValueC = rowTotalC.createCell(6);
			cellTotalDiscaValueC.setCellValue(totalC.doubleValue());
			cellTotalDiscaValueC.setCellStyle(styleMoneyBold);

			i++;

		}

		HSSFRow rowTotalC = sheet.createRow(i);
		HSSFCell cellTotalC = rowTotalC.createCell(2);
		cellTotalC.setCellValue(new HSSFRichTextString("TOTAL"));
		cellTotalC.setCellStyle(styleBold);

		HSSFCell cellTotalValueC = rowTotalC.createCell(3);
		cellTotalValueC.setCellValue(totalNoDiscaC.add(totalNoDisca)
				.doubleValue());
		cellTotalValueC.setCellStyle(styleMoneyBold);

		HSSFCell cellTotalNoValueC = rowTotalC.createCell(4);
		cellTotalNoValueC.setCellValue(totalDiscaC.add(totalDisca)
				.doubleValue());
		cellTotalNoValueC.setCellStyle(styleMoneyBold);
		
		HSSFCell cellTotalNoDiscriValueC = rowTotalC.createCell(5);
		cellTotalNoDiscriValueC.setCellValue(totalliqNoDiscrimina.doubleValue());
		cellTotalNoDiscriValueC.setCellStyle(styleMoneyBold);

		HSSFCell cellTotalDiscaValueC = rowTotalC.createCell(6);
		cellTotalDiscaValueC.setCellValue(totalC.add(total).doubleValue());
		cellTotalDiscaValueC.setCellStyle(styleMoneyBold);

		HSSFCell cellC = rowTotalC.createCell(7);
		cellC.setCellValue(new HSSFRichTextString("C"));

		i = i + 2;

		HSSFRow rowResumen = sheet.createRow(i);
		HSSFCell cellResumen = rowResumen.createCell(2);
		cellResumen.setCellValue(new HSSFRichTextString(
				"Monto total de prestaciones (B+C-A-D)"));
		cellResumen.setCellStyle(styleBold);

		HSSFCell cellResumen3 = rowResumen.createCell(3);
		BigDecimal totalBCAD = total.add(totalC)
				.subtract(totalOtrosReintegrosCompro)
				.subtract(totalAmtimaRestoMed);
		cellResumen3.setCellValue(totalBCAD.doubleValue());
		cellResumen3.setCellStyle(styleMoneyBold);

		i++;
		HSSFRow rowResumen1 = sheet.createRow(i);
		HSSFCell cellResumen1 = rowResumen1.createCell(2);
		cellResumen1.setCellValue(new HSSFRichTextString(
				"Debitado a Omint x Reintegros"));
		cellResumen1.setCellStyle(styleBold);

		HSSFCell cellResumen2 = rowResumen1.createCell(3);
		cellResumen2.setCellValue(totalDebitadoOmintReintegros.doubleValue());
		cellResumen2.setCellStyle(styleMoneyBold);

		i++;
		HSSFRow rowResumen12 = sheet.createRow(i);
		HSSFCell cellResumen121 = rowResumen12.createCell(2);
		cellResumen121.setCellValue(new HSSFRichTextString(
				"Debitado a Omint x Liquidaciones"));
		cellResumen121.setCellStyle(styleBold);
		
		totalDebitadoOmintLiquidaciones=totalDebitadoOmintLiquidaciones.add(totalliqConveniosGlobales);

		HSSFCell cellResumen122 = rowResumen12.createCell(3);
		cellResumen122.setCellValue(totalliqConveniosGlobales.doubleValue());
		cellResumen122.setCellStyle(styleMoneyBold);

		i++;
		HSSFRow rowResumen2 = sheet.createRow(i);
		HSSFCell cellResumen21 = rowResumen2.createCell(2);
		cellResumen21.setCellValue(new HSSFRichTextString(
				"Monto abonado por OS"));
		cellResumen21.setCellStyle(styleBold);

		BigDecimal totalAbonado = totalBCAD.subtract(
				totalDebitadoOmintReintegros).subtract(
				totalliqConveniosGlobales);
		HSSFCell cellResumen22 = rowResumen2.createCell(3);
		cellResumen22.setCellValue(totalAbonado.doubleValue());
		cellResumen22.setCellStyle(styleMoneyBold);

		sheet.setColumnWidth(0, 10360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		return wb;
	}

	private static HSSFWorkbook generarReporteConceptoAgrupado(HSSFWorkbook wb,
			Date fechaIni, Date fechaFin, List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Egresos por Concepto Agrupados");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer(
				"Listado de Egresos por Conceptos Agrupados, Cta. Nación 79848/46 y 800352/44 OP - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		// cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		createHeader(wb, sheet, styleHeader);

		BigDecimal total = BigDecimal.ZERO;

		int i = 3;

		for (EgresoLiquidacion l : libro) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell2 = row.createCell(0);
			cell2.setCellValue(new HSSFRichTextString(l.getDescripcion()));
			cell2.setCellStyle(styleAll);

			BigDecimal importe = l.getImporte();
			total = total.add(importe);
			HSSFCell cell3 = row.createCell(1);
			cell3.setCellValue(importe.doubleValue());
			cell3.setCellStyle(styleMoney);

			i++;
		}

		HSSFRow rowTotal = sheet.createRow(i);

		HSSFCell cellTotal = rowTotal.createCell(0);
		cellTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTotal.setCellStyle(styleBold);

		HSSFCell cellTotalValue = rowTotal.createCell(1);
		cellTotalValue.setCellValue(total.doubleValue());
		cellTotalValue.setCellStyle(styleMoneyBold);

		sheet.setColumnWidth(0, 10360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		return wb;
	}

	private static HSSFWorkbook generarReporteConcepto(HSSFWorkbook wb,
			Date fechaIni, Date fechaFin, List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Egresos por Concepto");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer(
				"Listado de Egresos por Concepto Cta. Nación 79848/46 y 800352/44 OP - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

		createHeader(wb, sheet, styleHeader);

		BigDecimal total = BigDecimal.ZERO;

		int i = 3;

		for (EgresoLiquidacion l : libro) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell2 = row.createCell(0);
			cell2.setCellValue(new HSSFRichTextString(l.getDescripcion()));
			cell2.setCellStyle(styleAll);

			BigDecimal importe = l.getImporte();
			total = total.add(importe);
			HSSFCell cell3 = row.createCell(1);
			cell3.setCellValue(importe.doubleValue());
			cell3.setCellStyle(styleMoney);

			i++;
		}

		HSSFRow rowTotal = sheet.createRow(i);
		HSSFCell cellTotal = rowTotal.createCell(0);
		cellTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTotal.setCellStyle(styleBold);

		HSSFCell cellTotalValue = rowTotal.createCell(1);
		cellTotalValue.setCellValue(total.doubleValue());
		cellTotalValue.setCellStyle(styleMoneyBold);

		sheet.setColumnWidth(0, 10360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		return wb;
	}
	
	private static void createHeaderOtrosReintegrosPrestacionOS(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(2);

		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Cod. Prestación"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(1);
		cell3.setCellValue(new HSSFRichTextString("Prestación"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(2);
		cell4.setCellValue(new HSSFRichTextString("Importe"));
		cell4.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	}

	private static void createHeaderEgresosPrestacionOS(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(2);

		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Cod. Prestación"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(1);
		cell3.setCellValue(new HSSFRichTextString("Prestación"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(2);
		cell4.setCellValue(new HSSFRichTextString("No Discapacidad"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(3);
		cell5.setCellValue(new HSSFRichTextString("Debitado No Discapacidad"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(4);
		cell6.setCellValue(new HSSFRichTextString("Discapacidad"));
		cell6.setCellStyle(styleHeader);
		
		HSSFCell cell7 = row.createCell(5);
		cell7.setCellValue(new HSSFRichTextString("Debitado Discapacidad"));
		cell7.setCellStyle(styleHeader);
		
		HSSFCell cell8 = row.createCell(6);
		cell8.setCellValue(new HSSFRichTextString("Total abonado X OS"));
		cell8.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	}

	private static void createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(2);

		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Descripción"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(1);
		cell3.setCellValue(new HSSFRichTextString("Importe"));
		cell3.setCellStyle(styleHeader);
		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	}

	private static void createHeaderEgresosPrestacion(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(2);

		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Cod. Prestacion"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(1);
		cell3.setCellValue(new HSSFRichTextString("Cuenta"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(2);
		cell4.setCellValue(new HSSFRichTextString("Prestación"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(3);
		cell5.setCellValue(new HSSFRichTextString("No Discapacidad"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(4);
		cell6.setCellValue(new HSSFRichTextString("Discapacidad"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell61 = row.createCell(5);
		cell61.setCellValue(new HSSFRichTextString("Sin Discriminar"));
		cell61.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(6);
		cell8.setCellValue(new HSSFRichTextString("Total"));
		cell8.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	}

}

package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import com.liferay.portal.SystemException;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.CuentaCorriente.Informacion;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente.SaldoInicial;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteCuentaCorriente extends ReporteXLS {

	protected static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<CuentaCorriente> ctas, boolean mostrarPeriodo,
			List<EstadoInicialCuentaCorriente> saldoIni, boolean soloConSaldo,
			boolean mostrarSoloComprobantesConSaldo, boolean mostrarMasInfo, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros, boolean incluirLiquidaciones_farmacia, boolean incluirReintegros_farmacia, int entidad) throws SystemException {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDate = getStyleDate(wb);
		styleDate.setBorderLeft(BorderStyle.THIN);
		styleDate.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleMoneyBorder = getStyleMoney(wb);
		styleMoneyBorder.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle stylePeriodo = getStyleDate(wb);
		stylePeriodo.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleBoldLeft = getStyleBold(wb);
		styleBoldLeft.setBorderLeft(BorderStyle.THIN);
		styleBoldLeft.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldCenter = getStyleBold(wb);
		styleBoldCenter.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldRight = getStyleBold(wb);
		styleBoldRight.setBorderTop(BorderStyle.THIN);
		styleBoldRight.setBorderRight(BorderStyle.THIN);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		StringBuilder headerRight = new StringBuilder();
		headerRight.append("N° de hoja: " + HSSFHeader.page());
		headerRight.append(" de " + HSSFHeader.numPages());
		headerRight.append("\n");
		headerRight.append(DateUtils.format(new Date(), DateUtils.LONG_SEC));
		headerRight.append("\n");
		sheet.getHeader().setRight(headerRight.toString());

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		cellTitulo.setCellValue(new HSSFRichTextString(
				"Cuentas Corrientes - Desde: "
						+ DateUtils.format(fechaIni, DateUtils.SHORT)
						+ " Hasta: "
						+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, mostrarPeriodo ? 7
				: 6));

		int i = 1;
		BigDecimal total = BigDecimal.ZERO;
		i = crearHeaderPpal(sheet, i, styleHeader, mostrarPeriodo,
				mostrarMasInfo, entidad, wb);
		for (CuentaCorriente cta : ctas) {			
			saldoIni = ContabilidadServiceUtil
					.getSaldoInicialCtasCtes(cta.getEmpresa().getCuit(), cta.getEmpresa().getSucursal(), cta.getEmpresa().getId_seccional(), fechaIni, incluirProveedores,
							incluirLiquidaciones, incluirReintegros, incluirLiquidaciones_farmacia, incluirReintegros_farmacia, entidad);
			i = crearHeader(cta.getEmpresa(), sheet, i, styleBoldLeft,
					styleBoldCenter, styleBoldRight, mostrarPeriodo,
					mostrarMasInfo, entidad);
			ResultadoAuxiliar ra =null;
			
			ra = crearDatos(cta, sheet, i, styleDate,
						stylePeriodo, styleAll, styleMoney, styleMoneyBorder,
						mostrarPeriodo, saldoIni, fechaIni, fechaFin, soloConSaldo,
						mostrarSoloComprobantesConSaldo, mostrarMasInfo, entidad);
			int cont=ra.getI();
			if(soloConSaldo && saldoIni.get(0).getSaldosIniciales().get(0).getImporte().add(ra.getTotal()).compareTo(BigDecimal.ZERO)==0){
				for(int j=i;j<ra.getI();j++){
					sheet.removeRow(sheet.getRow(j));
					cont--;
				}
				i=--cont;
			}else{
				i = ra.getI();
			}
			total = total.add(ra.getTotal());
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleBoldCenter);
		int cant = 4;
		if (mostrarPeriodo) {
			cant++;
		}
		if (mostrarMasInfo) {
			cant++;
			cant++;
		}
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, cant));

		String totalStr = "Total General";
		if (mostrarSoloComprobantesConSaldo) {
			totalStr = "Total de Comprobantes";
		}

		HSSFRow rowTotal = sheet.createRow(i + 2);
		HSSFCell cellTotalTxt = rowTotal.createCell(mostrarPeriodo ? 4 : 3);
		cellTotalTxt.setCellValue(new HSSFRichTextString(totalStr));
		cellTotalTxt.setCellStyle(styleBoldCenter);

		HSSFCell cellTotal = rowTotal.createCell(mostrarPeriodo ? 5 : 4);
		cellTotal.setCellValue(total.doubleValue());
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		cellTotal.setCellStyle(styleMoneyBold);

		sheet.autoSizeColumn((short) 0);
		if (mostrarPeriodo) {
			sheet.autoSizeColumn((short) 1);
			sheet.setColumnWidth(2, 15360);
		} else {
			sheet.setColumnWidth(1, 15360);
			sheet.autoSizeColumn((short) 2);
		}

		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);

		if (mostrarPeriodo) {
			sheet.autoSizeColumn((short) 5);
		}
		return wb;
	}

	private static int crearHeaderPpal(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, boolean mostrarPeriodo,
			boolean mostrarMasInfo, int entidad,  HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Cuit"));
		cell.setCellStyle(styleHeader);

		int indexBase = 1;
		if (mostrarPeriodo) {
			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString("Periodo"));
			cell1.setCellStyle(styleHeader);
			indexBase++;
		}

		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString("Razón social"));
		cell2.setCellStyle(styleHeader);

		int index2 = 0;
		if (mostrarMasInfo) {
			int indexMostra=indexBase;
			if(entidad==WebKeysGlobal.UOMA){
				HSSFCell cell3 = row.createCell(++indexMostra);
				cell3.setCellValue(new HSSFRichTextString("Obs. Compro "));
				cell3.setCellStyle(styleHeader);
				index2++;
			}
			HSSFCell cell4 = row.createCell(++indexMostra);
			cell4.setCellValue(new HSSFRichTextString("OP"));
			cell4.setCellStyle(styleHeader);
			index2++;
		}

		HSSFCell cell3 = row.createCell(indexBase + index2 + 1);
		cell3.setCellValue(new HSSFRichTextString("Debe"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(indexBase + index2 + 2);
		cell4.setCellValue(new HSSFRichTextString("Haber"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(indexBase + index2 + 3);
		cell5.setCellValue(new HSSFRichTextString("Saldo"));
		cell5.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, indexBase + index2 + 3, i, i);

		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		
		i++;
		return i;
	}

	
	
	private static ResultadoAuxiliar crearDatos(CuentaCorriente cta,
			HSSFSheet sheet, int i, HSSFCellStyle styleDate,
			HSSFCellStyle stylePeriodo, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoney, HSSFCellStyle styleMoneyBorder,
			boolean mostrarPeriodo,
			List<EstadoInicialCuentaCorriente> saldoIni, Date fechaIni,
			Date fechaFin, boolean soloConSaldo,
			boolean mostrarSoloComprobantesConSaldo, boolean mostrarMasInfo, int entidad) {
		
		BigDecimal saldo = BigDecimal.ZERO;
		saldo=	saldoIni.get(0).getSaldosIniciales().get(0).getImporte();
		getRowSaldoInicial(fechaIni, saldoIni.get(0).getSaldosIniciales().get(0).getImporte(), styleMoney, sheet,
					styleDate, styleAll, i, mostrarPeriodo, mostrarMasInfo,
					"Saldo Inicial Calculado", entidad);
		i++;
	

		int indexBase = 1;
		if (mostrarPeriodo) {
			indexBase++;
		}
				
		for (Informacion info : cta.getInfo()) {
			if (info.getFecha().compareTo(fechaIni) >= 0 && info.getFecha().compareTo(fechaFin) <= 0) {
				if (mostrarSoloComprobantesConSaldo
						&& (!info.isDeuda() || info.getPagadaFecha() != null)) {
					continue;
				}

				HSSFRow row = sheet.createRow(i);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue(info.getFecha());
				cell.setCellStyle(styleDate);

				if (mostrarPeriodo) {
					HSSFCell cell1 = row.createCell(1);
					if (info.getPeriodo() != null) {
						cell1.setCellValue(info.getPeriodo());
					} else {
						cell1.setCellValue(new HSSFRichTextString(" "));
					}
					cell1.setCellStyle(stylePeriodo);
				}

				HSSFCell cell2 = row.createCell(indexBase);
				cell2.setCellValue(new HSSFRichTextString(info.getDescripcion()));
				cell2.setCellStyle(styleAll);

				BigDecimal importeDebe = BigDecimal.ZERO;
				BigDecimal importeHaber = BigDecimal.ZERO;
				if (info.getDebitoCredito().trim().equals("D") || info.getDebitoCredito().trim().equals("C-D")) {
					saldo = saldo.subtract(info.getImporte());
					importeHaber = info.getImporte();
				}  
				if(info.getDebitoCredito().trim().equals("C")|| info.getDebitoCredito().trim().equals("C-D")){
					saldo = saldo.add(info.getImporte());
					importeDebe = info.getImporte();
				} 
				if(info.getDebitoCredito().trim().equals("I")){
					saldo = info.getImporte();
				}

				int index2 = 0;
				if (mostrarMasInfo) {
					int indexMostro=indexBase;
					if(entidad==WebKeysGlobal.UOMA){
						HSSFCell cellMasInfo = row.createCell(++indexMostro);
						cellMasInfo.setCellValue(new HSSFRichTextString(info.getObservacionCompro()));
						cellMasInfo.setCellStyle(styleAll);
						index2++;
					}
					HSSFCell cellMasInfo2 = row.createCell(++indexMostro);
					cellMasInfo2.setCellValue(info.getIdPago());
					cellMasInfo2.setCellStyle(styleAll);
					index2++;
				}

				HSSFCell cell3 = row.createCell(indexBase + index2 + 1);
				if(importeDebe.compareTo(BigDecimal.ZERO)>0){
					cell3.setCellValue(importeDebe.doubleValue());
				}else{
					cell3.setCellValue(new HSSFRichTextString(""));					
				}
				cell3.setCellStyle(styleMoney);

				HSSFCell cell4 = row.createCell(indexBase + index2 + 2);
				if(importeHaber.compareTo(BigDecimal.ZERO)!=0){
					cell4.setCellValue(importeHaber.doubleValue());
				}else{
					cell4.setCellValue(new HSSFRichTextString(""));					
				}
				cell4.setCellStyle(styleMoney);

				HSSFCell cell5 = row.createCell(indexBase + index2 + 3);
				cell5.setCellValue(saldo.doubleValue());
				cell5.setCellStyle(styleMoneyBorder);

				i++;
			}
		}

		if (mostrarSoloComprobantesConSaldo) {
			soloConSaldo = true;
		}

		/*if (soloConSaldo && saldo.compareTo(BigDecimal.ZERO) == 0) {
			for (int j = indexIni; j < i; j++) {
				sheet.removeRow(sheet.getRow(j));
			}
			i = indexIni;
		}*/

		ResultadoAuxiliar ra = new ResultadoAuxiliar();
		ra.setI(i);
		ra.setTotal(saldo);
		return ra;
	}

	private static void getRowSaldoInicial(Date fecha, BigDecimal saldo,
			HSSFCellStyle styleMoney, HSSFSheet sheet, HSSFCellStyle styleDate,
			HSSFCellStyle styleAll, int i, boolean mostrarPeriodo,
			boolean mostrarMasInfo, String texto, int entidad) {

		HSSFRow rowSaldoIni = sheet.createRow(i);

		HSSFCell cell0 = rowSaldoIni.createCell(0);
		cell0.setCellValue(fecha);
		cell0.setCellStyle(styleDate);

		if (mostrarPeriodo) {
			HSSFCell cell1 = rowSaldoIni.createCell(1);
			cell1.setCellStyle(styleAll);
		}

		int indexBase = 1;
		if (mostrarPeriodo) {
			indexBase++;
		}

		HSSFCell cell2 = rowSaldoIni.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString(texto));
		cell2.setCellStyle(styleAll);

		int index2 = 0;
		if (mostrarMasInfo) {
			int indexMostra=indexBase;
			if(entidad==WebKeysGlobal.UOMA){
				HSSFCell cell = rowSaldoIni.createCell(++indexMostra);
				cell.setCellStyle(styleAll);
				index2++;
			}
			HSSFCell cell2a = rowSaldoIni.createCell(++indexMostra);
			cell2a.setCellStyle(styleAll);
			index2++;
		}

		HSSFCell cell3 = rowSaldoIni.createCell(indexBase + index2 + 1);
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowSaldoIni.createCell(indexBase + index2 + 2);
		cell4.setCellStyle(styleAll);

		HSSFCell cellS = rowSaldoIni.createCell(indexBase + index2 + 3);
		cellS.setCellValue(saldo.doubleValue());
		cellS.setCellStyle(styleMoney);

	}

	private static BigDecimal calcularRowSaldoInicial(
			List<SaldoInicial> saldosIniciales, Date fechaIni, Empresa empresa,
			List<Informacion> info, HSSFCellStyle styleMoney, HSSFSheet sheet,
			HSSFCellStyle styleDate, HSSFCellStyle styleAll, int i,
			boolean mostrarPeriodo, boolean mostrarMasInfo, int entidad) {

		BigDecimal saldoInicial = BigDecimal.ZERO;
		SaldoInicial minSaldoIni = null;
		if (saldosIniciales != null && saldosIniciales.size() > 0) {
			minSaldoIni = (SaldoInicial) Collections.min(saldosIniciales);
			if (DateUtils.compararFechasTruncarEnDia(minSaldoIni.getFecha(),
					fechaIni) < 0) {
				saldoInicial = minSaldoIni.getImporte();
			}
		}

		Iterator<Informacion> it = info.iterator();
		boolean stop = false;
		while (it.hasNext() && !stop) {
			Informacion l = it.next();
			// el saldo inicial a la fecha XX es al ppio de ese dia.
			// (el saldo inicial no incluye los movimientos del dia XX)
			if (DateUtils.compararFechasTruncarEnDia(l.getFecha(), fechaIni) < 0) {
				
//DS - Agregado para corregir no contemplacion de saldo forzado con registros menores a fecha Inicial				
				if(minSaldoIni!=null) {
					if (DateUtils.compararFechasTruncarEnDia(l.getFecha(), minSaldoIni.getFecha()) < 0) {
						saldoInicial=minSaldoIni.getImporte();
					}else {
						if (l.getDebitoCredito().equals("D")) {
							saldoInicial = saldoInicial.subtract(l.getImporte());
						} else {
							saldoInicial = saldoInicial.add(l.getImporte());
						}
					}
				}else {
				
				  if (l.getDebitoCredito().equals("D")) {
					saldoInicial = saldoInicial.subtract(l.getImporte());
				  } else {
					saldoInicial = saldoInicial.add(l.getImporte());
				  }
				
				}
//DS - Fin				
			} else {
				stop = true;
			}
		}

		getRowSaldoInicial(fechaIni, saldoInicial, styleMoney, sheet,
				styleDate, styleAll, i, mostrarPeriodo, mostrarMasInfo,
				"Saldo Inicial Calculado", entidad);

		return saldoInicial;
	}

	private static int crearHeader(Empresa e, HSSFSheet sheet, int i,
			HSSFCellStyle styleLeft, HSSFCellStyle styleBoldCenter,
			HSSFCellStyle styleBoldRight, boolean mostrarPeriodo,
			boolean mostrarMasInfo, int entidad) {
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(e.getCuit() + "-"
				+ e.getSucursal()));
		cell.setCellStyle(styleLeft);

		int indexBase = 1;
		if (mostrarPeriodo) {
			indexBase++;
		}

		if (mostrarPeriodo) {
			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString("Periodo"));
			cell1.setCellStyle(styleLeft);
		}

		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString(e.getRazon_soc()));
		cell2.setCellStyle(styleBoldCenter);

		int index2 = 0;
		if (mostrarMasInfo) {
			int indexMostra=indexBase;
			if(entidad==WebKeysGlobal.UOMA){
				HSSFCell cellMasInfo = row.createCell(++indexMostra);
				cellMasInfo.setCellValue(new HSSFRichTextString("Obs.Compro"));
				cellMasInfo.setCellStyle(styleBoldCenter);
				index2++;
			}
			HSSFCell cellMasInfo2 = row.createCell(++indexMostra);
			cellMasInfo2.setCellValue(new HSSFRichTextString("OP"));
			cellMasInfo2.setCellStyle(styleBoldCenter);
			index2++;
		}

		HSSFCell cell3 = row.createCell(indexBase + index2 + 1);
		cell3.setCellValue(new HSSFRichTextString("Debe"));
		cell3.setCellStyle(styleBoldCenter);

		HSSFCell cell4 = row.createCell(indexBase + index2 + 2);
		cell4.setCellValue(new HSSFRichTextString("Haber"));
		cell4.setCellStyle(styleBoldCenter);

		HSSFCell cell5 = row.createCell(indexBase + index2 + 3);
		cell5.setCellValue(new HSSFRichTextString("Saldo"));
		cell5.setCellStyle(styleBoldRight);
		i++;
		return i;
	}

	private static class ResultadoAuxiliar {
		private int i;
		private BigDecimal total;

		public void setTotal(BigDecimal total) {
			this.total = total;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public void setI(int i) {
			this.i = i;
		}

		public int getI() {
			return i;
		}
	}
	
	protected static HSSFWorkbook generarReporteAcCo(Date fechaIni, Date fechaFin,
			List<CuentaCorriente> ctas, boolean mostrarPeriodo,
			List<EstadoInicialCuentaCorriente> saldoIni, boolean soloConSaldo,
			boolean mostrarSoloComprobantesConSaldo, boolean mostrarMasInfo, int entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDate = getStyleDate(wb);
		styleDate.setBorderLeft(BorderStyle.THIN);
		styleDate.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleMoneyBorder = getStyleMoney(wb);
		styleMoneyBorder.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle stylePeriodo = getStyleDate(wb);
		stylePeriodo.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleBoldLeft = getStyleBold(wb);
		styleBoldLeft.setBorderLeft(BorderStyle.THIN);
		styleBoldLeft.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldCenter = getStyleBold(wb);
		styleBoldCenter.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldRight = getStyleBold(wb);
		styleBoldRight.setBorderTop(BorderStyle.THIN);
		styleBoldRight.setBorderRight(BorderStyle.THIN);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		StringBuilder headerRight = new StringBuilder();
		headerRight.append("N° de hoja: " + HSSFHeader.page());
		headerRight.append(" de " + HSSFHeader.numPages());
		headerRight.append("\n");
		headerRight.append(DateUtils.format(new Date(), DateUtils.LONG_SEC));
		headerRight.append("\n");
		sheet.getHeader().setRight(headerRight.toString());

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		cellTitulo.setCellValue(new HSSFRichTextString(
				"Cuentas Corrientes - Desde: "
						+ DateUtils.format(fechaIni, DateUtils.SHORT)
						+ " Hasta: "
						+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, mostrarPeriodo ? 5
				: 4));

		int i = 1;
		BigDecimal total = BigDecimal.ZERO;
		i = crearHeaderPpal(sheet, i, styleHeader, mostrarPeriodo,
				mostrarMasInfo, entidad, wb);
		for (CuentaCorriente cta : ctas) {
			i = crearHeader(cta.getEmpresa(), sheet, i, styleBoldLeft,
					styleBoldCenter, styleBoldRight, mostrarPeriodo,
					mostrarMasInfo, entidad);
			ResultadoAuxiliar ra = crearDatosAcCo(cta, sheet, i, styleDate,
					stylePeriodo, styleAll, styleMoney, styleMoneyBorder,
					mostrarPeriodo, saldoIni, fechaIni, fechaFin, soloConSaldo,
					mostrarSoloComprobantesConSaldo, mostrarMasInfo, entidad);
			i = ra.getI();
			total = total.add(ra.getTotal());
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleBoldCenter);
		int cant = 4;
		if (mostrarPeriodo) {
			cant++;
		}
		if (mostrarMasInfo) {
			cant++;
			cant++;
		}
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, cant));

		String totalStr = "Total General";
		if (mostrarSoloComprobantesConSaldo) {
			totalStr = "Total de Comprobantes";
		}

		HSSFRow rowTotal = sheet.createRow(i + 2);
		HSSFCell cellTotalTxt = rowTotal.createCell(mostrarPeriodo ? 4 : 3);
		cellTotalTxt.setCellValue(new HSSFRichTextString(totalStr));
		cellTotalTxt.setCellStyle(styleBoldCenter);

		HSSFCell cellTotal = rowTotal.createCell(mostrarPeriodo ? 5 : 4);
		cellTotal.setCellValue(total.doubleValue());
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		cellTotal.setCellStyle(styleMoneyBold);

		sheet.autoSizeColumn((short) 0);
		if (mostrarPeriodo) {
			sheet.autoSizeColumn((short) 1);
			sheet.setColumnWidth(2, 15360);
		} else {
			sheet.setColumnWidth(1, 15360);
			sheet.autoSizeColumn((short) 2);
		}

		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);

		if (mostrarPeriodo) {
			sheet.autoSizeColumn((short) 5);
		}
		return wb;
	}
	
	private static ResultadoAuxiliar crearDatosAcCo(CuentaCorriente cta,
			HSSFSheet sheet, int i, HSSFCellStyle styleDate,
			HSSFCellStyle stylePeriodo, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoney, HSSFCellStyle styleMoneyBorder,
			boolean mostrarPeriodo,
			List<EstadoInicialCuentaCorriente> saldoIni, Date fechaIni,
			Date fechaFin, boolean soloConSaldo,
			boolean mostrarSoloComprobantesConSaldo, boolean mostrarMasInfo, int entidad) {
		int indexIni = i - 1;
		BigDecimal saldo = BigDecimal.ZERO;
		if (saldoIni == null) {
			saldoIni = new ArrayList<EstadoInicialCuentaCorriente>();
		}

		List<SaldoInicial> saldosIniciales = new ArrayList<SaldoInicial>();
		EstadoInicialCuentaCorriente est = new EstadoInicialCuentaCorriente();
		est.setEmpresa(cta.getEmpresa());

		for (EstadoInicialCuentaCorriente estado : saldoIni) {
			if (estado.getEmpresa().getCuit()
					.equals(cta.getEmpresa().getCuit())
					&&
				estado.getEmpresa().getSucursal().equals(cta.getEmpresa().getSucursal())
				) {
				for (SaldoInicial saldoInicialEmpresa : estado
						.getSaldosIniciales()) {
					saldosIniciales.add(saldoInicialEmpresa);
				}
			}
			Collections.sort(saldosIniciales);
		}

		if (!mostrarSoloComprobantesConSaldo) {
			// -solo calculo saldo ini cuando tengo un saldo inicial forzado con
			// fecha menor al inicio del reporte
			// -si las fechas son iguales no tengo que calcular nada,
			// simplemente muestro la linea de "saldo ini forzado"
			// -si la fecha de saldo ini forzado es mayor a la fecha de inicio
			// del reporte tengo que mostrar todo (y no hace falta calcular un
			// saldo previo)
			boolean calcularSaldoIni = false;
			if (saldosIniciales != null && saldosIniciales.size() > 0) {
				SaldoInicial min = (SaldoInicial) Collections
						.min(saldosIniciales);
				if (DateUtils.compararFechasTruncarEnDia(min.getFecha(),
						fechaIni) < 0) {
					calcularSaldoIni = true;
				}
			}
			if (calcularSaldoIni) {
				// actualizo el saldo a la fechaIni
				saldo = calcularRowSaldoInicial(saldosIniciales, fechaIni,
						cta.getEmpresa(), cta.getInfo(), styleMoney, sheet,
						styleDate, styleAll, i, mostrarPeriodo, mostrarMasInfo, entidad);
				i++;
			}
		}

		if (saldo == null) {
			saldo = BigDecimal.ZERO;
		}

		int indexBase = 1;
		if (mostrarPeriodo) {
			indexBase++;
		}

		int indexSaldosIni = 0;
		if (saldosIniciales != null && saldosIniciales.size() > 0) {
			if (saldosIniciales.get(0).getFecha().compareTo(fechaIni) < 0) {
				indexSaldosIni++;
			}
		}

		Date anterior = null;
		for (Informacion info : cta.getInfo()) {
			if (info.getFecha().compareTo(fechaIni) >= 0 && info.getFecha().compareTo(fechaFin)<=0) {
				if (mostrarSoloComprobantesConSaldo
						&& (!info.isDeuda() || info.getPagadaFecha() != null)) {
					continue;
				}

				if (saldosIniciales != null
						&& saldosIniciales.size() > indexSaldosIni) {					
					for (int j = indexSaldosIni; j < saldosIniciales.size(); j++) {
						Date fechaSI = saldosIniciales.get(j)
								.getFecha();
						if (DateUtils.compararFechasTruncarEnDia(fechaSI,
								info.getFecha()) <= 0
								&& (anterior == null || DateUtils
										.compararFechasTruncarEnDia(fechaSI,
												anterior) > 0)) {

							saldo = saldosIniciales.get(indexSaldosIni)
									.getImporte();
							getRowSaldoInicial(
									saldosIniciales.get(indexSaldosIni)
											.getFecha(), saldo,
									styleMoneyBorder, sheet, styleDate,
									styleAll, i, mostrarPeriodo,
//									mostrarMasInfo, "Saldo Inicial Forzado", entidad);
									mostrarMasInfo, "Saldo Inicial", entidad);
							i++;
							indexSaldosIni++;
						}
					}
				}

				HSSFRow row = sheet.createRow(i);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue(info.getFecha());
				cell.setCellStyle(styleDate);

				anterior = info.getFecha();

				if (mostrarPeriodo) {
					HSSFCell cell1 = row.createCell(1);
					if (info.getPeriodo() != null) {
						cell1.setCellValue(info.getPeriodo());
					} else {
						cell1.setCellValue(new HSSFRichTextString(" "));
					}
					cell1.setCellStyle(stylePeriodo);
				}

				HSSFCell cell2 = row.createCell(indexBase);
				cell2.setCellValue(new HSSFRichTextString(info.getDescripcion()));
				cell2.setCellStyle(styleAll);

				BigDecimal importeDebe = BigDecimal.ZERO;
				BigDecimal importeHaber = BigDecimal.ZERO;
				if (info.getDebitoCredito().equals("D")) {
					saldo = saldo.subtract(info.getImporte());
					importeHaber = info.getImporte();
				} else {
					saldo = saldo.add(info.getImporte());
					importeDebe = info.getImporte();
				}

				int index2 = 0;
				if (mostrarMasInfo) {
					int indexMostro=indexBase;
					if(entidad==WebKeysGlobal.UOMA){
						HSSFCell cellMasInfo = row.createCell(++indexMostro);
						cellMasInfo.setCellValue(new HSSFRichTextString(info.getObservacionCompro()));
						cellMasInfo.setCellStyle(styleAll);
						index2++;
					}
					HSSFCell cellMasInfo2 = row.createCell(++indexMostro);
					cellMasInfo2.setCellValue(info.getIdPago());
					cellMasInfo2.setCellStyle(styleAll);
					index2++;
				}

				HSSFCell cell3 = row.createCell(indexBase + index2 + 1);
				if(importeDebe.compareTo(BigDecimal.ZERO)>0){
					cell3.setCellValue(importeDebe.doubleValue());
				}else{
					cell3.setCellValue(new HSSFRichTextString(""));					
				}
				cell3.setCellStyle(styleMoney);

				HSSFCell cell4 = row.createCell(indexBase + index2 + 2);
				if(importeHaber.compareTo(BigDecimal.ZERO)!=0){
					cell4.setCellValue(importeHaber.doubleValue());
				}else{
					cell4.setCellValue(new HSSFRichTextString(""));					
				}
				cell4.setCellStyle(styleMoney);

				HSSFCell cell5 = row.createCell(indexBase + index2 + 3);
				cell5.setCellValue(saldo.doubleValue());
				cell5.setCellStyle(styleMoneyBorder);

				i++;
			}
		}

		// si quedaban saldos iniciales
		if (saldosIniciales != null && saldosIniciales.size() > indexSaldosIni) {
			for (int index = indexSaldosIni; index < saldosIniciales.size(); index++) {
				Date fechaSI = saldosIniciales.get(index).getFecha();
				if (DateUtils.compararFechasTruncarEnDia(fechaSI, fechaFin) <= 0) {
					saldo = saldosIniciales.get(index).getImporte();
					getRowSaldoInicial(saldosIniciales.get(index).getFecha(),
							saldo, styleMoneyBorder, sheet, styleDate,
							styleAll, i, mostrarPeriodo, mostrarMasInfo,
							"Saldo Inicial Forzado", entidad);
					i++;
				}
			}
		}

		if (mostrarSoloComprobantesConSaldo) {
			soloConSaldo = true;
		}

		if (soloConSaldo && saldo.compareTo(BigDecimal.ZERO) == 0) {
			for (int j = indexIni; j < i; j++) {
				sheet.removeRow(sheet.getRow(j));
			}
			i = indexIni;
		}

		ResultadoAuxiliar ra = new ResultadoAuxiliar();
		ra.setI(i);
		ra.setTotal(saldo);
		return ra;
	}


}

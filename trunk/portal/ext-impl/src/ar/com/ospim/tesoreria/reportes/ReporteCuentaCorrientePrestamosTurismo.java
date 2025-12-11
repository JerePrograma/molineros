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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import com.liferay.ibm.icu.text.DecimalFormat;
import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.SystemException;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.CuentaCorriente.Informacion;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboPrestamo;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente.SaldoInicial;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;

public class ReporteCuentaCorrientePrestamosTurismo extends ReporteXLS {

	private static int crearHeaderPpal(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, boolean mostrarPeriodo,
			boolean mostrarMasInfo, int entidad,  HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Numero - Fecha"));
		cell.setCellStyle(styleHeader);

		int indexBase = 1;
		if (mostrarPeriodo) {
			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString("Periodo"));
			cell1.setCellStyle(styleHeader);
			indexBase++;
		}

		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString("Afiliado"));
		cell2.setCellStyle(styleHeader);
		indexBase++;

		HSSFCell cell3 = row.createCell(indexBase);
		cell3.setCellValue(new HSSFRichTextString("Seccional"));
		cell3.setCellStyle(styleHeader);
		indexBase++;

		HSSFCell cell4 = row.createCell(indexBase);
		cell4.setCellValue(new HSSFRichTextString("Hotel"));
		cell4.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell5 = row.createCell(indexBase);
		cell5.setCellValue(new HSSFRichTextString("Cuotas"));
		cell5.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell6 = row.createCell(indexBase);
		cell6.setCellValue(new HSSFRichTextString("Otorgado"));
		cell6.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell7 = row.createCell(indexBase);
		cell7.setCellValue(new HSSFRichTextString("Pagado"));
		cell7.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell8 = row.createCell(indexBase);
		cell8.setCellValue(new HSSFRichTextString("Saldo"));
		cell8.setCellStyle(styleHeader);
		indexBase++;
		
		//wb.setRepeatingRowsAndColumns(0, 0, indexBase + index2 + 3, i, i);

		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		
		i++;
		return i;
	}


	private static int crearHeaderPrestamo(Prestamo pre, HSSFSheet sheet, int i,
			HSSFCellStyle styleLeft, HSSFCellStyle styleBoldCenter,
			HSSFCellStyle styleBoldRight, boolean mostrarPeriodo,
			boolean mostrarMasInfo, int entidad) {
				
		i++;
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell = row.createCell(0);
		
		// Col numero y Fecha
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		cell.setCellValue(new HSSFRichTextString((String.valueOf(pre.getId())) + "-" +
				((pre.getAcuerdoFecha()!=null) ? sdf.format(pre.getAcuerdoFecha()) : "")));
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

		// Col Apellido
		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString(
				(String.format("%-65s",pre.getAfiliado().getApellido()+" (" + pre.getAfiliado().getCuil_titular()+")"  ))));
		cell2.setCellStyle(styleBoldCenter);
		indexBase++;

		// Col Seccional
		HSSFCell cell3 = row.createCell(indexBase);
		cell3.setCellValue(new HSSFRichTextString(
				(String.format("%-50s",pre.getAfiliado().getSeccional()!=null && 
						pre.getAfiliado().getSeccional().getDescripcion()!=null?pre.getAfiliado().getSeccional().getDescripcion():
					""))));
		cell3.setCellStyle(styleBoldCenter);
		indexBase++;
		
		// Hotel
		HSSFCell cell4 = row.createCell(indexBase);
		cell4.setCellValue(new HSSFRichTextString((pre.getDescripcionHotel())));
		cell4.setCellStyle(styleBoldCenter);
		indexBase++;

		// Cuotas
		HSSFCell cell5 = row.createCell(indexBase);
		cell5.setCellValue(new HSSFRichTextString(String.valueOf(pre.getCantidadCuotas())));
		cell5.setCellStyle(styleBoldCenter);
		indexBase++;	
		
		HSSFCell cell6 = row.createCell(indexBase);
		cell6.setCellValue(new HSSFRichTextString("Otorgado"));
		cell6.setCellStyle(styleBoldCenter);
		indexBase++;

		HSSFCell cell7 = row.createCell(indexBase);
		cell7.setCellValue(new HSSFRichTextString("Pagado"));
		cell7.setCellStyle(styleBoldCenter);
		indexBase++;

		HSSFCell cell8 = row.createCell(indexBase);
		cell8.setCellValue(new HSSFRichTextString("Saldo"));
		cell8.setCellStyle(styleBoldRight);
		indexBase++;
		
		i++;
		return i;
	}

	private static class ResultadoAuxiliar {
		private int i;
		private BigDecimal sumSaldo;
		private BigDecimal sumDebe;
		private BigDecimal sumHaber;

		public void setTotalSaldo(BigDecimal total) {
			this.sumSaldo = total;
		}

		public BigDecimal getTotalSaldo() {
			return sumSaldo;
		}

		public void setTotalDebe(BigDecimal total) {
			this.sumDebe = total;
		}

		public BigDecimal getTotalDebe() {
			return sumDebe;
		}

		public void setTotalHaber(BigDecimal total) {
			this.sumHaber = total;
		}

		public BigDecimal getTotalHaber() {
			return sumHaber;
		}

		
		public void setI(int i) {
			this.i = i;
		}

		public int getI() {
			return i;
		}
	}
	
	protected static HSSFWorkbook generarReportePreTur(
			Date fechaIni, Date fechaFin,
			List<Prestamo> prestamos, 
			boolean soloConSaldo,
			int entidad, 
			boolean soloReporteConsolidado,Date fechaCCHasta) {
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

		HSSFCellStyle styleAlignRight = getStyleBoldAligned(wb, HorizontalAlignment.RIGHT);
		
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
				"Beneficios Turismo - Desde: "
 						+ DateUtils.format(fechaIni, DateUtils.SHORT)
						+ " Hasta: "
						+ DateUtils.format(fechaFin, DateUtils.SHORT)
				        + "  - Corte Cuenta Corriente al: "	
				        + DateUtils.format(fechaCCHasta, DateUtils.SHORT)
 				));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

		int i = 1;
		BigDecimal totalSaldo = BigDecimal.ZERO;
		BigDecimal totalDebe = BigDecimal.ZERO;
		BigDecimal totalHaber = BigDecimal.ZERO;
		i = crearHeaderPpal(sheet, i, styleHeader, false,
				false, entidad, wb);
				 
		for (Prestamo pre : prestamos) {
			
			double importeDebe = 0;
			double importeHaber = 0;
			double importeSaldo = 0;
			importeDebe = ((pre.getTotal()!=null) ? pre.getTotal() : 0);
			importeHaber = ((pre.getPagado()!=null) ? pre.getPagado() : 0);
			importeSaldo = importeDebe - importeHaber;

			if ((!soloConSaldo) || (importeSaldo != 0)) {
							
				i = crearHeaderPrestamo(pre, sheet, i, styleBoldLeft,
						styleBoldCenter, styleBoldRight, false,
						false, entidad);
				
				ResultadoAuxiliar ra = crearDatosPrestamo(pre, sheet, i, styleDate,
						stylePeriodo, styleAll, styleMoney, styleMoneyBorder,
						styleAlignRight,
						false, soloConSaldo,
						false, false, entidad, 
						soloReporteConsolidado,fechaCCHasta);
				i = ra.getI();
				totalSaldo = totalSaldo.add(ra.getTotalSaldo());
				totalDebe = totalDebe.add(ra.getTotalDebe());
				totalHaber = totalHaber.add(ra.getTotalHaber());
			}			
		}
		
		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleBoldCenter);
		int cant = 4;
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, cant));

		HSSFCellStyle styleOnlyBoldCenter = getStyleBold(wb);
		
		HSSFRow rowTotal = sheet.createRow(i + 1);
		HSSFCell cellTotalTxt = rowTotal.createCell(5);
		cellTotalTxt.setCellValue(new HSSFRichTextString("Otorgado"));
		cellTotalTxt.setCellStyle(styleOnlyBoldCenter);		
		
		cellTotalTxt = rowTotal.createCell(6);
		cellTotalTxt.setCellValue(new HSSFRichTextString("Pagado"));
		cellTotalTxt.setCellStyle(styleOnlyBoldCenter);

		cellTotalTxt = rowTotal.createCell(7);
		cellTotalTxt.setCellValue(new HSSFRichTextString("Saldo"));
		cellTotalTxt.setCellStyle(styleOnlyBoldCenter);
		
		String totalStr = "Total General";

		rowTotal = sheet.createRow(i + 2);
		cellTotalTxt = rowTotal.createCell(3);
		cellTotalTxt.setCellValue(new HSSFRichTextString(totalStr));
		cellTotalTxt.setCellStyle(styleBoldCenter);

		HSSFCell cellTotal = rowTotal.createCell(5);
		cellTotal.setCellValue(totalDebe.doubleValue());
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		cellTotal.setCellStyle(styleMoneyBold);

		cellTotal = rowTotal.createCell(6);
		cellTotal.setCellValue(totalHaber.doubleValue());
		cellTotal.setCellStyle(styleMoneyBold);

		cellTotal = rowTotal.createCell(7);
		cellTotal.setCellValue(totalSaldo.doubleValue());
		cellTotal.setCellStyle(styleMoneyBold);

		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}

		return wb;
	}

	private static ResultadoAuxiliar crearDatosPrestamo(Prestamo pre,
			HSSFSheet sheet, int i, HSSFCellStyle styleDate,
			HSSFCellStyle stylePeriodo, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoney, HSSFCellStyle styleMoneyBorder,
			HSSFCellStyle styleAlignRight,
			boolean mostrarPeriodo,
			boolean soloConSaldo,
			boolean mostrarSoloComprobantesConSaldo, 
			boolean mostrarMasInfo, int entidad,
			boolean soloReporteConsolidado,
			Date fechaCCHasta) {
		int indexBase = 1;	
		
		double importeDebe = 0;
		double importeHaber = 0;
		double importeSaldo = 0;
		importeDebe = ((pre.getTotal()!=null) ? pre.getTotal() : 0);
		importeHaber = ((pre.getPagado()!=null) ? pre.getPagado() : 0);
		importeSaldo = importeDebe - importeHaber;

		ResultadoAuxiliar ra = new ResultadoAuxiliar();
					
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell2 = row.createCell(indexBase);
		if (soloReporteConsolidado)
			cell2.setCellValue(new HSSFRichTextString("Beneficio Otorgado"));
		else			
			cell2.setCellValue(new HSSFRichTextString("Estado Beneficio"));
		cell2.setCellStyle(styleAll);		
				
		indexBase += 4;
		HSSFCell cellDebe = row.createCell(indexBase);
		cellDebe.setCellValue(importeDebe);
		cellDebe.setCellStyle(styleMoney);

		indexBase++;
		HSSFCell cellHaber = row.createCell(indexBase);
		cellHaber.setCellValue(importeHaber);
		cellHaber.setCellStyle(styleMoney);

		indexBase++;
		HSSFCell cellSaldo = row.createCell(indexBase);
		cellSaldo.setCellValue(importeSaldo);
		cellSaldo.setCellStyle(styleMoneyBorder);
				
		if (!soloReporteConsolidado) {
			
			Double sdo = ((pre.getTotal()!=null) ? pre.getTotal() : 0);
			SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
			List<Recibo> recibos = null;
			try {
				recibos =HotelesServiceUtil.getPrestamoPagos(pre.getId(),entidad,fechaCCHasta);
				
				if(!recibos.isEmpty()) i++;
				
				for (int iRec = 0; iRec < recibos.size(); iRec++) {	    
					Recibo liq = (Recibo) recibos.get(iRec);					
					for(ReciboPrestamo rp : liq.getReciboPrestamos()){
			
						i++;
						HSSFRow rowDet = sheet.createRow(i);

						HSSFCell cellRecibo = rowDet.createCell(0);
						cellRecibo.setCellValue(new HSSFRichTextString("Recibo:"));
						cellRecibo.setCellStyle(styleAlignRight);						
						
						HSSFCell cellDetalle = rowDet.createCell(1);
						cellDetalle.setCellValue(sdf.format(rp.getPrestamo().getAcuerdoFecha()));

						cellDetalle = rowDet.createCell(2);
						cellDetalle.setCellValue(String.valueOf(liq.getNumero()));

						double importeRecibo = 0;
						importeRecibo = (rp.getPrestamo()!=null ? rp.getPrestamo().getMonto() : 0);
						HSSFCell cellMonto = rowDet.createCell(6);
						cellMonto.setCellValue(importeRecibo);
						cellMonto.setCellStyle(styleMoney);
						
						sdo -= importeRecibo;
						HSSFCell cellSdo = rowDet.createCell(7);
						cellSdo.setCellValue(sdo);
						cellSdo.setCellStyle(styleMoney);
					}
				}

			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
		i++;
		

		ra.setI(i);
		
		BigDecimal auxBD = new BigDecimal(importeSaldo);
		ra.setTotalSaldo(auxBD);
		auxBD = new BigDecimal(importeDebe);
		ra.setTotalDebe(auxBD);
		auxBD = new BigDecimal(importeHaber);
		ra.setTotalHaber(auxBD);
		
		return ra;
	}
}

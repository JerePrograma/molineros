package ar.com.ospim.tesoreria.reportes.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;
import ar.com.ospim.tesoreria.beans.EgresoLiquidacion;
import ar.com.ospim.tesoreria.service.ReporteDerivacionTercerizadorasServiceImpl;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceImpl;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteDerivacionTercerizadorasExcel extends ReporteXLS {

	private static Log _log = LogFactoryUtil
			.getLog(ReporteDerivacionTercerizadorasExcel.class);

	private static BigDecimal totalDebitadoOmintLiquidaciones=BigDecimal.ZERO;
	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		String fechaLiq="";
		
		if(req!=null) {
		  fechaLiq = ParamUtil.getString(req, "fechaLiq");
		}else {
			try {
				List<Date>f =TraeListasServiceUtil.getFechasLiquidacionHistoricaTercerizadoras();
				fechaLiq=format.format(f.get(0));
			} catch (SystemException e) {
				
			}
		}
		
		try {
			Date fechaFin = format.parse(fechaLiq);
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaFin);
			cal.set(cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			cal.add(Calendar.MONTH, -1);
            Date fechaIni = cal.getTime();

			ReporteDerivacionTercerizadorasServiceImpl repo = new ReporteDerivacionTercerizadorasServiceImpl();
			
			List<EgresoLiquidacion> libroConcepto = repo
					.getLiquidacionesConceptoAgrupado(fechaFin);
			List<EgresoLiquidacion> libroPeriodo = repo.getLiquidacionesPeriodo(fechaFin);
			List<EgresoLiquidacion> libroCuiles = repo.getLiquidacionesCuilPorTercerizadora(fechaFin);
			
			LiquidaDesreguladosServiceImpl liqui = new LiquidaDesreguladosServiceImpl();
			List<ConsolidadoLiquidaciones>libroDerivados = liqui.getConsolidadoLiquidaciones(null, fechaIni, fechaFin);

			HSSFWorkbook reporte = new HSSFWorkbook();
			
			reporte = generarReporteConceptoAgrupado(reporte, fechaIni,
					fechaFin, libroConcepto);
			reporte = generarReportePorPeriodo(reporte, fechaIni,
					fechaFin, libroPeriodo);
			reporte = generarReportePorCuiles(reporte, fechaIni,
					fechaFin, libroCuiles);
				
			if(libroDerivados.size()>0)
			    reporte = generarReporteDerivacionesDesreguladas(reporte,fechaIni,fechaFin,libroDerivados);
			
			return reporte;
		} catch (Exception e) {
			_log.error("Error al generar listado estado comprobantes", e);
			return null;
		}
	}
	
	private static HSSFWorkbook generarReporteConceptoAgrupado(HSSFWorkbook wb,
			Date fechaIni, Date fechaFin, List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Aportes por Concepto");
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
				"Concepto de aportes por fecha de Transferencia desde el: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));

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
	
	private static HSSFWorkbook generarReportePorPeriodo(HSSFWorkbook wb,
			Date fechaIni, Date fechaFin, List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Aportes por Período");
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
				"Cantidad de aportes por período recibidos para liq: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		createHeaderPorPeriodo(wb, sheet, styleHeader);
		
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

	private static void createHeaderPorPeriodo(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(2);

		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Período"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(1);
		cell3.setCellValue(new HSSFRichTextString("Importe"));
		cell3.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
	}
	
	private static HSSFWorkbook generarReportePorCuiles(HSSFWorkbook wb,
			Date fechaIni, Date fechaFin, List<EgresoLiquidacion> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);
		
		HSSFSheet sheet = wb.createSheet("Cuiles por Tercerizadora");
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
				"Cantidad de cuiles por tercerizadora en liq. De: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		createHeaderPorCuiles(wb, sheet, styleHeader);
		
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
			cell3.setCellValue(importe.intValue());
			cell3.setCellStyle(styleMoney);

			i++;
		}
		
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

	private static void createHeaderPorCuiles(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(2);

		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(1);
		cell3.setCellValue(new HSSFRichTextString("Cant.Cuiles"));
		cell3.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
	}
	
	
	private static HSSFWorkbook generarReporteDerivacionesDesreguladas(HSSFWorkbook wb,
			Date fechaIni, Date fechaFin, List<ConsolidadoLiquidaciones> libro) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
		List<ConsolidadoLiquidaciones>anterior = new ArrayList<ConsolidadoLiquidaciones>();
		List<ConsolidadoLiquidaciones>actual = new ArrayList<ConsolidadoLiquidaciones>();
		for(ConsolidadoLiquidaciones c:libro){
		   if(sdf.format(c.getFechaLiq()).equalsIgnoreCase(sdf.format(fechaIni))){
			   anterior= updateLista(anterior,c);
//			  anterior.add(c); 
		   }else if(sdf.format(c.getFechaLiq()).equalsIgnoreCase(sdf.format(fechaFin))){
			   actual=updateLista(actual,c); 
//			  actual.add(c); 
		   }
		}
		
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Derivaciones Desreguladas");
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
				"Derivaciones Desreguladas: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		int iRow = 1;
		BigDecimal total = BigDecimal.ZERO;
		if(anterior.size()>0){
		   iRow=createHeaderDerivacionesDesreguladas(wb, sheet, styleHeader,anterior,iRow);
		   iRow=createRowDerivacionesDesreguladas(wb, sheet, anterior,iRow);
		
		   iRow=iRow+2;
		}
		
		if(actual.size()>0){
		   iRow=createHeaderDerivacionesDesreguladas(wb, sheet, styleHeader,actual,iRow);
		   iRow=createRowDerivacionesDesreguladas(wb, sheet, actual,iRow);
		}
		if(anterior.size()>0){
		   iRow=createVariacionDerivacionesDesreguladas(wb, sheet, actual,anterior,iRow);
		}   
		
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
		sheet.autoSizeColumn((short) 11);
		sheet.autoSizeColumn((short) 12);
		sheet.autoSizeColumn((short) 13);
		sheet.autoSizeColumn((short) 14);
		sheet.autoSizeColumn((short) 15);
		sheet.autoSizeColumn((short) 16);
		sheet.autoSizeColumn((short) 17);
		sheet.autoSizeColumn((short) 18);
		sheet.autoSizeColumn((short) 19);
		sheet.autoSizeColumn((short) 20);
		sheet.autoSizeColumn((short) 21);
		sheet.autoSizeColumn((short) 22);
		sheet.autoSizeColumn((short) 23);
		sheet.autoSizeColumn((short) 24);
		sheet.autoSizeColumn((short) 25);
		sheet.autoSizeColumn((short) 26);
		sheet.autoSizeColumn((short) 27);
		sheet.autoSizeColumn((short) 28);
		sheet.autoSizeColumn((short) 29);
		sheet.autoSizeColumn((short) 30);
		return wb;
	}

	private static int createHeaderDerivacionesDesreguladas(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader,List<ConsolidadoLiquidaciones>lista,int iRow) {
		HSSFRow row = sheet.createRow(iRow);
 
		SimpleDateFormat sdf=new SimpleDateFormat("MM/yyyy");
        
        HSSFCell cell1 = row.createCell(0);
		cell1.setCellValue(new HSSFRichTextString(lista.get(0).getFechaLiqAsString()));
		cell1.setCellStyle(styleHeader);
		
		Calendar c = Calendar.getInstance();
		c.setTime(lista.get(0).getFechaLiq());
		c.add(Calendar.MONTH, -2);
		iRow++;
        row=sheet.createRow(iRow);
        HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("DDJJ "+sdf.format(c.getTime())));
		cell2.setCellStyle(styleHeader);
		
		int icel=1;
        for(ConsolidadoLiquidaciones cl:lista){
        	HSSFCell cell3 = row.createCell(icel);
        	cell3.setCellValue(new HSSFRichTextString(cl.getTercerizadora()));
    		cell3.setCellStyle(styleHeader);
    		icel++;
        }
        
        HSSFCell cell4 = row.createCell(icel);
		cell4.setCellValue(new HSSFRichTextString("TOTALES"));
		cell4.setCellStyle(styleHeader);
		
        iRow++;
		
		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
        
    	for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
        
		return iRow;
	}
	
	
	private static int createRowDerivacionesDesreguladas(HSSFWorkbook wb,
			HSSFSheet sheet, List<ConsolidadoLiquidaciones>lista,int iRow) {
		
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFRow row = sheet.createRow(iRow);
		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("IMPORTE TOTAL"));
		cell2.setCellStyle(styleAll);
		int icel=1;
		Double totalRow=0D;
		for (ConsolidadoLiquidaciones cl : lista) {
			HSSFCell cell3 = row.createCell(icel);
    		cell3.setCellValue(cl.getImporteTotal().doubleValue());
    		cell3.setCellStyle(styleMoney);
    		icel++;
    		totalRow+=cl.getImporteTotal().doubleValue();
		}
		HSSFCell cell4 = row.createCell(icel);
		cell4.setCellValue(totalRow);
		cell4.setCellStyle(styleMoneyBold);
		iRow++;
		
		row = sheet.createRow(iRow);
		HSSFCell cell5 = row.createCell(0);
		cell5.setCellValue(new HSSFRichTextString("IMPORTE NETO"));
		cell5.setCellStyle(styleAll);
		icel=1;
		totalRow=0D;
		for (ConsolidadoLiquidaciones cl : lista) {
			HSSFCell cell6 = row.createCell(icel);
    		cell6.setCellValue(cl.getImporteDerivar().doubleValue());
    		cell6.setCellStyle(styleMoney);
    		icel++;
    		totalRow+=cl.getImporteDerivar().doubleValue();
		}
		HSSFCell cell7 = row.createCell(icel);
		cell7.setCellValue(totalRow);
		cell7.setCellStyle(styleMoneyBold);
		iRow++;
		
		row = sheet.createRow(iRow);
		HSSFCell cell8 = row.createCell(0);
		cell8.setCellValue(new HSSFRichTextString("COMISION"));
		cell8.setCellStyle(styleAll);
		icel=1;
		totalRow=0D;
		for (ConsolidadoLiquidaciones cl : lista) {
			HSSFCell cell9 = row.createCell(icel);
    		cell9.setCellValue(cl.getImporteTotal().doubleValue()-cl.getImporteDerivar().doubleValue());
    		cell9.setCellStyle(styleMoney);
    		icel++;
    		totalRow+=cl.getImporteTotal().doubleValue()-cl.getImporteDerivar().doubleValue();
		}
		HSSFCell cell10 = row.createCell(icel);
		cell10.setCellValue(totalRow);
		cell10.setCellStyle(styleMoneyBold);
		iRow++;

		return iRow;
	}

	
	private static int createVariacionDerivacionesDesreguladas(HSSFWorkbook wb,
			HSSFSheet sheet, List<ConsolidadoLiquidaciones>actual, List<ConsolidadoLiquidaciones>anterior,int iRow) {
		
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFRow row = sheet.createRow(iRow);
		HSSFCell cell2 = row.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("VARIACION MES ANTERIOR"));
		cell2.setCellStyle(styleAll);
		int icel=1;
		Double totalActual=0D;
		
		for (ConsolidadoLiquidaciones cl : actual) {
			Double comisionActual=cl.getImporteTotal().doubleValue()-cl.getImporteDerivar().doubleValue();
			Double comisionAnterior=0D;
			totalActual+=comisionActual;
			for(ConsolidadoLiquidaciones cla:anterior){
				if(cl.getIdTercerizadora().equalsIgnoreCase(cla.getIdTercerizadora())){
					comisionAnterior =cla.getImporteTotal().doubleValue()-cla.getImporteDerivar().doubleValue();
					break;
				}
			}
			
			HSSFCell cell3 = row.createCell(icel);
			if(comisionAnterior!=0D){
    		   cell3.setCellValue((comisionActual-comisionAnterior)*100/comisionAnterior);
			}else{
//				cell3.setCellValue(100);	
			}
    		cell3.setCellStyle(styleMoney);
    		icel++;
		}
		
		Double totalAnterior =0D;
		for(ConsolidadoLiquidaciones cla:anterior){
			totalAnterior +=cla.getImporteTotal().doubleValue()-cla.getImporteDerivar().doubleValue(); 	
		}

		if(totalAnterior!=0D){
			HSSFCell cell4 = row.createCell(icel);
			cell4.setCellValue((totalActual-totalAnterior)*100/totalAnterior);
			cell4.setCellStyle(styleMoneyBold);
		}
		
		iRow++;
		return iRow;
	}
	
	private static List<ConsolidadoLiquidaciones> updateLista( List<ConsolidadoLiquidaciones>lista,ConsolidadoLiquidaciones consolidado){
		List<ConsolidadoLiquidaciones> ret= new ArrayList<ConsolidadoLiquidaciones>();
		if(lista.size()!=0){
		   Boolean encontro=false;	
		   for(ConsolidadoLiquidaciones co:lista){
			 if(co.getIdTercerizadora().equalsIgnoreCase(consolidado.getIdTercerizadora())){
				consolidado.setImporteTotal(consolidado.getImporteTotal().add(co.getImporteTotal()));
				consolidado.setImporteDerivar(consolidado.getImporteDerivar().add(co.getImporteDerivar()));
				if(consolidado.getFechaLiq().before(co.getFechaLiq())){
					consolidado.setFechaLiq(co.getFechaLiq());
				}
				ret.add(consolidado);
				encontro=true;
			 }else{
				ret.add(co);
			 }
		   }
		   if(!encontro) ret.add(consolidado);
		   
		}else{
			ret.add(consolidado);
		}
		return ret;
	}

}

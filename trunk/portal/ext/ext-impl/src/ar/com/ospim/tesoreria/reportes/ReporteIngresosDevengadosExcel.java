package ar.com.ospim.tesoreria.reportes;

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
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.ReporteIngresosDevengadosBean;
import ar.com.ospim.tesoreria.service.ReportesServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteIngresosDevengadosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteIngresosDevengadosExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
       try{
						
	 	  List<ReporteIngresosDevengadosBean>reporte = (List<ReporteIngresosDevengadosBean>) ReportesServiceUtil.getIngresosDevengados() ;
			
			
		  return generarReporte(reporte);
			
			
		} catch (Exception e) {
			_log.error("Error al generar Reporte Ingresos Devengados", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(List<ReporteIngresosDevengadosBean> reporte) throws SystemException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);
	
		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
	
		HSSFCellStyle styleAllTop = getStyleAll(wb);
	
		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
	
		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
	
		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
	
		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
	
		HSSFCellStyle styleMoneyRightBold = getStyleMoneyBold(wb);
	
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);
		
		HSSFCellStyle styleMoneyRightGris= getStyleMoneyFondoGris(wb);
		
		HSSFCellStyle styleAllFondoGris = getStyleAllFondoGris(wb);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		i++;
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
				
		for (ReporteIngresosDevengadosBean repo : reporte) {
			
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		}
		
		i++;
		
		HSSFRow row = sheet.createRow(i);
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell0 = rowTitulo.createCell(0);
		Integer rowIni=3;
		cell0.setCellValue(new HSSFRichTextString("Total"));
		cell0.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell1 = rowTitulo.createCell(1);
		cell1.setCellFormula("SUM(B"+Integer.toString(rowIni)  +":B"+ Integer.toString(i) +")");
		cell1.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell2 = rowTitulo.createCell(2);
		cell2.setCellFormula("SUM(C"+Integer.toString(rowIni)  +":"+ "C"+ Integer.toString(i) +")");
		cell2.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell3 = rowTitulo.createCell(3);
		cell3.setCellFormula("SUM(D"+Integer.toString(rowIni)  +":D"+ Integer.toString(i) +")");
		cell3.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell4 = rowTitulo.createCell(4);
		cell4.setCellFormula("SUM(E"+Integer.toString(rowIni)  +":E"+ Integer.toString(i) +")");
		cell4.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell5 = rowTitulo.createCell(5);
		cell5.setCellFormula("SUM(F"+Integer.toString(rowIni)  +":F"+ Integer.toString(i) +")");
		cell5.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell6 = rowTitulo.createCell(6);
		cell6.setCellFormula("SUM(G"+Integer.toString(rowIni)  +":G"+ Integer.toString(i) +")");
		cell6.setCellStyle(styleMoneyRightGris);
		
		HSSFCell cell7 = rowTitulo.createCell(7);
		cell7.setCellFormula("SUM(H"+Integer.toString(rowIni)  +":H"+ Integer.toString(i) +")");
		cell7.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell8 = rowTitulo.createCell(8);
		cell8.setCellFormula("SUM(I"+Integer.toString(rowIni)  +":I"+ Integer.toString(i) +")");
		cell8.setCellStyle(styleMoneyRightGris);
		
		HSSFCell cell9 = rowTitulo.createCell(9);
		cell9.setCellFormula("SUM(J"+Integer.toString(rowIni)  +":J"+ Integer.toString(i) +")");
		cell9.setCellStyle(styleAllFondoGris);
		
		HSSFCell cell10 = rowTitulo.createCell(10);
		cell10.setCellFormula("SUM(K"+Integer.toString(rowIni)  +":K"+ Integer.toString(i) +")");
		cell10.setCellStyle(styleMoneyRightGris);
		
		
		HSSFCell cell11 = rowTitulo.createCell(11);
		cell11.setCellFormula("SUM(L"+Integer.toString(rowIni)  +":L"+ Integer.toString(i) +")");
		cell11.setCellStyle(styleMoneyRightGris);
		
		
		HSSFCell cell12 = rowTitulo.createCell(12);
		cell12.setCellFormula("SUM(M"+Integer.toString(rowIni)  +":M"+ Integer.toString(i) +")");
		cell12.setCellStyle(styleMoneyRightGris);
		
		HSSFCell cell13 = rowTitulo.createCell(13);
		cell13.setCellFormula("SUM(N"+Integer.toString(rowIni)  +":N"+ Integer.toString(i) +")");
		cell13.setCellStyle(styleMoneyRightGris);
		
		i++;
		
		
//		sheet.autoSizeColumn((short) 0);
		sheet.setColumnWidth(0, 6200);
		
		for(int x=1;x<58;x++){
//			sheet.autoSizeColumn((short) x);
			sheet.setColumnWidth(x, 3300);
		}
		
		createFooter( wb,sheet, i);
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ReporteIngresosDevengadosBean repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0 = row.createCell(0);
		if(repo.getTercerizadora()  !=null){
		  cell0.setCellValue(new HSSFRichTextString(repo.getTercerizadora()));
		  cell0.setCellStyle(styleAll);
	    }else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getSinRemuneracion()  !=null){
		  cell1.setCellValue(repo.getSinRemuneracion());
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell11 = row.createCell(2);
		if(repo.getEfectoresCantidad()  !=null){
		  cell11.setCellValue(repo.getEfectoresCantidad());
		  cell11.setCellStyle(styleAll);
	    }else{
		  cell11.setCellValue(new HSSFRichTextString(""));
		  cell11.setCellStyle(styleAll);
		}
		
		HSSFCell cell12 = row.createCell(3);
		if(repo.getServicioDomesticoCantidad() !=null){
		  cell12.setCellValue(repo.getServicioDomesticoCantidad());
		  cell12.setCellStyle(styleAll);
	    }else{
		  cell12.setCellValue(new HSSFRichTextString(""));
		  cell12.setCellStyle(styleAll);
		}
		
		HSSFCell cell13 = row.createCell(4);
		if(repo.getMonotributistasCantidad()  !=null){
		  cell13.setCellValue(repo.getMonotributistasCantidad());
		  cell13.setCellStyle(styleAll);
	    }else{
		  cell13.setCellValue(new HSSFRichTextString(""));
		  cell13.setCellStyle(styleAll);
		}
		
		HSSFCell cell2 = row.createCell(5);
		if(repo.getCantidadRemuneracionPeriodo() !=null){
		  cell2.setCellValue(repo.getCantidadRemuneracionPeriodo());
		  cell2.setCellStyle(styleAll);
	    }else{
		  cell2.setCellValue(new HSSFRichTextString(""));
		  cell2.setCellStyle(styleAll);
		}
		
		HSSFCell cell3 = row.createCell(6);
		if(repo.getTotalRemenueracionPeriodo() !=null){
		  cell3.setCellValue(repo.getTotalRemenueracionPeriodo());
		  cell3.setCellStyle(styleMoneyRight);
	    }else{
		  cell3.setCellValue(new HSSFRichTextString(""));
		  cell3.setCellStyle(styleAll);
		}
		
		HSSFCell cell4 = row.createCell(7);
		if(repo.getCantidadRemuneracionPeriodoAnterior() !=null){
		  cell4.setCellValue(repo.getCantidadRemuneracionPeriodoAnterior());
		  cell4.setCellStyle(styleAll);
	    }else{
		  cell4.setCellValue(new HSSFRichTextString(""));
		  cell4.setCellStyle(styleAll);
		}
		
		HSSFCell cell5 = row.createCell(8);
		if(repo.getTotalRemuneracionPeriodoAnterior() !=null){
		  cell5.setCellValue(repo.getTotalRemuneracionPeriodoAnterior());
		  cell5.setCellStyle(styleMoneyRight);
	    }else{
		  cell5.setCellValue(new HSSFRichTextString(""));
		  cell5.setCellStyle(styleAll);
		}
		
		HSSFCell cell6 = row.createCell(9);
		if(repo.getCantidadRemuneracionPeriodoNoPadron() !=null){
		  cell6.setCellValue(repo.getCantidadRemuneracionPeriodoNoPadron());
		  cell6.setCellStyle(styleAll);
	    }else{
		  cell6.setCellValue(new HSSFRichTextString(""));
		  cell6.setCellStyle(styleAll);
		}
		
		HSSFCell cell7 = row.createCell(10);
		if(repo.getTotalRemuneracionPeriodoNoPadron() !=null){
		  cell7.setCellValue(repo.getTotalRemuneracionPeriodoNoPadron());
		  cell7.setCellStyle(styleMoneyRight);
	    }else{
		  cell7.setCellValue(new HSSFRichTextString(""));
		  cell7.setCellStyle(styleAll);
		}
		
		HSSFCell cell8 = row.createCell(11);
		if(repo.getAportes() !=null){
		  cell8.setCellValue(repo.getAportes() );
		  cell8.setCellStyle(styleMoneyRight);
	    }else{
		  cell8.setCellValue(new HSSFRichTextString(""));
		  cell8.setCellStyle(styleAll);
		}
		
		HSSFCell cell9 = row.createCell(12);
		if(repo.getContribuciones() !=null){
		  cell9.setCellValue(repo.getContribuciones() );
		  cell9.setCellStyle(styleMoneyRight);
	    }else{
		  cell9.setCellValue(new HSSFRichTextString(""));
		  cell9.setCellStyle(styleAll);
		}
		
		HSSFCell cell10 = row.createCell(13);
		if(repo.getTotalAportesContrib() !=null){
		  cell10.setCellValue(repo.getTotalAportesContrib() );
		  cell10.setCellStyle(styleMoneyRight);
	    }else{
		  cell10.setCellValue(new HSSFRichTextString(""));
		  cell10.setCellStyle(styleAll);
		}
		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Sin \n Remun."));
		cell1.setCellStyle(styleHeader);
		
		HSSFCell cell11 = row.createCell(2);
		cell11.setCellValue(new HSSFRichTextString("Efectores \nSociales"));
		cell11.setCellStyle(styleHeader);
		
		HSSFCell cell12 = row.createCell(3);
		cell12.setCellValue(new HSSFRichTextString("Serv.\nDoméstico"));
		cell12.setCellStyle(styleHeader);
		
		HSSFCell cell13 = row.createCell(4);
		cell13.setCellValue(new HSSFRichTextString("Monotrib."));
		cell13.setCellStyle(styleHeader);
		
		
		HSSFCell cell2 = row.createCell(5);
		cell2.setCellValue(new HSSFRichTextString("Con Remun. \nen Período"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(6);
		cell3.setCellValue(new HSSFRichTextString("Remun. \ndel Período"));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(7);
		cell4.setCellValue(new HSSFRichTextString("Con Remun. en\nPeríodo Anterior"));
		cell4.setCellStyle(styleHeader);
		
		HSSFCell cell5 = row.createCell(8);
		cell5.setCellValue(new HSSFRichTextString("Remun. del \nPeríodo Anterior"));
		cell5.setCellStyle(styleHeaderL);

		HSSFCell cell6 = row.createCell(9);
		cell6.setCellValue(new HSSFRichTextString("No \nEmpadronados"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(10);
		cell7.setCellValue(new HSSFRichTextString("Remun. No \nEmpadronados"));
		cell7.setCellStyle(styleHeader);
		
		HSSFCell cell8 = row.createCell(11);
		cell8.setCellValue(new HSSFRichTextString("Aportes"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(12);
		cell9.setCellValue(new HSSFRichTextString("Contribuciones"));
		cell9.setCellStyle(styleHeader);
		
		HSSFCell cell10 = row.createCell(13);
		cell10.setCellValue(new HSSFRichTextString("Total Aportes \n+ Contrib"));
		cell10.setCellStyle(styleHeader);
		
		
		row.setHeight((short) 500);
		
		
		HSSFCellStyle st =  wb.createCellStyle();
		st.setDataFormat((short) 6);
//		st.setFillForegroundColor( HSSFColor.GREY_25_PERCENT.index);
		st.setFillForegroundColor( HSSFColorPredefined.AQUA.getIndex());
		st.setFillBackgroundColor(FillPatternType.SOLID_FOREGROUND.getCode());
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		for(int xi=0;xi<=13;xi++){
	      row.getCell(xi).setCellStyle(st);
		}
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila) throws SystemException {

		
		List parametros = SchedulerServiceUtil.getParameters("reporte.ingreso_devengado_periodo");
        String fDesde = parametros.get(0).toString().substring(6)+"/"+parametros.get(0).toString().substring(4, 6) +"/" +
                        parametros.get(0).toString().substring(0, 4);
        
        String fHasta = parametros.get(1).toString().substring(6)+"/"+parametros.get(1).toString().substring(4, 6) +"/" +
                parametros.get(1).toString().substring(0, 4);
		
        String tituloReporte = "Ingresos Devengados desde "+fDesde +" a " + fHasta;
		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		
		HSSFCell cell12 = rowTitulo.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Impresión: "
				+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell12.setCellStyle(getStyleAllCenter(wb));
		
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 12, 13));
		fila++;

		return fila;
	}
	
	
	private static int createFooter(HSSFWorkbook wb, HSSFSheet sheet,
			int fila) throws SystemException {

			
		fila++;
		
		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Sin Remuneración: cantidad de titulares sin remuneración en el mes analizado ni en los 2 anteriores."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;

		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Efectores Sociales: cantidad de beneficiarios (titulares y adherentes) de alta en el padrón como Efectores Sociales."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Servicio Doméstico: cantidad de beneficiarios (titulares y adherentes) de alta en el padrón como Servicio Doméstico."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Montoributistas: cantidad de beneficiarios (titulares y adherentes) de alta en el padrón como Monotributistas."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Con Remuneración en Período: cantidad de titulares con remuneración (declaración) en AFIP en el mes analizado."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Remuneración del Período: suma de las remuneraciones declaradas en AFIP en el mes analizado correspondiente a los titulares del punto anterior."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Con Remuneración en Período Anterior: cantidad de titulares que no tuvieron remuneración (declaración) en AFIP en el mes analizado, habiendo tenido en al menos uno de los 2 meses anteriores."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Remuneración del Período Anterior: suma de las remuneraciones declaradas en AFIP en uno de los dos meses anteriores al analizado correspondiente a los titulares del punto anterior."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("No empadronados: cantidad de titulares con remuneración (declaración) en AFIP en el mes analizado y que no están incluidos en el Portal Molineros. Se excluyen a los que pertenecen a empresas del ramo 100."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Remuneración no Empadronados: suma de las remuneraciones declaradas en AFIP en el mes analizado correspondiente a los titulares del punto anterior."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Aportes: para relación de dependencia, 85% del 3% de la remuneración (teniendo en cuenta el tope dispuesto por AFIP). Para Efectores Sociales, Serv Dom y Monotributo, 90% del pago de obra social por beneficiario."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Contribuciones: para relación de dependencia, 85% del 6% de la remuneración."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		rowTitulo = sheet.createRow(fila);
		cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Total Aportes más Contribuciones: suma de los 2 puntos anteriores."));
		cell.setCellStyle(getStyleAll(wb));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 13));
		fila++;
		
		return fila;
	}
	
}

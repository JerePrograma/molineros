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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.ReporteAportesPagoRamoBean;
import ar.com.ospim.tesoreria.service.ReportesServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteAportesPagoRamoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteAportesPagoRamoExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
       try{
						
	 	  List<ReporteAportesPagoRamoBean>reporte = (List<ReporteAportesPagoRamoBean>) ReportesServiceUtil.getAportesPagoRamo() ;
			
			
		  return generarReporte(reporte);
			
			
		} catch (Exception e) {
			_log.error("Error al generar Aporte Pago Ramo Empresas", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(List<ReporteAportesPagoRamoBean> reporte) throws SystemException {
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

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		
		int rowIni= i+1;
				
		for (ReporteAportesPagoRamoBean repo : reporte) {
			
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		}
		
		
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell0 = rowTitulo.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Total"));
		
		HSSFCell cell1 = rowTitulo.createCell(1);
		cell1.setCellFormula("SUM(B"+Integer.toString(rowIni)  +":B"+ Integer.toString(i) +")");
		cell1.setCellStyle(styleMoneyRight);
		
		HSSFCell cell2 = rowTitulo.createCell(2);
		cell2.setCellFormula("SUM(C"+Integer.toString(rowIni)  +":"+ "C"+ Integer.toString(i) +")");
		cell2.setCellStyle(styleMoneyRight);
		
		HSSFCell cell3 = rowTitulo.createCell(3);
		cell3.setCellFormula("SUM(D"+Integer.toString(rowIni)  +":D"+ Integer.toString(i) +")");
		cell3.setCellStyle(styleMoneyRight);
		
		HSSFCell cell4 = rowTitulo.createCell(4);
		cell4.setCellFormula("SUM(E"+Integer.toString(rowIni)  +":E"+ Integer.toString(i) +")");
		cell4.setCellStyle(styleMoneyRight);
		
		HSSFCell cell5 = rowTitulo.createCell(5);
		cell5.setCellFormula("SUM(F"+Integer.toString(rowIni)  +":F"+ Integer.toString(i) +")");
		cell5.setCellStyle(styleMoneyRight);
		
		HSSFCell cell6 = rowTitulo.createCell(6);
		cell6.setCellFormula("SUM(G"+Integer.toString(rowIni)  +":G"+ Integer.toString(i) +")");
		cell6.setCellStyle(styleMoneyRight);
		
		HSSFCell cell7 = rowTitulo.createCell(7);
		cell7.setCellFormula("SUM(H"+Integer.toString(rowIni)  +":H"+ Integer.toString(i) +")");
		cell7.setCellStyle(styleMoneyRight);
		
		i++;
		
		
		
		for(int x=0;x<58;x++){
			sheet.autoSizeColumn((short) x);
		}
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ReporteAportesPagoRamoBean repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		if(repo.getPeriodo()!=null){
		  cell0.setCellValue(repo.getPeriodo());
		  cell0.setCellStyle(styleFechaLeftTop);
		}else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getCalculado10()!=null){
		  cell1.setCellValue(repo.getCalculado10().doubleValue() );
		  cell1.setCellStyle(styleMoneyRight);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		HSSFCell cell2 = row.createCell(2);
		if(repo.getPagado10()!=null){
		  cell2.setCellValue(repo.getPagado10().doubleValue() );
		  cell2.setCellStyle(styleMoneyRight);
	    }else{
		  cell2.setCellValue(new HSSFRichTextString(""));
		  cell2.setCellStyle(styleAll);
		}
		
		HSSFCell cell3 = row.createCell(3);
		if(repo.getCalculado50()!=null){
		  cell3.setCellValue(repo.getCalculado50().doubleValue() );
		  cell3.setCellStyle(styleMoneyRight);
	    }else{
		  cell3.setCellValue(new HSSFRichTextString(""));
		  cell3.setCellStyle(styleAll);
		}
		
		HSSFCell cell4 = row.createCell(4);
		if(repo.getPagado50()!=null){
		  cell4.setCellValue(repo.getPagado50().doubleValue() );
		  cell4.setCellStyle(styleMoneyRight);
	    }else{
		  cell4.setCellValue(new HSSFRichTextString(""));
		  cell4.setCellStyle(styleAll);
		}
		
		HSSFCell cell5 = row.createCell(5);
		if(repo.getCalculado99()!=null){
		  cell5.setCellValue(repo.getCalculado99().doubleValue() );
		  cell5.setCellStyle(styleMoneyRight);
	    }else{
		  cell5.setCellValue(new HSSFRichTextString(""));
		  cell5.setCellStyle(styleAll);
		}
		
		HSSFCell cell6 = row.createCell(6);
		if(repo.getPagado99()!=null){
		  cell6.setCellValue(repo.getPagado99().doubleValue() );
		  cell6.setCellStyle(styleMoneyRight);
	    }else{
		  cell6.setCellValue(new HSSFRichTextString(""));
		  cell6.setCellStyle(styleAll);
		}
		
		HSSFCell cell7 = row.createCell(7);
		if(repo.getMontribPagado() !=null){
		  cell7.setCellValue(repo.getMontribPagado().doubleValue() );
		  cell7.setCellStyle(styleMoneyRight);
	    }else{
		  cell7.setCellValue(new HSSFRichTextString(""));
		  cell7.setCellStyle(styleAll);
		}
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		
			
		
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0r1 = row.createCell(0);
		cell0r1.setCellValue(new HSSFRichTextString("Período"));
		cell0r1.setCellStyle(styleHeaderL);
		
		HSSFCell cell1r1 = row.createCell(1);
		cell1r1.setCellValue(new HSSFRichTextString("Ramo 1 al 10"));
		cell1r1.setCellStyle(styleHeader);
		
		HSSFCell cell3r1 = row.createCell(3);
		cell3r1.setCellValue(new HSSFRichTextString("Ramo 50"));
		cell3r1.setCellStyle(styleHeader);
		
		HSSFCell cell5r1 = row.createCell(5);
		cell5r1.setCellValue(new HSSFRichTextString("Ramo 99"));
		cell5r1.setCellStyle(styleHeader);
		
		HSSFCell cell7r1 = row.createCell(7);
		cell7r1.setCellValue(new HSSFRichTextString("Monotributistas/Serv.Doméstico"));
		cell7r1.setCellStyle(styleHeader);
		
		i++;
		row = sheet.createRow(i);
		
		
		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(""));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Calculado"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Pagado"));
		cell2.setCellStyle(styleHeader);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Calculado"));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Pagado"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Calculado"));
		cell5.setCellStyle(styleHeaderL);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Pagado"));
		cell6.setCellStyle(styleHeaderL);
		
		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Pagado"));
		cell7.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(i-1, i, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 5, 6));
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila) throws SystemException {
        List parametros = SchedulerServiceUtil.getParameters("reporte.aportes_pago_ramo");
        String fDesde = parametros.get(0).toString().substring(6)+"/"+parametros.get(0).toString().substring(4, 6) +"/" +
                        parametros.get(0).toString().substring(0, 4);
        
        String fHasta = parametros.get(1).toString().substring(6)+"/"+parametros.get(1).toString().substring(4, 6) +"/" +
                parametros.get(1).toString().substring(0, 4);
        
		String tituloReporte = "Resumen Calculado y pagado por Aportes y Contribuciones desde " + fDesde
				+ " a " + fHasta;

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		
		HSSFCell cell12 = rowTitulo.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Impresión: "
				+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell12.setCellStyle(getStyleAllCenter(wb));
		
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;
        fila++;
		return fila;
	}
}

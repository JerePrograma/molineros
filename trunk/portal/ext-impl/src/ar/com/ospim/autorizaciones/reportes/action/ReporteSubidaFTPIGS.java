package ar.com.ospim.autorizaciones.reportes.action;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.TotalesPadronIGS;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteSubidaFTPIGS extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSubidaFTPIGS.class);

	public static HSSFWorkbook generaReporte(List<TotalesPadronIGS> tatales,
			String periodo) {
		_log.debug("generando reporte");

		try {							
			return generarReporte(tatales, periodo);	
			
		} catch (Exception e) {
			_log.error("Error al generar reporter subida FTP IGS", e);
			return null;
		}
		
	}
	
      
	private static HSSFWorkbook generarReporte(List<TotalesPadronIGS> totales, String periodo) {
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
		
	
		HSSFCellStyle styleWithBorder = getStyleAllWithBorder(wb);


		HSSFSheet sheet = wb.createSheet("SUBIDA_FTP_IGS  " + periodo);
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i, styleWithBorder);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb,  styleWithBorder);
		
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		Integer total = 0;
		
		for (TotalesPadronIGS repo : totales) {
			  		total = total + repo.getCantidad();    	
			    	i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
			    			styleMoneyRight, styleFechaLeftTop, styleAllTop,
			    			styleMoneyRightTop,styleWithBorder); 
		}
		
		i = generarTotal(sheet, i, total, styleFechaLeft, styleAll,
    			styleMoneyRight, styleFechaLeftTop, styleAllTop,
    			styleMoneyRightTop,styleWithBorder); 
		
		i++;
		
		for(int x=0;x<58;x++){
			sheet.autoSizeColumn((short) x);
		}
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			TotalesPadronIGS repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop, HSSFCellStyle styleWithBorder) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		
		cell0.setCellValue(new HSSFRichTextString(repo.getNombreIGS()) );
		cell0.setCellStyle(styleWithBorder);
		
		
		HSSFCell cell1 = row.createCell(1);
	
		cell1.setCellValue(repo.getCantidad());
		cell1.setCellStyle(styleWithBorder);
	
		
		
		
		return ++i;
	}
	
	private static int generarTotal(HSSFSheet sheet, int i,
			Integer total, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop, HSSFCellStyle styleWithBorder ) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		
		cell0.setCellValue(new HSSFRichTextString("Total") );
		cell0.setCellStyle(styleWithBorder);
		
		
		HSSFCell cell1 = row.createCell(1);
	
		cell1.setCellValue(total);
		cell1.setCellStyle(styleWithBorder);
	
		
		
		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb, HSSFCellStyle styleWithBorder) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Plan"));
		cell0.setCellStyle(styleWithBorder);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Cantidad"));
		cell1.setCellStyle(styleWithBorder);
		
		
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, HSSFCellStyle styleWithBorder) {

		String tituloReporte = "Reporte padrón subida IGS";

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);
        cell.setCellStyle(styleWithBorder);
		
		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		cell.setCellStyle(styleWithBorder);

		
		

		return fila;
	}
}
package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.autorizaciones.beans.RespuestaPreAutorizPSDTO;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

public class ReporteRespuestasPSaPreautorizacionesExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteRespuestasPSaPreautorizacionesExcel.class);

//	public static HSSFWorkbook generaReporte(
//			HttpServletRequest renderRequest, HttpServletResponse res) {
	public static HSSFWorkbook generaReporte(String to, String titulo, List<RespuestaPreAutorizPSDTO> rtasPreAutoriz) {	
		
		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFCellStyle styleAll =getStyleAll(wb);
		styleAll.setWrapText(true);
		HSSFSheet sheet = wb.createSheet("Autorizaciones");
	    
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		int index = createHeader(wb, sheet,titulo);
		  index++;
		
		for(RespuestaPreAutorizPSDTO p:rtasPreAutoriz){
			int column = 0;
			  HSSFRow row = sheet.createRow(index++);
			  
			  HSSFCell cell01 = row.createCell(column++);
			  cell01.setCellValue(new HSSFRichTextString(p.getFinishDate() ));
			  
			  HSSFCell cell02 = row.createCell(column++);
			  cell02.setCellValue(new HSSFRichTextString(p.getTransactionId()!=null?p.getTransactionId().toString():"" ));
			  
			  HSSFCell cell03 = row.createCell(column++);
			  cell03.setCellValue(new HSSFRichTextString(p.getTributaryCodeNumber() ));
			  
			  HSSFCell cell04 = row.createCell(column++);
			  cell04.setCellValue(new HSSFRichTextString(p.getAfiliadoApeyNom() ));
			  
			  HSSFCell cell05 = row.createCell(column++);
			  cell05.setCellValue(new HSSFRichTextString(p.getAuthorizationProposalNumber().toString()));
			  
			  HSSFCell cell06 = row.createCell(column++);
			  cell06.setCellValue(new HSSFRichTextString(p.getMedicalPractice()));
			  
			  HSSFCell cell07 = row.createCell(column++);
			  cell07.setCellValue(new HSSFRichTextString(p.getMedicalPracticeDescription()));
				
			  /**doc v3.
			   * Estados: 
			   * A - Autorizada. 
			   * R - Rechazada. 
			   * D - Solicitud de documentación adicional.
			   * Aquellas solicitudes para las que no se reciba estado se encontrarán en gestión.
			   */
			  HSSFCell cell08 = row.createCell(column++);
			  if(p.getAuthorizationStatus()!=null && p.getAuthorizationStatus().equalsIgnoreCase("A")) {
				  cell08.setCellValue(new HSSFRichTextString("Autorizada"));
			  }else if (p.getAuthorizationStatus()!=null && p.getAuthorizationStatus().equalsIgnoreCase("R")) {
				  cell08.setCellValue(new HSSFRichTextString("Rechazada"));
			  }else if (p.getAuthorizationStatus()!=null && p.getAuthorizationStatus().equalsIgnoreCase("D")) {
				  cell08.setCellValue(new HSSFRichTextString("Observada"));	
			  }else if (p.getAuthorizationStatus()!=null && p.getAuthorizationStatus().equalsIgnoreCase("P")) {
				  cell08.setCellValue(new HSSFRichTextString("Pendiente"));	
			  }else if (p.getAuthorizationStatus()!=null && p.getAuthorizationStatus().equalsIgnoreCase("X")) {
				  cell08.setCellValue(new HSSFRichTextString("Desestimado"));		  
			  }else {
				  cell08.setCellValue(new HSSFRichTextString("No Definido"));	
			  }
			  
		}
		
		for(int i=0;i<10;i++)
			     sheet.autoSizeColumn((short) i);
		
		
		return wb;
		
	}
		
	
	protected static HSSFCellStyle getStyleAll(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) size);
		styleAll.setFont(font);
		return styleAll;
	}
	
	protected static void setThinBorders(HSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}
	
    private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet,String titulo) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar gmtMenos3 = DateUtils.getCalendarGMTMenos3();

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		row.setHeight((short) 400);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Autorizaciones de " + titulo));
		cell.setCellStyle(styleHeaderEnca);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
		
		HSSFRow row1 = sheet.createRow(index++);

		HSSFCell cell2 = row1.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(gmtMenos3.getTime())));
		
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row2 = sheet.createRow(index);

		int column = 0;

		HSSFCell cell32 = row2.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Fecha"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row2.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("ID"));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row2.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Cuil"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row2.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell36 = row2.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Autorizacion"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row2.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Prestacion"));
		cell37.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell38 = row2.createCell(column++);
		cell38.setCellValue(new HSSFRichTextString("Nombre Prestacion"));
		cell38.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell39 = row2.createCell(column++);
		cell39.setCellValue(new HSSFRichTextString("Estado"));
		cell39.setCellStyle(styleHeaderEnca2);
		
		
		return index;
	}
    
    protected static HSSFCellStyle getStyleHeaderWithBorderNoColor(
			HSSFWorkbook wb, int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
		styleHeader.setAlignment(HorizontalAlignment.CENTER);
		return styleHeader;
	}
    
    protected static HSSFCellStyle getStyleBold(HSSFWorkbook wb, int size) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) size);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}
    
    protected static HSSFCellStyle getStyleHeaderWithBorderLeftNoColor(
			HSSFWorkbook wb, int size) {
		HSSFCellStyle styleHeader = getStyleBold(wb, size);
		setThinBorders(styleHeader);
		styleHeader.setAlignment(HorizontalAlignment.LEFT);
		return styleHeader;
	}
    
    protected static HSSFCellStyle getStyleMoney(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = getStyleAll(wb);
		styleAll.setDataFormat((short) 4);
		return styleAll;
	}
    
	
}



package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteImportadorImagenesErrorExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteImportadorImagenesErrorExcel.class);

	

	public static HSSFWorkbook generaReporteAfiNoExiste(
			List<String> list) {
		
		_log.debug("Inicio ReporteImportadorImagenesErrorExcel ");
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Afiliados");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber= getStyleNumber(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte Afiliado no existe en padrón: ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell0H.setCellStyle(styleBold);
		
		
		index++;
		
		for(String cuil: list){
			index=crearDatos(sheet, cuil, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber );
		}

		index++;
		sheet.createRow(index);
		
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
	
		_log.debug("Fin ReporteImportadorImagenesErrorExcel ");

		return wb;
		
	}

	private static int crearDatos(HSSFSheet sheet,String cuil, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(cuil));
		cell0.setCellStyle(styleNumber);
		
		
		
		return index++;
	}	
}
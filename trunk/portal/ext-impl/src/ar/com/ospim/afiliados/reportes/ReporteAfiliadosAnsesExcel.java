package ar.com.ospim.afiliados.reportes;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteAfiliadosAnsesExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteAfiliadosAnsesExcel.class);

	public static HSSFWorkbook generaReporteAfiliadosAnses(
			HttpServletRequest req, HttpServletResponse res) {		

		try {			
			ReportesBonosServiceImpl reporteService = new ReportesBonosServiceImpl();						
				
																				
			List<ReporteAfiliadosAnses> listado= reporteService.getReporteAfiliadosAnses();
			
			return generarReporteAfilAnses(listado);
			
		} catch (Exception e) {
			_log.error("Error al generar listado jubilados", e);
			return null;
		}
	}
		
	private static HSSFWorkbook generarReporteAfilAnses(List<ReporteAfiliadosAnses> listado) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Jubilados");
		try {
			int index = createHeaderAfilAnses(wb, sheet);

			for (ReporteAfiliadosAnses rep : listado) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(0);			
				cell0.setCellValue(new HSSFRichTextString(String.valueOf(rep.getTipo())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(rep.getPeriodo())));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue(new HSSFRichTextString(String.valueOf(rep.getCuil_titular())));
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell3 = row.createCell(3);
				cell3.setCellValue(new HSSFRichTextString(String.valueOf(rep.getCuil())));
				cell3.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(4);
				cell4.setCellValue(new HSSFRichTextString(String.valueOf(rep.getApellido())));
				cell4.setCellStyle(styleAllWithBorder);
				HSSFCell cell5 = row.createCell(5);
				cell5.setCellValue(new HSSFRichTextString(String.valueOf(rep.getNombre())));
				cell5.setCellStyle(styleAllWithBorder);
				HSSFCell cell6 = row.createCell(6);
				cell6.setCellValue(new HSSFRichTextString(String.valueOf(rep.getEdadAnios())));
				cell6.setCellStyle(styleAllWithBorder);
				HSSFCell cell7 = row.createCell(7);
				cell7.setCellValue(new HSSFRichTextString(String.valueOf(rep.getEdad())));
				cell7.setCellStyle(styleAllWithBorder);
				
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);
			sheet.autoSizeColumn((short) 6);
			sheet.autoSizeColumn((short) 7);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}
	

	private static int createHeaderAfilAnses(HSSFWorkbook wb, HSSFSheet sheet) {		
		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorder(wb, 10);
		int index = 0;	
			 		
		
		
		HSSFRow row3 = sheet.createRow(index++);

		HSSFCell cell31 = row3.createCell(0);
		cell31.setCellValue(new HSSFRichTextString("Tipo"));
		cell31.setCellStyle(styleHeaderEnca3);

		HSSFCell cell32 = row3.createCell(1);
		cell32.setCellValue(new HSSFRichTextString("Período"));
		cell32.setCellStyle(styleHeaderEnca3);

		HSSFCell cell33 = row3.createCell(2);
		cell33.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell33.setCellStyle(styleHeaderEnca3);

		HSSFCell cell34 = row3.createCell(3);
		cell34.setCellValue(new HSSFRichTextString("Cuil"));
		cell34.setCellStyle(styleHeaderEnca3);

		HSSFCell cell35 = row3.createCell(4);
		cell35.setCellValue(new HSSFRichTextString("Apellido"));
		cell35.setCellStyle(styleHeaderEnca3);		

		HSSFCell cell36 = row3.createCell(5);
		cell36.setCellValue(new HSSFRichTextString("Nombre"));
		cell36.setCellStyle(styleHeaderEnca3);		

		HSSFCell cell37 = row3.createCell(6);
		cell37.setCellValue(new HSSFRichTextString("Edad"));
		cell37.setCellStyle(styleHeaderEnca3);		

		HSSFCell cell38 = row3.createCell(7);
		cell38.setCellValue(new HSSFRichTextString("Edad Detalle"));
		cell38.setCellStyle(styleHeaderEnca3);		

		
		return index;
	}

	
}

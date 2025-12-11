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

public class ReporteCantBonosSeccionalExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCantBonosSeccionalExcel.class);

	public static HSSFWorkbook generaReporteCantBonosSeccional(
			HttpServletRequest req, HttpServletResponse res) {
		

		try {			
			ReportesBonosServiceImpl reporteService = new ReportesBonosServiceImpl();
			List<ReporteCantBonosSeccional> listado= reporteService.getReporteCantBonosSeccional();
			return generarReporte(listado);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(List<ReporteCantBonosSeccional> listado) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet);

			for (ReporteCantBonosSeccional rep : listado) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(new HSSFRichTextString(rep.getSeccional()));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(rep.getTotal_beneficiarios())));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue(new HSSFRichTextString(String.valueOf(rep.getCant_bonos1())));
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell = row.createCell(3);
				cell.setCellValue(new HSSFRichTextString(String.valueOf(rep.getCant_bonos2())));
				cell.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(4);
				cell4.setCellValue(new HSSFRichTextString(String.valueOf(rep.getCant_bonos3())));
				cell4.setCellStyle(styleAllWithBorder);
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet) {		
		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorder(wb, 10);
		int index = 0;
		
		HSSFRow row3 = sheet.createRow(index++);

		HSSFCell cell31 = row3.createCell(0);
		cell31.setCellValue(new HSSFRichTextString("Seccional"));
		cell31.setCellStyle(styleHeaderEnca3);

		HSSFCell cell32 = row3.createCell(1);
		cell32.setCellValue(new HSSFRichTextString("Total Beneficiarios"));
		cell32.setCellStyle(styleHeaderEnca3);

		HSSFCell cell33 = row3.createCell(2);
		cell33.setCellValue(new HSSFRichTextString("Cant.Bonos 1"));
		cell33.setCellStyle(styleHeaderEnca3);

		HSSFCell cell34 = row3.createCell(3);
		cell34.setCellValue(new HSSFRichTextString("Cant.Bonos 2"));
		cell34.setCellStyle(styleHeaderEnca3);

		HSSFCell cell35 = row3.createCell(4);
		cell35.setCellValue(new HSSFRichTextString("Cant. Bonos 3"));
		cell35.setCellStyle(styleHeaderEnca3);

		return index;
	}

}

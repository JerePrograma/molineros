package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.Date;
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

public class ReportePosiblesInconsistenciasExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReportePosiblesInconsistenciasExcel.class);

	public static HSSFWorkbook generaReportePosiblesInconsistencias(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		ReportesAfiliadoServiceImpl reporteService = new ReportesAfiliadoServiceImpl();

		try {
			List<ReportePosiblesInconsistenciasResult> reporte = reporteService
					.getReportePosiblesInconsistencias();
			String fecha = format.format(new Date(System.currentTimeMillis()));

			return generarReporte(fecha, reporte);

		} catch (Exception e) {
			_log.error("Error al generar reporte inconsistencias", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(String fecha,
			List<ReportePosiblesInconsistenciasResult> lista) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, fecha);

			for (ReportePosiblesInconsistenciasResult rep : lista) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(0);
				cell0
						.setCellValue(new HSSFRichTextString(rep
								.getCuil_titular()));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(rep
						.getInte())));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue(new HSSFRichTextString(rep
						.getTercerizadora()));
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell = row.createCell(3);
				cell
						.setCellValue(new HSSFRichTextString(rep
								.getObservaciones()));
				cell.setCellStyle(styleAllWithBorder);
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);

		} catch (Exception e) {
			_log.error(e);
		}

		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			String fecha) {

		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorder(wb, 10);
		int index = 0;

		HSSFRow row3 = sheet.createRow(index);

		HSSFCell cell31 = row3.createCell(0);
		cell31.setCellValue(new HSSFRichTextString("CUIL"));
		cell31.setCellStyle(styleHeaderEnca3);

		HSSFCell cell32 = row3.createCell(1);
		cell32.setCellValue(new HSSFRichTextString("Inte"));
		cell32.setCellStyle(styleHeaderEnca3);

		HSSFCell cell33 = row3.createCell(2);
		cell33.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell33.setCellStyle(styleHeaderEnca3);

		HSSFCell cell34 = row3.createCell(3);
		cell34.setCellValue(new HSSFRichTextString("Observaciones"));
		cell34.setCellStyle(styleHeaderEnca3);

		return ++index;
	}

}

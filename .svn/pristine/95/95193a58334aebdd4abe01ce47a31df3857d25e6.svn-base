package ar.com.ospim.afiliados.reportes;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.EnvioBonos;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteResultBusquedaBonosSeccionalExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteResultBusquedaBonosSeccionalExcel.class);

	public static HSSFWorkbook generaReporteResultBonosSeccional(
			HttpServletRequest req, HttpServletResponse res) {
		try {
			List<EnvioBonos> listado= (ArrayList<EnvioBonos>) req.getSession().getAttribute(WebKeysAfiliados.ENVIO_BONOS);
			return generarReporte(listado);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(List<EnvioBonos> listado) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet);

			for (EnvioBonos rep : listado) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(new HSSFRichTextString(rep.getTipo_bono_string()));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(new HSSFRichTextString(rep.getSeccional_string()));
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue(new HSSFRichTextString(rep.getFecha_envio_string()));
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell = row.createCell(3);
				cell.setCellValue(new HSSFRichTextString(String.valueOf(rep.getBono_desde())));
				cell.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(4);
				cell4.setCellValue(new HSSFRichTextString(String.valueOf(rep.getBono_hasta())));
				cell4.setCellStyle(styleAllWithBorder);
				HSSFCell cell5 = row.createCell(5);
				cell5.setCellValue(new HSSFRichTextString(String.valueOf(rep.getCant_envio())));
				cell5.setCellStyle(styleAllWithBorder);
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);

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
		cell31.setCellValue(new HSSFRichTextString("Tipo de Bono"));
		cell31.setCellStyle(styleHeaderEnca3);

		HSSFCell cell32 = row3.createCell(1);
		cell32.setCellValue(new HSSFRichTextString("Seccional"));
		cell32.setCellStyle(styleHeaderEnca3);

		HSSFCell cell33 = row3.createCell(2);
		cell33.setCellValue(new HSSFRichTextString("Fecha Envío"));
		cell33.setCellStyle(styleHeaderEnca3);

		HSSFCell cell34 = row3.createCell(3);
		cell34.setCellValue(new HSSFRichTextString("Bono Desde"));
		cell34.setCellStyle(styleHeaderEnca3);

		HSSFCell cell35 = row3.createCell(4);
		cell35.setCellValue(new HSSFRichTextString("Bono Hasta"));
		cell35.setCellStyle(styleHeaderEnca3);
		

		HSSFCell cell36 = row3.createCell(5);
		cell36.setCellValue(new HSSFRichTextString("Cantidad"));
		cell36.setCellStyle(styleHeaderEnca3);

		return index;
	}

}

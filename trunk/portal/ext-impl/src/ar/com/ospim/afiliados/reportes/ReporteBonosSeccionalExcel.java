package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteBonosSeccionalExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteBonosSeccionalExcel.class);

	public static HSSFWorkbook generaReporteBonosSeccional(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		String tipoBono = ParamUtil.getString(req, "tipoBono");
		int id_seccional = ParamUtil.getInteger(req, "id_seccional");
		String seccional = ParamUtil.getString(req, "seccional");
		int bono_desde = ParamUtil.getInteger(req, "bono_desde");
		int bono_hasta = ParamUtil.getInteger(req, "bono_hasta");

		try {
			String fecha = format.format(new Date(System.currentTimeMillis()));

			return generarReporte(tipoBono, id_seccional, seccional,
					bono_desde, bono_hasta, fecha);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(String tipoBono,
			int id_seccional, String seccional, int bono_desde, int bono_hasta,
			String fecha) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, tipoBono, seccional, fecha);

			for (int i = bono_desde; i <= bono_hasta; i++) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(0);
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(2);
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell = row.createCell(3);
				cell.setCellValue(new HSSFRichTextString(String.valueOf(i)));
				cell.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(4);
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
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			String tipo_bono, String seccional, String fecha) {
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 14);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 12);
		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);
		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ENVIO DE BONOS"));
		cell.setCellStyle(styleHeaderEnca);
		
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		
		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("Tipo de Bono: "+tipo_bono));
		cell1.setCellStyle(styleHeaderEnca2);
		
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
		
		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Envío: "+fecha));
		cell2.setCellStyle(styleHeaderEnca2);
		
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));
		

		HSSFRow row3a = sheet.createRow(index++);

		HSSFCell cell21 = row3a.createCell(0);
		cell21.setCellValue(new HSSFRichTextString("SECCIONAL: "+seccional));
		cell21.setCellStyle(styleHeaderEnca2);
		
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));

		HSSFRow row3 = sheet.createRow(index++);

		HSSFCell cell31 = row3.createCell(0);
		cell31.setCellValue(new HSSFRichTextString("FECHA"));
		cell31.setCellStyle(styleHeaderEnca3);
		
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 4));

		HSSFCell cell32 = row3.createCell(1);
		cell32.setCellValue(new HSSFRichTextString("NOMBRE Y APELLIDO"));
		cell32.setCellStyle(styleHeaderEnca3);

		HSSFCell cell33 = row3.createCell(2);
		cell33.setCellValue(new HSSFRichTextString("NO. O.S.P.I.M."));
		cell33.setCellStyle(styleHeaderEnca3);

		HSSFCell cell34 = row3.createCell(3);
		cell34.setCellValue(new HSSFRichTextString("NO.ORDEN"));
		cell34.setCellStyle(styleHeaderEnca3);

		HSSFCell cell35 = row3.createCell(4);
		cell35.setCellValue(new HSSFRichTextString("IMPORTE"));
		cell35.setCellStyle(styleHeaderEnca3);

		return index;
	}

}

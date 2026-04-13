package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.reportes.beans.ReporteAmtimaPMI;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteAmtimaPmiExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteBonosSeccionalExcel.class);

	public static HSSFWorkbook generaReporteAmtimaPmi(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
		Boolean soloConyuges=ParamUtil.getBoolean(req, "solo_conyuges");
		
		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			ReportesAmtimaPmiServiceImpl service=new ReportesAmtimaPmiServiceImpl();
			ArrayList<ReporteAmtimaPMI> lista= (ArrayList<ReporteAmtimaPMI>)service.getReporteAmtimaPMI(fechaIni,fechaFin,soloConyuges);
			
			return generarReporte(lista, fechaIni, fechaFin);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(ArrayList<ReporteAmtimaPMI> lista, Date fechaIni, Date fechaFin) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date hoy= new Date(System.currentTimeMillis());
		try {

			int index = createHeader(wb, sheet, format.format(fechaIni), format.format(fechaFin), format.format(hoy));

			for (ReporteAmtimaPMI amt:lista) {
				HSSFRow row = sheet.createRow(index++);
				HSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(new HSSFRichTextString(format.format(amt.getFechaVto())));
				cell0.setCellStyle(styleAllWithBorder);
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(amt.getId_amtima());
				cell1.setCellStyle(styleAllWithBorder);
				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue(amt.getInte());
				cell2.setCellStyle(styleAllWithBorder);
				HSSFCell cell = row.createCell(3);
				cell.setCellValue(new HSSFRichTextString(amt.getApe_nom()));
				cell.setCellStyle(styleAllWithBorder);
				HSSFCell cell4 = row.createCell(4);
				cell4.setCellValue(new HSSFRichTextString(amt.getSeccional()));
				cell4.setCellStyle(styleAllWithBorder);
				HSSFCell cell5 = row.createCell(5);
				cell5.setCellValue(new HSSFRichTextString(amt.getEmpresa()));
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

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, String fechaIni, String fechaFin, String fechaHoy) {
		HSSFCellStyle styleHeaderEnca = getStyleBoldAligned(wb, HorizontalAlignment.CENTER);		
		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorder(wb, 10);
		int index = 0;
		
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Listado de vencimientos en certificados -- "+fechaIni+" al "+fechaFin+" Impreso el "+fechaHoy));
		cell.setCellStyle(styleHeaderEnca);
				
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
		
		HSSFRow row21= sheet.createRow(index++);

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("Fecha"));
		cell1.setCellStyle(styleHeaderEnca3);

		HSSFCell cell34 = row1.createCell(1);
		cell34.setCellValue(new HSSFRichTextString("Nro. Socio"));
		cell34.setCellStyle(styleHeaderEnca3);

		HSSFCell cell35 = row1.createCell(2);
		cell35.setCellValue(new HSSFRichTextString("Inte"));
		cell35.setCellStyle(styleHeaderEnca3);
		
		HSSFCell cell36 = row1.createCell(3);
		cell36.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell36.setCellStyle(styleHeaderEnca3);
		
		HSSFCell cell37 = row1.createCell(4);
		cell37.setCellValue(new HSSFRichTextString("Seccional"));
		cell37.setCellStyle(styleHeaderEnca3);
		
		HSSFCell cell38 = row1.createCell(5);
		cell38.setCellValue(new HSSFRichTextString("Titular"));
		cell38.setCellStyle(styleHeaderEnca3);
		
		HSSFCell cell39 = row1.createCell(5);
		cell39.setCellValue(new HSSFRichTextString("Empresa"));
		cell39.setCellStyle(styleHeaderEnca3);

		return index;
	}

}

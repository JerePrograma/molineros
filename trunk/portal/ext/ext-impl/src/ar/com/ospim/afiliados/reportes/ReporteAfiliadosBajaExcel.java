package ar.com.ospim.afiliados.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import ar.com.ospim.afiliados.beans.Baja;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.reportes.action.ReporteReintegrosExcel;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteAfiliadosBajaExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteReintegrosExcel.class);

	public static HSSFWorkbook generaReporteAfiliadosBajaExcel(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}

		List<Baja> baja = new ArrayList<Baja>();
		try {
			baja = ReportesAfiliadoServiceUtil.getReporteListadoBajas(
					fechaDesde, fechaHasta);
		} catch (Exception e) {
			_log.error("Error al generar reporte de reintegros totales", e);
			return null;
		}
		return generarReporte(baja);
	}

	private static HSSFWorkbook generarReporte(List<Baja> list) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Hoja 1");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("Cuil"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("DNI"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("Parentesco"));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("Apellido"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(4);
		cell4H.setCellValue(new HSSFRichTextString("Nombre"));
		cell4H.setCellStyle(styleBold);

		HSSFCell cell5H = rowHeader.createCell(5);
		cell5H.setCellValue(new HSSFRichTextString("alta_fecha"));
		cell5H.setCellStyle(styleBold);

		HSSFCell cell6H = rowHeader.createCell(6);
		cell6H.setCellValue(new HSSFRichTextString("baja_fecha"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(7);
		cell7H.setCellValue(new HSSFRichTextString("Plan"));
		cell7H.setCellStyle(styleBold);

		HSSFCell cell8H = rowHeader.createCell(8);
		cell8H.setCellValue(new HSSFRichTextString("tipo de baja"));
		cell8H.setCellStyle(styleBold);

		BigDecimal total = new BigDecimal("0");
		for (Baja baja : list) {
			index++;
			total = total.add(crearHeader(sheet, index, baja, baja.getCuil(),
					styleBold, styleAll, styleDate));
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		index++;
		sheet.createRow(index);

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		return wb;
	}

	@SuppressWarnings("deprecation")
	private static BigDecimal crearHeader(HSSFSheet sheet, int index,
			Baja baja, String cuil, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate) {

		BigDecimal importe = BigDecimal.ZERO;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(baja.getCuil()));
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(baja.getDni()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(baja.getParentesco()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(baja.getApellido()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(baja.getNombre()));
		cell4.setCellStyle(styleAll);

		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(baja.getAlta_fecha());
		cell5.setCellStyle(styleDate);

		HSSFCell cell6 = rowHeader.createCell(6);
		cell6.setCellValue(baja.getBaja_fecha());
		cell6.setCellStyle(styleDate);

		HSSFCell cell7 = rowHeader.createCell(7);
		cell7.setCellValue(new HSSFRichTextString(baja.getUltimo_plan()
				.getDescripcion()));
		cell7.setCellStyle(styleAll);

		HSSFCell cell8 = rowHeader.createCell(8);
		cell8.setCellValue(new HSSFRichTextString(baja.getTipo_de_baja()));
		cell8.setCellStyle(styleAll);

		return importe;
	}
}
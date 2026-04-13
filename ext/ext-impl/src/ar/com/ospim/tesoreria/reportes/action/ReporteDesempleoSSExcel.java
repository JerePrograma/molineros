package ar.com.ospim.tesoreria.reportes.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.DesempleoSS;
import ar.com.ospim.tesoreria.service.ReporteDesempleoSServiceImpl;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteDesempleoSSExcel extends ReporteXLS {

	private static Log _log = LogFactoryUtil
			.getLog(ReporteDesempleoSSExcel.class);
	
	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");		

		try {
			String idTerc = ParamUtil.getString(req, "idTerc");
			boolean registrar = ParamUtil.getBoolean(req, "registrar");
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			ReporteDesempleoSServiceImpl repo = new ReporteDesempleoSServiceImpl();
			List<DesempleoSS> libro = repo.getReporteDesempleoSS(
					idTerc, fechaIni, registrar);
			
			HSSFWorkbook reporte = new HSSFWorkbook();
			
			reporte = generarReporteDesempleo(reporte, fechaIni, libro);
			

			return reporte;
		} catch (Exception e) {
			_log.error("Error al generar listado estado comprobantes", e);
			return null;
		}
	}
	
	private static HSSFWorkbook generarReporteDesempleo(
			HSSFWorkbook wb, Date fechaIni,
			List<DesempleoSS> libro) {

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb
				.createSheet("Desempleo OS");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer(
				"Listado de desempleo - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		// cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		createHeader(wb, sheet, styleHeader);

		BigDecimal total = BigDecimal.ZERO;
		
		int i = 3;
		
		


		for (DesempleoSS l : libro) {
			int j=0;
			HSSFRow row = sheet.createRow(i);
			
			HSSFCell cell2 = row.createCell(j++);
			cell2.setCellValue(new HSSFRichTextString(l.getCuil()));
			cell2.setCellStyle(styleAll);

			HSSFCell cell3 = row.createCell(j++);
			cell3.setCellValue(new HSSFRichTextString(l.getCuil_titular()));
			cell3.setCellStyle(styleAll);
			
			HSSFCell cell4 = row.createCell(j++);
			cell4.setCellValue(new HSSFRichTextString(l.getDocu_numero()));
			cell4.setCellStyle(styleAll);

			HSSFCell cell5 = row.createCell(j++);
			cell5.setCellValue(new HSSFRichTextString(l.getNombreApe()));
			cell5.setCellStyle(styleAll);
						
			SimpleDateFormat sdf1=new SimpleDateFormat("MM/yyyy");
			SimpleDateFormat sdf=new SimpleDateFormat("MM/yyyy");
			HSSFCell cell6 = row.createCell(j++);
			cell6.setCellValue(new HSSFRichTextString(sdf1.format(l.getFecha_nac())));
			cell6.setCellStyle(styleAll);
			
			HSSFCell cell7 = row.createCell(j++);
			cell7.setCellValue(new HSSFRichTextString(l.getSexo()));
			cell7.setCellStyle(styleAll);
			
			HSSFCell cell8 = row.createCell(j++);
			cell8.setCellValue(new HSSFRichTextString(sdf.format(l.getFecha_vig())));
			cell8.setCellStyle(styleAll);
			
			HSSFCell cell9 = row.createCell(j++);
			cell9.setCellValue(new HSSFRichTextString(sdf.format(l.getAcredita())));
			cell9.setCellStyle(styleAll);

			BigDecimal importe=l.getNeto();			
			HSSFCell cell10 = row.createCell(j++);
			cell10.setCellValue(importe.doubleValue());
			cell10.setCellStyle(styleMoney);

			i++;
		}
		
		sheet.setColumnWidth(0, 10360);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		return wb;
	}




	private static void createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(2);

		int i=0;
		HSSFCell cell2 = row.createCell(i++);
		cell2.setCellValue(new HSSFRichTextString("CUIL"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(i++);
		cell3.setCellValue(new HSSFRichTextString("CUIL Titular"));		
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(i++);
		cell4.setCellValue(new HSSFRichTextString("Doc. Nro."));
		cell4.setCellStyle(styleHeader);
		
		HSSFCell cell5 = row.createCell(i++);
		cell5.setCellValue(new HSSFRichTextString("Nombre"));
		cell5.setCellStyle(styleHeader);
		
		HSSFCell cell6 = row.createCell(i++);
		cell6.setCellValue(new HSSFRichTextString("Fecha. Nac"));
		cell6.setCellStyle(styleHeader);
		
		HSSFCell cell7 = row.createCell(i++);
		cell7.setCellValue(new HSSFRichTextString("Sexo"));
		cell7.setCellStyle(styleHeader);
		
		HSSFCell cell8 = row.createCell(i++);
		cell8.setCellValue(new HSSFRichTextString("Fecha Vig."));
		cell8.setCellStyle(styleHeader);
		
		HSSFCell cell9 = row.createCell(i++);
		cell9.setCellValue(new HSSFRichTextString("Acredita"));
		cell9.setCellStyle(styleHeader);
		
		HSSFCell cell10 = row.createCell(i++);
		cell10.setCellValue(new HSSFRichTextString("Neto"));
		cell10.setCellStyle(styleHeader);
		
		//wb.setRepeatingRowsAndColumns(0, 0, 10, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
	}

}

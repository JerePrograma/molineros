package ar.com.ospim.tesoreria.reportes;

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
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.LiquidacionActaConvenio;
import ar.com.ospim.tesoreria.services.LiquidaActaConveniosServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteLiqActaConvenio extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteLiqActaConvenio.class);

	public static HSSFWorkbook generaReporteLiqActaConvenio(HttpServletRequest req,
			HttpServletResponse res) {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleAllWithHeader = getStyleAllWithBorder(wb);		
		HSSFCellStyle styleHeaderWithBorder = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);

		try {

			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("dd/MM/yyyy");
			String fechaLiq = ParamUtil.getString(req,"fechaLiq");
			Date fechaLiqDate = null;
			try {
				fechaLiqDate = formatoDePeriodos.parse(fechaLiq);
			} catch (Exception e) {
				fechaLiqDate = null;
			}
			

			List<LiquidacionActaConvenio> liqActaConvenio = null;
			liqActaConvenio = LiquidaActaConveniosServiceUtil.getLiqActaConvenioFechaLiq(fechaLiqDate);

			HSSFSheet sheet = wb.createSheet("Hoja 1");
			int index = 1;
			

			crearHeaderLiqActaConvenio(wb, sheet, styleHeaderWithBorder, fechaLiq);
			for (LiquidacionActaConvenio repo : liqActaConvenio) {
				++index;
				crearInfoLiqActaConvenio(sheet, repo, index,
						styleDate, styleAllWithHeader, styleMoney);
			}
			sheet.autoSizeColumn((short) 0);
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
		
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return wb;
	}
	
	private static void crearInfoLiqActaConvenio(HSSFSheet sheet, LiquidacionActaConvenio liq, int index, HSSFCellStyle styleDate,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoney) {

		HSSFRow row = sheet.createRow(index);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(liq.getFechaLiqAsString()));
		cell.setCellStyle(styleAll);
		
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(liq.getNumeroActa()));		
		cell1.setCellStyle(styleAll);
		
		HSSFCell cell2 = row.createCell(2);		
		cell2.setCellValue(new HSSFRichTextString(liq.getNumeroRecibo()));		
		cell2.setCellStyle(styleAll);
				
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(liq.getCuit()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(liq.getRazonSoc()));
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(liq.getCuil()));
		cell5.setCellStyle(styleAll);
		
		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString(liq.getAfiliado()));
		cell6.setCellStyle(styleAll);
		
		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString(liq.getPeriodoAsString()));
		cell7.setCellStyle(styleAll);
		
		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(liq.getRemunera().doubleValue());
		cell8.setCellStyle(styleMoney);
		
		HSSFCell cell9 = row.createCell(9);
		cell9.setCellValue(liq.getTotalTerce().doubleValue());
		cell9.setCellStyle(styleMoney);
		
		HSSFCell cell10 = row.createCell(10);
		cell10.setCellValue(new HSSFRichTextString(liq.getTercerizadora()));
		cell10.setCellStyle(styleAll);
		
		
	}
	
	private static void crearHeaderLiqActaConvenio(HSSFWorkbook wb, HSSFSheet sheet, HSSFCellStyle styleHeader, String fechaLiq) {
		HSSFRow rowHeader = sheet.createRow(0);
		HSSFCell cellHeader = rowHeader.createCell(0);
		cellHeader.setCellValue(new HSSFRichTextString("Reporte de Liq. de Actas y Convenios al "+fechaLiq));
		cellHeader.setCellStyle(styleHeader);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));  
		
		HSSFRow row = sheet.createRow(1);
		
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Fecha Obligación"));
		cell.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(1);
		cellAcreed.setCellValue(new HSSFRichTextString("Número Acta/Convenio"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(2);
		cellRaz.setCellValue(new HSSFRichTextString("Recibos"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("CUIT"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Empresa"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Cuil"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Afiliado"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Periodo"));
		cell7.setCellStyle(styleHeader);

		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(new HSSFRichTextString("Remuneración"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(9);
		cell9.setCellValue(new HSSFRichTextString("Derivado a Terc."));
		cell9.setCellStyle(styleHeader);
		
		HSSFCell cell10 = row.createCell(10);
		cell10.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell10.setCellStyle(styleHeader);


	}
	
}

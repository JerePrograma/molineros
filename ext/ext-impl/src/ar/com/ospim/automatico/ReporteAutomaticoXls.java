package ar.com.ospim.automatico;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.beans.ResultadoReporteAutomatico;
import ar.com.ospim.automatico.beans.ResultadoReporteAutomatico.ItemResultadoReporteAutomatico;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

public class ReporteAutomaticoXls extends ReporteXLS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1752331265619138488L;

	public static HSSFWorkbook obtenerXls(ResultadoReporteAutomatico res,
			ReporteAutomatico ra) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeader = getStyleHeader(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleNum = getStyleInt(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		StringBuilder headerRight = new StringBuilder();
		headerRight.append("N° de hoja: " + HSSFHeader.page());
		headerRight.append(" de " + HSSFHeader.numPages());
		headerRight.append("\n");
		headerRight.append(DateUtils.format(new Date(), DateUtils.LONG_SEC));
		headerRight.append("\n");
		sheet.getHeader().setRight(headerRight.toString());
		int i = 0;
		i = generarHeaderPPal(wb, sheet, i, styleHeader, res, ra);
		i = generarHeaderSecundario(wb, sheet, i, styleHeader, res, ra);
		//wb.setRepeatingRowsAndColumns(0, 0, res.getNombres().size() - 1, i - 1,
		//		i - 1);
				
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}		
				
		for (ItemResultadoReporteAutomatico repo : res.getItems()) {
			i = generarDatos(sheet, i, repo, styleAll, styleDate, styleMoney,
					styleNum);
		}

		for (int j = 0; j < res.getNombres().size(); j++) {
			sheet.autoSizeColumn((short) j);
		}
        if (i == 2) {  //Si el excel viene sin datos nuleamos la salida para que no envie el mail
        	wb = null;
        }
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ItemResultadoReporteAutomatico repo, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate, HSSFCellStyle styleMoney,
			HSSFCellStyle styleNum) {
		HSSFRow row = sheet.createRow(i);

		for (int j = 0; j < repo.getObjects().size(); j++) {
			Object obj = repo.getObjects().get(j);
			HSSFCell cell = row.createCell(j);
			crearCelda(cell, obj, styleAll, styleDate, styleMoney, styleNum);
		}

		return ++i;
	}

	private static void crearCelda(HSSFCell cell, Object obj,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNum) {
		if (obj instanceof Integer) {
			cell.setCellValue(Double.valueOf(((Integer) obj)).intValue());
			cell.setCellStyle(styleNum);
		} else if (obj instanceof String) {
			cell.setCellValue(new HSSFRichTextString((String) obj));
			cell.setCellStyle(styleAll);
		} else if (obj instanceof Date) {
			cell.setCellValue((Date) obj);
			cell.setCellStyle(styleDate);
		} else if (obj instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal) obj).doubleValue());
			cell.setCellStyle(styleMoney);
		}
	}

	private static int generarHeaderSecundario(HSSFWorkbook wb,
			HSSFSheet sheet, int i, HSSFCellStyle styleHeader,
			ResultadoReporteAutomatico res, ReporteAutomatico ra) {
		HSSFRow rowTitulo = sheet.createRow(i);

		for (int j = 0; j < res.getNombres().size(); j++) {
			HSSFCell cell = rowTitulo.createCell(j);
			cell.setCellValue(new HSSFRichTextString(res.getNombres().get(j)));
			cell.setCellStyle(styleHeader);
		}

		return ++i;
	}

	private static int generarHeaderPPal(HSSFWorkbook wb, HSSFSheet sheet,
			int i, HSSFCellStyle styleHeader, ResultadoReporteAutomatico res,
			ReporteAutomatico ra) {

		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString(ra.getTitulo()));
		cell.setCellStyle(styleHeader);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, res.getNombres()
				.size()));
		return ++i;
	}

}

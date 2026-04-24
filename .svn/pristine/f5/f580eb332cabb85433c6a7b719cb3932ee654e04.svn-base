package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.afiliados.reportes.beans.ReporteLegajosCred;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.reportes.action.ReporteReintegrosExcel;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteLegajosProcesadosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteReintegrosExcel.class);

	public static HSSFWorkbook generaReporteLegajosProcesados(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		int nroLote=ParamUtil.getInteger(renderRequest, "nro_lote",0);
		String periodo=ParamUtil.getString(renderRequest, "periodo","/"); //viene como mes/anio
		
		List<ReporteLegajosCred> reporte = new ArrayList<ReporteLegajosCred>();
		
		try {
			reporte = ReportesAfiliadoServiceUtil.getReporteLegajosCredEmitidasHistorico(nroLote);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte de legajos procesados", e);
			return null;
		}
		
		return generarReporte(reporte, periodo);
	}

	private static HSSFWorkbook generarReporte(List<ReporteLegajosCred> list, String periodo) {
				
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
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		int index = 0;
		int indexRow = 0;
		HSSFRow rowHeaderInicio = sheet.createRow(indexRow++);
		HSSFCell cell0Hi = rowHeaderInicio.createCell(0);
		cell0Hi.setCellValue(new HSSFRichTextString("Reporte de legajos/credenciales emitidas para período "+periodo));
		cell0Hi.setCellStyle(styleHeaderEnca);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
		
		indexRow++;
		HSSFRow rowHeader= sheet.createRow(indexRow++);

		HSSFCell cell0H = rowHeader.createCell(index++);
		cell0H.setCellValue(new HSSFRichTextString("Fecha Recepción"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(index++);
		cell1H.setCellValue(new HSSFRichTextString("Nro.Correspondencia"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(index++);
		cell2H.setCellValue(new HSSFRichTextString("Seccional"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(index++);
		cell3H.setCellValue(new HSSFRichTextString("CUIL Titular"));
		cell3H.setCellStyle(styleBold);
		
		HSSFCell cell4H = rowHeader.createCell(index++);
		cell4H.setCellValue(new HSSFRichTextString("Inte"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell5H = rowHeader.createCell(index++);
		cell5H.setCellValue(new HSSFRichTextString("Apellido"));
		cell5H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(index++);
		cell6H.setCellValue(new HSSFRichTextString("Nombre"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell7H = rowHeader.createCell(index++);
		cell7H.setCellValue(new HSSFRichTextString("Fecha Impr.Cred."));
		cell7H.setCellStyle(styleBold);

		HSSFCell cell8H = rowHeader.createCell(index++);
		cell8H.setCellValue(new HSSFRichTextString("Plan"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(index++);
		cell9H.setCellValue(new HSSFRichTextString("Duración Proceso"));
		cell9H.setCellStyle(styleBold);

		
		for (ReporteLegajosCred repor : list) {			
			crearInfo(sheet, indexRow, repor, styleBold, styleAll, styleDate);
			indexRow++;
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
		
		return wb;
	}
	
	private static void crearInfo(HSSFSheet sheet, int index,
			ReporteLegajosCred legajoDet, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

		HSSFRow rowHeader = sheet.createRow(index);
		int nroColumna = 0;
		
		HSSFCell cell0 = rowHeader.createCell(nroColumna++);
		cell0.setCellValue(new HSSFRichTextString(sdf.format(legajoDet.getFechaIngreso())));
		cell0.setCellStyle(styleDate);

		HSSFCell cell1 = rowHeader.createCell(nroColumna++);
		cell1.setCellValue(new HSSFRichTextString(String.valueOf(legajoDet.getIdCorrespondencia())));
		cell1.setCellStyle(styleAll);
		
		HSSFCell cell2 = rowHeader.createCell(nroColumna++);
		cell2.setCellValue(new HSSFRichTextString(legajoDet.getSeccional()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(nroColumna++);
		cell3.setCellValue(new HSSFRichTextString(legajoDet.getCuilTitular()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader.createCell(nroColumna++);
		cell4.setCellValue(new HSSFRichTextString(String.valueOf(legajoDet.getInte())));
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell5 = rowHeader.createCell(nroColumna++);
		cell5.setCellValue(new HSSFRichTextString(legajoDet.getApellido()));
		cell5.setCellStyle(styleAll);
		
		HSSFCell cell6 = rowHeader.createCell(nroColumna++);
		cell6.setCellValue(new HSSFRichTextString(legajoDet.getNombre()));
		cell6.setCellStyle(styleAll);

		HSSFCell cell7 = rowHeader.createCell(nroColumna++);
		cell7.setCellValue(new HSSFRichTextString(sdf.format(legajoDet.getFechaImpresionCred())));
		cell7.setCellStyle(styleAll);
		
		HSSFCell cell8 = rowHeader.createCell(nroColumna++);
		cell8.setCellValue(new HSSFRichTextString(legajoDet.getPlan()));
		cell8.setCellStyle(styleAll);
		
		HSSFCell cell9 = rowHeader.createCell(nroColumna++);
		cell9.setCellValue(new HSSFRichTextString(String.valueOf(
				DateUtils.calculaDiasHabilesEntreFechas(legajoDet.getFechaIngreso(),
						legajoDet.getFechaImpresionCred(), true, null) )));
		cell9.setCellStyle(styleAll);

	}
}
package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.beans.HistoricoMovimientoAfiliado;
import ar.com.ospim.afiliados.services.HistoricoMovimientoServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.reportes.action.ReporteReintegrosExcel;

public class ReporteHistoricoMovimientosAfiliadoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteReintegrosExcel.class);

	public static HSSFWorkbook generaReporteHistoricoMovimientosAfiliado(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		String cuilTitular=ParamUtil.getString(renderRequest, "cuil_titular",""); 
		
		String fechaDesdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
		Calendar fechaDesdeCalendar = Calendar.getInstance();
		
		String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
		
		Calendar fechaHastaCalendar = Calendar.getInstance();
		
		fechaDesdeCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaDesdeDia));
		fechaDesdeCalendar.set(Calendar.MONTH, Integer.parseInt(fechaDesdeMes));
		fechaDesdeCalendar.set(Calendar.YEAR, Integer.parseInt(fechaDesdeAnio));					
		
		fechaHastaCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaHastaDia));
		fechaHastaCalendar.set(Calendar.MONTH, Integer.parseInt(fechaHastaMes));
		fechaHastaCalendar.set(Calendar.YEAR, Integer.parseInt(fechaHastaAnio));					
		
		List<HistoricoMovimientoAfiliado> modificaciones = null;
		
		try {
			try {
				modificaciones =  HistoricoMovimientoServiceUtil.buscarHistorico(cuilTitular, fechaDesdeCalendar.getTime() , fechaHastaCalendar.getTime() );
			} catch (Exception e) {
				_log.error(e);
			} 
			
		} catch (Exception e) {
			_log.error("Error al generar reporte de historico de movimientos de afiliados", e);
			return null;
		}
		
		return generarReporte(modificaciones, cuilTitular, fechaDesdeCalendar.getTime(), fechaHastaCalendar.getTime());
	}
	
	public static HSSFWorkbook generaReporteHistoricoMovimientosAfiliado(String cuilTitular, Date fechaDesdeCalendar, Date fechaHastaCalendar) {
		
		List<HistoricoMovimientoAfiliado> modificaciones = null;
		
		try {
			try {
				modificaciones =  HistoricoMovimientoServiceUtil.buscarHistorico(cuilTitular, fechaDesdeCalendar , fechaHastaCalendar);
			} catch (Exception e) {
				_log.error(e);
			} 
			
		} catch (Exception e) {
			_log.error("Error al generar reporte de historico de movimientos de afiliados", e);
			return null;
		}
		
		return generarReporte(modificaciones, cuilTitular, fechaDesdeCalendar, fechaHastaCalendar);
	}

	private static HSSFWorkbook generarReporte(List<HistoricoMovimientoAfiliado> list, String cuilTitular, Date fechaDesde, Date fechaHasta) {
				
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
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
		cell0Hi.setCellValue(new HSSFRichTextString("Reporte de movimientos del Grupo Fliar: "
					+cuilTitular + " desde el " + sdf.format(fechaDesde) + " al " + sdf.format(fechaHasta)));
		cell0Hi.setCellStyle(styleHeaderEnca);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
		
		indexRow++;
		HSSFRow rowHeader= sheet.createRow(indexRow++);
		
		HSSFCell cell0H = rowHeader.createCell(index++);
		cell0H.setCellValue(new HSSFRichTextString("CUIL Titular"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(index++);
		cell1H.setCellValue(new HSSFRichTextString("Inte"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(index++);
		cell2H.setCellValue(new HSSFRichTextString("Parentesco"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(index++);
		cell3H.setCellValue(new HSSFRichTextString("Nro. Documento"));
		cell3H.setCellStyle(styleBold);
		
		HSSFCell cell4H = rowHeader.createCell(index++);
		cell4H.setCellValue(new HSSFRichTextString("Apellido"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell5H = rowHeader.createCell(index++);
		cell5H.setCellValue(new HSSFRichTextString("Nombre"));
		cell5H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(index++);
		cell6H.setCellValue(new HSSFRichTextString("Cambio"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell7H = rowHeader.createCell(index++);
		cell7H.setCellValue(new HSSFRichTextString("Cambio Anterior"));
		cell7H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(index++);
		cell8H.setCellValue(new HSSFRichTextString("Real"));
		cell8H.setCellStyle(styleBold);

		HSSFCell cell9H = rowHeader.createCell(index++);
		cell9H.setCellValue(new HSSFRichTextString("Usuario"));
		cell9H.setCellStyle(styleBold);
		
		HSSFCell cell10H = rowHeader.createCell(index++);
		cell10H.setCellValue(new HSSFRichTextString("Fecha Modificación"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(index++);
		cell11H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell11H.setCellStyle(styleBold);

		
		for (HistoricoMovimientoAfiliado repor : list) {			
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
		sheet.autoSizeColumn((short) 10);
		sheet.autoSizeColumn((short) 11);
		
		return wb;
	}
	
	private static void crearInfo(HSSFSheet sheet, int index,
			HistoricoMovimientoAfiliado movHisto, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

		HSSFRow rowHeader = sheet.createRow(index);
		int nroColumna = 0;

		HSSFCell cell0 = rowHeader.createCell(nroColumna++);
		cell0.setCellValue(new HSSFRichTextString(movHisto.getCuil_titularMasked() ));
		cell0.setCellStyle(styleDate);

		HSSFCell cell1 = rowHeader.createCell(nroColumna++);
		cell1.setCellValue(new HSSFRichTextString(movHisto.getInteAsString()));
		cell1.setCellStyle(styleAll);
		
		HSSFCell cell2 = rowHeader.createCell(nroColumna++);
		cell2.setCellValue(new HSSFRichTextString(movHisto.getParentesco()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(nroColumna++);
		cell3.setCellValue(new HSSFRichTextString(movHisto.getNro_documento()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader.createCell(nroColumna++);
		cell4.setCellValue(new HSSFRichTextString(movHisto.getApellido()));
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell5 = rowHeader.createCell(nroColumna++);
		cell5.setCellValue(new HSSFRichTextString(movHisto.getNombre()));
		cell5.setCellStyle(styleAll);

		HSSFCell cell6 = rowHeader.createCell(nroColumna++);
		cell6.setCellValue(new HSSFRichTextString(movHisto.getModificacion()));
		cell6.setCellStyle(styleAll);
		
		HSSFCell cell7 = rowHeader.createCell(nroColumna++);
		cell7.setCellValue(new HSSFRichTextString(movHisto.getValor_anterior()));
		cell7.setCellStyle(styleAll);

		HSSFCell cell8 = rowHeader.createCell(nroColumna++);
		cell8.setCellValue(new HSSFRichTextString(movHisto.getValor_actual()));
		cell8.setCellStyle(styleAll);
		
		HSSFCell cell9 = rowHeader.createCell(nroColumna++);
		cell9.setCellValue(new HSSFRichTextString(movHisto.getUsuario()));
		cell9.setCellStyle(styleAll);
		
		HSSFCell cell10 = rowHeader.createCell(nroColumna++);
		cell10.setCellValue(new HSSFRichTextString(movHisto.getFecha_modificacionAsString()));
		cell10.setCellStyle(styleAll);
		
		HSSFCell cell11 = rowHeader.createCell(nroColumna++);
		cell11.setCellValue(new HSSFRichTextString("1".equals(movHisto.getDiscapacitado())?"SI":""));
		cell11.setCellStyle(styleAll);

	}
}
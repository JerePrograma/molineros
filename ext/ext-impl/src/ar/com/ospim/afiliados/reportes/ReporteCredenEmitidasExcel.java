package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import ar.com.ospim.afiliados.reportes.beans.ReporteCredenResult;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.reportes.action.ReporteReintegrosExcel;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteCredenEmitidasExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteReintegrosExcel.class);

	public static HSSFWorkbook generaReporteCredenEmitidas(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Calendar fechaDesdeCalendar = Calendar.getInstance();
		
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Calendar fechaHastaCalendar = Calendar.getInstance();
		
		
		String tipoInformeString=ParamUtil.getString(renderRequest, "tipoInforme",null);
		
		
		boolean informar=ParamUtil.getBoolean(renderRequest,"informar");
		boolean ultimo=ParamUtil.getBoolean(renderRequest,"ultimo");
		List<ReporteCredenResult> reporte = new ArrayList<ReporteCredenResult>();
		Date fechaDesde=null;
		Date fechaHasta=null;
		int idReporte=0;
		try {
			if(null==tipoInformeString){				
				if(!ultimo){
					fechaDesdeCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaDesdeDia));
					fechaDesdeCalendar.set(Calendar.MONTH, Integer.parseInt(fechaDesdeMes));
					fechaDesdeCalendar.set(Calendar.YEAR, Integer.parseInt(fechaDesdeAnio));					
					fechaDesde=fechaDesdeCalendar.getTime();
					
					fechaHastaCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaHastaDia));
					fechaHastaCalendar.set(Calendar.MONTH, Integer.parseInt(fechaHastaMes));
					fechaHastaCalendar.set(Calendar.YEAR, Integer.parseInt(fechaHastaAnio));					
					fechaHasta=fechaHastaCalendar.getTime();
				}
				reporte = ReportesAfiliadoServiceUtil.getReporteCredencialesEmitidas(fechaDesde, fechaHasta, informar);
			}else{				
				idReporte=ParamUtil.getInteger(renderRequest, "id_reporte");
				reporte = ReportesAfiliadoServiceUtil.getReporteCredencialesEmitidasHistorico(idReporte);
			}
		} catch (Exception e) {
			_log.error("Error al generar reporte de reintegros totales", e);
			return null;
		}
		
		return generarReporte(reporte, fechaDesde, idReporte);
	}

	private static HSSFWorkbook generarReporte(List<ReporteCredenResult> list, Date fechaDesde, int id_reporte) {
				
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");

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
		int indexRow = 0;
		HSSFRow rowHeaderInicio = sheet.createRow(indexRow++);
		HSSFCell cell0Hi = rowHeaderInicio.createCell(0);
		String desde=fechaDesde!=null?" desde el día "+sdf.format(fechaDesde):id_reporte>0?".":" desde el día posterior al último informe";
		cell0Hi.setCellValue(new HSSFRichTextString("Reporte de credenciales emitidas"+desde));
		cell0Hi.setCellStyle(styleBold);
		
		indexRow++;
		HSSFRow rowHeader= sheet.createRow(indexRow++);

		HSSFCell cell0H = rowHeader.createCell(index++);
		cell0H.setCellValue(new HSSFRichTextString("Fecha Alta"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(index++);
		cell1H.setCellValue(new HSSFRichTextString("Seccional"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(index++);
		cell2H.setCellValue(new HSSFRichTextString("Nombre"));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(index++);
		cell3H.setCellValue(new HSSFRichTextString("Apellido"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(index++);
		cell4H.setCellValue(new HSSFRichTextString("Plan"));
		cell4H.setCellStyle(styleBold);

		
		
		for (ReporteCredenResult repor : list) {			
			crearInfo(sheet, indexRow, repor, fechaDesde, styleBold, styleAll, styleDate);
			indexRow++;
		}
		
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		
		return wb;
	}
	
	private static void crearInfo(HSSFSheet sheet, int index,
			ReporteCredenResult repor, Date fechaDesde, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate) {
		
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(repor.getFechaAlta());
		cell0.setCellStyle(styleDate);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(repor.getSeccional()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(repor.getNombre()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(repor.getApellido()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(repor.getPlan()));
		cell4.setCellStyle(styleAll);

	}
}
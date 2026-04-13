package ar.com.ospim.autorizaciones.reportes.action;

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
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteAutorizacionesPmiExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteAutorizacionesPmiExcel.class);

	public static HSSFWorkbook generaReporteAutorizacionesPmi(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

		String fechaRecetaDia = ParamUtil.getString(renderRequest,"fechaRecetaDia");
		String fechaRecetaMes = ParamUtil.getString(renderRequest,"fechaRecetaMes");
		String fechaRecetaAnio = ParamUtil.getString(renderRequest,"fechaRecetaAnio");
		Date fechaReceta = null;
		try {
			fechaReceta = formatoDePeriodo.parse(fechaRecetaDia + "/"
					+ (Integer.parseInt(fechaRecetaMes) + 1) + "/"
					+ fechaRecetaAnio);
		} catch (Exception e) {
			fechaReceta = null;
		}
		
		int inte = ParamUtil.getInteger(renderRequest, "inte");
		String cuil = ParamUtil.getString(renderRequest, "cuil");
		int numReceta = ParamUtil.getInteger(renderRequest, "receta");

		List<AutorizacionesPmi> autorizaciones = new ArrayList<AutorizacionesPmi>();

		try {
			autorizaciones = AutorizacionesServiceUtil.getListaAutorizacionesPmi(fechaReceta, cuil, inte, numReceta);
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte autorizaciones recetas PMI",e);
			return null;
		}
		return generaReporteAutorizacionesPmi(autorizaciones,fechaReceta, cuil, inte, numReceta);
	}

	private static HSSFWorkbook generaReporteAutorizacionesPmi(
			List<AutorizacionesPmi> list, Date fechaReceta, String cuil, int inte, int numReceta) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Ficha");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber= getStyleNumber(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte Autorizaciones Bonos PMI: ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBold);
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("Nro. Autorización"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Nro. de Receta"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Nro. de Afiliado"));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Apellido"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Nombre"));
		cell4H.setCellStyle(styleBold);

		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Período"));
		cell5H.setCellStyle(styleBold);
	
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Seccional"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Observaciones"));
		cell7H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Fecha de Baja"));
		cell8H.setCellStyle(styleBold);
		index++;
		
		for(AutorizacionesPmi autorizaciones: list){
			index=crearDatosFicha(sheet, autorizaciones, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber );
		}

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

	private static int crearDatosFicha(HSSFSheet sheet,AutorizacionesPmi autorizaciones, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(autorizaciones.getId_autorizacion_string()));
		cell0.setCellStyle(styleNumber);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(autorizaciones.getReceta_string()));
		cell1.setCellStyle(styleNumber);
	
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(autorizaciones.getId_ospimToString()));
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(autorizaciones.getApellido()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(autorizaciones.getNombre()));
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(autorizaciones.getFecha_string()));
		cell5.setCellStyle(styleDate);
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(autorizaciones.getIdConcatNombSecc()));
		cell6.setCellStyle(styleNumber);
		
		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(autorizaciones.getObservaciones()));
		cell7.setCellStyle(styleAll);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(autorizaciones.getBaja_Fecha_string()));
		cell8.setCellStyle(styleDate);
		
		return index++;
	}	
}
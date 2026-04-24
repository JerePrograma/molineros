package ar.com.ospim.autorizaciones.reportes.action;

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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.SituacionMedicaExcel;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteSituacionMedica extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSituacionMedica.class);


	public static HSSFWorkbook generaReporteSituacionMedica (
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		
		
		// *************************************************************
		// carga de variables recibidas de la JSP 
		// *************************************************************
		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
				"dd/MM/yyyy");
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
			fechaHasta= formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta= null;
		}
		// resto de parametros de la busqueda
		int tipoSituMedica  = ParamUtil.getInteger(renderRequest, "situacionMedica", 0);
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);				 			
		String cuilTitular = ParamUtil.getString(renderRequest,"cuil_titular", null);
		boolean  esReporteCompleto= ParamUtil.getBoolean (renderRequest, "completo", false);
		
		
	    // *************************************************************		
		//  fin  de carga de variables de la JSP 
	    // *************************************************************		
		
		List<SituacionMedicaExcel> registrosSituacionMedica= new ArrayList<SituacionMedicaExcel>();

		try {
			registrosSituacionMedica= AutorizacionesServiceUtil.getListaSituMedica(fechaDesde , fechaHasta, inte , cuilTitular , tipoSituMedica );
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de situaciones medicas",e);
			return null;
		}
		return generaReporte(registrosSituacionMedica,esReporteCompleto);
	}

	private static HSSFWorkbook generaReporte(
			List<SituacionMedicaExcel> list , boolean reporteCompleto) {
		
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
				
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}		
		StringBuffer titulo1=new StringBuffer("Reporte Situaciones Médicas: ").append(sdf.format(hoy));
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
		cell0H.setCellValue(new HSSFRichTextString("Nro"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Apellido Nombre"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Cuil"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Inte"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Discapacitado"));
		cell4H.setCellStyle(styleBold);		

		if (reporteCompleto){ // añade datos extras de discapacidad 
			HSSFCell cell41H = rowHeader.createCell(++col);
			cell41H.setCellValue(new HSSFRichTextString("Detalle Discapacidades"));
			cell41H.setCellStyle(styleBold);		
			
			HSSFCell cell42H = rowHeader.createCell(++col);
			cell42H.setCellValue(new HSSFRichTextString("Telefono Contacto"));
			cell42H.setCellStyle(styleBold);		
		}
		
		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Situación Medica"));
		cell5H.setCellStyle(styleBold);
		
	
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Vigencia Desde"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Vigencia Hasta"));
		cell7H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Diagnostico"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Codigo Cie X"));
		cell9H.setCellStyle(styleBold);
		
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Descripción Cie X"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Dependencia"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Detalle Tipo Situacion Medica"));
		cell12H.setCellStyle(styleBold);
				
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Baja"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Fecha Baja"));
		cell14H.setCellStyle(styleBold);
				
		index++;
		
		for(SituacionMedicaExcel  autorizaciones: list){			
				index=crearDatosFicha(sheet, autorizaciones, index, styleAll,
						styleNumber, styleNumber, styleNumber, styleNumber, styleMoneyRight,reporteCompleto);
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
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		sheet.autoSizeColumn((short) 11);
		sheet.autoSizeColumn((short) 12);
		sheet.autoSizeColumn((short) 13);
		sheet.autoSizeColumn((short) 14);
		
		
		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,SituacionMedicaExcel   situacionMedica , 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, HSSFCellStyle styleMoneyRight , boolean reporteCompleto) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		try {
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString( String.valueOf(situacionMedica.getId_String()  )   ));
		cell0.setCellStyle(styleNumber);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(situacionMedica.getAfiliado().getApellidoNombre()));
		cell1.setCellStyle(styleNumber);
	
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(situacionMedica.getAfiliado().getCuil_titular() ));
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(String.valueOf(situacionMedica.getAfiliado().getInteAsString()  )));
		cell3.setCellStyle(styleNumber);
		
		HSSFCell cell4 = rowHeader.createCell(++col);		
		cell4.setCellValue(new HSSFRichTextString(situacionMedica.isDiscapacitado() ? "Si" : "No")  );
		cell4.setCellStyle(styleNumber);
		if (reporteCompleto) {
			HSSFCell cell41 = rowHeader.createCell(++col);		
			cell41.setCellValue(new HSSFRichTextString(String.valueOf(situacionMedica.getDetalleTextoDiscapacidades() )));
			
			HSSFCell cell42 = rowHeader.createCell(++col);		
			cell42.setCellValue(new HSSFRichTextString(String.valueOf(situacionMedica.getTelefonoContacto())));
			
		}
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(situacionMedica.getTipoSituMedica()   ));
		cell5.setCellStyle(styleNumber);
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		if(situacionMedica.getFechaVigen_Desde()==null ){
			cell6.setCellValue(new HSSFRichTextString(""));
		}else{
			cell6.setCellValue(new HSSFRichTextString(sdf.format(situacionMedica.getFechaVigen_Desde()) ));
		}
		cell6.setCellStyle(styleNumber);
		
		HSSFCell cell7 = rowHeader.createCell(++col);
		if(situacionMedica.getFechaVigen_Hasta()==null ){
			cell7.setCellValue(new HSSFRichTextString(""));
		}else{
			cell7.setCellValue(new HSSFRichTextString(sdf.format(situacionMedica.getFechaVigen_Hasta() ) ));
		}
		
		cell7.setCellStyle(styleNumber);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(situacionMedica.getDiagnostico() ));
		cell8.setCellStyle(styleNumber);
		
		HSSFCell cell9 = rowHeader.createCell(++col);		
		cell9.setCellValue(new HSSFRichTextString(situacionMedica.getCodigoCieDiez() ));
		cell9.setCellStyle(styleNumber);
		
		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(situacionMedica.getDiagnosticoCieDiez()));
		cell10.setCellStyle(styleNumber);

		HSSFCell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(new HSSFRichTextString(situacionMedica.getDependencia() ));
		cell11.setCellStyle(styleNumber);
		
		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(new HSSFRichTextString(situacionMedica.getDetalleTipoSituacionMedica()  ));
		cell12.setCellStyle(styleNumber);
	
		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(new HSSFRichTextString(situacionMedica.getDetalleBaja()   ));
		cell13.setCellStyle(styleNumber);
		
		HSSFCell cell14 = rowHeader.createCell(++col);
		if(situacionMedica.getFechaBaja()==null ){
			cell14.setCellValue(new HSSFRichTextString(""));
		}else{
			cell14.setCellValue(new HSSFRichTextString(sdf.format(situacionMedica.getFechaBaja() ) ));
		}
		cell14.setCellStyle(styleNumber);		
	    	    
		}catch(Exception e){
			_log.error("Error al generar Excel Situacion Medica en crearDatosFicha", e);			
		}
		
		return index++;
	}	

	
	
	

}

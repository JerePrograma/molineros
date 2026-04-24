package ar.com.ospim.farmaciaOspim.reportes.action;

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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReportePrestadoresInexistentesExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReportePrestadoresInexistentesExcel.class);


	public static HSSFWorkbook generaReporte (
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		// *************************************************************
		// carga de variables recibidas de la JSP 
		// *************************************************************
		
		List<Prestador> registros= new ArrayList<Prestador>();

		try {
			registros= TraeListasServiceUtil.getPrestodresInexistentesMedicacionEspecial() ;
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de prestadores inexistentes",e);
			return null;
		}
		return generaReporte(registros);
	}

	private static HSSFWorkbook generaReporte(
			List<Prestador> list) {
		
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
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte Prestadores Inexistentes: ").append(sdf.format(hoy));
	
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
		cell0H.setCellValue(new HSSFRichTextString("CUIT"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Descripción"));
		cell1H.setCellStyle(styleBold);
						
		index++;
		
		for(Prestador  autorizaciones: list){
			index=crearDatos(sheet, autorizaciones, index, styleAll,styleAll, styleAll, styleAll,  styleMoneyRight);
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
		sheet.autoSizeColumn((short) 15);
		sheet.autoSizeColumn((short) 16);
		sheet.autoSizeColumn((short) 17);
		sheet.autoSizeColumn((short) 18);
		sheet.autoSizeColumn((short) 19);
		sheet.autoSizeColumn((short) 20);
		sheet.autoSizeColumn((short) 21);
		sheet.autoSizeColumn((short) 22);
		sheet.autoSizeColumn((short) 23);
		sheet.autoSizeColumn((short) 24);
		sheet.autoSizeColumn((short) 25);
		sheet.autoSizeColumn((short) 26);
		sheet.autoSizeColumn((short) 27);
		sheet.autoSizeColumn((short) 28);
		sheet.autoSizeColumn((short) 29);
		sheet.autoSizeColumn((short) 30);
		sheet.autoSizeColumn((short) 31);
		sheet.autoSizeColumn((short) 32);
		sheet.autoSizeColumn((short) 33);
		sheet.autoSizeColumn((short) 34);
		sheet.autoSizeColumn((short) 35);
		sheet.autoSizeColumn((short) 36);
		sheet.autoSizeColumn((short) 37);
		sheet.autoSizeColumn((short) 38);
		sheet.autoSizeColumn((short) 39);
		sheet.autoSizeColumn((short) 40);
		
		
		
		return wb;
	}

	private static int crearDatos(HSSFSheet sheet,Prestador prestador , 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney,  HSSFCellStyle styleMoneyRight) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		try {
			
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(  prestador.getCuit()    ));
		cell0.setCellStyle(styleAll);
		cell0.setCellType(CellType.STRING);
		
		
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(prestador.getDescripcion()));
		cell2.setCellStyle(styleAll);
		
				
		
		}catch(Exception e){
			_log.error("Error al generar Excel Prestadores Inexistentes", e);			
		}
		
		return index++;
	}	

}

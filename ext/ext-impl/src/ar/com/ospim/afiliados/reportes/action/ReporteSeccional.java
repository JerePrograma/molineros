package ar.com.ospim.afiliados.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.afiliados.beans.SeccionalExcel;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteSeccional extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSeccional.class);


	public static HSSFWorkbook generaReporteSeccionales (
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		// *************************************************************
		// carga de variables recibidas de la JSP 
		// *************************************************************
		
		int  provinciaSeleccionada= ParamUtil.getInteger(renderRequest, "provinciaSeleccionada", 0);		
		
	    // *************************************************************		
		//  fin  de carga de variables de la JSP 
	    // *************************************************************		
		
		List<SeccionalExcel> registrosSeccionales = new ArrayList<SeccionalExcel>();
		List<SeccionalExcel> registrosSeccionalesContactos= new ArrayList<SeccionalExcel>();
		 

		try {
			registrosSeccionalesContactos = SeccionalServiceUtil.getListaSeccionalesContactos(provinciaSeleccionada);
			registrosSeccionales = SeccionalServiceUtil.getListaSeccionales(provinciaSeleccionada);
			
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de reclamos prestacionales",e);
			return null;
		}
		return generaReporte(registrosSeccionales , registrosSeccionalesContactos , (provinciaSeleccionada==0));
	}

	private static HSSFWorkbook generaReporte(
			List<SeccionalExcel> listSeccionales ,List<SeccionalExcel> listContactos ,  boolean todasLasProvincias ) {
		
		
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		String tituloHoja="";				
		HSSFWorkbook wb = new HSSFWorkbook();

		try {
		// primer hoja
		HSSFSheet sheet = wb.createSheet("Seccionales");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber= getStyleNumber(wb);
				
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);

		if (listSeccionales == null || listSeccionales.isEmpty()) {
			return wb;
		}
		
		if ( ! todasLasProvincias  ){
			tituloHoja= "Reporte Seccionales (" +  listSeccionales.get(0).getDetalleProvincia() + " )        ";	
		}else{
			tituloHoja="Reporte Seccionales        ";
		}
		StringBuffer titulo1=new StringBuffer(tituloHoja).append(sdf.format(hoy));
		
//Definimos el estilo de la cabecera
        HSSFCellStyle headerStyle = wb.createCellStyle();
       
//Estilo de la fuente
        HSSFFont hfont = wb.createFont();
        //hfont.setBoldweight(hfont.BOLDWEIGHT_BOLD);
        hfont.setBold(true);
        hfont.setFontHeightInPoints((short) 10);
        headerStyle.setFont(hfont);
//Alineacion Horizontal
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
				
		int index = 0;		
		int col = -1;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		cell0HA.setCellStyle(headerStyle);
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0H = rowHeader.createCell(++col);		
		cell0H.setCellValue(new HSSFRichTextString("Nro. Secc."));
		
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Descripción"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Vigencia"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Provincia"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Localidad"));
		cell4H.setCellStyle(styleBold);		

		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Calle"));
		cell5H.setCellStyle(styleBold);
		
	
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Número"));
		cell6H.setCellStyle(styleBold);				
		
		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Piso"));
		cell7H.setCellStyle(styleBold);				
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Depto."));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Cod. Postal"));
		cell9H.setCellStyle(styleBold);
		
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Contacto"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Destino correo postal"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Observaciones"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Email Contacto"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Horario de Atención"));
		cell14H.setCellStyle(styleBold);
		
		index++;
		
		for(SeccionalExcel  seccionales: listSeccionales ){
			index=crearDatosFichaSeccionales(sheet, seccionales, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber, styleMoneyRight);
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
		
		// segunda hoja		
		HSSFSheet sheet1 = wb.createSheet("Contactos Sec.");

		HSSFPrintSetup ps1 = sheet1.getPrintSetup();
		sheet1.setAutobreaks(true);
		ps1.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps1.setFitHeight((short) 0);
		ps1.setFitWidth((short) 1);

		if (listContactos == null || listContactos.isEmpty()) {
			return wb;
		}
		
		if ( ! todasLasProvincias  ){
			tituloHoja= "Reporte Contactos Seccionales  (" + listContactos.get(0).getDetalleProvincia() + " )        " ;	
		}else{
			tituloHoja="Reporte Contactos Seccionales        ";
		}
		StringBuffer titulo2=new StringBuffer(tituloHoja).append(sdf.format(hoy));		
		
//Estilo de la fuente
        HSSFFont hfont1 = wb.createFont();
      //  hfont1.setBoldweight(hfont.BOLDWEIGHT_BOLD);
        hfont.setBold(true);
        hfont1.setFontHeightInPoints((short) 10);
        headerStyle.setFont(hfont1);
//Alineacion Horizontal
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
		
		sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		
		index = 0;		
		col = -1;
		HSSFRow rowHeaderANT1 = sheet1.createRow(index);		
		HSSFCell cell0HA1 = rowHeaderANT1.createCell(0);
		
		cell0HA1.setCellValue(new HSSFRichTextString(titulo2.toString()));
		//cell0HA1.setCellStyle(styleBold);
		cell0HA1.setCellStyle(headerStyle);
				
		index++;
		HSSFRow rowHeader1 = sheet1.createRow(index);
		
		HSSFCell cell0H0 = rowHeader1.createCell(++col);		
		cell0H0.setCellValue(new HSSFRichTextString("Nro Seccional"));
		cell0H0.setCellStyle(styleBold);
		
		HSSFCell cell1H1 = rowHeader1.createCell(++col);
		cell1H1.setCellValue(new HSSFRichTextString("Descripción"));
		cell1H1.setCellStyle(styleBold);
		
		HSSFCell cell2H2 = rowHeader1.createCell(++col);
		cell2H2.setCellValue(new HSSFRichTextString("Tipo Contacto"));
		cell2H2.setCellStyle(styleBold);
		
		HSSFCell cell3H3 = rowHeader1.createCell(++col);
		cell3H3.setCellValue(new HSSFRichTextString("Numero"));
		cell3H3.setCellStyle(styleBold);

		HSSFCell cell4H4 = rowHeader1.createCell(++col);
		cell4H4.setCellValue(new HSSFRichTextString("Email"));
		cell4H4.setCellStyle(styleBold);		

		HSSFCell cell5H5 = rowHeader1.createCell(++col);
		cell5H5.setCellValue(new HSSFRichTextString("Cargo"));
		cell5H5.setCellStyle(styleBold);
		
	
		HSSFCell cell6H6 = rowHeader1.createCell(++col);
		cell6H6.setCellValue(new HSSFRichTextString("Apellido Nombre"));
		cell6H6.setCellStyle(styleBold);				
		
		HSSFCell cell7H7 = rowHeader1.createCell(++col);
		cell7H7.setCellValue(new HSSFRichTextString("Provincia Seccional"));
		cell7H7.setCellStyle(styleBold);				
		
		
		index++;
		
		for(SeccionalExcel  contactos: listContactos ){
			index=crearDatosFicha(sheet1, contactos, index, styleAll,
					styleNumber, styleNumber, styleNumber, styleNumber, styleMoneyRight);
		}

		index++;
		sheet1.createRow(index);
		
		sheet1.autoSizeColumn((short) 0);
		sheet1.autoSizeColumn((short) 1);
		sheet1.autoSizeColumn((short) 2);
		sheet1.autoSizeColumn((short) 3);
		sheet1.autoSizeColumn((short) 4);
		sheet1.autoSizeColumn((short) 5);
		sheet1.autoSizeColumn((short) 6);
		sheet1.autoSizeColumn((short) 7);
		
		
		} catch (Exception e) {
			_log.error(
					"Error al generar el reporte",e);
			return null;
		}
		
		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,SeccionalExcel   seccionales, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, HSSFCellStyle styleMoneyRight) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		try {
		
		
		HSSFCell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(new HSSFRichTextString( String.valueOf(seccionales.getId()   )   ));
		cell20.setCellStyle(styleNumber);
		
		HSSFCell cell21 = rowHeader.createCell(++col);
		cell21.setCellValue(new HSSFRichTextString(seccionales.getDescripcion() ));
		cell21.setCellStyle(styleNumber);
	
		HSSFCell cell22 = rowHeader.createCell(++col);
		cell22.setCellValue(new HSSFRichTextString(seccionales.getTipoContacto()  ));
		cell22.setCellStyle(styleNumber);
		
		HSSFCell cell23 = rowHeader.createCell(++col);
		if (seccionales.getTelefonoNumero().equals("")) {
			cell23.setCellValue(new HSSFRichTextString(""));
		}else{
			if (seccionales.getCodigoArea() ==null){
				cell23.setCellValue(new HSSFRichTextString(seccionales.getTelefonoNumero()));
			}else {
				cell23.setCellValue(new HSSFRichTextString("(" + seccionales.getCodigoArea() +")" + seccionales.getTelefonoNumero()));	
			}				
		}
		
		cell23.setCellStyle(styleNumber);
		
		HSSFCell cell24 = rowHeader.createCell(++col);
		cell24.setCellValue(new HSSFRichTextString(seccionales.getEmail() ));
		cell24.setCellStyle(styleNumber);
		
		HSSFCell cell25 = rowHeader.createCell(++col);
		cell25.setCellValue(new HSSFRichTextString(seccionales.getCargoDescripcion()));
		cell25.setCellStyle(styleNumber);

		HSSFCell cell26 = rowHeader.createCell(++col);
		cell26.setCellValue(new HSSFRichTextString(seccionales.getNombreContacto()  ));
		cell26.setCellStyle(styleNumber);
	
		HSSFCell cell27 = rowHeader.createCell(++col);
		cell27.setCellValue(new HSSFRichTextString(seccionales.getDetalleProvincia()   ));
		cell27.setCellStyle(styleNumber);
	
		
		
		}catch(Exception e){
			_log.error("Error al generar Excel de Seccionales al crear ficha de contactos", e);			
		}
		
		return index++;
	}	

	private static int crearDatosFichaSeccionales(HSSFSheet sheet,SeccionalExcel   seccionales, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, HSSFCellStyle styleMoneyRight) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		try {
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString( String.valueOf(seccionales.getId_seccional()   )   ));
		cell0.setCellStyle(styleNumber);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(seccionales.getDescripcion()  ));
		cell1.setCellStyle(styleNumber);
	
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(seccionales.getVigen_fecha()!= null ? DateUtils.format(seccionales.getVigen_fecha(), "dd/MM/yyyy") : "" ));
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString( seccionales.getDetalleProvincia() ));
		cell3.setCellStyle(styleNumber);
		
		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(new HSSFRichTextString(seccionales.getLocalidad() ));
		cell4.setCellStyle(styleNumber);
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString(seccionales.getCalle() ));
		cell5.setCellStyle(styleNumber);

		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(seccionales.getNumero()   ));
		cell6.setCellStyle(styleNumber);
	
		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(seccionales.getPiso()    ));
		cell7.setCellStyle(styleNumber);
	
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(seccionales.getDepto() ));
		cell8.setCellStyle(styleNumber);
		
		HSSFCell cell9 = rowHeader.createCell(++col);
		cell9.setCellValue(new HSSFRichTextString(seccionales.getPostal_codi()     ));
		cell9.setCellStyle(styleNumber);
		
		HSSFCell cell10= rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(seccionales.getNombreContacto() ));
		cell10.setCellStyle(styleNumber);
		
		HSSFCell cell11= rowHeader.createCell(++col);
		cell11.setCellValue(new HSSFRichTextString(seccionales.getDestino_corr()    ));
		cell11.setCellStyle(styleNumber);
		
		HSSFCell cell12= rowHeader.createCell(++col);
		cell12.setCellValue(new HSSFRichTextString(seccionales.getObservaciones() ));
		cell12.setCellStyle(styleNumber);
		
		HSSFCell cell13= rowHeader.createCell(++col);
		cell13.setCellValue(new HSSFRichTextString(seccionales.getContacto_mail() ));
		cell13.setCellStyle(styleNumber);
		
		HSSFCell cell14= rowHeader.createCell(++col);
		cell14.setCellValue(new HSSFRichTextString(seccionales.getHorarioAtencion()  ));
		cell14.setCellStyle(styleNumber);
		
		
		}catch(Exception e){
			_log.error("Error al generar Excel de Seccionales al crear ficha seccionales", e);			
		}
		
		return index++;
	}	

	
}

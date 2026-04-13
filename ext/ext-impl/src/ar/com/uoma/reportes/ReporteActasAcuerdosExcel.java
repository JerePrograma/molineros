package ar.com.uoma.reportes;

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
import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;
import ar.com.uoma.beans.ActasAcuerdos;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.util.ParamUtil;
import com.opensymphony.oscache.util.StringUtil;

public class ReporteActasAcuerdosExcel extends ReporteXLS {
	
	public static HSSFWorkbook generaReporteActasAcuerdos(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		String cuit=ParamUtil.getString(renderRequest, "cuit_entidad");
		String sucu=ParamUtil.getString(renderRequest, "sucursal_entidad");
		
		Calendar fechaIniCalendar=null;
		
		if(ParamUtil.getInteger(renderRequest, "fechaDesdeMes")>=0 && ParamUtil.getInteger(renderRequest, "fechaDesdeAnio")>0 ){
			fechaIniCalendar=Calendar.getInstance();
			fechaIniCalendar.set(Calendar.YEAR, ParamUtil.getInteger(renderRequest, "fechaDesdeAnio"));
			fechaIniCalendar.set(Calendar.MONTH, ParamUtil.getInteger(renderRequest, "fechaDesdeMes"));
			fechaIniCalendar.set(Calendar.DATE, ParamUtil.getInteger(renderRequest, "fechaDesdeDia"));
		}
		
		Calendar fechaFinCalendar=null;
		
		if(ParamUtil.getInteger(renderRequest, "fechaHastaMes")>=0 && ParamUtil.getInteger(renderRequest, "fechaHastaAnio")>0 ){
			fechaFinCalendar=Calendar.getInstance();
			fechaFinCalendar.set(Calendar.YEAR, ParamUtil.getInteger(renderRequest, "fechaHastaAnio"));
			fechaFinCalendar.set(Calendar.MONTH, ParamUtil.getInteger(renderRequest, "fechaHastaMes"));
			fechaFinCalendar.set(Calendar.DATE, ParamUtil.getInteger(renderRequest, "fechaHastaDia"));
		}
		Calendar fechaPagoCalendar=null;
		
		if(ParamUtil.getInteger(renderRequest, "fechaPagoMes")>=0 && ParamUtil.getInteger(renderRequest, "fechaPagoAnio")>0 ){
			fechaPagoCalendar=Calendar.getInstance();
			fechaPagoCalendar.set(Calendar.YEAR, ParamUtil.getInteger(renderRequest, "fechaPagoAnio"));
			fechaPagoCalendar.set(Calendar.MONTH, ParamUtil.getInteger(renderRequest, "fechaPagoMes"));
			fechaPagoCalendar.set(Calendar.DATE, ParamUtil.getInteger(renderRequest, "fechaPagoDia"));
		}
		
		int conSaldo=ParamUtil.getInteger(renderRequest, "conSaldo");
		
		String tipoReporte=ParamUtil.getString(renderRequest, "tipoReporte");
		List<ActasAcuerdos> actasAcuerdos=null;
		if(null!=tipoReporte && tipoReporte.trim().equals("ACTAS")){
			actasAcuerdos=ActaNoOSServiceUtil.reporteActasNoOS(cuit!=null&&cuit.trim().length()>0?cuit:null, sucu!=null&&sucu.trim().length()>0?sucu:null, 
																			   fechaIniCalendar!=null?fechaIniCalendar.getTime():null, 
																						   fechaFinCalendar!=null?fechaFinCalendar.getTime():null, 
																						   fechaPagoCalendar!=null?fechaPagoCalendar.getTime():null, conSaldo);
		}else{
			actasAcuerdos=ConvenioNoOSServiceUtil.reporteAcuerdosNoOS(cuit!=null&&cuit.trim().length()>0?cuit:null, sucu!=null&&sucu.trim().length()>0?sucu:null, 
					   fechaIniCalendar!=null?fechaIniCalendar.getTime():null, 
								   fechaFinCalendar!=null?fechaFinCalendar.getTime():null, 
								   fechaPagoCalendar!=null?fechaPagoCalendar.getTime():null, conSaldo);			
		}
			
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Actas-Acuerdos");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		
		HSSFCellStyle styleBoldTitulo = getStyleBold(wb);
		

		if (actasAcuerdos == null || actasAcuerdos.isEmpty()) {
			return wb;
		}
				
		StringBuffer titulo1=new StringBuffer("Reporte de ").append(tipoReporte).append(" al ").append(sdf.format(hoy));
		if(!StringUtil.isEmpty(cuit)){
			titulo1.append(" - CUIT: ").append(cuit);			
		}
		if(null!=fechaIniCalendar){
			titulo1.append(" - Período desde: ").append(sdf.format(fechaIniCalendar.getTime()));
		}
		if(null!=fechaFinCalendar){
			titulo1.append(" - Período hasta: ").append(sdf.format(fechaFinCalendar.getTime()));
		}
		if(null!=fechaPagoCalendar){
			titulo1.append(" - Hasta fecha pago: ").append(sdf.format(fechaPagoCalendar.getTime()));
		}
		titulo1.append(" - ").append(conSaldo==0?"Con Saldo":conSaldo==1?"Sin Saldo":"Con y sin Saldo");
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		int index = 0;
		HSSFRow rowHeaderANT = sheet.createRow(index);		
		HSSFCell cell0HA = rowHeaderANT.createCell(0);
		
		cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
		cell0HA.setCellStyle(styleBoldTitulo);
		
		if(null!=tipoReporte && tipoReporte.trim().equals("ACTAS")){
			index=crearHeaderActas(sheet,wb,index);
			index++;			
			for(ActasAcuerdos acta: actasAcuerdos){
				index=crearDatosActas(sheet, wb, acta, index);			
				
			}
		}else{
			index=crearHeaderAcuerdos(sheet,wb,index);
			index++;			
			for(ActasAcuerdos acta: actasAcuerdos){
				index=crearDatosAcuerdos(sheet, wb, acta, index);			
				
			}
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

		return wb;
	}
	
	private static int crearHeaderActas(HSSFSheet sheet, HSSFWorkbook wb, int index ){
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
				
		int col = -1;
		
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("CUIT"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Razón Social"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Número"));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Fecha Acta"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Período Ini"));
		cell4H.setCellStyle(styleBold);

		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Período Fin"));
		cell5H.setCellStyle(styleBold);
	
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Capital Sindicato"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Capital Solidario"));
		cell7H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Capital Art.46"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Capital Usufructo"));
		cell9H.setCellStyle(styleBold);
		
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Total Capital"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Int.Sindicato"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Int.Solidario "));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Int.Art.46"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Int. Usufructo"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Total Interés"));
		cell15H.setCellStyle(styleBold);
		
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Total Acta"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Convenio Pago"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Total Pagado"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Saldo"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Usuario"));
		cell19H.setCellStyle(styleBold);
		
		return index;
	}
	
	private static int crearHeaderAcuerdos(HSSFSheet sheet, HSSFWorkbook wb, int index ){
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
				
		int col = -1;
		
		
		index++;
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0H = rowHeader.createCell(++col);
		cell0H.setCellValue(new HSSFRichTextString("CUIT"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Razón Social"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Número"));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Fecha Acuerdo"));
		cell3H.setCellStyle(styleBold);
		
		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Actas Asociadas"));
		cell4H.setCellStyle(styleBold);
				
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Capital"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Interes"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Ajuste Capital"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Ajuste Interes"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Total"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Cant.Ctas."));
		cell15H.setCellStyle(styleBold);
		
		
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Vto.1er Cta."));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Vto.Ult.Cta."));
		cell17H.setCellStyle(styleBold);
				
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Saldo"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Usuario"));
		cell19H.setCellStyle(styleBold);
		
		return index;
	}

	private static int crearDatosActas(HSSFSheet sheet, HSSFWorkbook wb, ActasAcuerdos acta, int index) {
		
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);		
		HSSFCellStyle styleNumber= getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		
		cell0.setCellValue(new HSSFRichTextString(acta.getCuit()));
		cell0.setCellStyle(styleAll);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(acta.getRazonSoc()));
		cell1.setCellStyle(styleNumber);
	
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(acta.getNumero()));
		cell2.setCellStyle(styleAll);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(acta.getFechaCierre());
		cell3.setCellStyle(styleDate);
		
		HSSFCell cell4 = rowHeader.createCell(++col);
		cell4.setCellValue(acta.getPeriodoIni());
		cell4.setCellStyle(styleDate);
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(acta.getPeriodoFin());
		cell5.setCellStyle(styleDate);
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(acta.getCapitalSindicato().doubleValue());
		cell6.setCellStyle(styleNumber);
		

		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(acta.getCapitalSolidario().doubleValue());
		cell7.setCellStyle(styleNumber);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(acta.getCapitalArt46().doubleValue());
		cell8.setCellStyle(styleNumber);
		
		HSSFCell cell9 = rowHeader.createCell(++col);		
		cell9.setCellValue(acta.getCapitalUsufructo().doubleValue());		
		cell9.setCellStyle(styleNumber);
		
		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(acta.getCapitalTotal().doubleValue());
		cell10.setCellStyle(styleNumber);
		
		HSSFCell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(acta.getInteresSindicato().doubleValue());
		cell11.setCellStyle(styleNumber);
		
		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(acta.getInteresSolidario().doubleValue());
		cell12.setCellStyle(styleNumber);
		
		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(acta.getInteresArt46().doubleValue());
		cell13.setCellStyle(styleNumber);
		
		HSSFCell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(acta.getInteresUsufructo().doubleValue());
		cell14.setCellStyle(styleNumber);
		
		HSSFCell cell15 = rowHeader.createCell(++col);
		cell15.setCellValue(acta.getInteresTotal().doubleValue());
		cell15.setCellStyle(styleNumber);
		
		HSSFCell cell16 = rowHeader.createCell(++col);
		cell16.setCellValue(acta.getTotal().doubleValue());
		cell16.setCellStyle(styleNumber);
		
		HSSFCell cell17 = rowHeader.createCell(++col);
		cell17.setCellValue(new HSSFRichTextString(acta.getConvenioPago()));
		cell17.setCellStyle(styleAll);
		
		HSSFCell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(acta.getTotalPagado().doubleValue());
		cell18.setCellStyle(styleNumber);
		
		HSSFCell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(acta.getSaldo().doubleValue());
		cell20.setCellStyle(styleAll);
		
		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(new HSSFRichTextString(acta.getModi_usr()));
		cell19.setCellStyle(styleAll);
		   				   				
		return index++;
	}	
	private static int crearDatosAcuerdos(HSSFSheet sheet, HSSFWorkbook wb, ActasAcuerdos acta, int index) {
		
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);		
		HSSFCellStyle styleNumber= getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		
		cell0.setCellValue(new HSSFRichTextString(acta.getCuit()));
		cell0.setCellStyle(styleAll);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(acta.getRazonSoc()));
		cell1.setCellStyle(styleNumber);
	
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(acta.getNumero()));
		cell2.setCellStyle(styleAll);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(acta.getFechaCierre());
		cell3.setCellStyle(styleDate);
		
		HSSFCell cell3b = rowHeader.createCell(++col);
		cell3b.setCellValue(new HSSFRichTextString(acta.getActasAsociadas()));
		cell3b.setCellStyle(styleAll);
				
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(acta.getCapitalTotal().doubleValue());
		cell6.setCellStyle(styleNumber);
		

		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(acta.getInteresTotal().doubleValue());
		cell7.setCellStyle(styleNumber);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(acta.getAjusteCapital().doubleValue());
		cell8.setCellStyle(styleNumber);
		
		HSSFCell cell9 = rowHeader.createCell(++col);		
		cell9.setCellValue(acta.getAjusteInteres().doubleValue());		
		cell9.setCellStyle(styleNumber);
		
		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(acta.getTotal().doubleValue());
		cell10.setCellStyle(styleNumber);
		
		HSSFCell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(acta.getCantCuotas());
		cell11.setCellStyle(styleNumber);
		
		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(acta.getVtoCuota1());
		cell12.setCellStyle(styleDate);
		
		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(acta.getVtoCuotaUltima());
		cell13.setCellStyle(styleDate);
		
		HSSFCell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(acta.getSaldo().doubleValue());
		cell14.setCellStyle(styleNumber);		
				
		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(new HSSFRichTextString(acta.getModi_usr()));
		cell19.setCellStyle(styleAll);
		   				   				
		return index++;
	}	
}
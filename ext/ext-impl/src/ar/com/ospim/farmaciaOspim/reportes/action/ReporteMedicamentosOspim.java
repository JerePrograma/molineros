package ar.com.ospim.farmaciaOspim.reportes.action;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.farmaciaOspim.beans.MedicacionOspimExcel;
import ar.com.ospim.farmaciaOspim.reportes.beans.BusquedaReporteMedicamentosFiltro;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteMedicamentosOspim extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteMedicamentosOspim.class);


	public static HSSFWorkbook generaReporteMedicacionOspim  (
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		BusquedaReporteMedicamentosFiltro filtro = getFiltrosPadron(renderRequest, res);
		List<MedicacionOspimExcel> registrosMedicacion = new ArrayList<MedicacionOspimExcel>();

		try {
			registrosMedicacion = BusquedaMedicamentoServiceUtil.getReporteMedicamentosOspimFiltro(filtro); //.getReporteMedicamentosOspim(troquel, registro, nombre, presentacion, laboratorio, mediCodBarra,fechaPeriodo,droga,manualDat  );			
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de medicacion OSPIM",e);
			return null;
		}
		return generaReporte(registrosMedicacion,filtro);
	}

	private static HSSFWorkbook generaReporte(
			List<MedicacionOspimExcel> list , BusquedaReporteMedicamentosFiltro filtro ) {
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		Date hoy=new Date();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Medicamentos");
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
				
		StringBuffer titulo1=new StringBuffer("Reporte Medicación OSPIM: ").append(sdf.format(hoy));
	
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
				
		int index = 0;		
		int col = -1;

		HSSFRow row1 = sheet.createRow(index);		
		HSSFCell cell00H = row1.createCell(0);		
		cell00H.setCellValue(new HSSFRichTextString(filtro.getDescripcionFiltros().toString() )); 
		cell00H.setCellStyle(styleBold);
		
		index++;
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
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
		cell1H.setCellValue(new HSSFRichTextString("Registro"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Troquel"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Codigo Barra"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Nombre"));
		cell4H.setCellStyle(styleBold);		

		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("Droga"));
		cell5H.setCellStyle(styleBold);
	
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Presentación"));
		cell6H.setCellStyle(styleBold);

		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Laboratorio"));
		cell7H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Acción"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("Presen Activa"));
		cell9H.setCellStyle(styleBold);
		
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("Tipo Venta"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Precio"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Fecha"));
		cell12H.setCellStyle(styleBold);
				
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("Período"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Fecha Baja"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Manual Dat"));
		cell15H.setCellStyle(styleBold);
		
		index++;
		
		for(MedicacionOspimExcel  autorizaciones: list){
			index=crearDatosFicha(sheet, autorizaciones, index, styleAll,
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
		sheet.autoSizeColumn((short) 15);
		
		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,MedicacionOspimExcel   medicacion, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, HSSFCellStyle styleMoneyRight) {
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		try {
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString( String.valueOf(medicacion.getId_medicamento()  )   ));
		cell0.setCellStyle(styleNumber);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString( String.valueOf(medicacion.getRegistro() )   ));
		cell1.setCellStyle(styleNumber);
	
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString( String.valueOf(medicacion.getTroquel()   )   ));
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString( medicacion.getCod_barra()      ));
		cell3.setCellStyle(styleNumber);
		
		HSSFCell cell4 = rowHeader.createCell(++col);		
		cell4.setCellValue(new HSSFRichTextString( medicacion.getNombre().toUpperCase()     ));
		cell4.setCellStyle(styleNumber);
		
		HSSFCell cell5 = rowHeader.createCell(++col);
		cell5.setCellValue(new HSSFRichTextString( medicacion.getDroga().toUpperCase() ));
		cell5.setCellStyle(styleNumber);
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString( medicacion.getPresentacion().toUpperCase()    ));
		cell6.setCellStyle(styleNumber);
		
		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString( medicacion.getLaboratorio().toUpperCase() ));
		cell7.setCellStyle(styleNumber);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString( medicacion.getAccion().toUpperCase() ));
		cell8.setCellStyle(styleNumber);
		
		HSSFCell cell9 = rowHeader.createCell(++col);
		if(medicacion.isPresentacionActivaMedicamento()  ){
			cell9.setCellValue(new HSSFRichTextString("SI"));
		}else{
			cell9.setCellValue(new HSSFRichTextString("NO"));
		}
		
		HSSFCell cell10 = rowHeader.createCell(++col);		
		cell10.setCellValue(new HSSFRichTextString( medicacion.getTipoventaMedicamento().toUpperCase()    ));
		cell10.setCellStyle(styleNumber);
		
		HSSFCell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(new HSSFRichTextString( String.valueOf(formatter.format(medicacion.getPrecio()) )   ));
		cell11.setCellStyle(styleNumber);
		
		HSSFCell cell12 = rowHeader.createCell(++col);
		if(medicacion.getFecha()==null ){
			cell12.setCellValue(new HSSFRichTextString(""));
		}else{
			cell12.setCellValue(new HSSFRichTextString(sdf.format(medicacion.getFecha()) ));
		}
		cell12.setCellStyle(styleNumber);
		
		HSSFCell cell13 = rowHeader.createCell(++col);
		if(medicacion.getPeriodo() ==null ){
			cell13.setCellValue(new HSSFRichTextString(""));
		}else{
			cell13.setCellValue(new HSSFRichTextString(sdf.format(medicacion.getPeriodo() ) ));
		}
		cell13.setCellStyle(styleNumber);
		
		HSSFCell cell14 = rowHeader.createCell(++col);
		if(medicacion.getFecha_baja()  ==null ){
			cell14.setCellValue(new HSSFRichTextString(""));
		}else{
			cell14.setCellValue(new HSSFRichTextString(sdf.format(medicacion.getFecha_baja() ) ));
		}
		cell14.setCellStyle(styleNumber);
	
		HSSFCell cell15 = rowHeader.createCell(++col);
		if(medicacion.getManualDat() ){
			cell15.setCellValue(new HSSFRichTextString("SI"));
		}else{
			cell15.setCellValue(new HSSFRichTextString("NO"));
		}
		cell15.setCellStyle(styleNumber);
		
		}catch(Exception e){
			_log.error("Error al generar Excel Situacion Medica en crearDatosFicha", e);			
		}
		return index++;
	}	
	
	public static BusquedaReporteMedicamentosFiltro getFiltrosPadron(HttpServletRequest req, HttpServletResponse res) {
		
		BusquedaReporteMedicamentosFiltro  filtro = new BusquedaReporteMedicamentosFiltro();
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
				"dd/MM/yyyy");
		String fechaDia ="01";
		String fechaMes = ParamUtil.getString(req,"mediPeriodoMes");
		String fechaAnio = ParamUtil.getString(req,"mediPeriodoYear");
		Date fechaPeriodo= null;
		try {
			fechaPeriodo= formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fechaPeriodo= null;
		}
		filtro.setPeriodo(fechaPeriodo);
		filtro.setNombre(ParamUtil.getString(req, "mediNombre", null));
		filtro.setPresentacion(ParamUtil.getString(req, "mediPresentacion", null));
		filtro.setDroga(ParamUtil.getString(req, "mediDroga", null));
		filtro.setLaboratorio(ParamUtil.getString(req, "mediLaboratorio",null));
		filtro.setTroquel(ParamUtil.getInteger(req, "mediTroquel", 0));
		filtro.setRegistro(ParamUtil.getInteger(req, "mediRegistro", 0));
		filtro.setCod_barra(ParamUtil.getString(req, "mediCodBarra", null));
        filtro.setManualDat(ParamUtil.getBoolean(req, "manualDat", false));
		filtro.setIncluyeBajas(ParamUtil.getBoolean(req, "incluyeBajas", false));
		return filtro ;		
	}

}

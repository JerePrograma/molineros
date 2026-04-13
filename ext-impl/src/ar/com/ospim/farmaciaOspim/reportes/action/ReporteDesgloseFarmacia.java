package ar.com.ospim.farmaciaOspim.reportes.action;

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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.DetalleDesglose;

public class ReporteDesgloseFarmacia extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteDesgloseFarmacia.class);


	public static HSSFWorkbook generaReporteDesgloseFarmacia (
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		// *************************************************************
		// carga de variables recibidas de la JSP 
		// *************************************************************
		
		String periodoArchivo  = renderRequest.getParameter("periodo");
		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
				"dd/MM/yyyy");

		Date fechaPeriodo = null;
		
		try {			
			fechaPeriodo= formatoDeFechas.parse(periodoArchivo);
		} catch (Exception e) {
			fechaPeriodo= null;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaPeriodo);
		int month = cal.get(Calendar.MONTH)+1;
		
        String  nombreArchivoDesgloseFarmacia="";				 			
        nombreArchivoDesgloseFarmacia="conciliacion.farmacia_preven_" +  String.format ("%02d", month) + cal.get(Calendar.YEAR ) ; 
	    
	    // *************************************************************		
		//  fin  de carga de variables de la JSP 
	    // *************************************************************		
		
		List<DetalleDesglose> registrosArchivoDesglose= new ArrayList<DetalleDesglose>();

		try {
			registrosArchivoDesglose= FarmaciaServiceUtil.getListaDesgloseArchivoFarmacia(nombreArchivoDesgloseFarmacia);
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de situaciones medicas",e);
			return null;
		}
		return generaReporte(registrosArchivoDesglose);
	}

	private static HSSFWorkbook generaReporte(
			List<DetalleDesglose> list) {
		
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
				
		StringBuffer titulo1=new StringBuffer("Reporte Archivo Desglose Farmacia: ").append(sdf.format(hoy));
	
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
		cell0H.setCellValue(new HSSFRichTextString("Hasta"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(++col);
		cell1H.setCellValue(new HSSFRichTextString("Cod Col"));
		cell1H.setCellStyle(styleBold);
		
		HSSFCell cell2H = rowHeader.createCell(++col);
		cell2H.setCellValue(new HSSFRichTextString("Colegio"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(++col);
		cell3H.setCellValue(new HSSFRichTextString("Cod Farmacia"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell4H = rowHeader.createCell(++col);
		cell4H.setCellValue(new HSSFRichTextString("Farmacia"));
		cell4H.setCellStyle(styleBold);		

		HSSFCell cell5H = rowHeader.createCell(++col);
		cell5H.setCellValue(new HSSFRichTextString("CUIT"));
		cell5H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(++col);
		cell6H.setCellValue(new HSSFRichTextString("Dirección"));
		cell6H.setCellStyle(styleBold);
		
		HSSFCell cell7H = rowHeader.createCell(++col);
		cell7H.setCellValue(new HSSFRichTextString("Localidad"));
		cell7H.setCellStyle(styleBold);

		HSSFCell cell8H = rowHeader.createCell(++col);
		cell8H.setCellValue(new HSSFRichTextString("Región"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(++col);
		cell9H.setCellValue(new HSSFRichTextString("codReg"));
		cell9H.setCellStyle(styleBold);
		
		HSSFCell cell10H = rowHeader.createCell(++col);
		cell10H.setCellValue(new HSSFRichTextString("codReceta"));
		cell10H.setCellStyle(styleBold);
		
		HSSFCell cell11H = rowHeader.createCell(++col);
		cell11H.setCellValue(new HSSFRichTextString("Orden"));
		cell11H.setCellStyle(styleBold);
		
		HSSFCell cell12H = rowHeader.createCell(++col);
		cell12H.setCellValue(new HSSFRichTextString("Env"));
		cell12H.setCellStyle(styleBold);
		
		HSSFCell cell13H = rowHeader.createCell(++col);
		cell13H.setCellValue(new HSSFRichTextString("PVP"));
		cell13H.setCellStyle(styleBold);
		
		HSSFCell cell14H = rowHeader.createCell(++col);
		cell14H.setCellValue(new HSSFRichTextString("Entidad"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(++col);
		cell15H.setCellValue(new HSSFRichTextString("Porcentaje"));
		cell15H.setCellStyle(styleBold);		
		  
		HSSFCell cell16H = rowHeader.createCell(++col);
		cell16H.setCellValue(new HSSFRichTextString("Troquel"));
		cell16H.setCellStyle(styleBold);
		
		HSSFCell cell17H = rowHeader.createCell(++col);
		cell17H.setCellValue(new HSSFRichTextString("Registro"));
		cell17H.setCellStyle(styleBold);
		
		HSSFCell cell18H = rowHeader.createCell(++col);
		cell18H.setCellValue(new HSSFRichTextString("Nombre Comercial"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(++col);
		cell19H.setCellValue(new HSSFRichTextString("Pot"));
		cell19H.setCellStyle(styleBold);
		
		HSSFCell cell20H = rowHeader.createCell(++col);
		cell20H.setCellValue(new HSSFRichTextString("Forma Farm"));
		cell20H.setCellStyle(styleBold);
		
		HSSFCell cell21H = rowHeader.createCell(++col);
		cell21H.setCellValue(new HSSFRichTextString("Cont"));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(++col);
		cell22H.setCellValue(new HSSFRichTextString("Principio"));
		cell22H.setCellStyle(styleBold);
		  
		HSSFCell cell23 = rowHeader.createCell(++col);
		cell23.setCellValue(new HSSFRichTextString("Acción"));
		cell23.setCellStyle(styleBold);
			
		HSSFCell cell24 = rowHeader.createCell(++col);
		cell24.setCellValue(new HSSFRichTextString("Fecha"));
		cell24.setCellStyle(styleBold);

		HSSFCell cell25 = rowHeader.createCell(++col);
		cell25.setCellValue(new HSSFRichTextString("Dispensa"));
		cell25.setCellStyle(styleBold);

		HSSFCell cell26 = rowHeader.createCell(++col);
		cell26.setCellValue(new HSSFRichTextString("Matrícula"));
		cell26.setCellStyle(styleBold);

		HSSFCell cell27 = rowHeader.createCell(++col);
		cell27.setCellValue(new HSSFRichTextString("Profesional"));
		cell27.setCellStyle(styleBold);

		HSSFCell cell28 = rowHeader.createCell(++col);
		cell28.setCellValue(new HSSFRichTextString("Grupo"));
		cell28.setCellStyle(styleBold);

		HSSFCell cell29 = rowHeader.createCell(++col);
		cell29.setCellValue(new HSSFRichTextString("Nombre Beneficiario"));
		cell29.setCellStyle(styleBold);

		HSSFCell cell30 = rowHeader.createCell(++col);
		cell30.setCellValue(new HSSFRichTextString("TP"));
		cell30.setCellStyle(styleBold);

		HSSFCell cell31 = rowHeader.createCell(++col);
		cell31.setCellValue(new HSSFRichTextString("PMI"));
		cell31.setCellStyle(styleBold);

		HSSFCell cell32 = rowHeader.createCell(++col);
		cell32.setCellValue(new HSSFRichTextString("Monto Ospim"));
		cell32.setCellStyle(styleBold);

		HSSFCell cell33 = rowHeader.createCell(++col);
		cell33.setCellValue(new HSSFRichTextString("Monto UOMA"));
		cell33.setCellStyle(styleBold);

		HSSFCell cell34 = rowHeader.createCell(++col);
		cell34.setCellValue(new HSSFRichTextString("Monto AMTIMA"));
		cell34.setCellStyle(styleBold);

		HSSFCell cell35 = rowHeader.createCell(++col);
		cell35.setCellValue(new HSSFRichTextString("Plan"));
		cell35.setCellStyle(styleBold);

		HSSFCell cell36 = rowHeader.createCell(++col);
		cell36.setCellValue(new HSSFRichTextString("Inte"));
		cell36.setCellStyle(styleBold);

		HSSFCell cell37 = rowHeader.createCell(++col);
		cell37.setCellValue(new HSSFRichTextString("Id_Ospim"));
		cell37.setCellStyle(styleBold);

		HSSFCell cell38 = rowHeader.createCell(++col);
		cell38.setCellValue(new HSSFRichTextString("id_Uoma"));
		cell38.setCellStyle(styleBold);

		HSSFCell cell39 = rowHeader.createCell(++col);
		cell39.setCellValue(new HSSFRichTextString("id_amtima"));
		cell39.setCellStyle(styleBold);

		HSSFCell cell40 = rowHeader.createCell(++col);
		cell40.setCellValue(new HSSFRichTextString("id_seccional"));
		cell40.setCellStyle(styleBold);

		HSSFCell cell41 = rowHeader.createCell(++col);
		cell41.setCellValue(new HSSFRichTextString("Seccional"));
		cell41.setCellStyle(styleBold);

		HSSFCell cell42 = rowHeader.createCell(++col);
		cell42.setCellValue(new HSSFRichTextString("Comentario"));
		cell42.setCellStyle(styleBold);

		HSSFCell cell43 = rowHeader.createCell(++col);
		cell43.setCellValue(new HSSFRichTextString("cuil titular"));
		cell43.setCellStyle(styleBold);		   
				
		index++;
		
		for(DetalleDesglose  autorizaciones: list){
			index=crearDatosFicha(sheet, autorizaciones, index, styleAll,styleAll, styleAll, styleAll,  styleMoneyRight);
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
		sheet.autoSizeColumn((short) 41);
		sheet.autoSizeColumn((short) 42);
		sheet.autoSizeColumn((short) 43);
		
		
		
		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,DetalleDesglose desgloseArchivoFarmacia , 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,
			HSSFCellStyle styleDate,HSSFCellStyle styleMoney,  HSSFCellStyle styleMoneyRight) {
		
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
		
		try {
			
		
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(new HSSFRichTextString(  desgloseArchivoFarmacia.getHasta()     ));
//		cell0.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getHasta()) ));
		cell0.setCellStyle(styleAll);
		cell0.setCellType(CellType.STRING);
		
		HSSFCell cell1 = rowHeader.createCell(++col);
		if (!desgloseArchivoFarmacia.getCod_col().equals("")){			
			//cell1.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getCod_col() ));
			cell1.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getCod_col()) ));
			cell1.setCellStyle(styleAll);
			cell1.setCellType(CellType.NUMERIC );
		}
		
		
		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getColegio()  ));
		cell2.setCellStyle(styleAll);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		//cell3.setCellValue(new HSSFRichTextString(String.valueOf(desgloseArchivoFarmacia.getCod_farmacia()  )));
		cell3.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getCod_farmacia()) ));
		cell3.setCellStyle(styleAll);
		cell3.setCellType(CellType.NUMERIC);
		
		HSSFCell cell4 = rowHeader.createCell(++col);		
		cell4.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getFarmacia())  );
		cell4.setCellStyle(styleAll);
		  
		HSSFCell cell5 = rowHeader.createCell(++col);
		//cell5.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getCuit()   ));
		if (!desgloseArchivoFarmacia.getCod_col().equals("")){
			cell5.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getCuit() ) ));		
			cell5.setCellStyle(styleAll);
			cell5.setCellType(CellType.NUMERIC );	
		}	
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getDireccion() ));
		cell6.setCellStyle(styleAll);
		
		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getLocalidad() ));
		cell7.setCellStyle(styleAll);
		
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getRegion()  ));
		cell8.setCellStyle(styleAll);
		
		HSSFCell cell9 = rowHeader.createCell(++col);
		cell9.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getCodReg()  ));
		cell9.setCellStyle(styleAll);
		
		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getCodReceta()  ));
		cell10.setCellStyle(styleAll);
		
		HSSFCell cell11 = rowHeader.createCell(++col);		
		if (desgloseArchivoFarmacia.getOrden()==null || desgloseArchivoFarmacia.getOrden().equals("") ){
			cell11.setCellValue(0);
		}else{
			cell11.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getOrden()) ));	
		}				
		cell11.setCellStyle(styleAll);
		cell11.setCellType(CellType.NUMERIC);
		
		HSSFCell cell12 = rowHeader.createCell(++col);		
		//cell12.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getEnv()   ));
		cell12.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getEnv()) ));		
		cell12.setCellStyle(styleAll);
		cell12.setCellType(CellType.NUMERIC );
		
		
		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(new Double((desgloseArchivoFarmacia.getPvp()==null?"0":desgloseArchivoFarmacia.getPvp())));
		cell13.setCellStyle(styleMoney);		
		cell13.setCellType(CellType.NUMERIC );

		HSSFCell cell14 = rowHeader.createCell(++col);
		cell14.setCellValue(new Double((desgloseArchivoFarmacia.getEntidad()==null?"0":desgloseArchivoFarmacia.getEntidad()) ));
		cell14.setCellStyle(styleMoney);
		cell14.setCellType(CellType.NUMERIC );
		  
		HSSFCell cell15 = rowHeader.createCell(++col);
		//cell15.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getPorcentaje() ));
		cell15.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getPorcentaje()) ));		
		cell15.setCellStyle(styleAll);
		cell15.setCellType(CellType.NUMERIC);
		
	
		HSSFCell cell16 = rowHeader.createCell(++col);
		//cell16.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getTroquel()  ));
		if (desgloseArchivoFarmacia.getTroquel()!=null  &&  !desgloseArchivoFarmacia.getTroquel().equals(""))  {
			cell16.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getTroquel()) ));		
			cell16.setCellStyle(styleAll);
			cell16.setCellType(CellType.NUMERIC );	
		}
		
		
		HSSFCell cell17 = rowHeader.createCell(++col);
		//cell17.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getRegistro()  ));				
		cell17.setCellValue(new Double(Double.parseDouble(desgloseArchivoFarmacia.getRegistro()) ));		
		cell17.setCellStyle(styleAll);
		cell17.setCellType( CellType.NUMERIC );
		
		HSSFCell cell18 = rowHeader.createCell(++col);
		cell18.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getNombre_comercial()   ));
		cell18.setCellStyle(styleAll);

		HSSFCell cell19 = rowHeader.createCell(++col);
		cell19.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getPot()  ));
		cell19.setCellStyle(styleAll);

		HSSFCell cell20 = rowHeader.createCell(++col);
		cell20.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getForma_farm() ));
		cell20.setCellStyle(styleAll);
		
		HSSFCell cell21 = rowHeader.createCell(++col);
		cell21.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getCont() ));
		cell21.setCellStyle(styleAll);
		  
		HSSFCell cell22 = rowHeader.createCell(++col);
		cell22.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getPrincipio()   ));
		cell22.setCellStyle(styleAll);
		
		HSSFCell cell23 = rowHeader.createCell(++col);
		cell23.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getAccion() ));
		cell23.setCellStyle(styleAll);
		
		HSSFCell cell24 = rowHeader.createCell(++col);
		cell24.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getFecha()  ));
		cell24.setCellStyle(styleAll);
		
		HSSFCell cell25 = rowHeader.createCell(++col);
		cell25.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getDispensa()  ));
		cell25.setCellStyle(styleAll);
		  
		HSSFCell cell26 = rowHeader.createCell(++col);
		cell26.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getMatricula()  ));
		cell26.setCellStyle(styleAll);		
		
		HSSFCell cell27 = rowHeader.createCell(++col);
		cell27.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getProfesional()   ));
		cell27.setCellStyle(styleAll);		
		
		HSSFCell cell28 = rowHeader.createCell(++col);
		cell28.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getGrupo()   ));
		cell28.setCellStyle(styleAll);		
		
		HSSFCell cell29 = rowHeader.createCell(++col);
		cell29.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getNombre_benef()  ));
		cell29.setCellStyle(styleAll);
		
		HSSFCell cell30 = rowHeader.createCell(++col);
		cell30.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getTp()  ));
		cell30.setCellStyle(styleAll);
		
		HSSFCell cell31 = rowHeader.createCell(++col);
		cell31.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.isPmi()?"Si":"No"));
		cell31.setCellStyle(styleAll);			    
		  
		HSSFCell cell32 = rowHeader.createCell(++col);		
		cell32.setCellValue(new Double(desgloseArchivoFarmacia.getMonto_ospim()));
		cell32.setCellStyle(styleMoney);
		cell32.setCellType( CellType.NUMERIC );
		
		HSSFCell cell33 = rowHeader.createCell(++col);
		cell33.setCellValue(new Double(desgloseArchivoFarmacia.getMonto_uoma()));
		cell33.setCellStyle(styleMoney);
		cell33.setCellType( CellType.NUMERIC );
		
		HSSFCell cell34 = rowHeader.createCell(++col);
		cell34.setCellValue(new Double(desgloseArchivoFarmacia.getMonto_amtima()));
		cell34.setCellStyle(styleMoney);
		cell34.setCellType( CellType.NUMERIC );
		
		HSSFCell cell35 = rowHeader.createCell(++col);
		cell35.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getPlan()  ));
		cell35.setCellStyle(styleAll);
		
		HSSFCell cell36 = rowHeader.createCell(++col);
		cell36.setCellValue(new HSSFRichTextString(String.valueOf(desgloseArchivoFarmacia.getInte() ) ));
		cell36.setCellStyle(styleAll);
		
		HSSFCell cell37 = rowHeader.createCell(++col);
		cell37.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getId_ospim()  ));
		cell37.setCellStyle(styleAll);		
		
		HSSFCell cell38 = rowHeader.createCell(++col);
		cell38.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getId_uoma()  ));
		cell38.setCellStyle(styleAll);
		
		
		HSSFCell cell39 = rowHeader.createCell(++col);
		cell39.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getId_amtima()  ));
		cell39.setCellStyle(styleAll);
		
		HSSFCell cell40 = rowHeader.createCell(++col);
		cell40.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getId_seccional() ));
		cell40.setCellStyle(styleAll);
		
		
		HSSFCell cell41 = rowHeader.createCell(++col);
		cell41.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getSeccional()   ));
		cell41.setCellStyle(styleAll);
		
		
		HSSFCell cell42 = rowHeader.createCell(++col);
		cell42.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getComentario()  ));
		cell42.setCellStyle(styleAll);
		
		HSSFCell cell43 = rowHeader.createCell(++col);
		cell43.setCellValue(new HSSFRichTextString(desgloseArchivoFarmacia.getCuil_titular()  ));
		cell43.setCellStyle(styleAll);
		
		
		}catch(Exception e){
			_log.error("Error al generar Excel Archivo Farmacia Prevencion crearDatosFicha", e);			
		}
		
		return index++;
	}	

}

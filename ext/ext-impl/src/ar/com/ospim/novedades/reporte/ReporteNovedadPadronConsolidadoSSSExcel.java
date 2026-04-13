package ar.com.ospim.novedades.reporte;

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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoAltas;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoBajas;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoInconsistencia;
import ar.com.ospim.novedades.service.ReporteNovedadPadronConsolidadoUtil;

public class ReporteNovedadPadronConsolidadoSSSExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteNovedadPadronConsolidadoSSSExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res)  {

		
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
		String fechaProc = ParamUtil.getString(renderRequest,"fechaProc");
		
		
		Date fechaDesde = null;
		
		
		
		try {
			fechaDesde = formatoDeFechas.parse(fechaProc);
		} catch (Exception e) {
			fechaDesde = null;
		}
		
			
		
		List<NovedadPadronConsolidadoBajas> bajas = new ArrayList<NovedadPadronConsolidadoBajas>();
		List<NovedadPadronConsolidadoAltas> altas = new ArrayList<NovedadPadronConsolidadoAltas>();
		List<NovedadPadronConsolidadoInconsistencia> inconsistencia = new ArrayList<NovedadPadronConsolidadoInconsistencia>();
		

		try {
			altas =  ReporteNovedadPadronConsolidadoUtil.getNovedadPadronConsolidadoAltas(fechaDesde);
			
			bajas =  ReporteNovedadPadronConsolidadoUtil.getNovedadPadronConsolidadoBajas(fechaDesde);
			
			inconsistencia = ReporteNovedadPadronConsolidadoUtil.getNovedadPadronConsolidadoInconsistentes(fechaDesde);
			
			
			
		} catch (Exception e) {
			_log.debug("No se pudo obtener Novedad_Padron_Consolidado ");
			_log.debug(e.getStackTrace());
		}
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
	
		HSSFSheet sheet = wb.createSheet("Bajas");
		
		generarReporteBajas(bajas, wb, sheet);
		
		HSSFSheet sheet2 = wb.createSheet("Altas");

		generarAltas(altas, wb, sheet2);
		
		HSSFSheet sheet3 = wb.createSheet("Inconsistencias");
		
		generarInconsistencias(inconsistencia, wb, sheet3);
		
	
		return  wb;

	
		
	}

	private static HSSFWorkbook generarReporteBajas(List<NovedadPadronConsolidadoBajas> list , HSSFWorkbook wb  , HSSFSheet sheet ) {


		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);


		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("NOVEDAD"));

		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("CODIGO MOVIMIENTO"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("PERIODO NOVEDAD"));
		cell2H.setCellStyle(styleBold);
		

		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		cell3H.setCellStyle(styleBold);
		
		
		HSSFCell cell4H = rowHeader.createCell(4);
		cell4H.setCellValue(new HSSFRichTextString("INTE"));
		cell4H.setCellStyle(styleBold);
		

		HSSFCell cell5H = rowHeader.createCell(5);
		cell5H.setCellValue(new HSSFRichTextString("CUIT"));
		cell5H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(6);
		cell6H.setCellValue(new HSSFRichTextString("RAZON SOCIAL"));
		cell6H.setCellStyle(styleBold);
		

		HSSFCell cell7H = rowHeader.createCell(7);
		cell7H.setCellValue(new HSSFRichTextString("VIGEN FECHA"));
		cell7H.setCellStyle(styleBold);
		
		HSSFCell cell8H = rowHeader.createCell(8);
		cell8H.setCellValue(new HSSFRichTextString("BAJA FECHA"));
		cell8H.setCellStyle(styleBold);
		
		HSSFCell cell9H = rowHeader.createCell(9);
		cell9H.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		cell9H.setCellStyle(styleBold);
		

		HSSFCell cell10H = rowHeader.createCell(10);
		cell10H.setCellValue(new HSSFRichTextString("SIT. REVISTA"));
		cell10H.setCellStyle(styleBold);
		

		HSSFCell cell11H = rowHeader.createCell(11);
		cell11H.setCellValue(new HSSFRichTextString("CATEGORIA"));
		cell11H.setCellStyle(styleBold);
		

		

		if (list == null || list.isEmpty()) {
			return wb;
		}


		int total = 0;


		for (NovedadPadronConsolidadoBajas novBajas : list) {
			index++;
			crearBajas(sheet, index, novBajas, styleBold,styleAll, styleDate, styleMoney);
			total = total  +1;
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(1);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(2);
		cell1.setCellValue(total);
		cell1.setCellStyle(styleAll);
		



		index++;
		sheet.createRow(index);

	
		
		for (int i = 0; i < 600; i++) {
			sheet.autoSizeColumn((short)i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 300);
		}
		

		return wb;
	}
	
	private static void crearBajas(HSSFSheet sheet, int index,
			NovedadPadronConsolidadoBajas bajas, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		HSSFRow rowHeader = sheet.createRow(index);
		
		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(bajas.getIdNovedad());
		cell0.setCellStyle(styleAll);
		
		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(bajas.getCodigoMovimiento()));
		cell1.setCellStyle(styleAll);
		
		HSSFCell cell2 = rowHeader.createCell(2);
    	cell2.setCellValue(new HSSFRichTextString(sdf.format(bajas.getProcesoFecha())));
		cell2.setCellStyle(styleAll);

		
		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(bajas.getCuilTitular()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(bajas.getInte());
		cell4.setCellStyle(styleAll);
		
		
		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(bajas.getCuit()));
		cell5.setCellStyle(styleAll);
		
		HSSFCell cell6 = rowHeader.createCell(6);
		cell6.setCellValue(new HSSFRichTextString(bajas.getRazonSocial() != null 
				&& !"MIGRADO SISTEMA ANTERIOR".equalsIgnoreCase(bajas.getRazonSocial()) 
				&& !"OBRA SOCIAL DEL PERSONAL DE LA INDUSTRIA MOLINERA".equalsIgnoreCase(bajas.getRazonSocial()) ? bajas.getRazonSocial() :"" ));
		cell6.setCellStyle(styleAll);
		
		
		HSSFCell cell7 = rowHeader.createCell(7);

		cell7.setCellValue(new HSSFRichTextString(bajas.getVigenFecha() != null ? sdf.format(bajas.getVigenFecha()) : "" ));
		cell7.setCellStyle(styleAll);
		

		HSSFCell cell8 = rowHeader.createCell(8);
		cell8.setCellValue(new HSSFRichTextString(bajas.getBajaFecha() != null ? sdf.format(bajas.getBajaFecha()) : ""));
		cell8.setCellStyle(styleAll);
		

		HSSFCell cell9 = rowHeader.createCell(9);
		cell9.setCellValue(new HSSFRichTextString(bajas.getMotivoBajaDesc()));
		cell9.setCellStyle(styleAll);
		
		HSSFCell cell10 = rowHeader.createCell(10);
		cell10.setCellValue(new HSSFRichTextString(bajas.getRevistaDesc()));
		cell10.setCellStyle(styleAll);
		
		HSSFCell cell11 = rowHeader.createCell(11);
		cell11.setCellValue(new HSSFRichTextString(bajas.getCategoria()));
		cell11.setCellStyle(styleAll);
		
		
	}


	private static void crearAltas(HSSFSheet sheet, int index,
			NovedadPadronConsolidadoAltas altas, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {

		HSSFRow rowHeader = sheet.createRow(index);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(altas.getIdNovedad());
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(altas.getCodigoMovimiento()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(sdf.format(altas.getProcesoFecha())));
		cell2.setCellStyle(styleAll);
		
		
		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(altas.getCuilTitular()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(altas.getInte());
		cell4.setCellStyle(styleAll);
		

		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(altas.getSucu()));
		cell5.setCellStyle(styleAll);

		
		if (altas.getIdOspimBajaFecha() !=  null){
			HSSFCell cell6 = rowHeader.createCell(6);
			cell6.setCellValue(new HSSFRichTextString(sdf.format(altas.getIdOspimBajaFecha())));
			cell6.setCellStyle(styleAll);	
		}else{
			HSSFCell cell6 = rowHeader.createCell(6);
			cell6.setCellValue(new HSSFRichTextString(""));
			cell6.setCellStyle(styleAll);
		}
		
		if(altas.getIdUomaBajaFecha() != null){
			HSSFCell cell7 = rowHeader.createCell(7);
			cell7.setCellValue(new HSSFRichTextString(sdf.format(altas.getIdUomaBajaFecha())));
			cell7.setCellStyle(styleAll);
		}else{
			HSSFCell cell7 = rowHeader.createCell(7);
			cell7.setCellValue(new HSSFRichTextString(""));
			cell7.setCellStyle(styleAll);
		}
		
		if(altas.getIdAmtimaBajaFecha() != null){
			HSSFCell cell8 = rowHeader.createCell(8);
			cell8.setCellValue(new HSSFRichTextString(sdf.format(altas.getIdAmtimaBajaFecha())));
			cell8.setCellStyle(styleAll);
		}else{
			HSSFCell cell8 = rowHeader.createCell(8);
			cell8.setCellValue(new HSSFRichTextString(""));
			cell8.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell9 = rowHeader.createCell(9);
		cell9.setCellValue(new HSSFRichTextString(altas.getApellido()));
		cell9.setCellStyle(styleAll);
		
		HSSFCell cell10 = rowHeader.createCell(10);
		cell10.setCellValue(new HSSFRichTextString(altas.getNombre()));
		cell10.setCellStyle(styleAll);
		
		HSSFCell cell11 = rowHeader.createCell(11);
		cell11.setCellValue(new HSSFRichTextString(altas.getTipoDocumento()));
		cell11.setCellStyle(styleAll);
		
		HSSFCell cell12 = rowHeader.createCell(12);
		cell12.setCellValue(new HSSFRichTextString(altas.getNumeroDocumento()));
		cell12.setCellStyle(styleAll);
		
		HSSFCell cell13 = rowHeader.createCell(13);
		cell13.setCellValue(new HSSFRichTextString(altas.getSexo()));
		cell13.setCellStyle(styleAll);
		
		HSSFCell cell14 = rowHeader.createCell(14);
		cell14.setCellValue(new HSSFRichTextString(altas.getCuit()));
		cell14.setCellStyle(styleAll);
		
		HSSFCell cell15 = rowHeader.createCell(15);
		cell15.setCellValue(new HSSFRichTextString(altas.getRazonSocial() !=  null && 
				!"MIGRADO SISTEMA ANTERIOR".equalsIgnoreCase(altas.getRazonSocial()) &&  
				!"OBRA SOCIAL DEL PERSONAL DE LA INDUSTRIA MOLINERA".equalsIgnoreCase(altas.getRazonSocial())  ? altas.getRazonSocial() :""));
		cell15.setCellStyle(styleAll);
		
		if (altas.getFechaNacimiento() != null){
			HSSFCell cell16 = rowHeader.createCell(16);
			cell16.setCellValue(new HSSFRichTextString(sdf.format(altas.getFechaNacimiento())));
			cell16.setCellStyle(styleAll);
		}else{
			HSSFCell cell16 = rowHeader.createCell(16);
			cell16.setCellValue(new HSSFRichTextString(""));
			cell16.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell17 = rowHeader.createCell(17);
		cell17.setCellValue(new HSSFRichTextString(altas.getEstadoCivilDesc()));
		cell17.setCellStyle(styleAll);
		
		HSSFCell cell18 = rowHeader.createCell(18);
		cell18.setCellValue(new HSSFRichTextString(altas.getNacionalidadDesc()));
		cell18.setCellStyle(styleAll);
		
		HSSFCell cell19 = rowHeader.createCell(19);
		cell19.setCellValue(new HSSFRichTextString(altas.getParentescoDesc()));
		cell19.setCellStyle(styleAll);
		
		HSSFCell cell20 = rowHeader.createCell(20);
		cell20.setCellValue(altas.getSeccional());
		cell20.setCellStyle(styleAll);
		
		if (altas.getVigenFecha() != null){
			HSSFCell cell21 = rowHeader.createCell(21);
			cell21.setCellValue(new HSSFRichTextString(sdf.format(altas.getVigenFecha())));
			cell21.setCellStyle(styleAll);
		}else{
			HSSFCell cell21 = rowHeader.createCell(21);
			cell21.setCellValue(new HSSFRichTextString(""));
			cell21.setCellStyle(styleAll);
			
		}
		
		HSSFCell cell22 = rowHeader.createCell(22);
		cell22.setCellValue(new HSSFRichTextString(altas.getObservaciones()));
		cell22.setCellStyle(styleAll);
		
		if (altas.getPresSsaludFecha() != null){
			HSSFCell cell23 = rowHeader.createCell(23);
			cell23.setCellValue(new HSSFRichTextString(sdf.format(altas.getPresSsaludFecha())));
			cell23.setCellStyle(styleAll);
		}else{
			HSSFCell cell23 = rowHeader.createCell(23);
			cell23.setCellValue(new HSSFRichTextString(""));
			cell23.setCellStyle(styleAll);
		}
		
		

		HSSFCell cell24 = rowHeader.createCell(24);
		cell24.setCellValue(new HSSFRichTextString(altas.getProcesoUsr()));
		cell24.setCellStyle(styleAll);
		
		HSSFCell cell25 = rowHeader.createCell(25);
		cell25.setCellValue(new HSSFRichTextString(altas.getDiscapacitado() == 0 ? "NO" : "SI"));
		cell25.setCellStyle(styleAll);
		
		HSSFCell cell26 = rowHeader.createCell(26);
		cell26.setCellValue(new HSSFRichTextString(altas.getNumeroDocumento()));
		cell26.setCellStyle(styleAll);
	

		HSSFCell cell27 = rowHeader.createCell(27);
		cell27.setCellValue(new HSSFRichTextString(altas.getTipoDocumento()));
		cell27.setCellStyle(styleAll);
	

		HSSFCell cell28 = rowHeader.createCell(28);
		cell28.setCellValue(new HSSFRichTextString(altas.getCalle()));
		cell28.setCellStyle(styleAll);
		

		HSSFCell cell29 = rowHeader.createCell(29);
		cell29.setCellValue(new HSSFRichTextString(altas.getPiso()));
		cell29.setCellStyle(styleAll);
		

		HSSFCell cell30 = rowHeader.createCell(30);
		cell30.setCellValue(new HSSFRichTextString(altas.getDepto()));
		cell30.setCellStyle(styleAll);


		HSSFCell cell31 = rowHeader.createCell(31);
		cell31.setCellValue(new HSSFRichTextString(altas.getOficina()));
		cell31.setCellStyle(styleAll);
		

		HSSFCell cell32 = rowHeader.createCell(32);
		cell32.setCellValue(new HSSFRichTextString(altas.getCodigoPostal()));
		cell32.setCellStyle(styleAll);
		

		HSSFCell cell33 = rowHeader.createCell(33);
		cell33.setCellValue(new HSSFRichTextString(altas.getBarrio()));
		cell33.setCellStyle(styleAll);
		

		HSSFCell cell34 = rowHeader.createCell(34);
		cell34.setCellValue(new HSSFRichTextString(altas.getTelefono()));
		cell34.setCellStyle(styleAll);
		
		HSSFCell cell35 = rowHeader.createCell(35);
		cell35.setCellValue(new HSSFRichTextString(altas.getProvinciaDesc()));
		cell35.setCellStyle(styleAll);
		
		HSSFCell cell36 = rowHeader.createCell(36);
		cell36.setCellValue(new HSSFRichTextString(altas.getLocalidadDesc()));
		cell36.setCellStyle(styleAll);
		
		if (altas.getBajaFecha() != null){
			HSSFCell cell37 = rowHeader.createCell(37);
			cell37.setCellValue(new HSSFRichTextString(sdf.format(altas.getBajaFecha())));
			cell37.setCellStyle(styleAll);
			
		}else{
			HSSFCell cell37 = rowHeader.createCell(37);
			cell37.setCellValue(new HSSFRichTextString(""));
			cell37.setCellStyle(styleAll);
			
		}
		
		HSSFCell cell38 = rowHeader.createCell(38);
		cell38.setCellValue(new HSSFRichTextString(altas.getMotivoBajaDesc()));
		cell38.setCellStyle(styleAll);
		
		
		HSSFCell cell39 = rowHeader.createCell(39);
		cell39.setCellValue(new HSSFRichTextString(altas.getRevistaDesc()));
		cell39.setCellStyle(styleAll);
		
		HSSFCell cell40 = rowHeader.createCell(40);
		cell40.setCellValue(new HSSFRichTextString(altas.getCategoria()));
		cell40.setCellStyle(styleAll);
		
		HSSFCell cell41 = rowHeader.createCell(41);
		cell41.setCellValue(new HSSFRichTextString(altas.getPlanDesc()));
		cell41.setCellStyle(styleAll);

		HSSFCell cell42 = rowHeader.createCell(42);
		cell42.setCellValue(new HSSFRichTextString(altas.getTercerizadoraDesc()));
		cell42.setCellStyle(styleAll);

		
	}
	

	

	private static HSSFWorkbook generarAltas(List<NovedadPadronConsolidadoAltas> list , HSSFWorkbook wb  , HSSFSheet sheet ) {


		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);


		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("NOVEDAD   "));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("CODIGO MOVIMIENTO"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("PERIODO"));
		cell2H.setCellStyle(styleBold);
		
		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("CUIL TITULAR   "));
		cell3H.setCellStyle(styleBold);
		
		
		HSSFCell cell4H = rowHeader.createCell(4);
		cell4H.setCellValue(new HSSFRichTextString("INTE"));
		cell4H.setCellStyle(styleBold);
		

		HSSFCell cell5H = rowHeader.createCell(5);
		cell5H.setCellValue(new HSSFRichTextString("SUCURSAL"));
		cell5H.setCellStyle(styleBold);
		

		HSSFCell cell6H = rowHeader.createCell(6);
		cell6H.setCellValue(new HSSFRichTextString("ID OSPIM BAJA FECHA"));
		cell6H.setCellStyle(styleBold);
		

		HSSFCell cell7H = rowHeader.createCell(7);
		cell7H.setCellValue(new HSSFRichTextString("ID UOMA BAJA FECHA"));
		cell7H.setCellStyle(styleBold);
		

		HSSFCell cell8H = rowHeader.createCell(8);
		cell8H.setCellValue(new HSSFRichTextString("ID AMTIMA BAJA FECHA"));
		cell8H.setCellStyle(styleBold);
		

		HSSFCell cell9H = rowHeader.createCell(9);
		cell9H.setCellValue(new HSSFRichTextString("APELLIDO            "));
		cell9H.setCellStyle(styleBold);
		
		

		HSSFCell cell10H = rowHeader.createCell(10);
		cell10H.setCellValue(new HSSFRichTextString("NOMBRE             "));
		cell10H.setCellStyle(styleBold);
		
		
		HSSFCell cell11H = rowHeader.createCell(11);
		cell11H.setCellValue(new HSSFRichTextString("TIPO DOCUMENTO"));
		cell11H.setCellStyle(styleBold);
		
		
		HSSFCell cell12H = rowHeader.createCell(12);
		cell12H.setCellValue(new HSSFRichTextString("DOCUMENTO"));
		cell12H.setCellStyle(styleBold);
		

		HSSFCell cell13H = rowHeader.createCell(13);
		cell13H.setCellValue(new HSSFRichTextString("SEXO"));
		cell13H.setCellStyle(styleBold);
		
		
		HSSFCell cell14H = rowHeader.createCell(14);
		cell14H.setCellValue(new HSSFRichTextString("CUIT"));
		cell14H.setCellStyle(styleBold);
		
		HSSFCell cell15H = rowHeader.createCell(15);
		cell15H.setCellValue(new HSSFRichTextString("RAZON SOCIAL             "));
		cell15H.setCellStyle(styleBold);
		
		
		
		HSSFCell cell16H = rowHeader.createCell(16);
		cell16H.setCellValue(new HSSFRichTextString("FECHA NACIMIENTO"));
		cell16H.setCellStyle(styleBold);
		

		HSSFCell cell17H = rowHeader.createCell(17);
		cell17H.setCellValue(new HSSFRichTextString("ESTADO CIVIL"));
		cell17H.setCellStyle(styleBold);
		

		HSSFCell cell18H = rowHeader.createCell(18);
		cell18H.setCellValue(new HSSFRichTextString("NACIONALIDAD"));
		cell18H.setCellStyle(styleBold);
		
		HSSFCell cell19H = rowHeader.createCell(19);
		cell19H.setCellValue(new HSSFRichTextString("PARENTESCO  "));
		cell19H.setCellStyle(styleBold);

		HSSFCell cell20H = rowHeader.createCell(20);
		cell20H.setCellValue(new HSSFRichTextString("SECCIONAL"));
		cell20H.setCellStyle(styleBold);
		
		
		HSSFCell cell21H = rowHeader.createCell(21);
		cell21H.setCellValue(new HSSFRichTextString("VIGEN FECHA"));
		cell21H.setCellStyle(styleBold);
		
		HSSFCell cell22H = rowHeader.createCell(22);
		cell22H.setCellValue(new HSSFRichTextString("OBSERVACIONES"));
		cell22H.setCellStyle(styleBold);
		
		
		HSSFCell cell23H = rowHeader.createCell(23);
		cell23H.setCellValue(new HSSFRichTextString("FECHA PRESS SSS"));
		cell23H.setCellStyle(styleBold);
		
		
		HSSFCell cell24H = rowHeader.createCell(24);
		cell24H.setCellValue(new HSSFRichTextString("ALTA USER"));
		cell24H.setCellStyle(styleBold);
		

		HSSFCell cell25H = rowHeader.createCell(25);
		cell25H.setCellValue(new HSSFRichTextString("DISCAPACITADO"));
		cell25H.setCellStyle(styleBold);
		
		HSSFCell cell26H = rowHeader.createCell(26);
		cell26H.setCellValue(new HSSFRichTextString("NUMERO DOCUMENTO"));
		cell26H.setCellStyle(styleBold);
		
		HSSFCell cell27H = rowHeader.createCell(27);
		cell27H.setCellValue(new HSSFRichTextString("DOMOCILIO TIPO"));
		cell27H.setCellStyle(styleBold);
		
		
		HSSFCell cell28H = rowHeader.createCell(28);
		cell28H.setCellValue(new HSSFRichTextString("CALLE"));
		cell28H.setCellStyle(styleBold);


		HSSFCell cell29H = rowHeader.createCell(29);
		cell29H.setCellValue(new HSSFRichTextString("PISO"));
		cell29H.setCellStyle(styleBold);


		HSSFCell cell30H = rowHeader.createCell(30);
		cell30H.setCellValue(new HSSFRichTextString("DEPTO"));
		cell30H.setCellStyle(styleBold);


		HSSFCell cell31H = rowHeader.createCell(31);
		cell31H.setCellValue(new HSSFRichTextString("OFICINA"));
		cell31H.setCellStyle(styleBold);
		

		HSSFCell cell32H = rowHeader.createCell(32);
		cell32H.setCellValue(new HSSFRichTextString("CODIGO POSTAL"));
		cell32H.setCellStyle(styleBold);
		
		HSSFCell cell33H = rowHeader.createCell(33);
		cell33H.setCellValue(new HSSFRichTextString("BARRIO"));
		cell33H.setCellStyle(styleBold);
		
		HSSFCell cell34H = rowHeader.createCell(34);
		cell34H.setCellValue(new HSSFRichTextString("TELEFONO"));
		cell34H.setCellStyle(styleBold);
		

		HSSFCell cell35H = rowHeader.createCell(35);
		cell35H.setCellValue(new HSSFRichTextString("PROVINCIA"));
		cell35H.setCellStyle(styleBold);
		
		HSSFCell cell36H = rowHeader.createCell(36);
		cell36H.setCellValue(new HSSFRichTextString("LOCALIDAD"));
		cell36H.setCellStyle(styleBold);
		
		HSSFCell cell37H = rowHeader.createCell(37);
		cell37H.setCellValue(new HSSFRichTextString("BAJA FECHA"));
		cell37H.setCellStyle(styleBold);
		
		
		HSSFCell cell38H = rowHeader.createCell(38);
		cell38H.setCellValue(new HSSFRichTextString("MOTIVO BAJA"));
		cell38H.setCellStyle(styleBold);
		

		HSSFCell cell39H = rowHeader.createCell(39);
		cell39H.setCellValue(new HSSFRichTextString("SIT. REVISTA                 "));
		cell39H.setCellStyle(styleBold);
		
		HSSFCell cell40H = rowHeader.createCell(40);
		cell40H.setCellValue(new HSSFRichTextString("CATEGORIA                "));
		cell40H.setCellStyle(styleBold);
		
		
		HSSFCell cell41H = rowHeader.createCell(41);
		cell41H.setCellValue(new HSSFRichTextString("ID PLAN"));
		cell41H.setCellStyle(styleBold);
		
		HSSFCell cell42H = rowHeader.createCell(42);
		cell42H.setCellValue(new HSSFRichTextString("TERCERIZADORA        "));
		cell42H.setCellStyle(styleBold);
		
		
		
		if (list == null || list.isEmpty()) {
			return wb;
		}

		int total = 0;

		for (NovedadPadronConsolidadoAltas altas : list) {
			index++;
			 crearAltas(sheet, index, altas, styleBold,
					styleAll, styleDate, styleMoney);
			total =  total + 1;
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(1);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(2);
		cell1.setCellValue(total);
		cell1.setCellStyle(styleAll);
		


		index++;
		sheet.createRow(index);

		for (int i = 0; i < 600; i++) {
			sheet.autoSizeColumn((short)i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 300);
		}
		
		return wb;
	}


	

	private static HSSFWorkbook generarInconsistencias(List<NovedadPadronConsolidadoInconsistencia> list , HSSFWorkbook wb  , HSSFSheet sheet ) {
	

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);


		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);
		
		
		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("NOVEDAD"));
		cell0H.setCellStyle(styleBold);
		
		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("CODIGO MOVIMIENTO"));
		cell1H.setCellStyle(styleBold);


		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("PERIODO"));
		cell2H.setCellStyle(styleBold);
		
		
		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("CUIL TITULAR"));
		cell3H.setCellStyle(styleBold);
		
		HSSFCell cell4H = rowHeader.createCell(4);
		cell4H.setCellValue(new HSSFRichTextString("INTE"));
		cell4H.setCellStyle(styleBold);
		
		HSSFCell cell5H = rowHeader.createCell(5);
		cell5H.setCellValue(new HSSFRichTextString("DOCUMENTO TIPO"));
		cell5H.setCellStyle(styleBold);
		
		HSSFCell cell6H = rowHeader.createCell(6);
		cell6H.setCellValue(new HSSFRichTextString("DOCUMENTO NUMERO"));
		cell6H.setCellStyle(styleBold);

		
		HSSFCell cell7H = rowHeader.createCell(7);
		cell7H.setCellValue(new HSSFRichTextString("NOMBRE Y APELLIDO"));
		cell7H.setCellStyle(styleBold);

		
		if (list == null || list.isEmpty()) {
			return wb;
		}

		int total = 0;

		for (NovedadPadronConsolidadoInconsistencia inc : list) {
			index++;
			crearInconsistencia(sheet, index, inc, styleBold,
					styleAll, styleDate, styleMoney);
			total =  total + 1;
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(2);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(3);
		cell1.setCellValue(total);
		cell1.setCellStyle(styleAll);
		
		
		
		index++;
		sheet.createRow(index);

		for (int i = 0; i < 600; i++) {
			sheet.autoSizeColumn((short)i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 300);
		}
		
		
		return wb;
	}

	private static void crearInconsistencia(HSSFSheet sheet, int index,
			NovedadPadronConsolidadoInconsistencia inc, HSSFCellStyle styleBold,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(inc.getId());
		cell0.setCellStyle(styleAll);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(inc.getCodigoMovimiento()));
		cell1.setCellStyle(styleAll);
		
		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(sdf.format(inc.getProcesoFecha())));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(inc.getCuilTitular()));
		cell3.setCellStyle(styleAll);	
		
		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(inc.getInte());
		cell4.setCellStyle(styleAll);	
		
		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString(inc.getTipoDocumento()));
		cell5.setCellStyle(styleAll);	
		
		HSSFCell cell6 = rowHeader.createCell(6);
		cell6.setCellValue(new HSSFRichTextString(inc.getNumeroDocumento()));
		cell6.setCellStyle(styleAll);	
		
		HSSFCell cell7 = rowHeader.createCell(7);
		cell7.setCellValue(new HSSFRichTextString(inc.getNombre()));
		cell7.setCellStyle(styleAll);	
		
		
	}
	
	
}
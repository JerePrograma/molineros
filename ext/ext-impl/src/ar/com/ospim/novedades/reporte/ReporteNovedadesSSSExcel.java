package ar.com.ospim.novedades.reporte;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadas;
import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadasCab;
import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadasDet;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.novedades.beans.NovedadTotal;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;

public class ReporteNovedadesSSSExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteNovedadesSSSExcel.class);

	public static HSSFWorkbook generaReporteNovedadSSS(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String cuil_titu = null;
		String cuil = null;
		String tipoDoc = null;
		String nroDoc = null;
		String apellido = null;
		String nombre = null;
		String tipoNov = null;
		String tipoNovDesc = null;
		String tipoOri = null;
		String fechaProc = null;
//		Integer mesHasta = null;
//		Integer anioHasta = null;
//		int cantResultados = 0;
				
		try {
			/*Filtro Novedades SSS */
			
			if (null != req.getParameter("cuil_titular")) {
				cuil_titu = req.getParameter("cuil_titular").trim().length() > 0 ? req
						.getParameter("cuil_titular") : null;
			}
			if (null != req.getParameter("cuil")) {
				cuil = req.getParameter("cuil").trim().length() > 0 ? req
						.getParameter("cuil") : null;
			}
			if (null != req.getParameter("tipoDoc")) {
				tipoDoc = req.getParameter("tipoDoc").trim().length() > 0 ? req
						.getParameter("tipoDoc") : null;
			}
			if (null != req.getParameter("nroDoc")) {
				nroDoc = req.getParameter("nroDoc").trim().length() > 0 ? req
						.getParameter("nroDoc") : null;
			}	
			
			if (null != req.getParameter("apellido")) {
				apellido = req.getParameter("apellido").trim()
						.length() > 0 ? req.getParameter("apellido")
						: null;
			}
			if (null != req.getParameter("nombre")) {
				nombre = req.getParameter("nombre").trim().length() > 0 ? req
						.getParameter("nombre") : null;
			}
			
			if (null != req.getParameter("tipoNov")) {
				tipoNov = req.getParameter("tipoNov").trim().length() > 0 ? req
						.getParameter("tipoNov") : null;
			}
			
			if (null != req.getParameter("tipoNovDesc")) {
				tipoNovDesc = req.getParameter("tipoNovDesc").trim().length() > 0 ? req
						.getParameter("tipoNovDesc") : null;
			}
			
			if (null != req.getParameter("tipoOri")) {
				tipoOri= req.getParameter("tipoOri").trim().length() > 0 ? req
						.getParameter("tipoOri") : null;
			}

			if (null != req.getParameter("fechaProc")) {
				fechaProc = req.getParameter("fechaProc").trim().length() > 0 ? req
						.getParameter("fechaProc") : null;
			}
			Calendar fechaHasta = Calendar.getInstance();
			if(fechaProc != null ){
				fechaHasta.setTime(format.parse(fechaProc));
			}else{
				fechaHasta = null;
			}
			List<NovedadTotal> busquedaNove=null;
			
//			String fecha = format.format(new Date(System.currentTimeMillis()));

			busquedaNove = NovedadesServiceUtil.getInstance().getNovedadesXls(cuil_titu, cuil, tipoDoc, nroDoc, apellido, nombre, tipoNov, tipoOri, fechaHasta!=null?fechaHasta.getTime():null);

//			cantResultados = busquedaNove.get(0).getTotal_registros();

			return generarReporte(fechaHasta, tipoNovDesc, busquedaNove);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte novedades empleadores", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Calendar fechaHasta, String tipoNovDesc, List<NovedadTotal> novedades) {
		
//		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, fechaHasta, tipoNovDesc);
			index++;
			for (NovedadTotal nov : novedades) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(nov.getCuil_titular()));
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(nov.getCuil()));
				cell1.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(nov.getApellido_nombre().toUpperCase()));
				cell2.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(nov.getParentescoDesc()));
				cell3.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(nov.getCuit_empleador()!=null?nov.getCuit_empleador():"No informado"));
				cell4.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(nov.getCodigo_movimiento()));
				cell5.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(nov.getDetalle_novedad()));
				cell6.setCellStyle(styleAllWithBorder);
				
				
			}

			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			sheet.autoSizeColumn((short) 4);
			sheet.autoSizeColumn((short) 5);
			sheet.autoSizeColumn((short) 6);

		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, Calendar fechaHasta, String tipoNovDesc) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Novedades de la SSS"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = null, descripcionTipoNov=null;
		if(fechaHasta != null){
			aux = new StringBuffer("Período: " + sdf2.format(fechaHasta.getTime()) );
		}else{
			aux = new StringBuffer("Período: TODOS");
		}
		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);
		HSSFCell cell2 = row2.createCell(0);
		if(tipoNovDesc != null){
			descripcionTipoNov = new StringBuffer("Tipos de Novedades: " + tipoNovDesc );
		}else{
			descripcionTipoNov = new StringBuffer("Tipos de Novedades: TODOS");
		}
		cell2.setCellValue(new HSSFRichTextString(descripcionTipoNov.toString()));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
		
		HSSFRow row3 = sheet.createRow(index++);

		HSSFCell cell3 = row3.createCell(0);
		cell3.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell3.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

		index = index + 2;
		HSSFRow row4 = sheet.createRow(index);

		int column = 0;

		HSSFCell cell20 = row4.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row4.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Cuil"));
		cell21.setCellStyle(styleHeaderEnca2);

		HSSFCell cell22 = row4.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell23 = row4.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Parentesco"));
		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row4.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("CUIT Empleador"));
		cell24.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell25 = row4.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Tipo Novedad"));
		cell25.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell26 = row4.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("Detalle Novedad"));
		cell26.setCellStyle(styleHeaderEnca2);

		return index;
	}
	
	
	public static HSSFWorkbook generaEstadisticaNovedadSSSProcesadas(HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

		List<ReporteNovedadesSSSProcesadas> estadistica = null;
		
		try {
			estadistica = NovedadesServiceUtil.getInstance().getEstadisticaNovedadesSSSProcesadas();
		} catch (SystemException e) {
			_log.error(e);
		} 
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeaderEstadistica(wb, sheet);
			index++;
			for (ReporteNovedadesSSSProcesadas estad : estadistica) {
				
				ReporteNovedadesSSSProcesadasCab cab = estad.getCabecera();
				ReporteNovedadesSSSProcesadasDet det = estad.getDetalle();
				
				int column = 0;
				int yaVigentesEnPortalMolinero = det.getTotalNovedadesSSS() - det.getTotalNovedadesAProcesar();
				int pendientes = det.getTotalNovedadesAcumuladas()-yaVigentesEnPortalMolinero-det.getTotalNovedadesResueltas();
				
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(sdf.format(cab.getFechaProceso())));
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(det.getPoblacion())));
				cell1.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(String.valueOf(det.getTotalNovedadesSSS())));
				cell2.setCellStyle(styleAllWithBorder);
				
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(String.valueOf(yaVigentesEnPortalMolinero)));
				cell4.setCellStyle(styleAllWithBorder);
				
				int Novedadesresueltas = det.getTotalNovedadesResueltas() + det.getTotalNovedadesInconsistentes();
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(String.valueOf(Novedadesresueltas)));
				cell5.setCellStyle(styleAllWithBorder);
		
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(String.valueOf(det.getTotalNovedadesResueltas())));
				cell6.setCellStyle(styleAllWithBorder);
				
				int sinProcesar = det.getTotalNovedadesAProcesar() -  det.getTotalNovedadesResueltas();
				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(String.valueOf(sinProcesar)));
				cell7.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell8 = row.createCell(column++);
				cell8.setCellValue(new HSSFRichTextString(String.valueOf(det.getTotalNovedadesInconsistentes())));
				cell8.setCellStyle(styleAllWithBorder);
				

				
				HSSFCell cell9 = row.createCell(column++);
			//	cell7.setCellValue(new HSSFRichTextString(String.valueOf((det.getTotalNovedadesResueltas() * 100) / det.getTotalNovedadesSSS())));
				int indicador = (( yaVigentesEnPortalMolinero + Novedadesresueltas ) * 100)/ det.getTotalNovedadesSSS();
				cell9.setCellValue(new HSSFRichTextString(String.valueOf((indicador))));
				
				
				cell9.setCellStyle(styleAllWithBorder);
				
				
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

		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		
		return wb;
		
	}
		
	private static int createHeaderEstadistica(HSSFWorkbook wb, HSSFSheet sheet) {
				
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Estadística de Procesamiento de las Novedades de la SSS"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);

		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell20 = row3a.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("Período"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row3a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Población"));
		cell21.setCellStyle(styleHeaderEnca2);

		HSSFCell cell22 = row3a.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Total Nov. SSS Período"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell24 = row3a.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("Total Ya Vigentes en Padrón"));
		cell24.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell25 = row3a.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Total a Procesar"));
		cell25.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell26 = row3a.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("Total Procesadas"));
		cell26.setCellStyle(styleHeaderEnca2);
		

		HSSFCell cell27 = row3a.createCell(column++);
		cell27.setCellValue(new HSSFRichTextString("Sin Procesar"));
		cell27.setCellStyle(styleHeaderEnca2);
		
		

		HSSFCell cell28 = row3a.createCell(column++);
		cell28.setCellValue(new HSSFRichTextString("Inconsistentes"));
		cell28.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell29 = row3a.createCell(column++);
		cell29.setCellValue(new HSSFRichTextString("Indicador"));
		cell29.setCellStyle(styleHeaderEnca2);

		return index;
	}
		
}

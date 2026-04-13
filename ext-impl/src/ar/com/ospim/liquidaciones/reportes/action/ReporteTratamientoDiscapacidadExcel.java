package ar.com.ospim.liquidaciones.reportes.action;

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

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.TipoDiscapacidad;
import ar.com.ospim.liquidaciones.beans.TratamiendoDiscapacidad;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.services.BusquedaLiquidacionServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteTratamientoDiscapacidadExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteTratamientoDiscapacidadExcel.class);
	private static List<TipoDiscapacidad> tiposDisc =  TraeListasServiceUtil.getTiposDiscapacidad();
	
	public static HSSFWorkbook generaReporteTratamientoDiscapacidadExcel(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		
		String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
				"periodoDesdeMesAnio");
		Date periodoDesde = null;
		try {
			periodoDesde = formatoDePeriodos.parse(Integer
					.parseInt(periodoDesdeMesAnio.substring(0, 1))
					+ 1 + "/" + periodoDesdeMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoDesde = null;
		}
		String periodoHastaMesAnio = ParamUtil.getString(renderRequest,
				"periodoHastaMesAnio");
		Date periodoHasta = null;
		try {
			periodoHasta = formatoDePeriodos.parse(Integer
					.parseInt(periodoHastaMesAnio.substring(0, 1))
					+ 1 + "/" + periodoHastaMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoHasta = null;
		}		
	
		boolean sur = ParamUtil.getBoolean(renderRequest, "sur", false);
		String ciex = ParamUtil.getString(renderRequest, "ciex",null);
		String codigoPrestacion = ParamUtil.getString(renderRequest, "codigo_prestacion",null);		
		String cuitPrestador = ParamUtil.getString(renderRequest, "cuit_entidad",null);
		String nombrePrestador = ParamUtil.getString(renderRequest, "entidad",null);
		boolean rangoEtario = ParamUtil.getBoolean(renderRequest, "rango_etario", false);
		String tipoDiscapacidad = ParamUtil.getString(renderRequest, "tiposDiscSel",null);
		
		List<TratamiendoDiscapacidad> fichas = new ArrayList<TratamiendoDiscapacidad>();
		
		try {
			fichas = BusquedaLiquidacionServiceUtil.getReporteTratamiendoDiscapacidad(periodoDesde, periodoHasta, sur,
					ciex, codigoPrestacion, cuitPrestador, rangoEtario, tipoDiscapacidad);
			
		}catch (Exception e){
			_log.error("Error al generar reporte tratamiendo discapacidad", e);
			return null;
		}
		
		return generarReporteTratamientoDiscapacidad(fichas,periodoDesde, periodoHasta, sur, ciex, 
				codigoPrestacion, cuitPrestador, nombrePrestador, rangoEtario, tipoDiscapacidad);
	}
	
	private static HSSFWorkbook generarReporteTratamientoDiscapacidad(
			List<TratamiendoDiscapacidad> list, Date periodoDesde, Date periodoHasta, boolean sur, 	String ciex,
			String codigoPrestacion, String cuitPrestador, String nombrePrestador, boolean rangoEtario, String tipoDiscapacidad) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Ficha");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		int index = createHeader(wb, sheet, periodoDesde, periodoHasta, sur, ciex, codigoPrestacion, cuitPrestador, nombrePrestador, rangoEtario, tipoDiscapacidad);
//		index++;
		
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleNumber= getStyleNumber(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		// si rangoEtario es Falso entonces sale Reporte Tratamiento Discapacidad
		if (rangoEtario == false ){ 
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
					
	//		int index = 0;		
			int col = -1;

//			HSSFRow rowHeaderANT = sheet.createRow(index);		
//			HSSFCell cell0HA = rowHeaderANT.createCell(0);
//			
//			cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
//			cell0HA.setCellStyle(styleBold);
			
			index++;
			HSSFRow rowHeader = sheet.createRow(index);
			
			HSSFCell cell0H = rowHeader.createCell(++col);
			cell0H.setCellValue(new HSSFRichTextString("ID OSPIM"));
			cell0H.setCellStyle(styleBold);

			HSSFCell cell1H = rowHeader.createCell(++col);
			cell1H.setCellValue(new HSSFRichTextString("CUIL"));
			cell1H.setCellStyle(styleBold);
			
			HSSFCell cell111H = rowHeader.createCell(++col);
			cell111H.setCellValue(new HSSFRichTextString("Beneficiario"));
			cell111H.setCellStyle(styleBold);

			HSSFCell cell3H = rowHeader.createCell(++col);
			cell3H.setCellValue(new HSSFRichTextString("Edad"));
			cell3H.setCellStyle(styleBold);

			HSSFCell cell4H = rowHeader.createCell(++col);
			cell4H.setCellValue(new HSSFRichTextString("Provincia"));
			cell4H.setCellStyle(styleBold);
			
			HSSFCell cell41H = rowHeader.createCell(++col);
			cell41H.setCellValue(new HSSFRichTextString("Diagnóstico"));
			cell41H.setCellStyle(styleBold);
		
			HSSFCell cell5H = rowHeader.createCell(++col);
			cell5H.setCellValue(new HSSFRichTextString("CIEX"));
			cell5H.setCellStyle(styleBold);

			HSSFCell cell6H = rowHeader.createCell(++col);
			cell6H.setCellValue(new HSSFRichTextString("Tipo Discapacidad"));
			cell6H.setCellStyle(styleBold);
			
			HSSFCell cell7H = rowHeader.createCell(++col);
			cell7H.setCellValue(new HSSFRichTextString("CUIT"));
			cell7H.setCellStyle(styleBold);

			HSSFCell cell8H = rowHeader.createCell(++col);
			cell8H.setCellValue(new HSSFRichTextString("Prestador"));
			cell8H.setCellStyle(styleBold);

			HSSFCell cell9H = rowHeader.createCell(++col);
			cell9H.setCellValue(new HSSFRichTextString("Periodo Desde"));
			cell9H.setCellStyle(styleBold);

			HSSFCell cell10H = rowHeader.createCell(++col);
			cell10H.setCellValue(new HSSFRichTextString("Periodo Hasta"));
			cell10H.setCellStyle(styleBold);
			
			HSSFCell cell11H = rowHeader.createCell(++col);
			cell11H.setCellValue(new HSSFRichTextString("SUR"));
			cell11H.setCellStyle(styleBold);

			HSSFCell cell12H = rowHeader.createCell(++col);
			cell12H.setCellValue(new HSSFRichTextString("Codigo"));
			cell12H.setCellStyle(styleBold);

			HSSFCell cell13H = rowHeader.createCell(++col);
			cell13H.setCellValue(new HSSFRichTextString("Prestación"));
			cell13H.setCellStyle(styleBold);
			
			HSSFCell cell14H = rowHeader.createCell(++col);
			cell14H.setCellValue(new HSSFRichTextString("Importe Total"));
			cell14H.setCellStyle(styleBold);
			index++;
			
			for(TratamiendoDiscapacidad tratamiento: list){
				index=crearDatosFicha(sheet, tratamiento, index, styleAll,
						styleNumber, styleNumber, styleNumber, styleNumber, rangoEtario);
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
			
			
		}
		
		else{ // sale reporte Tratamiento Discapacidad por Edad
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
			
//			int index = 0;		
			int col = -1;
//			HSSFRow rowHeaderANT = sheet.createRow(index);		
//			HSSFCell cell0HA = rowHeaderANT.createCell(0);
//			
//			cell0HA.setCellValue(new HSSFRichTextString(titulo1.toString()));
//			cell0HA.setCellStyle(styleBold);
			
//			index++;
			HSSFRow rowHeader = sheet.createRow(index);
			
			HSSFCell cell0H = rowHeader.createCell(++col);
			cell0H.setCellValue(new HSSFRichTextString("ID"));
			cell0H.setCellStyle(styleBold);

			HSSFCell cell1H = rowHeader.createCell(++col);
			cell1H.setCellValue(new HSSFRichTextString("Cantidad"));
			cell1H.setCellStyle(styleBold);

			HSSFCell cell3H = rowHeader.createCell(++col);
			cell3H.setCellValue(new HSSFRichTextString("Grupo"));
			cell3H.setCellStyle(styleBold);
			index++;
			
			for(TratamiendoDiscapacidad tratamiento: list){
				index=crearDatosFicha(sheet, tratamiento, index, styleAll,
						styleNumber, styleNumber, styleNumber, styleNumber, rangoEtario);
				
			}
				
			index++;
			sheet.createRow(index);
				
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			
		}
		
		return wb;
	}

	private static int crearDatosFicha(HSSFSheet sheet,TratamiendoDiscapacidad tratamiento, 
			int index, HSSFCellStyle styleAll,HSSFCellStyle styleBold,HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNumber, boolean rangoEtario) {
		
		if (rangoEtario == false){ 
			
		int col = -1;
		HSSFRow rowHeader = sheet.createRow(index++);
			
		HSSFCell cell0 = rowHeader.createCell(++col);
		cell0.setCellValue(tratamiento.getIdOspim());
		cell0.setCellStyle(styleNumber);

		HSSFCell cell1 = rowHeader.createCell(++col);
		cell1.setCellValue(new HSSFRichTextString(tratamiento.getCuil()
				.toString()));
		cell1.setCellStyle(styleNumber);
		
		HSSFCell cell121 = rowHeader.createCell(++col);
		cell121.setCellValue(new HSSFRichTextString(tratamiento.getBeneficiario()));
		cell121.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(++col);
		cell2.setCellValue(tratamiento.getEdad());
		cell2.setCellStyle(styleNumber);
		
		HSSFCell cell3 = rowHeader.createCell(++col);
		cell3.setCellValue(new HSSFRichTextString(tratamiento.getProvincia()));
		cell3.setCellStyle(styleAll);
		
		HSSFCell cell31 = rowHeader.createCell(++col);
		cell31.setCellValue(new HSSFRichTextString(tratamiento.getDiagnostico()));
		cell31.setCellStyle(styleAll);
		
		HSSFCell cell4 = rowHeader.createCell(++col); 
		cell4.setCellValue(new HSSFRichTextString(tratamiento.getCieDiez()));
		cell4.setCellStyle(styleNumber);
		
		HSSFCell cell5 = rowHeader.createCell(++col); 
		/* recuperar las descripciones de los ids de tipos de disc.*/
		TipoDiscapacidad td;
		int id, pos;
		String tiposDiscapDescripcion = "";
		
		if(tratamiento.getTiposDiscapacidades() != null && tratamiento.getTiposDiscapacidades().length() > 0){
			String[] tipDisAux = tratamiento.getTiposDiscapacidades().split(",");
			
			for (int i = 0; i < tipDisAux.length; i++) {
				id = Integer.parseInt(tipDisAux[i]);
				td = new TipoDiscapacidad(id, "");
				pos = tiposDisc.indexOf(td);
				td = tiposDisc.get(pos);
				tiposDiscapDescripcion = tiposDiscapDescripcion + td.getDescripcion() + ", ";
			}
			tiposDiscapDescripcion = tiposDiscapDescripcion.substring(0, tiposDiscapDescripcion.length()-2);
		}
		cell5.setCellValue(new HSSFRichTextString(tiposDiscapDescripcion));
		cell5.setCellStyle(styleNumber);
		
		HSSFCell cell6 = rowHeader.createCell(++col);
		cell6.setCellValue(new HSSFRichTextString(tratamiento.getCuit()));
		cell6.setCellStyle(styleNumber);
		
		HSSFCell cell7 = rowHeader.createCell(++col);
		cell7.setCellValue(new HSSFRichTextString(tratamiento.getPrestador()));
		cell7.setCellStyle(styleAll);
				
		HSSFCell cell8 = rowHeader.createCell(++col);
		cell8.setCellValue(new HSSFRichTextString(tratamiento.getPeriodoDesde()
				.toString()));
		cell8.setCellStyle(styleDate);
		
		HSSFCell cell9 = rowHeader.createCell(++col);
		cell9.setCellValue(new HSSFRichTextString(tratamiento.getPeriodoHasta()
				.toString()));
		cell9.setCellStyle(styleDate);
		
		HSSFCell cell10 = rowHeader.createCell(++col);
		cell10.setCellValue(new HSSFRichTextString(tratamiento.getSur()));
		cell10.setCellStyle(styleAll);
		
		HSSFCell cell11 = rowHeader.createCell(++col);
		cell11.setCellValue(new HSSFRichTextString(tratamiento.getCodigo()));
		cell11.setCellStyle(styleAll);
		
		HSSFCell cell12 = rowHeader.createCell(++col);
		cell12.setCellValue(new HSSFRichTextString(tratamiento.getPrestacion()));
		cell12.setCellStyle(styleAll);
		
		HSSFCell cell13 = rowHeader.createCell(++col);
		cell13.setCellValue(tratamiento.getImporteTotal()!=null?tratamiento.getImporteTotal().doubleValue():0);
		cell13.setCellStyle(styleMoney);
	
		return index++;
		}
		
		else { 
			
			int col = -1;
			HSSFRow rowHeader = sheet.createRow(index++);
				
			HSSFCell cell0 = rowHeader.createCell(++col);
			cell0.setCellValue(tratamiento.getId());
			cell0.setCellStyle(styleNumber);

			HSSFCell cell1 = rowHeader.createCell(++col);
			cell1.setCellValue(tratamiento.getCantidad());
			cell1.setCellStyle(styleNumber);

			HSSFCell cell2 = rowHeader.createCell(++col);
			cell2.setCellValue(new HSSFRichTextString(tratamiento.getGrupo()
					.toString()));
			cell2.setCellStyle(styleNumber);
			
			return index++;
			
		}
		
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			Date periodoDesde, Date periodoHasta, boolean sur, 	String ciex,
			String codigoPrestacion, String cuitPrestador, String nombrePrestador, 
			boolean rangoEtario, String tipoDiscapacidad) {
		
		String titulo = null;
		
		if (rangoEtario == false){
			titulo = "Reporte Estadístico de Tratamiento de Discapacidad.";
		}else {
			titulo = "Reporte Estadístico de Tratamiento de Discapacidad por Edad.";
		}
		
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString(titulo));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer("Destino: ");
		
		if(periodoDesde!=null){
			aux.append(" - Periodo Desde: ").append(sdf.format(periodoDesde));
		}
		if(periodoHasta!=null){
			aux.append(" - Periodo Hasta: ").append(sdf.format(periodoHasta));
		}
		if(sur==false){
			aux.append(" - SUR: ").append("No");
		}else { 
			aux.append(" - SUR: ").append("Si");
		}
		if(null!=ciex && ciex.trim().length()>0){
			aux.append(" - CIEX: ").append(ciex);
		}
		if(null!=codigoPrestacion && codigoPrestacion.trim().length()>0){
			aux.append(" - Codigo Prestación: ").append(codigoPrestacion);
		}
		if(null!=cuitPrestador && cuitPrestador.trim().length()>0){
			aux.append(" - CUIL Prestador: ").append(cuitPrestador);
		}
		if(null!=nombrePrestador && nombrePrestador.trim().length()>0){
			aux.append(" - Nombre Prestador: ").append(nombrePrestador);
		}
		if(rangoEtario==false){
			aux.append(" - Rango Etario: ").append("No");
		}else { 
			aux.append(" - Rango Etario: ").append("Si");
		}
		if(StringUtils.checkNotEmpty(tipoDiscapacidad)){
			
			TipoDiscapacidad td = new TipoDiscapacidad(Integer.valueOf(tipoDiscapacidad), "");
			
			int pos = tiposDisc.indexOf(td);
			
			td = tiposDisc.get(pos);
			
			aux.append(" - Tipo Discapacidad: ").append(td.getDescripcion());
		}else { 
			aux.append(" - Tipo Discapacidad: ").append("Todas");
		}
		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 10));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

//		HSSFCell cell20 = row3a.createCell(column++);
//		cell20.setCellValue(new HSSFRichTextString("ID"));
//		cell20.setCellStyle(styleHeaderEnca2);
//
//		HSSFCell cell21 = row3a.createCell(column++);
//		cell21.setCellValue(new HSSFRichTextString("DESTINO"));
//		cell21.setCellStyle(styleHeaderEnca2);

		

		return index;
	}
	
}
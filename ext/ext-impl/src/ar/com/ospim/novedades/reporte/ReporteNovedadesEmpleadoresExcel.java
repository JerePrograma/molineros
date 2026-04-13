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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.novedades.beans.NovedadEmpleadorTotal;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;
import ar.com.ospim.util.StringUtils;

public class ReporteNovedadesEmpleadoresExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteNovedadesEmpleadoresExcel.class);

	public static HSSFWorkbook generaReporteNovedadEmpleadores(
			HttpServletRequest req, HttpServletResponse res) {

//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		String tipoNov = null;
		String tipoOri = null;
		Integer mesHasta = null;
		Integer anioHasta = null;
		String tipoNoveEmpl = null;
		int cantResultados = 0;
		
		try {
			/*Filtro Novedades Empleadores */
			String periodoAux="";
			if (null != req.getParameter("mesHasta")) {
				periodoAux = req.getParameter("mesHasta"); 
				mesHasta = periodoAux != null ? Integer.parseInt(periodoAux) : null;
			}
			if (null != req.getParameter("anioHasta")) {
				periodoAux = req.getParameter("anioHasta"); 				
				anioHasta = periodoAux != null ? Integer.parseInt(periodoAux) : null;
			}
			if (null != req.getParameter("tipoNoveEmpl")) {
				tipoNoveEmpl = req.getParameter("tipoNoveEmpl").trim().length() > 0 ? req
						.getParameter("tipoNoveEmpl") : null;
			}
			Calendar fechaHasta = Calendar.getInstance();
			fechaHasta.set(Calendar.YEAR, anioHasta);
			fechaHasta.set(Calendar.MONTH, mesHasta-1);
			fechaHasta.set(Calendar.DATE, fechaHasta.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			int pagina_sel = ParamUtil.getInteger(req, "pagina", 1);
			pagina_sel--;
			
			List<NovedadEmpleadorTotal> busquedaEmp=null;
			
//			String fecha = format.format(new Date(System.currentTimeMillis()));

			busquedaEmp = NovedadesServiceUtil.getInstance().getNovedadesEmpleadoresXls(fechaHasta.getTime(), tipoNoveEmpl, pagina_sel);

			cantResultados = busquedaEmp.get(0).getTotal_registros();

			return generarReporte(tipoNoveEmpl, fechaHasta, busquedaEmp);
			
		} catch (Exception e) {
			_log.error("Error al generar reporte novedades empleadores", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(String tipoNovedadEmpl, Calendar fechaHasta, List<NovedadEmpleadorTotal> novedades) {
		
//		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllWithBorder = getStyleAllWithBorder(wb, 10);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		try {

			int index = createHeader(wb, sheet, tipoNovedadEmpl, fechaHasta);
			index++;
			for (NovedadEmpleadorTotal nov : novedades) {
				int column = 0;
				HSSFRow row = sheet.createRow(index++);
				
				HSSFCell cell0 = row.createCell(column++);
				cell0.setCellValue(new HSSFRichTextString(nov.getCuil_titular()));
				cell0.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell1 = row.createCell(column++);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(nov.getInte())));
				cell1.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell2 = row.createCell(column++);
				cell2.setCellValue(new HSSFRichTextString(nov.getApellido().toUpperCase() + ", "+nov.getNombre().toUpperCase()));
				cell2.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell3 = row.createCell(column++);
				cell3.setCellValue(new HSSFRichTextString(StringUtils.checkNotEmpty(nov.getDescSeccional()) ? nov.getDescSeccional()  :"" ) );
				cell3.setCellStyle(styleAllWithBorder);
				
				
				
				
//				HSSFCell cell3 = row.createCell(column++);
//				cell3.setCellValue(new HSSFRichTextString(sdf2.format(nov.getPeriodo())));
//				cell3.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell4 = row.createCell(column++);
				cell4.setCellValue(new HSSFRichTextString(nov.getEmpresa_cuit() ));
				cell4.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell5 = row.createCell(column++);
				cell5.setCellValue(new HSSFRichTextString(nov.getEmpresa_razon_social()));
				cell5.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell6 = row.createCell(column++);
				cell6.setCellValue(new HSSFRichTextString(nov.getEmpresa_planta()));
				cell6.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell7 = row.createCell(column++);
				cell7.setCellValue(new HSSFRichTextString(nov.getEmpresa_provincia()));
				cell7.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell8 = row.createCell(column++);
				cell8.setCellValue(new HSSFRichTextString(nov.getEmpresa_localidad()));
				cell8.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell9 = row.createCell(column++);
				cell9.setCellValue(new HSSFRichTextString(nov.getEmpresa_calle()));
				cell9.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell10 = row.createCell(column++);
				cell10.setCellValue(new HSSFRichTextString(nov.getEmpresa_numero()));
				cell10.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell11 = row.createCell(column++);
				cell11.setCellValue(new HSSFRichTextString(nov.getEmpresa_piso()));
				cell11.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell12 = row.createCell(column++);
				cell12.setCellValue(new HSSFRichTextString(nov.getEmpresa_depto()));
				cell12.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell13 = row.createCell(column++);
				cell13.setCellValue(new HSSFRichTextString(nov.getEmpresa_codigo_postal()));
				cell13.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell14 = row.createCell(column++);
				cell14.setCellValue(new HSSFRichTextString(nov.getEmpresa_telefono()));
				cell14.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell15 = row.createCell(column++);
				cell15.setCellValue(new HSSFRichTextString(nov.getPlan_actual_desc()));
				cell15.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell16 = row.createCell(column++);
				cell16.setCellValue(new HSSFRichTextString(nov.getNovedad_desc()));
				cell16.setCellStyle(styleAllWithBorder);
				
				HSSFCell cell17 = row.createCell(column++);
				cell17.setCellValue(new HSSFRichTextString(nov.getPlan_que_corresponde_desc()));
				cell17.setCellStyle(styleAllWithBorder);
				
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
			sheet.autoSizeColumn((short) 9);
			sheet.autoSizeColumn((short) 10);
			sheet.autoSizeColumn((short) 11);
			sheet.autoSizeColumn((short) 12);
			sheet.autoSizeColumn((short) 13);
			sheet.autoSizeColumn((short) 14);
			sheet.autoSizeColumn((short) 15);
			
		} catch (Exception e) {
			_log.error(e);
		}
		//wb.setRepeatingRowsAndColumns(0, 0, 4, 0, 4);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return wb;
	}

	private static int createHeader(HSSFWorkbook wb, HSSFSheet sheet, String tipoNovedadEmpl, Calendar fechaHasta) {
		
		HSSFCellStyle styleHeaderEnca = getStyleHeaderWithBorderNoColor(wb, 10);
		HSSFCellStyle styleHeaderEnca2 = getStyleHeaderWithBorderLeftNoColor(wb, 10);
//		HSSFCellStyle styleHeaderEnca3 = getStyleHeaderWithBorderNoColor(wb, 10);

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		int index = 0;
		HSSFRow row = sheet.createRow(index++);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Novedades de Empleadores"));
		cell.setCellStyle(styleHeaderEnca);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		HSSFRow row1 = sheet.createRow(index++);
		HSSFCell cell1 = row1.createCell(0);
		StringBuffer aux = new StringBuffer(" Tipo Novedad: ").append(tipoNovedadEmpl)
				.append(" Período: " + sdf2.format(fechaHasta.getTime()));
		
		cell1.setCellValue(new HSSFRichTextString(aux.toString()));
		cell1.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		HSSFRow row2 = sheet.createRow(index++);

		HSSFCell cell2 = row2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Fecha de Reporte: "
				+ sdf1.format(new Date(System.currentTimeMillis()))));
		cell2.setCellStyle(styleHeaderEnca2);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		index = index + 2;
		HSSFRow row3a = sheet.createRow(index);

		int column = 0;

		HSSFCell cell20 = row3a.createCell(column++);
		cell20.setCellValue(new HSSFRichTextString("Cuil Titular"));
		cell20.setCellStyle(styleHeaderEnca2);

		HSSFCell cell21 = row3a.createCell(column++);
		cell21.setCellValue(new HSSFRichTextString("Inte"));
		cell21.setCellStyle(styleHeaderEnca2);

		HSSFCell cell22 = row3a.createCell(column++);
		cell22.setCellValue(new HSSFRichTextString("Apellido y Nombre"));
		cell22.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell23 = row3a.createCell(column++);
		cell23.setCellValue(new HSSFRichTextString("Seccional"));
		cell23.setCellStyle(styleHeaderEnca2);
		
//		HSSFCell cell23 = row3a.createCell(column++);
//		cell23.setCellValue(new HSSFRichTextString("Período"));
//		cell23.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell24 = row3a.createCell(column++);
		cell24.setCellValue(new HSSFRichTextString("CUIT"));
		cell24.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell25 = row3a.createCell(column++);
		cell25.setCellValue(new HSSFRichTextString("Razón Social"));
		cell25.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell26 = row3a.createCell(column++);
		cell26.setCellValue(new HSSFRichTextString("Planta"));
		cell26.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell27 = row3a.createCell(column++);
		cell27.setCellValue(new HSSFRichTextString("Provincia"));
		cell27.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell28 = row3a.createCell(column++);
		cell28.setCellValue(new HSSFRichTextString("Localidad"));
		cell28.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell29 = row3a.createCell(column++);
		cell29.setCellValue(new HSSFRichTextString("Calle"));
		cell29.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell30 = row3a.createCell(column++);
		cell30.setCellValue(new HSSFRichTextString("Número"));
		cell30.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell31 = row3a.createCell(column++);
		cell31.setCellValue(new HSSFRichTextString("Piso"));
		cell31.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell32 = row3a.createCell(column++);
		cell32.setCellValue(new HSSFRichTextString("Dpto"));
		cell32.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell33 = row3a.createCell(column++);
		cell33.setCellValue(new HSSFRichTextString("C.P."));
		cell33.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell34 = row3a.createCell(column++);
		cell34.setCellValue(new HSSFRichTextString("Teléfono"));
		cell34.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell35 = row3a.createCell(column++);
		cell35.setCellValue(new HSSFRichTextString("Plan Actual"));
		cell35.setCellStyle(styleHeaderEnca2);
		
		
		HSSFCell cell36 = row3a.createCell(column++);
		cell36.setCellValue(new HSSFRichTextString("Tipo Novedad"));
		cell36.setCellStyle(styleHeaderEnca2);
		
		HSSFCell cell37 = row3a.createCell(column++);
		cell37.setCellValue(new HSSFRichTextString("Detalle Novedad"));
		cell37.setCellStyle(styleHeaderEnca2);

		return index;
	}
}

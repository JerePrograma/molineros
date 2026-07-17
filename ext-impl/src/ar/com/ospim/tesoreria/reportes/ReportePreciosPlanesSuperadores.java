package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.tesoreria.beans.AjustePlanSuperador;
import ar.com.ospim.tesoreria.beans.PrecioPlanSuperador;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.LiquidacionPlanesSuperadoresServiceUtil;
import ar.com.uoma.beans.CentroCosto;
import ar.com.uoma.centro_costo.CentroCostoServiceUtil;
import ar.com.uoma.centro_costo.CuentaAsientoDetalle;

public class ReportePreciosPlanesSuperadores extends ReporteConabilidad {
	
	private static Log _log = LogFactoryUtil
			.getLog(ReportePreciosPlanesSuperadores.class);
	
	public static HSSFWorkbook generarListado(HttpServletRequest req,
			HttpServletResponse res) {
	  try {	
		  
		 DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		 Long idPrecio = ParamUtil.getLong(req,"id_precio", 0);
		 String fechaDdeDia = ParamUtil.getString(req,"fechaDesdeDia");
		 String fechaDdeMes = ParamUtil.getString(req,"fechaDesdeMes");
		 String fechaDdeAnio = ParamUtil.getString(req,"fechaDesdeAnio");
		 String descripcion = ParamUtil.getString(req,"descripcion");
		 Date fechaDde = null;
		 try {
				fechaDde = format.parse(fechaDdeDia + "/"
						+ (Integer.parseInt(fechaDdeMes) + 1) + "/"
						+ fechaDdeAnio);
		 } catch (Exception e) {
				fechaDde = null;
		 }
		 Integer plan = ParamUtil.getInteger(req,"plan");
		 Integer parentesco = ParamUtil.getInteger(req,"parentesco");
		 Integer provincia = ParamUtil.getInteger(req,"provincia");
		 PrecioPlanSuperador filtro = new PrecioPlanSuperador();
		 filtro.setId(idPrecio.intValue());
		 filtro.setDescripcion(descripcion);
		 Plan p =new Plan();
		 p.setId(plan);
		 filtro.getPlanes().add(p);
		 Parentesco pa = new Parentesco();
		 pa.setCodigo(parentesco);
		filtro.getParentescos().add(pa);
		Provincia pr = new Provincia();
		pr.setId(provincia);
		filtro.getProvincias().add(pr);
		List<PrecioPlanSuperador>precios= LiquidacionPlanesSuperadoresServiceUtil.searchPlanSuperador(filtro);
			
		if(precios.isEmpty()) {
				_log.error("No se encontraron resultados para los detalles ...");
				//return null;
		}
		  
		// Creacion de workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllBorder = wb.createCellStyle();
		styleAllBorder.setBorderTop(BorderStyle.THIN);
		styleAllBorder.setBorderBottom(BorderStyle.THIN);
		styleAllBorder.setBorderLeft(BorderStyle.THIN);
		styleAllBorder.setBorderRight(BorderStyle.THIN);
		styleAllBorder.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setRightBorderColor(IndexedColors.BLACK.getIndex());
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
					
		// Creacion de hoja
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		// Creacion de fila
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		// Creacion de fila
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		// Creacion de fila
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);
		// Creacion de fila - getPrintSetup() es un metodo que devuelve un objeto de configuracion de impresion
		HSSFPrintSetup ps = sheet.getPrintSetup();
		// Creacion de fila - setAutobreaks(true) es un metodo que activa el ajuste automatico de impresion
		sheet.setAutobreaks(true);
		// setPaperSize(HSSFPrintSetup.A4_PAPERSIZE) es un metodo que establece el tamaño de papel
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		// setFitHeight((short) 0) es un metodo que establece el ajuste de altura
		ps.setFitHeight((short) 0);
		// setFitWidth((short) 1) es un metodo que establece el ajuste de ancho
		ps.setFitWidth((short) 1);
		// setLandscape(false) es un metodo que establece la orientacion de la hoja
		ps.setLandscape(false);
					
		
		int i = 0;
		
	    HSSFRow rowTitulo = sheet.createRow(i);
	    HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Precios de Planes Superadores: "));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
		i += 2;
						
		int rowIndex = i; // contador de fila
		HSSFRow row = sheet.createRow(rowIndex);
		
		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Id"));
		cell0.setCellStyle(styleAllBorder);
					
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Descripción"));
		cell1.setCellStyle(styleAllBorder);
		 
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Vigente Dde"));
		cell2.setCellStyle(styleAllBorder);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Vigente Hta"));
		cell3.setCellStyle(styleAllBorder);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Edad Dde"));
		cell4.setCellStyle(styleAllBorder);
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Edad Hta"));
		cell5.setCellStyle(styleAllBorder);
		
		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Planes"));
		cell6.setCellStyle(styleAllBorder);
		
		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Parentescos"));
		cell7.setCellStyle(styleAllBorder);
		
		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(new HSSFRichTextString("Provincias"));
		cell8.setCellStyle(styleAllBorder);
		
		HSSFCell cell9 = row.createCell(9);
		cell9.setCellValue(new HSSFRichTextString("Valores"));
		cell9.setCellStyle(styleAllBorder);
		
		rowIndex++;
		if(precios!=null) {
		  for(PrecioPlanSuperador c:precios) {
			  HSSFRow rowA = sheet.createRow(rowIndex);
			  
			  HSSFCell cellA_1 = rowA.createCell(0);
			  cellA_1.setCellValue(c.getId());
			  cellA_1.setCellStyle(styleAllBorder);
				
			  HSSFCell cellA_0 = rowA.createCell(1); 
			  cellA_0.setCellValue(new HSSFRichTextString(c.getDescripcion()));
			  cellA_0.setCellStyle(styleAllBorder);
			  
			  
			  HSSFCell cellA_2 = rowA.createCell(2); 
			  cellA_2.setCellValue(new HSSFRichTextString(c.getFechaDesdeAsString() ));
			  cellA_2.setCellStyle(styleAllBorder);
					
			  HSSFCell cellA_3 = rowA.createCell(3); 
			  cellA_3.setCellValue(new HSSFRichTextString(c.getFechaHastaAsString() ));
			  cellA_3.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_4 = rowA.createCell(4);
			  cellA_4.setCellValue(c.getEdadDesde());
			  cellA_4.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_5 = rowA.createCell(5);
			  cellA_5.setCellValue(c.getEdadHasta());
			  cellA_5.setCellStyle(styleAllBorder);
			  
			  
			  HSSFCell cellA_6 = rowA.createCell(6); 
			  cellA_6.setCellValue(new HSSFRichTextString(c.getPlanesString() ));
			  cellA_6.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_7 = rowA.createCell(7); 
			  cellA_7.setCellValue(new HSSFRichTextString(c.getParentescosString() ));
			  cellA_7.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_8 = rowA.createCell(8); 
			  cellA_8.setCellValue(new HSSFRichTextString(c.getProvinciasString() ));
			  cellA_8.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_9 = rowA.createCell(9); 
			  cellA_9.setCellValue(new HSSFRichTextString(c.getValoresString() ));
			  cellA_9.setCellStyle(styleAllBorder);
			  
			  rowIndex++;	
		  }	
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
		
		return wb;
	  } catch (Exception e) {
			_log.error("Error al generar Listado Precios  Planes Superadores", e);
			return null;
	  }	
	}
	
	
	/////////
	/////////
	
	public static HSSFWorkbook generarListadoAjustes(HttpServletRequest req,
			HttpServletResponse res) {
	  try {	
		  
		 DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		 Long idPrecio = ParamUtil.getLong(req,"id_precio", 0);
		 String fechaDdeDia = ParamUtil.getString(req,"fechaDesdeDia");
		 String fechaDdeMes = ParamUtil.getString(req,"fechaDesdeMes");
		 String fechaDdeAnio = ParamUtil.getString(req,"fechaDesdeAnio");
		 String descripcion = ParamUtil.getString(req,"descripcion");
		 Date fechaDde = null;
		 try {
				fechaDde = format.parse(fechaDdeDia + "/"
						+ (Integer.parseInt(fechaDdeMes) + 1) + "/"
						+ fechaDdeAnio);
		 } catch (Exception e) {
				fechaDde = null;
		 }
		 Integer plan = ParamUtil.getInteger(req,"plan");
		 Integer parentesco = ParamUtil.getInteger(req,"parentesco");
		 Integer provincia = ParamUtil.getInteger(req,"provincia");
		 String cuil = ParamUtil.getString(req,"cuil");
		 Afiliado a = new Afiliado(cuil,0);
		 
		 AjustePlanSuperador filtro = new AjustePlanSuperador();
		 filtro.setId(idPrecio.intValue());
		 filtro.setDescripcion(descripcion);
		 filtro.getAfiliados().add(a);
		 Plan p =new Plan();
		 p.setId(plan);
		 filtro.getPlanes().add(p);
		 Parentesco pa = new Parentesco();
		 pa.setCodigo(parentesco);
		filtro.getParentescos().add(pa);
		Provincia pr = new Provincia();
		pr.setId(provincia);
		filtro.getProvincias().add(pr);
		List<AjustePlanSuperador>precios= LiquidacionPlanesSuperadoresServiceUtil.searchPlanSuperadorAjustes(filtro);
			
		if(precios.isEmpty()) {
				_log.error("No se encontraron resultados para los detalles ...");
				//return null;
		}
		  
		// Creacion de workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllBorder = wb.createCellStyle();
		styleAllBorder.setBorderTop(BorderStyle.THIN);
		styleAllBorder.setBorderBottom(BorderStyle.THIN);
		styleAllBorder.setBorderLeft(BorderStyle.THIN);
		styleAllBorder.setBorderRight(BorderStyle.THIN);
		styleAllBorder.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleAllBorder.setRightBorderColor(IndexedColors.BLACK.getIndex());
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
					
		// Creacion de hoja
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		// Creacion de fila
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		// Creacion de fila
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		// Creacion de fila
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);
		// Creacion de fila - getPrintSetup() es un metodo que devuelve un objeto de configuracion de impresion
		HSSFPrintSetup ps = sheet.getPrintSetup();
		// Creacion de fila - setAutobreaks(true) es un metodo que activa el ajuste automatico de impresion
		sheet.setAutobreaks(true);
		// setPaperSize(HSSFPrintSetup.A4_PAPERSIZE) es un metodo que establece el tamaño de papel
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		// setFitHeight((short) 0) es un metodo que establece el ajuste de altura
		ps.setFitHeight((short) 0);
		// setFitWidth((short) 1) es un metodo que establece el ajuste de ancho
		ps.setFitWidth((short) 1);
		// setLandscape(false) es un metodo que establece la orientacion de la hoja
		ps.setLandscape(false);
					
		
		int i = 0;
		
	    HSSFRow rowTitulo = sheet.createRow(i);
	    HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte de Ajustes de Planes Superadores: "));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
		i += 2;
						
		int rowIndex = i; // contador de fila
		HSSFRow row = sheet.createRow(rowIndex);
		
		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Id"));
		cell0.setCellStyle(styleAllBorder);
					
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Descripción"));
		cell1.setCellStyle(styleAllBorder);
		 
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Vigente Dde"));
		cell2.setCellStyle(styleAllBorder);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Vigente Hta"));
		cell3.setCellStyle(styleAllBorder);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Edad Dde"));
		cell4.setCellStyle(styleAllBorder);
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Edad Hta"));
		cell5.setCellStyle(styleAllBorder);
		
		HSSFCell cell51 = row.createCell(6);
		cell51.setCellValue(new HSSFRichTextString("Porcentaje"));
		cell51.setCellStyle(styleAllBorder);
		
		HSSFCell cell52 = row.createCell(7);
		cell52.setCellValue(new HSSFRichTextString("Importe"));
		cell52.setCellStyle(styleAllBorder);
		
		HSSFCell cell6 = row.createCell(8);
		cell6.setCellValue(new HSSFRichTextString("Planes"));
		cell6.setCellStyle(styleAllBorder);
		
		HSSFCell cell7 = row.createCell(9);
		cell7.setCellValue(new HSSFRichTextString("Parentescos"));
		cell7.setCellStyle(styleAllBorder);
		
		HSSFCell cell8 = row.createCell(10);
		cell8.setCellValue(new HSSFRichTextString("Provincias"));
		cell8.setCellStyle(styleAllBorder);
		
		HSSFCell cell9 = row.createCell(11);
		cell9.setCellValue(new HSSFRichTextString("Cuiles"));
		cell9.setCellStyle(styleAllBorder);
		
		rowIndex++;
		if(precios!=null) {
		  for(AjustePlanSuperador c:precios) {
			  HSSFRow rowA = sheet.createRow(rowIndex);
			  
			  HSSFCell cellA_1 = rowA.createCell(0);
			  cellA_1.setCellValue(c.getId());
			  cellA_1.setCellStyle(styleAllBorder);
				
			  HSSFCell cellA_0 = rowA.createCell(1); 
			  cellA_0.setCellValue(new HSSFRichTextString(c.getDescripcion()));
			  cellA_0.setCellStyle(styleAllBorder);
			  
			  
			  HSSFCell cellA_2 = rowA.createCell(2); 
			  cellA_2.setCellValue(new HSSFRichTextString(c.getFechaDesdeAsString() ));
			  cellA_2.setCellStyle(styleAllBorder);
					
			  HSSFCell cellA_3 = rowA.createCell(3); 
			  cellA_3.setCellValue(new HSSFRichTextString(c.getFechaHastaAsString() ));
			  cellA_3.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_4 = rowA.createCell(4);
			  cellA_4.setCellValue(c.getEdadDesde());
			  cellA_4.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_5 = rowA.createCell(5);
			  cellA_5.setCellValue(c.getEdadHasta());
			  cellA_5.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_51 = rowA.createCell(6);
			  cellA_51.setCellValue(c.getPorcentaje());
			  cellA_51.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_52 = rowA.createCell(7);
			  cellA_52.setCellValue(c.getImporte().doubleValue());
			  cellA_52.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_6 = rowA.createCell(8); 
			  cellA_6.setCellValue(new HSSFRichTextString(c.getPlanesString() ));
			  cellA_6.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_7 = rowA.createCell(9); 
			  cellA_7.setCellValue(new HSSFRichTextString(c.getParentescosString() ));
			  cellA_7.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_8 = rowA.createCell(10); 
			  cellA_8.setCellValue(new HSSFRichTextString(c.getProvinciasString() ));
			  cellA_8.setCellStyle(styleAllBorder);
			  
			  HSSFCell cellA_9 = rowA.createCell(11); 
			  cellA_9.setCellValue(new HSSFRichTextString(c.getAfiliadosString() ));
			  cellA_9.setCellStyle(styleAllBorder);
			  
			  rowIndex++;	
		  }	
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
		
		return wb;
	  } catch (Exception e) {
			_log.error("Error al generar Listado Ajustes  Planes Superadores", e);
			return null;
	  }	
	}

}

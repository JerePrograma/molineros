package ar.com.ospim.tesoreria.reportes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.procesaArchivos.beans.JubiladosSitaci;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteJubiladosSitaciExcel extends
         ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteJubiladosSitaciExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {

		HttpSession session = (HttpSession) req.getSession();
		
		String cuil=ParamUtil.getString(req,"cuil",null);
		String dni =  ParamUtil.getString(req, "dni",null);
		String periodo =  ParamUtil.getString(req, "periodo",null);
		String tercerizadora =  ParamUtil.getString(req, "tercerizadora",null);
		        
		try {
			
	        List<JubiladosSitaci> lista = LiquidaDesreguladosServiceUtil.getJubilados(periodo, cuil, dni,tercerizadora);
	        
			return generarReporteJubilados(periodo, cuil, dni,tercerizadora, lista);
		} catch (Exception e) {
			_log.error("Error al generar reporte jubilados sitaci", e);
			return null;
		}
		
	}
	
	protected static HSSFWorkbook generarReporteJubilados(
			String periodo, String cuil,String dni,String tercerizadora,
			List<JubiladosSitaci> lista) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDate = getStyleDate(wb);
		styleDate.setBorderLeft(BorderStyle.THIN);
		styleDate.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleMoneyBorder = getStyleMoney(wb);
		styleMoneyBorder.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle stylePeriodo = getStyleDate(wb);
		stylePeriodo.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleBoldLeft = getStyleBold(wb);
		styleBoldLeft.setBorderLeft(BorderStyle.THIN);
		styleBoldLeft.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldCenter = getStyleBold(wb);
		styleBoldCenter.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleBoldRight = getStyleBold(wb);
		styleBoldRight.setBorderTop(BorderStyle.THIN);
		styleBoldRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleAlignRight = getStyleBoldAligned(wb, HorizontalAlignment.RIGHT);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		StringBuilder headerRight = new StringBuilder();
		headerRight.append("N° de hoja: " + HSSFHeader.page());
		headerRight.append(" de " + HSSFHeader.numPages());
		headerRight.append("\n");
		headerRight.append(DateUtils.format(new Date(), DateUtils.LONG_SEC));
		headerRight.append("\n");
		sheet.getHeader().setRight(headerRight.toString());

 		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
 		cellTitulo.setCellValue(new HSSFRichTextString(
				"Jubilados SITACI - Período: "
 						+ periodo
						+ " -  Cuil : "
						+ cuil
						+ " -  DNI : "
						+ dni
						+ " -  Tercerizadora : "
						+ tercerizadora));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

		int i = 1;
		
		i = crearHeaderPpal(sheet, i, styleHeader, wb);
				 
		for (JubiladosSitaci pre : lista) {
				crearDatos(pre, sheet, i, styleDate,
						stylePeriodo, styleAll, styleMoney, styleMoneyBorder,
						styleAlignRight);
			i++;	
					
		}
		
		
		for(int j=0;j<30;j++){
		     sheet.autoSizeColumn((short) j);
		}

		return wb;
	}

	private static int crearHeaderPpal(HSSFSheet sheet, int i,HSSFCellStyle styleHeader,HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Beneficio"));
		cell.setCellStyle(styleHeader);

		int indexBase = 1;
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Afiliado"));
		cell1.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString("Tipo 1"));
		cell2.setCellStyle(styleHeader);
		indexBase++;

		HSSFCell cell3 = row.createCell(indexBase);
		cell3.setCellValue(new HSSFRichTextString("Tipo 2"));
		cell3.setCellStyle(styleHeader);
		indexBase++;

		HSSFCell cell4 = row.createCell(indexBase);
		cell4.setCellValue(new HSSFRichTextString("DNI"));
		cell4.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell5 = row.createCell(indexBase);
		cell5.setCellValue(new HSSFRichTextString("Concepto/Empresa"));
		cell5.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell51 = row.createCell(indexBase);
		cell51.setCellValue(new HSSFRichTextString("Sumatoria"));
		cell51.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell52 = row.createCell(indexBase);
		cell52.setCellValue(new HSSFRichTextString("Concepto Importe"));
		cell52.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell6 = row.createCell(indexBase);
		cell6.setCellValue(new HSSFRichTextString("Período"));
		cell6.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell7 = row.createCell(indexBase);
		cell7.setCellValue(new HSSFRichTextString("Cuil"));
		cell7.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell010 = row.createCell(indexBase);
		cell010.setCellValue(new HSSFRichTextString("Nacimiento"));
		cell010.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell011 = row.createCell(indexBase);
		cell011.setCellValue(new HSSFRichTextString("Sexo"));
		cell011.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell8 = row.createCell(indexBase);
		cell8.setCellValue(new HSSFRichTextString("Registro"));
		cell8.setCellStyle(styleHeader);
		indexBase++;
		
		
		HSSFCell cell012 = row.createCell(indexBase);
		cell012.setCellValue(new HSSFRichTextString("Fecha Liquidación"));
		cell012.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell013 = row.createCell(indexBase);
		cell013.setCellValue(new HSSFRichTextString("Importe Liquidación"));
		cell013.setCellStyle(styleHeader);
		indexBase++;
		
		HSSFCell cell014 = row.createCell(indexBase);
		cell014.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell014.setCellStyle(styleHeader);
		indexBase++;
		
	    for(int j=0;j<30;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		
		i++;
		return i;
	}
	
	private static void crearDatos(JubiladosSitaci pre,
			HSSFSheet sheet, int i, HSSFCellStyle styleDate,
			HSSFCellStyle stylePeriodo, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoney, HSSFCellStyle styleMoneyBorder,
			HSSFCellStyle styleAlignRight) {
		
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		
		int indexBase = 0;	
		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell = row.createCell(0);
		
		// Beneficio
		HSSFCell cell2 = row.createCell(indexBase);
		cell2.setCellValue(new HSSFRichTextString(pre.getBeneficio()));
		cell2.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell3 = row.createCell(indexBase);
		cell3.setCellValue(new HSSFRichTextString(pre.getAfiliado()));
		cell3.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell4 = row.createCell(indexBase);
		cell4.setCellValue(new HSSFRichTextString(pre.getTipo1()));
		cell4.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell5 = row.createCell(indexBase);
		cell5.setCellValue(new HSSFRichTextString(pre.getTipo2()));
		cell5.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell6 = row.createCell(indexBase);
		cell6.setCellValue(new HSSFRichTextString(pre.getDni()));
		cell6.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell7 = row.createCell(indexBase);
		cell7.setCellValue(new HSSFRichTextString(pre.getConcepto()));
		cell7.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell8 = row.createCell(indexBase);
		cell8.setCellValue(pre.getSumatoria());
		cell8.setCellStyle(styleMoney);
		indexBase++;
		
		HSSFCell cell9 = row.createCell(indexBase);
		cell9.setCellValue(pre.getConceptoImporte());
		cell9.setCellStyle(styleMoney);
		indexBase++;
		
		HSSFCell cell10 = row.createCell(indexBase);
		cell10.setCellValue(new HSSFRichTextString(pre.getPeriodo()));
		cell10.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell11 = row.createCell(indexBase);
		cell11.setCellValue(new HSSFRichTextString(pre.getCuil()));
		cell11.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell12 = row.createCell(indexBase);
		cell12.setCellValue(new HSSFRichTextString(sdf.format(pre.getNacimiento())));
		cell12.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell13 = row.createCell(indexBase);
		cell13.setCellValue(new HSSFRichTextString(pre.getSexo()));
		cell13.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell14 = row.createCell(indexBase);
		cell14.setCellValue(new HSSFRichTextString(pre.getRegistro()));
		cell14.setCellStyle(styleAll);
		indexBase++;
		
		
		HSSFCell cell15 = row.createCell(indexBase);
		if(pre.getFechaLiquidado()!=null) {
		  cell15.setCellValue(new HSSFRichTextString(sdf.format(pre.getFechaLiquidado())));
		}  
		cell15.setCellStyle(styleAll);
		indexBase++;
		
		HSSFCell cell16 = row.createCell(indexBase);
		if(pre.getImporteLiquidado()!=null) {
		   cell16.setCellValue(pre.getImporteLiquidado());
		}   
		cell16.setCellStyle(styleMoney);
		indexBase++;
		
		HSSFCell cell17 = row.createCell(indexBase);
		if(pre.getTercerizadora()!=null) {
		   cell17.setCellValue(new HSSFRichTextString(pre.getTercerizadoraDescripcion()));
		}   
		cell17.setCellStyle(styleAll);
		indexBase++;
				
	}
		
	
}

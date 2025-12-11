package ar.com.ospim.tesoreria.reportes;

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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.ReporteRankingDeudaEmpresaBean;
import ar.com.ospim.tesoreria.service.ReportesServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteNuevosAfiliadosEmpresasExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteNuevosAfiliadosEmpresasExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
       try{
			
    	   
    	Calendar fechaIniCalendar=null;
   		
   		if(ParamUtil.getInteger(req, "fechaDesdeMes")>=0 && ParamUtil.getInteger(req, "fechaDesdeAnio")>0 ){
   			fechaIniCalendar=Calendar.getInstance();
   			fechaIniCalendar.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaDesdeAnio"));
   			fechaIniCalendar.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaDesdeMes"));
   			fechaIniCalendar.set(Calendar.DATE,1);
   		}
   		
   		Calendar fechaFinCalendar=null;
   		
   			fechaFinCalendar=Calendar.getInstance();
   			fechaFinCalendar.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaDesdeAnio"));
   			fechaFinCalendar.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaDesdeMes"));
   			fechaFinCalendar.set(Calendar.DATE, 1);
   		   
   			fechaFinCalendar.add(Calendar.MONTH, 1);
	 	    List<ReporteRankingDeudaEmpresaBean>reporte = (List<ReporteRankingDeudaEmpresaBean>) ReportesServiceUtil.getNuevosAfiliadosEmpresas(fechaIniCalendar.getTime(), fechaFinCalendar.getTime()) ;
	 	    List<ReporteRankingDeudaEmpresaBean>reporteMolinero = (List<ReporteRankingDeudaEmpresaBean>) ReportesServiceUtil.getNuevosAfiliadosEmpresasPortalMolineros(fechaIniCalendar.getTime(), fechaFinCalendar.getTime()) ;
	 	    List<ReporteRankingDeudaEmpresaBean>reporteRamo = (List<ReporteRankingDeudaEmpresaBean>) ReportesServiceUtil.getNuevosAfiliadosEmpresasPorRamo(fechaIniCalendar.getTime()) ;
			
		  return generarReporte(reporte,reporteMolinero,reporteRamo,fechaIniCalendar,fechaFinCalendar);
			
			
		} catch (Exception e) {
			_log.error("Error al generar Reporte Afiliados Nuevos Empresas", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(List<ReporteRankingDeudaEmpresaBean> reporte,List<ReporteRankingDeudaEmpresaBean> reporteMolinero,List<ReporteRankingDeudaEmpresaBean> reporteRamo,Calendar fechaDesde,Calendar fechaHasta) throws SystemException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);
	
		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
	
		HSSFCellStyle styleAllTop = getStyleAll(wb);
	
		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
	
		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
	
		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
	
		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
	
		HSSFCellStyle styleMoneyRightBold = getStyleMoneyBold(wb);
	
		HSSFCellStyle styleMoneyBold = getStyleMoneyBold(wb);

		HSSFSheet sheet = wb.createSheet("Desde Portal Empleadores");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i,fechaDesde,fechaHasta);

		
		i = generarHeaderRamo(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb,"Desde Portal Empleadores");
		
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		int rowIni= i+1;
				
		for (ReporteRankingDeudaEmpresaBean repo : reporte) {
			
				i = generarDatosRamo(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		}
		
		i++;
		
		i=0;
		HSSFSheet sheet1 = wb.createSheet("Desde información de AFIP");
		sheet1.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet1.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet1.setMargin(HSSFSheet.TopMargin, 1.3);
		
		i = createTitulosHeader(wb, sheet1, i,fechaDesde,fechaHasta);
		
		i = generarHeaderRamo(sheet1, i, styleHeader, styleHeaderLeft,
				styleHeaderRight, wb,"Desde información de AFIP");
	
	
	    //wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	
	    rowIni= i+1;
			
	    if(reporteMolinero != null){
	      for (ReporteRankingDeudaEmpresaBean repo : reporteMolinero) {
		
			i = generarDatosRamo(sheet1, i, repo, styleFechaLeft, styleAll,
					styleMoneyRight, styleFechaLeftTop, styleAllTop,
					styleMoneyRightTop);
		
	      }
	    }  
		
	    
	    
	    i++;
	    
	    i=0;
		HSSFSheet sheet2 = wb.createSheet("Desde padrón de Afiliados");
		sheet2.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet2.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet2.setMargin(HSSFSheet.TopMargin, 1.3);
		
		i = createTitulosHeader(wb, sheet2, i,fechaDesde,fechaHasta);
	    
	    i = generarHeaderRamo(sheet2, i, styleHeader, styleHeaderLeft,
				styleHeaderRight, wb,"Desde padrón de Afiliados");
	    
	    
	    if(reporteRamo != null){
		      for (ReporteRankingDeudaEmpresaBean repo : reporteRamo) {
				i = generarDatosRamo(sheet2, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		      }
		}  

		for(int x=0;x<58;x++){
			sheet.autoSizeColumn((short) x);
			sheet1.autoSizeColumn((short) x);
			sheet2.autoSizeColumn((short) x);
		}
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ReporteRankingDeudaEmpresaBean repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		if(repo.getRazonSocial()!=null){
		  cell0.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
		  cell0.setCellStyle(styleAll);
		}else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getCuit()!=null){
		  cell1.setCellValue(new HSSFRichTextString(repo.getCuit()));
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		HSSFCell cell2 = row.createCell(2);
		if(repo.getTotal_calculo_deuda()!=null){
		  cell2.setCellValue(repo.getTotal_calculo_deuda().doubleValue() );
		  cell2.setCellStyle(styleAll);
	    }else{
		  cell2.setCellValue(new HSSFRichTextString(""));
		  cell2.setCellStyle(styleAll);
		}
		
				return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb,String titulo) {
		
			
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0r01 = row.createCell(0);
		cell0r01.setCellValue(new HSSFRichTextString(titulo));
		cell0r01.setCellStyle(styleHeaderL);
		i++;
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 1, 2));
		
		row = sheet.createRow(i);
		HSSFCell cell0r1 = row.createCell(0);
		cell0r1.setCellValue(new HSSFRichTextString("Razón Social"));
		cell0r1.setCellStyle(styleHeaderL);
		
		HSSFCell cell1r1 = row.createCell(1);
		cell1r1.setCellValue(new HSSFRichTextString("CUIT"));
		cell1r1.setCellStyle(styleHeader);
		
		HSSFCell cell3r1 = row.createCell(2);
		cell3r1.setCellValue(new HSSFRichTextString("Afiliados"));
		cell3r1.setCellStyle(styleHeader);
		
		i++;
		row = sheet.createRow(i);
		
		
	/*		
		sheet.addMergedRegion(new CellRangeAddress(i-1, i, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 5, 6));
*/		
		return ++i;
	}

	
	private static int generarHeaderRamo(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb,String titulo) {
		
			
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0r01 = row.createCell(0);
		cell0r01.setCellValue(new HSSFRichTextString(titulo));
		cell0r01.setCellStyle(styleHeaderL);
		i++;
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 1, 2));
		
		row = sheet.createRow(i);
		HSSFCell cell0r1 = row.createCell(0);
		cell0r1.setCellValue(new HSSFRichTextString("Razón Social"));
		cell0r1.setCellStyle(styleHeaderL);
		
		HSSFCell cell1r1 = row.createCell(1);
		cell1r1.setCellValue(new HSSFRichTextString("CUIT"));
		cell1r1.setCellStyle(styleHeader);
		
		HSSFCell cell1r4 = row.createCell(2);
		cell1r4.setCellValue(new HSSFRichTextString("Ramo"));
		cell1r4.setCellStyle(styleHeader);
		
		HSSFCell cell3r1 = row.createCell(3);
		cell3r1.setCellValue(new HSSFRichTextString("Afiliados"));
		cell3r1.setCellStyle(styleHeader);
		
		i++;
		row = sheet.createRow(i);
		
		
	/*		
		sheet.addMergedRegion(new CellRangeAddress(i-1, i, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(i-1, i-1, 5, 6));
*/		
		return ++i;
	}

	
	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila,Calendar fDesde,Calendar fHasta) throws SystemException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
		
		String tituloReporte = "Listado Empresas Nuevos Afiliados período " + sdf.format(fDesde.getTime());

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		
		HSSFCell cell12 = rowTitulo.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Impresión: "
				+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell12.setCellStyle(getStyleAllCenter(wb));
		
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 11));
		fila++;
        fila++;
		return fila;
	}
	
	private static int generarDatosRamo(HSSFSheet sheet, int i,
			ReporteRankingDeudaEmpresaBean repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		if(repo.getRazonSocial()!=null){
		  cell0.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
		  cell0.setCellStyle(styleAll);
		}else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getCuit()!=null){
		  cell1.setCellValue(new HSSFRichTextString(repo.getCuit()));
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		HSSFCell cell3 = row.createCell(2);
		if(repo.getRamoEmpresaId()!=null){
		  cell3.setCellValue(repo.getRamoEmpresaId());
		  cell3.setCellStyle(styleAll);
	    }else{
		  cell3.setCellValue(new HSSFRichTextString(""));
		  cell3.setCellStyle(styleAll);
		}
		
		HSSFCell cell2 = row.createCell(3);
		if(repo.getTotal_calculo_deuda()!=null){
		  cell2.setCellValue(repo.getTotal_calculo_deuda().doubleValue() );
		  cell2.setCellStyle(styleAll);
	    }else{
		  cell2.setCellValue(new HSSFRichTextString(""));
		  cell2.setCellStyle(styleAll);
		}
		
		return ++i;
	}

}

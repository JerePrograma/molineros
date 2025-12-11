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

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.ReporteResumenProcesoCalcDeudaMasivoBean;
import ar.com.ospim.tesoreria.service.ReportesServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteResumenProcesoCalcDeudaMasivoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteResumenProcesoCalcDeudaMasivoExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
       try{
				
    	  int idProceso = ParamUtil.get(req, "idProceso", 0);
    	  
	 	  List<ReporteResumenProcesoCalcDeudaMasivoBean> reporte = (List<ReporteResumenProcesoCalcDeudaMasivoBean>) ReportesServiceUtil.getResumenProcesoCalcDeudaMasivo(idProceso);
			
			
		  return generarReporte(reporte);
			
			
		} catch (Exception e) {
			_log.error("Error al generar Reporte Resumen Proceso Calc. Deuda Masivo", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(List<ReporteResumenProcesoCalcDeudaMasivoBean> reporte) throws SystemException {
		
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

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i, reporte.get(0).getIdProceso() );

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
				
		for (ReporteResumenProcesoCalcDeudaMasivoBean repo : reporte) {
			
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		}
		
		i++;
		
		for(int x=0;x<8;x++){
			sheet.autoSizeColumn((short) x);
		}
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ReporteResumenProcesoCalcDeudaMasivoBean repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		
		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		if(repo.getCuit()  !=null){
		  cell0.setCellValue(new HSSFRichTextString(repo.getCuit()));
		  cell0.setCellStyle(styleAll);
	    }else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getSucursal() !=null){
		  cell1.setCellValue(new HSSFRichTextString(repo.getSucursal() ));
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		HSSFCell cell2 = row.createCell(2);
		if(repo.getRazonSocial()  !=null){
		  cell2.setCellValue(new HSSFRichTextString(repo.getRazonSocial() ));
		  cell2.setCellStyle(styleAll);
	    }else{
		  cell2.setCellValue(new HSSFRichTextString(""));
		  cell2.setCellStyle(styleAll);
		}
	
		HSSFCell cell3 = row.createCell(3);
//		if(repo.isMolinera()  !=null){
		  cell3.setCellValue(new HSSFRichTextString(repo.isMolinera()?"SI":"NO"));
		  cell3.setCellStyle(styleAll);
//	    }else{
//		  cell3.setCellValue(new HSSFRichTextString(""));
//		  cell3.setCellStyle(styleAll);
//		}
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(repo.isEmpresaOMonotrib()?"SI":"NO"));
		cell4.setCellStyle(styleAll);
		
		HSSFCell cell5 = row.createCell(5);
		if(repo.getPeriodo()!=null){
		  cell5.setCellValue(new HSSFRichTextString(sdf.format(repo.getPeriodo()) ));
		  cell5.setCellStyle(styleMoneyRight);
	    }else{
		  cell5.setCellValue(new HSSFRichTextString(""));
		  cell5.setCellStyle(styleAll);
		}
		
		HSSFCell cell6 = row.createCell(6);
		if(repo.getTotalDeuda()!=null){
		  cell6.setCellValue(new HSSFRichTextString(repo.getTotalDeuda().toString() ));
		  cell6.setCellStyle(styleMoneyRight);
	    }else{
		  cell6.setCellValue(new HSSFRichTextString(""));
		  cell6.setCellStyle(styleAll);
		}
		
		HSSFCell cell7 = row.createCell(7);
		if(repo.getTotalDeuda() !=null){
		  cell7.setCellValue(repo.getCantidadAfiliados());
		  cell7.setCellStyle(styleAll);
	    }else{
		  cell7.setCellValue(new HSSFRichTextString(""));
		  cell7.setCellStyle(styleAll);
		}
		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("CUIT"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Sucursal"));
		cell1.setCellStyle(styleHeader);
		
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Razón Social"));
		cell2.setCellStyle(styleHeader);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Molinera"));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Monotributista"));
		cell4.setCellStyle(styleHeader);
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Min.Período"));
		cell5.setCellStyle(styleHeader);
		
		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Deuda"));
		cell6.setCellStyle(styleHeader);
		
		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Cantidad Afiliados"));
		cell7.setCellStyle(styleHeader);
		
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, int idProceso) throws SystemException {

		
        String tituloReporte = "Resúmen del proceso de cálculo de deuda masivo N° "+ idProceso;
		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		
		HSSFCell cell7 = rowTitulo.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Impresión: "
				+ DateUtils.format(new Date(), DateUtils.LONG)));
		cell7.setCellStyle(getStyleAllCenter(wb));
		
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 6));
		fila++;

		return fila;
	}
}

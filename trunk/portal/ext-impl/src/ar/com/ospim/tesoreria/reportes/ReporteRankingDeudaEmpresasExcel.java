package ar.com.ospim.tesoreria.reportes;

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

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.action.RankingDeudaEmpresaPeriodoAction;
import ar.com.ospim.tesoreria.beans.ReporteRankingDeudaEmpresaBean;
import ar.com.ospim.tesoreria.service.ReportesServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteRankingDeudaEmpresasExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteRankingDeudaEmpresasExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
       try{
						
	 	  List<ReporteRankingDeudaEmpresaBean>reporte = (List<ReporteRankingDeudaEmpresaBean>) ReportesServiceUtil.getRankingDeudaEmpresas();
			
			
		  return generarReporte(reporte);
			
			
		} catch (Exception e) {
			_log.error("Error al generar Reporte Ranking Deuda Empresas", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(List<ReporteRankingDeudaEmpresaBean> reporte) throws SystemException {
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
		
		i = createTitulosHeader(wb, sheet, i);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
				
		for (ReporteRankingDeudaEmpresaBean repo : reporte) {
			
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		}
		
		i++;
		
		for(int x=0;x<58;x++){
			sheet.autoSizeColumn((short) x);
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
		if(repo.getCuit()  !=null){
		  cell0.setCellValue(new HSSFRichTextString(repo.getCuit()));
		  cell0.setCellStyle(styleAll);
	    }else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getRazonSocial()  !=null){
		  cell1.setCellValue(new HSSFRichTextString(repo.getRazonSocial() ));
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		HSSFCell cell2 = row.createCell(2);
		if(repo.getRamoEmpresaDesc() !=null){
		  cell2.setCellValue(new HSSFRichTextString(repo.getRamoEmpresaDesc() ));
		  cell2.setCellStyle(styleAll);
	    }else{
		  cell2.setCellValue(new HSSFRichTextString(""));
		  cell2.setCellStyle(styleAll);
		}
		
		HSSFCell cell3 = row.createCell(3);
		if(repo.getTercerizadora()  !=null){
		  cell3.setCellValue(new HSSFRichTextString(repo.getTercerizadora()));
		  cell3.setCellStyle(styleAll);
	    }else{
		  cell3.setCellValue(new HSSFRichTextString(""));
		  cell3.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell4 = row.createCell(4);
		if(repo.getSum()!=null){
		  cell4.setCellValue(repo.getSum().doubleValue() );
		  cell4.setCellStyle(styleMoneyRight);
	    }else{
		  cell4.setCellValue(new HSSFRichTextString(""));
		  cell4.setCellStyle(styleAll);
		}
		
		HSSFCell cell5 = row.createCell(5);
		if(repo.getNumero()  !=null){
		  cell5.setCellValue(new HSSFRichTextString(repo.getNumero() ));
		  cell5.setCellStyle(styleAll);
	    }else{
		  cell5.setCellValue(new HSSFRichTextString(""));
		  cell5.setCellStyle(styleAll);
		}
		
		HSSFCell cell6 = row.createCell(6);
		if(repo.getMinPeriodo()!=null){
		  cell6.setCellValue(repo.getMinPeriodo());
		  cell6.setCellStyle(styleFechaLeftTop);
		}else{
		  cell6.setCellValue(new HSSFRichTextString(""));
		  cell6.setCellStyle(styleAll);
		}

		HSSFCell cell7 = row.createCell(7);
		if(repo.getMaxPeriodo()!=null){
		  cell7.setCellValue(repo.getMaxPeriodo());
		  cell7.setCellStyle(styleFechaLeftTop);
		}else{
		  cell7.setCellValue(new HSSFRichTextString(""));
		  cell7.setCellStyle(styleAll);
		}
		
		HSSFCell cell8 = row.createCell(8);
		if(repo.getTotalActa()!=null){
		  cell8.setCellValue(repo.getTotalActa().doubleValue() );
		  cell8.setCellStyle(styleMoneyRight);
	    }else{
		  cell8.setCellValue(new HSSFRichTextString(""));
		  cell8.setCellStyle(styleAll);
		}
		
		HSSFCell cell9 = row.createCell(9);
		if(repo.getTotalPagado()!=null){
		  cell9.setCellValue(repo.getTotalPagado().doubleValue() );
		  cell9.setCellStyle(styleMoneyRight);
	    }else{
		  cell9.setCellValue(new HSSFRichTextString(""));
		  cell9.setCellStyle(styleAll);
		}
		
		HSSFCell cell10 = row.createCell(10);
		if(repo.getTotal_calculo_deuda()!=null){
		  cell10.setCellValue(repo.getTotal_calculo_deuda().doubleValue() );
		  cell10.setCellStyle(styleMoneyRight);
	    }else{
		  cell10.setCellValue(new HSSFRichTextString(""));
		  cell10.setCellStyle(styleAll);
		}
		
		HSSFCell cell11 = row.createCell(11);
		if(repo.getMin_periodo_cal_deuda()!=null){
		  cell11.setCellValue(repo.getMin_periodo_cal_deuda() );
		  cell11.setCellStyle(styleFechaLeftTop);
		}else{
		  cell11.setCellValue(new HSSFRichTextString(""));
		  cell11.setCellStyle(styleAll);
		}

		HSSFCell cell12 = row.createCell(12);
		if(repo.getMax_periodo_cal_deuda()!=null){
		  cell12.setCellValue(repo.getMax_periodo_cal_deuda() );
		  cell12.setCellStyle(styleFechaLeftTop);
		}else{
		  cell12.setCellValue(new HSSFRichTextString(""));
		  cell12.setCellStyle(styleAll);
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
		cell1.setCellValue(new HSSFRichTextString("Razón Social"));
		cell1.setCellStyle(styleHeader);
		
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Ramo"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Tercerizadora"));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Deuda"));
		cell4.setCellStyle(styleHeader);
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Ult.Acta"));
		cell5.setCellStyle(styleHeaderL);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Periodo Ini"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Período Fin"));
		cell7.setCellStyle(styleHeader);
		
		HSSFCell cell8 = row.createCell(8);
		cell8.setCellValue(new HSSFRichTextString("Total Acta"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(9);
		cell9.setCellValue(new HSSFRichTextString("Total Pagado"));
		cell9.setCellStyle(styleHeader);
		
		HSSFCell cell10 = row.createCell(10);
		cell10.setCellValue(new HSSFRichTextString("Total Cálculo Deuda"));
		cell10.setCellStyle(styleHeader);
		
		HSSFCell cell11 = row.createCell(11);
		cell11.setCellValue(new HSSFRichTextString("Periodo Ini C.Deuda"));
		cell11.setCellStyle(styleHeader);
		
		HSSFCell cell12 = row.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Periodo Fin C.Deuda"));
		cell12.setCellStyle(styleHeader);
		
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila) throws SystemException {

		
		List<String> parametros = SchedulerServiceUtil.getParameters(RankingDeudaEmpresaPeriodoAction.reporte_system_config);
       
		String fDesde = parametros.get(0).toString().substring(6)+"/"+parametros.get(0).toString().substring(4, 6) +"/" +
                        parametros.get(0).toString().substring(0, 4);
        
        String fHasta = parametros.get(1).toString().substring(6)+"/"+parametros.get(1).toString().substring(4, 6) +"/" +
                parametros.get(1).toString().substring(0, 4);
		
        String tituloReporte = "Empresas con deuda calculada > $500 desde "+fDesde +" a " + fHasta;
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

		return fila;
	}
}

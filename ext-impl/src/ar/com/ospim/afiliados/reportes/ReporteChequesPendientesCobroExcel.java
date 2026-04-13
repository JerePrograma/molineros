package ar.com.ospim.afiliados.reportes;

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
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.service.ReportesServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteChequesPendientesCobroExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteChequesPendientesCobroExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
       try{
    	  String saldoStr=ParamUtil.getString(req, "saldo"); 
    	  Double saldo=0.00;//ParamUtil.getDouble(req, "saldo");
    	  saldoStr = saldoStr.replaceAll("\\.", "");
    	  saldoStr = saldoStr.replaceAll(",", ".");
    	  saldo = Double.parseDouble(saldoStr);
//    	  saldo = Double.valueOf(saldoStr);
    	  Double facturas=ParamUtil.getDouble(req, "facturas");
    	  Integer idCta =ParamUtil.getInteger(req,"cuenta");
          String saldoLb=ParamUtil.getString(req, "saldolb");
    	  CuentaBancaria cb = new CuentaBancaria();
    	  List<CuentaBancaria>listCB = TraeListasServiceUtil.getCtasBcrias();
    	  for(CuentaBancaria c:listCB){
    		  if(c.getId_cuenta_bcria()==idCta){
    			  cb=c;
    			  break;
    		  }
    	  }
    	 
						
	 	  List<Cheque>reporte = (List<Cheque>) ReportesServiceUtil.getChequesPendientesCobro(idCta) ;
			
			
		  return generarReporte(reporte,cb,saldo,saldoLb,facturas);
			
			
		} catch (Exception e) {
			_log.error("Error al generar Cheques Pendientes de Cobro", e);
			return null;
		}
		
	}

	private static HSSFWorkbook generarReporte(List<Cheque> reporte,CuentaBancaria cb,Double saldo,String saldoLb,Double facturas) throws SystemException {
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
		
		HSSFCellStyle styleMoneyRightGris= getStyleMoneyFondoGris(wb);
		
		HSSFCellStyle styleAllFondoGris = getStyleAllFondoGris(wb);
		
		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i,saldoLb);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		i++;
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	
		Double saldoCheque=saldo;
		
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell_00 = row.createCell(3);
		cell_00.setCellValue(new HSSFRichTextString(saldoLb));
		cell_00.setCellStyle(styleAll);
		
		
		HSSFCell cell_01 = row.createCell(5);
		cell_01.setCellValue(saldoCheque);
		cell_01.setCellStyle(styleMoneyRight);
		
		i++;
		
		row = sheet.createRow(i);
		HSSFCell cell_02 = row.createCell(3);
		cell_02.setCellValue(new HSSFRichTextString("Facturas a Pagar"));
		cell_02.setCellStyle(styleAll);
		
		HSSFCell cell_03 = row.createCell(4);
		cell_03.setCellValue(facturas*-1);
		cell_03.setCellStyle(styleMoneyRight);
		
		saldoCheque -= facturas;
		HSSFCell cell_04 = row.createCell(5);
		cell_04.setCellValue(saldoCheque);
		cell_04.setCellStyle(styleMoneyRight);
		
		i++;
		
		for (Cheque repo : reporte) {
			
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop,saldoCheque);
			
		}
		
		i++;
		
		Integer ultimaFila = i-1;
		
		i++;
		
		HSSFRow rowT = sheet.createRow(i);
		HSSFCell cellT_02 = rowT.createCell(3);
		cellT_02.setCellValue(new HSSFRichTextString("Disponibilidad"));
		cellT_02.setCellStyle(styleHeader);
		
		HSSFCell cellT_04 = rowT.createCell(5);
		cellT_04.setCellFormula("+F"+ Integer.toString(ultimaFila) );
		cellT_04.setCellStyle(styleMoneyRightBold);
		
		
/*		
		for(int x=0;x<7;x++){
			sheet.autoSizeColumn((short) x);
		}
*/		
		sheet.setColumnWidth(0, 2500);
		sheet.setColumnWidth(1, 2500);
		sheet.setColumnWidth(2, 2500);
		sheet.setColumnWidth(3, 10300);
		sheet.setColumnWidth(4, 3300);
		sheet.setColumnWidth(5, 3300);
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			Cheque repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop,Double saldoCheque) {
        
		
		
		HSSFRow row = sheet.createRow(i);
		HSSFCell cell0 = row.createCell(0);
		if(repo.getFechaAsString()  !=null){
		  cell0.setCellValue(new HSSFRichTextString(repo.getFechaAsString()));
		  cell0.setCellStyle(styleAll);
	    }else{
		  cell0.setCellValue(new HSSFRichTextString(""));
		  cell0.setCellStyle(styleAll);
		}
		
		HSSFCell cell1 = row.createCell(1);
		if(repo.getNumero()  !=null){
		  cell1.setCellValue(repo.getNumero().longValue());
		  cell1.setCellStyle(styleAll);
	    }else{
		  cell1.setCellValue(new HSSFRichTextString(""));
		  cell1.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell11 = row.createCell(2);
		if(repo.getCuit()  !=null){
		  cell11.setCellValue(new HSSFRichTextString(repo.getCuit()));
		  cell11.setCellStyle(styleAll);
	    }else{
		  cell11.setCellValue(new HSSFRichTextString(""));
		  cell11.setCellStyle(styleAll);
		}
		
		HSSFCell cell12 = row.createCell(3);
		if(repo.getaNombreDe()  !=null){
		  cell12.setCellValue(new HSSFRichTextString(repo.getaNombreDe()));
		  cell12.setCellStyle(styleAll);
	    }else{
		  cell12.setCellValue(new HSSFRichTextString(""));
		  cell12.setCellStyle(styleAll);
		}
		
		HSSFCell cell17 = row.createCell(4);
		if(repo.getImporte() !=null){
		  cell17.setCellValue(repo.getImporte().doubleValue());
		  cell17.setCellStyle(styleMoneyRight);
	    }else{
		  cell17.setCellValue(new HSSFRichTextString(""));
		  cell17.setCellStyle(styleAll);
		}
		
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellFormula("E"+Integer.toString(i+1)  +"+F"+ Integer.toString(i) );
		cell5.setCellStyle(styleMoneyRight);
		
/*		Double importe = repo.getImporte().doubleValue();
		saldoCheque += importe;
		
		HSSFCell cell5 = row.createCell(5);
		if(repo.getImporte() !=null){
		  cell5.setCellValue(saldoCheque);
		  cell5.setCellStyle(styleMoneyRight);
	    }else{
		  cell5.setCellValue(new HSSFRichTextString(""));
		  cell5.setCellStyle(styleAll);
		}
		
*/		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Nro.Cheque"));
		cell1.setCellStyle(styleHeader);
		
		HSSFCell cell11 = row.createCell(2);
		cell11.setCellValue(new HSSFRichTextString("O.Pago"));
		cell11.setCellStyle(styleHeader);
		
		HSSFCell cell12 = row.createCell(3);
		cell12.setCellValue(new HSSFRichTextString("Razón Social"));
		cell12.setCellStyle(styleHeader);
		
		HSSFCell cell13 = row.createCell(4);
		cell13.setCellValue(new HSSFRichTextString("Importe"));
		cell13.setCellStyle(styleHeader);
		
		HSSFCell cell2 = row.createCell(5);
		cell2.setCellValue(new HSSFRichTextString("Saldo"));
		cell2.setCellStyle(styleHeader);

				
		row.setHeight((short) 500);
		
		
		HSSFCellStyle st =  wb.createCellStyle();
		st.setDataFormat((short) 6);
//		st.setFillForegroundColor( HSSFColor.GREY_25_PERCENT.index);
//		st.setFillForegroundColor( HSSFColor.AQUA.index);
		st.setFillForegroundColor( HSSFColorPredefined.AQUA.getIndex());
		//st.setFillBackgroundColor((short) HSSFCellStyle.SOLID_FOREGROUND);
		st.setFillBackgroundColor(FillPatternType.SOLID_FOREGROUND.getCode());
		st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
	//	FillPatternType.SOLID_FOREGROUND
		
		for(int xi=0;xi<=5;xi++){
	      row.getCell(xi).setCellStyle(st);
		}
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila,String saldo) throws SystemException {

		String tituloReporte = "Informe Cheque Pendientes de Cobro - " + saldo;
		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		
		HSSFCell cell12 = rowTitulo.createCell(5);
		cell12.setCellValue(new HSSFRichTextString("Impresión: "
				+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell12.setCellStyle(getStyleAllCenter(wb));
		
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 4));
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 5, 10));
		fila++;

		return fila;
	}
	
}

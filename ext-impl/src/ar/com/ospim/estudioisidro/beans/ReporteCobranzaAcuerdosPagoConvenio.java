package ar.com.ospim.estudioisidro.beans;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;

public class ReporteCobranzaAcuerdosPagoConvenio extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCobranzaAcuerdosPagoConvenio.class);

	public static HSSFWorkbook generaReporte(List<ConvenioPagosReporte> convenio) {
		_log.debug("generando reporte");

		try {							
			return generarReporte(convenio);	
			
		} catch (Exception e) {
			_log.error("Error al generar reporter ReporteCobranzaAcuerdosPagoConvenio", e);
			return null;
		}
		
	}
	
      
	private static HSSFWorkbook generarReporte(List<ConvenioPagosReporte> convenio) {
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
		
	
		HSSFCellStyle styleWithBorder = getStyleAllWithBorder(wb);


		HSSFSheet sheet = wb.createSheet("AVISO_VENCIMIENTO_CONVENIO");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE );
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i, styleWithBorder);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb,  styleWithBorder);
		
						
		for (ConvenioPagosReporte repo : convenio) {
			    	i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
			    			styleMoneyRight, styleFechaLeftTop, styleAllTop,
			    			styleMoneyRightTop,styleWithBorder); 
		}
		
		
		
		i++;
		
		for(int x=0;x<58;x++){
			sheet.autoSizeColumn((short) x);
		}
		
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			ConvenioPagosReporte convenio, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop, HSSFCellStyle styleWithBorder) {

		HSSFRow row = sheet.createRow(i);
		
		
		HSSFCell cell0 = row.createCell(0);
		
		cell0.setCellValue(new HSSFRichTextString(convenio.getTipoConvenio()));
		cell0.setCellStyle(styleWithBorder);
		
		HSSFCell cell1 = row.createCell(1);
		
		cell1.setCellValue(new HSSFRichTextString(convenio.getRazonSoc()) );
		cell1.setCellStyle(styleWithBorder);
		
		HSSFCell cell2 = row.createCell(2);
		
		cell2.setCellValue(new HSSFRichTextString(convenio.getCuit()));
		cell2.setCellStyle(styleWithBorder);
		
		HSSFCell cell3 = row.createCell(3);
		
		cell3.setCellValue(new HSSFRichTextString(convenio.getNumeroConvenio()));
		cell3.setCellStyle(styleWithBorder);
		
		HSSFCell cell4 = row.createCell(4);
		
		cell4.setCellValue(convenio.getIdCuota().doubleValue());
		cell4.setCellStyle(styleWithBorder);
		
		

		HSSFCell cell5 = row.createCell(5);
		
		cell5.setCellValue(new HSSFRichTextString(convenio.getFechaPagoAsString()));
		cell5.setCellStyle(styleWithBorder);
	
	
		HSSFCell cell6 = row.createCell(6);
		
		cell6.setCellValue(convenio.getImporte().doubleValue());
		cell6.setCellStyle(styleWithBorder);
		
		
		HSSFCell cell7 = row.createCell(7);
		
		cell7.setCellValue(new HSSFRichTextString(convenio.getContactoEmail()));
		cell7.setCellStyle(styleWithBorder);
		
		
		return ++i;
	}
	


	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb, HSSFCellStyle styleWithBorder) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Tipo Convenio"));
		cell0.setCellStyle(styleWithBorder);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Razón Social"));
		cell1.setCellStyle(styleWithBorder);
		

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Cuit"));
		cell2.setCellStyle(styleWithBorder);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Número Convenio"));
		cell3.setCellStyle(styleWithBorder);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Cuota"));
		cell4.setCellStyle(styleWithBorder);
		

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Fecha de Pago"));
		cell5.setCellStyle(styleWithBorder);


		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Importe"));
		cell6.setCellStyle(styleWithBorder);
		
		
		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Email"));
		cell7.setCellStyle(styleWithBorder);
		
		
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila, HSSFCellStyle styleWithBorder) {

		String tituloReporte = "Reporte cobranza acuerdos pago por convenio";

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);
        cell.setCellStyle(styleWithBorder);
		
		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		cell.setCellStyle(styleWithBorder);

		
		

		return fila;
	}
}
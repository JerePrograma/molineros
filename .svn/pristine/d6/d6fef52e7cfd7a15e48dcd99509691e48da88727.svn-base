package ar.com.ospim.autorizaciones.reportes.action;

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
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.ReporteCuentasFiltrarInterbanking;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

public class ReporteInterbanking extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteInterbanking.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		
		try {
			List<IntegracionDetalleDS> integracionDS = null;
			int idLote = ParamUtil.getInteger(req, "id_lote");
			List<ReporteCuentasFiltrarInterbanking> repo =  new ArrayList<ReporteCuentasFiltrarInterbanking>();
			integracionDS = IntegracionServiceUtil.detalleLiquidacionByIdLotePorOp(idLote);
	
			for (IntegracionDetalleDS integracionDetalleDS : integracionDS) {
				ReporteCuentasFiltrarInterbanking r = new ReporteCuentasFiltrarInterbanking();
				r.setOrdenPago(String.valueOf(integracionDetalleDS.getOrdenPago()));
				r.setCuitPrestador(integracionDetalleDS.getCuitPrestador());
				
				if (!exiteElemento(repo,r)) {
				   repo.add(r);
				} 
			}
							
			return generarReporte(repo);	
			
		} catch (Exception e) {
			_log.error("Error al generar Ordenes de Pago", e);
			return null;
		}
		
	}
	
     private static boolean exiteElemento(List<ReporteCuentasFiltrarInterbanking> repo, ReporteCuentasFiltrarInterbanking r) {
		 
    	 for (ReporteCuentasFiltrarInterbanking reporteCuentas : repo) {
    		 if (reporteCuentas.getOrdenPago().equals(r.getOrdenPago())) {
    			 return true;
    		 }
    		 
    	 }
    	 
    	 return false;
    	 
     }
    
	
	
	private static HSSFWorkbook generarReporte(List<ReporteCuentasFiltrarInterbanking>  reporte) {
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
				
		for (ReporteCuentasFiltrarInterbanking repo : reporte) {
			  		    	
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
			ReporteCuentasFiltrarInterbanking repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		
		  cell0.setCellValue(repo.getOrdenPago() );
		  cell0.setCellStyle(styleAll);
		
		
		HSSFCell cell1 = row.createCell(1);
	
		  cell1.setCellValue(new HSSFRichTextString(repo.getCuitPrestador()));
		  cell1.setCellStyle(styleAll);
	
		
		
		
		return ++i;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Orden De pago"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("CUIT Prestador"));
		cell1.setCellStyle(styleHeader);
		
		
		return ++i;
	}

	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila) {

		String tituloReporte = "Ordenes de PAgo";

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		
		HSSFCell cell12 = rowTitulo.createCell(12);
		cell12.setCellValue(new HSSFRichTextString("Cuit Prestador "
				+ DateUtils.format(new Date(), DateUtils.SHORT)));
		cell12.setCellStyle(getStyleAllCenter(wb));
		
		

		return fila;
	}
}
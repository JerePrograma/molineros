package ar.com.uoma.reportes;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

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
import ar.com.uoma.beans.EmpresaSituacionFinanciera;

public class ControlIngresosEgresosEmpresas1Excel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ControlIngresosEgresosNivel1Excel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) throws SystemException, SQLException {
		_log.debug("generando reporte");

		String leyenda = ParamUtil.getString(req, "leyenda");
		
		Map<String, EmpresaSituacionFinanciera> reporte = (TreeMap<String, EmpresaSituacionFinanciera>) req.getSession().getAttribute("CONTROL_DECLARADOS");
		
		return generarReporte(reporte,leyenda);
		
	}

	private static HSSFWorkbook generarReporte(Map<String, EmpresaSituacionFinanciera>  reporte,String leyenda) {
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
		ps.setLandscape(true);
		int i = 0;
		
		i = createTitulosHeader(wb, sheet, i,leyenda);

		
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
					styleHeaderRight, wb);
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);
		
		//wb.setRepeatingRowsAndColumns(0, 0, 7, 0, i - 1);


		
		
		String comprobante = new String();
		HSSFRow row = sheet.createRow(i);
		
		
		Double totalDeclarado=0D;
		Double totalPagado=0D;
		Double totalEstimado=0D;
		Double totalPendientePago=0D;
		
		for (EmpresaSituacionFinanciera repo : reporte.values()) {
				totalDeclarado += repo.getTotal().doubleValue();
				totalPagado += repo.getTotalPagado().doubleValue();
				totalEstimado += repo.getEstimado().doubleValue();
				
				totalPendientePago += repo.getTotal().doubleValue()-repo.getTotalPagado().doubleValue()>0?repo.getTotal().doubleValue()-repo.getTotalPagado().doubleValue():0D;
				
				i = generarDatos(sheet, i, repo, styleFechaLeft, styleAll,
						styleMoneyRight, styleFechaLeftTop, styleAllTop,
						styleMoneyRightTop);
			
		}

	    i++;	
		i++;
		
		row = sheet.createRow(i);
		HSSFCell createCell00 = row.createCell(1);
		createCell00.setCellValue(new HSSFRichTextString("Totales:"));
		
		HSSFCell cell3t = row.createCell(2);
		cell3t.setCellValue(totalDeclarado);
		cell3t.setCellStyle(styleMoneyRight);
		
		HSSFCell cell4t = row.createCell(3);
		cell4t.setCellValue(totalPagado);
		cell4t.setCellStyle(styleMoneyRight);
		
		HSSFCell cell5t = row.createCell(4);
		cell5t.setCellValue(totalPendientePago);
		cell5t.setCellStyle(styleMoneyRight);
		
		HSSFCell cell6t = row.createCell(5);
		cell6t.setCellValue(totalEstimado);
		cell6t.setCellStyle(styleMoneyRight);
		
		i++;
		
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
			
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			EmpresaSituacionFinanciera repo, HSSFCellStyle stylefechaLeft,
			HSSFCellStyle styleAll, HSSFCellStyle styleMoneyRight,
			HSSFCellStyle styleFechaLeftTop, HSSFCellStyle styleAllTop,
			HSSFCellStyle styleMoneyRightTop) {

		HSSFRow row = sheet.createRow(i);
		
		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(repo.getCuit() ));
		cell0.setCellStyle(styleAll);
		
		
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(repo.getRazonSoc()));
		cell1.setCellStyle(styleAll);
		
		HSSFCell cell3 = row.createCell(2);
		cell3.setCellValue(repo.getTotal().doubleValue());
		cell3.setCellStyle(styleMoneyRight);
		
		HSSFCell cell4 = row.createCell(3);
		cell4.setCellValue(repo.getTotalPagado().doubleValue());
		cell4.setCellStyle(styleMoneyRight);
		
		HSSFCell cell5 = row.createCell(4);
		Double pendiente = repo.getTotal().doubleValue()-repo.getTotalPagado().doubleValue()>0?repo.getTotal().doubleValue()-repo.getTotalPagado().doubleValue():0D;
		cell5.setCellValue(pendiente);
		cell5.setCellStyle(styleMoneyRight);
		
		HSSFCell cell6 = row.createCell(5);
		cell6.setCellValue(repo.getEstimado().doubleValue());
		cell6.setCellStyle(styleMoneyRight);
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
		cell1.setCellValue(new HSSFRichTextString("Descripción"));
		cell1.setCellStyle(styleHeader);
		
		
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Declarado"));
		cell2.setCellStyle(styleHeader);
		
		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Pagado"));
		cell3.setCellStyle(styleHeader);
		
		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Pendiente Pago"));
		cell4.setCellStyle(styleHeader);
		
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Estimado"));
		cell5.setCellStyle(styleHeader);
		
		return ++i;
	}


	
	
	private static int createTitulosHeader(HSSFWorkbook wb, HSSFSheet sheet,
			int fila,String leyenda) {

		String tituloReporte = "Control de Ingresos y Egresos (" + leyenda +")";

		HSSFRow rowTitulo = sheet.createRow(fila);
		HSSFCell cell = rowTitulo.createCell(0);

		cell.setCellValue(new HSSFRichTextString(tituloReporte
					.toUpperCase()));
		cell.setCellStyle(getStyleBoldUnderlinedHeader(wb, 12));
		
		sheet.addMergedRegion(new CellRangeAddress(fila, fila, 0, 7));
		fila++;
		return fila;
	}
}

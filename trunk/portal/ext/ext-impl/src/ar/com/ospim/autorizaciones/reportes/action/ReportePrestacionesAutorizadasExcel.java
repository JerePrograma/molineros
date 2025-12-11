package ar.com.ospim.autorizaciones.reportes.action;

import java.text.SimpleDateFormat;
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
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.autorizaciones.beans.EstadisticaPrestAutorizada;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;

public class ReportePrestacionesAutorizadasExcel extends ReporteXLS {
	
	private static Log _log = LogFactoryUtil
			.getLog(ReportePrestacionesAutorizadasExcel.class);

	public static HSSFWorkbook generaReporte(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		
		String fechaDia = ParamUtil.getString(renderRequest, "fechaDia");
		String fechaMes = ParamUtil.getString(renderRequest, "fechaMes");
		fechaMes = String.valueOf(Integer.valueOf(fechaMes) + 1);
		String fechaAnio = ParamUtil.getString(renderRequest, "fechaAnio");
		
		
		Date fecha = new Date();
		try {
			fecha = format.parse(fechaDia + "-" + fechaMes
					+ "-" + fechaAnio);
			
		} catch (Exception e) {
			_log.error("Error al parsear período", e);
			return null;
		}

		List<EstadisticaPrestAutorizada> lista = new ArrayList<EstadisticaPrestAutorizada>();

		try {

			 lista = PreAutorizacionServiceUtil.estadisticaPrestacionesAutorizadas(fecha);
			 
			 return generaReporte(lista,fecha);
			 
		} catch (SystemException e) {
			_log.debug("Error al generar Estadistica de Prestaciones Autorizadas por PS");
			
			return null;
		}	
	}

	public static HSSFWorkbook generaReporte(List<EstadisticaPrestAutorizada> reporte, Date fecha) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleFechaLeft = getStyleDateWithBorder(wb);		

		HSSFCellStyle styleTop = getStyleAllWithBorder(wb);
		

		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);

		HSSFCellStyle styleHeader = getStyleWhiteHeaderWithBorder(wb);

		HSSFCellStyle styleHeaderLeft = getStyleHeaderWithBorder(wb);
		

		HSSFCellStyle styleHeaderRight = getStyleAllWithBorder(wb);

		HSSFCellStyle styleFechaLeftTop = getStyleDateWithBorder(wb);
		
		
		HSSFSheet sheet = wb.createSheet("Prest_Autorizadas");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Estadística Prestaciones Autorizadas - Período:"
						+ DateUtils.format(fecha, DateUtils.PERIODO) ) );
		
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		
		int i = 1;
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
				styleHeaderRight, wb);

		for (EstadisticaPrestAutorizada repo : reporte) {
			
			HSSFRow row = sheet.createRow(i);
			i++;
			
			HSSFCell cell0 = row.createCell(0);
			cell0.setCellValue(new HSSFRichTextString(repo.getCodigo()));
			cell0.setCellStyle(styleFechaLeft);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(repo.getDescripcion()));
			cell1.setCellStyle(styleAll);
			
			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(new HSSFRichTextString(String.valueOf(repo.getCantidad())));
			cell2.setCellStyle(styleAll);

			
		}

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);

		return wb;
	}

	

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Código"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Descripción"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Cantidad"));
		cell2.setCellStyle(styleHeader);

		return ++i;
	}

		
}



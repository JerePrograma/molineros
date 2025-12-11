package ar.com.ospim.liquidaciones.reportes.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.reportes.action.ReporteOrdenesPagoAction.ReporteOrdenPagoOspim;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteOrdenesPagoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteOrdenesPagoExcel.class);

	public static HSSFWorkbook generaReporteOPs(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);

			List<ReporteOrdenPagoOspim> reporte = OrdenPagoServiceUtil
					.reporteOrdenPagoOspim(fechaIni, fechaFin);

			return generarReporte(fechaIni, fechaFin, reporte);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ReporteOrdenPagoOspim> reporte) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		// sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		// sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		// ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Ordenes de Pago - Desde:"
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " - Hasta:"
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
		sheet.getFooter().setRight(
				HSSFFooter.page() + "/" + HSSFFooter.numPages());
		generarHeader(sheet, styleHeader);

		BigDecimal total = BigDecimal.ZERO;
		BigDecimal totalAnulado = BigDecimal.ZERO;

		int i = 2;
		for (ReporteOrdenPagoOspim repo : reporte) {
			HSSFRow row = sheet.createRow(i);
			generarDatos(row, repo, styleDate, styleAll, styleMoney);
			total = total.add(repo.getImporteOp());
			if (repo.getFechaBajaOP() != null) {
				totalAnulado = totalAnulado.add(repo.getImporteOp());
			}
			i++;
		}

		/*
		 * HSSFRow row = sheet.createRow(i); HSSFCell cell3 = row.createCell(0);
		 * cell3.setCellValue(new HSSFRichTextString("Total OPs"));
		 * cell3.setCellStyle(getStyleBoldWithBorder(wb));
		 * 
		 * HSSFCell cell4 = row.createCell(3);
		 * cell4.setCellValue(total.doubleValue());
		 * cell4.setCellStyle(styleMoney); sheet.addMergedRegion(new
		 * CellRangeAddress(i, i, 0, 2));
		 * 
		 * HSSFRow rowTotalAnulado = sheet.createRow(i + 1); HSSFCell cellAn2 =
		 * rowTotalAnulado.createCell(0); cellAn2.setCellValue(new
		 * HSSFRichTextString("Total OPs Anuladas"));
		 * cellAn2.setCellStyle(getStyleBoldWithBorder(wb));
		 * 
		 * HSSFCell cellAn3 = rowTotalAnulado.createCell(3);
		 * cellAn3.setCellValue(totalAnulado.doubleValue());
		 * cellAn3.setCellStyle(styleMoney); sheet.addMergedRegion(new
		 * CellRangeAddress(i + 1, i + 1, 0, 2));
		 * 
		 * HSSFRow rowDiff = sheet.createRow(i + 2); HSSFCell cellDiff2 =
		 * rowDiff.createCell(0); cellDiff2.setCellValue(new
		 * HSSFRichTextString("Neto OPs"));
		 * cellDiff2.setCellStyle(getStyleBoldWithBorder(wb));
		 * 
		 * HSSFCell cellDiff3 = rowDiff.createCell(3);
		 * cellDiff3.setCellValue(total.subtract(totalAnulado).doubleValue());
		 * cellDiff3.setCellStyle(styleMoney); sheet.addMergedRegion(new
		 * CellRangeAddress(i + 2, i + 2, 0, 2));
		 */

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.setColumnWidth(3, 5200);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);

		return wb;
	}

	private static void generarDatos(HSSFRow row, ReporteOrdenPagoOspim repo,
			HSSFCellStyle styleDate, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoney) {

		HSSFCell cell1 = row.createCell(0);
		cell1.setCellValue(repo.getIdOrdenPago());
		cell1.setCellStyle(styleAll);

		HSSFCell cell = row.createCell(1);
		cell.setCellValue(repo.getFecha());
		cell.setCellStyle(styleDate);

		HSSFCell cellAcre = row.createCell(2);
		String cuit = repo.getAcreedor().getCuit();
		String sucursal = repo.getAcreedor().getSucursal();
		if (repo.getSeccional() != null && repo.getSeccional().getId() != 0) {
			sucursal = String.valueOf(repo.getSeccional().getId());
		}
		cellAcre.setCellValue(new HSSFRichTextString((cuit != null ? cuit : "")
				+ (sucursal != null ? "-" + sucursal : "")));
		cellAcre.setCellStyle(styleAll);

		HSSFCell cellAcreRZ = row.createCell(3);
		cellAcreRZ.setCellValue(new HSSFRichTextString(repo.getAcreedor()
				.getRazon_soc()));
		cellAcreRZ.setCellStyle(styleAll);
		
		HSSFCell cellFavorDe = row.createCell(4);
		cellFavorDe.setCellValue(new HSSFRichTextString(repo.getaFavorDe()));
		cellFavorDe.setCellStyle(styleAll);


		HSSFCell cell3 = row.createCell(5);
		if (repo.getImporteOp() != null) {
			cell3.setCellValue(repo.getImporteOp().doubleValue());
		}
		cell3.setCellStyle(styleMoney);

		HSSFCell cell1bajaOP = row.createCell(6);
		if (repo.getFechaBajaOP() != null) {
			cell1bajaOP.setCellValue(repo.getFechaBajaOP());
		}
		cell1bajaOP.setCellStyle(styleDate);

	}

	private static void generarHeader(HSSFSheet sheet, HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(1);

		HSSFCell cell1 = row.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("OP"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString("Fecha"));
		cell.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("Acreedor"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Acreedor Razon Social"));
		cellRaz.setCellStyle(styleHeader);
		
		HSSFCell cellFav = row.createCell(4);
		cellFav.setCellValue(new HSSFRichTextString("A FAVOR DE"));
		cellFav.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(5);
		cell3.setCellValue(new HSSFRichTextString("Importe OP"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cellBajaOP = row.createCell(6);
		cellBajaOP.setCellValue(new HSSFRichTextString("Baja OP"));
		cellBajaOP.setCellStyle(styleHeader);

	}

}

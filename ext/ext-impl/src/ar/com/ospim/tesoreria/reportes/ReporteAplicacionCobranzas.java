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
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.ReporteCobranzaActaBean;
import ar.com.ospim.tesoreria.beans.ReporteCobranzaConvenioBean;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteAplicacionCobranzas extends ReporteXLS {

	private static Log _log = LogFactoryUtil
			.getLog(ReporteAplicacionCobranzas.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);

			List<ReporteCobranzaConvenioBean> reporteCobranzasConvenios = ConvenioServiceUtil
					.reporteCobranzaConvenios(fechaIni, fechaFin);
			List<ReporteCobranzaActaBean> reporteCobranzasActas = ActaServiceUtil
					.reporteCobranzaActas(fechaIni, fechaFin);
			return generarReporte(fechaIni, fechaFin, reporteCobranzasActas,
					reporteCobranzasConvenios);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ReporteCobranzaActaBean> reporteCobranzasActas,
			List<ReporteCobranzaConvenioBean> reporteCobranzasConvenios) {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeader = getStyleHeader(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);
		HSSFCellStyle styleNumber = getStyleNumber(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		int i = 0;
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString(
				"Reporte de Aplciacion de Cobranzas"));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 14));
		i++;

		HSSFRow rowTitulo2 = sheet.createRow(i);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 14));
		i++;

		HSSFRow rowTituloActas = sheet.createRow(i);
		HSSFCell cellActas = rowTituloActas.createCell(0);
		cellActas.setCellValue(new HSSFRichTextString("Actas"));
		cellActas.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 14));
		i++;

		i = ReporteActas.crearHeaderPrincipal(wb, sheet, i, styleHeader, false, false);
		HSSFRow rowTituloActa = sheet.getRow(i - 1);
		HSSFCell cellTituloPagadoActa = rowTituloActa.createCell(14);
		cellTituloPagadoActa.setCellValue(new HSSFRichTextString("Pagado"));
		cellTituloPagadoActa.setCellStyle(styleHeader);
		for (ReporteCobranzaActaBean repo : reporteCobranzasActas) {
			i = ReporteActas.generarDatos(repo, i, styleAll, styleDate,
					styleMoney, styleNumber, sheet, false, false);

			HSSFRow rowacta = sheet.getRow(i - 1);
			HSSFCell cellPagado = rowacta.createCell(14);
			cellPagado.setCellValue(repo.getPagado().doubleValue());
			cellPagado.setCellStyle(styleMoney);
		}

		i++;
		HSSFRow rowTituloConvenios = sheet.createRow(i);
		HSSFCell cellConvenios = rowTituloConvenios.createCell(0);
		cellConvenios.setCellValue(new HSSFRichTextString("Convenios"));
		cellConvenios.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 14));
		i++;

		i = ReporteConvenios.crearHeaderPrincipal(wb, sheet, i, styleHeader, false);
		HSSFRow rowTituloConvenio = sheet.getRow(i - 1);
		HSSFCell cellTituloPagado = rowTituloConvenio.createCell(11);
		cellTituloPagado.setCellValue(new HSSFRichTextString("Pagado"));
		cellTituloPagado.setCellStyle(styleHeader);
		for (ReporteCobranzaConvenioBean repo : reporteCobranzasConvenios) {
			i = ReporteConvenios.generarDatos(repo, i, styleAll, styleDate,
					styleMoney, sheet, true, false);
			HSSFRow rowConvenio = sheet.getRow(i - 1);
			HSSFCell cellPagado = rowConvenio.createCell(11);
			cellPagado.setCellValue(repo.getPagado().doubleValue());
			cellPagado.setCellStyle(styleMoney);
		}

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		sheet.autoSizeColumn((short) 11);
		sheet.autoSizeColumn((short) 12);
		sheet.autoSizeColumn((short) 13);
		sheet.autoSizeColumn((short) 14);

		return wb;
	}

}

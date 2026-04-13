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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteSubdiario;

public class ReporteSubdiarioEgresoInterbankingExcel extends ReporteSubdiario {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSubdiarioEgresoExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		List<Seccional> seccionales = TraeListasServiceUtil.getSeccionales();

		int entidad = ParamUtil.getInteger(req, "entidad");
		int tipopago = ParamUtil.getInteger(req, "tipopago");
		int ctabcria = ParamUtil.getInteger(req, "ctabcria");

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			
			List<OrdenPagoOspim> reporte = null;
			reporte = OrdenPagoServiceUtil.reporteOrdenPagoInterbanking(fechaIni, fechaFin, tipopago, ctabcria, entidad);
			
			return generarReporte( reporte, entidad);

			
		} catch (Exception e) {
			_log.error("Error al generar subdiario egresos", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(List<OrdenPagoOspim> reporte,int entidad) throws Exception {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setAlignment(HorizontalAlignment.LEFT);
		// styleHeaderLeft.setBorderLeft(BorderStyle.THIN);
		// styleHeaderLeft.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleDateLeft = getStyleDate(wb);
		// styleDateLeft.setBorderLeft(BorderStyle.THIN);
		HSSFCellStyle styleAllLeft = getStyleAll(wb);
		// styleAllLeft.setBorderLeft(BorderStyle.THIN);
		HSSFCellStyle styleBoldLeft = getStyleBold(wb);
		// styleBoldLeft.setBorderLeft(BorderStyle.THIN);
		HSSFCellStyle styleMoneyLeft = getStyleMoney(wb);
		// styleMoneyLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleHeaderRight = getStyleHeader(wb);
		styleHeaderRight.setAlignment(HorizontalAlignment.RIGHT);
		// styleHeaderRight.setBorderRight(BorderStyle.THIN);
		// styleHeaderRight.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleDateRight = getStyleDate(wb);
		// styleDateRight.setBorderRight(BorderStyle.THIN);
		HSSFCellStyle styleAllRight = getStyleAll(wb);
		// styleAllRight.setBorderRight(BorderStyle.THIN);
		HSSFCellStyle styleBoldRight = getStyleBold(wb);
		// styleBoldRight.setBorderRight(BorderStyle.THIN);
		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		// styleMoneyRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleHeader = getStyleHeader(wb);
		// styleHeader.setBorderTop(BorderStyle.THIN);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleTotalesL = getStyleBold(wb);
		// styleTotalesL.setBorderLeft(BorderStyle.THIN);
		// styleTotalesL.setBorderBottom(BorderStyle.THIN);
		HSSFCellStyle styleTotalesR = getStyleMoneyBold(wb);
		// styleTotalesR.setBorderRight(BorderStyle.THIN);
		// styleTotalesR.setBorderBottom(BorderStyle.THIN);

		HSSFCellStyle styleTotales = getStyleAll(wb);
		// styleTotales.setBorderBottom(BorderStyle.THIN);

		HSSFCellStyle styleTotalesMoneyR = getStyleMoneyBold(wb);
		// styleTotalesMoneyR.setBorderRight(BorderStyle.THIN);
		// styleTotalesMoneyR.setBorderBottom(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
		// styleMoneyRightTop.setBorderRight(BorderStyle.THIN);
		// styleMoneyRightTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllTop = getStyleAll(wb);
		// styleAllTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllLeftTop = getStyleAll(wb);
		// styleAllLeftTop.setBorderLeft(BorderStyle.THIN);
		// styleAllLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAllRightTop = getStyleAll(wb);
		// styleAllRightTop.setBorderRight(BorderStyle.THIN);
		// styleAllRightTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleDateLeftTop = getStyleDate(wb);
		// styleAllLeftTop.setBorderLeft(BorderStyle.THIN);
		// styleDateLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleTotalesMoneyL = getStyleMoneyBold(wb);
		// styleTotalesMoneyL.setBorderLeft(BorderStyle.THIN);
		// styleTotalesMoneyL.setBorderBottom(BorderStyle.THIN);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.LEGAL_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		int i = 0;

		if (entidad == WebKeysGlobal.OSPIM) {
		}
		if (entidad == WebKeysGlobal.UOMA) {
		}


		if (entidad == WebKeysGlobal.OSPIM) {
		} else {
		}
		try {
			for (OrdenPagoOspim repo : reporte) {
				i = generarDatos(sheet, i, repo, styleDateLeft, styleDateRight,
						styleAllLeft, styleAllRight, styleBoldLeft,
						styleBoldRight, styleMoneyLeft, styleMoneyRight,
						styleAll, styleDate, styleTotalesL, styleTotalesR,
						styleTotales, styleTotalesMoneyR, styleMoneyRightTop,
						styleAllTop, styleAllLeftTop, styleAllRightTop,
						styleDateLeftTop, styleTotalesMoneyL, entidad);
			}
		} catch (Exception e) {
			throw e;
		}

		
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		return wb;
	}

	private static int generarDatos(HSSFSheet sheet, int i,
			OrdenPagoOspim repo, HSSFCellStyle styleDateLeft,
			HSSFCellStyle styleDateRight, HSSFCellStyle styleAllLeft,
			HSSFCellStyle styleAllRight, HSSFCellStyle styleBoldLeft,
			HSSFCellStyle styleBoldRight, HSSFCellStyle styleMoneyLeft,
			HSSFCellStyle styleMoneyRight, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate, HSSFCellStyle styleTotalesL,
			HSSFCellStyle styleTotalesR, HSSFCellStyle styleTotales,
			HSSFCellStyle styleTotalesMoneyR, HSSFCellStyle styleMoneyRightTop,
			HSSFCellStyle styleAllTop, HSSFCellStyle styleAllLeftTop,
			HSSFCellStyle styleAllRightTop, HSSFCellStyle styleDateLeftTop,
			HSSFCellStyle styleTotalesMoneyL,int entidad) throws Exception {

		int max = 0;

		try {
			HSSFRow row = sheet.createRow(i);
			HSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(new HSSFRichTextString(repo.getId().toString()));
			cell1.setCellStyle(styleAllTop);
			
			HSSFCell cellAcre = row.createCell(1);
			cellAcre.setCellValue(new HSSFRichTextString(repo.getCuit()));
			cellAcre.setCellStyle(styleAllTop);
			
			i++;
			
		} catch (Exception e) {
			_log.error(e);
			throw e;
		}

		return i;
	}

	
	
}

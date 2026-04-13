package ar.com.ospim.liquidaciones.reportes.action;

import java.math.BigDecimal;
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

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteReintegrosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteReintegrosExcel.class);

	public static HSSFWorkbook generaReporteReintegrosExcel(
			HttpServletRequest renderRequest, HttpServletResponse res) {
		String entidad = ParamUtil.getString(renderRequest, "entidad", null);
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest,
				"fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest,
				"fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(renderRequest,
				"fechaDesdeAnio");
		Date fechaDesde = null;
		try {
			fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		String fechaHastaDia = ParamUtil.getString(renderRequest,
				"fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest,
				"fechaHastaMes");
		String fechaHastaAnio = ParamUtil.getString(renderRequest,
				"fechaHastaAnio");
		Date fechaHasta = null;
		try {
			fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
				"periodoDesdeMesAnio");
		Date periodoDesde = null;
		try {
			periodoDesde = formatoDePeriodos.parse(Integer
					.parseInt(periodoDesdeMesAnio.substring(0, 1))
					+ 1 + "/" + periodoDesdeMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoDesde = null;
		}
		String periodoHastaMesAnio = ParamUtil.getString(renderRequest,
				"periodoHastaMesAnio");
		Date periodoHasta = null;
		try {
			periodoHasta = formatoDePeriodos.parse(Integer
					.parseInt(periodoHastaMesAnio.substring(0, 1))
					+ 1 + "/" + periodoHastaMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodoHasta = null;
		}

		int seccional = ParamUtil
				.getInteger(renderRequest, "id_seccional_r", 0);
		int numero = ParamUtil.getInteger(renderRequest, "numero", 0);
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
		String cuil_titular = ParamUtil
				.getString(renderRequest, "cuil_titular");
		int estado = ParamUtil.getInteger(renderRequest, "estado", 0);
		String alta_usr = ParamUtil.getString(renderRequest, "alta_usr", "");
		String codPrest = ParamUtil.getString(renderRequest, "codPrest", null);

		String pagos = ParamUtil.getString(renderRequest, "pagos", "0");
		String presta = ParamUtil.getString(renderRequest, "presta", "0");
		String ortop = ParamUtil.getString(renderRequest, "ortop", "0");
		String protesis = ParamUtil.getString(renderRequest, "protesis", "0");
		List<Reintegro> busqueda = new ArrayList<Reintegro>();
		try {
				if (presta.equalsIgnoreCase("1")) {
					List<Reintegro> busquedaPrest = ReintegroServiceUtil
							.buscarReintegros(entidad, fechaDesde, fechaHasta,
									periodoDesde, periodoHasta, codPrest,
									nroAfi, inte, cuil_titular, seccional,
									numero, pagos, alta_usr);
					for (int i = 0; i < busquedaPrest.size(); i++) {
						busqueda.add(busquedaPrest.get(i));
					}
				}
				if (ortop.equalsIgnoreCase("1")) {
					codPrest = null;
					List<Reintegro> busquedaProt = ReintegroServiceUtil
							.buscarReintegrosOdoOrto(entidad, fechaDesde,
									fechaHasta, periodoDesde, periodoHasta,
									codPrest, nroAfi, inte, cuil_titular,
									seccional, numero, pagos, alta_usr, estado);
					for (int i = 0; i < busquedaProt.size(); i++) {
						busqueda.add(busquedaProt.get(i));
					}
				}
				if (protesis.equalsIgnoreCase("1")) {
					codPrest = null;
					List<Reintegro> busquedaOrto = ReintegroServiceUtil
							.buscarReintegrosOdoProtesis(entidad, fechaDesde,
									fechaHasta, periodoDesde, periodoHasta,
									codPrest, nroAfi, inte, cuil_titular,
									seccional, numero, pagos, alta_usr, estado);
					for (int i = 0; i < busquedaOrto.size(); i++) {
						busqueda.add(busquedaOrto.get(i));
					}
				}
				renderRequest
						.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
				renderRequest.setAttribute(
						WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, busqueda);
				if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
					seccional = busqueda.get(0).getId_seccional();
				}
				renderRequest.setAttribute(
						WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, seccional);
		} catch (Exception e) {
			_log.error("Error al generar reporte de reintegros totales", e);
			return null;
		}

		List<ReporteOrdenPagoReintegros> list = new ArrayList<ReporteOrdenPagoReintegros>();
		for (int i = 0; i < busqueda.size(); i++) {
			Afiliado afiliado = busqueda.get(i).getAfiliado();
			Reintegro reintegro = busqueda.get(i);
			ReporteOrdenPagoReintegros reporte = new ReporteOrdenPagoReintegros();
			reporte.setAfiliado(afiliado);
			reporte.setReintegro(reintegro);
			list.add(reporte);
		}
		return generarReporte(list);
	}

	private static HSSFWorkbook generarReporte(
			List<ReporteOrdenPagoReintegros> list) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Hoja 1");

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleBold = getStyleBold(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);

		if (list == null || list.isEmpty()) {
			return wb;
		}

		int index = 0;
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0H = rowHeader.createCell(0);
		cell0H.setCellValue(new HSSFRichTextString("Fecha"));
		cell0H.setCellStyle(styleBold);

		HSSFCell cell1H = rowHeader.createCell(1);
		cell1H.setCellValue(new HSSFRichTextString("Seccional"));
		cell1H.setCellStyle(styleBold);

		HSSFCell cell2H = rowHeader.createCell(2);
		cell2H.setCellValue(new HSSFRichTextString("Reintegro N°"));
		cell2H.setCellStyle(styleBold);

		HSSFCell cell3H = rowHeader.createCell(3);
		cell3H.setCellValue(new HSSFRichTextString("Afiliado (Cuil Titular, Inte)"));
		cell3H.setCellStyle(styleBold);

		HSSFCell cell5H = rowHeader.createCell(4);
		cell5H.setCellValue(new HSSFRichTextString("Importe"));
		cell5H.setCellStyle(styleBold);

		BigDecimal total = new BigDecimal("0");
		for (ReporteOrdenPagoReintegros repo : list) {
			index++;
			total= total.add(crearHeader(sheet, index, repo, repo.getReintegro(), styleBold,
					styleAll, styleDate));			
		}
		index++;
		HSSFRow rowTotal = sheet.createRow(index);

		HSSFCell cell = rowTotal.createCell(3);
		cell.setCellValue(new HSSFRichTextString("Total"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowTotal.createCell(4);
		cell1.setCellValue(total.doubleValue());
		cell1.setCellStyle(styleAll);

		index++;
		sheet.createRow(index);

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		return wb;
	}

	private static BigDecimal crearHeader(HSSFSheet sheet, int index,
			ReporteOrdenPagoReintegros repo, Reintegro reintegro,
			HSSFCellStyle styleBold, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate) {
		
		BigDecimal importe = BigDecimal.ZERO;
		HSSFRow rowHeader = sheet.createRow(index);
				
		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(reintegro.getFecha());
		cell0.setCellStyle(styleDate);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(reintegro.getAfiliado()
				.getSeccional().getDescripcion()));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowHeader.createCell(2);
		if (!reintegro.getTipo_reintegro().equals(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			cell2.setCellValue(reintegro.getId_reintegro_user());
		} else {
			cell2.setCellValue(reintegro.getDetalleCuota().get(0)
					.getId_reintegro_user());
		}
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(repo.getAfiliado()
				.getCuil_titular()
				+ ", " + repo.getAfiliado().getInte()));
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowHeader.createCell(4);
		if (!reintegro.getTipo_reintegro().equals(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			cell4.setCellValue(reintegro.getImporteTotal().doubleValue());
			cell4.setCellStyle(styleAll);
			importe = reintegro.getImporteTotal();
		} else {
			cell4.setCellValue((reintegro.getDetalleCuota() != null && reintegro.getDetalleCuota().get(0) != null && reintegro.getDetalleCuota().get(0).getImporte() != null) ? 
					reintegro.getDetalleCuota().get(0).getImporte().doubleValue() : new Double(0).doubleValue());
			cell4.setCellStyle(styleAll);
			importe = (reintegro.getDetalleCuota() != null && reintegro.getDetalleCuota().get(0) != null && reintegro.getDetalleCuota().get(0).getImporte() != null) ? 
					reintegro.getDetalleCuota().get(0).getImporte() : BigDecimal.ZERO;
		}
		return importe;
	}
}
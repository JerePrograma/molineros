package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTercero;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleLiq;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleReint;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleReintOrtod;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.liquidaciones.services.LiquidacionDebitoTerceroServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteDebitosTercerosExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteDebitosTercerosExcel.class);

	public static HSSFWorkbook generaReporteDetalleDebitosATerceros(
			HttpServletRequest req, HttpServletResponse res) {

		int id_liquidacion = ParamUtil.getInteger(req, "id_liquidacion");
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(req, "periodo");
		Date periodo = null;
		try {
			periodo = formatoDePeriodos.parse("0"
					+ String.valueOf((Integer.parseInt(periodoMesAnio
							.substring(0, 1)) + 1)) + "/"
					+ periodoMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodo = null;
		}
		if (periodo == null) {
			try {
				periodo = formatoDePeriodos.parse(Integer
						.parseInt(periodoMesAnio.substring(0, 2))
						+ 1 + "/" + periodoMesAnio.substring(3, 7));
			} catch (Exception e) {
				periodo = null;
			}
		}
		if (periodo == null) {
			String periodoHidden = ParamUtil.getString(req, "periodoHidden");
			try {
				periodo = formatoDePeriodos.parse(periodoHidden);
			} catch (Exception e) {
				periodo = null;
			}
		}
		try {
			LiquidacionDebitoTercero liquidacionDebitos = null;
			if (id_liquidacion != 0) {
				liquidacionDebitos = LiquidacionDebitoTerceroServiceUtil
						.getLiquidacionesDebitosTerceros(id_liquidacion);
			} else {
				liquidacionDebitos = new LiquidacionDebitoTercero();
				liquidacionDebitos.setId_liquidacion(0);
				liquidacionDebitos.setPeriodoHasta(periodo);
				liquidacionDebitos
						.setDetalleLiquidacionDebitosTercerosReint(LiquidacionDebitoTerceroServiceUtil
								.getDetalleReintegrosPagosPeriodo(liquidacionDebitos
										.getPeriodoHasta()));
				liquidacionDebitos
						.setDetalleLiquidacionDebitosTercerosReintOrtod(LiquidacionDebitoTerceroServiceUtil
								.getDetalleReintegrosOrtPagosPeriodo(liquidacionDebitos
										.getPeriodoHasta()));
				liquidacionDebitos
						.setDetalleLiquidacionDebitosTercerosLiq(LiquidacionDebitoTerceroServiceUtil
								.getDetalleLiquidacionesPagasPeriodo(liquidacionDebitos
										.getPeriodoHasta()));
				liquidacionDebitos.generarImporteTotal();
			}
			return generarReporte(liquidacionDebitos);
		} catch (Exception e) {
			_log.error("Error al generar detalle de tercerizados", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(
			LiquidacionDebitoTercero liquidacionDebitosTerceros) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);
		
		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);

		cellTitulo.setCellValue(new HSSFRichTextString(
				"Detalle Liquidación Débitos a Terceros para el Periodo: "
						+ liquidacionDebitosTerceros.getPeriodoString()));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		int liquidacionesSize = liquidacionDebitosTerceros
				.getDetalleLiquidacionDebitosTercerosLiq().size();
		int reintegrosSize = liquidacionDebitosTerceros
				.getDetalleLiquidacionDebitosTercerosReint().size()
				+ liquidacionDebitosTerceros
						.getDetalleLiquidacionDebitosTercerosReintOrtod()
						.size();

		BigDecimal sumaImporte = liquidacionDebitosTerceros.getImporte_total();

		int i = 1;
		if (liquidacionesSize > 0) {
			createHeaderLiquidaciones(wb, sheet, styleHeader, i);
			i++;
		}
		for (LiquidacionDebitoTerceroDetalleLiq l : liquidacionDebitosTerceros
				.getDetalleLiquidacionDebitosTercerosLiq()) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString(l.getLiquidacion()
					.getId_liquidacionString()));
			cell.setCellStyle(styleAll);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(l.getComprobante()
					.toString()));
			cell1.setCellStyle(styleAll);

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(l.getLiquidacion().getFecha_recibido());
			cell2.setCellStyle(styleDate);

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(new HSSFRichTextString(l.getLiquidacion()
					.getPrestador_lugar_atencion().getPrestador().getCuit()));
			cell3.setCellStyle(styleAll);

			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(new HSSFRichTextString(l.getLiquidacion()
					.getPrestador_lugar_atencion().getPrestador()
					.getDescripcion()));
			cell4.setCellStyle(styleAll);

			HSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(l.getComprobanteConcepto().getImporte()
					.doubleValue());
			cell5.setCellStyle(styleMoney);

			HSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(new HSSFRichTextString(l.getOp()
					.getFechaAltaAsString()
					+ " " + l.getOp().getNumeroOP()));
			cell6.setCellStyle(styleAll);

			i++;
		}
		if (reintegrosSize > 0) {
			createHeaderReintegros(wb, sheet, styleHeader, i);
			i++;
		}
		for (LiquidacionDebitoTerceroDetalleReint l : liquidacionDebitosTerceros
				.getDetalleLiquidacionDebitosTercerosReint()) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString(l.getReintegroPrestacion()
					.getReintegro().getId_reintegro_userString()));
			cell.setCellStyle(styleAll);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(String.valueOf(l
					.getReintegroPrestacion().getReintegro().getSeccional()
					.getIdSeccional())));
			cell1.setCellStyle(styleAll);

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(l.getReintegroPrestacion().getReintegro()
					.getFecha());
			cell2.setCellStyle(styleDate);

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(new HSSFRichTextString(l
					.getReintegroPrestacion().getReintegro().getAfiliado()
					.getCuil()));
			cell3.setCellStyle(styleAll);

			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(new HSSFRichTextString(l
					.getReintegroPrestacion().getReintegro().getAfiliado()
					.getApeNombre()));
			cell4.setCellStyle(styleAll);

			HSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(l.getReintegroPrestacion().getImporte()
					.multiply(l.getReintegroPrestacion()
									.getCantidad()).doubleValue());
			cell5.setCellStyle(styleMoney);

			HSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(new HSSFRichTextString(l.getOp()
					.getFechaAltaAsString()
					+ " " + l.getOp().getNumeroOP()));
			cell6.setCellStyle(styleAll);

			i++;
		}

		for (LiquidacionDebitoTerceroDetalleReintOrtod l : liquidacionDebitosTerceros
				.getDetalleLiquidacionDebitosTercerosReintOrtod()) {
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString(l.getReintegroPrestacion()
					.getReintegro().getDetalleCuota().get(0)
					.getId_reintegro_userString()));
			cell.setCellStyle(styleAll);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(String.valueOf(l
					.getReintegroPrestacion().getReintegro().getSeccional()
					.getIdSeccional())));
			cell1.setCellStyle(styleAll);

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(l.getReintegroPrestacion().getReintegro()
					.getDetalleCuota().get(0).getFecha());
			cell2.setCellStyle(styleDate);

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(new HSSFRichTextString(l
					.getReintegroPrestacion().getReintegro().getAfiliado()
					.getCuil()));
			cell3.setCellStyle(styleAll);

			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(new HSSFRichTextString(l
					.getReintegroPrestacion().getReintegro().getAfiliado()
					.getApeNombre()));
			cell4.setCellStyle(styleAll);

			HSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(l.getReintegroPrestacion().getReintegro()
					.getDetalleCuota().get(0).getImporte().doubleValue());
			cell5.setCellStyle(styleMoney);

			HSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(new HSSFRichTextString(l.getOp()
					.getFechaAltaAsString()
					+ " " + l.getOp().getNumeroOP()));
			cell6.setCellStyle(styleAll);

			i++;
		}

		createFooter(wb, sheet, styleHeader, styleMoney, sumaImporte, i);

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		return wb;
	}

	private static void createHeaderLiquidaciones(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader, int iRow) {
		HSSFRow row = sheet.createRow(iRow);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Liquidación"));
		cell.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Comprobante"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Fecha Recepción"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Cuit Prestador"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Razón Social"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Importe"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("OP"));
		cell6.setCellStyle(styleHeader);
	}

	private static void createHeaderReintegros(HSSFWorkbook wb,
			HSSFSheet sheet, HSSFCellStyle styleHeader, int iRow) {
		HSSFRow row = sheet.createRow(iRow);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reintegro"));
		cell.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Seccional"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Fecha Reintegro"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Cuil Afiliado"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Apelido y Nombres"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Importe"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("OP"));
		cell6.setCellStyle(styleHeader);
	}

	private static void createFooter(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader, HSSFCellStyle styleMoney,
			BigDecimal sumaImporte, int i) {

		HSSFRow row = sheet.createRow(i);

		HSSFCell cellTitulo = row.createCell(0);

		cellTitulo.setCellValue(new HSSFRichTextString("Suma Importe: "));
		cellTitulo.setCellStyle(styleHeader);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 4));
		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(sumaImporte.doubleValue());
		cell5.setCellStyle(styleMoney);
	}
}

package ar.com.ospim.liquidaciones.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacion;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoOrtopediaOrtodoncia;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoProtesis;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteReintegros {
	private static Log _log = LogFactoryUtil.getLog(ReporteReintegros.class);

	public static HSSFWorkbook generaReporteReintegros(HttpServletRequest req,
			HttpServletResponse res) {
		int idReporte = ParamUtil.getInteger(req, "idReporte");
		try {
			String entidad = ParamUtil.getString(req, "entidad", null);
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
			Date fechaDesde = null;
			try {
				fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechaDesde = null;
			}
			String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
			Date fechaHasta = null;
			try {
				fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechaHasta = null;
			}
			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			String periodoDesdeMesAnio = ParamUtil.getString(req,
					"periodoDesdeMesAnio");
			Date periodoDesde = null;
			try {
				periodoDesde = formatoDePeriodos.parse(Integer
						.parseInt(periodoDesdeMesAnio.substring(0, 1))
						+ 1 + "/" + periodoDesdeMesAnio.substring(2, 6));
			} catch (Exception e) {
				periodoDesde = null;
			}
			String periodoHastaMesAnio = ParamUtil.getString(req,
					"periodoHastaMesAnio");
			Date periodoHasta = null;
			try {
				periodoHasta = formatoDePeriodos.parse(Integer
						.parseInt(periodoHastaMesAnio.substring(0, 1))
						+ 1 + "/" + periodoHastaMesAnio.substring(2, 6));
			} catch (Exception e) {
				periodoHasta = null;
			}

			int seccional = ParamUtil.getInteger(req, "id_seccional_r", 0);
			int numero = ParamUtil.getInteger(req, "numero", 0);

			String tipo_reintegro = ParamUtil.getString(req, "tipo_reintegro",
					WebKeysLiquidaciones.REINTEGRO_PRE);

			int inte = ParamUtil.getInteger(req, "inte", 0);
			int nroAfi = ParamUtil.getInteger(req, "numero_afi", 0);
			String cuil_titular = ParamUtil.getString(req, "cuil_titular");

			int estado = ParamUtil.getInteger(req, "estado", 0);

			String pagos = ParamUtil.getString(req, "pagos", "0");

			String alta_usr = ParamUtil.getString(req, "alta_usr", "");
			String codPrest = ParamUtil.getString(req, "codPrest", null);
			List<Reintegro> busqueda = null;
			if (tipo_reintegro
					.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {

				busqueda = ReintegroServiceUtil.buscarReintegros(entidad,
						fechaDesde, fechaHasta, periodoDesde, periodoHasta,
						codPrest, nroAfi, inte, cuil_titular, seccional,
						numero, pagos, alta_usr);
				if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
					seccional = busqueda.get(0).getId_seccional();
				}
			} else if (tipo_reintegro
					.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
				codPrest = null;
				busqueda = ReintegroServiceUtil.buscarReintegrosOdoProtesis(
						entidad, fechaDesde, fechaHasta, periodoDesde,
						periodoHasta, codPrest, nroAfi, inte, cuil_titular,
						seccional, numero, pagos, alta_usr, estado);
				if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
					seccional = busqueda.get(0).getId_seccional();
				}
			} else if (tipo_reintegro
					.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
				codPrest = null;
				busqueda = ReintegroServiceUtil.buscarReintegrosOdoOrto(
						entidad, fechaDesde, fechaHasta, periodoDesde,
						periodoHasta, codPrest, nroAfi, inte, cuil_titular,
						seccional, numero, pagos, alta_usr, estado);
				if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
					seccional = busqueda.get(0).getId_seccional();
				}
			} else {
				// List<ReintegroPrestacion>
				busqueda = new ArrayList<Reintegro>();
			}
		} catch (Exception e) {
			_log.error(e);
		}

		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			List<ReporteOrdenPagoReintegros> list = null;
			if (idReporte != 0) {
				HSSFSheet sheet = wb.createSheet("Hoja 1");
				try {
					list = ReintegroActionUtil
							.getReintegrosFromReporteId(idReporte);

				} catch (NoSuchReintegroEntryException nsree) {
					list = null;
				}

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
				cell1H.setCellStyle(styleAll);

				HSSFCell cell2H = rowHeader.createCell(2);
				cell2H.setCellValue(new HSSFRichTextString("Seccional"));
				cell2H.setCellStyle(styleAll);

				HSSFCell cell3H = rowHeader.createCell(3);
				cell3H.setCellValue(new HSSFRichTextString("Cuil Titular"));
				cell3H.setCellStyle(styleAll);

				HSSFCell cell4H = rowHeader.createCell(4);
				cell4H.setCellValue(new HSSFRichTextString("Inte"));
				cell4H.setCellStyle(styleAll);

				sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 2));

				HSSFCell cell5H = rowHeader.createCell(5);
				cell5H.setCellValue(new HSSFRichTextString("N° Importe"));
				// cell5H.setCellValue(list.get(0).getReintegro().getFecha());
				cell5H.setCellStyle(styleDate);

				HSSFCell cell6H = rowHeader.createCell(6);
				cell6H.setCellValue(new HSSFRichTextString(
						"OP (Número/Cheque/Fecha)"));
				cell6H.setCellStyle(styleAll);

				index++;
				sheet.createRow(index);

				BigDecimal total = new BigDecimal("0");
				for (ReporteOrdenPagoReintegros repo : list) {
					index++;
					crearHeader(sheet, index, repo, repo.getReintegro(),
							styleBold, styleAll, styleDate);
					index++;
					crearHeaderPrestacion(sheet, index, styleBold);
					for (ReintegroPrestacion rp : repo.getReintegro()
							.getReintegroPrestacion()) {
						index++;
						crearFilaInfoPrestacion(sheet, index, rp, styleAll,
								styleDate);
					}
					index++;
					HSSFRow rowSubtotal = sheet.createRow(index);
					HSSFCell cellSubtotalTexo = rowSubtotal.createCell(4);
					cellSubtotalTexo.setCellValue(new HSSFRichTextString(
							"Subtotal"));
					cellSubtotalTexo.setCellStyle(styleBold);

					HSSFCell cellSubtotalValor = rowSubtotal.createCell(5);
					cellSubtotalValor.setCellValue(repo.getReintegro()
							.getImporteTotal().doubleValue());
					cellSubtotalValor.setCellStyle(styleAll);

					index++;
					sheet.createRow(index);
					total = total.add(repo.getTotal());
				}
				index++;
				sheet.createRow(index);
				index++;
				HSSFRow rowTotal = sheet.createRow(index);

				HSSFCell cell = rowTotal.createCell(4);
				cell.setCellValue(new HSSFRichTextString("Total"));
				cell.setCellStyle(styleBold);

				HSSFCell cell1 = rowTotal.createCell(5);
				cell1.setCellValue(total.doubleValue());
				cell1.setCellStyle(styleAll);

				index++;
				sheet.createRow(index);
				index++;

				sheet.autoSizeColumn((short) 0);
				sheet.autoSizeColumn((short) 1);
				sheet.autoSizeColumn((short) 2);
				sheet.autoSizeColumn((short) 3);
				sheet.autoSizeColumn((short) 4);
				sheet.autoSizeColumn((short) 5);
			}
		} catch (SystemException e) {
			_log.error("Error al generar reporte", e);
		}
		return wb;
	}

	private static HSSFCellStyle getStyleAll(HSSFWorkbook wb) {
		HSSFCellStyle styleAll = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleAll.setFont(font);
		return styleAll;
	}

	private static HSSFCellStyle getStyleBold(HSSFWorkbook wb) {
		HSSFCellStyle styleBold = wb.createCellStyle();
		HSSFFont fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 8);
		fontBold.setBold(true);
		styleBold.setFont(fontBold);
		return styleBold;
	}

	private static HSSFCellStyle getStyleDate(HSSFWorkbook wb) {
		HSSFCellStyle styleDate = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		styleDate.setFont(font);
		styleDate.setDataFormat(wb.createDataFormat().getFormat("dd/MM/yyyy"));

		return styleDate;
	}

	private static void crearHeaderPrestacion(HSSFSheet sheet, int index,
			HSSFCellStyle styleBold) {
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell = rowHeader.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Realizado"));
		cell.setCellStyle(styleBold);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(
				"Prestacion                   "));
		cell1.setCellStyle(styleBold);

		HSSFCell cell2 = rowHeader.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Codigo NN"));
		cell2.setCellStyle(styleBold);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Cant"));
		cell3.setCellStyle(styleBold);

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("M. Unit"));
		cell4.setCellStyle(styleBold);

		HSSFCell cell5 = rowHeader.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("M. Total"));
		cell5.setCellStyle(styleBold);
	}

	private static void crearFilaInfoPrestacion(HSSFSheet sheet, int index,
			ReintegroPrestacion rp, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate) {
		HSSFRow row = sheet.createRow(index);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(rp.getFecha_prestacion());
		cell.setCellStyle(styleDate);

		HSSFCell cell1 = row.createCell(1);
		String desc = rp.getPlan_prestacion().getNomenclador().getDescripcion();
		cell1.setCellValue(new HSSFRichTextString(desc.length() < 35 ? desc
				: desc.substring(0, 35)));
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(rp.getCodigo()));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = row.createCell(3);
		if (rp instanceof ReintegroPrestacionNormal) {
			cell3.setCellValue(((ReintegroPrestacionNormal) rp).getCantidad().doubleValue());
		} else if (rp instanceof ReintegroPrestacionOdoProtesis) {
			cell3.setCellValue(((ReintegroPrestacionOdoProtesis) rp)
					.getCantidad().doubleValue());
		} else if (rp instanceof ReintegroPrestacionOdoOrtopediaOrtodoncia) {
			cell3.setCellValue(1);
		}
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(rp.getImporte().doubleValue());
		cell4.setCellStyle(styleAll);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(rp.getImporteTotal().doubleValue());
		cell5.setCellStyle(styleAll);
	}

	private static void crearHeader(HSSFSheet sheet, int index,
			ReporteOrdenPagoReintegros repo, Reintegro reintegro,
			HSSFCellStyle styleBold, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate) {
		HSSFRow rowHeader = sheet.createRow(index);

		HSSFCell cell0 = rowHeader.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleBold);

		HSSFCell cell1 = rowHeader.createCell(1);
		cell1.setCellValue(reintegro.getFecha());
		cell1.setCellStyle(styleDate);

		HSSFCell cell2 = rowHeader.createCell(2);
		if (!reintegro.getTipo_reintegro().equals(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			cell2.setCellValue(new HSSFRichTextString("Reintegro N° "
					+ reintegro.getId_reintegro_user()));
		} else {
			cell2
					.setCellValue(new HSSFRichTextString("Reintegro N° "
							+ reintegro.getDetalleCuota().get(0)
									.getId_reintegro_user()));
		}
		cell2.setCellStyle(styleBold);

		HSSFCell cell3 = rowHeader.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Afiliado"));
		cell3.setCellStyle(styleBold);

		int id = 0;
		if (repo.getReintegro().getEntidad().equals("A.M.T.I.M.A.")) {
			id = repo.getAfiliado().getId_amtima();
		} else if (repo.getReintegro().getEntidad().equals("U.O.M.A.")) {
			id = repo.getAfiliado().getId_uoma();
		}
		if (repo.getReintegro().getEntidad().equals("O.S.P.I.M.")) {
			id = repo.getAfiliado().getId_ospim();
		}

		HSSFCell cell4 = rowHeader.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(String.valueOf(id) + " - "
				+ repo.getAfiliado().getApeNombre()));
		cell4.setCellStyle(styleAll);

		sheet.addMergedRegion(new CellRangeAddress(index, // first row (0-based)
				index, // last row (0-based)
				4, // first column (0-based)
				6 // last column (0-based)
				));

	}

}

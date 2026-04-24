package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteContabilidadDiario extends ReporteXLS {

	private static Log _log = LogFactoryUtil
			.getLog(ReporteContabilidadDiario.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
			
			int entidad=ParamUtil.getInteger(req,"entidad");
					
			
			Calendar desdeC = DateUtils.getDesdePeriodo(req,entidad);
			Calendar hastaC = DateUtils.getHastaPeriodo(req, entidad);

			String nrosDD = req.getParameter("nro_asiento_desde");
			String nrosHta = req.getParameter("nro_asiento_hasta");
			boolean incluirAutomaticos = ParamUtil.getBoolean(req,
					"incluir_automaticos");
			boolean incluirManuales = ParamUtil.getBoolean(req,
					"incluir_manuales");
			boolean incluirDetalle = ParamUtil.getBoolean(req,
					"incluir_detalle");

			Integer desde = null;
			Integer hasta = null;
			try {
				desde = Integer.valueOf(nrosDD);
			} catch (Exception e) {
			}
			try {
				hasta = Integer.valueOf(nrosHta);
			} catch (Exception e) {
			}

			List<Asiento> asientos = AsientoServiceUtil
					.buscarAsientosConDetalle(desdeC.getTime(),
							hastaC.getTime(), desde, hasta, incluirAutomaticos,
							incluirManuales, entidad);
			return generarReporte(desdeC.getTime(), hastaC.getTime(), asientos,
					incluirDetalle, entidad);
		} catch (Exception e) {
			_log.error("Error al generar diario", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<Asiento> reporte, boolean incluirDetalle, int entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllBorder = getStyleAllWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);
		HSSFCellStyle styleNumber = getStyleNumber(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 1.3);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.LEGAL_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(false);

		int i = crearHeaderPrincipal(wb, sheet, 6, entidad);

		if (reporte == null || reporte.size() == 0) {
			return wb;
		}

		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
		cell.setCellValue(new HSSFRichTextString("Libro Diario. Ejercicio: "
				+ format.format(fechaIni) + ". " + formatFecha.format(fechaIni)
				+ " al " + formatFecha.format(fechaFin) + ". Asiento: "
				+ reporte.get(0).getNro() + " al "
				+ reporte.get(reporte.size() - 1).getNro()));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
		i += 2;

		for (Asiento repo : reporte) {
			i = generarDatos(repo, i, styleAll, styleDate, styleMoney,
					styleNumber, sheet, styleAllBorder, styleMoneyBold,
					incluirDetalle);
		}

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		return wb;
	}

	public static int generarDatos(Asiento repo, int i, HSSFCellStyle styleAll,
			HSSFCellStyle styleDate, HSSFCellStyle styleMoney,
			HSSFCellStyle styleNumber, HSSFSheet sheet,
			HSSFCellStyle styleAllBorder, HSSFCellStyle styleMoneyBold,
			boolean incluirDetalle) {
		i++;
		i = crearHeaderAsiento(repo, sheet, i, styleAll);
		i++;
		i = crearHeaderDetalle(sheet, i, styleAllBorder);
		BigDecimal debe = BigDecimal.ZERO;
		BigDecimal haber = BigDecimal.ZERO;
		if (repo.getDetalle() != null) {
			for (Detalle det : repo.getDetalle()) {
				if (incluirDetalle) {
					HSSFRow row = sheet.createRow(i);
					HSSFCell cell0 = row.createCell(0);
					cell0.setCellValue(det.getPase());
					cell0.setCellStyle(styleAllBorder);

					HSSFCell cell1 = row.createCell(1);
					cell1.setCellValue(new HSSFRichTextString(det.getCuenta()
							.getNumero()));
					cell1.setCellStyle(styleAllBorder);

					HSSFCell cell2 = row.createCell(2);
					cell2.setCellValue(new HSSFRichTextString(det.getCuenta()
							.getCuenta()));
					cell2.setCellStyle(styleAllBorder);

					HSSFCell cell3 = row.createCell(3);
					cell3.setCellValue(new HSSFRichTextString(det
							.getComprobante()));
					cell3.setCellStyle(styleAllBorder);

					HSSFCell cell4 = row.createCell(4);
					cell4.setCellValue(new HSSFRichTextString(det
							.getObservaciones()));
					cell4.setCellStyle(styleAllBorder);

					HSSFCell cell5 = row.createCell(5);
					cell5.setCellValue(det.getDebe().doubleValue());
					cell5.setCellStyle(styleMoney);

					HSSFCell cell6 = row.createCell(6);
					cell6.setCellValue(det.getHaber().doubleValue());
					cell6.setCellStyle(styleMoney);
					i++;
				}
				debe = debe.add(det.getDebe());
				haber = haber.add(det.getHaber());
			}
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellTot = row.createCell(4);
		cellTot.setCellValue(new HSSFRichTextString("Totales"));
		cellTot.setCellStyle(styleAll);

		HSSFCell cellTotDebe = row.createCell(5);
		cellTotDebe.setCellValue(debe.doubleValue());
		cellTotDebe.setCellStyle(styleMoneyBold);

		HSSFCell cellTotHaber = row.createCell(6);
		cellTotHaber.setCellValue(haber.doubleValue());
		cellTotHaber.setCellStyle(styleMoneyBold);
		i++;
		return i;
	}

	public static int crearHeaderDetalle(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Pase"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Cuenta"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("Descripción Cuenta"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Comprobante"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Observaciones"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Debe"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(new HSSFRichTextString("Haber"));
		cell6.setCellStyle(styleHeader);

		return ++i;
	}

	public static int crearHeaderAsiento(Asiento asiento, HSSFSheet sheet,
			int i, HSSFCellStyle styleAll) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
		cell0.setCellValue(new HSSFRichTextString("Periodo: "
				+ format.format(asiento.getFecha())));
		cell0.setCellStyle(styleAll);

		SimpleDateFormat format2 = new SimpleDateFormat("dd");
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Dia: "
				+ format2.format(asiento.getFecha())));
		cell1.setCellStyle(styleAll);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("Asiento: "
				+ asiento.getNro()));
		cellAcreed.setCellStyle(styleAll);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Obs: "
				+ asiento.getDescripcion()));
		cellRaz.setCellStyle(styleAll);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 3, 6));

		return ++i;
	}
}

package ar.com.ospim.portalempleadores.reportes;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.webservice.WSClient;

import com.google.gson.reflect.TypeToken;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteCuentaCorrientePortalEmpleadoresExcel extends ReporteXLS {

	private static Log _log = LogFactoryUtil
			.getLog(ReporteCuentaCorrientePortalEmpleadoresExcel.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");

		String fechaPagoHastaDia = ParamUtil
				.getString(req, "fechaPagoHastaDia");
		String fechaPagoHastaMes = ParamUtil
				.getString(req, "fechaPagoHastaMes");
		fechaPagoHastaMes = String
				.valueOf(Integer.valueOf(fechaPagoHastaMes) + 1);
		String fechaPagoHastaAnio = ParamUtil.getString(req,
				"fechaPagoHastaAnio");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);

		if (seccional != 0) {
			sucu = "000";
		}

		boolean aporte_solidario_uoma = ParamUtil.getBoolean(req,
				"aporte_solidario_uoma");
		boolean cuota_social_uoma = ParamUtil.getBoolean(req,
				"cuota_social_uoma");
		boolean art46 = ParamUtil.getBoolean(req, "art46");
		boolean cuota_usufructo = ParamUtil.getBoolean(req, "cuota_usufructo");
		boolean cuota_amtima = ParamUtil.getBoolean(req, "cuota_amtima");

		boolean amtima = ParamUtil.getBoolean(req, "amtima");
		boolean uoma = ParamUtil.getBoolean(req, "uoma");
		boolean ospim = ParamUtil.getBoolean(req, "ospim");

		try {
			String desde = fechaDesdeDia + "/" + fechaDesdeMes + "/"
					+ fechaDesdeAnio;
			String hasta = fechaHastaDia + "/" + fechaHastaMes + "/"
					+ fechaHastaAnio;
			String pagoHasta = fechaPagoHastaDia + "/" + fechaPagoHastaMes
					+ "/" + fechaPagoHastaAnio;

			String token = "875283adde581de2b4fbb7ec78f207b1";
			String url = "/web/ws/cuenta_corriente?fechaDesde="
					+ URLEncoder.encode(desde) + "&fechaHasta="
					+ URLEncoder.encode(hasta) + "&fechaPagoHasta="
					+ URLEncoder.encode(pagoHasta) + "&token=" + token
					+ (StringUtils.isNotBlank(cuit) ? "&cuit=" + cuit : "")
					+ "&aporte_solidario_uoma=" + aporte_solidario_uoma
					+ "&cuota_social_uoma=" + cuota_social_uoma + "&art46="
					+ art46 + "&cuota_usufructo=" + cuota_usufructo
					+ "&cuota_amtima=" + cuota_amtima + "&amtima=" + amtima
					+ "&uoma=" + uoma + "&ospim=" + ospim;

			List<CuentaCorrientePortalEmpleadores> result = WSClient
					.getHttpsListResult(
							"www.uomaempleadores.org.ar",
							url,
							new TypeToken<Collection<CuentaCorrientePortalEmpleadores>>() {
							});

			Collections.sort(result);
			return generarReporte(format.parse(desde), format.parse(hasta),
					format.parse(pagoHasta), cuit, result);
		} catch (Exception e) {
			_log.error("Error al generar cta cte portal empleadores", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			Date fechaPagoFin, String cuit,
			List<CuentaCorrientePortalEmpleadores> reporte) {
		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFCellStyle styleHeader = getStyleHeader(wb);
		HSSFCellStyle styleDate = getStyleDate(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleMoney = getStyleMoney(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);

		int i = 0;
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("DDJJ y Pagos"));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 8));
		i++;

		HSSFRow rowTitulo2 = sheet.createRow(i);
		HSSFCell cell2 = rowTitulo2.createCell(0);
		cell2.setCellValue(new HSSFRichTextString("Desde "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " al "
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell2.setCellStyle(getStyleAllCenter(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 8));
		i++;

		i = getHeaders(sheet, i, styleHeader);

		generarDetalle(reporte, styleDate, styleAll, styleMoney, sheet, i);

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		return wb;
	}

	private static void generarDetalle(
			List<CuentaCorrientePortalEmpleadores> reporte,
			HSSFCellStyle styleDate, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoney, HSSFSheet sheet, int i) {

		BigDecimal saldo = BigDecimal.ZERO;
		String cuitAux = null;

		for (int index = 0; index < reporte.size(); index++) {
			CuentaCorrientePortalEmpleadores cta = reporte.get(index);
			boolean mostrarSaldo = false;
			if (cuitAux == null || !cuitAux.equals(cta.getCuit())) {
				if (cuitAux != null) {
					i++;
				}
				cuitAux = cta.getCuit();
			}
			if (index == reporte.size() - 1
					|| !cta.getTipo().equals(reporte.get(index + 1).getTipo())
					|| !cta.getCuit().equals(reporte.get(index + 1).getCuit())
					|| !cta.getPeriodo().equals(
							reporte.get(index + 1).getPeriodo())) {
				mostrarSaldo = true;
			}
			HSSFRow row = sheet.createRow(i);

			HSSFCell cellCuit = row.createCell(0);
			cellCuit.setCellValue(new HSSFRichTextString(cta.getCuit()));
			cellCuit.setCellStyle(styleAll);

			HSSFCell cellRazon = row.createCell(1);
			cellRazon.setCellValue(new HSSFRichTextString(cta.getRazon_soc()));
			cellRazon.setCellStyle(styleAll);

			HSSFCell cellPeriodo = row.createCell(2);
			cellPeriodo.setCellValue(cta.getPeriodo());
			cellPeriodo.setCellStyle(styleDate);

			HSSFCell cellFecha = row.createCell(3);
			cellFecha.setCellValue(cta.getFecha());
			cellFecha.setCellStyle(styleDate);

			HSSFCell cellTipo = row.createCell(4);
			cellTipo.setCellValue(new HSSFRichTextString(cta.getTipo()));
			cellTipo.setCellStyle(styleAll);

			HSSFCell cellDesc = row.createCell(5);
			cellDesc.setCellValue(new HSSFRichTextString(cta.getDescripcion()));
			cellDesc.setCellStyle(styleAll);

			HSSFCell cellDebe = row.createCell(6);
			cellDebe.setCellValue(cta.getDebe().doubleValue());
			cellDebe.setCellStyle(styleMoney);

			HSSFCell cellHaber = row.createCell(7);
			cellHaber.setCellValue(cta.getHaber().doubleValue());
			cellHaber.setCellStyle(styleMoney);

			saldo = saldo.add(cta.getDebe()).subtract(cta.getHaber());
			if (mostrarSaldo) {
				HSSFCell cellSaldo = row.createCell(8);
				cellSaldo.setCellValue(saldo.doubleValue());
				cellSaldo.setCellStyle(styleMoney);
				saldo = BigDecimal.ZERO;
			}
			++i;
		}
	}

	private static int getHeaders(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader) {

		HSSFRow row = sheet.createRow(i);

		HSSFCell cellCuit = row.createCell(0);
		cellCuit.setCellValue(new HSSFRichTextString("Cuit"));
		cellCuit.setCellStyle(styleHeader);

		HSSFCell cellRazon = row.createCell(1);
		cellRazon.setCellValue(new HSSFRichTextString("Razon Social"));
		cellRazon.setCellStyle(styleHeader);

		HSSFCell cellPeriodo = row.createCell(2);
		cellPeriodo.setCellValue(new HSSFRichTextString("Periodo"));
		cellPeriodo.setCellStyle(styleHeader);

		HSSFCell cellFecha = row.createCell(3);
		cellFecha.setCellValue(new HSSFRichTextString("Fecha"));
		cellFecha.setCellStyle(styleHeader);

		HSSFCell cellTipo = row.createCell(4);
		cellTipo.setCellValue(new HSSFRichTextString("Tipo"));
		cellTipo.setCellStyle(styleHeader);

		HSSFCell cellDesc = row.createCell(5);
		cellDesc.setCellValue(new HSSFRichTextString("Descripcion"));
		cellDesc.setCellStyle(styleHeader);

		HSSFCell cellDebe = row.createCell(6);
		cellDebe.setCellValue(new HSSFRichTextString("Debe"));
		cellDebe.setCellStyle(styleHeader);

		HSSFCell cellHaber = row.createCell(7);
		cellHaber.setCellValue(new HSSFRichTextString("Haber"));
		cellHaber.setCellStyle(styleHeader);

		HSSFCell cellSaldo = row.createCell(8);
		cellSaldo.setCellValue(new HSSFRichTextString("Saldo"));
		cellSaldo.setCellStyle(styleHeader);

		return ++i;
	}

}

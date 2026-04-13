package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.compass.core.util.backport.java.util.Collections;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.tesoreria.beans.BalanceSumasYSaldos;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteContabilidadMayorGeneralExcel extends ReporteConabilidad {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteContabilidadMayorGeneralExcel.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
						
			int entidad=ParamUtil.getInteger(req, "entidad");
						
			Calendar desdeC = DateUtils.getDesdePeriodo(req, entidad);
			Calendar hastaC = DateUtils.getHastaPeriodo(req, entidad);

			String cuentas = req.getParameter("cuentas");

			boolean incluirAutomaticos = ParamUtil.getBoolean(req,
					"incluir_automaticos");
			boolean incluirManuales = ParamUtil.getBoolean(req,
					"incluir_manuales");
			boolean incluir_asiento_inicial = ParamUtil.getBoolean(req,
					"incluir_asiento_inicial");
			// boolean incluir_asiento_final = ParamUtil.getBoolean(req,
			// "incluir_asiento_final");
			boolean incluir_saldo_inicial = ParamUtil.getBoolean(req,
					"incluir_saldo_inicial");

			List<Asiento> asientos = AsientoServiceUtil.buscarDetalleAsientos(
					desdeC.getTime(), hastaC.getTime(), null, null,
					incluirAutomaticos, incluirManuales, entidad);

			Calendar desdeEjercicio = DateUtils.getDesdeEjercicio(req, entidad);
			Calendar hastaEjercicio = DateUtils.getHastaEjercicio(req, entidad);

			List<BalanceSumasYSaldos> saldosIniciales = null;
			if (incluir_saldo_inicial) {
				saldosIniciales = getSaldoInicial(desdeC, desdeEjercicio,
						hastaEjercicio, incluirAutomaticos, incluirManuales,
						incluir_asiento_inicial, entidad);
			}

			Map<PlanCuentas, List<Asiento>> reporte = armarReporte(asientos,
					saldosIniciales);

			Set<String> filtroCuentas = new HashSet<String>();
			if (StringUtils.isNotBlank(cuentas) && !cuentas.equals("null")) {
				String numerosCuentas[] = cuentas.split(",");
				for (String nro : numerosCuentas) {
					filtroCuentas.add(nro.trim());
				}
			}
			return generarReporte(desdeC.getTime(), hastaC.getTime(), reporte,
					saldosIniciales, filtroCuentas, entidad);
		} catch (Exception e) {
			_log.error("Error al generar diario", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			Map<PlanCuentas, List<Asiento>> reporte,
			List<BalanceSumasYSaldos> saldosIniciales, Set<String> filtroCuentas, int entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllBorder = getStyleAllWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleDate = getStyleDateWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);
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
		ps.setLandscape(false);

		int i = crearHeaderPrincipal(wb, sheet, 6, entidad);

		if (reporte == null || reporte.size() == 0) {
			return wb;
		}

		List<PlanCuentas> pc = new ArrayList<PlanCuentas>();
		pc.addAll(reporte.keySet());
		Collections.sort(pc);

		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
		cell.setCellValue(new HSSFRichTextString("Mayor General. Ejercicio: "
				+ format.format(fechaIni) + ". " + formatFecha.format(fechaIni)
				+ " al " + formatFecha.format(fechaFin) + ". Cuentas: "
				+ pc.get(0).getNumero() + " al "
				+ pc.get(pc.size() - 1).getNumero()));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
		i += 2;

		for (PlanCuentas plan : pc) {
			if (filtroCuentas.size() > 0
					&& !filtroCuentas.contains(plan.getNumero())) {
				continue;
			}
			BigDecimal debeAnterior = BigDecimal.ZERO;
			BigDecimal haberAnterior = BigDecimal.ZERO;
			if (saldosIniciales != null) {
				int indexOf = saldosIniciales.indexOf(new BalanceSumasYSaldos(
						plan));
				if (indexOf != -1) {
					BalanceSumasYSaldos saldoAnterior = saldosIniciales
							.get(indexOf);
					debeAnterior = saldoAnterior.getDebe();
					haberAnterior = saldoAnterior.getHaber();
				}
			}
			i = generarDatos(plan, reporte.get(plan), i, styleAll, styleDate,
					styleMoney, styleNumber, sheet, styleAllBorder,
					styleMoneyBold, debeAnterior, haberAnterior);
			++i;
		}

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		return wb;
	}

	private static int generarDatos(PlanCuentas pc, List<Asiento> asientos,
			int i, HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNumber,
			HSSFSheet sheet, HSSFCellStyle styleAllBorder,
			HSSFCellStyle styleMoneyBold, BigDecimal anteriorDebe,
			BigDecimal anteriorHaber) {

		Collections.sort(asientos, new Comparator<Asiento>() {

			public int compare(Asiento asiento0, Asiento asiento1) {
				if (asiento0.getNro() < asiento1.getNro()) {
					return -1;
				}
				if (asiento0.getNro() > asiento1.getNro()) {
					return 1;
				}
				int pase0 = asiento0.getDetalle().get(0).getPase();
				int pase1 = asiento1.getDetalle().get(0).getPase();
				if (pase0 == pase1) {
					return 0;
				} else if (pase0 < pase1) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		i++;
		i = crearHeaderDetalle(sheet, i, styleAllBorder);
		i = crearHeaderResumenAnterior(pc, sheet, i, styleAllBorder,
				styleMoney, anteriorDebe, anteriorHaber);

		BigDecimal debe = BigDecimal.ZERO;
		BigDecimal haber = BigDecimal.ZERO;
		BigDecimal saldo = BigDecimal.ZERO;
		if (asientos != null) {
			for (Asiento asiento : asientos) {
				HSSFRow row = sheet.createRow(i);

				Detalle det = asiento.getDetalle().get(0);

				HSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(asiento.getFecha());
				cell0.setCellStyle(styleDate);

				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(asiento.getNro());
				cell1.setCellStyle(styleAllBorder);

				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue(det.getPase());
				cell2.setCellStyle(styleAllBorder);

				HSSFCell cell3 = row.createCell(3);
				cell3.setCellValue(new HSSFRichTextString(det.getComprobante()));
				cell3.setCellStyle(styleAllBorder);

				HSSFCell cell4 = row.createCell(4);
				cell4.setCellValue(new HSSFRichTextString(det
						.getObservaciones()));
				cell4.setCellStyle(styleAllBorder);

				HSSFCell cell5 = row.createCell(5);
				cell5.setCellValue(det.getDebe().doubleValue());
				cell5.setCellStyle(styleMoney);
				debe = debe.add(det.getDebe());

				HSSFCell cell6 = row.createCell(6);
				cell6.setCellValue(det.getHaber().doubleValue());
				cell6.setCellStyle(styleMoney);
				haber = haber.add(det.getHaber());

				saldo = saldo.add(det.getDebe()).subtract(det.getHaber());
				HSSFCell cell7 = row.createCell(7);
				cell7.setCellValue(saldo.doubleValue());
				cell7.setCellStyle(styleMoney);

				i++;
			}
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellTot = row.createCell(4);
		cellTot.setCellValue(new HSSFRichTextString("Total del período"));
		cellTot.setCellStyle(styleAll);

		HSSFCell cellTotDebe = row.createCell(5);
		cellTotDebe.setCellValue(debe.doubleValue());
		cellTotDebe.setCellStyle(styleMoneyBold);

		HSSFCell cellTotHaber = row.createCell(6);
		cellTotHaber.setCellValue(haber.doubleValue());
		cellTotHaber.setCellStyle(styleMoneyBold);

		HSSFCell cellTotSaldo = row.createCell(7);
		cellTotSaldo.setCellValue(saldo.doubleValue());
		cellTotSaldo.setCellStyle(styleMoneyBold);
		i++;

		HSSFRow rowAcum = sheet.createRow(i);
		HSSFCell cellTotAcum = rowAcum.createCell(4);
		cellTotAcum.setCellValue(new HSSFRichTextString("Total acumulado"));
		cellTotAcum.setCellStyle(styleAll);

		HSSFCell cellTotDebeAcum = rowAcum.createCell(5);
		BigDecimal totalDebeAcum = anteriorDebe.add(debe);
		cellTotDebeAcum.setCellValue(totalDebeAcum.doubleValue());
		cellTotDebeAcum.setCellStyle(styleMoneyBold);

		HSSFCell cellTotHaberAcum = rowAcum.createCell(6);
		BigDecimal totalHaberAcum = anteriorHaber.add(haber);
		cellTotHaberAcum.setCellValue(totalHaberAcum.doubleValue());
		cellTotHaberAcum.setCellStyle(styleMoneyBold);

		HSSFCell cellTotSaldoAcum = rowAcum.createCell(7);
		cellTotSaldoAcum.setCellValue(totalDebeAcum.subtract(totalHaberAcum)
				.doubleValue());
		cellTotSaldoAcum.setCellStyle(styleMoneyBold);
		i++;
		return i;
	}

	private static Map<PlanCuentas, List<Asiento>> armarReporte(
			List<Asiento> asientos, List<BalanceSumasYSaldos> saldosIniciales) {

		Map<PlanCuentas, List<Asiento>> reporte = new HashMap<PlanCuentas, List<Asiento>>();
		if (asientos == null) {
			return reporte;
		}

		for (Asiento asiento : asientos) {
			// para este reporte cada objeto asiento tiene solo 1 detalle, o
			// sea un asiento con n detalles viene en forma de n asientos con 1
			// detalle cada uno
			if (asiento.getDetalle() == null
					|| asiento.getDetalle().size() != 1
					|| asiento.getNro() == 1) {
				continue;
			}
			Detalle detalle = asiento.getDetalle().get(0);
			if (reporte.containsKey(detalle.getCuenta())) {
				reporte.get(detalle.getCuenta()).add(asiento);
			} else {
				List<Asiento> listaAsientos = new ArrayList<Asiento>();
				listaAsientos.add(asiento);
				reporte.put(detalle.getCuenta(), listaAsientos);
			}
		}

		// agrego todas las cuentas para las que exista un saldo
		// inicial/anterior pero que no existan asientos para el periodo dado
		Set<PlanCuentas> cuentasUtilizadas = reporte.keySet();
		if (saldosIniciales != null) {
			for (BalanceSumasYSaldos saldos : saldosIniciales) {
				PlanCuentas planCuentasSaldoInicial = new PlanCuentas(
						saldos.getNumeroCuenta(), "");
				if (!cuentasUtilizadas.contains(planCuentasSaldoInicial)) {
					reporte.put(planCuentasSaldoInicial,
							new ArrayList<Asiento>());
				}
			}
		}
		return reporte;
	}

	public static int crearHeaderDetalle(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Fecha"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Asiento"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("Pase"));
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

		HSSFCell cell7 = row.createCell(7);
		cell7.setCellValue(new HSSFRichTextString("Saldo"));
		cell7.setCellStyle(styleHeader);

		return ++i;
	}

	private static int crearHeaderResumenAnterior(PlanCuentas pc,
			HSSFSheet sheet, int i, HSSFCellStyle styleHeader,
			HSSFCellStyle styleMoney, BigDecimal anteriorDebe,
			BigDecimal anteriorHaber) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Cuenta:"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(pc.getNumero()));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString(pc.getCuenta()));
		cellAcreed.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 3));

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Anterior:"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(anteriorDebe.doubleValue());
		cell5.setCellStyle(styleMoney);

		HSSFCell cell6 = row.createCell(6);
		cell6.setCellValue(anteriorHaber.doubleValue());
		cell6.setCellStyle(styleMoney);

		HSSFCell cell7 = row.createCell(7);
		// cell7.setCellValue(anteriorHaber.subtract(anteriorDebe).doubleValue());
		// cell7.setCellStyle(styleMoney);
		cell7.setCellValue(new HSSFRichTextString(""));
		cell7.setCellStyle(styleHeader);

		return ++i;
	}

}

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
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.tesoreria.beans.BalanceSumasYSaldos;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteContabilidadBalanceGeneralExcel extends ReporteConabilidad {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteContabilidadBalanceGeneralExcel.class);

	public static HSSFWorkbook generar(HttpServletRequest req,
			HttpServletResponse res) {
		try {
									
			int entidad=WebKeysGlobal.OSPIM;
			if(ParamUtil.getInteger(req, "entidad")>0){
				entidad=ParamUtil.getInteger(req, "entidad");
			}
			
			
			Calendar desdeC = DateUtils.getDesdePeriodo(req, entidad);
			Calendar hastaC = DateUtils.getHastaPeriodo(req, entidad);

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

			Calendar desdeEjercicio = DateUtils.getDesdeEjercicio(req, entidad);
			Calendar hastaEjercicio = DateUtils.getHastaEjercicio(req, entidad);
			List<BalanceSumasYSaldos> saldosIniciales = null;
			if (incluir_saldo_inicial) {
				saldosIniciales = getSaldoInicial(desdeC, desdeEjercicio,
						hastaEjercicio, incluirAutomaticos, incluirManuales,
						incluir_asiento_inicial, entidad);
			}

			List<BalanceSumasYSaldos> balanceSumasYSaldos = AsientoServiceUtil
					.buscarBalanceSumasYSaldos(desdeC.getTime(),
							hastaC.getTime(), incluirAutomaticos,
							incluirManuales, false, entidad);

			mergearCuentas(balanceSumasYSaldos, saldosIniciales);
			Collections.sort(balanceSumasYSaldos);
			return generarReporte(desdeC.getTime(), hastaC.getTime(),
					balanceSumasYSaldos, entidad);
		} catch (Exception e) {
			_log.error("Error al generar diario", e);
			return null;
		}
	}

	private static void mergearCuentas(
			List<BalanceSumasYSaldos> balanceSumasYSaldos,
			List<BalanceSumasYSaldos> saldosIniciales) {
		// agrego todas las cuentas para las que exista un saldo
		// inicial/anterior pero que no existan asientos para el periodo dado
		if (saldosIniciales != null) {
			for (BalanceSumasYSaldos saldos : saldosIniciales) {
				BalanceSumasYSaldos balanceSaldoInicial = new BalanceSumasYSaldos(
						new PlanCuentas(saldos.getNumeroCuenta(),
								saldos.getDescripcionCuenta()));
				int indexOf = balanceSumasYSaldos.indexOf(balanceSaldoInicial);
				if (indexOf == -1) {
					balanceSumasYSaldos.add(saldos);
				} else {
					BalanceSumasYSaldos balanceAActualizar = balanceSumasYSaldos
							.get(indexOf);
					balanceAActualizar.setDebe(balanceAActualizar.getDebe()
							.add(saldos.getDebe()));
					balanceAActualizar.setHaber(balanceAActualizar.getHaber()
							.add(saldos.getHaber()));
				}
			}
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<BalanceSumasYSaldos> balanceSumasYSaldos, int entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleAllBorder = getStyleAllWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAll(wb);
		HSSFCellStyle styleAllBold = getStyleBoldWithBorder(wb);
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
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(false);

		int i = crearHeaderPrincipal(wb, sheet, 6, entidad);

		if (balanceSumasYSaldos == null || balanceSumasYSaldos.size() == 0) {
			return wb;
		}
		HSSFRow rowTitulo = sheet.createRow(i);
		HSSFCell cell = rowTitulo.createCell(0);
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
		String titulo = "Balance General. Ejercicio: "
				+ format.format(fechaIni)
				+ ". "
				+ formatFecha.format(fechaIni)
				+ " al "
				+ formatFecha.format(fechaFin)
				+ ". Cuentas: "
				+ balanceSumasYSaldos.get(0).getNumeroCuenta()
				+ " al "
				+ balanceSumasYSaldos.get(balanceSumasYSaldos.size() - 1)
						.getNumeroCuenta();
		cell.setCellValue(new HSSFRichTextString(titulo));
		cell.setCellStyle(getStyleBoldUnderlined(wb));
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
		i += 2;
		i = crearHeader(sheet, i, styleAllBorder);

		i = generarDatos(balanceSumasYSaldos, i, styleAll, styleDate,
				styleMoney, styleNumber, sheet, styleAllBorder, styleMoneyBold,
				styleAllBold);

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		return wb;
	}

	private static int generarDatos(
			List<BalanceSumasYSaldos> balanceSumasYSaldos, int i,
			HSSFCellStyle styleAll, HSSFCellStyle styleDate,
			HSSFCellStyle styleMoney, HSSFCellStyle styleNumber,
			HSSFSheet sheet, HSSFCellStyle styleAllBorder,
			HSSFCellStyle styleMoneyBold, HSSFCellStyle styleAllBold) {

		BigDecimal saldo = BigDecimal.ZERO;
		BigDecimal saldoA = BigDecimal.ZERO;
		BigDecimal saldoB = BigDecimal.ZERO;
		BigDecimal saldoC = BigDecimal.ZERO;

		for (int index = 0; index < balanceSumasYSaldos.size(); index++) {
			BalanceSumasYSaldos repo = balanceSumasYSaldos.get(index);
			i = crearFila(i, styleMoney, sheet, styleAllBorder,
					repo.getNumeroCuenta(), repo.getDescripcionCuenta(), repo
							.getDebe().subtract(repo.getHaber()));
			saldo = saldo.add(repo.getDebe().subtract(repo.getHaber()));
			saldoA = saldoA.add(repo.getDebe().subtract(repo.getHaber()));
			saldoB = saldoB.add(repo.getDebe().subtract(repo.getHaber()));
			saldoC = saldoC.add(repo.getDebe().subtract(repo.getHaber()));

			String nroActual = balanceSumasYSaldos.get(index).getNumeroCuenta();
			if (index == balanceSumasYSaldos.size() - 1
					|| !nroActual.equals(balanceSumasYSaldos.get(index + 1)
							.getNumeroCuenta())) {

				boolean mostrarTotalA = false;
				boolean mostrarTotalB = false;
				boolean mostrarTotalC = false;
				String nros[] = nroActual.split("\\.");

				if (index == balanceSumasYSaldos.size() - 1) {
					mostrarTotalA = true;
					mostrarTotalB = true;
					mostrarTotalC = true;
				} else {
					String numeroCuentaSiguiente = balanceSumasYSaldos.get(
							index + 1).getNumeroCuenta();
					String nrosSiguiente[] = numeroCuentaSiguiente.split("\\.");

					if (!nros[0].equals(nrosSiguiente[0])) {
						mostrarTotalA = true;
						mostrarTotalB = true;
						mostrarTotalC = true;
					} else if (!nros[1].equals(nrosSiguiente[1])) {
						mostrarTotalB = true;
						mostrarTotalC = true;
					} else if (!nros[2].equals(nrosSiguiente[2])) {
						mostrarTotalC = true;
					}
				}

				if (mostrarTotalC) {
					i = crearTotales(sheet, i, 2, nros[2] + " Suma", saldoC,
							styleAllBold, styleMoneyBold);
					saldoC = BigDecimal.ZERO;
				}

				if (mostrarTotalB) {
					i = crearTotales(sheet, i, 1, nros[1] + " Suma", saldoB,
							styleAllBold, styleMoneyBold);
					saldoB = BigDecimal.ZERO;
				}

				if (mostrarTotalA) {
					i = crearTotales(sheet, i, 0, nros[0] + " Suma", saldoA,
							styleAllBold, styleMoneyBold);
					saldoA = BigDecimal.ZERO;
				}
			}

		}

		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("Total"));

		cell0.setCellStyle(styleAllBold);
		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString(""));
		cellRaz.setCellStyle(styleAll);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(""));
		cell4.setCellStyle(styleAll);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(saldo.doubleValue());
		cell5.setCellStyle(styleMoneyBold);

		return i;
	}

	private static int crearTotales(HSSFSheet sheet, int i, int posicion,
			String texto, BigDecimal saldo, HSSFCellStyle styleAllBold,
			HSSFCellStyle styleMoneyBold) {
		HSSFRow row = sheet.createRow(i);

		String textoAMostrar = "";
		if (posicion == 0) {
			textoAMostrar = texto;
		}
		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(textoAMostrar));
		cell0.setCellStyle(styleAllBold);

		String textoAMostrar1 = "";
		if (posicion == 1) {
			textoAMostrar1 = texto;
		}
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(textoAMostrar1));
		cell1.setCellStyle(styleMoneyBold);

		String textoAMostrar2 = "";
		if (posicion == 2) {
			textoAMostrar2 = texto;
		}
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(textoAMostrar2));
		cell2.setCellStyle(styleMoneyBold);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(""));
		cell3.setCellStyle(styleMoneyBold);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(""));
		cell4.setCellStyle(styleMoneyBold);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(saldo.doubleValue());
		cell5.setCellStyle(styleMoneyBold);
		i++;
		return i;
	}

	private static int crearFila(int i, HSSFCellStyle styleMoney,
			HSSFSheet sheet, HSSFCellStyle styleAllBorder, String nroCuenta,
			String texto, BigDecimal importe) {
		String nros[] = nroCuenta.split("\\.");
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString(nros[0]));
		cell0.setCellStyle(styleAllBorder);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(nros[1]));
		cell1.setCellStyle(styleAllBorder);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString(nros[2]));
		cell2.setCellStyle(styleAllBorder);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString(nros[3]));
		cell3.setCellStyle(styleAllBorder);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString(texto));
		cell4.setCellStyle(styleAllBorder);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(importe.doubleValue());
		cell5.setCellStyle(styleMoney);
		i++;
		return i;
	}

	private static int crearHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell0 = row.createCell(0);
		cell0.setCellValue(new HSSFRichTextString("a"));
		cell0.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("b"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("c"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("d"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Descripción Cuenta"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Saldo"));
		cell5.setCellStyle(styleHeader);

		return ++i;
	}
}

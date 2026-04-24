package ar.com.ospim.liquidaciones.ordenespago.reportes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
 import org.apache.poi.ss.util.CellRangeAddress;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public abstract class ReporteSubdiario extends ReporteXLS {

	protected static int getCuadroDatos(HSSFSheet sheet, int i,
			Map<String, BigDecimal> mapa, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoneyRight, HSSFCellStyle styleBold,
			HSSFCellStyle styleMoneyBoldRight, HSSFCellStyle styleAllLeft,
			List<PlanCuentas> planCuentas) {

		List<String> lista = new ArrayList<String>();
		lista.addAll(mapa.keySet());
		Collections.sort(lista);
		BigDecimal total = BigDecimal.ZERO;
		for (String key : lista) {
			BigDecimal importe = mapa.get(key);
			HSSFRow rowEgresosHeader = sheet.createRow(i);
			HSSFCell cellNro = rowEgresosHeader.createCell(2);
			cellNro.setCellValue(new HSSFRichTextString(key));
			cellNro.setCellStyle(styleAllLeft);

			HSSFCell cellCuenta = rowEgresosHeader.createCell(3);
			int indexOf = planCuentas.indexOf(new PlanCuentas(key, ""));
			if (indexOf != -1) {
				cellCuenta.setCellValue(new HSSFRichTextString(planCuentas.get(
						indexOf).getCuenta()));
			} else {
				cellCuenta.setCellValue(new HSSFRichTextString(" "));
			}
			cellCuenta.setCellStyle(styleAll);

			HSSFCell cellImporte = rowEgresosHeader.createCell(4);
			cellImporte.setCellValue(importe.doubleValue());
			cellImporte.setCellStyle(styleMoneyRight);

			total = total.add(importe);
			i++;
		}

		HSSFRow rowEgresos = sheet.createRow(i);

		HSSFCell cellVacia = rowEgresos.createCell(2);
		cellVacia.setCellValue(new HSSFRichTextString(" "));
		cellVacia.setCellStyle(styleAllLeft);

		HSSFCell cellCuenta = rowEgresos.createCell(3);
		cellCuenta.setCellValue(new HSSFRichTextString("Totales"));
		cellCuenta.setCellStyle(styleBold);

		HSSFCell cellImporte = rowEgresos.createCell(4);
		cellImporte.setCellValue(total.doubleValue());
		cellImporte.setCellStyle(styleMoneyBoldRight);
		return ++i;
	}

	protected static int getCuadroHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleAllTop, HSSFCellStyle styleAllTopRight,
			HSSFCellStyle styleAllTopLeft) {
		HSSFRow rowEgresosHeader = sheet.createRow(i);
		HSSFCell cellNro = rowEgresosHeader.createCell(2);
		cellNro.setCellValue(new HSSFRichTextString(" "));
		cellNro.setCellStyle(styleAllTopLeft);

		HSSFCell cellCuenta = rowEgresosHeader.createCell(3);
		cellCuenta.setCellValue(new HSSFRichTextString("Cuentas"));
		cellCuenta.setCellStyle(styleAllTop);

		HSSFCell cellImporte = rowEgresosHeader.createCell(4);
		cellImporte.setCellValue(new HSSFRichTextString("Importe"));
		cellImporte.setCellStyle(styleAllTopRight);
		i++;
		return i;
	}

	protected static int incluirCuadro(HSSFSheet sheet, int i,
			Map<String, BigDecimal> resumenDesde,
			Map<String, BigDecimal> resumenHasta, HSSFCellStyle styleAll,
			HSSFCellStyle styleMoneyRight, HSSFCellStyle styleBold,
			HSSFCellStyle styleMoneyBoldRight, HSSFCellStyle styleAllLeft,
			HSSFCellStyle styleAllTop, HSSFCellStyle styleAllTopRight,
			HSSFCellStyle styleAllTopLeft, Date fechaIni, int entidad) {

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentas(fechaIni, entidad);
		if(entidad!=WebKeysGlobal.UOMA){
			i++;
			i = getCuadroHeader(sheet, i, styleAllTop, styleAllTopRight,
					styleAllTopLeft);
			i = getCuadroDatos(sheet, i, resumenDesde, styleAll, styleMoneyRight,
					styleBold, styleMoneyBoldRight, styleAllLeft, planCuentas);
			HSSFRow rowIntermedio = sheet.createRow(i);
			HSSFCell cellInt = rowIntermedio.createCell(2);
			cellInt.setCellValue(new HSSFRichTextString(" "));
			cellInt.setCellStyle(styleAllTop);
			sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 4));
	
			i++;
			i = getCuadroHeader(sheet, i, styleAllTop, styleAllTopRight,
					styleAllTopLeft);
			i = getCuadroDatos(sheet, i, resumenHasta, styleAll, styleMoneyRight,
					styleBold, styleMoneyBoldRight, styleAllLeft, planCuentas);
			HSSFRow rowIntermedio2 = sheet.createRow(i);
			HSSFCell cellInt2 = rowIntermedio2.createCell(2);
			cellInt2.setCellValue(new HSSFRichTextString(" "));
			cellInt2.setCellStyle(styleAllTop);
			sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 4));
		}else{
			i++;
			i = getCuadroHeader(sheet, i, styleAllTop, styleAllTopRight,
					styleAllTopLeft);
			i = getCuadroDatos(sheet, i, resumenHasta, styleAll, styleMoneyRight,
					styleBold, styleMoneyBoldRight, styleAllLeft, planCuentas);
			HSSFRow rowIntermedio2 = sheet.createRow(i);
			HSSFCell cellInt2 = rowIntermedio2.createCell(2);
			cellInt2.setCellValue(new HSSFRichTextString(" "));
			cellInt2.setCellStyle(styleAllTop);
			sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 4));
			
			i++;
			i = getCuadroHeader(sheet, i, styleAllTop, styleAllTopRight,
					styleAllTopLeft);
			i = getCuadroDatos(sheet, i, resumenDesde, styleAll, styleMoneyRight,
					styleBold, styleMoneyBoldRight, styleAllLeft, planCuentas);
			HSSFRow rowIntermedio = sheet.createRow(i);
			HSSFCell cellInt = rowIntermedio.createCell(2);
			cellInt.setCellValue(new HSSFRichTextString(" "));
			cellInt.setCellStyle(styleAllTop);
			sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 4));
		}
		return ++i;
	}

	public static class Totales {
		private BigDecimal totalComprobantes = BigDecimal.ZERO;
		private BigDecimal totalDesde = BigDecimal.ZERO;
		private BigDecimal totalHacia = BigDecimal.ZERO;
		private Map<String, BigDecimal> resumenDesde = new HashMap<String, BigDecimal>();
		private Map<String, BigDecimal> resumenHasta = new HashMap<String, BigDecimal>();

		private BigDecimal totalAnticipos = BigDecimal.ZERO;
		private Map<String, BigDecimal> resumenDesdeAnulados = new HashMap<String, BigDecimal>();
		private Map<String, BigDecimal> resumenHastaAnulados = new HashMap<String, BigDecimal>();

		public BigDecimal getTotalComprobantes() {
			return totalComprobantes;
		}

		public void setTotalComprobantes(BigDecimal totalComprobantes) {
			this.totalComprobantes = totalComprobantes;
		}

		public BigDecimal getTotalDesde() {
			return totalDesde;
		}

		public void setTotalDesde(BigDecimal totalDesde) {
			this.totalDesde = totalDesde;
		}

		public BigDecimal getTotalHacia() {
			return totalHacia;
		}

		public void setTotalHacia(BigDecimal totalHacia) {
			this.totalHacia = totalHacia;
		}

		public Map<String, BigDecimal> getResumenDesde() {
			return resumenDesde;
		}

		public Map<String, BigDecimal> getResumenHasta() {
			return resumenHasta;
		}

		public Map<String, BigDecimal> getResumenDesdeAnulados() {
			return resumenDesdeAnulados;
		}

		public void setResumenDesdeAnulados(
				Map<String, BigDecimal> resumenDesdeAnulados) {
			this.resumenDesdeAnulados = resumenDesdeAnulados;
		}

		public Map<String, BigDecimal> getResumenHastaAnulados() {
			return resumenHastaAnulados;
		}

		public void setResumenHastaAnulados(
				Map<String, BigDecimal> resumenHastaAnulados) {
			this.resumenHastaAnulados = resumenHastaAnulados;
		}

		public BigDecimal getTotalAnticipos() {
			return totalAnticipos;
		}

		public void setTotalAnticipos(BigDecimal totalAnticipos) {
			this.totalAnticipos = totalAnticipos;
		}

	}
}

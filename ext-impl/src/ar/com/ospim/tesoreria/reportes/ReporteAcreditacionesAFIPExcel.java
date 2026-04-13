package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.global.beans.TipoMovExtractoBancario;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteAcreditacionesAFIPExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteAcreditacionesAFIPExcel.class);

	public static HSSFWorkbook generaReporteLibroBanco(HttpServletRequest req,
			HttpServletResponse res) {

		Map<Date, List<ResumenExtractoBancario>> resumenes = null;

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");

		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			resumenes = MovimientoBancarioServiceUtil
					.getResumenExtractoBancario(fechaIni, fechaFin);
			return generarReporte(fechaIni, fechaFin, resumenes);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			Map<Date, List<ResumenExtractoBancario>> resumenes)
			throws SQLException {

		Map<Date, List<ResumenExtractoBancario>> resumenOSAportes = AfipServiceUtil
				.getResumenOSAportes(fechaIni, fechaFin);

		Map<Date, List<ResumenExtractoBancario>> resumenSubsidioDesempleo = AfipServiceUtil
				.getResumenSubsidioDesempleo(fechaIni, fechaFin);

		List<TipoMovExtractoBancario> tipos = TraeListasServiceUtil
				.getTiposMovExtractoBancario();

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDateWithBorder = getStyleDateWithBorder(wb);
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleMoneyBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		addDefaultHeader(sheet);

		Map<Integer, BigDecimal> totales = new HashMap<Integer, BigDecimal>();

		if (!fechaIni.after(fechaFin)) {
			createHeaders(sheet, styleHeader, fechaIni, fechaFin);
			int i = 3;
			Calendar fechaAux = Calendar.getInstance();
			fechaAux.setTime(fechaIni);
			while (!fechaAux.getTime().after(fechaFin)) {
				HSSFRow row = sheet.createRow(i);
				HSSFCell cell = row.createCell(0);
				cell.setCellValue(fechaAux.getTime());
				cell.setCellStyle(styleDateWithBorder);
				if (resumenes.get(fechaAux.getTime()) != null || resumenSubsidioDesempleo.get(fechaAux.getTime())!=null || resumenOSAportes.get(fechaAux.getTime())!=null) {
					completarDatos(resumenes.get(fechaAux.getTime()),
							resumenOSAportes.get(fechaAux.getTime()),
							resumenSubsidioDesempleo.get(fechaAux.getTime()),
							row, styleAll, styleBold, styleMoney, totales);
				}
				i++;
				fechaAux.add(Calendar.DATE, 1);
			}
			HSSFRow row = sheet.createRow(i);
			HSSFCell cellTotales = row.createCell(0);
			cellTotales.setCellValue(new HSSFRichTextString("Totales"));
			cellTotales.setCellStyle(styleBold);
			for (int j = 0; j < tipos.size() + 10; j++) {
				BigDecimal total = BigDecimal.ZERO;
				if (totales.get(j) != null) {
					total = totales.get(j);
				}
				HSSFCell cell = row.createCell(j + 1);
				cell.setCellValue(total.doubleValue());
				cell.setCellStyle(styleMoney);
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
			sheet.autoSizeColumn((short) 15);

		}
		return wb;
	}

	private static void completarDatos(
			List<ResumenExtractoBancario> resumenesBanco,
			List<ResumenExtractoBancario> resumenesAportes,
			List<ResumenExtractoBancario> resumenSubsidioYDesempleo,
			HSSFRow row, HSSFCellStyle styleAll, HSSFCellStyle styleBold,
			HSSFCellStyle styleMoney, Map<Integer, BigDecimal> totalesGenerales) {
		List<TipoMovExtractoBancario> tipos = TraeListasServiceUtil
				.getTiposMovExtractoBancario();

		boolean casillerosCompletos[] = new boolean[tipos.size()];
		BigDecimal total = BigDecimal.ZERO;
		if (resumenesBanco != null) {
			for (ResumenExtractoBancario resumen : resumenesBanco) {
				int indexOf = tipos.indexOf(resumen.getTipo());
				if (indexOf != -1) {
					casillerosCompletos[indexOf] = true;
					HSSFCell cellCol = row.createCell(indexOf + 1);
					BigDecimal totalParticular = resumen.getTotal();
					cellCol.setCellValue(totalParticular.doubleValue());
					total = total.add(totalParticular);
					cellCol.setCellStyle(styleMoney);
					setearTotalGeneral(totalesGenerales, indexOf,
							totalParticular);
				}
			}
		}

		// seteo ceros
		for (int i = 0; i < casillerosCompletos.length; i++) {
			if (!casillerosCompletos[i]) {
				HSSFCell cellCol1 = row.createCell(i + 1);
				cellCol1.setCellValue(0D);
				cellCol1.setCellStyle(styleMoney);
			}
		}
		HSSFCell cellColtotal = row.createCell(tipos.size() + 1);
		cellColtotal.setCellValue(total.doubleValue());
		cellColtotal.setCellStyle(styleBold);
		setearTotalGeneral(totalesGenerales, tipos.size(), total);

		int aPartirDe = tipos.size() + 1;
		BigDecimal totalAportes = agregarInfo(resumenesAportes, row,
				styleMoney, aPartirDe, 5, totalesGenerales);

		HSSFCell cellColtotalAportes = row.createCell(aPartirDe + 5 + 1);
		cellColtotalAportes.setCellValue(totalAportes.doubleValue());
		cellColtotalAportes.setCellStyle(styleBold);
		setearTotalGeneral(totalesGenerales, aPartirDe + 5, totalAportes);

		HSSFCell diff = row.createCell(aPartirDe + 5 + 2);
		diff.setCellValue(total.subtract(totalAportes).doubleValue());
		diff.setCellStyle(styleBold);
		setearTotalGeneral(totalesGenerales, aPartirDe + 5 + 1, total.subtract(totalAportes));
		
		aPartirDe = tipos.size() + 1 + 5 + 2;
		agregarInfo(resumenSubsidioYDesempleo, row,
				styleMoney, aPartirDe, 2, totalesGenerales);

	}

	private static void setearTotalGeneral(
			Map<Integer, BigDecimal> totalesGenerales, int indexOf,
			BigDecimal totalParticular) {
		if (totalesGenerales.get(indexOf) == null) {
			totalesGenerales.put(indexOf, totalParticular);
		} else {
			totalesGenerales.put(indexOf,
					totalesGenerales.get(indexOf).add(totalParticular));
		}
	}

	private static BigDecimal agregarInfo(
			List<ResumenExtractoBancario> resumenes, HSSFRow row,
			HSSFCellStyle styleMoney, int aPartirDe, int cantidadItems,
			Map<Integer, BigDecimal> totalesGenerales) {
		boolean casillerosCompletosAportes[] = new boolean[cantidadItems];
		BigDecimal total = BigDecimal.ZERO;
		if (resumenes != null) {
			for (ResumenExtractoBancario resumen : resumenes) {
				casillerosCompletosAportes[resumen.getTipo()
						.getCodigoMovimiento() - 1] = true;
				HSSFCell cellCol = row.createCell(resumen.getTipo()
						.getCodigoMovimiento() + aPartirDe);
				cellCol.setCellValue(resumen.getTotal().doubleValue());
				total = total.add(resumen.getTotal());
				cellCol.setCellStyle(styleMoney);
				setearTotalGeneral(totalesGenerales, resumen.getTipo()
						.getCodigoMovimiento() + aPartirDe - 1,
						resumen.getTotal());
			}
		}
		// seteo ceros
		for (int i = 0; i < casillerosCompletosAportes.length; i++) {
			if (!casillerosCompletosAportes[i]) {
				HSSFCell cellCol1 = row.createCell(i + 1 + aPartirDe);
				cellCol1.setCellValue(0D);
				cellCol1.setCellStyle(styleMoney);
			}
		}
		return total;
	}

	private static void createHeaders(HSSFSheet sheet,
			HSSFCellStyle styleHeader, Date fechaIni, Date fechaFin) {

		List<TipoMovExtractoBancario> tipos = TraeListasServiceUtil
				.getTiposMovExtractoBancario();

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		cellTitulo.setCellValue(new HSSFRichTextString(
				"Cuadro Acreditaciones AFIP - Desde:"
						+ DateUtils.format(fechaIni, DateUtils.SHORT)
						+ " - Hasta:"
						+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cellTitulo.setCellStyle(styleHeader);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,
				tipos.size() + 5 + 3));

		HSSFRow rowTitulo2 = sheet.createRow(1);
		HSSFCell cellH1 = rowTitulo2.createCell(1);
		cellH1.setCellValue(new HSSFRichTextString(
				"INFORMACIÓN DE MOVIMIENTOS BANCARIOS"));
		cellH1.setCellStyle(styleHeader);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, tipos.size() + 1));

		HSSFCell cellH2 = rowTitulo2.createCell(tipos.size() + 2);
		cellH2.setCellValue(new HSSFRichTextString(
				"INFORMACIÓN DEL DETALLE DE NOMINAS/TRANSFERENCIAS"));
		cellH2.setCellStyle(styleHeader);
//		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,
//				tipos.size() + 5 + 2 + 2));
//		sheet.addMergedRegion(new CellRangeAddress(1, 1, tipos.size() + 2,
//				tipos.size() + 5 + 2));

		HSSFRow row = sheet.createRow(2);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Fecha"));
		cell.setCellStyle(styleHeader);

		for (int j = 0; j < tipos.size(); j++) {
			HSSFCell cellCol = row.createCell(j + 1);
			cellCol.setCellValue(new HSSFRichTextString(tipos.get(j)
					.getDescripcionMovimiento()));
			cellCol.setCellStyle(styleHeader);
		}

		HSSFCell cellTot = row.createCell(tipos.size() + 1);
		cellTot.setCellValue(new HSSFRichTextString("Total"));
		cellTot.setCellStyle(styleHeader);

		HSSFCell cell1s = row.createCell(tipos.size() + 2);
		cell1s.setCellValue(new HSSFRichTextString("Distribución Normal"));
		cell1s.setCellStyle(styleHeader);

		HSSFCell cell2s = row.createCell(tipos.size() + 3);
		cell2s.setCellValue(new HSSFRichTextString("Comisiones"));
		cell2s.setCellStyle(styleHeader);

		HSSFCell cell3s = row.createCell(tipos.size() + 4);
		cell3s.setCellValue(new HSSFRichTextString("Hospitales"));
		cell3s.setCellStyle(styleHeader);

		HSSFCell cell4s = row.createCell(tipos.size() + 5);
		cell4s.setCellValue(new HSSFRichTextString("Dto Anticipos"));
		cell4s.setCellStyle(styleHeader);

		HSSFCell cell5s = row.createCell(tipos.size() + 6);
		cell5s.setCellValue(new HSSFRichTextString("Pago Anticipos"));
		cell5s.setCellStyle(styleHeader);

		HSSFCell cell6s = row.createCell(tipos.size() + 7);
		cell6s.setCellValue(new HSSFRichTextString("Total"));
		cell6s.setCellStyle(styleHeader);

		HSSFCell cellDiff = row.createCell(tipos.size() + 8);
		cellDiff.setCellValue(new HSSFRichTextString("Diferencia"));
		cellDiff.setCellStyle(styleHeader);

		HSSFCell cellSubsidio = row.createCell(tipos.size() + 9);
		cellSubsidio.setCellValue(new HSSFRichTextString("Subsidio"));
		cellSubsidio.setCellStyle(styleHeader);

		HSSFCell cellDesempleo = row.createCell(tipos.size() + 10);
		cellDesempleo.setCellValue(new HSSFRichTextString("Desempleo"));
		cellDesempleo.setCellStyle(styleHeader);

	}

	public static class ResumenExtractoBancario {
		private Date fecha;
		private TipoMovExtractoBancario tipo;

		private BigDecimal total;

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public void setTotal(BigDecimal total) {
			this.total = total;
		}

		public void setTipo(TipoMovExtractoBancario tipo) {
			this.tipo = tipo;
		}

		public TipoMovExtractoBancario getTipo() {
			return tipo;
		}

		public static ResumenExtractoBancario getMapping(ResultSet rs)
				throws SQLException {
			ResumenExtractoBancario resumen = new ResumenExtractoBancario();
			resumen.setTipo(TipoMovExtractoBancario.getMapping(rs));
			resumen.setFecha(rs.getDate("fecha"));
			resumen.setTotal(rs.getBigDecimal("total"));
			return resumen;
		}
	}
}

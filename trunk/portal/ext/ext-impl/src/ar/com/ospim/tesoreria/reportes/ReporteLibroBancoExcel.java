package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
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
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.LibroBanco;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteLibroBancoExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteLibroBancoExcel.class);

	public static HSSFWorkbook generaReporteLibroBanco(HttpServletRequest req,
			HttpServletResponse res) {

		Integer ctaBcria = ParamUtil.getInteger(req, "id_cta_bcria");		
		int entidad=ParamUtil.getInteger(req, "entidad");
		

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);

			// busco el ultimo saldo disponible antes de la fechaIni
			EstadoInicialLibroBanco saldoIni = ContabilidadServiceUtil
					.getSaldoInicialBanco(ctaBcria, fechaIni);

			// Busco el libroBanco a partir de la fecha del ultimo saldo
			// disponible para poder calcular el saldo actualizado al dia
			// fechaIni
			List<LibroBanco> libro = ContabilidadServiceUtil.libroBanco(
					saldoIni.getFecha()!=null?saldoIni.getFecha():fechaIni, fechaFin, ctaBcria, entidad);

			return generarReporte(fechaIni, fechaFin, libro, saldoIni);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<LibroBanco> libro, EstadoInicialLibroBanco saldoIni) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDateWithBorder = getStyleDateWithBorder(wb);
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);

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
		CuentaBancaria ctaBcria = saldoIni.getCtaBcria();
		cellTitulo.setCellValue(new HSSFRichTextString("Libro Banco - Desde: "
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " Hasta: "
				+ DateUtils.format(fechaFin, DateUtils.SHORT) + "- Cuenta: "
				+ ctaBcria.getNro_cuenta() + "/" + ctaBcria.getSucursal()
				+ " - " + ctaBcria.getDescripcion()));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

		createHeader(wb, sheet, styleHeader);

		// actualizo el saldo a la fechaIni
		BigDecimal saldo = getSaldoInicial(saldoIni, fechaIni, libro);

		// agrego la fila del saldo con la fechaIni, porque el saldo esta
		// actualizado a esta fecha
		getRowSaldoInicial(fechaIni, saldo, styleMoney, sheet,
				styleDateWithBorder, styleAll);

		int i = 3;
		for (LibroBanco l : libro) {
			// ignoro todas las filas que busque para actualizar el saldo
			if (l.getFecha().compareTo(fechaIni) >= 0) {
				HSSFRow row = sheet.createRow(i);

				HSSFCell cell = row.createCell(0);
				cell.setCellValue(l.getFecha());
				cell.setCellStyle(styleDateWithBorder);

				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(new HSSFRichTextString(l.getComprobante()));
				cell1.setCellStyle(styleAll);

				HSSFCell cell2 = row.createCell(2);
				cell2.setCellValue(new HSSFRichTextString(l.getDescripcion()));
				cell2.setCellStyle(styleAll);

				HSSFCell cell3 = row.createCell(3);
				if (l.getDebito_credito().equalsIgnoreCase("C")) {
					cell3.setCellValue(l.getImporte().doubleValue());
					saldo = saldo.add(l.getImporte());
				} else {
					cell3.setCellValue(new HSSFRichTextString(" "));
				}
				cell3.setCellStyle(styleMoney);

				HSSFCell cell4 = row.createCell(4);
				if (l.getDebito_credito().equalsIgnoreCase("D")) {
					cell4.setCellValue(l.getImporte().doubleValue());
					saldo = saldo.subtract(l.getImporte());
				} else {
					cell4.setCellValue(new HSSFRichTextString(" "));
				}
				cell4.setCellStyle(styleMoney);

				HSSFCell cell5 = row.createCell(5);
				cell5.setCellValue(saldo.doubleValue());
				cell5.setCellStyle(styleMoney);
				i++;
			}
		}

		sheet.autoSizeColumn((short) 0);
		sheet.setColumnWidth(1, 7680);
		sheet.setColumnWidth(2, 15360);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);

		return wb;
	}

	private static BigDecimal getSaldoInicial(EstadoInicialLibroBanco saldoIni,
			Date fechaIni, List<LibroBanco> libro) {

		BigDecimal saldoInicial = saldoIni.getImporte();
		Iterator<LibroBanco> it = libro.iterator();
		boolean stop = false;
		while (it.hasNext() && !stop) {
			LibroBanco l = it.next();
			if (l.getFecha().compareTo(fechaIni) < 0) {
				if (l.getDebito_credito().equalsIgnoreCase("C")) {
					saldoInicial = saldoInicial.add(l.getImporte());
				} else {
					saldoInicial = saldoInicial.subtract(l.getImporte());
				}
			} else {
				stop = true;
			}
		}
		return saldoInicial;
	}

	private static void getRowSaldoInicial(Date fecha, BigDecimal saldoIni,
			HSSFCellStyle styleMoney, HSSFSheet sheet,
			HSSFCellStyle styleDateWithBorder, HSSFCellStyle styleAll) {
		HSSFRow rowSaldoIni = sheet.createRow(2);

		HSSFCell cell = rowSaldoIni.createCell(0);
		cell.setCellValue(fecha);
		cell.setCellStyle(styleDateWithBorder);

		HSSFCell cell1 = rowSaldoIni.createCell(1);
		cell1.setCellStyle(styleAll);

		HSSFCell cell2 = rowSaldoIni.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Saldo Inicial"));
		cell2.setCellStyle(styleAll);

		HSSFCell cell3 = rowSaldoIni.createCell(3);
		cell3.setCellStyle(styleAll);

		HSSFCell cell4 = rowSaldoIni.createCell(4);
		cell4.setCellStyle(styleAll);

		HSSFCell cellS = rowSaldoIni.createCell(5);
		cellS.setCellValue(saldoIni.doubleValue());
		cellS.setCellStyle(styleMoney);
	}

	private static void createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader) {
		HSSFRow row = sheet.createRow(1);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Fecha"));
		cell.setCellStyle(styleHeader);

		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue(new HSSFRichTextString("Comprobante"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue(new HSSFRichTextString("Decripcion"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(3);
		cell3.setCellValue(new HSSFRichTextString("Debe"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(4);
		cell4.setCellValue(new HSSFRichTextString("Haber"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(5);
		cell5.setCellValue(new HSSFRichTextString("Saldo"));
		cell5.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, 5, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
	}

	public static class EstadoInicialLibroBanco {
		private Date fecha;
		private BigDecimal importe;
		private CuentaBancaria ctaBcria;

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public Date getFecha() {
			return fecha;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setCtaBcria(CuentaBancaria ctaBcria) {
			this.ctaBcria = ctaBcria;
		}

		public CuentaBancaria getCtaBcria() {
			return ctaBcria;
		}
	}

	/*public static HSSFWorkbook prueba() {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Integer ctaBcria = 2;

		try {
			Date fechaIni = format.parse("01-01-2011");
			Date fechaFin = format.parse("01-02-2011");

			// busco el ultimo saldo disponible antes de la fechaIni
			EstadoInicialLibroBanco saldoIni = ContabilidadServiceUtil
					.getSaldoInicialBanco(ctaBcria, fechaIni);

			// Busco el libroBanco a partir de la fecha del ultimo saldo
			// disponible para poder calcular el saldo actualizado al dia
			// fechaIni
			List<LibroBanco> libro = ContabilidadServiceUtil.libroBanco(
					saldoIni.getFecha(), fechaFin, ctaBcria);

			return generarReporte(fechaIni, fechaFin, libro, saldoIni);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}*/

}

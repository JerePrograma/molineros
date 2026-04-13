package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteAnticiposExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteAnticiposExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {
		_log.debug("generando reporte");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);

		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);

			Empresa empresa = new Empresa(cuit, sucu, "");
			empresa.setId_seccional(seccional);

			List<ReporteAnticipos> recibos = ReciboServiceUtil
					.getReporteAnticipos(fechaIni, fechaFin, empresa);

			return generarReporte(fechaIni, fechaFin, recibos);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ReporteAnticipos> reporte) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleFechaLeft = getStyleDate(wb);
		styleFechaLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleTop = getStyleAll(wb);
		styleTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleAll = getStyleAll(wb);

		HSSFCellStyle styleMoneyRight = getStyleMoney(wb);
		styleMoneyRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleMoney = getStyleMoney(wb);

		HSSFCellStyle styleHeader = getStyleHeader(wb);

		HSSFCellStyle styleHeaderLeft = getStyleHeader(wb);
		styleHeaderLeft.setBorderLeft(BorderStyle.THIN);

		HSSFCellStyle styleHeaderRight = getStyleAll(wb);
		styleHeaderRight.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleMoneyRightTop = getStyleMoney(wb);
		styleMoneyRightTop.setBorderTop(BorderStyle.THIN);
		styleMoneyRightTop.setBorderRight(BorderStyle.THIN);

		HSSFCellStyle styleFechaLeftTop = getStyleDate(wb);
		styleFechaLeftTop.setBorderLeft(BorderStyle.THIN);
		styleFechaLeftTop.setBorderTop(BorderStyle.THIN);

		HSSFCellStyle styleMoneyTop = getStyleMoney(wb);
		styleMoneyTop.setBorderTop(BorderStyle.THIN);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.LeftMargin, 0.2);
		sheet.setMargin(HSSFSheet.RightMargin, 0.2);
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cell = rowTitulo.createCell(0);
		cell.setCellValue(new HSSFRichTextString("Reporte Anticipos - Desde:"
				+ DateUtils.format(fechaIni, DateUtils.SHORT) + " - Hasta:"
				+ DateUtils.format(fechaFin, DateUtils.SHORT)));
		cell.setCellStyle(getStyleWhiteHeaderWithBorder(wb));

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		int i = 1;
		i = generarHeader(sheet, i, styleHeader, styleHeaderLeft,
				styleHeaderRight, wb);
		BigDecimal total = BigDecimal.ZERO;
		String cuit = "";
		for (ReporteAnticipos repo : reporte) {
			boolean nuevo = false;
			if (!repo.getCuit().equals(cuit)) {
				nuevo = true;
				cuit = repo.getCuit();
				total = BigDecimal.ZERO;
			}
			HSSFRow row = sheet.createRow(i);
			i++;
			HSSFCell cell0 = row.createCell(0);
			cell0.setCellValue(repo.getFecha());
			if (nuevo) {
				cell0.setCellStyle(styleFechaLeftTop);
			} else {
				cell0.setCellStyle(styleFechaLeft);
			}

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(new HSSFRichTextString(repo.getDescripcion()));
			if (nuevo) {
				cell1.setCellStyle(styleTop);
			} else {
				cell1.setCellStyle(styleAll);
			}

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(new HSSFRichTextString(repo.getCuit()));
			if (nuevo) {
				cell2.setCellStyle(styleTop);
			} else {
				cell2.setCellStyle(styleAll);
			}

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(new HSSFRichTextString(repo.getRazonSocial()));
			if (nuevo) {
				cell3.setCellStyle(styleTop);
			} else {
				cell3.setCellStyle(styleAll);
			}

			HSSFCell cell4 = row.createCell(4);
			if (repo.getDebito_credito().equals("C")) {
				cell4.setCellValue(repo.getImporte().doubleValue());
				total = total.add(repo.getImporte());
			} else {
				cell4.setCellValue(0D);
			}

			if (nuevo) {
				cell4.setCellStyle(styleMoneyTop);
			} else {
				cell4.setCellStyle(styleMoney);
			}

			HSSFCell cell5 = row.createCell(5);
			if (repo.getDebito_credito().equals("D")) {
				cell5.setCellValue(repo.getImporte().doubleValue());
				total = total.subtract(repo.getImporte());
			} else {
				cell5.setCellValue(0D);
			}
			if (nuevo) {
				cell5.setCellStyle(styleMoneyTop);
			} else {
				cell5.setCellStyle(styleMoney);
			}

			HSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(total.doubleValue());
			if (nuevo) {
				cell6.setCellStyle(styleMoneyRightTop);
			} else {
				cell6.setCellStyle(styleMoneyRight);
			}
		}

		HSSFRow row = sheet.createRow(i);
		HSSFCell cellFin = row.createCell(0);
		cellFin.setCellValue(new HSSFRichTextString(" "));
		cellFin.setCellStyle(styleTop);
		sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));

		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		return wb;
	}

	private static int generarHeader(HSSFSheet sheet, int i,
			HSSFCellStyle styleHeader, HSSFCellStyle styleHeaderL,
			HSSFCellStyle styleHeaderR, HSSFWorkbook wb) {
		HSSFRow row = sheet.createRow(i);

		HSSFCell cell1 = row.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("Fecha"));
		cell1.setCellStyle(styleHeader);

		HSSFCell cell0 = row.createCell(1);
		cell0.setCellValue(new HSSFRichTextString("Descripcion"));
		cell0.setCellStyle(styleHeaderL);

		HSSFCell cellAcreed = row.createCell(2);
		cellAcreed.setCellValue(new HSSFRichTextString("CUIT"));
		cellAcreed.setCellStyle(styleHeader);

		HSSFCell cellRaz = row.createCell(3);
		cellRaz.setCellValue(new HSSFRichTextString("Razon Social"));
		cellRaz.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(4);
		cell3.setCellValue(new HSSFRichTextString("Debe"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(5);
		cell4.setCellValue(new HSSFRichTextString("Haber"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(6);
		cell5.setCellValue(new HSSFRichTextString("Saldo"));
		cell5.setCellStyle(styleHeader);

		//wb.setRepeatingRowsAndColumns(0, 0, 6, i, i);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
		return ++i;
	}

	public static class ReporteAnticipos {
		private String descripcion;
		private Date fecha;
		private String cuit;
		private String sucursal;
		private BigDecimal importe;
		private String debito_credito;
		private String razonSocial;

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public Date getFecha() {
			return fecha;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public String getCuit() {
			return cuit;
		}

		public void setCuit(String cuit) {
			this.cuit = cuit;
		}

		public String getSucursal() {
			return sucursal;
		}

		public void setSucursal(String sucursal) {
			this.sucursal = sucursal;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public String getDebito_credito() {
			return debito_credito;
		}

		public void setDebito_credito(String debitoCredito) {
			debito_credito = debitoCredito;
		}

		public static ReporteAnticipos getMapping(ResultSet rs)
				throws SQLException {
			ReporteAnticipos ra = new ReporteAnticipos();
			ra.setCuit(rs.getString("cuit"));
			ra.setDebito_credito(rs.getString("debito_credito"));
			ra.setFecha(rs.getDate("fecha"));
			ra.setImporte(rs.getBigDecimal("importe"));
			ra.setDescripcion(rs.getString("recibo"));
			ra.setSucursal(rs.getString("sucursal"));
			ra.setRazonSocial(rs.getString("razon_soc"));
			return ra;
		}

		public void setRazonSocial(String razonSocial) {
			this.razonSocial = razonSocial;
		}

		public String getRazonSocial() {
			return razonSocial;
		}

	}
}

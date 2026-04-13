package ar.com.ospim.tesoreria.reportes;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteAnticiposOPExcel extends ReporteXLS {

	private static Log _log = LogFactoryUtil
			.getLog(ReporteAnticiposOPExcel.class);

	public static HSSFWorkbook generaReporte(HttpServletRequest req,
			HttpServletResponse res) {

		try {
			int entidad = ParamUtil.getInteger(req, "entidad");
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			Date fechaUtil = null;
			String cuit=ParamUtil.getString(req, "cuit");
			String sucursal=ParamUtil.getString(req, "sucursal");
			int idSeccional=ParamUtil.getInteger(req, "id_seccional");
			if (entidad == WebKeysGlobal.UOMA) {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				String fechaDesdeDia = ParamUtil.getString(req,
						"fechaHastaDiaUtil");
				String fechaDesdeMes = ParamUtil.getString(req,
						"fechaHastaMesUtil");
				fechaDesdeMes = String
						.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
				String fechaDesdeAnio = ParamUtil.getString(req,
						"fechaHastaAnioUtil");

				fechaUtil = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
						+ "-" + fechaDesdeAnio);
			} else {
				fechaUtil = new Date();
			}

			List<ItemAnticipoOP> libro = ComprobanteServiceUtil
					.listadoAnticiposPagos(fechaIni, fechaFin, fechaUtil, cuit, sucursal, idSeccional,
							entidad);

			return generarReporte(fechaIni, fechaFin, libro, entidad);
		} catch (Exception e) {
			_log.error("Error al generar listado estado comprobantes", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<ItemAnticipoOP> libro, int entidad) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleDateWithBorder = getStyleDateWithBorder(wb);
		HSSFCellStyle styleHeader = getStyleHeaderWithBorder(wb);
		HSSFCellStyle styleAll = getStyleAllWithBorder(wb);
		HSSFCellStyle styleBold = getStyleBoldWithBorder(wb);
		HSSFCellStyle styleMoney = getStyleMoneyWithBorder(wb);
		HSSFCellStyle styleMoneyBold = getStyleMoneyBoldWithBorder(wb);

		HSSFSheet sheet = wb.createSheet("Hoja 1");
		sheet.setMargin(HSSFSheet.TopMargin, 0.8);
		addDefaultHeader(sheet);

		HSSFPrintSetup ps = sheet.getPrintSetup();
		sheet.setAutobreaks(true);
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setFitHeight((short) 0);
		ps.setFitWidth((short) 1);
		ps.setLandscape(true);

		HSSFRow rowTitulo = sheet.createRow(0);
		HSSFCell cellTitulo = rowTitulo.createCell(0);
		StringBuffer sb = new StringBuffer("Listado Anticipos OP - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));

		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		cellTitulo.setCellStyle(styleHeader);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

		createHeader(wb, sheet, styleHeader, entidad);

		BigDecimal total = BigDecimal.ZERO;
		BigDecimal totalAplicado = BigDecimal.ZERO;
		int i = 2;
		int colcount = 0;

		for (ItemAnticipoOP l : libro) {
			colcount = 0;
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell2 = row.createCell(colcount++);
			cell2.setCellValue(new HSSFRichTextString(l.getDescripcion()));
			cell2.setCellStyle(styleAll);

			BigDecimal importe = l.getImporte() != null ? l.getImporte()
					: BigDecimal.ZERO;
			total = total.add(importe);
			HSSFCell cell3 = row.createCell(colcount++);
			cell3.setCellValue(importe.doubleValue());
			cell3.setCellStyle(styleMoney);

			HSSFCell cell4 = row.createCell(colcount++);
			cell4.setCellValue(new HSSFRichTextString(l.getEmpresa().getCuit()));
			cell4.setCellStyle(styleAll);

			HSSFCell cell5 = row.createCell(colcount++);
			cell5.setCellValue(new HSSFRichTextString(l.getEmpresa()
					.getRazon_soc()));
			cell5.setCellStyle(styleAll);

			HSSFCell cell6 = row.createCell(colcount++);
			cell6.setCellValue(l.getFecha());
			cell6.setCellStyle(styleDateWithBorder);

			/*HSSFCell cell7 = row.createCell(colcount++);
			if (l.getPeriodoPrestacion() != null) {
				cell7.setCellValue(l.getPeriodoPrestacion());
			}
			cell7.setCellStyle(styleDateWithBorder);*/

			HSSFCell cell8 = row.createCell(colcount++);
			cell8.setCellValue(l.getFechaPago());
			cell8.setCellStyle(styleDateWithBorder);

			HSSFCell cell9 = row.createCell(colcount++);
			cell9.setCellValue(l.getOp());
			cell9.setCellStyle(styleAll);

			if (l.getFechaPagoAplicacion() != null) {
				HSSFCell cellFechaAplic = row.createCell(colcount++);
				cellFechaAplic.setCellValue(l.getFechaPagoAplicacion());
				cellFechaAplic.setCellStyle(styleDateWithBorder);

				HSSFCell cellOpAplic = row.createCell(colcount++);
				cellOpAplic.setCellValue(l.getOpAplicacion());
				cellOpAplic.setCellStyle(styleAll);

				HSSFCell cell10 = row.createCell(colcount++);
				cell10.setCellValue(l.getImporteAplicado().doubleValue());
				cell10.setCellStyle(styleMoney);
				totalAplicado = totalAplicado.add(l.getImporteAplicado());
			} else {
				HSSFCell cellFechaAplic = row.createCell(colcount++);
				cellFechaAplic.setCellValue(new HSSFRichTextString(""));
				cellFechaAplic.setCellStyle(styleAll);

				HSSFCell cellOpAplic = row.createCell(colcount++);
				cellOpAplic.setCellValue(new HSSFRichTextString(""));
				cellOpAplic.setCellStyle(styleAll);

				HSSFCell cell10 = row.createCell(colcount++);
				cell10.setCellValue(new HSSFRichTextString(""));
				cell10.setCellStyle(styleAll);
			}
			if (entidad == WebKeysGlobal.UOMA ) {
				HSSFCell cell11 = row.createCell(colcount++);
				cell11.setCellValue(l.getNroCuota());
				cell11.setCellStyle(styleAll);

				HSSFCell cell12 = row.createCell(colcount++);
				cell12.setCellValue(l.getCantCuotas());
				cell12.setCellStyle(styleAll);

				HSSFCell cell13 = row.createCell(colcount++);
				cell13.setCellValue(l.getValorCuota().doubleValue());
				cell13.setCellStyle(styleMoney);

				HSSFCell cell14 = row.createCell(colcount++);
				cell14.setCellValue(l.getSaldo().doubleValue());
				cell14.setCellStyle(styleMoney);
			}
			
			if(	entidad == WebKeysGlobal.AMTIMA) {
				HSSFCell cell14 = row.createCell(colcount++);
				cell14.setCellValue(l.getSaldo().doubleValue());
				cell14.setCellStyle(styleMoney);
			}
			

			i++;
		}

		/*HSSFRow rowTotal = sheet.createRow(i);
		HSSFCell cellTotal = rowTotal.createCell(0);
		cellTotal.setCellValue(new HSSFRichTextString("Total"));
		cellTotal.setCellStyle(styleBold);

		HSSFCell cellTotalValue = rowTotal.createCell(1);
		cellTotalValue.setCellValue(total.doubleValue());
		cellTotalValue.setCellStyle(styleMoneyBold);

		HSSFCell cellTotalAplicado = rowTotal.createCell(9);
		cellTotalAplicado
				.setCellValue(new HSSFRichTextString("Total Aplicado"));
		cellTotalAplicado.setCellStyle(styleBold);

		HSSFCell cellTotalValueAplicado = rowTotal.createCell(10);
		cellTotalValueAplicado.setCellValue(totalAplicado.doubleValue());
		cellTotalValueAplicado.setCellStyle(styleMoneyBold);*/

		sheet.setColumnWidth(0, 10360);

		for (int k = 0; k <= colcount; k++) {
			sheet.autoSizeColumn((short) k);
		}
		return wb;
	}

	private static void createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader, int entidad) {
		int rowCount = 0;
		HSSFRow row = sheet.createRow(1);

		HSSFCell cell2 = row.createCell(rowCount++);
		cell2.setCellValue(new HSSFRichTextString("Comprobante"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(rowCount++);
		cell3.setCellValue(new HSSFRichTextString("Importe Comprobante"));
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(rowCount++);
		cell4.setCellValue(new HSSFRichTextString("Cuit"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(rowCount++);
		cell5.setCellValue(new HSSFRichTextString("Razon Social"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(rowCount++);
		cell6.setCellValue(new HSSFRichTextString("Recepcion"));
		cell6.setCellStyle(styleHeader);

		/*HSSFCell cell7 = row.createCell(rowCount++);
		cell7.setCellValue(new HSSFRichTextString("Período"));
		cell7.setCellStyle(styleHeader);*/

		HSSFCell cell8 = row.createCell(rowCount++);
		cell8.setCellValue(new HSSFRichTextString("Fecha del egreso"));
		cell8.setCellStyle(styleHeader);

		HSSFCell cell9 = row.createCell(rowCount++);
		cell9.setCellValue(new HSSFRichTextString("OP"));
		cell9.setCellStyle(styleHeader);

		HSSFCell cellFechaAplicacion = row.createCell(rowCount++);
		cellFechaAplicacion.setCellValue(new HSSFRichTextString(
				"Fecha Aplicación"));
		cellFechaAplicacion.setCellStyle(styleHeader);

		HSSFCell cellOPAplicacion = row.createCell(rowCount++);
		cellOPAplicacion.setCellValue(new HSSFRichTextString("OP/Rec.Aplicación"));
		cellOPAplicacion.setCellStyle(styleHeader);

		HSSFCell importeAplicado = row.createCell(rowCount++);
		importeAplicado
				.setCellValue(new HSSFRichTextString("Importe Aplicado"));
		importeAplicado.setCellStyle(styleHeader);

		if (entidad == WebKeysGlobal.UOMA) {
			HSSFCell nroCuota = row.createCell(rowCount++);
			nroCuota.setCellValue(new HSSFRichTextString("Nro.Cuota"));
			nroCuota.setCellStyle(styleHeader);

			HSSFCell cantCuotas = row.createCell(rowCount++);
			cantCuotas.setCellValue(new HSSFRichTextString("Cant.Cuotas"));
			cantCuotas.setCellStyle(styleHeader);

			HSSFCell valorCuotas = row.createCell(rowCount++);
			valorCuotas.setCellValue(new HSSFRichTextString("Valor Cuotas"));
			valorCuotas.setCellStyle(styleHeader);

			HSSFCell saldo = row.createCell(rowCount++);
			saldo.setCellValue(new HSSFRichTextString("Saldo"));
			saldo.setCellStyle(styleHeader);
		}

		if (entidad == WebKeysGlobal.AMTIMA) {
			HSSFCell saldo = row.createCell(rowCount++);
			saldo.setCellValue(new HSSFRichTextString("Saldo"));
			saldo.setCellStyle(styleHeader);
		}

		
		//wb.setRepeatingRowsAndColumns(0, 0, rowCount, 1, 1);
		
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
	}

	public static class ItemAnticipoOP {
		private Integer op;
		private Date fechaPago;
		private String descripcion;
		private BigDecimal importe;
		private Empresa empresa;
		private Seccional seccional;
		private Date fecha;
		private Date periodoPrestacion;
		private boolean debitoParaEgreso;
		private String opAplicacion;
		private Date fechaPagoAplicacion;
		private BigDecimal importeAplicado;
		private BigDecimal valorCuota;
		private int cantCuotas;
		private int nroCuota;
		private BigDecimal saldo;

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public Empresa getEmpresa() {
			return empresa;
		}

		public void setEmpresa(Empresa empresa) {
			this.empresa = empresa;
		}

		public Seccional getSeccional() {
			return seccional;
		}

		public void setSeccional(Seccional seccional) {
			this.seccional = seccional;
		}

		public static ItemAnticipoOP getMapping(ResultSet rs)
				throws SQLException {
			return getMapping(rs, "");
		}

		public static ItemAnticipoOP getMapping(ResultSet rs, String prefix)
				throws SQLException {
			ItemAnticipoOP cta = new ItemAnticipoOP();
			cta.setOp(rs.getInt("op"));
			cta.setFechaPago(rs.getDate("fecha_pago"));

			String sucu = rs.getString(prefix + "sucu_acreedor");
			int id_seccional = rs.getInt(prefix + "id_seccional");
			if (id_seccional != 0) {
				sucu = String.valueOf(id_seccional);
			}
			String seccional = rs.getString(prefix + "seccional");
			if (StringUtils.checkEmpty(seccional)) {
				seccional = "";
			}
			cta.setEmpresa(new Empresa(rs.getString(prefix + "cuit_acreedor"),
					sucu, rs.getString(prefix + "razon_soc") + " " + seccional));
			cta.setImporte(rs.getBigDecimal(prefix + "total"));

			cta.setFecha(rs.getDate("fecha_recepcion"));
			cta.setDescripcion(rs.getString(prefix + "descripcion"));
			cta.setPeriodoPrestacion(rs.getDate(prefix + "periodo_prestacion"));
			cta.setDebitoParaEgreso(rs.getBoolean("debito_para_egreso"));

			cta.setOpAplicacion(rs.getString("op_aplicacion"));
			cta.setFechaPagoAplicacion(rs.getDate("fecha_pago_aplicacion"));
			cta.setImporteAplicado(rs.getBigDecimal("importe_aplicado"));

			return cta;
		}

		public void setPeriodoPrestacion(Date periodoPrestacion) {
			this.periodoPrestacion = periodoPrestacion;
		}

		public Date getPeriodoPrestacion() {
			return periodoPrestacion;
		}

		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

		public Date getFecha() {
			return fecha;
		}

		public void setDebitoParaEgreso(boolean debitoParaEgreso) {
			this.debitoParaEgreso = debitoParaEgreso;
		}

		public boolean isDebitoParaEgreso() {
			return debitoParaEgreso;
		}

		public Integer getOp() {
			return op;
		}

		public void setOp(Integer op) {
			this.op = op;
		}

		public Date getFechaPago() {
			return fechaPago;
		}

		public void setFechaPago(Date fechaPago) {
			this.fechaPago = fechaPago;
		}

		public String getOpAplicacion() {
			return opAplicacion;
		}

		public void setOpAplicacion(String opAplicacion) {
			this.opAplicacion = opAplicacion;
		}

		public Date getFechaPagoAplicacion() {
			return fechaPagoAplicacion;
		}

		public void setFechaPagoAplicacion(Date fechaPagoAplicacion) {
			this.fechaPagoAplicacion = fechaPagoAplicacion;
		}

		public BigDecimal getImporteAplicado() {
			return importeAplicado;
		}

		public void setImporteAplicado(BigDecimal importeAplicado) {
			this.importeAplicado = importeAplicado;
		}

		public BigDecimal getValorCuota() {
			return valorCuota;
		}

		public void setValorCuota(BigDecimal valorCuota) {
			this.valorCuota = valorCuota;
		}

		public int getCantCuotas() {
			return cantCuotas;
		}

		public void setCantCuotas(int cantCuotas) {
			this.cantCuotas = cantCuotas;
		}

		public int getNroCuota() {
			return nroCuota;
		}

		public void setNroCuota(int nroCuota) {
			this.nroCuota = nroCuota;
		}

		public BigDecimal getSaldo() {
			return saldo;
		}

		public void setSaldo(BigDecimal saldo) {
			this.saldo = saldo;
		}

	}
}

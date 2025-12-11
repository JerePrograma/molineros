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
 import org.apache.poi.ss.util.CellRangeAddress;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteEstadoComprobantesExcel extends ReporteXLS {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteEstadoComprobantesExcel.class);

	public static HSSFWorkbook generaReporteEstadoComprobantes(
			HttpServletRequest req, HttpServletResponse res) {

		boolean incluirReintegros = ParamUtil.getBoolean(req,
				"incluir_reintegros");
		boolean incluirLiquidaciones = ParamUtil.getBoolean(req,
				"incluir_liquidaciones");
		boolean incluirProveedores = ParamUtil.getBoolean(req,
				"incluir_proveedores");

		int entidad = ParamUtil.getInteger(req, "entidad");

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
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
		
		int fechaEmiDesdeDia = ParamUtil
				.getInteger(req, "fechaEmiDesdeDia");
		int fechaEmiDesdeMes = ParamUtil
				.getInteger(req, "fechaEmiDesdeMes");		
		int fechaEmiDesdeAnio = ParamUtil.getInteger(req,
				"fechaEmiDesdeAnio");
		
		int fechaEmiHastaDia = ParamUtil
				.getInteger(req, "fechaEmiHastaDia");
		int fechaEmiHastaMes = ParamUtil
				.getInteger(req, "fechaEmiHastaMes");		
		int fechaEmiHastaAnio = ParamUtil.getInteger(req,
				"fechaEmiHastaAnio");
		
		Calendar fechaEmiDesdeCalendar=null;
		Calendar fechaEmiHastaCalendar=null;
		
		if(entidad!=WebKeysGlobal.OSPIM && fechaEmiDesdeAnio>0){
			fechaEmiDesdeCalendar=Calendar.getInstance();			
			fechaEmiDesdeCalendar.set(Calendar.DATE, fechaEmiDesdeDia>0?fechaEmiDesdeDia:1);
			fechaEmiDesdeCalendar.set(Calendar.MONTH, fechaEmiDesdeMes>0?fechaEmiDesdeMes:0);
			fechaEmiDesdeCalendar.set(Calendar.YEAR, fechaEmiDesdeAnio);
		}
		if(entidad!=WebKeysGlobal.OSPIM && fechaEmiHastaAnio>0){
			fechaEmiHastaCalendar=Calendar.getInstance();
			fechaEmiHastaCalendar.set(Calendar.DATE, fechaEmiHastaDia>0?fechaEmiHastaDia:1);
			fechaEmiHastaCalendar.set(Calendar.MONTH, fechaEmiHastaMes>0?fechaEmiHastaMes:0);
			fechaEmiHastaCalendar.set(Calendar.YEAR, fechaEmiHastaAnio);
		}
		
		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);

		if (seccional != 0) {
			sucu = "000";
		}

		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);

			Date fechaPagoFin = format.parse(fechaPagoHastaDia + "-"
					+ fechaPagoHastaMes + "-" + fechaPagoHastaAnio);

			boolean soloConSaldo = ParamUtil.getBoolean(req, "soloConSaldo");

			List<EstadoComprobante> libro = ContabilidadServiceUtil
					.listadoEstadoComprobantes(fechaIni, fechaFin,
							fechaPagoFin, cuit, sucu, seccional, soloConSaldo,
							incluirProveedores, incluirLiquidaciones,
							incluirReintegros, null!=fechaEmiDesdeCalendar?fechaEmiDesdeCalendar.getTime():null, 
									null!=fechaEmiHastaCalendar?fechaEmiHastaCalendar.getTime():null, entidad);

			return generarReporte(fechaIni, fechaFin, libro, fechaPagoFin,
					soloConSaldo, cuit, entidad);
		} catch (Exception e) {
			_log.error("Error al generar listado estado comprobantes", e);
			return null;
		}
	}

	private static HSSFWorkbook generarReporte(Date fechaIni, Date fechaFin,
			List<EstadoComprobante> libro, Date fechaHasta,
			boolean soloConSaldo, String cuit, int entidad) {
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
		StringBuffer sb = new StringBuffer(
				"Listado Estado de comprobantes - Desde: ");
		sb.append(DateUtils.format(fechaIni, DateUtils.SHORT));
		sb.append(" Hasta: ");
		sb.append(DateUtils.format(fechaFin, DateUtils.SHORT));
		sb.append(" - Considerar pagos hasta: ");
		sb.append(DateUtils.format(fechaHasta, DateUtils.SHORT));
		if (soloConSaldo) {
			sb.append(" - Solo comprobantes con Saldo");
		}
		if (null != cuit && !cuit.trim().equals("")) {
			sb.append(" - CUIT: " + cuit);
		}
		cellTitulo.setCellValue(new HSSFRichTextString(sb.toString()));
		cellTitulo.setCellStyle(styleHeader);

		createHeader(wb, sheet, styleHeader, entidad);

		int i = 2;
		int col = 0;
		for (EstadoComprobante l : libro) {
			col = 0;
			BigDecimal saldo = BigDecimal.ZERO;
			HSSFRow row = sheet.createRow(i);

			HSSFCell cell0 = row.createCell(col++);
			cell0.setCellValue(new HSSFRichTextString(l.getEmpresa().getCuit()));
			cell0.setCellStyle(styleAll);

			if (entidad != WebKeysGlobal.OSPIM) {
				HSSFCell cell00 = row.createCell(col++);
				cell00.setCellValue(new HSSFRichTextString(l.getEmpresa()
						.getSucursal()));
				cell00.setCellStyle(styleAll);
			}

			HSSFCell cell1 = row.createCell(col++);
			cell1.setCellValue(new HSSFRichTextString(l.getEmpresa()
					.getRazon_soc()));
			cell1.setCellStyle(styleAll);

			if (entidad != WebKeysGlobal.OSPIM) {
				HSSFCell cell11 = row.createCell(col++);
				cell11.setCellValue(new HSSFRichTextString(l.getEmpresaCompro()
						.getCuit()));
				cell11.setCellStyle(styleAll);

				HSSFCell cell12 = row.createCell(col++);
				cell12.setCellValue(new HSSFRichTextString(l.getEmpresaCompro()
						.getRazon_soc()));
				cell12.setCellStyle(styleAll);

			}

			HSSFCell cell2 = row.createCell(col++);
			cell2.setCellValue(l.getFecha());
			cell2.setCellStyle(styleDateWithBorder);

			HSSFCell cell3 = row.createCell(col++);
			if (entidad == WebKeysGlobal.OSPIM) {
				if (l.getPeriodoPrestacion() != null) {
					cell3.setCellValue(l.getPeriodoPrestacion());
				}
			} else {
				if (l.getFecha_emision() != null) {
					cell3.setCellValue(l.getFecha_emision());
				}
			}
			cell3.setCellStyle(styleDateWithBorder);

			HSSFCell cell4 = row.createCell(col++);
			cell4.setCellValue(new HSSFRichTextString(l.getDescripcion()));
			cell4.setCellStyle(styleAll);

			BigDecimal importeDebe = null;
			BigDecimal importeHaber = null;
			if (!l.isDebitoParaEgreso()) {
				importeDebe = l.getImportePagado();
				importeHaber = l.getImporte();
			} else {
				importeDebe = l.getImporte();
				importeHaber = l.getImportePagado();
			}
			HSSFCell cell5 = row.createCell(col++);
			cell5.setCellValue(importeDebe.doubleValue());
			cell5.setCellStyle(styleMoney);

			HSSFCell cell6 = row.createCell(col++);
			cell6.setCellValue(importeHaber.doubleValue());
			cell6.setCellStyle(styleMoney);

			HSSFCell cell7 = row.createCell(col++);
			saldo = saldo.add(importeDebe.subtract(importeHaber));
			cell7.setCellValue(saldo.doubleValue());
			cell7.setCellStyle(styleMoney);

			if (entidad == WebKeysGlobal.UOMA) {
				HSSFCell cell8 = row.createCell(col++);
				cell8.setCellValue(new HSSFRichTextString(l.getObservaciones()));
				cell8.setCellStyle(styleAll);

				HSSFCell cell9 = row.createCell(col++);
				cell9.setCellValue(new HSSFRichTextString(l.getConcepto()));
				cell9.setCellStyle(styleAll);

				HSSFCell cell10 = row.createCell(col++);
				cell10.setCellValue(l.getImporteConcepto().doubleValue());
				cell10.setCellStyle(styleMoney);

				HSSFCell cell11 = row.createCell(col++);
				cell11.setCellValue(l.getIdOrdenPago());
				cell11.setCellStyle(styleAll);

				HSSFCell cell12 = row.createCell(col);
				if (null != l.getFechaOP()) {
					cell12.setCellValue(l.getFechaOP());
				}
				cell12.setCellStyle(styleDateWithBorder);
			}

			i++;
		}

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, col));

		for (int j = 0; j < col; j++) {
			sheet.autoSizeColumn((short) j);
		}

		sheet.setColumnWidth(2, 10360);
		return wb;
	}

	private static void createHeader(HSSFWorkbook wb, HSSFSheet sheet,
			HSSFCellStyle styleHeader, int entidad) {
		int col = 0;
		HSSFRow row = sheet.createRow(1);
		HSSFCell cell = row.createCell(col++);
		cell.setCellValue(new HSSFRichTextString("Cuit Acreedor"));
		cell.setCellStyle(styleHeader);

		if (entidad != WebKeysGlobal.OSPIM) {
			HSSFCell cell0 = row.createCell(col++);
			cell0.setCellValue(new HSSFRichTextString("Suc. Ac."));
			cell0.setCellStyle(styleHeader);
		}

		HSSFCell cell1 = row.createCell(col++);
		cell1.setCellValue(new HSSFRichTextString("Razon Social Ac."));
		cell1.setCellStyle(styleHeader);

		if (entidad != WebKeysGlobal.OSPIM) {
			HSSFCell cell00 = row.createCell(col++);
			cell00.setCellValue(new HSSFRichTextString("Cuit Compro"));
			cell00.setCellStyle(styleHeader);

			HSSFCell cell02 = row.createCell(col++);
			cell02.setCellValue(new HSSFRichTextString("Razon Social Compro."));
			cell02.setCellStyle(styleHeader);
		}

		HSSFCell cell2 = row.createCell(col++);
		cell2.setCellValue(new HSSFRichTextString("Recepcion"));
		cell2.setCellStyle(styleHeader);

		HSSFCell cell3 = row.createCell(col++);

		if (entidad != WebKeysGlobal.OSPIM) {
			cell3.setCellValue(new HSSFRichTextString("Fecha Emisión"));
		} else {
			cell3.setCellValue(new HSSFRichTextString("Período"));
		}
		cell3.setCellStyle(styleHeader);

		HSSFCell cell4 = row.createCell(col++);
		cell4.setCellValue(new HSSFRichTextString("Comprobante"));
		cell4.setCellStyle(styleHeader);

		HSSFCell cell5 = row.createCell(col++);
		cell5.setCellValue(new HSSFRichTextString("Debe"));
		cell5.setCellStyle(styleHeader);

		HSSFCell cell6 = row.createCell(col++);
		cell6.setCellValue(new HSSFRichTextString("Haber"));
		cell6.setCellStyle(styleHeader);

		HSSFCell cell7 = row.createCell(col++);
		cell7.setCellValue(new HSSFRichTextString("Saldo"));
		cell7.setCellStyle(styleHeader);
		if (entidad == WebKeysGlobal.UOMA) {
			HSSFCell cell71 = row.createCell(col++);
			cell71.setCellValue(new HSSFRichTextString("Observaciones"));
			cell71.setCellStyle(styleHeader);

			HSSFCell cell8 = row.createCell(col++);
			cell8.setCellValue(new HSSFRichTextString("Concepto"));
			cell8.setCellStyle(styleHeader);

			HSSFCell cell9 = row.createCell(col++);
			cell9.setCellValue(new HSSFRichTextString("Importe Conc."));
			cell9.setCellStyle(styleHeader);

			HSSFCell cell10 = row.createCell(col++);
			cell10.setCellValue(new HSSFRichTextString("Id Orden Pago"));
			cell10.setCellStyle(styleHeader);

			HSSFCell cell11 = row.createCell(col++);
			cell11.setCellValue(new HSSFRichTextString("Fecha OP"));
			cell11.setCellStyle(styleHeader);
		}

		//wb.setRepeatingRowsAndColumns(0, 0, col, 1, 1);
		for(int j=0;j<24;j++){
		     sheet.autoSizeColumn((short) j);
		}
		
	}

	public static class EstadoComprobante {
		private String descripcion;
		private BigDecimal importe;
		private BigDecimal importePagado;
		private Empresa empresa;
		private Empresa empresaCompro;
		private Seccional seccional;
		private boolean pagado;
		private Date fecha;
		private Date fecha_emision;
		private Date periodoPrestacion;
		private boolean debitoParaEgreso;
		private String observaciones;
		private String concepto;
		private BigDecimal importeConcepto;
		private int idOrdenPago;
		private Date fechaOP;

		public Date getFecha_emision() {
			return fecha_emision;
		}

		public void setFecha_emision(Date fecha_emision) {
			this.fecha_emision = fecha_emision;
		}

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

		public boolean isPagado() {
			return pagado;
		}

		public void setPagado(boolean pagado) {
			this.pagado = pagado;
		}

		public String getObservaciones() {
			return observaciones;
		}

		public void setObservaciones(String observaciones) {
			this.observaciones = observaciones;
		}

		public String getConcepto() {
			return concepto;
		}

		public void setConcepto(String concepto) {
			this.concepto = concepto;
		}

		public BigDecimal getImporteConcepto() {
			return importeConcepto;
		}

		public void setImporteConcepto(BigDecimal importeConcepto) {
			this.importeConcepto = importeConcepto;
		}

		public int getIdOrdenPago() {
			return idOrdenPago;
		}

		public void setIdOrdenPago(int idOrdenPago) {
			this.idOrdenPago = idOrdenPago;
		}

		public Date getFechaOP() {
			return fechaOP;
		}

		public void setFechaOP(Date fechaOP) {
			this.fechaOP = fechaOP;
		}

		public static EstadoComprobante getMapping(ResultSet rs, int entidad)
				throws SQLException {
			return getMapping(rs, "", entidad);
		}

		public static EstadoComprobante getMapping(ResultSet rs, String prefix,
				int entidad) throws SQLException {
			EstadoComprobante cta = new EstadoComprobante();
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
			cta.setPagado(rs.getBoolean(prefix + "pagado"));
			if (cta.isPagado()) {
				cta.setImportePagado(cta.getImporte());
			} else {
				cta.setImportePagado(BigDecimal.ZERO);
			}
			if (entidad == WebKeysGlobal.OSPIM) {
				cta.setPeriodoPrestacion(rs.getDate(prefix
						+ "periodo_prestacion"));
			}
			cta.setDebitoParaEgreso(rs.getBoolean("debito_para_egreso"));

			if (entidad != WebKeysGlobal.OSPIM) {
				cta.setEmpresaCompro(new Empresa(rs.getString("cuit_compro"),
						"000", rs.getString("razon_soc_compro")));
				cta.setFecha_emision(rs.getDate("fecha_emision"));
			}

			if (entidad == WebKeysGlobal.UOMA) {
				cta.setObservaciones(rs.getString("observaciones"));
				cta.setConcepto(rs.getString("concepto"));
				cta.setImporteConcepto(rs.getBigDecimal("importe_concepto"));
				cta.setIdOrdenPago(rs.getInt("id_orden_pago"));
				cta.setFechaOP(rs.getDate("fecha_op"));
			}

			return cta;
		}

		public Empresa getEmpresaCompro() {
			return empresaCompro;
		}

		public void setEmpresaCompro(Empresa empresaCompro) {
			this.empresaCompro = empresaCompro;
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

		public void setImportePagado(BigDecimal importePagado) {
			this.importePagado = importePagado;
		}

		public BigDecimal getImportePagado() {
			return importePagado;
		}

		public void setDebitoParaEgreso(boolean debitoParaEgreso) {
			this.debitoParaEgreso = debitoParaEgreso;
		}

		public boolean isDebitoParaEgreso() {
			return debitoParaEgreso;
		}
	}
}

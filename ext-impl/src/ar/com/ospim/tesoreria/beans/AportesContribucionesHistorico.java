package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

public class AportesContribucionesHistorico {

	private Date fechaDesde;
	private Date fechaHasta;
	private BigDecimal topeRemuneracion;
	private BigDecimal topeAporte;
	private BigDecimal aporteReal;
	private Date altaFecha;
	private String altaUsr;

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public BigDecimal getTopeRemuneracion() {
		return topeRemuneracion;
	}

	public void setTopeRemuneracion(BigDecimal topeRemuneracion) {
		this.topeRemuneracion = topeRemuneracion;
	}

	public BigDecimal getTopeAporte() {
		return topeAporte;
	}

	public void setTopeAporte(BigDecimal topeAporte) {
		this.topeAporte = topeAporte;
	}

	public BigDecimal getAporteReal() {
		return aporteReal;
	}

	public void setAporteReal(BigDecimal aporteReal) {
		this.aporteReal = aporteReal;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public static AportesContribucionesHistorico getMapping(ResultSet rs) throws Exception {

		AportesContribucionesHistorico h = new AportesContribucionesHistorico();

		h.setFechaDesde(rs.getDate("fecha_desde"));
		h.setFechaHasta(rs.getDate("fecha_hasta"));
		h.setTopeRemuneracion(rs.getBigDecimal("tope_remuneracion"));
		h.setTopeAporte(rs.getBigDecimal("tope_aporte"));
		h.setAporteReal(rs.getBigDecimal("aporte_real"));
		h.setAltaFecha(rs.getTimestamp("alta_fecha"));
		h.setAltaUsr(rs.getString("alta_usr"));

		return h;
	}
}
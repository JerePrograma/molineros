package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

public class AportesContribuciones {

	private Date fechaDesde;
	private Date fechaHasta;
	private BigDecimal topeRemuneracion;
	private BigDecimal topeAporte;
	private BigDecimal aporteReal;


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


	public static AportesContribuciones getMapping(ResultSet rs) throws Exception {

		AportesContribuciones tope = new AportesContribuciones();

		tope.setFechaDesde(rs.getDate("fecha_desde"));
		tope.setFechaHasta(rs.getDate("fecha_hasta"));
		tope.setTopeRemuneracion(rs.getBigDecimal("tope_remuneracion"));
		tope.setTopeAporte(rs.getBigDecimal("tope_aporte"));
		tope.setAporteReal(rs.getBigDecimal("aporte_real"));

		return tope;
	}
}
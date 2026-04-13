package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.util.DateUtils;

public class ReporteAportesPagoRamoBean {
	private Date periodo;
	private BigDecimal calculado10;
	private BigDecimal pagado10;
	private BigDecimal calculado50;
	private BigDecimal pagado50;
	private BigDecimal calculado99;
	private BigDecimal pagado99;
	private BigDecimal montribPagado;
	
	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public BigDecimal getCalculado10() {
		return calculado10;
	}

	public void setCalculado10(BigDecimal calculado10) {
		this.calculado10 = calculado10;
	}

	public BigDecimal getPagado10() {
		return pagado10;
	}

	public void setPagado10(BigDecimal pagado10) {
		this.pagado10 = pagado10;
	}

	public BigDecimal getCalculado50() {
		return calculado50;
	}

	public void setCalculado50(BigDecimal calculado50) {
		this.calculado50 = calculado50;
	}

	public BigDecimal getPagado50() {
		return pagado50;
	}

	public void setPagado50(BigDecimal pagado50) {
		this.pagado50 = pagado50;
	}

	public BigDecimal getCalculado99() {
		return calculado99;
	}

	public void setCalculado99(BigDecimal calculado99) {
		this.calculado99 = calculado99;
	}

	public BigDecimal getPagado99() {
		return pagado99;
	}

	public void setPagado99(BigDecimal pagado99) {
		this.pagado99 = pagado99;
	}

	public BigDecimal getMontribPagado() {
		return montribPagado;
	}

	public void setMontribPagado(BigDecimal montribPagado) {
		this.montribPagado = montribPagado;
	}

	public String getPeriodoAsString() {
		return null != periodo ? DateUtils.format(periodo,
				DateUtils.SHORT) : "";
	}
	
	public static ReporteAportesPagoRamoBean getMapping(ResultSet rs) throws SQLException {
		ReporteAportesPagoRamoBean a = new ReporteAportesPagoRamoBean();
        a.setCalculado10(rs.getBigDecimal("calculado_10"));
        a.setCalculado50(rs.getBigDecimal("calculado_50"));
        a.setCalculado99(rs.getBigDecimal("calculado_99"));
        a.setMontribPagado(rs.getBigDecimal("montrib_pagado"));
        a.setPagado10(rs.getBigDecimal("pagado_10"));
        a.setPagado50(rs.getBigDecimal("pagado_50"));
        a.setPagado99(rs.getBigDecimal("pagado_99"));
        a.setPeriodo(rs.getDate("periodo"));
        return a;
	}

}

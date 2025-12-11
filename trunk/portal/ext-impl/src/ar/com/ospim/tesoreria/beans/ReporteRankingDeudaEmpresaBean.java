package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.util.DateUtils;

public class ReporteRankingDeudaEmpresaBean {
	private String cuit;
	private String tercerizadora;
	private BigDecimal sum;
	private String razonSocial;
	private Integer ramoEmpresaId;
	private String ramoEmpresaDesc;
	private Integer actaId;
	private String numero;
	private Date maxPeriodo;
	private Date minPeriodo;
	private BigDecimal totalActa;
	private BigDecimal totalPagado;
	
	private BigDecimal total_calculo_deuda;
	private Date max_periodo_cal_deuda;
	private Date min_periodo_cal_deuda;
	
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public String getTercerizadora() {
		return tercerizadora;
	}
	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}
	public BigDecimal getSum() {
		return sum;
	}
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public Integer getRamoEmpresaId() {
		return ramoEmpresaId;
	}
	public void setRamoEmpresaId(Integer ramoEmpresaId) {
		this.ramoEmpresaId = ramoEmpresaId;
	}
	public String getRamoEmpresaDesc() {
		return ramoEmpresaDesc;
	}
	public void setRamoEmpresaDesc(String ramoEmpresaDesc) {
		this.ramoEmpresaDesc = ramoEmpresaDesc;
	}
	public Integer getActaId() {
		return actaId;
	}
	public void setActaId(Integer actaId) {
		this.actaId = actaId;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getMaxPeriodo() {
		return maxPeriodo;
	}
	public void setMaxPeriodo(Date maxPeriodo) {
		this.maxPeriodo = maxPeriodo;
	}
	
	public String getMaxPeriodoAsString() {
		return null != maxPeriodo ? DateUtils.format(maxPeriodo,
				DateUtils.SHORT) : "";
	}

	
	public Date getMinPeriodo() {
		return minPeriodo;
	}
	public void setMinPeriodo(Date minPeriodo) {
		this.minPeriodo = minPeriodo;
	}
	
	public String getMinPeriodoAsString() {
		return null != minPeriodo ? DateUtils.format(minPeriodo,
				DateUtils.SHORT) : "";
	}
	
	public BigDecimal getTotalActa() {
		return totalActa;
	}
	public void setTotalActa(BigDecimal totalActa) {
		this.totalActa = totalActa;
	}
	public BigDecimal getTotalPagado() {
		return totalPagado;
	}
	public void setTotalPagado(BigDecimal totalPagado) {
		this.totalPagado = totalPagado;
	}
	
	
	
	public BigDecimal getTotal_calculo_deuda() {
		return total_calculo_deuda;
	}
	public void setTotal_calculo_deuda(BigDecimal total_calculo_deuda) {
		this.total_calculo_deuda = total_calculo_deuda;
	}
	public Date getMax_periodo_cal_deuda() {
		return max_periodo_cal_deuda;
	}
	public void setMax_periodo_cal_deuda(Date max_periodo_cal_deuda) {
		this.max_periodo_cal_deuda = max_periodo_cal_deuda;
	}
	public Date getMin_periodo_cal_deuda() {
		return min_periodo_cal_deuda;
	}
	public void setMin_periodo_cal_deuda(Date min_periodo_cal_deuda) {
		this.min_periodo_cal_deuda = min_periodo_cal_deuda;
	}
	public static ReporteRankingDeudaEmpresaBean getMapping(ResultSet rs) throws SQLException {
		ReporteRankingDeudaEmpresaBean a = new ReporteRankingDeudaEmpresaBean();
		
		a.setActaId(rs.getInt("id_acta"));
		a.setCuit(rs.getString("cuit_contribuyente"));
		a.setMaxPeriodo(rs.getDate("max_periodo"));
		a.setMinPeriodo(rs.getDate("min_periodo"));
		a.setNumero(rs.getString("numero"));
		a.setRamoEmpresaDesc(rs.getString("ramo"));
		a.setRamoEmpresaId(rs.getInt("id_ramo_empresa"));
		a.setRazonSocial(rs.getString("razon_soc"));
		a.setSum(rs.getBigDecimal("sum"));
		a.setTercerizadora(rs.getString("tercerizadora"));
		a.setTotalActa(rs.getBigDecimal("total_acta"));
		a.setTotalPagado(rs.getBigDecimal("total_pagado"));
		a.setTotal_calculo_deuda(rs.getBigDecimal("total_calculo_deuda"));
		a.setMax_periodo_cal_deuda(rs.getDate("max_periodo_cal_deuda"));
		a.setMin_periodo_cal_deuda( rs.getDate("min_periodo_cal_deuda"));
		
		return a;
	}

}

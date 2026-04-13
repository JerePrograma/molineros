package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.util.DateUtils;

public class ReporteIngresosDevengadosBean {
	private String tercerizadora;
	private Long sinRemuneracion;
	private Long cantidadRemuneracionPeriodo;
	private Double totalRemenueracionPeriodo;
	private Long cantidadRemuneracionPeriodoAnterior;
	private Double totalRemuneracionPeriodoAnterior;
	private Long cantidadRemuneracionPeriodoNoPadron;
	private Double totalRemuneracionPeriodoNoPadron;
	private Double aportes;
	private Double contribuciones;
	private Double totalAportesContrib;
	private Long efectoresCantidad;
	private Long servicioDomesticoCantidad;
	private Long monotributistasCantidad;
	
	public String getTercerizadora() {
		return tercerizadora;
	}
	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}
	public Long getSinRemuneracion() {
		return sinRemuneracion;
	}
	public void setSinRemuneracion(Long sinRemuneracion) {
		this.sinRemuneracion = sinRemuneracion;
	}
	public Long getCantidadRemuneracionPeriodo() {
		return cantidadRemuneracionPeriodo;
	}
	public void setCantidadRemuneracionPeriodo(Long cantidadRemuneracionPeriodo) {
		this.cantidadRemuneracionPeriodo = cantidadRemuneracionPeriodo;
	}
	public Double getTotalRemenueracionPeriodo() {
		return totalRemenueracionPeriodo;
	}
	public void setTotalRemenueracionPeriodo(Double totalRemenueracionPeriodo) {
		this.totalRemenueracionPeriodo = totalRemenueracionPeriodo;
	}
	public Long getCantidadRemuneracionPeriodoAnterior() {
		return cantidadRemuneracionPeriodoAnterior;
	}
	public void setCantidadRemuneracionPeriodoAnterior(
			Long cantidadRemuneracionPeriodoAnterior) {
		this.cantidadRemuneracionPeriodoAnterior = cantidadRemuneracionPeriodoAnterior;
	}
	public Double getTotalRemuneracionPeriodoAnterior() {
		return totalRemuneracionPeriodoAnterior;
	}
	public void setTotalRemuneracionPeriodoAnterior(
			Double totalRemuneracionPeriodoAnterior) {
		this.totalRemuneracionPeriodoAnterior = totalRemuneracionPeriodoAnterior;
	}
	public Long getCantidadRemuneracionPeriodoNoPadron() {
		return cantidadRemuneracionPeriodoNoPadron;
	}
	public void setCantidadRemuneracionPeriodoNoPadron(
			Long cantidadRemuneracionPeriodoNoPadron) {
		this.cantidadRemuneracionPeriodoNoPadron = cantidadRemuneracionPeriodoNoPadron;
	}
	public Double getTotalRemuneracionPeriodoNoPadron() {
		return totalRemuneracionPeriodoNoPadron;
	}
	public void setTotalRemuneracionPeriodoNoPadron(
			Double totalRemuneracionPeriodoNoPadron) {
		this.totalRemuneracionPeriodoNoPadron = totalRemuneracionPeriodoNoPadron;
	}
	public Double getAportes() {
		return aportes;
	}
	public void setAportes(Double aportes) {
		this.aportes = aportes;
	}
	public Double getContribuciones() {
		return contribuciones;
	}
	public void setContribuciones(Double contribuciones) {
		this.contribuciones = contribuciones;
	}
	public Double getTotalAportesContrib() {
		return totalAportesContrib;
	}
	public void setTotalAportesContrib(Double totalAportesContrib) {
		this.totalAportesContrib = totalAportesContrib;
	}
	
	public Long getEfectoresCantidad() {
		return efectoresCantidad;
	}
	public void setEfectoresCantidad(Long efectoresCantidad) {
		this.efectoresCantidad = efectoresCantidad;
	}
	public Long getServicioDomesticoCantidad() {
		return servicioDomesticoCantidad;
	}
	public void setServicioDomesticoCantidad(Long servicioDomesticoCantidad) {
		this.servicioDomesticoCantidad = servicioDomesticoCantidad;
	}
	public Long getMonotributistasCantidad() {
		return monotributistasCantidad;
	}
	public void setMonotributistasCantidad(Long monotributistasCantidad) {
		this.monotributistasCantidad = monotributistasCantidad;
	}
	public static ReporteIngresosDevengadosBean getMapping(ResultSet rs) throws SQLException {
			ReporteIngresosDevengadosBean a = new ReporteIngresosDevengadosBean();
		
        a.setAportes(rs.getDouble("aportes"));
        a.setCantidadRemuneracionPeriodo(rs.getLong("cantidad_remuneracion_periodo"));
        a.setCantidadRemuneracionPeriodoAnterior(rs.getLong("cantidad_remuneracion_periodo_anterior"));
        a.setCantidadRemuneracionPeriodoNoPadron(rs.getLong("cantidad_remuneracion_periodo_no_padron"));
        a.setContribuciones(rs.getDouble("contribuciones"));
        a.setSinRemuneracion(rs.getLong("sin_remuneracion"));
        a.setTercerizadora(rs.getString("tercerizadora"));
        a.setTotalAportesContrib(rs.getDouble("total_aportes_contrib"));
        a.setTotalRemenueracionPeriodo(rs.getDouble("total_remuneracion_periodo"));
        a.setTotalRemuneracionPeriodoAnterior(rs.getDouble("total_remuneracion_periodo_anterior"));
        a.setTotalRemuneracionPeriodoNoPadron(rs.getDouble("total_remuneracion_periodo_no_padron"));
	    a.setEfectoresCantidad(rs.getLong("efectores"));
	    a.setServicioDomesticoCantidad(rs.getLong("servicio_domestico"));
	    a.setMonotributistasCantidad(rs.getLong("monotributistas"));
	    
        
		return a;
	}

}

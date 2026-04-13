package ar.com.ospim.afip.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;


public class ArchivoSubidoAfip {
	private String tipo;
	private Date fechaProceso;
	private int cantReg;
	private BigDecimal importeTotal;
	
	public ArchivoSubidoAfip(){};
	
	public static ArchivoSubidoAfip getMapping(ResultSet rs) throws SQLException {
		ArchivoSubidoAfip archivo = new ArchivoSubidoAfip();
		archivo.setTipo(rs.getString("tipo"));
		archivo.setFechaProceso(rs.getDate("fecha_proceso"));
		archivo.setCantReg(rs.getInt("cant_reg"));
		archivo.setImporteTotal(rs.getBigDecimal("importe_total"));
		return archivo;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Date getFechaProceso() {
		return fechaProceso;
	}
	
	public String getFechaProcesoAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fechaProceso);
	}
	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}
	public int getCantReg() {
		return cantReg;
	}
	public void setCantReg(int cantReg) {
		this.cantReg = cantReg;
	}
	public BigDecimal getImporteTotal() {
		return importeTotal;
	}
	public String getImporteTotalAsString(){
		DecimalFormat nf=new DecimalFormat("#,##0.00");
		return importeTotal!=null?nf.format(importeTotal):"****";
	}
	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}
	
		
}

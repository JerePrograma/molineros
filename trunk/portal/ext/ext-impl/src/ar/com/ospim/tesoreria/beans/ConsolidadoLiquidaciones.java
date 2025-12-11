package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsolidadoLiquidaciones {
	
	private String tercerizadora;
	private String idTercerizadora;
	private int cantRegistros;
	private Date fechaLiq;
	private BigDecimal importeTotal;
	private BigDecimal importeDerivar;
		
	
	public static ConsolidadoLiquidaciones getMapping(ResultSet rs) throws SQLException {
		ConsolidadoLiquidaciones liquidacion = new ConsolidadoLiquidaciones();
		liquidacion.setFechaLiq(rs.getDate("fecha"));
		liquidacion.setImporteTotal(rs.getBigDecimal("importe"));		
		return liquidacion;
	}
	
	public static ConsolidadoLiquidaciones getMappingDesregulados(ResultSet rs) throws SQLException {
		ConsolidadoLiquidaciones liquidacion = new ConsolidadoLiquidaciones();
		liquidacion.setTercerizadora(rs.getString("tercerizadora"));
		liquidacion.setFechaLiq(rs.getDate("fecha_liq"));
		liquidacion.setCantRegistros(rs.getInt("cant_reg"));
		liquidacion.setImporteDerivar(rs.getBigDecimal("derivado"));
		liquidacion.setImporteTotal(rs.getBigDecimal("importetotal"));
		liquidacion.setIdTercerizadora(rs.getString("id_terc"));
		return liquidacion;
	}



	public Date getFechaLiq() {
		return fechaLiq;
	}

	public String getFechaLiqAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaLiq!=null?sdf.format(fechaLiq):"";
	}

	public void setFechaLiq(Date fechaLiq) {
		this.fechaLiq = fechaLiq;
	}



	public BigDecimal getImporteTotal() {
		return importeTotal;
	}
	
	public String getImporteTotalAsString() {
		DecimalFormat nf=new DecimalFormat("#,##0.00");
		return importeTotal!=null?nf.format(importeTotal):"0.00";
	}



	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}



	public String getTercerizadora() {
		return tercerizadora;
	}



	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}



	public int getCantRegistros() {
		return cantRegistros;
	}



	public void setCantRegistros(int cantRegistros) {
		this.cantRegistros = cantRegistros;
	}

	public String getIdTercerizadora() {
		return idTercerizadora;
	}

	public void setIdTercerizadora(String idTercerizadora) {
		this.idTercerizadora = idTercerizadora;
	}

	public BigDecimal getImporteDerivar() {
		return importeDerivar;
	}
	
	public String getImporteDerivarAsString() {
		DecimalFormat nf=new DecimalFormat("#,##0.00");
		return importeDerivar!=null?nf.format(importeDerivar):"0.00";
	}

	public void setImporteDerivar(BigDecimal importeDerivar) {
		this.importeDerivar = importeDerivar;
	}
	
	
	
		
}

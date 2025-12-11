package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EgresoLiquidacion {
	
	String codPrestacion;
	String cuenta;	
	String descripcion;
	BigDecimal importe;
	BigDecimal noDiscapacidad;
	BigDecimal discapacidad;
	BigDecimal debitadoNoDiscapacidad;
	BigDecimal debitadoDiscapacidad;	
	BigDecimal debitado;
	
	public EgresoLiquidacion() {
	}

	
	public static EgresoLiquidacion getMapping(ResultSet rs)
				throws SQLException {
			EgresoLiquidacion egre = new EgresoLiquidacion();
			egre.setDescripcion(rs.getString("descripcion"));
			egre.setImporte(rs.getBigDecimal("importe"));
			return egre;
	}
	
	public static EgresoLiquidacion getMappingEgresos(ResultSet rs)
			throws SQLException {
		EgresoLiquidacion egre = new EgresoLiquidacion();
		egre.setCodPrestacion(rs.getString("codigo"));
		egre.setCuenta(rs.getString("cta"));
		egre.setDescripcion(rs.getString("descripcion"));
		egre.setNoDiscapacidad(rs.getBigDecimal("no_discapacidad"));
		egre.setDiscapacidad(rs.getBigDecimal("discapacidad"));
		egre.setImporte(rs.getBigDecimal("total"));		
		return egre;
	}
	
	public static EgresoLiquidacion getMappingEgresosOS(ResultSet rs)
			throws SQLException {
		EgresoLiquidacion egre = new EgresoLiquidacion();
		egre.setCodPrestacion(rs.getString("codigo"));		
		egre.setDescripcion(rs.getString("descripcion"));
		egre.setDiscapacidad(rs.getBigDecimal("discapacidad"));
		egre.setDebitadoDiscapacidad(rs.getBigDecimal("debitado_discapacidad"));
		egre.setNoDiscapacidad(rs.getBigDecimal("no_discapacidad"));
		egre.setDebitadoNoDiscapacidad(rs.getBigDecimal("debitado_no_discapacidad"));		
		return egre;
	}
	
	public static EgresoLiquidacion getMappingOtrosReintegosOS(ResultSet rs)
			throws SQLException {
		EgresoLiquidacion egre = new EgresoLiquidacion();
		egre.setCodPrestacion(rs.getString("codigo"));		
		egre.setDescripcion(rs.getString("descripcion"));		
		egre.setImporte(rs.getBigDecimal("importe"));
		return egre;
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


	public String getCodPrestacion() {
		return codPrestacion;
	}


	public void setCodPrestacion(String codPrestacion) {
		this.codPrestacion = codPrestacion;
	}


	public String getCuenta() {
		return cuenta;
	}


	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}


	public BigDecimal getNoDiscapacidad() {
		return noDiscapacidad;
	}


	public void setNoDiscapacidad(BigDecimal noDiscapacidad) {
		this.noDiscapacidad = noDiscapacidad;
	}


	public BigDecimal getDiscapacidad() {
		return discapacidad;
	}


	public void setDiscapacidad(BigDecimal discapacidad) {
		this.discapacidad = discapacidad;
	}


	public BigDecimal getDebitado() {
		return debitado;
	}


	public void setDebitado(BigDecimal debitado) {
		this.debitado = debitado;
	}


	public BigDecimal getDebitadoNoDiscapacidad() {
		return debitadoNoDiscapacidad;
	}


	public void setDebitadoNoDiscapacidad(BigDecimal debitadoNoDiscapacidad) {
		this.debitadoNoDiscapacidad = debitadoNoDiscapacidad;
	}


	public BigDecimal getDebitadoDiscapacidad() {
		return debitadoDiscapacidad;
	}


	public void setDebitadoDiscapacidad(BigDecimal debitadoDiscapacidad) {
		this.debitadoDiscapacidad = debitadoDiscapacidad;
	}
	
	

		
}

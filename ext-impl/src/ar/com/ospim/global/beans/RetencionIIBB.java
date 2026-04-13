package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public class RetencionIIBB implements Pago {	
	public static final Integer BSAS = 902; 
	private BigDecimal importe;
	private CuentaBancaria cuentaBancaria;
	private Integer jurisdiccion;
	private Double alicuota;
	private String error;

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public CuentaBancaria getCuentaBancaria() {
		return cuentaBancaria;
	}

	public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
	}

	public String getNumeroStr() {
		return "";
	}
	
	

	public Integer getJurisdiccion() {
		return jurisdiccion;
	}

	public void setJurisdiccion(Integer jurisdiccion) {
		this.jurisdiccion = jurisdiccion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cuentaBancaria == null) ? 0 : cuentaBancaria.hashCode());
		result = prime * result + ((importe == null) ? 0 : importe.hashCode());
		result = prime * result + ((jurisdiccion == null) ? 0 : jurisdiccion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetencionIIBB other = (RetencionIIBB) obj;
		if (cuentaBancaria == null) {
			if (other.cuentaBancaria != null)
				return false;
		} else if (!cuentaBancaria.equals(other.cuentaBancaria))
			return false;
		if (importe == null) {
			if (other.importe != null)
				return false;
		} else if (!importe.equals(other.importe))
			return false;
		
		if (jurisdiccion == null) {
			if (other.jurisdiccion != null)
				return false;
		} else if (!jurisdiccion.equals(other.jurisdiccion))
			return false;
		return true;
	}

	public String getDescripcion() {
		return "";
	}

	public String getANombreDe() {
		return "";
	}

	
	public Date getBaja_fecha() {
		return null;
	}

	public String getTipo() {
		return this.getClass().getSimpleName();
	}

	public String getIdTipo() {
		return "";
	}

	public void savePago(OrdenPago op, String screenName, Connection con,
			int entidad) throws Exception {
		OrdenPagoServiceUtil.savePago(this, op, screenName, con, entidad);
	}
	
	public Double getAlicuota() {
		return alicuota;
	}

	public void setAlicuota(Double alicuota) {
		this.alicuota = alicuota;
	}

	@Override
	public PagoBancario getPagoBancario() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}

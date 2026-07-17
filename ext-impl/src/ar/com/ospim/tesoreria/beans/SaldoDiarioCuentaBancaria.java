package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.util.Date;

public class SaldoDiarioCuentaBancaria {

	private int idCuentaBcria;
	private String cuentaBancaria;
	private Date fechaInicioEjercicio;
	private BigDecimal saldo;

	public int getIdCuentaBcria() {
		return idCuentaBcria;
	}

	public void setIdCuentaBcria(int idCuentaBcria) {
		this.idCuentaBcria = idCuentaBcria;
	}

	public String getCuentaBancaria() {
		return cuentaBancaria;
	}

	public void setCuentaBancaria(String cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
	}

	public Date getFechaInicioEjercicio() {
		return fechaInicioEjercicio;
	}

	public void setFechaInicioEjercicio(Date fechaInicioEjercicio) {
		this.fechaInicioEjercicio = fechaInicioEjercicio;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
}
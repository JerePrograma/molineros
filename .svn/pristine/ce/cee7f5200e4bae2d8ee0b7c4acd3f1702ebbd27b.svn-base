package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public interface Pago {
	CuentaBancaria getCuentaBancaria();

	String getNumeroStr();

	BigDecimal getImporte();

	String getDescripcion();

	String getANombreDe();

	String getTipo();

	String getIdTipo();
	
	PagoBancario getPagoBancario();


	
	public int hashCode();

	public boolean equals(Object obj);
	
	public Date getBaja_fecha();

	void savePago(OrdenPago op, String screenName, Connection con, int entidad)
			throws Exception;
}

package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public class PagoBancario implements Pago {

	private CuentaBancaria cuentaBancaria;
	private BigDecimal importe;
	private String numero;  // cbu o alias
	private int tipo_pago;
	private String descripcionTipoPago;
	
	//cuenta de reclamo
	private String cuilCuenta;
	private String emailCuenta;
	private String apellidoCuenta;
	private String nombreCuenta;
	
	
	

	public PagoBancario(BigDecimal importe, String numero, int tipo_pago,
			String descripcionTipoPago, String cuilCuenta, String emailCuenta, String apellidoCuenta,
			String nombreCuenta) {
		super();
		this.importe = importe;
		this.numero = numero;
		this.tipo_pago = tipo_pago;
		this.descripcionTipoPago = descripcionTipoPago;
		this.cuilCuenta = cuilCuenta;
		this.emailCuenta = emailCuenta;
		this.apellidoCuenta = apellidoCuenta;
		this.nombreCuenta = nombreCuenta;
	}

	public PagoBancario() {
	}

	public String getCuilCuenta() {
		return cuilCuenta;
	}

	public void setCuilCuenta(String cuilCuenta) {
		this.cuilCuenta = cuilCuenta;
	}

	public String getEmailCuenta() {
		return emailCuenta;
	}

	public void setEmailCuenta(String emailCuenta) {
		this.emailCuenta = emailCuenta;
	}

	public String getApellidoCuenta() {
		return apellidoCuenta;
	}

	public void setApellidoCuenta(String apellidoCuenta) {
		this.apellidoCuenta = apellidoCuenta;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}

	public static final int ID_PAGO_DEBITO_BANCARIO = 1;
	public static final int ID_PAGO_TRANSFERENCIA_BANCARIA = 3;
	public static final int ID_PAGO_DEBITO_POR_AUTOGESTION = 2;
	public static final int ID_PAGO_TARJETA_RECARGA = 6;

	public CuentaBancaria getCuentaBancaria() {
		return cuentaBancaria;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public String getNumeroStr() {
		return numero != null ? numero : "";
	}

	public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cuentaBancaria == null) ? 0 : cuentaBancaria.hashCode());
		result = prime * result + ((importe == null) ? 0 : importe.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + tipo_pago;
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
		PagoBancario other = (PagoBancario) obj;
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
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		if (tipo_pago != other.tipo_pago)
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

	public void setTipo_pago(int tipo_pago) {
		this.tipo_pago = tipo_pago;
	}

	public int getTipo_pago() {
		return tipo_pago;
	}

	public void setDescripcionTipoPago(String descripcionTipoPago) {
		this.descripcionTipoPago = descripcionTipoPago;
	}

	public String getDescripcionTipoPago() {
		return descripcionTipoPago;
	}

	public String getTipo() {
		// TODO: esto es horrible, sacarlo de aca
		if (tipo_pago == ID_PAGO_DEBITO_BANCARIO) {
			return "Debito Bancario";
		}
		if (tipo_pago == ID_PAGO_DEBITO_POR_AUTOGESTION) {
			return "Debito por autogestion";
		}
		if (tipo_pago == ID_PAGO_TRANSFERENCIA_BANCARIA) {
			return "Transferencia bancaria";
		}
		if (tipo_pago == ID_PAGO_TARJETA_RECARGA) {
			return "Tarjeta VISA Recargable";
		}
		return "";
	}

	public String getIdTipo() {
		return String.valueOf(tipo_pago);
	}

	public void savePago(OrdenPago op, String screenName, Connection con,
			int entidad) throws Exception {
		OrdenPagoServiceUtil.savePago(this, op, screenName, con, entidad);
	}

	@Override
	public PagoBancario getPagoBancario() {
		PagoBancario pago = new PagoBancario(importe, numero,tipo_pago,descripcionTipoPago,cuilCuenta,
				emailCuenta,apellidoCuenta,nombreCuenta);
		return pago;
	}
}

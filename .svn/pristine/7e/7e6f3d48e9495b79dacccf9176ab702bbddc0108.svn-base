package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ItemSubdiarioIngreso {
	private Date fecha;
	private Date baja_fecha;
	private String cuit;
	private String comprobante;
	private String sucursal;
	private String razonSocial;
	private String cuenta;
	private String numeroCuenta;
	private int cuentaId;
	private String formaPago;
	private String cuentaFormaPago;
	private int cuentaIdFormaPago;
	private BigDecimal importe;

	public ItemSubdiarioIngreso() {
	}

	public ItemSubdiarioIngreso(ItemSubdiarioIngreso est) {
		this.fecha = est.getFecha();
		this.baja_fecha = est.getBaja_fecha();
		this.cuit = est.getCuit();
		this.comprobante = est.comprobante;
		this.sucursal = est.sucursal;
		this.razonSocial = est.razonSocial;
		this.cuenta = est.cuenta;
		this.numeroCuenta = est.numeroCuenta;
		this.cuentaFormaPago = est.cuentaFormaPago;
		this.formaPago = est.formaPago;
		this.importe = est.importe;
		this.cuentaIdFormaPago = est.cuentaIdFormaPago;
		this.cuentaId = est.cuentaId;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public void setNumeroCuenta(String numeroCuenta) {
		this.numeroCuenta = numeroCuenta;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public static ItemSubdiarioIngreso getMapping(ResultSet rs)
			throws SQLException {
		ItemSubdiarioIngreso item = new ItemSubdiarioIngreso();
		item.setComprobante(rs.getString("numero_comprobante"));
		item.setFecha(rs.getDate("fecha"));
		item.setBaja_fecha(rs.getDate("baja_fecha"));
		item.setCuit(rs.getString("cuit"));
		item.setSucursal(rs.getString("sucursal"));
		item.setRazonSocial(rs.getString("razon_soc"));
		item.setCuenta(rs.getString("cuenta"));
		item.setNumeroCuenta(rs.getString("numero"));
		item.setFormaPago(rs.getString("f_de_pag"));
		item.setCuentaFormaPago(rs.getString("cuenta_f_de_pag"));
		item.setImporte(rs.getBigDecimal("importe"));
		item.setCuentaId(rs.getInt("cuenta_id"));
		item.setCuentaIdFormaPago(rs.getInt("cuenta_id_f_de_pag"));
		return item;
	}
	
	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

	public String getComprobante() {
		return comprobante != null ? comprobante : "";
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setCuentaFormaPago(String cuentaFormaPago) {
		this.cuentaFormaPago = cuentaFormaPago;
	}

	public String getFormaPago() {
		if (getBaja_fecha() != null) {
			return numeroCuenta;
		} else {
			return formaPago;
		}
	}

	public String getNumeroCuenta() {
		if (getBaja_fecha() != null) {
			return formaPago;
		} else {
			return numeroCuenta;
		}
	}

	public String getCuentaFormaPago() {
		if (getBaja_fecha() != null) {
			return cuenta;
		} else {
			return cuentaFormaPago;
		}
	}

	public int getCuentaId() {
		if (getBaja_fecha() != null) {
			return cuentaIdFormaPago;
		} else {
			return cuentaId;
		}
	}

	public void setCuentaId(int cuentaId) {
		this.cuentaId = cuentaId;
	}

	public void setCuentaIdFormaPago(int cuentaIdFormaPago) {
		this.cuentaIdFormaPago = cuentaIdFormaPago;
	}

	public int getCuentaIdFormaPago() {
		if (getBaja_fecha() != null) {
			return cuentaId;
		} else {
			return cuentaIdFormaPago;
		}
	}

	public String getCuenta() {
		if (getBaja_fecha() != null) {
			return formaPago;
		} else {
			return cuenta;
		}
	}
	
	public String getCuentaBaja() {		
			return cuenta;		
	}
	
}

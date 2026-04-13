package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class LibroCaja {
	private Date fecha;
	private String debito_credito;
	private String comprobante;
	private String descripcion;
	private BigDecimal importe;

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDebito_credito() {
		return debito_credito;
	}

	public void setDebito_credito(String debitoCredito) {
		debito_credito = debitoCredito;
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

	public static LibroCaja getMapping(ResultSet rs) throws SQLException {
		LibroCaja lb = new LibroCaja();
		lb.setFecha(rs.getDate("fecha"));
		lb.setDebito_credito(rs.getString("debito_credito"));
		lb.setDescripcion(rs.getString("descripcion"));
		lb.setImporte(rs.getBigDecimal("importe"));
		lb.setComprobante(rs.getString("comprobante"));
		return lb;
	}

	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

	public String getComprobante() {
		return comprobante;
	}
}

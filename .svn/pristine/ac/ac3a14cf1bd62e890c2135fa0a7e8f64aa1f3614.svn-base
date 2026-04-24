package ar.com.uoma.facturacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Producto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4665955898759239451L;
	
	private int id;
	private String descripcion;
	private BigDecimal precioUnitario;
	private BigDecimal iva;
	private String debitoCredito; //'D' o 'C'
	private Date vigenDesde;
	private Date vigenHasta;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public BigDecimal getIva() {
		return iva;
	}
	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}
	public Date getVigenDesde() {
		return vigenDesde;
	}
	public void setVigenDesde(Date vigenDesde) {
		this.vigenDesde = vigenDesde;
	}
	public Date getVigenHasta() {
		return vigenHasta;
	}
	public void setVigenHasta(Date vigenHasta) {
		this.vigenHasta = vigenHasta;
	}
	public String getDebitoCredito() {
		return debitoCredito;
	}
	public void setDebitoCredito(String debitoCredito) {
		this.debitoCredito = debitoCredito;
		
	}
	
	public static Producto getMapping(String prefix, ResultSet rs) throws SQLException {
		
		Producto p = new Producto();
		
		p.setId(rs.getInt(prefix + "id"));
		p.setDebitoCredito(rs.getString(prefix+"debito_credito"));
		p.setDescripcion(rs.getString(prefix + "descripcion"));
//		p.setIva(rs.getBigDecimal(prefix + "iva"));
//		p.setPrecioUnitario(rs.getBigDecimal(prefix+"precio_unitario"));
//		p.setVigenDesde(rs.getDate(prefix+"vigen_desde"));
//		p.setVigenHasta(rs.getDate(prefix+"vigen_hasta"));
		
		return p;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Producto other = (Producto) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
	
}

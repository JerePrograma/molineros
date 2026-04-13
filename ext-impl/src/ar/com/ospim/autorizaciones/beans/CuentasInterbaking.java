package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CuentasInterbaking implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String cuit;
	private String cbu;
	private String descripcion; 
	private String email;
	private String accion;
	//private Integer ordenPagoId;
	
	
	public String getCuit() {
		return cuit;
	}
	public String getCbu() {
		return cbu;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public void setCbu(String cbu) {
		this.cbu = cbu;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAccion() {
		return accion;
	}
	
	public void setAccion(String accion) {
		this.accion = accion;
	}
	/*
	public Integer getOrdenPagoId() {
		return ordenPagoId;
	}
	public void setOrdenPagoId(Integer ordenPagoId) {
		this.ordenPagoId = ordenPagoId;
	}
	*/
	public static CuentasInterbaking getMapping(ResultSet rs) throws SQLException {
		CuentasInterbaking cuenta = new CuentasInterbaking();
		cuenta.setCuit(rs.getString("cuit"));
		cuenta.setCbu(rs.getString("cbu"));
		cuenta.setDescripcion(rs.getString("razon_soc"));
		
		try {
			cuenta.setEmail(rs.getString("email"));
			cuenta.setAccion(rs.getString("accion"));
		}catch(Exception e) {}
		
//		cuenta.setOrdenPagoId(rs.getInt("ordenpago_id"));
		return cuenta;
	}
		
	
}

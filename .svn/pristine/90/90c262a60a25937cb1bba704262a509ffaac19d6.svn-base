package ar.com.ospim.afiliados.reportes.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteCredenResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3657385430190902263L;
	
	private String seccional;
	private String apellido;
	private String nombre;	
	private String plan;
	private Date fechaAlta;
		
	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}
	
	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}
		
	
	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public static ReporteCredenResult getMapping(ResultSet rs)
			throws SQLException {
		ReporteCredenResult res = new ReporteCredenResult();
		res.setFechaAlta(rs.getDate("fecha_alta"));		
		res.setSeccional(rs.getString("seccional"));		
		res.setApellido(rs.getString("apellido"));
		res.setNombre(rs.getString("nombre"));
		res.setPlan(rs.getString("plan"));			
		return res;
	}

}

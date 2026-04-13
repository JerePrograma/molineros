package ar.com.ospim.seccional.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.beans.Seccional;

public class GestionSeccional implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5472185034317982309L;
	private Integer id;
	private Seccional seccional;
	private Date fecha;
	private String observaciones;
	private Date altaFecha;
	private String altaUsr;
	private Date bajaFecha;
	private String bajaUsr;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Seccional getSeccional() {
		return seccional;
	}
	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	public String getBajaUsr() {
		return bajaUsr;
	}
	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}
	
	public static GestionSeccional getMapping(String prefix, ResultSet rs) throws SQLException {
		
		GestionSeccional gs = new GestionSeccional();
		
		gs.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		gs.setAltaUsr(rs.getString(prefix + "alta_usr"));
		gs.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		gs.setBajaUsr(rs.getString(prefix + "baja_usr"));		
		gs.setFecha(rs.getDate(prefix + "fecha"));
		gs.setId(rs.getInt(prefix + "id"));
		gs.setObservaciones(rs.getString(prefix+"observaciones"));
		gs.setSeccional(new Seccional(rs.getInt(prefix + "id_seccional")));
		
		return gs;
		
	}
	
}


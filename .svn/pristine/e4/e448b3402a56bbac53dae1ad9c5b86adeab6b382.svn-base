package ar.com.ospim.correspondencia.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Paquete {

	private long id;	
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String estado;
	private String descripcion;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}
			
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public static Paquete getMapping(ResultSet rs, String prefix)
			throws SQLException {

		Paquete e = new Paquete();
		e.setId(rs.getLong(prefix + "id"));
		e.setEstado(rs.getString(prefix + "estado"));
		e.setDescripcion(rs.getString(prefix + "descripcion"));
		e.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		e.setAlta_usr(rs.getString(prefix + "alta_usr"));
		e.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		e.setModi_usr(rs.getString(prefix + "modi_usr"));
		e.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		e.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return e;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

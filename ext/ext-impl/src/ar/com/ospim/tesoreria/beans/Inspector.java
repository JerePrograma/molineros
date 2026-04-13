package ar.com.ospim.tesoreria.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Inspector {

	private String nombre;
	private int id;
	private Date altaFecha;
	private String altaUsuario;
	private Date modiFecha;
	private String modiUsuario;
	private Date bajaFecha;
	private String bajaUsuario;

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAltaUsuario() {
		return altaUsuario;
	}

	public void setAltaUsuario(String altaUsuario) {
		this.altaUsuario = altaUsuario;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModiUsuario() {
		return modiUsuario;
	}

	public void setModiUsuario(String modiUsuario) {
		this.modiUsuario = modiUsuario;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsuario() {
		return bajaUsuario;
	}

	public void setBajaUsuario(String bajaUsuario) {
		this.bajaUsuario = bajaUsuario;
	}

	public Inspector() {
	}

	public Inspector(int id, String nombre) {
		this.nombre = nombre;
		this.id = id;
	}
	
	public Inspector(Inspector inspector) {
		this.nombre = inspector.nombre;
		this.id = inspector.id;
		this.altaFecha = inspector.altaFecha;
		this.altaUsuario = inspector.altaUsuario;
		this.modiFecha = inspector.modiFecha;
		this.modiUsuario = inspector.modiUsuario;
		this.bajaFecha = inspector.bajaFecha;
		this.bajaUsuario = inspector.bajaUsuario;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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
		Inspector other = (Inspector) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static Inspector getMapping(ResultSet rs) throws SQLException {
		Inspector inspector = new Inspector();
		inspector.setNombre(rs.getString("nombre"));
		inspector.setId(rs.getInt("id"));
		inspector.setAltaFecha(rs.getDate("alta_fecha"));
		inspector.setAltaUsuario(rs.getString("alta_usr"));
		inspector.setModiFecha(rs.getDate("modi_fecha"));
		inspector.setModiUsuario(rs.getString("modi_usr"));
		inspector.setBajaFecha(rs.getDate("baja_fecha"));
		inspector.setBajaUsuario(rs.getString("baja_usr"));
		return inspector;
	}
}

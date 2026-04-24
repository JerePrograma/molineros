package ar.com.empresas.beans;

import java.sql.ResultSet;
import java.sql.SQLException;



public class Actividad {
	private int codigo;
	private String descripcion;

	public Actividad() {

	}
	
	public Actividad(int id) {
		this.codigo = id;		
	}

	public Actividad(int id, String descripcion) {
		this.codigo = id;
		this.descripcion = descripcion;
	}

	public int getCodigo() {
		return codigo;
	}
	
	public int getId() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public static Actividad getMapping(ResultSet rs)
			throws SQLException {
		Actividad actividad = new Actividad();
		actividad.setCodigo(rs.getInt("codigo"));
		actividad.setDescripcion(rs.getString("descripcion"));
		return actividad;
	}

}
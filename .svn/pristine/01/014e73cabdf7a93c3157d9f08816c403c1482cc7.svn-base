package ar.com.ospim.global.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PosicionIva {
	private int id;
	private String descripcion;

	public PosicionIva(int id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}
	
	public PosicionIva(int id) {
		this.id = id;		
	}

	public PosicionIva() {
	}

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
		PosicionIva other = (PosicionIva) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public static PosicionIva getMapping(ResultSet rs) throws SQLException {
		PosicionIva posicion = new PosicionIva();
		posicion.setId(rs.getInt("id_posicion"));
		posicion.setDescripcion(rs.getString("detalle"));
		return posicion;
	}
}

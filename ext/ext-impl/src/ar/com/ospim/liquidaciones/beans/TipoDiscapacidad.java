package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoDiscapacidad implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3351869269406108167L;
	private int id;
	private String descripcion;
	
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
	
	public TipoDiscapacidad(int id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}
	
	public static TipoDiscapacidad getMapping(String prefix, ResultSet rs) throws SQLException {
		
		return new TipoDiscapacidad(rs.getInt(prefix + "id"), rs.getString(prefix + "descripcion"));
		
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
		TipoDiscapacidad other = (TipoDiscapacidad) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
}

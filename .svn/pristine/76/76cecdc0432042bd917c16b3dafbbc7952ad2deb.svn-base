package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaContacto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3455454544277863584L;
	private Integer id;
	private String descripcion;
	
	public CategoriaContacto (Integer id, String descripcion){
		
		super();
		this.id = id;
		this.descripcion = descripcion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String toString(){
		return this.descripcion;
	}
	
	public static CategoriaContacto getMapping(String prefix, ResultSet rs) throws SQLException{
		
		CategoriaContacto cc = new CategoriaContacto(rs.getInt(prefix + "id"), rs.getString(prefix + "descripcion"));
		
		return cc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CategoriaContacto other = (CategoriaContacto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}

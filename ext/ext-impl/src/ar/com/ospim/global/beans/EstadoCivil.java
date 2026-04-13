package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstadoCivil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1332978276747701637L;
	
	private int codigo;
	private String descripcion;
	
	public int getCodigo() {
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
	
	public static EstadoCivil getMapping(ResultSet rs, String prefix) throws SQLException{
		
		EstadoCivil ec = new EstadoCivil(rs.getInt(prefix+"codigo"), 
					rs.getString(prefix+"descripcion"));
	
		return ec;
	}
	
	public EstadoCivil(){
		super();
	}
	
	public EstadoCivil(int codigo, String descripcion){
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codigo;
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
		EstadoCivil other = (EstadoCivil) obj;
		if (codigo != other.codigo)
			return false;
		return true;
	}
	
	
}

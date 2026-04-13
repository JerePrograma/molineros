package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Parentesco implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3553262623907259694L;
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
	
	public static Parentesco getMapping(ResultSet rs, String prefix) throws SQLException{
		
		Parentesco p = new Parentesco(rs.getInt(prefix+"codigo"), 
					rs.getString(prefix+"descripcion"));
	
		return p;
	}
	
	public Parentesco(){
		super();
	}
	
	public Parentesco(int codigo, String descripcion){
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
		Parentesco other = (Parentesco) obj;
		if (codigo != other.codigo)
			return false;
		return true;
	}
	
	
}

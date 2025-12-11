package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Provincia implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7492319357651661499L;
	
	private int id;
	private String descripcion;
	private int idSss;
	
	public Provincia(){
		super();
	}
	
	public Provincia(int id, String descripcion){
		this.id=id;
		this.descripcion=descripcion;
	}
	
	public Provincia(int id, String descripcion, int idSssalud){
		this.id=id;
		this.descripcion=descripcion;
		this.idSss = idSssalud;
	}

	public Provincia(int provinciaId) {
		this.id = provinciaId;
		this.descripcion = "";
	}


	public static Provincia getMapping(ResultSet rs) throws SQLException{
		
		Provincia provincia = new Provincia(rs.getInt("id_provincia"),
											rs.getString("detalle"), 
											rs.getInt("id_sssalud"));
		
		return provincia;
	}
	
	public static Provincia getMapping(String prefix, ResultSet rs) throws SQLException{
		
		Provincia provincia = new Provincia(rs.getInt(prefix+"id_provincia"),
											rs.getString(prefix+"detalle"), 
											rs.getInt(prefix+"id_sssalud"));
		
		return provincia;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}



	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}



	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public int getIdSss() {
		return idSss;
	}


	public void setIdSss(int idSss) {
		this.idSss = idSss;
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
		Provincia other = (Provincia) obj;
		if (id != other.id)
			return false;
		return true;
	}

	

	
	
	
}
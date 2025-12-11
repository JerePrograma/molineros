package ar.com.ospim.global.beans;

import java.io.Serializable;



public class Nacionalidad implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2076920827004500202L;
	
	public int id;
	public String descripcion;
	public int id_ssuper;
	
	public Nacionalidad(int id, String descripcion){
		super();
		this.id=id;
		this.descripcion=descripcion;
	}
	
	public Nacionalidad(int id, String descripcion, int id_sssuper){
		super();
		this.id=id;
		this.descripcion=descripcion;
		this.id_ssuper = id_sssuper;
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

	public int getId_ssuper() {
		return id_ssuper;
	}

	public void setId_ssuper(int id_ssuper) {
		this.id_ssuper = id_ssuper;
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
		Nacionalidad other = (Nacionalidad) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
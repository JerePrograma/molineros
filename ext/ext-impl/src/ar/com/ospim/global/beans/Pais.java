package ar.com.ospim.global.beans;

import java.io.Serializable;



public class Pais implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String descripcion;

	
	public Pais(int id, String descripcion){
		this.id=id;
		this.descripcion=descripcion;
	}
	

	public Pais(int provinciaId) {
		this.id = provinciaId;
		this.descripcion = "";
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

	
}
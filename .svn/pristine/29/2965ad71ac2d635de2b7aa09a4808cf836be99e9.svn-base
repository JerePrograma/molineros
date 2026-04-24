package ar.com.ospim.global.beans;

import java.io.Serializable;



public class ObraSocialCampo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String descripcion;

	
	public ObraSocialCampo(int id, String descripcion){
		this.id=id;
		this.descripcion=descripcion;
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
	 * @return the descripcion version corta de la descripción
	 */
	public String getDescripcionShort() {
		if (descripcion != null) {
			if (descripcion.length() > 75 ) {
				return descripcion.substring(0, 60) + " ... " + descripcion.substring(descripcion.length() - 10, descripcion.length()-1);
			}
		}
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	
}
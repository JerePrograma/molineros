package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

public class ReclamosPrestacionalesIntegracion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String descripcion;
	public String descripcionLarga;
	
	
	public ReclamosPrestacionalesIntegracion(int id, String descripcion, String descripcionLarga ) {
		this.id = id;
		this.descripcion = descripcion;
		this.descripcionLarga = descripcionLarga;
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
	public void setDescripcionLarga(String descripcionLarga) {
		this.descripcionLarga = descripcionLarga;
	}
	
	public String getDescripcionLarga() {
		return descripcionLarga;
	}



}


package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

public class TiposDeGestionReclamosPrestacionales implements Serializable {

	private static final long serialVersionUID = 1L;
	public int id;
	public String descripcion;
	 
		
	public TiposDeGestionReclamosPrestacionales ( String strdescripcion ){
	}

	public TiposDeGestionReclamosPrestacionales (int intid, String strdescripcion ) {
		// TODO Auto-generated constructor stub
		this.id=intid;
		this.descripcion=strdescripcion ;
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
	
	

}



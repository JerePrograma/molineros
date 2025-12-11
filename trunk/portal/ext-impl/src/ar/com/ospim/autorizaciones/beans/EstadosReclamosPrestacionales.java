package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

public class EstadosReclamosPrestacionales implements Serializable {

	private static final long serialVersionUID = 1L;
	public int id;
	public String descripcion;
    public String codigo ;
	
	
	 
	
	
	public EstadosReclamosPrestacionales ( String strdescripcion , String strcodigo){
	}



	public EstadosReclamosPrestacionales(int intid, String strdescripcion , String strcodigo ) {
		// TODO Auto-generated constructor stub
		this.id=intid;
		this.descripcion=strdescripcion ;
		this.codigo= strcodigo ;
		
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



	public String getCodigo() {
		return codigo;
	}



	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	



}

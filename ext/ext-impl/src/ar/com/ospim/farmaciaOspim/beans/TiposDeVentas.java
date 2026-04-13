
package ar.com.ospim.farmaciaOspim.beans;

import java.io.Serializable;

public class TiposDeVentas implements Serializable {

	private static final long serialVersionUID = 1L;
	private  int codigo;
	private String descripcion;
	  

	public TiposDeVentas  (int  codigo, String strdescripcion ) {		
		this.descripcion=strdescripcion ;
		this.codigo=codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
   public void setCodigo (int codigo ){
	    this.codigo=  codigo;
   }
   
   public int getCodigo (){
	   return this.codigo;
   }

}



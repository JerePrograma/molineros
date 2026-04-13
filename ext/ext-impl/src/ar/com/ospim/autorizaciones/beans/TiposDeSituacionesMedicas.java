
package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

public class TiposDeSituacionesMedicas implements Serializable {

	private static final long serialVersionUID = 1L;
	public int id;
	public String descripcion;
	public String codigo;  
		
	public TiposDeSituacionesMedicas ( String strdescripcion ){
	}

	public TiposDeSituacionesMedicas (int intid,String codigo, String strdescripcion ) {
		// TODO Auto-generated constructor stub
		this.id=intid;
		this.descripcion=strdescripcion ;
		this.codigo=codigo;
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
	
   public void setCodigo (String codigo ){
	    this.codigo=  codigo;
   }
   
   public String getCodigo (){
	   return this.codigo;
   }
   

}



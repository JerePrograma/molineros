package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;

public class OpcionesPrestacion implements Serializable {

	private static final long serialVersionUID = 1L;
	public int id;
	public String descripcionOpcion;
    public String prestacionCodigo ;
    public int idPrestacion;
	 
	
	public OpcionesPrestacion(){
		
	}

	public OpcionesPrestacion(int id, String opcionDescripcion	 , String codigoPrestacion  ) {
		this.id=id;
		this.descripcionOpcion=opcionDescripcion	 ;
		this.prestacionCodigo = codigoPrestacion  ;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcionOpcion;
	}

	public void setDescripcion(String descripcionOpcion) {
		this.descripcionOpcion = descripcionOpcion;
	}

	public String getCodigo() {
		return prestacionCodigo;
	}

	public void setCodigo(String codigoPrestacion) {
		this.prestacionCodigo = codigoPrestacion;
	}


}

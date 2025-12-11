package ar.com.ospim.webservice.beans;

import java.io.Serializable;

public class ResultadoActualizacionCredencial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 834115673658293200L;
	
	private Integer idTransaccion;
	private String descripcionError;
	
	public Integer getIdTransaccion() {
		return idTransaccion;
	}
	public void setIdTransaccion(Integer idTransaccion) {
		this.idTransaccion = idTransaccion;
	}
	public String getDescripcionError() {
		return descripcionError;
	}
	public void setDescripcionError(String descripcionError) {
		this.descripcionError = descripcionError;
	}
	
	
	
}

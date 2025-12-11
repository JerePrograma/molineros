package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;

/**
 * @author Carlos Rivas
 * @version 1.0
 * @created 01-Oct-2012 11:34:23 a.m.
 */
public class MotivoAltaDiscapacidad implements Serializable {	

	private static final long serialVersionUID = 1960475082350362614L;
	private int estadoAlta;
	private String mensajeAltaEstado;
	
	public MotivoAltaDiscapacidad() {
	}
	public MotivoAltaDiscapacidad(int estadoAlta, String mensajeAltaEstado) {	
		this.estadoAlta = estadoAlta;
		this.mensajeAltaEstado = mensajeAltaEstado;
	}
	
	public int getEstadoAlta() {
		return estadoAlta;
	}
	
	public void setEstadoAlta(int estadoAlta) {
		this.estadoAlta = estadoAlta;
	}
	
	public String getMensajeAltaEstado() {
		return mensajeAltaEstado;
	}
	
	public void setMensajeAltaEstado(String mensajeAltaEstado) {
		this.mensajeAltaEstado = mensajeAltaEstado;
	}
	
			
}
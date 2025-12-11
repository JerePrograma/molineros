package ar.com.ospim.global.beans;

import java.io.Serializable;



public class Motivo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id_motivo;	
    private String compro_tipo;    
    private String descripcion;
    private String observaciones;

	
	public Motivo (int id_motivo, String compro_tipo, String descripcion, String observaciones){
		this.id_motivo=id_motivo;
		this.compro_tipo = compro_tipo;
		this.descripcion=descripcion;
		this.observaciones = observaciones;
	}

	public Motivo (int id_motivo, String descripcion){
		this.id_motivo=id_motivo;		
		this.descripcion=descripcion;		
	}


	/**
	 * @return the id_motivo
	 */
	public int getId_motivo() {
		return id_motivo;
	}


	/**
	 * @param idMotivo the id_motivo to set
	 */
	public void setId_motivo(int idMotivo) {
		id_motivo = idMotivo;
	}


	/**
	 * @return the compro_tipo
	 */
	public String getCompro_tipo() {
		return compro_tipo;
	}


	/**
	 * @param comproTipo the compro_tipo to set
	 */
	public void setCompro_tipo(String comproTipo) {
		compro_tipo = comproTipo;
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


	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}


	/**
	 * @param observaciones the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

}
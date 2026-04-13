package ar.com.global.beans;

import java.util.List;

/**
 * @author sistema-09
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class Boletin{
	private int idBoletin;
	private String nombre;
	private String asunto;	
	private String observaciones;
	private String[] listas;
	private List<Contenido> listaContenidos;
	private String alta_user;
	private int idListaBoletin;
	private boolean soloTexto;
	private boolean difusion;
		
	
	public Boletin(){}

	

	public int getIdListaBoletin() {
		return idListaBoletin;
	}



	public void setIdListaBoletin(int idListaBoletin) {
		this.idListaBoletin = idListaBoletin;
	}



	public int getIdBoletin() {
		return idBoletin;
	}


	public void setIdBoletin(int idBoletin) {
		this.idBoletin = idBoletin;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getAsunto() {
		return asunto;
	}


	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}


	public String getObservaciones() {
		return observaciones;
	}


	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}


	public String[] getListas() {
		return listas;
	}


	public void setListas(String[] listas) {
		this.listas = listas;
	}


	public List<Contenido> getListaContenidos() {
		return listaContenidos;
	}


	public void setListaContenidos(List<Contenido> listaContenidos) {
		this.listaContenidos = listaContenidos;
	}


	public String getAlta_user() {
		return alta_user;
	}


	public void setAlta_user(String alta_user) {
		this.alta_user = alta_user;
	}



	public boolean isSoloTexto() {
		return soloTexto;
	}



	public void setSoloTexto(boolean soloTexto) {
		this.soloTexto = soloTexto;
	}



	public boolean isDifusion() {
		return difusion;
	}



	public void setDifusion(boolean difusion) {
		this.difusion = difusion;
	}
	
	
	
		
}


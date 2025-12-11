package ar.com.cgt.ddhh.beans;

import java.util.List;

import ar.com.ospim.global.beans.Domicilio;


/**
 * @author sistema-09
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class Organismo {

	private int id_organismo;
	private String nombre;
	private String sigla;
	private String telefono;
	private String web;
	private String ambito;
	private String observaciones;
	private List<Comentario> comentario;
	private List<Contacto> contactos;
	private List<LineaTrabajo> lineasTrabajo;
	private List<Area> areas;
	private String lineasString;
	private String orbita;
	private String email;
	private Domicilio domicilio;
	
	public int getId_organismo() {
		return id_organismo;
	}
	public void setId_organismo(int id_organismo) {
		this.id_organismo = id_organismo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getAmbito() {
		return ambito;
	}
	public void setAmbito(String ambito) {
		this.ambito = ambito;
	}
	public List<Contacto> getContactos() {
		return contactos;
	}
	public void setContactos(List<Contacto> contactos) {
		this.contactos = contactos;
	}
	public List<LineaTrabajo> getLineasTrabajo() {
		return lineasTrabajo;
	}
	public void setLineasTrabajo(List<LineaTrabajo> lineasTrabajo) {
		this.lineasTrabajo = lineasTrabajo;
	}
	public List<Comentario> getComentario() {
		return comentario;
	}
	public void setComentario(List<Comentario> comentario) {
		this.comentario = comentario;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getLineasString() {
		return lineasString;
	}
	public void setLineasString(String lineasString) {
		this.lineasString = lineasString;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public List<Area> getAreas() {
		return areas;
	}
	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
	public String getOrbita() {
		return orbita;
	}
	public void setOrbita(String orbita) {
		this.orbita = orbita;
	}
	public Domicilio getDomicilio() {
		return domicilio;
	}
	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
		
	
		
}

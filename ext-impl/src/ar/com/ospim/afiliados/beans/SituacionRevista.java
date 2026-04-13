package ar.com.ospim.afiliados.beans;

import java.util.Date;

/**
 * @author Administrador
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 */
public class SituacionRevista {	 
	
	private int id_situ_revista;
	private String descripcion;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private int meses_a_baja;	
	

	public SituacionRevista(){}
	
	public SituacionRevista(String descrip){
		this.descripcion=descrip;
	}
	
	public SituacionRevista(int id, String descrip){
		this.id_situ_revista=id;
		this.descripcion=descrip;
	}

	public int getId_situ_revista() {
		return id_situ_revista;
	}

	public void setId_situ_revista(int idSituRevista) {
		id_situ_revista = idSituRevista;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	public int getMeses_a_baja() {
		return meses_a_baja;
	}

	public void setMeses_a_baja(int mesesABaja) {
		meses_a_baja = mesesABaja;
	}

}
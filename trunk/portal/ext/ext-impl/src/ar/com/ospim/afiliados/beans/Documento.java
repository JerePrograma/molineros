package ar.com.ospim.afiliados.beans;

import java.util.Date;

/**
 * @author fbrachi
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class Documento {

	private int id_documento;
	private String descripcion;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private int id_motivo_baja;

	public Documento(int id, String descrip){
		this.id_documento=id;
		this.descripcion=descrip;
	}
	
	public Documento(int id, String descrip, int id_mot_baja){
		this.id_documento=id;
		this.descripcion=descrip;
		this.id_motivo_baja = id_mot_baja;
	}

	public int getId_documento() {
		return id_documento;
	}

	public void setId_documento(int idDocumento) {
		id_documento = idDocumento;
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
	
	public int getId_motivo_baja() {
		return id_motivo_baja;
	}

	public void setId_motivo_baja(int idMotivoBaja) {
		id_motivo_baja = idMotivoBaja;
	}

}
package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author sistema-09
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class TercerizadoraServicio {

	private String id_tercerizadora;
	private String descripcion;
	private String observaciones;
	private Date fechaInicio;
	private Date fechaFin;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String estado;
	private boolean convenios;

	public TercerizadoraServicio(String id, String descrip) {
		this.id_tercerizadora = id;
		this.descripcion = descrip;

	}
	
	public TercerizadoraServicio(String id, String descrip, Date fechaI, Date fechaF) {
		this.id_tercerizadora = id;
		this.descripcion = descrip;
		this.fechaInicio = fechaI;
		this.fechaFin = fechaF;

	}

	public TercerizadoraServicio() {
	}

	public TercerizadoraServicio(String id_tercerizadora) {
		this.id_tercerizadora = id_tercerizadora;
	}

	public String getId_tercerizadora() {
		return id_tercerizadora;
	}

	public void setId_tercerizadora(String idTercerizadora) {
		id_tercerizadora = idTercerizadora;
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((id_tercerizadora == null) ? 0 : id_tercerizadora.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TercerizadoraServicio other = (TercerizadoraServicio) obj;
		if (id_tercerizadora == null) {
			if (other.id_tercerizadora != null)
				return false;
		} else if (!id_tercerizadora.equals(other.id_tercerizadora))
			return false;
		return true;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	private boolean isConvenios() {
		return convenios;
	}

	private void setConvenios(boolean convenios) {
		this.convenios = convenios;
	}

	public static TercerizadoraServicio getMapping(ResultSet rs, String prefix)
			throws SQLException {

		TercerizadoraServicio ap = new TercerizadoraServicio();

		ap.setId_tercerizadora(rs.getString(prefix + "id_tercerizadora"));
		ap.setDescripcion(rs.getString(prefix + "descripcion"));
		ap.setObservaciones(rs.getString(prefix + "observaciones"));
		ap.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ap.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ap.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ap.setModi_usr(rs.getString(prefix + "modi_usr"));
		ap.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ap.setBaja_usr(rs.getString(prefix + "baja_usr"));
		ap.setConvenios(rs.getBoolean(prefix + "convenios"));

		return ap;
	}

}
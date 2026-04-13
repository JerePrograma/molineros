package ar.com.ospim.global.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Federico Brachi
 * @version 1.0
 * @created 23-Jul-2010 02:09:01 p.m.
 */
public class EntidadCamaraEmpresa {

	private int id_entidad_cam_empresa;
	private String descripcion;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;

	public EntidadCamaraEmpresa() {

	}

	public EntidadCamaraEmpresa(int id_entidad_cam_empresa, String descripcion) {
		this.id_entidad_cam_empresa = id_entidad_cam_empresa;
		this.descripcion = descripcion;
	}
	public EntidadCamaraEmpresa(int id_entidad_cam_empresa) {
		this.id_entidad_cam_empresa = id_entidad_cam_empresa;		
	}

	public int getId_entidad_cam_empresa() {
		return id_entidad_cam_empresa;
	}

	public void setId_entidad_cam_empresa(int idEntidadCamEmpresa) {
		id_entidad_cam_empresa = idEntidadCamEmpresa;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_entidad_cam_empresa;
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
		EntidadCamaraEmpresa other = (EntidadCamaraEmpresa) obj;
		if (id_entidad_cam_empresa != other.id_entidad_cam_empresa)
			return false;
		return true;
	}

	public static EntidadCamaraEmpresa getMapping(ResultSet rs)
			throws SQLException {
		EntidadCamaraEmpresa entidad = new EntidadCamaraEmpresa();
		entidad.setAlta_fecha(rs.getDate("alta_fecha"));
		entidad.setAlta_usr(rs.getString("alta_usr"));
		entidad.setBaja_fecha(rs.getDate("baja_fecha"));
		entidad.setBaja_usr(rs.getString("baja_usr"));
		entidad.setDescripcion(rs.getString("descripcion"));
		entidad.setId_entidad_cam_empresa(rs.getInt("id_entidad_cam_empresa"));
		entidad.setModi_fecha(rs.getDate("modi_fecha"));
		entidad.setModi_usr(rs.getString("modi_usr"));
		entidad.setObservaciones(rs.getString("observaciones"));
		return entidad;
	}

}
package ar.com.ospim.global.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Federico Brachi
 * @version 1.0
 * @created 23-Jul-2010 02:09:08 p.m.
 */
public class RamoEmpresa {

	private int id_ramo_empresa;
	private String descripcion;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;

	public RamoEmpresa(){}

	public RamoEmpresa(int idRamoEmpresa) {
		this.id_ramo_empresa = idRamoEmpresa;
	}
	public RamoEmpresa(int idRamoEmpresa, String descripcion) {
		this.id_ramo_empresa = idRamoEmpresa;
		this.descripcion = descripcion;
	}

	public int getId_ramo_empresa() {
		return id_ramo_empresa;
	}

	public void setId_ramo_empresa(int idRamoEmpresa) {
		id_ramo_empresa = idRamoEmpresa;
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
	
	public static RamoEmpresa getMapping(ResultSet rs) throws SQLException {
		RamoEmpresa ramo = new RamoEmpresa();
		ramo.setAlta_fecha(rs.getDate("alta_fecha"));
		ramo.setAlta_usr(rs.getString("alta_usr"));
		ramo.setBaja_fecha(rs.getDate("baja_fecha"));
		ramo.setBaja_usr(rs.getString("baja_usr"));
		ramo.setDescripcion(rs.getString("descripcion"));
		ramo.setId_ramo_empresa(rs.getInt("id_ramo_empresa"));
		ramo.setModi_fecha(rs.getDate("modi_fecha"));
		ramo.setModi_usr(rs.getString("modi_usr"));
		ramo.setObservaciones(rs.getString("observaciones"));
		return ramo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_ramo_empresa;
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
		RamoEmpresa other = (RamoEmpresa) obj;
		if (id_ramo_empresa != other.id_ramo_empresa)
			return false;
		return true;
	}
	
}
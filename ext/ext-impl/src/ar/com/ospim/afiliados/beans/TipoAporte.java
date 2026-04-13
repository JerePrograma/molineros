package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Administrador
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 * @edited SVA
 * 
 */

//Representa el mapeo de la tabla Aporte
public class TipoAporte {

	public enum ID_GENERADO {
		U, A, O
	};

	private int id_aporte;
	private String tipo_aporte;
	private String plan;
	private String descripcion;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private ID_GENERADO genera_id_socio;
	private boolean esOS;

	
	public TipoAporte() {
	}

	public TipoAporte(int id, String descrip) {
		this.id_aporte = id;
		this.descripcion = descrip;
	}

	public int getId_aporte() {
		return id_aporte;
	}

	public void setId_aporte(int idAporte) {
		id_aporte = idAporte;
	}

	public String getTipo_aporte() {
		return tipo_aporte;
	}

	public void setTipo_aporte(String tipoAporte) {
		tipo_aporte = tipoAporte;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
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

	public void setGenera_id_socio(ID_GENERADO idGENERADO) {
		this.genera_id_socio = idGENERADO;
	}

	public ID_GENERADO getGenera_id_socio() {
		return genera_id_socio;
	}

	public boolean esOS() {
		return esOS;
	}

	public void setEsOS(boolean esOS) {
		this.esOS = esOS;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_aporte;
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
		TipoAporte other = (TipoAporte) obj;
		if (id_aporte != other.id_aporte)
			return false;
		return true;
	}

	public static TipoAporte getMapping(ResultSet rs) throws SQLException {
		return getMapping("",rs);
	}

	public static TipoAporte getMapping(String prefix, ResultSet rs)
			throws SQLException {
		TipoAporte tipoAporte = new TipoAporte();
		tipoAporte.setId_aporte(rs.getInt(prefix + "id_aporte"));
		tipoAporte.setTipo_aporte(rs.getString(prefix + "tipo_aporte"));
		tipoAporte.setPlan(rs.getString(prefix + "plan"));
		tipoAporte.setDescripcion(rs.getString(prefix + "descripcion"));
		tipoAporte.setObservaciones(rs.getString(prefix + "observaciones"));
		String generaIdSocio = rs.getString(prefix + "genera_id_socio");
		if (generaIdSocio != null) {
			tipoAporte.setGenera_id_socio(ID_GENERADO.valueOf(generaIdSocio
					.trim()));
		}
		tipoAporte.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		tipoAporte.setAlta_usr(rs.getString(prefix + "alta_usr"));
		tipoAporte.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		tipoAporte.setModi_usr(rs.getString(prefix + "modi_usr"));
		tipoAporte.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		tipoAporte.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return tipoAporte;
	}
}
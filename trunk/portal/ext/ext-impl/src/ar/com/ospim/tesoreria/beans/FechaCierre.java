package ar.com.ospim.tesoreria.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FechaCierre {
	private Date fecha;
	private String observacion;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getFechaString() {
		if (fecha == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(fecha);
	}

	public void setFechaString(String fecha) throws ParseException {
		if (fecha != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			this.fecha = format.parse(fecha);
		}
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public String getBaja_fechaString() {
		if (baja_fecha == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(baja_fecha);
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}

	public static FechaCierre getMapping(ResultSet rs) throws SQLException {
		FechaCierre fecha = new FechaCierre();
		fecha.setAlta_fecha(rs.getDate("alta_fecha"));
		fecha.setAlta_usr(rs.getString("alta_usr"));
		fecha.setModi_fecha(rs.getDate("modi_fecha"));
		fecha.setModi_usr(rs.getString("modi_usr"));
		fecha.setBaja_fecha(rs.getDate("baja_fecha"));
		fecha.setBaja_usr(rs.getString("baja_usr"));
		fecha.setObservacion(rs.getString("observacion"));
		fecha.setFecha(rs.getDate("fecha_cierre"));
		return fecha;
	}

}

package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class SeguimientoSurDetalle implements Serializable{

	private static final long serialVersionUID = 7826784467169052985L;
	private Integer id;
	private Date fechaCarga;
	private Date fechaEnvio;
	private Date fechaNotificacion;
	private Date fechaRespuesta;
	private String observaciones;
	private Integer estadoId;
	private String estadoDescripcion;
	
	private Date baja_fecha;
	
	public static SeguimientoSurDetalle getMapping(ResultSet rs) throws SQLException {
		SeguimientoSurDetalle archivo = new SeguimientoSurDetalle();
		archivo.setBaja_fecha(rs.getDate("baja_fecha"));
		archivo.setId(rs.getInt("id"));
		archivo.setFechaCarga(rs.getDate("fecha_carga"));
		archivo.setFechaNotificacion(rs.getDate("fecha_notificacion"));
		archivo.setObservaciones(rs.getString("observaciones"));
		archivo.setEstadoId(rs.getInt("estado_id"));
		archivo.setEstadoDescripcion(rs.getString("estado_descripcion"));
		return archivo;
		
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public final Date getBaja_fecha() {
		return baja_fecha;
	}
	
	public String getBaja_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return baja_fecha != null ? sdf.format(baja_fecha)
				: "";
	}

	public final void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	
	public String getFechaEnvio_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaEnvio != null ? sdf.format(fechaEnvio): "";
	}
	
	public Date getFechaCarga() {
		return fechaCarga;
	}

	public void setFechaCarga(Date fechaCarga) {
		this.fechaCarga = fechaCarga;
	}
	
	public String getFechaCarga_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaCarga != null ? sdf.format(fechaCarga): "";
	}

	public Date getFechaNotificacion() {
		return fechaNotificacion;
	}

	public void setFechaNotificacion(Date fechaNotificacion) {
		this.fechaNotificacion = fechaNotificacion;
	}

	public String getFechaNotificacion_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaNotificacion != null ? sdf.format(fechaNotificacion): "";
	}
	
	public Date getFechaRespuesta() {
		return fechaRespuesta;
	}

	public void setFechaRespuesta(Date fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}
	
	public String getFechaRespuesta_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaRespuesta != null ? sdf.format(fechaRespuesta): "";
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Integer getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(Integer estadoId) {
		this.estadoId = estadoId;
	}

	public String getEstadoDescripcion() {
		return estadoDescripcion;
	}

	public void setEstadoDescripcion(String estadoDescripcion) {
		this.estadoDescripcion = estadoDescripcion;
	}

	
	
}

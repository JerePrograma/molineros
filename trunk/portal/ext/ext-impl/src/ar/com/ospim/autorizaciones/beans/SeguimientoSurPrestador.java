package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class SeguimientoSurPrestador implements Serializable{
	private static final long serialVersionUID = 5974916015327981460L;
	private Integer id;
	private Integer idPrestador;
	private String descripcionPrestador;
	private String cuitPrestador;
	private Date fechaEstado;
	private String observaciones;
	private String usuario;
	
	
	private Date baja_fecha;
	
	public static SeguimientoSurPrestador getMapping(ResultSet rs) throws SQLException {
		SeguimientoSurPrestador archivo = new SeguimientoSurPrestador();
		archivo.setBaja_fecha(rs.getDate("baja_fecha"));
		archivo.setId(rs.getInt("id"));
		archivo.setIdPrestador(rs.getInt("id_prestador"));
		archivo.setDescripcionPrestador(rs.getString("descripcion_prestador"));
		archivo.setFechaEstado(rs.getDate("fecha_estado"));
		archivo.setObservaciones(rs.getString("observaciones"));
		archivo.setUsuario(rs.getString("usuario"));
		archivo.setIdPrestador(rs.getInt("id_prestador"));
		archivo.setCuitPrestador(rs.getString("cuit_prestador"));
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

	public Date getFechaEstado() {
		return fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	
	public String getFechaEstado_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaEstado != null ? sdf.format(fechaEstado): "";
	}
	
	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Integer getIdPrestador() {
		return idPrestador;
	}

	public void setIdPrestador(Integer idPrestador) {
		this.idPrestador = idPrestador;
	}

	public String getDescripcionPrestador() {
		return descripcionPrestador;
	}

	public void setDescripcionPrestador(String descripcionPrestador) {
		this.descripcionPrestador = descripcionPrestador;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getCuitPrestador() {
		return cuitPrestador;
	}

	public void setCuitPrestador(String cuitPrestador) {
		this.cuitPrestador = cuitPrestador;
	}
	
	
}

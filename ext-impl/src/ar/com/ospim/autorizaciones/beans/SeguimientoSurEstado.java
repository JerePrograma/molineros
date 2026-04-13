package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class SeguimientoSurEstado implements Serializable{
	private static final long serialVersionUID = 1891591677706025223L;
	private Integer id;
	private Integer idEstado;
	private String descripcionEstado;
	private Date fechaEstado;
	private String observaciones;
	private String usuario;
	private Integer idMotivo;
	private String descripcionMotivo;
	private Boolean actualizaFechaBaja;

	
	private Date baja_fecha;
	
	public static SeguimientoSurEstado getMapping(ResultSet rs) throws SQLException {
		SeguimientoSurEstado archivo = new SeguimientoSurEstado();
		archivo.setBaja_fecha(rs.getDate("baja_fecha"));
		archivo.setId(rs.getInt("id"));
		archivo.setIdEstado(rs.getInt("id_estado"));
		archivo.setDescripcionEstado(rs.getString("descripcion_estado"));
		archivo.setFechaEstado(rs.getDate("fecha_estado"));
		archivo.setObservaciones(rs.getString("observaciones"));
		archivo.setUsuario(rs.getString("usuario"));
		archivo.setDescripcionMotivo(rs.getString("descripcion_motivo"));
		archivo.setIdMotivo(rs.getInt("id_motivo"));
		archivo.setActualizaFechaBaja(rs.getBoolean("actualiza_baja_fecha"));
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

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public String getDescripcionEstado() {
		return descripcionEstado;
	}

	public void setDescripcionEstado(String descripcionEstado) {
		this.descripcionEstado = descripcionEstado;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Integer getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Integer idMotivo) {
		this.idMotivo = idMotivo;
	}

	public String getDescripcionMotivo() {
		return descripcionMotivo;
	}

	public void setDescripcionMotivo(String descripcionMotivo) {
		this.descripcionMotivo = descripcionMotivo;
	}

	public Boolean getActualizaFechaBaja() {
		return actualizaFechaBaja;
	}

	public void setActualizaFechaBaja(Boolean actualizaFechaBaja) {
		this.actualizaFechaBaja = actualizaFechaBaja;
	}

	
	
}

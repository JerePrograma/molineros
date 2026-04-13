package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Estado implements Serializable {

	private static final long serialVersionUID = -1570076894279256131L;
	private String id;
	private String descripcion;
	private Integer cantidadOcurrencias;
	private String motivoRechazo; 
	private String observacionesExternas; 
	private Date fecha;
	private Integer idSerial;
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Integer getIdSerial() {
		return idSerial;
	}

	public void setIdSerial(Integer idSerial) {
		this.idSerial = idSerial;
	}

	public Estado(){
		super();
	}
	
	public Estado(String id, String descripcion){
		super();
		this.id = id;
		this.descripcion = descripcion;
		
	}
	
	public Estado(String id, String descripcion, String motivoRechazo, String observExternas){
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.motivoRechazo = motivoRechazo;
		this.observacionesExternas = observExternas;
		
	}
	
	public Integer getCantidadOcurrencias() {
		return cantidadOcurrencias;
	}
	
	public void setCantidadOcurrencias(Integer cantidadOcurrencias) {
		this.cantidadOcurrencias = cantidadOcurrencias;
	}
	
	public static Estado getMapping(String prefix, ResultSet rs) throws SQLException {
		
		Estado estado = new Estado();
		estado.setId(rs.getString(prefix + "id"));
		estado.setDescripcion(rs.getString(prefix + "descripcion"));
		estado.setMotivoRechazo(rs.getString(prefix+"motivo_rechazo"));
		estado.setObservacionesExternas(rs.getString(prefix+"observaciones_externas"));
		return estado;
	
    }
	
	public String getMotivoRechazo() {
		return motivoRechazo;
	}
	
	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}

	public String getObservacionesExternas() {
		return observacionesExternas;
	}

	public void setObservacionesExternas(String observacionesExternas) {
		this.observacionesExternas = observacionesExternas;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	
}

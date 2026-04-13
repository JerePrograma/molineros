package ar.com.global.beans;

import java.io.Serializable;
import java.sql.Blob;

import org.w3c.dom.Document;

public class MensajeXMLBase implements Serializable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = -8891275328793761443L;
//	private int id;
//	private Date altaFecha;
	private String servicio;
	private int idReferencia;
//	private Blob mensaje;
	private String mensaje;
	
	
	
	public MensajeXMLBase(String servicio, int idReferencia, String mensaje) {
		super();
		this.servicio = servicio;
		this.idReferencia = idReferencia;
		this.mensaje = mensaje;
	}
	
	public String getServicio() {
		return servicio;
	}
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}
	public int getIdReferencia() {
		return idReferencia;
	}
	public void setIdReferencia(int idReferencia) {
		this.idReferencia = idReferencia;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
	
	
//	public MensajeXMLBase(String servicio, int idReferencia, Blob mensaje) {
//		super();
//		this.servicio = servicio;
//		this.idReferencia = idReferencia;
//		this.mensaje = mensaje;
//	}
//	
//	public String getServicio() {
//		return servicio;
//	}
//	public void setServicio(String servicio) {
//		this.servicio = servicio;
//	}
//	public int getIdReferencia() {
//		return idReferencia;
//	}
//	public void setIdReferencia(int idReferencia) {
//		this.idReferencia = idReferencia;
//	}
//	public Blob getMensaje() {
//		return mensaje;
//	}
//	public void setMensaje(Blob mensaje) {
//		this.mensaje = mensaje;
//	}
	 
	 
}


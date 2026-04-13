package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DestinatarioPorProceso implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7453071200210061889L;
	
	private int idProceso;
	private String nombreProceso;
	private String descripcionProceso;
	private String usuario;
	private String sector;
	private String edificio;
	private String correo;
	private String cabecera;
	
	public int getIdProceso() {
		return idProceso;
	}
	public void setIdProceso(int idProceso) {
		this.idProceso = idProceso;
	}
	public String getNombreProceso() {
		return nombreProceso;
	}
	public void setNombreProceso(String nombreProceso) {
		this.nombreProceso = nombreProceso;
	}
	public String getDescripcionProceso() {
		return descripcionProceso;
	}
	public void setDescripcionProceso(String descripcionProceso) {
		this.descripcionProceso = descripcionProceso;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getEdificio() {
		return edificio;
	}
	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getCabecera() {
		return cabecera;
	}
	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
	}
	
	public static DestinatarioPorProceso getMapping(String prefix, ResultSet rs) throws SQLException{
		 
		DestinatarioPorProceso dp = new DestinatarioPorProceso();
		dp.setCabecera(rs.getString(prefix +"cabecera"));
		dp.setCorreo(rs.getString(prefix +"email"));
		dp.setDescripcionProceso(rs.getString(prefix +"proceso_descripcion"));
		dp.setEdificio(rs.getString(prefix +"edificio"));
		dp.setIdProceso(rs.getInt(prefix +"id_proceso"));
		dp.setNombreProceso(rs.getString(prefix +"proceso_nombre"));
		dp.setSector(rs.getString(prefix +"sector"));
		dp.setUsuario(rs.getString(prefix +"usuario"));
		
		return dp;
	}
	
}

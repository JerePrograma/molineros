package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class NoAfiliado implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7763236829350181373L;
	
	private String documentoTipo;
	private String documentoNumero;
	private String apellido;
	private String nombre;
	private String telefono;
	private String email;
	private String altaUsr;
	private Date altaFecha;
	
	public String getDocumentoTipo() {
		return documentoTipo;
	}
	public void setDocumentoTipo(String documentoTipo) {
		this.documentoTipo = documentoTipo;
	}
	public String getDocumentoNumero() {
		return documentoNumero;
	}
	public void setDocumentoNumero(String documentoNumero) {
		this.documentoNumero = documentoNumero;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	@Override
	public String toString() {
		return "NoAfiliado [documentoTipo=" + documentoTipo
				+ ", documentoNumero=" + documentoNumero + ", apellido="
				+ apellido + ", nombre=" + nombre + "]";
	}
	
	public NoAfiliado(){
		super();
	}
	
	public NoAfiliado( String documentoTipo, String documentoNumero, 
			String apellido, String nombre, String telefono, String email, 
			String altaUsr, Date altaFecha){
		super();
		
		this.documentoTipo = documentoTipo;
		this.documentoNumero = documentoNumero;
		this.apellido = apellido;
		this.nombre = nombre;
		this.email = email;
		this.telefono = telefono;
		this.altaFecha = altaFecha;
		this.altaUsr = altaUsr;
	}
	
	public static NoAfiliado getMapping(String prefix, ResultSet rs) throws SQLException{
		
		NoAfiliado noa = new NoAfiliado(rs.getString("documento_tipo"), 
								rs.getString("documentoNumero"), 
								rs.getString("apellido"), 
								rs.getString("nombre"),
								rs.getString("telefono"),
								rs.getString("email"),
								rs.getString("altaUsr"), 
								rs.getDate("altaFecha"));
		
		return noa;
	}
	
	
}

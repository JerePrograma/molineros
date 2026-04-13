package ar.com.ospim.correspondencia.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Carlos Rivas
 * @version 1.0
 * @created 21-Feb-2013 03:11 a.m.
 */
public class UsuarioCorrespondencia implements Serializable {
			
	private static final long serialVersionUID = 3095329411059118898L;
	private int id;
	private String screenName;	
	private String apellido;
	private String nombre;
	private String sector;	
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String edificio;
		

	public UsuarioCorrespondencia() {		
	}
	
	public UsuarioCorrespondencia(int id, String screenName, String apellido,
			String nombre, String sector, Date altaFecha,
			String altaUsr, Date modiFecha, String modiUsr, Date bajaFecha,
			String bajaUsr, String edificio) {
		super();
		this.id = id;
		this.screenName = screenName;
		this.apellido = apellido;
		this.nombre = nombre;
		this.sector = sector;
		alta_fecha = altaFecha;
		alta_usr = altaUsr;
		modi_fecha = modiFecha;
		modi_usr = modiUsr;
		baja_fecha = bajaFecha;
		baja_usr = bajaUsr;
		this.edificio = edificio;
	}
	
	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public String getScreenName() {
		return screenName;
	}




	public void setScreenName(String screenName) {
		this.screenName = screenName;
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




	public String getSector() {
		return sector;
	}




	public void setSector(String sector) {
		this.sector = sector;
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



	public String getEdificio() {
		return edificio;
	}

	
	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}

	
	public static UsuarioCorrespondencia getMapping(ResultSet rs, String prefix)
			throws SQLException {
		UsuarioCorrespondencia ta = new UsuarioCorrespondencia();		
		ta.setId(rs.getInt(prefix + "id"));
		ta.setScreenName(rs.getString(prefix + "screenname"));
		ta.setNombre(rs.getString(prefix + "name"));
		ta.setApellido(rs.getString(prefix + "lastname"));		
		ta.setSector(rs.getString(prefix + "sector"));
		ta.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ta.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ta.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ta.setModi_usr(rs.getString(prefix + "modi_usr"));
		ta.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ta.setBaja_usr(rs.getString(prefix + "baja_usr"));
		ta.setEdificio(rs.getString(prefix + "edificio"));
		return ta;
	}
}
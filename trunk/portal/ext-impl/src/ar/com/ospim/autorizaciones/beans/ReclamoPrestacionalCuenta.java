package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

import ar.com.ospim.util.StringUtils;

public class ReclamoPrestacionalCuenta  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String cmbTitular;
	private String cbu;
	private String cuil;
	private String email;
	private int idReclamoPrestacional;
	private String apellido;
	private String nombre;
	private String imagenCBU;
	private String imagenNotaAutorizada;
	private Date altaFecha;
	private String altaUsr;
	private String cuilGrupoFamiliar;
			
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCmbTitular() {
		return cmbTitular;
	}
	public void setCmbTitular(String cmbTitular) {
		this.cmbTitular = cmbTitular;
	}
	public String getCbu() {
		return cbu;
	}
	public void setCbu(String cbu) {
		this.cbu = cbu;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getIdReclamoPrestacional() {
		return idReclamoPrestacional;
	}
	public void setIdReclamoPrestacional(int idReclamoPrestacional) {
		this.idReclamoPrestacional = idReclamoPrestacional;
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
	public String getImagenCBU() {
		return imagenCBU;
	}
	public void setImagenCBU(String imagenCBU) {
		this.imagenCBU = imagenCBU;
	}
	public String getImagenNotaAutorizada() {
		return imagenNotaAutorizada;
	}
	public void setImagenNotaAutorizada(String imagenNotaAutorizada) {
		this.imagenNotaAutorizada = imagenNotaAutorizada;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	
	
	public String getCuilGrupoFamiliar() {
		return cuilGrupoFamiliar;
	}
	public void setCuilGrupoFamiliar(String cuilGrupoFamiliar) {
		this.cuilGrupoFamiliar = cuilGrupoFamiliar;
	}
	
	public static ReclamoPrestacionalCuenta getMapping(ResultSet rs, String prefix)
			throws Exception {

		
		ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();

		cuenta.setId(rs.getInt(prefix + "id"));
		cuenta.setCuil(rs.getString(prefix + "cuil"));	
		cuenta.setEmail(rs.getString(prefix + "email") != null && !StringUtils.checkEmpty(rs.getString(prefix + "email")) ? rs.getString(prefix + "email") : "");	
		cuenta.setCbu(rs.getString(prefix + "cbu"));
		cuenta.setCmbTitular(rs.getString(prefix + "chk_titular"));
		cuenta.setIdReclamoPrestacional(rs.getInt(prefix + "id_reclamo_prestacional"));
		cuenta.setImagenCBU(rs.getString(prefix + "file_cbu") != null && !StringUtils.checkEmpty(rs.getString(prefix + "file_cbu")) ?  rs.getString(prefix + "file_cbu") : "" );
		cuenta.setImagenNotaAutorizada(rs.getString(prefix + "file_nota_autorizada") != null && !StringUtils.checkEmpty(rs.getString(prefix + "file_nota_autorizada")) ? rs.getString(prefix + "file_nota_autorizada") : "");
		cuenta.setApellido(rs.getString(prefix + "apellido"));
		cuenta.setNombre(rs.getString(prefix + "nombre"));
		cuenta.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		cuenta.setAltaUsr(rs.getString(prefix + "alta_usr"));
	
		

		return cuenta;
	}
	
	
}

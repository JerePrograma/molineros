package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AfiCuentaBancaria {

	private int id;
	private int idReclamo;
	private String cboTitular;
	private String cbu;
	private String email;
	private String cuil;
	private String apellido;
	private String nombre;
	private String adjClaveCBU;
	private String adjClaveNota;
	private String altaUsr;
	private Date altaFecha;
	private String cuilGrupoFamilial;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdReclamo() {
		return idReclamo;
	}
	public void setIdReclamo(int idReclamo) {
		this.idReclamo = idReclamo;
	}
	public String getAdjClaveCBU() {
		return adjClaveCBU;
	}
	public void setAdjClaveCBU(String adjClaveCBU) {
		this.adjClaveCBU = adjClaveCBU;
	}
	public String getAdjClaveNota() {
		return adjClaveNota;
	}
	public void setAdjClaveNota(String adjClaveNota) {
		this.adjClaveNota = adjClaveNota;
	}
	public String getCboTitular() {
		return cboTitular;
	}
	public void setCboTitular(String cboTitular) {
		this.cboTitular = cboTitular;
	}
	public String getCbu() {
		return cbu;
	}
	public void setCbu(String cbu) {
		this.cbu = cbu;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
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
	
	public String getCuilGrupoFamilial() {
		return cuilGrupoFamilial;
	}
	public void setCuilGrupoFamilial(String cuilGrupoFamilial) {
		this.cuilGrupoFamilial = cuilGrupoFamilial;
	}
	
	public static AfiCuentaBancaria getMapping(ResultSet rs ,String prefix ) throws SQLException {
		

		AfiCuentaBancaria cuenta = new AfiCuentaBancaria();
		
		cuenta.setId(rs.getInt(prefix + "id"));
		cuenta.setIdReclamo(rs.getInt(prefix + "id_reclamo_prestacional"));
		cuenta.setCboTitular(rs.getString(prefix + "chk_titular"));
		cuenta.setCuil(rs.getString(prefix + "cuil"));
		cuenta.setCbu(rs.getString(prefix + "cbu"));
		cuenta.setEmail(rs.getString(prefix + "email"));
		cuenta.setAdjClaveCBU(rs.getString(prefix + "file_cbu"));
		cuenta.setAdjClaveNota(rs.getString(prefix + "file_nota_autorizada"));
		cuenta.setApellido(rs.getString(prefix + "apellido"));
		cuenta.setNombre(rs.getString(prefix + "nombre"));
		cuenta.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		cuenta.setAltaUsr(rs.getString(prefix + "alta_usr"));
		cuenta.setCuilGrupoFamilial(rs.getString(prefix + "cuil_grupo_familiar"));
				
		return cuenta;
	}
	
	

}

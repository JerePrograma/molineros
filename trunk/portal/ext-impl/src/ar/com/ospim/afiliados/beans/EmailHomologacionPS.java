package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailHomologacionPS {

	private int  idProvinciaSuper;
	private String provinciaDesc;
	private int idLocalidad_sss;
	private String  localidadDesc;
	private String codigoPostal;
	private String discapacitado;
    private String tipoDocumento;
    private int idNacionalidadSuper;
    private String nacionalidad;
    private int idParentesco; 
    private String parentesco;
    private int idEstadoCivilSuper;
    private String estadoCivil;
    private String mensaje;
	
	
	public String getDiscapacitado() {
		return discapacitado;
	}

	
	public String getNacionalidad() {
		return nacionalidad;
	}

	public String getParentesco() {
		return parentesco;
	}
	public int getIdEstadoCivilSuper() {
		return idEstadoCivilSuper;
	}
	public String getEstadoCivil() {
		return estadoCivil;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}
		
	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	
	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}
	public void setIdEstadoCivilSuper(int idEstadoCivilSuper) {
		this.idEstadoCivilSuper = idEstadoCivilSuper;
	}
	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	public String getProvinciaDesc() {
		return provinciaDesc;
	}
	public int getIdLocalidad_sss() {
		return idLocalidad_sss;
	}
	public String getLocalidadDesc() {
		return localidadDesc;
	}
	public String getCodigoPostal() {
		return codigoPostal;
	}
	public void setProvinciaDesc(String provinciaDesc) {
		this.provinciaDesc = provinciaDesc;
	}
	public void setIdLocalidad_sss(int idLocalidad_sss) {
		this.idLocalidad_sss = idLocalidad_sss;
	}
	public void setLocalidadDesc(String localidadDesc) {
		this.localidadDesc = localidadDesc;
	}
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	
	public static EmailHomologacionPS getMapping(ResultSet rs) throws SQLException{
		EmailHomologacionPS eh = new EmailHomologacionPS();
		
		eh.setIdProvinciaSuper(rs.getInt("id_provincia_sss"));
		eh.setProvinciaDesc(rs.getString("provincia_desc"));
		eh.setIdLocalidad_sss(rs.getInt("id_localidad_sss"));
		eh.setLocalidadDesc(rs.getString("localidad_desc"));
		eh.setCodigoPostal(rs.getString("codigo_postal"));
		eh.setDiscapacitado(rs.getString("discapacitado"));
		eh.setTipoDocumento(rs.getString("documento_tipo"));
		eh.setIdNacionalidadSuper(rs.getInt("id_nacionalidad_sss"));
		eh.setNacionalidad(rs.getString("nacionalidad"));
		eh.setIdParentesco(rs.getInt("parentesco_id"));
		eh.setParentesco(rs.getString("parentesco"));
		eh.setIdEstadoCivilSuper(rs.getInt("id_estado_civil_sss"));
		eh.setEstadoCivil(rs.getString("estado_civil"));
		eh.setMensaje(rs.getString("mensaje"));
		
		return eh;
		
		
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}


	public int getIdNacionalidadSuper() {
		return idNacionalidadSuper;
	}


	public void setIdNacionalidadSuper(int idNacionalidadSuper) {
		this.idNacionalidadSuper = idNacionalidadSuper;
	}


	public int getIdParentesco() {
		return idParentesco;
	}


	public void setIdParentesco(int idParentesco) {
		this.idParentesco = idParentesco;
	}


	public int getIdProvinciaSuper() {
		return idProvinciaSuper;
	}


	public void setIdProvinciaSuper(int idProvinciaSuper) {
		this.idProvinciaSuper = idProvinciaSuper;
	}


}

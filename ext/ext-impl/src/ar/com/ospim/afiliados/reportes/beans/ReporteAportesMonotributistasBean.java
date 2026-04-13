package ar.com.ospim.afiliados.reportes.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.tesoreria.beans.ReporteIngresosDevengadosBean;



public class ReporteAportesMonotributistasBean {

	private String cuilTitular;
	private String apellido;
	private String nombre;
	private String calle;
	private String numero;
	private String piso;
	private String depto;
	private String barrio;
	private String localidad;
	private String provincia;
	private String codPostal;
	private String telefono;
	private String codAreaCelular;
	private String celular;
	private String codAreaTelLaboral;
	private String telLaboral;
	private String eMail;
	private String categoria;
	private Integer integrantes;
	private Double aportesEstimados;
	private Double aportesPercibidos;
	private Double diferencia;
	
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
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
	public Double getAportesEstimados() {
		return aportesEstimados;
	}
	public void setAportesEstimados(Double aportesEstimados) {
		this.aportesEstimados = aportesEstimados;
	}
	public Double getAportesPercibidos() {
		return aportesPercibidos;
	}
	public void setAportesPercibidos(Double aportesPercibidos) {
		this.aportesPercibidos = aportesPercibidos;
	}
		
	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
	public String getDepto() {
		return depto;
	}
	public void setDepto(String depto) {
		this.depto = depto;
	}
	public String getBarrio() {
		return barrio;
	}
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
		
	public String getCodPostal() {
		return codPostal;
	}
	public void setCodPostal(String codPostal) {
		this.codPostal = codPostal;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getCodAreaCelular() {
		return codAreaCelular;
	}
	public void setCodAreaCelular(String codAreaCelular) {
		this.codAreaCelular = codAreaCelular;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getCodAreaTelLaboral() {
		return codAreaTelLaboral;
	}
	public void setCodAreaTelLaboral(String codAreaTelLaboral) {
		this.codAreaTelLaboral = codAreaTelLaboral;
	}
	public String getTelLaboral() {
		return telLaboral;
	}
	public void setTelLaboral(String telLaboral) {
		this.telLaboral = telLaboral;
	}
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public Integer getIntegrantes() {
		return integrantes;
	}
	public void setIntegrantes(Integer integrantes) {
		this.integrantes = integrantes;
	}
	public Double getDiferencia() {
		return diferencia;
	}
	public void setDiferencia(Double diferencia) {
		this.diferencia = diferencia;
	}
	public static ReporteAportesMonotributistasBean getMapping(ResultSet rs) throws SQLException {
		ReporteAportesMonotributistasBean a = new ReporteAportesMonotributistasBean();
	    
		a.setCuilTitular(rs.getString("cuil_titular"));
		a.setApellido(rs.getString("apellido"));
		a.setNombre(rs.getString("nombre"));
		a.setCalle(rs.getString("calle"));
		a.setNumero(rs.getString("numero"));
		a.setPiso(rs.getString("piso"));
		a.setDepto(rs.getString("depto"));
		a.setBarrio(rs.getString("barrio"));
		a.setLocalidad(rs.getString("localidad")); 
		a.setProvincia(rs.getString("provincia"));
		a.setCodPostal(rs.getString("postal_codi"));
		a.setTelefono(rs.getString("telefono"));
		a.setCodAreaCelular(rs.getString("cod_area_celular"));
		a.setCelular(rs.getString("celular"));
		a.setCodAreaTelLaboral(rs.getString("cod_area_tel_laboral"));
		a.setTelLaboral(rs.getString("tel_laboral"));
		a.seteMail(rs.getString("email"));
		a.setCategoria(rs.getString("categoria"));
		a.setIntegrantes(rs.getInt("integrantes"));
		a.setAportesEstimados(rs.getDouble("aportes_estimados"));
		a.setAportesPercibidos(rs.getDouble("aportes_percibidos"));
		a.setDiferencia(rs.getDouble("diferencia"));
		
	   return a;
    }
	
}

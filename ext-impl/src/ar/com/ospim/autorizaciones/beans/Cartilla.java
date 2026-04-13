package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ar.com.ospim.afiliados.beans.AporteAfiliado;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class Cartilla implements Serializable{

	
	private String tipo;
	private String prestador;
	private String plan;
	private String localidad;
	private String provincia;
	private String especialidad;
	private String trabajaen;
	private String domicilio;
	private String telefono;
	private Date bajaFecha;
	private Date vigenciaDesde;
	private Date vigenciaHasta;
	private Integer id;
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getPrestador() {
		return prestador;
	}
	public void setPrestador(String prestador) {
		this.prestador = prestador;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
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
	public String getEspecialidad() {
		return especialidad;
	}
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}
	public String getTrabajaen() {
		return trabajaen;
	}
	public void setTrabajaen(String trabajaen) {
		this.trabajaen = trabajaen;
	}
	public String getDomicilio() {
		return domicilio;
	}
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	public Date getVigenciaDesde() {
		return vigenciaDesde;
	}
	public void setVigenciaDesde(Date vigenciaDesde) {
		this.vigenciaDesde = vigenciaDesde;
	}
	public Date getVigenciaHasta() {
		return vigenciaHasta;
	}
	public void setVigenciaHasta(Date vigenciaHasta) {
		this.vigenciaHasta = vigenciaHasta;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getBaja_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return bajaFecha != null ? sdf.format(bajaFecha)
				: "";
	}
	
	public static Cartilla getMapping(ResultSet rs) throws SQLException{
		Cartilla c = new Cartilla();
		c.setDomicilio(rs.getString("domicilio"));
		c.setEspecialidad(rs.getString("especialidad"));
		c.setLocalidad(rs.getString("localidad"));
		c.setPlan(rs.getString("plan"));
		c.setPrestador(rs.getString("prestador"));
		c.setProvincia(rs.getString("provincia"));
		c.setTelefono(rs.getString("telefono"));
		c.setTipo(rs.getString("tipo"));
		c.setTrabajaen(rs.getString("trabaja_en"));
		c.setVigenciaDesde(rs.getDate("vigen_desde"));
		c.setVigenciaHasta(rs.getDate("vigen_hasta"));
		c.setBajaFecha(rs.getDate("baja_fecha"));
		c.setId(rs.getInt("id"));
		return c;
	}
	
}

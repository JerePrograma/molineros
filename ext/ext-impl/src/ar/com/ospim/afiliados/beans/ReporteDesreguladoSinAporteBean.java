package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporteDesreguladoSinAporteBean {
	private String cuil;
	private Integer inte;
	private String nombre;
	private String apellido;
	private String cuit;
	private String razon_social;
	private Date fecha_vigencia;
	private String plan;
	private String seccional;
	private String categoria;
	private String tercerizadora;
	
		
	public static ReporteDesreguladoSinAporteBean getMapping(ResultSet rs) throws SQLException{
		ReporteDesreguladoSinAporteBean rdsab= new ReporteDesreguladoSinAporteBean();
		rdsab.setCuil(rs.getString("cuil"));
		rdsab.setInte(rs.getInt("inte"));
		rdsab.setApellido(rs.getString("apellido"));
		rdsab.setNombre(rs.getString("nombre"));
		rdsab.setFecha_vigencia(rs.getDate("vigen_fecha"));
		rdsab.setPlan(rs.getString("plan"));
		rdsab.setSeccional(rs.getString("seccional"));
		rdsab.setCategoria(rs.getString("categoria"));
		rdsab.setRazon_social(rs.getString("empresa"));
		rdsab.setTercerizadora(rs.getString("id_tercerizadora"));		
		return rdsab;
	}
	
	/**
	 * @return the cuil
	 */
	public String getCuil() {
		return cuil;
	}
	/**
	 * @param cuil the cuil to set
	 */
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return the apellido
	 */
	public String getApellido() {
		return apellido;
	}
	/**
	 * @param apellido the apellido to set
	 */
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	/**
	 * @return the cuit
	 */
	public String getCuit() {
		return cuit;
	}
	/**
	 * @param cuit the cuit to set
	 */
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	/**
	 * @return the razon_social
	 */
	public String getRazon_social() {
		return razon_social;
	}
	/**
	 * @param razonSocial the razon_social to set
	 */
	public void setRazon_social(String razonSocial) {
		razon_social = razonSocial;
	}
	
	
	public Date getFecha_vigencia() {
		return fecha_vigencia;
	}
	
	public String getFecha_vigencia_as_String() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fecha_vigencia!=null?sdf.format(fecha_vigencia):"";
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getTercerizadora() {
		return tercerizadora;
	}

	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}

	public void setFecha_vigencia(Date fecha_vigencia) {
		this.fecha_vigencia = fecha_vigencia;
	}

	public Integer getInte() {
		return inte;
	}

	public void setInte(Integer inte) {
		this.inte = inte;
	}
	
	
	
}
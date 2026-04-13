package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AfiSuspencionCobertura implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1343984078151342910L;
	
	private Integer id;
	private Date vigenDesde;
	private Date vigenHasta;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	
	
	public AfiSuspencionCobertura() {
		super();
		
	}
	
	public AfiSuspencionCobertura(Integer id, Date vigenDesde, Date vigenHasta) {
		super();
		this.id = id;
		this.vigenDesde = vigenDesde;
		this.vigenHasta = vigenHasta;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Date getVigenDesde() {
		return vigenDesde;
	}
	
	public void setVigenDesde(Date vigenDesde) {
		this.vigenDesde = vigenDesde;
	}
	
	public Date getVigenHasta() {
		return vigenHasta;
	}
	
	public void setVigenHasta(Date vigenHasta) {
		this.vigenHasta = vigenHasta;
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
	
	public Date getModiFecha() {
		return modiFecha;
	}
	
	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}
	
	public String getModiUsr() {
		return modiUsr;
	}
	
	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}
	
	public Date getBajaFecha() {
		return bajaFecha;
	}
	
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	
	public String getBajaUsr() {
		return bajaUsr;
	}
	
	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}
	
	public static AfiSuspencionCobertura getMapping(String prefix, ResultSet rs) throws SQLException {
		
		AfiSuspencionCobertura asc = new AfiSuspencionCobertura();
		
		asc.setId(rs.getInt(prefix+"id"));
		asc.setVigenDesde(rs.getDate(prefix+"vigen_desde"));
		asc.setVigenHasta(rs.getDate(prefix+"vigen_hasta"));
		asc.setAltaFecha(rs.getTimestamp(prefix+"alta_fecha"));
		asc.setAltaUsr(rs.getString(prefix+"alta_usr"));
		asc.setModiFecha(rs.getTimestamp(prefix+"modi_fecha"));
		asc.setModiUsr(rs.getString(prefix+"modi_usr"));
		asc.setBajaFecha(rs.getTimestamp(prefix+"baja_fecha"));
		asc.setBajaUsr(rs.getString(prefix+"baja_usr"));
		
		return asc;
		
	}
	
	
}

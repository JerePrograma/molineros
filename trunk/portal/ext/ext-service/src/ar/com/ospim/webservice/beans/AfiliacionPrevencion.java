package ar.com.ospim.webservice.beans;

import java.util.Date;

import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

public class AfiliacionPrevencion extends AfiliacionPrevencionDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5410086618928109455L;
	
	private Integer inte;
	private Date altaFecha;
	private Date modiFecha;
	private Date bajaFecha;
	private String altaUsr;
	private String modiUsr;
	private String bajaUsr;
	
	public Integer getInte() {
		return inte;
	}
	public void setInte(Integer inte) {
		this.inte = inte;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public Date getModiFecha() {
		return modiFecha;
	}
	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public String getModiUsr() {
		return modiUsr;
	}
	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}
	public String getBajaUsr() {
		return bajaUsr;
	}
	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

}

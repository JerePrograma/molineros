package ar.com.ospim.procesaArchivos.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 
 * @author sergio
 * 
 */

public class ArchivoARBAPadronAlicuota implements Serializable { /**
	 * 
	 */
	private static final long serialVersionUID = 5962875041258345481L;
	private SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
	
	private String cuit;
	private Date vigenciaDesde;
	private Date vigenciaHasta;
	private String regimen; //R - retencion  ---- P -percepcion
	private Double alicuota;
	private String operacion;
	
		
	private String altaUsr;
	private Date altaFecha;
	private String modiUsr;
	private Date modiFecha;
	private String bajaUsr;
	private Date bajaFecha;
	
	
	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRegimen() {
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public Double getAlicuota() {
		return alicuota;
	}

	public void setAlicuota(Double alicuota) {
		this.alicuota = alicuota;
	}
	
	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public ArchivoARBAPadronAlicuota() {
		super();
	}
	
//	1002017036821;30710363761;2018;100;CERTIFICADOS DE EXCLUSIÓN;Definitivo SIN Provisorio;0000000;01/01/2018;01/01/2018;31/12/2018
	public ArchivoARBAPadronAlicuota(String[] vLine) {
		super();
		this.cuit = vLine[1];
		try {
			this.regimen=vLine[0];
			this.cuit=vLine[4];
			this.operacion=vLine[6];
			this.alicuota=Double.parseDouble(vLine[8].replace(",","."));
			this.vigenciaDesde = sdf.parse(vLine[2]);
			this.vigenciaHasta = sdf.parse(vLine[3]);
		} catch (Exception e) {
		}
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
	public String getModiUsr() {
		return modiUsr;
	}
	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}
	public Date getModiFecha() {
		return modiFecha;
	}
	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}
	public String getBajaUsr() {
		return bajaUsr;
	}
	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

}

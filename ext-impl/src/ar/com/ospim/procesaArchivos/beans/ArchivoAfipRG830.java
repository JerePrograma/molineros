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

public class ArchivoAfipRG830 implements Serializable { //Afip exceptuados retencion ganancias 

	/**
	 * 
	 */
	private static final long serialVersionUID = 28264585178476950L;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private String credencialNumero;
	private String credencialDescripcion;
	private String credencialAlcance;
	private int porcentajeExcep;
	private String cuit;
	private int anio;
	private int nro;
	private Date fechaPresentacion; 
	private Date vigenciaDesde;
	private Date vigenciaHasta;
	private String altaUsr;
	private Date altaFecha;
	private String modiUsr;
	private Date modiFecha;
	private String bajaUsr;
	private Date bajaFecha;
	
	
	public ArchivoAfipRG830() {
		super();
	}
	
//	1002017036821;30710363761;2018;100;CERTIFICADOS DE EXCLUSIÓN;Definitivo SIN Provisorio;0000000;01/01/2018;01/01/2018;31/12/2018
	public ArchivoAfipRG830(String[] vLine) {
		super();
		this.credencialNumero = vLine[0];
		this.credencialDescripcion = vLine[4];
		this.credencialAlcance = vLine[5];
		this.porcentajeExcep = Integer.parseInt(vLine[3]);
		this.cuit = vLine[1];
		this.anio = Integer.parseInt(vLine[2]);
		this.nro = Integer.parseInt(vLine[6]);
		try {
			this.fechaPresentacion =  sdf.parse(vLine[7]);
		} catch (ParseException e) {
		}
		try {
			this.vigenciaDesde = sdf.parse(vLine[8]);
		} catch (ParseException e) {
		}
		try {
			this.vigenciaHasta = sdf.parse(vLine[9]);
		} catch (ParseException e) {
		}
	}
	
	public String getCredencialNumero() {
		return credencialNumero;
	}
	public void setCredencialNumero(String credencialNumero) {
		this.credencialNumero = credencialNumero;
	}
	public String getCredencialDescripcion() {
		return credencialDescripcion;
	}
	public void setCredencialDescripcion(String credencialDescripcion) {
		this.credencialDescripcion = credencialDescripcion;
	}
	public String getCredencialAlcance() {
		return credencialAlcance;
	}
	public void setCredencialAlcance(String credencialAlcance) {
		this.credencialAlcance = credencialAlcance;
	}
	public int getPorcentajeExcep() {
		return porcentajeExcep;
	}
	public void setPorcentajeExcep(int porcentajeExcep) {
		this.porcentajeExcep = porcentajeExcep;
	}
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public int getNro() {
		return nro;
	}
	public void setNro(int nro) {
		this.nro = nro;
	}
	public Date getFechaPresentacion() {
		return fechaPresentacion;
	}
	public void setFechaPresentacion(Date fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
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

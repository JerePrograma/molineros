package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;

public abstract class Ingreso {
	protected Date alta_fecha;
	protected String alta_usr;
	protected String alta_ip;
	protected Date modi_fecha;
	protected String modi_usr;
	protected String modi_ip;
	protected Date baja_fecha;
	protected String baja_usr;
	protected String baja_ip;
	private String nroRecibo;
	private Date fechaRecibo;
	protected int convenioId;
	protected int actaId;


	public abstract Date getFecha();

	public abstract String getFechaAsString();

	public abstract BigDecimal getImporte();

	public abstract String getNumeroStr();

	public abstract Banco getBanco();

	public abstract CuentaBancaria getCuentaBancaria();

	public abstract boolean isNew();

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public abstract void setCuit(String cuit);

	public abstract String getTipo();
	
	public abstract Integer getEmisor();
	
	public abstract void setEmisor(Integer emisor);
	
	public abstract String getEmisorDescripcion();
	
	public abstract void setEmisorDescripcion(String emisorDescripcion);
	
    public abstract int getCuotas();
	
	public abstract void setCuotas(int cuotas);

	public abstract int saveIngreso(ReciboGlobalServiceImpl instance, Recibo recibo,
			String user, Connection con, int amtima) throws SystemException,
			DuplicateNumeroChequeException;
		
		
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public String getAlta_ip() {
		return alta_ip;
	}

	public void setAlta_ip(String altaIp) {
		alta_ip = altaIp;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public String getModi_ip() {
		return modi_ip;
	}

	public void setModi_ip(String modiIp) {
		modi_ip = modiIp;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	public void setBaja_ip(String baja_ip) {
		this.baja_ip = baja_ip;
	}

	public String getBaja_ip() {
		return baja_ip;
	}

	public String getNroRecibo() {
		return nroRecibo;
	}

	public void setNroRecibo(String nroRecibo) {
		this.nroRecibo = nroRecibo;
	}

	public Date getFechaRecibo() {
		return fechaRecibo;
	}

	public void setFechaRecibo(Date fechaRecibo) {
		this.fechaRecibo = fechaRecibo;
	}

	public int getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(int convenioId) {
		this.convenioId = convenioId;
	}

	public int getActaId() {
		return actaId;
	}

	public void setActaId(int actaId) {
		this.actaId = actaId;
	}
		

}

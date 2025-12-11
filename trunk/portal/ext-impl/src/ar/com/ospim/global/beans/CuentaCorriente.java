package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;

public class CuentaCorriente extends Ingreso {
	public static final int CTACTE = 8;
	private BigDecimal importe;
	private Date fecha;
	transient private String cuit;
	private String nroRecibo;
	private Date fechaRecibo;

	public CuentaCorriente() {

	}

	public CuentaCorriente(BigDecimal importe) {
		this.importe = importe;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getFechaAsString() {
		return null != fecha ? DateUtils.format(fecha, DateUtils.SHORT) : "";
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumeroStr() {
		return "";
	}

	public Banco getBanco() {
		return null;
	}

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

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((importe == null) ? 0 : importe.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CuentaCorriente other = (CuentaCorriente) obj;
		if (importe == null) {
			if (other.importe != null)
				return false;
		} else if (!importe.equals(other.importe))
			return false;
		return true;
	}

	public boolean isNew() {
		return alta_fecha == null;
	}

	public int saveIngreso(ReciboGlobalServiceImpl instance, Recibo recibo,
			String user, Connection con, int entidad) throws SystemException,
			DuplicateNumeroChequeException {
//		return instance.save(this, recibo, user, con, entidad);
		return 0;
	}

	public static CuentaCorriente getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static CuentaCorriente getMapping(ResultSet rs, String prefix)
			throws SQLException {
		CuentaCorriente ef = new CuentaCorriente();
		ef.setImporte(rs.getBigDecimal(prefix + "importe"));
		ef.setFecha(rs.getDate(prefix + "fecha"));
		ef.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ef.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ef.setModi_usr(rs.getString(prefix + "modi_usr"));
		ef.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ef.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ef.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return ef;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;

	}

	public String getCuit() {
		return cuit;
	}


	@Override
	public CuentaBancaria getCuentaBancaria() {
		return null;
	};

	public String getTipo() {
		return "CuentaCorriente";
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
	
	public String getFechaReciboAsString(){
		return null != fechaRecibo ? DateUtils.format(fechaRecibo, DateUtils.SHORT) : "";
	}

	@Override
	public Integer getEmisor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmisor(Integer emisor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEmisorDescripcion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmisorDescripcion(String emisorDescripcion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCuotas() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCuotas(int cuotas) {
		// TODO Auto-generated method stub
		
	}

		
}

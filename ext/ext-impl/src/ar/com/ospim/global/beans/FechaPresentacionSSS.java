package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

public class FechaPresentacionSSS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Date fechaPresSSS;
	private String altaUsr;
	private Date AltaFechaUsr;
	private Date ultimaFechaPressOpc;
	
	
	public Date getFechaPresSSS() {
		return fechaPresSSS;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public Date getAltaFechaUsr() {
		return AltaFechaUsr;
	}
	public void setFechaPresSSS(Date fechaPresSSS) {
		this.fechaPresSSS = fechaPresSSS;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public void setAltaFechaUsr(Date altaFechaUsr) {
		AltaFechaUsr = altaFechaUsr;
	}
	
	public Date getUltimaFechaPressOpc() {
		return ultimaFechaPressOpc;
	}
	public void setUltimaFechaPressOpc(Date ultimaFechaPressOpc) {
		this.ultimaFechaPressOpc = ultimaFechaPressOpc;
	}
	
	public static FechaPresentacionSSS getMapping(ResultSet rs) throws Exception{
		FechaPresentacionSSS ap = new FechaPresentacionSSS();
		
		ap.setAltaUsr(rs.getString("alta_user"));
		ap.setFechaPresSSS(rs.getDate("fecha_opcion_sss"));
		ap.setAltaFechaUsr(rs.getTimestamp("alta_fecha"));
	
		
		return ap;
	}
	
	

	
	
}

package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AfiObservacion implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8227448911660756208L;
	private int id;
	private String cuilTitular;
	private int inte;
	private String observacion;
	private String altaUsr;
	private Date altaFecha;
	private String bajaUsr;
	private Date bajaFecha;
	
	public AfiObservacion(int id, String cuilTitular, int inte, String observacion, String altaUsr, Date altaFecha,
			String bajaUsr, Date bajaFecha) {
		super();
		this.id = id;
		this.cuilTitular = cuilTitular;
		this.inte = inte;
		this.observacion = observacion;
		this.altaUsr = altaUsr;
		this.altaFecha = altaFecha;
		this.bajaUsr = bajaUsr;
		this.bajaFecha = bajaFecha;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
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
	
	public static AfiObservacion getMapping(ResultSet rs, String prefix) throws SQLException {
		
		AfiObservacion obsInt = new AfiObservacion(rs.getInt(prefix +"id"), 
				rs.getString(prefix +"cuil_titular"),
				rs.getInt(prefix+"inte"), rs.getString(prefix+"observacion"), 
				rs.getString(prefix + "alta_usr"), rs.getTimestamp(prefix + "alta_fecha"),
				rs.getString(prefix + "baja_usr"), rs.getTimestamp(prefix + "baja_fecha"));
				
		return obsInt;
	}
	
	
	
	
}

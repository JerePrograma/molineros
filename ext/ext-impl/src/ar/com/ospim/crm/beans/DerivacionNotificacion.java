package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DerivacionNotificacion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5741207622028816446L;
	
	private int id;
	private String derivacionUsr;
	private String derivacionSector;
	private String derivacionEdificio;
	private String derivacionEmail;
	private String responsableUsr;
	private String responsableEmail;
	private String derivacionMensaje;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDerivacionUsr() {
		return derivacionUsr;
	}
	public void setDerivacionUsr(String derivacionUsr) {
		this.derivacionUsr = derivacionUsr;
	}
	public String getDerivacionSector() {
		return derivacionSector;
	}
	public void setDerivacionSector(String derivacionSector) {
		this.derivacionSector = derivacionSector;
	}
	public String getDerivacionEdificio() {
		return derivacionEdificio;
	}
	public void setDerivacionEdificio(String derivacionEdificio) {
		this.derivacionEdificio = derivacionEdificio;
	}
	public String getDerivacionEmail() {
		return derivacionEmail;
	}
	public void setDerivacionEmail(String derivacionEmail) {
		this.derivacionEmail = derivacionEmail;
	}
	public String getResponsableUsr() {
		return responsableUsr;
	}
	public void setResponsableUsr(String responsableUsr) {
		this.responsableUsr = responsableUsr;
	}
	public String getResponsableEmail() {
		return responsableEmail;
	}
	public void setResponsableEmail(String responsableEmail) {
		this.responsableEmail = responsableEmail;
	}
	public String getDerivacionMensaje() {
		return derivacionMensaje;
	}
	public void setDerivacionMensaje(String derivacionMensaje) {
		this.derivacionMensaje = derivacionMensaje;
	}
	
	@Override
	public String toString() {
		return "DerivacionNotificacion [derivacionUsr=" + derivacionUsr
				+ ", derivacionSector=" + derivacionSector
				+ ", derivacionEdificio=" + derivacionEdificio
				+ ", derivacionEmail=" + derivacionEmail + "]";
	}
	
	public DerivacionNotificacion(){
		super();
	}
	
	public DerivacionNotificacion(int id, String derivacionUsr, String derivacionSector, String derivacionEdificio, 
			String derivacionEmail, String responsableUsr, String responsableEmail, String derivacionMensaje){
		
		super();
		this.id = id;
		this.derivacionEdificio = derivacionEdificio;
		this.derivacionEmail = derivacionEmail;
		this.derivacionMensaje = derivacionMensaje;
		this.derivacionSector = derivacionSector;
		this.derivacionUsr = derivacionUsr;
		this.responsableEmail = responsableEmail;
		this.responsableUsr = responsableUsr;

	}
	
	public static DerivacionNotificacion getMapping(String prefix, ResultSet rs) throws SQLException{
		
		DerivacionNotificacion dn = new DerivacionNotificacion();
		
		dn.setDerivacionEdificio(rs.getString(prefix + "derivacion_edificio"));
		dn.setDerivacionEmail(rs.getString(prefix + "derivacion_email"));
		dn.setDerivacionMensaje(rs.getString(prefix + "derivacion_mensaje"));
		dn.setDerivacionSector(rs.getString(prefix + "derivacion_sector"));
		dn.setDerivacionUsr(rs.getString(prefix + "derivacion_usr"));
		dn.setId(rs.getInt(prefix + "id"));
		dn.setResponsableEmail(rs.getString(prefix + "responsable_email"));
		dn.setResponsableUsr(rs.getString(prefix + "responsable_usr"));

		return dn;
	}
	
	
}

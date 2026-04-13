package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class CRMEficacia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -995185513642623989L;
	
	private Integer id;
	private Integer idContacto;
	private String contacto_a;
	private boolean conforme;
	private String observaciones;
	private Date altaFecha;
	private String altaUsr;
	private String altaSector;
	private Date modiFecha;
	private String modiUsr;
	private String modiSector;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getIdContacto() {
		return idContacto;
	}
	public void setIdContacto(Integer idContacto) {
		this.idContacto = idContacto;
	}
	public String getContacto_a() {
		return contacto_a;
	}
	public void setContacto_a(String contacto_a) {
		this.contacto_a = contacto_a;
	}
	public boolean isConforme() {
		return conforme;
	}
	public void setConforme(boolean conforme) {
		this.conforme = conforme;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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
	public String getAltaSector() {
		return altaSector;
	}
	public void setAltaSector(String altaSector) {
		this.altaSector = altaSector;
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
	public String getModiSector() {
		return modiSector;
	}
	public void setModiSector(String modiSector) {
		this.modiSector = modiSector;
	}
	
	@Override
	public String toString() {
		return "CRMEficacia [id=" + id + ", idContacto=" + idContacto
				+ ", contacto_a=" + contacto_a + ", conforme=" + conforme
				+ ", alta_fecha=" + altaFecha + ", alta_usr=" + altaUsr
				+ ", alta_sector=" + altaSector + "]";
	}
	public CRMEficacia(){
		super();
	}
	
	public CRMEficacia(Integer id, Integer idContacto, String contacto_a,
			boolean conforme, String observaciones, Date alta_fecha,
			String alta_usr, String alta_sector, Date modi_fecha,
			String modi_usr, String modi_sector) {
		
		super();
		this.id = id;
		this.idContacto = idContacto;
		this.contacto_a = contacto_a;
		this.conforme = conforme;
		this.observaciones = observaciones;
		this.altaFecha = alta_fecha;
		this.altaUsr = alta_usr;
		this.altaSector = alta_sector;
		this.modiFecha = modi_fecha;
		this.modiUsr = modi_usr;
		this.modiSector = modi_sector;
	}
	
	public static CRMEficacia getMapping(String prefix, ResultSet rs) throws SQLException{
		
		CRMEficacia efi = new CRMEficacia();
		
		efi.setId(rs.getInt(prefix + "id"));
		efi.setIdContacto(rs.getInt(prefix + "id_contacto"));
		efi.setConforme(rs.getBoolean(prefix + "conforme"));
		efi.setContacto_a(rs.getString(prefix + "contacto_a"));
		efi.setObservaciones(rs.getString(prefix + "observaciones"));
		efi.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		efi.setAltaSector(rs.getString(prefix + "alta_sector"));
		efi.setAltaUsr(rs.getString(prefix + "alta_usr"));
//		efi.setModiFecha(rs.getDate(prefix + "modi_fecha"));
//		efi.setModiSector(rs.getString(prefix + "modi_sector"));
//		efi.setModiUsr(rs.getString(prefix + "modi_usr"));

		return efi;
		
	}
	
	
	
}

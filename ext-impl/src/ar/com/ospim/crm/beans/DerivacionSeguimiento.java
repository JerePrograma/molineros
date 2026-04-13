package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DerivacionSeguimiento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8731828127287428507L;
	private Integer id;
	private Integer idContacto;
	private String derivacionUsr;
	private String derivacionSector;
	private String derivacionEdificio;
	private String observaciones;
	private Date altaFecha;
	private String altaUsr;
	private String altaSector;
	private Date modiFecha;
	private String modiUsr;
	private String modiSector;
	
	public DerivacionSeguimiento(){
		super();
	}
	
	public DerivacionSeguimiento(int id, int idContacto, String derivacionUsr, String derivacionSector, 
			String derivacionEdificio, String obs, Date altaFecha, String altaUsr, String altaSector, 
			Date modiFecha, String modiUsr, String modiSector){
		
		super();
		this.id = id;
		this.idContacto = idContacto;
		this.derivacionUsr = derivacionUsr;
		this.derivacionSector = derivacionSector;
		this.derivacionEdificio = derivacionEdificio;
		this.observaciones = obs;
		this.altaFecha = altaFecha;
		this.altaUsr = altaUsr;
		this.altaSector = altaSector;
		this.modiFecha = modiFecha;
		this.modiUsr = modiUsr;
		this.modiSector = modiSector;

	}
	
	public static DerivacionSeguimiento getMapping(String prefix, ResultSet rs) throws SQLException{
		
		DerivacionSeguimiento ds = new DerivacionSeguimiento();
		
		ds.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		ds.setAltaSector(rs.getString(prefix + "alta_sector"));
		ds.setAltaUsr(rs.getString(prefix + "alta_usr"));
		ds.setDerivacionEdificio(rs.getString(prefix + "derivacion_edificio"));
		ds.setDerivacionSector(rs.getString(prefix + "derivacion_sector"));
		ds.setDerivacionUsr(rs.getString(prefix + "derivacion_usr"));
		ds.setId(rs.getInt(prefix + "id"));
		ds.setIdContacto(rs.getInt(prefix + "id_contacto"));
		ds.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		ds.setModiSector(rs.getString(prefix + "modi_sector"));
		ds.setModiUsr(rs.getString(prefix + "modi_usr"));
		ds.setObservaciones(rs.getString(prefix + "derivacion_observaciones"));

		return ds;
	}

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

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	@Override
	public String toString() {
		return "DerivacionSeguimiento [idContacto=" + idContacto
				+ ", derivacionEdificio=" + derivacionEdificio
				+ ", derivacionSector=" + derivacionSector + ", derivacionUsr="
				+ derivacionUsr + ", altaFecha=" + altaFecha + ", altaSector="
				+ altaSector + ", altaUsr=" + altaUsr + "]";
	}

	
	
}

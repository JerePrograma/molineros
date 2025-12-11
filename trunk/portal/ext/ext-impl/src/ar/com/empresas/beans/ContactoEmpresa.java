package ar.com.empresas.beans;

import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Telefono;

/**
 * @author sistema-09
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class ContactoEmpresa {

	private List<Telefono> telefonos;
	private List<Domicilio> domicilios;
	private List<ContactoElectronico> contactos;
	private Date bajaFecha;
	private String cargo;
	private String nombreApe;
	private String observaciones;
	
	
	
	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajafecha) {
		this.bajaFecha = bajafecha;
	}
	
	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getNombreApe() {
		return nombreApe;
	}

	public void setNombreApe(String nombreApe) {
		this.nombreApe = nombreApe;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nombreApe == null) ? 0 : nombreApe.hashCode());
		result = prime * result
				+ ((cargo == null) ? 0 : cargo.hashCode());
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
		ContactoEmpresa other = (ContactoEmpresa) obj;

		if (null==getNombreApe()) {
			if(null!=other.getNombreApe()){
				return false;
			}
		} else if(null!=other.getNombreApe()){
			if(other.getNombreApe().equals(other.getNombreApe())){
				return false;
			}
		}
		
		if (null==getCargo()) {
			if(null!=other.getCargo()){
				return false;
			}
		} else if(null!=other.getCargo()){
			if(other.getCargo().equals(other.getCargo())){
				return false;
			}
		}
		
			
		
		
		return true;
	}

	public List<Telefono> getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(List<Telefono> telefonos) {
		this.telefonos = telefonos;
	}

	public List<Domicilio> getDomicilios() {
		return domicilios;
	}

	public void setDomicilios(List<Domicilio> domicilios) {
		this.domicilios = domicilios;
	}

	public List<ContactoElectronico> getContactos() {
		return contactos;
	}

	public void setContactos(List<ContactoElectronico> contactos) {
		this.contactos = contactos;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	

}
package ar.com.empresas.beans;

import java.io.Serializable;
import java.util.Date;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Telefono;

public class Contacto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1492470294868604811L;
	
	private Telefono telefono;
	private Domicilio domicilio;
	private ContactoElectronico contacto;
	private Date bajaFecha;
	private String cargo;
	private String cargoDescripcion;
	private String nombreApe;
	private String profesion;
	private Seccional seccional;
	
	private ESTADOS estado;
    
    public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	public Telefono getTelefono() {
		return telefono;
	}

	public void setTelefono(Telefono telefono) {
		this.telefono = telefono;
	}

	public ContactoElectronico getContacto() {
		return contacto;
	}

	public void setContacto(ContactoElectronico contacto) {
		this.contacto = contacto;
	}

	public String getContactoAsString() {
		if (telefono != null && !telefono.getNumero().trim().equals("")) {
			return telefono.toString();
		} else if(null!=contacto && null!=contacto.getContacto()) {
			return contacto.getContacto();
		}else{
			return "";
		}
	}

	public String getTipoAsString() {
		if (telefono != null && !telefono.getNumero().trim().equals("")) {
			return "TELEFONO";
		} else if (contacto!=null && null!= contacto.getTipo()){			
			return contacto.getTipo().toString();
		}else{
			return "";
		}
	}	

	public int getIdContacto() {
		if (telefono != null && telefono.getId() > 0) {
			return telefono.getId();
		} else {
			return contacto.getId();
		}
	}

	public String getObservaciones() {
		if (telefono != null) {
			return telefono.getObservaciones();
		} else {
			return contacto.getObservaciones();
		}
	}

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

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contacto == null) ? 0 : contacto.hashCode());
		result = prime * result
				+ ((telefono == null) ? 0 : telefono.hashCode());
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
		Contacto other = (Contacto) obj;

		if (getIdContacto() == 0) {
			if(other.getIdContacto()!=0){
				return false;
			}
			if (!other.getTipoAsString().equals(this.getTipoAsString())) {
				return false;
			}
			if (!other.getContactoAsString().equals(this.getContactoAsString())) {
				return false;
			}
		}else{
			if(getIdContacto()!=other.getIdContacto()){
				return false;
			}
		}
		
		return true;
	}

	public String getProfesion() {
		return profesion;
	}

	public void setProfesion(String profesion) {
		this.profesion = profesion;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	public String getCargoDescripcion() {
		return cargoDescripcion;
	}

	public void setCargoDescripcion(String cargoDescripcion) {
		this.cargoDescripcion = cargoDescripcion;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}
    
	
}
package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ContactoElectronico implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4514290575982842916L;

	//Estos ids se corresponden con los ids de la base de datos.
	//Si este conjunto crece, se deberian obtener de la base y sacar este hardcode
	 public enum Tipo { 
		 EMAIL ("E"), SITIOWEB ("S"), FAX ("F"), PERSONAL ("P"),EMAILCBU ("EC");
		 
		 private String id;
		 Tipo (String id){
			 this.id = id;
		 }
		 
		 public String getId(){
			 return this.id;
		 }
		 
		 public static Tipo getTipoById(String id){
			 for (Tipo tipo: Tipo.values()){
				 if (tipo.getId().equals(id)){
					 return tipo;
				 }				 
			 }			 
			 return Tipo.EMAIL;
		 }
		 
	}

	private int id;
	private Tipo tipo;
	private Date vigenDesde;
	private String contacto;
	private String observaciones;
	private Date altaFecha;
	private String altaUsuario;
	private Date modiFecha;
	private String modiUsuario;
	private Date bajaFecha;
	private String bajaUsuario;
	private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	public ContactoElectronico(){
		super();
	}

	public ContactoElectronico(int id, Tipo tipo, String contacto,
			String observaciones) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.contacto = contacto;
		this.observaciones = observaciones;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Date getVigenDesde() {
		return vigenDesde;
	}

	public void setVigenDesde(Date vigenDesde) {
		this.vigenDesde = vigenDesde;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
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

	public String getAltaUsuario() {
		return altaUsuario;
	}

	public void setAltaUsuario(String altaUsuario) {
		this.altaUsuario = altaUsuario;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModiUsuario() {
		return modiUsuario;
	}

	public void setModiUsuario(String modiUsuario) {
		this.modiUsuario = modiUsuario;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsuario() {
		return bajaUsuario;
	}

	public void setBajaUsuario(String bajaUsuario) {
		this.bajaUsuario = bajaUsuario;
	}
	
	public static ContactoElectronico getMapping(ResultSet rs) throws SQLException {
		ContactoElectronico contactoElectronico = new ContactoElectronico();
		contactoElectronico.setId(rs.getInt("id_contacto_e"));
		contactoElectronico.setTipo(Tipo.getTipoById(rs.getString("tipo_contacto_e")));
		contactoElectronico.setVigenDesde(rs.getDate("vigen_desde"));
		contactoElectronico.setContacto(rs.getString("contacto"));
		contactoElectronico.setObservaciones(rs.getString("observaciones"));
		contactoElectronico.setAltaFecha(rs.getDate("alta_fecha"));
		contactoElectronico.setAltaUsuario(rs.getString("alta_usr"));
		contactoElectronico.setModiFecha(rs.getDate("modi_fecha"));
		contactoElectronico.setModiUsuario(rs.getString("modi_usr"));
		contactoElectronico.setBajaFecha(rs.getDate("baja_fecha"));
		contactoElectronico.setBajaUsuario(rs.getString("baja_usr"));
		return contactoElectronico;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "ContactoElectronico [tipo=" + tipo + ", contacto=" + contacto
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		ContactoElectronico other = (ContactoElectronico) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public static ContactoElectronico getMappingCompleto(ResultSet rs) throws SQLException {
		ContactoElectronico contactoElectronico = new ContactoElectronico();		
		contactoElectronico.setId(rs.getInt("id_contacto_e"));
		contactoElectronico.setTipo(Tipo.getTipoById(rs.getString("tipo_contacto_e")));		
		contactoElectronico.setContacto(rs.getString("contacto"));
		contactoElectronico.setObservaciones(rs.getString("observaciones"));		
		return contactoElectronico;
	}
	
	
	
}

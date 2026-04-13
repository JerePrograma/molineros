package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Telefono implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3349845751415189543L;
	
	private int id;
	private String tipo;
	private Date vigenDesde;
	private String codigoPais;
	private String codigoArea;
	private String numero;
	private String extension;
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
	
	public Telefono(){
	}

	public Telefono(String codigoPais, String codigoArea, String numero,
			String extension) {
		this.codigoPais = codigoPais;
		this.codigoArea = codigoArea;
		this.numero = numero;
		this.extension = extension;
	}

	public Telefono(int id, String tipo, String codigoPais, String codigoArea,
			String numero, String extension, String observaciones) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.codigoPais = codigoPais;
		this.codigoArea = codigoArea;
		this.numero = numero;
		this.extension = extension;
		this.observaciones = observaciones;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Date getVigenDesde() {
		return vigenDesde;
	}

	public void setVigenDesde(Date vigenDesde) {
		this.vigenDesde = vigenDesde;
	}

	public String getCodigoPais() {
		return codigoPais;
	}

	public void setCodigoPais(String codigoPais) {
		this.codigoPais = codigoPais;
	}

	public String getCodigoArea() {
		return codigoArea;
	}

	public void setCodigoArea(String codigoArea) {
		this.codigoArea = codigoArea;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
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

	public static Telefono getMapping(ResultSet rs) throws SQLException {
		Telefono tel = new Telefono();		
		tel.setId(rs.getInt("id_telefono"));
		tel.setTipo(rs.getString("tipo_tele"));		
		tel.setCodigoPais(rs.getString("codigo_pais"));
		tel.setCodigoArea(rs.getString("codigo_area"));
		tel.setNumero(rs.getString("numero"));
		tel.setExtension(rs.getString("extension"));
		tel.setObservaciones(rs.getString("observaciones"));
		try{
		  tel.setVigenDesde(rs.getTimestamp("vigen_desde"));
		  tel.setAltaFecha(rs.getTimestamp("alta_fecha"));
		  tel.setAltaUsuario(rs.getString("alta_usr"));
		  tel.setModiFecha(rs.getTimestamp("modi_fecha"));
		  tel.setModiUsuario(rs.getString("modi_usr"));
		  tel.setBajaFecha(rs.getTimestamp("baja_fecha"));
		  tel.setBajaUsuario(rs.getString("baja_usr"));
		}catch(Exception e){}
		return tel;
	}
	
	public String toString(){
		StringBuilder strb = new StringBuilder();
		strb.append(codigoPais != null ? codigoPais : "");
		strb.append(" ");
		strb.append(codigoArea != null ? codigoArea : "");
		strb.append(" ");
		strb.append(numero != null ? numero : "");
		if (extension != null && !extension.trim().equals("")){
			strb.append(" ext. ");
			strb.append(extension);
		}
		return strb.toString();
	}
	
	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
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
		Telefono other = (Telefono) obj;
		if (id != other.id)
			return false;
		return true;
	}
	

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	public boolean compareTo(Object aThat) {
	     if (this == aThat) return true;
	     if (!(aThat instanceof Telefono )) return false;

	     Telefono that = (Telefono )aThat;
	     return
	       ( this.getId() == that.getId() ) &&
	       ( this.getNumero().equals(that.getNumero()) ) &&
	       ( this.getCodigoArea().equals(that.getCodigoArea()) ) &&
	       ( this.getTipo().equals(that.getTipo()) )
	     ;
	   }
	
	
	
	
}

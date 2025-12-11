package ar.com.ospim.correspondencia.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.util.DateUtils;

public class ListaPaquete {

	private long id;
	private long id_item_correspondencia;
	private long id_paquete;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;	

	private String paq_estado;    //estos attrib son de paquete
	private String paq_descripcion; //estos attrib son de paquete
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId_item_correspondencia() {
		return id_item_correspondencia;
	}

	public void setId_item_correspondencia(long idItemCorrespondencia) {
		id_item_correspondencia = idItemCorrespondencia;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public String getAlta_fechaString() {
		return DateUtils.getDateString(alta_fecha, DateUtils.SHORT);
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

	public long getId_paquete() {
		return id_paquete;
	}

	public void setId_paquete(long idPaquete) {
		id_paquete = idPaquete;
	}

	public static ListaPaquete getMapping(ResultSet rs, String prefix)
			throws SQLException {

		ListaPaquete e = new ListaPaquete();
		e.setId(rs.getLong(prefix + "id"));
		e.setId_item_correspondencia(rs.getLong(prefix + "id_item_correspondencia"));
		e.setId_paquete(rs.getLong(prefix + "id_paquete"));
		e.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		e.setAlta_usr(rs.getString(prefix + "alta_usr"));
		e.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		e.setModi_usr(rs.getString(prefix + "modi_usr"));
		e.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		e.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return e;
	}

	public String getPaq_estado() {
		return paq_estado;
	}

	public void setPaq_estado(String paq_estado) {
		this.paq_estado = paq_estado;
	}

	public String getPaq_descripcion() {
		return paq_descripcion;
	}

	public void setPaq_descripcion(String paq_descripcion) {
		this.paq_descripcion = paq_descripcion;
	}
	
	
}


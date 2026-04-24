package ar.com.ospim.afiliados.beans;

import groovy.sql.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;


/**
 * @author Administrador
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 */

/***
 * 
 * @author sergio
 * 
 *String myValue = rs.getString("myColumn");
 *if (rs.wasNull())
 *myValue = ""; // set it to empty string as you desire.
 *
 *You may want to refer to wasNull() documentation -
 *
 *From java.sql.ResultSet
 *boolean wasNull() throws SQLException;
 *
 * Reports whether
 * the last column read had a value of SQL <code>NULL</code>.
 * Note that you must first call one of the getter methods
 * on a column to try to read its value and then call
 * the method <code>wasNull</code> to see if the value read was
 * SQL <code>NULL</code>.
 *
 * @return <code>true</code> if the last column value read was SQL
 *         <code>NULL</code> and <code>false</code> otherwise
 * @exception SQLException if a database access error occurs or this method is 
 *            called on a closed result set
 */

//Mapea la tabla motivo_baja

public class MotivoBaja {	
	
	private int id_motivo_baja;
	private String descripcion;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private int meses_a_baja;	
	

	public static MotivoBaja getMapping(ResultSet rs) throws SQLException{
		
		MotivoBaja mb = new MotivoBaja();
		mb.setId_motivo_baja(rs.getInt("id_motivo_baja"));
		mb.setDescripcion(rs.getString("descripcion"));
		mb.setMeses_a_baja(rs.getInt("meses_a_baja"));
		mb.setAlta_usr(rs.getString("alta_usr"));
		mb.setAlta_fecha(rs.getDate("alta_fecha"));
		mb.setModi_usr(rs.getString("modi_usr"));
		mb.setModi_fecha(rs.getDate("modi_fecha"));
		mb.setBaja_fecha(rs.getDate("baja_fecha"));
		mb.setBaja_usr(rs.getString("baja_usr"));
		
		return mb;
	}
	
	public static MotivoBaja getMapping(String prefix, ResultSet rs) throws SQLException{
		
		MotivoBaja mb = new MotivoBaja();
		Integer idMotBaja = rs.getInt(prefix + "id_motivo_baja"); 
		if(rs.wasNull()){
			return null;
		}else{
			mb.setId_motivo_baja(idMotBaja);
		}
		mb.setId_motivo_baja(rs.getInt(prefix + "id_motivo_baja"));
		mb.setDescripcion(rs.getString(prefix + "descripcion"));
		mb.setMeses_a_baja(rs.getInt(prefix + "meses_a_baja"));
		mb.setAlta_usr(rs.getString(prefix + "alta_usr"));
		mb.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		mb.setModi_usr(rs.getString(prefix + "modi_usr"));
		mb.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		mb.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		mb.setBaja_usr(rs.getString(prefix + "baja_usr"));

		return mb;
	}
	
	public MotivoBaja(){}
	
	public MotivoBaja(String descrip){
		this.descripcion=descrip;
	}
	
	public MotivoBaja(int id, String descrip){
		this.id_motivo_baja=id;
		this.descripcion=descrip;
	}
	
	public MotivoBaja(int id, String descrip, int meses){
		this.id_motivo_baja=id;
		this.descripcion=descrip;
		this.meses_a_baja=meses;
	}

	public int getId_motivo_baja() {
		return id_motivo_baja;
	}
	
	public int getMeses_a_baja() {
		return meses_a_baja;
	}

	public void setMeses_a_baja(int mesesABaja) {
		meses_a_baja = mesesABaja;
	}

	public void setId_motivo_baja(int idMotivoBaja) {
		id_motivo_baja = idMotivoBaja;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_motivo_baja;
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
		MotivoBaja other = (MotivoBaja) obj;
		if (id_motivo_baja != other.id_motivo_baja)
			return false;
		return true;
	}	
}
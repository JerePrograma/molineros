package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.liferay.portal.SystemException;

public class EstadisticaPrestAutorizada implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4810882719211142983L;
	
	private String codigo;
	private String descripcion;
	private int cantidad;
	
	
	
	public EstadisticaPrestAutorizada(String codigo, String descripcion, int cantidad) {
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	public static EstadisticaPrestAutorizada getMapping(String prefix, ResultSet rs) throws SystemException{
		
		EstadisticaPrestAutorizada epa = null;
		
		try {
			
			epa = new EstadisticaPrestAutorizada(rs.getString(prefix + "codigo"), 
												rs.getString(prefix + "descripcion"), 
												rs.getInt(prefix + "cantidad"));
			
		}catch (SQLException e) {
			epa = null;
			throw new SystemException();
		}
		
		return epa;
	}
	
}

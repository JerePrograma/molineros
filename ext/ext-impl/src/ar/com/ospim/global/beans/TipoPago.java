package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;



public class TipoPago implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -222563241855599884L;
	/**
	 * 
	 */	
	public int id;
	public String descripcion;

	
	public TipoPago(int id, String descripcion){
		this.id=id;
		this.descripcion=descripcion;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}



	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public static TipoPago getMapping(String prefix, ResultSet rs) throws SQLException{
		
		return new TipoPago(rs.getInt(prefix+"id_tipo_pago"), rs.getString(prefix+"descripcion"));
		
	}
	
}
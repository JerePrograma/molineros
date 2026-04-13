package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoReclamo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8709954957397751710L;
	
	private Integer id;
	private String descripcion;
	
	public TipoReclamo (Integer id, String descripcion){
		
		super();
		this.id = id;
		this.descripcion = descripcion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String toString(){
		return this.descripcion;
	}
	
	public static TipoReclamo getMapping(String prefix, ResultSet rs) throws SQLException{
		
		TipoReclamo cc = new TipoReclamo(rs.getInt(prefix + "id"), rs.getString(prefix + "descripcion"));
		
		return cc;
	}
	
}

package ar.com.ospim.tesoreria.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActaEstadoSeguimiento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7103904675152310240L;
	private Integer id;
	private String descripcion;
	
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
	
	
	
	public ActaEstadoSeguimiento(Integer id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}
	
	public ActaEstadoSeguimiento getMapping(String prefix, ResultSet rs) throws SQLException{
		
		ActaEstadoSeguimiento aes = new ActaEstadoSeguimiento(rs.getInt(prefix + "id"), rs.getString(prefix + "descripcion" ));
		
		return aes;
	}
}

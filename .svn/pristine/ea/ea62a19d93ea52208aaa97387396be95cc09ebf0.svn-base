package ar.com.ospim.tesoreria.beans.convenio;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConvenioEstadoSeguimiento implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6921467177589334303L;
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
	
	
	
	public ConvenioEstadoSeguimiento(Integer id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}
	
	public ConvenioEstadoSeguimiento getMapping(String prefix, ResultSet rs) throws SQLException{
		
		ConvenioEstadoSeguimiento aes = new ConvenioEstadoSeguimiento(rs.getInt(prefix + "id"), rs.getString(prefix + "descripcion" ));
		
		return aes;
	}
}

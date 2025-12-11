package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MotivoExcepcion {

	private String id;
	private String descripcion;

	public MotivoExcepcion(){

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
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
    
	public static MotivoExcepcion getMapping(ResultSet rs) throws SQLException {
		
		MotivoExcepcion archivo = new MotivoExcepcion();
		archivo.setDescripcion(rs.getString("descripcion"));
		archivo.setId(rs.getString("id"));
		return archivo;
	}
}
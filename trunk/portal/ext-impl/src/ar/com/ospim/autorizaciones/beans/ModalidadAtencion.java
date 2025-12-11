package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModalidadAtencion {

	private int id;
	private String descripcion;

	public ModalidadAtencion(){

	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

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
    
	public static ModalidadAtencion getMapping(ResultSet rs) throws SQLException {
		
		ModalidadAtencion archivo = new ModalidadAtencion();
		archivo.setDescripcion(rs.getString("descripcion"));
		archivo.setId(rs.getInt("id"));
		return archivo;
	}
}
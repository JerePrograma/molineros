package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.ospim.autorizaciones.beans.Nomenclador;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:26:01 p.m.
 */
public class TipoNomenclador {

	private int id_tipo_nomenclador;
	private String descripcion;

	public TipoNomenclador(){

	}

	/**
	 * @return the id_tipo_nomenclador
	 */
	public int getId_tipo_nomenclador() {
		return id_tipo_nomenclador;
	}

	/**
	 * @param idTipoNomenclador the id_tipo_nomenclador to set
	 */
	public void setId_tipo_nomenclador(int idTipoNomenclador) {
		id_tipo_nomenclador = idTipoNomenclador;
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
    
	public static TipoNomenclador getMapping(String prefix, ResultSet rs) throws SQLException {
		
		TipoNomenclador archivo = new TipoNomenclador();
		archivo.setDescripcion(rs.getString(prefix + "descripcion"));
		archivo.setId_tipo_nomenclador(rs.getInt(prefix + "id_tipo_nomenclador"));
		return archivo;
	}
}
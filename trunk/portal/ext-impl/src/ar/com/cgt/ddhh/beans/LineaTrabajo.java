package ar.com.cgt.ddhh.beans;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author sistema-09
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class LineaTrabajo {

	private int id_linea;
	private String descripcion;
	private String tipoLinea;
	
	public LineaTrabajo(int id, String descrip) {
		this.id_linea= id;
		this.descripcion = descrip;

	}

	public LineaTrabajo() {
	}

	public LineaTrabajo(int id_linea) {
		this.id_linea= id_linea;
	}
	
	public LineaTrabajo(String tipoLinea, String id_linea) {
		this.tipoLinea=tipoLinea;
		this.descripcion= id_linea;
	}
	
	
		
	public String getTipoLinea() {
		return tipoLinea;
	}

	public void setTipoLinea(String tipoLinea) {
		this.tipoLinea = tipoLinea;
	}

	public int getId_linea() {
		return id_linea;
	}

	public void setId_linea(int id_linea) {
		this.id_linea = id_linea;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public static LineaTrabajo getMapping(ResultSet rs)
			throws SQLException {
		LineaTrabajo linea = new LineaTrabajo();
		linea.setId_linea(rs.getInt( "id_linea"));
		linea.setDescripcion(rs.getString( "descripcion"));		
		return linea;
	}
		
}
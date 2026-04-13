package ar.com.cgt.ddhh.beans;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Sergio Valentini
 * @version 1.0
 * @created 10-06-2013 05:27:49 p.m.
 */
public class TiposNormasDDHH {
	private int id;
	private String sistema;
	private String descripcion;
	
	public TiposNormasDDHH(Integer id, String descrip){
		super();
		this.id=id;
		this.descripcion=descrip;
	}
	
	public TiposNormasDDHH() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public static TiposNormasDDHH getMapping(ResultSet rs)
			throws SQLException {
		
		TiposNormasDDHH t = new TiposNormasDDHH();
		t.setId(rs.getInt("id"));
		t.setSistema(rs.getString("sistema"));
		t.setDescripcion(rs.getString("descripcion"));		
		
		return t;
	}

	
}

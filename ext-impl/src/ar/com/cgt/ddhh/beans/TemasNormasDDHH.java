package ar.com.cgt.ddhh.beans;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Sergio Valentini
 * @version 1.0
 * @created 10-06-2013 05:27:49 p.m.
 */
public class TemasNormasDDHH {
	private int id;
	private String descripcion;
	
	public TemasNormasDDHH(Integer id, String descrip){
		super();
		this.id=id;
		this.descripcion=descrip;
	}
	
	public TemasNormasDDHH() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public static TemasNormasDDHH getMapping(ResultSet rs)
			throws SQLException {
		
		TemasNormasDDHH t = new TemasNormasDDHH();
		t.setId(rs.getInt("id"));
		t.setDescripcion(rs.getString("descripcion"));		
		
		return t;
	}
	
	
	
	
		
}

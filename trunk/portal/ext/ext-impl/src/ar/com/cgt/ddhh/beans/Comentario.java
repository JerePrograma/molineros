package ar.com.cgt.ddhh.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comentario {

	private Date fecha;
	private String descripcion;
	
	public Comentario(Date fecha, String descrip) {
		this.fecha= fecha;
		this.descripcion = descrip;

	}

	public Comentario() {
	}

	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getFechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");		
		return fecha!=null?sdf.format(fecha):"";
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public static Comentario getMapping(ResultSet rs)
			throws SQLException {
		Comentario linea = new Comentario();
		linea.setFecha(rs.getDate( "fecha"));
		linea.setDescripcion(rs.getString( "descripcion"));		
		return linea;
	}
		
}
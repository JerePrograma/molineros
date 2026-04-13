package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class EstadoGestion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5547319393655574979L;
	
	private Integer id;
	private String descripcion;
	private Date fecha;
	
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
	
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public EstadoGestion(){
		super();
	}
	
	public EstadoGestion(Integer id, String descripcion){
		super();
		this.id = id;
		this.descripcion = descripcion;
		
	}
	
	public EstadoGestion(Integer id, String descripcion, Date fecha) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.fecha = fecha;
	}
	
	public static EstadoGestion getMapping(String prefix, ResultSet rs) throws SQLException {
		
		EstadoGestion estado = new EstadoGestion();
		estado.setId(rs.getInt(prefix + "id"));
		estado.setDescripcion(rs.getString(prefix + "descripcion"));

		return estado;
	
}

}

package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReglaValidacionParametros implements Serializable{
	
	public ReglaValidacionParametros() {
		super();
		
	}


	private static final long serialVersionUID = -3920316967596113241L;
	
	protected Integer id;
	protected String nombre;
	protected String valor;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
    
	
	public static ReglaValidacionParametros getMapping(ResultSet rs) throws SQLException {
		ReglaValidacionParametros a = new ReglaValidacionParametros();
		a.setNombre(rs.getString("nombre"));
		a.setValor(rs.getString("valor"));
		return a;
	}
}


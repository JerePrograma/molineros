package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;

public class Personal  implements Serializable {
	
	private static final long serialVersionUID = 3338582602924828630L;
	
	private String hotel;
	private Integer id;
	private String apellido;
	private String nombre;
    private String categoria;
    private String password;
    
	public String getHotel() {
		return hotel;
	}

    public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public static Personal getMapping(ResultSet rs, String prefix) throws Exception {
		Personal  per  = new Personal();
		
		per.setApellido(rs.getString(prefix+"apellido"));
		per.setNombre(rs.getString(prefix+"nombre"));
		per.setCategoria(rs.getString(prefix+"categoria"));
		per.setHotel(rs.getString(prefix+"cod_hotel"));
		per.setId(rs.getInt(prefix+"id"));
		per.setPassword(rs.getString(prefix+"password"));
		return per ;
    }

		

	
}

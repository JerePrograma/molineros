package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;

public class Habitacion  implements Serializable {
	
	private static final long serialVersionUID = -2615236220771181954L;
	
	private int numero;
	private String descripcion;
	private String grupo;
    private String codHotel;
	
   
	public int getNumero() {
		return numero;
	}



	public void setNumero(int numero) {
		this.numero = numero;
	}



	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}



	public String getGrupo() {
		return grupo;
	}



	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}



	public String getHotel() {
		return codHotel;
	}



	public void setHotel(String codHotel) {
		this.codHotel = codHotel;
	}



	public static Habitacion  getMapping(ResultSet rs, String prefix) throws Exception {
		Habitacion   hab  = new Habitacion();
		
		hab.setDescripcion(rs.getString(prefix+"descripcion"));
		hab.setGrupo(rs.getString(prefix+"grupo"));
		hab.setNumero(rs.getInt(prefix+"nro_habitacion"));
		hab.setHotel(rs.getString(prefix+"cod_hotel"));
		return hab ;
   }

	public Habitacion() {
		super();
	}

	public Habitacion(int numero, String descripcion, String grupo) {
		super();
		this.numero = numero;
		this.descripcion = descripcion;
		this.grupo = grupo;
	}	
	

	
}

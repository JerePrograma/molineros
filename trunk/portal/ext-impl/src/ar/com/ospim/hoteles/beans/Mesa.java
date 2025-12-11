package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;

public class Mesa  implements Serializable {
	
	private String hotel;
	private int numero;
	private String descripcion;
	private String grupo;
    private Personal mozo;
	
   
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
		return hotel;
	}



	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

    

	public Personal getMozo() {
		return mozo;
	}



	public void setMozo(Personal mozo) {
		this.mozo = mozo;
	}



	public static Mesa  getMapping(ResultSet rs, String prefix) throws Exception {
		Mesa   mesa  = new Mesa();
		Personal mozo = new Personal();
		try {
		  mozo.setId(rs.getInt(prefix+"personal_id"));
		}  catch(Exception e) {}
		mesa.setMozo(mozo);
		mesa.setDescripcion(rs.getString(prefix+"descripcion"));
		mesa.setGrupo(rs.getString(prefix+"grupo"));
		mesa.setNumero(rs.getInt(prefix+"nro_mesa"));
		mesa.setHotel(rs.getString(prefix+"cod_hotel"));
		return mesa ;
   }

	public Mesa() {
		super();
	}

	public Mesa(int numero, String descripcion, String grupo) {
		super();
		this.numero = numero;
		this.descripcion = descripcion;
		this.grupo = grupo;
	}	
	

	
}

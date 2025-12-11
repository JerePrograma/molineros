package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;

public class ProductoCategoria  implements Serializable {
	
	private static final long serialVersionUID = -2615236220771181954L;
	
	private String codigo;
	private String descripcion;
	private String aplicaA;
	private String hotel;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getAplicaA() {
		return aplicaA;
	}

	public void setAplicaA(String aplicaA) {
		this.aplicaA = aplicaA;
	}
	
	

	public ProductoCategoria() {
		super();
	}

	public String getHotel() {
		return hotel;
	}

	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public ProductoCategoria(String codigo, String descripcion, String aplicaA) {
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.aplicaA = aplicaA;
	}

	public static ProductoCategoria  getMapping(ResultSet rs, String prefix) throws Exception {
		ProductoCategoria   cat  = new ProductoCategoria();
		
		cat.setDescripcion(rs.getString(prefix+"descripcion"));
		cat.setCodigo(rs.getString(prefix+"codigo"));
		cat.setAplicaA(rs.getString(prefix+"aplica_a"));
		try {
			cat.setHotel(rs.getString(prefix+"cod_hotel"));
		}catch(Exception e) {
			
		}
		
		return cat ;
    }

		

	
}

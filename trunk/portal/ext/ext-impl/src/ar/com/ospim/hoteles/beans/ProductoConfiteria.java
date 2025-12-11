package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;

public class ProductoConfiteria implements Serializable{
	
	private static final long serialVersionUID = -2466096384604321672L;

	private String codigo;
	private String descripcion;
	private String descripcionCorta;
	private ProductoCategoria categoria;
	private Double precio;
	private String hotel;
	private boolean habilitadoHabitaciones;
	
	public boolean isHabilitadoHabitaciones() {
		return habilitadoHabitaciones;
	}

	public void setHabilitadoHabitaciones(boolean habilitadoHabitaciones) {
		this.habilitadoHabitaciones = habilitadoHabitaciones;
	}

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

	public String getDescripcionCorta() {
		return descripcionCorta;
	}

	public void setDescripcionCorta(String descripcionCorta) {
		this.descripcionCorta = descripcionCorta;
	}

	public ProductoCategoria getCategoria() {
		return categoria;
	}

	public void setCategoria(ProductoCategoria categoria) {
		this.categoria = categoria;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public ProductoConfiteria() {
		// TODO Auto-generated constructor stub
	}
	
	public String getHotel() {
		return hotel;
	}

	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public static ProductoConfiteria  getMapping(ResultSet rs, String prefix) throws Exception {
		ProductoCategoria   cat  = new ProductoCategoria();
		cat.setCodigo(rs.getString(prefix+"categoria_id"));
		cat.setDescripcion(rs.getString(prefix+"categoria_descripcion"));
		
		ProductoConfiteria prod = new ProductoConfiteria();
		prod.setCategoria(cat);
		prod.setCodigo(rs.getString(prefix+"producto_id"));
		prod.setDescripcion(rs.getString(prefix+"descripcion"));
		prod.setDescripcionCorta(rs.getString(prefix+"descripcion_corta"));
		prod.setPrecio(rs.getDouble(prefix+"precio"));
		prod.setHabilitadoHabitaciones(rs.getBoolean(prefix + "habilitado_habitaciones"));
		prod.setHotel(rs.getString(prefix+"cod_hotel"));
		return prod ;
    }

}

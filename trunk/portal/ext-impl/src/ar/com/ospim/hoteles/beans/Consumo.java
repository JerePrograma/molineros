package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Producto;

public class Consumo  implements Serializable {
	
	private static final long serialVersionUID = 893906667281804036L;

	private ProductoConfiteria producto;
	private Integer cantidad;
	private Double precio;
	private Habitacion habitacion;
	private Mesa mesa;
	private String estado;
	private Cliente cliente;

	public ProductoConfiteria getProducto() {
		return producto;
	}

	public void setProducto(ProductoConfiteria producto) {
		this.producto = producto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}

	public Mesa getMesa() {
		return mesa;
	}

	public void setMesa(Mesa mesa) {
		this.mesa = mesa;
	}
	
	public Consumo() {
		super();
	}
	
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Consumo(ProductoConfiteria producto, Integer cantidad, Double precio, Habitacion habitacion, Mesa mesa) {
		super();
		this.producto = producto;
		this.cantidad = cantidad;
		this.precio = precio;
		this.habitacion = habitacion;
		this.mesa = mesa;
	}

	public static Consumo  getMapping(ResultSet rs, String prefix,String tipo) throws Exception {
		Consumo  consumo  = new Consumo();
		consumo.setCantidad(rs.getInt(prefix+"cantidad"));
		consumo.setPrecio(rs.getDouble(prefix+"precio"));
		ProductoConfiteria producto = new ProductoConfiteria();
		producto.setCodigo(rs.getString(prefix+"producto_id"));
		producto.setDescripcion(rs.getString(prefix+"descripcion"));
		
		
		Cliente cliente = new Cliente();
		
		try {
		   cliente.setApellido(rs.getString(prefix +"cliente"));
		}catch(Exception e) {cliente.setApellido("");}   
		consumo.setCliente(cliente);
		
		consumo.setProducto(producto);
		consumo.setEstado(rs.getString(prefix+"estado"));
		return consumo;
   }

	
}

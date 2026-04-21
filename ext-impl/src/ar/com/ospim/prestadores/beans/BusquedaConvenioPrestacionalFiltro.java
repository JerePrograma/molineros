package ar.com.ospim.prestadores.beans;

import java.io.Serializable;

public class BusquedaConvenioPrestacionalFiltro implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4858399190389232621L;
	
	private String cuit;
	private String descripcion;
	private Integer idPrestador;
	private int estado;
	private int pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	
	
	public BusquedaConvenioPrestacionalFiltro(String cuit, String descripcion,
			int idPrestador, int estado, int pagina) {
		
		super();
		this.cuit = cuit;
		this.descripcion = descripcion;
		this.idPrestador = idPrestador;
		this.estado = estado;
		this.pagina = pagina;
	}
	
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Integer getIdPrestador() {
		return idPrestador;
	}
	public void setIdPrestador(Integer idPrestador) {
		this.idPrestador = idPrestador;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public int getRegistrosTotal() {
		return registrosTotal;
	}
	public void setRegistrosTotal(int registrosTotal) {
		this.registrosTotal = registrosTotal;
	}
	public int getRegistrosPorPagina() {
		return registrosPorPagina;
	}
	
}

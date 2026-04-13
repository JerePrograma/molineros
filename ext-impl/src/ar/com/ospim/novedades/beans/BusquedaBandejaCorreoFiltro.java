package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaBandejaCorreoFiltro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1317878718464195850L;

	private Date fechaDesde;
	private Date fechaHasta;
	private String estado;
	private String cuit;
	private int pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
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

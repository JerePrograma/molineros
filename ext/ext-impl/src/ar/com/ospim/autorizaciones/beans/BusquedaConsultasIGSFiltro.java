package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaConsultasIGSFiltro implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7463379283483457975L;
	private Date fechaDesde;
	private Date fechaHasta;
	private int pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	
	
	public BusquedaConsultasIGSFiltro(Date fechaDesde, Date fechaHasta, int pagina) {
		
		super();
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.pagina = pagina;
	}
	
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

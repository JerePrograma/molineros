package ar.com.uoma.facturacion;

import java.io.Serializable;
import java.util.Date;

public class BusquedaFacturasFiltro implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7463379283483457975L;
	private String tipo;
	private String sucursal;
	private String letra; 
	private String numero;
	private Date fechaDesde;
	private Date fechaHasta;
	private Integer pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	
	public BusquedaFacturasFiltro() {
		super();
		
	}
	public BusquedaFacturasFiltro(Date fechaDesde, Date fechaHasta, 
			String tipo, String sucursal, String letra, String numero,
			int pagina) {
		
		super();
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.tipo = tipo;
		this.letra = letra;
		this.sucursal = sucursal;
		this.numero = numero;
		this.pagina = pagina;
	}
	
	public BusquedaFacturasFiltro(Date fechaDesde, Date fechaHasta, 
			String tipo, String sucursal, String letra, String numero,
			Integer pagina) {
		
		super();
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.tipo = tipo;
		this.letra = letra;
		this.sucursal = sucursal;
		this.numero = numero;
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
	public Integer getPagina() {
		return pagina;
	}
	public void setPagina(Integer pagina) {
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
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getLetra() {
		return letra;
	}
	public void setLetra(String letra) {
		this.letra = letra;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
}

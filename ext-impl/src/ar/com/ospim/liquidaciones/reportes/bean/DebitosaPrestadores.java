package ar.com.ospim.liquidaciones.reportes.bean;

import java.math.BigDecimal;

public class DebitosaPrestadores {
	
	private BigDecimal numero;
	private int idLiquidacion;
	private String prestador;
	private String factura;
	private BigDecimal monto;
	private String ordenPago;
	private BigDecimal cargoPrestadora;
	private Integer reclamoPrestacional;
	private String reclamosPrestacionales;
	
	public String getPrestador() {
		return prestador;
	}
	public String getFactura() {
		return factura;
	}
	public BigDecimal getMonto() {
		return monto;
	}
	public String getOrdenPago() {
		return ordenPago;
	}
	public void setPrestador(String prestador) {
		this.prestador = prestador;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}
	public void setOrdenPago(String ordenPago) {
		this.ordenPago = ordenPago;
	}
	public BigDecimal getCargoPrestadora() {
		return cargoPrestadora;
	}
	public void setCargoPrestadora(BigDecimal cargoPrestadora) {
		this.cargoPrestadora = cargoPrestadora;
	}
	public int getIdLiquidacion() {
		return idLiquidacion;
	}
	public void setIdLiquidacion(int idLiquidacion) {
		this.idLiquidacion = idLiquidacion;
	}
	public BigDecimal getNumero() {
		return numero;
	}
	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}
	public Integer getReclamoPrestacional() {
		return reclamoPrestacional;
	}
	public void setReclamoPrestacional(Integer reclamoPrestacional) {
		this.reclamoPrestacional = reclamoPrestacional;
	}
	public String getReclamosPrestacionales() {
		return reclamosPrestacionales;
	}
	public void setReclamosPrestacionales(String reclamosPrestacionales) {
		this.reclamosPrestacionales = reclamosPrestacionales;
	}
	
}

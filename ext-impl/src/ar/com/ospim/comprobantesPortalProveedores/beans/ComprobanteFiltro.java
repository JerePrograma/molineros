package ar.com.ospim.comprobantesPortalProveedores.beans;

import java.math.BigDecimal;
import java.util.Date;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;

public class ComprobanteFiltro extends Comprobante {
	private Date fechaEmisionDesde;
	private Date fechaEmisionHasta;
	private Date fechaRecepcionDesde;
	private Date fechaRecepcionHasta;
	private String medicamentoCodigo;
	private String medicamentoDescripcion;
	private Date fechaVencimientoDesde;
	private Date fechaVencimientoHasta;
	private Date periodoHasta;
	private Date carpeta;
	private Boolean pendientes;
	
	public Date getFechaEmisionDesde() {
		return fechaEmisionDesde;
	}
	public void setFechaEmisionDesde(Date fechaEmisionDesde) {
		this.fechaEmisionDesde = fechaEmisionDesde;
	}
	public Date getFechaEmisionHasta() {
		return fechaEmisionHasta;
	}
	public void setFechaEmisionHasta(Date fechaEmisionHasta) {
		this.fechaEmisionHasta = fechaEmisionHasta;
	}
	public Date getFechaRecepcionDesde() {
		return fechaRecepcionDesde;
	}
	public void setFechaRecepcionDesde(Date fechaRecepcionDesde) {
		this.fechaRecepcionDesde = fechaRecepcionDesde;
	}
	public Date getFechaRecepcionHasta() {
		return fechaRecepcionHasta;
	}
	public void setFechaRecepcionHasta(Date fechaRecepcionHasta) {
		this.fechaRecepcionHasta = fechaRecepcionHasta;
	}
	public ComprobanteFiltro() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ComprobanteFiltro(Comprobante comp) {
		super(comp);
		// TODO Auto-generated constructor stub
	}
	
	
	public String getMedicamentoCodigo() {
		return medicamentoCodigo;
	}
	public void setMedicamentoCodigo(String medicamentoCodigo) {
		this.medicamentoCodigo = medicamentoCodigo;
	}
	public String getMedicamentoDescripcion() {
		return medicamentoDescripcion;
	}
	public void setMedicamentoDescripcion(String medicamentoDescripcion) {
		this.medicamentoDescripcion = medicamentoDescripcion;
	}
		
	public Date getFechaVencimientoDesde() {
		return fechaVencimientoDesde;
	}
	public void setFechaVencimientoDesde(Date fechaVencimientoDesde) {
		this.fechaVencimientoDesde = fechaVencimientoDesde;
	}
	public Date getFechaVencimientoHasta() {
		return fechaVencimientoHasta;
	}
	public void setFechaVencimientoHasta(Date fechaVencimientoHasta) {
		this.fechaVencimientoHasta = fechaVencimientoHasta;
	}
	
	public Date getPeriodoHasta() {
		return periodoHasta;
	}
	public void setPeriodoHasta(Date periodoHasta) {
		this.periodoHasta = periodoHasta;
	}
	public Date getCarpeta() {
		return carpeta;
	}
	public void setCarpeta(Date carpeta) {
		this.carpeta = carpeta;
	}
	
	public Boolean getPendientes() {
		return pendientes;
	}
	
	public void setPendientes(Boolean pendientes) {
		this.pendientes = pendientes;
	}
	
	public ComprobanteFiltro(int ptoVenta, String tipoComprobante, String nroComprobante, String cuitEmisor,
			Date fechaEmision, Date fechaRecepcion, BigDecimal importeComprobante, String letraComprobante,
			int sucuComprobante, Date fechaVencimiento, Empresa empresaAcreedora, Date periodo) {
		super(ptoVenta, tipoComprobante, nroComprobante, cuitEmisor, fechaEmision, fechaRecepcion, importeComprobante,
				letraComprobante, sucuComprobante, fechaVencimiento, empresaAcreedora, periodo);
		// TODO Auto-generated constructor stub
	}
	public ComprobanteFiltro(int ptoVenta, String tipoComprobante, String nroComprobante, String cuitEmisor,
			Date fechaEmision, Date fechaRecepcion, BigDecimal importeComprobante, String letraComprobante,
			int sucuComprobante, Date fechaVencimiento) {
		super(ptoVenta, tipoComprobante, nroComprobante, cuitEmisor, fechaEmision, fechaRecepcion, importeComprobante,
				letraComprobante, sucuComprobante, fechaVencimiento);
		// TODO Auto-generated constructor stub
	}
	public ComprobanteFiltro(int ptoVenta, String tipoComprobante, String nroComprobante, String letraComprobante,
			int sucuComprobante, String cuitEmisor) {
		super(ptoVenta, tipoComprobante, nroComprobante, letraComprobante, sucuComprobante, cuitEmisor);
		// TODO Auto-generated constructor stub
	}
		
    
}

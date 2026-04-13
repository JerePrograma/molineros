package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaReporteReclamoFiltro implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date fechaOspim;
	private Date fechaOspim1;
	private int inte;
	private String cuilTitular;
	private int nroReclamo;
	private String  codePrestacion;
	private int tipoPrestacion;
	private int estado;
	private Date FechaCierre; 
	private Date FechaCierre1;    
	private String codigoTipoGestion;
	private String  resolucion;
	private String sectorSeleccionado; 
	private String tipoPedido;
	private Integer nroLote;
	private String frecuencia;
	private String  comprobanteTipo;
	private String sucursalComprobante;
	private String numeroComprobante;
	private Date fechaComprobante;
	private String  cuitEntidadComprobante;
	private int seccional;
	private int codintegracion;
	private int recuperableSur;
	
	
	public BusquedaReporteReclamoFiltro(Date fechaOspim, Date fechaOspim1, int inte, String cuilTitular, int nroReclamo,
			String codePrestacion, int tipoPrestacion, int estado, Date fechaCierre, Date fechaCierre1,
			String codigoTipoGestion, String resolucion, String sectorSeleccionado, String tipoPedido, Integer nroLote,
			String frecuencia, String comprobanteTipo, String sucursalComprobante, String numeroComprobante,
			Date fechaComprobante, String cuitEntidadComprobante, int seccional, int codintegracion,int recuperableSur) {
		super();
		this.fechaOspim = fechaOspim;
		this.fechaOspim1 = fechaOspim1;
		this.inte = inte;
		this.cuilTitular = cuilTitular;
		this.nroReclamo = nroReclamo;
		this.codePrestacion = codePrestacion;
		this.tipoPrestacion = tipoPrestacion;
		this.estado = estado;
		FechaCierre = fechaCierre;
		FechaCierre1 = fechaCierre1;
		this.codigoTipoGestion = codigoTipoGestion;
		this.resolucion = resolucion;
		this.sectorSeleccionado = sectorSeleccionado;
		this.tipoPedido = tipoPedido;
		this.nroLote = nroLote;
		this.frecuencia = frecuencia;
		this.comprobanteTipo = comprobanteTipo;
		this.sucursalComprobante = sucursalComprobante;
		this.numeroComprobante = numeroComprobante;
		this.fechaComprobante = fechaComprobante;
		this.cuitEntidadComprobante = cuitEntidadComprobante;
		this.seccional = seccional;
		this.codintegracion = codintegracion;
		this.recuperableSur =  recuperableSur;
	}
	
	
	public Date getFechaOspim() {
		return fechaOspim;
	}
	public void setFechaOspim(Date fechaOspim) {
		this.fechaOspim = fechaOspim;
	}
	public Date getFechaOspim1() {
		return fechaOspim1;
	}
	public void setFechaOspim1(Date fechaOspim1) {
		this.fechaOspim1 = fechaOspim1;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	public int getNroReclamo() {
		return nroReclamo;
	}
	public void setNroReclamo(int nroReclamo) {
		this.nroReclamo = nroReclamo;
	}
	public String getCodePrestacion() {
		return codePrestacion;
	}
	public void setCodePrestacion(String codePrestacion) {
		this.codePrestacion = codePrestacion;
	}
	public int getTipoPrestacion() {
		return tipoPrestacion;
	}
	public void setTipoPrestacion(int tipoPrestacion) {
		this.tipoPrestacion = tipoPrestacion;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public Date getFechaCierre() {
		return FechaCierre;
	}
	public void setFechaCierre(Date fechaCierre) {
		FechaCierre = fechaCierre;
	}
	public Date getFechaCierre1() {
		return FechaCierre1;
	}
	public void setFechaCierre1(Date fechaCierre1) {
		FechaCierre1 = fechaCierre1;
	}
	public String getCodigoTipoGestion() {
		return codigoTipoGestion;
	}
	public void setCodigoTipoGestion(String codigoTipoGestion) {
		this.codigoTipoGestion = codigoTipoGestion;
	}
	public String getResolucion() {
		return resolucion;
	}
	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}
	public String getSectorSeleccionado() {
		return sectorSeleccionado;
	}
	public void setSectorSeleccionado(String sectorSeleccionado) {
		this.sectorSeleccionado = sectorSeleccionado;
	}
	public String getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(String tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	public Integer getNroLote() {
		return nroLote;
	}
	public void setNroLote(Integer nroLote) {
		this.nroLote = nroLote;
	}
	public String getFrecuencia() {
		return frecuencia;
	}
	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}
	public String getComprobanteTipo() {
		return comprobanteTipo;
	}
	public void setComprobanteTipo(String comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}
	public String getSucursalComprobante() {
		return sucursalComprobante;
	}
	public void setSucursalComprobante(String sucursalComprobante) {
		this.sucursalComprobante = sucursalComprobante;
	}
	public String getNumeroComprobante() {
		return numeroComprobante;
	}
	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}
	public Date getFechaComprobante() {
		return fechaComprobante;
	}
	public void setFechaComprobante(Date fechaComprobante) {
		this.fechaComprobante = fechaComprobante;
	}
	public String getCuitEntidadComprobante() {
		return cuitEntidadComprobante;
	}
	public void setCuitEntidadComprobante(String cuitEntidadComprobante) {
		this.cuitEntidadComprobante = cuitEntidadComprobante;
	}
	public int getSeccional() {
		return seccional;
	}
	public void setSeccional(int seccional) {
		this.seccional = seccional;
	}


	public int getCodintegracion() {
		return codintegracion;
	}


	public void setCodintegracion(int codintegracion) {
		this.codintegracion = codintegracion;
	}


	public int getRecuperableSur() {
		return recuperableSur;
	}


	public void setRecuperableSur(int recuperableSur) {
		this.recuperableSur = recuperableSur;
	}
	
	
	
	
	

}

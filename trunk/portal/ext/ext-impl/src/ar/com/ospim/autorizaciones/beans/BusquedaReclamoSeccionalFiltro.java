package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaReclamoSeccionalFiltro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String nroReclamo;
	private String tipoPedido;
	private String sector;
	private Date fechaDesde;
	private Date fechaHasta;
	private String nroAfilido;
	private String cuil;
	private String integrante;
	private String tipoDocumentoSel;
	private String nroDocumento;
	private String apellido;
	private String nombre;
	private String idSeccionalAfiliado;
	private String descSeccionalAfiliado;
	private String estado;
	
	
	public BusquedaReclamoSeccionalFiltro(String nroReclamo, String tipoPedido, String estado, 
			String sector, Date fechaDesde,
			Date fechaHasta, String nroAfilido, String cuil, String integrante, String tipoDocumentoSel,
			String nroDocumento,   String apellido,
			String nombre, String idSeccionalAfiliado, String descSeccionalAfiliado) {
		super();
		this.nroReclamo = nroReclamo;
		this.tipoPedido = tipoPedido;
		this.sector = sector;
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.nroAfilido = nroAfilido;
		this.cuil = cuil;
		this.integrante = integrante;
		this.tipoDocumentoSel = tipoDocumentoSel;
		this.nroDocumento = nroDocumento;
		this.apellido = apellido;
		this.nombre = nombre;
		this.idSeccionalAfiliado = idSeccionalAfiliado;
		this.descSeccionalAfiliado = descSeccionalAfiliado;
		this.estado = estado;
	}
	

	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getNroReclamo() {
		return nroReclamo;
	}
	public void setNroReclamo(String nroReclamo) {
		this.nroReclamo = nroReclamo;
	}
	public String getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(String tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
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
	public String getNroAfilido() {
		return nroAfilido;
	}
	public void setNroAfilido(String nroAfilido) {
		this.nroAfilido = nroAfilido;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public String getIntegrante() {
		return integrante;
	}
	public void setIntegrante(String integrante) {
		this.integrante = integrante;
	}
	public String getTipoDocumentoSel() {
		return tipoDocumentoSel;
	}
	public void setTipoDocumentoSel(String tipoDocumentoSel) {
		this.tipoDocumentoSel = tipoDocumentoSel;
	}
	public String getNroDocumento() {
		return nroDocumento;
	}
	public void setNroDocumento(String nroDocumento) {
		this.nroDocumento = nroDocumento;
	}
	
	
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getIdSeccionalAfiliado() {
		return idSeccionalAfiliado;
	}

	public void getIdSeccionalAfiliado(String idSeccional) {
		this.idSeccionalAfiliado = idSeccional;
	}

	public String getDescSeccionalAfiliado() {
		return descSeccionalAfiliado;
	}

	public void setDescSeccionalAfiliado(String descSeccionalAfiliado) {
		this.descSeccionalAfiliado = descSeccionalAfiliado;
	}
	
	
	
	


}

package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaContactoFiltro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4279476824625735475L;
	private Date fechaDesde;
	private Date fechaHasta;
	private int motivo;
	private int categoria;
	private int tipo;
	private String estado;
	private String cuil_titular;
	private String inte;
	private int incluirA;
	private int importancia = 99;
	private int incumplimientoContacto = 99;
	private int pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	private int nro_contacto;
	private String sector;
	private String usuario;
	private int idPlan;
	private int idPlanOmint; 
	private int eficaciaConformidad = 99;
	private int seccional;
	private String noAfiliadoDocNumero;
	private Integer prestador;
	private String cuit;
	private String sucursal;
	
	
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
	public int getRegistrosTotal() {
		return registrosTotal;
	}
	public void setRegistrosTotal(int registrosTotal) {
		this.registrosTotal = registrosTotal;
	}
	public int getRegistrosPorPagina() {
		return registrosPorPagina;
	}
	public int getMotivo() {
		return motivo;
	}
	public void setMotivo(int motivo) {
		this.motivo = motivo;
	}
	public int getCategoria() {
		return categoria;
	}
	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public String getCuil_titular() {
		return cuil_titular;
	}
	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}
	public String getInte() {
		return inte;
	}
	public void setInte(String inte) {
		this.inte = inte;
	}
	public int getNro_contacto() {
		return nro_contacto;
	}
	public void setNro_contacto(int nro_contacto) {
		this.nro_contacto = nro_contacto;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public int getIdPlan() {
		return idPlan;
	}
	public void setIdPlan(int idPlan) {
		this.idPlan = idPlan;
	}
	public int getIdPlanOmint() {
		return idPlanOmint;
	}
	public void setIdPlanOmint(int idPlanOmint) {
		this.idPlanOmint = idPlanOmint;
	}
	public int getImportancia() {
		return importancia;
	}
	public void setImportancia(int importancia) {
		this.importancia = importancia;
	}
	public int getIncluirA() {
		return incluirA;
	}
	public void setIncluirA(int incluirA) {
		this.incluirA = incluirA;
	}
	public int getIncumplimientoContacto() {
		return incumplimientoContacto;
	}
	public void setIncumplimientoContacto(int incumplimientoContacto) {
		this.incumplimientoContacto = incumplimientoContacto;
	}
	public int getEficaciaConformidad() {
		return eficaciaConformidad;
	}
	public void setEficaciaConformidad(int eficaciaConformidad) {
		this.eficaciaConformidad = eficaciaConformidad;
	}
	public int getSeccional() {
		return seccional;
	}
	public void setSeccional(int seccional) {
		this.seccional = seccional;
	}
	public String getNoAfiliadoDocNumero() {
		return noAfiliadoDocNumero;
	}
	public void setNoAfiliadoDocNumero(String noAfiliadoDocNumero) {
		this.noAfiliadoDocNumero = noAfiliadoDocNumero;
	}
	public Integer getPrestador() {
		return prestador;
	}
	public void setPrestador(Integer prestador) {
		this.prestador = prestador;
	}
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	
}

package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaDocumLegalFiltro implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4970497589850577916L;
	
	private Date fechaDesde;
	private Date fechaHasta;
	private int motivo;
	private int tipoReclamo;
	private String cuil_titular;
	private String inte;
	private int incluirA;
	private int pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	private int idDocumLegal;
	private int idPlan;
	private int idPlanOmint;
	private boolean antecedente; 
	private boolean concluido; 
	private boolean noConcluido; 
	
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
	public int getMotivo() {
		return motivo;
	}
	public void setMotivo(int motivo) {
		this.motivo = motivo;
	}
	public int getTipoReclamo() {
		return tipoReclamo;
	}
	public void setTipoReclamo(int tipoReclamo) {
		this.tipoReclamo = tipoReclamo;
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
	public int getIdDocumLegal() {
		return idDocumLegal;
	}
	public void setIdDocumLegal(int idDocumLegal) {
		this.idDocumLegal = idDocumLegal;
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
	public int getIncluirA() {
		return incluirA;
	}
	public void setIncluirA(int incluirA) {
		this.incluirA = incluirA;
	}
	public boolean isTieneAntecedente() {
		return antecedente;
	}
	public void setAntecedente(boolean antecedente) {
		this.antecedente = antecedente;
	}
	public boolean isConcluido() {
		return concluido;
	}
	public void setConcluido(boolean concluido) {
		this.concluido = concluido;
	}
	public boolean isNoConcluido() {
		return noConcluido;
	}
	public void setNoConcluido(boolean noConcluido) {
		this.noConcluido = noConcluido;
	}
	
}

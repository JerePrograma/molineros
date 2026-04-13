package ar.com.uoma.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

public class ActasAcuerdos {
	
	public static final int CON_SALDO=0;
	public static final int SIN_SALDO=1;
	public static final int TODOS=2;
	
	private String cuit;
	private String razonSoc; 
	private String numero;
	private Date fechaCierre; 
	private Date periodoIni;
	private Date periodoFin;
	private BigDecimal capitalSindicato;
	private BigDecimal capitalSolidario;
	private BigDecimal capitalArt46;
	private BigDecimal capitalUsufructo;
	private BigDecimal capitalTotal;
	private BigDecimal interesSindicato; 
	private BigDecimal interesSolidario;
	private BigDecimal interesArt46; 
    private BigDecimal interesUsufructo;
    private BigDecimal interesTotal;
    private BigDecimal total;
    private String convenioPago; 
    private BigDecimal totalPagado; 
    private String modi_usr;
    private BigDecimal saldo;
    private String actasAsociadas;
    private BigDecimal ajusteCapital;
    private BigDecimal ajusteInteres;
    private int cantCuotas;
    private Date vtoCuota1;
    private Date vtoCuotaUltima;
	
	public static ActasAcuerdos getMappingActas(ResultSet rs)
			throws Exception {
		ActasAcuerdos acAc=new ActasAcuerdos();
		acAc.setCuit(rs.getString("cuit"));
		acAc.setRazonSoc(rs.getString("razon_soc"));
		acAc.setNumero(rs.getString("numero"));
		acAc.setFechaCierre(rs.getDate("cierre_fecha"));
		acAc.setPeriodoIni(rs.getDate("periodo_ini"));
		acAc.setPeriodoFin(rs.getDate("periodo_fin"));
		acAc.setCapitalSindicato(rs.getBigDecimal("capital_sindicato"));
		acAc.setCapitalSolidario(rs.getBigDecimal("capital_solidario"));
		acAc.setCapitalArt46(rs.getBigDecimal("capital_art46"));
		acAc.setCapitalUsufructo(rs.getBigDecimal("capital_usufructo"));
		acAc.setCapitalTotal(rs.getBigDecimal("capital"));
		acAc.setInteresSindicato(rs.getBigDecimal("interes_sindicato"));
		acAc.setInteresSolidario(rs.getBigDecimal("interes_solidario"));
		acAc.setInteresArt46(rs.getBigDecimal("interes_art46"));
		acAc.setInteresUsufructo(rs.getBigDecimal("interes_usufructo"));
		acAc.setInteresTotal(rs.getBigDecimal("interes"));
		acAc.setTotal(rs.getBigDecimal("total"));
		acAc.setConvenioPago(rs.getString("convenio"));
		acAc.setTotalPagado(rs.getBigDecimal("total_pagos"));
		acAc.setModi_usr(rs.getString("modi_usr"));
		acAc.setSaldo(rs.getBigDecimal("saldo"));
		return acAc;	
	}
	
	public static ActasAcuerdos getMappingAcuerdos(ResultSet rs)
			throws Exception {
		ActasAcuerdos acAc=new ActasAcuerdos();
		acAc.setCuit(rs.getString("cuit"));
		acAc.setRazonSoc(rs.getString("razon_soc"));
		acAc.setNumero(rs.getString("numero"));
		acAc.setFechaCierre(rs.getDate("fecha"));
		acAc.setActasAsociadas(rs.getString("actas_asoc"));		
		acAc.setCapitalTotal(rs.getBigDecimal("capital"));
		acAc.setInteresTotal(rs.getBigDecimal("interes"));
		acAc.setAjusteCapital(rs.getBigDecimal("ajuste_capital"));
		acAc.setAjusteInteres(rs.getBigDecimal("ajuste_interes"));
		acAc.setTotal(rs.getBigDecimal("total"));
		acAc.setCantCuotas(rs.getInt("cant_cuotas"));
		acAc.setVtoCuota1(rs.getDate("vto_1_cuota"));
		acAc.setVtoCuotaUltima(rs.getDate("vto_uma_cuota"));		
		acAc.setTotalPagado(rs.getBigDecimal("total_pagos"));		
		acAc.setSaldo(rs.getBigDecimal("saldo"));
		acAc.setModi_usr(rs.getString("modi_usr"));
		return acAc;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazonSoc() {
		return razonSoc;
	}

	public void setRazonSoc(String razonSoc) {
		this.razonSoc = razonSoc;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public Date getPeriodoIni() {
		return periodoIni;
	}

	public void setPeriodoIni(Date periodoIni) {
		this.periodoIni = periodoIni;
	}

	public Date getPeriodoFin() {
		return periodoFin;
	}

	public void setPeriodoFin(Date periodoFin) {
		this.periodoFin = periodoFin;
	}

	public BigDecimal getCapitalSindicato() {
		return capitalSindicato;
	}

	public void setCapitalSindicato(BigDecimal capitalSindicato) {
		this.capitalSindicato = capitalSindicato;
	}

	public BigDecimal getCapitalSolidario() {
		return capitalSolidario;
	}

	public void setCapitalSolidario(BigDecimal capitalSolidario) {
		this.capitalSolidario = capitalSolidario;
	}

	public BigDecimal getCapitalArt46() {
		return capitalArt46;
	}

	public void setCapitalArt46(BigDecimal capitalArt46) {
		this.capitalArt46 = capitalArt46;
	}

	public BigDecimal getCapitalUsufructo() {
		return capitalUsufructo;
	}

	public void setCapitalUsufructo(BigDecimal capitalUsufructo) {
		this.capitalUsufructo = capitalUsufructo;
	}

	public BigDecimal getCapitalTotal() {
		return capitalTotal;
	}

	public void setCapitalTotal(BigDecimal capitalTotal) {
		this.capitalTotal = capitalTotal;
	}

	public BigDecimal getInteresSindicato() {
		return interesSindicato;
	}

	public void setInteresSindicato(BigDecimal interesSindicato) {
		this.interesSindicato = interesSindicato;
	}

	public BigDecimal getInteresSolidario() {
		return interesSolidario;
	}

	public void setInteresSolidario(BigDecimal interesSolidario) {
		this.interesSolidario = interesSolidario;
	}

	public BigDecimal getInteresArt46() {
		return interesArt46;
	}

	public void setInteresArt46(BigDecimal interesArt46) {
		this.interesArt46 = interesArt46;
	}

	public BigDecimal getInteresUsufructo() {
		return interesUsufructo;
	}

	public void setInteresUsufructo(BigDecimal interesUsufructo) {
		this.interesUsufructo = interesUsufructo;
	}

	public BigDecimal getInteresTotal() {
		return interesTotal;
	}

	public void setInteresTotal(BigDecimal interesTotal) {
		this.interesTotal = interesTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getConvenioPago() {
		return convenioPago;
	}

	public void setConvenioPago(String convenioPago) {
		this.convenioPago = convenioPago;
	}

	public BigDecimal getTotalPagado() {
		return totalPagado;
	}

	public void setTotalPagado(BigDecimal totalPagado) {
		this.totalPagado = totalPagado;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public String getActasAsociadas() {
		return actasAsociadas;
	}

	public void setActasAsociadas(String actasAsociadas) {
		this.actasAsociadas = actasAsociadas;
	}

	public BigDecimal getAjusteCapital() {
		return ajusteCapital;
	}

	public void setAjusteCapital(BigDecimal ajusteCapital) {
		this.ajusteCapital = ajusteCapital;
	}

	public BigDecimal getAjusteInteres() {
		return ajusteInteres;
	}

	public void setAjusteInteres(BigDecimal ajusteInteres) {
		this.ajusteInteres = ajusteInteres;
	}

	public int getCantCuotas() {
		return cantCuotas;
	}

	public void setCantCuotas(int cantCuotas) {
		this.cantCuotas = cantCuotas;
	}

	public Date getVtoCuota1() {
		return vtoCuota1;
	}

	public void setVtoCuota1(Date vtoCuota1) {
		this.vtoCuota1 = vtoCuota1;
	}

	public Date getVtoCuotaUltima() {
		return vtoCuotaUltima;
	}

	public void setVtoCuotaUltima(Date vtoCuotaUltima) {
		this.vtoCuotaUltima = vtoCuotaUltima;
	}		
	
	
	
}

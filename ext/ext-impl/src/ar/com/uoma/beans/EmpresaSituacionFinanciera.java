package ar.com.uoma.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

public class EmpresaSituacionFinanciera {
	
	private String cuit;
	private String razonSoc; 
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
    private BigDecimal totalPagado; 
    private BigDecimal saldo;
    private BigDecimal estimado;
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
	public BigDecimal getTotalPagado() {
		return totalPagado;
	}
	public void setTotalPagado(BigDecimal totalPagado) {
		this.totalPagado = totalPagado;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public BigDecimal getEstimado() {
		return estimado;
	}
	public void setEstimado(BigDecimal estimado) {
		this.estimado = estimado;
	}
    
	
	
	public EmpresaSituacionFinanciera() {
		super();
		this.capitalSindicato = BigDecimal.ZERO ;
		this.capitalSolidario = BigDecimal.ZERO;
		this.capitalArt46 = BigDecimal.ZERO;
		this.capitalUsufructo = BigDecimal.ZERO;
		this.capitalTotal = BigDecimal.ZERO;
		this.interesSindicato = BigDecimal.ZERO;
		this.interesSolidario = BigDecimal.ZERO;
		this.interesArt46 = BigDecimal.ZERO;
		this.interesUsufructo = BigDecimal.ZERO;
		this.interesTotal = BigDecimal.ZERO;
		this.total = BigDecimal.ZERO;
		this.totalPagado = BigDecimal.ZERO;
		this.saldo = BigDecimal.ZERO;
		this.estimado = BigDecimal.ZERO;
	}
	
	
	
}

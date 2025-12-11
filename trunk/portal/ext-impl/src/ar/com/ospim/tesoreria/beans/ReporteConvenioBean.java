package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteConvenioBean {
	private String numero;
	private Date fecha;
	private String cuit;
	private String sucursal;
	private String razonSocial;
	private BigDecimal capital;
	private BigDecimal interes;
	private BigDecimal ajusteCapital;
	private BigDecimal ajusteInteres;
	private String numeroActaAsociada;
	private BigDecimal total;
	private String entidad;
	
	

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
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

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getInteres() {
		return interes;
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

	public String getNumeroActaAsociada() {
		return numeroActaAsociada;
	}

	public void setNumeroActaAsociada(String numeroActaAsociada) {
		this.numeroActaAsociada = numeroActaAsociada;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
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

	public static ReporteConvenioBean getMapping(ResultSet rs)
			throws SQLException {
		ReporteConvenioBean repo = new ReporteConvenioBean();
		map(rs, repo, false);
		return repo;
	}
	
	public static ReporteConvenioBean getMappingNoOS(ResultSet rs)
			throws SQLException {
		ReporteConvenioBean repo = new ReporteConvenioBean();
		map(rs, repo, true);
		return repo;
	}

	protected static void map(ResultSet rs, ReporteConvenioBean repo, boolean no_os)
			throws SQLException {
		repo.setNumero(rs.getString("numero"));
		repo.setFecha(rs.getDate("fecha_inicio"));
		repo.setCapital(rs.getBigDecimal("capital"));
		repo.setInteres(rs.getBigDecimal("interes"));
		repo.setAjusteCapital(rs.getBigDecimal("ajuste_capital"));
		repo.setAjusteInteres(rs.getBigDecimal("ajuste_interes"));
		repo.setTotal(rs.getBigDecimal("total"));
		repo.setCuit(rs.getString("cuit"));
		repo.setSucursal(rs.getString("sucursal"));
		repo.setRazonSocial(rs.getString("razon_soc"));
		repo.setNumeroActaAsociada(rs.getString("acta_asoc"));
		if(no_os){
			repo.setEntidad(rs.getString("entidad"));
		}else{
			repo.setEntidad("OSPIM");
		}
	}
}

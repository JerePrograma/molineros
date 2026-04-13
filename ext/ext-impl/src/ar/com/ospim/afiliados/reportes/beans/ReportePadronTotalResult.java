package ar.com.ospim.afiliados.reportes.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;

import ar.com.ospim.global.beans.RamoEmpresa;

public class ReportePadronTotalResult {

	private String tercerizadora;
	private String parentesco;
	private String plan;
	private String seccional;
	private String cuit;
	private String razon_soc;
	private int total;
	private int uoma_titular;
	private int ospim_titular;
	private int amtima_titular;
	private int uoma_adherente;
	private int ospim_adherente;
	private int amtima_adherente;
	private int totalTitulares;
	private int totalIntegrantes;
	private int totalCapitasTitular;
	private int totalDesreguladosTitular;
	private int totalCapitasAdherente;
	private int totalDesreguladosAdherente;	
	private RamoEmpresa ramoEmpresa;
	private int nroSocio;
	private BigDecimal nroCreden;
	private int cantTitular;
	private int cantAdherente;
	
 
	
	
	public static ReportePadronTotalResult getMapping(ResultSet rs, BusquedaReportePadronFiltro filtro) throws Exception {
		
		ReportePadronTotalResult repo = new ReportePadronTotalResult();
		
		if (filtro.isTotalesPorEntidad()) {
			repo.setSeccional(rs.getString("seccional"));
			repo.setUoma_titular(rs.getInt("uoma_titular"));
			repo.setUoma_adherente(rs.getInt("uoma_adherente"));
			repo.setOspim_titular(rs.getInt("ospim_titular"));
			repo.setOspim_adherente(rs.getInt("ospim_adherente"));
			repo.setAmtima_titular(rs.getInt("amtima_titular"));
			repo.setAmtima_adherente(rs.getInt("amtima_adherente"));
			repo.setTotalTitulares(rs.getInt("total_titulares"));
			repo.setTotalIntegrantes(rs.getInt("total_integrantes"));
			repo.setTotalCapitasTitular(rs.getInt("ospim_capitas_titular"));
			repo.setTotalDesreguladosTitular(rs.getInt("ospim_desregulados_titular"));
			repo.setTotalCapitasAdherente(rs.getInt("ospim_capitas_adherente"));
			repo.setTotalDesreguladosAdherente(rs.getInt("ospim_desregulados_adherente"));
			
		} else {
			if (filtro.isTotalesPorTercerizadora()) {
				repo.setTercerizadora(rs.getString("id_tercerizadora"));
			}
			if (filtro.isTotalesPorPlan()) {
				repo.setPlan(rs.getString("plan"));
			}
			if (filtro.isTotalesPorSeccional()) {
				repo.setSeccional(rs.getString("seccional"));
			}
			if (filtro.isTotalesPorEmpresa()) {
				repo.setCuit(rs.getString("cuit"));
				repo.setRazon_soc(rs.getString("razon_soc"));
				repo.setRamoEmpresa(new RamoEmpresa(Integer.valueOf(rs.getString("ramo").trim()),rs.getString("descripcion_ramo") ));
			}
			if (filtro.isTotalesPorPlan() && filtro.isVistaPrevencion()) {
				repo.setCantTitular(rs.getInt("titular"));
				repo.setCantAdherente(rs.getInt("adherente"));
			}else {				
				repo.parentesco = rs.getString("parentesco");
				repo.total = rs.getInt("total");
			}
		}
		
		return repo;
	}

	public String getTercerizadora() {
		return tercerizadora;
	}

	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}

	public String getParentesco() {
		return parentesco;
	}

	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazon_soc() {
		return razon_soc;
	}

	public void setRazon_soc(String razon_soc) {
		this.razon_soc = razon_soc;
	}

	public int getUoma_titular() {
		return uoma_titular;
	}

	public void setUoma_titular(int uoma_titular) {
		this.uoma_titular = uoma_titular;
	}

	public int getOspim_titular() {
		return ospim_titular;
	}

	public void setOspim_titular(int ospim_titular) {
		this.ospim_titular = ospim_titular;
	}

	public int getAmtima_titular() {
		return amtima_titular;
	}

	public void setAmtima_titular(int amtima_titular) {
		this.amtima_titular = amtima_titular;
	}

	public int getUoma_adherente() {
		return uoma_adherente;
	}

	public void setUoma_adherente(int uoma_adherente) {
		this.uoma_adherente = uoma_adherente;
	}

	public int getOspim_adherente() {
		return ospim_adherente;
	}

	public void setOspim_adherente(int ospim_adherente) {
		this.ospim_adherente = ospim_adherente;
	}

	public int getAmtima_adherente() {
		return amtima_adherente;
	}

	public void setAmtima_adherente(int amtima_adherente) {
		this.amtima_adherente = amtima_adherente;
	}

	public int getTotalTitulares() {
		return totalTitulares;
	}

	public void setTotalTitulares(int totalTitulares) {
		this.totalTitulares = totalTitulares;
	}

	public int getTotalIntegrantes() {
		return totalIntegrantes;
	}

	public void setTotalIntegrantes(int totalIntegrantes) {
		this.totalIntegrantes = totalIntegrantes;
	}

	public int getTotalCapitasTitular() {
		return totalCapitasTitular;
	}

	public void setTotalCapitasTitular(int totalCapitasTitular) {
		this.totalCapitasTitular = totalCapitasTitular;
	}

	public int getTotalDesreguladosTitular() {
		return totalDesreguladosTitular;
	}

	public void setTotalDesreguladosTitular(int totalDesreguladosTitular) {
		this.totalDesreguladosTitular = totalDesreguladosTitular;
	}

	public int getTotalCapitasAdherente() {
		return totalCapitasAdherente;
	}

	public void setTotalCapitasAdherente(int totalCapitasAdherente) {
		this.totalCapitasAdherente = totalCapitasAdherente;
	}

	public int getTotalDesreguladosAdherente() {
		return totalDesreguladosAdherente;
	}

	public void setTotalDesreguladosAdherente(int totalDesreguladosAdherente) {
		this.totalDesreguladosAdherente = totalDesreguladosAdherente;
	}
	
	
    public RamoEmpresa getRamoEmpresa() {
		return ramoEmpresa;
	}

	public void setRamoEmpresa(RamoEmpresa ramoEmpresa) {
		this.ramoEmpresa = ramoEmpresa;
	}
	

	public int getNroSocio() {
		return nroSocio;
	}

	public BigDecimal getNroCreden() {
		return nroCreden;
	}

	public void setNroSocio(int nroSocio) {
		this.nroSocio = nroSocio;
	}

	public void setNroCreden(BigDecimal nroCreden) {
		this.nroCreden = nroCreden;
	}

	public int getCantTitular() {
		return cantTitular;
	}

	public int getCantAdherente() {
		return cantAdherente;
	}

	public void setCantTitular(int cantTitular) {
		this.cantTitular = cantTitular;
	}

	public void setCantAdherente(int cantAdherente) {
		this.cantAdherente = cantAdherente;
	}


	@Override
	public String toString() {
		return "ReportePadronTotalResult [tercerizadora=" + tercerizadora
				+ ", parentesco=" + parentesco + ", plan=" + plan
				+ ", seccional=" + seccional + ", cuit=" + cuit
				+ ", razon_soc=" + razon_soc + ", total=" + total
				+ ", uoma_titular=" + uoma_titular + ", ospim_titular="
				+ ospim_titular + ", amtima_titular=" + amtima_titular
				+ ", uoma_adherente=" + uoma_adherente + ", ospim_adherente="
				+ ospim_adherente + ", amtima_adherente=" + amtima_adherente
				+ ", totalTitulares=" + totalTitulares + ", totalIntegrantes="
				+ totalIntegrantes + ", totalCapitasTitular="
				+ totalCapitasTitular + ", totalDesreguladosTitular="
				+ totalDesreguladosTitular + ", totalCapitasAdherente="
				+ totalCapitasAdherente + ", totalDesreguladosAdherente="
				+ totalDesreguladosAdherente + "]";
	}

	
}

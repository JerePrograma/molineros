package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteResumenProcesoCalcDeudaMasivoBean {
	private int idProceso;
	private String cuit;
	private String sucursal;
	private String razonSocial;
	private boolean molinera;
	private boolean empresaOMonotrib;
	private Date periodo; 
	private BigDecimal totalDeuda;
	private int cantidadAfiliados;

	
	public int getIdProceso() {
		return idProceso;
	}

	public void setIdProceso(int idProceso) {
		this.idProceso = idProceso;
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

	public boolean isMolinera() {
		return molinera;
	}

	public void setMolinera(boolean molinera) {
		this.molinera = molinera;
	}

	public boolean isEmpresaOMonotrib() {
		return empresaOMonotrib;
	}

	public void setEmpresaOMonotrib(boolean empresaOMonotrib) {
		this.empresaOMonotrib = empresaOMonotrib;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public BigDecimal getTotalDeuda() {
		return totalDeuda;
	}

	public void setTotalDeuda(BigDecimal totalDeuda) {
		this.totalDeuda = totalDeuda;
	}

	public int getCantidadAfiliados() {
		return cantidadAfiliados;
	}

	public void setCantidadAfiliados(int cantidadAfiliados) {
		this.cantidadAfiliados = cantidadAfiliados;
	}

	public static ReporteResumenProcesoCalcDeudaMasivoBean getMapping(ResultSet rs) throws SQLException {
		
		ReporteResumenProcesoCalcDeudaMasivoBean a = new ReporteResumenProcesoCalcDeudaMasivoBean();
		
		a.setIdProceso(rs.getInt("id_proceso"));
		a.setCuit(rs.getString("cuit"));
		a.setSucursal(rs.getString("sucursal"));
		a.setMolinera(rs.getBoolean("molinera"));
		a.setEmpresaOMonotrib(rs.getBoolean("monotributista"));
		a.setCantidadAfiliados(rs.getInt("cant_afiliados"));
		a.setRazonSocial(rs.getString("razon_social"));
		a.setTotalDeuda(rs.getBigDecimal("total_deuda"));
		a.setPeriodo(rs.getDate("min_periodo"));
		
		return a;
	}

}

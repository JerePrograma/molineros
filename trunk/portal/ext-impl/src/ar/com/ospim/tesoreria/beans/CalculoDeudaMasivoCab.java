package ar.com.ospim.tesoreria.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.estudioisidro.beans.ActaAcuerdoSeguimiento;

public class CalculoDeudaMasivoCab implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1086506862791380079L;
	
	private int idProceso;
	private String entidad;
	private String solicitaUsr;
	private Date solicitaFecha;
	private Date deudaNominaDesde; 
	private Date deudaNominaHasta;
	private Date deudaDesde; 
	private Date deudaHasta;
	private boolean sinDeudaNomina;
	private Date fechaImpago;
	private Date fechaObligacion;
	private boolean extender30DiasMoli;
	private Integer cantidadEmpresas;
	private BigDecimal importeDeudaTotal;
	
	public int getIdProceso() {
		return idProceso;
	}
	public void setIdProceso(int idProceso) {
		this.idProceso = idProceso;
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public String getSolicitaUsr() {
		return solicitaUsr;
	}
	public void setSolicitaUsr(String solicitaUsr) {
		this.solicitaUsr = solicitaUsr;
	}
	public Date getSolicitaFecha() {
		return solicitaFecha;
	}
	public void setSolicitaFecha(Date solicitaFecha) {
		this.solicitaFecha = solicitaFecha;
	}
	public Date getDeudaNominaDesde() {
		return deudaNominaDesde;
	}
	public void setDeudaNominaDesde(Date deudaNominaDesde) {
		this.deudaNominaDesde = deudaNominaDesde;
	}
	public Date getDeudaNominaHasta() {
		return deudaNominaHasta;
	}
	public void setDeudaNominaHasta(Date deudaNominaHasta) {
		this.deudaNominaHasta = deudaNominaHasta;
	}
	public Date getDeudaDesde() {
		return deudaDesde;
	}
	public void setDeudaDesde(Date deudaDesde) {
		this.deudaDesde = deudaDesde;
	}
	public Date getDeudaHasta() {
		return deudaHasta;
	}
	public void setDeudaHasta(Date deudaHasta) {
		this.deudaHasta = deudaHasta;
	}
	public boolean isSinDeudaNomina() {
		return sinDeudaNomina;
	}
	public void setSinDeudaNomina(boolean sinDeudaNomina) {
		this.sinDeudaNomina = sinDeudaNomina;
	}
	public Date getFechaImpago() {
		return fechaImpago;
	}
	public void setFechaImpago(Date fechaImpago) {
		this.fechaImpago = fechaImpago;
	}
	public Date getFechaObligacion() {
		return fechaObligacion;
	}
	public void setFechaObligacion(Date fechaObligacion) {
		this.fechaObligacion = fechaObligacion;
	}
	public boolean isExtender30DiasMoli() {
		return extender30DiasMoli;
	}
	public void setExtender30DiasMoli(boolean extender30DiasMoli) {
		this.extender30DiasMoli = extender30DiasMoli;
	}
	public Integer getCantidadEmpresas() {
		return cantidadEmpresas;
	}
	public void setCantidadEmpresas(Integer cantidadEmpresas) {
		this.cantidadEmpresas = cantidadEmpresas;
	}
	public BigDecimal getImporteDeudaTotal() {
		return importeDeudaTotal;
	}
	public void setImporteDeudaTotal(BigDecimal importeDeudaTotal) {
		this.importeDeudaTotal = importeDeudaTotal;
	}
	
	public static CalculoDeudaMasivoCab getMapping(ResultSet rs) throws SQLException {
		
		CalculoDeudaMasivoCab cab=new CalculoDeudaMasivoCab();	
		
		cab.setIdProceso(rs.getInt("id_proceso"));
		cab.setEntidad(rs.getString("entidad"));
		cab.setSolicitaUsr(rs.getString("solicita_usr"));
		cab.setSolicitaFecha(rs.getTimestamp("solicita_fecha"));
		cab.setDeudaNominaDesde(rs.getDate("deuda_nomina_desde"));
		cab.setDeudaNominaHasta(rs.getDate("deuda_nomina_hasta"));
		cab.setDeudaDesde(rs.getDate("deuda_desde"));
		cab.setDeudaHasta(rs.getDate("deuda_hasta"));
		cab.setSinDeudaNomina(rs.getBoolean("sin_deuda_nomina"));
		cab.setFechaImpago(rs.getDate("fecha_impago"));
		cab.setFechaObligacion(rs.getDate("fecha_obligacion"));
		cab.setExtender30DiasMoli(rs.getBoolean("extender_30_dias_moli"));
		
		try{
			cab.setImporteDeudaTotal(rs.getBigDecimal("importe_deuda_total"));
			cab.setCantidadEmpresas(rs.getInt("cantidad_empresas"));
		}catch(Exception e){
			cab.setImporteDeudaTotal(new BigDecimal(0));
			cab.setCantidadEmpresas(0);
		}

		return cab;
	}
	
}

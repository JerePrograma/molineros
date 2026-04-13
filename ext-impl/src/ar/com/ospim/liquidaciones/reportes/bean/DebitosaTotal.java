package ar.com.ospim.liquidaciones.reportes.bean;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DebitosaTotal {
	
	private int id;
	private BigDecimal montoHospitales;
	private BigDecimal montoReintegros;
	private BigDecimal montoPrestadores;
	private BigDecimal montoLiquidacionPendiente;
	
	//Montos de reintegro de la prestadora prevencion
	private BigDecimal montoHospitaleDebito;
	private BigDecimal montoReintegroDebito;
	private BigDecimal montoPrestadoreDebito;
	private BigDecimal montoLiquidacionPendienteDebito;
	
	private boolean existeDebito;
	
	private Date altaFecha;
	private Date periodo;
	private String altaUsr;
	private String idTercerizadora;
	private String descTercerizadora;
	private BigDecimal total;
	
	
	
	
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getMontoHospitaleDebito() {
		return montoHospitaleDebito;
	}
	public BigDecimal getMontoReintegroDebito() {
		return montoReintegroDebito;
	}
	public BigDecimal getMontoPrestadoreDebito() {
		return montoPrestadoreDebito;
	}

	public void setMontoHospitaleDebito(BigDecimal montoHospitaleDebito) {
		this.montoHospitaleDebito = montoHospitaleDebito;
	}
	public void setMontoReintegroDebito(BigDecimal montoReintegroDebito) {
		this.montoReintegroDebito = montoReintegroDebito;
	}
	public void setMontoPrestadoreDebito(BigDecimal montoPrestadoreDebito) {
		this.montoPrestadoreDebito = montoPrestadoreDebito;
	}
	/*public void setMontoAutogestionDebito(BigDecimal montoAutogestionDebito) {
		this.montoLiquidacionPendienteDebito = montoAutogestionDebito;
	}
	public BigDecimal getMontoAutogestionDebito() {
		return montoLiquidacionPendienteDebito;
	}*/
	
	public BigDecimal getMontoHospitales() {
		return montoHospitales;
	}
	public BigDecimal getMontoReintegros() {
		return montoReintegros;
	}
	public BigDecimal getMontoPrestadores() {
		return montoPrestadores;
	}
	
	public void setMontoHospitales(BigDecimal montoHospitales) {
		this.montoHospitales = montoHospitales;
	}
	public void setMontoReintegros(BigDecimal montoReintegros) {
		this.montoReintegros = montoReintegros;
	}
	public void setMontoPrestadores(BigDecimal montoPrestadores) {
		this.montoPrestadores = montoPrestadores;
	}
	public boolean isExisteDebito() {
		return existeDebito;
	}
	public void setExisteDebito(boolean existeDebito) {
		this.existeDebito = existeDebito;
	}
	
	public BigDecimal getMontoLiquidacionPendiente() {
		return montoLiquidacionPendiente;
	}
	public void setMontoLiquidacionPendiente(BigDecimal montoLiquidacionPendiente) {
		this.montoLiquidacionPendiente = montoLiquidacionPendiente;
	}
	public BigDecimal getMontoLiquidacionPendienteDebito() {
		return montoLiquidacionPendienteDebito;
	}
	public void setMontoLiquidacionPendienteDebito(BigDecimal montoLiquidacionPendienteDebito) {
		this.montoLiquidacionPendienteDebito = montoLiquidacionPendienteDebito;
	}
	
	
	
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public Date getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	public String getAltaUsr() {
		return altaUsr;
	}
	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}
	public String getIdTercerizadora() {
		return idTercerizadora;
	}
	public void setIdTercerizadora(String idTercerizadora) {
		this.idTercerizadora = idTercerizadora;
	}
	


	public static DebitosaTotal  getMapping(ResultSet rs, String sub) throws SQLException {
		DebitosaTotal  archivo = new DebitosaTotal  ();
		archivo.setAltaUsr(rs.getString(sub +"alta_usr"));
		archivo.setAltaFecha(rs.getDate(sub + "alta_fecha"));
		archivo.setPeriodo(rs.getDate(sub + "periodo")); 
		archivo.setIdTercerizadora(rs.getString(sub + "id_tercerizadora"));
		archivo.setDescTercerizadora(rs.getString(sub + "tercerizadora"));
		archivo.setMontoHospitales(rs.getBigDecimal(sub + "monto_hospital"));
		archivo.setMontoReintegros(rs.getBigDecimal(sub + "monto_reintegro"));
		archivo.setMontoPrestadores(rs.getBigDecimal(sub +  "monto_prestador"));
		archivo.setMontoLiquidacionPendiente(rs.getBigDecimal(sub + "monto_autogestion"));
		archivo.setTotal(rs.getBigDecimal(sub + "total"));
			
		return archivo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescTercerizadora() {
		return descTercerizadora;
	}
	public void setDescTercerizadora(String descTercerizadora) {
		this.descTercerizadora = descTercerizadora;
	}
		

}

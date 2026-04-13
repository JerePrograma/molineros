package ar.com.ospim.autorizaciones.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagosInterbanking {
	
	private String tipoRegistro;
	private String numeroCBU;
	private String CUIT;
	private int    ordenPago;
	private String sucursal;
	private BigDecimal importeTranferencia;
	private String observacion;
	private BigDecimal importeNotaCredito;
	private String tipoComprobante;
	private String nroComprobante;
	private String tipoRetencion;
	private BigDecimal totalRetencion;

	
	public String getNumeroCBU() {
		return numeroCBU;
	}
	public String getCUIT() {
		return CUIT;
	}
	public void setNumeroCBU(String numeroCBU) {
		this.numeroCBU = numeroCBU;
	}
	public void setCUIT(String cUIT) {
		CUIT = cUIT;
	}
	
	public String getSucursal() {
		return sucursal;
	}
	public BigDecimal getImporteTranferencia() {
		return importeTranferencia;
	}
	public String getObservacion() {
		return observacion;
	}
	
	public String getTipoComprobante() {
		return tipoComprobante;
	}
	public String getNroComprobante() {
		return nroComprobante;
	}
	
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public void setImporteTranferencia(BigDecimal importeTranferencia) {
		this.importeTranferencia = importeTranferencia;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	public void setNroComprobante(String nroComprobante) {
		this.nroComprobante = nroComprobante;
	}
	public BigDecimal getImporteNotaCredito() {
		return importeNotaCredito;
	}
	public void setImporteNotaCredito(BigDecimal importeNotaCredito) {
		this.importeNotaCredito = importeNotaCredito;
	}
	
	public int getOrdenPago() {
		return ordenPago;
	}
	public void setOrdenPago(int ordenPago) {
		this.ordenPago = ordenPago;
	}
	
	public static PagosInterbanking getMapping(ResultSet rs) throws SQLException {
		PagosInterbanking pago = new PagosInterbanking();
		pago.setOrdenPago(rs.getInt("orden_pago"));
		pago.setCUIT(rs.getString("cuit"));
		pago.setSucursal(rs.getString("sucursal"));
		pago.setNumeroCBU(rs.getString("cbu"));
		pago.setImporteTranferencia(rs.getBigDecimal("importe"));
		pago.setObservacion(rs.getString("observacion"));
		pago.setImporteNotaCredito(rs.getBigDecimal("importe_nota_credito"));
		pago.setTipoComprobante(rs.getString("tipo_comprabante"));
		pago.setNroComprobante(String.valueOf(rs.getInt("orden_pago")));
		BigDecimal retencion = rs.getBigDecimal("importe_retencion");
		if(retencion != null && !retencion.equals(BigDecimal.ZERO)) {
			pago.setTipoRetencion("02");
			pago.setTotalRetencion(retencion);
		}
		pago.setTipoRegistro(rs.getString("tipo_registro"));
		
		return pago;
	}
	public BigDecimal getTotalRetencion() {
		return totalRetencion;
	}
	public void setTotalRetencion(BigDecimal totalRetencion) {
		this.totalRetencion = totalRetencion;
	}
	public String getTipoRetencion() {
		return tipoRetencion;
	}
	public void setTipoRetencion(String tipoRetencion) {
		this.tipoRetencion = tipoRetencion;
	}
	public String getTipoRegistro() {
		return tipoRegistro;
	}
	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	
	
}

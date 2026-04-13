package ar.com.ospim.afip.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteDeudaEmpresaConsolidado implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5270625027893816754L;
	
	private String cuit;
	private String razonSocial;
	private int ramo;
	private BigDecimal totalCalculado;
	private BigDecimal pagado;
	private BigDecimal pagadoActaConvenio;
	private BigDecimal deuda;
	private String calle; 
	private String numero;
	private String piso;
	private String dpto; 
	private String localidad; 
	private String provincia;  
	private String codigoPostal;
	
	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public int getRamo() {
		return ramo;
	}

	public void setRamo(int ramo) {
		this.ramo = ramo;
	}

	public BigDecimal getTotalCalculado() {
		return totalCalculado;
	}

	public void setTotalCalculado(BigDecimal totalCalculado) {
		this.totalCalculado = totalCalculado;
	}

	public BigDecimal getPagado() {
		return pagado;
	}

	public void setPagado(BigDecimal pagado) {
		this.pagado = pagado;
	}

	public BigDecimal getPagadoActaConvenio() {
		return pagadoActaConvenio;
	}

	public void setPagadoActaConvenio(BigDecimal pagadoActaConvenio) {
		this.pagadoActaConvenio = pagadoActaConvenio;
	}

	public BigDecimal getDeuda() {
		return deuda;
	}

	public void setDeuda(BigDecimal deuda) {
		this.deuda = deuda;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getDpto() {
		return dpto;
	}

	public void setDpto(String dpto) {
		this.dpto = dpto;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public static ReporteDeudaEmpresaConsolidado getMapping(ResultSet rs) throws SQLException {
		
		ReporteDeudaEmpresaConsolidado cons = new ReporteDeudaEmpresaConsolidado();
		
		cons.setCalle(rs.getString("calle"));
		cons.setCodigoPostal(rs.getString("cod_postal"));
		cons.setCuit(rs.getString("cuit"));
		cons.setDeuda(rs.getBigDecimal("deuda"));
		cons.setDpto(rs.getString("dpto"));
		cons.setLocalidad(rs.getString("localidad"));
		cons.setNumero(rs.getString("numero"));
		cons.setPagado(rs.getBigDecimal("pagado"));
		cons.setPagadoActaConvenio(rs.getBigDecimal("pagado_acta_convenio"));
		cons.setPiso(rs.getString("piso"));
		cons.setProvincia(rs.getString("provincia"));
		cons.setRamo(rs.getInt("ramo"));
		cons.setRazonSocial(rs.getString("razon_soc"));
		cons.setTotalCalculado(rs.getBigDecimal("total_calculado"));
		
		return cons;
	}
}

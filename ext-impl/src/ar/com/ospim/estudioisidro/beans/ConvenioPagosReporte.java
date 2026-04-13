package ar.com.ospim.estudioisidro.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ConvenioPagosReporte {
	
	private String tipoConvenio;
	private BigDecimal idConvenio;
	private Date fechaPago;
	private BigDecimal idCuota;
	private BigDecimal importe;
	private BigDecimal interes;
    private String numeroConvenio;
    private String cuit;
    private String sucursal;
    private String razonSoc;
    private BigDecimal idConacto;
    private String contactoEmail;
    
    
	public BigDecimal getIdConvenio() {
		return idConvenio;
	}
	public void setIdConvenio(BigDecimal idConvenio) {
		this.idConvenio = idConvenio;
	}
	public BigDecimal getIdCuota() {
		return idCuota;
	}
	public void setIdCuota(BigDecimal idCuota) {
		this.idCuota = idCuota;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	public BigDecimal getInteres() {
		return interes;
	}
	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}
	public String getNumeroConvenio() {
		return numeroConvenio;
	}
	public void setNumeroConvenio(String numeroConvenio) {
		this.numeroConvenio = numeroConvenio;
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
	public String getRazonSoc() {
		return razonSoc;
	}
	public void setRazonSoc(String razonSoc) {
		this.razonSoc = razonSoc;
	}
	public BigDecimal getIdConacto() {
		return idConacto;
	}
	public void setIdConacto(BigDecimal idConacto) {
		this.idConacto = idConacto;
	}
	public String getContactoEmail() {
		return contactoEmail;
	}
	public void setContactoEmail(String contactoEmail) {
		this.contactoEmail = contactoEmail;
	}
	public Date getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	} 
    
    
	public String getTipoConvenio() {
		return tipoConvenio;
	}
	public void setTipoConvenio(String tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}
	
	public String getFechaPagoAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");		
		return fechaPago!=null?sdf.format(fechaPago):"";
	}
    

	public static ConvenioPagosReporte getMapping(ResultSet rs)
			throws SQLException {
		ConvenioPagosReporte cp = new ConvenioPagosReporte();
		
		cp.setIdConvenio(rs.getBigDecimal("convenio_id"));
		cp.setFechaPago(rs.getDate("fecha_pago"));
		cp.setInteres(rs.getBigDecimal("interes"));
		cp.setImporte(rs.getBigDecimal("importe"));
		cp.setNumeroConvenio(rs.getString("numero"));
		cp.setCuit(rs.getString("cuit"));
		cp.setSucursal(rs.getString("sucursal"));
		cp.setRazonSoc(rs.getString("razon_soc"));
		cp.setIdCuota(rs.getBigDecimal("cuota_id"));
		cp.setIdConacto(rs.getBigDecimal("id_contacto_e"));
		cp.setContactoEmail(rs.getString("contacto"));
		
		
		return cp;
	}
}

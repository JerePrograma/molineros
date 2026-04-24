package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Regimen implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5821807278034085950L;
	
	private Integer codigoRegimen;
	private String regimenAnexo; 
	private String regimenDescripcion;
	private Date fechaInicio; 
	private Date fechaFin;
	private BigDecimal porcentajeRetInscripto; 
	private BigDecimal montoNoSujetoRetencion;
	
	public Regimen() {
		super();
	}
	
	public Regimen(Integer codReg) {
		super();
		this.codigoRegimen = codReg;
	}
	
	public Integer getCodigoRegimen() {
		return codigoRegimen;
	}
	public void setCodigoRegimen(Integer codigoRegimen) {
		this.codigoRegimen = codigoRegimen;
	}
	public String getRegimenAnexo() {
		return regimenAnexo;
	}
	public void setRegimenAnexo(String regimenAnexo) {
		this.regimenAnexo = regimenAnexo;
	}
	public String getRegimenDescripcion() {
		return regimenDescripcion;
	}
	public void setRegimenDescripcion(String regimenDescripcion) {
		this.regimenDescripcion = regimenDescripcion;
	}
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public BigDecimal getPorcentajeRetInscripto() {
		return porcentajeRetInscripto;
	}
	public void setPorcentajeRetInscripto(BigDecimal porcentajeRetInscripto) {
		this.porcentajeRetInscripto = porcentajeRetInscripto;
	}
	public BigDecimal getMontoNoSujetoRetencion() {
		return montoNoSujetoRetencion;
	}
	public void setMontoNoSujetoRetencion(BigDecimal montoNoSujetoRetencion) {
		this.montoNoSujetoRetencion = montoNoSujetoRetencion;
	}
	
	public static Regimen getMapping(ResultSet rs, String prefix ) throws SQLException {
		
		Regimen reg = new Regimen();
		
		reg.setCodigoRegimen(rs.getInt(prefix + "codigo_regimen"));
		reg.setFechaFin(rs.getDate(prefix + "fecha_fin"));
		reg.setFechaInicio(rs.getDate(prefix + "fecha_inicio"));
		reg.setMontoNoSujetoRetencion(rs.getBigDecimal(prefix + "monto_no_sujeto_retencion"));
		reg.setPorcentajeRetInscripto(rs.getBigDecimal(prefix + "porcentaje_ret_inscripto"));
		reg.setRegimenAnexo(rs.getString(prefix + "regimen_anexo"));
		reg.setRegimenDescripcion(rs.getString(prefix + "regimen_descripcion"));
		
		return reg;
		
	}
	
}

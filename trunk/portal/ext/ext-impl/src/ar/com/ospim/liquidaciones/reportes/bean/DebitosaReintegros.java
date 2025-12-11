package ar.com.ospim.liquidaciones.reportes.bean;

import java.math.BigDecimal;
import java.util.Date;

import ar.com.ospim.util.StringUtils;

public class DebitosaReintegros {
	
	
	private String documento;
	private String seccional;
	private String descripcion;
	private BigDecimal importeTotal;
	private String numeroOP;
	private Date fechaOP;
	private BigDecimal cargoPrestadora;
	private String apellido;
	private String nombre;
	private int numReintegro;
	private Integer reclamoPrestacional;

	
	public String getDocumento() {
		return documento;
	}
	public String getSeccional() {
		return seccional;
	}
	public String getDescripcion() {
		return descripcion;
	}
	
	public String getNumeroOP() {
		return numeroOP;
	}
	public Date getFechaOP() {
		return fechaOP;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public void setNumeroOP(String numeroOP) {
		this.numeroOP = numeroOP;
	}
	public void setFechaOP(Date fechaOP) {
		this.fechaOP = fechaOP;
	}
	public BigDecimal getImporteTotal() {
		return importeTotal;
	}
	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public BigDecimal getCargoPrestadora() {
		return cargoPrestadora;
	}
	public void setCargoPrestadora(BigDecimal cargoPrestadora) {
		this.cargoPrestadora = cargoPrestadora;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getNumReintegro() {
		return numReintegro;
	}
	
	public String getNumReintegroToString() {
		return StringUtils.getValueOrEmpty(numReintegro);	
	}
	
	public void setNumReintegro(int numReintegro) {
		this.numReintegro = numReintegro;
	}
	
	public Integer getReclamoPrestacional() {
		return reclamoPrestacional;
	}
	
	public void setReclamoPrestacional(Integer reclamoPrestacional) {
		this.reclamoPrestacional = reclamoPrestacional;
	}
	
	
	

}

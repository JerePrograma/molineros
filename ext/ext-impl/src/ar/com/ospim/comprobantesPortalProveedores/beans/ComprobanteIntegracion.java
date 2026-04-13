package ar.com.ospim.comprobantesPortalProveedores.beans;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;

public class ComprobanteIntegracion extends Comprobante {
	private String cud;
	private Date cudVto;
	private Double importeSolicitado;
	private String dependencia;
	private Integer provincia;
	private Date carpeta;
	private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM");
	private boolean conProblema;
	private String error;
	private Integer loteSSS;
	private Integer cabeceraId;
	private Integer carpetaInt;
	private Integer orden;
	
	public String getCud() {
		return cud;
	}
	public void setCud(String cud) {
		this.cud = cud;
	}
	public Date getCudVto() {
		return cudVto;
	}
	public void setCudVto(Date cudVto) {
		this.cudVto = cudVto;
	}
	public Double getImporteSolicitado() {
		return importeSolicitado;
	}
	public void setImporteSolicitado(Double importeSolicitado) {
		this.importeSolicitado = importeSolicitado;
	}
	public String getDependencia() {
		return dependencia;
	}
	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}
	public Integer getProvincia() {
		return provincia;
	}
	public void setProvincia(Integer provincia) {
		this.provincia = provincia;
	}
	public Date getCarpeta() {
		return carpeta;
	}
	public void setCarpeta(Date carpeta) {
		this.carpeta = carpeta;
	}
	
	public String getCarpetaStr() {
		return this.carpeta!=null?sdf.format(this.carpeta):"";
	}
	public boolean isConProblema() {
		return conProblema;
	}
	public void setConProblema(boolean conProblema) {
		this.conProblema = conProblema;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Integer getLoteSSS() {
		return loteSSS;
	}
	public void setLoteSSS(Integer loteSSS) {
		this.loteSSS = loteSSS;
	}
	public Integer getCabeceraId() {
		return cabeceraId;
	}
	public void setCabeceraId(Integer cabeceraId) {
		this.cabeceraId = cabeceraId;
	}
	public Integer getCarpetaInt() {
		return carpetaInt;
	}
	public void setCarpetaInt(Integer carpetaInt) {
		this.carpetaInt = carpetaInt;
	}
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	
	
  }

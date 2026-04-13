package ar.com.ospim.comprobantesPortalProveedores.beans;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;

public class ComprobanteHospital extends Comprobante {
	private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM");
	private boolean conProblema;
	private String error;
	private Integer orden;
	private Double cantidadAreaMedica;
	private Double importeAreaMedica;
	private Double totalAreaMedica;
	private Double cargoOspim;
	private Double cargoTercerizadora;
	private Double cargoTercerizadoraMonotributistas;
	private Double reconocidoSSS;
	private Integer recuperable;
	private String observacionesPrestacion;
	private String reclamoEstado;
	private Integer reclamoPrestacionalPrestacionId;
	private Boolean generado;
	private List<ReclamoPrestacional> reclamos;
	
	
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
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	public Double getCantidadAreaMedica() {
		return cantidadAreaMedica;
	}
	public void setCantidadAreaMedica(Double cantidadAreaMedica) {
		this.cantidadAreaMedica = cantidadAreaMedica;
	}
	public Double getImporteAreaMedica() {
		return importeAreaMedica;
	}
	public void setImporteAreaMedica(Double importeAreaMedica) {
		this.importeAreaMedica = importeAreaMedica;
	}
	public Double getCargoOspim() {
		return cargoOspim;
	}
	public void setCargoOspim(Double cargoOspim) {
		this.cargoOspim = cargoOspim;
	}
	public Double getCargoTercerizadora() {
		return cargoTercerizadora;
	}
	public void setCargoTercerizadora(Double cargoTercerizadora) {
		this.cargoTercerizadora = cargoTercerizadora;
	}
	public Double getCargoTercerizadoraMonotributistas() {
		return cargoTercerizadoraMonotributistas;
	}
	public void setCargoTercerizadoraMonotributistas(Double cargoTercerizadoraMonotributistas) {
		this.cargoTercerizadoraMonotributistas = cargoTercerizadoraMonotributistas;
	}
	public Double getReconocidoSSS() {
		return reconocidoSSS;
	}
	public void setReconocidoSSS(Double reconocidoSSS) {
		this.reconocidoSSS = reconocidoSSS;
	}
	public String getObservacionesPrestacion() {
		return observacionesPrestacion;
	}
	public void setObservacionesPrestacion(String observaciones) {
		this.observacionesPrestacion = observaciones;
	}
	public Double getTotalAreaMedica() {
	    Double t=0D;	
		try {
		   t =importeAreaMedica*cantidadAreaMedica;
		}catch(Exception e) {}
		return t;
	}
	public void setTotalAreaMedica(Double totalAreaMedica) {
		this.totalAreaMedica = totalAreaMedica;
	}
	public Integer getRecuperable() {
		return recuperable;
	}
	public void setRecuperable(Integer recuperable) {
		this.recuperable = recuperable;
	}
	public String getReclamoEstado() {
		return reclamoEstado;
	}
	public void setReclamoEstado(String reclamoEstado) {
		this.reclamoEstado = reclamoEstado;
	}
	public Integer getReclamoPrestacionalPrestacionId() {
		return reclamoPrestacionalPrestacionId;
	}
	public void setReclamoPrestacionalPrestacionId(Integer reclamoPrestacionalPrestacionId) {
		this.reclamoPrestacionalPrestacionId = reclamoPrestacionalPrestacionId;
	}
	public Boolean getGenerado() {
		return generado;
	}
	public void setGenerado(Boolean generado) {
		this.generado = generado;
	}
	public List<ReclamoPrestacional> getReclamos() {
		return reclamos;
	}
	public void setReclamos(List<ReclamoPrestacional> reclamos) {
		this.reclamos = reclamos;
	}
	public ComprobanteHospital(List<ReclamoPrestacional> reclamos) {
		super();
		this.reclamos = reclamos;
	}
	public ComprobanteHospital() {
		super();
		// TODO Auto-generated constructor stub
	}
		
  }

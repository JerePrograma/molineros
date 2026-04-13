package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.Date;

public class PreAutorizacionPrestacion implements Serializable{


	private static final long serialVersionUID = 3039253625910202736L;
	private Integer id;
	private Nomenclador nomenclador;
	private Date fechaBaja;
	private Double cantidad;
	private Double importe;
	private Integer idAux;
	private OpcionesPrestacion opcionApoyo;
	
	public PreAutorizacionPrestacion() {
		super();
		nomenclador= new Nomenclador();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Nomenclador getNomenclador() {
		return nomenclador;
	}
	public void setNomenclador(Nomenclador nomenclador) {
		this.nomenclador = nomenclador;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getImporte() {
		return importe;
	}
	public void setImporte(Double importe) {
		this.importe = importe;
	}
	public Integer getIdAux() {
		return idAux;
	}
	public void setIdAux(Integer idAux) {
		this.idAux = idAux;
	}
	public OpcionesPrestacion getOpcionApoyo() {
		return opcionApoyo;
	}
	public void setOpcionApoyo(OpcionesPrestacion opcionApoyo) {
		this.opcionApoyo = opcionApoyo;
	}
	
}

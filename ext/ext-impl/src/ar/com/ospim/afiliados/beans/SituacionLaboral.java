package ar.com.ospim.afiliados.beans;

import java.util.Date;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.DateUtils;

/**
 * @author Federico Brachi
 * @version 1.0
 * @created 23-Jul-2010 02:01:47 p.m.
 */
public class SituacionLaboral {
	private int id;
	private Date fecha_ingre;
	private Date viejaFechaIngreso;
	private Date fecha_baja;
	private int id_puesto;
	private int id_revista;
	private String revista;
	private String categoria;
	private int id_categoria;
	private String cuil_conyuje; // ESTE NO FUI YO!!! CON J HDP!!!!! --fbrachi
	private Afiliado afiliado;
	private Empresa empresa;
	private MotivoBaja motivoBaja;
	private String escala_salarial;
	private String estado;
	private boolean baja_cascada;
	private boolean activo;
	private Date fecha_baja_logica;

	public SituacionLaboral() {}
	
	public SituacionLaboral(Afiliado afi, Empresa emp, Date fecha_ingreso, Date fecha_baja){
		this.afiliado=afi;
		this.empresa=emp;		
		this.fecha_ingre=fecha_ingreso;
		this.fecha_baja=fecha_baja;
	}
	
	public SituacionLaboral(Afiliado afi, Empresa emp, Date fecha_ingreso, Date fecha_baja, String revista, String categoria, int id_categoria, int id_revista, MotivoBaja motivoBaja, String escala_salarial ){
		this.afiliado=afi;
		this.empresa=emp;		
		this.fecha_ingre=fecha_ingreso;
		this.fecha_baja=fecha_baja;
		this.categoria=categoria;
		this.revista=revista;
		this.id_categoria=id_categoria;
		this.id_revista=id_revista;
		this.motivoBaja=motivoBaja;
		this.escala_salarial=escala_salarial;
		this.activo = false;
	}
	
	public SituacionLaboral(Empresa emp, Date fecha_ingreso, Date fecha_baja){
		this.empresa=emp;
		this.fecha_ingre=fecha_ingreso;
		this.fecha_baja=fecha_baja;
	}
	
	public String getRevista() {
		return revista;
	}

	public void setRevista(String revista) {
		this.revista = revista;
	}

	public int getId_categoria() {
		return id_categoria;
	}

	public void setId_categoria(int idCategoria) {
		id_categoria = idCategoria;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Date getFecha_ingre() {
		return fecha_ingre;
	}
	public String getFecha_ingreAsString() {
		return fecha_ingre!=null?DateUtils.format(fecha_ingre,"dd/MM/yyyy"):"";
	}

	public void setFecha_ingre(Date fechaIngre) {
		fecha_ingre = fechaIngre;
	}

	public Date getFecha_baja() {
		return fecha_baja;
	}
	public String getFecha_bajaAsString() {
		return fecha_baja!=null?DateUtils.format(fecha_baja,"dd/MM/yyyy"):"";
	}

	public void setFecha_baja(Date fechaBaja) {
		fecha_baja = fechaBaja;
	}

	public int getId_puesto() {
		return id_puesto;
	}

	public void setId_puesto(int idPuesto) {
		id_puesto = idPuesto;
	}

	public int getId_revista() {
		return id_revista;
	}

	public void setId_revista(int idRevista) {
		id_revista = idRevista;
	}

	public String getCuil_conyuje() {
		return cuil_conyuje;
	}

	public void setCuil_conyuje(String cuilConyuje) {
		cuil_conyuje = cuilConyuje;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public MotivoBaja getMotivoBaja() {
		return motivoBaja;
	}

	public void setMotivoBaja(MotivoBaja motivoBaja) {
		this.motivoBaja = motivoBaja;
	}

	public String getEscala_salarial() {
		return escala_salarial;
	}

	public void setEscala_salarial(String escalaSalarial) {
		escala_salarial = escalaSalarial;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public boolean isBaja_cascada() {
		return baja_cascada;
	}

	public void setBaja_cascada(boolean bajaCascada) {
		baja_cascada = bajaCascada;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFecha_baja_logica() {
		return fecha_baja_logica;
	}

	public void setFecha_baja_logica(Date fechaBajaLogica) {
		fecha_baja_logica = fechaBajaLogica;
	}

	/**
	 * @return the viejaFechaIngreso
	 */
	public Date getViejaFechaIngreso() {
		return viejaFechaIngreso;
	}

	/**
	 * @param viejaFechaIngreso the viejaFechaIngreso to set
	 */
	public void setViejaFechaIngreso(Date viejaFechaIngreso) {
		this.viejaFechaIngreso = viejaFechaIngreso;
	}	
	
	public String getViejaFechaIngresoAsString() {
		return viejaFechaIngreso!=null?DateUtils.format(viejaFechaIngreso,"dd/MM/yyyy"):"";
	}
}
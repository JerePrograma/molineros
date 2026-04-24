package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.util.Date;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.DateUtils;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:25:51 p.m.
 */
public abstract class ReintegroPrestacion {
	private Date fecha_prestacion;
	private BigDecimal importe;
	private BigDecimal importeTotal;
	private String tercerizado;
	private Reintegro reintegro;
	private String cuit;
	private String descripcion;
	private PlanPrestacion plan_prestacion;
	private Date alta_fecha;
	private String alta_usr;	
	private Date modi_fecha;
	private String modi_usr;	
	private String codigo;
	private String compro_a_debitar_tipo;
	private String comproaDebitarLetra;
	private String compro_a_debitar_sucursal;
	private String compro_a_debitar_numero;
	private String cuit_entidad;
	private String sucursal_entidad;
	private String razon_social_entidad;
	private String descripcion_entidad;
	private String comprobanteString;
	private Date fecha_comprobante;
	private BigDecimal importe_comprobante;
	private BigDecimal honorarios;
	private BigDecimal importeOspim;
	private BigDecimal importePrestadora;
	private BigDecimal importeImesa;
	
	
	
	public BigDecimal getImporteOspim() {
		return importeOspim;
	}



	public void setImporteOspim(BigDecimal importeOspim) {
		this.importeOspim = importeOspim;
	}


	private int id_reclamo_prestacional ;
	private int id_prestacion_reclamo;
		
	
	public ReintegroPrestacion() {

	}

	public ReintegroPrestacion(Reintegro reintegro, int id_prestacion, String codigo,
			String cuit, String descripcion, BigDecimal importeTotal) {
		this.reintegro = reintegro;
		setId_prestacion(id_prestacion, codigo);		
		this.importeTotal = importeTotal;
		this.cuit = cuit;
		this.descripcion = descripcion;
	}
	
	public ReintegroPrestacion(Reintegro reintegro, int id_prestacion,
			String cuit, String descripcion, BigDecimal importeTotal) {
		this.reintegro = reintegro;
		setId_prestacion(id_prestacion);		
		this.importeTotal = importeTotal;
		this.cuit = cuit;
		this.descripcion = descripcion;
	}
	
	/**
	 * @return the id_reintegro
	 */
	public int getId_reintegro() {
		if (reintegro == null) {
			return 0;
		}
		return reintegro.getId_reintegro();
	}

	/**
	 * @return the id_reintegro
	 */
	public String getId_reintegroString() {
		return String.valueOf(getId_reintegro());
	}

	/**
	 * @param idReintegro
	 *            the id_reintegro to set
	 */
	public void setId_reintegro(int idReintegro) {
		if (reintegro == null) {
			reintegro = new Reintegro();
		}
		reintegro.setId_reintegro(idReintegro);
	}

	/**
	 * @return the cuit
	 */
	public String  getCuit() {
		return cuit != null ? cuit : "";
	}

	/**
	 * @return the string value of field descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param cuit
	 */
	public void setCuit(String cuit) {
			this.cuit = cuit; 
	}

	/**
	 * @return the id_prestacion
	 */
	public int getId_prestacion() {
		if (plan_prestacion == null) {
			return 0;
		}
		return plan_prestacion.getId_prestacion();
	}

	/**
	 * @return the string value of id_prestacion
	 */
	public String getId_prestacionString() {
		return String.valueOf(getId_prestacion());
	}

	/**
	 * @param idPrestacion
	 *            the id_prestacion to set
	 */
	public void setId_prestacion(int idPrestacion) {
		if (plan_prestacion == null) {
			plan_prestacion = new PlanPrestacion();
		}
		plan_prestacion.setId_prestacion(idPrestacion);		
	}
	
	/**
	 * @param idPrestacion
	 * @param codigo
	 *            the id_prestacion to set, the codigo to set
	 */
	public void setId_prestacion(int idPrestacion, String codigo) {
		if (plan_prestacion == null) {
			plan_prestacion = new PlanPrestacion();
		}
		plan_prestacion.setId_prestacion(idPrestacion, codigo);		
	}

	/**
	 * @return the id_plan
	 */
	public int getId_plan() {
		if (plan_prestacion == null) {
			return 0;
		}
		return plan_prestacion.getId_plan();
	}

	/**
	 * @return the string value of field id_plan
	 */
	public String getId_planString() {
		return String.valueOf(getId_plan());
	}

	/**
	 * @param idPlan
	 *            the id_plan to set
	 */
	public void setId_plan(int idPlan) {
		if (plan_prestacion == null) {
			plan_prestacion = new PlanPrestacion();
		}
		plan_prestacion.setId_plan(idPlan);
	}

	/**
	 * @return the fecha_prestacion
	 */
	public Date getFecha_prestacion() {
		return fecha_prestacion;
	}

	/**
	 * @return the fecha_prestacion
	 */
	public String getFecha_prestacionAsString() {
		return null!=fecha_prestacion?DateUtils.format(fecha_prestacion,DateUtils.SHORT):"";
	}

	/**
	 * @param fechaPrestacion
	 *            the fecha_prestacion to set
	 */
	public void setFecha_prestacion(Date fechaPrestacion) {
		fecha_prestacion = fechaPrestacion;
	}

	/**
	 * @return the importe
	 */
	public BigDecimal getImporte() {
		return importe;
	}

	/**
	 * @param importe
	 *            the importe to set
	 */
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	/**
	 * @return the tercerizado
	 */
	public String getTercerizado() {
		return tercerizado;
	}

	/**
	 * @return the tercerizado
	 */
	public String getTercerizadoString() {
		if (tercerizado.equals("0")) {
			return WebKeysGlobal.STRING_OSPIM;
		}
		else if (tercerizado.equals("1")) {
			return WebKeysGlobal.STRING_OMINT;
		}
		return "";
	}

	
	/**
	 * @param tercerizado
	 *            the tercerizado to set
	 */
	public void setTercerizado(String tercerizado) {
		this.tercerizado = tercerizado;
	}

	/**
	 * @return the reintegro
	 */
	public Reintegro getReintegro() {
		return reintegro;
	}

	/**
	 * @param reintegro
	 *            the reintegro to set
	 */
	public void setReintegro(Reintegro reintegro) {
		this.reintegro = reintegro;
	}

	/**
	 * @param decripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the plan_prestacion
	 */
	public PlanPrestacion getPlan_prestacion() {
		return plan_prestacion;
	}

	/**
	 * @param planPrestacion
	 *            the plan_prestacion to set
	 */
	public void setPlan_prestacion(PlanPrestacion planPrestacion) {
		plan_prestacion = planPrestacion;
	}

	/**
	 * @return the importeTotal
	 */
	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	/**
	 * @param importeTotal
	 *            the importeTotal to set
	 */
	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}
	
	public String getAltaFechaAsString() {
		return null!=alta_fecha?DateUtils.format(alta_fecha,DateUtils.LONG_MILI_SEC):"";
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	/**
	 * @return the compro_a_debitar_tipo
	 */
	public String getCompro_a_debitar_tipo() {
		return compro_a_debitar_tipo;
	}

	/**
	 * @param comproADebitarTipo
	 *            the compro_a_debitar_tipo to set
	 */
	public void setCompro_a_debitar_tipo(String comproADebitarTipo) {
		compro_a_debitar_tipo = comproADebitarTipo;
	}

	/**
	 * @return the compro_a_debitar_numero
	 */
	public String getCompro_a_debitar_numero() {
		return compro_a_debitar_numero;
	}

	/**
	 * @param comproADebitarNumero
	 *            the compro_a_debitar_numero to set
	 */
	public void setCompro_a_debitar_numero(String comproADebitarNumero) {
		compro_a_debitar_numero = comproADebitarNumero;
	}

	public String getCuit_entidad() {
		return cuit_entidad;
	}

	public void setCuit_entidad(String cuitEntidad) {
		cuit_entidad = cuitEntidad;
	}

	public String getSucursal_entidad() {
		return sucursal_entidad;
	}

	public void setSucursal_entidad(String sucuEntidad) {
		sucursal_entidad = sucuEntidad;
	}

	public String getDescripcion_entidad() {
		return descripcion_entidad;
	}

	public void setDescripcion_entidad(String descripcionEntidad) {
		descripcion_entidad = descripcionEntidad;
	}

	public String getRazon_social_entidad() {
		return razon_social_entidad;
	}

	public void setRazon_social_entidad(String razonSocialEntidad) {
		razon_social_entidad = razonSocialEntidad;
	}

	public String getComprobanteString() {
		return comprobanteString;
	}

	public void setComprobanteString(String comprobanteString) {
		this.comprobanteString = comprobanteString;
	}

	public Date getFecha_comprobante() {
		return fecha_comprobante;
	}

	public void setFecha_comprobante(Date fechaComprobante) {
		fecha_comprobante = fechaComprobante;
	}

	public BigDecimal getImporte_comprobante() {
		return importe_comprobante;
	}

	public void setImporte_comprobante(BigDecimal importeComprobante) {
		importe_comprobante = importeComprobante;
	}

	/**
	 * @return the fecha_comprobante
	 */
	public String getFecha_comprobanteAsString() {
		return null!=fecha_comprobante?DateUtils.format(fecha_comprobante,DateUtils.SHORT):"";
	}

	public BigDecimal getHonorarios() {
		return honorarios;
	}

	public void setHonorarios(BigDecimal honorarios) {
		this.honorarios = honorarios;
	}

	
	public int getId_reclamo_prestacional() {
		return id_reclamo_prestacional;
	}

	public void setId_reclamo_prestacional(int id_reclamo_prestacional) {
		this.id_reclamo_prestacional = id_reclamo_prestacional;
	}

	public int getId_prestacion_reclamo() {
		return id_prestacion_reclamo;
	}

	public void setId_prestacion_reclamo(int id_prestacion_reclamo) {
		this.id_prestacion_reclamo = id_prestacion_reclamo;
	}

	public BigDecimal getImportePrestadora() {
		return importePrestadora;
	}

	public void setImportePrestadora(BigDecimal importePrestadora) {
		this.importePrestadora = importePrestadora;
	}

	public String getCompro_a_debitar_sucursal() {
		return compro_a_debitar_sucursal;
	}

	public void setCompro_a_debitar_sucursal(String compro_a_debitar_sucursal) {
		this.compro_a_debitar_sucursal = compro_a_debitar_sucursal;
	}

	public String getComproaDebitarLetra() {
		return comproaDebitarLetra;
	}

	public void setComproaDebitarLetra(String comproaDebitarLetra) {
		this.comproaDebitarLetra = comproaDebitarLetra;
	}



	public BigDecimal getImporteImesa() {
		return importeImesa;
	}



	public void setImporteImesa(BigDecimal importeImesa) {
		this.importeImesa = importeImesa;
	}

	
}

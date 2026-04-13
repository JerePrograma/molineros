package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import oasis.names.tc.wsrp.v1.types.GetServiceDescription;

import ar.com.ospim.global.beans.Prestacion;

/**
 * @version 1.0
 * @created 19-Oct-2012 02:25:41 p.m.
 */
public class ConvenioPrestacionalDetalle implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2871379242655844634L;
	
	private int id; //id_convenio_prest_detalle
	private int idConvenioPrestacional;
	private Date fechaDesde;
	private Date fechaHasta;
//	private int id_prestacion_desde;
	private TipoNomenclador tipoNomenclador;
	private Prestacion prestacionDesde;
	private String codigoDesde;
//	private String descripcion_prestacion_desde;
//	private int id_prestacion_hasta;
	private Prestacion prestacionHasta;
	private String codigoHasta;
//	private String descripcion_prestacion_hasta;
	private int idPlan; // --todos los id_plan mas la opción 'todos' que es 0
	private String planDescripcion;
//	private int idCartilla;
	private BigDecimal coseguro;
	private String tipoValorizacion;
//	private BigDecimal honorarios;
//	private BigDecimal gastos;
//	private BigDecimal importeTotal;
	private BigDecimal importe;
	private BigDecimal porcentaje;
	private String servicio;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;

	private ESTADOS estado;
    
    public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
//	public ConvenioPrestacionalDetalle(int idContratoDetalle, int idContrato, Date fechaDesde, Date fechaHasta,
//			int idPrestacionDesde, String codigoDesde, int idPrestacionHasta,
//			String codigoHasta, int idPlan, int idCartilla,
//			BigDecimal coseguro, BigDecimal honorarios, BigDecimal gastos,
//			BigDecimal importeTotal, Date altaFecha, String altaUsr,
//			Date modiFecha, String modiUsr, Date bajaFecha, String bajaUsr, String plan_descripcion, String servicio, String tipo_valoriza) {
//		super();
//		id_contrato_detalle = idContratoDetalle;
//		id_contrato = idContrato;
//		fecha_desde = fechaDesde;
//		fecha_hasta = fechaHasta;
//		id_prestacion_desde = idPrestacionDesde;
//		codigo_desde = codigoDesde;
//		id_prestacion_hasta = idPrestacionHasta;
//		codigo_hasta = codigoHasta;
//		id_plan = idPlan;
//		id_cartilla = idCartilla;
//		this.coseguro = coseguro;
//		this.honorarios = honorarios;
//		this.gastos = gastos;
//		importe_total = importeTotal;
//		alta_fecha = altaFecha;
//		alta_usr = altaUsr;
//		modi_fecha = modiFecha;
//		modi_usr = modiUsr;
//		baja_fecha = bajaFecha;
//		baja_usr = bajaUsr;
//		this.plan_descripcion = plan_descripcion;
//		this.servicio = servicio;
//		tipo_valorizacion = tipo_valoriza;
//	}	
	
	public ConvenioPrestacionalDetalle() {	
		super();
	}

	
	
	public ConvenioPrestacionalDetalle(int id, int idConvenioPrestacional,
		Date fechaDesde, Date fechaHasta, TipoNomenclador tipoNomenclador,
		Prestacion prestacionDesde, String codigoDesde,
		Prestacion prestacionHasta, String codigoHasta, int idPlan,
		String planDescripcion, BigDecimal coseguro, String tipoValorizacion,
		BigDecimal importe, BigDecimal porcentaje, String servicio) {
		
	super();
	this.id = id;
	this.idConvenioPrestacional = idConvenioPrestacional;
	this.fechaDesde = fechaDesde;
	this.fechaHasta = fechaHasta;
	this.tipoNomenclador = tipoNomenclador;
	this.prestacionDesde = prestacionDesde;
	this.codigoDesde = codigoDesde;
	this.prestacionHasta = prestacionHasta;
	this.codigoHasta = codigoHasta;
	this.idPlan = idPlan;
	this.planDescripcion = planDescripcion;
	this.coseguro = coseguro;
	this.tipoValorizacion = tipoValorizacion;
	this.importe = importe;
	this.porcentaje = porcentaje;
	this.servicio = servicio;
}



	public TipoNomenclador getTipoNomenclador() {
		return tipoNomenclador;
	}

	public void setTipoNomenclador(TipoNomenclador tipoNomenclador) {
		this.tipoNomenclador = tipoNomenclador;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	public ConvenioPrestacionalDetalle(String line) throws ParseException {
//		TODO para exportar archivo...
	}
	
//	public int getId_contrato_detalle() {
//		return id_contrato_detalle;
//	}
//
//	public void setId_contrato_detalle(int idContratoDetalle) {
//		id_contrato_detalle = idContratoDetalle;
//	}
//
//	public int getId_contrato() {
//		return id_contrato;
//	}
//
//	public void setId_contrato(int idContrato) {
//		id_contrato = idContrato;
//	}
//
//	public Date getFecha_desde() {
//		return fecha_desde;
//	}
//
//	public String getFecha_Desde_AsString() {
//		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
//		return fecha_desde!=null?sdf.format(fecha_desde):"";
//	}
//	
//	public void setFecha_desde(Date fechaDesde) {
//		fecha_desde = fechaDesde;
//	}
//
//	public Date getFecha_hasta() {
//		return fecha_hasta;
//	}
//
//	public String getFecha_Hasta_AsString() {
//		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
//		return fecha_hasta!=null?sdf.format(fecha_hasta):"";
//	}
//	
//	public void setFecha_hasta(Date fechaHasta) {
//		fecha_hasta = fechaHasta;
//	}
//
//	public int getId_prestacion_desde() {
//		return id_prestacion_desde;
//	}
//
//	public void setId_prestacion_desde(int idPrestacionDesde) {
//		id_prestacion_desde = idPrestacionDesde;
//	}
//
//	public String getCodigo_desde() {
//		return codigo_desde;
//	}
//
//	public void setCodigo_desde(String codigoDesde) {
//		codigo_desde = codigoDesde;
//	}
//
//	public String getDescripcion_prestacion_desde() {
//		return descripcion_prestacion_desde;
//	}
//
//	public void setDescripcion_prestacion_desde(
//			String descripcionPrestacionDesde) {
//		descripcion_prestacion_desde = descripcionPrestacionDesde;
//	}
//
//	public int getId_prestacion_hasta() {
//		return id_prestacion_hasta;
//	}
//
//	public void setId_prestacion_hasta(int idPrestacionHasta) {
//		id_prestacion_hasta = idPrestacionHasta;
//	}
//
//	public String getCodigo_hasta() {
//		return codigo_hasta;
//	}
//
//	public void setCodigo_hasta(String codigoHasta) {
//		codigo_hasta = codigoHasta;
//	}
//
//	public String getDescripcion_prestacion_hasta() {
//		return descripcion_prestacion_hasta;
//	}
//
//	public void setDescripcion_prestacion_hasta(
//			String descripcionPrestacionHasta) {
//		descripcion_prestacion_hasta = descripcionPrestacionHasta;
//	}
//
//	public int getId_plan() {
//		return id_plan;
//	}
//
//	public void setId_plan(int idPlan) {
//		id_plan = idPlan;
//	}
//
//	public int getId_cartilla() {
//		return id_cartilla;
//	}
//
//	public void setId_cartilla(int idCartilla) {
//		id_cartilla = idCartilla;
//	}
//
//	public BigDecimal getCoseguro() {
//		return coseguro;
//	}
//
//	public void setCoseguro(BigDecimal coseguro) {
//		this.coseguro = coseguro;
//	}
//
//	public BigDecimal getHonorarios() {
//		return honorarios;
//	}
//
//	public void setHonorarios(BigDecimal honorarios) {
//		this.honorarios = honorarios;
//	}
//
//	public BigDecimal getGastos() {
//		return gastos;
//	}
//
//	public void setGastos(BigDecimal gastos) {
//		this.gastos = gastos;
//	}
//
//	public BigDecimal getImporte_total() {
//		return importe_total;
//	}
//
//	public void setImporte_total(BigDecimal importeTotal) {
//		importe_total = importeTotal;
//	}
//
//	public Date getAlta_fecha() {
//		return alta_fecha;
//	}
//
//	public void setAlta_fecha(Date altaFecha) {
//		alta_fecha = altaFecha;
//	}
//
//	public String getAlta_usr() {
//		return alta_usr;
//	}
//
//	public void setAlta_usr(String altaUsr) {
//		alta_usr = altaUsr;
//	}
//
//	public Date getModi_fecha() {
//		return modi_fecha;
//	}
//
//	public void setModi_fecha(Date modiFecha) {
//		modi_fecha = modiFecha;
//	}
//
//	public String getModi_usr() {
//		return modi_usr;
//	}
//
//	public void setModi_usr(String modiUsr) {
//		modi_usr = modiUsr;
//	}
//
//	public Date getBaja_fecha() {
//		return baja_fecha;
//	}
//
//	public void setBaja_fecha(Date bajaFecha) {
//		baja_fecha = bajaFecha;
//	}
//
//	public String getBaja_usr() {
//		return baja_usr;
//	}
//
//	public void setBaja_usr(String bajaUsr) {
//		baja_usr = bajaUsr;
//	}
//		
//	public String getTipo_valorizacion() {
//		return tipo_valorizacion;
//	}
//
//
//	public void setTipo_valorizacion(String tipoValorizacion) {
//		tipo_valorizacion = tipoValorizacion;
//	}

	
//	public String getPlan_descripcion() {
//		return getId_plan() == 0 ? "TODOS" : plan_descripcion;
//	}
//
//
//	public void setPlan_descripcion(String planDescripcion) {		
//		plan_descripcion = planDescripcion;		
//	}
		
//	public String getServicio() {
//		return servicio;
//	}
//
//	public void setServicio(String servicio) {
//		this.servicio = servicio;
//	}
//	
//	public String getMarcaEdit() {
//		return marcaEdit;
//	}
//
//	public void setMarcaEdit(String marcaEdit) {
//		this.marcaEdit = marcaEdit;
//	}
//	
//	public static ConvenioPrestacionalDetalle getMapping(ResultSet rs) throws SQLException {
//		return getMapping(rs, "");		
//	}
	
	public static ConvenioPrestacionalDetalle getMapping(ResultSet rs, String prefix) throws SQLException {
		  				
		TipoNomenclador tipoNomenclador =TipoNomenclador.getMapping("nomenclador_", rs);
		
		Prestacion prestacionDesde = new Prestacion(rs.getInt(prefix+"id_prestacion_desde"), 
				rs.getString(prefix+"prestacion_descripcion_desde") );
		prestacionDesde.setId_tipo_nomenclador(tipoNomenclador.getId_tipo_nomenclador());
		
		Prestacion prestacionHasta = new Prestacion(rs.getInt(prefix+"id_prestacion_hasta"), 
				rs.getString(prefix+"prestacion_descripcion_hasta") );
		prestacionDesde.setId_tipo_nomenclador(tipoNomenclador.getId_tipo_nomenclador());
		
		ConvenioPrestacionalDetalle convenioPrestDet = new ConvenioPrestacionalDetalle();
		convenioPrestDet.setIdConvenioPrestacional(rs.getInt(prefix+"id_convenio_prest"));
		convenioPrestDet.setTipoNomenclador(tipoNomenclador);
		convenioPrestDet.setId(rs.getInt(prefix+"id_convenio_prest_detalle"));
		convenioPrestDet.setFechaDesde(rs.getDate(prefix+"fecha_desde"));
		convenioPrestDet.setFechaHasta(rs.getDate(prefix+"fecha_hasta"));		
		convenioPrestDet.setCodigoDesde(rs.getString(prefix+"codigo_desde"));
		convenioPrestDet.setCodigoHasta(rs.getString(prefix+"codigo_hasta"));
		convenioPrestDet.setPrestacionHasta(new Prestacion(rs.getInt(prefix+"id_prestacion_hasta"),""));
		convenioPrestDet.setIdPlan(rs.getInt(prefix+"id_plan"));
		convenioPrestDet.setPlanDescripcion(rs.getString(prefix+"plan_descripcion"));
//		convenioPrestDet.setId_cartilla(rs.getInt(prefix+"id_cartilla"));
		convenioPrestDet.setCoseguro(rs.getBigDecimal(prefix+"coseguro"));
		convenioPrestDet.setTipoValorizacion(rs.getString(prefix+"tipo_valorizacion"));		
//		convenioPrestDet.setHonorarios(rs.getBigDecimal(prefix+"honorarios"));
//		convenioPrestDet.setGastos(rs.getBigDecimal(prefix+"gastos"));
//		convenioPrestDet.setImporteTotal(rs.getBigDecimal(prefix+"importe_total"));
		convenioPrestDet.setImporte(rs.getBigDecimal(prefix+"importe"));
		convenioPrestDet.setPorcentaje(rs.getBigDecimal(prefix+"porcentaje"));
		convenioPrestDet.setServicio(rs.getString(prefix+"servicio"));
		convenioPrestDet.setAltaFecha(rs.getDate(prefix+"alta_fecha")); 
		convenioPrestDet.setAltaUsr(rs.getString(prefix+"alta_usr")); 
		convenioPrestDet.setModiFecha(rs.getDate(prefix+"modi_fecha")); 
		convenioPrestDet.setModiUsr(rs.getString(prefix+"modi_usr")); 
		convenioPrestDet.setBajaFecha(rs.getDate(prefix+"baja_fecha")); 
		convenioPrestDet.setBajaUsr(rs.getString(prefix+"baja_usr"));
		
		convenioPrestDet.setPrestacionDesde(prestacionDesde);
		convenioPrestDet.setPrestacionHasta(prestacionHasta);
		
		return convenioPrestDet;
	}

//	public ConvenioPrestacionalDetalle(ConvenioPrestacionalDetalle contratoDetalle) {
//		id_contrato_detalle = contratoDetalle.id_contrato_detalle;
//		id_contrato = contratoDetalle.id_contrato;
//		fecha_desde = contratoDetalle.fecha_desde;
//		fecha_hasta = contratoDetalle.fecha_hasta;
//		id_prestacion_desde = contratoDetalle.id_prestacion_desde;
//		codigo_desde = contratoDetalle.codigo_desde;
//		id_prestacion_hasta = contratoDetalle.id_prestacion_desde;
//		codigo_hasta = contratoDetalle.codigo_hasta;
//		id_plan = contratoDetalle.id_plan;
//		id_cartilla = contratoDetalle.id_cartilla;
//		this.coseguro = contratoDetalle.coseguro;
//		this.honorarios = contratoDetalle.honorarios;
//		this.gastos = contratoDetalle.gastos;
//		importe_total = contratoDetalle.importe_total;
//		alta_fecha = contratoDetalle.alta_fecha;
//		alta_usr = contratoDetalle.alta_usr;
//		modi_fecha = contratoDetalle.modi_fecha;
//		modi_usr = contratoDetalle.modi_usr;
//		baja_fecha = contratoDetalle.baja_fecha;
//		baja_usr = contratoDetalle.baja_usr;
//		this.plan_descripcion = contratoDetalle.plan_descripcion;
//		this.servicio = contratoDetalle.servicio;
//		this.tipo_valorizacion = contratoDetalle.tipo_valorizacion;
//	}	
	
	public String getPlanDescripcion() {
		return getIdPlan() == 0 ? "TODOS" : planDescripcion;
	}
	public void setPlanDescripcion(String planDescripcion) {		
		this.planDescripcion = planDescripcion;		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdConvenioPrestacional() {
		return idConvenioPrestacional;
	}

	public void setIdConvenioPrestacional(int idConvenioPrestacional) {
		this.idConvenioPrestacional = idConvenioPrestacional;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Prestacion getPrestacionDesde() {
		return prestacionDesde;
	}

	public void setPrestacionDesde(Prestacion prestacionDesde) {
		this.prestacionDesde = prestacionDesde;
	}

	public String getCodigoDesde() {
		return codigoDesde;
	}

	public void setCodigoDesde(String codigoDesde) {
		this.codigoDesde = codigoDesde;
	}

	public Prestacion getPrestacionHasta() {
		return prestacionHasta;
	}

	public void setPrestacionHasta(Prestacion prestacionHasta) {
		this.prestacionHasta = prestacionHasta;
	}

	public String getCodigoHasta() {
		return codigoHasta;
	}

	public void setCodigoHasta(String codigoHasta) {
		this.codigoHasta = codigoHasta;
	}

	public int getIdPlan() {
		return idPlan;
	}

	public void setIdPlan(int idPlan) {
		this.idPlan = idPlan;
	}

//	public int getIdCartilla() {
//		return idCartilla;
//	}
//
//	public void setIdCartilla(int idCartilla) {
//		this.idCartilla = idCartilla;
//	}

	public BigDecimal getCoseguro() {
		return coseguro;
	}

	public void setCoseguro(BigDecimal coseguro) {
		this.coseguro = coseguro;
	}

	public String getTipoValorizacion() {
		return tipoValorizacion;
	}

	public void setTipoValorizacion(String tipoValorizacion) {
		this.tipoValorizacion = tipoValorizacion;
	}

//	public BigDecimal getHonorarios() {
//		return honorarios;
//	}
//
//	public void setHonorarios(BigDecimal honorarios) {
//		this.honorarios = honorarios;
//	}
//
//	public BigDecimal getGastos() {
//		return gastos;
//	}
//
//	public void setGastos(BigDecimal gastos) {
//		this.gastos = gastos;
//	}
//
//	public BigDecimal getImporteTotal() {
//		return importeTotal;
//	}
//
//	public void setImporteTotal(BigDecimal importeTotal) {
//		this.importeTotal = importeTotal;
//	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModiUsr() {
		return modiUsr;
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "ConvenioPrestacionalDetalle [id=" + id + ", fechaDesde="
				+ fechaDesde + ", codigoDesde=" + codigoDesde
				+ ", codigoHasta=" + codigoHasta + ", idPlan=" + idPlan
				+ ", planDescripcion=" + planDescripcion + ", importe="
				+ importe + ", porcentaje=" + porcentaje + ", servicio="
				+ servicio + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConvenioPrestacionalDetalle other = (ConvenioPrestacionalDetalle) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
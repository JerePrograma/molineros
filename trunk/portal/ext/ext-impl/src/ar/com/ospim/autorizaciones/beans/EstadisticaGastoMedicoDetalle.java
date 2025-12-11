package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Prestacion;

public class EstadisticaGastoMedicoDetalle implements Serializable{

	private static final long serialVersionUID = -4485127537115608219L;
	private String cuit;
	private String sucursal;
	private String razonSocial;
	private Date ordenPagoFecha;
	private Integer ordenPagoId;
	private String origen;
	private Integer id;
	private String tipo;
	private String sector;
	private Double cantidad;
	private Double importe;
	private Double total;
	private Double cargoOspim;
	private Double cargoPrestadora;
	private Integer idTipoGestion;
	private Afiliado afiliado;
	private ReclamoPrestacionalExcel reclamo;
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
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

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Date getOrdenPagoFecha() {
		return ordenPagoFecha;
	}

	public void setOrdenPagoFecha(Date ordenPagoFecha) {
		this.ordenPagoFecha = ordenPagoFecha;
	}

	public Integer getOrdenPagoId() {
		return ordenPagoId;
	}

	public void setOrdenPagoId(Integer ordenPagoId) {
		this.ordenPagoId = ordenPagoId;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
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

	public Double getCargoOspim() {
		return cargoOspim;
	}

	public void setCargoOspim(Double cargoOspim) {
		this.cargoOspim = cargoOspim;
	}

	public Double getCargoPrestadora() {
		return cargoPrestadora;
	}

	public void setCargoPrestadora(Double cargoPrestadora) {
		this.cargoPrestadora = cargoPrestadora;
	}

	public Integer getIdTipoGestion() {
		return idTipoGestion;
	}

	public void setIdTipoGestion(Integer idTipoGestion) {
		this.idTipoGestion = idTipoGestion;
	}

	
	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public ReclamoPrestacionalExcel getReclamo() {
		return reclamo;
	}

	public void setReclamo(ReclamoPrestacionalExcel reclamo) {
		this.reclamo = reclamo;
	}

	public static EstadisticaGastoMedicoDetalle getMapping(ResultSet rs) throws SQLException {
		
		EstadisticaGastoMedicoDetalle gasto = new EstadisticaGastoMedicoDetalle();
		gasto.setCuit(rs.getString("cuit"));
		gasto.setRazonSocial(rs.getString("razon_social"));
		gasto.setSucursal(rs.getString("sucursal"));
		gasto.setOrdenPagoFecha(rs.getDate("fecha_op"));
		gasto.setOrdenPagoId(rs.getInt("id_orden_pago"));
		gasto.setOrigen(rs.getString("origen"));
		gasto.setId(rs.getInt("id"));
		gasto.setTipo(rs.getString("tipo"));
		gasto.setSector(rs.getString("sector"));
		gasto.setCantidad(rs.getDouble("cantidad"));
		gasto.setImporte(rs.getDouble("importe"));
		gasto.setTotal(rs.getDouble("total"));
		gasto.setCargoOspim(rs.getDouble("cargo_ospim"));
		gasto.setCargoPrestadora(rs.getDouble("cargo_prestadora"));
		gasto.setIdTipoGestion(rs.getInt("id_tipo_gestion"));
		Afiliado afiliado = new Afiliado();
		afiliado.setCuil_titular(rs.getString("cuil_titular"));
		afiliado.setInte(rs.getInt("inte"));
		afiliado.setNombre(rs.getString("afiliado_nombre"));
		afiliado.setDiscapacitado(rs.getString("discapacitado"));
		gasto.setAfiliado(afiliado);
		
		ReclamoPrestacionalExcel reclamo = new ReclamoPrestacionalExcel();
		reclamo.setNroReclamo(rs.getInt("reclamo_id"));
		reclamo.setBajaFecha(rs.getDate("reclamo_baja"));
		reclamo.setAmparoTexto(rs.getString("reclamo_amparo"));
		reclamo.setCaso_vinculado(rs.getInt("reclamo_caso_asociado"));
		reclamo.setTextoSeccional(rs.getString("seccional"));
		afiliado.setPlanAfiliado(rs.getString("afiliado_plan_molinero"));
		reclamo.setPlanPrevencion(rs.getString("afiliado_plan_ensalud"));
		Prestacion prestacion=new Prestacion();
		prestacion.setCodigo(rs.getString("prestacion_codigo"));
		prestacion.setDescripcion(rs.getString("prestacion_descripcion"));
		reclamo.setPrestacion(prestacion);
		reclamo.setPrestacionRevisionResolucion(rs.getString("revision_resolucion"));
		reclamo.setPrestacionRevisionResponsable(rs.getString("revision_responsable"));
		reclamo.setObsRevision(rs.getString("revision_observacion"));
		reclamo.setObsAuditoriaMedica(rs.getString("auditoria_medica_observacion"));
		reclamo.setObsCierre(rs.getString("cierre_observacion"));
		reclamo.setJustificacionMedica(rs.getString("justificacion_medica"));
		reclamo.setDictamenComision(rs.getString("dictamen_comision"));
		reclamo.setFecha_cierre(rs.getDate("cierre_fecha"));
		reclamo.setCierreIncluidoGerenciadoraTexto(rs.getString("cierre_incluido_convenio"));
		reclamo.setCierreDosPorCientoTexto(rs.getString("cierre_dos_por_ciento"));
		reclamo.setCierreDebitoPrestadoraTexto(rs.getString("cierre_debito_prestadora"));
		reclamo.setCierreTipoGestion(rs.getString("cierre_tipo_gestion"));
		reclamo.setNroLote(rs.getInt("nro_lote"));
		reclamo.setFechaMailSeccional(rs.getDate("fecha_mail_seccional"));
		reclamo.setReclamoRecuperable(rs.getString("reclamo_recuperable"));
		reclamo.setDescIntegracion(rs.getString("integracion"));
		
		gasto.setReclamo(reclamo);
		
		return gasto;
	}
	
		
}


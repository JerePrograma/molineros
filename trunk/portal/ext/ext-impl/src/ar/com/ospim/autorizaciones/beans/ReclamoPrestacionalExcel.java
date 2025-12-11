package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.util.DateUtils;

public class ReclamoPrestacionalExcel extends ReclamoPrestacional {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String textoSeccional;
    private String planPrevencion;	
    private String amparoTexto ;
    private String reclamoEnTramite;
    private String reclamoRecuperable;
    private String reclamoSuperIntendencia;
    private String Troquel;
    private String prestacionTexto;
    private String prestacionFrecuencia;
    private double prestacionCargoOspim;
    private double prestacionCargoPs;
    private double prestacionImporte;
    private double prestacionTotalImporte;    
    private double prestacionCantidad; 
    private String prestacionRevisionResolucion;
    private String prestacionRevisionResponsable ;
    private String prestacionEstado;
    private String cierreFacturaTexto;
    private String cierreaNegociaTexto;
    private String cierreDebitoPrestadoraTexto;
    private String cierreIncluidoGerenciadoraTexto;
    private String cierreDosPorCientoTexto;
    private String cierreTipoGestion;
    private String nroOpLiquidacionReintegro;
	//private int id_autorizaciones_pmi;
	// private Date fecha;
	
    private String descAltaSeccional;
    private Date altaFechaSeccional;
    private String obsAuditoriaMedica;
    private String obsRevision;
    private String obsCierre;
    private String justificacionMedica;
    private String dictamenComision;
    private Date bajaFecha;
    private Double reconocidoSSS;
    private String discapacitado;
    
    
   
    private String descIntegracion;
    private String recuperableSur;
    
	public void setTroquel(String troquel) {
		Troquel = troquel;
	}

	public String getPrestacionTexto() {
		return prestacionTexto;
	}

	public void setPrestacionTexto(String prestacionTexto) {
		this.prestacionTexto = prestacionTexto;
	}

	public String getPrestacionFrecuencia() {
		return prestacionFrecuencia;
	}

	public void setPrestacionFrecuencia(String prestacionFrecuencia) {
		this.prestacionFrecuencia = prestacionFrecuencia;
	}

	public double getPrestacionCargoOspim() {
		return prestacionCargoOspim;
	}

	public void setPrestacionCargoOspim(double prestacionCargoOspim) {
		this.prestacionCargoOspim = prestacionCargoOspim;
	}

	public double getPrestacionCargoPs() {
		return prestacionCargoPs;
	}

	public void setPrestacionCargoPs(double prestacionCargoPs) {
		this.prestacionCargoPs = prestacionCargoPs;
	}

	public String getPrestacionRevisionResolucion() {
		return prestacionRevisionResolucion;
	}

	public void setPrestacionRevisionResolucion(String prestacionRevisionResolucion) {
		this.prestacionRevisionResolucion = prestacionRevisionResolucion;
	}

	public String getPrestacionRevisionResponsable() {
		return prestacionRevisionResponsable;
	}

	public void setPrestacionRevisionResponsable(String prestacionRevisionResponsable) {
		this.prestacionRevisionResponsable = prestacionRevisionResponsable;
	}

	public String getReclamoEnTramite() {
		return reclamoEnTramite;
	}

	public void setReclamoEnTramite(String reclamoEnTramite) {
		this.reclamoEnTramite = reclamoEnTramite;
	}

	public String getReclamoRecuperable() {
		return reclamoRecuperable;
	}

	public void setReclamoRecuperable(String reclamoRecuperable) {
		this.reclamoRecuperable = reclamoRecuperable;
	}

	public String getReclamoSuperIntendencia() {
		return reclamoSuperIntendencia;
	}

	public void setReclamoSuperIntendencia(String reclamoSuperIntendencia) {
		this.reclamoSuperIntendencia = reclamoSuperIntendencia;
	}

	public String getAmparoTexto() {
		return amparoTexto;
	}

	public void setAmparoTexto(String amparoTexto) {
		this.amparoTexto = amparoTexto;
	}
	
	

	public String getObsRevision() {
		return obsRevision;
	}

	public void setObsRevision(String obsRevision) {
		this.obsRevision = obsRevision;
	}

	public String getObsCierre() {
		return obsCierre;
	}

	public void setObsCierre(String obsCierre) {
		this.obsCierre = obsCierre;
	}

	public String getJustificacionMedica() {
		return justificacionMedica;
	}

	public void setJustificacionMedica(String justificacionMedica) {
		this.justificacionMedica = justificacionMedica;
	}

	public String getDictamenComision() {
		return dictamenComision;
	}

	public void setDictamenComision(String dictamenComision) {
		this.dictamenComision = dictamenComision;
	}
	
	
	public ReclamoPrestacionalExcel() {
		super();
	}
	
	

	public Double getReconocidoSSS() {
		return reconocidoSSS;
	}

	public void setReconocidoSSS(Double reconocidoSSS) {
		this.reconocidoSSS = reconocidoSSS;
	}

	public static ReclamoPrestacionalExcel getMapping(ResultSet rs) throws SQLException {
		ReclamoPrestacionalExcel archivo = new ReclamoPrestacionalExcel ();
		Afiliado afi = new Afiliado();
		afi.setCuil(rs.getString("rpt_afiliado_cuil_titular"));
		afi.setInte(rs.getInt("rpt_afiliado_inte"));
		afi.setApellido(rs.getString("rpt_afiliado_apellido"));
		afi.setNombre(rs.getString("rpt_afiliado_nombre"));
		afi.setPlanAfiliado(rs.getString("rpt_afiliado_plan_molineros"));
		afi.setDocu_numero(rs.getString("rpt_dni_afiliado")); 
		archivo.setAfiliado(afi);
		
		archivo.setTextoSeccional(rs.getString("rpt_afiliado_seccional"));
		archivo.getAfiliado().setPlanAfiliado(rs.getString("rpt_afiliado_plan_molineros"));		
		archivo.setPlanPrevencion(rs.getString("rpt_afiliado_plan_prevencion"));
		archivo.setEstadoReclamoPrestacion(rs.getString("rpt_reclamo_estado")  );
		archivo.setNroReclamo(rs.getInt("rpt_reclamo_nrocaso"));
		archivo.setOspim_fecha(rs.getDate("rpt_reclamo_fechaospim"));
		archivo.setSeccional_fecha(rs.getDate("rpt_reclamo_fechaseccional"));
		archivo.setSector(rs.getString("rpt_reclamo_sector"));
		archivo.setAmparoTexto(rs.getString("rpt_reclamo_amparo"));
		archivo.setReclamoEnTramite(rs.getString("rpt_reclamo_entramite"));
		archivo.setReclamoRecuperable(rs.getString("rpt_reclamo_recuperable"));
		archivo.setReclamoSuperIntendencia(rs.getString("rpt_reclamo_superintendencia"));
		archivo.setCaso_vinculado(rs.getInt("rpt_reclamo_casoasociado"));
        archivo.setTroquel(rs.getString("rpt_prestacion_codigotroquel"));
        archivo.setPrestacionTexto(rs.getString("rpt_prestacion_prestacion"));
        archivo.setPrestacionFrecuencia(rs.getString("rpt_prestacion_frecuencia"));
        
        archivo.setPrestacionCantidad(rs.getDouble("rpt_prestacion_cantidad"));
        archivo.setPrestacionImporte(rs.getDouble("rpt_prestacion_importe"));
        archivo.setPrestacionTotalImporte(rs.getDouble("rpt_prestacion_total"));
        archivo.setPrestacionEstado(rs.getString("rpt_estadoprestacion"));
        
        archivo.setPrestacionCargoOspim(rs.getDouble("rpt_prestacion_cargo_ospim"));
        archivo.setPrestacionCargoPs(rs.getDouble("rpt_prestacion_cargo_ps"));
        archivo.setPrestacionRevisionResolucion(rs.getString("rpt_revision_resolucion"));
		archivo.setPrestacionRevisionResponsable(rs.getString("rpt_revision_responsable"));
		archivo.setFecha_cierre(rs.getDate("rpt_cierre_fecha"));
		archivo.setCierreFacturaTexto(rs.getString("rpt_cierre_ps_factura"));
		archivo.setCierreaNegociaTexto(rs.getString("rpt_cierre_a_negociar"));
		archivo.setCierreIncluidoGerenciadoraTexto(rs.getString("rpt_cierre_incluidoconveniogerenciadora"));
		archivo.setCierreDebitoPrestadoraTexto(rs.getString("rpt_debito_prestadora"));
		archivo.setCierreDosPorCientoTexto(rs.getString("rpt_cierre_dosporciento"));
		archivo.setCierreTipoGestion(rs.getString("rpt_cierre_tipogestion")  );		
		archivo.setNroOpLiquidacionReintegro(rs.getString("rpt_nroop")  );
		archivo.setTipoPedido(rs.getString("rpt_tipo_pedido")  );
		archivo.setNroLote(rs.getInt("rpt_nro_lote")  );
		archivo.setDescAltaSeccional(rs.getString("rpt_seccional_alta")  );
		archivo.setAltaFechaSeccional(rs.getTimestamp("rpt_alta_secc_fecha"));
		archivo.setObsAuditoriaMedica(rs.getString("rpt_descripcion")  );
        archivo.setBaja_fecha(rs.getDate("rpt_baja_fecha"));
        archivo.setDescIntegracion(rs.getString("desc_integracion"));
        archivo.setRecuperableSur(rs.getString("recuperablesur"));
        archivo.setObsRevision(rs.getString("obs_revision"));
        archivo.setObsCierre(rs.getString("obs_cierre"));
        archivo.setJustificacionMedica(rs.getString("justificacion_medica"));
        archivo.setDictamenComision(rs.getString("dictamen_comision"));   
        archivo.setFechaOP(rs.getDate("fecha_op"));
        archivo.setReconocidoSSS(rs.getDouble("rpt_prestacion_reconocido_sss"));
		return archivo;
	}

	public String getCierreFacturaTexto() {
		return cierreFacturaTexto;
	}

	public void setCierreFacturaTexto(String cierreFacturaTexto) {
		this.cierreFacturaTexto = cierreFacturaTexto;
	}

	public String getCierreaNegociaTexto() {
		return cierreaNegociaTexto;
	}

	public void setCierreaNegociaTexto(String cierreaNegociaTexto) {
		this.cierreaNegociaTexto = cierreaNegociaTexto;
	}

	public String getCierreDebitoPrestadoraTexto() {
		return cierreDebitoPrestadoraTexto;
	}

	public void setCierreDebitoPrestadoraTexto(String cierreDebitoPrestadoraTexto) {
		this.cierreDebitoPrestadoraTexto= cierreDebitoPrestadoraTexto;
	}
	
	
	
	public String getCierreIncluidoGerenciadoraTexto() {
		return cierreIncluidoGerenciadoraTexto;
	}

	public void setCierreIncluidoGerenciadoraTexto(String cierreIncluidoGerenciadoraTexto) {
		this.cierreIncluidoGerenciadoraTexto = cierreIncluidoGerenciadoraTexto;
	}

	public String getCierreDosPorCientoTexto() {
		return cierreDosPorCientoTexto;
	}

	public void setCierreDosPorCientoTexto(String cierreDosPorCientoTexto) {
		this.cierreDosPorCientoTexto = cierreDosPorCientoTexto;
	}

	public String getCierreTipoGestion() {
		return cierreTipoGestion;
	}

	public void setCierreTipoGestion(String cierreTipoGestion) {
		this.cierreTipoGestion = cierreTipoGestion;
	}

	public String getTextoSeccional() {
		return textoSeccional;
	}

	public void setTextoSeccional(String textoSeccional) {
		this.textoSeccional = textoSeccional;
	}
	
	
	public String getPlanPrevencion() {
		return planPrevencion;
	}

	public void setPlanPrevencion(String planPrevencion) {
		this.planPrevencion = planPrevencion;
	}

	public String  getFecha_cierre_Texto(){
		return super.getFecha_cierre_Texto();
	}
	public String getTroquel() {
		return Troquel;
	}
	
	public double getPrestacionImporte() {
		return prestacionImporte;
	}

	public void setPrestacionImporte(double prestacionImporte) {
		this.prestacionImporte = prestacionImporte;
	}

	public double getPrestacionCantidad() {
		return prestacionCantidad;
	}

	public void setPrestacionCantidad(double prestacionCantidad) {
		this.prestacionCantidad = prestacionCantidad;
	}
    public void setPrestacionTotalImporte( double valor ) { 
    	prestacionTotalImporte=valor ; 
    }
	public double getPrestacionTotalImporte() {
		return prestacionTotalImporte;
	}
	public void setPrestacionEstado( String estadoPrestacion) { 
		prestacionEstado=estadoPrestacion; 
    }
	public String getPrestacionEstado () {
		return prestacionEstado;
	}
	
	public String getNroOpLiquidacionReintegro() {
		return nroOpLiquidacionReintegro;
	}

	public void setNroOpLiquidacionReintegro(String nroOpLiquidacionReintegro) {
		this.nroOpLiquidacionReintegro = nroOpLiquidacionReintegro;
	}

	public String getDescAltaSeccional() {
		return descAltaSeccional;
	}

	public void setDescAltaSeccional(String descAltaSeccional) {
		this.descAltaSeccional = descAltaSeccional;
	}

	public Date getAltaFechaSeccional() {
		return altaFechaSeccional;
	}

	public void setAltaFechaSeccional(Date altaFechaSeccional) {
		this.altaFechaSeccional = altaFechaSeccional;
	}

	public String getFechaMailSeccional_fechaAsString() {
		return altaFechaSeccional != null ? DateUtils.format(altaFechaSeccional, "dd/MM/yyyy HH:MM") : "";
	}

	public String getObsAuditoriaMedica() {
		return obsAuditoriaMedica;
	}

	public void setObsAuditoriaMedica(String obsAuditoriaMedica) {
		this.obsAuditoriaMedica = obsAuditoriaMedica;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getDescIntegracion() {
		return descIntegracion;
	}

	public void setDescIntegracion(String descIntegracion) {
		this.descIntegracion = descIntegracion;
	}

	public String getRecuperableSur() {
		return recuperableSur;
	}

	public void setRecuperableSur(String recuperableSur) {
		this.recuperableSur = recuperableSur;
	}

	public String getDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}
	
	
}

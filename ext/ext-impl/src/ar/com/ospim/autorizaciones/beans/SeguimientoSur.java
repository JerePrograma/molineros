package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;

public class SeguimientoSur implements Serializable{

	private static final long serialVersionUID = -4714618031210541813L;
	
	private Integer id;
	private int anio;
	private Integer id_bimestre;
	private String bimestreDescripcion;
	private Integer id_tipo_expediente;
	private Integer id_tipo_expediente_tercerizadora;
	private Integer id_autoriza_omint;
	private String nro_solicitud_sur;
	private String nro_expediente;
	private Integer id_codigo_presentado;
	private String cuilTitular;
	private Integer integrante;
//	FIXME Acá había que poner como atributo la clase Afiliado, mantengo la chanchada para no tener que romper demasiado.	
	private String cuil;  
	private String afiliadoNombre;
	private String codigoPresentado;
	private String descripcionPresentado;
	private Integer tipoNomencladorId;
	private List<TratamientoDiscapacidadSeguimiento> tratamientos;
	private List<SeguimientoSurDetalle> detalles;
	private Double importePresentado;
	private Date baja_fecha;
	private Date alta_fecha;
	private Date presentacion_fecha;
	private Date cierre_fecha;
	private String cierre_motivo;
	private Integer norma;
	private Integer patologia;
	private boolean tutelaje;
	private Date tutelaje_fecha;
	private String claseExpediente;
	private List<ComprobanteTratamientoDiscapacidad> liquidaciones;
	
	private String comprobanteTipo;
	private String comprobanteLetra;
	private Integer comprobanteSucursal;
	private String comprobanteNumero;
	private Date comprobanteFecha;
	private Double comprobanteImporte;
	private Date mesaEntrada_fecha;
	private List<SeguimientoSurEstado> estados;
	private String motivoBaja;
	private List<SeguimientoSurPrestador> prestadores;
	
	private String nro_correspondencia_sur;
	private String tutelaje_observaciones;
	private Double topeRecupero;
	
	private String unidadMedidaDiagnostico;
	private Double valorUnitario;
	private Date diagnostico_fecha;
	private Date finTratamiento_fecha;
	private Integer cantidadMesesTratamiento;
	private String alta_usr;
	private String patologiaDescripcion;
	
	private Integer id_tipo_expediente_nro;
	private String observaciones;
	private Integer cantidadAfiliados;
	
//	FIXME
	private Date ultimoEstadoAltaFecha;
	private String ultimoEstado;
	
	private String observacionUltimoEstado;
	
	private List<Nomenclador> codigosPresentados;
	private List<SeguimientoSurComprobante>comprobantes;
	private Double importeReconocido;
	private String periodicidadHemofilia;
	
	private Date fecha_ingreso_area_sur;
	
	private String codigoHIV;
	
	private Double proporcionalAdelantado;
	private Date fechaProporcionalAdelantado;
	private String tipoRegistro;
	
	private Double importeOspim;
	private Double importeOmint;
	private Double importePrevencion;
	private Double importeEnSalud;
	private Double importeCemic;
	
	private String convenioTercerizadora;  
	private String afiliadoPlan;
	private Date ddjj;
	
	public SeguimientoSur() {
		tratamientos=new ArrayList<TratamientoDiscapacidadSeguimiento>();
		detalles=new ArrayList<SeguimientoSurDetalle>();
		liquidaciones=new ArrayList<ComprobanteTratamientoDiscapacidad>();
		estados=new ArrayList<SeguimientoSurEstado>();
		prestadores=new ArrayList<SeguimientoSurPrestador>();
		codigosPresentados = new ArrayList<Nomenclador>();
		comprobantes= new ArrayList<SeguimientoSurComprobante>();
	}

	public static SeguimientoSur getMapping(ResultSet rs) throws SQLException {
		SeguimientoSur expSur = new SeguimientoSur();
		expSur.setAlta_fecha(rs.getDate("alta_fecha"));
		expSur.setBaja_fecha(rs.getDate("baja_fecha"));
		expSur.setAnio(rs.getInt("anio"));
		expSur.setId_bimestre(rs.getInt("id_bimestre"));
		expSur.setBimestreDescripcion(rs.getString("des_bimestre")==null?"":rs.getString("des_bimestre"));
		expSur.setId(rs.getInt("id"));
		expSur.setId_autoriza_omint(rs.getInt("id_autoriza_omint"));
		expSur.setId_codigo_presentado(rs.getInt("id_codigo_presentado"));
		expSur.setId_tipo_expediente(rs.getInt("id_tipo_expediente"));
		expSur.setId_tipo_expediente_tercerizadora(rs.getInt("id_tipo_expediente_tercerizadora"));
		expSur.setNro_expediente(rs.getString("nro_expediente")==null?"":rs.getString("nro_expediente"));
		expSur.setNro_solicitud_sur(rs.getString("nro_solicitud_sur")==null?"":rs.getString("nro_solicitud_sur"));
		expSur.setCuilTitular(rs.getString("cuil_titular")==null?"":rs.getString("cuil_titular"));
		expSur.setIntegrante(rs.getInt("inte"));
		expSur.setCuil(rs.getString("cuil"));
		expSur.setAfiliadoNombre(rs.getString("nombre_afiliado")==null?"":rs.getString("nombre_afiliado"));
		expSur.setCodigoPresentado(rs.getString("cd_codigo_presentado")==null?"":rs.getString("cd_codigo_presentado"));
		expSur.setDescripcionPresentado(rs.getString("des_codigo_presentado")==null?"":rs.getString("des_codigo_presentado"));
		expSur.setTipoNomencladorId(rs.getInt("id_tipo_nomenclador"));
		expSur.setPresentacion_fecha(rs.getDate("presentacion_fecha"));
		expSur.setImportePresentado(rs.getDouble("importe_presentado"));
		expSur.setCierre_fecha(rs.getDate("cierre_fecha"));
		expSur.setNorma(rs.getInt("norma"));
		expSur.setPatologia(rs.getInt("patologia"));
		expSur.setTutelaje(rs.getBoolean("tutelaje"));
		expSur.setClaseExpediente(rs.getString("clase")==null?"":rs.getString("clase"));
		expSur.setCierre_motivo(rs.getString("cierre_motivo")==null?"":rs.getString("cierre_motivo"));
		
		expSur.setComprobanteLetra(rs.getString("comprobante_letra"));
		expSur.setComprobanteNumero(rs.getString("comprobante_numero"));
		expSur.setComprobanteSucursal(rs.getInt("comprobante_sucursal"));
		expSur.setComprobanteTipo(rs.getString("comprobante_tipo"));
		expSur.setComprobanteFecha(rs.getDate("comprobante_fecha"));
		expSur.setComprobanteImporte(rs.getDouble("comprobante_importe"));
		
		expSur.setMesaEntrada_fecha(rs.getDate("mesaentrada_fecha"));
		expSur.setNro_correspondencia_sur(rs.getString("nro_correspondencia_sur")==null?"":rs.getString("nro_correspondencia_sur"));
		expSur.setTutelaje_fecha(rs.getDate("tutelaje_fecha"));
		expSur.setMotivoBaja(rs.getString("motivo_baja"));
		
		expSur.setTutelaje_observaciones(rs.getString("tutelaje_observacion"));
		expSur.setTopeRecupero(rs.getDouble("tope_recupero"));
		
		expSur.setUnidadMedidaDiagnostico(rs.getString("unidad_medida_diagnostico"));
		expSur.setCantidadMesesTratamiento(rs.getInt("cantidad_medida_diagnostico"));
		expSur.setValorUnitario(rs.getDouble("valor_unitario"));
		expSur.setDiagnostico_fecha(rs.getDate("diagnostico_fecha"));
		expSur.setFinTratamiento_fecha(rs.getDate("fin_tratamiento_fecha"));

		expSur.setAlta_usr(rs.getString("alta_usr"));
		expSur.setPatologiaDescripcion(rs.getString("patologia_descripcion"));
		
		expSur.setId_tipo_expediente_nro(rs.getInt("id_tipo_expediente_nro"));
		expSur.setObservaciones(rs.getString("observaciones"));
		expSur.setCantidadAfiliados(rs.getInt("cantidad_afiliados"));
		expSur.setImporteReconocido(rs.getDouble("importe_reconocido"));
		expSur.setPeriodicidadHemofilia(rs.getString("periodicidad_hemofilia"));
		expSur.setImporteOmint(rs.getDouble("importe_omint"));
		expSur.setImporteOspim(rs.getDouble("importe_ospim"));
		expSur.setImportePrevencion(rs.getDouble("importe_prevencion"));
		expSur.setImporteEnSalud(rs.getDouble("importe_ensalud"));
		expSur.setImporteCemic(rs.getDouble("importe_cemic"));
		
		expSur.setFecha_ingreso_area_sur(rs.getDate("fecha_ingreso_area_sur"));
		
		try{
		  expSur.setConvenioTercerizadora(rs.getString("convenio_tercerizadora"));
		  expSur.setUltimoEstadoDescripcion(rs.getString("estado_descripcion"));
		  expSur.setObservacionUltimoEstado(rs.getString("observacion_estado"));		  
		}catch(Exception e){
			expSur.setUltimoEstadoDescripcion("");	
			expSur.setObservacionUltimoEstado("");
		}
		expSur.setCodigoHIV(rs.getString("codigo_hiv")==null?"":rs.getString("codigo_hiv"));
		expSur.setProporcionalAdelantado(rs.getDouble("proporcional_adelantado"));
		expSur.setFechaProporcionalAdelantado(rs.getDate("fecha_pago_prop"));
		try {
			expSur.setAfiliadoPlan(rs.getString("plan"));
		}catch(Exception e) {
			expSur.setAfiliadoPlan("");
		}

		try {
			expSur.setDdjj(rs.getDate("fecha_ddjj"));
		}catch(Exception e) {
			expSur.setDdjj(null);
		}
		return expSur;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public final Date getBaja_fecha() {
		return baja_fecha;
	}
	
	public String getBaja_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return baja_fecha != null ? sdf.format(baja_fecha)
				: "";
	}

	public final void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public Integer getId_bimestre() {
		return id_bimestre;
	}

	public void setId_bimestre(Integer id_bimestre) {
		this.id_bimestre = id_bimestre;
	}

	public Integer getId_tipo_expediente() {
		return id_tipo_expediente;
	}

	public void setId_tipo_expediente(Integer id_tipo_expediente) {
		this.id_tipo_expediente = id_tipo_expediente;
	}

	public Integer getId_autoriza_omint() {
		return id_autoriza_omint;
	}

	public void setId_autoriza_omint(Integer id_autoriza_omint) {
		this.id_autoriza_omint = id_autoriza_omint;
	}

	public String getNro_solicitud_sur() {
		return nro_solicitud_sur;
	}

	public void setNro_solicitud_sur(String nro_solicitud_sur) {
		this.nro_solicitud_sur = nro_solicitud_sur;
	}

	public String getNro_expediente() {
		return nro_expediente;
	}

	public void setNro_expediente(String nro_expediente) {
		this.nro_expediente = nro_expediente;
	}

	public Integer getId_codigo_presentado() {
		return id_codigo_presentado;
	}

	public void setId_codigo_presentado(Integer id_codigo_presentado) {
		this.id_codigo_presentado = id_codigo_presentado;
	}

	public String getBimestreDescripcion() {
		return bimestreDescripcion;
	}

	public void setBimestreDescripcion(String bimestreDescripcion) {
		this.bimestreDescripcion = bimestreDescripcion;
	}

	public List<TratamientoDiscapacidadSeguimiento> getTratamientos() {
		return tratamientos;
	}

	public void setTratamientos(List<TratamientoDiscapacidadSeguimiento> tratamientos) {
		this.tratamientos = tratamientos;
	}

	public List<SeguimientoSurDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<SeguimientoSurDetalle> detalles) {
		this.detalles = detalles;
	}

	public String getCuilTitular() {
		return cuilTitular;
	}

	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}

	public Integer getIntegrante() {
		return integrante;
	}

	public void setIntegrante(Integer integrante) {
		this.integrante = integrante;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getAfiliadoNombre() {
		return afiliadoNombre;
	}

	public void setAfiliadoNombre(String afiliadoNombre) {
		this.afiliadoNombre = afiliadoNombre;
	}

	public String getCodigoPresentado() {
		return codigoPresentado;
	}

	public void setCodigoPresentado(String codigoPresentado) {
		this.codigoPresentado = codigoPresentado;
	}

	public String getDescripcionPresentado() {
		return descripcionPresentado;
	}

	public void setDescripcionPresentado(String descripcionPresentado) {
		this.descripcionPresentado = descripcionPresentado;
	}

	public Integer getTipoNomencladorId() {
		return tipoNomencladorId;
	}

	public void setTipoNomencladorId(Integer tipoNomencladorId) {
		this.tipoNomencladorId = tipoNomencladorId;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}
	
	public String getAlta_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return alta_fecha != null ? sdf.format(alta_fecha)
				: "";
	}
	
	public Date getPresentacion_fecha() {
		return presentacion_fecha;
	}

	public void setPresentacion_fecha(Date presentacion_fecha) {
		this.presentacion_fecha = presentacion_fecha;
	}
	
	public String getPresentacion_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return presentacion_fecha != null ? sdf.format(presentacion_fecha)
				: "";
	}

	public Double getImportePresentado() {
		return importePresentado;
	}

	public void setImportePresentado(Double importePresentado) {
		this.importePresentado = importePresentado;
	}
	

	public Double getImporteOspim() {
		return importeOspim;
	}

	public void setImporteOspim(Double importeOspim) {
		this.importeOspim= importeOspim;
	}

	public Double getImporteOmint() {
		return importeOmint;
	}

	public void setImporteOmint(Double importeOmint) {
		this.importeOmint= importeOmint;
	}
	
	public Double getImportePrevencion() {
		return importePrevencion;
	}

	public void setImportePrevencion(Double importePrevencion) {
		this.importePrevencion= importePrevencion;
	}
	
	
	public Date getCierre_fecha() {
		return cierre_fecha;
	}

	public void setCierre_fecha(Date cierre_fecha) {
		this.cierre_fecha = cierre_fecha;
	}
	
	public String getCierre_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return cierre_fecha != null ? sdf.format(cierre_fecha)
				: "";
	}

	public String getCierre_motivo() {
		return cierre_motivo;
	}

	public void setCierre_motivo(String cierre_motivo) {
		this.cierre_motivo = cierre_motivo;
	}

	public Integer getNorma() {
		return norma;
	}

	public void setNorma(Integer norma) {
		this.norma = norma;
	}

	public Integer getPatologia() {
		return patologia;
	}

	public void setPatologia(Integer patologia) {
		this.patologia = patologia;
	}

	public boolean getTutelaje() {
		return tutelaje;
	}

	public void setTutelaje(boolean tutelaje) {
		this.tutelaje = tutelaje;
	}

	public String getClaseExpediente() {
		return claseExpediente;
	}

	public void setClaseExpediente(String claseExpediente) {
		this.claseExpediente = claseExpediente ;
	}

	public List<ComprobanteTratamientoDiscapacidad> getLiquidaciones() {
		return liquidaciones;
	}

	public void setLiquidaciones(
			List<ComprobanteTratamientoDiscapacidad> liquidaciones) {
		this.liquidaciones = liquidaciones;
	}

	public String getComprobanteTipo() {
		return comprobanteTipo;
	}

	public void setComprobanteTipo(String comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}

	public String getComprobanteLetra() {
		return comprobanteLetra;
	}

	public void setComprobanteLetra(String comprobanteLetra) {
		this.comprobanteLetra = comprobanteLetra;
	}

	public Integer getComprobanteSucursal() {
		return comprobanteSucursal;
	}

	public void setComprobanteSucursal(Integer comprobanteSucursal) {
		this.comprobanteSucursal = comprobanteSucursal;
	}

	public String getComprobanteNumero() {
		return comprobanteNumero;
	}

	public void setComprobanteNumero(String comprobanteNumero) {
		this.comprobanteNumero = comprobanteNumero;
	}

	public Date getComprobanteFecha() {
		return comprobanteFecha;
	}

	public void setComprobanteFecha(Date comprobanteFecha) {
		this.comprobanteFecha = comprobanteFecha;
	}

	public String getComprobante_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return comprobanteFecha != null ? sdf.format(comprobanteFecha)
				: "";
	}
	
	public Double getComprobanteImporte() {
		return comprobanteImporte;
	}

	public void setComprobanteImporte(Double comprobanteImporte) {
		this.comprobanteImporte = comprobanteImporte;
	}

	public Integer getId_tipo_expediente_tercerizadora() {
		return id_tipo_expediente_tercerizadora;
	}

	public void setId_tipo_expediente_tercerizadora(
			Integer id_tipo_expediente_tercerizadora) {
		this.id_tipo_expediente_tercerizadora = id_tipo_expediente_tercerizadora;
	}
	
	public List<SeguimientoSurEstado> getEstados() {
		return estados;
	}

	public void setEstados(List<SeguimientoSurEstado> estados) {
		this.estados = estados;
	}

	public Date getMesaEntrada_fecha() {
		return mesaEntrada_fecha;
	}

	public void setMesaEntrada_fecha(Date mesaEntrada_fecha) {
		this.mesaEntrada_fecha = mesaEntrada_fecha;
	}

	public String getNro_correspondencia_sur() {
		return nro_correspondencia_sur;
	}

	public void setNro_correspondencia_sur(String nro_correspondencia_sur) {
		this.nro_correspondencia_sur = nro_correspondencia_sur;
	}

	public Date getTutelaje_fecha() {
		return tutelaje_fecha;
	}

	public void setTutelaje_fecha(Date tutelaje_fecha) {
		this.tutelaje_fecha = tutelaje_fecha;
	}

	public String getMotivoBaja() {
		return motivoBaja;
	}

	public void setMotivoBaja(String motivoBaja) {
		this.motivoBaja = motivoBaja;
	}

	public List<SeguimientoSurPrestador> getPrestadores() {
		return prestadores;
	}

	public void setPrestadores(List<SeguimientoSurPrestador> prestadores) {
		this.prestadores = prestadores;
	}

	public String getTutelaje_observaciones() {
		return tutelaje_observaciones;
	}

	public void setTutelaje_observaciones(String tutelaje_observaciones) {
		this.tutelaje_observaciones = tutelaje_observaciones;
	}

	public Double getTopeRecupero() {
		return topeRecupero;
	}

	public void setTopeRecupero(Double topeRecupero) {
		this.topeRecupero = topeRecupero;
	}
    
	public String getAfiliadoPlan() {
		return afiliadoPlan;
	}

	public void setAfiliadoPlan(String afiliadoPlan) {
		this.afiliadoPlan = afiliadoPlan;
	}
//	FIXME
	public String getUltimoEstado(){
		/*
		Collections.sort(estados, new Comparator<SeguimientoSurEstado>() {
			public int compare(SeguimientoSurEstado o1, SeguimientoSurEstado o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		
		Collections.reverse(estados);
		
		String estado ="";
		if(estados.size()>0){
			estado=estados.get(0).getDescripcionEstado();
		}
		return estado;
		*/
		String ret = "";
		SeguimientoSurEstado estado;
		try {
			estado = SeguimientoSurServiceUtil.ultimoEstadoSeguimientoSUR(id);
			if(estado!=null && estado.getDescripcionEstado()!=null){
				ret=estado.getDescripcionEstado();
			}
		} catch (Exception e) {
			ret="";
		}
		return ret;
		
	}
	
	public String getUltimoEstadoDescripcion() {
		return ultimoEstado;
	}

	public void setUltimoEstadoDescripcion(String ultimoEstado) {
		this.ultimoEstado = ultimoEstado;
	}

	public Date getUltimoEstadoAltaFecha() {
		return ultimoEstadoAltaFecha;
	}

	public void setUltimoEstadoAltaFecha(Date ultimoEstadoAltaFecha) {
		this.ultimoEstadoAltaFecha = ultimoEstadoAltaFecha;
	}
	
	public String getObservacionUltimoEstado() {
		return observacionUltimoEstado;
	}

	public void setObservacionUltimoEstado(String obsUltimoEstado) {
		this.observacionUltimoEstado= obsUltimoEstado;
	}
	
	
	public String getUnidadMedidaDiagnostico() {
		return unidadMedidaDiagnostico;
	}

	public void setUnidadMedidaDiagnostico(String unidadMedidaDiagnostico) {
		this.unidadMedidaDiagnostico = unidadMedidaDiagnostico;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Date getDiagnostico_fecha() {
		return diagnostico_fecha;
	}

	public void setDiagnostico_fecha(Date diagnostico_fecha) {
		this.diagnostico_fecha = diagnostico_fecha;
	}

	public Date getFinTratamiento_fecha() {
		return finTratamiento_fecha;
	}

	public void setFinTratamiento_fecha(Date finTratamiento_fecha) {
		this.finTratamiento_fecha = finTratamiento_fecha;
	}

	public Integer getCantidadMesesTratamiento() {
		return cantidadMesesTratamiento;
	}

	public void setCantidadMesesTratamiento(Integer cantidadMesesTratamiento) {
		this.cantidadMesesTratamiento = cantidadMesesTratamiento;
	}
	
	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public String getPatologiaDescripcion() {
		return patologiaDescripcion;
	}

	public void setPatologiaDescripcion(String patologiaDescripcion) {
		this.patologiaDescripcion = patologiaDescripcion;
	}

	public Integer getId_tipo_expediente_nro() {
		return id_tipo_expediente_nro;
	}

	public void setId_tipo_expediente_nro(Integer id_tipo_expediente_nro) {
		this.id_tipo_expediente_nro = id_tipo_expediente_nro;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Integer getCantidadAfiliados() {
		return cantidadAfiliados;
	}

	public void setCantidadAfiliados(Integer cantidadAfiliados) {
		this.cantidadAfiliados = cantidadAfiliados;
	}

	public List<Nomenclador> getCodigosPresentados() {
		return codigosPresentados;
	}

	public void setCodigosPresentados(List<Nomenclador> codigosPresentados) {
		this.codigosPresentados = codigosPresentados;
	}

	public List<SeguimientoSurComprobante> getComprobantes() {
		return comprobantes;
	}

	public void setComprobantes(List<SeguimientoSurComprobante> comprobantes) {
		this.comprobantes = comprobantes;
	}

	public Double getImporteReconocido() {
		return importeReconocido;
	}

	public void setImporteReconocido(Double importeReconocido) {
		this.importeReconocido = importeReconocido;
	}

	public String getPeriodicidadHemofilia() {
		return periodicidadHemofilia;
	}

	public void setPeriodicidadHemofilia(String periodicidadHemofilia) {
		this.periodicidadHemofilia = periodicidadHemofilia;
	}

	public Date getFecha_ingreso_area_sur() {
		return fecha_ingreso_area_sur;
	}

	public void setFecha_ingreso_area_sur(Date fecha_ingreso_area_sur) {
		this.fecha_ingreso_area_sur = fecha_ingreso_area_sur;
	}

	public String getCodigoHIV() {
		return codigoHIV;
	}

	public void setCodigoHIV(String codigoHIV) {
		this.codigoHIV = codigoHIV;
	}

	public Double getProporcionalAdelantado() {
		return proporcionalAdelantado;
	}

	public void setProporcionalAdelantado(Double proporcionalAdelantado) {
		this.proporcionalAdelantado = proporcionalAdelantado;
	}

	public Date getFechaProporcionalAdelantado() {
		return fechaProporcionalAdelantado;
	}

	public void setFechaProporcionalAdelantado(Date fechaProporcionalAdelantado) {
		this.fechaProporcionalAdelantado = fechaProporcionalAdelantado;
	}
	
	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}
		
	public String getConvenioTercerizadora() {
		return convenioTercerizadora;
	}

	public void setConvenioTercerizadora(String convenioTercerizadora) {
		this.convenioTercerizadora = convenioTercerizadora;
	}
	
	public Date getDdjj() {
		return ddjj;
	}

	public void setDdjj(Date ddjj) {
		this.ddjj = ddjj;
	}

	public Double getImporteEnSalud() {
		return importeEnSalud;
	}

	public void setImporteEnSalud(Double importeEnSalud) {
		this.importeEnSalud = importeEnSalud;
	}
	
	public Double getImporteCemic() {
		return importeCemic;
	}

	public void setImporteCemic(Double importeCemic) {
		this.importeCemic = importeCemic;
	}

	@Override
	public String toString() {
		return "SeguimientoSur [nro_solicitud_sur=" + nro_solicitud_sur + ", nro_expediente=" + nro_expediente
				+ ", afiliadoNombre=" + afiliadoNombre + ", presentacion_fecha=" + presentacion_fecha
				+ ", importeReconocido=" + importeReconocido + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nro_solicitud_sur == null) ? 0 : nro_solicitud_sur.hashCode());
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
		SeguimientoSur other = (SeguimientoSur) obj;
		if (nro_solicitud_sur == null) {
			if (other.nro_solicitud_sur != null)
				return false;
		} else if (!nro_solicitud_sur.equals(other.nro_solicitud_sur))
			return false;
		return true;
	}

}

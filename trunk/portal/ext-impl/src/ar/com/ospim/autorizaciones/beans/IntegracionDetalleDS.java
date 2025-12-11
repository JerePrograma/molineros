package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntegracionDetalleDS implements Serializable{

	
	private static final long serialVersionUID = 991710426678067179L;
	
	private Integer id;
	
	private String tipoArchivo;
	private Integer idObraSocial;
	private String cuil;
	private String certificadoCodigo;
	private Date certificadoVencimiento;
	private Integer periodoPrestacion;
	private String cuitPrestador;
	private Integer comprobanteTipo;
	private String comprobanteTipoEmision;
	private Date comprobanteFechaEmision;
	private String comprobanteCAECAI;
	private Integer comprobantePtoVta;
	private Integer comprobanteNro;
	private Double comprobanteImporte;
	private Double importeSolicitado;
	private String prestacionCodigo;
	private Integer prestacionCantidad;
	private Integer provincia;
	private String dependencia;
	private String tercerizadora;
	private String tercerizadoraId;
	private Integer ordenPago;
	private Integer liquidacion;
	private String descripcionPrestador;
	private String comprobanteString;
	private String cbu;
	private String opFecha;
	private Double opImporte;
	private Date fechaAvisoTransferencia;
	private Date fechaExportacionInterbanking;
    private String nroRecibo;		
	private Double importeSubsidiado;
	private String nroLiquidacionSSS;
	private String prestacionDescripcion;
	private Date enviadoSSS;
	private Integer periodo;
	private Double importeSolicitadoSSS;
	private String afiliado;
	private String entidad;
	private Double importeDebito;
	private String motivoDebito;
	
	private String error;
	private String errorSSS;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public String getTipoArchivo() {
		return tipoArchivo;
	}

	public void setTipoArchivo(String tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}

	public Integer getIdObraSocial() {
		return idObraSocial;
	}

	public void setIdObraSocial(Integer idObraSocial) {
		this.idObraSocial = idObraSocial;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getCertificadoCodigo() {
		return certificadoCodigo;
	}

	public void setCertificadoCodigo(String certificadoCodigo) {
		this.certificadoCodigo = certificadoCodigo;
	}

	public Date getCertificadoVencimiento() {
		return certificadoVencimiento;
	}

	public void setCertificadoVencimiento(Date certificadoVencimiento) {
		this.certificadoVencimiento = certificadoVencimiento;
	}

	public Integer getPeriodoPrestacion() {
		return periodoPrestacion;
	}

	public void setPeriodoPrestacion(Integer periodoPrestacion) {
		this.periodoPrestacion = periodoPrestacion;
	}

	public String getCuitPrestador() {
		return cuitPrestador;
	}

	public void setCuitPrestador(String cuitPrestador) {
		this.cuitPrestador = cuitPrestador;
	}

	public Integer getComprobanteTipo() {
		return comprobanteTipo;
	}

	public void setComprobanteTipo(Integer comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}

	public String getComprobanteTipoEmision() {
		return comprobanteTipoEmision;
	}

	public void setComprobanteTipoEmision(String comprobanteTipoEmision) {
		this.comprobanteTipoEmision = comprobanteTipoEmision;
	}

	public Date getComprobanteFechaEmision() {
		return comprobanteFechaEmision;
	}

	public void setComprobanteFechaEmision(Date comprobanteFechaEmision) {
		this.comprobanteFechaEmision = comprobanteFechaEmision;
	}

	public String getComprobanteCAECAI() {
		return comprobanteCAECAI;
	}

	public void setComprobanteCAECAI(String comprobanteCAECAI) {
		this.comprobanteCAECAI = comprobanteCAECAI;
	}

	public Integer getComprobantePtoVta() {
		return comprobantePtoVta;
	}

	public void setComprobantePtoVta(Integer comprobantePtoVta) {
		this.comprobantePtoVta = comprobantePtoVta;
	}

	public Integer getComprobanteNro() {
		return comprobanteNro;
	}

	public void setComprobanteNro(Integer comprobanteNro) {
		this.comprobanteNro = comprobanteNro;
	}

	public Double getComprobanteImporte() {
		Double ret=0D;
		if(comprobanteImporte!=null) {
		  ret=	Math.round(comprobanteImporte*100)/100.00;
		}
		return ret;
	}

	public void setComprobanteImporte(Double comprobanteImporte) {
		this.comprobanteImporte = comprobanteImporte;
	}

	public Double getImporteSolicitado() {
		return Math.round(importeSolicitado*100)/100.00;
	}

	public void setImporteSolicitado(Double importeSolicitado) {
		this.importeSolicitado = importeSolicitado;
	}

	public String getPrestacionCodigo() {
		return prestacionCodigo;
	}

	public void setPrestacionCodigo(String prestacionCodigo) {
		this.prestacionCodigo = prestacionCodigo;
	}

	public Integer getPrestacionCantidad() {
		return prestacionCantidad;
	}

	public void setPrestacionCantidad(Integer prestacionCantidad) {
		this.prestacionCantidad = prestacionCantidad;
	}

	public Integer getProvincia() {
		return provincia;
	}

	public void setProvincia(Integer provincia) {
		this.provincia = provincia;
	}

	public String getDependencia() {
		return dependencia;
	}

	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}

	public String getTercerizadora() {
		return tercerizadora;
	}

	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Date getEnviadoSSS() {
		return enviadoSSS;
	}

	public void setEnviadoSSS(Date enviadoSSS) {
		this.enviadoSSS = enviadoSSS;
	}
	
	public Integer getOrdenPago() {
		return ordenPago;
	}

	public void setOrdenPago(Integer ordenPago) {
		this.ordenPago = ordenPago;
	}

	public Integer getLiquidacion() {
		return liquidacion;
	}

	public void setLiquidacion(Integer liquidacion) {
		this.liquidacion = liquidacion;
	}

	
	
	public String getDescripcionPrestador() {
		return descripcionPrestador;
	}

	public void setDescripcionPrestador(String descripcionPrestador) {
		this.descripcionPrestador = descripcionPrestador;
	}

	
	
	public String getComprobanteString() {
		return comprobanteString;
	}

	public void setComprobanteString(String comprobanteString) {
		this.comprobanteString = comprobanteString;
	}

	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public String getOpFecha() {
		return opFecha;
	}

	public void setOpFecha(String opFecha) {
		this.opFecha = opFecha;
	}
	
	

	public String getTercerizadoraId() {
		return tercerizadoraId;
	}

	public void setTercerizadoraId(String tercerizadoraId) {
		this.tercerizadoraId = tercerizadoraId;
	}

	
	public Double getOpImporte() {
		return opImporte;
	}

	public void setOpImporte(Double opImporte) {
		this.opImporte = opImporte;
	}
	
	

	public Date getFechaAvisoTransferencia() {
		return fechaAvisoTransferencia;
	}

	public void setFechaAvisoTransferencia(Date fechaAvisoTransferencia) {
		this.fechaAvisoTransferencia = fechaAvisoTransferencia;
	}
	
	
	public String getNroRecibo() {
		return nroRecibo;
	}

	public void setNroRecibo(String nroRecibo) {
		this.nroRecibo = nroRecibo;
	}

	public Double getImporteSubsidiado() {
		return importeSubsidiado;
	}

	public void setImporteSubsidiado(Double importeSubsidiado) {
		this.importeSubsidiado = importeSubsidiado;
	}
	
	public String getNroLiquidacionSSS() {
		return nroLiquidacionSSS;
	}

	public void setNroLiquidacionSSS(String nroLiquidacionSSS) {
		this.nroLiquidacionSSS = nroLiquidacionSSS;
	}

	public String getPrestacionDescripcion() {
		return prestacionDescripcion;
	}

	public void setPrestacionDescripcion(String prestacionDescripcion) {
		this.prestacionDescripcion = prestacionDescripcion;
	}

	
	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	
	public Double getImporteSolicitadoSSS() {
		return importeSolicitadoSSS;
	}

	public void setImporteSolicitadoSSS(Double importeSolicitadoSSS) {
		this.importeSolicitadoSSS = importeSolicitadoSSS;
	}
	
	
	public String getErrorSSS() {
		return errorSSS;
	}

	public void setErrorSSS(String errorSSS) {
		this.errorSSS = errorSSS;
	}

	public static IntegracionDetalleDS getMapping(ResultSet rs) throws SQLException {
		IntegracionDetalleDS a = new IntegracionDetalleDS();
		
		a.setIdObraSocial(rs.getInt("id_obra_social"));
		a.setCuil(rs.getString("cuil"));
		a.setCertificadoCodigo(rs.getString("certificado_codigo"));
		a.setCertificadoVencimiento(rs.getDate("certificado_vencimiento"));
		a.setPeriodoPrestacion(rs.getInt("periodo_prestacion"));
		a.setCuitPrestador(rs.getString("cuit_prestador"));
		a.setComprobanteTipo(rs.getInt("comprobante_tipo"));
		a.setComprobanteTipoEmision(rs.getString("comprobante_tipo_emision"));
		a.setComprobanteFechaEmision(rs.getDate("comprobante_fecha_emision"));
		a.setComprobanteCAECAI(rs.getString("comprobante_cae_cai"));
		a.setComprobantePtoVta(rs.getInt("comprobante_pto_venta"));
		a.setComprobanteNro(rs.getInt("comprobante_nro"));
		a.setComprobanteImporte(rs.getDouble("comprobante_importe"));
		a.setImporteSolicitado(rs.getDouble("importe_solicitado"));
		a.setPrestacionCodigo(rs.getString("prestacion_codigo"));
		a.setPrestacionCantidad(rs.getInt("prestacion_cantidad"));   
		a.setProvincia(rs.getInt("provincia"));
		a.setDependencia(rs.getString("dependencia"));
		a.setError(rs.getString("cod_error"));
		a.setId(rs.getInt("id"));
		a.setTipoArchivo(rs.getString("tipo_registro"));
		try {
		  a.setImporteDebito(rs.getDouble("debito_importe"));
		  a.setMotivoDebito(rs.getString("debito_motivo"));
		} catch(Exception e){
			
		}
		return a;
	}
	
		
	public Integer getComprobanteFechaAsInteger() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		return Integer.parseInt(sdf.format(getComprobanteFechaEmision()));
	}

	public String getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(String afiliado) {
		this.afiliado = afiliado;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public Date getFechaExportacionInterbanking() {
		return fechaExportacionInterbanking;
	}

	public void setFechaExportacionInterbanking(Date fechaExportacionInterbanking) {
		this.fechaExportacionInterbanking = fechaExportacionInterbanking;
	}

	public Double getImporteDebito() {
		return importeDebito;
	}

	public void setImporteDebito(Double importeDebito) {
		this.importeDebito = importeDebito;
	}

	public String getMotivoDebito() {
		return motivoDebito;
	}

	public void setMotivoDebito(String motivoDebito) {
		this.motivoDebito = motivoDebito;
	}
	
}


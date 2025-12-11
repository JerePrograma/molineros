package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntegracionDetalleDR implements Serializable{

	
	private static final long serialVersionUID = -3330616630229522146L;

	private Integer id;
	private String clave;
	private Integer idObraSocial;
	private String tipoArchivo;
	private Integer periodoPresentacion;
	private Integer periodoPrestacion;
	private String cuil;
	private Integer prestacionCodigo;
	
	private Double importeLiquidado;
	private Double importeSolicitado;
	private String cuitPrestador;
	private Integer comprobanteTipo;
	private Integer comprobantePtoVta;
	private Integer comprobanteNro;
	private Integer nroEnvioAfip;
	
	private String cbu;
	private String cbuCuit;
	private Integer ordenPagoI;
    private Integer ordenPagoII;
    private Date fechaTransferenciaI;
    private Date fechaTransferenciaII;
    private String cheque;
    private Double importeTransferido;
    private Double retencionGanancias;
    private Double retencionIIBB;
    private Double otrasRetenciones;
    private Double importeAplicado;
    private Double fondosPropiosDiscapacidad;
    private Double fondosPropiosOtraCuenta;
    private Integer nroRecibo;
    private Double importeTrasladado;
    private Double importeDevuelto;
    private Double saldoNoAplicado;
    private Double recuperoFondosPropios;
    private String observaciones;
    private String error;
    private boolean soloErrores;
    
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
	
	public Double getImporteSolicitado() {
		return importeSolicitado;
	}

	public void setImporteSolicitado(Double importeSolicitado) {
		this.importeSolicitado = importeSolicitado;
	}

	public Integer getPrestacionCodigo() {
		return prestacionCodigo;
	}

	public void setPrestacionCodigo(Integer prestacionCodigo) {
		this.prestacionCodigo = prestacionCodigo;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Integer getPeriodoPresentacion() {
		return periodoPresentacion;
	}

	public void setPeriodoPresentacion(Integer periodoPresentacion) {
		this.periodoPresentacion = periodoPresentacion;
	}

	public Double getImporteLiquidado() {
		return importeLiquidado;
	}

	public void setImporteLiquidado(Double importeLiquidado) {
		this.importeLiquidado = importeLiquidado;
	}

	public Integer getNroEnvioAfip() {
		return nroEnvioAfip;
	}

	public void setNroEnvioAfip(Integer nroEnvioAfip) {
		this.nroEnvioAfip = nroEnvioAfip;
	}
	
	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public Integer getOrdenPagoI() {
		return ordenPagoI;
	}

	public void setOrdenPagoI(Integer ordenPagoI) {
		this.ordenPagoI = ordenPagoI;
	}

	public Integer getOrdenPagoII() {
		return ordenPagoII;
	}

	public void setOrdenPagoII(Integer ordenPagoII) {
		this.ordenPagoII = ordenPagoII;
	}

	public Date getFechaTransferenciaI() {
		return fechaTransferenciaI;
	}

	public void setFechaTransferenciaI(Date fechaTransferenciaI) {
		this.fechaTransferenciaI = fechaTransferenciaI;
	}

	public Date getFechaTransferenciaII() {
		return fechaTransferenciaII;
	}

	public void setFechaTransferenciaII(Date fechaTransferenciaII) {
		this.fechaTransferenciaII = fechaTransferenciaII;
	}

	public String getCheque() {
		return cheque;
	}

	public void setCheque(String cheque) {
		this.cheque = cheque;
	}

	public Double getImporteTransferido() {
		return importeTransferido;
	}

	public void setImporteTransferido(Double importeTransferido) {
		this.importeTransferido = importeTransferido;
	}

	public Double getRetencionGanancias() {
		return retencionGanancias;
	}

	public void setRetencionGanancias(Double retencionGanancias) {
		this.retencionGanancias = retencionGanancias;
	}

	public Double getRetencionIIBB() {
		return retencionIIBB;
	}

	public void setRetencionIIBB(Double retencionIIBB) {
		this.retencionIIBB = retencionIIBB;
	}

	public Double getOtrasRetenciones() {
		return otrasRetenciones;
	}

	public void setOtrasRetenciones(Double otrasRetenciones) {
		this.otrasRetenciones = otrasRetenciones;
	}

	public Double getImporteAplicado() {
		return importeAplicado;
	}

	public void setImporteAplicado(Double importeAplicado) {
		this.importeAplicado = importeAplicado;
	}

	public Double getFondosPropiosDiscapacidad() {
		return fondosPropiosDiscapacidad;
	}

	public void setFondosPropiosDiscapacidad(Double fondosPropiosDiscapacidad) {
		this.fondosPropiosDiscapacidad = fondosPropiosDiscapacidad;
	}

	public Double getFondosPropiosOtraCuenta() {
		return fondosPropiosOtraCuenta;
	}

	public void setFondosPropiosOtraCuenta(Double fondosPropiosOtraCuenta) {
		this.fondosPropiosOtraCuenta = fondosPropiosOtraCuenta;
	}

	public Integer getNroRecibo() {
		return nroRecibo;
	}

	public void setNroRecibo(Integer nroRecibo) {
		this.nroRecibo = nroRecibo;
	}

	public Double getImporteTrasladado() {
		return importeTrasladado;
	}

	public void setImporteTrasladado(Double importeTrasladado) {
		this.importeTrasladado = importeTrasladado;
	}

	public Double getImporteDevuelto() {
		return importeDevuelto;
	}

	public void setImporteDevuelto(Double importeDevuelto) {
		this.importeDevuelto = importeDevuelto;
	}

	public Double getSaldoNoAplicado() {
		return saldoNoAplicado;
	}

	public void setSaldoNoAplicado(Double saldoNoAplicado) {
		this.saldoNoAplicado = saldoNoAplicado;
	}

	public Double getRecuperoFondosPropios() {
		return recuperoFondosPropios;
	}

	public void setRecuperoFondosPropios(Double recuperoFondosPropios) {
		this.recuperoFondosPropios = recuperoFondosPropios;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public String getCbuCuit() {
		return cbuCuit;
	}

	public void setCbuCuit(String cbuCuit) {
		this.cbuCuit = cbuCuit;
	}
	
	
	public boolean isSoloErrores() {
		return soloErrores;
	}

	public void setSoloErrores(boolean soloErrores) {
		this.soloErrores = soloErrores;
	}

	public boolean isConProblema() {
		
		return analizaError()!=0;
	}

	
    public Integer analizaError() {
		Integer ret = 0;
		
		if(error!=null && !"OK".equalsIgnoreCase(error) && !"0".equalsIgnoreCase(error) ) {
			String[]vError =error.split("-");
			if(vError.length>1) {
			  ret=Integer.parseInt(vError[1]);
			}
		}
		if(ret==0 && !"OK".equalsIgnoreCase(error)) {
		   if (ordenPagoI==null || ordenPagoI==0) ret=615;
		   if((cbu==null || "".equalsIgnoreCase(cbu) || cbu.trim().length()<22)) ret=614;
		   if(cuitPrestador==null || cuitPrestador.trim().length()<11) ret=611;
		   if(!cuitPrestador.startsWith("30") &&
				!cuitPrestador.startsWith("33") && !cuitPrestador.startsWith("34") &&
				!cuitPrestador.startsWith("30") && !cuitPrestador.startsWith("20") &&
				!cuitPrestador.startsWith("27") && !cuitPrestador.startsWith("23") &&
				!cuitPrestador.startsWith("24")) ret=612;
		
		   if(fechaTransferenciaI==null) ret=616;
		   
		   if(importeAplicado*100D != importeTransferido*100D+ retencionGanancias*100D  ) {
			   ret=622;
		   }
		   
		   if(importeSolicitado*100D != fondosPropiosDiscapacidad*100D + fondosPropiosOtraCuenta*100D + importeAplicado*100D +
				   importeTrasladado*100D + importeDevuelto*100D + saldoNoAplicado*100D + recuperoFondosPropios*100D) {
			   ret=623;
		   }
		   
		   if(importeLiquidado*100D !=  importeAplicado*100D +  importeTrasladado*100D + importeDevuelto*100D + 
				   saldoNoAplicado*100D + recuperoFondosPropios*100D) {
//			   ret=624;
		   }
		   
		   
		}
		return ret;
	}
	
	public static IntegracionDetalleDR getMapping(ResultSet rs) throws SQLException {
		IntegracionDetalleDR a = new IntegracionDetalleDR();
		
		a.setIdObraSocial(rs.getInt("id_obra_social"));
		a.setCuil(rs.getString("cuil"));
		a.setPeriodoPresentacion(rs.getInt("periodo"));
		a.setPeriodoPrestacion(rs.getInt("periodo_prestacion"));
		a.setCuitPrestador(rs.getString("cuit_prestador"));
		a.setComprobanteTipo(rs.getInt("comprobante_tipo"));
		a.setComprobantePtoVta(rs.getInt("comprobante_pto_vta"));
		a.setComprobanteNro(rs.getInt("comprobante_nro"));
		a.setImporteSolicitado(rs.getDouble("importe_solicitado"));
		a.setImporteLiquidado(rs.getDouble("importe_liquidado"));
		a.setPrestacionCodigo(rs.getInt("prestacion_codigo"));
		a.setError(rs.getString("cod_error"));
		a.setId(rs.getInt("detalle_id"));
		a.setTipoArchivo(rs.getString("tipo_archivo"));
		a.setNroEnvioAfip(rs.getInt("afip_nro_envio"));
		a.setClave(rs.getString("clave"));
		
		a.setCbuCuit(rs.getString("cbu_cuit"));
		a.setCbu(rs.getString("cbu_nro"));
		a.setOrdenPagoI(rs.getInt("orden_pago_i"));
		a.setOrdenPagoII(rs.getInt("orden_pago_ii"));
		a.setFechaTransferenciaI(rs.getDate("fecha_transferencia_i"));
		a.setFechaTransferenciaII(rs.getDate("fecha_transferencia_ii"));
		a.setCheque(rs.getString("cheque"));
		a.setImporteTransferido(rs.getDouble("importe_transferido"));
		a.setRetencionGanancias(rs.getDouble("retencion_ganancias"));
		a.setRetencionIIBB(rs.getDouble("retencion_iibb"));
		a.setOtrasRetenciones(rs.getDouble("otras_retenciones"));
		a.setImporteAplicado(rs.getDouble("importe_aplicado_sss"));
		a.setFondosPropiosDiscapacidad(rs.getDouble("fondos_propios_discapacidad"));
		a.setFondosPropiosOtraCuenta(rs.getDouble("fondos_propios_otra_cuenta"));
		a.setNroRecibo(rs.getInt("recibo_nro"));
		a.setImporteTrasladado(rs.getDouble("importe_trasladado"));
		a.setImporteDevuelto(rs.getDouble("importe_devuelto_a_sss"));
		a.setSaldoNoAplicado(rs.getDouble("saldo_no_aplicado"));
		a.setRecuperoFondosPropios(rs.getDouble("recupero_fondos_propios"));
		a.setObservaciones(rs.getString("observaciones"));
		
		
		
		
		
		return a;
	}
	
		
}


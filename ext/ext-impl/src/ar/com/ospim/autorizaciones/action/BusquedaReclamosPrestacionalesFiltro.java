package ar.com.ospim.autorizaciones.action;

import java.io.Serializable;
import java.util.Date;

public class BusquedaReclamosPrestacionalesFiltro implements Serializable {


	private static final long serialVersionUID = -7340444418524791311L;
	
	private String entidad;
	private Date fechaOspimDesde;
	private Date fechaOspimHasta;
	private Date fechaCierreReclamoDesde;
	private Date fechaCierreReclamoHasta;
	private Date fechaSeccionalDesde;
	private Date fechaSeccionalHasta;
	
	private int numero;
	private String codPrest;
	private String codPrestaci;
	private String prestador;
	private int estado;
	private int inte;
	private int nroReclamo;
	private int nroAfi;
	private int tipoPrestacion;
	private String codePrestacion;
	private int tipoNomencladorBuscado;	 			
	private String cuilTitular;
	private int idPrestacion = 0;
	private String codigoTipoGestion;
	private String resolucion;
	private String tipoPedido;
	private String sectorSel;
	private Integer nroAutorizacion;
    private Integer nroLote;
    
	private int pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	
	
	
	//inicio Datos del Comprobante  
	private String frecuencia;
	private String comprobanteTipo;
	private String sucursalComprobante;
	private String numeroComprobante;
	private Date fechaComprobante;
	private String cuitEntidadComprobante;
	//fin Datos del Comprobante 
	
	private Integer idSeccional;
	
	private boolean marcaSeccional;
	
	private int codIntegracion;
	private int recuperableSur;
	
	public BusquedaReclamosPrestacionalesFiltro() {
	}
	
	public int getIntegracion() {
		return codIntegracion;
	}
	public void setIntegracion(int integracion) {
		this.codIntegracion = integracion;
	}
	public int getRecuperableSur() {
		return recuperableSur;
	}
	public void setRecuperableSur(int recuperableSur) {
		this.recuperableSur = recuperableSur;
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public Date getFechaOspimDesde() {
		return fechaOspimDesde;
	}
	public void setFechaOspimDesde(Date fechaOspimDesde) {
		this.fechaOspimDesde = fechaOspimDesde;
	}
	public Date getFechaOspimHasta() {
		return fechaOspimHasta;
	}
	public void setFechaOspimHasta(Date fechaOspimHasta) {
		this.fechaOspimHasta = fechaOspimHasta;
	}
	public Date getFechaCierreReclamoDesde() {
		return fechaCierreReclamoDesde;
	}
	public void setFechaCierreReclamoDesde(Date fechaCierreReclamoDesde) {
		this.fechaCierreReclamoDesde = fechaCierreReclamoDesde;
	}
	public Date getFechaCierreReclamoHasta() {
		return fechaCierreReclamoHasta;
	}
	public void setFechaCierreReclamoHasta(Date fechaCierreReclamoHasta) {
		this.fechaCierreReclamoHasta = fechaCierreReclamoHasta;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public String getCodPrest() {
		return codPrest;
	}
	public void setCodPrest(String codPrest) {
		this.codPrest = codPrest;
	}
	public String getCodPrestaci() {
		return codPrestaci;
	}
	public void setCodPrestaci(String codPrestaci) {
		this.codPrestaci = codPrestaci;
	}
	public String getPrestador() {
		return prestador;
	}
	public void setPrestador(String prestador) {
		this.prestador = prestador;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
	public int getNroReclamo() {
		return nroReclamo;
	}
	public void setNroReclamo(int nroReclamo) {
		this.nroReclamo = nroReclamo;
	}
	public int getNroAfi() {
		return nroAfi;
	}
	public void setNroAfi(int nroAfi) {
		this.nroAfi = nroAfi;
	}
	public int getTipoPrestacion() {
		return tipoPrestacion;
	}
	public void setTipoPrestacion(int tipoPrestacion) {
		this.tipoPrestacion = tipoPrestacion;
	}
	public String getCodePrestacion() {
		return codePrestacion;
	}
	public void setCodePrestacion(String codePrestacion) {
		this.codePrestacion = codePrestacion;
	}
	public int getTipoNomencladorBuscado() {
		return tipoNomencladorBuscado;
	}
	public void setTipoNomencladorBuscado(int tipoNomencladorBuscado) {
		this.tipoNomencladorBuscado = tipoNomencladorBuscado;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	public int getIdPrestacion() {
		return idPrestacion;
	}
	public void setIdPrestacion(int idPrestacion) {
		this.idPrestacion = idPrestacion;
	}
	public String getCodigoTipoGestion() {
		return codigoTipoGestion;
	}
	public void setCodigoTipoGestion(String codigoTipoGestion) {
		this.codigoTipoGestion = codigoTipoGestion;
	}
	public String getResolucion() {
		return resolucion;
	}
	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}
	public String getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(String tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	public String getSectorSel() {
		return sectorSel;
	}
	public void setSectorSel(String sectorSel) {
		this.sectorSel = sectorSel;
	}
	public Integer getNroAutorizacion() {
		return nroAutorizacion;
	}
	public void setNroAutorizacion(Integer nroAutorizacion) {
		this.nroAutorizacion = nroAutorizacion;
	}
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public int getRegistrosTotal() {
		return registrosTotal;
	}
	public void setRegistrosTotal(int registrosTotal) {
		this.registrosTotal = registrosTotal;
	}
	public int getRegistrosPorPagina() {
		return registrosPorPagina;
	}
	
	public Integer getNroLote() {
		return nroLote;
	}
	public void setNroLote(Integer nroLote) {
		this.nroLote = nroLote;
	}
	
	public String getFrecuencia() {
		return frecuencia;
	}
	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}
	public String getComprobanteTipo() {
		return comprobanteTipo;
	}
	public void setComprobanteTipo(String comprobanteTipo) {
		this.comprobanteTipo = comprobanteTipo;
	}
	public String getSucursalComprobante() {
		return sucursalComprobante;
	}
	public void setSucursalComprobante(String sucursalComprobante) {
		this.sucursalComprobante = sucursalComprobante;
	}
	public String getNumeroComprobante() {
		return numeroComprobante;
	}
	public void setNumeroComprobante(String numeroComprobante) {
		this.numeroComprobante = numeroComprobante;
	}
	public Date getFechaComprobante() {
		return fechaComprobante;
	}
	public void setFechaComprobante(Date fechaComprobante) {
		this.fechaComprobante = fechaComprobante;
	}
	public String getCuitEntidadComprobante() {
		return cuitEntidadComprobante;
	}
	public void setCuitEntidadComprobante(String cuitEntidadComprobante) {
		this.cuitEntidadComprobante = cuitEntidadComprobante;
	}

	public Date getFechaSeccionalDesde() {
		return fechaSeccionalDesde;
	}
	public void setFechaSeccionalDesde(Date fechaSeccionalDesde) {
		this.fechaSeccionalDesde = fechaSeccionalDesde;
	}
	
	public Date getFechaSeccionalHasta() {
		return fechaSeccionalHasta;
	}
	public void setFechaSeccionalHasta(Date fechaSeccionalHasta) {
		this.fechaSeccionalHasta = fechaSeccionalHasta;
	}
	
	public BusquedaReclamosPrestacionalesFiltro(String entidad, Date fechaOspimDesde, Date fechaOspimHasta,
			Date fechaCierreReclamoDesde, Date fechaCierreReclamoHasta, int numero, String codPrest, String codPrestaci,
			String prestador, int estado, int inte, int nroReclamo, int nroAfi, int tipoPrestacion,
			String codePrestacion, int tipoNomencladorBuscado, String cuilTitular, int idPrestacion,
			String codigotipogestion, String resolucion, String tipoPedido, String sectorSel, Integer nroAutorizacion,
			int pagina) {
		super();
		this.entidad = entidad;
		this.fechaOspimDesde = fechaOspimDesde;
		this.fechaOspimHasta = fechaOspimHasta;
		this.fechaCierreReclamoDesde = fechaCierreReclamoDesde;
		this.fechaCierreReclamoHasta = fechaCierreReclamoHasta;
		this.numero = numero;
		this.codPrest = codPrest;
		this.codPrestaci = codPrestaci;
		this.prestador = prestador;
		this.estado = estado;
		this.inte = inte;
		this.nroReclamo = nroReclamo;
		this.nroAfi = nroAfi;
		this.tipoPrestacion = tipoPrestacion;
		this.codePrestacion = codePrestacion;
		this.tipoNomencladorBuscado = tipoNomencladorBuscado;
		this.cuilTitular = cuilTitular;
		this.idPrestacion = idPrestacion;
		this.codigoTipoGestion = codigotipogestion;
		this.resolucion = resolucion;
		this.tipoPedido = tipoPedido;
		this.sectorSel = sectorSel;
		this.nroAutorizacion = nroAutorizacion;
		this.pagina = pagina;
	}
	
	public BusquedaReclamosPrestacionalesFiltro(String entidad, Date fechaOspimDesde, Date fechaOspimHasta,
			Date fechaCierreReclamoDesde, Date fechaCierreReclamoHasta, int numero, String codPrest, String codPrestaci,
			String prestador, int estado, int inte, int nroReclamo, int nroAfi, int tipoPrestacion,
			String codePrestacion, int tipoNomencladorBuscado, String cuilTitular, int idPrestacion,
			String codigoTipoGestion, String resolucion, String tipoPedido, String sectorSel, Integer nroAutorizacion,
			int pagina,Integer nroLote, String frecuencia, String comprobanteTipo, String sucursalComprobante,
			String numeroComprobante, Date fechaComprobante, String cuitEntidadComprobante, Integer idSeccional,
			Date fechaSeccional, Date fechaSeccionalHta, boolean marcaSeccional,  int codIntegracion, int recuperableSur) {
		
		super();
		this.entidad = entidad;
		this.fechaOspimDesde = fechaOspimDesde;
		this.fechaOspimHasta = fechaOspimHasta;
		this.fechaCierreReclamoDesde = fechaCierreReclamoDesde;
		this.fechaCierreReclamoHasta = fechaCierreReclamoHasta;
		this.numero = numero;
		this.codPrest = codPrest;
		this.codPrestaci = codPrestaci;
		this.prestador = prestador;
		this.estado = estado;
		this.inte = inte;
		this.nroReclamo = nroReclamo;
		this.nroAfi = nroAfi;
		this.tipoPrestacion = tipoPrestacion;
		this.codePrestacion = codePrestacion;
		this.tipoNomencladorBuscado = tipoNomencladorBuscado;
		this.cuilTitular = cuilTitular;
		this.idPrestacion = idPrestacion;
		this.codigoTipoGestion = codigoTipoGestion;
		this.resolucion = resolucion;
		this.tipoPedido = tipoPedido;
		this.sectorSel = sectorSel;
		this.nroAutorizacion = nroAutorizacion;
		this.nroLote = nroLote;
		this.pagina = pagina;
		this.frecuencia = frecuencia;
		this.comprobanteTipo = comprobanteTipo;
		this.sucursalComprobante = sucursalComprobante;
		this.numeroComprobante = numeroComprobante;
		this.fechaComprobante = fechaComprobante;
		this.cuitEntidadComprobante = cuitEntidadComprobante;
		this.idSeccional = idSeccional;
		this.fechaSeccionalDesde = fechaSeccional;
		this.fechaSeccionalHasta = fechaSeccionalHta;
		this.marcaSeccional = marcaSeccional;
		this.codIntegracion = codIntegracion;
		this.recuperableSur = recuperableSur;
		
	}
	public Integer getIdSeccional() {
		return idSeccional;
	}
	public void setIdSeccional(Integer idSeccional) {
		this.idSeccional = idSeccional;
	}
	public boolean isMarcaSeccional() {
		return marcaSeccional;
	}
	public void setMarcaSeccional(boolean marcaSeccional) {
		this.marcaSeccional = marcaSeccional;
	}
	
	
	
}

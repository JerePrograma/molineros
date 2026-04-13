package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaReclamoFiltro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String nroReclamo;
	private String tipoPedido;
	private String sector;
	private String resolucion;
	private String estados;
	private String tipoGestion;
	private String nroLote;
	private String idSeccionalCarga;
	private String descSeccionalCarga;
	
	private String prestacione;
	private Date fDesde;
	private Date fHasta;
	private Date cierreDesde;
	private Date cierreHasta;
	private String nroAfilido;
	private String cuil;
	private String integrante;
	private String tipoDocumentoSel;
	private String nroDocumento;
	private String apellido;
	private String nombre;
	private String endidadAfi;
	private String idSeccionalAfiliado;
	private String descSeccionalAfiliado;
	
	private String frecuencia;
	private String comprobante;
	private String sucursalComprobate;
	private String nroComprobante;
	private Date fechaEmision;
	private String cuit;
	private String sucursalEmpresa;
	private String razonSocia;
	private String idFarmacia;
	private String descFarmacia;
	private String idPrestacion;
	private String descPrestacion;
	private int codIntegracion;
	private int recuperoSur;











	public BusquedaReclamoFiltro(String nroReclamo, String tipoPedido, String sector, String resolucion, String estados,
			String tipoGestion, String nroLote, String idSeccionalCarga, String descSeccionalCarga, String prestacione,
			Date fDesde, Date fHasta, Date cierreDesde, Date cierreHasta, String nroAfilido, String cuil,
			String integrante, String tipoDocumentoSel, String nroDocumento, String apellido, String nombre,
			String idSeccionalAfiliado, String descSeccionalAfiliado, String frecuencia, String comprobante,
			String sucursalComprobate, String nroComprobante, Date fechaEmision, String cuit, String sucursalEmpresa,
			String razonSocia, String idFarmacia, String descFarmacia,String idPrestacion, String descPrestacion ,
			String entidad,  int codIntegracion, int recuperoSur) {
		super();
		this.nroReclamo = nroReclamo;
		this.tipoPedido = tipoPedido;
		this.sector = sector;
		this.resolucion = resolucion;
		this.estados = estados;
		this.tipoGestion = tipoGestion;
		this.nroLote = nroLote;
		this.idSeccionalCarga = idSeccionalCarga;
		this.descSeccionalCarga = descSeccionalCarga;
		this.prestacione = prestacione;
		this.fDesde = fDesde;
		this.fHasta = fHasta;
		this.cierreDesde = cierreDesde;
		this.cierreHasta = cierreHasta;
		this.nroAfilido = nroAfilido;
		this.cuil = cuil;
		this.integrante = integrante;
		this.tipoDocumentoSel = tipoDocumentoSel;
		this.nroDocumento = nroDocumento;
		this.apellido = apellido;
		this.nombre = nombre;
		this.idSeccionalAfiliado = idSeccionalAfiliado;
		this.descSeccionalAfiliado = descSeccionalAfiliado;
		this.frecuencia = frecuencia;
		this.comprobante = comprobante;
		this.sucursalComprobate = sucursalComprobate;
		this.nroComprobante = nroComprobante;
		this.fechaEmision = fechaEmision;
		this.cuit = cuit;
		this.sucursalEmpresa = sucursalEmpresa;
		this.razonSocia = razonSocia;
		this.idFarmacia = idFarmacia;
		this.descFarmacia = descFarmacia;
		this.idPrestacion = idPrestacion;
		this.descPrestacion = descPrestacion;
		this.endidadAfi = entidad;
		this.codIntegracion = codIntegracion;
		this.setRecuperoSur(recuperoSur);
	}
	




	
	public String getIdFarmacia() {
		return idFarmacia;
	}

	public void setIdFarmacia(String idFarmacia) {
		this.idFarmacia = idFarmacia;
	}

	public String getDescFarmacia() {
		return descFarmacia;
	}

	public void setDescFarmacia(String descFarmacia) {
		this.descFarmacia = descFarmacia;
	}

	public String getIdPrestacion() {
		return idPrestacion;
	}

	public void setIdPrestacion(String idPrestacion) {
		this.idPrestacion = idPrestacion;
	}
	public String getDescPrestacion() {
		return descPrestacion;
	}

	public void setDescPrestacion(String descPrestacion) {
		this.descPrestacion = descPrestacion;
	}



	public String getNroReclamo() {
		return nroReclamo;
	}
	public void setNroReclamo(String nroReclamo) {
		this.nroReclamo = nroReclamo;
	}
	public String getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(String tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getResolucion() {
		return resolucion;
	}
	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}
	public String getEstados() {
		return estados;
	}
	public void setEstados(String estados) {
		this.estados = estados;
	}
	public String getTipoGestion() {
		return tipoGestion;
	}
	public void setTipoGestion(String tipoGestion) {
		this.tipoGestion = tipoGestion;
	}
	public String getNroLote() {
		return nroLote;
	}
	public void setNroLote(String nroLote) {
		this.nroLote = nroLote;
	}
	public String getIdSeccionalCarga() {
		return idSeccionalCarga;
	}
	public void setIdSeccionalCarga(String idSeccionalCarga) {
		this.idSeccionalCarga = idSeccionalCarga;
	}
	public String getDescSeccionalCarga() {
		return descSeccionalCarga;
	}
	public void setDescSeccionalCarga(String descSeccionalCarga) {
		this.descSeccionalCarga = descSeccionalCarga;
	}
	public String getPrestacione() {
		return prestacione;
	}
	public void setPrestacione(String prestacione) {
		this.prestacione = prestacione;
	}
	public Date getfDesde() {
		return fDesde;
	}
	public void setfDesde(Date fDesde) {
		this.fDesde = fDesde;
	}
	public Date getfHasta() {
		return fHasta;
	}
	public void setfHasta(Date fHasta) {
		this.fHasta = fHasta;
	}
	public Date getCierreDesde() {
		return cierreDesde;
	}
	public void setCierreDesde(Date cierreDesde) {
		this.cierreDesde = cierreDesde;
	}
	public Date getCierreHasta() {
		return cierreHasta;
	}
	public void setCierreHasta(Date cierreHasta) {
		this.cierreHasta = cierreHasta;
	}
	public String getNroAfilido() {
		return nroAfilido;
	}
	public void setNroAfilido(String nroAfilido) {
		this.nroAfilido = nroAfilido;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public String getIntegrante() {
		return integrante;
	}
	public void setIntegrante(String integrante) {
		this.integrante = integrante;
	}
	public String getTipoDocumentoSel() {
		return tipoDocumentoSel;
	}
	public void setTipoDocumentoSel(String tipoDocumentoSel) {
		this.tipoDocumentoSel = tipoDocumentoSel;
	}
	public String getNroDocumento() {
		return nroDocumento;
	}
	public void setNroDocumento(String nroDocumento) {
		this.nroDocumento = nroDocumento;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getIdSeccionalAfiliado() {
		return idSeccionalAfiliado;
	}
	public void setIdSeccionalAfiliado(String idSeccionalAfiliado) {
		this.idSeccionalAfiliado = idSeccionalAfiliado;
	}
	public String getDescSeccionalAfiliado() {
		return descSeccionalAfiliado;
	}
	public void setDescSeccionalAfiliado(String descSeccionalAfiliado) {
		this.descSeccionalAfiliado = descSeccionalAfiliado;
	}
	public String getFrecuencia() {
		return frecuencia;
	}
	public void setFrecuencia(String frecuencia) {
		this.frecuencia = frecuencia;
	}
	public String getComprobante() {
		return comprobante;
	}
	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}
	public String getSucursalComprobate() {
		return sucursalComprobate;
	}
	public void setSucursalComprobate(String sucursalComprobate) {
		this.sucursalComprobate = sucursalComprobate;
	}
	public String getNroComprobante() {
		return nroComprobante;
	}
	public void setNroComprobante(String nroComprobante) {
		this.nroComprobante = nroComprobante;
	}
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public String getSucursalEmpresa() {
		return sucursalEmpresa;
	}
	public void setSucursalEmpresa(String sucursalEmpresa) {
		this.sucursalEmpresa = sucursalEmpresa;
	}
	public String getRazonSocia() {
		return razonSocia;
	}
	public void setRazonSocia(String razonSocia) {
		this.razonSocia = razonSocia;
	}

	public String getEndidadAfi() {
		return endidadAfi;
	}

	public void setEndidadAfi(String endidadAfi) {
		this.endidadAfi = endidadAfi;
	}

	public int getCodIntegracion() {
		return codIntegracion;
	}

	public void setCodIntegracion(int codIntegracion) {
		this.codIntegracion = codIntegracion;
	}


	public int getRecuperoSur() {
		return recuperoSur;
	}


	public void setRecuperoSur(int recuperoSur) {
		this.recuperoSur = recuperoSur;
	}

	

	

}

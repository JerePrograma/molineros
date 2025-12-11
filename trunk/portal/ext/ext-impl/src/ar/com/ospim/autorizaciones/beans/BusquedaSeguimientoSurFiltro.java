package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaSeguimientoSurFiltro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6673048353522042721L;
	
	private int anio; 
	private int bimestre;
	private int tipoExpediente;
	private int autorizaOmint;
	private String nroSolicitud;
	private String codigoPresentado;
	private String descripcionPresentado;
	private String nroExpediente;
	private String cuil;
	private String inte;
	private Date fechaDde; 
	private Date fechaHta;
	private boolean incluyeBajas;
	private String estadoExpediente;
	private String clase;
	private String usuarioAlta;
	private String estadoSSS; 
	private int claseNro;
	private Date fechaCorresDde; 
	private Date fechaCorresHta; 
	private int tipoTercerizadora; 
	private String nroCorrespondencia; 
	private String convenioTercerizadora; 
	private Date fechaEstadoDde;
	private Date fechaEstadoHta; 
	private String estadoHisSSS;
	private Date fechaDdeSur; 
	private Date fechaHtaSur;
	private Integer ddjj;
	private String codigoHIV;
	
//	private int pagina;
//	private int registrosTotal;
//	private final int registrosPorPagina = 50;
	
	
	public BusquedaSeguimientoSurFiltro(int anio, int bimestre, int tipoExpediente, int autorizaOmint,
			String nroSolicitud, String codigoPresentado, String descripcionPresentado, String nroExpediente,
			String cuil, String inte, Date fechaDde, Date fechaHta, Boolean incluyeBajas, String estadoExpediente,
			String clase, String usuarioAlta, String estadoSSS, int claseNro, Date fechaCorresDde, Date fechaCorresHta,
			int tipoTercerizadora, String nroCorrespondencia, String convenioTercerizadora, Date fechaEstadoDde,
			Date fechaEstadoHta, String estadoHisSSS, Date fechaDdeSur, Date fechaHtaSur,Integer ddjj, String codigoHIV) {
		super();
		this.anio = anio;
		this.bimestre = bimestre;
		this.tipoExpediente = tipoExpediente;
		this.autorizaOmint = autorizaOmint;
		this.nroSolicitud = nroSolicitud;
		this.codigoPresentado = codigoPresentado;
		this.descripcionPresentado = descripcionPresentado;
		this.nroExpediente = nroExpediente;
		this.cuil = cuil;
		this.inte = inte;
		this.fechaDde = fechaDde;
		this.fechaHta = fechaHta;
		this.incluyeBajas = incluyeBajas;
		this.estadoExpediente = estadoExpediente;
		this.clase = clase;
		this.usuarioAlta = usuarioAlta;
		this.estadoSSS = estadoSSS;
		this.claseNro = claseNro;
		this.fechaCorresDde = fechaCorresDde;
		this.fechaCorresHta = fechaCorresHta;
		this.tipoTercerizadora = tipoTercerizadora;
		this.nroCorrespondencia = nroCorrespondencia;
		this.convenioTercerizadora = convenioTercerizadora;
		this.fechaEstadoDde = fechaEstadoDde;
		this.fechaEstadoHta = fechaEstadoHta;
		this.estadoHisSSS = estadoHisSSS;
		this.fechaDdeSur = fechaDdeSur;
		this.fechaHtaSur = fechaHtaSur;
		this.ddjj=ddjj;
		this.codigoHIV=codigoHIV;
	}
	
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public int getBimestre() {
		return bimestre;
	}
	public void setBimestre(int bimestre) {
		this.bimestre = bimestre;
	}
	public int getTipoExpediente() {
		return tipoExpediente;
	}
	public void setTipoExpediente(int tipoExpediente) {
		this.tipoExpediente = tipoExpediente;
	}
	public int getAutorizaOmint() {
		return autorizaOmint;
	}
	public void setAutorizaOmint(int autorizaOmint) {
		this.autorizaOmint = autorizaOmint;
	}
	public String getNroSolicitud() {
		return nroSolicitud;
	}
	public void setNroSolicitud(String nroSolicitud) {
		this.nroSolicitud = nroSolicitud;
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
	public String getNroExpediente() {
		return nroExpediente;
	}
	public void setNroExpediente(String nroExpediente) {
		this.nroExpediente = nroExpediente;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public String getInte() {
		return inte;
	}
	public void setInte(String inte) {
		this.inte = inte;
	}
	public Date getFechaDde() {
		return fechaDde;
	}
	public void setFechaDde(Date fechaDde) {
		this.fechaDde = fechaDde;
	}
	public Date getFechaHta() {
		return fechaHta;
	}
	public void setFechaHta(Date fechaHta) {
		this.fechaHta = fechaHta;
	}
//	public Boolean getIncluyeBajas() {
//		return incluyeBajas;
//	}
//	public void setIncluyeBajas(Boolean incluyeBajas) {
//		this.incluyeBajas = incluyeBajas;
//	}
	
	public String getEstadoExpediente() {
		return estadoExpediente;
	}
	
	public boolean isIncluyeBajas() {
		return incluyeBajas;
	}

	public void setIncluyeBajas(boolean incluyeBajas) {
		this.incluyeBajas = incluyeBajas;
	}

	public void setEstadoExpediente(String estadoExpediente) {
		this.estadoExpediente = estadoExpediente;
	}
	public String getClase() {
		return clase;
	}
	public void setClase(String clase) {
		this.clase = clase;
	}
	public String getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(String usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public String getEstadoSSS() {
		return estadoSSS;
	}
	public void setEstadoSSS(String estadoSSS) {
		this.estadoSSS = estadoSSS;
	}
	public int getClaseNro() {
		return claseNro;
	}
	public void setClaseNro(int claseNro) {
		this.claseNro = claseNro;
	}
	public Date getFechaCorresDde() {
		return fechaCorresDde;
	}
	public void setFechaCorresDde(Date fechaCorresDde) {
		this.fechaCorresDde = fechaCorresDde;
	}
	public Date getFechaCorresHta() {
		return fechaCorresHta;
	}
	public void setFechaCorresHta(Date fechaCorresHta) {
		this.fechaCorresHta = fechaCorresHta;
	}
	public int getTipoTercerizadora() {
		return tipoTercerizadora;
	}
	public void setTipoTercerizadora(int tipoTercerizadora) {
		this.tipoTercerizadora = tipoTercerizadora;
	}
	public String getNroCorrespondencia() {
		return nroCorrespondencia;
	}
	public void setNroCorrespondencia(String nroCorrespondencia) {
		this.nroCorrespondencia = nroCorrespondencia;
	}
	public String getConvenioTercerizadora() {
		return convenioTercerizadora;
	}
	public void setConvenioTercerizadora(String convenioTercerizadora) {
		this.convenioTercerizadora = convenioTercerizadora;
	}
	public Date getFechaEstadoDde() {
		return fechaEstadoDde;
	}
	public void setFechaEstadoDde(Date fechaEstadoDde) {
		this.fechaEstadoDde = fechaEstadoDde;
	}
	public Date getFechaEstadoHta() {
		return fechaEstadoHta;
	}
	public void setFechaEstadoHta(Date fechaEstadoHta) {
		this.fechaEstadoHta = fechaEstadoHta;
	}
	public String getEstadoHisSSS() {
		return estadoHisSSS;
	}
	public void setEstadoHisSSS(String estadoHisSSS) {
		this.estadoHisSSS = estadoHisSSS;
	}
	public Date getFechaDdeSur() {
		return fechaDdeSur;
	}
	public void setFechaDdeSur(Date fechaDdeSur) {
		this.fechaDdeSur = fechaDdeSur;
	}
	public Date getFechaHtaSur() {
		return fechaHtaSur;
	}
	public void setFechaHtaSur(Date fechaHtaSur) {
		this.fechaHtaSur = fechaHtaSur;
	}

	public Integer getDdjj() {
		return ddjj;
	}

	public void setDdjj(Integer ddjj) {
		this.ddjj = ddjj;
	}

	public String getCodigoHIV() {
		return codigoHIV;
	}

	public void setCodigoHIV(String codigoHIV) {
		this.codigoHIV = codigoHIV;
	}
	
}

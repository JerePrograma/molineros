package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;

public class DemandaJudicial implements Serializable {

	
	private static final long serialVersionUID = -401408632190394513L;
	private SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	
	private Integer id;
	private String expediente;
	private String caratula;
	private String tipo;
	private String entidad;
	private Date fecha;
	private Double montoOriginal;
	private String cuit;
	private String sucursal;
	private String razonSocial;
	private String observaciones;
	private String juzgado;
	
	private List<Acta> actas;
	private List<Convenio> convenios;
	private List<Cheque>cheques;
	private List<Estado>estados;
	private Date altaFecha;
	private String altaUsr;
	private Date bajaFecha;
	private String bajaUsr;
	
	private Date fechaDde;
	private Date fechaHta;
	
	private String ultimoEstado;
	private Integer totalRegistros;
	private List<DLFileEntryImpl>imagenes;
	private List<Asiento>asientos;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public String getCaratula() {
		return caratula;
	}
	public void setCaratula(String caratula) {
		this.caratula = caratula;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Double getMontoOriginal() {
		return montoOriginal;
	}
	public void setMontoOriginal(Double montoOriginal) {
		this.montoOriginal = montoOriginal;
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
	public List<Acta> getActas() {
		return actas;
	}
	public void setActas(List<Acta> actas) {
		this.actas = actas;
	}
	public List<Convenio> getConvenios() {
		return convenios;
	}
	public void setConvenios(List<Convenio> convenios) {
		this.convenios = convenios;
	}
	public List<Cheque> getCheques() {
		return cheques;
	}
	public void setCheques(List<Cheque> cheques) {
		this.cheques = cheques;
	}
	public List<Estado> getEstados() {
		return estados;
	}
	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
	public String getFechaAsString() {
		return fecha!=null?sdf.format(fecha):"";
	}
	
	public String getEstadoActual() {
		return "";
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public String getJuzgado() {
		return juzgado;
	}
	public void setJuzgado(String juzgado) {
		this.juzgado = juzgado;
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
	
	public String getUltimoEstado() {
		return ultimoEstado;
	}
	public void setUltimoEstado(String ultimoEstado) {
		this.ultimoEstado = ultimoEstado;
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
	
	
	
	public Integer getTotalRegistros() {
		return totalRegistros;
	}
	public void setTotalRegistros(Integer totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	
	public List<DLFileEntryImpl> getImagenes() {
		return imagenes;
	}
	public void setImagenes(List<DLFileEntryImpl> imagenes) {
		this.imagenes = imagenes;
	}
	
	public List<Asiento> getAsientos() {
		return asientos;
	}
	public void setAsientos(List<Asiento> asientos) {
		this.asientos = asientos;
	}
	
	public DemandaJudicial() {
		super();
		setActas(new ArrayList<Acta>());
		setConvenios(new ArrayList<Convenio>());
		setCheques(new ArrayList<Cheque>());
		setEstados(new ArrayList<Estado>());
		setAsientos(new ArrayList<Asiento>());
	}
	
	
	public DemandaJudicial(Integer idDemanda) {
		this.id=idDemanda;
	}
	public static DemandaJudicial getMapping(String prefix, ResultSet rs) throws SQLException {
		
		DemandaJudicial demanda = new DemandaJudicial();
		demanda.setId(rs.getInt(prefix + "id"));
		demanda.setTipo(rs.getString(prefix+"tipo"));
		demanda.setBajaFecha(rs.getDate(prefix+"baja_fecha"));
		demanda.setBajaUsr(rs.getString(prefix+"baja_usr"));
		demanda.setAltaFecha(rs.getDate(prefix+"alta_fecha"));
		demanda.setAltaUsr(rs.getString(prefix+"alta_usr"));
		demanda.setFecha(rs.getDate(prefix+"fecha"));
		demanda.setEntidad(rs.getString(prefix+"entidad"));
		demanda.setExpediente(rs.getString(prefix+"expediente"));
		demanda.setCaratula(rs.getString(prefix+"caratula"));
		demanda.setJuzgado(rs.getString(prefix+"juzgado"));
		demanda.setCuit(rs.getString(prefix+"cuit"));
		demanda.setSucursal(rs.getString(prefix+"sucursal"));
		demanda.setObservaciones(rs.getString(prefix+"observaciones"));
		demanda.setMontoOriginal(rs.getDouble(prefix+"importe"));
		demanda.setTotalRegistros(rs.getInt(prefix+"total_registros"));
		demanda.setRazonSocial(rs.getString(prefix+"razon_social"));
		demanda.setUltimoEstado(rs.getString(prefix+"estado_id"));

		return demanda;
	
    }
    

}

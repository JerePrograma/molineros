package ar.com.ospim.autorizaciones.beans;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

public class ReportePreCargaReclamo  {


	private Integer idReclamo;
	private Date altaFecha;
	private String sector;
	private String apellido;
	private String nombre;
	private String sexo;
	private String tipoDocu;
	private String docuNumero;
	private BigDecimal totalComprobante;
	private double cantPrestaciones;
	private String seccionalDescripcion;
	private Date fechaMailSeccional;
	private String tipoPedido;
	private String estado;
	private String prestacion;
	private String revision;
	private String plan;
	private String observaciones;
	
	public Integer getIdReclamo() {
		return idReclamo;
	}
	public void setIdReclamo(Integer idReclamo) {
		this.idReclamo = idReclamo;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
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
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getTipoDocu() {
		return tipoDocu;
	}
	public void setTipoDocu(String tipoDocu) {
		this.tipoDocu = tipoDocu;
	}
	public String getDocuNumero() {
		return docuNumero;
	}
	public void setDocuNumero(String docuNumero) {
		this.docuNumero = docuNumero;
	}
	public BigDecimal getTotalComprobante() {
		return totalComprobante;
	}
	public void setTotalComprobante(BigDecimal totalComprobante) {
		this.totalComprobante = totalComprobante;
	}
	public double getCantPrestaciones() {
		return cantPrestaciones;
	}
	public void setCantPrestaciones(double cantPrestaciones) {
		this.cantPrestaciones = cantPrestaciones;
	}
	public String getSeccionalDescripcion() {
		return seccionalDescripcion;
	}
	public void setSeccionalDescripcion(String seccionalDescripcion) {
		this.seccionalDescripcion = seccionalDescripcion;
	}
	public Date getFechaMailSeccional() {
		return fechaMailSeccional;
	}
	public void setFechaMailSeccional(Date fechaMailSeccional) {
		this.fechaMailSeccional = fechaMailSeccional;
	}
	

	public String getTipoPedido() {
		return tipoPedido;
	}
	public void setTipoPedido(String tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getPrestacion() {
		return prestacion;
	}
	public void setPrestacion(String prestacion) {
		this.prestacion = prestacion;
	}
	
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public static ReportePreCargaReclamo getMappingReporte(ResultSet rs, String prefix) throws Exception {
		

		ReportePreCargaReclamo reclamo = new ReportePreCargaReclamo();
		
		try{		
			
			reclamo.setIdReclamo(rs.getInt(prefix + "id"));
			reclamo.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
			reclamo.setSector(rs.getString(prefix + "sector"));		
			reclamo.setApellido(rs.getString(prefix + "apellido"));
			reclamo.setNombre(rs.getString(prefix + "nombre"));
			reclamo.setSexo(rs.getString(prefix + "sexo"));    
			reclamo.setTipoDocu(rs.getString(prefix + "docu_tipo"));
			reclamo.setDocuNumero(rs.getString(prefix + "docu_nro"));
			reclamo.setTotalComprobante(rs.getBigDecimal(prefix + "total_comprobante") );
			reclamo.setCantPrestaciones(rs.getDouble(prefix + "cant_prestaciones") );
		    reclamo.setSeccionalDescripcion(rs.getString(prefix + "seccional_descripcion"));
		    reclamo.setFechaMailSeccional(rs.getDate(prefix + "fecha_mail_seccional"));
		    reclamo.setTipoPedido(rs.getString(prefix + "tipopedido"));
		    reclamo.setEstado(rs.getString(prefix + "estado"));
		    reclamo.setPrestacion(rs.getString(prefix + "prestacion"));
		    reclamo.setRevision(rs.getString(prefix + "revision"));
		    reclamo.setPlan(rs.getString(prefix + "plan"));
		    reclamo.setObservaciones(rs.getString(prefix + "observaciones"));
		
		}
		catch (Exception e ){
		    throw e;
		}
				
		return reclamo;

	}
	
	
}

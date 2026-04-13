package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CRMEstadistica implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4489513892232692740L;
	private String sector;
    private Integer tipo_llamado_entrante;
    private Integer tipo_llamado_saliente;
    private Integer tipo_atencion_seccional;
    private Integer tipo_whatsapp_entrante;
    private Integer tipo_otros;
    private Integer motivo_otros;
    private Integer categoria_consulta;
    private Integer categoria_reclamo;
    private Integer categoria_queja;
    private Integer categoria_sugerencia;
    private Integer categoria_felicitacion;
    private Integer estado_pendiente;
    private Integer estado_derivado;
    private Integer estado_cerrado;
//    private Integer total;
    
    
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public Integer getTipo_llamado_entrante() {
		return tipo_llamado_entrante!=null?tipo_llamado_entrante:0;
	}
	public void setTipo_llamado_entrante(Integer tipo_llamado_entrante) {
		this.tipo_llamado_entrante = tipo_llamado_entrante;
	}
	public Integer getTipo_llamado_saliente() {
		return tipo_llamado_saliente!=null?tipo_llamado_saliente:0;
	}
	public void setTipo_llamado_saliente(Integer tipo_llamado_saliente) {
		this.tipo_llamado_saliente = tipo_llamado_saliente;
	}
	public Integer getTipo_atencion_seccional() {
		return tipo_atencion_seccional!=null?tipo_atencion_seccional:0;
	}
	public void setTipo_atencion_seccional(Integer tipo_atencion_seccional) {
		this.tipo_atencion_seccional = tipo_atencion_seccional;
	}
	public Integer getTipo_otros() {
		return tipo_otros!=null?tipo_otros:0;
	}
	public void setTipo_otros(Integer tipo_otros) {
		this.tipo_otros = tipo_otros;
	}
	public Integer getMotivo_otros() {
		return motivo_otros!=null?motivo_otros:0;
	}
	public void setMotivo_otros(Integer motivo_otros) {
		this.motivo_otros = motivo_otros;
	}
	public Integer getCategoria_consulta() {
		return categoria_consulta!=null?categoria_consulta:0;
	}
	public void setCategoria_consulta(Integer categoria_consulta) {
		this.categoria_consulta = categoria_consulta;
	}
	public Integer getCategoria_reclamo() {
		return categoria_reclamo!=null?categoria_reclamo:0;
	}
	public void setCategoria_reclamo(Integer categoria_reclamo) {
		this.categoria_reclamo = categoria_reclamo;
	}
	public Integer getCategoria_queja() {
		return categoria_queja!=null?categoria_queja:0;
	}
	public void setCategoria_queja(Integer categoria_queja) {
		this.categoria_queja = categoria_queja;
	}
	public Integer getCategoria_sugerencia() {
		return categoria_sugerencia!=null?categoria_sugerencia:0;
	}
	public void setCategoria_sugerencia(Integer categoria_sugerencia) {
		this.categoria_sugerencia = categoria_sugerencia;
	}
	public Integer getCategoria_felicitacion() {
		return categoria_felicitacion!=null?categoria_felicitacion:0;
	}
	public void setCategoria_felicitacion(Integer categoria_felicitacion) {
		this.categoria_felicitacion = categoria_felicitacion;
	}
	public Integer getEstado_pendiente() {
		return estado_pendiente!=null?estado_pendiente:0;
	}
	public void setEstado_pendiente(Integer estado_pendiente) {
		this.estado_pendiente = estado_pendiente;
	}
	public Integer getEstado_derivado() {
		return estado_derivado!=null?estado_derivado:0;
	}
	public void setEstado_derivado(Integer estado_derivado) {
		this.estado_derivado = estado_derivado;
	}
	public Integer getEstado_cerrado() {
		return estado_cerrado!=null?estado_cerrado:0;
	}
	public void setEstado_cerrado(Integer estado_cerrado) {
		this.estado_cerrado = estado_cerrado;
	}
	public Integer getTotal() {
//		return total;
		return /*getCategoria_consulta()+ getCategoria_felicitacion()+ getCategoria_queja()+ getCategoria_reclamo()+ getCategoria_sugerencia()+*/
				getEstado_cerrado()+ getEstado_derivado()+ getEstado_pendiente()/*+ getMotivo_otros()+ getTipo_atencion_seccional()+ 
				getTipo_llamado_entrante()+ getTipo_llamado_saliente()+ getTipo_otros()*/;
	}
//	public void setTotal(Integer total) {
//		this.total = total;
//	}

	
	
	
	public CRMEstadistica(){
		super();
	}
	
	public Integer getTipo_whatsapp_entrante() {
		return tipo_whatsapp_entrante!=null?tipo_whatsapp_entrante:0;
	}
	
	public void setTipo_whatsapp_entrante(Integer tipo_whatsapp_entrante) {
		this.tipo_whatsapp_entrante = tipo_whatsapp_entrante;
	}
	
	public CRMEstadistica(String sector, Integer tipo_llamado_entrante,
			Integer tipo_llamado_saliente, Integer tipo_atencion_seccional,
			Integer tipo_otros, Integer motivo_otros, Integer categoria_consulta,
			Integer categoria_reclamo, Integer categoria_queja,
			Integer categoria_sugerencia, Integer categoria_felicitacion,
			Integer estado_pendiente, Integer estado_derivado, Integer estado_cerrado) {
		
		super();
		this.sector = sector;
		this.tipo_llamado_entrante = tipo_llamado_entrante;
		this.tipo_llamado_saliente = tipo_llamado_saliente;
		this.tipo_atencion_seccional = tipo_atencion_seccional;
		this.tipo_otros = tipo_otros;
		this.motivo_otros = motivo_otros;
		this.categoria_consulta = categoria_consulta;
		this.categoria_reclamo = categoria_reclamo;
		this.categoria_queja = categoria_queja;
		this.categoria_sugerencia = categoria_sugerencia;
		this.categoria_felicitacion = categoria_felicitacion;
		this.estado_pendiente = estado_pendiente;
		this.estado_derivado = estado_derivado;
		this.estado_cerrado = estado_cerrado;
	}
	
	public CRMEstadistica(String sector, Integer tipo_llamado_entrante,
			Integer tipo_llamado_saliente, Integer tipo_atencion_seccional,
			Integer tipo_otros, Integer motivo_otros, Integer categoria_consulta,
			Integer categoria_reclamo, Integer categoria_queja,
			Integer categoria_sugerencia, Integer categoria_felicitacion,
			Integer estado_pendiente, Integer estado_derivado, Integer estado_cerrado,Integer tipo_whatsapp_entrante) {
		
		super();
		this.sector = sector;
		this.tipo_llamado_entrante = tipo_llamado_entrante;
		this.tipo_llamado_saliente = tipo_llamado_saliente;
		this.tipo_atencion_seccional = tipo_atencion_seccional;
		this.tipo_otros = tipo_otros;
		this.motivo_otros = motivo_otros;
		this.categoria_consulta = categoria_consulta;
		this.categoria_reclamo = categoria_reclamo;
		this.categoria_queja = categoria_queja;
		this.categoria_sugerencia = categoria_sugerencia;
		this.categoria_felicitacion = categoria_felicitacion;
		this.estado_pendiente = estado_pendiente;
		this.estado_derivado = estado_derivado;
		this.estado_cerrado = estado_cerrado;
		this.tipo_whatsapp_entrante=tipo_whatsapp_entrante;
	}
	
	@Override
	public String toString() {
		return "CRMEstadistica [sector=" +sector
				+ ", tipo_llamado_entrante=" + tipo_llamado_entrante
				+ ", tipo_llamado_saliente=" + tipo_llamado_saliente
				+ ", tipo_atencion_seccional=" + tipo_atencion_seccional
				+ ", tipo_otros=" + tipo_otros + ",tipo_whatsapp_entrante="+ tipo_whatsapp_entrante + ", motivo_otros="
				+ motivo_otros + ", categoria_consulta=" + categoria_consulta
				+ ", categoria_reclamo=" + categoria_reclamo
				+ ", categoria_queja=" + categoria_queja
				+ ", categoria_sugerencia=" + categoria_sugerencia
				+ ", categoria_felicitacion=" + categoria_felicitacion
				+ ", estado_pendiente=" + estado_pendiente
				+ ", estado_derivado=" + estado_derivado + ", estado_cerrado="
				+ estado_cerrado + "]";
	}
	
	public static CRMEstadistica getMapping(String prefix, ResultSet rs) throws SQLException{
		
		CRMEstadistica crmEst = new CRMEstadistica();
		
		crmEst.setSector(rs.getString(prefix + "sector"));
		crmEst.setCategoria_consulta(rs.getInt(prefix + "categoria_consulta"));
		crmEst.setCategoria_felicitacion(rs.getInt(prefix + "categoria_felicitacion"));
		crmEst.setCategoria_queja(rs.getInt(prefix + "categoria_queja"));
		crmEst.setCategoria_reclamo(rs.getInt(prefix + "categoria_reclamo"));
		crmEst.setCategoria_sugerencia(rs.getInt(prefix + "categoria_sugerencia"));
		crmEst.setEstado_cerrado(rs.getInt(prefix + "estado_cerrado"));
		crmEst.setEstado_derivado(rs.getInt(prefix + "estado_derivado"));
		crmEst.setEstado_pendiente(rs.getInt(prefix + "estado_pendiente"));
		crmEst.setMotivo_otros(rs.getInt(prefix + "motivo_otros"));
		crmEst.setTipo_atencion_seccional(rs.getInt(prefix + "tipo_atencion_seccional") );
		crmEst.setTipo_llamado_entrante(rs.getInt(prefix + "tipo_llamado_entrante"));
		crmEst.setTipo_llamado_saliente(rs.getInt(prefix + "tipo_llamado_saliente"));
		crmEst.setTipo_otros(rs.getInt(prefix + "tipo_otros"));
		crmEst.setTipo_whatsapp_entrante(rs.getInt(prefix + "tipo_whatsapp_entrante"));
	
		return crmEst;
		
	}
	
}

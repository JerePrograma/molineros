package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ConsultaIGS implements Serializable {

   /**
	 * 
	 */
	private static final long serialVersionUID = 550156413369786695L;
	
	private String cuilParametro;
	private String idOspimParam;
	private Integer inteParam;
	private String docuTipoParam;
	private String docuNumeroParam;
	private BigDecimal nroCredencialParam;
	private String cuilTitular;
	private String apellido;
	private String nombre;
	private String docuTipo;
	private String docuNumero;
	private Integer idOspim;
	private Integer inte;
	private String plan;
	private String telefono;
	private String localidad;
	private String provincia;
	private Date altaFecha;
	private String ip;
	private BigDecimal nroCredencial;
	private String estado;
	  
	public String getCuilParametro() {
		return cuilParametro;
	}
	public void setCuilParametro(String cuilParametro) {
		this.cuilParametro = cuilParametro;
	}
	public String getIdOspimParam() {
		return idOspimParam;
	}
	public void setIdOspimParam(String idOspimParam) {
		this.idOspimParam = idOspimParam;
	}
	public Integer getInteParam() {
		return inteParam;
	}
	public void setInteParam(Integer inteParam) {
		this.inteParam = inteParam;
	}
	public String getDocuTipoParam() {
		return docuTipoParam;
	}
	public void setDocuTipoParam(String docuTipoParam) {
		this.docuTipoParam = docuTipoParam;
	}
	public String getDocuNumeroParam() {
		return docuNumeroParam;
	}
	public void setDocuNumeroParam(String docuNumeroParam) {
		this.docuNumeroParam = docuNumeroParam;
	}
	public BigDecimal getNroCredencialParam() {
		return nroCredencialParam;
	}
	public void setNroCredencialParam(BigDecimal nroCredencialParam) {
		this.nroCredencialParam = nroCredencialParam;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
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
	public String getDocuTipo() {
		return docuTipo;
	}
	public void setDocuTipo(String docuTipo) {
		this.docuTipo = docuTipo;
	}
	public String getDocuNumero() {
		return docuNumero;
	}
	public void setDocuNumero(String docuNumero) {
		this.docuNumero = docuNumero;
	}
	public Integer getIdOspim() {
		return idOspim;
	}
	public void setIdOspim(Integer idOspim) {
		this.idOspim = idOspim;
	}
	public Integer getInte() {
		return inte;
	}
	public void setInte(Integer inte) {
		this.inte = inte;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public Date getAltaFecha() {
		return altaFecha;
	}
	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public BigDecimal getNroCredencial() {
		return nroCredencial;
	}
	public void setNroCredencial(BigDecimal nroCredencial) {
		this.nroCredencial = nroCredencial;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	  
	  
	public static ConsultaIGS getMapping(String prefix, ResultSet rs) throws SQLException{
		
		ConsultaIGS cIGS = new ConsultaIGS();
		
		cIGS.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		cIGS.setApellido(rs.getString(prefix + "apellido"));
		cIGS.setCuilParametro(rs.getString(prefix + "cuil_parametro"));
		cIGS.setCuilTitular(rs.getString(prefix + "cuil_titular"));
		cIGS.setDocuNumero(rs.getString(prefix + "docu_numero"));
		cIGS.setDocuNumeroParam(rs.getString(prefix + "docu_numero_param"));
		cIGS.setDocuTipo(rs.getString(prefix + "docu_tipo"));
		cIGS.setDocuTipoParam(rs.getString(prefix + "docu_tipo_param"));
		cIGS.setEstado(rs.getString(prefix + "estado"));
		cIGS.setIdOspim(rs.getInt(prefix + "id_ospim"));
		cIGS.setIdOspimParam(rs.getString(prefix + "id_ospim_param"));
		cIGS.setInte(rs.getInt(prefix + "inte"));
		cIGS.setInteParam(rs.getInt(prefix + "inte_param"));
		cIGS.setIp(rs.getString(prefix + "ip"));
		cIGS.setLocalidad(rs.getString(prefix + "localidad"));
		cIGS.setNombre(rs.getString(prefix + "nombre"));
		cIGS.setNroCredencial(rs.getBigDecimal(prefix + "nro_credencial"));
		cIGS.setNroCredencialParam(rs.getBigDecimal(prefix + "nro_credencial_param"));
		cIGS.setPlan(rs.getString(prefix + "plan"));
		cIGS.setProvincia(rs.getString(prefix + "provincia"));
		cIGS.setTelefono(rs.getString(prefix + "telefono"));

		return cIGS;
		
	}

}

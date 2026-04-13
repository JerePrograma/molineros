package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AfiliadoCambioCuil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6226724549505140092L;
	
	private String cuil_titular;
	private int inte;
	private String cuil;
	private String documento_tipo;
	private String documento_numero;
	private Date vigen_fecha;
	private String cuil_titular_anterior;
	private int inte_anterior;
	private String cuil_anterior;
	private String documento_tipo_anterior;
	private String documento_numero_anterior;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	  
	public AfiliadoCambioCuil(){
		super();
	}
	
	public AfiliadoCambioCuil(String cuilTitular, int inte, String cuil, String docTipo, String docNro,
						Date fechaVigencia, String cuilTitularAnt, int inteAnt, String cuilAnt, String docTipoAnt, 
						String docNroAnt, Date altaFecha, String altaUsr, Date modiFecha, String modiUsr, Date bajaFecha, String bajaUsr){
		super();
		this.cuil_titular = cuilTitular;
		this.inte = inte;
		this.cuil = cuil;
		this.documento_tipo = docTipo ;
		this.documento_numero = docNro;
		this.vigen_fecha = fechaVigencia;
		this.cuil_titular_anterior = cuilTitularAnt;
		this.inte_anterior = inteAnt;
		this.cuil_anterior = cuilAnt;
		this.documento_tipo_anterior = docTipoAnt ;
		this.documento_numero_anterior = docNroAnt;
		this.alta_fecha = altaFecha;
		this.alta_usr = altaUsr;
		this.modi_fecha = modiFecha;
		this.modi_usr = modiUsr;
		this.baja_fecha = bajaFecha;
		this.baja_usr = bajaUsr;
	}
	
	public String getCuil_titular() {
		return cuil_titular;
	}
	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public String getDocumento_tipo() {
		return documento_tipo;
	}
	public void setDocumento_tipo(String documento_tipo) {
		this.documento_tipo = documento_tipo;
	}
	public String getDocumento_numero() {
		return documento_numero;
	}
	public void setDocumento_numero(String documento_numero) {
		this.documento_numero = documento_numero;
	}
	public Date getVigen_fecha() {
		return vigen_fecha;
	}
	public void setVigen_fecha(Date vigen_fecha) {
		this.vigen_fecha = vigen_fecha;
	}
	public String getCuil_titular_anterior() {
		return cuil_titular_anterior;
	}
	public void setCuil_titular_anterior(String cuil_titular_anterior) {
		this.cuil_titular_anterior = cuil_titular_anterior;
	}
	public int getInte_anterior() {
		return inte_anterior;
	}
	public void setInte_anterior(int inte_anterior) {
		this.inte_anterior = inte_anterior;
	}
	public String getCuil_anterior() {
		return cuil_anterior;
	}
	public void setCuil_anterior(String cuil_anterior) {
		this.cuil_anterior = cuil_anterior;
	}
	public String getDocumento_tipo_anterior() {
		return documento_tipo_anterior;
	}
	public void setDocumento_tipo_anterior(String documento_tipo_anterior) {
		this.documento_tipo_anterior = documento_tipo_anterior;
	}
	public String getDocumento_numero_anterior() {
		return documento_numero_anterior;
	}
	public void setDocumento_numero_anterior(String documento_numero_anterior) {
		this.documento_numero_anterior = documento_numero_anterior;
	}
	public Date getAlta_fecha() {
		return alta_fecha;
	}
	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}
	public String getAlta_usr() {
		return alta_usr;
	}
	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}
	public Date getModi_fecha() {
		return modi_fecha;
	}
	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}
	public String getModi_usr() {
		return modi_usr;
	}
	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}
	public Date getBaja_fecha() {
		return baja_fecha;
	}
	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}
	public String getBaja_usr() {
		return baja_usr;
	}
	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}
	  
	
	public static AfiliadoCambioCuil getMapping(ResultSet rs, String prefix) throws SQLException{
		
		AfiliadoCambioCuil acc = new AfiliadoCambioCuil();
		
		acc.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		acc.setInte(rs.getInt(prefix + "inte"));
		acc.setCuil(rs.getString(prefix + "cuil"));
		acc.setDocumento_tipo(rs.getString(prefix + "documento_tipo"));
		acc.setDocumento_numero(rs.getString(prefix + "documento_numero"));
		acc.setVigen_fecha(rs.getDate(prefix + "vigen_fecha"));
		acc.setCuil_titular_anterior(rs.getString(prefix + "cuil_titular_anterior"));
		acc.setInte_anterior(rs.getInt(prefix + "inte_anterior"));
		acc.setCuil_anterior(rs.getString(prefix + "cuil_anterior"));
		acc.setDocumento_tipo_anterior(rs.getString(prefix + "documento_tipo_anterior"));
		acc.setDocumento_numero_anterior(rs.getString(prefix + "documento_numero_anterior"));
		acc.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		acc.setAlta_usr(rs.getString(prefix + "alta_usr"));
		acc.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		acc.setModi_usr(rs.getString(prefix + "modi_usr"));
		acc.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		acc.setBaja_usr(rs.getString(prefix + "baja_usr"));
		
		return acc;
	}
	
}



 

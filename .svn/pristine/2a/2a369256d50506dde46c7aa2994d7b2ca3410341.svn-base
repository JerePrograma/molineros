package ar.com.ospim.novedades.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class NovedadPadronConsolidadoBajas {
	
	
	private int idNovedad ;
	private String cuilTitular;
	private int inte;
	private String codigoMovimiento;
	private Date procesoFecha;
	private String procesoUsr ;
	private Date vigenFecha;
	private Date bajaFecha;
	private int idMotivo;
	private int idRevista;
	private int idCategoria;
	private String cuit;
	private String motivoBajaDesc;
	private String revistaDesc;
	private String categoria;
	private String razonSocial;
	
	
	
	public int getIdNovedad() {
		return idNovedad;
	}
	public void setIdNovedad(int idNovedad) {
		this.idNovedad = idNovedad;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
	public String getCodigoMovimiento() {
		return codigoMovimiento;
	}
	public void setCodigoMovimiento(String codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}
	public Date getProcesoFecha() {
		return procesoFecha;
	}
	public void setProcesoFecha(Date procesoFecha) {
		this.procesoFecha = procesoFecha;
	}
	public String getProcesoUsr() {
		return procesoUsr;
	}
	public void setProcesoUsr(String procesoUsr) {
		this.procesoUsr = procesoUsr;
	}
	public Date getVigenFecha() {
		return vigenFecha;
	}
	public void setVigenFecha(Date vigenFecha) {
		this.vigenFecha = vigenFecha;
	}
	public Date getBajaFecha() {
		return bajaFecha;
	}
	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	public int getIdMotivo() {
		return idMotivo;
	}
	public void setIdMotivo(int idMotivo) {
		this.idMotivo = idMotivo;
	}
	public int getIdRevista() {
		return idRevista;
	}
	public void setIdRevista(int idRevista) {
		this.idRevista = idRevista;
	}
	public int getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	
	public String getMotivoBajaDesc() {
		return motivoBajaDesc;
	}
	public void setMotivoBajaDesc(String motivoBajaDesc) {
		this.motivoBajaDesc = motivoBajaDesc;
	}
	
	public String getRevistaDesc() {
		return revistaDesc;
	}
	public void setRevistaDesc(String revistaDesc) {
		this.revistaDesc = revistaDesc;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
	public static NovedadPadronConsolidadoBajas getMapping(ResultSet rs) throws SQLException{
		
		NovedadPadronConsolidadoBajas nov = new NovedadPadronConsolidadoBajas();
		
		nov.setIdNovedad(rs.getInt( "id_novedad"));
		nov.setCodigoMovimiento(rs.getString("codigo_movimiento"));
		nov.setProcesoFecha(rs.getDate("periodo_novedad"));
		nov.setInte(rs.getInt( "inte"));
		nov.setCuilTitular(rs.getString("cuil_titular"));	
		nov.setVigenFecha(rs.getDate("vigen_fecha"));
		nov.setBajaFecha(rs.getDate("baja_fecha"));
		nov.setIdMotivo(rs.getInt("id_motivo"));
		nov.setIdRevista(rs.getInt("id_revista"));
		nov.setIdCategoria(rs.getInt("id_categoria"));
		nov.setCuit(rs.getString("cuit"));
		nov.setMotivoBajaDesc(rs.getString("motivo_baja_desc"));
		nov.setRevistaDesc(rs.getString("revista_desc"));
		nov.setCategoria(rs.getString("categoria"));
		nov.setRazonSocial(rs.getString("razon_soc"));


		return nov;
	}
	
	
}

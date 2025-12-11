package ar.com.ospim.novedades.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class NovedadPadronConsolidadoInconsistencia {

	private int id ;
	private String cuilTitular;
	private int inte;
	private String codigoMovimiento;
	private Date procesoFecha;
	private String procesoUsr ;
	private String tipoDocumento;
	private String numeroDocumento;
	private String nombre;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	
	public String getCodigoMovimiento() {
		return codigoMovimiento;
	}
	public void setCodigoMovimiento(String codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
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
	

	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	


	public static NovedadPadronConsolidadoInconsistencia getMapping(ResultSet rs) throws SQLException{
		
		NovedadPadronConsolidadoInconsistencia nov = new NovedadPadronConsolidadoInconsistencia();
		
		nov.setId(rs.getInt( "id_novedad"));
		nov.setCodigoMovimiento(rs.getString("codigo_movimiento"));
		nov.setProcesoFecha(rs.getDate("periodo_novedad"));
		nov.setInte(rs.getInt( "inte"));
		nov.setCuilTitular(rs.getString("cuil_titular"));	
		nov.setTipoDocumento(rs.getString("documento_tipo"));
		nov.setNumeroDocumento(rs.getString("docu_numero"));
		nov.setNombre(rs.getString("nombre"));

		
		return nov;
	}
	
}

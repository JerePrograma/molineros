package ar.com.ospim.afiliados.reportes.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteLegajosCred extends ReporteCredenResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4671535599961904823L;
	
	private int idLote;
	private Date fechaProceso;
	private int mes;
	private int anio;
	private String cuilTitular;
	private int inte;
	private int idCorrespondencia;
	private Date fechaImpresionCred;
	private Date fechaIngreso;

	public int getIdLote() {
		return idLote;
	}
	
	public void setIdLote(int idLote) {
		this.idLote = idLote;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
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

	public int getIdCorrespondencia() {
		return idCorrespondencia;
	}

	public void setIdCorrespondencia(int idCorrespondencia) {
		this.idCorrespondencia = idCorrespondencia;
	}

	public Date getFechaImpresionCred() {
		return fechaImpresionCred;
	}

	public void setFechaImpresionCred(Date fechaImpresionCred) {
		this.fechaImpresionCred = fechaImpresionCred;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public static ReporteLegajosCred getMapping(ResultSet rs)throws SQLException {
		
		ReporteLegajosCred res = new ReporteLegajosCred();
//		res.setAnio(rs.getInt("anio"));
		res.setApellido(rs.getString("apellido"));
		res.setCuilTitular(rs.getString("cuil_titular"));
//		res.setFechaAlta(fechaAlta)
		res.setFechaImpresionCred(rs.getDate("fecha_impresion_cred"));
		res.setFechaIngreso(rs.getDate("fecha_ingreso"));
		res.setIdCorrespondencia(rs.getInt("id_correspondencia"));
//		res.setIdLote(rs.getInt("id_lote"));
		res.setInte(rs.getInt("inte"));
//		res.setMes(rs.getInt("mes"));
		res.setNombre(rs.getString("nombre"));
		res.setPlan(rs.getString("plan"));
		res.setSeccional(rs.getString("seccional"));

		return res;
	}

	
}

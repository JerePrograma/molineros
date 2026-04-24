package ar.com.ospim.afip.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteDeudaEmpresaCab implements Serializable {
		
	private static final long serialVersionUID = 608967240021831430L;
	
	private int id;
	private String usuario;
	private Date fechaProceso;
	private Date fechaDesdeParam;
	private Date fechaHastaParam;
	private int ramoDesdeParam;
	private int ramoHastaParam;
	private boolean agrupaXRemunerParam;
	private boolean empresasSinDeudaParam;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public Date getFechaProceso() {
		return fechaProceso;
	}
	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}
	public Date getFechaDesdeParam() {
		return fechaDesdeParam;
	}
	public void setFechaDesdeParam(Date fechaDesdeParam) {
		this.fechaDesdeParam = fechaDesdeParam;
	}
	public Date getFechaHastaParam() {
		return fechaHastaParam;
	}
	public void setFechaHastaParam(Date fechaHastaParam) {
		this.fechaHastaParam = fechaHastaParam;
	}
	public int getRamoDesdeParam() {
		return ramoDesdeParam;
	}
	public void setRamoDesdeParam(int ramoDesdeParam) {
		this.ramoDesdeParam = ramoDesdeParam;
	}
	public int getRamoHastaParam() {
		return ramoHastaParam;
	}
	public void setRamoHastaParam(int ramoHastaParam) {
		this.ramoHastaParam = ramoHastaParam;
	}
	public boolean isAgrupaXRemunerParam() {
		return agrupaXRemunerParam;
	}
	public void setAgrupaXRemunerParam(boolean agrupaXRemunerParam) {
		this.agrupaXRemunerParam = agrupaXRemunerParam;
	}
	public boolean isEmpresasSinDeudaParam() {
		return empresasSinDeudaParam;
	}
	public void setEmpresasSinDeudaParam(boolean empresasSinDeudaParam) {
		this.empresasSinDeudaParam = empresasSinDeudaParam;
	}
	
	public static ReporteDeudaEmpresaCab getMapping(ResultSet rs) throws SQLException {
		
		ReporteDeudaEmpresaCab cab = new ReporteDeudaEmpresaCab();
			cab.setId(rs.getInt("id"));
			cab.setUsuario(rs.getString("usuario"));
			cab.setFechaProceso(rs.getDate("fecha_solicitado"));
			cab.setFechaDesdeParam(rs.getDate("fecha_desde_param"));
			cab.setFechaHastaParam(rs.getDate("fecha_hasta_param"));
			cab.setRamoDesdeParam(rs.getInt("ramo_desde_param"));
			cab.setRamoHastaParam(rs.getInt("ramo_hasta_param"));
			cab.setAgrupaXRemunerParam(rs.getBoolean("agrupa_x_remun_param"));
			cab.setEmpresasSinDeudaParam(rs.getBoolean("empresa_sin_deuda_param"));	
			
		return cab;
	}
}

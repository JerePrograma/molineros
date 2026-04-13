package ar.com.ospim.afiliados.reportes.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteNovedadesSSSProcesadasCab implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3136412876372282046L;
	private int id;
	private String usuario;
	private Date fechaSolicitado;
	private Date fechaProceso;
	private Date fechaNovedades;
	private Date fechaPadronInicio;
	private Date fechaPadronFinal;
	private boolean informar;
	private Date bajaFecha;
	private String bajaUsr;
	
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

	public Date getFechaNovedades() {
		return fechaNovedades;
	}

	public void setFechaNovedades(Date fechaNovedades) {
		this.fechaNovedades = fechaNovedades;
	}

	public Date getFechaPadronInicio() {
		return fechaPadronInicio;
	}

	public void setFechaPadronInicio(Date fechaPadronInicio) {
		this.fechaPadronInicio = fechaPadronInicio;
	}

	public Date getFechaPadronFinal() {
		return fechaPadronFinal;
	}

	public void setFechaPadronFinal(Date fechaPadronFinal) {
		this.fechaPadronFinal = fechaPadronFinal;
	}

	public boolean isInformar() {
		return informar;
	}

	public void setInformar(boolean informar) {
		this.informar = informar;
	}

	public Date getFechaSolicitado() {
		return fechaSolicitado;
	}

	public void setFechaSolicitado(Date fechaSolicitado) {
		this.fechaSolicitado = fechaSolicitado;
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
	
	public static ReporteNovedadesSSSProcesadasCab getMapping(String prefix, ResultSet rs) throws SQLException {
		
		ReporteNovedadesSSSProcesadasCab cab = new ReporteNovedadesSSSProcesadasCab();
			cab.setId(rs.getInt(prefix +"id"));
			cab.setUsuario(rs.getString(prefix +"usuario_solicito"));
			cab.setFechaSolicitado(rs.getTimestamp(prefix +"fecha_solicitado"));
			cab.setFechaProceso(rs.getDate(prefix +"fecha_proceso"));
			cab.setFechaNovedades(rs.getDate(prefix +"fecha_novedad"));
			cab.setFechaPadronInicio(rs.getDate(prefix +"fecha_padron_inicial"));
			cab.setFechaPadronFinal(rs.getDate(prefix +"fecha_padron_final"));
			cab.setInformar(rs.getBoolean(prefix +"informar"));
			cab.setBajaFecha(rs.getTimestamp(prefix +"baja_fecha"));
			cab.setBajaUsr(rs.getString(prefix +"baja_usr"));
			
		return cab;
	}

}

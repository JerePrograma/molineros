package ar.com.ospim.afiliados.reportes.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteNovedadesSSSProcesadasDet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3136412876372282046L;
	private int id;
	private int idCab;
	private int poblacion;
	private int totalNovedadesSSS;
	private int totalNovedadesAcumuladas;
	private int totalNovedadesAProcesar;
	private int totalNovedadesResueltas;
	private int totalNovedadesInconsistentes;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdCab() {
		return idCab;
	}

	public void setIdCab(int idCab) {
		this.idCab = idCab;
	}

	public int getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(int poblacion) {
		this.poblacion = poblacion;
	}

	public int getTotalNovedadesAcumuladas() {
		return totalNovedadesAcumuladas;
	}

	public void setTotalNovedadesAcumuladas(int totalNovedadesAcumuladas) {
		this.totalNovedadesAcumuladas = totalNovedadesAcumuladas;
	}

	public int getTotalNovedadesAProcesar() {
		return totalNovedadesAProcesar;
	}

	public void setTotalNovedadesAProcesar(int totalNovedadesAProcesar) {
		this.totalNovedadesAProcesar = totalNovedadesAProcesar;
	}

	public int getTotalNovedadesResueltas() {
		return totalNovedadesResueltas;
	}	

	public void setTotalNovedadesResueltas(int totalNovedadesResueltas) {
		this.totalNovedadesResueltas = totalNovedadesResueltas;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altafecha) {
		this.altaFecha = altafecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modifecha) {
		this.modiFecha = modifecha;
	}

	public String getModiUsr() {
		return modiUsr;
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajafecha) {
		this.bajaFecha = bajafecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public static ReporteNovedadesSSSProcesadasDet getMapping(String prefix, ResultSet rs) throws SQLException {
		
		ReporteNovedadesSSSProcesadasDet det = new ReporteNovedadesSSSProcesadasDet();
			det.setId(rs.getInt(prefix +"id"));
			det.setIdCab(rs.getInt(prefix +"id_cab"));
			det.setPoblacion(rs.getInt(prefix +"poblacion"));
			det.setTotalNovedadesSSS(rs.getInt(prefix +"total_novedades_sss"));
			det.setTotalNovedadesAcumuladas(rs.getInt(prefix +"total_novedades_acumuladas"));
			det.setTotalNovedadesAProcesar(rs.getInt(prefix +"total_novedades_a_procesar"));
			det.setTotalNovedadesResueltas(rs.getInt(prefix +"total_novedades_resueltas"));
			det.setAltaFecha(rs.getTimestamp(prefix +"alta_fecha"));  
			det.setAltaUsr(rs.getString(prefix +"alta_usr"));
			det.setModiFecha(rs.getTimestamp(prefix +"modi_fecha"));
			det.setModiUsr(rs.getString(prefix +"modi_usr"));
			det.setBajaFecha(rs.getTimestamp(prefix +"baja_fecha"));
			det.setBajaUsr(rs.getString(prefix +"baja_usr"));	
			det.setTotalNovedadesInconsistentes(rs.getInt(prefix + "total_inconsistencia"));
		return det;
	}

	public int getTotalNovedadesSSS() {
		return totalNovedadesSSS;
	}

	public void setTotalNovedadesSSS(int totalNovedadesSSS) {
		this.totalNovedadesSSS = totalNovedadesSSS;
	}

	public int getTotalNovedadesInconsistentes() {
		return totalNovedadesInconsistentes;
	}

	public void setTotalNovedadesInconsistentes(int totalNovedadesInconsistentes) {
		this.totalNovedadesInconsistentes = totalNovedadesInconsistentes;
	}
}

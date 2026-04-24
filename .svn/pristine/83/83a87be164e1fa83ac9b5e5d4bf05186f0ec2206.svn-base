package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.autorizaciones.beans.Nomenclador;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:25:15 p.m.
 */
public class Especialidad {

	private int id_especialidad;
	private int id_tipo_nomenclador;
	private String descripcion;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private TipoNomenclador tipoNomenclador;
	private String tipoEspecialidad;

	public Especialidad(){
	}

	/**
	 * @return the id_especialidad
	 */
	public int getId_especialidad() {
		return id_especialidad;
	}

	/**
	 * @param idEspecialidad the id_especialidad to set
	 */
	public void setId_especialidad(int idEspecialidad) {
		id_especialidad = idEspecialidad;
	}

	/**
	 * @return the id_tipo_nomenclador
	 */
	public int getId_tipo_nomenclador() {
		return id_tipo_nomenclador;
	}

	/**
	 * @param idTipoNomenclador the id_tipo_nomenclador to set
	 */
	public void setId_tipo_nomenclador(int idTipoNomenclador) {
		id_tipo_nomenclador = idTipoNomenclador;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * @return the alta_fecha
	 */
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	/**
	 * @param altaFecha the alta_fecha to set
	 */
	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	/**
	 * @return the alta_usr
	 */
	public String getAlta_usr() {
		return alta_usr;
	}

	/**
	 * @param altaUsr the alta_usr to set
	 */
	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	/**
	 * @return the modi_fecha
	 */
	public Date getModi_fecha() {
		return modi_fecha;
	}

	/**
	 * @param modiFecha the modi_fecha to set
	 */
	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	/**
	 * @return the modi_usr
	 */
	public String getModi_usr() {
		return modi_usr;
	}

	/**
	 * @param modiUsr the modi_usr to set
	 */
	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	/**
	 * @return the baja_fecha
	 */
	public Date getBaja_fecha() {
		return baja_fecha;
	}

	/**
	 * @param bajaFecha the baja_fecha to set
	 */
	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	/**
	 * @return the baja_usr
	 */
	public String getBaja_usr() {
		return baja_usr;
	}

	/**
	 * @param bajaUsr the baja_usr to set
	 */
	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	/**
	 * @return the tipoNomenclador
	 */
	public TipoNomenclador getTipoNomenclador() {
		return tipoNomenclador;
	}

	/**
	 * @param tipoNomenclador the tipoNomenclador to set
	 */
	public void setTipoNomenclador(TipoNomenclador tipoNomenclador) {
		this.tipoNomenclador = tipoNomenclador;
	}
	
	public String getTipoEspecialidad() {
		return tipoEspecialidad;
	}

	public void setTipoEspecialidad(String tipoEspecialidad) {
		this.tipoEspecialidad = tipoEspecialidad;
	}

	public static Especialidad getMapping(ResultSet rs) throws SQLException {
		
		Especialidad archivo = new Especialidad();
		archivo.setDescripcion(rs.getString("descripcion"));
		archivo.setId_especialidad(rs.getInt("id"));
		
		return archivo;
	}
	
}
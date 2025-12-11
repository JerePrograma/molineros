package ar.com.ospim.rrhh.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Carlos Rivas
 * @version 1.0
 * @created 21-Feb-2013 03:11 a.m.
 */
public class TarjetaAcceso implements Serializable {
	
	private static final long serialVersionUID = -1926053363012546744L;
	private int id;
	private int id_tarjeta_acceso;	
	private String apellido;
	private String nombre;
	private String entidad;
	private String sector;
	private String piso;
	private int legajo;	
	private double horas_jornada;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;

		
	private static Log _log = LogFactoryUtil.getLog(TarjetaAcceso.class);
	
	public TarjetaAcceso() {
		super();
	}
	
	public TarjetaAcceso(int id, int idTarjetaAcceso, String apellido,
			String nombre, String entidad, int legajo, double horasJornada, Date altaFecha,
			String altaUsr, Date modiFecha, String modiUsr, Date bajaFecha,
			String bajaUsr) {
		super();
		this.id = id;
		id_tarjeta_acceso = idTarjetaAcceso;
		this.apellido = apellido;
		this.nombre = nombre;
		this.entidad = entidad;
		this.legajo = legajo;
		horas_jornada = horasJornada;
		alta_fecha = altaFecha;
		alta_usr = altaUsr;
		modi_fecha = modiFecha;
		modi_usr = modiUsr;
		baja_fecha = bajaFecha;
		baja_usr = bajaUsr;
	}
	
	public TarjetaAcceso(int id, int idTarjetaAcceso, String apellido,
			String nombre, String entidad, String sector, String piso, 
			int legajo, double horasJornada, Date altaFecha, String altaUsr, 
			Date modiFecha, String modiUsr, Date bajaFecha, String bajaUsr) {
		super();
		this.id = id;
		id_tarjeta_acceso = idTarjetaAcceso;
		this.apellido = apellido;
		this.nombre = nombre;
		this.entidad = entidad;
		this.legajo = legajo;
		this.setSector(sector);
		this.setPiso(piso);
		horas_jornada = horasJornada;
		alta_fecha = altaFecha;
		alta_usr = altaUsr;
		modi_fecha = modiFecha;
		modi_usr = modiUsr;
		baja_fecha = bajaFecha;
		baja_usr = bajaUsr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_tarjeta_acceso() {
		return id_tarjeta_acceso;
	}

	public void setId_tarjeta_acceso(int idTarjetaAcceso) {
		id_tarjeta_acceso = idTarjetaAcceso;
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

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}
	
	public int getLegajo() {
		return legajo;
	}

	public void setLegajo(int legajo) {
		this.legajo = legajo;
	}

	public double getHoras_jornada() {
		return horas_jornada;
	}

	public void setHoras_jornada(double horasJornada) {
		horas_jornada = horasJornada;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}


	/*public static TarjetaAcceso getMapping2(ResultSet rs, String prefix)
			throws SQLException {
		TarjetaAcceso ta = new TarjetaAcceso();		
		ta.setId(rs.getInt(prefix + "id"));
		ta.setId_tarjeta_acceso(rs.getInt(prefix + "id_tarjeta_acceso"));
		ta.setNombre(rs.getString(prefix + "nombre"));
		ta.setApellido(rs.getString(prefix + "apellido"));
		ta.setEntidad(rs.getString(prefix + "entidad"));
		ta.setSector(rs.getString(prefix + "sector"));
		ta.setPiso(rs.getString(prefix + "piso"));		
		ta.setLegajo(rs.getInt(prefix + "legajo"));
		ta.setHoras_jornada(rs.getDouble(prefix + "horas_jornada"));
		ta.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ta.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ta.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ta.setModi_usr(rs.getString(prefix + "modi_usr"));
		ta.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ta.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return ta;
	}
*/

	
	
	public static TarjetaAcceso getMapping(ResultSet rs, String prefix)
			throws SQLException {
		TarjetaAcceso ta = new TarjetaAcceso();		
		ta.setId(rs.getInt(prefix + "id"));
		ta.setId_tarjeta_acceso(rs.getInt(prefix + "id_tarjeta_acceso"));
		ta.setNombre(rs.getString(prefix + "nombre"));
		ta.setApellido(rs.getString(prefix + "apellido"));		
		ta.setEntidad(rs.getString(prefix + "entidad"));
		ta.setLegajo(rs.getInt(prefix + "legajo"));
		ta.setHoras_jornada(rs.getDouble(prefix + "horas_jornada"));		
		ta.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ta.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ta.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ta.setModi_usr(rs.getString(prefix + "modi_usr"));
		ta.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ta.setBaja_usr(rs.getString(prefix + "baja_usr"));		
		try {
			ta.setPiso(rs.getString(prefix + "piso"));
			ta.setSector(rs.getString(prefix + "sector"));
		} catch (Exception e) {
			_log.error(e);
		}
		return ta;
	}
	public static ItemTarjetasTotal  getMappingBuscadorTotal(ResultSet rs, String prefix)
			throws SQLException {
		
		ItemTarjetasTotal ta = new ItemTarjetasTotal();		
		ta.setId(rs.getInt(prefix + "id"));
		ta.setId_tarjeta_acceso(rs.getInt(prefix + "id_tarjeta_acceso"));
		ta.setNombre(rs.getString(prefix + "nombre"));
		ta.setApellido(rs.getString(prefix + "apellido"));
		ta.setEntidad(rs.getString(prefix + "entidad"));
		ta.setSector(rs.getString(prefix + "sector"));
		ta.setPiso(rs.getString(prefix + "piso"));		
		ta.setLegajo(rs.getInt(prefix + "legajo"));
		ta.setHoras_jornada(rs.getDouble(prefix + "horas_jornada"));
		ta.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ta.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ta.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ta.setModi_usr(rs.getString(prefix + "modi_usr"));
		ta.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ta.setBaja_usr(rs.getString(prefix + "baja_usr"));
		ta.setTotal_registros(rs.getInt(prefix + "total"));
		return ta;
	}
	
		
	
	
}
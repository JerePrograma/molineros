package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.liquidaciones.beans.Especialidad;

/**
 * @author Federico Brachi
 * @version 1.0
 * @created 14-Jul-2010 03:30:13 p.m.
 */
public class Prestacion implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3135750853787717690L;
	
	private int id_prestacion;
	private int id_especialidad;
	private String descripcion;
	private int marca_rein_liq;
	private String observaciones;	
	private Date alta_fecha;
	private String alta_usr;	
	private Date modi_fecha;
	private String modi_usr;	
	private Date baja_fecha;
	private String baja_usr;
	private Especialidad especialidad;
	private String codigo;
	private BigDecimal importe;
	private int id_tipo_nomenclador;
	private BigDecimal honorarios;
	private BigDecimal gastos;

	
	
	private int id_reclamo_prestacion=0;
	private int id_prestacion_reclamo=0;
	
	public Prestacion(){}
	
	public Prestacion(int id_prestacion, String descripcion){
		this.id_prestacion=id_prestacion;
		this.descripcion=descripcion;
	}

	public int getId_prestacion() {
		return id_prestacion;
	}

	public String getId_prestacionString() {
		return String.valueOf(id_prestacion);
	}

	public int getId() {
		return id_prestacion;
	}
	
	public void setId_reclamo_prestacional(int idReclamoPrestacional ) {
		id_reclamo_prestacion= idReclamoPrestacional ;
	}
	
	public int getIdReclamopPrestacional() {
		return id_reclamo_prestacion;
	}
	
	
	public void setId_reclamo_prestacional_prestacion (int idPrestacionReclamoPrestacional) {
		id_prestacion_reclamo= idPrestacionReclamoPrestacional;
	}
	public int getIdPrestacionReclamoPrestacional() {
		return id_prestacion_reclamo;
	}
	
	
	
	public void setId_prestacion(int idPrestacion) {
		id_prestacion = idPrestacion;
	}

	public int getId_especialidad() {
		return id_especialidad;
	}

	public void setId_especialidad(int idEspecialidad) {
		id_especialidad = idEspecialidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getMarca_rein_liq() {
		return marca_rein_liq;
	}

	public void setMarca_rein_liq(int marcaReinLiq) {
		marca_rein_liq = marcaReinLiq;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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

	/**
	 * @return the especialidad
	 */
	public Especialidad getEspecialidad() {
		return especialidad;
	}

	/**
	 * @param especialidad the especialidad to set
	 */
	public void setEspecialidad(Especialidad especialidad) {
		this.especialidad = especialidad;
	}
	
	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public static Prestacion getMappingSimple(ResultSet rs, String prefix) throws SQLException {
		
		Prestacion prestacion = new Prestacion();
		prestacion.setId_prestacion(rs.getInt(prefix+"id_prestacion"));	
		prestacion.setDescripcion(rs.getString(prefix+"descripcion"));
		prestacion.setId_tipo_nomenclador(rs.getInt(prefix+"id_tipo_nomenclador"));
		
		return prestacion;
	}
	public static Prestacion getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}
	
	public static Prestacion getMapping(ResultSet rs, String prefix) throws SQLException {
		Prestacion prestacion = new Prestacion();				
		prestacion.setId_especialidad(rs.getInt(prefix+"id_especialidad")); 
		prestacion.setDescripcion(rs.getString(prefix+"descripcion"));
		prestacion.setMarca_rein_liq(rs.getInt(prefix+"marca_rein_liq"));
		prestacion.setObservaciones(rs.getString(prefix+"observaciones"));
		prestacion.setAlta_fecha(rs.getDate(prefix+"alta_fecha"));
		prestacion.setAlta_usr(rs.getString(prefix+"alta_usr"));  
		prestacion.setModi_fecha(rs.getDate(prefix+"modi_fecha"));
		prestacion.setModi_usr(rs.getString(prefix+"modi_usr"));  
		prestacion.setBaja_fecha(rs.getDate(prefix+"baja_fecha"));
		prestacion.setBaja_usr(rs.getString(prefix+"baja_usr"));
		prestacion.setCodigo(rs.getString(prefix+"codigo") != null ? rs.getString(prefix+"codigo") : "");
		
		
		try {
			prestacion.setId_reclamo_prestacional(rs.getInt(prefix+"id_reclamo_prestacional"));			
		} catch (Exception e){
			//DO nothing
		}
		
		
		try {
			prestacion.setId_reclamo_prestacional_prestacion(rs.getInt(prefix+"id_prestacion_reclamo_prestacion"));			
		} catch (Exception e){
			//DO nothing
		}
		 
		
		
		try {
			prestacion.setId_prestacion(rs.getInt(prefix+"id_prestacion"));			
		} catch (Exception e){
			//DO nothing
		}
		try {
			prestacion.setId_tipo_nomenclador(rs.getInt(prefix+"id_tipo_nomenclador"));
		} catch (Exception e){
			//DO nothing
		}
		try {
			prestacion.setImporte(rs.getBigDecimal(prefix+"importe") == null ? BigDecimal.ZERO : rs.getBigDecimal(prefix+"importe"));
		} catch (Exception e){
			//DO nothing
		}
		try {
			prestacion.setGastos(rs.getBigDecimal(prefix+"gastos") == null ? BigDecimal.ZERO : rs.getBigDecimal(prefix+"gastos"));
		} catch (Exception e){
			//DO nothing
		}
		try {
			prestacion.setHonorarios(rs.getBigDecimal(prefix+"honorarios") == null ? BigDecimal.ZERO : rs.getBigDecimal(prefix+"honorarios"));
		} catch (Exception e){
			//DO nothing
		}
		return prestacion;
	}

	/**
	 * @return the importe
	 */
	public BigDecimal getImporte() {
		return importe;
	}

	/**
	 * @param importe the importe to set
	 */
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public int getId_tipo_nomenclador() {
		return id_tipo_nomenclador;
	}

	public void setId_tipo_nomenclador(int idTipoNomenclador) {
		id_tipo_nomenclador = idTipoNomenclador;
	}

	public BigDecimal getHonorarios() {
		return honorarios;
	}

	public void setHonorarios(BigDecimal honorarios) {
		this.honorarios = honorarios;
	}

	public BigDecimal getGastos() {
		return gastos;
	}

	public void setGastos(BigDecimal gastos) {
		this.gastos = gastos;
	}	
}
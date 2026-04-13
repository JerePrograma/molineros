package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutorizacionesPmi implements Serializable{

	private static final long serialVersionUID = -5315008261173195191L;
	private int id_autorizaciones_pmi;
	private Date fecha;
	private static Date naci_fecha;
	private String apellido;
	private String nombre;
	private String cuil_titular;
	private int inte;
	private int nro_receta;
	private int id_ospim;
	private Date baja_fecha;
	private String docu_numero;
	private String documento_tipo;
	private Date baja_afi;
	private int id_seccional;
	private String descSecc;
	private String observaciones;
	
	public static AutorizacionesPmi getMapping(ResultSet rs) throws SQLException {
		AutorizacionesPmi archivo = new AutorizacionesPmi();
		archivo.setId_autorizaciones_pmi(rs.getInt("id_autorizacion_pmi"));
		archivo.setFecha(rs.getDate("fecha"));
		archivo.setNaci_fecha(rs.getDate("naci_fecha"));
		archivo.setApellido(rs.getString("apellido"));
		archivo.setNombre(rs.getString("nombre"));
		archivo.setCuil_titular(rs.getString("cuil_titular"));
		archivo.setInte(rs.getInt("inte"));
		archivo.setNro_receta(rs.getInt("nro_receta"));
		archivo.setId_ospim(rs.getInt("id_ospim"));
		archivo.setBaja_fecha(rs.getDate("baja_fecha"));
		archivo.setDocu_numero(rs.getString("docu_numero"));
		archivo.setDocumento_tipo(rs.getString("documento_tipo"));
		archivo.setBaja_afi(rs.getDate("baja_afi"));
		archivo.setId_seccional(rs.getInt("id_seccional"));
		archivo.setDescSecc(rs.getString("descripcion_secc"));
		archivo.setObservaciones(rs.getString("observaciones"));

		return archivo;
	}
	
	public final Date getFecha() {
		return fecha;
	}
	
	public String getFecha_string() {
	SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	return fecha != null ? sdf.format(fecha)
			: "";
	}
	
	public final String getCuil_titular() {
		return cuil_titular;
	}

	public final int getInte() {
		return inte;
	}
	
	public String getInte_string() {
		String inte_a_string = Integer.toString(getInte());
		return inte_a_string;
	}
	
	public final int getNro_receta() {
		return nro_receta;
	}
	
	public String getReceta_string() {
		String receta_a_string = Integer.toString(getNro_receta());
		return receta_a_string;
	}

	public final void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public final void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}

	public final void setInte(int inte) {
		this.inte = inte;
	}

	public final void setNro_receta(int nro_receta) {
		this.nro_receta = nro_receta;
	}
	
	public final String getApellido() {
		return apellido;
	}

	public final String getNombre() {
		return nombre;
	}

	public final void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public final void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public final static Date getNaci_fecha() {
		return naci_fecha;
	}
	
	public static String getFecha_naci_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		return sdf.format(naci_fecha);
	}

	public final void setNaci_fecha(Date naci_fecha) {
		AutorizacionesPmi.naci_fecha = naci_fecha;
	}
	
	public final int getId_autorizaciones_pmi() {
		return id_autorizaciones_pmi;
	}

	public final void setId_autorizaciones_pmi(int id_autorizaciones_pmi) {
		this.id_autorizaciones_pmi = id_autorizaciones_pmi;
	}
	
	public String getId_autorizacion_string() {
	String id_autorizaciones_pmi = Integer.toString(getId_autorizaciones_pmi());
	return id_autorizaciones_pmi;
	}

	public final int getId_ospim() {
		return id_ospim;
	}
	
	public String getId_ospimToString() {
		String id_ospim = Integer.toString(getId_ospim());
		return id_ospim;
	}

	public final void setId_ospim(int id_ospim) {
		this.id_ospim = id_ospim;
	}

	public final Date getBaja_fecha() {
		return baja_fecha;
	}
	
	public String getBaja_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return baja_fecha != null ? sdf.format(baja_fecha)
				: "";
		}

	public final void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public final String getDocu_numero() {
		return docu_numero;
	}
	
	public final String getDocumento_tipo() {
		return documento_tipo;
	}

	public final void setDocu_numero(String docu_numero) {
		this.docu_numero = docu_numero;
	}

	public final void setDocumento_tipo(String documento_tipo) {
		this.documento_tipo = documento_tipo;
	}

	public final Date getBaja_afi() {
		return baja_afi;
	}
	
	public String getBaja_afi_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return baja_afi != null ? sdf.format(baja_afi)
				: "";
		}

	public final void setBaja_afi(Date baja_afi) {
		this.baja_afi = baja_afi;
	}

	public final int getId_seccional() {
		return id_seccional;
	}
	
	public String getIdSeccToString() {
		String idSeccToString = Integer.toString(getId_seccional());
		return idSeccToString;
	}
	
	public final void setId_seccional(int id_seccional) {
		this.id_seccional = id_seccional;
	}

	public final String getDescSecc() {
		return descSecc;
	}

	public final void setDescSecc(String descSecc) {
		this.descSecc = descSecc;
	}
	
	public String getIdConcatNombSecc() {
		String idConcatNombSecc = getIdSeccToString() + "-" + getDescSecc();
		return idConcatNombSecc;
	}

	public final String getObservaciones() {
		return observaciones;
	}

	public final void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
}

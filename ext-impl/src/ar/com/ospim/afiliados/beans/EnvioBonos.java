package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fbrachi
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class EnvioBonos extends TipoBono {

//	private int tipo_bono;
	private int idEnvio;
//	private String tipo_bono_string;
	private int seccional;
	private String seccional_string;
	private Date fecha_envio;
	private int  bono_desde;
	private int bono_hasta;
	private int id_seccional;
	private Date fecha_rendido;
	private int cant_envio;
	private Date fecha_anulacion; 	
	
		
	public EnvioBonos(String tipoBonoString, int id_seccional, String seccional_string, Date fechaEnvio,
			int bonoDesde, int bonoHasta, int id_envio, Date fecha_rendido, int cant_envio ,Date fecha_anulacion  ) {
		super();
		tipo_bono_string = tipoBonoString;
		this.seccional_string = seccional_string;
		fecha_envio = fechaEnvio;
		bono_desde = bonoDesde;
		bono_hasta = bonoHasta;
		this.idEnvio=id_envio;
		this.id_seccional=id_seccional;
		this.fecha_rendido=fecha_rendido;				
		this.cant_envio=cant_envio;
		this.fecha_anulacion= fecha_anulacion;
	}

	public int getTipo_bono() {
		return tipo_bono;
	}

	public void setTipo_bono(int tipoBono) {
		tipo_bono = tipoBono;
	}

	public int getSeccional() {
		return seccional;
	}

	public void setSeccional(int seccional) {
		this.seccional = seccional;
	}
	
		
	public String getFecha_anulacion_string() {	 
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");		
		return fecha_anulacion!=null?sdf.format(fecha_anulacion):"";
	}

	public Date getFecha_anulacion() {
		return fecha_anulacion;
	}
	
	public void setFecha_anulacion(Date fechaAnulacion) {
		fecha_anulacion = fechaAnulacion;
	}
		
		
	public Date getFecha_envio() {
		return fecha_envio;
	}
	
	public String getFecha_envio_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");		
		return fecha_envio!=null?sdf.format(fecha_envio):"";
	}
	
	public String getFecha_rendido_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fecha_rendido!=null?sdf.format(fecha_rendido):"";
	}

	public void setFecha_envio(Date fechaEnvio) {
		fecha_envio = fechaEnvio;
	}

	public int getBono_desde() {
		return bono_desde;
	}

	public void setBono_desde(int bonoDesde) {
		bono_desde = bonoDesde;
	}

	public int getBono_hasta() {
		return bono_hasta;
	}

	public void setBono_hasta(int bonoHasta) {
		bono_hasta = bonoHasta;
	}

	public String getSeccional_string() {
		return seccional_string;
	}

	public void setSeccional_string(String seccionalString) {
		seccional_string = seccionalString;
	}
		
	public String getTipo_bono_string() {
		return tipo_bono_string;
	}

	public void setTipo_bono_string(String tipoBonoString) {
		tipo_bono_string = tipoBonoString;
	}

		public int getIdEnvio() {
		return idEnvio;
	}

	public void setIdEnvio(int idEnvio) {
		this.idEnvio = idEnvio;
	}

	public int getId_seccional() {
		return id_seccional;
	}

	public void setId_seccional(int idSeccional) {
		id_seccional = idSeccional;
	}

	public Date getFecha_rendido() {
		return fecha_rendido;
	}

	public void setFecha_rendido(Date fechaRendido) {
		fecha_rendido = fechaRendido;
	}

	public int getCant_envio() {
		return cant_envio;
	}

	public void setCant_envio(int cantEnvio) {
		cant_envio = cantEnvio;
	}
	
	public static EnvioBonos getMapping(ResultSet rs) throws SQLException{
		
		EnvioBonos bp = new EnvioBonos(rs.getString("tipo_bono"),
				rs.getInt("id_seccional"), 
				rs.getString("seccional"), 
				rs.getDate("fecha_envio"), 
				rs.getInt("nro_bono_desde"), 
				rs.getInt("nro_bono_hasta"), 
				rs.getInt("id_envio"), 
				rs.getDate("fecha_rendido"), 
				rs.getInt("cantidad"),
				rs.getDate("fecha_anulacion") 
				);		
		return bp;
	}
	
	
}
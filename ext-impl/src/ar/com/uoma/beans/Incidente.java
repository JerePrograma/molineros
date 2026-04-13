package ar.com.uoma.beans;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Seccional;



public class Incidente {
	private Date fecha;
	private Domicilio lugarIncidente;
	private Afiliado afiliado;
	private String detalleIncidente;
	private String seguimientoIncidenteNuevo;
	private List<SeguimientoIncidente> seguimientoIncidente;
	private int idSeccional;
	private String descripcionSeccional;
	private int idIncidente;
	private Date fechaRecepcion;
	
	public static Incidente getMappingIncidentes(ResultSet rs) throws Exception{
		Incidente incidente=new Incidente();
		Afiliado afiliado=new Afiliado();
		incidente.setFecha(rs.getDate("fecha"));
		afiliado.setCuil_titular(rs.getString("cuil_titular"));
		afiliado.setNombre(rs.getString("nombre"));
		afiliado.setApellido(rs.getString("apellido"));
		afiliado.setDocu_numero(rs.getString("nro_doc"));
		afiliado.setDocumento_tipo(rs.getString("docu_tipo"));
		afiliado.setBaja_fecha(rs.getDate("baja_fecha_afi"));
		Seccional seccional=new Seccional(rs.getInt("id_secc_afi"), rs.getString("descrip_secc_afi"));
		afiliado.setSeccional(seccional);
		incidente.setAfiliado(afiliado);
		incidente.setDetalleIncidente(rs.getString("detalle_incidente"));
		incidente.setIdSeccional(rs.getInt("id_seccional"));
		incidente.setDescripcionSeccional(rs.getString("seccional"));
		incidente.setIdIncidente(rs.getInt("id_incidente"));
		incidente.setFechaRecepcion(rs.getDate("fecha_recepcion"));		
		
		return incidente;		
	}
	
	public static Incidente getMappingIncidente(ResultSet rs) throws Exception{
		Incidente incidente=new Incidente();
		Afiliado afiliado=new Afiliado();
		incidente.setFecha(rs.getDate("fecha"));
		afiliado.setCuil_titular(rs.getString("cuil_titular"));
		afiliado.setNombre(rs.getString("nombre"));
		afiliado.setApellido(rs.getString("apellido"));
		afiliado.setDocu_numero(rs.getString("nro_doc"));
		afiliado.setDocumento_tipo(rs.getString("docu_tipo"));
		afiliado.setBaja_fecha(rs.getDate("baja_fecha"));
		Seccional seccional=new Seccional(rs.getInt("id_secc_afi"), rs.getString("descrip_secc_afi"));
		afiliado.setSeccional(seccional);
		incidente.setAfiliado(afiliado);
		incidente.setDetalleIncidente(rs.getString("detalle_incidente"));
		incidente.setIdSeccional(rs.getInt("id_seccional"));
		incidente.setDescripcionSeccional(rs.getString("seccional"));
		incidente.setIdIncidente(rs.getInt("id_incidente"));		
		Domicilio domicilio= new Domicilio();		
		domicilio.setId_domicilio(rs.getInt("id_domicilio"));
		domicilio.setProvinciaId(rs.getInt("id_provincia"));
		domicilio.setLocalidadId(rs.getInt("id_localidad"));
		domicilio.setCalle(rs.getString("calle"));
		domicilio.setNumero(rs.getString("numero"));
		domicilio.setPiso(rs.getString("piso"));
		domicilio.setDepto(rs.getString("depto"));
		domicilio.setPostal_codi(rs.getString("cod_postal"));
		domicilio.setObservaciones(rs.getString("observacion"));
		incidente.setFechaRecepcion(rs.getDate("fecha_recepcion"));
		incidente.setLugarIncidente(domicilio);
		
		return incidente;		
	}
	
	public static Incidente getMappingUltimoIncidente(ResultSet rs) throws Exception{
		Incidente incidente=new Incidente();
		
		incidente.setFecha(rs.getDate("fecha"));
		incidente.setDetalleIncidente(rs.getString("detalle_incidente"));
		incidente.setIdSeccional(rs.getInt("id_seccional"));
		incidente.setDescripcionSeccional(rs.getString("detalle_incidente"));
		incidente.setIdIncidente(rs.getInt("id_incidente"));
		incidente.setFechaRecepcion(rs.getDate("fecha_recepcion"));		
		return incidente;		
	}
	
	public Domicilio getLugarIncidente() {
		return lugarIncidente;
	}
	public void setLugarIncidente(Domicilio lugarIncidente) {
		this.lugarIncidente = lugarIncidente;
	}
	public Afiliado getAfiliado() {
		return afiliado;
	}
	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}
	public String getDetalleIncidente() {
		return detalleIncidente;
	}
	public void setDetalleIncidente(String detalleIncidente) {
		this.detalleIncidente = detalleIncidente;
	}
	
	public int getIdSeccional() {
		return idSeccional;
	}
	public void setIdSeccional(int idSeccional) {
		this.idSeccional = idSeccional;
	}
	public Date getFecha() {
		return fecha;
	}
	
	public String getFechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fecha);
	}
	
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcionSeccional() {
		return descripcionSeccional;
	}

	public void setDescripcionSeccional(String descripcionSeccional) {
		this.descripcionSeccional = descripcionSeccional;
	}

	public int getIdIncidente() {
		return idIncidente;
	}

	public void setIdIncidente(int idIncidente) {
		this.idIncidente = idIncidente;
	}

	public List<SeguimientoIncidente> getSeguimientoIncidente() {
		return seguimientoIncidente;
	}

	public void setSeguimientoIncidente(
			List<SeguimientoIncidente> seguimientoIncidente) {
		this.seguimientoIncidente = seguimientoIncidente;
	}

	public String getSeguimientoIncidenteNuevo() {
		return seguimientoIncidenteNuevo;
	}

	public void setSeguimientoIncidenteNuevo(String seguimientoIncidenteNuevo) {
		this.seguimientoIncidenteNuevo = seguimientoIncidenteNuevo;
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}
	
	
	
	
}

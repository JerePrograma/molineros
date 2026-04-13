package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.StringUtils;

/**
 * @author sistema-09
 * @version 1.0
 * @created 13-Sep-2010 04:30:17 p.m.
 */
public class PrestadorLugarAtencion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 94847231047682375L;
	
	private int id_prestador;
	private Prestador prestador;
	private Domicilio domicilio;
	private int id_domicilio;
	private Date vigen_desde;
	private String factura;
	private String nombre;
	private Integer idPrestadorAtencion;
	private Prestador prestadorAtencion;
	private Integer numeroHabilitacion;
	private String autoridadHabilitacion;
	private Date vigenciaDesdeHabilitacion;
	private Date vigenciaHastaHabilitacion;
	private boolean presentaCopiaHabilitacion;
	private String categoriaProfesional;
	private String registroHistoriaClinica;
	private List<TelefonoPrestador> telefonos;
	private List<ContactoElectronicoPrestador> contactosElectronicos;
	private Date altaFecha;
	private String  altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private String bajaUsr;
	private Date bajaFecha;
	
	private ESTADOS estado;
	    
    public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
		
	public PrestadorLugarAtencion(){

	}
	
	public PrestadorLugarAtencion(int id_prestador, int id_domicilio,
			Date vigen_desde, Date baja_fecha, String factura, String nombre,
			Integer idPrestadorAtencion, Integer numeroHabilitacion,
			String autoridadHabilitacion, Date vigenciaDesdeHabilitacion,
			Date vigenciaHastaHabilitacion, boolean presentaCopiaHabilitacion,
			String categoriaProfesional, String registroHistoriaClinica,
			List<TelefonoPrestador> telefonos,
			List<ContactoElectronicoPrestador> contactosElectronicos) {
	
		super();
		this.id_prestador = id_prestador;
		this.id_domicilio = id_domicilio;
		this.vigen_desde = vigen_desde;
		this.bajaFecha = baja_fecha;
		this.factura = factura;
		this.nombre = nombre;
		this.idPrestadorAtencion = idPrestadorAtencion;
		this.numeroHabilitacion = numeroHabilitacion;
		this.autoridadHabilitacion = autoridadHabilitacion;
		this.vigenciaDesdeHabilitacion = vigenciaDesdeHabilitacion;
		this.vigenciaHastaHabilitacion = vigenciaHastaHabilitacion;
		this.presentaCopiaHabilitacion = presentaCopiaHabilitacion;
		this.categoriaProfesional = categoriaProfesional;
		this.registroHistoriaClinica = registroHistoriaClinica;
		this.telefonos = telefonos;
		this.contactosElectronicos = contactosElectronicos;
	}


//	public String getId() {
//		return (getPrestador() != null) ? getPrestador().getCuit() : "";
//	}
	
	/**
	 * @return the id_prestador
	 */
	public int getId_prestador() {
		return id_prestador;
	}

	/**
	 * @param idPrestador the id_prestador to set
	 */
	public void setId_prestador(int idPrestador) {
		id_prestador = idPrestador;
	}

	/**
	 * @return the id_domicilio
	 */
	public int getId_domicilio() {
		return id_domicilio;
	}

	/**
	 * @param idDomicilio the id_domicilio to set
	 */
	public void setId_domicilio(int idDomicilio) {
		id_domicilio = idDomicilio;
	}

	/**
	 * @return the vigen_desde
	 */
	public Date getVigen_desde() {
		return vigen_desde;
	}

	/**
	 * @param vigenDesde the vigen_desde to set
	 */
	public void setVigen_desde(Date vigenDesde) {
		vigen_desde = vigenDesde;
	}

	/**
	 * @return the baja_fecha
	 */
	public Date getBajaFecha() {
		return bajaFecha;
	}

	/**
	 * @param bajaFecha the baja_fecha to set
	 */
	public void setBajaFecha(Date baja_Fecha) {
		bajaFecha = baja_Fecha;
	}

	/**
	 * @return the prestador
	 */
	public Prestador getPrestador() {
		return prestador;
	}

	/**
	 * @param prestador the prestador to set
	 */
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	/**
	 * @return the domicilio
	 */
	public Domicilio getDomicilio() {
		return domicilio;
	}

	/**
	 * @param domicilio the domicilio to set
	 */
	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public static PrestadorLugarAtencion getMappingSimple(ResultSet rs, String prefix) throws SQLException {
		PrestadorLugarAtencion prestadorLugarAtencion = new PrestadorLugarAtencion();		
		prestadorLugarAtencion.setId_domicilio(rs.getInt(prefix+"id_domicilio"));		
		prestadorLugarAtencion.setVigen_desde(rs.getDate(prefix+"vigen_desde")); 
		prestadorLugarAtencion.setBajaFecha(rs.getDate(prefix+"baja_fecha"));  
		return prestadorLugarAtencion;		
	}

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getIdPrestadorAtencion() {
		return idPrestadorAtencion;
	}

	public void setIdPrestadorAtencion(Integer idPrestadorAtencion) {
		this.idPrestadorAtencion = idPrestadorAtencion;
	}

	public Prestador getPrestadorAtencion() {
		return prestadorAtencion;
	}

	public void setPrestadorAtencion(Prestador prestadorAtencion) {
		this.prestadorAtencion = prestadorAtencion;
	}

	public Integer getNumeroHabilitacion() {
		return numeroHabilitacion;
	}

	public void setNumeroHabilitacion(Integer numeroHabilitacion) {
		this.numeroHabilitacion = numeroHabilitacion;
	}

	public String getAutoridadHabilitacion() {
		return autoridadHabilitacion;
	}

	public void setAutoridadHabilitacion(String autoridadHabilitacion) {
		this.autoridadHabilitacion = autoridadHabilitacion;
	}

	public Date getVigenciaDesdeHabilitacion() {
		return vigenciaDesdeHabilitacion;
	}

	public void setVigenciaDesdeHabilitacion(Date vigenciaDesdeHabilitacion) {
		this.vigenciaDesdeHabilitacion = vigenciaDesdeHabilitacion;
	}

	public Date getVigenciaHastaHabilitacion() {
		return vigenciaHastaHabilitacion;
	}

	public void setVigenciaHastaHabilitacion(Date vigenciaHastaHabilitacion) {
		this.vigenciaHastaHabilitacion = vigenciaHastaHabilitacion;
	}

	public boolean isPresentaCopiaHabilitacion() {
		return presentaCopiaHabilitacion;
	}

	public void setPresentaCopiaHabilitacion(boolean presentaCopiaHabilitacion) {
		this.presentaCopiaHabilitacion = presentaCopiaHabilitacion;
	}

	public String getCategoriaProfesional() {
		return categoriaProfesional;
	}

	public void setCategoriaProfesional(String categoriaProfesional) {
		this.categoriaProfesional = categoriaProfesional;
	}

	public String getRegistroHistoriaClinica() {
		return registroHistoriaClinica;
	}

	public void setRegistroHistoriaClinica(String registroHistoriaClinica) {
		this.registroHistoriaClinica = registroHistoriaClinica;
	}

	public List<TelefonoPrestador> getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(List<TelefonoPrestador> telefonos) {
		this.telefonos = telefonos;
	}

	public List<ContactoElectronicoPrestador> getContactosElectronicos() {
		return contactosElectronicos;
	}

	public void setContactosElectronicos(
			List<ContactoElectronicoPrestador> contactosElectronicos) {
		this.contactosElectronicos = contactosElectronicos;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}


	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
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


	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}


	public String getModiUsr() {
		return modiUsr;
	}


	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}


	public String getBajaUsr() {
		return bajaUsr;
	}


	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public String getCorreoElectronico() {
		String mostrarEmail="Email no encontrado";
		if(this.contactosElectronicos!=null && this.contactosElectronicos.size() > 0) {
			for (Iterator iterator = contactosElectronicos.iterator(); iterator.hasNext();) {
				ContactoElectronicoPrestador cep = (ContactoElectronicoPrestador) iterator.next();
				if(cep.getBajaFecha()==null 
						&& (cep.getTipo().equals(cep.getTipo().EMAIL) 
								|| cep.getTipo().equals(cep.getTipo().EMAILCBU)
								|| cep.getTipo().equals(cep.getTipo().FAX)
						   )
						){
					mostrarEmail = cep.getContacto();
				}
			}
			
		}
		return mostrarEmail;
	}
	
	public String getTelefonosConcatenados() {
		StringBuilder telConcatenados = new StringBuilder();
		
		if (telefonos!=null && telefonos.size()>0) {
			
			for (Iterator<TelefonoPrestador> iterator = telefonos.iterator(); iterator.hasNext();) {
				TelefonoPrestador tel = (TelefonoPrestador) iterator.next();
				
				telConcatenados.append(StringUtils.checkNotEmpty(tel.getCodigoPais())?"("+tel.getCodigoPais()+") ":"");
				telConcatenados.append(StringUtils.checkNotEmpty(tel.getCodigoArea())?" " +tel.getCodigoArea():"");
				telConcatenados.append(StringUtils.checkNotEmpty(tel.getNumero())?" " +tel.getNumero():"");
				telConcatenados.append(StringUtils.checkNotEmpty(tel.getExtension())?" " +tel.getExtension():"");
				telConcatenados.append(" / ");

			}
			
		}
		
		
		return telConcatenados.toString();
	}

	public static PrestadorLugarAtencion getMapping(String prefix, ResultSet rs) throws SQLException{
		
		PrestadorLugarAtencion lugarAt = new PrestadorLugarAtencion();
		lugarAt.setId_prestador(rs.getInt(prefix + "id_prestador"));		
		lugarAt.setId_domicilio(rs.getInt(prefix + "id_domicilio"));		
		lugarAt.setVigen_desde(rs.getDate(prefix + "vigen_desde"));
		lugarAt.setFactura(rs.getString(prefix + "factura"));
		lugarAt.setNombre(rs.getString(prefix + "nombre"));
		lugarAt.setIdPrestadorAtencion(rs.getInt(prefix + "id_prestador_atencion"));		
		lugarAt.setNumeroHabilitacion(rs.getInt(prefix + "numero_habilitacion"));		
		lugarAt.setAutoridadHabilitacion(rs.getString(prefix + "autoridad_habilitacion"));
		lugarAt.setVigenciaDesdeHabilitacion(rs.getDate(prefix + "vigencia_desde_habilitacion"));
		lugarAt.setVigenciaHastaHabilitacion(rs.getDate(prefix + "vigencia_hasta_habilitacion"));
		lugarAt.setPresentaCopiaHabilitacion(rs.getBoolean(prefix + "presenta_copia_habilitacion"));
		lugarAt.setCategoriaProfesional(rs.getString(prefix + "categoria_profesional"));
		lugarAt.setRegistroHistoriaClinica(rs.getString(prefix + "registro_historia_clinica"));
		lugarAt.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		lugarAt.setAltaUsr(rs.getString(prefix + "alta_usr"));
		lugarAt.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		lugarAt.setModiUsr(rs.getString(prefix + "modi_usr"));
		lugarAt.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		lugarAt.setBajaUsr(rs.getString(prefix + "baja_usr"));
		
		return lugarAt;
	}
	
	public static PrestadorLugarAtencion getMappingConDomicilio(String prefix, ResultSet rs) throws SQLException{
		
		PrestadorLugarAtencion lugarAt = new PrestadorLugarAtencion();
		lugarAt.setId_prestador(rs.getInt(prefix + "id_prestador"));		
		lugarAt.setId_domicilio(rs.getInt(prefix + "id_domicilio"));
		Domicilio dom = Domicilio.getMappingLugarAt(rs, "dom_");
		lugarAt.setDomicilio(dom);
		lugarAt.setVigen_desde(rs.getDate(prefix + "vigen_desde"));
		lugarAt.setFactura(rs.getString(prefix + "factura"));
		lugarAt.setNombre(rs.getString(prefix + "nombre"));
		lugarAt.setIdPrestadorAtencion(rs.getInt(prefix + "id_prestador_atencion"));
		Prestador pr = new Prestador(rs.getString("prs_" + "cuit"), rs.getInt("prs_" + "id_prestador"), rs.getString("prs_" + "descripcion"));
		lugarAt.setPrestadorAtencion(pr);
		lugarAt.setNumeroHabilitacion(rs.getInt(prefix + "numero_habilitacion"));		
		lugarAt.setAutoridadHabilitacion(rs.getString(prefix + "autoridad_habilitacion"));
		lugarAt.setVigenciaDesdeHabilitacion(rs.getDate(prefix + "vigencia_desde_habilitacion"));
		lugarAt.setVigenciaHastaHabilitacion(rs.getDate(prefix + "vigencia_hasta_habilitacion"));
		lugarAt.setPresentaCopiaHabilitacion(rs.getBoolean(prefix + "presenta_copia_habilitacion"));
		lugarAt.setCategoriaProfesional(rs.getString(prefix + "categoria_profesional"));
		lugarAt.setRegistroHistoriaClinica(rs.getString(prefix + "registro_historia_clinica"));
		lugarAt.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		lugarAt.setAltaUsr(rs.getString(prefix + "alta_usr"));
		lugarAt.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		lugarAt.setModiUsr(rs.getString(prefix + "modi_usr"));
		lugarAt.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		lugarAt.setBajaUsr(rs.getString(prefix + "baja_usr"));
		
		return lugarAt;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "PrestadorLugarAtencion [factura=" + factura + ", nombre="
				+ nombre + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_domicilio;
		result = prime * result + id_prestador;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrestadorLugarAtencion other = (PrestadorLugarAtencion) obj;
		if (id_domicilio != other.id_domicilio)
			return false;
		if (id_prestador != other.id_prestador)
			return false;
		return true;
	}
		
	
}
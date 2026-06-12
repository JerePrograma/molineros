package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.DateUtils;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:25:41 p.m.
 */
public class Prestador implements Serializable {

	private static final long serialVersionUID = -2927982359506401196L;

	private int id_prestador;
	private String cuit;
	private TipoPrestador tipo;
	private String contacto;
	private int id_seccional;
	private String observaciones;
	private int rein_liqui;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String descripcion;
	private Domicilio domicilio;
	private List<Telefono> telefonos;
	private List<ContactoElectronico> contactosElectronicos;	
	private String codigoHospital;
	private String ciaSeguro;
	private boolean seguroCobertura;
	private boolean certificacionProfesional;
	private boolean solicitarCotizacion;
	private String otorgaCertificacion;
	private Date fechaVtoSeguro;
	private Date fechaVtoCertificacion;

	private List<MatriculaPrestador> matriculas;
	private List<ProfesionPrestador> profesiones;
	private List<PrestadorLugarAtencion> lugaresAtencion;
	private List<PrestadorPlan> planes;
	private String empresaCaiCaeNro;
	private String cbu;
	private Boolean convenioDirecto;
	
	public Prestador() {
		super();
	}

	public Prestador(int idPrestador) {
		this.id_prestador = idPrestador;
	}
	
	public Prestador(String cuit, int id_prestador, String descripcion) {
		this.cuit = cuit;
		this.id_prestador = id_prestador;
		this.descripcion = descripcion;
	}
	
	public Prestador(int id_prestador, String cuit, TipoPrestador tipo,
			String contacto, String observaciones, /*int rein_liqui,*/
			String descripcion, String codigoHospital, String ciaSeguro,
			boolean seguroCobertura, boolean certificacionProfesional,
			String otorgaCertificacion, Date fechaVtoSeguro,
			Date fechaVtoCertificacion) {
		super();
		this.id_prestador = id_prestador;
		this.cuit = cuit;
		this.tipo = tipo;
		this.contacto = contacto;
		this.observaciones = observaciones;
		this.descripcion = descripcion;
		this.codigoHospital = codigoHospital;
		this.ciaSeguro = ciaSeguro;
		this.seguroCobertura = seguroCobertura;
		this.certificacionProfesional = certificacionProfesional;
		this.otorgaCertificacion = otorgaCertificacion;
		this.fechaVtoSeguro = fechaVtoSeguro;
		this.fechaVtoCertificacion = fechaVtoCertificacion;
	}

	public final String getCiaSeguro() {
		return ciaSeguro;
	}

	public final void setCiaSeguro(String ciaSeguro) {
		this.ciaSeguro = ciaSeguro;
	}

	public final boolean getSeguroCobertura() {
		return seguroCobertura;
	}

	public final void setSeguroCobertura(boolean seguroCobertura) {
		this.seguroCobertura = seguroCobertura;
	}

	public final boolean getCertificacionProfesional() {
		return certificacionProfesional;
	}

	public final void setCertificacionProfesional(boolean certificacionProfesional) {
		this.certificacionProfesional = certificacionProfesional;
	}

	public final String getOtorgaCertificacion() {
		return otorgaCertificacion;
	}

	public final void setOtorgaCertificacion(String otorgaCertificacion) {
		this.otorgaCertificacion = otorgaCertificacion;
	}

	public final Date getFechaVtoSeguro() {
		return fechaVtoSeguro;
	}

	public final void setFechaVtoSeguro(Date fechaVtoSeguro) {
		this.fechaVtoSeguro = fechaVtoSeguro;
	}

	public final Date getFechaVtoCertificacion() {
		return fechaVtoCertificacion;
	}

	public final void setFechaVtoCertificacion(Date fechaVtoCertificacion) {
		this.fechaVtoCertificacion = fechaVtoCertificacion;
	}

	public String getId() {
		return getCuit();
	}

	/**
	 * @return the id_prestador
	 */
	public int getId_prestador() {
		return id_prestador;
	}

	public String getId_prestadorString() {
		return String.valueOf(id_prestador);
	}

	/**
	 * @param idPrestador
	 *            the id_prestador to set
	 */
	public void setId_prestador(int idPrestador) {
		id_prestador = idPrestador;
	}

	/**
	 * @return the cuit
	 */
	public String getCuit() {
		return cuit;
	}

	/**
	 * @param cuit
	 *            the cuit to set
	 */
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	/**
	 * @return the id_tipo_prestador
	 */
	public int getId_tipo_prestador() {
		if (tipo == null) {
			return 0;
		}
		return tipo.getId();
	}

	/**
	 * @param idTipoPrestador
	 *            the id_tipo_prestador to set
	 */
	public void setId_tipo_prestador(int idTipoPrestador) {
		tipo = new TipoPrestador(idTipoPrestador, "");
	}
	/**
	 * @return the contacto
	 */
	public String getContacto() {
		return contacto;
	}

	/**
	 * @param contacto
	 *            the contacto to set
	 */
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

//	FIXME ID_seccional estaria mal ubicado en el objeto y tabla prestador,
//	debería corresponder al lugar de atencion del prestador
	public int getId_seccional() {
		return id_seccional;
	}
	public void setId_seccional(int idSeccional) {
		id_seccional = idSeccional;
	}

	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones
	 *            the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * @return the rein_liqui
	 */
	public int getRein_liqui() {
		return rein_liqui;
	}

	/**
	 * @param reinLiqui
	 *            the rein_liqui to set
	 */
	public void setRein_liqui(int reinLiqui) {
		rein_liqui = reinLiqui;
	}
	/**
	 * @return the alta_fecha
	 */
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	/**
	 * @param altaFecha
	 *            the alta_fecha to set
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
	 * @param altaUsr
	 *            the alta_usr to set
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
	 * @param modiFecha
	 *            the modi_fecha to set
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
	 * @param modiUsr
	 *            the modi_usr to set
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

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
	}

	/**
	 * @param bajaFecha
	 *            the baja_fecha to set
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
	 * @param bajaUsr
	 *            the baja_usr to set
	 */
	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion == null ? "" : descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
//TODO sacar el domicilio del prestador, ahora esta asociado al lugar de atencion del prestador
	
	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

//TODO sacar los telefonos del prestador, ahora esta asociado al lugar de atencion del prestador

	public void setTelefonos(List<Telefono> telefonos) {
		this.telefonos = telefonos;
	}

	public List<Telefono> getTelefonos() {
		return telefonos;
	}
//TODO sacar los contactos electronicos del prestador, ahora esta asociado al lugar de atencion del prestador

	public void setContactosElectronicos(
			List<ContactoElectronico> contactosElectronicos) {
		this.contactosElectronicos = contactosElectronicos;
	}

	public List<ContactoElectronico> getContactosElectronicos() {
		return contactosElectronicos;
	}

//	public ContactoElectronico getSitioWeb() {
//		if (contactosElectronicos == null) {
//			return null;
//		}
//		for (ContactoElectronico contacto : contactosElectronicos) {
//			if (contacto.getTipo().equals(ContactoElectronico.Tipo.SITIOWEB)) {
//				return contacto;
//			}
//		}
//		return null;
//	}

//	public ContactoElectronico getFax() {
//		if (contactosElectronicos == null) {
//			return null;
//		}
//		for (ContactoElectronico contacto : contactosElectronicos) {
//			if (contacto.getTipo().equals(ContactoElectronico.Tipo.FAX)) {
//				return contacto;
//			}
//		}
//		return null;
//	}

//	public ContactoElectronico getEmail() {
//		if (contactosElectronicos == null) {
//			return null;
//		}
//		for (ContactoElectronico contacto : contactosElectronicos) {
//			if (contacto.getTipo().equals(ContactoElectronico.Tipo.EMAIL)) {
//				return contacto;
//			}
//		}
//		return null;
//	}

	public void setTipo(TipoPrestador tipo) {
		this.tipo = tipo;
	}

	public TipoPrestador getTipo() {
		return tipo;
	}

	
	public String getCodigoHospital() {
		return codigoHospital;
	}

	public void setCodigoHospital(String codigoHospital) {
		this.codigoHospital = codigoHospital;
	}
	
	public static Prestador getMappingTipo(ResultSet rs, String prefix)
			throws SQLException {
		
		Prestador prestador = new Prestador();
		  
		prestador.setId_prestador(rs.getInt(prefix + "id_prestador"));
		prestador.setCuit(rs.getString(prefix + "cuit"));
		prestador.setDescripcion(rs.getString(prefix + "descripcion"));
		prestador.setId_tipo_prestador(rs.getInt(prefix + "id_tipo_prestador"));
		TipoPrestador tp = new TipoPrestador(rs.getInt(prefix + "id_tipo_prestador"), rs.getString(prefix + "descripcion_tipo_prestador"));
		prestador.setTipo(tp);
		
		return prestador;
	}
	
	public static Prestador getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Prestador prestador = new Prestador();
		  
		prestador.setId_prestador(rs.getInt(prefix + "id_prestador"));
		prestador.setCuit(rs.getString(prefix + "cuit"));
		prestador.setId_tipo_prestador(rs.getInt(prefix + "id_tipo_prestador"));
		TipoPrestador tp = new TipoPrestador(rs.getInt(prefix + "id_tipo_prestador"), rs.getString(prefix + "descripcion_tipo_prestador"));
		prestador.setTipo(tp);
		prestador.setContacto(rs.getString(prefix + "contacto"));
//		prestador.setId_seccional(rs.getInt(prefix + "id_seccional"));
		prestador.setObservaciones(rs.getString(prefix + "observaciones"));
		prestador.setRein_liqui(rs.getInt(prefix + "rein_liqui"));
		prestador.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		prestador.setAlta_usr(rs.getString(prefix + "alta_usr"));
		prestador.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		prestador.setModi_usr(rs.getString(prefix + "modi_usr"));
		prestador.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		prestador.setBaja_usr(rs.getString(prefix + "baja_usr"));
		prestador.setDescripcion(rs.getString(prefix + "descripcion"));
		prestador.setCodigoHospital(rs.getString(prefix + "codigo_htal"));
		prestador.setCertificacionProfesional(rs.getBoolean(prefix + "certificacion_profesional"));
		prestador.setFechaVtoCertificacion(rs.getDate(prefix + "vto_certificacion"));
		prestador.setSeguroCobertura(rs.getBoolean(prefix + "seguro_cobertura"));
		prestador.setCiaSeguro(rs.getString(prefix + "cia_seguro"));
		prestador.setFechaVtoSeguro(rs.getDate(prefix + "vto_cobertura_seguro"));
		prestador.setOtorgaCertificacion(rs.getString(prefix + "otorga_cert"));
		prestador.setEmpresaCaiCaeNumero(rs.getString(prefix +"cai_cae_numero_completo"));
		prestador.setSolicitarCotizacion(rs.getBoolean(prefix + "solicitar_cotizacion"));

		return prestador;
		
	}
	
	public static Prestador getMappingSimple(ResultSet rs, String prefix)
			throws SQLException {
		Prestador prestador = new Prestador();
		  
		prestador.setId_prestador(rs.getInt(prefix + "id_prestador"));
		prestador.setCuit(rs.getString(prefix + "cuit"));
		prestador.setId_tipo_prestador(rs.getInt(prefix + "id_tipo_prestador"));		
//		TipoPrestador tp = new TipoPrestador(rs.getInt(prefix + "id_tipo_prestador"), rs.getString(prefix + "descripcion_tipo_prestador"));
//		prestador.setTipo(tp);
		prestador.setContacto(rs.getString(prefix + "contacto"));	
//		prestador.setId_seccional(rs.getInt(prefix + "id_seccional"));			
		prestador.setObservaciones(rs.getString(prefix + "observaciones"));
		prestador.setRein_liqui(rs.getInt(prefix + "rein_liqui"));		
		prestador.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		prestador.setAlta_usr(rs.getString(prefix + "alta_usr"));
		prestador.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		prestador.setModi_usr(rs.getString(prefix + "modi_usr"));
		prestador.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		prestador.setBaja_usr(rs.getString(prefix + "baja_usr"));
		prestador.setDescripcion(rs.getString(prefix + "descripcion"));
		prestador.setCodigoHospital(rs.getString(prefix + "codigo_htal"));
		prestador.setCertificacionProfesional(rs.getBoolean(prefix + "certificacion_profesional"));
		prestador.setFechaVtoCertificacion(rs.getDate(prefix + "vto_certificacion"));
		prestador.setSeguroCobertura(rs.getBoolean(prefix + "seguro_cobertura"));
		prestador.setCiaSeguro(rs.getString(prefix + "cia_seguro"));
		prestador.setFechaVtoSeguro(rs.getDate(prefix + "vto_cobertura_seguro"));
		prestador.setOtorgaCertificacion(rs.getString(prefix + "otorga_cert"));
		prestador.setSolicitarCotizacion(rs.getBoolean(prefix + "solicitar_cotizacion"));
		return prestador;
		
	}
	
	public List<ProfesionPrestador> getProfesiones() {
		return profesiones;
	}

	public void setProfesiones(List<ProfesionPrestador> profesiones) {
		this.profesiones = profesiones;
	}

	public List<MatriculaPrestador> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<MatriculaPrestador> matriculas) {
		this.matriculas = matriculas;
	}

	public List<PrestadorLugarAtencion> getLugaresAtencion() {
		return lugaresAtencion;
	}

	public void setLugaresAtencion(List<PrestadorLugarAtencion> lugaresAtencion) {
		this.lugaresAtencion = lugaresAtencion;
	}

	public List<PrestadorPlan> getPlanes() {
		return planes;
	}

	public void setPlanes(List<PrestadorPlan> planes) {
		this.planes = planes;
	}

	public void setEmpresaCaiCaeNumero(String caiCaeNumeroCompleto){
		this.empresaCaiCaeNro = caiCaeNumeroCompleto;
	}
	
	public String getEmpresaCaiCaeNumero(){
		return this.empresaCaiCaeNro;
	}
	
	public static class TipoPrestador {
		private int id;
		private String descripcion;

		public TipoPrestador(int idTipoPrestador, String desc) {
			this.id = idTipoPrestador;
			this.descripcion = desc;
		}

		public TipoPrestador() {
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
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
			TipoPrestador other = (TipoPrestador) obj;
			if (id != other.id)
				return false;
			return true;
		}
		
		public static TipoPrestador getMapping(ResultSet rs)
				throws SQLException {
			TipoPrestador tipo = new TipoPrestador();
			tipo.setDescripcion(rs.getString("descripcion"));
			tipo.setId(rs.getInt("id_tipo_prestador"));
			return tipo;
		}

	}

	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public Boolean getConvenioDirecto() {
		return convenioDirecto;
	}

	public void setConvenioDirecto(Boolean convenioDirecto) {
		this.convenioDirecto = convenioDirecto;
	}

	public boolean isSolicitarCotizacion() {
		return solicitarCotizacion;
	}

	public void setSolicitarCotizacion(boolean solicitarCotizacion) {
		this.solicitarCotizacion = solicitarCotizacion;
	}
}











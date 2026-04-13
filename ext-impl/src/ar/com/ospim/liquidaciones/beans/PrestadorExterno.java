package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.PosicionIva;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.DateUtils;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:25:41 p.m.
 */
public class PrestadorExterno {
	private int id_prestador;
	private String cuit;
	private String tipo;
	private String tipo_matricula;
	private int nro_matricula;
	private Provincia provinciaMatricula;
	private String id_mat_categoria;
	private String contacto;
	private int id_seccional;
	private String observaciones;
	private int rein_liqui;
	private String cheque_a_nombre_de;
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
	private PosicionIva posicionIva;

	public PrestadorExterno() {

	}

	public PrestadorExterno(String cuit, int id_prestador, String descripcion) {
		this.cuit = cuit;
		this.id_prestador = id_prestador;
		this.descripcion = descripcion;
	}

	public PrestadorExterno(int idPrestador) {
		this.id_prestador = idPrestador;
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
	 * @return the tipo_matricula
	 */
	public String getTipo_matricula() {
		return tipo_matricula;
	}

	/**
	 * @param tipoMatricula
	 *            the tipo_matricula to set
	 */
	public void setTipo_matricula(String tipoMatricula) {
		tipo_matricula = tipoMatricula;
	}

	/**
	 * @return the nro_matricula
	 */
	public int getNro_matricula() {
		return nro_matricula;
	}

	/**
	 * @param nroMatricula
	 *            the nro_matricula to set
	 */
	public void setNro_matricula(int nroMatricula) {
		nro_matricula = nroMatricula;
	}

	/**
	 * @return the id_mat_provincia
	 */
	public int getId_mat_provincia() {
		if (provinciaMatricula == null) {
			return 0;
		}
		return provinciaMatricula.getId();
	}

	/**
	 * @param idMatProvincia
	 *            the id_mat_provincia to set
	 */
	public void setId_mat_provincia(int idMatProvincia) {
		provinciaMatricula = new Provincia(idMatProvincia, "");
	}

	/**
	 * @return the id_mat_categoria
	 */
	public String getId_mat_categoria() {
		return id_mat_categoria;
	}

	/**
	 * @param idMatCategoria
	 *            the id_mat_categoria to set
	 */
	public void setId_mat_categoria(String idMatCategoria) {
		id_mat_categoria = idMatCategoria;
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

	/**
	 * @return the id_seccional
	 */
	public int getId_seccional() {
		return id_seccional;
	}

	/**
	 * @param idSeccional
	 *            the id_seccional to set
	 */
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
	 * @return the id_condicion_de_iva
	 */
	public int getId_condicion_de_iva() {
		if (posicionIva == null) {
			return 0;
		}
		return posicionIva.getId();
	}

	/**
	 * @param idCondicionDeIva
	 *            the id_condicion_de_iva to set
	 */
	public void setId_condicion_de_iva(int idCondicionDeIva) {
		posicionIva = new PosicionIva(idCondicionDeIva, "");
	}

	/**
	 * @return the cheque_a_nombre_de
	 */
	public String getCheque_a_nombre_de() {
		return cheque_a_nombre_de;
	}

	/**
	 * @param chequeANombreDe
	 *            the cheque_a_nombre_de to set
	 */
	public void setCheque_a_nombre_de(String chequeANombreDe) {
		cheque_a_nombre_de = chequeANombreDe;
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

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setTelefonos(List<Telefono> telefonos) {
		this.telefonos = telefonos;
	}

	public List<Telefono> getTelefonos() {
		return telefonos;
	}

	public void setContactosElectronicos(
			List<ContactoElectronico> contactosElectronicos) {
		this.contactosElectronicos = contactosElectronicos;
	}

	public List<ContactoElectronico> getContactosElectronicos() {
		return contactosElectronicos;
	}

	public void setPosicionIva(PosicionIva posicionIva) {
		this.posicionIva = posicionIva;
	}

	public PosicionIva getPosicionIva() {
		return posicionIva;
	}

	public ContactoElectronico getSitioWeb() {
		if (contactosElectronicos == null) {
			return null;
		}
		for (ContactoElectronico contacto : contactosElectronicos) {
			if (contacto.getTipo().equals(ContactoElectronico.Tipo.SITIOWEB)) {
				return contacto;
			}
		}
		return null;
	}

	public ContactoElectronico getFax() {
		if (contactosElectronicos == null) {
			return null;
		}
		for (ContactoElectronico contacto : contactosElectronicos) {
			if (contacto.getTipo().equals(ContactoElectronico.Tipo.FAX)) {
				return contacto;
			}
		}
		return null;
	}

	public ContactoElectronico getEmail() {
		if (contactosElectronicos == null) {
			return null;
		}
		for (ContactoElectronico contacto : contactosElectronicos) {
			if (contacto.getTipo().equals(ContactoElectronico.Tipo.EMAIL)) {
				return contacto;
			}
		}
		return null;
	}

	public void setProvinciaMatricula(Provincia provinciaMatricula) {
		this.provinciaMatricula = provinciaMatricula;
	}

	public Provincia getProvinciaMatricula() {
		return provinciaMatricula;
	}

	public static PrestadorExterno getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static PrestadorExterno getMapping(ResultSet rs, String prefix)
			throws SQLException {
		PrestadorExterno prestador = new PrestadorExterno();

		prestador.setCuit(rs.getString(prefix + "cuit"));
		prestador.setTipo(rs.getString(prefix + "tipo"));
		prestador.setTipo_matricula(rs.getString(prefix + "tipo_matricula"));
		prestador.setNro_matricula(rs.getInt(prefix + "nro_matricula"));
		prestador.setId_mat_provincia(rs.getInt(prefix + "id_mat_provincia"));
		prestador
				.setId_mat_categoria(rs.getString(prefix + "id_mat_categoria"));
		prestador.setRein_liqui(rs.getInt(prefix + "rein_liqui"));
		prestador.setId_condicion_de_iva(rs.getInt(prefix
				+ "id_condicion_de_iva"));
		prestador.setCheque_a_nombre_de(rs.getString(prefix
				+ "cheque_a_nombre_de"));
		prestador.setDescripcion(rs.getString(prefix + "descripcion"));
		prestador.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		prestador.setAlta_usr(rs.getString(prefix + "alta_usr"));
		prestador.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		prestador.setModi_usr(rs.getString(prefix + "modi_usr"));
		prestador.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		prestador.setBaja_usr(rs.getString(prefix + "baja_usr"));
		try {
			prestador.setId_prestador(rs.getInt(prefix + "id_prestador"));
		} catch (SQLException e) {
			// nothing
		}
		try {
			prestador.setContacto(rs.getString(prefix + "contacto"));
		} catch (SQLException e) {
			// nothing
		}
		try {
			prestador.setId_seccional(rs.getInt(prefix + "id_seccional"));
		} catch (SQLException e) {
			// nothing
		}
		try {
			prestador.setObservaciones(rs.getString(prefix + "observaciones"));
		} catch (SQLException e) {
			// nothing
		}
		return prestador;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
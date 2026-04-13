package ar.com.uoma.beans;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;

import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;

public class Correspondencia {
	private String destino;
	private Date fechaEnvioRecepcion;
	private String lugarRecepcion;
	private String lugarRecepcionDescription;
	private TipoCorrespondencia tipo;
	private String apellidoRemitente;
	private String nombreRemitente;
	private String apellidoDestinatario;
	private String nombreDestinatario;
	private Domicilio domicilioRemitente;
	private Domicilio domicilioDestinatario;
	private String edificioRemitente;
	private String edificioDestinatario;
	private Seccional seccionalRemitente;
	private Seccional seccionalDestinatario;
	private String observaciones;
	private int idCorrespondencia;
	private String altaUsr;
	private String razonPrestadorRemitente;
	private String razonPrestadorDestinatario;
	private String datosFactura;
	private boolean gastoSeccional;
	private boolean reintegro;
	private boolean padrones;
	private boolean discapacidad;
	private boolean otros;
	private boolean facturacion;
	private boolean documentacion;
	private boolean medicamentos;
	private boolean tesoreria;
	private String tipoEnvio;
	private String oblea;
	private String codFarmacia;
	private String farmacia;

	private ArrayList<ItemCorrespondencia> itemsCorrespondencia;
	
	public static Correspondencia getMappingCorrespondenciaId(ResultSet rs)
			throws Exception {
		
		Correspondencia corr = new Correspondencia();
		corr.setDestino(rs.getString("destino"));
		Domicilio domiRemitente = new Domicilio();
		domiRemitente.setId_domicilio(rs.getInt("id_domicilio_remitente"));
		domiRemitente.setCalle(rs.getString("calle_remitente"));
		domiRemitente.setPiso(rs.getString("piso_remitente"));
		domiRemitente.setDepto(rs.getString("depto_remitente"));
		domiRemitente.setOficina(rs.getString("oficina_remitente"));
		domiRemitente.setPostal_codi(rs.getString("postal_codi_remitente"));
		domiRemitente.setBarrio(rs.getString("barrio_remitente"));
		domiRemitente.setTelefono(rs.getString("telefono_remitente"));
		domiRemitente.setObservaciones(rs.getString("observaciones_remitente"));
		// domiRemitente.setProvinciaId(rs.getInt("id_provincia_remitente"));
		// domiRemitente.setLocalidadId(rs.getInt("id_localidad_remitente"));
		domiRemitente.setProvinciaId(rs.getInt("id_provincia_corr"));
		domiRemitente.setLocalidadId(rs.getInt("id_localidad_corr"));
		domiRemitente.setNumero(rs.getString("numero_remitente"));
		corr.setDomicilioRemitente(domiRemitente);
		Domicilio domiDestina = new Domicilio();
		domiDestina.setId_domicilio(rs.getInt("id_domicilio_destina"));
		domiDestina.setCalle(rs.getString("calle_destina"));
		domiDestina.setPiso(rs.getString("piso_destina"));
		domiDestina.setDepto(rs.getString("depto_destina"));
		domiDestina.setOficina(rs.getString("oficina_destina"));
		domiDestina.setPostal_codi(rs.getString("postal_codi_destina"));
		domiDestina.setBarrio(rs.getString("barrio_destina"));
		domiDestina.setTelefono(rs.getString("telefono_destina"));
		domiDestina.setObservaciones(rs.getString("observaciones_destina"));
		domiDestina.setProvinciaId(rs.getInt("id_provincia_destina"));
		domiDestina.setLocalidadId(rs.getInt("id_localidad_destina"));
		domiDestina.setNumero(rs.getString("numero_destina"));
		corr.setDomicilioDestinatario(domiDestina);

		corr.setApellidoRemitente(rs.getString("apellido_remitente"));
		corr.setNombreRemitente(rs.getString("nombre_remitente"));
		corr.setApellidoDestinatario(rs.getString("apellido_destinatario"));
		corr.setNombreDestinatario(rs.getString("nombre_destinatario"));
		corr.setFechaEnvioRecepcion(rs.getDate("fecha_envio_recepcion"));
		corr.setTipo(new TipoCorrespondencia(rs.getInt("tipo_correo")));
		corr.setLugarRecepcion(rs.getString("edificio_recep"));
		corr.setObservaciones(rs.getString("observaciones"));

		corr.setSeccionalRemitente(new Seccional(rs
				.getInt("seccional_remitente"), rs
				.getString("seccional_remitente_nombre")));
		corr.setSeccionalDestinatario(new Seccional(rs
				.getInt("seccional_destinatario"), rs
				.getString("seccional_destinatario_nombre")));
		corr.setEdificioDestinatario(rs.getString("edificio_destino"));
		corr.setEdificioRemitente(rs.getString("edificio_origen"));
		corr.setIdCorrespondencia(rs.getInt("id_correspondencia"));
		corr.setGastoSeccional(rs.getBoolean("gastos_seccional"));
		corr.setReintegro(rs.getBoolean("reintegro"));
		corr.setPadrones(rs.getBoolean("padrones"));
		corr.setDiscapacidad(rs.getBoolean("discapacidad"));
		corr.setOtros(rs.getBoolean("otros"));
		corr.setFacturacion(rs.getBoolean("facturacion"));
		corr.setDocumentacion(rs.getBoolean("documentacion"));
		corr.setDatosFactura(rs.getString("datos_factura"));
		corr.setTesoreria(rs.getBoolean("tesoreria"));
		corr.setMedicamentos(rs.getBoolean("medicamentos"));
		corr.setRazonPrestadorRemitente(rs
				.getString("razon_prestador_remitente"));
		corr.setRazonPrestadorDestinatario(rs
				.getString("razon_prestador_destinatario"));
		corr.setTipoEnvio(rs.getString("tipo_envio"));
		corr.setOblea(rs.getString("oblea"));
		corr.setCodFarmacia(rs.getString("cod_farmacia"));
		corr.setFarmacia(rs.getString("farmacia"));

		return corr;
	}

	public static Correspondencia getMappingCorrespondencia(ResultSet rs)
			throws Exception {
		Correspondencia corr = new Correspondencia();

		corr.setIdCorrespondencia(rs.getInt("id_correspondencia"));
		corr.setDestino(rs.getString("destino"));
		corr.setFechaEnvioRecepcion(rs.getDate("fecha_envio_recepcion"));
		corr.setAltaUsr(rs.getString("alta_usr"));
		// corr.setTipo(new TipoCorrespondencia(rs.getString("tipo_corr")));
		corr.setLugarRecepcion(rs.getString("edificio_recep"));
		corr.setRazonPrestadorRemitente(rs
				.getString("razon_prestador_remitente"));
		corr.setRazonPrestadorDestinatario(rs
				.getString("razon_prestador_destinatario"));
		corr.setObservaciones(rs.getString("observaciones"));
		int seccional = rs.getInt("seccional_remitente");
		if (seccional > 0) {
			corr.setSeccionalRemitente(new Seccional(seccional, rs
					.getString("seccional_remitente_nombre")));
		}
		corr.setEdificioRemitente(rs.getString("edificio_origen"));
		corr.setEdificioDestinatario(rs.getString("edificio_destino"));

		int id_localidad = rs.getInt("id_localidad_corr");
		int id_provincia = rs.getInt("id_provincia_corr");
		Domicilio domi=null;
		if (id_localidad > 0) {
			domi = new Domicilio();
			domi.setLocalidad(new Localidad(id_localidad, rs
					.getString("localidad_corr_nombre")));
			domi.setProvincia(new Provincia(id_provincia, rs
					.getString("provincia_corr_nombre")));
		}
		corr.setDomicilioRemitente(domi);
		corr.setGastoSeccional(rs.getBoolean("gastos_seccional"));
		corr.setReintegro(rs.getBoolean("reintegro"));
		corr.setPadrones(rs.getBoolean("padrones"));
		corr.setDiscapacidad(rs.getBoolean("discapacidad"));
		corr.setOtros(rs.getBoolean("otros"));
		corr.setFacturacion(rs.getBoolean("facturacion"));
		corr.setDocumentacion(rs.getBoolean("documentacion"));
		corr.setTesoreria(rs.getBoolean("tesoreria"));
		corr.setMedicamentos(rs.getBoolean("medicamentos"));
		
		corr.setDatosFactura(rs.getString("datos_factura"));

		corr.setTipoEnvio(rs.getString("tipo_envio"));
		corr.setOblea(rs.getString("codigo_oblea"));
		corr.setCodFarmacia(rs.getString("cod_farmacia"));
		corr.setFarmacia(rs.getString("farmacia"));


		return corr;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public Date getFechaEnvioRecepcion() {
		return fechaEnvioRecepcion;
	}

	public void setFechaEnvioRecepcion(Date fechaEnvioRecepcion) {
		this.fechaEnvioRecepcion = fechaEnvioRecepcion;
	}

	public String getLugarRecepcion() {
		return lugarRecepcion;
	}

	public void setLugarRecepcion(String lugarRecepcion) {
		this.lugarRecepcion = lugarRecepcion;
	}

	public TipoCorrespondencia getTipo() {
		return tipo;
	}

	public void setTipo(TipoCorrespondencia tipo) {
		this.tipo = tipo;
	}

	public String getApellidoRemitente() {
		return apellidoRemitente;
	}

	public void setApellidoRemitente(String apellidoRemitente) {
		this.apellidoRemitente = apellidoRemitente;
	}

	public String getNombreRemitente() {
		return nombreRemitente;
	}

	public void setNombreRemitente(String nombreRemitente) {
		this.nombreRemitente = nombreRemitente;
	}

	public String getApellidoDestinatario() {
		return apellidoDestinatario;
	}

	public void setApellidoDestinatario(String apellidoDestinatario) {
		this.apellidoDestinatario = apellidoDestinatario;
	}

	public String getNombreDestinatario() {
		return nombreDestinatario;
	}

	public void setNombreDestinatario(String nombreDestinatario) {
		this.nombreDestinatario = nombreDestinatario;
	}

	public Domicilio getDomicilioRemitente() {
		return domicilioRemitente;
	}

	public void setDomicilioRemitente(Domicilio domicilioRemitente) {
		this.domicilioRemitente = domicilioRemitente;
	}

	public Domicilio getDomicilioDestinatario() {
		return domicilioDestinatario;
	}

	public void setDomicilioDestinatario(Domicilio domicilioDestinatario) {
		this.domicilioDestinatario = domicilioDestinatario;
	}

	public String getEdificioRemitente() {
		return edificioRemitente;
	}

	public void setEdificioRemitente(String edificioRemitente) {
		this.edificioRemitente = edificioRemitente;
	}

	public String getEdificioDestinatario() {
		return edificioDestinatario;
	}

	public void setEdificioDestinatario(String edificioDestinatario) {
		this.edificioDestinatario = edificioDestinatario;
	}

	public Seccional getSeccionalRemitente() {
		return seccionalRemitente;
	}

	public void setSeccionalRemitente(Seccional seccionalRemitente) {
		this.seccionalRemitente = seccionalRemitente;
	}

	public Seccional getSeccionalDestinatario() {
		return seccionalDestinatario;
	}

	public void setSeccionalDestinatario(Seccional seccionalDestinatario) {
		this.seccionalDestinatario = seccionalDestinatario;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public int getIdCorrespondencia() {
		return idCorrespondencia;
	}

	public void setIdCorrespondencia(int id_correspondencia) {
		this.idCorrespondencia = id_correspondencia;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public String getFechaEnvioRecepcionAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return fechaEnvioRecepcion != null ? sdf.format(fechaEnvioRecepcion)
				: "";
	}

	public String getRazonPrestadorRemitente() {
		return razonPrestadorRemitente;
	}

	public void setRazonPrestadorRemitente(String razonPrestadorRemitente) {
		this.razonPrestadorRemitente = razonPrestadorRemitente;
	}

	public String getRazonPrestadorDestinatario() {
		return razonPrestadorDestinatario;
	}

	public void setRazonPrestadorDestinatario(String razonPrestadorDestinatario) {
		this.razonPrestadorDestinatario = razonPrestadorDestinatario;
	}

	public boolean isGastoSeccional() {
		return gastoSeccional;
	}

	public void setGastoSeccional(boolean gastoSeccional) {
		this.gastoSeccional = gastoSeccional;
	}

	public boolean isReintegro() {
		return reintegro;
	}

	public void setReintegro(boolean reintegro) {
		this.reintegro = reintegro;
	}

	public boolean isPadrones() {
		return padrones;
	}

	public void setPadrones(boolean padrones) {
		this.padrones = padrones;
	}

	public boolean isDiscapacidad() {
		return discapacidad;
	}

	public void setDiscapacidad(boolean discapacidad) {
		this.discapacidad = discapacidad;
	}

	public boolean isOtros() {
		return otros;
	}

	public void setOtros(boolean otros) {
		this.otros = otros;
	}

	public String getDatosFactura() {
		return datosFactura;
	}

	public void setDatosFactura(String datosFactura) {
		this.datosFactura = datosFactura;
	}

	public String getTipoEnvio() {
		return tipoEnvio;
	}

	public void setTipoEnvio(String tipoEnvio) {
		this.tipoEnvio = tipoEnvio;
	}

	public String getOblea() {
		return oblea;
	}

	public void setOblea(String oblea) {
		this.oblea = oblea;
	}

	public boolean isFacturacion() {
		return facturacion;
	}

	public void setFacturacion(boolean facturacion) {
		this.facturacion = facturacion;
	}

	public boolean isDocumentacion() {
		return documentacion;
	}

	public void setDocumentacion(boolean documentacion) {
		this.documentacion = documentacion;
	}

	public String getCodFarmacia() {
		return codFarmacia;
	}

	public void setCodFarmacia(String codFarmacia) {
		this.codFarmacia = codFarmacia;
	}

	public String getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(String farmacia) {
		this.farmacia = farmacia;
	}

	public boolean isMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(boolean medicamentos) {
		this.medicamentos = medicamentos;
	}

	public boolean isTesoreria() {
		return tesoreria;
	}

	public void setTesoreria(boolean tesoreria) {
		this.tesoreria = tesoreria;
	}
	
	public ArrayList<ItemCorrespondencia> getItemsCorrespondencia() {
		return itemsCorrespondencia;
	}

	public void setItemsCorrespondencia(
			ArrayList<ItemCorrespondencia> itemsCorrespondencia) {
		this.itemsCorrespondencia = itemsCorrespondencia;
	}

	public String getLugarRecepcionDescription() {
		return lugarRecepcionDescription;
	}

	public void setLugarRecepcionDescription(String lugarRecepcionDescription) {
		this.lugarRecepcionDescription = lugarRecepcionDescription;
	}	
}

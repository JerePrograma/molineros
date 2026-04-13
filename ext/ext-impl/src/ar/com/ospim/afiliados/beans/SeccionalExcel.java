package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.reportes.action.ReporteSeccional;
import ar.com.ospim.global.beans.Seccional;


public class SeccionalExcel extends Seccional {

	private static final long serialVersionUID = 1L;

	// campos excel seccionales
// solapa contactos
	private  String cargoDescripcion ;
	private  String nombreContacto ;
	private  String telefonoNumero ;
	private  String telefonoTipo ;
	private  String codigoArea  ;
	private String provinciaDetalle;
	private String tipoContacto ;
	private String contactoEmail;
// solapa seccionales listado 	
	
	
	private String localidad;
	private String calle;
	private String numero;
	private String piso; 
	private String depto;
	private String postalCodi;
	private String contacto;
	private String destinoCorr;
	private String observaciones;
	private String contactoMail;
	
	
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSeccional.class);
	
	
	public SeccionalExcel () {
		super();
	}

	public static SeccionalExcel  getMapping(ResultSet rs) throws SQLException {
		SeccionalExcel  archivo = new SeccionalExcel ();		
		try {
			
			
			archivo.setTipoContacto(rs.getString("contac_sec_tipo_contacto") );
			archivo.setEmail(rs.getString("contac_sec_email") );
			archivo.setId_seccional(rs.getInt("contac_sec_id_Seccional"));
			archivo.setDescripcion(rs.getString("contac_sec_seccional") );
			archivo.setCargoDescripcion(rs.getString("contac_sec_cargo_descripcion") );
			archivo.setNombreContacto(rs.getString("contac_sec_nombre") );
			archivo.setTelefonoNumero(rs.getString("contac_sec_telefono_nro") );
			archivo.setTelefonoTipo(rs.getString("contac_sec_telefono_tipo") );
			archivo.setCodigoArea(rs.getString("contac_sec_cod_area") );
			archivo.setprovinciaDetalle(rs.getString("contac_sec_provincia") );
						
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de seccionales",e);
			return null;
		}
		
		return archivo;
	}

	public static SeccionalExcel  getMappingSeccional(ResultSet rs) throws SQLException {
		SeccionalExcel  archivo = new SeccionalExcel ();		
		try {
			
			
			archivo.setId_seccional(rs.getInt("sec_nro_seccional"));
			archivo.setDescripcion(rs.getString("sec_descripcion") );
			archivo.setVigen_fecha(rs.getDate("sec_vigencia")); 		
			archivo.setLocalidad(rs.getString("sec_localidad") );
			archivo.setprovinciaDetalle(rs.getString("sec_provincia") );
			archivo.setCalle(rs.getString("sec_calle") );
			archivo.setNumero(rs.getString("sec_numero") );
			archivo.setPiso(rs.getString("sec_piso") );
			archivo.setDepto(rs.getString("sec_depto") );
			archivo.setPostal_codi(rs.getString("sec_postal_codi") );
			archivo.setContacto(rs.getString("sec_contacto") );
			archivo.setDestino_corr(rs.getString("sec_destino_corr") );
			archivo.setObservaciones(rs.getString("sec_observaciones") );
			archivo.setContacto_mail(rs.getString("sec_contacto_mail") );
			archivo.setHorarioAtencion(rs.getString("sec_horario_atencion") );
			
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de seccionales",e);
			return null;
		}
		
		return archivo;
	}

	
		

		public String getCargoDescripcion() {
			return cargoDescripcion;
		}

		public void setCargoDescripcion(String cargoDescripcion) {
			this.cargoDescripcion = cargoDescripcion;
		}

		public String getNombreContacto() {
			return nombreContacto;
		}

		public void setNombreContacto(String nombreContacto) {
			this.nombreContacto = nombreContacto;
		}

		public String getTelefonoNumero() {
			return telefonoNumero;
		}

		public void setTelefonoNumero(String telefonoNumero) {
			this.telefonoNumero = telefonoNumero;
		}

		public String getTelefonoTipo() {
			return telefonoTipo;
		}

		public void setTelefonoTipo(String telefonoTipo) {
			this.telefonoTipo = telefonoTipo;
		}

		public String getCodigoArea() {
			return codigoArea;
		}

		public void setCodigoArea(String codigoArea) {
			this.codigoArea = codigoArea;
		}

       public void setprovinciaDetalle(String provincia ){
    	   this.provinciaDetalle=provincia;
       }
       public String getDetalleProvincia(){
    	   return this.provinciaDetalle;
       }

		
	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getDepto() {
		return depto;
	}

	public void setDepto(String depto) {
		this.depto = depto;
	}

	public String getPostal_codi() {
		return postalCodi;
	}

	public void setPostal_codi(String postalCodi) {
		this.postalCodi = postalCodi;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public String getDestino_corr() {
		return destinoCorr;
	}

	public void setDestino_corr(String destinoCorr) {
		this.destinoCorr = destinoCorr;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getContacto_mail() {
		return contactoMail;
	}

	public void setContacto_mail(String contactoMail) {
		this.contactoMail = contactoMail;
	}
	
	public String getTipoContacto () {
		return tipoContacto ;
	}

	public void setTipoContacto (String tipoContactoMail) {
		this.tipoContacto  = tipoContactoMail;
	}
	
	public String getEmail () {
		return contactoEmail;
	}

	public void setEmail (String emailContacto ) {
		this.contactoEmail= emailContacto ;
	}
	
	
	
       
       
}

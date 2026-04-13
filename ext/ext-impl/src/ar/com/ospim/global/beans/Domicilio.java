package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Carlos Rivas
 * @version 1.0
 * @created 14-Jul-2010 03:30:53 p.m.
 */
public class Domicilio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5850537670143262181L;

	private int id_domicilio;
	private String domi_tipo;
	private String calle;
	private String numero;
	private String piso;
	private String depto;
	private String oficina;
	private String postal_codi;
	private String barrio;
	private String cod_area_telefono;
	private String telefono;
	private String cod_area_tel_laboral;
	private String tel_laboral;
	private String cod_area_celular;
	private String celular;
	private String observaciones;
	private String domi_val;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private Localidad localidad;
	private Provincia provincia;
	private Pais pais;
	private String cargo;
	private String nomape;
	private String profesion;
	private String planta;

	private ESTADOS estado;
    
    public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};
	
	public Domicilio() {
	}

	/**
	 * @return the id_domicilio
	 */
	public int getId_domicilio() {
		return id_domicilio;
	}

	/**
	 * @param idDomicilio
	 *            the id_domicilio to set
	 */
	public void setId_domicilio(int idDomicilio) {
		id_domicilio = idDomicilio;
	}

	/**
	 * @return the domi_tipo
	 */
	public String getDomi_tipo() {
		return domi_tipo;
	}

	/**
	 * @param domiTipo
	 *            the domi_tipo to set
	 */
	public void setDomi_tipo(String domiTipo) {
		domi_tipo = domiTipo;
	}

	/**
	 * @return the calle
	 */
	public String getCalle() {
		return calle;
	}

	/**
	 * @param calle
	 *            the calle to set
	 */
	public void setCalle(String calle) {
		this.calle = calle;
	}

	/**
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * @param numero
	 *            the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * @return the piso
	 */
	public String getPiso() {
		return piso;
	}

	/**
	 * @param piso
	 *            the piso to set
	 */
	public void setPiso(String piso) {
		this.piso = piso;
	}

	/**
	 * @return the depto
	 */
	public String getDepto() {
		return depto;
	}

	/**
	 * @param depto
	 *            the depto to set
	 */
	public void setDepto(String depto) {
		this.depto = depto;
	}

	/**
	 * @return the oficina
	 */
	public String getOficina() {
		return oficina;
	}

	/**
	 * @param oficina
	 *            the oficina to set
	 */
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	/**
	 * @return the postal_codi
	 */
	public String getPostal_codi() {
		return postal_codi;
	}

	/**
	 * @param postalCodi
	 *            the postal_codi to set
	 */
	public void setPostal_codi(String postalCodi) {
		postal_codi = postalCodi;
	}

	/**
	 * @return the localidad id
	 */
	public int getLocalidadId() {
		return localidad.getId();
	}

	/**
	 * @param localidad
	 *            the localidad to set
	 */
	public void setLocalidadId(int localidadId) {
		localidad = new Localidad(localidadId);
	}

	/**
	 * @return the localidad
	 */
	public Localidad getLocalidad() {
		return localidad;
	}

	public String getLocalidadAsString() {
		if (localidad != null && null != localidad.getDescripcion()) {
			return localidad.getDescripcion();
		} else {
			return "";
		}
	}

	public void setLocalidad(Localidad localidad) {
		this.localidad = localidad;
	}

	/**
	 * @return the barrio
	 */
	public String getBarrio() {
		return barrio;
	}

	/**
	 * @param barrio
	 *            the barrio to set
	 */
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	/**
	 * @return the telefono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * @param telefono
	 *            the telefono to set
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCod_area_telefono() {
		return cod_area_telefono;
	}

	public void setCod_area_telefono(String cod_area_telefono) {
		this.cod_area_telefono = cod_area_telefono;
	}

	public String getCod_area_celular() {
		return cod_area_celular;
	}

	public void setCod_area_celular(String cod_area_celular) {
		this.cod_area_celular = cod_area_celular;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
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

	public int getPaisId() {
		return pais.getId();
	}

	public void setPaisId(int id) {
		this.pais = new Pais(id);
	}

	/**
	 * @return the provincia id
	 */
	public int getProvinciaId() {
		return provincia.getId();
	}

	/**
	 * @param provincia
	 *            the provincia to set
	 */
	public void setProvinciaId(int provinciaId) {
		this.provincia = new Provincia(provinciaId);
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	/**
	 * @return the provincia
	 */
	public Provincia getProvincia() {
		return provincia;
	}

	public String getProvinciaAsString() {
		if (provincia != null && null != provincia.getDescripcion()) {
			return provincia.getDescripcion();
		} else {
			return "";
		}
	}

	/**
	 * @return the domi_val
	 */
	public String getDomi_val() {
		return domi_val;
	}

	/**
	 * @param domiVal
	 *            the domi_val to set
	 */
	public void setDomi_val(String domiVal) {
		domi_val = domiVal;
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
	
	public String getModi_fechaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return modi_fecha!=null?sdf.format(modi_fecha):"";
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

	public static Domicilio getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static Domicilio getMappingEmpresa(ResultSet rs, String prefix)
			throws SQLException {
		Domicilio domicilio = new Domicilio();
		domicilio.setId_domicilio(rs.getInt(prefix + "id_domicilio"));
		domicilio.setDomi_tipo(rs.getString(prefix + "domi_tipo"));
		domicilio.setCalle(rs.getString(prefix + "calle"));
		domicilio.setPiso(rs.getString(prefix + "piso"));
		domicilio.setDepto(rs.getString(prefix + "depto"));
		domicilio.setOficina(rs.getString(prefix + "oficina"));
		domicilio.setPostal_codi(rs.getString(prefix + "postal_codi"));
		domicilio.setBarrio(rs.getString(prefix + "barrio"));
		domicilio.setNumero(rs.getString(prefix + "numero"));
		domicilio.setDomi_val(rs.getString(prefix + "domi_val"));
		domicilio.setObservaciones(rs.getString(prefix + "observaciones"));
		domicilio.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		domicilio.setCargo(rs.getString(prefix + "cargo"));
		domicilio.setNomape(rs.getString(prefix + "nomape"));
		domicilio.setProfesion(rs.getString(prefix + "profesion"));
		String provincia_str = rs.getString(prefix + "provincia");		
		int provincia_int= rs.getInt(prefix + "id_provincia");
		if (null != provincia_str) {
			Provincia provincia = new Provincia();
			provincia.setDescripcion(provincia_str);
			provincia.setId(provincia_int);
			domicilio.setProvincia(provincia);
		}

		String localidad_str = rs.getString(prefix + "localidad");
		int localidad_int= rs.getInt(prefix + "id_localidad");
		if (null != localidad_str) {			
			Localidad localidad = new Localidad();
			localidad.setDescripcion(localidad_str);
			localidad.setId(localidad_int);
			domicilio.setLocalidad(localidad);
		}

		return domicilio;
	}

	public static Domicilio getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Domicilio domicilio = new Domicilio();
		domicilio.setId_domicilio(rs.getInt(prefix + "id_domicilio"));
		domicilio.setDomi_tipo(rs.getString(prefix + "domi_tipo"));
		domicilio.setCalle(rs.getString(prefix + "calle"));
		domicilio.setPiso(rs.getString(prefix + "piso"));
		domicilio.setDepto(rs.getString(prefix + "depto"));
		domicilio.setOficina(rs.getString(prefix + "oficina"));
		domicilio.setPostal_codi(rs.getString(prefix + "postal_codi"));
		domicilio.setBarrio(rs.getString(prefix + "barrio"));
		domicilio.setCod_area_telefono(rs.getString(prefix
				+ "cod_area_telefono"));
		domicilio.setTelefono(rs.getString(prefix + "telefono"));
		try {
			domicilio.setCod_area_tel_laboral(rs.getString(prefix
					+ "cod_area_tel_laboral"));
			domicilio.setTel_laboral(rs.getString(prefix + "tel_laboral"));
		} catch (Exception e) {
			domicilio.setCod_area_tel_laboral("");
			domicilio.setTel_laboral("");
		}
		domicilio
				.setCod_area_celular(rs.getString(prefix + "cod_area_celular"));
		domicilio.setCelular(rs.getString(prefix + "celular"));
		domicilio.setObservaciones(rs.getString(prefix + "observaciones"));
		domicilio.setDomi_val(rs.getString(prefix + "domi_val"));
		domicilio.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		domicilio.setAlta_usr(rs.getString(prefix + "alta_usr"));
		domicilio.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		domicilio.setModi_usr(rs.getString(prefix + "modi_usr"));
		domicilio.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		domicilio.setBaja_usr(rs.getString(prefix + "baja_usr"));
		domicilio.setProvincia(new Provincia(rs.getInt(prefix + "provincia"),
				rs.getString(prefix + "provincia_nombre")));
		domicilio.setLocalidad(new Localidad(rs.getInt(prefix + "localidad"),
				rs.getString(prefix + "localidad_nombre")));
		domicilio.setNumero(rs.getString(prefix + "numero"));
		return domicilio;
	}

	public static Domicilio getMappingAfiDomicilio(ResultSet rs, String prefix)
			throws SQLException {
		Domicilio domicilio = new Domicilio();
		domicilio.setDomi_tipo(rs.getString(prefix + "domi_tipo"));
		domicilio.setCalle(rs.getString(prefix + "calle"));
		domicilio.setPiso(rs.getString(prefix + "piso"));
		domicilio.setDepto(rs.getString(prefix + "depto"));
		domicilio.setOficina(rs.getString(prefix + "oficina"));
		domicilio.setPostal_codi(rs.getString(prefix + "postal_codi"));
		domicilio.setBarrio(rs.getString(prefix + "barrio"));
		domicilio.setTelefono(rs.getString(prefix + "telefono"));
		try {
			domicilio.setCod_area_telefono(rs.getString(prefix
					+ "cod_area_telefono"));
			domicilio.setCod_area_tel_laboral(rs.getString(prefix
					+ "cod_area_tel_laboral"));
			domicilio.setTel_laboral(rs.getString(prefix + "tel_laboral"));
			domicilio.setCod_area_celular(rs.getString(prefix
					+ "cod_area_celular"));
			domicilio.setCelular(rs.getString(prefix + "celular"));
		} catch (Exception e) {
			domicilio.setCod_area_telefono("");
			domicilio.setCod_area_tel_laboral("");
			domicilio.setTel_laboral("");
			domicilio.setCod_area_celular("");
			domicilio.setCelular("");
		}
		domicilio.setObservaciones(rs.getString(prefix + "observaciones"));
		domicilio.setDomi_val(rs.getString(prefix + "domi_val"));
		domicilio.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		domicilio.setAlta_usr(rs.getString(prefix + "alta_usr"));
		domicilio.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		domicilio.setModi_usr(rs.getString(prefix + "modi_usr"));
		domicilio.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		domicilio.setBaja_usr(rs.getString(prefix + "baja_usr"));
		domicilio.setProvincia(new Provincia(rs.getInt(prefix + "provincia"),
				null));
		domicilio.setLocalidad(new Localidad(rs.getInt(prefix + "localidad"),
				null));
		domicilio.setNumero(rs.getString(prefix + "numero"));
		return domicilio;
	}

	public static Domicilio getMappingLugarAt(ResultSet rs, String prefix) throws SQLException {
		Domicilio domicilio = new Domicilio();
		domicilio.setId_domicilio(rs.getInt(prefix + "id_domicilio"));
		domicilio.setDomi_tipo(rs.getString(prefix + "domi_tipo"));
		domicilio.setCalle(rs.getString(prefix + "calle"));
		domicilio.setNumero(rs.getString(prefix + "numero"));
		domicilio.setPiso(rs.getString(prefix + "piso"));
		domicilio.setDepto(rs.getString(prefix + "depto"));
		domicilio.setOficina(rs.getString(prefix + "oficina"));
		domicilio.setPostal_codi(rs.getString(prefix + "postal_codi"));
		domicilio.setBarrio(rs.getString(prefix + "barrio"));
		domicilio.setObservaciones(rs.getString(prefix + "observaciones"));
		domicilio.setDomi_val(rs.getString(prefix + "domi_val"));
		domicilio.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		domicilio.setAlta_usr(rs.getString(prefix + "alta_usr"));
		domicilio.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		domicilio.setModi_usr(rs.getString(prefix + "modi_usr"));
		domicilio.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		domicilio.setBaja_usr(rs.getString(prefix + "baja_usr"));
		domicilio.setProvincia(new Provincia(rs.getInt(prefix + "id_provincia"),rs.getString(prefix + "provincia")));
		domicilio.setLocalidad(new Localidad(rs.getInt(prefix + "id_localidad"),rs.getString(prefix + "localidad")));
		
		return domicilio;
	}
	
	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public String getCod_area_tel_laboral() {
		return cod_area_tel_laboral;
	}

	public void setCod_area_tel_laboral(String cod_area_tel_laboral) {
		this.cod_area_tel_laboral = cod_area_tel_laboral;
	}

	public String getTel_laboral() {
		return tel_laboral;
	}

	public void setTel_laboral(String tel_laboral) {
		this.tel_laboral = tel_laboral;
	}

	public String getPlanta() {
		return planta;
	}

	public void setPlanta(String planta) {
		this.planta = planta;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getNomape() {
		return nomape;
	}

	public void setNomape(String nomape) {
		this.nomape = nomape;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calle == null) ? 0 : calle.hashCode());
		result = prime * result + ((depto == null) ? 0 : depto.hashCode());
		result = prime * result
				+ ((domi_tipo == null) ? 0 : domi_tipo.hashCode());
		result = prime * result
				+ ((localidad == null) ? 0 : localidad.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + ((oficina == null) ? 0 : oficina.hashCode());
		result = prime * result + ((piso == null) ? 0 : piso.hashCode());
		result = prime * result
				+ ((postal_codi == null) ? 0 : postal_codi.hashCode());
		result = prime * result
				+ ((provincia == null) ? 0 : provincia.hashCode());
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
		Domicilio other = (Domicilio) obj;
		if (id_domicilio == 0) {
			if (other.getId_domicilio() != 0) {
				return false;
			}
			if (calle == null) {
				if (other.calle != null)
					return false;
			} else if (!calle.equals(other.calle))
				return false;
			if (depto == null) {
				if (other.depto != null)
					return false;
			} else if (!depto.equals(other.depto))
				return false;
			if (domi_tipo == null) {
				if (other.domi_tipo != null)
					return false;
			} else if (!domi_tipo.equals(other.domi_tipo))
				return false;
			if (localidad == null) {
				if (other.localidad != null)
					return false;
			} else if (!localidad.equals(other.localidad))
				return false;
			if (numero == null) {
				if (other.numero != null)
					return false;
			} else if (!numero.equals(other.numero))
				return false;
			if (oficina == null) {
				if (other.oficina != null)
					return false;
			} else if (!oficina.equals(other.oficina))
				return false;
			if (piso == null) {
				if (other.piso != null)
					return false;
			} else if (!piso.equals(other.piso))
				return false;
			if (postal_codi == null) {
				if (other.postal_codi != null)
					return false;
			} else if (!postal_codi.equals(other.postal_codi))
				return false;
			if (provincia == null) {
				if (other.provincia != null)
					return false;
			} else if (!provincia.equals(other.provincia))
				return false;
		}else{
			if(id_domicilio!=other.id_domicilio){
				return false;
			}
		}
		return true;
	}

	public String getProfesion() {
		return profesion;
	}

	public void setProfesion(String profesion) {
		this.profesion = profesion;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	public boolean compareTo(Object aThat) {
	     if (this == aThat) return true;
	     if (!(aThat instanceof Domicilio )) return false;

	     Domicilio  that = (Domicilio )aThat;
	     return
	       ( this.getLocalidadId() == that.getLocalidadId() ) &&
	       ( this.getProvinciaId() == that.getProvinciaId() ) &&
	       ( this.getPiso().equals(that.getPiso()) ) &&
	       ( this.getDepto().equals(that.getDepto()) ) &&
	       ( this.getCalle().equals(that.getCalle()) ) &&
	       ( this.getBarrio().equals(that.getBarrio()) ) &&
	       ( this.getPostal_codi().equals(that.getPostal_codi()) ) &&
	       ( this.getNumero().equals(that.getNumero()) );
	   }

	
}
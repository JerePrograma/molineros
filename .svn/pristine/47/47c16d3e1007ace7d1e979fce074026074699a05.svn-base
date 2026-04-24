package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ar.com.empresas.beans.Actividad;
import ar.com.empresas.beans.Contacto;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.estudioisidro.beans.EstadoGestion;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

/**
 * @author Federico Brachi
 * @version 1.0
 * @created 23-Jul-2010 02:08:54 p.m.
 */
public class Empresa implements EntidadPadronUnificado, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4141619848595289734L;
	private Logger log = Logger.getLogger(this.getClass());
	
	private String cuit;
	private String sucursal;
	private String razon_soc;
	private String nombre_fantasia;
	private String iva;	
	private Seccional seccional;
	private String contacto;
	private String observaciones;
	private Date vigen_fecha;
	private String motivo_baja;
	private Date alta_fecha;
	private String alta_usr;
	private String alta_ip;
	private Date modi_fecha;
	private String modi_usr;
	private String modi_ip;
	private Date baja_fecha;
	private String baja_usr;
	private String baja_ip;
	private RamoEmpresa ramoEmpresa;
	private Actividad actividadPrincipal;
	private Actividad actividadSecundaria;
	private EntidadCamaraEmpresa entidadCamaraEmpresa;
	private List<Domicilio> domicilios;
	//Domicilios reemplaza dom y dom fiscal
	private Domicilio domicilio;
	private Domicilio domicilioFiscal;
	//
	private List<Contacto> contactos;
	//CONTACTO REEMPLAZA CONTACTO ELECTRONICO Y TELEFONOS
	private List<Telefono> telefonos;
	private List<ContactoElectronico> contactosElectronicos;	
		
	private String domicilioAfip;
	private String domicilioRemo;
	private String domicilioEstudio;
	private String impGanancias;
	private String impIva;
	private String monotributo;
	private String integranteSoc;
	private String empleador;
	private String actividadMonotributo;	
//	private String estado;
	private EstadoGestion estado;
	private String cartaDoc;
	private String ubicacionCarpeta;
	private Date fechaUltimoCalculoDeuda;
	private boolean molinera;
	private String destinoCorrespondencia;
	private String CBU;
	private String portaCheque;
	private String caeCai;	
	private String numeroCaeCai;
	
	private List<CuentaBancaria> cuentasBcrias;
	private Regimen regimen;
	
	
	public Empresa() {

	}

	public Empresa(String cuit) {
		this.cuit = cuit;
	}
	
	public Empresa(String cuit, String sucur) {
		this.cuit = cuit;
		this.sucursal=sucur;
	}

	public Empresa(String cuit, String sucur, String razon) {
		this.cuit = cuit;
		this.sucursal = sucur;
		this.razon_soc = razon;

	}
	
	public Empresa(String cuit, String sucur, String razon, int id_ramo) {
		this.cuit = cuit;
		this.sucursal = sucur;
		this.razon_soc = razon;
		this.ramoEmpresa=new RamoEmpresa(id_ramo);

	}
	
	public Empresa(String cuit, String sucur, String razon, int id_ramo, String posicionGanancias, Date bajaFecha) {
		this.cuit = cuit;
		this.sucursal = sucur;
		this.razon_soc = razon;
		this.ramoEmpresa=new RamoEmpresa(id_ramo);
		this.impGanancias=posicionGanancias;
		this.baja_fecha=bajaFecha;

	}

	public Empresa(String cuit2, String sucur, String razon, Integer seccional) {
		this.cuit = cuit2;
		this.sucursal = sucur;
		this.razon_soc = razon;
		if (seccional != null) {
			sucursal = seccional.toString();
		}
	}
	
	public static Empresa getFromPadronAfip(String line){
		Empresa empresa=new Empresa();
		empresa.setCuit(line.substring(0,11));
		empresa.setRazon_soc(line.substring(11,41));
		empresa.setImpGanancias(line.substring(41,43));
		empresa.setImpIva(line.substring(43,45));
		empresa.setMonotributo(line.substring(45,47));
		empresa.setIntegranteSoc(line.substring(47,48));
		empresa.setEmpleador(line.substring(48,49));
		empresa.setEmpleador(line.substring(49,51));
		return empresa;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getRazon_soc() {
		return razon_soc;
	}

	public void setRazon_soc(String razonSoc) {
		razon_soc = razonSoc;
	}

	public String getNombre_fantasia() {
		return nombre_fantasia;
	}

	public void setNombre_fantasia(String nombreFantasia) {
		nombre_fantasia = nombreFantasia;
	}

	public int getId_ramo_empresa() {
		return ramoEmpresa != null ? ramoEmpresa.getId_ramo_empresa() : 0;
	}

	public void setId_ramo_empresa(int idRamoEmpresa) {
		ramoEmpresa = new RamoEmpresa(idRamoEmpresa);
	}
	
	public String getIva() {
		return iva;
	}

	public void setIva(String iva) {
		this.iva = iva;
	}

	public int getId_seccional() {
		return null!=seccional?seccional.getId_seccional():0;
	}

	public void setId_seccional(int diSeccional) {
		if(null!=seccional){
			seccional.setId_seccional(diSeccional);	
		}else{
			seccional=new Seccional(diSeccional);
		}
		 
	}
	
	public String getDescripcionSeccional() {
		return null!=seccional&&seccional.getDescripcion()!=null?seccional.getDescripcion():"";
	}
	
	public void setSeccionalDescripcion(String descSecc) {
		if(null!=seccional){
			seccional.setDescripcion(descSecc);	
		}else{
			seccional=new Seccional(0,descSecc);
		}
		 
	}

	public String getContacto() {
		return contacto!=null?contacto:"";
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public int getId_entidad_cam_empresa() {
		if (entidadCamaraEmpresa == null) {
			return 0;
		}
		return entidadCamaraEmpresa.getId_entidad_cam_empresa();
	}

	public void setId_entidad_cam_empresa(int idEntidadCamEmpresa) {
		entidadCamaraEmpresa = new EntidadCamaraEmpresa(idEntidadCamEmpresa, "");
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Date getVigen_fecha() {
		return vigen_fecha;
	}

	public void setVigen_fecha(Date vigenFecha) {
		vigen_fecha = vigenFecha;
	}

	public String getMotivo_baja() {
		return motivo_baja;
	}

	public void setMotivo_baja(String motivoBaja) {
		motivo_baja = motivoBaja;
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

	public String getAlta_ip() {
		return alta_ip;
	}

	public void setAlta_ip(String altaIp) {
		alta_ip = altaIp;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}
	
	public String getModi_fechaAsString() {
		if(null!=modi_fecha){
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm");
			return sdf.format(modi_fecha);
		}else{
			return "";
		}
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

	public String getModi_ip() {
		return modi_ip;
	}

	public void setModi_ip(String modiIp) {
		modi_ip = modiIp;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
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

	public String getBaja_ip() {
		return baja_ip;
	}

	public void setBaja_ip(String bajaIp) {
		baja_ip = bajaIp;
	}

	public RamoEmpresa getRamoEmpresa() {
		return ramoEmpresa;
	}

	public void setRamoEmpresa(RamoEmpresa ramoEmpresa) {
		this.ramoEmpresa = ramoEmpresa;
	}
	

	public EntidadCamaraEmpresa getEntidadCamaraEmpresa() {
		return entidadCamaraEmpresa;
	}

	public void setEntidadCamaraEmpresa(
			EntidadCamaraEmpresa entidadCamaraEmpresa) {
		this.entidadCamaraEmpresa = entidadCamaraEmpresa;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}
	
	public String getDomicilioAsString() {
		StringBuffer domBuffer=null;
		if(null!=domicilios){
			domBuffer=new StringBuffer();
			for(Domicilio dom: domicilios){				
				domBuffer.append(null!=dom.getCalle()?dom.getCalle():"").append(" ").append(null!=dom.getNumero()?dom.getNumero():"").append(", ")
				.append(null!=dom.getLocalidad()?dom.getLocalidad().getDescripcion():"");
				domBuffer.append(" ").append(null!=dom.getProvincia()?dom.getProvincia().getDescripcion():"").append(" C.P.: ")
				.append(null!=dom.getPostal_codi()?dom.getPostal_codi():"");
				domBuffer.append(" / ");
			}
		}
		return domBuffer!=null?domBuffer.toString():null;
	}
	

	public static Empresa getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
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


	public void setDomicilioFiscal(Domicilio domicilioFiscal) {
		this.domicilioFiscal = domicilioFiscal;
	}

	public Domicilio getDomicilioFiscal() {
		return domicilioFiscal;
	}

	public String getDomicilioAfip() {
		return domicilioAfip;
	}

	public void setDomicilioAfip(String domicilioAfip) {
		this.domicilioAfip = domicilioAfip;
	}

	public String getDomicilioRemo() {
		return domicilioRemo;
	}

	public void setDomicilioRemo(String domicilioRemo) {
		this.domicilioRemo = domicilioRemo;
	}

	public String getDomicilioEstudio() {
		return domicilioEstudio;
	}

	public void setDomicilioEstudio(String domicilioEstudio) {
		this.domicilioEstudio = domicilioEstudio;
	}

	public String getImpGanancias() {
		return impGanancias;
	}

	public void setImpGanancias(String impGanancias) {
		this.impGanancias = impGanancias;
	}

	public String getImpIva() {
		return impIva;
	}

	public void setImpIva(String impIva) {
		this.impIva = impIva;
	}

	public String getMonotributo() {
		return monotributo;
	}

	public void setMonotributo(String monotributo) {
		this.monotributo = monotributo;
	}

	
	public String getIntegranteSoc() {
		return integranteSoc;
	}

	public void setIntegranteSoc(String integranteSoc) {
		this.integranteSoc = integranteSoc;
	}

	public String getEmpleador() {
		return empleador;
	}

	public void setEmpleador(String empleador) {
		this.empleador = empleador;
	}

	public String getActividadMonotributo() {
		return actividadMonotributo;
	}

	public void setActividadMonotributo(String actividadMonotributo) {
		this.actividadMonotributo = actividadMonotributo;
	}	

	public static Empresa getMappingCompleto(ResultSet rs, String prefix)
			throws SQLException {
		Empresa emp = new Empresa(rs.getString(prefix + "cuit"),
				rs.getString(prefix + "sucursal"), rs.getString(prefix
						+ "razon_soc"));
		emp.setNombre_fantasia(rs.getString(prefix + "nombre_fantasia"));
		emp.setId_ramo_empresa(rs.getInt(prefix + "id_ramo_empresa"));
		emp.setActividadPrincipal(new Actividad(rs.getInt("cod_act_prin"), rs.getString("desc_act_prin")));
		emp.setActividadSecundaria(new Actividad(rs.getInt("cod_act_sec"), rs.getString("desc_act_prin")));	
		emp.setId_seccional(rs.getInt(prefix + "id_seccional"));
		emp.setSeccionalDescripcion(rs.getString(prefix + "seccional"));
		emp.setContacto(rs.getString(prefix + "contacto"));
		emp.setId_entidad_cam_empresa(rs.getInt(prefix
				+ "id_entidad_cam_empresa"));
		emp.setObservaciones(rs.getString(prefix + "observaciones"));
		
		emp.setIva(rs.getString("afip"));
				
		emp.setImpGanancias(rs.getString(prefix + "imp_ganancias"));
		emp.setImpIva(rs.getString(prefix + "imp_iva"));
		emp.setMonotributo(rs.getString(prefix + "monotributo"));
		emp.setIntegranteSoc(rs.getString(prefix + "integrante_soc"));
		emp.setEmpleador(rs.getString(prefix + "empleador"));
		emp.setActividadMonotributo(rs.getString("actividad_monotrib"));
		emp.setMolinera(rs.getBoolean(prefix + "molinera"));	
		emp.setDestinoCorrespondencia(rs.getString(prefix+"destino_corr"));
		emp.setCBU(rs.getString(prefix+"CBU"));
		emp.setPortaCheque(rs.getString(prefix+"porta_cheque"));
		emp.setModi_fecha(rs.getDate(prefix+"modi_fecha"));
		emp.setModi_usr(rs.getString(prefix+"modi_usr"));
		emp.setCaeCai(rs.getString(prefix+"cai_cae"));
		emp.setNumeroCaeCai(rs.getString(prefix+"cai_cae_numero"));
		emp.setBaja_fecha(rs.getDate(prefix+"baja_fecha"));
		
		return emp;
	}
	
	public static Empresa getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Empresa emp = new Empresa(rs.getString(prefix + "cuit"),
				rs.getString(prefix + "sucursal"), rs.getString(prefix
						+ "razon_soc"));
		emp.setNombre_fantasia(rs.getString(prefix + "nombre_fantasia"));
		emp.setId_ramo_empresa(rs.getInt(prefix + "id_ramo_empresa"));
		emp.setId_seccional(rs.getInt(prefix + "id_seccional"));
		emp.setContacto(rs.getString(prefix + "contacto"));
		emp.setId_entidad_cam_empresa(rs.getInt(prefix
				+ "id_entidad_cam_empresa"));
		emp.setObservaciones(rs.getString(prefix + "observaciones"));
		emp.setVigen_fecha(rs.getDate(prefix + "vigen_fecha"));
		emp.setMotivo_baja(rs.getString(prefix + "motivo_baja"));
		emp.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		emp.setAlta_usr(rs.getString(prefix + "alta_usr"));
		emp.setAlta_ip(rs.getString(prefix + "alta_ip"));
		emp.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		emp.setModi_usr(rs.getString(prefix + "modi_usr"));
		emp.setModi_ip(rs.getString(prefix + "modi_ip"));
		emp.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		emp.setBaja_usr(rs.getString(prefix + "baja_usr"));
		emp.setBaja_ip(rs.getString(prefix + "baja_ip"));

		try{
			emp.setMolinera(rs.getBoolean(prefix + "molinera"));
		}catch(Exception e){
			
		}
		try {
		if (prefix != null && !prefix.equals("")) {
			emp.setDomicilioAfip(rs.getString(prefix + "domiafip"));
			emp.setDomicilioRemo(rs.getString(prefix + "domiremo"));
			emp.setDomicilioEstudio(rs.getString(prefix + "domiestudio"));
		}
		}catch(Exception e){
			
		}
		return emp;
	}
	public static Empresa getMappingSeguimiento(ResultSet rs)
			throws SQLException {
		Empresa emp = new Empresa();
		emp.setRazon_soc(rs.getString("razon_soc"));
		emp.setCuit(rs.getString("cuit"));
		emp.setContacto(rs.getString("contacto"));
		emp.setFechaUltimoCalculoDeuda(rs.getDate("fecha_calc_deuda"));		
//		emp.setEstado(rs.getString("estado"));
		emp.setEstado(EstadoGestion.getMapping("estado_",rs));
		emp.setMolinera(rs.getBoolean("molinera"));
		emp.setCartaDoc(rs.getString("carta_doc"));
		emp.setUbicacionCarpeta(rs.getString("ubicacion_carpeta"));
		return emp;
	}

	public static Empresa getMappingAfip(ResultSet rs, String prefix)
			throws SQLException {
		Empresa emp = new Empresa(rs.getString(prefix + "cuit"), rs.getString(prefix + "sucursal"), rs.getString(prefix + "razon_soc"));
		emp.setId_ramo_empresa(rs.getInt(prefix + "id_ramo_empresa"));
		emp.setImpGanancias(rs.getString(prefix + "imp_ganancias"));
		emp.setImpIva(rs.getString(prefix + "imp_iva"));
		emp.setMonotributo(rs.getString(prefix + "monotributo"));
		emp.setIntegranteSoc(rs.getString(prefix + "integrante_soc"));
		emp.setCBU(rs.getString(prefix+"CBU"));
		emp.setRegimen(new Regimen(rs.getInt(prefix +"codigo_regimen")) );
		emp.setAlta_fecha(rs.getDate(prefix+"alta_fecha"));
		emp.setAlta_usr(rs.getString(prefix+"alta_usr"));
		emp.setModi_fecha(rs.getDate(prefix+"modi_fecha"));
		emp.setModi_usr(rs.getString(prefix+"modi_usr"));
		emp.setBaja_fecha(rs.getDate(prefix+"baja_fecha"));
		emp.setBaja_usr(rs.getString(prefix+"baja_usr"));
	
		
		return emp;
	}
	
	public String getDescripcion() {
		return razon_soc;
	}

	@Deprecated
	public int getIdSeccional() {
		return getId_seccional();
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cuit == null) ? 0 : cuit.hashCode());
		result = prime * result
				+ ((sucursal == null) ? 0 : sucursal.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Empresa other = (Empresa) obj;
		if (cuit == null) {
			if (other.cuit != null)
				return false;
		} else if (!cuit.equals(other.cuit))
			return false;
		if (sucursal == null) {
			if (other.sucursal != null)
				return false;
		} else if (!sucursal.equals(other.sucursal))
			return false;
		return true;
	}

	
//	public String getEstado() {
//		return estado;
//	}
//
//	public void setEstado(String estado) {
//		this.estado = estado;
//	}
	public EstadoGestion getEstado() {
		return estado;
	}

	public void setEstado(EstadoGestion estado) {
		this.estado = estado;
	}
	
	public Date getFechaUltimoCalculoDeuda() {
		return fechaUltimoCalculoDeuda;
	}

	public void setFechaUltimoCalculoDeuda(Date fechaUltimoCalculoDeuda) {
		this.fechaUltimoCalculoDeuda = fechaUltimoCalculoDeuda;
	}
	
	public String getFechaUltimoCalculoDeudaAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return null!=fechaUltimoCalculoDeuda?sdf.format(fechaUltimoCalculoDeuda):"Sin cálculo de deuda";
	}

	public boolean isMolinera() {
		return molinera;
	}

	public void setMolinera(boolean molinera) {
		this.molinera = molinera;
	}

	public String getCartaDoc() {
		return cartaDoc;
	}

	public void setCartaDoc(String cartaDoc) {
		this.cartaDoc = cartaDoc;
	}

	public String getUbicacionCarpeta() {
		return ubicacionCarpeta;
	}

	public void setUbicacionCarpeta(String ubicacionCarpeta) {
		this.ubicacionCarpeta = ubicacionCarpeta;
	}

	public List<Contacto> getContactos() {
		return contactos;
	}

	public void setContactos(List<Contacto> contactos) {
		this.contactos = contactos;
	}

	public List<Domicilio> getDomicilios() {
		return domicilios;
	}

	public void setDomicilios(List<Domicilio> domicilios) {
		this.domicilios = domicilios;
	}

	public String getDestinoCorrespondencia() {
		return destinoCorrespondencia;
	}

	public void setDestinoCorrespondencia(String destinoCorrespondencia) {
		this.destinoCorrespondencia = destinoCorrespondencia;
	}

	public String getCBU() {
		return CBU;
	}

	public void setCBU(String cBU) {
		CBU = cBU;
	}

	public String getPortaCheque() {
		return portaCheque;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public List<CuentaBancaria> getCuentasBcrias() {
		return cuentasBcrias;
	}

	public void setCuentasBcrias(List<CuentaBancaria> cuentasBcrias) {
		this.cuentasBcrias = cuentasBcrias;
	}

	public void setPortaCheque(String portaCheque) {
		this.portaCheque = portaCheque;
	}
	
	public String getTelefonosConcat(){
		StringBuffer sb=new StringBuffer("");
		if(null!=telefonos){
			for(Telefono tel:telefonos){
				sb.append(tel.toString());
				sb.append(" / ");
			}
		}
		return sb.toString();
	}
	public String getContactosEConcat(String tipo){
		StringBuffer sb=new StringBuffer("");
		if(null!=contactos){
			for(Contacto cont:contactos){
				if(null!=tipo&&!tipo.trim().equals("") && null!=cont.getTipoAsString()&& cont.getTipoAsString().startsWith(tipo.trim())){
				//if(null!=tipo&&!tipo.trim().equals("") && null!=cont.getTipoAsString()&& cont.getTipoAsString().equals(tipo.trim())){
					sb.append(cont.getContactoAsString());
					sb.append(" / ");	
				}else if(null==tipo|| tipo.trim().equals("") ){
					sb.append(cont.getContactoAsString());
					sb.append(" / ");
				}
				
			}
		}
		return sb.toString();
	}
	public String getContactosEConcatSinPersonas(String tipo){
		StringBuffer sb=new StringBuffer("");
		if(null!=contactos){
			for(Contacto cont:contactos){
				if(null!=tipo&&!tipo.trim().equals("") && null!=cont.getTipoAsString() 
						&& cont.getTipoAsString().startsWith(tipo.trim())
						&& cont.getNombreApe() == null){
					sb.append(cont.getContactoAsString());
					sb.append(" / ");	
				}else if(null==tipo|| tipo.trim().equals("") 
						&& cont.getNombreApe() == null){
					sb.append(cont.getContactoAsString());
					sb.append(" / ");
				}
				
			}
		}
		return sb.toString();
	}
	public String getContactosEConcatPersonas(String tipo){
		StringBuffer sb=new StringBuffer("");
		if(null!=contactos){
			for(Contacto cont:contactos){
				if(null!=tipo&&!tipo.trim().equals("") && null!=cont.getTipoAsString() 
						&& cont.getTipoAsString().startsWith(tipo.trim())
						&& cont.getNombreApe() != null){
					sb.append(cont.getContactoAsString());
					sb.append(" / ");	
				}else if(null==tipo|| tipo.trim().equals("") 
						&& cont.getNombreApe() != null){
					sb.append(cont.getContactoAsString());
					sb.append(" / ");
				}
				
			}
		}
		return sb.toString();
	}

	public Actividad getActividadPrincipal() {
		return actividadPrincipal;
	}

	public void setActividadPrincipal(Actividad actividadPrincipal) {
		this.actividadPrincipal = actividadPrincipal;
	}

	public Actividad getActividadSecundaria() {
		return actividadSecundaria;
	}

	public void setActividadSecundaria(Actividad actividadSecundaria) {
		this.actividadSecundaria = actividadSecundaria;
	}
	
	public String getCaeCai() {
		return caeCai;
	}

	public void setCaeCai(String caeCai) {
		this.caeCai = caeCai;
	}

	public String getNumeroCaeCai() {
		return numeroCaeCai;
	}

	public void setNumeroCaeCai(String numeroCaeCai) {
		this.numeroCaeCai = numeroCaeCai;
	}

	public Regimen getRegimen() {
		return regimen;
	}

	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}
	
	public List<Contacto> getContactosPorNombreApe(){
		
		List<Contacto> list = contactos;
		Comparator<Contacto> comparator = new Comparator<Contacto>() {
		    public int compare(Contacto c1, Contacto c2) {
		    	
		    	String nomApe1 = c1.getNombreApe()==null?"":c1.getNombreApe();
		    	String nomApe2 = c2.getNombreApe()==null?"":c2.getNombreApe();
		    	
//		        return (c2.getNombreApe().compareToIgnoreCase(c1.getNombreApe())); 
		    	 return (nomApe1.compareToIgnoreCase(nomApe2)); 
		    }
		};

		Collections.sort(list, comparator); // use the comparator as much as u want
		
		
		return list;
		
	}
	
	
    public List<Contacto> getContactosPorNombreApePersonas(){
		
		List<Contacto> list = new ArrayList<Contacto>();
		for(Contacto c:contactos){
		   if(c.getNombreApe()!=null && !"".equalsIgnoreCase(c.getNombreApe())){
			   list.add(c);
		   }
		}
		
		Comparator<Contacto> comparator = new Comparator<Contacto>() {
		    public int compare(Contacto c1, Contacto c2) {
		    	
		    	String nomApe1 = c1.getNombreApe()==null?"":c1.getNombreApe();
		    	String nomApe2 = c2.getNombreApe()==null?"":c2.getNombreApe();
		    	
//		        return (c2.getNombreApe().compareToIgnoreCase(c1.getNombreApe())); 
		    	 return (nomApe1.compareToIgnoreCase(nomApe2)); 
		    }
		};

		Collections.sort(list, comparator); // use the comparator as much as u want
		
		
		return list;
		
	}

	public String getContactosPorNombreApeConcatenados(){
		
		Map<String, String> nomApeContactos = new HashMap<String, String>();
		StringBuffer sb=new StringBuffer("");
		
		if(null!=contactos){
			for(Contacto cont:contactos){
				
				if(StringUtils.checkNotEmpty(cont.getNombreApe())){
					String clave = (cont.getProfesion()!=null?cont.getProfesion():"")+" "+
								   (cont.getNombreApe()!=null?cont.getNombreApe():"") +" "+
								   (cont.getCargo()!=null?cont.getCargo():"");
					
					if(nomApeContactos.containsKey(clave)){
						String valorContatenados = nomApeContactos.get(clave);
						valorContatenados = valorContatenados.concat(", "+cont.getContactoAsString());
						nomApeContactos.remove(clave);
						nomApeContactos.put(clave, valorContatenados);
					}else{
						nomApeContactos.put(clave, cont.getContactoAsString());
					}
				}
			}
		}
		
		Set<String> claves = nomApeContactos.keySet();
		for (String c : claves) {
			
			sb.append(c + ": " + nomApeContactos.get(c) + " / ");
			
		}
		
		return sb.toString();
	}

}
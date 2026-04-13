package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

/**
 * @author Carlos Rivas
 * @version 1.0
 * @created 14-Jul-2010 03:30:13 p.m.
 */
public class Seccional implements EntidadPadronUnificado, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6807070326287271143L;
	private String cuitEntidad;
	private int id_seccional;
	private String descripcion;
	private String cheque_a_la_orden;
	private String tipo;
	private int id_domicilio;
	private String contacto;
	private String observaciones;
	private Date vigen_fecha;
	private Date alta_fecha;
	private String alta_usr;
	private String alta_ip;
	private Date modi_fecha;
	private String modi_usr;
	private String modi_ip;
	private Date baja_fecha;
	private String baja_usr;
	private String baja_ip;
	private String destino;
	private Domicilio domicilio;
	private String CBU;
	private List<Contacto> contactos;
	private List<CuentaBancaria> ctasBcrias;
	private List<Contacto>plantel;
	
	private boolean ospim;
	private boolean uoma;
	private boolean amtima;
	private String descripcion_uoma;
	private String descripcion_amtima;
	private Integer id_delegacion_sss;
	private Integer imaginaria;
	private List<Delegacion>delegaciones;
	private String modo;
	private String horarioAtencion ; 
	
	private String nroTarjetaRecargable;
	private boolean pagoSeccional;
	
	
	public Seccional() {

	}
	
	public Seccional(int id) {
		this.id_seccional = id;		
	}

	public Seccional(int id, String descripcion) {
		this.id_seccional = id;
		this.descripcion = descripcion;
	}

	public Seccional(int id, String descripcion, String cuitAcreedor) {
		this.id_seccional = id;
		this.descripcion = descripcion;
		this.cuitEntidad = cuitAcreedor;
	}

	/**
	 * @return the id_seccional
	 */
	public int getId() {
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
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the cheque_a_la_orden
	 */
	public String getCheque_a_la_orden() {
		return cheque_a_la_orden;
	}

	/**
	 * @param chequeALaOrden
	 *            the cheque_a_la_orden to set
	 */
	public void setCheque_a_la_orden(String chequeALaOrden) {
		cheque_a_la_orden = chequeALaOrden;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	 * @return the vigen_fecha
	 */
	public Date getVigen_fecha() {
		return vigen_fecha;
	}

	/**
	 * @param vigenFecha
	 *            the vigen_fecha to set
	 */
	public void setVigen_fecha(Date vigenFecha) {
		vigen_fecha = vigenFecha;
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
	 * @return the alta_ip
	 */
	public String getAlta_ip() {
		return alta_ip;
	}

	/**
	 * @param altaIp
	 *            the alta_ip to set
	 */
	public void setAlta_ip(String altaIp) {
		alta_ip = altaIp;
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
	 * @return the modi_ip
	 */
	public String getModi_ip() {
		return modi_ip;
	}

	/**
	 * @param modiIp
	 *            the modi_ip to set
	 */
	public void setModi_ip(String modiIp) {
		modi_ip = modiIp;
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

	/**
	 * @return the baja_ip
	 */
	public String getBaja_ip() {
		return baja_ip;
	}

	/**
	 * @param bajaIp
	 *            the baja_ip to set
	 */
	public void setBaja_ip(String bajaIp) {
		baja_ip = bajaIp;
	}

	public static Seccional getMappingSeccionalParaReintegros(ResultSet rs,
			String prefix) throws SQLException {
		Seccional seccional = new Seccional(rs.getInt(prefix + "id_seccional"),
				rs.getString(prefix + "descripcion"));
		return seccional;
	}

	public static Seccional getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static Seccional getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Seccional seccional = new Seccional();
		seccional.setId_seccional(rs.getInt(prefix + "id_seccional"));
		seccional.setDescripcion(rs.getString(prefix + "descripcion"));
		seccional.setCheque_a_la_orden(rs.getString(prefix
				+ "cheque_a_la_orden"));
		seccional.setTipo(rs.getString(prefix + "tipo"));
		seccional.setId_domicilio(rs.getInt(prefix + "id_domicilio"));
		seccional.setContacto(rs.getString(prefix + "contacto"));
		seccional.setObservaciones(rs.getString(prefix + "observaciones"));
		seccional.setVigen_fecha(rs.getDate(prefix + "vigen_fecha"));
		seccional.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		seccional.setAlta_usr(rs.getString(prefix + "alta_usr"));
		seccional.setAlta_ip(rs.getString(prefix + "alta_ip"));
		seccional.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		seccional.setModi_usr(rs.getString(prefix + "modi_usr"));
		seccional.setModi_ip(rs.getString(prefix + "modi_ip"));
		seccional.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		seccional.setBaja_usr(rs.getString(prefix + "baja_usr"));
		seccional.setBaja_ip(rs.getString(prefix + "baja_ip"));
		try{
			seccional.setDestino(rs.getString(prefix + "destino"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return seccional;
	}
	
	public static Seccional getMappingCompleto(ResultSet rs, String prefix)
			throws SQLException {
		Seccional seccional = new Seccional();
		
		seccional.setCuitEntidad(rs.getString("cuit"));
		seccional.setDescripcion(rs.getString("descripcion"));
		seccional.setId_seccional(rs.getInt("id_seccional"));
		seccional.setContacto(rs.getString("contacto"));
		seccional.setObservaciones(rs.getString("observaciones"));
		seccional.setDestino(rs.getString("destino_corr"));
		seccional.setCheque_a_la_orden(rs.getString("porta_cheque"));
		seccional.setCBU(rs.getString("cbu"));
		Domicilio domicilio=new Domicilio();
		domicilio.setId_domicilio(rs.getInt("id_domicilio"));
		domicilio.setDomi_tipo(rs.getString("domi_tipo"));
		domicilio.setCalle(rs.getString("calle"));
		domicilio.setPiso(rs.getString("piso"));
		domicilio.setDepto(rs.getString("depto"));
		domicilio.setOficina(rs.getString("oficina"));
		domicilio.setPostal_codi(rs.getString("postal_codi"));
		domicilio.setBarrio(rs.getString("barrio"));
		domicilio.setObservaciones(rs.getString("observaciones_dom"));
		domicilio.setDomi_val(rs.getString("domi_val"));
		domicilio.setNumero(rs.getString("numero"));
		Localidad loca=new Localidad();
		loca.setDescripcion(rs.getString("localidad"));
		domicilio.setLocalidad(loca);
		Provincia prov=new Provincia();
		prov.setDescripcion(rs.getString("provincia"));
		domicilio.setProvincia(prov);		
		seccional.setDomicilio(domicilio);
		
		
		if(seccional.getContacto()!=null){
			List<Contacto> contactos=new ArrayList<Contacto>();
			ContactoElectronico cont = new ContactoElectronico();
			cont.setTipo(ContactoElectronico.Tipo.PERSONAL);
			cont.setContacto(seccional.getContacto());
			Contacto contacto = new Contacto();
			contacto.setContacto(cont);
			contactos.add(contacto);
			seccional.setContactos(contactos);
		}
		
		return seccional;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_seccional;
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
		Seccional other = (Seccional) obj;
		if (id_seccional != other.id_seccional)
			return false;
		return true;
	}

	public String getCuit() {
		return cuitEntidad;
	}

	public String getSucursal() {
		return String.valueOf(id_seccional);
	}

	public int getIdSeccional() {
		return id_seccional;
	}

	public void setCuitEntidad(String cuitEntidad) {
		this.cuitEntidad = cuitEntidad;
	}

	public String getCuitEntidad() {
		return cuitEntidad;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}
	
	public List<Domicilio> getDomicilios() {
		
		if(null!=domicilio){
			List<Domicilio> domicilios=new ArrayList<Domicilio>();  
			domicilios.add(domicilio);
			return domicilios;
		}
		return null;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public int getId_seccional() {
		return id_seccional;
	}

	public String getDestinoCorrespondencia() {
		return destino;
	}
	
	public String getCBU() {		
		return CBU;
	}
	public void setCBU(String cbu) {		
		this.CBU=cbu;
	}

	public String getPortaCheque() {		
		return cheque_a_la_orden;
	}

	public List<Contacto> getContactos() {
		return contactos;
	}

	public void setContactos(List<Contacto> contactos) {
		this.contactos = contactos;
	}
	
	public List<CuentaBancaria> getCuentasBcrias() {	
		return ctasBcrias;
	}

	@Override
	public List<Contacto> getContactosPorNombreApe() {
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
	
	@Override
	public List<Contacto> getContactosPorNombreApePersonas() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isOspim() {
		return ospim;
	}

	public void setOspim(boolean ospim) {
		this.ospim = ospim;
	}

	public boolean isUoma() {
		return uoma;
	}

	public void setUoma(boolean uoma) {
		this.uoma = uoma;
	}

	public boolean isAmtima() {
		return amtima;
	}

	public void setAmtima(boolean amtima) {
		this.amtima = amtima;
	}

	public String getDescripcion_uoma() {
		return descripcion_uoma;
	}

	public void setDescripcion_uoma(String descripcion_uoma) {
		this.descripcion_uoma = descripcion_uoma;
	}

	public String getDescripcion_amtima() {
		return descripcion_amtima;
	}

	public void setDescripcion_amtima(String descripcion_amtima) {
		this.descripcion_amtima = descripcion_amtima;
	}

	public Integer getId_delegacion_sss() {
		return id_delegacion_sss;
	}

	public void setId_delegacion_sss(Integer id_delegacion_sss) {
		this.id_delegacion_sss = id_delegacion_sss;
	}

	public Integer getImaginaria() {
		return imaginaria;
	}

	public void setImaginaria(Integer imaginaria) {
		this.imaginaria = imaginaria;
	}

	public List<Delegacion> getDelegaciones() {
		return delegaciones;
	}

	public void setDelegaciones(List<Delegacion> delegaciones) {
		this.delegaciones = delegaciones;
	}

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public String getHorarioAtencion () {
		return horarioAtencion ;
	}

	public void setHorarioAtencion (String horarioDeAtencion ) {
		this.horarioAtencion = horarioDeAtencion ;
	}
	
	
	public List<Contacto> getPlantel() {
		return plantel;
	}

	public void setPlantel(List<Contacto> plantel) {
		this.plantel = plantel;
	}

	public String getNroTarjetaRecargable() {
		return nroTarjetaRecargable;
	}

	public void setNroTarjetaRecargable(String nroTarjetaRecargable) {
		this.nroTarjetaRecargable = nroTarjetaRecargable;
	}

	public boolean isPagoSeccional() {
		return pagoSeccional;
	}

	public void setPagoSeccional(boolean pagoSeccional) {
		this.pagoSeccional = pagoSeccional;
	}
	
	
	
}
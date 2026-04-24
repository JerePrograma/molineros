package ar.com.ospim.correspondencia.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.Prestador;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;

public class ItemCorrespondencia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6715144189201560252L;
	
	private long id;
	private long id_correspondencia;

	private CabeceraCorrespondencia cabecera;
	private ListaPaquete listaPaquete;
	private Paquete paquete;

	private String entradaSalida; // ENTRADA, SALIDA
	private String tipoRemitenteDestinatario; // PRESTADOR, PROVEEDOR,
	// SECCIONAL, FARMACIA,
	// AFILIADO, OTRO //APORTEMPL

	private Afiliado afiliado;
	private Farmacia farmacia;
	private String otro;
	private Prestador prestador;

	private Empresa proveedor;
	private Seccional seccional;

	private String edificio;
	private String edificioDescripcion;
	private String sector;
	private String sectorDescripcion;
	private String usuario;
	
	private String empresa_remite;
	private String empresa_rem_Descripcion;
	private String sector_remite;
	private String sector_rem_Descripcion;
	private String usuario_remite;
	
	private String contenido;
	private String estado;

	private int id_punto_venta;
	private String compro_tipo;
	private String compro_nro;
	private String cuit;
	private String compro_letra;
	private int compro_sucu;
	private BigDecimal importe;
	private Date compro_periodo; //solo para auditoria de farmacia (Tittarelli) 
	private Date fecha_emision;
	private Date fecha_vencimiento;

	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String alta_sector;
	private String seguimientoPaquete;
	private Integer idCRMContacto;
	private RemitenteDestinatario remiDest;

	
//	private String marcaEdit; // marca para fines administrativos en sesion
	static HashMap<String, String> empresaHM = new HashMap<String,String>();
	static HashMap<String, String> grupoHM = new HashMap<String,String>();

	public ItemCorrespondencia(long numeroCorrespondencia, String tipoRegistro,
			long paquete, String tipoEnvio, String tipoRemitente, String cuil,
			int inte, String nombreAfiliado, String apellidoAfiliado,
			String id_farmacia, String id_farmacia_serial, String descFarmacia, String otros, int idPrestador,
			String descPrestador, String cuitEntidad, String sucursalEntidad,
			String descEmpresa, int idSeccional, String descSeccional,
			String tipoCompro, String letraCompro, int sucu, String nroCompro,
			String importeTotal, Date periodoCompro, String edificioDestino, String usuarioDestino,
			String sectorDestino, String empresa_remite, String sector_remite,
			String usuario_remite, String contenido, Date fechaE, Date fechaV, String seguimiento_paquete, 
			Integer id_CRM_Contacto) {

		super();
		completarEmpresasGrupos();
		
		this.id_correspondencia = numeroCorrespondencia;
		this.entradaSalida = tipoRegistro;
		this.tipoRemitenteDestinatario = tipoRemitente;
		this.afiliado = new Afiliado();
		this.afiliado.setCuil_titular(cuil);
		this.afiliado.setInte(inte);
		this.afiliado.setNombre(nombreAfiliado);
		this.afiliado.setApellido(apellidoAfiliado);
		this.farmacia = new Farmacia(Integer.parseInt(id_farmacia_serial),id_farmacia,descFarmacia);
		this.prestador = new Prestador();
		this.prestador.setId_prestador(idPrestador);
		this.prestador.setDescripcion(descPrestador);		
		this.proveedor = new Empresa();
		this.proveedor.setCuit(cuitEntidad);
		this.proveedor.setSucursal(sucursalEntidad);
		this.proveedor.setRazon_soc(descEmpresa);
		this.seccional = new Seccional(idSeccional,descSeccional);
		this.compro_tipo = tipoCompro;
		this.compro_sucu = sucu;
		this.compro_letra = letraCompro;
		this.compro_nro = nroCompro;
		this.importe = importeTotal != null && importeTotal.length() > 0 ? new BigDecimal(importeTotal) : BigDecimal.ZERO;
		this.compro_periodo = periodoCompro;
		this.edificio = edificioDestino;
		this.usuario = usuarioDestino;
		this.sector = sectorDestino;
		this.empresa_remite = empresa_remite;
		this.sector_remite = sector_remite;
		this.usuario_remite = usuario_remite;
		this.contenido = contenido;
		this.fecha_emision = fechaE;
		this.fecha_vencimiento = fechaV;
		this.listaPaquete = new ListaPaquete();
		this.otro = otros;
		this.seguimientoPaquete = seguimiento_paquete;
		this.idCRMContacto = id_CRM_Contacto;
	}

	public ItemCorrespondencia() {
		super();
		completarEmpresasGrupos();
	}

	private void completarEmpresasGrupos() {
		empresaHM = new HashMap<String,String>();
		grupoHM = new HashMap<String,String>();
		List<Organization> empresas = null;
		List<UserGroup> grupos = null;
		try {
			empresas = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		for (Iterator<Organization> iterator = empresas.iterator(); iterator.hasNext();) {
			Organization org = iterator.next();
			empresaHM.put(String.valueOf(org.getOrganizationId()), org.getName());
		}
		
		try {
			grupos = UserGroupLocalServiceUtil.getUserGroups(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		for (Iterator<UserGroup> iterator = grupos.iterator(); iterator.hasNext();) {
			UserGroup usrGrp = (UserGroup) iterator.next();
			grupoHM.put(String.valueOf(usrGrp.getUserGroupId()), usrGrp.getName());
		}
	}

//	public String getMarcaEdit() {
//		return marcaEdit;
//	}
//
//	public void setMarcaEdit(String marcaEdit) {
//		this.marcaEdit = marcaEdit;
//	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEntradaSalida() {
		return entradaSalida;
	}

	public void setEntradaSalida(String entradaSalida) {
		this.entradaSalida = entradaSalida;
	}

	public String getTipoRemitenteDestinatario() {
		return tipoRemitenteDestinatario;
	}

	public void setTipoRemitenteDestinatario(String tipoRemitenteDestinatario) {
		this.tipoRemitenteDestinatario = tipoRemitenteDestinatario;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public Empresa getProveedor() {
		return proveedor;
	}

	public void setProveedor(Empresa proveedor) {
		this.proveedor = proveedor;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public Farmacia getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(Farmacia farmacia) {
		this.farmacia = farmacia;
	}

	public String getOtro() {
		return otro;
	}

	public void setOtro(String otro) {
		this.otro = otro;
	}

	public String getEdificio() {
		return edificio;
	}

	public void setEdificio(String edificio) {
		this.edificio = edificio;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public long getId_correspondencia() {
		return id_correspondencia;
	}

	public void setId_correspondencia(long idCorrespondencia) {
		id_correspondencia = idCorrespondencia;
	}

	public int getId_punto_venta() {
		return id_punto_venta;
	}

	public void setId_punto_venta(int idPuntoVenta) {
		id_punto_venta = idPuntoVenta;
	}

	public String getCompro_tipo() {
		return compro_tipo;
	}

	public void setCompro_tipo(String comproTipo) {
		compro_tipo = comproTipo;
	}

	public String getCompro_nro() {
		return compro_nro;
	}

	public void setCompro_nro(String comproNro) {
		compro_nro = comproNro;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getCompro_letra() {
		return compro_letra;
	}

	public void setCompro_letra(String comproLetra) {
		compro_letra = comproLetra;
	}

	public int getCompro_sucu() {
		return compro_sucu;
	}

	public void setCompro_sucu(int comproSucu) {
		compro_sucu = comproSucu;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
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

	public Date getModi_fecha() {
		return modi_fecha;
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

	public Date getBaja_fecha() {
		return baja_fecha;
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

	public ListaPaquete getListaPaquete() {
		return listaPaquete;
	}

	public void setListaPaquete(ListaPaquete listaPaquete) {
		this.listaPaquete = listaPaquete;
	}

	public Date getFecha_emision() {
		return fecha_emision;
	}

	public void setFecha_emision(Date fechaEmision) {
		fecha_emision = fechaEmision;
	}

	public Date getFecha_vencimiento() {
		return fecha_vencimiento;
	}

	public void setFecha_vencimiento(Date fechaVencimiento) {
		fecha_vencimiento = fechaVencimiento;
	}

	public Paquete getPaquete() {
		return paquete;
	}

	public void setPaquete(Paquete paquete) {
		this.paquete = paquete;
	}

	public String getDescRemitenteDestinatario() {
		String desc = "";
		if (this.tipoRemitenteDestinatario.equals("AFILIADO")) {
			desc = this.afiliado.getApeNombre();
		}
		if (this.tipoRemitenteDestinatario.equals("FARMACIA")) {
			desc = this.farmacia.getDescripcion();
		}
		if (this.tipoRemitenteDestinatario.equals("PRESTADOR")) {
			desc = this.prestador.getDescripcion();
		}
		if (this.tipoRemitenteDestinatario.equals("PROVEEDOR")) {
			desc = this.proveedor.getDescripcion();
		}
		if (this.tipoRemitenteDestinatario.equals("SECCIONAL")) {
			desc = this.seccional.getDescripcion();
		}
		if (this.tipoRemitenteDestinatario.equals("OTROS")) {
			desc = this.otro;
		}
		if (this.tipoRemitenteDestinatario.equals("SSS")) {
			desc = this.otro;
		}
		if (this.tipoRemitenteDestinatario.equals("DRISIDRO")) {
			desc = this.otro;
		}
		if (this.tipoRemitenteDestinatario.equals("OMINT")) {
			desc = this.otro;
		}
		if (this.tipoRemitenteDestinatario.equals("PREVENCION")) {
			desc = this.otro;
		}
		if (this.tipoRemitenteDestinatario.equals("HTALALEMAN")) {
			desc = this.otro;
		}
		if (this.tipoRemitenteDestinatario.equals("USUARIO") && this.entradaSalida.equals("SALIDA")) {
			desc = this.getEdificioDescripcion()  + "/" +  
				   this.getSectorDescripcion() + "-" +		
				   this.getUsuario()	;
		}
		if (this.tipoRemitenteDestinatario.equals("USUARIO") && this.entradaSalida.equals("ENTRADA")) {
			desc = this.getEmpresa_rem_Descripcion()  + "/" +  
				   this.getSector_rem_Descripcion() + "-" +		
				   this.getUsuario_remite()	;
		}
		if (this.tipoRemitenteDestinatario.equals("APORTEMPL")) {
//			desc = this.proveedor.getDescripcion();
			desc = this.proveedor.getCuit();
		}
		if (this.tipoRemitenteDestinatario.equals("EMAIL")) {
			desc = this.otro;
		}
		if (this.tipoRemitenteDestinatario.equals("FUTUROAFI")) {
			desc = this.otro;
		}
		return desc;
	}

	public String getCodRemitenteDestinatario() {
		String desc = "";
		if (this.tipoRemitenteDestinatario.equals("AFILIADO")) {
			desc = this.afiliado.getCuil_titular() + " - "
					+ this.afiliado.getInteAsString();
		}
		if (this.tipoRemitenteDestinatario.equals("FARMACIA")) {
//			desc = String.valueOf(this.farmacia.getId_farmacia());
			desc = this.farmacia.getCodigo();
		}
		if (this.tipoRemitenteDestinatario.equals("PRESTADOR")) {
			desc = this.prestador.getCuit();
//			desc = this.prestador.getId_prestadorString();
		}
		if (this.tipoRemitenteDestinatario.equals("PROVEEDOR")) {
			desc = this.proveedor.getCuit(); //+ " - "
//					+ this.proveedor.getSucursal();
		}
		if (this.tipoRemitenteDestinatario.equals("SECCIONAL")) {
			desc = String.valueOf(this.seccional.getIdSeccional());
		}
		if (this.tipoRemitenteDestinatario.equals("OTROS")) {
			desc = "";
		}
		if (this.tipoRemitenteDestinatario.equals("SSS")) {
			desc = "";
		}
		if (this.tipoRemitenteDestinatario.equals("DRISIDRO")) {
			desc = "";
		}
		if (this.tipoRemitenteDestinatario.equals("OMINT")) {
			desc = "";
		}
		if (this.tipoRemitenteDestinatario.equals("PREVENCION")) {
			desc = "";
		}
		if (this.tipoRemitenteDestinatario.equals("USUARIO")) {
			desc = "";
		}
		if (this.tipoRemitenteDestinatario.equals("APORTEMPL")) {
			desc = this.proveedor.getCuit(); //+ " - "
//					+ this.proveedor.getSucursal();
		}
		if (this.tipoRemitenteDestinatario.equals("HTALALEMAN")) {
			desc = "";
		}
		if (this.tipoRemitenteDestinatario.equals("EMAIL")) {
			desc = "";
		}
		return desc;
	}

	public String getRemitente(){
		
		String remitente = "";
		
		if(entradaSalida.equalsIgnoreCase("ENTRADA")){
			remitente = getCodRemitenteDestinatario() + " " + getDescRemitenteDestinatario();
		}
		if(entradaSalida.equalsIgnoreCase("SALIDA")){
//			remitente = this.getEdificioDescripcion() + " / " + this.getSectorDescripcion() + " - " + this.getUsuario() ;
			remitente = this.getEmpresa_rem_Descripcion() + " / " + this.getSector_rem_Descripcion() + " - " + this.getUsuario_remite() ;
		}
		
		return remitente;
	}
	
	public String getDestinatario(){
		
		String destinatario = "";
		
		if(entradaSalida.equalsIgnoreCase("ENTRADA")){
			destinatario = this.getEdificioDescripcion() + " / " + this.getSectorDescripcion() + " - " + this.getUsuario() ;
		}
		if(entradaSalida.equalsIgnoreCase("SALIDA")){
			destinatario = getCodRemitenteDestinatario() + " " + getDescRemitenteDestinatario();
		}

		return destinatario;
	}
	
	public String getComprobanteString() {
		if (compro_nro != null && compro_nro.length() > 0) {
			return compro_tipo + "-" + compro_letra + "-" + compro_sucu + "-" + compro_nro;
		}
		return "";
	}

	public static ItemCorrespondenciaTotal getMappingItemCorrespondencia(ResultSet rs, String prefix) throws Exception {
		
		ItemCorrespondenciaTotal corr = new ItemCorrespondenciaTotal();

		corr.setId(rs.getLong(prefix + "id"));
		corr.setId_correspondencia(rs.getLong(prefix + "id_correspondencia"));
		corr.setEntradaSalida(rs.getString(prefix + "entrada_salida"));
		corr.setTipoRemitenteDestinatario(rs.getString(prefix+ "tipo_remitente_destinatario"));
		corr.setEdificio(rs.getString(prefix + "edificio"));
		corr.setSector(rs.getString(prefix + "sector"));
		corr.setUsuario(rs.getString(prefix + "usuario"));
		corr.setEmpresa_remite(rs.getString(prefix + "empresa_remite"));
		corr.setSector_remite(rs.getString(prefix + "sector_remite"));
		corr.setUsuario_remite(rs.getString(prefix + "usuario_remite"));
		corr.setContenido(rs.getString(prefix + "contenido"));

		corr.setOtro(rs.getString(prefix + "otro"));

		corr.setCompro_nro(rs.getString(prefix + "compro_nro"));
		corr.setCompro_tipo(rs.getString(prefix + "compro_tipo"));
		corr.setId_punto_venta(rs.getInt(prefix + "id_punto_venta"));
		corr.setCuit(rs.getString(prefix + "cuit"));
		corr.setCompro_letra(rs.getString(prefix + "compro_letra"));
		corr.setCompro_sucu(rs.getInt(prefix + "compro_sucu"));
		corr.setCompro_periodo(rs.getDate(prefix + "compro_periodo"));

		corr.setFecha_emision(rs.getDate(prefix + "fecha_emision"));
		corr.setFecha_vencimiento(rs.getDate(prefix + "fecha_vencimiento"));

		corr.setImporte(rs.getBigDecimal(prefix + "importe"));

		corr.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		corr.setAlta_usr(rs.getString(prefix + "alta_usr"));
		corr.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		corr.setModi_usr(rs.getString(prefix + "modi_usr"));
		corr.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		corr.setBaja_usr(rs.getString(prefix + "baja_usr"));
		corr.setAlta_sector(rs.getString(prefix + "alta_sector"));

		corr.setEstado(rs.getString(prefix + "estado"));
		corr.setSeguimientoPaquete(rs.getString(prefix +"seguimiento_paquete"));
		corr.setIdCRMContacto(rs.getInt(prefix +"id_crm_contacto"));
		
		String prefixAfi   = "a_";
		String prefixFarm  = "f_";
		String prefixProv  = "e_";
		String prefixPrest = "prs__";
		String prefixSec   = "s_";
		String prefixPaq   = "lp_";
		
		Afiliado afi = new Afiliado();
		afi.setCuil_titular(rs.getString(prefixAfi + "cuil_titular"));
		afi.setInte(rs.getInt(prefixAfi + "inte"));
		afi.setId_ospim(rs.getInt(prefixAfi + "id_ospim") );
		afi.setId_amtima(rs.getInt(prefixAfi + "id_amtima") );
		afi.setId_uoma(rs.getInt(prefixAfi + "id_uoma") );
		afi.setApellido(rs.getString(prefixAfi + "apellido"));
		afi.setNombre(rs.getString(prefixAfi + "nombre"));		
		afi.setDocumento_tipo(rs.getString(prefixAfi + "documento_tipo"));
		afi.setDocu_numero(rs.getString(prefixAfi + "docu_numero"));
		afi.setSexo(rs.getString(prefixAfi + "sexo"));   
		afi.setCuil(rs.getString(prefixAfi + "cuil"));
		
		Farmacia farm = new Farmacia();
		farm.setId_farmacia(rs.getInt(prefixFarm + "id_farmacia"));
		farm.setFarmacia(rs.getString(prefixFarm + "farmacia"));
//		farm.setCuit(rs.getString(prefixFarm + "cuit"));
		farm.setCodigo(rs.getString(prefixFarm + "codigo"));
		farm.setCodigoFarmacia(rs.getString(prefixFarm + "cod_farm"));
//		farm.setSucursal(rs.getString(prefixFarm + "sucursal"));
		
		Prestador pres = new Prestador();
		pres.setId_prestador(rs.getInt(prefixPrest + "id_prestador"));
		pres.setDescripcion(rs.getString(prefixPrest + "descripcion"));
		pres.setCuit(rs.getString(prefixPrest + "cuit"));
		pres.setId_seccional(rs.getInt(prefixPrest + "id_seccional"));
		pres.setId_tipo_prestador(rs.getInt(prefixPrest + "id_tipo_prestador") );
		
		Seccional sec = new Seccional();    
		sec.setId_seccional(rs.getInt(prefixSec + "id_seccional"));
		sec.setDescripcion(rs.getString(prefixSec + "descripcion"));
		    
		Empresa emp = new Empresa();
		emp.setCuit(rs.getString(prefixProv + "cuit"));
		emp.setSucursal(rs.getString(prefixProv + "sucursal"));
		emp.setNombre_fantasia(rs.getString(prefixProv + "nombre_fantasia"));		
		emp.setRazon_soc(rs.getString(prefixProv + "razon_soc"));
		emp.setId_seccional(rs.getInt(prefixProv + "id_seccional") );

		ListaPaquete paq = new ListaPaquete();
		paq.setId(Long.valueOf(String.valueOf(rs.getInt(prefixPaq +"id"))) );
		paq.setId_paquete(rs.getInt(prefixPaq + "id_paquete"));
		paq.setId_item_correspondencia(Long.valueOf(String.valueOf(rs.getInt(prefixPaq + "id_item_correspondencia"))));
		paq.setAlta_fecha(rs.getDate(prefixPaq + "alta_fecha"));
		paq.setAlta_usr(rs.getString(prefixPaq + "alta_usr")); 
		paq.setPaq_descripcion(rs.getString(prefixPaq + "descripcion"));
		paq.setPaq_estado(rs.getString(prefixPaq + "estado"));
		
//		CabeceraCorrespondencia cab = new CabeceraCorrespondencia();
//		cab.setId_correspondencia(rs.getLong(prefix + "id_correspondencia"));		
//		cab.setLugarRecepEmision(rs.getString(prefix + "lugar_recep_emision"));
//		cab.setLugarDescription( empresaHM.get( Long.parseLong(rs.getString(prefix + "lugar")) ));
//		cab.setFecha(rs.getTimestamp(prefix + "fecha_recep_emision"));
//		cab.setTipoRegistro(rs.getString(prefix + "tipo_registro"));
//		cab.setTipoEnvio(rs.getString(prefix + "tipo_envio"));	
//		cab.setOblea(rs.getString(prefix + "oblea"));
		
		corr.setCabecera(CabeceraCorrespondencia.getMapping(rs, "cab_"));
		corr.setPrestador(pres);
		corr.setProveedor(emp);
		corr.setSeccional(sec);
		corr.setAfiliado(afi);
		corr.setFarmacia(farm);
		corr.setListaPaquete(paq);

		corr.setTotal_registros(rs.getInt("total_registros_v"));
		return corr;
	}
	public String getEdificioDescripcion() {
		if(edificio != null && edificioDescripcion == null){
			edificioDescripcion = empresaHM.get(edificio);
		}
		return edificioDescripcion;
	}

	public void setEdificioDescripcion(String edificioDescripcion) {
		this.edificioDescripcion = edificioDescripcion;
	}

	public String getSectorDescripcion() {
		if(sector != null && sectorDescripcion == null){
			sectorDescripcion = grupoHM.get(sector);
		}
		
		return sectorDescripcion;
	}

	public void setSectorDescripcion(String sectorDescripcion) {
		this.sectorDescripcion = sectorDescripcion;
	}

	public String getEmpresa_remite() {
		return empresa_remite;
	}

	public void setEmpresa_remite(String empresa_remite) {
		this.empresa_remite = empresa_remite;
	}

	public String getEmpresa_rem_Descripcion() {
		if(empresa_remite != null && empresa_rem_Descripcion == null){
			empresa_rem_Descripcion = empresaHM.get(empresa_remite);
		}
		return empresa_rem_Descripcion;
	}

	public void setEmpresa_rem_Descripcion(String empresa_rem_Descripcion) {
		this.empresa_rem_Descripcion = empresa_rem_Descripcion;
	}

	public String getSector_remite() {
		return sector_remite;
	}

	public void setSector_remite(String sector_remite) {
		this.sector_remite = sector_remite;
	}

	public String getSector_rem_Descripcion() {
		if(sector_remite != null && sector_rem_Descripcion == null){
			sector_rem_Descripcion = grupoHM.get(sector_remite);
		}
		return sector_rem_Descripcion;
	}

	public void setSector_rem_Descripcion(String sector_rem_Descripcion) {
		this.sector_rem_Descripcion = sector_rem_Descripcion;
	}

	public String getUsuario_remite() {
		return usuario_remite;
	}

	public void setUsuario_remite(String usuario_remite) {
		this.usuario_remite = usuario_remite;
	}

	public String getAlta_sector() {
		return alta_sector;
	}

	public void setAlta_sector(String alta_sector) {
		this.alta_sector = alta_sector;
	}
	
	public String toString(){
		String result = "";
		
		result += getId_correspondencia() + 
				  getId() + 
				  getTipoRemitenteDestinatario() + 
				  getCodRemitenteDestinatario()==null?"":getCodRemitenteDestinatario() + 
				  getDescRemitenteDestinatario() +
				  getDestinatario()+
				  getRemitente()+
				  getContenido()+
				  getComprobanteString();
				
		return result;
	}

	public CabeceraCorrespondencia getCabecera() {
		return cabecera;
	}

	public void setCabecera(CabeceraCorrespondencia cabecera) {
		this.cabecera = cabecera;
	}

	public Date getCompro_periodo() {
		return compro_periodo;
	}

	public void setCompro_periodo(Date compro_periodo) {
		this.compro_periodo = compro_periodo;
	}

	public String getSeguimientoPaquete() {
		return seguimientoPaquete;
	}

	public void setSeguimientoPaquete(String seguimientoPaquete) {
		this.seguimientoPaquete = seguimientoPaquete;
	}

	public Integer getIdCRMContacto() {
		return idCRMContacto;
	}

	public void setIdCRMContacto(Integer idCRMContacto) {
		this.idCRMContacto = idCRMContacto;
	}
	
	public RemitenteDestinatario getRemiDest() {
		return remiDest;
	}

	public void setRemiDest(RemitenteDestinatario remiDest) {
		this.remiDest = remiDest;
	}

	public static class RemitenteDestinatario{
		
		private String tipoRemitenteDestinatario; // PRESTADOR, PROVEEDOR,
		// SECCIONAL, FARMACIA,
		// AFILIADO, OTRO //APORTEMPL

		private Afiliado afiliado;
		private Farmacia farmacia;
		private String otro;
		private Prestador prestador;

		private Empresa proveedor;
		private Seccional seccional;

//		private String edificio;
//		private String edificioDescripcion;
//		private String sector;
//		private String sectorDescripcion;
//		private String usuario;
		
		private String empresa_remite;
		private String empresa_rem_Descripcion;
		private String sector_remite;
		private String sector_rem_Descripcion;
		private String usuario_remite;
		
		public RemitenteDestinatario(String tipoRemitenteDestinatario, Afiliado afiliado, Farmacia farmacia, String otro,
				Prestador prestador, Empresa proveedor, Seccional seccional, String edificio,
				String edificioDescripcion, String sector, String sectorDescripcion, String usuario) {
			super();
			this.tipoRemitenteDestinatario = tipoRemitenteDestinatario;
			this.afiliado = afiliado;
			this.farmacia = farmacia;
			this.otro = otro;
			this.prestador = prestador;
			this.proveedor = proveedor;
			this.seccional = seccional;
			this.empresa_remite = edificio;
			this.empresa_rem_Descripcion = edificioDescripcion;
			this.sector_remite = sector;
			this.sector_rem_Descripcion = sectorDescripcion;
			this.usuario_remite = usuario;
		}

		public String getTipoRemitenteDestinatario() {
			return tipoRemitenteDestinatario;
		}

		public void setTipoRemitenteDestinatario(String tipoRemitenteDestinatario) {
			this.tipoRemitenteDestinatario = tipoRemitenteDestinatario;
		}

		public Afiliado getAfiliado() {
			return afiliado;
		}

		public void setAfiliado(Afiliado afiliado) {
			this.afiliado = afiliado;
		}

		public Farmacia getFarmacia() {
			return farmacia;
		}

		public void setFarmacia(Farmacia farmacia) {
			this.farmacia = farmacia;
		}

		public String getOtro() {
			return otro;
		}

		public void setOtro(String otro) {
			this.otro = otro;
		}

		public Prestador getPrestador() {
			return prestador;
		}

		public void setPrestador(Prestador prestador) {
			this.prestador = prestador;
		}

		public Empresa getProveedor() {
			return proveedor;
		}

		public void setProveedor(Empresa proveedor) {
			this.proveedor = proveedor;
		}

		public Seccional getSeccional() {
			return seccional;
		}

		public void setSeccional(Seccional seccional) {
			this.seccional = seccional;
		}

		public String getEmpresa_remite() {
			return empresa_remite;
		}

		public void setEmpresa_remite(String empresa_remite) {
			this.empresa_remite = empresa_remite;
		}

		public String getEmpresa_rem_Descripcion() {
			return empresa_rem_Descripcion;
		}

		public void setEmpresa_rem_Descripcion(String empresa_rem_Descripcion) {
			this.empresa_rem_Descripcion = empresa_rem_Descripcion;
		}

		public String getSector_remite() {
			return sector_remite;
		}

		public void setSector_remite(String sector_remite) {
			this.sector_remite = sector_remite;
		}

		public String getSector_rem_Descripcion() {
			return sector_rem_Descripcion;
		}

		public void setSector_rem_Descripcion(String sector_rem_Descripcion) {
			this.sector_rem_Descripcion = sector_rem_Descripcion;
		}

		public String getUsuario_remite() {
			return usuario_remite;
		}

		public void setUsuario_remite(String usuario_remite) {
			this.usuario_remite = usuario_remite;
		}

	}
	
}

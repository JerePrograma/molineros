package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liferay.portal.NoSuchUserGroupException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.UserGroupUtil;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class ContactoCRM implements Serializable {

	private static final long serialVersionUID = -6763262618911821255L;
	private static Log logger = LogFactoryUtil.getLog(ContactoCRM.class);

	public enum ESTADOS {
		PENDIENTE, DERIVADO, CERRADO
	};
	
	private Integer id;
	private Integer idContacto;
	private Afiliado afiliado;
	private NoAfiliado noAfiliado;
	private String descripcion;
	private ESTADOS estado;  
	private Integer idCrmRelacionado;
	
	private Integer idReclamoPrestacional;
	
	
	private MotivoContacto motivo;
	private CategoriaContacto categoria;
	private TipoContacto tipo;
	private EdificioSectorUsuarioLiferay derivacion;
	private String comentarioAvance;
	private String comentarioCierre;
	private int importancia;
	private int incumplimientoDelContrato;
	private Date altaFecha;
	private String altaUsr;
	private String altaSector;
	private Date modiFecha;
	private String modiUsr;
	private String modiSector;
	private Date bajaFecha;
	private String bajaUsr;
	private List<DerivacionSeguimiento> seguimiento;
	private CRMEficacia eficacia;
	private Contacto contactoSeccional;
	private Empresa empresa;
	private Prestador prestador;
	private EdificioSectorUsuarioLiferay companiero;
	
	
	public ContactoCRM (){	
		super();
	}	
	
	public ContactoCRM (Integer id, Integer idContacto, Afiliado afiliado, String descripcion,
			String estado, Integer idCrmRelacionado, int importancia, int incumplContrato, MotivoContacto motivo, CategoriaContacto categoria,
			TipoContacto tipo, String comentarios, Date altaFecha, String altaUsr, String altaSector, Date modiFecha,
			String modiUsr, String modiSector, Date bajaFecha, String bajaUsr){
		
		 super();
		
		 this.id = id;
		 this.idContacto = idContacto;
		 this.afiliado = afiliado;
		 this.descripcion = descripcion;
		 this.estado = ESTADOS.valueOf(estado);
		 this.idCrmRelacionado = idCrmRelacionado;
		 this.motivo = motivo;
		 this.categoria = categoria;
		 this.tipo = tipo;
		 this.comentarioCierre = comentarios;
		 this.importancia = importancia;
		 this.incumplimientoDelContrato = incumplContrato;
		 this.altaFecha = altaFecha;
		 this.altaUsr = altaUsr;
		 this.altaSector = altaSector;
		 this.modiFecha = modiFecha;
		 this.modiUsr = modiUsr;
		 this.modiSector = modiSector;
		 this.bajaFecha = bajaFecha;
		 this.bajaUsr = bajaUsr;
	}
	
	public ContactoCRM (Integer id, Integer idContacto, NoAfiliado noAfiliado, String descripcion,
			String estado, Integer idCrmRelacionado, MotivoContacto motivo, CategoriaContacto categoria,
			TipoContacto tipo, String comentarios, int importancia, int incumplContrato, Date altaFecha, String altaUsr, String altaSector, Date modiFecha,
			String modiUsr, String modiSector, Date bajaFecha, String bajaUsr){
		
		 super();
		
		 this.id = id;
		 this.idContacto = idContacto;
		 this.noAfiliado = noAfiliado;
		 this.descripcion = descripcion;
		 this.estado = ESTADOS.valueOf(estado);
		 this.idCrmRelacionado = idCrmRelacionado;
		 this.motivo = motivo;
		 this.categoria = categoria;
		 this.tipo = tipo;
		 this.comentarioCierre = comentarios;
		 this.importancia = importancia;
		 this.incumplimientoDelContrato = incumplContrato;
		 this.altaFecha = altaFecha;
		 this.altaUsr = altaUsr;
		 this.altaSector = altaSector;
		 this.modiFecha = modiFecha;
		 this.modiUsr = modiUsr;
		 this.modiSector = modiSector;
		 this.bajaFecha = bajaFecha;
		 this.bajaUsr = bajaUsr;
	}
	
	public String toString(){
		return this.idContacto + " " + descripcion;
	}
	
	
	
	public static ContactoCRM getMappingConReclamo(String prefix, ResultSet rs) throws SQLException{
		String prefiMotivo = "mot_";
		String prefiTipo = "tipo_";
		String prefiCateg = "cate_";
		String prefiAfi = "afi_" ;
		String prefiNoAfi = "noafi_" ;
		String prefiReclamoPrestacional = "reclamo_" ;
		
		ContactoCRM ccrm = new ContactoCRM();
		Afiliado afi = new Afiliado();
		NoAfiliado noAfi = new NoAfiliado(); 
		
		String dni = null;
		String cuilTitu = null;
		
//		ccrm.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		ccrm.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		ccrm.setAltaSector(rs.getString(prefix + "alta_sector"));
		ccrm.setAltaUsr(rs.getString(prefix + "alta_usr"));
		ccrm.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		ccrm.setBajaUsr(rs.getString(prefix + "baja_usr"));
		ccrm.setComentarioAvance(rs.getString(prefix + "comentario_avance"));
		ccrm.setComentarioCierre(rs.getString(prefix + "comentario_cierre"));
		ccrm.setDescripcion(rs.getString(prefix + "descripcion"));
		ccrm.setEstado(ESTADOS.valueOf(rs.getString(prefix + "estado")) );
		ccrm.setId(rs.getInt(prefix + "id"));
		ccrm.setIdContacto(rs.getInt(prefix + "id_contacto"));
		ccrm.setIdCrmRelacionado(rs.getInt(prefix + "relacionado_con_id"));
		ccrm.setImportancia(rs.getInt(prefix + "importancia"));
		ccrm.setIncumplimientoDelContrato(rs.getInt(prefix + "incumplimiento_contrato"));
//		ccrm.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		ccrm.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		ccrm.setModiSector(rs.getString(prefix + "modi_sector"));
		ccrm.setModiUsr(rs.getString(prefix + "modi_usr"));

		ccrm.setTipo(new TipoContacto(rs.getInt(prefiTipo + "id_tipo_contacto"), rs.getString(prefiTipo + "descripcion")));
		ccrm.setMotivo(new MotivoContacto(rs.getInt(prefiMotivo + "id_motivo"), rs.getString(prefiMotivo + "descripcion") ) );
		ccrm.setCategoria(new CategoriaContacto(rs.getInt(prefiCateg + "id_categoria"), rs.getString(prefiCateg + "descripcion") ) );
		
		ccrm.setIdCrmReclamoPrestacional(rs.getInt(prefiReclamoPrestacional  + "id_contactocrm") ); 
		
		cuilTitu = rs.getString(prefiAfi + "cuil_titular");
		if(!StringUtils.checkEmpty(cuilTitu)){
			
			afi.setCuil_titular(rs.getString(prefiAfi + "cuil_titular"));
			afi.setInte(rs.getInt(prefiAfi + "inte"));
			afi.setApellido(rs.getString(prefiAfi + "apellido"));
			afi.setNombre(rs.getString(prefiAfi + "nombre"));
			afi.setDocumento_tipo(rs.getString(prefiAfi + "documento_tipo"));
			afi.setDocu_numero(rs.getString(prefiAfi + "docu_numero"));
			afi.setId_ospim(rs.getInt(prefiAfi + "id_ospim"));
			afi.setId_amtima(rs.getInt(prefiAfi + "id_amtima"));
			
			afi.setSeccional(new Seccional(rs.getInt(prefiAfi + "id"), rs.getString(prefiAfi + "descripcion")));
	  
			Plan plan= new Plan(rs.getInt(prefiAfi + "id_plan"),rs.getString(prefiAfi + "plan"));
			afi.setUltimo_plan(plan);
	//		afi.setIngre_fecha(rs.getDate("ingre_fecha"));
			afi.setBaja_fecha(rs.getDate(prefiAfi + "baja_fecha"));
			afi.setId_uoma(rs.getInt(prefiAfi + "id_uoma"));
			afi.setVigen_fecha(rs.getDate(prefiAfi + "vigen_fecha"));
			try{
				afi.setEmail(rs.getString(prefiAfi + "email"));
				Domicilio afiDom = new Domicilio();
				afiDom.setTelefono(rs.getString(prefiAfi + "telefonos"));
				afi.setDomicilioDefault(afiDom); 
				
			}catch (Exception e) {}
			ccrm.setAfiliado(afi);
		}
		
		try{
			dni = rs.getString(prefiNoAfi + "documento_numero");
		}catch (Exception e) {
			dni = null;
		}	
		if(!StringUtils.checkEmpty(dni)){
			
			noAfi.setDocumentoTipo(rs.getString(prefiNoAfi + "documento_tipo") );
			noAfi.setDocumentoNumero(rs.getString(prefiNoAfi + "documento_numero") );
			noAfi.setApellido(rs.getString(prefiNoAfi + "apellido") );
			noAfi.setNombre(rs.getString(prefiNoAfi + "nombre") );
			noAfi.setTelefono(rs.getString(prefiNoAfi + "telefono") );
			noAfi.setEmail(rs.getString(prefiNoAfi + "email") );
			noAfi.setAltaFecha(rs.getTimestamp(prefiNoAfi + "alta_fecha") );
			noAfi.setAltaUsr(rs.getString(prefiNoAfi + "alta_usr") );
			
			ccrm.setNoAfiliado(noAfi);
		}
		
		
		Contacto con = null;
		try{
		 if(rs.getInt("contacto_seccional")!=0){
			ContactoElectronico ce = new ContactoElectronico();
			ce.setId(rs.getInt("contacto_seccional"));
			con=new Contacto();
			con.setContacto(ce);
			ccrm.setContactoSeccional(con);
			if(rs.getInt(prefiAfi + "id")!=0 && rs.getString(prefiAfi + "descripcion")!=null){
			   con.setSeccional(new Seccional(rs.getInt(prefiAfi + "id"), rs.getString(prefiAfi + "descripcion")));
			}   
			if( rs.getString(prefiAfi + "apellido")!=null){
		 	   con.setNombreApe(rs.getString(prefiAfi + "apellido"));
			} 
			if( rs.getString(prefiAfi + "nombre")!=null){
			   con.setCargoDescripcion(rs.getString(prefiAfi + "nombre"));	
			}
		 }
		}catch(Exception e){
			ccrm.setContactoSeccional(con);
		}
		
		return ccrm;
	}
	public static ContactoCRM getMapping(String prefix, ResultSet rs) throws SQLException{
		
		String prefiMotivo = "mot_";
		String prefiTipo = "tipo_";
		String prefiCateg = "cate_";
		String prefiAfi = "afi_" ;
		String prefiNoAfi = "noafi_" ;
		String prefiPrestad = "prest_";
		String prefiEmpresa = "empresa_";
		
		ContactoCRM ccrm = new ContactoCRM();
		Afiliado afi = new Afiliado();
		NoAfiliado noAfi = new NoAfiliado();
		
		String dni = null;
		String cuilTitu = null;
		
//		ccrm.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		ccrm.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		ccrm.setAltaSector(rs.getString(prefix + "alta_sector"));
		ccrm.setAltaUsr(rs.getString(prefix + "alta_usr"));
		ccrm.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		ccrm.setBajaUsr(rs.getString(prefix + "baja_usr"));
		ccrm.setComentarioAvance(rs.getString(prefix + "comentario_avance"));
		ccrm.setComentarioCierre(rs.getString(prefix + "comentario_cierre"));
		ccrm.setDescripcion(rs.getString(prefix + "descripcion"));
		ccrm.setEstado(ESTADOS.valueOf(rs.getString(prefix + "estado")) );
		ccrm.setId(rs.getInt(prefix + "id"));
		ccrm.setIdContacto(rs.getInt(prefix + "id_contacto"));
		ccrm.setIdCrmRelacionado(rs.getInt(prefix + "relacionado_con_id"));
		ccrm.setImportancia(rs.getInt(prefix + "importancia"));
		ccrm.setIncumplimientoDelContrato(rs.getInt(prefix + "incumplimiento_contrato"));
//		ccrm.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		ccrm.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		ccrm.setModiSector(rs.getString(prefix + "modi_sector"));
		ccrm.setModiUsr(rs.getString(prefix + "modi_usr"));

		ccrm.setTipo(new TipoContacto(rs.getInt(prefiTipo + "id_tipo_contacto"), rs.getString(prefiTipo + "descripcion")));
		ccrm.setMotivo(new MotivoContacto(rs.getInt(prefiMotivo + "id_motivo"), rs.getString(prefiMotivo + "descripcion") ) );
		ccrm.setCategoria(new CategoriaContacto(rs.getInt(prefiCateg + "id_categoria"), rs.getString(prefiCateg + "descripcion") ) );
		
		cuilTitu = rs.getString(prefiAfi + "cuil_titular");
		if(!StringUtils.checkEmpty(cuilTitu)){
			
			afi.setCuil_titular(rs.getString(prefiAfi + "cuil_titular"));
			afi.setInte(rs.getInt(prefiAfi + "inte"));
			afi.setApellido(rs.getString(prefiAfi + "apellido"));
			afi.setNombre(rs.getString(prefiAfi + "nombre"));
			afi.setDocumento_tipo(rs.getString(prefiAfi + "documento_tipo"));
			afi.setDocu_numero(rs.getString(prefiAfi + "docu_numero"));
			afi.setId_ospim(rs.getInt(prefiAfi + "id_ospim"));
			afi.setId_amtima(rs.getInt(prefiAfi + "id_amtima"));
			
			afi.setSeccional(new Seccional(rs.getInt(prefiAfi + "id"), rs.getString(prefiAfi + "descripcion")));
	  
			Plan plan= new Plan(rs.getInt(prefiAfi + "id_plan"),rs.getString(prefiAfi + "plan"));
			afi.setUltimo_plan(plan);
	//		afi.setIngre_fecha(rs.getDate("ingre_fecha"));
			afi.setBaja_fecha(rs.getDate(prefiAfi + "baja_fecha"));
			afi.setId_uoma(rs.getInt(prefiAfi + "id_uoma"));
			afi.setVigen_fecha(rs.getDate(prefiAfi + "vigen_fecha"));
			try{
				afi.setEmail(rs.getString(prefiAfi + "email"));
				Domicilio afiDom = new Domicilio();
				afiDom.setTelefono(rs.getString(prefiAfi + "telefonos"));
				afi.setDomicilioDefault(afiDom); 
				
			}catch (Exception e) {}
			ccrm.setAfiliado(afi);
		}
		
		try{
			dni = rs.getString(prefiNoAfi + "documento_numero");
		}catch (Exception e) {
			dni = null;
		}	
		if(!StringUtils.checkEmpty(dni)){
			
			noAfi.setDocumentoTipo(rs.getString(prefiNoAfi + "documento_tipo") );
			noAfi.setDocumentoNumero(rs.getString(prefiNoAfi + "documento_numero") );
			noAfi.setApellido(rs.getString(prefiNoAfi + "apellido") );
			noAfi.setNombre(rs.getString(prefiNoAfi + "nombre") );
			noAfi.setTelefono(rs.getString(prefiNoAfi + "telefono") );
			noAfi.setEmail(rs.getString(prefiNoAfi + "email") );
			noAfi.setAltaFecha(rs.getTimestamp(prefiNoAfi + "alta_fecha") );
			noAfi.setAltaUsr(rs.getString(prefiNoAfi + "alta_usr") );
			
			ccrm.setNoAfiliado(noAfi);
		}
		
		
//		Contacto con = null;
//		try{
//		 if(rs.getInt("contacto_seccional")!=0){
//			ContactoElectronico ce = new ContactoElectronico();
//			ce.setId(rs.getInt("contacto_seccional"));
//			con=new Contacto();
//			con.setContacto(ce);
//			ccrm.setContactoSeccional(con);
//			if(rs.getInt(prefiAfi + "id")!=0 && rs.getString(prefiAfi + "descripcion")!=null){
//			   con.setSeccional(new Seccional(rs.getInt(prefiAfi + "id"), rs.getString(prefiAfi + "descripcion")));
//			}   
//			if( rs.getString(prefiAfi + "apellido")!=null){
//		 	   con.setNombreApe(rs.getString(prefiAfi + "apellido"));
//			} 
//			if( rs.getString(prefiAfi + "nombre")!=null){
//			   con.setCargoDescripcion(rs.getString(prefiAfi + "nombre"));	
//			}
//		 }
//		}catch(Exception e){
//			ccrm.setContactoSeccional(con);
//		}
		
		Contacto con = null;
		try{ //no se que pasó para que pongan este try, pero...lo dejo...
		 if(rs.getInt("contacto_seccional")!=0){
			con=new Contacto();
			 
			ContactoElectronico ce = new ContactoElectronico();
			ce.setId(rs.getInt("contacto_seccional"));
			
			con.setContacto(ce);
			
			if(rs.getInt(prefiAfi + "id")!=0 && rs.getString(prefiAfi + "descripcion")!=null){
			   con.setSeccional(new Seccional(rs.getInt(prefiAfi + "id"), rs.getString(prefiAfi + "descripcion")));
			}   
			if( rs.getString(prefiAfi + "apellido")!=null){
		 	   con.setNombreApe(rs.getString(prefiAfi + "apellido"));
			} 
			if( rs.getString(prefiAfi + "nombre")!=null){
			   con.setCargoDescripcion(rs.getString(prefiAfi + "nombre"));	
			}
		 }
		}catch(Exception e){
//			ccrm.setContactoSeccional(con);
		}
		ccrm.setContactoSeccional(con);
		
		Prestador prest = null;
		
		if(rs.getInt(prefiPrestad+"id_prestador") > 0) {
			prest = new Prestador(null, rs.getInt(prefiPrestad+"id_prestador"), rs.getString(prefiPrestad+"descripcion"));
		}
		ccrm.setPrestador(prest);
		
		Empresa emp = null;
		
		if(rs.getString(prefiEmpresa+"cuit") != null) {
			emp = new Empresa(rs.getString(prefiEmpresa+"cuit"), rs.getString(prefiEmpresa+"sucu"), rs.getString(prefiEmpresa+"razon_social"));
		}
		
		ccrm.setEmpresa(emp);

		EdificioSectorUsuarioLiferay esu = null;
		
		if(rs.getString("usuario") != null) {
			String edi = rs.getString("edificio");
			String sec = rs.getString("sector");
			String usu = rs.getString("usuario");         
			esu = new EdificioSectorUsuarioLiferay(edi, sec, usu);
			try {
				esu.setEmpresaDescripcion(OrganizationLocalServiceUtil.getOrganization(Long.parseLong(edi)).getName());
				esu.setSectorDescripcion(UserGroupLocalServiceUtil.getUserGroup(Long.parseLong(sec)).getName());
				esu.setUsuarioApeyNom(UserLocalServiceUtil.getUserByScreenName(10112, usu).getFullName());
			}catch(Exception e) {
				logger.error(e);
			}
		
			
		}
		ccrm.setCompaniero(esu);
		
		return ccrm;
	}
	
	public static ContactoCRM getMappingConSeguimiento(String prefix, ResultSet rs) throws SQLException{

		String prefiSeguim = "seg_";
		ContactoCRM contacto = null;
		EdificioSectorUsuarioLiferay ediSecUsu = null;
		
		contacto = ContactoCRM.getMapping(prefix, rs);
		
		contacto.setSeguimiento(new ArrayList<DerivacionSeguimiento>());
		
		DerivacionSeguimiento ds = DerivacionSeguimiento.getMapping(prefiSeguim, rs);
		
		if(ds.getDerivacionUsr() != null && !ds.getDerivacionUsr().isEmpty()){
			ediSecUsu = new EdificioSectorUsuarioLiferay(ds.getDerivacionEdificio(), ds.getDerivacionSector(), ds.getDerivacionUsr());
			contacto.getSeguimiento().add(ds);
			contacto.setDerivacion(ediSecUsu);
		}
		
		return contacto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdContacto() {
		return idContacto;
	}

	public void setIdContacto(Integer idContacto) {
		this.idContacto = idContacto;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	public Integer getIdCrmRelacionado() {
		return idCrmRelacionado;
	}

	public void setIdCrmRelacionado(Integer idCrmRelacionado) {
		this.idCrmRelacionado = idCrmRelacionado;
	}

	
	public Integer getIdCrmReclamoPrestacional() {
		return this.idReclamoPrestacional ;
	}

	public void setIdCrmReclamoPrestacional(Integer idCrmReclamoPRestacional ) {
		this.idReclamoPrestacional = idCrmReclamoPRestacional ;
	}

	
	public MotivoContacto getMotivo() {
		return motivo;
	}

	public void setMotivo(MotivoContacto motivo) {
		this.motivo = motivo;
	}

	public CategoriaContacto getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaContacto categoria) {
		this.categoria = categoria;
	}

	public TipoContacto getTipo() {
		return tipo;
	}

	public void setTipo(TipoContacto tipo) {
		this.tipo = tipo;
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

	public String getAltaSector() {
		
		UserGroup group = null;
		try {
			group = UserGroupUtil.findByPrimaryKey(Long.parseLong(altaSector)); // puede venir o numero o descrip del sector
		} catch (NoSuchUserGroupException e) {
			logger.error(e);
		} catch (NumberFormatException e) {
			//logger.error(e);
			return altaSector;
		} catch (SystemException e) {
			logger.error(e);
		}
		//return group!=null?group.getDescription():"no encontrado";
		return group!=null?(group.getName()!=null?group.getName():group.getDescription()):"no encontrado";
	}

	public void setAltaSector(String altaSector) {
		this.altaSector = altaSector;
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

	public String getModiSector() {
		UserGroup group = null;
		try {
			group = UserGroupUtil.findByPrimaryKey(Long.parseLong(modiSector)); // puede venir o numero o descrip del sector
		} catch (NoSuchUserGroupException e) {
			logger.error(e);
		} catch (NumberFormatException e) {
			//logger.error(e);
			return modiSector;
		} catch (SystemException e) {
			logger.error(e);
		}
		//return group!=null?group.getDescription():"no encontrado";
		return group!=null?(group.getName()!=null?group.getName():group.getDescription()):"no encontrado";
	}

	public void setModiSector(String modiSector) {
		this.modiSector = modiSector;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public EdificioSectorUsuarioLiferay getDerivacion() {
		return derivacion;
	}

	public void setDerivacion(EdificioSectorUsuarioLiferay derivacion) {
		this.derivacion = derivacion;
	}

	public List<DerivacionSeguimiento> getSeguimiento() {
		return seguimiento;
	}

	public void setSeguimiento(List<DerivacionSeguimiento> seguimiento) {
		this.seguimiento = seguimiento;
	}

	public NoAfiliado getNoAfiliado() {
		return noAfiliado;
	}

	public void setNoAfiliado(NoAfiliado noAfiliado) {
		this.noAfiliado = noAfiliado;
	}

	public int getTiempoResolucion(){
//		fechas calendario
//		long fechainicialms = this.altaFecha.getTime();
//		long fechafinalms = this.modiFecha.getTime();
//		long diferencia = fechafinalms - fechainicialms;
//		double dias = Math.floor(diferencia / 86400000L);// 3600*24*1000 
//		
//		return (int) dias;
		
//		Para evitar feriados y fin de semanas
		Calendar calAlta = Calendar.getInstance();
		Calendar calModif = Calendar.getInstance();
		calAlta.setTime(altaFecha);
		calAlta.set(Calendar.HOUR_OF_DAY, 0);
		calAlta.set(Calendar.MINUTE, 0);
		calAlta.set(Calendar.SECOND, 0);
		calAlta.set(Calendar.MILLISECOND, 0);
		calModif.setTime(modiFecha);
		calModif.set(Calendar.HOUR_OF_DAY, 0);
		calModif.set(Calendar.MINUTE, 0);
		calModif.set(Calendar.SECOND, 0);
		calModif.set(Calendar.MILLISECOND, 0);

		return DateUtils.calculaDiasHabilesEntreFechas(calAlta.getTime(), calModif.getTime(), true, null);
	}

	public String getComentarioCierre() {
		return comentarioCierre;
	}

	public void setComentarioCierre(String comentarioCierre) {
		this.comentarioCierre = comentarioCierre;
	}

	public CRMEficacia getEficacia() {
		return eficacia;
	}

	public void setEficacia(CRMEficacia eficacia) {
		this.eficacia = eficacia;
	}

	public int getImportancia() {
		return importancia;
	}
	
	public String getImportanciaDescripcion() {
		
		String desc = WebKeysCrm.CRM_IMPORTANCIA[this.importancia][1];
		
		return desc;
	}

	public void setImportancia(int importancia) {
		this.importancia = importancia;
	}

	public int getIncumplimientoDelContrato() {
		return incumplimientoDelContrato;
	}

	public void setIncumplimientoDelContrato(int incumplimientoDelContrato) {
		this.incumplimientoDelContrato = incumplimientoDelContrato;
	}

	public String getComentarioAvance() {
		return comentarioAvance;
	}

	public void setComentarioAvance(String comentarioAvance) {
		this.comentarioAvance = comentarioAvance;
	}

	public Contacto getContactoSeccional() {
		return contactoSeccional;
	}

	public void setContactoSeccional(Contacto contactoSeccional) {
		this.contactoSeccional = contactoSeccional;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public EdificioSectorUsuarioLiferay getCompaniero() {
		return companiero;
	}

	public void setCompaniero(EdificioSectorUsuarioLiferay companiero) {
		this.companiero = companiero;
	}

	
}

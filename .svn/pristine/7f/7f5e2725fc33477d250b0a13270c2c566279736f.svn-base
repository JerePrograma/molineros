package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.NoSuchUserGroupException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.persistence.UserGroupUtil;

public class DocumentoLegalCRM implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5855690996630838859L;

	private static Log logger = LogFactoryUtil.getLog(DocumentoLegalCRM.class);
	
	private Integer id;
//	Seguimiento reclamos
	private Afiliado afiliado;
	private NoAfiliado noAfiliado;
	private TipoReclamo tipo;
	private Date fechaNotificacion;
	private MotivoContacto motivo;
	private String descripcion;
	private boolean tieneAntecedentes;
	private boolean concluido;
//	Notificaciones
	private Date fechaVencimiento;
	private Date fechaRespuesta;
	private Date fechaAvisoAlEstudio;
	private Date fechaContactoPSOM;
//	Otros Datos
	private String expediente;
	private String resolucion;
	private String descripcionSolucion;
	private String descripcionEstudio;
	private Integer tramiteNumero;
	private String radicacion;
	private BigDecimal importeReclamado;
	
	private Date altaFecha;
	private String altaUsr;
	private String altaSector;
	private Date modiFecha;
	private String modiUsr;
	private String modiSector;
	private Date bajaFecha;
	private String bajaUsr;
	
	public DocumentoLegalCRM (){	
		super();
	}	
	
	public DocumentoLegalCRM(Integer id, Afiliado afiliado,
			NoAfiliado noAfiliado, TipoReclamo tipo, Date fechaNotificacion,
			MotivoContacto motivo, String descripcion, Date fechaVencimiento,
			Date fechaRespuesta, Date fechaAvisoAlEstudio,
			Date fechaContactoPSOM, String expediente, String resolucion,
			String descripcionSolucion, Integer tramiteNumero,
			String radicacion, BigDecimal importeReclamado) {
		
		super();
		this.id = id;
		this.afiliado = afiliado;
		this.noAfiliado = noAfiliado;
		this.tipo = tipo;
		this.fechaNotificacion = fechaNotificacion;
		this.motivo = motivo;
		this.descripcion = descripcion;
		this.fechaVencimiento = fechaVencimiento;
		this.fechaRespuesta = fechaRespuesta;
		this.fechaAvisoAlEstudio = fechaAvisoAlEstudio;
		this.fechaContactoPSOM = fechaContactoPSOM;
		this.expediente = expediente;
		this.resolucion = resolucion;
		this.descripcionSolucion = descripcionSolucion;
		this.tramiteNumero = tramiteNumero;
		this.radicacion = radicacion;
		this.importeReclamado = importeReclamado;
	}


	public static DocumentoLegalCRM getMapping(String prefix, ResultSet rs) throws SQLException{
		
		String prefiMotivo = "mot_";
		String prefiTipoReclamo = "tipo_";
		String prefiAfi = "afi_" ;
		String prefiNoAfi = "noafi_" ;
		
		DocumentoLegalCRM dlcrm = new DocumentoLegalCRM();
		Afiliado afi = new Afiliado();
		NoAfiliado noAfi = new NoAfiliado();
		
		String dni = null;
		String cuilTitu = null;

//		ccrm.setAltaFecha(rs.getDate(prefix + "alta_fecha"));
		dlcrm.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		dlcrm.setAltaSector(rs.getString(prefix + "alta_sector"));
		dlcrm.setAltaUsr(rs.getString(prefix + "alta_usr"));
		dlcrm.setBajaFecha(rs.getDate(prefix + "baja_fecha"));
		dlcrm.setBajaUsr(rs.getString(prefix + "baja_usr"));
		dlcrm.setDescripcion(rs.getString(prefix + "descripcion"));
		dlcrm.setDescripcionSolucion(rs.getString(prefix + "descripcion_solucion"));
		dlcrm.setExpediente(rs.getString(prefix + "expediente"));
		dlcrm.setFechaAvisoAlEstudio(rs.getDate(prefix + "fecha_aviso_estudio"));
		dlcrm.setFechaContactoPSOM(rs.getDate(prefix + "fecha_contacto_ps_om"));
		dlcrm.setFechaNotificacion(rs.getDate(prefix + "fecha_notificacion"));
		dlcrm.setFechaRespuesta(rs.getDate(prefix + "fecha_respuesta"));
		dlcrm.setFechaVencimiento(rs.getDate(prefix + "fecha_vencimiento"));
		dlcrm.setImporteReclamado(rs.getBigDecimal(prefix +"importe_reclamado"));
		dlcrm.setId(rs.getInt(prefix + "id"));
//		ccrm.setModiFecha(rs.getDate(prefix + "modi_fecha"));
		dlcrm.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		dlcrm.setModiSector(rs.getString(prefix + "modi_sector"));
		dlcrm.setModiUsr(rs.getString(prefix + "modi_usr"));

		dlcrm.setTipo(new TipoReclamo(rs.getInt(prefiTipoReclamo + "id_tipo_contacto"), rs.getString(prefiTipoReclamo + "descripcion")));
		dlcrm.setMotivo(new MotivoContacto(rs.getInt(prefiMotivo + "id_motivo"), rs.getString(prefiMotivo + "descripcion") ) );
		dlcrm.setRadicacion(rs.getString(prefix + "radicacion"));
		dlcrm.setResolucion(rs.getString(prefix + "resolucion"));
		dlcrm.setTramiteNumero(rs.getInt(prefix + "tramite_nro"));
		dlcrm.setDescripcionEstudio(rs.getString(prefix + "descripcion_estudio"));
		dlcrm.setTieneAntecedentes(rs.getBoolean(prefix + "antecedente"));
		dlcrm.setConcluido(rs.getBoolean(prefix + "concluido"));
		
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
			dlcrm.setAfiliado(afi);
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
			
			dlcrm.setNoAfiliado(noAfi);
		}
		
		
		return dlcrm;
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
		return group!=null?group.getDescription():"no encontrado";
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
		return group!=null?group.getDescription():"no encontrado";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public NoAfiliado getNoAfiliado() {
		return noAfiliado;
	}

	public void setNoAfiliado(NoAfiliado noAfiliado) {
		this.noAfiliado = noAfiliado;
	}

	public TipoReclamo getTipo() {
		return tipo;
	}

	public void setTipo(TipoReclamo tipo) {
		this.tipo = tipo;
	}

	public Date getFechaNotificacion() {
		return fechaNotificacion;
	}

	public void setFechaNotificacion(Date fechaNotificacion) {
		this.fechaNotificacion = fechaNotificacion;
	}

	public MotivoContacto getMotivo() {
		return motivo;
	}

	public void setMotivo(MotivoContacto motivo) {
		this.motivo = motivo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public Date getFechaRespuesta() {
		return fechaRespuesta;
	}

	public void setFechaRespuesta(Date fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}

	public Date getFechaAvisoAlEstudio() {
		return fechaAvisoAlEstudio;
	}

	public void setFechaAvisoAlEstudio(Date fechaAvisoAlEstudio) {
		this.fechaAvisoAlEstudio = fechaAvisoAlEstudio;
	}

	public Date getFechaContactoPSOM() {
		return fechaContactoPSOM;
	}

	public void setFechaContactoPSOM(Date fechaContactoPSOM) {
		this.fechaContactoPSOM = fechaContactoPSOM;
	}

	public String getExpediente() {
		return expediente;
	}

	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}

	public String getResolucion() {
		return resolucion;
	}

	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}

	public String getDescripcionSolucion() {
		return descripcionSolucion;
	}

	public void setDescripcionSolucion(String descripcionSolucion) {
		this.descripcionSolucion = descripcionSolucion;
	}

	public Integer getTramiteNumero() {
		return tramiteNumero;
	}

	public void setTramiteNumero(Integer tramiteNumero) {
		this.tramiteNumero = tramiteNumero;
	}

	public String getRadicacion() {
		return radicacion;
	}

	public void setRadicacion(String radicacion) {
		this.radicacion = radicacion;
	}

	public BigDecimal getImporteReclamado() {
		return importeReclamado;
	}

	public void setImporteReclamado(BigDecimal importeReclamado) {
		this.importeReclamado = importeReclamado;
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

	public void setAltaSector(String altaSector) {
		this.altaSector = altaSector;
	}

	public void setModiSector(String modiSector) {
		this.modiSector = modiSector;
	}

	
	public boolean isTieneAntecedentes() {
		return tieneAntecedentes;
	}

	public void setTieneAntecedentes(boolean tieneAntecedentes) {
		this.tieneAntecedentes = tieneAntecedentes;
	}

	public boolean isConcluido() {
		return concluido;
	}

	public void setConcluido(boolean concluido) {
		this.concluido = concluido;
	}

	public String getDescripcionEstudio() {
		return descripcionEstudio;
	}

	public void setDescripcionEstudio(String descripcionEstudio) {
		this.descripcionEstudio = descripcionEstudio;
	}

	@Override
	public String toString() {
		return "DocumentoLegalCRM [id=" + id + ", fechaNotificacion="
				+ fechaNotificacion + ", expediente=" + expediente + "]";
	}

	
}

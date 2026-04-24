package ar.com.ospim.tesoreria.beans.caja_chica;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.portlet.PortletRequest;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceImpl;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CajaChica implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6514721875746277069L;
	private static Log _log = LogFactoryUtil.getLog(CajaChica.class);
	
	private Integer id;
	private String descripcion;
	private WorkflowDefinition estado;
	private String observaciones;
	private Date ultimaReposicion;
	private Date solicitudReposicion;
	private Double asignado;
	//private Double saldo;
	private Concepto concepto;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private Seccional seccional;
	private Double importeOriginal;
	private Integer entidad;
	private String emailsController;
	private Boolean pideSeccionalGasto;
	private Concepto conceptoUnicoOP;
	
	private List<User>usuariosHabilitados;
	
	public CajaChica() {
		concepto = new Concepto();
		seccional = new Seccional();
		estado=new WorkflowDefinition();
		descripcion="";
		observaciones="";
		importeOriginal=0D;
		usuariosHabilitados = new ArrayList<User>();
		pideSeccionalGasto=false;
		conceptoUnicoOP= new Concepto();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public WorkflowDefinition getEstado() {
		return estado;
	}

	public void setEstado(WorkflowDefinition estado) {
		this.estado = estado;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}
	
	public String getAlta_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return alta_fecha != null ? sdf.format(alta_fecha)
				: "";
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}

	public Date getUltimaReposicion() {
		return ultimaReposicion;
	}

	public String getUltimaReposicion_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_APRUEBA_REPOSICION");
		WorkflowDefinition estado=new WorkflowDefinition();
		try {
			estado = CajaChicaServiceUtil.getUltimoEstadoPorId(id,Integer.parseInt(estadoId));
		} catch (Exception e) {}	
		
		return estado != null && estado.getFecha()!=null? sdf.format(estado.getFecha()): "";
	}

	public Date getSolicitudReposicion() {
		return solicitudReposicion;
	}

	public void setSolicitudReposicion(Date solicitudReposicion) {
		this.solicitudReposicion = solicitudReposicion;
	}
	
	
	public String getSolicitudReposicion_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return solicitudReposicion != null ? sdf.format(solicitudReposicion)
				: "";
	}

	public Double getSaldo() {
		try {
			return CajaChicaServiceUtil.getSaldo(id,entidad);
		} catch (Exception e) {
			_log.error("Error al traer saldo Caja Chica", e);
			return 0D;
		}
	}

	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Double getAsignado() {
		return asignado;
	}

	public void setAsignado(Double asignado) {
		this.asignado = asignado;
	}

	public Concepto getConcepto() {
		return concepto;
	}

	public void setConcepto(Concepto concepto) {
		this.concepto = concepto;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}
	
	public Double getImporteOriginal() {
		return importeOriginal;
	}

	public void setImporteOriginal(Double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}

	public Integer getEntidad() {
		return entidad;
	}

	public void setEntidad(Integer entidad) {
		this.entidad = entidad;
	}

	public List<User> getUsuariosHabilitados() {
		return usuariosHabilitados;
	}

	public void setUsuariosHabilitados(List<User> usuariosHabilitados) {
		this.usuariosHabilitados = usuariosHabilitados;
	}

	
	public List<ComprobanteCajaChica> getComprobantesPendientesRendicion(){
		List<ComprobanteCajaChica> result = new ArrayList<ComprobanteCajaChica>();
		try {
			return CajaChicaServiceUtil.comprobantesPendientesRendicion(entidad, id);
		} catch (Exception e) {
			_log.error("Error al traer comprobantes pendientes de rendicion Caja Chica", e);
			return result;
		}
	}
	
	public String getEmailsController() {
		return emailsController;
	}

	public void setEmailsController(String emailsController) {
		this.emailsController = emailsController;
	}

	public List<ComprobanteCajaChica> getComprobantesEnviadosARendicion(){
		List<ComprobanteCajaChica> result = new ArrayList<ComprobanteCajaChica>();
		try {
			return CajaChicaServiceUtil.comprobantesEnviadosARendicion(entidad, id);
		} catch (Exception e) {
			_log.error("Error al traer comprobantes enviados a rendicion Caja Chica", e);
			return result;
		}
	}
	
	public List<ComprobanteCajaChica> getComprobantesEnviadosARendicionResumido(){
		List<ComprobanteCajaChica> result = new ArrayList<ComprobanteCajaChica>();
		try {
			return CajaChicaServiceUtil.comprobantesEnviadosARendicionResumido(entidad, id);
		} catch (Exception e) {
			_log.error("Error al traer comprobantes enviados a rendicion Caja Chica", e);
			return result;
		}
	}
	
	
	public Boolean getPideSeccionalGasto() {
		return pideSeccionalGasto;
	}

	public void setPideSeccionalGasto(Boolean pideSeccionalGasto) {
		this.pideSeccionalGasto = pideSeccionalGasto;
	}

	public Concepto getConceptoUnicoOP() {
		return conceptoUnicoOP;
	}

	public void setConceptoUnicoOP(Concepto conceptoUnicoOP) {
		this.conceptoUnicoOP = conceptoUnicoOP;
	}

	public static CajaChica getMapping(ResultSet rs) throws SQLException {
		CajaChica cc = new CajaChica();
		cc.setAlta_fecha(rs.getDate("alta_fecha"));
		cc.setAsignado(rs.getDouble("asignado"));
		List<User>usuarios = new ArrayList<User>();
		cc.setUsuariosHabilitados(usuarios);
		
		List<Concepto> conceptos= new ArrayList<Concepto>();
		try {
			conceptos = CajaChicaServiceUtil.getConceptos(rs.getDate("alta_fecha"), rs.getInt("concepto_id"));
		} catch (SystemException e) {}
		
		Concepto concepto = new Concepto(rs.getInt("concepto_id"),rs.getString("concepto_descripcion"));
		if(conceptos.size()>0){
		  concepto = conceptos.get(0); 	
		}
		
		cc.setConcepto(concepto);
		
		Concepto conceptoUnicoOP = new Concepto(rs.getInt("concepto_unico_op_id"),rs.getString("concepto_unico_op_descripcion"));
		cc.setConceptoUnicoOP(conceptoUnicoOP);
		
		cc.setDescripcion(rs.getString("descripcion")==null?"":rs.getString("descripcion"));
		WorkflowDefinition estado=new WorkflowDefinition();
		try {
			estado = CajaChicaServiceUtil.getEstadoActual(rs.getInt("id"));
		} catch (Exception e) {}
		
		String emailsController="";
		try{
			emailsController = rs.getString("emails_controller");
		}catch (Exception e){}
		cc.setEmailsController(emailsController);
		
/*		
		int estadoId=rs.getInt("estado_id");
		String estadoDes="";
		for(int i = 0; i < WebKeysCajaChica.ESTADO_CAJA_CHICA.length; i++ ) {
            if(Integer.parseInt(WebKeysCajaChica.ESTADO_CAJA_CHICA[i][0])==estadoId) { 
               estadoDes=WebKeysCajaChica.ESTADO_CAJA_CHICA[i][1];
               break;
            }		   
        }
		
		WorkflowDefinition estado = new WorkflowDefinition(estadoId,estadoDes);
*/		
		cc.setEstado(estado);
		
		cc.setId(rs.getInt("id"));
		cc.setObservaciones(rs.getString("observaciones")==null?"":rs.getString("observaciones"));
		cc.setImporteOriginal(rs.getDouble("importe_original"));
		
		Seccional seccional = new Seccional(rs.getInt("seccional_id"),rs.getString("seccional_descripcion"));
		cc.setSeccional(seccional);
		
		cc.setSolicitudReposicion(rs.getDate("solicitud_reposicion"));
//		cc.setUltimaReposicion(rs.getDate("ultima_reposicion"));
		
		
//		archivo.setBaja_fecha(rs.getDate("baja_fecha"));
		Boolean pideSeccionalGasto=false;
		try{
	      pideSeccionalGasto=rs.getBoolean("pide_seccional_gasto");
		}catch(Exception e){}
		cc.setPideSeccionalGasto(pideSeccionalGasto);
		return cc;
	}
	
}

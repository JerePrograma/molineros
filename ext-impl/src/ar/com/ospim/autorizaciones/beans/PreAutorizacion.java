package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.Prestador;

public class PreAutorizacion implements Serializable{

	private static final long serialVersionUID = 274947043234660883L;
	
	private Integer id;
	private List<Estado> estados;
	private Afiliado afiliado;
	private List<PreAutorizacionPrestacion> codigosPresentados;
	private Date fecha;
	private Estado ultimoEstado;
	private Date fechaRespuestaPS;
	private Date fechaNotificacionAfiliado;
	private Date fechaEntregaRespuesta;
	private String tipoEntrega;
	private Date baja_Fecha;
	private String observaciones;
	private Timestamp fechaEmail;
	private boolean historiaClinica;
	private boolean estudiosComplementarios;
	private boolean biopsia;
	private boolean anatomiaPatologica;
	private String prestaciones;
	private Integer seccionalAltaUsr;
	private Date fechaEmail2;
	
	private String alta_usr;
	private Timestamp alta_fecha;
	private String modi_usr;
	private Timestamp modi_fecha;
	private String seccionalDescripcionAltaUsr;
	private Integer diasParaAlertaGerencial;
	private boolean alertaRoja;
	private Date alertaRojaFecha;
	private boolean discapacidad;
	private Date fechaEnvioTercerizadora;
	private Date fechaRecepcionTercerizadora;
	
	private boolean supra;
	private String tipoPedidoGestionOSPIM;
	private String tipoGestionOSPIM;
	private Integer idReclamoPrestacional;
	
	private Estado ultimoEstadoOSPIM;
	private String observacionesOSPIM;
	private Integer idAutorizacionWS;
	private String observacionesTercerizadoras;
	private boolean medicamento;
	private List<PreAutorizacionMedicamento> medicamentosPresentados;
	private boolean cirugia;
	private Integer preAutorizAsociada;
	private Integer preAutorizOrigen;
	private ClaseBase diagnostico;
	private boolean alojamiento;
	private Date alojamientoDesde;
	private Date alojamientoHasta;
	private boolean protesisOrtesis;
	private boolean ART;
	private Prestador prestador;
	private int idPedidoApp;
	
	public Integer getDiasParaAlertaGerencial() {
		return diasParaAlertaGerencial;
	}

	public void setDiasParaAlertaGerencial(Integer diasParaAlertaGerencial) {
		this.diasParaAlertaGerencial = diasParaAlertaGerencial;
	}

	public Date getAlertaRojaFecha() {
		return alertaRojaFecha;
	}

	public void setAlertaRojaFecha(Date alertaRojaFecha) {
		this.alertaRojaFecha = alertaRojaFecha;
	}
	
	public boolean isART() {
		return ART;
	}

	public void setART(boolean aRT) {
		ART = aRT;
	}

	public PreAutorizacion() {
		afiliado= new Afiliado();
		estados=new ArrayList<Estado>();
		codigosPresentados = new ArrayList<PreAutorizacionPrestacion>();
		medicamentosPresentados = new ArrayList<PreAutorizacionMedicamento>();
	}

	public static PreAutorizacion getMapping(ResultSet rs) throws SQLException {
		
		PreAutorizacion preAut = new PreAutorizacion();
		
		preAut.setFecha(rs.getDate("fecha"));
		Afiliado afiliado = new Afiliado();
		afiliado.setCuil_titular(rs.getString("cuil_titular"));
		afiliado.setInte(rs.getInt("inte"));
		afiliado.setApellido(rs.getString("apellido"));
		afiliado.setNombre(rs.getString("nombre"));
		afiliado.setDocu_numero(rs.getString("docu_nro"));
		afiliado.setDocumento_tipo(rs.getString("docu_tipo"));
		Seccional seccional = new Seccional();
		seccional.setId_seccional(rs.getInt("seccional_id"));
		seccional.setDescripcion(rs.getString("seccional_descripcion"));
		afiliado.setSeccional(seccional);
		AfiPlan afiPlan = new AfiPlan();
		Plan plan = new Plan();
		plan.setId(rs.getInt("plan_id"));
		plan.setDescripcion(rs.getString("plan_descripcion"));
		afiPlan.setPlan(plan);
		afiliado.setAfiPlan(afiPlan);
		
		preAut.setAfiliado(afiliado);
		preAut.setFechaNotificacionAfiliado(rs.getDate("fecha_notificacion"));
		preAut.setFechaRespuestaPS(rs.getDate("fecha_respuesta_ps"));
		preAut.setFechaEntregaRespuesta(rs.getDate("fecha_entrega_respuesta"));
		preAut.setTipoEntrega(rs.getString("tipo_entrega"));
		
		preAut.setFechaEmail(rs.getTimestamp("email_fecha"));
		preAut.setFechaEmail2(rs.getTimestamp("email_fecha_2"));
		
		Estado estado = new Estado(rs.getString("estado_id"),"",rs.getString("motivo_rechazo"),rs.getString("observaciones_externas"));
		
		preAut.setUltimoEstado(estado);
		preAut.setBaja_Fecha(rs.getDate("baja_fecha"));
		preAut.setId(rs.getInt("id"));
		preAut.setObservaciones(rs.getString("observaciones"));
		preAut.setObservacionesTercerizadoras(rs.getString("observaciones_tercerizadora"));
		preAut.setHistoriaClinica(rs.getBoolean("historia_clinica"));
		preAut.setEstudiosComplementarios(rs.getBoolean("estudios_complementarios"));
		preAut.setBiopsia(rs.getBoolean("biopsia"));
		preAut.setAnatomiaPatologica(rs.getBoolean("anatomia_patologica"));
		preAut.setAlta_fecha(rs.getTimestamp("alta_fecha"));
		preAut.setAlta_usr(rs.getString("alta_usr"));
		preAut.setSeccionalAltaUsr(rs.getInt("alta_usr_seccional"));
		preAut.setSeccionalDescripcionAltaUsr(rs.getString("alta_usr_seccional_descripcion"));
		preAut.setModi_fecha(rs.getTimestamp("modi_fecha"));
		preAut.setModi_usr(rs.getString("modi_usr"));
		preAut.setAlertaRoja(rs.getBoolean("alerta_roja"));
		preAut.setAlertaRojaFecha(rs.getDate("alerta_roja_fecha"));
		preAut.setDiscapacidad(rs.getBoolean("discapacidad"));
		preAut.setFechaEnvioTercerizadora(rs.getDate("fecha_envio_tercerizadora"));
		preAut.setFechaRecepcionTercerizadora(rs.getDate("fecha_recepcion_tercerizadora"));
		preAut.setSupra(rs.getBoolean("supra"));
//		preAut.setIdReclamoPrestacional(rs.getInt("reclamoprestacional_id"));
		preAut.setIdAutorizacionWS(rs.getInt("id_autorizacion_ws"));
		preAut.setMedicamento(rs.getBoolean("medicamento"));
		preAut.setPreAutorizAsociada(rs.getInt("id_preautorizacion_asociado"));
		preAut.setPreAutorizOrigen(rs.getInt("id_preautorizacion_origen"));
		ClaseBase diagnostico = new ClaseBase();
		diagnostico.setId(rs.getString("diagnostico"));
		preAut.setDiagnostico(diagnostico);
		
		preAut.setAlojamiento(rs.getBoolean("alojamiento"));
		preAut.setAlojamientoDesde(rs.getDate("alojamiento_desde"));
		preAut.setAlojamientoHasta(rs.getDate("alojamiento_hasta"));
		
		preAut.setProtesisOrtesis(rs.getBoolean("protesis_ortesis"));
		preAut.setCirugia(rs.getBoolean("cirugia"));
		preAut.setART(rs.getBoolean("posible_art"));
		preAut.setPrestaciones(rs.getString("prestaciones"));
		Prestador prestador = new Prestador();
		try {
		   prestador.setId_prestador(rs.getInt("prestador_id"));
		   prestador.setCuit(rs.getString("prestador_cuit"));
		   prestador.setDescripcion(rs.getString("prestador_descripcion"));
		}catch(Exception e) {}   
		preAut.setPrestador(prestador);
		preAut.setIdPedidoApp(rs.getInt("id_pedido_app"));
		return preAut;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Estado> getEstados() {
		return estados;
	}

	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}

	public List<PreAutorizacionPrestacion> getCodigosPresentados() {
		return codigosPresentados;
	}

	public void setCodigosPresentados(List<PreAutorizacionPrestacion> codigosPresentados) {
		this.codigosPresentados = codigosPresentados;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFechaRespuestaPS() {
		return fechaRespuestaPS;
	}

	public void setFechaRespuestaPS(Date fechaRespuestaPS) {
		this.fechaRespuestaPS = fechaRespuestaPS;
	}
	
	public String getFechaRespuestaPS_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaRespuestaPS != null ? sdf.format(fechaRespuestaPS): "";
	}

	public Date getFechaNotificacionAfiliado() {
		return fechaNotificacionAfiliado;
	}

	public void setFechaNotificacionAfiliado(Date fechaNotificacionAfiliado) {
		this.fechaNotificacionAfiliado = fechaNotificacionAfiliado;
	}
	
	public String getFechaNotificacionAfiliado_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaNotificacionAfiliado != null ? sdf.format(fechaNotificacionAfiliado): "";
	}
	
	public Date getFechaEntregaRespuesta() {
		return fechaEntregaRespuesta;
	}

	public void setFechaEntregaRespuesta(Date fechaEntregaRespuesta) {
		this.fechaEntregaRespuesta = fechaEntregaRespuesta;
	}

	public String getFechaEntregaRespuesta_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaEntregaRespuesta != null ? sdf.format(fechaEntregaRespuesta): "";
	}
	
	public String getTipoEntrega() {
		return tipoEntrega;
	}

	public void setTipoEntrega(String tipoEntrega) {
		this.tipoEntrega = tipoEntrega;
	}

	public String getFecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fecha != null ? sdf.format(fecha): "";
	}

	public Date getBaja_Fecha() {
		return baja_Fecha;
	}

	public void setBaja_Fecha(Date baja_Fecha) {
		this.baja_Fecha = baja_Fecha;
	}

	public Estado getUltimoEstado() {
		return ultimoEstado;
	}

	public void setUltimoEstado(Estado ultimoEstado) {
		this.ultimoEstado = ultimoEstado;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Date getFechaEmail() {
		return fechaEmail;
	}
	public void setFechaEmail(Timestamp fechaEmail) {
		this.fechaEmail = fechaEmail;
	}
	
	public List<DLFileEntryImpl>  getImagenes() {
	   List<DLFileEntryImpl> list=new ArrayList<DLFileEntryImpl>();
	   try {
		list = PreAutorizacionServiceUtil.getImagenesPreautorizacion("PREAUT_"+getId()+"-");
	   } catch (SystemException e) {}
	   return list;
	}
	
	public boolean getRequiereAutorizacion(){
		boolean ret=false;
		for(PreAutorizacionPrestacion p:getCodigosPresentados()){
			if(p.getNomenclador().getRequiereAutorizacion()){
				ret=true;
				break;
			}
		}
		return ret;
	}

	public boolean isHistoriaClinica() {
		return historiaClinica;
	}

	public void setHistoriaClinica(boolean historiaClinica) {
		this.historiaClinica = historiaClinica;
	}

	public boolean isEstudiosComplementarios() {
		return estudiosComplementarios;
	}

	public void setEstudiosComplementarios(boolean estudiosComplementarios) {
		this.estudiosComplementarios = estudiosComplementarios;
	}

	public boolean isBiopsia() {
		return biopsia;
	}

	public void setBiopsia(boolean biopsia) {
		this.biopsia = biopsia;
	}

	public boolean isAnatomiaPatologica() {
		return anatomiaPatologica;
	}

	public void setAnatomiaPatologica(boolean anatomiaPatologica) {
		this.anatomiaPatologica = anatomiaPatologica;
	}

	public String getPrestaciones() {
		return prestaciones;
	}

	public void setPrestaciones(String prestaciones) {
		this.prestaciones = prestaciones;
	}
	
	public boolean getExisteHistoriaClinica(){
		boolean ret=false;
		for(PreAutorizacionPrestacion p:getCodigosPresentados()){
			if(p.getFechaBaja()==null){
			   Nomenclador m;
			   try {
				m = NomencladorServiceUtil.getEstudiosRequeridosPorId(p.getNomenclador().getId_prestacion());
				if(m.isRequiereHistoriaClinica()){
					ret=true;
					break;
				}
			   } catch (SystemException e) {
				 ret=false;
			   }
			}	   
			
		}
		return ret;
	}
	
	public boolean getExisteEstudiosComplementarios(){
		boolean ret=false;
		for(PreAutorizacionPrestacion p:getCodigosPresentados()){
		  if(p.getFechaBaja()==null){	
			Nomenclador m;
			try {
				m = NomencladorServiceUtil.getEstudiosRequeridosPorId(p.getNomenclador().getId_prestacion());
				if(m.isRequiereEstudiosComplementarios()){
					ret=true;
					break;
				}
			} catch (SystemException e) {
				ret=false;
			}
			
		  }
		}  
		return ret;
	}
	
	public boolean getExisteBiopsia(){
		boolean ret=false;
		for(PreAutorizacionPrestacion p:getCodigosPresentados()){
	      if(p.getFechaBaja()==null){
			Nomenclador m;
			try {
				m = NomencladorServiceUtil.getEstudiosRequeridosPorId(p.getNomenclador().getId_prestacion());
				if(m.isRequiereBiopsia()){
					ret=true;
					break;
				}
			} catch (SystemException e) {
				ret=false;
			}
			
		  }
		}  
		return ret;
	}
	
	public boolean getExisteAnatomiaPatologica(){
		boolean ret=false;
		for(PreAutorizacionPrestacion p:getCodigosPresentados()){
		  if(p.getFechaBaja()==null){	
			Nomenclador m;
			try {
				m = NomencladorServiceUtil.getEstudiosRequeridosPorId(p.getNomenclador().getId_prestacion());
				if(m.isRequiereAnatomiaPatologica()){
					ret=true;
					break;
				}
			} catch (SystemException e) {
				ret=false;
			}
			
		  }
		}  
		return ret;
	}
	
	
	public String getFechaEnvioMail_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaEmail != null ? sdf.format(fechaEmail): "";
	}

	public Integer getSeccionalAltaUsr() {
		return seccionalAltaUsr;
	}

	public void setSeccionalAltaUsr(Integer seccionalAltaUsr) {
		this.seccionalAltaUsr = seccionalAltaUsr;
	}

	public Date getFechaEmail2() {
		return fechaEmail2;
	}

	public void setFechaEmail2(Date fechaEmail2) {
		this.fechaEmail2 = fechaEmail2;
	}
	
	public String getFechaEnvioMail2_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaEmail2 != null ? sdf.format(fechaEmail2): "";
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}

	public Timestamp getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Timestamp alta_fecha) {
		this.alta_fecha = alta_fecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}

	public Timestamp getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Timestamp modi_fecha) {
		this.modi_fecha = modi_fecha;
	}

	public String getSeccionalDescripcionAltaUsr() {
		return seccionalDescripcionAltaUsr;
	}

	public void setSeccionalDescripcionAltaUsr(String seccionalDescripcionAltaUsr) {
		this.seccionalDescripcionAltaUsr = seccionalDescripcionAltaUsr;
	}

	public boolean isAlertaRoja() {
		return alertaRoja;
	}

	public void setAlertaRoja(boolean alertaRoja) {
		this.alertaRoja = alertaRoja;
	}

	public boolean isDiscapacidad() {
		return discapacidad;
	}

	public void setDiscapacidad(boolean discapadidad) {
		this.discapacidad = discapadidad;
	}

	public Date getFechaEnvioTercerizadora() {
		return fechaEnvioTercerizadora;
	}

	public void setFechaEnvioTercerizadora(Date fechaEnvioTercerizadora) {
		this.fechaEnvioTercerizadora = fechaEnvioTercerizadora;
	}

	public Date getFechaRecepcionTercerizadora() {
		return fechaRecepcionTercerizadora;
	}

	public void setFechaRecepcionTercerizadora(Date fechaRecepcionTercerizadora) {
		this.fechaRecepcionTercerizadora = fechaRecepcionTercerizadora;
	}

	public boolean isSupra() {
		return supra;
	}

	public void setSupra(boolean supra) {
		this.supra = supra;
	}

	public String getTipoPedidoGestionOSPIM() {
		return tipoPedidoGestionOSPIM;
	}

	public void setTipoPedidoGestionOSPIM(String tipoPedidoGestionOSPIM) {
		this.tipoPedidoGestionOSPIM = tipoPedidoGestionOSPIM;
	}

	public Estado getUltimoEstadoOSPIM() {
		return ultimoEstadoOSPIM;
	}

	public void setUltimoEstadoOSPIM(Estado ultimoEstadoOSPIM) {
		this.ultimoEstadoOSPIM = ultimoEstadoOSPIM;
	}

	public String getTipoGestionOSPIM() {
		return tipoGestionOSPIM;
	}

	public void setTipoGestionOSPIM(String tipoGestionOSPIM) {
		this.tipoGestionOSPIM = tipoGestionOSPIM;
	}

	public String getObservacionesOSPIM() {
		return observacionesOSPIM;
	}

	public void setObservacionesOSPIM(String observacionesOSPIM) {
		this.observacionesOSPIM = observacionesOSPIM;
	}
	
	public boolean isRecuperableSUR() throws SystemException {
		boolean ret=false;
		
		for(PreAutorizacionPrestacion p: codigosPresentados) {
			Nomenclador n = NomencladorServiceUtil.buscarNomencladorPorId(p.getNomenclador().getId_prestacion());
			if(n.getRecuperaSUR()) {
				ret=true;
				break;
			}
		}
		return ret;
	}

	public Integer getIdReclamoPrestacional() {
		return idReclamoPrestacional;
	}

	public void setIdReclamoPrestacional(Integer idReclamoPrestacional) {
		this.idReclamoPrestacional = idReclamoPrestacional;
	}

	public Integer getIdAutorizacionWS() {
		return idAutorizacionWS;
	}

	public void setIdAutorizacionWS(Integer idAutorizacionWS) {
		this.idAutorizacionWS = idAutorizacionWS;
	}

	public String getObservacionesTercerizadoras() {
		return observacionesTercerizadoras;
	}

	public void setObservacionesTercerizadoras(String observacionesTercerizadoras) {
		this.observacionesTercerizadoras = observacionesTercerizadoras;
	}

	public boolean isMedicamento() {
		return medicamento;
	}

	public void setMedicamento(boolean medicamento) {
		this.medicamento = medicamento;
	}

	public List<PreAutorizacionMedicamento> getMedicamentosPresentados() {
		return medicamentosPresentados;
	}

	public void setMedicamentosPresentados(List<PreAutorizacionMedicamento> medicamentosPresentados) {
		this.medicamentosPresentados = medicamentosPresentados;
	}

	public boolean isCirugia() {
		return cirugia;
	}

	public void setCirugia(boolean cirugia) {
		this.cirugia = cirugia;
	}

	public Integer getPreAutorizAsociada() {
		return preAutorizAsociada;
	}

	public void setPreAutorizAsociada(Integer preAutorizAsociada) {
		this.preAutorizAsociada = preAutorizAsociada;
	}

	public Integer getPreAutorizOrigen() {
		return preAutorizOrigen;
	}

	public void setPreAutorizOrigen(Integer preAutorizOrigen) {
		this.preAutorizOrigen = preAutorizOrigen;
	}

	public ClaseBase getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(ClaseBase diagnostico) {
		this.diagnostico = diagnostico;
	}

	public boolean isAlojamiento() {
		return alojamiento;
	}

	public void setAlojamiento(boolean alojamiento) {
		this.alojamiento = alojamiento;
	}

	public Date getAlojamientoDesde() {
		return alojamientoDesde;
	}

	public void setAlojamientoDesde(Date alojamientoDesde) {
		this.alojamientoDesde = alojamientoDesde;
	}

	public Date getAlojamientoHasta() {
		return alojamientoHasta;
	}

	public void setAlojamientoHasta(Date alojamientoHasta) {
		this.alojamientoHasta = alojamientoHasta;
	}

	public boolean isProtesisOrtesis() {
		return protesisOrtesis;
	}

	public void setProtesisOrtesis(boolean protesisOrtesis) {
		this.protesisOrtesis = protesisOrtesis;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	
	public int getIdPedidoApp() {
		return idPedidoApp;
	}

	public void setIdPedidoApp(int idPedidoApp) {
		this.idPedidoApp = idPedidoApp;
	}
	
	
}


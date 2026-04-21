package ar.com.ospim.autorizaciones.action;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiSuspencionCobertura;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.afiliados.services.TercerizadoraServiceUtil;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.beans.AutoPrestacional;
import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.OpcionesPrestacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.desarrolloAppMobile.beans.ClienteAppMobile;
import ar.com.ospim.desarrolloAppMobile.services.ClienteAppMobileServiceUtil;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;


public class PreAutorizacionAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	private PlanServiceUtil planService = new PlanServiceUtil();

	private String rid() {
		return "PA#" + System.currentTimeMillis() + "-" + Math.abs(new Random().nextInt(100000));
	}

	private String s(Object o) {
		return o == null ? "null" : String.valueOf(o);
	}

	private void logPreautorizacionSnapshot(String rid, String tag, PreAutorizacion p) {
		if (p == null) {
			_log.info("[" + rid + "][" + tag + "] preautorizacion=null");
			return;
		}

		String ultimoEstado = (p.getUltimoEstado() != null) ? p.getUltimoEstado().getId() : null;
		String estadoOspim = (p.getUltimoEstadoOSPIM() != null) ? p.getUltimoEstadoOSPIM().getId() : null;
		String cuil = (p.getAfiliado() != null) ? p.getAfiliado().getCuil_titular() : null;
		String inte = (p.getAfiliado() != null) ? String.valueOf(p.getAfiliado().getInte()) : null;
		String prestador = (p.getPrestador() != null) ? String.valueOf(p.getPrestador().getId_prestador()) : null;
		String diagnostico = (p.getDiagnostico() != null) ? p.getDiagnostico().getId() : null;

		int codigos = p.getCodigosPresentados() != null ? p.getCodigosPresentados().size() : -1;
		int meds = p.getMedicamentosPresentados() != null ? p.getMedicamentosPresentados().size() : -1;
		int imgs = p.getImagenes() != null ? p.getImagenes().size() : -1;

		_log.info("[" + rid + "][" + tag + "]"
				+ " id=" + s(p.getId())
				+ " estado=" + s(ultimoEstado)
				+ " estadoOspim=" + s(estadoOspim)
				+ " cuil=" + s(cuil)
				+ " inte=" + s(inte)
				+ " fecha=" + s(p.getFecha())
				+ " fechaRespPS=" + s(p.getFechaRespuestaPS())
				+ " fechaEntrega=" + s(p.getFechaEntregaRespuesta())
				+ " fechaNotif=" + s(p.getFechaNotificacionAfiliado())
				+ " fechaEmail=" + s(p.getFechaEmail())
				+ " discapacidad=" + p.isDiscapacidad()
				+ " medicamento=" + p.isMedicamento()
				+ " alojamiento=" + p.isAlojamiento()
				+ " supra=" + p.isSupra()
				+ " protesisOrtesis=" + p.isProtesisOrtesis()
				+ " art=" + p.isART()
				+ " diagnostico=" + s(diagnostico)
				+ " prestador=" + s(prestador)
				+ " codigos=" + codigos
				+ " medicamentos=" + meds
				+ " imagenes=" + imgs
		);
	}

	private void logRequestBasico(String rid, HttpServletRequest req) {
		_log.info("[" + rid + "][REQ]"
				+ " cmd=" + s(ParamUtil.getString(req, Constants.CMD))
				+ " id_preautorizacion=" + s(ParamUtil.getString(req, "id_preautorizacion"))
				+ " estadoPreautorizacion=" + s(ParamUtil.getString(req, "estadoPreautorizacion"))
				+ " cuil=" + s(ParamUtil.getString(req, "cuil"))
				+ " inte=" + s(ParamUtil.getString(req, "inte"))
				+ " diagnostico=" + s(ParamUtil.getString(req, "id_diagnostico"))
				+ " discapacidadChk=" + ParamUtil.getBoolean(req, "discapacidadChk")
				+ " medicamentoChk=" + ParamUtil.getBoolean(req, "medicamentoChk")
				+ " alojamientoChk=" + ParamUtil.getBoolean(req, "alojamientoChk")
				+ " protesisOrtChk=" + ParamUtil.getBoolean(req, "protesisOrtChk")
				+ " artChk=" + ParamUtil.getBoolean(req, "artChk")
				+ " id_prestador_aut=" + s(ParamUtil.getString(req, "id_prestador_aut"))
				+ " nroDoc=" + s(ParamUtil.getString(req, "nroDoc"))
		);
	}

	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		String rid = rid();
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		HttpServletRequest req = PortalUtil.getHttpServletRequest(actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		Boolean esDatosTab = ParamUtil.getBoolean(actionRequest, "esDatosTab");

		_log.info("[" + rid + "][PROCESS][START] cmd=" + s(cmd) + ", esDatosTab=" + esDatosTab);
		logRequestBasico(rid, req);

		if (cmd.equals(Constants.MOVE) && esDatosTab) {
			PreAutorizacion preautorizacion = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			logPreautorizacionSnapshot(rid, "PROCESS][MOVE][BEFORE_ACTUALIZA", preautorizacion);

			actualizaPreautorizacion(preautorizacion, req, rid);

			logPreautorizacionSnapshot(rid, "PROCESS][MOVE][AFTER_ACTUALIZA", preautorizacion);
			session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION , preautorizacion);
			_log.info("[" + rid + "][PROCESS][MOVE][SESSION_UPDATED]");
		}

		_log.info("[" + rid + "][PROCESS][END]");
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		PreAutorizacion preautorizacion=null;
		Integer idPreautorizacion = 0;
		String msg = "";
		String tabSel = ParamUtil.get(renderRequest, "tab_seleccionada", "datos");
		tabSel="null".equalsIgnoreCase(tabSel)?"datos":tabSel;
		String rid = rid();
		_log.info("[" + rid + "][RENDER][START] user=" + (user != null ? user.getScreenName() : "null")
				+ ", cmd=" + s(cmd)
				+ ", id_preautorizacion=" + s(ParamUtil.getString(renderRequest,"id_preautorizacion"))
				+ ", tabSel=" + s(tabSel));
		if (!StringUtils.checkEmpty(cmd)) {
			idPreautorizacion = ParamUtil.getInteger(renderRequest,"id_preautorizacion", 0);
			if(cmd.equals(Constants.WRITE) ){
				_log.info("[" + rid + "][RENDER][START] user=" + (user != null ? user.getScreenName() : "null")
						+ ", cmd=" + s(cmd)
						+ ", id_preautorizacion=" + s(ParamUtil.getString(renderRequest,"id_preautorizacion"))
						+ ", tabSel=" + s(tabSel));
				preautorizacion = new PreAutorizacion();
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION , preautorizacion);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.preautorizacion_editar"));
			}
			
			if(cmd.equals("addPrestacion")){
				
	           addPrestacion(renderRequest,session);		   	
				
   			   return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.preautorizacion_prestaciones_result"));	
			}
			
			if(cmd.equals("deletePrestacion")){
				
		           deletePrestacion(renderRequest,session);		   	
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.autorizaciones.preautorizacion_prestaciones_result"));	
			}
			
			if(cmd.equals("filterPrestacion")){
				
		           filterPreautorizacion(renderRequest,session);		   	
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.autorizaciones.preautorizacion_result"));	
			}
			
            if(cmd.equals(Constants.EDIT) ){
				_log.info("[" + rid + "][RENDER][EDIT] buscando preautorizacion id=" + idPreautorizacion);
				preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
				logPreautorizacionSnapshot(rid, "RENDER][EDIT][LOADED", preautorizacion);
            	preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
            	session.setAttribute("esPopUp","S");
				session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION , preautorizacion);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				renderRequest.setAttribute("tab", tabSel);
				
				
				String tipoEdicion = ParamUtil.get(renderRequest, "accion", "E");
				
				renderRequest.setAttribute("view", "E".equalsIgnoreCase(tipoEdicion)?"EDIT":"VIEW");
				_log.info("[" + rid + "][RENDER][EDIT] buscando preautorizacion id=" + idPreautorizacion);
				preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
				logPreautorizacionSnapshot(rid, "RENDER][EDIT][LOADED", preautorizacion);
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.preautorizacion_editar"));
			}

            if(cmd.equals(Constants.DELETE) ){ 
            	
            	try {           		
                    Integer idPedidoApp = ClienteAppMobileServiceUtil.getIdPedidoAppPorPreautorizacion(idPreautorizacion);
                    
	            	PreAutorizacionServiceUtil.eliminaPreautorizacion(idPreautorizacion, user.getScreenName());
	            	
	            	//actualiza estado RE en base externa
	            	if (idPedidoApp != null && idPedidoApp > 0) {
	                    String token = ClienteAppMobile.obtenerToken();
	                    if (token != null) {
	                        try {
	                            ClienteAppMobile.actualizarEstadoPedidoAutorizacion(idPedidoApp, "RE", token);
	                        } catch (Exception e) {
	                            _log.error("Error al actualizar estado");
	                        }
	                    } else {
	                        _log.warn("Token nulo");
	                    }
	                 } else {
	                	 _log.debug("Preautorizacion eliminada");
	                 }

	            } catch (Exception e) {
	                _log.error("Error eliminando preautorizacion");
	            }
            	
	                List<PreAutorizacion>ln= (List<PreAutorizacion>) session.getAttribute(WebKeysAutorizaciones.BUSQUEDA_PREAUTORIZACIONES_RESULT);
	                List<PreAutorizacion>lista=new ArrayList<PreAutorizacion>();
	           	    for(PreAutorizacion n:ln){
	            		if(!n.getId().equals(idPreautorizacion)){
	            		   //n.setBaja_fecha(new Date());	
	            		   lista.add(n);
	            		}
	            	}
	           	
	            	session.setAttribute(WebKeysAutorizaciones.BUSQUEDA_PREAUTORIZACIONES_RESULT,lista);
	            	return mapping.findForward("portlet.autorizaciones.preautorizacion_result");
			}

            if(cmd.equals("imagenes") ){
            	String desdeResult = ParamUtil.get(renderRequest, "desde_result", "");
                session.setAttribute("esPopUp","N");
                session.setAttribute("desde_result",desdeResult);
                if(preautorizacion==null){
                    preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
                    session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, preautorizacion);
                    		
                }    
            	return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.imagenes_preautorizacion"));
						
            }
            
            if(cmd.equals("addMedicamento")){
				
 	           addMedicamento(renderRequest,session);		   	
 				
    			   return mapping.findForward(getForward(renderRequest,
 						"portlet.autorizaciones.preautorizacion_medicamentos_result"));	
 			}
            
            if(cmd.equals("deleteMedicamento")){
				
		           deleteMedicamento(renderRequest,session);		   	
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.autorizaciones.preautorizacion_medicamentos_result"));	
			}
			
			if(cmd.equals("filterMedicamento")){
				
//		           filterPreautorizacion(renderRequest,session);		   	
//					
//	   			   return mapping.findForward(getForward(renderRequest,
//							"portlet.autorizaciones.preautorizacion_result"));	
			}
			
            if(cmd.equals("email") ){
            	session.setAttribute("esPopUp","N");
                if(preautorizacion==null){
            	   preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
            	   session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION , preautorizacion);
                }
//              PreAutorizacionServiceUtil.enviarSolicitudAutorizacionPorEmail(preautorizacion, user);
//              
//              preautorizacion.setFechaEmail(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
//              
//  			  session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION , preautorizacion);
                try{
                	//si la preautorizaci�n viene de APP, cambiar a CARGADO
                    if (preautorizacion.getUltimoEstado() != null 
                        && "AP".equals(preautorizacion.getUltimoEstado().getId())) {

                        Estado nuevoEstado = new Estado();
                        nuevoEstado.setId("CA"); // CARGADO
                        preautorizacion.setUltimoEstado(nuevoEstado);

                        PreAutorizacionServiceUtil.insertaPreAutorizacionEstado(
                            preautorizacion.getId(),
                            preautorizacion,
                            user.getScreenName(),
                            null                        
                        );
                        
                        preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(preautorizacion.getId());
                        session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, preautorizacion);
                        }
                    
                    Integer idExterno = preautorizacion.getIdPedidoApp();
                    if (idExterno > 0) {
                        try {
                            String token = ClienteAppMobile.obtenerToken();
                            if (token != null) {
                                String codigoExterno = null;
                                
                                String estadoActual = preautorizacion.getUltimoEstado().getId();
                                if ("CA".equals(estadoActual)) {
                                    codigoExterno = "CA";
                                }

                                if (codigoExterno != null) {
                                    ClienteAppMobile.actualizarEstadoPedidoAutorizacion(idExterno, codigoExterno, token);
                                }
                            } else {
                                _log.warn("Token nulo al actualizar estado de preautorizacion " + idExterno);
                            }
                        } catch (Exception e) {
                            _log.error("Error al actualizar estado externo de preautorizacion " + idExterno, e);
                        }
                    }

                	ReportesAutomaticosConfiguracion rac = ReportesServiceUtil.getConfiguracion();
            		String from="";//rac.getMailFrom();
            		List<String>emails = new ArrayList<String>();
            		String subject="";
            		String body="";
            		String destino ="";
            		List<MimeBodyPart> adjunto = null;
            		int cantidadImagenes = 0;

            		boolean rta= false;
            		
//            		SVA, controlar env�o de im�genes pendientes unicamente. 
//            		Ensalud controla las repetidas, pero se nos solicito no enviar todo nuevamente si hay observaciones.
            		List<String> seguimDocs = PreAutorizacionServiceUtil.buscarSeguimientoDocumentos(preautorizacion.getId());
            		List<DLFileEntryImpl>listaImgCasoTodas = PreAutorizacionServiceUtil.getImagenesPreautorizacion("PREAUT_"+preautorizacion.getId()+"-");
            		List<DLFileEntryImpl>listaIngCasoPendientes = new ArrayList<DLFileEntryImpl>();
            		DLFileEntryImpl doc = null;
            		
            		for (int i = 0; i < listaImgCasoTodas.size(); i++) {
    					
    			        doc = listaImgCasoTodas.get(i);
    			        
    			        if(seguimDocs.contains(doc.getName())) {
    			        	listaIngCasoPendientes.add(doc);
    			        }
            		}    
            		
            		if( ( (preautorizacion.isSupra() || preautorizacion.isMedicamento())  && 
            				"GO".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId()) )
            			|| preautorizacion.isAlojamiento() || preautorizacion.isProtesisOrtesis()	){
            		
//            			List<DLFileEntryImpl>list = PreAutorizacionServiceUtil.getImagenesPreautorizacion("PREAUT_"+preautorizacion.getId()+"-");
                		
            			rta = enviarCorreoAutorizacion(preautorizacion, listaIngCasoPendientes, emails, from, subject, body, destino, adjunto, user, rac.getPass());
            			
            			if(rta ) {
            				preautorizacion.setFechaEmail(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
//            				if(preautorizacion.isAlojamiento()  ) {
            				  for(DLFileEntryImpl l:listaIngCasoPendientes) {
            				      PreAutorizacionServiceUtil.marcaEnvioWSSeguimientoDocumento(preautorizacion.getId(), l.getName());
            				  }
//            				}  
            			}

            			
            		}else {
//            			boolean rta=MailUtils.enviarMailGmailconAdjuntoYRespuesta(from, rac.getPass(), emails, subject, body, adjunto);
            			
            			rta = enviarCorreoAutorizacion(preautorizacion, listaIngCasoPendientes, emails, from, subject, body, destino, adjunto, user, rac.getPass());
            			
//DS -- 09/03/2020  -- Agregado para que siempre que se envie un mail, se marque la fecha de envio de las im�genes            			
            			if(rta ) {
            				preautorizacion.setFechaEmail(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
            				  for(DLFileEntryImpl l:listaIngCasoPendientes) {
            				      PreAutorizacionServiceUtil.marcaEnvioWSSeguimientoDocumento(preautorizacion.getId(), l.getName());
            				  }
            			}
//DS Fin agregado 09/03/2020            			
            			
            			
            			//cantidadImagenes = PreAutorizacionServiceUtil.buscarSeguimientoDocumentos(preautorizacion.getId()).size();
            			
//               		Agendar la solicitud de autorizacion a PS, el reporte automatico la correr� en ciclo de 1 hora durante el d�a en curso.
//            			si la cantidad de imagenes es superior al limit de envio por ws (SolitudAutorizacionPS.filesLimit) se debe enviar
//            			1 creacion de solicitud (si es envioEmail = falso) y n envios de informacion adicional, 
//            			(si es envioEmail = true) entonces solo los n envios de informacion adicional
            			
            			/*
            			int cociente,resto;
            			boolean marcaPrimerEnvio = false;
            			boolean necesitaEnviarInfoAdic = false;
            			
            			cociente=cantidadImagenes/SolicitudAutorizacionPS.filesLimit;
            			resto=cantidadImagenes%SolicitudAutorizacionPS.filesLimit;
            			if(resto > 0) {
            				cociente ++;
            			}
            			for (int i = 1; i <= cociente; i++) {
							
            				if(preautorizacion.getFechaEmail()==null && !marcaPrimerEnvio) {
            					rta = this.agendarSolicitudAutorizacionPorWS(preautorizacion.getId(), user, 
                       		    		preautorizacion.getFechaEmail()==null?true:false, i, necesitaEnviarInfoAdic);
            					marcaPrimerEnvio = true;
            					if(cociente > 1) {
            						necesitaEnviarInfoAdic = true;
            					}
            				}else {
            					rta = this.agendarSolicitudAutorizacionPorWS(preautorizacion.getId(), user, 
                       		    		preautorizacion.getFechaEmail()==null?true:false, i, necesitaEnviarInfoAdic);
            				}
            				
            				
						}
            			*/
               		    _log.info("Agendando la solicitud de autorizaci�n a PS - ID: " + preautorizacion.getId());


            		}
            		
           		    
            		if(rta){
            			if(preautorizacion.getFechaEmail()==null){
           				  PreAutorizacionServiceUtil.saveEnvioEmail(idPreautorizacion, user.getScreenName(),true, null); 
            			  preautorizacion.setFechaEmail(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
            			}else{
            			  PreAutorizacionServiceUtil.saveEnvioEmail(idPreautorizacion, user.getScreenName(), false, null);
 //             			  preautorizacion.setFechaEmail2(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
              			  
              			  if("OB".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())){
            				Estado estado= new Estado();
            				estado.setId("CA");
            				preautorizacion.setUltimoEstado(estado);
            				PreAutorizacionServiceUtil.insertaPreAutorizacionEstado(preautorizacion.getId(), preautorizacion, user.getScreenName(), null);
            			  }
              			  
            			}
            			
            			if("NR".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())){
            				Estado estado= new Estado();
            				estado.setId("CA");
            				preautorizacion.setUltimoEstado(estado);
            				PreAutorizacionServiceUtil.insertaPreAutorizacionEstado(preautorizacion.getId(), preautorizacion, user.getScreenName(), null);
            			}
            			session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION , preautorizacion);
            		}else {
            		   SessionErrors.add(renderRequest, "errorAfiliadoNull");
     				   renderRequest.setAttribute("msgInsertError","Error al enviar Mail");
            		}
            		
                }catch(Exception e){
                	_log.error(e);
                }
//                tabSel="datos-imagenes";
                String desdeResult = (String) session.getAttribute("desde_result");
                if(!"SI".equalsIgnoreCase(desdeResult)){
                   renderRequest.setAttribute("tab", tabSel);
				   return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.preautorizacion_editar"));
                }else{
                	return mapping.findForward(getForward(renderRequest,
    						"portlet.autorizaciones.imagenes_preautorizacion"));
                }
			}
           
            if(cmd.equals(Constants.MOVE)){
//            	String cmdEnCurso = ParamUtil.getString(renderRequest, "accionEnCurso");
				String moverATab = ParamUtil.getString(renderRequest, "moverATab");
				String view = ParamUtil.getString(renderRequest, "view");
				session.setAttribute("desde_result","NO");
				renderRequest.setAttribute("view", view);
				tabSel = moverATab;
            }
            
            if(cmd.equals("procesaArchivo")){
				   return mapping.findForward(getForward(renderRequest,
							"portlet.autorizaciones.preautorizacion_procesa_archivo"));	
			}
            
            if(cmd.equals("enviarPS")){
            	
              	String from="";//rac.getMailFrom();
        		List<String>emails = new ArrayList<String>();
        		String subject="";
        		String body="";
        		String destino ="";
        		List<MimeBodyPart> adjunto = null;
        		int cantidadImagenes = 0;

        		boolean rta= false;
            	preautorizacion = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
            	
   	    	    preautorizacion.setFechaEmail(null);
   	    	    
   	    	    PreAutorizacionServiceUtil.desmarcaEnvioWSSeguimientoDocumento(preautorizacion.getId());
   	    	    
   	    	    cantidadImagenes = PreAutorizacionServiceUtil.buscarSeguimientoDocumentos(preautorizacion.getId()).size();
  			   
  			  // int cociente,resto;
  			   //boolean marcaPrimerEnvio = false;
  			   //boolean necesitaEnviarInfoAdic = false;
  			
  			    //cociente=cantidadImagenes/SolicitudAutorizacionPS.filesLimit;
  			    //resto=cantidadImagenes%SolicitudAutorizacionPS.filesLimit;
  			    //if(resto > 0) {
  				//   cociente ++;
  			    //}
  			    
  			    if(!PreAutorizacionServiceUtil.tieneWSSinEnviar(preautorizacion.getId())) {
  			    /* for (int i = 1; i <= cociente; i++) {
					
  				   if(preautorizacion.getFechaEmail()==null && !marcaPrimerEnvio) {
  					 
  				      rta = this.agendarSolicitudAutorizacionPorWS(preautorizacion.getId(), user, 
           		    		preautorizacion.getFechaEmail()==null?true:false, i, necesitaEnviarInfoAdic);
  					 marcaPrimerEnvio = true;
  					 if(cociente > 1) {
  						necesitaEnviarInfoAdic = true;
  					 }
  				   }else {
  					 rta = this.agendarSolicitudAutorizacionPorWS(preautorizacion.getId(), user, 
             		    		preautorizacion.getFechaEmail()==null?true:false, i, necesitaEnviarInfoAdic);
  				   }
  			     }*/
  			    }else {
  			    	rta=true;
  			    }
  			    
  			    if(rta) {
  			      preautorizacion.setFechaEmail(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
    			  
    			  Estado estado= new Estado();
     	    	  estado.setId("CA");
     	    	  preautorizacion.setUltimoEstado(estado);
     	    	  PreAutorizacionServiceUtil.insertaPreAutorizacionEstado(preautorizacion.getId(), preautorizacion, user.getScreenName(), null);
  			    }
   	    	    tabSel="datos";	
			    session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, preautorizacion);
            }
            
			if(cmd.equals(Constants.UPDATE) ){
				boolean validaOk = true;
				
				 //Recupera Datos cargados en la jsp
				preautorizacion = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
				_log.info("[" + rid + "][RENDER][EDIT] buscando preautorizacion id=" + idPreautorizacion);
				preautorizacion = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
				logPreautorizacionSnapshot(rid, "RENDER][EDIT][LOADED", preautorizacion);
				actualizaPreautorizacion(preautorizacion,PortalUtil.getHttpServletRequest(renderRequest), rid);
				
				boolean supra=false;
				if(preautorizacion.getCodigosPresentados()!=null && preautorizacion.getCodigosPresentados().size()>0) {
					for(PreAutorizacionPrestacion p : preautorizacion.getCodigosPresentados()) {
						if(p.getFechaBaja()==null) {
					      supra=preautorizacion.getCodigosPresentados().get(0).getNomenclador().isSupra();
					      break;
						}  
					}    
				}
				preautorizacion.setSupra(supra);
				
//				Validaciones
				if(preautorizacion.getAfiliado()==null || preautorizacion.getAfiliado().getCuil_titular()==null){
				   SessionErrors.add(renderRequest, "errorAfiliadoNull");
				   renderRequest.setAttribute("msgInsertError","Debe Ingresar el Cuil V�lido del Afiliado");
				   validaOk = false;
				}
				_log.info("[" + rid + "][RENDER][UPDATE][VALIDACION]"
						+ " afiliadoNull=" + (preautorizacion.getAfiliado() == null)
						+ ", afiliadoCuil=" + s(preautorizacion.getAfiliado() != null ? preautorizacion.getAfiliado().getCuil_titular() : null)
						+ ", afiliadoInte=" + s(preautorizacion.getAfiliado() != null ? preautorizacion.getAfiliado().getInte() : null)
						+ ", ultimoEstado=" + s(preautorizacion.getUltimoEstado() != null ? preautorizacion.getUltimoEstado().getId() : null));
				List<AfiSuspencionCobertura> suspCoberMedica = null;
				if(preautorizacion.getAfiliado()!=null 
						&& StringUtils.checkNotEmpty(preautorizacion.getAfiliado().getCuil_titular())
//						&& "CA".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId())
					) {
					suspCoberMedica = PlanServiceUtil.getSuspencionesCobMedicaBeneficiario(preautorizacion.getAfiliado().getCuil_titular(), preautorizacion.getAfiliado().getInte());
					
					
					if(!"RE".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId()) ) {
					  if(suspCoberMedica!=null && suspCoberMedica.size()>0) {
						AfiSuspencionCobertura ascm = suspCoberMedica.get(0);
						if(ascm.getVigenDesde().before(preautorizacion.getFecha()) 
								&& (ascm.getVigenHasta() == null 
								|| ascm.getVigenHasta().after(preautorizacion.getFecha()) ) ) {
							
							SessionErrors.add(renderRequest, "errorAfiliadoSinCobertMed");
							   renderRequest.setAttribute("msgErrorAfiSinCobMed","El Afiliado tiene suspendida la cobertura m�dica");
							   validaOk = false;
							   
						}
					  }
					}
				}
				
				if(validaOk){
				    
					/*
					if(supra && PreAutorizacionServiceUtil.getValidaPlanMolinero(preautorizacion.getAfiliado().getCuil_titular(), 0) ) {
						Estado estado= new Estado();
        				estado.setId("GO");
        				preautorizacion.setUltimoEstado(estado);
					}
					*/
					
					if(idPreautorizacion==0){ //Nuevo
						
						if(supra && PreAutorizacionServiceUtil.getValidaPlanMolinero(preautorizacion.getAfiliado().getCuil_titular(), 0) ) {
							Estado estado= new Estado();
	        				estado.setId("GO");
	        				preautorizacion.setUltimoEstado(estado);
						}
						
						_log.debug("usuario preautorizaciones "  + user.getScreenName());
						
						//List<UserGroup> grupos = UserUtil.getUserGroups(user.getUserId());
						List<UserGroup> grupos = UserGroupLocalServiceUtil.getUserUserGroups(user.getUserId());
						if (grupos != null && !grupos.isEmpty()) {
						idPreautorizacion= insertPreautorizacion(preautorizacion,user.getScreenName(),
							   //String.valueOf(UserUtil.getUserGroups(user.getUserId()).get(0).getUserGroupId()),
							   String.valueOf(UserGroupLocalServiceUtil.getUserUserGroups(user.getUserId()).get(0).getUserGroupId()),
							   user.getExpandoBridge().getAttribute("id_seccional").toString());	
						} else {
						    _log.error("El usuario " + user.getScreenName() + " no pertenece a ning�n grupo.");
						    throw new SystemException("El usuario no pertenece a ning�n grupo.");
						}
					   preautorizacion.setId((Integer) idPreautorizacion);
					   try{
					     preautorizacion.setAlta_usr(user.getScreenName());
					     preautorizacion.setAlta_fecha( new Timestamp(new Date().getTime()));
					     preautorizacion.setSeccionalDescripcionAltaUsr(user.getExpandoBridge().getAttribute("seccional").toString());
					   }catch(Exception e){
						   _log.error("Este usuario: "+user.getScreenName() +" no esta correctamente configurado para preautorizaciones m�dicas");
						   _log.error(e);
					   }
					   
					   String tercMONO =TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_TERCERIZADORA_MONOTRIBUTO"); 
					   
					   //Creaomos la autorizacion cuando tiene seleccionado el check de discapacidad en la pantalla de pre autorizacion (this)
					   if (preautorizacion.isDiscapacidad() ||
							   tercMONO.equals(preautorizacion.getAfiliado().getId_tercerizadora())){
						   String obs =""; 	   
					       String periodicidad=null;
					       Date desde=null;
					       Date hasta=null;
					       Integer estado=0;
						   if(!preautorizacion.isDiscapacidad() && tercMONO.equals(preautorizacion.getAfiliado().getId_tercerizadora())) {
							   Calendar calendar =  Calendar.getInstance();
							   calendar.setTime(preautorizacion.getFecha());
							   calendar.add(Calendar.DAY_OF_YEAR, 30);
							   desde = preautorizacion.getFecha();
							   hasta = calendar.getTime();
							   periodicidad="Unica";
							   estado=WebKeysAutorizaciones.TRATAMIENTO_DISCA_ESTADO_MONOTRIBUTO;
							   obs=TraeListasServiceUtil.getSystemConfig("AUTORIZACION_PRESTACIONAL_PREAUTORIZACION_OBS_MONO");
						   }else {
							  desde = DateUtils.getFirstDateOfMonth(new Date() , true);
							  hasta = DateUtils.getLastDateOfYear(new Date(), true);
							  obs=TraeListasServiceUtil.getSystemConfig("AUTORIZACION_PRESTACIONAL_PREAUTORIZACION_OBS");
						   }
						   
						   for(PreAutorizacionPrestacion prest : preautorizacion.getCodigosPresentados()){
						
							   AutoPrestacional autorizacionesPrestacionales =  
									   new AutoPrestacional(0,prest.getNomenclador().getId_prestacion(),
											   preautorizacion.getAfiliado().getCuil_titular(),
											   preautorizacion.getAfiliado().getInte(),
											   String.valueOf( prest.getCantidad()),
											   String.valueOf(prest.getImporte()),
											   periodicidad,
											   desde, //DateUtils.getFirstDateOfMonth(new Date() , true), 
											   hasta, //DateUtils.getLastDateOfYear(new Date(), true),
											   user, "",
											   "", "0", obs, false,
											   estado, null, "0","0", "0","0", "0", "0","0", "0",
											   null,preautorizacion.getPrestador()!=null && 
											   preautorizacion.getPrestador().getId_prestador()>0?
													   preautorizacion.getPrestador().getId_prestador():0,"false",
													   preautorizacion.isDiscapacidad()?true:false,"0");
							   
							   
							   autorizacionesPrestacionales.setCopago(0);
							   autorizacionesPrestacionales.setObservacionesInternas(preautorizacion.getObservaciones());
							   
							   AutorizacionPrestacionalServiceUtil.save(autorizacionesPrestacionales, idPreautorizacion);
							   
						   }
						  
					   }
					   
					   msg = LanguageUtil.get(defaultLocale, "insert-preautorizacion");
							  msg = msg +" " +idPreautorizacion;
							  SessionMessages.add(renderRequest, "insertCabOk");
							  renderRequest.setAttribute("msgCabOk", msg);
							  _log.debug("Usuario: " + user.getScreenName() 
									+ " cmd: " + cmd 
									+ " id preAutoriz: " + idPreautorizacion
									);
						
					}else if(idPreautorizacion!=0){
						
						PreAutorizacion original = PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
						String estadoAnterior = original != null && original.getUltimoEstado() != null 
						    ? original.getUltimoEstado().getId() 
						    : null;
						    
						 //obtener el estado seleccionado en el formulario
						 String estadoNuevoSeleccionado = ParamUtil.getString(renderRequest, "estadoPreautorizacion");

						 if (estadoNuevoSeleccionado != null && !estadoNuevoSeleccionado.isEmpty()) {
							 Estado nuevoEstado = new Estado(estadoNuevoSeleccionado, "");
						     preautorizacion.setUltimoEstado(nuevoEstado);
						 }
    
						updatePreautorizacion(preautorizacion, user.getScreenName());
						
						String estadoNuevo = preautorizacion.getUltimoEstado() != null 
							    ? preautorizacion.getUltimoEstado().getId() 
							    : null;
						
					    Integer idPedidoApp = ClienteAppMobileServiceUtil.getIdPedidoAppPorPreautorizacion(idPreautorizacion);

						if (idPedidoApp != null && estadoNuevo != null && estadoNuevo != null && !"AP".equalsIgnoreCase(estadoNuevo) && !"GO".equalsIgnoreCase(estadoNuevo)) {
						    if (idPedidoApp != null) {
						        String token = ClienteAppMobile.obtenerToken();
						        if (token != null) {
						            ClienteAppMobile.actualizarEstadoPedidoAutorizacion(idPedidoApp, estadoNuevo, token);
						        } else {
						            _log.warn("No se pudo obtener token para actualizar estado");
						        }
						    } else {
						        _log.warn("No se encontr� idPedidoApp para la preautorizaci�n " + idPreautorizacion);
						    }
						}
						
						msg = LanguageUtil.get(defaultLocale, "update-preautorizacion");
						msg = msg + " "+ idPreautorizacion;
						SessionMessages.add(renderRequest, "updateCabOk");
						renderRequest.setAttribute("msgCabOk", msg);
						_log.debug("Usuario: " + user.getScreenName() 
								+ " cmd: " + cmd 
								+ " id preAutoriz: " + idPreautorizacion
								);
						
					}
					
					if(idPreautorizacion!=0) {
						preautorizacion=PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacion);
					}
					
				}	
					
				tabSel="datos";	
				session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, preautorizacion);	
			}
			
			if(cmd.equals(Constants.COPY) ){
				
				int idPreautorizacionClon = PreAutorizacionServiceUtil.clonarPreautorizacion(idPreautorizacion, user);
				
				preautorizacion=PreAutorizacionServiceUtil.buscarPreautorizacionPorId(idPreautorizacionClon);
				
				session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, preautorizacion);
				
			}
			
			
		}
		renderRequest.setAttribute("tab", tabSel);
		return mapping.findForward("portlet.autorizaciones.preautorizacion_editar");
		
	}


	private Integer insertPreautorizacion(PreAutorizacion preautorizacion, String user,String sector,String id_seccional) throws Exception{
		String rid = rid();
		_log.info("[" + rid + "][INSERT_PREAUT][START] user=" + s(user)
				+ ", sector=" + s(sector)
				+ ", id_seccional=" + s(id_seccional));
		logPreautorizacionSnapshot(rid, "INSERT_PREAUT][PAYLOAD", preautorizacion);

		Integer seccionalId = Integer.valueOf(id_seccional);
		Integer id = PreAutorizacionServiceUtil.insertaPreAutorizacion(preautorizacion, user,sector,seccionalId);

		_log.info("[" + rid + "][INSERT_PREAUT][END] idGenerado=" + s(id));
		return id;
	}

	private long updatePreautorizacion(PreAutorizacion preautorizacion, String user) throws Exception{
		String rid = rid();
		_log.info("[" + rid + "][UPDATE_PREAUT][START] user=" + s(user));
		logPreautorizacionSnapshot(rid, "UPDATE_PREAUT][PAYLOAD", preautorizacion);

		long id = PreAutorizacionServiceUtil.updatePreautorizacion(preautorizacion, user);

		_log.info("[" + rid + "][UPDATE_PREAUT][END] result=" + id);
		return id;
	}
	
//----
//----	



	private void actualizaPreautorizacion(PreAutorizacion preautorizacion,HttpServletRequest renderRequest, String rid) throws SystemException{

		_log.info("[" + rid + "][ACTUALIZA_PREAUT][START]");
		logRequestBasico(rid, renderRequest);
		logPreautorizacionSnapshot(rid, "ACTUALIZA_PREAUT][BEFORE", preautorizacion);

		String fechaPreAutorizacionDia = ParamUtil.getString(renderRequest,"fechaPreAutorizacionDia");
		String fechaPreAutorizacionMes = ParamUtil.getString(renderRequest,"fechaPreAutorizacionMes");
		String fechaPreAutorizacionAnio = ParamUtil.getString(renderRequest,"fechaPreAutorizacionAnio");

		Date fechaAutorizacion = null;
		try {
			fechaAutorizacion = formatoDeFechas.parse(fechaPreAutorizacionDia + "/"
					+ (Integer.parseInt(fechaPreAutorizacionMes) + 1) + "/"
					+ fechaPreAutorizacionAnio);
		} catch (Exception e) {
			_log.warn("[" + rid + "][ACTUALIZA_PREAUT][PARSE_FECHA_AUTORIZACION][WARN]"
					+ " dia=" + fechaPreAutorizacionDia
					+ ", mes=" + fechaPreAutorizacionMes
					+ ", anio=" + fechaPreAutorizacionAnio, e);
			fechaAutorizacion = null;
		}

		String estadoPreautorizacion=ParamUtil.getString(renderRequest,"estadoPreautorizacion",null);
		String motivoRechazo=ParamUtil.getString(renderRequest,"motivoRechazo",null);

		String cuilTitular=ParamUtil.getString(renderRequest,"cuil",null);
		Integer integrante=ParamUtil.getInteger(renderRequest, "inte",0);

		_log.info("[" + rid + "][ACTUALIZA_PREAUT][AFILIADO_INPUT]"
				+ " cuilTitular=" + s(cuilTitular)
				+ ", integrante=" + s(integrante)
				+ ", estadoPreautorizacion=" + s(estadoPreautorizacion)
				+ ", motivoRechazo=" + s(motivoRechazo));

		String fechaRespuestaPSDia = ParamUtil.getString(renderRequest,"fechaRespuestaPSDia");
		String fechaRespuestaPSMes = ParamUtil.getString(renderRequest,"fechaRespuestaPSMes");
		String fechaRespuestaPSAnio = ParamUtil.getString(renderRequest,"fechaRespuestaPSAnio");

		Date fechaRespuesta = null;
		try {
			fechaRespuesta = formatoDeFechas.parse(fechaRespuestaPSDia + "/"
					+ (Integer.parseInt(fechaRespuestaPSMes) + 1) + "/"
					+ fechaRespuestaPSAnio);
		} catch (Exception e) {
			_log.warn("[" + rid + "][ACTUALIZA_PREAUT][PARSE_FECHA_RESPUESTA][WARN]"
					+ " dia=" + fechaRespuestaPSDia
					+ ", mes=" + fechaRespuestaPSMes
					+ ", anio=" + fechaRespuestaPSAnio, e);
			fechaRespuesta = null;
		}

		String fechaNotificacionDia = ParamUtil.getString(renderRequest,"fechaNotificacionDia");
		String fechaNotificacionMes = ParamUtil.getString(renderRequest,"fechaNotificacionMes");
		String fechaNotificacionAnio = ParamUtil.getString(renderRequest,"fechaNotificacionAnio");

		Date fechaNotificacion = null;
		try {
			fechaNotificacion = formatoDeFechas.parse(fechaNotificacionDia + "/"
					+ (Integer.parseInt(fechaNotificacionMes) + 1) + "/"
					+ fechaNotificacionAnio);
		} catch (Exception e) {
			_log.warn("[" + rid + "][ACTUALIZA_PREAUT][PARSE_FECHA_NOTIFICACION][WARN]"
					+ " dia=" + fechaNotificacionDia
					+ ", mes=" + fechaNotificacionMes
					+ ", anio=" + fechaNotificacionAnio, e);
			fechaNotificacion = null;
		}

		String tipoEntrega=ParamUtil.getString(renderRequest,"tipoEntrega",null);
		tipoEntrega="0".equalsIgnoreCase(tipoEntrega)?null:tipoEntrega;

		String fechaEntregaDia = ParamUtil.getString(renderRequest,"fechaEntregaDia");
		String fechaEntregaMes = ParamUtil.getString(renderRequest,"fechaEntregaMes");
		String fechaEntregaAnio = ParamUtil.getString(renderRequest,"fechaEntregaAnio");

		Date fechaEntrega = null;
		try {
			fechaEntrega = formatoDeFechas.parse(fechaEntregaDia + "/"
					+ (Integer.parseInt(fechaEntregaMes) + 1) + "/"
					+ fechaEntregaAnio);
		} catch (Exception e) {
			_log.warn("[" + rid + "][ACTUALIZA_PREAUT][PARSE_FECHA_ENTREGA][WARN]"
					+ " dia=" + fechaEntregaDia
					+ ", mes=" + fechaEntregaMes
					+ ", anio=" + fechaEntregaAnio, e);
			fechaEntrega = null;
		}

		Afiliado afiliado= new Afiliado();
		try {
			_log.info("[" + rid + "][ACTUALIZA_PREAUT][AFILIADO_FETCH][START]"
					+ " cuilTitular=" + s(cuilTitular)
					+ ", integrante=" + s(integrante));

			afiliado = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuilTitular, integrante);

			_log.info("[" + rid + "][ACTUALIZA_PREAUT][AFILIADO_FETCH][OK]"
					+ " cuilTitular=" + s(afiliado != null ? afiliado.getCuil_titular() : null)
					+ ", inte=" + s(afiliado != null ? afiliado.getInte() : null)
					+ ", doc=" + s(afiliado != null ? afiliado.getDocu_numero() : null)
					+ ", id_tercerizadora(before)=" + s(afiliado != null ? afiliado.getId_tercerizadora() : null));

			AfiTercerizadoraServicio ats = TercerizadoraServiceUtil.getInstance().buscarUltimaTercerizadoraDelAfiliado(null, cuilTitular);

			_log.info("[" + rid + "][ACTUALIZA_PREAUT][TERCERIZADORA_FETCH][OK]"
					+ " cuilTitular=" + s(cuilTitular)
					+ ", atsNull=" + (ats == null)
					+ ", tercerizadoraNull=" + (ats != null ? ats.getTercerizadora() == null : true)
					+ ", id_tercerizadora=" + s(ats != null && ats.getTercerizadora() != null ? ats.getTercerizadora().getId_tercerizadora() : null));

			if (afiliado != null && ats != null && ats.getTercerizadora() != null) {
				afiliado.setId_tercerizadora(ats.getTercerizadora().getId_tercerizadora());
			}
		} catch (Exception e) {
			_log.error("[" + rid + "][ACTUALIZA_PREAUT][AFILIADO_FETCH][ERROR]"
					+ " cuilTitular=" + s(cuilTitular)
					+ ", integrante=" + s(integrante), e);
		}

		preautorizacion.setAfiliado(afiliado);

		preautorizacion.setFecha(fechaAutorizacion);
		preautorizacion.setFechaEntregaRespuesta(fechaEntrega);

		if(fechaNotificacion==null && fechaEntrega != null){
			preautorizacion.setFechaNotificacionAfiliado(fechaEntrega);
		}else{
			preautorizacion.setFechaNotificacionAfiliado(fechaNotificacion);
		}
		preautorizacion.setFechaRespuestaPS(fechaRespuesta);
		preautorizacion.setTipoEntrega(tipoEntrega);

		Estado e =new Estado();
		e.setId(estadoPreautorizacion);
		e.setMotivoRechazo(motivoRechazo);
		preautorizacion.setUltimoEstado(e);

		String observaciones=ParamUtil.getString(renderRequest,"observacionesPreautorizacion",null);
		preautorizacion.setObservaciones(observaciones);

		String observacionesTerc=ParamUtil.getString(renderRequest,"observacionesPreautorizacionTerc",null);
		preautorizacion.setObservacionesTercerizadoras(observacionesTerc);

		boolean esMedicamento=ParamUtil.getBoolean(renderRequest, "medicamentoChk");

		boolean chHC = ParamUtil.getBoolean(renderRequest, "chHC");
		boolean chEC = ParamUtil.getBoolean(renderRequest, "chEC");
		boolean chBI = ParamUtil.getBoolean(renderRequest, "chBI");
		boolean chAN = ParamUtil.getBoolean(renderRequest, "chAN");

		preautorizacion.setHistoriaClinica(chHC);
		preautorizacion.setEstudiosComplementarios(chEC);
		preautorizacion.setBiopsia(chBI);
		preautorizacion.setAnatomiaPatologica(chAN);
		preautorizacion.setMedicamento(esMedicamento);

		boolean alertaRoja = ParamUtil.getBoolean(renderRequest, "alertaRoja");
		boolean esDiscapacidad=ParamUtil.getBoolean(renderRequest, "discapacidadChk");

		preautorizacion.setAlertaRoja(alertaRoja);
		preautorizacion.setDiscapacidad(esDiscapacidad);

		_log.info("[" + rid + "][ACTUALIZA_PREAUT][FLAGS]"
				+ " alertaRoja=" + alertaRoja
				+ ", esDiscapacidad=" + esDiscapacidad
				+ ", esMedicamento=" + esMedicamento
				+ ", chHC=" + chHC
				+ ", chEC=" + chEC
				+ ", chBI=" + chBI
				+ ", chAN=" + chAN);

		String fechaEnvioTercerizadoraDia = ParamUtil.getString(renderRequest,"fechaEnvioTercerizadoraDia");
		String fechaEnvioTercerizadoraMes = ParamUtil.getString(renderRequest,"fechaEnvioTercerizadoraMes");
		String fechaEnvioTercerizadoraAnio = ParamUtil.getString(renderRequest,"fechaEnvioTercerizadoraAnio");

		Date fechaEnvioTercerizadora = null;
		try {
			fechaEnvioTercerizadora = formatoDeFechas.parse(fechaEnvioTercerizadoraDia + "/"
					+ (Integer.parseInt(fechaEnvioTercerizadoraMes) + 1) + "/"
					+ fechaEnvioTercerizadoraAnio);
		} catch (Exception e1) {
			fechaEnvioTercerizadora = null;
		}

		String fechaRecepcionTercerizadoraDia = ParamUtil.getString(renderRequest,"fechaRecepcionTercerizadoraDia");
		String fechaRecepcionTercerizadoraMes = ParamUtil.getString(renderRequest,"fechaRecepcionTercerizadoraMes");
		String fechaRecepcionTercerizadoraAnio = ParamUtil.getString(renderRequest,"fechaRecepcionTercerizadoraAnio");

		Date fechaRecepcionTercerizadora = null;
		try {
			fechaRecepcionTercerizadora = formatoDeFechas.parse(fechaRecepcionTercerizadoraDia + "/"
					+ (Integer.parseInt(fechaRecepcionTercerizadoraMes) + 1) + "/"
					+ fechaRecepcionTercerizadoraAnio);
		} catch (Exception e1) {
			fechaRecepcionTercerizadora = null;
		}

		if(!esDiscapacidad){
			preautorizacion.setFechaEnvioTercerizadora(null);
			preautorizacion.setFechaRecepcionTercerizadora(null);
		}else{
			preautorizacion.setFechaEnvioTercerizadora(fechaEnvioTercerizadora);
			preautorizacion.setFechaRecepcionTercerizadora(fechaRecepcionTercerizadora);
		}

		try {
			String tipoPedidoOspim = ParamUtil.getString(renderRequest,"tipoPedidoOSPIM");
			String strEstadoOspim = ParamUtil.getString(renderRequest,"estadoOSPIM");
			String gestionOspim = ParamUtil.getString(renderRequest,"gestionOSPIM");
			String observacionOspim = ParamUtil.getString(renderRequest,"observacionesOSPIM");

			preautorizacion.setTipoPedidoGestionOSPIM(tipoPedidoOspim);
			preautorizacion.setTipoGestionOSPIM(gestionOspim);
			preautorizacion.setObservacionesOSPIM(observacionOspim);

			Estado estadoOspim = new Estado();
			estadoOspim.setId(strEstadoOspim);
			preautorizacion.setUltimoEstadoOSPIM(estadoOspim);

			_log.info("[" + rid + "][ACTUALIZA_PREAUT][OSPIM]"
					+ " tipoPedidoOspim=" + s(tipoPedidoOspim)
					+ ", strEstadoOspim=" + s(strEstadoOspim)
					+ ", gestionOspim=" + s(gestionOspim)
					+ ", observacionOspim=" + s(observacionOspim));
		}catch(Exception ex) {
			_log.error("[" + rid + "][ACTUALIZA_PREAUT][OSPIM][ERROR]", ex);
		}

		String diagnostico = ParamUtil.getString(renderRequest,"id_diagnostico");
		if(diagnostico!=null) {
			preautorizacion.setDiagnostico(new ClaseBase(diagnostico,""));
		}

		boolean esAlojamiento=ParamUtil.getBoolean(renderRequest, "alojamientoChk");
		preautorizacion.setAlojamiento(esAlojamiento);

		if (preautorizacion.getUltimoEstado() != null && !"AP".equals(preautorizacion.getUltimoEstado().getId())){
			if(preautorizacion.getFechaEmail()==null
					&& !preautorizacion.getRequiereAutorizacion()
					&& !preautorizacion.isSupra()
					&& !preautorizacion.isMedicamento()
					&& !preautorizacion.isAlojamiento()){

				_log.info("[" + rid + "][ACTUALIZA_PREAUT][AUTO_ESTADO_NR]"
						+ " fechaEmail=" + s(preautorizacion.getFechaEmail())
						+ ", requiereAutorizacion=" + preautorizacion.getRequiereAutorizacion()
						+ ", supra=" + preautorizacion.isSupra()
						+ ", medicamento=" + preautorizacion.isMedicamento()
						+ ", alojamiento=" + preautorizacion.isAlojamiento());

				Estado estado = new Estado();
				estado.setId("NR");
				preautorizacion.setUltimoEstado(estado);
			}
		}

		if(esAlojamiento) {
			String fechaAlojamientoDesdeDia = ParamUtil.getString(renderRequest,"fechaPreAutorizacionAlojamientoDiaDesde");
			String fechaAlojamientoDesdeMes = ParamUtil.getString(renderRequest,"fechaPreAutorizacionAlojamientoMesDesde");
			String fechaAlojamientoDesdeAnio = ParamUtil.getString(renderRequest,"fechaPreAutorizacionAlojamientoAnioDesde");

			Date fechaAlojamientoDesde = null;
			try {
				fechaAlojamientoDesde = formatoDeFechas.parse(fechaAlojamientoDesdeDia + "/"
						+ (Integer.parseInt(fechaAlojamientoDesdeMes) + 1) + "/"
						+ fechaAlojamientoDesdeAnio);
			} catch (Exception e1) {
				fechaAlojamientoDesde = null;
			}
			preautorizacion.setAlojamientoDesde(fechaAlojamientoDesde);

			String fechaAlojamientoHastaDia = ParamUtil.getString(renderRequest,"fechaPreAutorizacionAlojamientoDiaHasta");
			String fechaAlojamientoHastaMes = ParamUtil.getString(renderRequest,"fechaPreAutorizacionAlojamientoMesHasta");
			String fechaAlojamientoHastaAnio = ParamUtil.getString(renderRequest,"fechaPreAutorizacionAlojamientoAnioHasta");

			Date fechaAlojamientoHasta = null;
			try {
				fechaAlojamientoHasta = formatoDeFechas.parse(fechaAlojamientoHastaDia + "/"
						+ (Integer.parseInt(fechaAlojamientoHastaMes) + 1) + "/"
						+ fechaAlojamientoHastaAnio);
			} catch (Exception e1) {
				fechaAlojamientoHasta = null;
			}
			preautorizacion.setAlojamientoHasta(fechaAlojamientoHasta);

			_log.info("[" + rid + "][ACTUALIZA_PREAUT][ALOJAMIENTO]"
					+ " desde=" + s(fechaAlojamientoDesde)
					+ ", hasta=" + s(fechaAlojamientoHasta));
		}

		boolean esProtesisOrt=ParamUtil.getBoolean(renderRequest, "protesisOrtChk");
		preautorizacion.setProtesisOrtesis(esProtesisOrt);

		boolean esPosibleArt=ParamUtil.getBoolean(renderRequest, "artChk");
		preautorizacion.setART(esPosibleArt);

		Prestador prestador=new Prestador();
		if(esDiscapacidad){
			Integer prestadorId = ParamUtil.getInteger(renderRequest,"id_prestador_aut");
			String prestadorCuit=ParamUtil.getString(renderRequest,"cuit_prestador_aut");
			String prestadorDescripcion=ParamUtil.getString(renderRequest,"nombre_prestador_aut");

			prestador.setId_prestador(prestadorId);
			prestador.setCuit(prestadorCuit);
			prestador.setDescripcion(prestadorDescripcion);

			_log.info("[" + rid + "][ACTUALIZA_PREAUT][PRESTADOR_DISCA]"
					+ " prestadorId=" + s(prestadorId)
					+ ", prestadorCuit=" + s(prestadorCuit)
					+ ", prestadorDescripcion=" + s(prestadorDescripcion));
		}
		preautorizacion.setPrestador(prestador);

		logPreautorizacionSnapshot(rid, "ACTUALIZA_PREAUT][AFTER", preautorizacion);
		_log.info("[" + rid + "][ACTUALIZA_PREAUT][END]");
	}
	
	
	
//--Preautorizacion
//--	
	private void addPrestacion(RenderRequest renderRequest,HttpSession session) throws Exception{
		String rid = rid();
		_log.info("[" + rid + "][ADD_PRESTACION][START]"
				+ " codigo=" + s(ParamUtil.getString(renderRequest,"codigo"))
				+ ", descripcion=" + s(ParamUtil.getString(renderRequest,"descripcion"))
				+ ", tiponomenclador=" + s(ParamUtil.getString(renderRequest,"tiponomenclador"))
				+ ", idpreautorizacioncodigo=" + s(ParamUtil.getString(renderRequest,"idpreautorizacioncodigo"))
				+ ", cantidad=" + s(ParamUtil.getString(renderRequest,"cantidad"))
				+ ", importe=" + s(ParamUtil.getString(renderRequest,"importe")));
		String codigo=ParamUtil.getString(renderRequest,"codigo",null);
		String descripcion=ParamUtil.getString(renderRequest,"descripcion",null);
		Integer tipoNomenclador = ParamUtil.getInteger(renderRequest, "tiponomenclador",0);
		String tipoNomencladorDescripcion = ParamUtil.getString(renderRequest, "tiponomencladordescripcion",null);
		Integer idDetalle = ParamUtil.getInteger(renderRequest, "iddetalle",0);
		Integer idDetalleAux = ParamUtil.getInteger(renderRequest, "iddetalleaux",0);
		Integer idPreautorizacionCodigo = ParamUtil.getInteger(renderRequest, "idpreautorizacioncodigo",0);
		Double cantidad=ParamUtil.getDouble(renderRequest, "cantidad",0);
		Double importe=ParamUtil.getDouble(renderRequest, "importe",0);
		boolean requiereAutorizacion = ParamUtil.getBoolean(renderRequest,"requiereautorizacion",false);
		boolean supra = ParamUtil.getBoolean(renderRequest,"supra",false);
		Integer tipoApoyo=ParamUtil.getInteger(renderRequest, "tipoapoyo");
		String tipoApoyoDescripcion=ParamUtil.getString(renderRequest,"tipoapoyodescripcion");
		boolean cirugia = ParamUtil.getBoolean(renderRequest,"cirugia",false);
	
		String cuilTitular=ParamUtil.getString(renderRequest,"cuil_titu",null);

		
		
		Nomenclador nomenclador = new Nomenclador();
		nomenclador.setCodigo(codigo);
		nomenclador.setDescripcion(descripcion);
		nomenclador.setId_prestacion(idPreautorizacionCodigo);
		nomenclador.setRequiereAutorizacion(requiereAutorizacion);
		nomenclador.setDescripcionTipoNomenclador(tipoNomencladorDescripcion);
		nomenclador.setId_tipo_nomenclador(tipoNomenclador);
		nomenclador.setSupra(supra);
		nomenclador.setCirugia(cirugia);
		
		/*
		if ("800000".equals(codigo)){
			AfiPlan afiPlan = planService.buscarUltimoPlanAportes(cuilTitular); 

			if (afiPlan.getPlan().getDescripcionEnsalud() !=null && 
					("INTEGRAL".equals(afiPlan.getPlan().getDescripcionTarjeta())
					|| "TOTAL".equals(afiPlan.getPlan().getDescripcionTarjeta())		
					||	"CORPORATIVO INTEGRAL".equals(afiPlan.getPlan().getDescripcionTarjeta())	
					||  "CORPORATIVO TOTAL".equals(afiPlan.getPlan().getDescripcionTarjeta())
							)){
				_log.debug("Marca Supra por codigo  800000");
				nomenclador.setSupra(true);
			}
		}
		*/
		Nomenclador n = NomencladorServiceUtil.buscarNomencladorPorId(idPreautorizacionCodigo);
		nomenclador.setRecuperaSUR(n.getRecuperaSUR()); 
		OpcionesPrestacion apoyo= new OpcionesPrestacion(tipoApoyo, tipoApoyoDescripcion, "");
		
		
		
		PreAutorizacionPrestacion prestacion = new PreAutorizacionPrestacion();
		prestacion.setNomenclador(nomenclador);
		prestacion.setCantidad(cantidad);
		prestacion.setImporte(importe);
		prestacion.setId(idDetalle);
		if(idDetalle==0 && idDetalleAux==0){
			Integer rnd = 0;
			Random r = new Random();
			rnd=r.nextInt((1000 - 1) + 1) + 1;
		    prestacion.setIdAux(rnd);	
		}else if(idDetalleAux!=0){
			prestacion.setIdAux(idDetalleAux);
		}
		
		prestacion.setOpcionApoyo(apoyo);
		
		PreAutorizacion pa= (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
		
		
		boolean encontro=false;
		for(int xi=0;xi<pa.getCodigosPresentados().size();xi++){
      	  if(pa.getCodigosPresentados().get(xi).getIdAux().equals(prestacion.getIdAux())){
      		pa.getCodigosPresentados().set(xi,prestacion);
      		encontro=true;
      		break;
      	  }
        }
		
		if(!encontro){
		  pa.getCodigosPresentados().add(prestacion);
		}
		session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, pa);
		_log.info("[" + rid + "][ADD_PRESTACION][END]"
				+ " totalCodigos=" + (pa.getCodigosPresentados() != null ? pa.getCodigosPresentados().size() : -1)
				+ ", encontro=" + encontro
				+ ", idAux=" + s(prestacion.getIdAux()));
	}
	
	
	private void deletePrestacion(RenderRequest renderRequest,HttpSession session) throws SystemException{
		Integer idDetalle = ParamUtil.getInteger(renderRequest, "detalleid",0);
		
		List<PreAutorizacionPrestacion> pap= new ArrayList<PreAutorizacionPrestacion>();
		
		PreAutorizacion pa= (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
		for(PreAutorizacionPrestacion p:pa.getCodigosPresentados()){
			if(!idDetalle.equals(p.getIdAux())){
				pap.add(p);
			}else if(idDetalle.equals(p.getIdAux()) && (p.getId()!=null && p.getId()!=0)){
			    p.setFechaBaja(new Date());
			    pap.add(p);
			}
		}
		pa.setCodigosPresentados(pap);
		session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, pa);
		
	}

	private void filterPreautorizacion(RenderRequest renderRequest,HttpSession session) throws SystemException{
		
		String cuil=ParamUtil.getString(renderRequest,"cuil",null);
		String inteParam =  ParamUtil.getString(renderRequest, "inte",null);
		Integer inte = null;
		try {
			inte = Integer.parseInt(inteParam);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String estado=ParamUtil.getString(renderRequest,"estado",null);
		
		String fechaDia = ParamUtil.getString(renderRequest,"fechadesdedia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechadesdemes");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
		
		String fechaDiaH = ParamUtil.getString(renderRequest,"fechahastadia");
		String fechaMesH = ParamUtil.getString(renderRequest,"fechahastames");
		String fechaAnioH = ParamUtil.getString(renderRequest,"fechahastaanio");
		
		Date fechaD = null;
		try {
			fechaD = formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fechaD = null;
		}
		
		Date fechaH = null;
		try {
			fechaH = formatoDeFechas.parse(fechaDiaH + "/"
					+ (Integer.parseInt(fechaMesH) + 1) + "/"
					+ fechaAnioH);
		} catch (Exception e) {
			fechaH = null;
		}
		
		Integer id = ParamUtil.getInteger(renderRequest, "id",0);
		
		
//////////////////////		
		String fechaEmailDia = ParamUtil.getString(renderRequest,"fechadesdeemaildia");
		String fechaEmailMes = ParamUtil.getString(renderRequest,"fechadesdeemailmes");
		String fechaEmailAnio = ParamUtil.getString(renderRequest,"fechadesdeemailanio");
		
		String fechaEmailDiaH = ParamUtil.getString(renderRequest,"fechahastaemaildia");
		String fechaEmailMesH = ParamUtil.getString(renderRequest,"fechahastaemailmes");
		String fechaEmailAnioH = ParamUtil.getString(renderRequest,"fechahastaemailanio");
		String seccional = ParamUtil.getString(renderRequest,"seccional");
		boolean alertaRoja=ParamUtil.getBoolean(renderRequest, "alertaroja");
		boolean discapacidad=ParamUtil.getBoolean(renderRequest, "discapacidad");
		boolean supra=ParamUtil.getBoolean(renderRequest, "supra");
		boolean cirugia=ParamUtil.getBoolean(renderRequest, "cirugia");
		boolean medicamento=ParamUtil.getBoolean(renderRequest, "medicamento");
		boolean sinReintento=ParamUtil.getBoolean(renderRequest, "sin_reintento");
		boolean alojamiento=ParamUtil.getBoolean(renderRequest, "alojamiento");
		boolean protesisOrt=ParamUtil.getBoolean(renderRequest, "protesisOrtesis");
		boolean art=ParamUtil.getBoolean(renderRequest, "posibleart");
		
		Integer idAutorizacion = ParamUtil.getInteger(renderRequest, "idAutorizacion",0);
		Date fechaEmail = null;
		try {
			fechaEmail= formatoDeFechas.parse(fechaEmailDia + "/"
					+ (Integer.parseInt(fechaEmailMes) + 1) + "/"
					+ fechaEmailAnio);
		} catch (Exception e) {
			fechaEmail = null;
		}
		
		Date fechaEmailH = null;
		try {
			fechaEmailH = formatoDeFechas.parse(fechaEmailDiaH + "/"
					+ (Integer.parseInt(fechaEmailMesH) + 1) + "/"
					+ fechaEmailAnioH);
		} catch (Exception e) {
			fechaEmailH = null;
		}
		
		Integer idSeccional=null;
		if(seccional!=null && !"".equalsIgnoreCase(seccional)){
			try{
			  idSeccional=Integer.valueOf(seccional);
			}catch(Exception e){}  
		}
		
		session.removeAttribute(WebKeysAutorizaciones.PREAUTORIZACIONES_FILTRO);
		session.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_PREAUTORIZACIONES_RESULT);
			
		BusquedaPreautorizacionesFiltro filtro = new BusquedaPreautorizacionesFiltro(id, cuil, inte, fechaD, fechaH, estado, fechaEmail, 
				fechaEmailH, idSeccional, alertaRoja, discapacidad, supra, cirugia, medicamento, sinReintento, 
				alojamiento, idAutorizacion, protesisOrt,art, 0);
		
		List<PreAutorizacion> lista = PreAutorizacionServiceUtil.getListaPreAutorizacion(filtro);
		
		session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACIONES_FILTRO,filtro);
		session.setAttribute(WebKeysAutorizaciones.BUSQUEDA_PREAUTORIZACIONES_RESULT,lista);
		
	}
	
	private boolean esPlanMolinero(Integer idPlan) {
		
		boolean ret=false;
		String planes = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_PLANES_MOLINEROS");
		
		int resultado = planes.indexOf(idPlan.toString().trim());
        
        if(resultado != -1) {
        	ret=true;
        }
		
		return ret;
	}
	
/*	private boolean agendarSolicitudAutorizacionPorWS(int idPreAut, User user, boolean primeraVez, int parte, boolean necesitaEnviarInfoAdic) throws SystemException {
		
		boolean result = true;
		
		ReporteAutomatico raAux = new ReporteAutomatico();
		raAux.setFechaUnicaVez(new Date()); 
		raAux.setHora(99);
		if(primeraVez && parte == 1) {
			raAux.setTitulo("Solitud de autorizaci�n por ws ID: " + idPreAut);
			raAux.setJava("ar.com.ospim.autorizaciones.beans.SolicitudAutorizacionPS");
		}
		if(primeraVez && parte > 1){
			raAux.setTitulo("Env�o de info adicional autorizaci�n por ws ID: " + idPreAut + " parte: " + parte + (necesitaEnviarInfoAdic?" CONTINUA":"") );
		    raAux.setJava("ar.com.ospim.autorizaciones.beans.EnvioInfoAdicionalPS");
		} 
		if(!primeraVez){
			raAux.setTitulo("Env�o de info adicional autorizaci�n por ws ID: " + idPreAut + " parte: " + parte + (necesitaEnviarInfoAdic?" CONTINUA":"") );
		    raAux.setJava("ar.com.ospim.autorizaciones.beans.EnvioInfoAdicionalPS");
		} 
		
		raAux.setEmails("");
		raAux.setBase(1);
		raAux.setCsvParameteres(user.getScreenName() + "=String,");
		raAux.setDiaDeLaSemana(0);
		raAux.setDiaDelMes(0);
		raAux.setDiario(false); 
		raAux.setDifusion(0);
		raAux.setIncluirFinDeSemana(false);
		raAux.setStoredProcedure(null);
		raAux.setUltimaEjecucion(null);
		
		try {
			ReportesServiceUtil.save(raAux);
		}catch (SystemException e) {
			_log.error(e);
			result= false;
		}	
		
		return result;
	}*/
	
	private boolean enviarCorreoAutorizacion(PreAutorizacion preautorizacion, List<DLFileEntryImpl>list, 
			List<String> emails, String from, String subject, String body, String destino, 
			List<MimeBodyPart> adjunto, User user, String password) {
		String sizeValidoAdjuntos =TraeListasServiceUtil.getSystemConfig("GMAIL_SIZE_VALIDO_ADJUNTOS");
		String planNuevaTercerizadora =TraeListasServiceUtil.getSystemConfig("EMAIL_PLAN_MONOTRIBUTO");
		String seccionalesExceptuadas =TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_TERCERIZADORA_ESPECIAL_SECCIONALES_EXCEPTUADAS");
		
		Double sizeValidoAdj=Double.valueOf(sizeValidoAdjuntos);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		boolean result = false;
		if(preautorizacion.isAlojamiento()) {
		   destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_ALOJAMIENTO");
 		}else if(preautorizacion.isProtesisOrtesis()|| preautorizacion.isCirugia() ){
   		   //destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_PROTESIS");
   		   if(preautorizacion.getAfiliado()!=null &&   preautorizacion.getAfiliado().getAfiPlan()!=null &&
        		   preautorizacion.getAfiliado().getAfiPlan().getPlan()!=null &&
        		   preautorizacion.getAfiliado().getAfiPlan().getPlan().getId()==Integer.parseInt(planNuevaTercerizadora)
        		   &&
             		    !seccionalesExceptuadas.contains(String.valueOf(preautorizacion.getAfiliado().getSeccional().getId()))
        		   ) { 			
 		      destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_PROTESIS_TERCERIZADORA_ESPECIAL");
           }else {	
 		      destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_PROTESIS");
           }  
		}else if("GO".equalsIgnoreCase(preautorizacion.getUltimoEstado().getId()) && (preautorizacion.isSupra())) {
 		   destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_SUPRA");
 		}else if(preautorizacion.isDiscapacidad()) {
           if(preautorizacion.getAfiliado()!=null &&   preautorizacion.getAfiliado().getAfiPlan()!=null &&
        		   preautorizacion.getAfiliado().getAfiPlan().getPlan()!=null &&
        		   preautorizacion.getAfiliado().getAfiPlan().getPlan().getId()==Integer.parseInt(planNuevaTercerizadora) 
        		   &&
        		    !seccionalesExceptuadas.contains(String.valueOf(preautorizacion.getAfiliado().getSeccional().getId()))
        		   ) {
 		     destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_DISCAPACIDAD_TERCERIZADORA_ESPECIAL");
           }else {
        	 destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_DISCAPACIDAD");  
           }
 		}else if(preautorizacion.isMedicamento()) {
  		   //destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_MEDICAMENTOS");  
  		 if(preautorizacion.getAfiliado()!=null &&   preautorizacion.getAfiliado().getAfiPlan()!=null &&
       		   preautorizacion.getAfiliado().getAfiPlan().getPlan()!=null &&
       		   preautorizacion.getAfiliado().getAfiPlan().getPlan().getId()==Integer.parseInt(planNuevaTercerizadora)
       		   &&
            		    !seccionalesExceptuadas.contains(String.valueOf(preautorizacion.getAfiliado().getSeccional().getId()))
       		   ) { 			
		      destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_MEDICAMENTOS_TERCERIZADORA_ESPECIAL");
          }else {	
		      destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_MEDICAMENTOS");
          }  
 		}else if(!preautorizacion.isDiscapacidad()){ // OJO es una x default bastante amplia...
 			if(preautorizacion.getAfiliado()!=null &&   preautorizacion.getAfiliado().getAfiPlan()!=null &&
         		   preautorizacion.getAfiliado().getAfiPlan().getPlan()!=null &&
         		   preautorizacion.getAfiliado().getAfiPlan().getPlan().getId()==Integer.parseInt(planNuevaTercerizadora)
         		   &&
              		    !seccionalesExceptuadas.contains(String.valueOf(preautorizacion.getAfiliado().getSeccional().getId()))
         		   ) { 			
  		      destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_TERCERIZADORA_ESPECIAL");
            }else {	
 		      destino=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO");
            }  
 		}   
		Seccional seccional = null;
 		String idSecc = user.getExpandoBridge().getAttribute("id_seccional").toString();
 		
// 		Seccional seccional =SeccionalServiceUtil.buscarSeccionalById(preautorizacion.getAfiliado().getSeccional().getId());
 		String[] destinatarios = destino.split(";");
 		if(destinatarios.length<=1) {
 			emails.add(destino);
 		}else {
 			
 			for (int i = 0; i < destinatarios.length; i++) {
 				emails.add(destinatarios[i]);
			}
 			
 		}
 
 		try{
 			//seccional =SeccionalServiceUtil.buscarSeccionalById(Integer.parseInt(idSecc));
 			//getAfiliado().getSeccional().getId_seccional()*/
 			seccional =SeccionalServiceUtil.buscarSeccionalById(preautorizacion.getSeccionalAltaUsr() );
     		if(idSecc!=null && Integer.parseInt(idSecc) > 0){
     			from = SeccionalServiceUtil.buscarContactosSeccionalEmail(seccional.getId()).get(0).getContacto(); 
     		}

 		}catch(NumberFormatException e){
 			_log.error("Error al parsear id seccional del usuario " + user.getScreenName());
 			_log.error(e);
 		}catch (Exception e) {
 			_log.error(e);
		}
 		
 		if(StringUtils.checkEmpty(from)){
 			from = "autorizaciones@ospim.org.ar";
 		}
// 		String emailUsr = StringUtils.checkNotEmpty(user.getEmailAddress())?user.getEmailAddress():"N/A" ;
// 		subject = "DNI " + 
// 				preautorizacion.getAfiliado().getDocu_numero() +" "+ 
// 				preautorizacion.getAfiliado().getApeNombre() + " " +
// 				"ID: " +preautorizacion.getId();
 		String secc =  seccional!=null && Integer.parseInt(idSecc)>0 ? seccional.getDescripcion() : "OSPIM CENTRAL"; 
 		
 		subject = "ID: " +preautorizacion.getId() +
 				" DNI " + 
 				preautorizacion.getAfiliado().getDocu_numero() +" "+ 
 				preautorizacion.getAfiliado().getApeNombre() + " " +
	 				" SECC.: " + secc + " " +
	 			(preautorizacion.isDiscapacidad()?"-DISCA":"" )	;
 		
 		
 		
 		if(preautorizacion.isAlojamiento()) {
 		  subject += "-ALOJAMIENTO";	
 		  body = "Probable Reserva Departamento desde el " + sdf.format(preautorizacion.getAlojamientoDesde())	+ " hasta el " +
 		         sdf.format(preautorizacion.getAlojamientoHasta()) +".";
 		}else if(preautorizacion.isProtesisOrtesis() || preautorizacion.isCirugia()) {
// 			subject = "Preautorizaci�n ID: " + preautorizacion.getId() + " - Incluye pr�tesis / �rtesis"; 
 			body = "Se carg� un pedido de autorizaci�n que incluye marca de pr�tesis / �rtesis / cirug�a.";
 	 	}else {
 		  body= "Se env�a adjunto orden para autorizar. " +"\n\n\n" ;
// 		  body +=	"Por favor responder a: " + from;
 		  body +=	"Observaciones: "+ (StringUtils.checkNotEmpty(preautorizacion.getObservacionesTercerizadoras())?preautorizacion.getObservacionesTercerizadoras():" - " );
 		}
 				
// 		seccional.getDescripcion()+"\n\n\n" +
// 		"Email seccional: " + from + "\n\n\n" +
// 		"Email usuario env�o: " + emailUsr ;
		
 		
 		
 		List<ArrayList<MimeBodyPart>> lAdjuntos = new ArrayList<ArrayList<MimeBodyPart>>();
 		
 		adjunto = new ArrayList<MimeBodyPart>();
 		
 		
 		try{
     		DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "PREAUTORIZACIONES");
     		Double totalAdjuntos=0D;
     		Double fsize=0D;
     		long folderIdNew=f.getFolderId();
     		//String PDF = "application/xls";
//     		List<DLFileEntryImpl>list = PreAutorizacionServiceUtil.getImagenesPreautorizacion("PREAUT_"+preautorizacion.getId()+"-");
     		if(list.size()>0){
     			for (int i = 0; i < list.size() ; i++) {
     		        DLFileEntry doc = list.get(i);
     		        
//DS - Agregado para segmentar los adjuntos a 25 MB
     		        fsize=Double.valueOf(doc.getSize());
     		        if(totalAdjuntos+fsize>=sizeValidoAdj) { //26000000
                    	ArrayList<MimeBodyPart> adjuntoParcial = new ArrayList<MimeBodyPart>();
                    	for(MimeBodyPart a:adjunto) {
                    		adjuntoParcial.add(a);
                    	}
                    	lAdjuntos.add( adjuntoParcial);
                    	totalAdjuntos=0D;
                    	adjunto = new ArrayList<MimeBodyPart>();
                    }
//DS - Fin Agregado
                    
     		        String PDF = MimeTypesUtil.getContentType(doc.getName());
     		        InputStream is = DLFileEntryLocalServiceUtil.getFileAsStream(10112, doc.getUserId(), folderIdNew, doc.getName());
     		        byte[] by = IOUtils.toByteArray(is);
     		        DataSource dataSource = new ByteArrayDataSource(by, PDF);
					
     		        MimeBodyPart pdfBodyPart = new MimeBodyPart();
					
     		        try {
						pdfBodyPart.setDataHandler(new DataHandler(dataSource));
						pdfBodyPart.setFileName(doc.getName());
						adjunto.add(pdfBodyPart);
//DS - Agregado para segmentar los adjuntos a 25 MB  						
						totalAdjuntos += fsize;
//DS - Fin						
						
					} catch (MessagingException e) {
						_log.error(e);
					}
     			}
//DS - Agregado para segmentar los adjuntos a 25 MB      			
     			lAdjuntos.add( (ArrayList<MimeBodyPart>) adjunto);
//DS - Fin Agregado     			
     		}
 		}catch (Exception e) {
     		_log.error("solo no pudo encontrar los adjuntos... ");
		}
 		
/*
 	emails=new ArrayList<String>();
 	emails.add("dsulfaro@uoma.org.ar");
*/ 		

// 		result=MailUtils.enviarMailGmailconAdjuntoYRespuesta(from, password, emails, subject, body, adjunto);
 		
//DS - Agregado para segmentar los adjuntos a 25 MB 
 		//DS -20230307 agregado para subsanar error envio mail gmail
 		List<String>em=new ArrayList<String>();
 		for(ArrayList<MimeBodyPart> adj:lAdjuntos) {
 			for(String email:emails) {
 			  em.clear();
 			  em.add(email);
 			  result=MailUtils.enviarMailGmailconAdjuntoYRespuesta(from, password, em, subject, body, adj);
 			}  
 		}
 		
 		if(preautorizacion.isART()) {
 	 		  String d=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_POSIBLE_ART");
 	 		  String dirg=TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_EMAIL_DESTINATARIO_POSIBLE_ART_DIRIGENTES");
 	 		  String planesMol = TraeListasServiceUtil.getSystemConfig("PREAUTORIZACION_POSIBLE_ART_PLANES_MOLINEROS");
 	 		  String[] correos = d.split(";");
 	 		  String[] correosDir = dirg.split(";");
 	 		  String planAfiliado=String.valueOf(preautorizacion.getAfiliado().getAfiPlan().getPlan().getId());
 	 		  
 	 		  String cuerpoMail = body + " Observaciones: "+ (StringUtils.checkNotEmpty(preautorizacion.getObservaciones())?preautorizacion.getObservaciones():"" );
 	 		  for(ArrayList<MimeBodyPart> adj:lAdjuntos) {
 	 			for (int i = 0; i < correos.length; i++) {
 	 				em.clear();
 	  			    em.add(correos[i]);
 	  			    result=MailUtils.enviarMailGmailconAdjuntoYRespuesta(from, password, em,"POSIBLE A.R.T. - " + subject  , cuerpoMail, adj);
 				}
 	 		  }
 	 		  
 	 		  if(planesMol.contains(planAfiliado)) {
 	 			for(ArrayList<MimeBodyPart> adj:lAdjuntos) {
 	 	 			for (int i = 0; i < correosDir.length; i++) {
 	 	 				em.clear();
 	 	  			    em.add(correosDir[i]);
 	 	  			    result=MailUtils.enviarMailGmailconAdjuntoYRespuesta(from, password, em,"POSIBLE A.R.T. - " + subject  , cuerpoMail, adj);
 	 				}
 	 	 		}
 	 			  
 	 		  }
 	 		  
 	 	}
 		
 		//DS -20230307 Comentado para subsanar error en envio mail gmail 
// 		result=MailUtils.enviarMailGmailconAdjuntoYRespuesta(from, password, emails, subject, body, adj);
//DS - Fin Agregado 
 		
 		return result;
	}

	private void addMedicamento(RenderRequest renderRequest,HttpSession session) throws SystemException{
		String rid = rid();
		_log.info("[" + rid + "][ADD_MEDICAMENTO][START]"
				+ " codigo=" + s(ParamUtil.getString(renderRequest,"codigo"))
				+ ", troquel=" + s(ParamUtil.getString(renderRequest,"troquel"))
				+ ", descripcion=" + s(ParamUtil.getString(renderRequest,"descripcion"))
				+ ", cantidad=" + s(ParamUtil.getString(renderRequest,"cantidad"))
				+ ", importe=" + s(ParamUtil.getString(renderRequest,"importe")));
		Integer codigo=ParamUtil.getInteger(renderRequest,"codigo",0);
		String troquel=ParamUtil.getString(renderRequest,"troquel",null);
		String descripcion=ParamUtil.getString(renderRequest,"descripcion",null);
		Integer idDetalle = ParamUtil.getInteger(renderRequest, "iddetalle",0);
		Integer idDetalleAux = ParamUtil.getInteger(renderRequest, "iddetalleaux",0);
		Integer idPreautorizacionMedic = ParamUtil.getInteger(renderRequest, "idpreautorizacionmedic",0);
		Double cantidad=ParamUtil.getDouble(renderRequest, "cantidad",0);
		Double importe=ParamUtil.getDouble(renderRequest, "importe",0);
		
		Medicamento med = new Medicamento();
		med.setId_medicamento(codigo);
		med.setTroquel(Integer.valueOf(troquel));
		med.setNombre(descripcion);
		
		PreAutorizacionMedicamento medicamento = new PreAutorizacionMedicamento();
		medicamento.setMedicamento(med);
		medicamento.setCantidad(cantidad);
		medicamento.setImporte(importe);
		medicamento.setId(idDetalle);
		if(idDetalle==0 && idDetalleAux==0){
			Integer rnd = 0;
			Random r = new Random();
			rnd=r.nextInt((1000 - 1) + 1) + 1;
			medicamento.setIdAux(rnd);	
		}else if(idDetalleAux!=0){
			medicamento.setIdAux(idDetalleAux);
		}
		
		
		PreAutorizacion pa= (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
		
		
		boolean encontro=false;
		for(int xi=0;xi<pa.getMedicamentosPresentados().size();xi++){
      	  if(pa.getMedicamentosPresentados().get(xi).getIdAux().equals(medicamento.getIdAux())){
      		pa.getMedicamentosPresentados().set(xi,medicamento);
      		encontro=true;
      		break;
      	  }
        }
		
		if(!encontro){
		  pa.getMedicamentosPresentados().add(medicamento);
		}
		
		session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, pa);
		_log.info("[" + rid + "][ADD_MEDICAMENTO][END]"
				+ " totalMedicamentos=" + (pa.getMedicamentosPresentados() != null ? pa.getMedicamentosPresentados().size() : -1)
				+ ", encontro=" + encontro
				+ ", idAux=" + s(medicamento.getIdAux()));
	}
	
	private void deleteMedicamento(RenderRequest renderRequest,HttpSession session) throws SystemException{
		Integer idDetalle = ParamUtil.getInteger(renderRequest, "detalleid",0);
		
		List<PreAutorizacionMedicamento> pap= new ArrayList<PreAutorizacionMedicamento>();
		
		PreAutorizacion pa= (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
		for(PreAutorizacionMedicamento p:pa.getMedicamentosPresentados()){
			if(!idDetalle.equals(p.getIdAux())){
				pap.add(p);
			}else if(idDetalle.equals(p.getIdAux()) && (p.getId()!=null && p.getId()!=0)){
			    p.setFechaBaja(new Date());
			    pap.add(p);
			}
		}
		pa.setMedicamentosPresentados(pap);
		session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION, pa);
		
	}
	
	
}
package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.StringUtils;

public class EditarSeguimientoSurAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		PortletSession portletSession = renderRequest.getPortletSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		SeguimientoSur seguimiento=null;
		long idSeguimiento = 0;
		String msg = "";
		if (!StringUtils.checkEmpty(cmd)) {
			idSeguimiento = ParamUtil.getInteger(renderRequest,"id_seguimiento", 0);
			if(cmd.equals(Constants.WRITE) ){ 
				
				seguimiento = new SeguimientoSur();
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
				portletSession.removeAttribute("clase_expediente");			
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.editar_seguimientosur"));
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	session.setAttribute("esPopUp","S");
            	
            	seguimiento = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int)idSeguimiento);

            	session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
            	
            	portletSession.removeAttribute("clase_expediente");
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id seguimiento sur: " + idSeguimiento
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.editar_seguimientosur"));
			}

            if (cmd.equals("eliminaSeguimiento")) { // borrado logico
            	session.setAttribute("esPopUp","N");
            	String motivo = ParamUtil.getString(renderRequest, "motivobajasur", null);
            	
            	SeguimientoSurServiceUtil.eliminaSeguimiento((int)idSeguimiento, user.getScreenName(),motivo);
            	
            	List<SeguimientoSur>ln= (List<SeguimientoSur>) session.getAttribute("SeguimientosSUR");
           	    for(SeguimientoSur n:ln){
            		if(n.getId()==idSeguimiento){
            		   n.setBaja_fecha(new Date());	
            		   n.setMotivoBaja(motivo);
            		}
            	}
           	
            	session.setAttribute("SeguimientosSUR",ln);

            	
				msg = LanguageUtil.get(defaultLocale, "delete-seguimiento-sur");
				msg = msg + " " +idSeguimiento;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id idSeg: " + idSeguimiento
						);
				return mapping.findForward("portlet.autorizaciones.buscar_seguimientosur");
			}
  
            
            if(cmd.equals(Constants.DELETE) ){ // Posibilita carga motivo de baja
            	session.setAttribute("esPopUp","N");
            	
            	seguimiento = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int)idSeguimiento);

            	session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
            	
            	
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id seguimiento sur: " + idSeguimiento
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.eliminar_seguimientosur"));
			}

            
            if (cmd.equals(Constants.RESTORE)) { //Recupera Seguimiento Sur Eliminado
            	session.setAttribute("esPopUp","N");
            	SeguimientoSurServiceUtil.recuperaSeguimiento((int)idSeguimiento, user.getScreenName());
            	
            	List<SeguimientoSur>ln= (List<SeguimientoSur>) session.getAttribute("SeguimientosSUR");
           	    for(SeguimientoSur n:ln){
            		if(n.getId()==idSeguimiento){
            		   n.setBaja_fecha(null);	
            		}
            	}
            	session.setAttribute("SeguimientosSUR",ln);
            	
				msg = LanguageUtil.get(defaultLocale, "restore-seguimiento-sur");
				msg = msg +" "+ idSeguimiento;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id idSeg: " + idSeguimiento
						);
				
				return mapping.findForward("portlet.autorizaciones.buscar_seguimientosur");
			}
            
            
            if(cmd.equals(Constants.LOCK) ){ // Pide fecha de cierre de Expediente
            	session.setAttribute("esPopUp","N");
            	
            	seguimiento = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int)idSeguimiento);

            	session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
            	
            	
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id seguimiento sur: " + idSeguimiento
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.cerrar_seguimientosur"));
			}
            
            if (cmd.equals(Constants.EXPIRE)) { // Graba Cierre de Expediente
            	session.setAttribute("esPopUp","N");
            	
            	String fechaCierreDia = ParamUtil.getString(renderRequest,"fechaCierreSurDia");
        		String fechaCierreMes = ParamUtil.getString(renderRequest,"fechaCierreSurMes");
        		String fechaCierreAnio = ParamUtil.getString(renderRequest,"fechaCierreSurAnio");
        		String motivoCierre = ParamUtil.getString(renderRequest,"motivoCierreSur");
        		
        		Date fechaCierre = null;
        		try {
        			fechaCierre = formatoDeFechas.parse(fechaCierreDia + "/"
        					+ (Integer.parseInt(fechaCierreMes) + 1) + "/"
        					+ fechaCierreAnio);
        		} catch (Exception e) {
        			fechaCierre = null;
        		}
            	
            	SeguimientoSurServiceUtil.cierraSeguimiento((int)idSeguimiento, fechaCierre,motivoCierre);
            	
            	List<SeguimientoSur>ln= (List<SeguimientoSur>) session.getAttribute("SeguimientosSUR");
           	    for(SeguimientoSur n:ln){
            		if(n.getId()==idSeguimiento){
            		   n.setCierre_fecha(fechaCierre);	
            		}
            	}
            	session.setAttribute("SeguimientosSUR",ln);
            	
				msg = LanguageUtil.get(defaultLocale, "cierre-seguimiento-sur");
				msg = msg + " " +idSeguimiento;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id idSeg: " + idSeguimiento
						);
				return mapping.findForward("portlet.autorizaciones.buscar_seguimientosur");
			}
            
            if(cmd.equals("imagenes") ){
                session.setAttribute("esPopUp","N");
                seguimiento = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int)idSeguimiento);
            	session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id seguimiento sur: " + idSeguimiento
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.imagenes_seguimientosur"));
            }
            
            
            if(cmd.equals("comprobante_edit") ){
            	session.setAttribute("esPopUp","N");
            	
            	seguimiento = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int)idSeguimiento);

            	session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
            	
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id seguimiento sur: " + idSeguimiento
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.editar_comprobante_tercerizadora_seguimientosur"));
				
			}
            
            if(cmd.equals("comprobante_save") ){
				session.setAttribute("esPopUp","N");
				seguimiento = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int)idSeguimiento);
				actualizaComprobanteSeguimientoSur(seguimiento,renderRequest);
				updateComprobanteSeguimientoSUR(seguimiento, user.getScreenName());
						
						msg = LanguageUtil.get(defaultLocale, "update-seguimiento");
						msg = msg + " "+ idSeguimiento;
						SessionMessages.add(renderRequest, "updateCabOk");
						renderRequest.setAttribute("msgCabOk", msg);
						_log.debug("Usuario: " + user.getScreenName() 
								+ " cmd: " + cmd 
								+ " id seguimiento sur: " + idSeguimiento
								);
					
				return mapping.findForward("portlet.autorizaciones.buscar_seguimientosur");
			}
            
            if(cmd.equals("opcion_pagos") ){
                return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.opcion_pagos_seguimientosur"));
            }
            
  
            //Recupera Datos cargados en la jsp
			seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			actualizaSeguimientoSur(seguimiento,renderRequest);
			
			_log.debug("cmd antes del update " + cmd);
			if(cmd.equals(Constants.UPDATE) ){
				
//validar prestaciones
//				if(seguimiento.getTratamientos().size()>0 || seguimiento.getLiquidaciones().size()>0){
				
				_log.debug("idSeguimiento: " + idSeguimiento);
				
					if(idSeguimiento==0){ //Nuevo
						if("DI".equalsIgnoreCase(seguimiento.getClaseExpediente())
								//||  "ME".equalsIgnoreCase(seguimiento.getClaseExpediente())
						  ){
						
							if( !SeguimientoSurServiceUtil.existeSeguimientoSurPorBimestre(seguimiento.getCuilTitular(),
									seguimiento.getIntegrante().toString(),seguimiento.getId_bimestre().toString()) ){
								
								if((seguimiento.getNro_expediente()==null ||
										"".equalsIgnoreCase(seguimiento.getNro_expediente())) ||
										!SeguimientoSurServiceUtil.existeSeguimientoSurNroExpediente(seguimiento.getNro_expediente() ,
										seguimiento.getId())){
									
	//Control modulos para DI			
									if(("DI".equalsIgnoreCase(seguimiento.getClaseExpediente()) && validaModulos(seguimiento)) ||
											   "ME".equalsIgnoreCase(seguimiento.getClaseExpediente())	){
									   idSeguimiento= insertSeguimientoSUR(seguimiento,user.getScreenName());	
									   seguimiento.setId((int) idSeguimiento);
									   
									   
									   SeguimientoSur ss = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int) idSeguimiento);
									   String xx="";
									   if(ss!=null){
										   xx= ss.getClaseExpediente()+" "+ss.getId_tipo_expediente_nro().toString();
									   }
										
										  msg = LanguageUtil.get(defaultLocale, "insert-seguimiento");
										  msg = msg +" " +xx;
										  SessionMessages.add(renderRequest, "insertCabOk");
										  renderRequest.setAttribute("msgCabOk", msg);
										  _log.debug("Usuario: " + user.getScreenName() 
												+ " cmd: " + cmd 
												+ " id idSeg: " + idSeguimiento
												);
									}else{
										msg = LanguageUtil.get(defaultLocale, "Debe cargar al menos 2 prestaciones del módulo");
									    SessionErrors.add(renderRequest, "avisoModuloIncompleto");
										renderRequest.setAttribute("msgModError",msg );
										_log.debug("Usuario: " + user.getScreenName() 
												+ " cmd: " + cmd 
												+ " id idSeg: " + idSeguimiento
												);  
									}
									  
									  
								}else{
									msg = LanguageUtil.get(defaultLocale, "Ya existe el Nro de Expediente cargado para otra Solicitud");
								    SessionErrors.add(renderRequest, "avisoSeguimientoDuplicado");
									renderRequest.setAttribute("msgInsertError",msg );
									_log.debug("Usuario: " + user.getScreenName() 
											+ " cmd: " + cmd 
											+ " id idSeg: " + idSeguimiento
											);   
								}
								  
							}else{
								msg = LanguageUtil.get(defaultLocale, "Ya existe un CUIL/Integrante/Periodo cargado");
							    SessionErrors.add(renderRequest, "avisoSeguimientoDuplicado");
								renderRequest.setAttribute("msgInsertError",msg );
								_log.debug("Usuario: " + user.getScreenName() 
										+ " cmd: " + cmd 
										+ " id idSeg: " + idSeguimiento
										);   
							}
						}else{
							if((seguimiento.getNro_expediente()==null ||
									"".equalsIgnoreCase(seguimiento.getNro_expediente())) ||
									!SeguimientoSurServiceUtil.existeSeguimientoSurNroExpediente(seguimiento.getNro_expediente() ,
									seguimiento.getId())){
							   idSeguimiento= insertSeguimientoSUR(seguimiento,user.getScreenName());	
							   seguimiento.setId((int) idSeguimiento);
								
							   msg = LanguageUtil.get(defaultLocale, "insert-seguimiento");
							   msg = msg +" " +idSeguimiento;
							   SessionMessages.add(renderRequest, "insertCabOk");
							   renderRequest.setAttribute("msgCabOk", msg);
							   _log.debug("Usuario: " + user.getScreenName() 
										+ " cmd: " + cmd 
										+ " id idSeg: " + idSeguimiento
										);
							}else{
								msg = LanguageUtil.get(defaultLocale, "Ya existe el Nro de Expediente cargado para otra Solicitud");
							    SessionErrors.add(renderRequest, "avisoSeguimientoDuplicado");
								renderRequest.setAttribute("msgInsertError",msg );
								_log.debug("Usuario: " + user.getScreenName() 
										+ " cmd: " + cmd 
										+ " id idSeg: " + idSeguimiento
										);   
							}
						}
						
					}else if(idSeguimiento!=0){
						if((seguimiento.getNro_expediente()==null ||
								"".equalsIgnoreCase(seguimiento.getNro_expediente())) ||
								!SeguimientoSurServiceUtil.existeSeguimientoSurNroExpediente(seguimiento.getNro_expediente() ,
								seguimiento.getId())){
							
							updateSeguimientoSUR(seguimiento, user.getScreenName());
							
							String leyenda="";
							if(seguimiento.getId_tipo_expediente_nro()==null){
							   SeguimientoSur ss = SeguimientoSurServiceUtil.buscarSeguimientoSurPorId((int) idSeguimiento);
							   
							   if(ss!=null){
								   leyenda= ss.getClaseExpediente()+" "+ss.getId_tipo_expediente_nro().toString();
							   }
							}else{
								leyenda=seguimiento.getClaseExpediente()+" "+seguimiento.getId_tipo_expediente_nro();
							}
							
							msg = LanguageUtil.get(defaultLocale, "update-seguimiento");
							msg = msg + " "+ leyenda;
							SessionMessages.add(renderRequest, "updateCabOk");
							renderRequest.setAttribute("msgCabOk", msg);
							_log.debug("Usuario: " + user.getScreenName() 
									+ " cmd: " + cmd 
									+ " id idSeg: " + idSeguimiento
									);
						}else{
							msg = LanguageUtil.get(defaultLocale, "Ya existe el Nro de Expediente cargado para otra Solicitud");
						    SessionErrors.add(renderRequest, "avisoSeguimientoDuplicado");
							renderRequest.setAttribute("msgInsertError",msg );
							_log.debug("Usuario: " + user.getScreenName() 
									+ " cmd: " + cmd 
									+ " id idSeg: " + idSeguimiento
									);  
						}
					}
/*					
				}else{
					msg = LanguageUtil.get(defaultLocale, "Debe cargar alguna prestación para el Seguimiento");
				    SessionErrors.add(renderRequest, "avisoSeguimientoDuplicado");
					renderRequest.setAttribute("msgInsertError",msg );
					_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id idSeg: " + idSeguimiento
							);  
				}
*/				
			}
		}
		
		session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
		return mapping.findForward("portlet.autorizaciones.editar_seguimientosur");
		
	}
	
	
	private long insertSeguimientoSUR(SeguimientoSur seguimiento, String user) throws Exception{
		long id = 0;
		
		id = SeguimientoSurServiceUtil.insertaSeguimientoSur(seguimiento, user);
		return id;
	}
	
	private long updateSeguimientoSUR(SeguimientoSur seguimiento, String user) throws Exception{
		long id = 0;
		
		id = SeguimientoSurServiceUtil.updateSeguimientoSur(seguimiento, user);
		return id;
	}
	
	private void actualizaSeguimientoSur(SeguimientoSur seguimiento,RenderRequest renderRequest) throws SystemException{
		Integer anio = ParamUtil.getInteger(renderRequest,"ejercicio",0);
		Integer bimestre = ParamUtil.getInteger(renderRequest, "bimestre",0);
		Integer tipoExpediente=ParamUtil.getInteger(renderRequest,"tipoExpediente");
		Integer tipoExpedienteTercerizadora=ParamUtil.getInteger(renderRequest,"tipoExpedienteTercerizadora");
		Integer autorizaOmint=ParamUtil.getInteger(renderRequest,"autorizaOmint",0);
		String nroSolicitudSur=ParamUtil.getString(renderRequest,"nroSolicitudSUR",null);
		String codigoSeguimiento=ParamUtil.getString(renderRequest,"codigoSeguimiento",null);
		String nroExpedienteSUR=ParamUtil.getString(renderRequest,"nroExpedienteSUR",null);
		String cuilTitular=ParamUtil.getString(renderRequest,"cuil",null);
		Integer integrante=ParamUtil.getInteger(renderRequest, "inte",0);
		int idTipoNomenclador = ParamUtil.getInteger(renderRequest,"tipoNomenclador",0);
		int idNomenclador=0;
		Double importePresentado=ParamUtil.getDouble(renderRequest, "importeSeguimientoSUR");
		Double topeRecupero=ParamUtil.getDouble(renderRequest, "topeRecuperoSeguimientoSUR");
		Double importeReconocido=ParamUtil.getDouble(renderRequest, "importeReconocidoSeguimientoSUR");
		
		Double importeOspim =ParamUtil.getDouble(renderRequest, "montoOspim");
		Double importeOmint=ParamUtil.getDouble(renderRequest, "montoOmint");
		Double importePrevencion=ParamUtil.getDouble(renderRequest, "montoprevencion");
		Double importeEnSalud=ParamUtil.getDouble(renderRequest, "montoEnSalud");
		Double importeCemic=ParamUtil.getDouble(renderRequest, "montoCemic");

		
		String fechaPresentacionDia = ParamUtil.getString(renderRequest,"fechaPresentacionSurDia");
		String fechaPresentacionMes = ParamUtil.getString(renderRequest,"fechaPresentacionSurMes");
		String fechaPresentacionAnio = ParamUtil.getString(renderRequest,"fechaPresentacionSurAnio");
		
		String fechaMesaEntradaDia = ParamUtil.getString(renderRequest,"fechaMesaEntradaSurDia");
		String fechaMesaEntradaMes = ParamUtil.getString(renderRequest,"fechaMesaEntradaSurMes");
		String fechaMesaEntradaAnio = ParamUtil.getString(renderRequest,"fechaMesaEntradaSurAnio");
		
		
		String claseExpediente = ParamUtil.getString(renderRequest, "claseExpediente",null);
		Integer norma = ParamUtil.getInteger(renderRequest,"normaSeguimiento",0);
		Integer patologia = ParamUtil.getInteger(renderRequest,"patologiaSeguimiento",0);
		String patologiaDescripcion=ParamUtil.getString(renderRequest,"patologia",null);
//		Integer patologia = ParamUtil.getInteger(renderRequest,"id_patologia",0);
		String periodicidadHemofilia = ParamUtil.getString(renderRequest, "periodicidadHemofiliaSUR",null);
		
		
		String fechaIngresoSurDia = ParamUtil.getString(renderRequest,"fechaIngresoSurDia");
		String fechaIngresoSurMes = ParamUtil.getString(renderRequest,"fechaIngresoSurMes");
		String fechaIngresoSurAnio = ParamUtil.getString(renderRequest,"fechaIngresoSurAnio");
		
		
		if("DI".equalsIgnoreCase(claseExpediente)){
			patologia = Integer.parseInt(TraeListasServiceUtil.getSystemConfig("SEGUIMIENTO_SUR_PATOLOGIA_DISCAPACIDAD"));
		}
		
		
		boolean tutelaje = ParamUtil.getBoolean(renderRequest,"tutelajeSeguimiento",false);
		
		Date fechaPresentacion = null;
		try {
			fechaPresentacion = formatoDeFechas.parse(fechaPresentacionDia + "/"
					+ (Integer.parseInt(fechaPresentacionMes) + 1) + "/"
					+ fechaPresentacionAnio);
		} catch (Exception e) {
			fechaPresentacion = null;
		}
		
		Date fechaMesaEntrada = null;
		try {
			fechaMesaEntrada = formatoDeFechas.parse(fechaMesaEntradaDia + "/"
					+ (Integer.parseInt(fechaMesaEntradaMes) + 1) + "/"
					+ fechaMesaEntradaAnio);
		} catch (Exception e) {
			fechaMesaEntrada = null;
		}
		
		
		Date fechaIngresoSur = null;
		try {
			fechaIngresoSur = formatoDeFechas.parse(fechaIngresoSurDia + "/"
					+ (Integer.parseInt(fechaIngresoSurMes) + 1) + "/"
					+ fechaIngresoSurAnio);
		} catch (Exception e) {
			fechaIngresoSur = null;
		}
		
		
		String nroCorrespondenciaSur=ParamUtil.getString(renderRequest,"nroCorrespondenciaSUR",null);
		
		if("DI".equalsIgnoreCase(claseExpediente) ||
				"DR".equalsIgnoreCase(claseExpediente) ){
			if(seguimiento.getId_codigo_presentado()!=null && seguimiento.getId_codigo_presentado()!=0){
			    Nomenclador nomenclador = NomencladorServiceUtil.buscarNomencladorPorId(seguimiento.getId_codigo_presentado());
			    if(nomenclador!=null){
			      seguimiento.setId_codigo_presentado(nomenclador.getId_prestacion());
			      seguimiento.setCodigoPresentado(nomenclador.getCodigo());
			      seguimiento.setDescripcionPresentado(nomenclador.getDescripcion());
			      seguimiento.setTipoNomencladorId(nomenclador.getId_tipo_nomenclador());
			    }
			}
			if(idNomenclador==0){
				   List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(idTipoNomenclador,"",0, codigoSeguimiento,false,"");
				   for(Nomenclador nom:nomencladores){
					   if(nom.getBaja_fecha()==null){
						   seguimiento.setId_codigo_presentado(nom.getId_prestacion());
						   seguimiento.setCodigoPresentado(nom.getCodigo());
						   seguimiento.setDescripcionPresentado(nom.getDescripcion());
						   seguimiento.setTipoNomencladorId(idTipoNomenclador);
					   }
				   }
			}  
		}
		 
		seguimiento.setAnio(anio);
		seguimiento.setId_bimestre(bimestre);
		seguimiento.setId_tipo_expediente(tipoExpediente);
		seguimiento.setId_tipo_expediente_tercerizadora(tipoExpedienteTercerizadora);
		seguimiento.setId_autoriza_omint(autorizaOmint);
		seguimiento.setNro_solicitud_sur(nroSolicitudSur);
		seguimiento.setNro_expediente(nroExpedienteSUR);
		seguimiento.setCuilTitular(cuilTitular);
		seguimiento.setIntegrante(integrante);
		seguimiento.setPresentacion_fecha(fechaPresentacion);
		seguimiento.setImportePresentado(importePresentado);
		seguimiento.setClaseExpediente(claseExpediente);
		seguimiento.setNorma(norma);
		seguimiento.setPatologia(patologia);
		seguimiento.setPatologiaDescripcion(patologiaDescripcion);
		seguimiento.setTutelaje(tutelaje);
		seguimiento.setMesaEntrada_fecha(fechaMesaEntrada);
		seguimiento.setNro_correspondencia_sur(nroCorrespondenciaSur);
		seguimiento.setTopeRecupero(topeRecupero);
		seguimiento.setImporteReconocido(importeReconocido);
		seguimiento.setPeriodicidadHemofilia(periodicidadHemofilia);
		seguimiento.setFecha_ingreso_area_sur(fechaIngresoSur);
		
		seguimiento.setImporteOmint(importeOmint );
		seguimiento.setImporteOspim(importeOspim);
		seguimiento.setImportePrevencion(importePrevencion);
		seguimiento.setImporteEnSalud(importeEnSalud);
		seguimiento.setImporteCemic(importeCemic);
		
		String fechaTutelajeDia = ParamUtil.getString(renderRequest,"fechaTutelajeSurDia");
		String fechaTutelajeMes = ParamUtil.getString(renderRequest,"fechaTutelajeSurMes");
		String fechaTutelajeAnio = ParamUtil.getString(renderRequest,"fechaTutelajeSurAnio");
		String observacionesTutelaje=ParamUtil.getString(renderRequest,"observacionesTutelaje",null);
		Date fechaTutelaje = null;
		try {
			fechaTutelaje = formatoDeFechas.parse(fechaTutelajeDia + "/"
					+ (Integer.parseInt(fechaTutelajeMes) + 1) + "/"
					+ fechaTutelajeAnio);
		} catch (Exception e) {
			fechaTutelaje = null;
		}
		
		if(tutelaje){
			seguimiento.setTutelaje_fecha(fechaTutelaje);
			seguimiento.setTutelaje_observaciones(observacionesTutelaje);
		}else{
			seguimiento.setTutelaje_observaciones("");
		}
		
        Double valorUnitario=ParamUtil.getDouble(renderRequest, "valorUnitarioMedicamentoSur",0);
		seguimiento.setValorUnitario(valorUnitario);
		
		String fechaDiagnosticoDia = ParamUtil.getString(renderRequest,"fechaDiagnosticoSurDia");
		String fechaDiagnosticoMes = ParamUtil.getString(renderRequest,"fechaDiagnosticoSurMes");
		String fechaDiagnosticoAnio = ParamUtil.getString(renderRequest,"fechaDiagnosticoSurAnio");
		Date fechaDiagnostico = null;
		try {
			fechaDiagnostico = formatoDeFechas.parse(fechaDiagnosticoDia + "/"
					+ (Integer.parseInt(fechaDiagnosticoMes) + 1) + "/"
					+ fechaDiagnosticoAnio);
		} catch (Exception e) {
			fechaDiagnostico = null;
		}
		
		String fechaFinTratamientoDia = ParamUtil.getString(renderRequest,"fechaFinTratamientoSurDia");
		String fechaFinTratamientoMes = ParamUtil.getString(renderRequest,"fechaFinTratamientoSurMes");
		String fechaFinTratamientoAnio = ParamUtil.getString(renderRequest,"fechaFinTratamientoSurAnio");
		Date fechaFinTratamiento = null;
		try {
			fechaFinTratamiento = formatoDeFechas.parse(fechaFinTratamientoDia + "/"
					+ (Integer.parseInt(fechaFinTratamientoMes) + 1) + "/"
					+ fechaFinTratamientoAnio);
		} catch (Exception e) {
			fechaFinTratamiento = null;
		}
		
		String unidadMedidaDiagnostico = ParamUtil.getString(renderRequest,"unidadMedidaSUR");
		Integer cantidadMesesDiagnostico=ParamUtil.getInteger(renderRequest, "cantidadMesesSUR",0);
		
		if(!"DR".equalsIgnoreCase(claseExpediente) ){
		   seguimiento.setUnidadMedidaDiagnostico(null);
		   seguimiento.setCantidadMesesTratamiento(null);
		   seguimiento.setFinTratamiento_fecha(null);
		   seguimiento.setDiagnostico_fecha(null);
		}else{
		   seguimiento.setUnidadMedidaDiagnostico(unidadMedidaDiagnostico);
		   seguimiento.setCantidadMesesTratamiento(cantidadMesesDiagnostico);	
		   seguimiento.setDiagnostico_fecha(fechaDiagnostico);
		   seguimiento.setFinTratamiento_fecha(fechaFinTratamiento);
		}
		
		String observaciones=ParamUtil.getString(renderRequest,"observacionesSeguimientoSUR",null);
		seguimiento.setObservaciones(observaciones);
		
		Integer cantidadAfiliados=ParamUtil.getInteger(renderRequest, "cantidadAfiliadosSeguimientoSUR",0);
		seguimiento.setCantidadAfiliados(cantidadAfiliados);
		String codigoHIV=ParamUtil.getString(renderRequest,"codigo_hiv",null);
		seguimiento.setCodigoHIV(codigoHIV);
		Double importeProporcional=ParamUtil.getDouble(renderRequest, "importeProporcionalAdelantadoSeguimientoSUR");
		seguimiento.setProporcionalAdelantado(importeProporcional);
	}
	
	private long updateComprobanteSeguimientoSUR(SeguimientoSur seguimiento, String user) throws Exception{
		long id = 0;
		
		id = SeguimientoSurServiceUtil.updateComprobanteSeguimientoSur(seguimiento, user);
		return id;
	}
	
	private void actualizaComprobanteSeguimientoSur(SeguimientoSur seguimiento,RenderRequest renderRequest) throws SystemException{
		String comprobanteTipo=ParamUtil.getString(renderRequest,"comprobante_tiposur",null);
		String comprobanteLetra=ParamUtil.getString(renderRequest,"comprobante_letrasur",null);
		String comprobanteNro=ParamUtil.getString(renderRequest,"comprobante_nrosur",null);
		Integer comprobanteSucursal = ParamUtil.getInteger(renderRequest, "comprobante_sucursalsur",0);
		Double comprobanteImporte=ParamUtil.getDouble(renderRequest, "comprobante_importesur",0);
		
		
		String fechaPresentacionDia = ParamUtil.getString(renderRequest,"fechaComprobantesurdia");
		String fechaPresentacionMes = ParamUtil.getString(renderRequest,"fechaComprobantesurmes");
		String fechaPresentacionAnio = ParamUtil.getString(renderRequest,"fechaComprobantesuranio");
		
		Date comprobanteFecha = null;
		try {
			comprobanteFecha = formatoDeFechas.parse(fechaPresentacionDia + "/"
					+ (Integer.parseInt(fechaPresentacionMes) + 1) + "/"
					+ fechaPresentacionAnio);
		} catch (Exception e) {
			comprobanteFecha = null;
		}
		seguimiento.setComprobanteFecha(comprobanteFecha);
		seguimiento.setComprobanteImporte(comprobanteImporte);
		seguimiento.setComprobanteLetra(comprobanteLetra);
		seguimiento.setComprobanteNumero(comprobanteNro);
		seguimiento.setComprobanteSucursal(comprobanteSucursal);
		seguimiento.setComprobanteTipo(comprobanteTipo);
				
	}
	
	private boolean validaModulos(SeguimientoSur seguimiento){
		Boolean ret=true;
		
		if("DI".equalsIgnoreCase(seguimiento.getClaseExpediente())){
		   if(seguimiento.getCodigoPresentado()!=null){
			 try {
				Nomenclador nomenclador = NomencladorServiceUtil.buscarNomencladorPorId(seguimiento.getId_codigo_presentado());
				if(nomenclador.isModulo()){
					String validos= TraeListasServiceUtil.getSystemConfig("DISCAPACIDAD_CONTROL_MODULOS_SUR");
					String[]validosVec = validos.split(";");
					
					int q=0;
					Map<String,Integer> map = new HashMap<String,Integer>();
					for(TratamientoDiscapacidadSeguimiento t:seguimiento.getTratamientos()){
						for(int i=0;i<validosVec.length;i++){
						   if( t.getPrestacion().getCodigo().equalsIgnoreCase(validosVec[i])){
							map.put(validosVec[i], 1);
						   }
						}    
					}
					
					if(map.size()>0){
					  Iterator it = map.entrySet().iterator();
					  while (it.hasNext()) {
						Map.Entry e = (Map.Entry)it.next();
						q+= (Integer)e.getValue();
					  }
					}
					if(q<2) ret=false;
					
				}
			 } catch (SystemException e) {
				 ret=false;
			 }
		   }
		}
		return ret;
	}
	
}

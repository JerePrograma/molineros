package ar.com.ospim.prestadores.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.PrestadorPlan;
import ar.com.ospim.liquidaciones.beans.ProfesionPrestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.prestadores.WebKeysPrestadores;
import ar.com.ospim.util.PermissionUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarPrestadoresEntryAction.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Martin Moreyra
 * @modif SVA
 */
public class EditarPrestadoresEntryAction extends PrestadoresBaseAction {

	private Logger _log = Logger.getLogger(this.getClass());
	
	private static final String SESSION_SOLICITAR_COTIZACION =
	        "SOLICITAR_COTIZACION_PRESTADOR";

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		Boolean esDatosTab = ParamUtil.getBoolean(actionRequest, "esDatosTab");
		
		User user = PortalUtil.getUser(actionRequest);

		boolean puedeSolicitarCotizacion =
		    PermissionUtil.userContainsRole(
		        user,
		        WebKeysPrestadores.ROL_SOLICITAR_COTIZACION_PRESTADOR
		    );

		if (cmd.equals(Constants.MOVE) && esDatosTab){  // cambio a solapa Lugar Atencion.
			
			Prestador prestadorAnterior = (Prestador) session.getAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION);

			Prestador prestador = null;
			// Datos del Prestador (solapa datos) 
			prestador = getPrestadorFromRequest(PortalUtil.getHttpServletRequest(actionRequest), prestador);
			
			// Mantener datos de auditoría al cambiar de solapa
		    if (prestadorAnterior != null) {

		        prestador.setAlta_usr(prestadorAnterior.getAlta_usr());
		        prestador.setAlta_fecha(prestadorAnterior.getAlta_fecha());
		        prestador.setModi_usr(prestadorAnterior.getModi_usr());
		        prestador.setModi_fecha(prestadorAnterior.getModi_fecha());
		        prestador.setBaja_usr(prestadorAnterior.getBaja_usr());
		        prestador.setBaja_fecha(prestadorAnterior.getBaja_fecha());
		    }

			// NUEVO
		    if (puedeSolicitarCotizacion) {

		        boolean solicitarCotizacion = ParamUtil.getBoolean(actionRequest, "solicitar_cotizacion");
		        session.setAttribute(SESSION_SOLICITAR_COTIZACION, solicitarCotizacion);

		        List<String> rubros = new ArrayList<String>();

		        if (solicitarCotizacion) {

			if (ParamUtil.getBoolean(actionRequest, "prov_insumos")) {
			    rubros.add("INSUMOS");
			}

			if (ParamUtil.getBoolean(actionRequest, "prov_leches")) {
			    rubros.add("ALIMENTACION");
			}

			if (ParamUtil.getBoolean(actionRequest, "prov_paniales")) {
			    rubros.add("PAÑALES");
			}

			if (ParamUtil.getBoolean(actionRequest, "prov_medicamentos")) {
			    rubros.add("MEDICAMENTOS");
			}

			if (ParamUtil.getBoolean(actionRequest, "protesis_cardiologia")) {
			    rubros.add("PROTESIS_CARDIOLOGIA");
			}

			if (ParamUtil.getBoolean(actionRequest, "protesis_general")) {
			    rubros.add("PROTESIS_GENERAL");
			}

			if (ParamUtil.getBoolean(actionRequest, "protesis_traumatologia")) {
			    rubros.add("PROTESIS_TRAUMATOLOGIA");
			}
		        }
		        session.setAttribute("RUBROS_PRESTADOR", rubros);
			 }
			    session.setAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION, prestador);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		User user = PortalUtil.getUser(renderRequest);

		boolean puedeSolicitarCotizacion =
			    PermissionUtil.userContainsRole(
			        user,
			        WebKeysPrestadores.ROL_SOLICITAR_COTIZACION_PRESTADOR
			    );

		boolean validaOk = true;
		String tabSel = ParamUtil.get(renderRequest, "tab_seleccionada", "datos");
		int idPrestador = 0;
		
		// Si CMD esta vacio, null, es primera pasada por aca en direccion al formulario de alta.
		// asi evitamos pasar por este codigo todas las veces que se redirige al render
		if(StringUtils.checkEmpty(cmd)){  
			
			this.cargarListas(renderRequest);
		}	

		Prestador prestador = null;
		
		if(!StringUtils.checkEmpty(cmd)){
			
			prestador = (Prestador) session.getAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION);
				
			if(cmd.equals(Constants.MOVE)){
				String cmdEnCurso = ParamUtil.getString(renderRequest, "accionEnCurso");
				String moverATab = ParamUtil.getString(renderRequest, "moverATab");
				
				List<MatriculaPrestador> matriculas = (List<MatriculaPrestador>) session.getAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
				List<ProfesionPrestador> profEsp = (List<ProfesionPrestador>) session.getAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);

				if(prestador != null && prestador.getTipo().getId() == 1 && (
					matriculas == null || matriculas.size() == 0 || estanTodasMatriculasdeBaja(matriculas))){
					SessionErrors.add(renderRequest, "error-prestador-matricula");
					validaOk = false;
					tabSel = "datos";
				}
				
//				if( (prestador != null && prestador.getTipo().getId() == 1 ) 
//						&& (matriculas != null && !existeMatriculaNacional(matriculas))){
//					SessionErrors.add(renderRequest, "error-prestador-matricula-nacional");
//					validaOk = false;
//					tabSel = "datos";
//				}
				
				if(prestador != null && prestador.getTipo().getId() == 1 && 
					(profEsp == null || profEsp.size() == 0 || estanTodasProfesionesEspecialidadesdeBaja(profEsp))){
					SessionErrors.add(renderRequest, "error-prestador-profesion");
					validaOk = false;
					tabSel = "datos";
				}
				
				if(validaOk){
					tabSel = moverATab;
				}
				
				if(cmdEnCurso.equalsIgnoreCase(Constants.VIEW)){
					renderRequest.setAttribute(Constants.CMD, Constants.VIEW);
				}else if(cmdEnCurso.equalsIgnoreCase(Constants.EDIT)){
					renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
				}else{
					renderRequest.setAttribute(Constants.CMD, Constants.ADD);
				}
			}
			
			if(cmd.equals(Constants.ADD)){
				tabSel = "lugar_atencion";
//				recuperamos el Prestador, la lista de matriculas, la lista de profesion-especialidad-subespecialidad
//				el Domicilio, los telefonos y los contactos electronicos
				List<MatriculaPrestador> matriculas = (List<MatriculaPrestador>) session.getAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
				List<ProfesionPrestador> profEsp = (List<ProfesionPrestador>) session.getAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
				List<PrestadorLugarAtencion> lugares = (List<PrestadorLugarAtencion>) session.getAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION);
				List<PrestadorPlan> planes =  (List<PrestadorPlan>) session.getAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
				
				Boolean solicitarCotizacion = (Boolean) session.getAttribute(SESSION_SOLICITAR_COTIZACION);

//				List<Telefono> telefonos = (List<Telefono>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
				List<ContactoElectronico> contactElec = (List<ContactoElectronico>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
				
//				Validamos ingreso minimo de las caracteristicas que forman al prestador
				if(prestador == null){
					SessionErrors.add(renderRequest, "error-prestador");
					validaOk = false;
					tabSel = "datos";
				}
				
//DS				
				if(prestador != null && prestador.getTipo().getId()!= 5){ //Tipo 5 corresponde a HOSPITALES
					    ArrayList<Prestador> prestadores = (ArrayList<Prestador>)PrestadorServiceUtil.getPrestadores(0, prestador.getCuit(), null, false);
					    if(prestadores!=null && prestadores.size()>0) {
					    	if(prestadores.get(0).getBaja_fecha()==null) {
					    		SessionErrors.add(renderRequest, "error-prestador-existente");
					    	}else {
					    		SessionErrors.add(renderRequest, "error-prestador-existente-con-fecha-baja");
					    	}
						   validaOk = false;
						   tabSel = "datos";
					    }   
				}		
//Find DS				
				if(prestador != null && prestador.getTipo().getId() == 1 && (
					matriculas == null || matriculas.size() == 0 || estanTodasMatriculasdeBaja(matriculas))){
					SessionErrors.add(renderRequest, "error-prestador-matricula");
					validaOk = false;
					tabSel = "datos";
				}
//				if( (prestador != null && prestador.getTipo().getId() == 1 ) 
//						&& (matriculas != null && !existeMatriculaNacional(matriculas))){
//					SessionErrors.add(renderRequest, "error-prestador-matricula-nacional");
//					validaOk = false;
//					tabSel = "datos";
//				}
				if(prestador != null && prestador.getTipo().getId() == 1 && 
					(profEsp == null || profEsp.size() == 0 || estanTodasProfesionesEspecialidadesdeBaja(profEsp))){
					SessionErrors.add(renderRequest, "error-prestador-profesion");
					validaOk = false;
					tabSel = "datos";
				}
				
				if(prestador != null && (prestador.getCbu()==null ||  prestador.getCbu().trim().length() < 22)){
						SessionErrors.add(renderRequest, "error-prestador-cbu");
						validaOk = false;
						tabSel = "datos";
				}
				
				if(lugares == null || lugares.size() == 0 || estanTodosLugaresAtenciondeBaja(lugares)){
					SessionErrors.add(renderRequest, "error-prestador-lugar-at");
					validaOk = false;
					tabSel = "lugar_atencion";
				}else if(lugares.size()>0) {
					Integer qCorreosValidos=0;
					Integer qCorreosElectronicos = 0;

					for(PrestadorLugarAtencion la:lugares) {
						if(la.getContactosElectronicos()!=null) {
						  if(!inexistenteMailFacturacion(la.getContactosElectronicos())) {
							qCorreosValidos++;
						  }	

							// Solo buscamos EMAIL si solicita cotización
				            if(Boolean.TRUE.equals(solicitarCotizacion)) {
				                if(!inexistenteMailElectronico(la.getContactosElectronicos())) {
				                    qCorreosElectronicos++;
				                }
				            }
						}

				        if(qCorreosValidos > 0 && (!Boolean.TRUE.equals(solicitarCotizacion) || qCorreosElectronicos > 0)) {
				            break;
				        }
					}
					
					if(qCorreosValidos==0) {
						SessionErrors.add(renderRequest, "error-prestador-contacto-facturacion");
						validaOk = false;
						tabSel = "lugar_atencion";
					}

					if (puedeSolicitarCotizacion && Boolean.TRUE.equals(solicitarCotizacion) && qCorreosElectronicos == 0) {
					    SessionErrors.add(renderRequest, "error-prestador-contacto-email");
					    validaOk = false;
					    tabSel = "lugar_atencion";
					}
				}
				
				
//				if(telefonos == null || telefonos.size() == 0 || estanTodosTelefonosLugarAtdeBaja(telefonos)){
//					SessionErrors.add(renderRequest, "error-prestador-telefono");
//					validaOk = false;
//					tabSel = "lugar_atencion";
//				}
				
				
//				if(contactElec == null || contactElec.size() == 0 || estanTodosContactoElecLugarAtdeBaja(contactElec)){
//					SessionErrors.add(renderRequest, "error-prestador-contacto");
//					validaOk = false;
//					tabSel = "lugar_atencion";
//				}	
//	            Fin validacion ingreso minimo
			
				
				if(validaOk){
					
					prestador.setMatriculas(matriculas);
					prestador.setProfesiones(profEsp);
					prestador.setLugaresAtencion(lugares);
					prestador.setPlanes(planes);
					
					idPrestador = PrestadorServiceUtil.insertar(prestador, user);

					if (puedeSolicitarCotizacion) {
						if (solicitarCotizacion != null) {
						    PrestadorServiceUtil.actualizarSolicitarCotizacionPrestador(
						            idPrestador,
						            solicitarCotizacion.booleanValue(),
						            user
						    );
						}

						List<String> rubros =
						        (List<String>) session.getAttribute("RUBROS_PRESTADOR");

						if (rubros != null) {
						    PrestadorServiceUtil.actualizarRubrosPrestador(
						            idPrestador,
						            rubros,
						            user
						    );
						}
					}

					prestador = PrestadorServiceUtil.getPrestador(idPrestador);
					
					session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
					session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
					
					session.setAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION, prestador);
					session.setAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION, prestador.getMatriculas());
					session.setAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION, prestador.getProfesiones());
					session.setAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION, prestador.getLugaresAtencion());
					session.setAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION, prestador.getPlanes());
					session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION, prestador.getLugaresAtencion().get(0).getTelefonos());
					session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION, prestador.getLugaresAtencion().get(0).getContactosElectronicos());
					
					if (SessionErrors.isEmpty(renderRequest)) {
						String successMessage = ParamUtil.getString(renderRequest, "successMessage");
						SessionMessages.add(renderRequest, "request_processed", successMessage);
					}
				}
//				else{
//					session.setAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION, prestador);
//					session.setAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION, prestador.getMatriculas());
//					session.setAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION, prestador.getProfesiones());
//					session.setAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION, prestador.getLugaresAtencion());
//				}
				renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
			}
			
			if(cmd.equals(Constants.EDIT) || cmd.equals(Constants.VIEW)){
				
				this.cargarListas(renderRequest);
				
				int idPrest = ParamUtil.getInteger(renderRequest, "prestador_id");
				
				if(idPrest > 0){
					prestador = PrestadorServiceUtil.getPrestador(idPrest);
					
					session.removeAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION);
					session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
					session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
					session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
					session.removeAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
					session.removeAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
					session.removeAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
					session.removeAttribute(SESSION_SOLICITAR_COTIZACION);
					session.removeAttribute("RUBROS_PRESTADOR");

					session.setAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION, prestador);
					session.setAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION, prestador.getMatriculas());
					session.setAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION, prestador.getProfesiones());
					session.setAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION, prestador.getLugaresAtencion());
					session.setAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION, prestador.getPlanes());
					if (puedeSolicitarCotizacion) {
						session.setAttribute(SESSION_SOLICITAR_COTIZACION,
								Boolean.valueOf(prestador.isSolicitarCotizacion()));
						session.setAttribute("RUBROS_PRESTADOR",
								PrestadorServiceUtil.getRubrosPrestador(idPrest));
					}

	//				session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION, prestador.getLugaresAtencion().get(0).getTelefonos());
	//				session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION, prestador.getLugaresAtencion().get(0).getContactosElectronicos());
				}
				tabSel = "datos";
				renderRequest.setAttribute(Constants.CMD, cmd);
				
			}
			
			if(cmd.equals(Constants.UPDATE)){
				tabSel = "datos";
				
//				recuperamos el Prestador, la lista de matriculas, la lista de profesion-especialidad-subespecialidad
//				el Domicilio, los telefonos y los contactos electronicos
				List<MatriculaPrestador> matriculas = (List<MatriculaPrestador>) session.getAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
				List<ProfesionPrestador> profEsp = (List<ProfesionPrestador>) session.getAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
				List<PrestadorLugarAtencion> lugares = (List<PrestadorLugarAtencion>) session.getAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION);
				List<PrestadorPlan> planes =  (List<PrestadorPlan>) session.getAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
//				List<Telefono> telefonos = (List<Telefono>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
				List<ContactoElectronico> contactElec = (List<ContactoElectronico>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
				
				Boolean solicitarCotizacion = (Boolean) session.getAttribute(SESSION_SOLICITAR_COTIZACION);

//				Validamos ingreso minimo de las caracteristicas que forman al prestador
				if(prestador == null){
					SessionErrors.add(renderRequest, "error-prestador");
					validaOk = false;
					tabSel = "datos";
				}
				if( (prestador != null && prestador.getTipo().getId() == 1 ) && (            //1;"PROFESIONAL" 
					matriculas == null || matriculas.size() == 0 || estanTodasMatriculasdeBaja(matriculas))){
					SessionErrors.add(renderRequest, "error-prestador-matricula");
					validaOk = false;
					tabSel = "datos";					
					
				}
//				if( (prestador != null && prestador.getTipo().getId() == 1 ) 
//					&& (matriculas != null && !existeMatriculaNacional(matriculas))){
//					SessionErrors.add(renderRequest, "error-prestador-matricula-nacional");
//					validaOk = false;
//					tabSel = "datos";
//				}
				if(prestador != null && prestador.getTipo().getId() == 1 && 
					(profEsp == null || profEsp.size() == 0 || estanTodasProfesionesEspecialidadesdeBaja(profEsp))){
					SessionErrors.add(renderRequest, "error-prestador-profesion");
					validaOk = false;
					tabSel = "datos";
				}
				
				if(prestador != null && (prestador.getCbu()==null ||  prestador.getCbu().trim().length()  < 22)){
					SessionErrors.add(renderRequest, "error-prestador-cbu");
					validaOk = false;
					tabSel = "datos";
			    }
				
				if(lugares == null || lugares.size() == 0 || estanTodosLugaresAtenciondeBaja(lugares)){
					SessionErrors.add(renderRequest, "error-prestador-lugar-at");
					validaOk = false;
					tabSel = "lugar_atencion";
				}else if(lugares.size()>0) {
					Integer qCorreosValidos=0;
					Integer qCorreosElectronicos = 0;

					for(PrestadorLugarAtencion la:lugares) {
						if(la.getContactosElectronicos()!=null) {
						  if(!inexistenteMailFacturacion(la.getContactosElectronicos())) {
							qCorreosValidos++;
						  }

						// Solo buscamos EMAIL si solicita cotización
				            if(Boolean.TRUE.equals(solicitarCotizacion)) {
				                if(!inexistenteMailElectronico(la.getContactosElectronicos())) {
				                    qCorreosElectronicos++;
				                }
				            }
						}

						if(qCorreosValidos > 0 &&  (!Boolean.TRUE.equals(solicitarCotizacion) || qCorreosElectronicos > 0)) {
				            break;
				        }
					}
					
					if(qCorreosValidos==0) {
						SessionErrors.add(renderRequest, "error-prestador-contacto-facturacion");
						validaOk = false;
						tabSel = "lugar_atencion";
					}

					if(puedeSolicitarCotizacion && Boolean.TRUE.equals(solicitarCotizacion) && qCorreosElectronicos == 0) {

				        SessionErrors.add(renderRequest,"error-prestador-contacto-email");
				        validaOk = false;
				        tabSel = "lugar_atencion";
				    }
				}
				
//				if(telefonos == null || telefonos.size() == 0 || estanTodosTelefonosLugarAtdeBaja(telefonos)){
//					SessionErrors.add(renderRequest, "error-prestador-telefono");
//					validaOk = false;
//					tabSel = "lugar_atencion";
//				}

//				if(contactElec == null || contactElec.size() == 0 || estanTodosContactoElecLugarAtdeBaja(contactElec)){
//				SessionErrors.add(renderRequest, "error-prestador-contacto");
//				validaOk = false;
//				tabSel = "lugar_atencion";
//		     	}					
//	            Fin validacion ingreso minimo
			
				
				if(validaOk){
					
					prestador.setMatriculas(matriculas);
					prestador.setProfesiones(profEsp);
					prestador.setLugaresAtencion(lugares);
					prestador.setPlanes(planes);
					
					PrestadorServiceUtil.update(prestador, user);

					if (puedeSolicitarCotizacion) {
					
						if (solicitarCotizacion != null) {
						    PrestadorServiceUtil.actualizarSolicitarCotizacionPrestador(
								prestador.getId_prestador(),
						            solicitarCotizacion.booleanValue(),
						            user
						    );
						}

						List<String> rubros =
						        (List<String>) session.getAttribute("RUBROS_PRESTADOR");

						if (rubros != null) {
						    PrestadorServiceUtil.actualizarRubrosPrestador(
						            prestador.getId_prestador(),
						            rubros,
						            user
						    );
						}
					}
					prestador = PrestadorServiceUtil.getPrestador(prestador.getId_prestador());
					
					session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
					session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
					
					session.setAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION, prestador);
					session.setAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION, prestador.getMatriculas());
					session.setAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION, prestador.getProfesiones());
					session.setAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION, prestador.getLugaresAtencion());
					session.setAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION, prestador.getPlanes());
					
//					session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION, prestador.getLugaresAtencion().get(0).getTelefonos());
//					session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION, prestador.getLugaresAtencion().get(0).getContactosElectronicos());
					
					if (SessionErrors.isEmpty(renderRequest)) {
						String successMessage = ParamUtil.getString(renderRequest, "successMessage");
						SessionMessages.add(renderRequest, "request_processed", successMessage);
					}
				}
				renderRequest.setAttribute(Constants.CMD, Constants.EDIT);

			}
			
		}else{ // es Nuevo
			session.removeAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.PROF_ESPEC_SUBESPEC_PRESTADOR_EN_SESSION);
			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_INDIRECTO_EN_EDICION);
			session.removeAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);

			session.removeAttribute(SESSION_SOLICITAR_COTIZACION);
			session.removeAttribute("RUBROS_PRESTADOR");
			
//			accionEnCurso
			renderRequest.setAttribute(Constants.CMD, Constants.ADD);
		}
		
		String domicilio = null;
		if(prestador != null && prestador.getCuit() !=null){
			domicilio = EmpresaServiceUtil.traerPrestadorDomicilioFiscal(prestador.getCuit());
		}
		renderRequest.setAttribute(WebKeysLiquidaciones.DOMICILIO_AFIP_PRESTADOR_EN_EDICION, domicilio);
		
		renderRequest.setAttribute("tab", tabSel);
		
		return mapping.findForward(getForward(renderRequest,
						"portlet.prestadores.editar_entry"));
	}
	
	private boolean estanTodasMatriculasdeBaja(List<MatriculaPrestador> matri){
		
		boolean result = true;
		
		for (Iterator<MatriculaPrestador> iterator = matri.iterator(); iterator.hasNext();) {
			MatriculaPrestador m = iterator.next();
			
			if( (m.getEstado()!=null && m.getEstado().equals(MatriculaPrestador.ESTADOS.BAJA)) ||
				(m.getFechaVto() != null && m.getFechaVto().before(new Date()))){
				result = true;
			}else{
				result = false;
				break;
			}
		}
		return result;
	}
	
//	private boolean existeMatriculaNacional(List<MatriculaPrestador> matri){
////		valido 1 matricula Nacional y que no este de baja o vencida
//		boolean result = false;
//		
//		for (Iterator<MatriculaPrestador> iterator = matri.iterator(); iterator.hasNext();) {
//			MatriculaPrestador m = iterator.next();
//			
//			if(m.getTipo().contains("N") && 
//				!(m.getEstado()!=null && m.getEstado().equals(MatriculaPrestador.ESTADOS.BAJA))){
//				result = true;
//				break;
//			}
//		}
//		return result;
//	}
	
	private boolean estanTodasProfesionesEspecialidadesdeBaja(List<ProfesionPrestador> profEsp){
		
		boolean result = true;
		
		for (Iterator<ProfesionPrestador> iterator = profEsp.iterator(); iterator.hasNext();) {
			ProfesionPrestador pe = iterator.next();
			
			if(pe.getEstado()!=null && pe.getEstado().equals(ProfesionPrestador.ESTADOS.BAJA)){
				result = true;
			}else{
				result = false;
				break;
			}
		}
		return result;
	}
	
	private boolean estanTodosLugaresAtenciondeBaja(List<PrestadorLugarAtencion> lugarat){
		
		boolean result = true;
		
		for (Iterator<PrestadorLugarAtencion> iterator = lugarat.iterator(); iterator.hasNext();) {
			PrestadorLugarAtencion la = iterator.next();

			if(la.getBajaFecha()!=null && (la.getBajaFecha() != null && la.getBajaFecha().before(new Date()))
				&& (la.getEstado()!=null && la.getEstado().equals(PrestadorLugarAtencion.ESTADOS.BAJA)) ){
//			if(!la.getEstado().equals(PrestadorLugarAtencion.ESTADOS.BAJA)){
				result = true;
			}else{
				result = false;
				break;
			}
		}
		return result;
	}
	
	private boolean estanTodosTelefonosLugarAtdeBaja(List<Telefono> tel){
		
		boolean result = true;
		
		for (Iterator<Telefono> iterator = tel.iterator(); iterator.hasNext();) {
			Telefono t = iterator.next();

			if((t.getEstado()!=null && t.getEstado().equals(Telefono.ESTADOS.BAJA)) ||
					(t.getBajaFecha() != null && t.getBajaFecha().before(new Date()))){
				result = true;
			}else{
				result = false;
				break;
			}
		}
		return result;
	}
	
	private boolean estanTodosContactoElecLugarAtdeBaja(List<ContactoElectronico> ce){
		
		boolean result = true;
		
		for (Iterator<ContactoElectronico> iterator = ce.iterator(); iterator.hasNext();) {
			ContactoElectronico c = iterator.next();

			if((c.getEstado()!=null && c.getEstado().equals(ContactoElectronico.ESTADOS.BAJA)) ||
					(c.getBajaFecha()!=null && c.getBajaFecha().before(new Date()))){
				result = true;
			}else{
				result = false;
				break;
			}
		}
		return result;
	}
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{
		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
//		TraeListasServiceUtil.getSeccionales(renderRequest);
//		TraeListasServiceUtil.getRamosEmpresa(renderRequest);
//		TraeListasServiceUtil.getPosicionesIva(renderRequest);
//		TraeListasServiceUtil.getEntidadesCamaraEmpresa(renderRequest);
		TraeListasServiceUtil.getTiposPrestador(renderRequest);
		TraeListasServiceUtil.getProfesion(renderRequest);
		TraeListasServiceUtil.getEspecialidadPrestador(renderRequest);
		TraeListasServiceUtil.getSubEspecialidadPrestador(renderRequest);
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		if(session.getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION)== null ){
			List<Plan> planes = TraeListasServiceUtil.getPlanesOspim();
			session.setAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION, planes);
		}
			
		
		
	}
	
	
     private boolean inexistenteMailFacturacion(List<ContactoElectronicoPrestador> ce){
		
		boolean result = true;
		
		for (Iterator<ContactoElectronicoPrestador> iterator = ce.iterator(); iterator.hasNext();) {
			ContactoElectronico c = iterator.next();

			if( c.getTipo().getId().equals("F")  ){
				result = false;
			}
		}
		return result;
	}
	
     private boolean inexistenteMailElectronico(List<ContactoElectronicoPrestador> ce) {

	    boolean result = true;

	    for (Iterator<ContactoElectronicoPrestador> iterator = ce.iterator(); iterator.hasNext();) {

	        ContactoElectronico c = iterator.next();

	        if (c.getTipo().getId().equals(ContactoElectronico.Tipo.EMAIL.getId())) {
	            result = false;
	            break;
	        }
	    }

	    return result;
	}
}

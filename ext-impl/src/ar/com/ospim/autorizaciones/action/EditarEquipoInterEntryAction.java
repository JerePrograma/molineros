package ar.com.ospim.autorizaciones.action;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mortbay.jetty.Request;

import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.beans.PrestacionesEquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.services.EquipoInterdisciplinarioServiceUtil;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

    // EditarEquipoInterEntryAction
	public class EditarEquipoInterEntryAction extends EquipoInterBaseAction  {
		
	//private Logger _log = Logger.getLogger(this.getClass());
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();

		Boolean esDatosTab = ParamUtil.getBoolean(actionRequest, "esDatosTab");
		
		User user = PortalUtil.getUser(actionRequest);
		
		if ( esDatosTab){
			EquipoInterdisciplinario equipoInterdisciplinario =null;	
			equipoInterdisciplinario  =getEquipoInterdisciplinarioFromRequest(PortalUtil.getHttpServletRequest(actionRequest), equipoInterdisciplinario ,user );
			session.setAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION, equipoInterdisciplinario );	
		}
		
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		User user = PortalUtil.getUser(renderRequest);
		boolean validaOk = true;
		int tipoDictamenBtnFirmar;
		
		//busco si corresponde habilitar el botton firma
		tipoDictamenBtnFirmar = EquipoInterdisciplinarioServiceUtil.getEsFirmante(user);
		renderRequest.setAttribute("Btn_Firmar",tipoDictamenBtnFirmar ); 
		
        int idRegEquipoInterdisciplinario = ParamUtil.getInteger(renderRequest, "id_registro_eq",0); 
        
        
        if(StringUtils.checkEmpty(cmd))		{ 			
												this.cargarListas(renderRequest);
											}	
		
        EquipoInterdisciplinario equipoInterdisciplinario  =null;
		
		if(!StringUtils.checkEmpty(cmd)){
			
			if(cmd.equals(Constants.DELETE)){
				  borraEquipoInterdiscipinarioEntry(renderRequest);
			      idRegEquipoInterdisciplinario =0;
			      return mapping.findForward("portlet.autorizaciones.view");
		    }
			
			if ( idRegEquipoInterdisciplinario ==0 ){  
				equipoInterdisciplinario = (EquipoInterdisciplinario) session.getAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION);	
			}else{ 
				equipoInterdisciplinario = EquipoInterdisciplinarioServiceUtil.getEquipoInterdisciplinario  (idRegEquipoInterdisciplinario);				
			}	
			
			if(validaOk){
				
				
				if(cmd.equals(Constants.SAVE)){
					
					List<PrestacionesEquipoInterdisciplinario> prestaciones= (List<PrestacionesEquipoInterdisciplinario>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION );
					equipoInterdisciplinario.setPrestaciones(prestaciones);
					
					if (equipoInterdisciplinario.getEstadoRegEquipoInter().equals("CARGADO")){
						equipoInterdisciplinario.setMotivoCierreEquipoInter("");
					}
					idRegEquipoInterdisciplinario = EquipoInterdisciplinarioServiceUtil.insertar(equipoInterdisciplinario   , user);					
					equipoInterdisciplinario = EquipoInterdisciplinarioServiceUtil.getEquipoInterdisciplinario(idRegEquipoInterdisciplinario ) ;
					 
					session.removeAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION);
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION );
					
					session.setAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION, equipoInterdisciplinario  );	
					session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION , equipoInterdisciplinario.getPrestaciones());
				}
				
				if(cmd.equals(Constants.EDIT ) || cmd.equals(Constants.VIEW )){
					if (idRegEquipoInterdisciplinario==0){						
						renderRequest.setAttribute(Constants.CMD,cmd ); 
						if (cmd.equals(Constants.VIEW )) {
							renderRequest.setAttribute("ModoConsulta","si" ); 
						}else{
							renderRequest.setAttribute("ModoConsulta","no" ); 	
						}
						return mapping.findForward(getForward(renderRequest,
								"portlet.autorizaciones.equipointerdisciplinario.editar_equipointerdisciplinarios_entry"));
					}
					
					
					session.removeAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION );
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION );
					
					session.setAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION , equipoInterdisciplinario   );	
					session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION , equipoInterdisciplinario.getPrestaciones());
					
					renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
					
					if (cmd.equals(Constants.VIEW ) ){
						renderRequest.setAttribute(Constants.CMD,Constants.VIEW);						
					}
				}
				
				if(cmd.equals(Constants.UPDATE )){
					
					idRegEquipoInterdisciplinario =equipoInterdisciplinario.getId_registroEquipoInter();					
					String diagnosticoOriginal;
					String codigoCie10Original;
					Telefono telefonoOriginal;
					Telefono telefonoIngresado ;
					Domicilio domicilioOriginal;
					Domicilio domicilioIngresado;					
					String emailOriginal ;
					String[] dictamenesOriginales;
					int idContactoE =0;
					//  Datos previos a la carga  
					telefonoOriginal = equipoInterdisciplinario.getTelefonoContacto();					
					diagnosticoOriginal=equipoInterdisciplinario.getDiagnosticoAfiliado();
					codigoCie10Original=equipoInterdisciplinario.getCodigoCie10() ;
					domicilioOriginal=equipoInterdisciplinario.getAfiliado().getDomicilioDefault();
					emailOriginal = equipoInterdisciplinario.getAfiliado().getEmail();
					idContactoE=equipoInterdisciplinario.getIdEmail() ;
					dictamenesOriginales=equipoInterdisciplinario.getDictamenOriginales();
					// equipoInterdisciplinario11=equipoInterdisciplinario;  
    				//   Datos posteriores a la carga
					equipoInterdisciplinario   =getEquipoInterdisciplinarioFromRequest(PortalUtil.getHttpServletRequest(renderRequest), equipoInterdisciplinario ,user);
					// aca david
					equipoInterdisciplinario.setDictamenesOrigianles(dictamenesOriginales);
					// carga del id del contacto de la base 
					equipoInterdisciplinario.setIdEmail(idContactoE); 
					// carga prestaciones de la lista 
					List<PrestacionesEquipoInterdisciplinario> prestaciones= (List<PrestacionesEquipoInterdisciplinario>) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION );
					equipoInterdisciplinario.setPrestaciones(prestaciones);
					
					telefonoIngresado =equipoInterdisciplinario.getTelefonoContacto();
					domicilioIngresado=equipoInterdisciplinario.getAfiliado().getDomicilioDefault();					
					telefonoIngresado.setId(telefonoOriginal.getId()  ); 
					//comparacion datos 
					if (!emailOriginal.equals(equipoInterdisciplinario.getAfiliado().getEmail() )  ){
						equipoInterdisciplinario.setCambioEmailAfiliado(true);
					}
					if ( ! codigoCie10Original.equals(equipoInterdisciplinario.getCodigoCie10())   || ! diagnosticoOriginal.equals(equipoInterdisciplinario.getDiagnosticoAfiliado()) ){
						equipoInterdisciplinario.setCambioDiagnosticoCie10(true);
					}
					if (!(telefonoIngresado.compareTo(telefonoOriginal) )){
						equipoInterdisciplinario.setCambioCambioTelefono(true);
					}
					
					if (!(domicilioIngresado.compareTo(domicilioOriginal) )){
						equipoInterdisciplinario.setCambioDomicilio(true);
					}
					
					equipoInterdisciplinario.setId(idRegEquipoInterdisciplinario );					
					//Actualiza Dictamen
					EquipoInterdisciplinarioServiceUtil.update(equipoInterdisciplinario   , user);
					equipoInterdisciplinario = EquipoInterdisciplinarioServiceUtil.getEquipoInterdisciplinario(idRegEquipoInterdisciplinario ) ;
					
					session.removeAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION);
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION);
					
					session.setAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION, equipoInterdisciplinario   );	
					session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION , equipoInterdisciplinario.getPrestaciones());
										
			 }	
					if (SessionErrors.isEmpty(renderRequest)  && (cmd.equals(Constants.UPDATE)  || cmd.equals(Constants.SAVE))  ) 	{
						String successMessage = ParamUtil.getString(renderRequest, "successMessage");
						SessionMessages.add(renderRequest, "request_processed", successMessage);																}
						renderRequest.setAttribute(Constants.CMD, Constants.EDIT);					
					    if (cmd.equals(Constants.VIEW) ){
						   renderRequest.setAttribute(Constants.CMD,Constants.VIEW);					                                    }					    
			   }
				
		}else{ // es Nuevo			
			  
				session.removeAttribute(WebKeysAutorizaciones.EQUIPO_DISCIPLINARIO_EN_EDICION);
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_EQUIPO_EN_SESION );
								
				renderRequest.setAttribute(Constants.CMD, Constants.ADD);								  
				
		
		}
		
		return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.equipointerdisciplinario.editar_equipointerdisciplinarios_entry"));		                 
	}	

    
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{

		//carga de listas en sesion localidaes y provincias
		TraeListasServiceUtil.getLocalidades(renderRequest);
		TraeListasServiceUtil.getProvincias(renderRequest);
		// carga la lista de CIE 10
		TraeListasServiceUtil.getListadoCieDiez(renderRequest);
		
	}

	protected void borraEquipoInterdiscipinarioEntry(RenderRequest renderRequest)
			throws Exception {
		    int id_equipo = ParamUtil.getInteger(renderRequest,
				"id_registro_eq", 0);
		User user = PortalUtil.getUser(renderRequest);
		EquipoInterdisciplinarioServiceUtil.borrar(id_equipo , user);
	}

	
}

package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.services.CredencialesServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.autorizaciones.beans.BusquedaSituacionMedicaFiltro;
import ar.com.ospim.autorizaciones.beans.ItemSituacionMedicaTotal;
import ar.com.ospim.autorizaciones.beans.SituacionMedica;
import ar.com.ospim.autorizaciones.services.SituacionesMedicasServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.StringUtils;


	public class EditarSituMedicaEntryAction extends PortletAction  {
		
	private Logger _log = Logger.getLogger(this.getClass());
	
	private PlanServiceUtil planService = new PlanServiceUtil();

	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		User user = PortalUtil.getUser(renderRequest);		
        int idRegSituacionMedica = ParamUtil.getInteger(renderRequest, "id_registro_sitmed",0);
        boolean consultaDetalleDiscaxCuilInte    = ParamUtil.getBoolean(renderRequest, "consultaDetalleDiscaxCuilInte",false);
        String cuilAfiliado   = null;
		int inteAfiliado = 0;
		
        Boolean abrioPopUpEnEdicion=false; 
        
        Integer idRegistroPopupEdicion=(Integer)session.getAttribute(WebKeysAutorizaciones.REGISTROVTNAPOPUP_EDICION_SITUACION_MEDICA);
        
        PortletSession portletSession = renderRequest.getPortletSession();
        
        
        if (null != idRegistroPopupEdicion) {
        	idRegSituacionMedica = idRegistroPopupEdicion;
        	session.removeAttribute(WebKeysAutorizaciones.REGISTROVTNAPOPUP_EDICION_SITUACION_MEDICA);
        	abrioPopUpEnEdicion=true ;
        	cmd=Constants.EDIT;
        }
        
        if (null != renderRequest.getParameter("idRegistroPopUp")) {
        	idRegistroPopupEdicion = Integer.valueOf(renderRequest.getParameter("idRegistroPopUp"));
        	idRegSituacionMedica = Integer.valueOf(renderRequest.getParameter("idRegistroEditado")); 
		}
        
        SituacionMedica situacionMedica =null;
		
		if(!StringUtils.checkEmpty(cmd) ){
			
			if(cmd.equals(Constants.DELETE)){ // delete desde el buscador de situaciones medicas 
				  borraSituacionMedicaEntry(renderRequest);
				  idRegSituacionMedica =0;
				  BusquedaSituacionMedicaFiltro   busquedaSituacionFiltro  = (BusquedaSituacionMedicaFiltro  )session.getAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_SITUACIONMEDICA);
				  List<ItemSituacionMedicaTotal> busqueda = SituacionesMedicasServiceUtil.buscarSituacionesMedicasTotales(busquedaSituacionFiltro ) ;
				  portletSession.setAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_SITUACIONES_MEDICAS,	busqueda);
			      return mapping.findForward("portlet.autorizaciones.view");
		    }
			
			if(cmd.equals(Constants.DEACTIVATE )){// delete desde la vista de edicion de la situacion medica 
				  borraSituacionMedicaEntry(renderRequest);
				  situacionMedica = SituacionesMedicasServiceUtil.getSituacionMedica(idRegSituacionMedica,null,0);
				  session.removeAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION );
				  session.removeAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION   );					
				  session.setAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION , situacionMedica  );	
				  session.setAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION   , situacionMedica.getPatologias());
			      return mapping.findForward("portlet.autorizaciones.situacionmedica.bajapatologiasituacionmedica");
		    }
			
			if(cmd.equals(Constants.PREVIEW) || consultaDetalleDiscaxCuilInte ){ // vtna popup de consulta 
				  if (consultaDetalleDiscaxCuilInte){
		        	if (null != renderRequest.getParameter("cuil")) {
		        		cuilAfiliado  = renderRequest.getParameter("cuil").trim().length() > 0 ? renderRequest
								.getParameter("cuil") : null;
					}
					if (null != renderRequest.getParameter("inte")) {
						inteAfiliado = Integer.valueOf( renderRequest.getParameter("inte")); 
					}
		        	situacionMedica = SituacionesMedicasServiceUtil.getSituacionMedica(0,cuilAfiliado  ,inteAfiliado );
		          }else{
		        	situacionMedica = SituacionesMedicasServiceUtil.getSituacionMedica(idRegistroPopupEdicion,null,0);  
		          }				  
				  session.removeAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_POPUP_EN_EDICION );				  
				  session.setAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_POPUP_EN_EDICION, situacionMedica  );
				  session.setAttribute(WebKeysAutorizaciones.REGISTROVTNAPOPUP_EDICION_SITUACION_MEDICA,idRegSituacionMedica ) ;
			      return mapping.findForward("portlet.administracion.consulta_situ_medica");			      
		    }
			if (!abrioPopUpEnEdicion){
				session.removeAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION );
				session.removeAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION   );	
			}
			
			if(cmd.equals(Constants.SAVE)){	
				situacionMedica  =getSituacionMedicaFromRequest(PortalUtil.getHttpServletRequest(renderRequest),  situacionMedica);
				idRegSituacionMedica = SituacionesMedicasServiceUtil.insertar(situacionMedica   , user);
				
				SituacionesMedicasServiceUtil.generarFormularioSiNoExiste(
				        idRegSituacionMedica,
				        situacionMedica.getIdTipoSituMedica(),
				        user.getScreenName()
				    );
				
				situacionMedica = SituacionesMedicasServiceUtil.getSituacionMedica(idRegSituacionMedica,null,0);
				if (situacionMedica.getIdTipoSituMedica() == 7 || situacionMedica.getIdTipoSituMedica() == 8 ){
					AfiPlan afiPlan = planService.buscarUltimoPlanAportes(situacionMedica.getCuit_titular()); 
					if (afiPlan != null && (("KRONO".equalsIgnoreCase(afiPlan.getPlan().getDescripcionEnsalud()) 
							&& ("A".equalsIgnoreCase(afiPlan.getPlan().getFarmaciaEnsalud()) 
							|| "B".equalsIgnoreCase(afiPlan.getPlan().getFarmaciaEnsalud() )))
						    ||  "DELTA".equalsIgnoreCase(afiPlan.getPlan().getDescripcionEnsalud())) ){
						
					
						CredencialesServiceUtil.insertarCredencial(situacionMedica.getCuit_titular(), situacionMedica.getInte(), user.getScreenName());
						List<String> emails;
						String destinos;
						
						emails = new ArrayList<String>();
						destinos=TraeListasServiceUtil.getSystemConfig("EXENTO_DE_COPAGO");
						String[] auxDestinos = destinos.split(";");
						for (String to : auxDestinos) {
							emails.add(to);
						}
						
						String detalle =  null;
						detalle =  "Aviso envío credencial exento de copago \n   \n"
									  + "afiliado     :  " + situacionMedica.getAfiliado().getApellido() + " " + situacionMedica.getAfiliado().getNombre() + "\n"
						   			  + "cuil_titular :  " + situacionMedica.getCuit_titular() + "\n"
						   			  + "inte         :  " + situacionMedica.getInte()  + "\n";
						EnviaEmailsThread.enviarMailDesatendido("Aviso envió credencial exento de copago", detalle, emails,1);
					}
				}
				
				session.setAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION , situacionMedica  );	
				session.setAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION   , situacionMedica.getPatologias());
				BusquedaSituacionMedicaFiltro   busquedaSituacionFiltro  = (BusquedaSituacionMedicaFiltro  )session.getAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_SITUACIONMEDICA);
				
				if (busquedaSituacionFiltro != null) {
					List<ItemSituacionMedicaTotal> busqueda = SituacionesMedicasServiceUtil.buscarSituacionesMedicasTotales(busquedaSituacionFiltro ) ;				
					portletSession.setAttribute(WebKeysAutorizaciones.BUSQUEDA_REGISTROS_SITUACIONES_MEDICAS,	busqueda);					
				}
				
				renderRequest.setAttribute(Constants.CMD, Constants.EDIT);										
			}
			
			if(cmd.equals(Constants.EDIT ) ||  cmd.equals(Constants.VIEW )){
				situacionMedica = SituacionesMedicasServiceUtil.getSituacionMedica(idRegSituacionMedica ,null,0 );
				session.setAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION  , situacionMedica   );	
				session.setAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION , situacionMedica.getPatologias() );					
				renderRequest.setAttribute(Constants.CMD, Constants.EDIT);					
				if (cmd.equals(Constants.VIEW ) ){
					renderRequest.setAttribute(Constants.CMD,Constants.VIEW);						
				}
			}
			
			if(cmd.equals(Constants.UPDATE )){
				situacionMedica  =getSituacionMedicaFromRequest(PortalUtil.getHttpServletRequest(renderRequest),  situacionMedica);  
				SituacionesMedicasServiceUtil.update(situacionMedica, user);
				situacionMedica= SituacionesMedicasServiceUtil.getSituacionMedica(situacionMedica.getIdSituacionMedica(),null,0);
				session.setAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION , situacionMedica);	
				session.setAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION, situacionMedica.getPatologias());			
			}
			
			if (SessionErrors.isEmpty(renderRequest)  && (cmd.equals(Constants.UPDATE)  || cmd.equals(Constants.SAVE))  ) 	{
					String successMessage = ParamUtil.getString(renderRequest, "successMessage");
					SessionMessages.add(renderRequest, "request_processed", successMessage);																
			}
			
			renderRequest.setAttribute(Constants.CMD, Constants.EDIT);					
		    if (cmd.equals(Constants.VIEW) ){
			   renderRequest.setAttribute(Constants.CMD,Constants.VIEW);					                                    
			}					    	
		}else{ // es Nuevo
				    session.removeAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION );
					session.removeAttribute(WebKeysAutorizaciones.LISTADO_PATOLOGIAS_SITUACION_MEDICA_EN_SESION);									
					renderRequest.setAttribute(Constants.CMD, Constants.ADD);
		}		
		return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.situacionmedica.editar_situacionmedica_entry"));
	}	
	
	

	protected void borraSituacionMedicaEntry(RenderRequest renderRequest)
			throws Exception {
		    int idSituMedica = ParamUtil.getInteger(renderRequest,
				"id_registro_sitmed", 0);
		    User user = PortalUtil.getUser(renderRequest);
		    SituacionesMedicasServiceUtil.borrar(idSituMedica, user);
	}
	

	public SituacionMedica getSituacionMedicaFromRequestCargaInicial(HttpServletRequest req, SituacionMedica situacionMedica ) {		

		try {			
			int inte = ParamUtil.getInteger(req,"inte");
			String cuil_titular = ParamUtil.getString(req,"cuil");			
			situacionMedica = new SituacionMedica(cuil_titular , inte );
		} catch (Exception e) {
			_log.error(e);
		}

		return situacionMedica ; 		
	}
	
public SituacionMedica getSituacionMedicaFromRequest(HttpServletRequest req, SituacionMedica situacionMedica ) {		

		try {
			Date fechaDesde;
			Date fechaHasta;
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");		
			String fechaDiaDesde = ParamUtil.getString(req,"fechaDesdeDia");
			String fechaMesDesde = ParamUtil.getString(req,"fechaDesdeMes") ;
			String fechaAnioDesde = ParamUtil.getString(req,"fechaDesdeAnio");

			fechaDesde= formatoDePeriodo.parse(fechaDiaDesde + "/"
					+ (Integer.parseInt(fechaMesDesde) + 1) + "/"
					+ fechaAnioDesde);
			String fechaDiaHasta = ParamUtil.getString(req,"fechaHastaDia");
			String fechaMesHasta = ParamUtil.getString(req,"fechaHastaMes") ;
			String fechaAnioHasta = ParamUtil.getString(req,"fechaHastaAnio");
			
			if (! (fechaDiaHasta=="" && fechaMesHasta==""  && fechaAnioHasta=="")){
				fechaHasta= formatoDePeriodo.parse(fechaDiaHasta + "/"
						+ (Integer.parseInt(fechaMesHasta) + 1) + "/"
						+ fechaAnioHasta);
			}else{
				fechaHasta=null;
			}
			
			int inte = ParamUtil.getInteger(req,"inte");
			String cuil_titular = ParamUtil.getString(req,"cuil");			
			String cie10= ParamUtil.getString(req,"codigoCie10");
			cie10= ParamUtil.getString(req,"codigoCie");
			String diagnostico; 
			String tipodiscapacidades= ParamUtil.getString(req,"tipo_discapacidad_seleccionados");
			boolean dependencia = ParamUtil.getBoolean(req,"dependencia");
			String telefono= ParamUtil.getString(req,"telefono_contacto");	
			int idSituacionMedica = ParamUtil.getInteger(req,"situacionMedica");
			String detalleSituMedica= ParamUtil.getString(req,"detalleSitMedEncode");
			int esDiscapacitado= ParamUtil.getInteger(req,"esDiscapacitado");
			if (esDiscapacitado==1){ 
				diagnostico= ParamUtil.getString(req,"diagnostico");
			}else{
				diagnostico= ParamUtil.getString(req,"diagnosticonodiscapacitado");
			}		
			int idRegistroSituacionMedica = ParamUtil.getInteger(req,"id_registro_situmedica"); 
			situacionMedica = new SituacionMedica(cuil_titular , inte , cie10 , diagnostico , tipodiscapacidades ,  dependencia , telefono , idSituacionMedica , detalleSituMedica,esDiscapacitado==1,fechaDesde,fechaHasta );
			situacionMedica.setIdSituacionMedica(idRegistroSituacionMedica); 
		} catch (Exception e) {
			_log.debug(e);
		}
		
		return situacionMedica ;
	}

	
}

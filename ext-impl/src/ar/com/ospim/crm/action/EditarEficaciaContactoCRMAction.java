package ar.com.ospim.crm.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.CRMEficacia;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.EdificioSectorUsuarioLiferay;
import ar.com.ospim.crm.beans.TipoContacto;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 * 
 */
public class EditarEficaciaContactoCRMAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());	
	
// redirige al render
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {

	// preferi no hacer nada x el processAction...
//			System.out.println("pasando x el processAction");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
	
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User usuario = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
        if(cmd==null || "".equalsIgnoreCase(cmd)){
        	cmd=ParamUtil.getString(renderRequest, "accion", null);
        }
		Integer idContacto = null;
		ContactoCRM contactoCrm = null, contactoCRMreferido=null;  
		// contactoCRMreferido: contacotCRM al que estamos cargando verificacion eficacia
		CRMEficacia eficaciaCRM = null;

		
		String msg = "";
		EdificioSectorUsuarioLiferay derivaUser = null;
//		cargarListas(session);
		
		if (!StringUtils.checkEmpty(cmd)) {
			
			idContacto = ParamUtil.getInteger(renderRequest, "id_contacto");
			
			if(cmd.equals(Constants.ADD) ){ // prepara una verif eficacia en blanco (vacio)
				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				renderRequest.setAttribute(WebKeysCrm.CRM_ID_CONTACTO, idContacto); 
			}
			
			if(cmd.equals(Constants.SAVE) ){ // inserta nuevo (si hay reapertura, nuevo contacto mas notificaciones)
				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
				
				eficaciaCRM = this.getVerifEficaciaFromRequest(renderRequest);
				idContacto = eficaciaCRM.getIdContacto();
				
//				 if(contactoCrm.getDerivacion() != null){ 
//						// seteamos ahora xq se inserta mas tarde la derivacion, luego de obtener el objeto...
//						 derivaUser = new EdificioSectorUsuarioLiferay(contactoCrm.getDerivacion().getEdificio(), 
//									contactoCrm.getDerivacion().getGrupo(), contactoCrm.getDerivacion().getUsuario());
//				 }
				 derivaUser = getDerivacionSiReaperturaFromRequest(renderRequest);
				 
				 int idEficacia = CrmServiceUtil.insertaEficacia(eficaciaCRM, usuario.getScreenName(),
						 String.valueOf(UserUtil.getUserGroups(usuario.getUserId()).get(0).getUserGroupId()) );
				 
				 eficaciaCRM.setId(idEficacia);
				 				  
				 msg = LanguageUtil.get(defaultLocale, "insert-crm-eficacia");
				 
				 msg = msg + " " + eficaciaCRM.getId();
				 
//				 analizamos si es DERIVADO que debemos: 
//				 	1) crear un nuevo contacto y asociar el id_referido con el viejo contacto
//				 	2) mandar derivacion 
//				 	3) mandar la notificación
				 
				 if(derivaUser != null){
					
					contactoCRMreferido = CrmServiceUtil.buscarContactoCRMbyIdContacto(idContacto);

					contactoCrm = new ContactoCRM();
					
					contactoCrm.setIdCrmRelacionado(idContacto); // 1)
					contactoCrm.setEstado(contactoCrm.getEstado().DERIVADO); // 2)
					contactoCrm.setDerivacion(derivaUser); // 2)
					contactoCrm.setDescripcion(eficaciaCRM.getObservaciones());
					
					contactoCrm.setCategoria(contactoCRMreferido.getCategoria());
					contactoCrm.setTipo(new TipoContacto(2, "LLAMADO SALIENTE"));
//					contactoCrm.setTipo(contactoCRMreferido.getTipo());
					contactoCrm.setMotivo(contactoCRMreferido.getMotivo());
					contactoCrm.setAfiliado(contactoCRMreferido.getAfiliado());
					
					int idSerialContacto = CrmServiceUtil.insertaContacto(contactoCrm, usuario.getScreenName(),
							 String.valueOf(UserUtil.getUserGroups(usuario.getUserId()).get(0).getUserGroupId()), null );
					contactoCrm = CrmServiceUtil.buscarContactoCRM(idSerialContacto);
					msg = LanguageUtil.get(defaultLocale, "insert-crm-contacto");
		
					msg = msg + " " + contactoCrm.getIdContacto();
					 
					msg = msg + CrmServiceUtil.insertaDerivacion(contactoCrm.getIdContacto(), contactoCrm.getImportancia(), 
							derivaUser, "Derivación de Auditoría", usuario.getScreenName(),
							 String.valueOf(UserUtil.getUserGroups(usuario.getUserId()).get(0).getUserGroupId()));
					
					CrmServiceUtil.insertarNotificacionInbox(contactoCrm, derivaUser, usuario); // 3)
				 }
				 
				 SessionMessages.add(renderRequest, "insertEficaciaOk");
				
				 _log.debug("Usuario: " + usuario.getScreenName() 
							+ " cmd: " + cmd 
							+ " id efi: " + idEficacia);

				 renderRequest.setAttribute("msgEficaciaOk", msg);				 
			}
			
//			if(cmd.equals(Constants.VIEW) ){ // Prepara popup view un contacto.
//				_log.debug("Usuario: " + usuario.getScreenName() + " cmd: " + cmd );
//	
//				int idSerialContrato = ParamUtil.getInteger(renderRequest, "idContactoSerial",0);
//				int idContacto = ParamUtil.getInteger(renderRequest, "idContacto",0);
//				
//				renderRequest.setAttribute(Constants.CMD, Constants.VIEW);
//
//
////				me mande la cagada de una busqueda x id y id_contacto, no se si se deberian unificar
//				if(idSerialContrato != 0){
//					contactoCrm = CrmServiceUtil.buscarContactoCRM(idSerialContrato);
//				}else{
//					contactoCrm = CrmServiceUtil.buscarContactoCRMbyIdContacto(idContacto);
//					renderRequest.setAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW, contactoCrm);
//					// esta vista muestra todos los seguimientos de 1 contacto
//					return mapping.findForward(getForward(renderRequest,"portlet.crm.view_seguimiento_contacto_entry"));
//				}
//				renderRequest.setAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW, contactoCrm);
//				// esta vista muestra de a 1 contacto
//				return mapping.findForward(getForward(renderRequest,"portlet.crm.editar_contacto_entry"));
//			}
			
//			if(!esNoAfiliado){
//				cargarAfiliadoyUltimosContactos(renderRequest, cuilTitular, inte);
//			}

			renderRequest.setAttribute(WebKeysCrm.CRM_EFICACIA_EN_EDICION, eficaciaCRM);

		}
		
		return mapping.findForward(getForward(renderRequest,"portlet.crm.editar_eficacia_entry"));

	}	
	
	private CRMEficacia getVerifEficaciaFromRequest(RenderRequest renderRequest){

		CRMEficacia efi = new CRMEficacia();
		
		String contacto_a = ParamUtil.getString(renderRequest, "crm_efi_contacto_a");
		String conforme = ParamUtil.getString(renderRequest, "crm_efi_conforme");
		String obs = ParamUtil.getString(renderRequest, "crm_efi_observaciones");
		int idContacto = ParamUtil.getInteger(renderRequest, "crm_efi_id_contacto");
		
		efi.setContacto_a(contacto_a);
		efi.setIdContacto(idContacto);
		efi.setConforme(conforme.equalsIgnoreCase("SI")?true:false);
		efi.setObservaciones(obs);
		
		return efi;
	}
	
	private EdificioSectorUsuarioLiferay getDerivacionSiReaperturaFromRequest(RenderRequest renderRequest){
		
		EdificioSectorUsuarioLiferay esu = null;
		String edificioDeri;
		String sectorDeri;
		String usuarioDeri;
		
		
		if(ParamUtil.getString(renderRequest,"crm_efi_reapertura")!=null && ParamUtil.getString(renderRequest,"crm_efi_reapertura").equalsIgnoreCase("on")){
			
			edificioDeri = ParamUtil.getString(renderRequest, "edificio_destino");
			sectorDeri = ParamUtil.getString(renderRequest, "sector_destino");
			usuarioDeri = ParamUtil.getString(renderRequest, "usuario_destino");
			
			esu =  new EdificioSectorUsuarioLiferay(edificioDeri, sectorDeri, usuarioDeri);
			 
		}
		
		return esu;
	}	
		
}

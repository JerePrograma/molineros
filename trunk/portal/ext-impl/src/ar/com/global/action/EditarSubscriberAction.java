package ar.com.global.action;

import java.text.ParseException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.global.beans.Destinatario;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.services.MailingServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarActasEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class EditarSubscriberAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarSubscriberAction.class);	

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);		
		
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				updateDestinatarioEntry(actionRequest, cmd);
				setForward(actionRequest, "portlet.global.editar_subscriber");
			}
		} catch (Exception e) {
			logger.debug("Error al guardar subscriber", e);			
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
	}

	
	private Destinatario updateDestinatarioEntry(ActionRequest actionRequest, String cmd)
			throws Exception {

		PortletSession portletSession =  actionRequest.getPortletSession();
		
		Destinatario dest = (Destinatario) portletSession
				.getAttribute(WebKeysGlobal.DESTINATARIO_EN_SESSION);
		Destinatario destNueva = null;

		if (dest == null) {
			dest = new Destinatario();
		}
		
		dest=getContacto(actionRequest);
		

		portletSession.setAttribute(WebKeysGlobal.DESTINATARIO_EN_SESSION, dest);
		
		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			destNueva=MailingServiceUtil.addSubscriber(dest, user.getScreenName());
		} else {
			destNueva=MailingServiceUtil.editarDestinatario(dest,user.getScreenName());
		}
		portletSession.setAttribute(WebKeysGlobal.DESTINATARIO_EN_SESSION,destNueva);
		String successMessage = ParamUtil.getString(actionRequest,
				"successMessage");
		SessionMessages.add(actionRequest, "request_processed",
				successMessage);
		//session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
		return destNueva;
	}

	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		// recien entro a la edicion/alta
					
		PortletSession portletSession =  renderRequest.getPortletSession();
		
		List<ListaDestinatarios> listas = null;
		
		listas=MailingServiceUtil.getListasMailing(null);				
		
		portletSession.setAttribute(WebKeysGlobal.ALL_LISTAS_MAILING, listas, PortletSession.APPLICATION_SCOPE);
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);	
		if(cmd!=null&&!cmd.equals(Constants.UPDATE)&&!cmd.equals(Constants.ADD)){
			portletSession.removeAttribute(WebKeysGlobal.DESTINATARIO_EN_SESSION);
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.global.editar_subscriber"));

	}
	
	private Destinatario getContacto(ActionRequest renderRequest)
			throws ParseException, SystemException {
		Destinatario contacto = new Destinatario();
		
		String nombre = ParamUtil.getString(renderRequest, "nombre");
		String apellido = ParamUtil.getString(renderRequest, "apellido");
		String email = ParamUtil.getString(renderRequest, "email");
		String tratamiento = ParamUtil.getString(renderRequest, "tratamiento");
		boolean paraPrueba = ParamUtil.getBoolean(renderRequest, "para_prueba");
		int idDestinatario= ParamUtil.getInteger(renderRequest, "id_destinatario");
		
		String idListasMailingP=ParamUtil.getString(renderRequest, "listas_mailing_p");

		contacto.setIdDestinatario(idDestinatario);
		contacto.setFirstname(nombre);
		contacto.setLastname(apellido);
		contacto.setEmail(email);
		contacto.setTitle(tratamiento);
		contacto.setCasillaPrueba(paraPrueba);
		contacto.setListas(idListasMailingP.split(","));
		
		
		return contacto;
	}
}
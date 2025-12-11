package ar.com.global.action;

import java.text.ParseException;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.beans.Destinatario;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.services.MailingServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarSubscriberAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarSubscriberAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();		
		
		Destinatario destinatario=getContacto(renderRequest);
		int id_destinatario=ParamUtil.getInteger(renderRequest, "id_destinatario");
		
		if(id_destinatario>0){
			Destinatario dest=MailingServiceUtil.getSubscriber(id_destinatario);
			portletSession.setAttribute(WebKeysGlobal.DESTINATARIO_EN_SESSION, dest);			
			List<ListaDestinatarios> listas=MailingServiceUtil.getListasMailing(null);
			portletSession.setAttribute(WebKeysGlobal.ALL_LISTAS_MAILING, listas, PortletSession.APPLICATION_SCOPE);
			return mapping.findForward(getForward(renderRequest,"portlet.global.editar_subscriber"));
		}else{
			ListaDestinatarios listaDestinatarios=MailingServiceUtil.getSubscribers(destinatario);		
			renderRequest.setAttribute("fromBusqueda", "true");
			portletSession.setAttribute(WebKeysGlobal.LISTA_DESTINATARIOS, listaDestinatarios);
			return mapping.findForward("portlet.global.agregar_mail_mailing");
		}
				
				

	}
	
	private Destinatario getContacto(RenderRequest renderRequest)
			throws ParseException, SystemException {
		Destinatario contacto = new Destinatario();
		
		String nombre = ParamUtil.getString(renderRequest, "nombre");
		String apellido = ParamUtil.getString(renderRequest, "apellido");
		String email = ParamUtil.getString(renderRequest, "email");
		String tratamiento = ParamUtil.getString(renderRequest, "tratamiento");


		contacto.setFirstname(nombre);
		contacto.setLastname(apellido);
		contacto.setEmail(email);

		contacto.setTitle(tratamiento);
		return contacto;
	}

	
}

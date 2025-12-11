package ar.com.global.action;

import java.util.Iterator;
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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BorrarSubscriberAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(BorrarSubscriberAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando linea a mailing");
		
		int id_destinatario=ParamUtil.getInteger(renderRequest, "id_destinatario");
		PortletSession portletSession = renderRequest.getPortletSession();	

		User user = PortalUtil.getUser(renderRequest);
		
		if (id_destinatario>0) {
			MailingServiceUtil.borrarDestinatario(id_destinatario, user.getScreenName());
		}
		
		ListaDestinatarios listaDestinatarios=(ListaDestinatarios)portletSession.getAttribute(WebKeysGlobal.LISTA_DESTINATARIOS);
		List<Destinatario> destinatarios=listaDestinatarios.getListaDestinatarios();
				
		try {
					
			removeContactoFromList(destinatarios, id_destinatario);

		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		listaDestinatarios.setListaDestinatarios(destinatarios);
		portletSession.setAttribute(WebKeysGlobal.LISTA_DESTINATARIOS, listaDestinatarios);
		
		return mapping.findForward("portlet.global.agregar_mail_mailing");
	}

	private void removeContactoFromList(List<Destinatario> list, int id_destinatario) {
		Iterator<Destinatario> it = list.iterator();
		while (it.hasNext()) {
			Destinatario aContactoEnLista = it.next();
			if (id_destinatario==aContactoEnLista.getIdDestinatario()) {				
					it.remove();	
			}

		}
	}

}

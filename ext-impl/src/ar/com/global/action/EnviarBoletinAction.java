package ar.com.global.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Destinatario;
import ar.com.global.services.MailingServiceUtil;
import ar.com.global.services.OpenemmClient;
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class EnviarBoletinAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(EnviarBoletinAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();		
		Boletin boletin=(Boletin) portletSession.getAttribute(WebKeysGlobal.BOLETIN_EN_EDICION);
		String tipoDestinatarios=ParamUtil.getString(renderRequest, "tipo_destinatarios");
		List<Destinatario> destinatarios=MailingServiceUtil.getDestinatariosFromListas(boletin.getListas());
		int id_lista=OpenemmClient.crearLista(boletin);
		OpenemmClient.insertSubscriberList(destinatarios, id_lista);
		boletin.setIdListaBoletin(id_lista);
		
		int id_mailing=OpenemmClient.crearMail(boletin);
		boletin.setIdBoletin(id_mailing);
				
		OpenemmClient.insertarContenido(boletin);		
		int result=OpenemmClient.enviarMail(boletin, tipoDestinatarios);	
		
		if(result>0){
			String successMessage = ParamUtil.getString(renderRequest,
					"Boletín enviado con éxito");
			SessionMessages.add(renderRequest, "request_processed",
				successMessage);
		}
		_log.debug("Enviado con éxito");
		return mapping.findForward("portlet.global.editar_boletin_entry");
	}	
		
}

package ar.com.global.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.services.OrganismoServiceUtil;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.services.MailingServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarListaMailingAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarListaMailingAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();		
		
		String nombre=ParamUtil.getString(renderRequest, "nombre_lista");
		int id_lista= ParamUtil.getInteger(renderRequest,"id_lista");
				
				
		if(id_lista!=0){ //Para la edición			
			ListaDestinatarios lista=MailingServiceUtil.getListaMailing(id_lista);
			portletSession.setAttribute(WebKeysGlobal.LISTA_DESTINATARIOS , lista);			
			renderRequest.setAttribute("cmd", Constants.UPDATE);			
			return mapping.findForward("portlet.global.editar_mailing_entry");
		}else{ //Búsqueda organismos
			List<ListaDestinatarios> lista=null;
			lista = MailingServiceUtil.getListasMailing(nombre);
			portletSession.removeAttribute(WebKeysGlobal.LISTAS_MAILING,PortletSession.APPLICATION_SCOPE);			
			portletSession.setAttribute(WebKeysGlobal.LISTAS_MAILING, lista, PortletSession.APPLICATION_SCOPE);
			return mapping.findForward("portlet.global.buscar_listas_mailing_result");
		}
		

	}

	
}

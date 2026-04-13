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

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Destinatario;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.services.MailingServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarBoletinAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarBoletinAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();		
		
		Boletin boletin=getContacto(renderRequest);
		int id_boletin=ParamUtil.getInteger(renderRequest, "id_boletin");
		
		if(id_boletin>0){
			Boletin dest=MailingServiceUtil.getBoletin(id_boletin);
			portletSession.setAttribute(WebKeysGlobal.BOLETIN_EN_EDICION, dest);			
			List<ListaDestinatarios> listas=MailingServiceUtil.getListasMailing(null);
			portletSession.setAttribute(WebKeysGlobal.ALL_LISTAS_MAILING, listas, PortletSession.APPLICATION_SCOPE);
			return mapping.findForward(getForward(renderRequest,"portlet.global.editar_boletin_entry"));
		}else{
			List<Boletin> boletines=MailingServiceUtil.getBoletines(boletin);		
			renderRequest.setAttribute("fromBusqueda", "true");
			portletSession.setAttribute(WebKeysGlobal.LISTA_BOLETINES, boletines);
			return mapping.findForward("portlet.global.boletines_search_result");
		}		
	
	}
	
	private Boletin getContacto(RenderRequest renderRequest)
			throws ParseException, SystemException {
		Boletin boletin = new Boletin();
		
		int id_boletin= ParamUtil.getInteger(renderRequest, "id_boletin");
		String nombre = ParamUtil.getString(renderRequest, "nombre_boletin");
		String subject = ParamUtil.getString(renderRequest, "subject");
		

		boletin.setIdBoletin(id_boletin);
		boletin.setNombre(nombre);
		boletin.setAsunto(subject);
		
		return boletin;
	}

	
}

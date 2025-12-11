package ar.com.global.action;

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

import ar.com.global.beans.Boletin;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.services.MailingServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;

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
public class EditarBoletinEntryAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarBoletinEntryAction.class);	

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);		
		
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				updateBoletin(actionRequest, cmd);
				setForward(actionRequest, "portlet.global.editar_boletin_entry");
			}
		} catch (Exception e) {
			logger.debug("Error al guardar acta", e);
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
	}
	
	private Boletin updateBoletin(ActionRequest actionRequest, String cmd)
			throws Exception {

		PortletSession portletSession =  actionRequest.getPortletSession();
		
		Boletin boletin = (Boletin) portletSession.getAttribute(WebKeysGlobal.BOLETIN_EN_EDICION);
		Boletin boletinNuevo = null;

		if (boletin == null) {
			boletin = new Boletin();
		}
		String nombre=ParamUtil.getString(actionRequest,"nombreBoletin");
		String asunto=ParamUtil.getString(actionRequest,"asuntoBoletin");
		String observaciones=ParamUtil.getString(actionRequest,"observaciones");
		String listasMail=ParamUtil.getString(actionRequest,"listas_mailing_h");
		boolean soloTexto=ParamUtil.getBoolean(actionRequest, "solo_texto");
		ParamUtil.getString(actionRequest, "solo_texto");
		
		String [] listas=null;
		
		
		boletin.setNombre(nombre);
		boletin.setAsunto(asunto);
		boletin.setObservaciones(observaciones);
		if(null!=listasMail){
			listas=listasMail.split(",");
		}
		boletin.setListas(listas);
		boletin.setSoloTexto(soloTexto);
				
		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			boletinNuevo=MailingServiceUtil.nuevoBoletin(boletin, user.getScreenName());
		} else {
			boletinNuevo=MailingServiceUtil.editarBoletin(boletin,user.getScreenName());
		}
		portletSession.setAttribute(WebKeysGlobal.BOLETIN_EN_EDICION,boletinNuevo);
		String successMessage = ParamUtil.getString(actionRequest,
				"successMessage");
		SessionMessages.add(actionRequest, "request_processed",
				successMessage);
		//session.removeAttribute(WebKeysTesoreria.INSPECTORES_AGREGADOS);
		return boletin;
	}

	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		// recien entro a la edicion/alta
					
		PortletSession portletSession =  renderRequest.getPortletSession();
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);	
		if(cmd!=null&&!cmd.equals(Constants.UPDATE)&&!cmd.equals(Constants.ADD)){
			portletSession.removeAttribute(WebKeysGlobal.BOLETIN_EN_EDICION);
		}
		
		List<ListaDestinatarios> listas = MailingServiceUtil.getListasMailing(null);			
		
		portletSession.setAttribute(WebKeysGlobal.ALL_LISTAS_MAILING, listas, PortletSession.APPLICATION_SCOPE);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.global.editar_boletin_entry"));

	}
}
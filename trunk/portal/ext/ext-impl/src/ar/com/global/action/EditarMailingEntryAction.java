package ar.com.global.action;

import java.util.ArrayList;
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
import ar.com.cgt.ddhh.beans.Organismo;
import ar.com.global.beans.Destinatario;
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

public class EditarMailingEntryAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarMailingEntryAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				updateListaDestinatariosEntry(actionRequest, cmd);
				setForward(actionRequest, "portlet.global.editar_mailing_entry");
			} else if (cmd.equals("crearListaOrganismo")) {
				generarListaDestinatariosFromOrganismos(actionRequest, cmd);								
			}
		} catch (Exception e) {
			logger.debug("Error al guardar acta", e);
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
	}

	private void generarListaDestinatariosFromOrganismos(
			ActionRequest actionRequest, String cmd) throws Exception {
		PortletSession portletSession = actionRequest.getPortletSession();
		List<Organismo> organismos = (ArrayList<Organismo>) portletSession
				.getAttribute(WebKeysCGT.BUSQUEDA_ORGANISMOS,
						PortletSession.APPLICATION_SCOPE);
		String itemsLista = ParamUtil.getString(actionRequest, "itemsLista");
		ListaDestinatarios listaNueva = new ListaDestinatarios();
		List<Destinatario> lista=null;
		
		if (itemsLista != null) {
			lista = new ArrayList<Destinatario>();
			for (Organismo org : organismos) {
				String[] seleccionados = itemsLista.split(";");
				for (String i : seleccionados) {					
					if (Integer.parseInt(i) == org.getId_organismo()) {
						Destinatario dest = new Destinatario();
						dest.setEmail(org.getEmail());
						dest.setFirstname(org.getNombre());
						lista.add(dest);
					}
				}

			}
			listaNueva.setListaDestinatarios(lista);
		}
		portletSession.setAttribute(WebKeysGlobal.LISTA_DESTINATARIOS,
				listaNueva);

	}

	private void updateListaDestinatariosEntry(
			ActionRequest actionRequest, String cmd) throws Exception {

		PortletSession portletSession = actionRequest.getPortletSession();

		ListaDestinatarios lista = (ListaDestinatarios) portletSession
				.getAttribute(WebKeysGlobal.LISTA_DESTINATARIOS);
		ListaDestinatarios listaNueva = null;

		if (lista == null) {
			lista = new ListaDestinatarios();
		}
		String nombre = ParamUtil.getString(actionRequest, "nombreLista");
		String observaciones = ParamUtil.getString(actionRequest,
				"observaciones");

		lista.setNombre(nombre);
		lista.setObservaciones(observaciones);

		User user = PortalUtil.getUser(actionRequest);
		if (cmd.equals(Constants.ADD)) {
			listaNueva = MailingServiceUtil.nuevaListaMail(lista,
					user.getScreenName());
		} else {
			listaNueva = MailingServiceUtil.editarMailingConDestinatarios(
					lista, user.getScreenName());
		}
		portletSession.setAttribute(WebKeysGlobal.LISTA_DESTINATARIOS,
				listaNueva);
		String successMessage = ParamUtil.getString(actionRequest,
				"successMessage");
		SessionMessages.add(actionRequest, "request_processed", successMessage);
 		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		// recien entro a la edicion/alta

		PortletSession portletSession = renderRequest.getPortletSession();
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		if (cmd!=null&&!cmd.equals(Constants.UPDATE)&&!cmd.equals(Constants.ADD)&&!cmd.equals("crearListaOrganismo")) {
			portletSession.removeAttribute(WebKeysGlobal.LISTA_DESTINATARIOS);
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.global.editar_mailing_entry"));

	}
}
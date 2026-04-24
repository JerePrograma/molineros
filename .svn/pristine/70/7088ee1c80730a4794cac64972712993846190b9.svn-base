package ar.com.global.action;

import java.util.ArrayList;
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
import ar.com.ospim.global.WebKeysGlobal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BorrarMailEntryAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(BorrarMailEntryAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Sacando linea a mailing");

		PortletSession portletSession = renderRequest.getPortletSession();

		List<Destinatario> list = null;

		ListaDestinatarios listaDestinatarios = (ListaDestinatarios) portletSession
				.getAttribute(WebKeysGlobal.LISTA_DESTINATARIOS);

		if (listaDestinatarios != null) {
			list = listaDestinatarios.getListaDestinatarios();
		}

		if (list == null) {
			list = new ArrayList<Destinatario>();
		}

		String email = ParamUtil.getString(renderRequest, "email");
		
		Destinatario ap = new Destinatario();

		try {
			ap.setEmail(email);			
			removeContactoFromList(list, ap);

		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		logger.debug("Saliendo de sacar linea a organismo");
		return mapping.findForward("portlet.global.agregar_mail");
	}

	private void removeContactoFromList(List<Destinatario> list, Destinatario ap) {
		Iterator<Destinatario> it = list.iterator();
		while (it.hasNext()) {
			Destinatario aContactoEnLista = it.next();
			if (ap.getEmail()
					.trim()
					.toUpperCase()
					.equals(aContactoEnLista.getEmail().trim()
							.toUpperCase())) {				
					it.remove();	
			}

		}
	}

}

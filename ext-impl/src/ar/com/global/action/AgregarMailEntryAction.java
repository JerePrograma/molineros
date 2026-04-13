package ar.com.global.action;

import java.text.ParseException;
import java.util.ArrayList;
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

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AgregarMailEntryAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarMailEntryAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando ingreso a acta");

		PortletSession portletSession = renderRequest.getPortletSession();
		
		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		List<Destinatario> list = null;

		ListaDestinatarios listaDestinatarios = (ListaDestinatarios) portletSession
				.getAttribute(WebKeysGlobal.LISTA_DESTINATARIOS);
		if (listaDestinatarios == null) {
			listaDestinatarios = new ListaDestinatarios();
		}
		list = listaDestinatarios.getListaDestinatarios();
		if (list == null) {
			list = new ArrayList<Destinatario>();
		}
		try {
			list.add(getContacto(renderRequest));
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}
		listaDestinatarios.setListaDestinatarios(list);
		portletSession.setAttribute(WebKeysGlobal.LISTA_DESTINATARIOS, listaDestinatarios);

		return mapping.findForward("portlet.global.agregar_mail_mailing");

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

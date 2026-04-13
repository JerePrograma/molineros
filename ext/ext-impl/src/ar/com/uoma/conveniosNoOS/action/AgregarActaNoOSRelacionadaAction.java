package ar.com.uoma.conveniosNoOS.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.ActaNoExisteException;
import ar.com.ospim.tesoreria.ActaPerteneceAOtraEmpresaException;
import ar.com.ospim.tesoreria.ActaYaRelacionadaException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.Convenio.ActaRelacionada;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarActaNoOSRelacionadaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(AgregarActaNoOSRelacionadaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		Convenio conv = (Convenio) session
				.getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);
		if (conv == null) {
			conv = new Convenio();
			session.setAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION, conv);
		}
		getConvenioFromRequest(renderRequest, conv);

		List<ActaRelacionada> lista = conv.getActasRelacionadas();
		if (lista == null) {
			lista = new ArrayList<ActaRelacionada>();
			conv.setActasRelacionadas(lista);
		}
		String actaNro = renderRequest.getParameter("acta_asociada_nro");
		// List<InteresAfip> intereses = TraeListasServiceUtil
		// .getInteresesAfip(renderRequest);

		if (!estaEnLista(lista, actaNro)) {
			ActaRelacionada actaRelacionada = ConvenioNoOSServiceUtil
					.getActaARelacionar(conv, actaNro);
			if (actaRelacionada == null) {
				ActaNoExisteException e = new ActaNoExisteException();
				SessionErrors.add(renderRequest, e.getClass().getName());
			} else {
				if (!actaRelacionada.getActaRelacionada().getEmpresa()
						.getCuit().equals(conv.getEmpresa().getCuit())) {
					ActaPerteneceAOtraEmpresaException e = new ActaPerteneceAOtraEmpresaException();
					SessionErrors.add(renderRequest, e.getClass().getName());
					_log.debug("Saliendo de render");
					return mapping
							.findForward("portlet.estudio_isidro.convenios.acta_no_os.relacionada.search.result");
				}
				if (!ActaNoOSServiceUtil.isActaRelacionada(actaRelacionada
						.getActaRelacionada().getId())) {
					// actaRelacionada.calcularSaldoConInteres(intereses);
					lista.add(actaRelacionada);
				} else {
					ActaYaRelacionadaException e = new ActaYaRelacionadaException();
					SessionErrors.add(renderRequest, e.getClass().getName());
				}
			}
		}

		_log.debug("Saliendo de render");
		return mapping
				.findForward("portlet.estudio_isidro.convenios.acta_no_os.relacionada.search.result");
	}

	private boolean estaEnLista(List<ActaRelacionada> lista, String actaNro) {
		for (ActaRelacionada acta : lista) {
			if (acta.getActaRelacionada().getNumero().equals(actaNro)) {
				return true;
			}
		}
		return false;
	}

	private void getConvenioFromRequest(RenderRequest renderRequest,
			Convenio conveino) throws ParseException {
		String cuit = ParamUtil.getString(renderRequest, "cuit");
		String entidad = ParamUtil.getString(renderRequest, "entidad_con");
		conveino.setEmpresa(new Empresa(cuit));
		conveino.setEntidad(entidad);
	}

}

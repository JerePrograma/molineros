package ar.com.ospim.tesoreria.actas.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.ActaNoExisteException;
import ar.com.ospim.tesoreria.ActaPerteneceAOtraEmpresaException;
import ar.com.ospim.tesoreria.ActaYaRelacionadaException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.InteresAfip;
import ar.com.ospim.tesoreria.beans.Acta.ActaRelacionada;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarActaRelacionadaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(AgregarActaRelacionadaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		if (renderRequest.getParameter("esEdicion") != null) {
			renderRequest.setAttribute("esEdicion", "esEdicion");
		}

		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		if (acta == null) {
			acta = new Acta();
			session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		}

		getActaFromRequest(renderRequest, acta);

		List<ActaRelacionada> lista = acta.getActasRelacionadas();
		if (lista == null) {
			lista = new ArrayList<ActaRelacionada>();
			acta.setActasRelacionadas(lista);
		}
		String actaNro = renderRequest.getParameter("acta_asociada_nro");
		List<InteresAfip> intereses = TraeListasServiceUtil
				.getInteresesAfip(renderRequest);
		if (!estaEnLista(lista, actaNro)) {
			ActaRelacionada actaRelacionada = ActaServiceUtil
					.getActaARelacionar(acta, actaNro);
			if (actaRelacionada == null) {
				ActaNoExisteException e = new ActaNoExisteException();
				SessionErrors.add(renderRequest, e.getClass().getName());
			} else {
				if (!actaRelacionada.getActaRelacionada().getEmpresa()
						.getCuit().equals(acta.getEmpresa().getCuit())) {
					ActaPerteneceAOtraEmpresaException e = new ActaPerteneceAOtraEmpresaException();
					SessionErrors.add(renderRequest, e.getClass().getName());
					_log.debug("Saliendo de render");
					return mapping
							.findForward("portlet.tesoreria.actas.acta.relacionada.search.result");
				}
				if (!ActaServiceUtil.isActaRelacionada(actaRelacionada
						.getActaRelacionada().getId())) {
					actaRelacionada.calcularSaldoConInteres(intereses);
					lista.add(actaRelacionada);
				} else {
					ActaYaRelacionadaException e = new ActaYaRelacionadaException();
					SessionErrors.add(renderRequest, e.getClass().getName());
				}
			}
		}

		acta.setDeudaActasRelacionadas(acta.getDeudaFromActasRelacionadas());

		_log.debug("Saliendo de render");
		return mapping
				.findForward("portlet.tesoreria.actas.acta.relacionada.search.result");
	}

	private void getActaFromRequest(RenderRequest renderRequest, Acta acta)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaPagoDia = ParamUtil.getString(renderRequest,
				"fechaObligDia");
		String fechaPagoMes = ParamUtil.getString(renderRequest,
				"fechaObligMes");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
		String fechaPagoAnio = ParamUtil.getString(renderRequest,
				"fechaObligAnio");
		Date fechaPago = format.parse(fechaPagoDia + "-" + fechaPagoMes + "-"
				+ fechaPagoAnio);
		acta.setFechaPago(fechaPago);
		String cuit = ParamUtil.getString(renderRequest, "cuit");
		acta.setEmpresa(new Empresa(cuit));
	}

	private boolean estaEnLista(List<ActaRelacionada> lista, String actaNro) {
		for (ActaRelacionada acta : lista) {
			if (acta.getActaRelacionada().getNumero().equals(actaNro)) {
				return true;
			}
		}
		return false;
	}

}

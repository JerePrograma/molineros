package ar.com.ospim.tesoreria.actas.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarActasAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarActasAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String empresa = null;
		String cuit = null;
		String actaNroStr = null;
		
		boolean fromBusquedaDeuda = (null != renderRequest
				.getParameter("fromBusquedaDeuda") && renderRequest
				.getParameter("fromBusquedaDeuda").equals("fromBusquedaDeuda"));

		if (renderRequest.getParameter("acta") != null){
			actaNroStr = renderRequest.getParameter("acta").trim().length() > 0 ? renderRequest
				.getParameter("acta")
				: null;
		}

		if (null != renderRequest.getParameter("empresa")) {
			empresa = renderRequest.getParameter("empresa").trim().length() > 0 ? renderRequest
					.getParameter("empresa")
					: null;
		}

		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit")
					: null;
		}
		try {
			List<Acta> actas = null;
			if (!fromBusquedaDeuda) {
				actas = ActaServiceUtil.getActas(actaNroStr, cuit, empresa);
			} else {
				_log.debug("Pasando por aca al solicitar Deuda");
				actas = ActaServiceUtil.getDeuda(cuit, empresa);
				renderRequest.setAttribute("fromBusquedaDeuda", "fromBusquedaDeuda");
			}

			renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS);
			renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS, actas);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.tesoreria.actas.result.search");
	}

}

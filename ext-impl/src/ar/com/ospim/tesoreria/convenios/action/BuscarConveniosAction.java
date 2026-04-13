package ar.com.ospim.tesoreria.convenios.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarConveniosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarConveniosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String empresa = null;
		String cuit = null;
		String convenioNroStr = null;
		
		if (renderRequest.getParameter("convenio") != null){
			convenioNroStr = renderRequest.getParameter("convenio").trim().length() > 0 ? renderRequest
				.getParameter("convenio")
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
			List<Convenio> convenios = null;
			convenios = ConvenioServiceUtil.getConvenios(convenioNroStr, cuit, empresa);

			renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_CONVENIOS);
			renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_CONVENIOS, convenios);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.tesoreria.convenios.result.search");
	}

}

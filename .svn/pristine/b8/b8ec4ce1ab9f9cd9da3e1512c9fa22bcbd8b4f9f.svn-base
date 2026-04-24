package ar.com.uoma.actasNoOS.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarActasNoOSAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarActasNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String empresa = null;
		String cuit = null;
		String actaNroStr = null;
		String entidad = null;
		String estado = null;
		boolean calculo=ParamUtil.getBoolean(renderRequest, "calculo");
				

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
		
		if (null != renderRequest.getParameter("entidad")) {
			entidad = renderRequest.getParameter("entidad").trim().length() > 0 ? renderRequest
					.getParameter("entidad")
					: null;
		}
		
		if (null != renderRequest.getParameter("estado")) {
			estado = renderRequest.getParameter("estado").trim().length() > 0 ? renderRequest
					.getParameter("estado")
					: null;
		}
		
		try {
			List<Acta> actas = null;
			if(calculo){
				actas = ActaNoOSServiceUtil.getDeuda(cuit, empresa, entidad);
			}else{
				actas = ActaNoOSServiceUtil.getActas(entidad, actaNroStr, cuit, empresa, estado);
			}
			
			renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS);
			renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_ACTAS, actas);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.estudio_isidro.actas.result.search");
	}

}


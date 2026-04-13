package ar.com.uoma.paritarias.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;

import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.beans.Paritaria;
import ar.com.uoma.paritarias.services.ParitariaServiceUtil;

public class BuscarAumentosParitariasAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarAumentosParitariasAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String nombreCamara = null;
		String periodoMes = null;
		String periodoAnno = null;
		
		
		if (renderRequest.getParameter("nombre_camara") != null){
			nombreCamara = renderRequest.getParameter("nombre_camara").trim().length() > 0 ? renderRequest
				.getParameter("nombre_camara")
				: null;
		}

		if (null != renderRequest.getParameter("fechaDesdeMes")) {
			periodoMes = renderRequest.getParameter("fechaDesdeMes").trim().length() > 0 ? renderRequest
					.getParameter("fechaDesdeMes")
					: null;
		}
		
		if (null != renderRequest.getParameter("fechaDesdeAnio")) {
			periodoAnno = renderRequest.getParameter("fechaDesdeAnio").trim().length() > 0 ? renderRequest
					.getParameter("fechaDesdeAnio")
					: null;
		}

	
		try {
			List<Paritaria> paritaria = null;
			paritaria = ParitariaServiceUtil.getListaParitaria(nombreCamara, periodoMes, periodoAnno);

			renderRequest.removeAttribute(WebKeysUOMA.BUSCAR_PARITARIAS);
			renderRequest.setAttribute(WebKeysUOMA.BUSCAR_PARITARIAS, paritaria);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.uoma.paritarias.result.search");
	}

}

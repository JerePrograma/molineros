package ar.com.ospim.afiliados.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.global.beans.Domicilio;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class HistoricoDomicilioAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
		
		List<Domicilio> lista = null;
		
		try {
			lista = EditarAfiliadoServiceUtil.historicoDomicilios(cuil_titular);

			renderRequest.setAttribute(WebKeysAfiliados.HISTORICO_DOMICILIOS,lista);
			
		} catch (Exception e) {
			setForward(renderRequest, "portlet.afiliados.error");
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.historico.domicilio.result.search"));
	}

}

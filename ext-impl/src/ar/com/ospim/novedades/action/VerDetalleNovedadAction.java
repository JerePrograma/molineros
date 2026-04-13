package ar.com.ospim.novedades.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.novedades.beans.Novedad;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class VerDetalleNovedadAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int idNovedad = ParamUtil.getInteger(renderRequest, "id_novedad");
		
		Novedad nove = null;
		
		try {
			nove = NovedadesServiceUtil.getInstance().getNovedadById(idNovedad);
				
			renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_DETALLE_NOVEDAD ,nove);
//DS			
			TraeListasServiceUtil.getSituacionRevista(renderRequest);
//DS - Fin			
		} catch (Exception e) {
			setForward(renderRequest, "portlet.afiliados.error");
		}

		return mapping.findForward(getForward(renderRequest, "portlet.novedades.ver.detalle.popup"));
	}

}

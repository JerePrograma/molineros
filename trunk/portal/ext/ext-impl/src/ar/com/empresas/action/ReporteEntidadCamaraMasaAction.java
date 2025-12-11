package ar.com.empresas.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.empresas.beans.ReporteEntidadCamaraMasaBean;
import ar.com.ospim.global.services.EmpresaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class ReporteEntidadCamaraMasaAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ReporteEntidadCamaraMasaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		String cuit = ParamUtil.getString(renderRequest, "cuit");
		String sucur = ParamUtil.getString(renderRequest, "sucur");
		
		try {
			
			ReporteEntidadCamaraMasaBean reporte = EmpresaServiceUtil.getReporteEntidadCamaraMasa(cuit, sucur);
			

			renderRequest.removeAttribute(WebKeysEmpresas.REPORTE_ENTIDAD_CAMARA_MASA);
			renderRequest.setAttribute(WebKeysEmpresas.REPORTE_ENTIDAD_CAMARA_MASA, reporte);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.empresa.reporte_ent_cam_masa.result.search");
	}

}

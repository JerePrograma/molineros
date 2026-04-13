package ar.com.ospim.estudioisidro.action;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarLlamadosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarLlamadosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cuit = null;
		int cursor = 0;
		int size = 0;
//		String estado = renderRequest.getParameter("estado");
//		Integer idEstado = ParamUtil.getInteger(renderRequest, "estado", 0);
		if (null != renderRequest.getParameter("cuit")) {
			cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
					.getParameter("cuit") : null;
		}
//		if (estado != null && estado.trim().equals("true")) {
//			estado = EmpresaServiceUtil.getEstadoEmpleador(cuit);
//			renderRequest.setAttribute("estado", estado);
//			return mapping.findForward("portlet.estudio_isidro.empresa.estado");
//		_log.debug("al buscar llamados, estado = " +idEstado);
//		if (idEstado != null) {
//			_log.debug("solicitando estado empresa cuit="+cuit);
//			EstadoGestion estado = EmpresaServiceUtil.getEstadoEmpleador(cuit);
//			renderRequest.setAttribute("estado", estado);
//			return mapping.findForward("portlet.estudio_isidro.empresa.estado");
//		} else {

			try {
				size = Integer.parseInt((String) renderRequest.getAttribute("total"));
			} catch (NumberFormatException nfe) {
				size = 0;
			}

			
			if (null != renderRequest.getParameter("cur")
					&& !"".equals(renderRequest.getParameter("cur"))) {
				cursor = Integer.parseInt(renderRequest.getParameter("cur"));
			}
			try {
				LlamadosEstudio llamadosEstudio=null;
				
				if (size == 0) {
					size = LlamadoServiceUtil.getTotalLlamados(cuit);
				}
				llamadosEstudio = LlamadoServiceUtil.getLlamados(cuit, cursor);

				renderRequest.setAttribute("total", size);
				renderRequest.setAttribute("cur", cursor);
				
				PortletSession portletSession = renderRequest.getPortletSession();
				portletSession.removeAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
				portletSession.setAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO, llamadosEstudio);
				
				/*renderRequest.removeAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO);
				renderRequest.setAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO,llamadosEstudio);*/				
				
			} catch (Exception e) {
				_log.error(e);
				return mapping.findForward("portlet.estudio_isidro.error");
			}		
		return mapping.findForward("portlet.estudio_isidro.result.search");
//		}
	}

}

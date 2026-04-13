package ar.com.uoma.recibos.action;

import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.recibos.service.ReciboNoOSServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarRecibosNoOSAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarRecibosNoOSAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String empresa = null;
		String cuit = null;
		String actaNroStr = null;
		String entidad = null;
		String origen=null;
		
		
		
		Date fechaDesde=DateUtils.getFechaDesde(renderRequest);
		Date fechaHasta= DateUtils.getFechaHasta(renderRequest);
			

		if (renderRequest.getParameter("recibo") != null) {
			actaNroStr = renderRequest.getParameter("recibo").trim().length() > 0 ? renderRequest
					.getParameter("recibo")
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
		
		if (null != renderRequest.getParameter("entidad_bla")) {
			entidad = renderRequest.getParameter("entidad_bla").trim().length() > 0 ? renderRequest
					.getParameter("entidad_bla")
					: null;
		}
		try {
			List<Recibo> actas = ReciboNoOSServiceUtil.get(actaNroStr, cuit,
					empresa, entidad,null, fechaDesde, fechaHasta);
			origen=ParamUtil.getString(renderRequest, "origen");
			renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS);
			renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS, actas);
			renderRequest.setAttribute("origen", origen);
		} catch (Exception e) {
			_log.error(e);
		}
		if(null!=origen && origen.equals("recibosTesoreria")){
			return mapping.findForward("portlet.tesoreria.recibos.result.search");
		}else{
			return mapping.findForward("portlet.estudio_isidro.recibos_no_os.result.search");
		}
	}
}

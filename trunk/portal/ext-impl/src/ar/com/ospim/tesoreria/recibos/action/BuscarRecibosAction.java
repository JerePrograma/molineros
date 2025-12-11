package ar.com.ospim.tesoreria.recibos.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarRecibosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarRecibosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String empresa = null;
		String cuit = null;
		String actaNroStr = null;
		String cuil_titular = null;
		Integer inte = null;
		Integer id_amtima=null;
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		
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
		
		if (null != renderRequest.getParameter("cuil_titular")) {
			cuil_titular = renderRequest.getParameter("cuil_titular").trim().length() > 0 ? renderRequest
					.getParameter("cuil_titular")
					: null;
		}
		
		if (null != renderRequest.getParameter("inte")) {
			try{
				inte = Integer.valueOf(renderRequest.getParameter("inte")) >= 0 ? Integer.valueOf(renderRequest.getParameter("inte"))
						: null;
			}catch(Exception e){
				inte =null;
			}
			
		}
		
		
		if (null != renderRequest.getParameter("id_amtima")) {
			try{
				id_amtima = Integer.valueOf(renderRequest.getParameter("id_amtima")) >= 0 ? Integer.valueOf(renderRequest.getParameter("id_amtima"))
						: null;
			}catch(Exception e){
				id_amtima =null;
			}
		}
		
		try {
			List<Recibo> actas = ReciboServiceUtil.get(actaNroStr, cuit,
					empresa, cuil_titular, inte, entidad,id_amtima);

			renderRequest.removeAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS);
			renderRequest.setAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS, actas);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.tesoreria.recibos.result.search");
	}
}

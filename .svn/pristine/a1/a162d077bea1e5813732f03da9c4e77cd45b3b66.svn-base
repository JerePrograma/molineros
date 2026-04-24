package ar.com.cgt.ddhh.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.beans.NormaDdHh;
import ar.com.cgt.ddhh.services.NormaDDHHServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BorrarNormaDdHhAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BorrarNormaDdHhAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
				
		int id_normadh= ParamUtil.getInteger(renderRequest, "id_normaddhh");		
		
//		User user = PortalUtil.getUser(renderRequest);
		User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(renderRequest));
		String fechaDesdeDia=ParamUtil.getString(renderRequest, "fechaDesdeDia");
		String fechaDesdeMes=ParamUtil.getString(renderRequest, "fechaDesdeMes");
		String fechaDesdeAnio=ParamUtil.getString(renderRequest, "fechaDesdeAnio");
		String fechaHastaDia=ParamUtil.getString(renderRequest, "fechaHastaDia");
		String fechaHastaMes=ParamUtil.getString(renderRequest, "fechaHastaMes");
		String fechaHastaAnio=ParamUtil.getString(renderRequest, "fechaHastaAnio");
		String sistema=ParamUtil.getString(renderRequest, "sistema");
		if(sistema.equalsIgnoreCase("Todos")){
			sistema=null;
		}
		String autor=ParamUtil.getString(renderRequest, "autor");
		String lugar=ParamUtil.getString(renderRequest, "lugar");
		String numero=ParamUtil.getString(renderRequest, "numero");
		int id_tema= ParamUtil.getInteger(renderRequest, "id_tema");	
		int id_tipo= ParamUtil.getInteger(renderRequest, "id_tipo");	
		
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = null;
		Date fechaHasta = null;
		try {
			fechaDesde = formatoDeFecha.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		try {
			fechaHasta = formatoDeFecha.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		if(id_normadh!=0){ //Para la borrar
			 NormaDDHHServiceUtil.borrarNormaDDHH(id_normadh, user);
		}
		List<NormaDdHh> normasDH=null;
		normasDH = NormaDDHHServiceUtil.getNormasDhHh(fechaDesde, fechaHasta, sistema, numero, id_tema, id_tipo, autor, lugar);
		renderRequest.getPortletSession().removeAttribute(WebKeysCGT.BUSQUEDA_NORMASDDHH,PortletSession.APPLICATION_SCOPE);
		renderRequest.getPortletSession().setAttribute(WebKeysCGT.BUSQUEDA_NORMASDDHH, normasDH, PortletSession.APPLICATION_SCOPE);
		return mapping
				.findForward("portlet.cgt_ddhh.norma_ddhh_search_result");


	}

	
}

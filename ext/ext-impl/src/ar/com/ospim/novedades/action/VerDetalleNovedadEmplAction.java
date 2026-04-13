package ar.com.ospim.novedades.action;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.novedades.beans.NovedadEmpleadorTotal;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class VerDetalleNovedadEmplAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

		renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_DETALLE_NOVEDAD); 
				
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular");
		String inte = ParamUtil.getString(renderRequest, "inte");
//		String periodo = ParamUtil.getString(renderRequest, "periodo");
		
		NovedadEmpleadorTotal nove = new NovedadEmpleadorTotal();
		nove.setCuil_titular(cuil_titular);
		nove.setInte(Integer.valueOf(inte));
//		nove.setPeriodo(sdf.parse(periodo));
		
		List<NovedadEmpleadorTotal> busqueda_result = (List<NovedadEmpleadorTotal>) session.getAttribute(WebKeysAfiliados.BUSQUEDA_NOVEDADES_EN_SESSION);
		
		int posi = busqueda_result.indexOf(nove);
		
		nove = busqueda_result.get(posi);
		
		renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_DETALLE_NOVEDAD ,nove);
			
		return mapping.findForward(getForward(renderRequest, "portlet.novedades.empl.ver.detalle.popup"));
	}

}

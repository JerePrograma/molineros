package ar.com.ospim.prestadores.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaLugarAtencionIndirectoAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaLugarAtencionIndirectoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		_log.debug("entre x aca");
		
	}
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		List<PrestadorLugarAtencion> lugaresAtencion = null;
		
//		lugaresAtencion = (ArrayList<PrestadorLugarAtencion>) session.getAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_INDIRECTO_EN_SESSION);
		
		if(cmd.equals(Constants.VIEW)){
			int idPrest = ParamUtil.getInteger(renderRequest, "idPrestadorIn");
//			int idDom = ParamUtil.getInteger(renderRequest, "domicilio_id");
			
//			PrestadorLugarAtencion plaAux = new PrestadorLugarAtencion();
//			plaAux.setId_domicilio(idDom);
//			plaAux.setId_prestador(idPrest);
//			
//			int pos = lugaresAtencion.indexOf(plaAux);
//			plaAux = lugaresAtencion.get(pos);

			Prestador p = PrestadorServiceUtil.getPrestador(idPrest);
		
			lugaresAtencion = p.getLugaresAtencion();
		}
		
//		if(cmd.equals(Constants.RESET)){ // limpia el lugar de at en edicion
//			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
//			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
//			session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
//			
//			renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
//		}
		
		//pongo la lista en session
		session.removeAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_INDIRECTO_EN_SESSION);

		session.setAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_INDIRECTO_EN_SESSION, lugaresAtencion);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.lugar_at_prestador_indirecto"));
	}

}
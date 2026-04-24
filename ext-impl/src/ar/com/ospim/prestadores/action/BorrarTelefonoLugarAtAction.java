package ar.com.ospim.prestadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.TelefonoPrestador;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class BorrarTelefonoLugarAtAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BorrarTelefonoLugarAtAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int idTelef = ParamUtil.getInteger(renderRequest, "idTelefono");
		TelefonoPrestador tel = new TelefonoPrestador();
		tel.setId(idTelef);
		
		_log.debug("Borrando telefono id: " + idTelef);
		
		List<TelefonoPrestador> listaTelLugarAt = (ArrayList<TelefonoPrestador>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
		
		int pos = listaTelLugarAt.indexOf(tel);
//		reemplazo por el objeto de la lista
		tel = listaTelLugarAt.get(pos);
		if(tel.getEstado()==null){ // esta telefono esta en BD
			tel.setEstado(Telefono.ESTADOS.BAJA);
		}else{
			listaTelLugarAt.remove(pos);
		}	
		session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_TELEFONOS_EN_SESSION, listaTelLugarAt);
		
		return mapping.findForward("portlet.liquidaciones.lugar_at_telefonos.prestador");
	}
		
}
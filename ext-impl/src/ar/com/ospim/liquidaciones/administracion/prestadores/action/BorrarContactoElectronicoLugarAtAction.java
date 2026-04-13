package ar.com.ospim.liquidaciones.administracion.prestadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class BorrarContactoElectronicoLugarAtAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BorrarContactoElectronicoLugarAtAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int idContactoe = ParamUtil.getInteger(renderRequest, "idContactoe");
		ContactoElectronicoPrestador ce = new ContactoElectronicoPrestador();
		ce.setId(idContactoe);
		
		_log.debug("Borrando contacto elect. id: " + idContactoe);
		
		List<ContactoElectronicoPrestador> listaContactosLugarAt = (ArrayList<ContactoElectronicoPrestador>) session.getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
		
		int pos = listaContactosLugarAt.indexOf(ce);
//		reemplazo por el objeto de la lista
		ce = listaContactosLugarAt.get(pos);
		if(ce.getEstado()==null){ // esta contactoe esta en BD
			ce.setEstado(ContactoElectronico.ESTADOS.BAJA);
		}else{
			listaContactosLugarAt.remove(pos);
		}	
		session.removeAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_CONTACTOES_EN_SESSION, listaContactosLugarAt);
		
		return mapping.findForward("portlet.liquidaciones.lugar_at_contactos.prestador");
	}
		
}
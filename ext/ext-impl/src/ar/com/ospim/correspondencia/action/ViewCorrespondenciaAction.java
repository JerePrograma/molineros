package ar.com.ospim.correspondencia.action;

import java.util.ArrayList;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ViewCorrespondenciaAction"><b><i>View Source</i></b></a>
 * 
 * @author SVA
 * 
 */
public class ViewCorrespondenciaAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int id_correspondencia = ParamUtil.getInteger(renderRequest,"id_correspondencia");
//		int id_item_correspondencia = ParamUtil.getInteger(renderRequest,"id_item_correspondencia");
		
		String tipo_registro = ParamUtil.getString(renderRequest,"tipo_registro");
		String view = ParamUtil.getString(renderRequest, "view");
		
				
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		if (id_correspondencia != 0) {
			
			session.removeAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION);
			session.removeAttribute(WebKeysCorrespondencia.ENTRADA_DETALLE_EN_EDICION);
			session.removeAttribute(WebKeysCorrespondencia.SALIDA_EN_EDICION);
			session.removeAttribute(WebKeysCorrespondencia.SALIDA_DETALLE_EN_EDICION);
					
			CabeceraCorrespondencia cabecera = CorrespondenciaServiceImpl.buscarCabeceraCorrespondenciaPorId(id_correspondencia);
			cabecera.setItemsCorrespondencia((ArrayList) CorrespondenciaServiceImpl
							.buscarItemsPorIdCorrespondencia(id_correspondencia));
			
			if (tipo_registro.equalsIgnoreCase("ENTRADA")) {
				
				session.setAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION, cabecera);								
			}
			
			if (tipo_registro.equalsIgnoreCase("SALIDA")) {
			
				session.setAttribute(WebKeysCorrespondencia.SALIDA_EN_EDICION, cabecera);
			}
//			renderRequest.setAttribute(WebKeysCorrespondencia.ID_CORRESPONDENCIA_EN_EDICION,id_correspondencia);
			renderRequest.setAttribute("view",view);
		}
		
		if (tipo_registro.equalsIgnoreCase("ENTRADA")) {
			return mapping.findForward(getForward(renderRequest,
					"portlet.correspondencia.editar_entrada_entry"));
		} else {
			return mapping.findForward(getForward(renderRequest,
					"portlet.correspondencia.editar_salida_entry"));
		}			
	}
}
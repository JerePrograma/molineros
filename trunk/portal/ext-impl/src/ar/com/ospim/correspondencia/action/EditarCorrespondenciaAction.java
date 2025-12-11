package ar.com.ospim.correspondencia.action;

import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.correspondencia.NoSuchItemCorrespondenciaEntryException;
import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarCorrespondenciaAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author SVA
 * 
 */

public class EditarCorrespondenciaAction extends PortletAction {

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(actionRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		User user = PortalUtil.getUser(actionRequest);		
		
		int id_correspondencia = ParamUtil.getInteger(actionRequest,"id_correspondencia");
		int id_item_correspondencia = ParamUtil.getInteger(actionRequest,"id_item_correspondencia");
		String t_registro = ParamUtil.getString(actionRequest,"tipo_registro");
		
		try {

			if (cmd.equals(Constants.EDIT)) {
				
				CabeceraCorrespondencia cabecera = CorrespondenciaServiceImpl.buscarCabeceraCorrespondenciaPorId(id_correspondencia);	
				ItemCorrespondencia item = CorrespondenciaServiceImpl.buscarItemCorrespondenciaPorId(id_item_correspondencia);
				ArrayList<ItemCorrespondencia> listaItems = new ArrayList<ItemCorrespondencia>();
				listaItems.add(item);
				cabecera.setItemsCorrespondencia(listaItems);
				
				if(t_registro.equalsIgnoreCase("ENTRADA")){

					session.setAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION, cabecera);
					session.removeAttribute(WebKeysCorrespondencia.ENTRADA_DETALLE_EN_EDICION);
				}else{

					session.setAttribute(WebKeysCorrespondencia.SALIDA_EN_EDICION, cabecera);
					session.removeAttribute(WebKeysCorrespondencia.SALIDA_DETALLE_EN_EDICION);
				}
			}
			if (cmd.equals(Constants.DELETE)) {
				CorrespondenciaServiceImpl service = new CorrespondenciaServiceImpl();
				service.borrarItemCorrespondencia(id_item_correspondencia,
						user.getScreenName());
				setForward(actionRequest, "portlet.correspondencia.view");
			}
			
		} catch (Exception e) {
			if (e instanceof NoSuchItemCorrespondenciaEntryException) {
				SessionErrors.add(actionRequest, e.getClass().getName());
				setForward(actionRequest, "portlet.correspondencia.error");
			} else {
				throw e;
			}
		}
//		if (SessionErrors.isEmpty(actionRequest) && !errors) {
//			String successMessage = ParamUtil.getString(actionRequest,"successMessage");
//			
//			SessionMessages.add(actionRequest, "request_processed",successMessage);
//		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String tipo_registro = ParamUtil.getString(renderRequest, "tipo_registro");

		if (tipo_registro.equalsIgnoreCase("ENTRADA")) {
			return mapping.findForward(getForward(renderRequest,
					"portlet.correspondencia.editar_entrada_entry"));
		} else {
			return mapping.findForward(getForward(renderRequest,
					"portlet.correspondencia.editar_salida_entry"));
		}
	}

}

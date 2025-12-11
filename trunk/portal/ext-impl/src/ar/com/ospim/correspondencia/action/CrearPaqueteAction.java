package ar.com.ospim.correspondencia.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
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

import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.beans.ListaPaquete;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CrearPaqueteAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(CrearPaqueteAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		try {
			User user = PortalUtil.getUser(actionRequest);
			String descrip_paquete = ParamUtil.get(actionRequest, "paq_descripcion", "");
			
			ArrayList<ItemCorrespondencia> correspondenciaList = getItemCorrespondenciaListFromRequest(actionRequest);

			int id = CorrespondenciaServiceUtil.savePaquete(correspondenciaList, descrip_paquete, user);			

//			Esta chanchada es para evitar que vuelvan a quedar estos items en el resultado de la session
//			y generen inconvenientes y/o se puedan volver a empaquetar x duplicados
			if(id>0){
				HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
				
				List<ItemCorrespondencia> resultBusqueda = (List<ItemCorrespondencia>) session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA);
				int i = 0;
				long id_item=0;
				boolean encontro=false;
				ItemCorrespondencia ic_aux;
				ListaPaquete paq_aux;
				paq_aux = new ListaPaquete();
				paq_aux.setId(id);
				paq_aux.setId_paquete(id);
				
				for (Iterator<ItemCorrespondencia> iterator = correspondenciaList.iterator(); iterator.hasNext();) {
					
					ItemCorrespondencia itemCor = iterator.next();
					id_item = itemCor.getId();
					encontro = false;
					i=0;
					while(!encontro){
						ic_aux = resultBusqueda.get(i);
						if(ic_aux.getId() == id_item){
							encontro = true;
							ic_aux.setListaPaquete(paq_aux);
							ic_aux.setEstado("ENVIADO");
							resultBusqueda.remove(i);
							resultBusqueda.add(ic_aux);
						}
						i++;
					}
					
				}
				session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA);
				
				session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA, resultBusqueda);
				
			}
			actionRequest.setAttribute("paquete_id", id);
		} catch (Exception e) {
			_log.error("Error al crear el paquete", e);
			throw e;
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward("portlet.correspondencia.view");
	}

	@SuppressWarnings("unchecked")
	private ArrayList<ItemCorrespondencia> getItemCorrespondenciaListFromRequest(ActionRequest actionRequest) {
		
		ArrayList<ItemCorrespondencia> itemList = new ArrayList<ItemCorrespondencia>();

		Enumeration parameters = actionRequest.getParameterNames();
		
		while (parameters.hasMoreElements()) {
			String paramName = (String) parameters.nextElement();

			if (paramName.indexOf("empaquetar") != -1) {
				String id = paramName.substring("empaquetar".length(),paramName.length());				
				ItemCorrespondencia ic = new ItemCorrespondencia();
				ic.setId(Long.parseLong(id));
				itemList.add(ic);
			}
		}
		return itemList;
	}
}
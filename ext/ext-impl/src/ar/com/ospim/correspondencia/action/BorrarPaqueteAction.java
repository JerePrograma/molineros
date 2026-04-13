package ar.com.ospim.correspondencia.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

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
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BorrarPaqueteAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BorrarPaqueteAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		User user = PortalUtil.getUser(actionRequest);
		Connection con = null;
		
		try {
			_log.debug("Entrando a desempaquetar... usuario: "+user.getScreenName());
			ArrayList<ItemCorrespondencia> itemsMarcadoDesempaquetar = new ArrayList<ItemCorrespondencia>();
			ArrayList<ItemCorrespondencia> itemsADevolver = new ArrayList<ItemCorrespondencia>();
			
			ArrayList<Long> idsPaquetes = new ArrayList<Long>();
			
			String paramChecked = "";
			
			@SuppressWarnings("unchecked")
			ArrayList<ItemCorrespondencia> resultBusqueda = (ArrayList<ItemCorrespondencia>) session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA_RESULT);
			_log.debug("cantidad items resultBusqueda: " + resultBusqueda!=null?resultBusqueda.size():"null!!!!!!!");
			for (Iterator<ItemCorrespondencia> iterator = resultBusqueda.iterator(); iterator.hasNext();) {
				
				ItemCorrespondencia itemCorr = iterator.next();
				
//				asi esta armado el nombre del checkbox que esta en la pagina paquetes_search_result.jsp
				paramChecked = "itempaq_" + itemCorr.getListaPaquete().getId_paquete()+ "-" +itemCorr.getId();
				String valorCheckBox = ParamUtil.getString(actionRequest, paramChecked);

				_log.debug("item: " + paramChecked + " " + valorCheckBox);
//				Esto es para filtrar los otros items que no tienen el checkbox, 
//				ya desempaquetados o recibidos que sino se le cambiaba accidentalmente el estado a REVISAR.
				if(itemCorr.getEstado().equalsIgnoreCase("ENVIADO") ){  
					if(paramChecked != null && valorCheckBox !=null && valorCheckBox.equalsIgnoreCase("on")){
						itemsMarcadoDesempaquetar.add(itemCorr);
					}else{
						itemsADevolver.add(itemCorr);
					}
					if(!idsPaquetes.contains(itemCorr.getListaPaquete().getId_paquete()) ){
						idsPaquetes.add(itemCorr.getListaPaquete().getId_paquete());
					}
				}
			}
			if(idsPaquetes!=null && idsPaquetes.size()>0) {
				_log.debug("comenzando el proceso de desempaquetado...");
				con = ConnectionHelper.getConnectionForTransaction();
	//			Actualizamos estado del paquete
				for (Long id_paq : idsPaquetes) {
					_log.debug("ids paquetes: "+id_paq);
					CorrespondenciaServiceUtil.actualiza_estado_paquete(con, Integer.parseInt(String.valueOf(id_paq)), "DESEMPAQUETADO", user.getScreenName());
				}
				_log.debug("cantidad de items a desempaquetar: " + itemsMarcadoDesempaquetar.size());
	//			Actualizamos los items que se van a desempaquetar y 			
				CorrespondenciaServiceUtil.actualizarEstadoItems(con, itemsMarcadoDesempaquetar, "INGRESADO", user.getScreenName());
				
	//			for (Iterator<Long> iterator = ids_paquetes.iterator(); iterator.hasNext();) {
	//				Long id_paq =  iterator.next();
	//				CorrespondenciaServiceUtil.borrar_todos_items_paquete(Integer.parseInt(String.valueOf(id_paq)), user.getScreenName());
	//			}
				
	//			los que serán devueltos a su recepcionista para revision
	//			CorrespondenciaServiceUtil.actualizarEstadoItems(itemsADevolver, "INGRESADO", user.getScreenName());
				_log.debug("cantidad de items a devolver: " + itemsADevolver.size());
				CorrespondenciaServiceUtil.actualizarEstadoItems(con, itemsADevolver, "REVISAR", user.getScreenName());
	//			los quitamos de su paquete
				for (Iterator<ItemCorrespondencia> iterator = itemsADevolver.iterator(); iterator.hasNext();) {
					ItemCorrespondencia ic = iterator.next();
					CorrespondenciaServiceUtil.borrarItemsDelPaquete(con, Integer.parseInt(String.valueOf(ic.getId())), user.getScreenName());
				}
				
				con.commit();
				_log.debug("fin de desempaquetar");
			}else {
				_log.debug("nada que desempaquetar");
			}
		} catch (Exception e) {
			con.rollback();
			_log.error("Error al desempaquetar items paquete", e);
			throw e;
		}finally{
			ConnectionHelper.cerrar(con);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward("portlet.correspondencia.view");
	}

}
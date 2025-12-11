package ar.com.ospim.crm.action;

import java.util.ArrayList;
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
import ar.com.ospim.correspondencia.beans.BusquedaBandejaCorreoFiltro;
import ar.com.ospim.correspondencia.beans.ItemCorrespondenciaTotal;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.EdificioSectorUsuarioLiferay;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class MarcarRecibidoyDerivarContactoAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(MarcarRecibidoyDerivarContactoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
         
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		User user = PortalUtil.getUser(renderRequest);
		int idItemCorr = 0;
		String marca = null;
		Integer idContactoCRM = null;
		String observaciones = null;
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		boolean esRecepcionista = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);
		boolean perteneceLiquidaciones =  UserLocalServiceUtil.hasUserGroupUser(99214, user.getUserId() ); //99214 es liquidaciones
		EdificioSectorUsuarioLiferay derivacion = null;
		String sector = null;
		ContactoCRM contactoCRM = null;
		
		try {
			idItemCorr = ParamUtil.getInteger(renderRequest, "item_corr");
			marca =  ParamUtil.getString(renderRequest, "tipo_marca");
			idContactoCRM = ParamUtil.getInteger(renderRequest, "id_crm_cont_derivado");
			// lo voy a pasar en null, en la marca de recibido para no cerrar el contacto, xq luego sera derivado nuevamente...
			sector = String.valueOf(UserUtil.getUserGroups(user.getUserId()).get(0).getUserGroupId());
						
			derivacion = new EdificioSectorUsuarioLiferay(
					ParamUtil.getString(renderRequest, "deriva_edificio_destino") , 
					ParamUtil.getString(renderRequest, "deriva_sector_destino") , 
					ParamUtil.getString(renderRequest, "deriva_usuario_destino") );
			
			observaciones = ParamUtil.getString(renderRequest, "deriva_observaciones");
			
			CorrespondenciaServiceUtil.marcarRecibidoCorresp(idItemCorr, marca, null, null, true, user.getScreenName(), sector);
//			Solo necesito el primer objeto, porque siempre repite los datos del contacto esta busqueda
			contactoCRM = CrmServiceUtil.buscarContactoCRMbyIdContacto(idContactoCRM);
			
			CrmServiceUtil.insertarNotificacionInbox(contactoCRM, derivacion, user);
			
			CrmServiceUtil.insertaDerivacion(idContactoCRM, contactoCRM.getImportancia(), derivacion, observaciones, user.getScreenName(), sector);
			
		} catch (Exception e) {
			_log.error("Error al marcar recibido corresp y derivar contacto", e);
			throw e;
		}
		
		BusquedaBandejaCorreoFiltro filtro = (BusquedaBandejaCorreoFiltro) 
				session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
//		List<ItemCorrespondencia> busqueda = new ArrayList<ItemCorrespondencia>();
		List<ItemCorrespondenciaTotal> busqueda = new ArrayList<ItemCorrespondenciaTotal>();
		
		if(filtro != null){
			busqueda = CorrespondenciaServiceUtil.bandejaEntradaPagina(user, esRecepcionista, filtro, perteneceLiquidaciones); 
					//.bandejaEntrada(user, esRecepcionista, filtro);
		}
		session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT);
		session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT, busqueda);
		_log.debug("Derivando 8: " + System.currentTimeMillis());

		return mapping.findForward("portlet.ospim.correspondencia_inbox.result.search");
	}

}
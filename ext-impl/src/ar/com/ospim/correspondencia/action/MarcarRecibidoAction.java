package ar.com.ospim.correspondencia.action;

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
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class MarcarRecibidoAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(MarcarRecibidoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
         
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		User user = PortalUtil.getUser(renderRequest);
		int idItemCorr = 0;
		String marca = null, observaciones = "";
		boolean esCierreContactoCRM = false;
		Integer idContactoCRM = null;
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		boolean esRecepcionista = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);
		boolean perteneceLiquidaciones =  UserLocalServiceUtil.hasUserGroupUser(99214, user.getUserId() ); //99214 es liquidaciones
		
		try {

			idItemCorr = ParamUtil.getInteger(renderRequest, "item_corr");
			marca =  ParamUtil.getString(renderRequest, "tipo_marca");
			idContactoCRM = ParamUtil.getInteger(renderRequest, "id_crm_cont_derivado");
			observaciones = ParamUtil.getString(renderRequest, "deriva_observaciones");
			esCierreContactoCRM = ParamUtil.getBoolean(renderRequest, "esCierre",false);
			
			CorrespondenciaServiceUtil.marcarRecibidoCorresp(idItemCorr, marca, idContactoCRM, observaciones, esCierreContactoCRM,
					user.getScreenName(), String.valueOf(UserUtil.getUserGroups(user.getUserId()).get(0).getUserGroupId()));

		} catch (Exception e) {
			_log.error("Error al marcar recibido corresp", e);
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
		
		return mapping.findForward("portlet.ospim.correspondencia_inbox.result.search");
	}

}
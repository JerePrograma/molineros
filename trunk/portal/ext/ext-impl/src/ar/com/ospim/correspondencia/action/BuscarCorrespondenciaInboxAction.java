	package ar.com.ospim.correspondencia.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarCorrespondenciaInboxAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de correspondencia de bandeja de entrada según parámetros de entrada
 * 
 * @author SVA
 * 
 */
public class BuscarCorrespondenciaInboxAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarCorrespondenciaInboxAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		User user = PortalUtil.getUser(renderRequest);
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		try {
			String estado = ParamUtil.getString(renderRequest, "estado",null);
			String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHastaFinal", null);
			String CUITprestadorProveedor = ParamUtil.getString(renderRequest, "prest_prov",null);
			
			BusquedaBandejaCorreoFiltro filtro = (BusquedaBandejaCorreoFiltro) 
											session.getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
			if(filtro == null){
				filtro = new BusquedaBandejaCorreoFiltro();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaDesde = null;
			try {
				fechaDesde = sdf.parse(fechaDesdeFinal);
			} catch (Exception e) {
				fechaDesde = null;
			}		
			Date fechaHasta = null;
			try {
				fechaHasta = sdf.parse(fechaHastaFinal);
			} catch (Exception e) {
				fechaHasta = null;
			}			

			int registrosTotalBusqueda = 0;
			String llamada = ParamUtil.getString(renderRequest, "viene_de", "");
			int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
			pagina_sel--;
			
			boolean esRecepcionista = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);
			boolean perteneceLiquidaciones =  UserLocalServiceUtil.hasUserGroupUser(99214, user.getUserId() ); //99214 es liquidaciones
			
			filtro.setEstado(estado);
			filtro.setFechaDesde(fechaDesde);
			filtro.setFechaHasta(fechaHasta);
			filtro.setPagina(pagina_sel);
			filtro.setCuit(CUITprestadorProveedor);
			
			List<ItemCorrespondenciaTotal> busqueda = CorrespondenciaServiceUtil.bandejaEntradaPagina(user, esRecepcionista, filtro, perteneceLiquidaciones);

			
			
			session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT);
			session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
			
			session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA, filtro);
			session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA_RESULT, busqueda);
			
			if(busqueda != null && busqueda.size() > 0){
				registrosTotalBusqueda = busqueda.get(0).getTotal_registros();
				filtro.setRegistrosTotal(registrosTotalBusqueda);
				
				session.setAttribute("total_registros", registrosTotalBusqueda);
				session.setAttribute("offset_reg", pagina_sel);
				session.setAttribute("llamada", llamada);
			}else{
				filtro.setRegistrosTotal(registrosTotalBusqueda);
				session.setAttribute("total_registros",0 );
				session.setAttribute("offset_reg", 0);
				session.setAttribute("llamada", null);
			}
			
		} catch (Exception e) {
			_log.error(e);
		}

		return mapping.findForward("portlet.ospim.correspondencia_inbox.result.search");

	}

}
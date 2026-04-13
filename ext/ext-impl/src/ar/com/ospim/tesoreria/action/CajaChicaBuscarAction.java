package ar.com.ospim.tesoreria.action;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CajaChicaBuscarAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(CajaChicaBuscarAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		String popup = null;
		User user = PortalUtil.getUser(renderRequest);
		
		try {
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
			
			String descripcion= ParamUtil.getString(renderRequest, "descripcioncajachica");
			int concepto = ParamUtil.getInteger(renderRequest,"conceptocajachica");
			int estado = ParamUtil.getInteger(renderRequest,"estadocajachica");
			int entidad = ParamUtil.getInteger(renderRequest,"entidadcajachica");

			popup = ParamUtil.getString(renderRequest, "popup");
			
			String usuarioAlta = ParamUtil.getString(renderRequest, "usuarioalta");
			
			
			List<CajaChica> autorizaciones = new ArrayList<CajaChica>();
			autorizaciones=CajaChicaServiceUtil.list(descripcion, concepto, estado,entidad);
			session.removeAttribute("ListaCajasChicas");
			boolean rolAdministradorCajaChica = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA);
			boolean rolAdministradorCajaChicaSinOP = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA_SIN_OP);
			boolean rolUsuarioCajaChica = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_USUARIO_CAJA_CHICA);
			if(rolUsuarioCajaChica){
			   List<CajaChica> cajas = new ArrayList<CajaChica>();
			   for(CajaChica caja:autorizaciones){
				  Boolean habilitado=false; 
				  for(User u:caja.getUsuariosHabilitados()){
					  if(u.getUserId()==user.getUserId()){
						  habilitado=true;
						  break;
					  }
				  }
				  if(habilitado) cajas.add(caja);
			   }
			   session.setAttribute("ListaCajasChicas", cajas);
			}else if(rolAdministradorCajaChica || rolAdministradorCajaChicaSinOP) {
				session.setAttribute("ListaCajasChicas", autorizaciones);
			}
		   
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		
	   if(renderResponse.getNamespace().equals("_UOM_1_")){
		  return mapping.findForward("portlet.uoma.cajachica.buscar_caja_chica");
	   }else{
		  return mapping.findForward("portlet.tesoreria.cajachica.buscar_caja_chica");
	   }
	}

}
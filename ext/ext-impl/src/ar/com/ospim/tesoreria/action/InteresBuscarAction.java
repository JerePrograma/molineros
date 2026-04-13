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

import ar.com.ospim.tesoreria.WebKeysInteres;
import ar.com.ospim.tesoreria.beans.interes.Interes;
import ar.com.ospim.tesoreria.service.InteresServiceUtil;


import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class InteresBuscarAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(InteresBuscarAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		String popup = null;
		User user = PortalUtil.getUser(renderRequest);
		
		try {
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			
			/*
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
			*/
			
			// Sacar
			String descripcion = "";
			int concepto = 0, estado = 0;
			
			String fechaIni= ParamUtil.getString(renderRequest, "fechaDesde");			
			String fechaFin= ParamUtil.getString(renderRequest, "fechaHasta");
			Double interesDia = 0D;
			int entidad = ParamUtil.getInteger(renderRequest,"entidadInteres");

			popup = ParamUtil.getString(renderRequest, "popup");
			
			String usuarioAlta = ParamUtil.getString(renderRequest, "usuarioalta");		
			
			List<Interes> _intereses = new ArrayList<Interes>();
			_intereses = InteresServiceUtil.list(fechaIni, fechaFin, interesDia, entidad);
			session.removeAttribute("ListaInteres");
			boolean rolAdministradorInteres = PermissionUtil.userContainsRole(user,WebKeysInteres.ROL_ADMINISTRADOR_INTERES);
			boolean rolUsuarioInteres = PermissionUtil.userContainsRole(user,WebKeysInteres.ROL_USUARIO_INTERES);
			if(rolUsuarioInteres){
			   List<Interes> _interes = new ArrayList<Interes>();
			   for(Interes _int:_intereses){
				  Boolean habilitado=false; 
				  _interes.add(_int);
			   }
			   session.setAttribute("ListaInteres", _interes);
			}else if(rolAdministradorInteres) {
				session.setAttribute("ListaInteres", _intereses);
			}
		   
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		
	   if(renderResponse.getNamespace().equals("_UOM_1_")){
		  return mapping.findForward("portlet.uoma.intereses.buscar_interes");
	   }else{
		  return mapping.findForward("portlet.tesoreria.intereses.buscar_interes");
	   }
	}

}
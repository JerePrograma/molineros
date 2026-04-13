package ar.com.uoma.cuentacorrienteempresa.action;

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
import ar.com.ospim.tesoreria.WebKeysInteres;
import ar.com.ospim.tesoreria.beans.interes.Interes;
import ar.com.ospim.tesoreria.service.InteresServiceUtil;


import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.PermissionUtil;
import ar.com.uoma.beans.SaldoInicial;
import ar.com.uoma.cuentacorrienteempresa.services.SaldoInicialServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SaldoInicialBuscarAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(SaldoInicialBuscarAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		String popup = null;
		User user = PortalUtil.getUser(renderRequest);
		
		try {
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
							
			String entidadCuit= ParamUtil.getString(renderRequest, "cuit");			
			String entidadSuc= ParamUtil.getString(renderRequest, "sucursal");
						
			popup = ParamUtil.getString(renderRequest, "popup");
			
			List<SaldoInicial> _saldo = new ArrayList<SaldoInicial>();
			_saldo = SaldoInicialServiceUtil.list(null, entidadCuit, entidadSuc);
			session.removeAttribute("ListaSaldoInicial");
			boolean rolAdministradorInteres = true; //PermissionUtil.userContainsRole(user,WebKeysInteres.ROL_ADMINISTRADOR_INTERES);
			boolean rolUsuarioInteres = true; //PermissionUtil.userContainsRole(user,WebKeysInteres.ROL_USUARIO_INTERES);
			if(rolUsuarioInteres){
			   session.setAttribute("ListaSaldoInicial", _saldo);
			}else if(rolAdministradorInteres) {
				session.setAttribute("ListaSaldoInicial", _saldo);
			}
		   
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}
		
		return mapping.findForward("portlet.uoma.cuentacorriente.buscar_saldoinicial");

	}

}
package ar.com.ospim.autorizaciones.action;

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

import ar.com.ospim.autorizaciones.beans.Cartilla;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurEstado;
import ar.com.ospim.autorizaciones.services.CartillaServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarCartillaAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarCartillaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		String popup = null;
		
		try {
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

			String tipo= ParamUtil.getString(renderRequest, "tipo");
			String prestador = ParamUtil.getString(renderRequest, "prestador");
			String plan = ParamUtil.getString(renderRequest, "plan");
			String localidad = ParamUtil.getString(renderRequest, "localidad");
			
			String provincia = ParamUtil.getString(renderRequest, "provincia");
			String especialidad = ParamUtil.getString(renderRequest,"especialidad");
			
			
			String trabajaen = ParamUtil.getString(renderRequest, "trabajaen");
			
			Boolean incluyeBajas=ParamUtil.getBoolean(renderRequest, "incluyebajas");
			
			List<Cartilla> cartillaList = new ArrayList<Cartilla>();
			cartillaList = CartillaServiceUtil.getListaCartillas(tipo, prestador, plan, localidad, provincia, especialidad, trabajaen,incluyeBajas);				
			session.removeAttribute("CartillasLista");
			session.setAttribute("CartillasLista", cartillaList);
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return mapping.findForward("portlet.autorizaciones.buscar_cartilla");
	}

}
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

import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarNomencladorAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarNomencladorAction.class);

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

			int tipoNomenclador = ParamUtil.getInteger(renderRequest,"tipoNomenclador");
			String descripcionNomenclador = ParamUtil.getString(renderRequest, "descripcionNomenclador");
			int especialidad = ParamUtil.getInteger(renderRequest,"especialidad");
			String codigoNomenclador = ParamUtil.getString(renderRequest, "codigoNomenclador");
			String resolucionNomenclador = ParamUtil.getString(renderRequest, "resolucionNomenclador");
			Boolean recuperaSUR=ParamUtil.getBoolean(renderRequest, "recuperaSUR");
			int esPrestMed = ParamUtil.getInteger(renderRequest,"esPrestMed");
			
			
			popup = ParamUtil.getString(renderRequest, "popup");
			
			List<Nomenclador> autorizaciones = new ArrayList<Nomenclador>();
			autorizaciones = NomencladorServiceUtil.getListaNomenclador(tipoNomenclador,descripcionNomenclador,especialidad,codigoNomenclador,recuperaSUR,resolucionNomenclador);
				
			session.removeAttribute("Nomenclador");
			session.setAttribute("Nomenclador", autorizaciones);
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return mapping.findForward("portlet.autorizaciones.buscar_nomenclador");
	}

}
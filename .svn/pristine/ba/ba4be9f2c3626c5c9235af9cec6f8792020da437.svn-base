package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class OrdenarAsientosAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		Calendar desdeEjercicio = DateUtils.getDesdeEjercicio(renderRequest, entidad);
		Calendar hastaEjercicio = DateUtils.getHastaEjercicio(renderRequest, entidad);

		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeEjercicio.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaEjercicio.getTime()));

		ContabilidadServiceUtil.ordenarAsientos(desdeEjercicio.getTime(),
				hastaEjercicio.getTime(), entidad);
		List<Asiento> asientos = AsientoServiceUtil.buscarAsientos(
				desdeEjercicio.getTime(), hastaEjercicio.getTime(),entidad);

		session.removeAttribute(WebKeysTesoreria.BUSQUEDA_ASIENTOS_EN_SESSION);
		session.setAttribute(WebKeysTesoreria.BUSQUEDA_ASIENTOS_EN_SESSION, asientos);
		
		renderRequest.setAttribute("fecha_cierre_asientos",
				ContabilidadServiceUtil.getFechaCierreAsientos(entidad));

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.asientos_search_result"));
	}

}

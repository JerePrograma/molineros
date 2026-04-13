package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public abstract class BusquedaAsientosBaseAction extends PortletAction {
	protected void buscarAsientos(RenderRequest renderRequest, int entidad)
			throws SystemException {

		PortletSession portletSession = renderRequest.getPortletSession();
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar desdeEjercicio = DateUtils.getDesdeEjercicio(renderRequest, entidad);
		Calendar hastaEjercicio = DateUtils.getHastaEjercicio(renderRequest, entidad);
		
		String ejercicio=ParamUtil.getString(renderRequest, "ejercicio");
		
		portletSession.removeAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
		portletSession.setAttribute("ejercicio_seleccionado", ejercicio, PortletSession.PORTLET_SCOPE);

		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeEjercicio.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaEjercicio.getTime()));

		String periodo = renderRequest.getParameter("periodo");
		if (!periodo.equals("-1")) {
			int periodoInt = Integer.parseInt(periodo);
			periodoInt--;
			desdeEjercicio.set(Calendar.MONTH, periodoInt);
			hastaEjercicio.set(Calendar.MONTH, periodoInt);
			hastaEjercicio.set(Calendar.DATE, hastaEjercicio.getActualMaximum(Calendar.DATE));
			if (periodoInt > Calendar.JULY && entidad!=WebKeysGlobal.AMTIMA) {
				hastaEjercicio.set(Calendar.YEAR,
						desdeEjercicio.get(Calendar.YEAR));
			} else if (periodoInt > Calendar.JUNE && entidad==WebKeysGlobal.AMTIMA) {
				hastaEjercicio.set(Calendar.YEAR,
						desdeEjercicio.get(Calendar.YEAR));
			}else{
				desdeEjercicio.set(Calendar.YEAR,
						hastaEjercicio.get(Calendar.YEAR));
			}
		}

		if (!ContabilidadServiceUtil.isAsientosOrdenados(
				desdeEjercicio.getTime(), hastaEjercicio.getTime(), entidad)) {
			renderRequest.setAttribute("ejercicio_desordenado",
					"ejercicio_desordenado");
		}
		
		portletSession.setAttribute("fecha_cierre_asientos",
				ContabilidadServiceUtil.getFechaCierreAsientos(entidad), PortletSession.APPLICATION_SCOPE);

		List<Asiento> asientos = AsientoServiceUtil.buscarAsientos(
				desdeEjercicio.getTime(), hastaEjercicio.getTime(), entidad);
		
		portletSession.removeAttribute(WebKeysTesoreria.BUSQUEDA_ASIENTOS_EN_SESSION, PortletSession.APPLICATION_SCOPE);			
		portletSession.setAttribute(WebKeysTesoreria.BUSQUEDA_ASIENTOS_EN_SESSION, asientos,PortletSession.APPLICATION_SCOPE);
		
	}
}

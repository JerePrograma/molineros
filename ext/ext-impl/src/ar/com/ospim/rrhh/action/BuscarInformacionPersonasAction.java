/**
 */
package ar.com.ospim.rrhh.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.rrhh.beans.RegistroAcceso;
import ar.com.ospim.rrhh.services.RegistroAccesoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarInformacionPersonasAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda información de personas
 * 
 * @author Carlos Rivas
 * 
 */
public class BuscarInformacionPersonasAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarInformacionPersonasAction.class);
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.rrhh.informacion.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			
			String periodicidad = ParamUtil.getString(renderRequest, "periodicidad");
			String verDetalle = ParamUtil.getString(renderRequest, "verDetalle");
			String periodoDesdeMesAnio = ParamUtil.getString(renderRequest, "periodoDesdeMesAnio");
			String periodoSemana = ParamUtil.getString(renderRequest, "periodoSemana");			
					
			Date fecha = null;
			Date fechaDesde = null;
			Date fechaHasta = null;
						
			if (periodicidad.equalsIgnoreCase("mes")) {
				try {
					String[] periodoDesdeSplit = null;
					if (periodoDesdeMesAnio.length() > 0) {
						 periodoDesdeSplit = periodoDesdeMesAnio.split("_");
					}					
					fecha = formatoDePeriodos.parse(Integer
							.parseInt(periodoDesdeSplit[0])
							+ 1 + "/" + periodoDesdeSplit[1]);
				} catch (Exception e) {
					fecha = null;
				}
				fechaDesde = DateUtils.getFirstDateOfMonth(fecha, true);
				fechaHasta = DateUtils.getLastDateOfMonth(fecha, true);
			} else if (periodicidad.equalsIgnoreCase("semana")) {
				try {
					fecha = formatoDeFechas.parse(periodoSemana);
				} catch (Exception e) {				
				}
				fechaDesde = DateUtils.getFirstDateOfWeek(fecha, true);
				fechaHasta = DateUtils.getLastDateOfWeek(fecha, true);
			}
			
			String id_tarjeta_acceso = ParamUtil.getString(renderRequest,
			"persona", null);
			
			List<RegistroAcceso> busqueda = null;
			
			if (id_tarjeta_acceso != null && id_tarjeta_acceso.length() > 0) {
				busqueda = RegistroAccesoServiceUtil
				.buscarInformacionUsuario(fechaDesde, fechaHasta, id_tarjeta_acceso, Boolean.valueOf(verDetalle));
			} else {
				busqueda = RegistroAccesoServiceUtil
				.buscarInformacionUsuarios(fechaDesde, fechaHasta, Boolean.valueOf(verDetalle));
			}
			
			renderRequest.removeAttribute(WebKeysGlobal.BUSQUEDA_LECTURAS);
			renderRequest.setAttribute(WebKeysGlobal.BUSQUEDA_LECTURAS,
					busqueda);
			
			PortletSession portletSession = renderRequest.getPortletSession();
			
			portletSession.removeAttribute(
					WebKeysGlobal.BUSQUEDA_LECTURAS,
					PortletSession.PORTLET_SCOPE);
			portletSession.setAttribute(
					WebKeysGlobal.BUSQUEDA_LECTURAS, busqueda,
					PortletSession.PORTLET_SCOPE);

		} catch (Exception e) {
			_log.error(e);
		}

		return mapping
				.findForward("portlet.rrhh.informacion.result.search");
	}
}
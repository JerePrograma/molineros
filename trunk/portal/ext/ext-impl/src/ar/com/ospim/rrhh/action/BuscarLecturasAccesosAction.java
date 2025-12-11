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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarLecturasAccesosAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de lecturas de acceso según parámetros de entrada
 * 
 * @author Carlos Rivas
 * 
 */
public class BuscarLecturasAccesosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarLecturasAccesosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.rrhh.lecturas.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			String entidad = ParamUtil
					.getString(renderRequest, "entidad", null);
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			String fechaDesdeDia = ParamUtil.getString(renderRequest,
					"fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest,
					"fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(renderRequest,
					"fechaDesdeAnio");
			Date fechaDesde = null;
			try {
				fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechaDesde = null;
			}
			String fechaHastaDia = ParamUtil.getString(renderRequest,
					"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,
					"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,
					"fechaHastaAnio");
			Date fechaHasta = null;
			try {
				fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechaHasta = null;
			}

			String id_tarjeta_acceso = ParamUtil.getString(renderRequest,
			"id_tarjeta_acceso", null);
			
			List<RegistroAcceso> busqueda = RegistroAccesoServiceUtil
					.buscarLecturasAcceso(fechaDesde, fechaHasta, id_tarjeta_acceso);
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
				.findForward("portlet.rrhh.lecturas.result.search");
	}
}
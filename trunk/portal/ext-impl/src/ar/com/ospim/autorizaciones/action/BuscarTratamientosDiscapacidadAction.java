/**
 */

package ar.com.ospim.autorizaciones.action;

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

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarTratamientosDiscapacidadAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarTratamientosDiscapacidadAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.discapacidad.result.search");
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

			int numero = ParamUtil.getInteger(renderRequest, "numero", 0);

			String codPrest = ParamUtil.getString(renderRequest, "codPrest", null);
			String codPrestaci = ParamUtil.getString(renderRequest, "codPrestaci", null);
			String prestador = ParamUtil.getString(renderRequest, "prestador",
					null);

			if("undefined".equalsIgnoreCase(codPrestaci)) codPrestaci=null;
			if("undefined".equalsIgnoreCase(codPrest)) codPrest=null;
			if("undefined".equalsIgnoreCase(prestador)) prestador=null;
			
			int estado = ParamUtil.getInteger(renderRequest, "estado", 0);

			PortletSession portletSession = renderRequest.getPortletSession();

			int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
			int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
			String cuil_titular = ParamUtil.getString(renderRequest,
					"cuil_titular", null);

			List<TratamientoDiscapacidad> busqueda = TratamientoDiscapacidadServiceUtil
					.buscarTratamientosDiscapacidad(entidad, fechaDesde, fechaHasta, nroAfi, inte, cuil_titular, 0, 0, codPrest, prestador,
							numero, estado, codPrestaci);
			renderRequest
					.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD);
			renderRequest.setAttribute(
					WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD,
					busqueda);

			portletSession
					.removeAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD);
			portletSession.setAttribute(
					WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD,
					busqueda);

		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.autorizaciones.discapacidad.popup.result.search");
	}
}
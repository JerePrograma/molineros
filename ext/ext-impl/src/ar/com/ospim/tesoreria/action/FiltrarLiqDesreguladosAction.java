package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class FiltrarLiqDesreguladosAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(FiltrarLiqDesreguladosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
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
		String id_terc = ParamUtil.getString(renderRequest, "id_terc", null);

				
		List<ConsolidadoLiquidaciones> liquidaciones = LiquidaDesreguladosServiceUtil
				.getConsolidadoLiquidaciones(id_terc, fechaDesde,
						fechaHasta);
		renderRequest.setAttribute("consolidadoLiquidacionesDesregulados",
					liquidaciones);
		
		String successMessage = ParamUtil.getString(renderRequest,
				"successMessage");
		SessionMessages.add(renderRequest, "request_processed", successMessage);

		return mapping
				.findForward("portlet.tesoreria.filtrar.desregulados.result");
	}

}

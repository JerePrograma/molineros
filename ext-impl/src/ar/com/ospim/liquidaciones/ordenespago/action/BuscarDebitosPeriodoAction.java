package ar.com.ospim.liquidaciones.ordenespago.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTercero;
import ar.com.ospim.liquidaciones.services.LiquidacionDebitoTerceroServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarDebitosPeriodoActions.java.html"><b><i>View Source</i></b></a>
 * <p>
 * 
 * @author Carlos Rivas
 * 
 */

public class BuscarDebitosPeriodoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarDebitosPeriodoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.liquidaciones.buscar_debitos_periodo.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {			
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
			"dd/MM/yyyy");
						
			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
					"periodoDesdeMesAnio");
			Date periodoDesde = null;
			try {
				periodoDesde = formatoDePeriodos.parse(Integer
						.parseInt(periodoDesdeMesAnio.substring(0, 1))
						+ 1 + "/" + periodoDesdeMesAnio.substring(2, 6));
			} catch (Exception e) {
				periodoDesde = null;
			}
			if (periodoDesde == null) {
				try {
					periodoDesde = formatoDePeriodos.parse(Integer
							.parseInt(periodoDesdeMesAnio.substring(0, 2))
							+ 1 + "/" + periodoDesdeMesAnio.substring(3, 7));
				} catch (Exception e) {
					periodoDesdeMesAnio = null;
				}
			}
			
			String periodoHastaMesAnio = ParamUtil.getString(renderRequest,
					"periodoHastaMesAnio");
			Date periodoHasta = null;
			try {
				periodoHasta = formatoDePeriodos.parse(Integer
						.parseInt(periodoHastaMesAnio.substring(0, 1))
						+ 1 + "/" + periodoHastaMesAnio.substring(2, 6));
			} catch (Exception e) {
				periodoHasta = null;
			}
			if (periodoHasta == null) {
				try {
					periodoHasta = formatoDePeriodos.parse(Integer
							.parseInt(periodoHastaMesAnio.substring(0, 2))
							+ 1 + "/" + periodoHastaMesAnio.substring(3, 7));
				} catch (Exception e) {
					periodoHastaMesAnio = null;
				}
			}
			
			int pendientes = ParamUtil.getInteger(renderRequest, "pendiente", 0);
			List<LiquidacionDebitoTercero> lista = new ArrayList<LiquidacionDebitoTercero>();			
			if (pendientes != 1 ) {			
				lista = LiquidacionDebitoTerceroServiceUtil.getLiquidacionesDebitosTerceros(
					periodoDesde, periodoHasta);
				renderRequest.setAttribute("pendientes", "0");
			} else {
				lista = LiquidacionDebitoTerceroServiceUtil.getLiquidacionesDebitosTercerosPendientes();
				renderRequest.setAttribute("pendientes", "1");
			}							
			renderRequest.setAttribute(
				WebKeysLiquidaciones.BUSQUEDA_LIQUIDACIONES_DEBITOS_TERCEROS, lista);
			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.liquidaciones.buscar_debitos_periodo.result.search");
	}
}
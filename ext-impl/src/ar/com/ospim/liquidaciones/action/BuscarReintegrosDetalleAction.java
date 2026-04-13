/**
 */

package ar.com.ospim.liquidaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacion;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarReintegrosDetalleAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de reintegros detallados según parámetros de entrada
 * 
 * @author Carlos Rivas
 * 
 */
public class BuscarReintegrosDetalleAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarReintegrosAction.class);
 
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.liquidaciones.reintegros_detalle.result.search");
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
			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
					"periodoDesdeMesAnio");
			Date periodoDesde = null;
			try {
				String[] periodoDesdeSplit = null;
				if (periodoDesdeMesAnio.length() > 0) {
					 periodoDesdeSplit = periodoDesdeMesAnio.split("_");
				}					
				periodoDesde = formatoDePeriodos.parse(Integer
						.parseInt(periodoDesdeSplit[0])
						+ 1 + "/" + periodoDesdeSplit[1]);
			} catch (Exception e) {
				periodoDesde = null;
			}
			String periodoHastaMesAnio = ParamUtil.getString(renderRequest,
					"periodoHastaMesAnio");
			
			Date periodoHasta = null;
			try {
				String[] periodoHastaSplit = null;
				if (periodoHastaMesAnio.length() > 0) {
					 periodoHastaSplit = periodoHastaMesAnio.split("_");
				}
				periodoHasta = formatoDePeriodos.parse(Integer
						.parseInt(periodoHastaSplit[0])
						+ 1 + "/" + periodoHastaSplit[1]);			
				} catch (Exception e) {
				periodoHasta = null;
			}

			int seccional = ParamUtil.getInteger(renderRequest, "id_seccional_r", 0);
			int numero = ParamUtil.getInteger(renderRequest, "numero", 0);

			String tipo_reintegro = ParamUtil.getString(renderRequest,
					"tipo_reintegro", WebKeysLiquidaciones.REINTEGRO_PRE);
			
			int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
			int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
			String cuil_titular = ParamUtil.getString(renderRequest,
					"cuil_titular");
			
			int estado = ParamUtil.getInteger(renderRequest, "estado", 0);
			
			String pagos = ParamUtil.getString(renderRequest, "pagos", "0");
			
			String alta_usr = ParamUtil.getString(renderRequest, "alta_usr", "");
			String codPrest = ParamUtil.getString(renderRequest, "codPrest",
					null);
			PortletSession portletSession = renderRequest.getPortletSession();
			if (tipo_reintegro
					.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {

 				List<Reintegro> busqueda = ReintegroServiceUtil
						.buscarReintegros(entidad, fechaDesde, fechaHasta,
								periodoDesde, periodoHasta, codPrest, nroAfi,
								inte, cuil_titular, 
								seccional, numero, pagos, alta_usr);				
				renderRequest
						.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
				renderRequest.setAttribute(
						WebKeysLiquidaciones.BUSQUEDA_REINTEGRO,  busqueda);
				if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
					seccional = busqueda.get(0).getId_seccional();
				}
				renderRequest.setAttribute(
						WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, seccional);				
				portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, busqueda, PortletSession.PORTLET_SCOPE);
				
				portletSession.removeAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, seccional, PortletSession.PORTLET_SCOPE);

			} else if (tipo_reintegro
					.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
				codPrest = null;
				List<Reintegro> busqueda = ReintegroServiceUtil
						.buscarReintegrosOdoProtesis(entidad, fechaDesde, fechaHasta,
								periodoDesde, periodoHasta, codPrest, nroAfi,
								inte, cuil_titular, 
								seccional, numero, pagos, alta_usr, estado);				
				renderRequest
						.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);				
				renderRequest.setAttribute(
						WebKeysLiquidaciones.BUSQUEDA_REINTEGRO,  busqueda);
				if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
					seccional = busqueda.get(0).getId_seccional();
				}
				renderRequest.setAttribute(
						WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, seccional);

				portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, busqueda, PortletSession.PORTLET_SCOPE);
				
				portletSession.removeAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, seccional, PortletSession.PORTLET_SCOPE);

			} else if (tipo_reintegro
					.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
				codPrest = null;
				List<Reintegro> busqueda = ReintegroServiceUtil
						.buscarReintegrosOdoOrto(entidad, fechaDesde, fechaHasta,
								periodoDesde, periodoHasta, codPrest, nroAfi,
								inte, cuil_titular, 
								seccional, numero, pagos, alta_usr, estado);
				renderRequest
					.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
				renderRequest.setAttribute(
						WebKeysLiquidaciones.BUSQUEDA_REINTEGRO,  busqueda);
				if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
					seccional = busqueda.get(0).getId_seccional();
				}
				renderRequest.setAttribute(
						WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, seccional);
		
				portletSession
						.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
				portletSession.setAttribute(
						WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, busqueda);
				
				portletSession.removeAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.REINTEGRO_DE_SECCIONAL, seccional, PortletSession.PORTLET_SCOPE);
			}
			else {
				List<ReintegroPrestacion> busqueda = new ArrayList<ReintegroPrestacion>();
				renderRequest
						.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO);
				
				renderRequest.setAttribute(
						WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, busqueda);
				
				portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.BUSQUEDA_REINTEGRO, busqueda, PortletSession.PORTLET_SCOPE);				

			}
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.liquidaciones.reintegros_detalle.result.search");
	}
}
package ar.com.ospim.liquidaciones.action;

import java.math.BigDecimal;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacionOdo;
import ar.com.ospim.liquidaciones.services.BusquedaLiquidacionServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarLiquidacionesAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de liquidaciones según parámetros de entrada
 * 
 * @author Carlos Rivas
 * 
 */
public class BuscarLiquidacionesAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarLiquidacionesAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.liquidaciones.liquidaciones.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			//Borro primero posibles daots de la sesión 
			HttpServletRequest httpServletRequest = PortalUtil
			.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();			
			session.removeAttribute("cuil_titular_servicio");
			session.removeAttribute("inte_servicio");
			session.removeAttribute("servicio");
			session.removeAttribute("fecha_prestacion_servicio");
			
			List<Concepto> conceptos = TraeListasServiceUtil.getConceptoLiquidacion(renderRequest);
			
			String entidad = ParamUtil.getString(renderRequest, "entidad", null);
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
			String fechaDesdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
			Date fechaDesde = null;
			try {
				fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechaDesde = null;
			}
			String fechaHastaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
			Date fechaHasta = null;
			try {
				fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechaHasta = null;
			}
			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,"periodoDesdeMesAnio");
			Date periodoDesde = null;
			try {
				String[] periodoDesdeMesAnioParts = periodoDesdeMesAnio.split("_") ;
				periodoDesde = formatoDePeriodos.parse(Integer
						.parseInt(periodoDesdeMesAnioParts[0])
						+ 1 + "/" + periodoDesdeMesAnioParts[1]);
			} catch (Exception e) {
				periodoDesde = null;
			}
			String periodoHastaMesAnio = ParamUtil.getString(renderRequest,"periodoHastaMesAnio");
			Date periodoHasta = null;
			try {
				String[] periodoHastaMesAnioParts = periodoHastaMesAnio.split("_") ;
				periodoHasta = formatoDePeriodos.parse(Integer
						.parseInt(periodoHastaMesAnioParts[0])
						+ 1 + "/" + periodoHastaMesAnioParts[1]);
			} catch (Exception e) {
				periodoHasta = null;
			}
						
			int numero = ParamUtil.getInteger(renderRequest, "numero", 0);
			
			String tipo_liquidacion = ParamUtil.getString(renderRequest,
					"tipo_liquidacion", WebKeysLiquidaciones.LIQUIDACION_PRE);

			int codPrest = ParamUtil.getInteger(renderRequest, "codPrest",0);
			int id_prestador = ParamUtil.getInteger(renderRequest,"id_prestador", 0);
			String cuit = ParamUtil.getString(renderRequest, "cuit", null);
			String prestador = ParamUtil.getString(renderRequest,"prestador", null);
			
			int estado =  ParamUtil.getInteger(renderRequest, "estado", 0);
			int id_orden_compra =  ParamUtil.getInteger(renderRequest, "nro_oc", 0);
			Integer sector= ParamUtil.getInteger(renderRequest, "sector", -1);
			
			PortletSession portletSession = renderRequest.getPortletSession();
			
			if (tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_PRE)) {
				
				BusquedaLiquidacionServiceUtil.getInstance();
				
				String tipo_compro = ParamUtil.getString(renderRequest, "comprobante_tipo", null);
				String letra_compro = ParamUtil.getString(renderRequest, "comprobante_letra", null);
				int sucu = ParamUtil.getInteger(renderRequest, "sucu", 0);
				String nro_compro = ParamUtil.getString(renderRequest, "comprobante_nro", null);				
				
 				List<Liquidacion> busqueda = BusquedaLiquidacionServiceUtil
						.getBusquedaLiquidaciones(entidad, fechaDesde,
								fechaHasta, periodoDesde, periodoHasta,
								codPrest, id_prestador, cuit, prestador,
								numero, tipo_compro, letra_compro, sucu,
								nro_compro, estado, id_orden_compra,sector);
 				
				renderRequest.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION);
				
				renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, busqueda);
				
				portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, busqueda, PortletSession.PORTLET_SCOPE);				

				if (id_prestador == 0 && numero != 0 && busqueda.size() == 1) {
					id_prestador = busqueda.get(0).getId_prestador();
				}

				renderRequest.removeAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION);
		
				renderRequest.setAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION, id_prestador);

				portletSession.removeAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.PRESTADOR_DE_LIQUIDACION, id_prestador, PortletSession.PORTLET_SCOPE);				

			} else if (tipo_liquidacion.equalsIgnoreCase(WebKeysLiquidaciones.LIQUIDACION_ODO)) {

				int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
				int seccional = ParamUtil.getInteger(renderRequest,"id_seccional", 0);
				int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi",0);
				String cuil_titular = ParamUtil.getString(renderRequest, "cuil_titular", null);

				String presupuesto = ParamUtil.getString(renderRequest, "presupuesto", null);
			
				BusquedaLiquidacionServiceUtil.getInstance();
				List<LiquidacionPrestacionOdo> busqueda = BusquedaLiquidacionServiceUtil
						.getBusquedaLiquidacionesOdo(entidad, fechaDesde,
								fechaHasta, periodoDesde, periodoHasta, nroAfi,
								inte, cuil_titular, seccional, new BigDecimal(
										presupuesto), numero);
				renderRequest.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION);
				renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, busqueda);
				
				portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, busqueda, PortletSession.PORTLET_SCOPE);								
				
			} else {
				List<LiquidacionPrestacion> busqueda = new ArrayList<LiquidacionPrestacion>();
				renderRequest.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION);
				renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, busqueda);
				
				portletSession.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute(WebKeysLiquidaciones.BUSQUEDA_LIQUIDACION, busqueda, PortletSession.PORTLET_SCOPE);								
			}
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping.findForward("portlet.liquidaciones.liquidaciones.result.search");
	}
}
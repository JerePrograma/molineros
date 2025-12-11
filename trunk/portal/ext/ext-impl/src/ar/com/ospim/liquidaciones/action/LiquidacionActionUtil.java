/**
 */

package ar.com.ospim.liquidaciones.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacionAjuste;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="LiquidacionActionUtil"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class LiquidacionActionUtil {

	public static void getLiquidacionEntry(HttpServletRequest request)
			throws Exception {
		int id_liquidacion_att = request
				.getAttribute(WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION) == null ? 0
				: (Integer) request
						.getAttribute(WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION);
		int id_liquidacion = ParamUtil.getInteger(request, "id_liquidacion", 0) != 0 ? ParamUtil
				.getInteger(request, "id_liquidacion", 0)
				: id_liquidacion_att;
		int paga = ParamUtil.getInteger(request, "paga", 0);
		int ajustar = ParamUtil.getInteger(request, "ajustar", 0);
		Liquidacion liquidacionEntry = null;
		List<LiquidacionPrestacion> liquidacionPrestaciones = new ArrayList<LiquidacionPrestacion>();
		HttpSession session = (HttpSession) request.getSession();
		if (id_liquidacion != 0) {
			liquidacionEntry = EditarLiquidacionServiceUtil
					.getLiquidacionEntry(id_liquidacion);
			liquidacionPrestaciones = liquidacionEntry
					.getLiquidacionPrestacion();			
			if (ajustar == 1) {
				session.removeAttribute("lista_ajustes_prestaciones");
				session.setAttribute("lista_ajustes_prestaciones",
						new ArrayList<LiquidacionPrestacionAjuste>());
			}
			if (paga == 1) {
				ArrayList<LiquidacionPrestacionAjuste> listaPrestacionAjuste = (ArrayList<LiquidacionPrestacionAjuste>) session
						.getAttribute("lista_ajustes_prestaciones");
				filtraListaPrestacionesDadosAjustes(liquidacionPrestaciones,
						listaPrestacionAjuste);
			}
		} else {
			session.removeAttribute("cuil_titular_servicio");
			session.removeAttribute("inte_servicio");
			session.removeAttribute("servicio");
			session.removeAttribute("fecha_prestacion_servicio");
			if(liquidacionEntry!=null) {
			  liquidacionEntry.setCompro_a_debitar_letra("");
			  liquidacionEntry.setCompro_a_debitar_numero("");
			  liquidacionEntry.setCompro_a_debitar_tipo("");
			  liquidacionEntry.setSucu(0);
			}  
		}
		request.setAttribute("paga", paga == 1 ? "1" : "0");
		request.setAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION,
				liquidacionEntry);
		request.setAttribute(
				WebKeysLiquidaciones.LIQUIDACION_PRESTACIONES_EN_EDICION,
				liquidacionPrestaciones);
	}

	/*
	 * recorre la lista de ajustes, por cada add se lo añade a la lista de
	 * prestaciones por cada edit se busca el item en la lista de prestaciones y
	 * se reemplaza por cada delete se borra el item de la lista de prestaciones
	 */
	public static void filtraListaPrestacionesDadosAjustes(
			List<LiquidacionPrestacion> liquidacionPrestaciones,
			ArrayList<LiquidacionPrestacionAjuste> listaPrestacionAjuste) {
		for (LiquidacionPrestacionAjuste liquidacionPrestacionAjustei : listaPrestacionAjuste) {
			if (liquidacionPrestacionAjustei.getAjuste().equals("ADD")) {
				liquidacionPrestaciones.add(liquidacionPrestacionAjustei);
			} else if (liquidacionPrestacionAjustei.getAjuste().equals("EDIT")) {
				for (LiquidacionPrestacion liquidacionPrestacion : liquidacionPrestaciones) {
					if (liquidacionPrestacion.getOrden() == liquidacionPrestacionAjustei
							.getOrden()) {
						liquidacionPrestaciones.remove(liquidacionPrestacion);
						liquidacionPrestaciones
								.add(liquidacionPrestacionAjustei);
						break;
					}
				}
			} else if (liquidacionPrestacionAjustei.getAjuste()
					.equals("DELETE")) {
				for (LiquidacionPrestacion liquidacionPrestacion : liquidacionPrestaciones) {
					if (liquidacionPrestacion.getOrden() == liquidacionPrestacionAjustei
							.getOrden()) {
						liquidacionPrestaciones.remove(liquidacionPrestacion);
						break;
					}
				}
			}
		}
	}

	public static void getLiquidacionEntry(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getLiquidacionEntry(request);
	}

	public static void getLiquidacionEntry(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getLiquidacionEntry(request);
	}

	public static Liquidacion getLiquidacionEntry(int id_liquidacion)
			throws Exception {

		Liquidacion liquidacionEntry = null;
		List<LiquidacionPrestacion> liquidacionPrestaciones = new ArrayList<LiquidacionPrestacion>();

		if (id_liquidacion != 0) {
			liquidacionEntry = EditarLiquidacionServiceUtil
					.getLiquidacionEntry(id_liquidacion);
			liquidacionPrestaciones = liquidacionEntry
					.getLiquidacionPrestacion();
			liquidacionEntry.setLiquidacionPrestacion(liquidacionPrestaciones);
			liquidacionEntry.getImporteTotal();
		}
		return liquidacionEntry;
	}
}
/**
 */

package ar.com.ospim.liquidaciones.action;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTercero;
import ar.com.ospim.liquidaciones.services.LiquidacionDebitoTerceroServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="LiquidacionDebitosActionUtil"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class LiquidacionDebitosActionUtil {

	public static void getLiquidacionDebitosEntry(HttpServletRequest request)
			throws Exception {
		
		int id_liquidacion = ParamUtil.getInteger(request, "id_liquidacion", 0);
		LiquidacionDebitoTercero liquidacionEntry = null;
		
		if (id_liquidacion != 0) {
			liquidacionEntry = LiquidacionDebitoTerceroServiceUtil.getLiquidacionesDebitosTerceros(
					id_liquidacion);			
		}		
		request.setAttribute(WebKeysLiquidaciones.LIQUIDACION_EN_EDICION,
				liquidacionEntry);
	}
	
	public static void getLiquidacionDebitosEntry(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getLiquidacionDebitosEntry(request);
	}

	public static void getLiquidacionEntry(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getLiquidacionDebitosEntry(request);
	}
	
	public static LiquidacionDebitoTercero getLiquidacionDebitosEntry(int id_liquidacion)
			throws Exception {
		
		LiquidacionDebitoTercero liquidacionEntry = null;		

		if (id_liquidacion != 0) {
			liquidacionEntry = LiquidacionDebitoTerceroServiceUtil.getLiquidacionesDebitosTerceros(id_liquidacion);
		}
		return liquidacionEntry;
	}
}
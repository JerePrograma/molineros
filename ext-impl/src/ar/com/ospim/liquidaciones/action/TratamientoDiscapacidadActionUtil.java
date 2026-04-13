/**
 */

package ar.com.ospim.liquidaciones.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacion;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="TratamientoDiscapacidadActionUtil"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class TratamientoDiscapacidadActionUtil {

	public static void getTratamientoEntry(HttpServletRequest req)
			throws Exception {

		Reintegro reintegro = (Reintegro) req
				.getAttribute(WebKeysLiquidaciones.REINTEGRO_EN_EDICION);
		
		String tipo_reintegro = ParamUtil.getString(req,
				"tipo_reintegro");
		
		if (reintegro == null) {
			req.getSession().removeAttribute(WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_EN_EDICION);
			
			int idReintegroAtt = req
					.getAttribute(WebKeysLiquidaciones.ID_REINTEGRO_EN_EDICION) == null ? 0
					: (Integer) req
							.getAttribute(WebKeysLiquidaciones.ID_REINTEGRO_EN_EDICION);
			int idReintegro = ParamUtil.getInteger(req, "id_reintegro", 0) != 0 ? ParamUtil
					.getInteger(req, "id_reintegro", 0)
					: idReintegroAtt;
					
			List<ReintegroPrestacion> reintPrest = new ArrayList<ReintegroPrestacion>();						
			
			if (idReintegro != 0) {
				reintegro = ReintegroServiceUtil.getReintegroEntry(idReintegro);
				reintPrest = reintegro.getReintegroPrestacion();
			}

			req.setAttribute(WebKeysLiquidaciones.REINTEGRO_EN_EDICION,
					reintegro);
			req.getSession().setAttribute(
					WebKeysLiquidaciones.REINTEGRO_PRESTACIONES_EN_EDICION,
					reintPrest);
		}
		if (tipo_reintegro == null || (tipo_reintegro != null && tipo_reintegro.length() == 0)) {
			tipo_reintegro = (String) (req.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION) != null ?
					req.getAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION) : WebKeysLiquidaciones.REINTEGRO_PRE);
		}
		req.setAttribute(WebKeysLiquidaciones.TIPO_REINTEGRO_EN_EDICION,
				tipo_reintegro);
	}

	public static void getTratamientoEntry(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getTratamientoEntry(request);
	}

	public static void getTratamientoEntry(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getTratamientoEntry(request);
	}
}
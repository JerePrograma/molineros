package ar.com.ospim.global.actions;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;

/**
 * Trae todos los conceptos que tengan validez en algun momento de las fechas
 * desde-hasta, pero NO necesariamente tendran validez en TODO ese periodo,
 * simplemente tienen solapamientos parciales
 * 
 * @author martin
 * 
 */
public class TraeConceptosEgresoParaPeriodoAction extends
		TraeConceptosJSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int entidad=ParamUtil.getInteger(req, "entidad");		

		try {
			Date fechaDesde = getDesde(req);
			Date fechaFin = getHasta(req);

			List<Concepto> conceptos = TraeListasServiceUtil
					.getConceptosEgresoValidosDentroDe(fechaDesde, fechaFin, entidad);
			return getConceptosJSON(conceptos, true);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}
}

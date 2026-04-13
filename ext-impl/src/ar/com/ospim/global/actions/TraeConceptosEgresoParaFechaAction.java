package ar.com.ospim.global.actions;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;

import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;

/**
 * Trae todos los conceptos que tengan validez en la fecha determinada
 * 
 * @author martin
 * 
 */
public class TraeConceptosEgresoParaFechaAction extends
		TraeConceptosJSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		try {
			int entidad=ParamUtil.getInteger(req, "entidad");
			Date fecha = getFecha(req);

			List<Concepto> conceptos = TraeListasServiceUtil
					.getConceptoEgresos(fecha, entidad);
			return getConceptosJSON(conceptos, false);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}
}

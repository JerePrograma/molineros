package ar.com.ospim.tesoreria.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

/**
 * @author martin
 * 
 */
public class EliminarDetalleAsientoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		try {			
			HttpSession session = req.getSession();
			Asiento asiento = (Asiento) session
					.getAttribute(WebKeysTesoreria.ASIENTO_EN_SESSION);
			String detalleId = req.getParameter("detalle_id");
			Detalle detalle = new Detalle(Integer.parseInt(detalleId));
			int indexOf = asiento.getDetalle().indexOf(detalle);
			detalle = asiento.getDetalle().get(indexOf);
			if (asiento.getDetalleBajasLogicas() == null) {
				asiento.setDetalleBajasLogicas(new ArrayList<Detalle>());
			}
			asiento.getDetalleBajasLogicas().add(detalle);
			asiento.getDetalle().remove(detalle);
			return "{\"status\":\"ok\"}";
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}
}

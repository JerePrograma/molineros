package ar.com.ospim.hoteles.action;

import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Factura;

public class BuscarReservasVigentesHotel extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String ptoVtaAfip="00030";
		User user = PortalUtil.getUser(req);
		try{
			ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
		}catch(Exception e){
			ptoVtaAfip="00030";
		}

		Integer idReserva = Integer.parseInt(StringUtils.checkNotEmpty(req.getParameter("id_reserva"))  ? req.getParameter("id_reserva") : "0" );

		String resultado = "{}";
 		
 		Calendar fecha = CalendarFactoryUtil.getCalendar(); 		
 		
		Integer totalConsumo = HotelesServiceUtil.getTotalConsumosPorReserva(idReserva);
		
		Integer totalReserva = HotelesServiceUtil.getTotalReserva(fecha.get(Calendar.YEAR), idReserva);
		
		HttpSession session = (HttpSession) req.getSession();
		
		Factura factura = (Factura)session.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
		List<Recibo>recibos=HotelesServiceUtil.getReciboByReserva(ptoVtaAfip, idReserva, fecha.get(Calendar.YEAR));
		factura.setRecibosAdelantos(recibos);
		session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION, factura);
		
		resultado = "{ \"consumos\" : \"" 
				    + totalConsumo
				    + "\",\"reserva\" : \""
				    + totalReserva
			        + "\" }";
		
		return resultado;
		
		
	}
}
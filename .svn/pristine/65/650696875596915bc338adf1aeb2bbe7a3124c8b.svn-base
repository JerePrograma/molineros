package ar.com.ospim.hoteles.action;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;

import ar.com.global.services.ComanderaService;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Consumo;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Factura;

public class HotelesRecuperaReservaJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		DecimalFormat df = new DecimalFormat("#.00");
		Formatter fmt = new Formatter();
		User user = PortalUtil.getUser(req);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar cal= Calendar.getInstance();
		int year= cal.get(Calendar.YEAR);
		Integer anio = Integer.parseInt(req.getParameter("anio"));
		Integer reservaId = Integer.parseInt(req.getParameter("reserva"));
		
		Reserva reserva=HotelesServiceUtil.getReservaById(null, anio, reservaId);
		String resultado = "{}";
		
		Double pagado=0D;
		Double totalAPagar=0D;
		Double senia=0D;
		
 		String cliente="";
 		String documento="";
 		String nombre="";
 		String fechaDde="";
 		String fechaHta="";
 		String fechaDdeId="";
 		String fechaHtaId="";
 		Integer clienteId=0;
 		
 		String ultimaAsignacionHTML="";
 		
 		String estado="ERROR";
 		
 		if(reserva!=null && reserva.getIdReserva()!=null) {
 		   	estado="OK";
 		   	cliente = reserva.getApellido()+" " + reserva.getNombre();
 		   	documento=reserva.getDocumento();
 		   	pagado=reserva.getPagado();
 		   	senia=reserva.getSenia();
 		   	fechaDde=sdf.format(reserva.getFechaDesde());
 		   	fechaHta=sdf.format(reserva.getFechaHasta());
 		   	fechaDdeId=reserva.getFechaDesdeId().toString();
 		   	fechaHtaId=reserva.getFechaHastaId().toString();
 		   	totalAPagar=reserva.getTotalAPagar();
 		   	clienteId=reserva.getIdCliente();
 		}
			 
		resultado = "{ \"cliente\" : \"" 
				    + cliente
				    + "\",\"clienteid\" : \""
				    + clienteId
				    + "\",\"pagado\" : \""
				    + "$ "+df.format(pagado)
				    + "\",\"senia\" : \""
				    + "$ "+df.format(senia)
				    + "\",\"apagar\" : \""
				    + "$ "+df.format(totalAPagar)
				    + "\",\"estado\" : \""
				    + estado
				    + "\",\"documento\" : \""
				    + documento
				    + "\",\"fechainicio\" : \""
				    + fechaDde
				    + "\",\"fechafin\" : \""
				    + fechaHta
				    + "\",\"fechainicioid\" : \""
				    + fechaDdeId
				    + "\",\"fechafinid\" : \""
				    + fechaHtaId
			        + "\" }";
		
		return resultado;
		
		
	}
	
}
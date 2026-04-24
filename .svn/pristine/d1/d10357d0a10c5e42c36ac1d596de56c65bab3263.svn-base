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
import ar.com.uoma.facturacion.BusquedaFacturasFiltro;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;

public class HotelesRecuperaFacturaJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		BusquedaFacturasFiltro filtro= new BusquedaFacturasFiltro();
		DecimalFormat df = new DecimalFormat("#.00");
		Formatter fmt = new Formatter();
		User user = PortalUtil.getUser(req);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar cal= Calendar.getInstance();
		int year= cal.get(Calendar.YEAR);
		
		String letra = req.getParameter("letra");
		String sucursal = req.getParameter("sucursal");
		String numero = req.getParameter("numero");
		String tipo = req.getParameter("tipo");
		filtro.setLetra(letra);
		filtro.setNumero(String.valueOf(Long.parseLong(numero) ));
		filtro.setSucursal(sucursal);
		filtro.setTipo(tipo);
		Factura factura=new Factura();
		List<Factura>facturas=FacturacionServiceUtil.getFacturas(filtro);
		if(!facturas.isEmpty()) {
			factura=facturas.get(0);
		}
		
		String resultado = "{}";
		
		Double pagado=0D;
		Double total=0D;
		Double senia=0D;
		
 		String cliente="";
 		String clienteNombre="";
 		String clienteDocumento="";
 		String fecha="";
 		String estado="ERROR";
 		
 		if(factura!=null && factura.getId()>0) {
 		   	estado="OK";
 		   	cliente = factura.getCliente().getDescripcionCliente();
 		   	clienteNombre=factura.getCliente().getClienteNombre();
 		   	clienteDocumento=factura.getCliente().getClienteDocumento();
 		   	pagado=0D;
 		   	fecha=sdf.format(factura.getFecha());
 		   	total=factura.getImporteTotalCalculado().doubleValue();
 		   	numero=String.format("%08d",Long.parseLong(factura.getNumero()));
 		}
 		
		resultado = "{ \"cliente\" : \"" 
				    + cliente
				    + "\",\"clienteNombre\" : \""
				    + clienteNombre
				    + "\",\"clienteDocumento\" : \""
				    + clienteDocumento
				    + "\",\"fecha\" : \"" 
				    + fecha
				    + "\",\"pagado\" : \""
				    + "$ "+df.format(pagado)
				    + "\",\"total\" : \""
				    + "$ "+df.format(total)
				    + "\",\"estado\" : \""
				    + estado
				    + "\",\"numero\" : \""
				    + numero 
				    + "\" }";
		
		return resultado;
		
		
	}
	
}
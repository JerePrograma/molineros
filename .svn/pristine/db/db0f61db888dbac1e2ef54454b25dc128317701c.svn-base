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

public class HotelesGestionProductosAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		DecimalFormat df = new DecimalFormat("#.00");
		Formatter fmt = new Formatter();
		User user = PortalUtil.getUser(req);
		
		Calendar cal= Calendar.getInstance();
		int year= cal.get(Calendar.YEAR);
		
		List<String> pie = new ArrayList<String>();	
		
		List<Habitacion> grupos = new ArrayList<Habitacion>();
		String tipo = req.getParameter("tipo");
		String ptovta = req.getParameter("ptovta");
		String idUnidad = req.getParameter("unidad");
		Integer cantidad = Integer.parseInt(req.getParameter("cantidad"));
		String cmd = req.getParameter("cmd");
		String estadoId = req.getParameter("estadoId");
		String producto = req.getParameter("producto");
		String personal = req.getParameter("idpersonal");
		Integer personalId=0;
		if(personal!=null && !"".equals(personal)) {
		  personalId=Integer.valueOf(personal);
		}
		Double total=0D;
		String resultado = "{}";
 		String consumosHTML="";
 		String ultimaAsignacionHTML="";
 		
 		String estado="";
// 		req.getSession().removeAttribute(WebKeysHoteles.ULTIMA_ASIGNACION_HABITACION);
 		List<Consumo> ultimosconsumos=new ArrayList<Consumo>();
 		if("MESAS".equalsIgnoreCase(tipo)) {
			ultimosconsumos=HotelesServiceUtil.getUltimoConsumoAsignadoHabitacion(ptovta, String.valueOf(year), Integer.parseInt(idUnidad), null); 
		}else if("HABITACIONESGRUPOS".equalsIgnoreCase(tipo)) {
		}else if("HABITACION".equalsIgnoreCase(tipo)) {
			ultimosconsumos=HotelesServiceUtil.getUltimoConsumoAsignadoHabitacion(ptovta, String.valueOf(year), null, idUnidad); 
		}
		
 		if(!ultimosconsumos.isEmpty())  {
 			ultimaAsignacionHTML="<input type='button' value='Reimprime Asignación Hab.' class='botonNegroSmall' onclick='javascript:reimprimirUltimaAsignacion();'/>";
// 			req.getSession().setAttribute(WebKeysHoteles.ULTIMA_ASIGNACION_HABITACION, ultimosconsumos);
 		}
		
		if("alta".equalsIgnoreCase(cmd)) {
			HotelesServiceUtil.actualizarConsumos(ptovta, tipo, producto, cantidad,idUnidad,personalId, user.getScreenName());
		}
		
		if("baja".equalsIgnoreCase(cmd)) {
			HotelesServiceUtil.eliminarConsumos(ptovta, tipo, producto, idUnidad, user.getScreenName());
		}
		
		if("cambiaestado".equalsIgnoreCase(cmd)) {
			HotelesServiceUtil.cambiarEstado(ptovta, tipo, producto, idUnidad, user.getScreenName(),estadoId);
		}
		
		if(cmd.equals("verificarfacturaconsumos") ){
			 Factura factura = (Factura) req.getSession().getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
			 if(factura.getCae()!=null && !"".equalsIgnoreCase(factura.getCae())) {
				 HotelesServiceUtil.insertOrdenConsumo(ptovta, tipo, Integer.parseInt(idUnidad), personalId, null,
						 user.getScreenName(),factura.getLetra()+"-"+factura.getSucursal()+"-" +fmt.format("%08d",Integer.parseInt(factura.getNumero())));
			 }
		}
		
		
		if(cmd.equals("liberar_consumos_mesas") ){
			HotelesServiceUtil.deleteConsumosActivos(ptovta, tipo, Integer.parseInt(idUnidad));
		}
		
	    ThemeDisplay themeDisplay = (ThemeDisplay)req.getAttribute(
				WebKeys.THEME_DISPLAY);
		
		List<Consumo> consumos= HotelesServiceUtil.getConsumos(ptovta, tipo,idUnidad);
		consumosHTML=HotelesServiceUtil.getConsumosHTML(ptovta, tipo,idUnidad,themeDisplay);
		
		for(Consumo c:consumos) {
			total+=c.getPrecio()*c.getCantidad();
		}
		
        if(cmd.equals("precomanda_consumos_mesas") ){ 
        	if("PCU".equalsIgnoreCase(estadoId) && !consumos.isEmpty())  {
        	   pie = new ArrayList<String>();	
     		   String datos =imprimirTicket("MESA Nro: " + idUnidad,ptovta, tipo,idUnidad,consumos,pie,1);
        	}
     	}
        
        
        if(cmd.equals("ticket_consumo_habitaciones") ){ 
        	if(!ultimosconsumos.isEmpty())  {
        		
        		String datosReserva="Reserva(HAB) "+ ultimosconsumos.get(0).getHabitacion().getDescripcion() +" - " +
        		        ultimosconsumos.get(0).getCliente().getApellido() +"(Reserva: "+ ultimosconsumos.get(0).getHabitacion().getNumero() +")";
        		pie.add("-----------------------------------------------");
        		pie.add("                   Firma                     ");	
        	    String datos =imprimirTicket(datosReserva,ptovta, tipo,idUnidad,ultimosconsumos,pie,2);
        	}    
     	}
       
		
		if("PCU".equalsIgnoreCase(estadoId) && !consumos.isEmpty())  {
		   estado=estadoId;	
//		   String datos =imprimirTicket("MESA Nro: " + idUnidad,ptovta, tipo,idUnidad,consumos,1);
		}else {
			
		  if(consumos.isEmpty()) {
			estado="LIB";
		  }else {
			if("PCU".equalsIgnoreCase(consumos.get(0).getEstado()) ){
			  estado="PCU";
			}else {
			  estado="OCU";
			}  
		  }
		}  

		
			 
		resultado = "{ \"consumos\" : \"" 
				    + consumosHTML
				    + "\",\"total\" : \""
				    + "$ "+df.format(total)
				    + "\",\"estado\" : \""
				    + estado
				    + "\",\"ultimaasignacion\" : \""
				    + ultimaAsignacionHTML
			        + "\" }";
		
		return resultado;
		
		
	}
	
	
	private String imprimirTicket(String cabecera,String ptovta,String tipo,String idUnidad,List<Consumo>consumos,List<String>pie,int copias) throws SystemException {
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		NumberFormat format2D = new DecimalFormat("##########0");
		NumberFormat forCantidad = new DecimalFormat("###0");
		
		Double total=0D;
		String datosReserva="";
		Calendar fecha = CalendarFactoryUtil.getCalendar(); 		
		fecha.setTime(new Date());
		
        
        String pathLogo = TraeListasServiceUtil.getSystemConfig("LOGO_HOTEL");
        
        File logo = new File(pathLogo);
        
        List<String> cb = new ArrayList<String>();
        List<String> cn = new ArrayList<String>();
//        List<String> pie = new ArrayList<String>();
        String linea="";

        cb.add("             COMPROBANTE DE CONSUMO          ");
        cb.add("                 "+formatoDeFechas.format(fecha.getTime()));
        cb.add("                                             ");
        cb.add(cabecera);
        cb.add("                                             ");
        cb.add("_______________________________________________");
        cb.add("PRODUCTO           CANT    $ PRECIO     $ TOTAL");
        cb.add("_______________________________________________");
//              123456789-123456789-123456789-123456789-12345  
                
        
        ComanderaService comand= new ComanderaService();
        comand.setLogo(logo);
        comand.setCabecera(cb);
        int xx=0;
        
        for(Consumo c:consumos) {
           xx= c.getProducto().getDescripcion().length()>18?18:c.getProducto().getDescripcion().length();
           linea  = String.format("%-18s",c.getProducto().getDescripcion().substring(0,xx).trim());
           linea +=  String.format("%5s",forCantidad.format( c.getCantidad()));
           linea +=  String.format("%12s",format2D.format(c.getPrecio()));
           linea +=  String.format("%12s",format2D.format(c.getPrecio()*c.getCantidad()));
           cn.add(linea);
           total += c.getPrecio()*c.getCantidad();
        }
                                                   
         cn.add("-----------------------------------------------");
        linea = "                           TOTAL $ " +String.format("%12s",format2D.format(total));
        cn.add(linea) ;    
	    comand.setCuerpo(cn);
        
	    comand.setPie(pie);
	    
	    for(int i=0;i<copias;i++) {
	       comand.imprimirTicketFactura();
	    }   
	    
	    return datosReserva;

	}

	
	
}
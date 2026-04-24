package ar.com.ospim.hoteles.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;

import ar.com.global.services.ComanderaService;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Consumo;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.Producto;
import ar.com.uoma.facturacion.services.FacturacionServiceUtil;


public class HotelesGestionComandosAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	NumberFormat format2D = new DecimalFormat("##########0");
	NumberFormat forCantidad = new DecimalFormat("###0");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
/*
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		Boolean esDatosTab = ParamUtil.getBoolean(actionRequest, "esDatosTab");
		
		if (cmd.equals(Constants.MOVE) && esDatosTab){  // cambio a solapa Lugar Atencion.
			
			PreAutorizacion preautorizacion = (PreAutorizacion) session.getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
					
			actualizaPreautorizacion(preautorizacion,PortalUtil.getHttpServletRequest(actionRequest));
   		    session.setAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION , preautorizacion);
			
		}
*/		
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		String msg = "";
		
		String tipo = renderRequest.getParameter("tipo");
		String ptovta = renderRequest.getParameter("ptovta");
		String idUnidad = renderRequest.getParameter("unidad");
		String personal = renderRequest.getParameter("idpersonal");
		
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		
		Integer personalId=0;
		if(personal!=null && !"".equals(personal)) {
		  personalId=Integer.valueOf(personal);
		}
		Double total=0D;
		
		if (!StringUtils.checkEmpty(cmd)) {
		
			if(cmd.equals("asignarconsumohabitacion") ){ 
				List<Consumo> consumos= HotelesServiceUtil.getConsumos(ptovta, tipo,idUnidad);
				session.setAttribute("esPopUp","S");
				session.setAttribute(WebKeysHoteles.CONSUMOS_HOTEL , consumos);
				session.setAttribute(WebKeysHoteles.MESA_ASIGNAR_HOTEL , idUnidad);
				session.setAttribute(WebKeysHoteles.EMPLEADO_A_ASIGNAR_HOTEL,personal);
				session.setAttribute(WebKeysHoteles.HOTEL_ID, ptovta);
				session.setAttribute(WebKeysHoteles.TIPO_UNIDAD, tipo);
				return mapping.findForward(getForward(renderRequest,
						"portlet.hoteles.asignar_consumos_habitacion"));
			}
			
			if(cmd.equals("update_consumo_habitaciones") ){
				String reserva = renderRequest.getParameter("reserva");
				List<Consumo> consumos= HotelesServiceUtil.getConsumos(ptovta, tipo,idUnidad);
				imprimirTicket(reserva,ptovta, tipo,idUnidad,consumos,2);
				
				HotelesServiceUtil.insertOrdenConsumo(ptovta, tipo, Integer.parseInt(idUnidad), personalId, Integer.parseInt(reserva), user.getScreenName(),null);
				return mapping.findForward(getForward(renderRequest,
						"portlet.hoteles.asignar_consumos_habitacion"));
			}
			
			
			if(cmd.equals("ticket_consumo_habitaciones") ){ 
				String reserva = renderRequest.getParameter("reserva");
				List<Consumo> consumos= HotelesServiceUtil.getConsumos(ptovta, tipo,idUnidad);
				String datosReserva=imprimirTicket(reserva,ptovta, tipo,idUnidad,consumos,2);

				                
				session.setAttribute(WebKeysHoteles.CONSUMOS_HOTEL , consumos);
				session.setAttribute(WebKeysHoteles.MESA_ASIGNAR_HOTEL , idUnidad);
				session.setAttribute(WebKeysHoteles.DATOS_RESERVAS , datosReserva);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.hoteles.ticket_consumos_habitacion"));
			}
			
			if(cmd.equals("facturarconsumos") ){ 
				List<Consumo> consumos= HotelesServiceUtil.getConsumos(ptovta, tipo,idUnidad);
				Double importe=0D;
				for(Consumo c:consumos) {
					importe += c.getPrecio()*c.getCantidad();
				}
				
				Cliente cliente = new Cliente();
				cliente.setCategoriaIVA("CS");
				cliente.setApellido("");
				cliente.setNombre("");
				cliente.setDocumentoNro("");
				
				Producto producto = new Producto();
				producto.setId(3);
				producto.setDescripcion("CONSUMO");
				producto.setPrecioUnitario(new BigDecimal(importe));
				producto.setDebitoCredito("D");
				
				FacturaDetalle detalle = new FacturaDetalle();
				detalle.setDetalle(producto);
				detalle.setPrecio(new BigDecimal(importe));
				
				
				List<FacturaDetalle>items = new ArrayList<FacturaDetalle>();
				items.add(detalle);
				
				Factura factura = new Factura(); 
				factura.setDetalle(items);
				factura.setEstado(Factura.ESTADOS.ALTA);
				factura.setTotalNeto(new BigDecimal(importe));
				factura.setSucursal(ptovta);
				factura.recalcularImportes();
				factura.setCliente(cliente);
				
				renderRequest.setAttribute("esEdicion", "esEdicion");
				
			    session.setAttribute(WebKeysUOMA.FACTURA_EN_EDICION, factura);
					
				return mapping.findForward(getForward(renderRequest,
						"portlet.hoteles.facturar_consumos"));
			}
			
/*			
			if(cmd.equals("verificarfacturaconsumos") ){
				 Factura factura = (Factura) session.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION);
				 if(factura.getCae()!=null && !"".equalsIgnoreCase(factura.getCae())) {
					 HotelesServiceUtil.insertOrdenConsumo(ptovta, tipo, Integer.parseInt(idUnidad), personalId, null,
							 user.getScreenName(),factura.getLetra()+"-"+factura.getSucursal()+"-" +factura.getNumero());
				 }
			}
*/			
		}
		return mapping.findForward("portlet.hoteles.asignar_consumos_habitacion");
		
	}
	
	private String imprimirTicket(String reserva,String ptovta,String tipo,String idUnidad,List<Consumo>consumos,int copias) throws SystemException {
		Double total=0D;
		String datosReserva="";
		Calendar fecha = CalendarFactoryUtil.getCalendar(); 		
		fecha.setTime(new Date());
		List<Reserva> reservas=HotelesServiceUtil.getReservasActivas(fecha.get(Calendar.YEAR), fecha.getTime());
        for(Reserva r:reservas) {
        	if(r.getIdReserva()==Integer.parseInt(reserva)) {
        		datosReserva="Reserva(HAB) "+ r.getIdHabitacion() +" - "+ r.getApellido()+" "+r.getNombre() +"(Reserva: "+r.getIdReserva()+")";
        		break;
        	}
        }
		
//        File logo = new File(themeDisplay.getPathThemeImages() +"/LogoHOTEL_30_DE_JUNIO.jpg");
        
        
        String pathLogo = TraeListasServiceUtil.getSystemConfig("LOGO_HOTEL");
        
        File logo = new File(pathLogo);
        
        List<String> cb = new ArrayList<String>();
        List<String> cn = new ArrayList<String>();
        List<String> pie = new ArrayList<String>();
        String linea="";

        cb.add("             COMPROBANTE DE CONSUMO          ");
        cb.add("                 "+formatoDeFechas.format(fecha.getTime()));
        cb.add("                                             ");
        cb.add(datosReserva);
        cb.add("                                             ");
        cb.add("_______________________________________________");
        cb.add("PRODUCTO           CANT    $ PRECIO     $ TOTAL");
        cb.add("_______________________________________________");
//              123456789-123456789-123456789-123456789-12345  
                
        
        ComanderaService comand= new ComanderaService();
        comand.setLogo(logo);
//        comand.setNameDevice("Comandera");
        comand.setCabecera(cb);
        int xx=0;
        
        for(Consumo c:consumos) {
           xx= c.getProducto().getDescripcion().length()>18?18:c.getProducto().getDescripcion().length();
           linea  = String.format("%-18s",c.getProducto().getDescripcion().substring(0,xx).trim());
//           linea += " ";
           linea +=  String.format("%5s",forCantidad.format( c.getCantidad()));
//           linea += " ";
           linea +=  String.format("%12s",format2D.format(c.getPrecio()));
//           linea += " ";
           linea +=  String.format("%12s",format2D.format(c.getPrecio()*c.getCantidad()));
           cn.add(linea);
           total += c.getPrecio()*c.getCantidad();
        }
                                                   
         cn.add("-----------------------------------------------");
        linea = "                           TOTAL $ " +String.format("%12s",format2D.format(total));
        cn.add(linea) ;    
	    comand.setCuerpo(cn);
        
	    
	    pie.add("-----------------------------------------------");
	    pie.add("                   Firma                     ");	
	    
	    comand.setPie(pie);
	    
	    for(int i=0;i<copias;i++) {
	       comand.imprimirTicketFactura();
	    }   
	    
	    return datosReserva;

	}
	
}
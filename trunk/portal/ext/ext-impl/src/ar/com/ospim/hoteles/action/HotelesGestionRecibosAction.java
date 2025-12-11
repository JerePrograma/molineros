package ar.com.ospim.hoteles.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.BusquedaPreautorizacionesFiltro;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Mesa;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.PermissionUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;


public class HotelesGestionRecibosAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();

		
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		boolean estoyEnCentral=PermissionUtil.userContainsRole(user,WebKeysHoteles.ROL_APROBACION_RECIBOS);
		
		Recibo recibo=null;
		Long idRecibo = 0L;
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idRecibo = ParamUtil.getLong(renderRequest,"id_recibo", 0);
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			if(cmd.equals("new") ){ 
				session.removeAttribute(WebKeysHoteles.RECIBO_EN_EDICION);
				Calendar cal = Calendar.getInstance();
				recibo = new Recibo();
				recibo.setSucursal(idHotel);
				Reserva reserva=new Reserva();
				reserva.setAnio(cal.get(Calendar.YEAR));
				recibo.setReserva(reserva);
				recibo.setFecha(cal.getTime());
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION , recibo);
				renderRequest.setAttribute("view","False");
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,"portlet.hoteles.recibo_editar"));
			}
			
			if(cmd.equals("filterRecibos")){
				
		           filterRecibos(renderRequest,session);		   	
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.hoteles.recibos_result"));	
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				
				recibo = (Recibo) session.getAttribute(WebKeysHoteles.RECIBO_EN_EDICION);
				actualizaRecibo(recibo,PortalUtil.getHttpServletRequest(renderRequest));
				
				idRecibo=updateRecibo(recibo, user.getScreenName());
				recibo.setNumero(idRecibo);
					
				msg = "Se ha actualizado el Recibo Hotel nro ";
				msg = msg + " "+ idRecibo;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Recibo: " + idRecibo
				);
				renderRequest.setAttribute("view","VIEW");
				recibo = HotelesServiceUtil.getReciboByNro(recibo.getSucursal(), idRecibo,estoyEnCentral);
				session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION, recibo);

			}
			
            if(cmd.equals(Constants.EDIT) ){
            	
            	
            	recibo = HotelesServiceUtil.getReciboByNro(idHotel, idRecibo,estoyEnCentral);
            	session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION , recibo);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.recibo_editar"));
			}
			
			if(cmd.equals(Constants.DELETE) ){
				recibo = HotelesServiceUtil.getReciboByNro(idHotel, idRecibo,estoyEnCentral);
            	session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION , recibo);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.recibo_anular"));
			}
			
			if(cmd.equals("anular_recibo") ){
				recibo = (Recibo) session.getAttribute(WebKeysHoteles.RECIBO_EN_EDICION);
				
				String fechaDia = ParamUtil.getString(renderRequest,"fechaAnulaDia");
				String fechaMes = ParamUtil.getString(renderRequest,"fechaAnulaMes");
				String fechaAnio = ParamUtil.getString(renderRequest,"fechaAnulaAnio");
				Date fechaD = null;
				try {
					fechaD = formatoDeFechas.parse(fechaDia + "/"
							+ (Integer.parseInt(fechaMes) + 1) + "/"
							+ fechaAnio);
				} catch (Exception e) {
					fechaD = null;
				}
				
				recibo.setFechaBaja(fechaD);
				
				idRecibo=anulaRecibo(recibo, user.getScreenName(),estoyEnCentral);
					
				msg = "Anulación Recibo Hotel nro ";
				msg = msg + " "+ idRecibo;
				SessionMessages.add(renderRequest, "updateCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Recibo: " + idRecibo
				);
					
				session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION, recibo);

				 return mapping.findForward(getForward(renderRequest,"portlet.hoteles.recibo_anular"));
			}
			if(cmd.equalsIgnoreCase("aprobacion")) {
			   
			   aprobarRecibos(renderRequest,session);
				
			   return mapping.findForward(getForward(renderRequest,
						"portlet.hoteles.recibos_result"));
			}
			
			if(cmd.equalsIgnoreCase("aprobar_recibo_individual")) {
				   
				   aprobarRecibosIndividual(renderRequest,session);
					
				   return mapping.findForward(getForward(renderRequest,"portlet.hoteles.recibo_editar"));
			}
			
		}
		return mapping.findForward("portlet.hoteles.recibo_editar");
   }
	
   private void actualizaRecibo(Recibo recibo,HttpServletRequest renderRequest) throws SystemException{
	    Long nro = ParamUtil.getLong(renderRequest, "nro");
	    String descripcion = ParamUtil.getString(renderRequest,"descripcion");
	    if(nro==null || nro==0) {
	      String clienteNombre=ParamUtil.getString(renderRequest, "cliente_recibo_nombre");
	      String clienteDocumento=ParamUtil.getString(renderRequest, "cliente_recibo_documento");
	      Cliente cliente= new Cliente();
	      cliente.setRazonSocial(clienteNombre);
	      cliente.setCuit(clienteDocumento);
	      recibo.setCliente(cliente);
	    
	      Integer idReserva= ParamUtil.getInteger(renderRequest, "reserva");
	      Integer anio= ParamUtil.getInteger(renderRequest, "anio");
	      Integer fini= ParamUtil.getInteger(renderRequest, "f_ini_reserva");
	      Integer ffin= ParamUtil.getInteger(renderRequest, "f_fin_reserva");
	      Integer clienteIdReserva=ParamUtil.getInteger(renderRequest, "cliente_reserva_id");
	      String fdde=ParamUtil.getString(renderRequest, "desde_reserva");
	      String fhta=ParamUtil.getString(renderRequest, "hasta_reserva");
	      
	      String ptoVta= ParamUtil.getString(renderRequest,"fc_sucursal");
	  	  String tipo = ParamUtil.getString(renderRequest,"fc_tipo");
	  	  String letra=ParamUtil.getString(renderRequest,"fc_letra");
	  	  String numero=ParamUtil.getString(renderRequest,"fc_numero");
	  	  String fecha=ParamUtil.getString(renderRequest,"fc_fecha");
	      Double totalFactura=ParamUtil.getDouble(renderRequest,"fc_total");
	      String fechaRecibo=ParamUtil.getString(renderRequest,"fecha_recibo");
	      String sucursal= ParamUtil.getString(renderRequest,"rc_sucursal");
	      
	      Date date1=null;
	      Date date2=null;
	      try {
			 date1=new SimpleDateFormat("dd/MM/yyyy").parse(fdde);
			 date2=new SimpleDateFormat("dd/MM/yyyy").parse(fhta);
		  } catch (ParseException e) { }  
	    
	      Date fFactura=null;
	      try {
			 fFactura=new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
		  } catch (ParseException e) { }  
	      
	      Date fRecibo=null;
	      try {
			 fRecibo=new SimpleDateFormat("dd/MM/yyyy").parse(fechaRecibo);
		  } catch (ParseException e) { }  
	      
	      recibo.setFecha(fRecibo);
	      
	      Reserva reserva=new Reserva();
	      reserva.setIdReserva(idReserva);
	      reserva.setFechaDesdeId(fini);
	      reserva.setFechaHastaId(ffin);
	      reserva.setFechaDesde(date1);
	      reserva.setFechaHasta(date2);
	      reserva.setIdCliente(clienteIdReserva);
	      reserva.setAnio(anio);
	      
	      Factura factura=new Factura();
	      factura.setTipo(tipo);
	      factura.setSucursal(ptoVta);
	      factura.setLetra(letra);
	      factura.setNumero(numero);
	      factura.setFecha(fFactura);
	      factura.setTotalNeto(new BigDecimal(totalFactura));
	      
	      recibo.setFactura(factura);
	      recibo.setReserva(reserva);
	      recibo.setDescripcion(descripcion);
		  recibo.setNumero(nro);
		  recibo.setSucursal(sucursal);
	    }
   }

   private long updateRecibo(Recibo recibo, String user) throws Exception{
	long id = 0;
	
	id = HotelesServiceUtil.updateRecibo(recibo, user);
	return id;
   }

   private long anulaRecibo(Recibo recibo, String user,boolean estoyEnCentral) throws Exception{
	long id = 0;
	
	id = HotelesServiceUtil.anulaRecibo(recibo, user,estoyEnCentral);
	return id;
   }

   private void filterRecibos(RenderRequest renderRequest,HttpSession session) throws SystemException{
		
		String sucursal=ParamUtil.getString(renderRequest,"sucursal",null);
		String clienteNombre =  ParamUtil.getString(renderRequest, "cliente",null);
		String clienteDoc =  ParamUtil.getString(renderRequest, "cliente_doc",null);
		String fechaDia = ParamUtil.getString(renderRequest,"fechadesdedia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechadesdemes");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
		
		String fechaDiaH = ParamUtil.getString(renderRequest,"fechahastadia");
		String fechaMesH = ParamUtil.getString(renderRequest,"fechahastames");
		String fechaAnioH = ParamUtil.getString(renderRequest,"fechahastaanio");
		Integer estado=ParamUtil.getInteger(renderRequest, "estado");
		
		Date fechaD = null;
		try {
			fechaD = formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fechaD = null;
		}
		
		Date fechaH = null;
		try {
			fechaH = formatoDeFechas.parse(fechaDiaH + "/"
					+ (Integer.parseInt(fechaMesH) + 1) + "/"
					+ fechaAnioH);
		} catch (Exception e) {
			fechaH = null;
		}
		
		Long id = ParamUtil.getLong(renderRequest, "id",0);
		
		Recibo reciboFiltro = new Recibo();
		reciboFiltro.setSucursal(sucursal);
		Cliente clienteFiltro=new Cliente();
		clienteFiltro.setRazonSocial(clienteNombre);
		clienteFiltro.setCuit(clienteDoc);
		reciboFiltro.setCliente(clienteFiltro);
		reciboFiltro.setFechaDdeFiltro(fechaD);
		reciboFiltro.setFechaHtaFiltro(fechaH);
		reciboFiltro.setNumero(id);
		reciboFiltro.setEstadoFiltro(estado);
		
		session.removeAttribute(WebKeysHoteles.RECIBOS_RESULT);
		session.removeAttribute(WebKeysHoteles.RECIBOS_FILTRO);
			
		List<Recibo> lista = HotelesServiceUtil.getRecibos(sucursal,id,fechaD,fechaH,clienteNombre,clienteDoc,estado);
		
		session.setAttribute(WebKeysHoteles.RECIBOS_RESULT,lista);
		session.setAttribute(WebKeysHoteles.RECIBOS_FILTRO,reciboFiltro);
		
	}

   private void aprobarRecibos(RenderRequest renderRequest,HttpSession session) throws SystemException{
		
		String sucursal=ParamUtil.getString(renderRequest,"sucursal",null);
		String clienteNombre =  ParamUtil.getString(renderRequest, "cliente",null);
		String clienteDoc =  ParamUtil.getString(renderRequest, "cliente_doc",null);
		String fechaDia = ParamUtil.getString(renderRequest,"fechadesdedia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechadesdemes");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
		
		String fechaDiaH = ParamUtil.getString(renderRequest,"fechahastadia");
		String fechaMesH = ParamUtil.getString(renderRequest,"fechahastames");
		String fechaAnioH = ParamUtil.getString(renderRequest,"fechahastaanio");
		Integer estado=ParamUtil.getInteger(renderRequest, "estado");
		
		String aprobados = ParamUtil.getString(renderRequest, "aprobados");
		
		Date fechaD = null;
		try {
			fechaD = formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fechaD = null;
		}
		
		Date fechaH = null;
		try {
			fechaH = formatoDeFechas.parse(fechaDiaH + "/"
					+ (Integer.parseInt(fechaMesH) + 1) + "/"
					+ fechaAnioH);
		} catch (Exception e) {
			fechaH = null;
		}
		
		Long id = ParamUtil.getLong(renderRequest, "id",0);
		
		Recibo reciboFiltro = new Recibo();
		reciboFiltro.setSucursal(sucursal);
		Cliente clienteFiltro=new Cliente();
		clienteFiltro.setRazonSocial(clienteNombre);
		clienteFiltro.setCuit(clienteDoc);
		reciboFiltro.setCliente(clienteFiltro);
		reciboFiltro.setFechaDdeFiltro(fechaD);
		reciboFiltro.setFechaHtaFiltro(fechaH);
		reciboFiltro.setNumero(id);
		reciboFiltro.setEstadoFiltro(estado);
		
		if(aprobados!=null && aprobados.length()>0) {
		  List<Recibo> aAprobar=new ArrayList<Recibo>();
		  String[] vaprobados = aprobados.split(";");
		  for(String s:vaprobados) {
			  String[]vRecibo=s.split("_");
			  aAprobar.add(new Recibo(vRecibo[0],new Long(vRecibo[1]),new Date()));
		  }
		
		  HotelesServiceUtil.aprobarRecibos(aAprobar);
		
		  session.removeAttribute(WebKeysHoteles.RECIBOS_RESULT);
		  session.removeAttribute(WebKeysHoteles.RECIBOS_FILTRO);
		  List<Recibo> lista = HotelesServiceUtil.getRecibos(sucursal,id,fechaD,fechaH,clienteNombre,clienteDoc,0);
		  session.setAttribute(WebKeysHoteles.RECIBOS_RESULT,lista);
		  session.setAttribute(WebKeysHoteles.RECIBOS_FILTRO,reciboFiltro);
		  
		}
	}

   private void aprobarRecibosIndividual(RenderRequest renderRequest,HttpSession session) throws SystemException{
		
		String sucursal=ParamUtil.getString(renderRequest,"rc_sucursal",null);
		
		Integer aprobado = ParamUtil.getInteger(renderRequest, "aprobar");
		
		
		Long id = ParamUtil.getLong(renderRequest, "nro",0);
		
		List<Recibo> aAprobar=new ArrayList<Recibo>();
		aAprobar.add(new Recibo(sucursal,id,aprobado==1?new Date():null));
		HotelesServiceUtil.aprobarRecibos(aAprobar);
		Recibo recibo = (Recibo) session.getAttribute(WebKeysHoteles.RECIBO_EN_EDICION);
		recibo.setAprobadoFecha(aprobado==1?new Date():null);
		session.setAttribute(WebKeysHoteles.RECIBO_EN_EDICION,recibo);
		
	}
   
	
}
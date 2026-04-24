package ar.com.ospim.hoteles.action;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

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

import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.hoteles.beans.PrestamoCuota;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.facturacion.Factura;
import jcifs.smb.FileEntry;


public class HotelesPrestamosAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		String tipo = ParamUtil.getString(actionRequest, "tipo", null);
		String msgError = null;
		if (StringUtils.checkNotEmpty(cmd)) {
			
            UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
			ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);			
			User user = PortalUtil.getUser(actionRequest);
			DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Prestamos"); 
			long folderId = f.getFolderId();
			File file=null;
			String filename = "";
			
			if(cmd.equals("addImagen") ){ 
				Prestamo prestamo =(Prestamo)uploadReq.getSession().getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
				String title="";
			    String description="";
			    
			    if("FCP".equalsIgnoreCase(tipo)) {
			    	title="PRESTAMO-"+String.valueOf( prestamo.getId())  +"-FCP" ;
			    	description="Factura del Préstamo " +String.valueOf( prestamo.getId()) ;
			    	file = uploadReq.getFile("fc_imagen");	
					filename = uploadReq.getFileName("fc_imagen");
			    }else if("CNV".equalsIgnoreCase(tipo)) {
			    	title="CONVENIO-"+String.valueOf( prestamo.getId())  +"-CNV" ;
			    	description="Convenio del Préstamo " +String.valueOf( prestamo.getId()) ;
			    	file = uploadReq.getFile("cv_imagen");	
					filename = uploadReq.getFileName("cv_imagen");
			    	
			    }
			    
			    String mimeType =  MimeTypesUtil.getContentType(file);
			    
			    if(!"".equalsIgnoreCase(filename)){
			      		try{
				          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, filename,
					        		filename, title, description, "", file, serviceContext);
				          
				          _log.debug("AGREGAR IMAGEN AL PRESTAMO " + entry.getDescription());
				            String msg = "";

				            if("FCP".equalsIgnoreCase(tipo)) {
	                            prestamo.setImgFactura(entry.getName());
	                            msg = "Se guardó correctamente la  imágen de la factura ";
	                        }else if("CNV".equalsIgnoreCase(tipo)) {
	                            prestamo.setImgConvenio(entry.getName()); 
	                            msg = "Se guardó correctamente la  imágen del convenio ";
	                        }   
				            HotelesServiceUtil.updatePrestamoImagen(prestamo,tipo,user.getScreenName());
				            
                            uploadReq.getSession().setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION,prestamo);
                            
                            
         				    SessionMessages.add(actionRequest, "updateCabOk");
        				    actionRequest.setAttribute("msgCabOk", msg);
                            
			      		}catch(FileSizeException e){
			      			SessionErrors.add(actionRequest, "errorUploadFile");
							actionRequest.setAttribute("msgInsertError","El archivo a subir supera el tamaño permitido");
							_log.error(e);
			      		}catch(FileNameException e){
			      			SessionErrors.add(actionRequest, "errorUploadFile");
							actionRequest.setAttribute("msgInsertError","El tipo de archivo a subir no está permitido");
							_log.error(e);
							
			      		}catch(Exception e){
			      			SessionErrors.add(actionRequest, "errorUploadFile");
			      			actionRequest.setAttribute("msgInsertError",e.getMessage());
			      			_log.error(e);
			      		}
			      	}
				}
			
			
			if(cmd.equals("deleteImagen") ){ 
				folderId = ParamUtil.getLong(uploadReq, "folderId");
				String name = ParamUtil.getString(uploadReq, "name");
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);				
				_log.debug("BORRAR IMAGEN FACTURA PRESTAMO: " + folderId + " " + name);
				
				Prestamo prestamo =(Prestamo)uploadReq.getSession().getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
				HotelesServiceUtil.deletePrestamoImagen(prestamo,tipo,user.getScreenName());
				if("FCP".equalsIgnoreCase(tipo)) {
                    prestamo.setImgFactura(""); 
                }else if("CNV".equalsIgnoreCase(tipo)) {
                    prestamo.setImgConvenio(""); 
                }   	
				session.removeAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
				session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION,prestamo);
				
			}	
		
			setForward(actionRequest, "portlet.hoteles.prestamos_editar");
		}
		
				

		
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		session.removeAttribute("esEdicion");
		
		Prestamo prestamo=null;
		Long idPrestamo = 0L;
		String idHotel="";
		String msg = "";
		
		if (!StringUtils.checkEmpty(cmd)) {
			idPrestamo = Long.valueOf(ParamUtil.getInteger(renderRequest,"id_prestamo", 0));
			idHotel = ParamUtil.getString(renderRequest,"id_hotel");
			
			if(cmd.equals("filterPrestamo")){
				
				   filterPrestamo(renderRequest,session);		   	
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.hoteles.prestamos_result"));	
			}
			
			if(cmd.equals("verPagos")){
				   
		           verPagosPrestamo(idPrestamo,1,renderRequest,session);
					
	   			   return mapping.findForward(getForward(renderRequest,
							"portlet.hoteles.prestamos_pagos_result"));	
			}
			if(cmd.equals(Constants.WRITE) ){ 
				
				prestamo = new Prestamo();
				Factura factura=new Factura();
				prestamo.setFactura(factura);
				prestamo.setHotel(idHotel);
				List<PrestamoCuota>cuotas=new ArrayList<PrestamoCuota>();
				prestamo.setCuotas(cuotas);
				session.setAttribute("esPopUp","N");
				session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION , prestamo);
				renderRequest.setAttribute("view","");
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				return mapping.findForward(getForward(renderRequest,"portlet.hoteles.prestamos_editar"));
			}
			
			if(cmd.equals(Constants.UPDATE) ){
				prestamo = (Prestamo) session.getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
				if(prestamo.getId()==null) {
				   prestamo.setId(idPrestamo);
				}   
				actualizaPrestamo(prestamo,PortalUtil.getHttpServletRequest(renderRequest));
				if(validarPrestamo(prestamo)) {
				   Long idPrestamoL=updatePrestamo(prestamo, user.getScreenName());
				   if(prestamo.getId()==null || prestamo.getId()==0) {
					   prestamo.setId(idPrestamoL);
				   }
					
				   msg = "Se guardó correctamente el Beneficio nro ";
				   msg = msg + " "+ idPrestamoL;
				   SessionMessages.add(renderRequest, "updateCabOk");
				   renderRequest.setAttribute("msgCabOk", msg);
				   _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id Prestamo: " + idPrestamoL
				    );
				}else {
					SessionErrors.add(renderRequest, "errorPrestamo");
					msg = prestamo.getErrorMsg();
					
					renderRequest.setAttribute("msgError", msg);	
				}
				session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION, prestamo);	
			}
			
            if(cmd.equals(Constants.EDIT) ){
            	
           	     prestamo = HotelesServiceUtil.getPrestamoById(idPrestamo);
            	session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION , prestamo);
            	_log.debug("Usuario: " + user.getScreenName() 
				     + " cmd: " + cmd 
				);
		        renderRequest.setAttribute("view","VIEW");
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.prestamos_editar"));
			}
            
            if(cmd.equals("generarCuotas") ){
            	prestamo = (Prestamo) session.getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
				generarCuotas(prestamo,PortalUtil.getHttpServletRequest(renderRequest));
          	    
              	session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION , prestamo);
           	
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.prestamos_cuotas_result"));
			}
			
            if(cmd.equals("agregarCuotas") ){
            	prestamo = (Prestamo) session.getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
				agregarCuotas(prestamo,PortalUtil.getHttpServletRequest(renderRequest));
          	    
              	session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION , prestamo);
           	
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.prestamos_cuotas_result"));
			}
            
            if(cmd.equals("eliminarCuotas") ){
            	prestamo = (Prestamo) session.getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION);
				eliminarCuotas(prestamo,PortalUtil.getHttpServletRequest(renderRequest));
          	    
              	session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION , prestamo);
           	
		        return mapping.findForward(getForward(renderRequest,"portlet.hoteles.prestamos_cuotas_result"));
			}
            
			if(cmd.equals(Constants.DELETE) ){ 
				prestamo= new Prestamo();
				prestamo.setHotel(idHotel);
				prestamo.setId(idPrestamo);
				HotelesServiceUtil.deletePrestamo(prestamo,user.getScreenName());
            	List<Prestamo> lista =  (List<Prestamo>) session.getAttribute(WebKeysHoteles.PRESTAMOS_RESULT);
            	List<Prestamo> lista1 = new ArrayList<Prestamo>();
            	for(Prestamo p:lista) {
            		if(p.getId()!=idPrestamo) {
            			lista1.add(p);
            		}
            	}
            	
            	session.setAttribute(WebKeysHoteles.PRESTAMOS_RESULT,lista1);
            	return mapping.findForward("portlet.hoteles.prestamos_turismo_list");
			}
			
			
            
			
		}
		return mapping.findForward("portlet.hoteles.prestamos_editar");
   }
	
   private void actualizaPrestamo(Prestamo prestamo,HttpServletRequest renderRequest) throws SystemException{
	   SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	   
	    Integer inte = ParamUtil.getInteger(renderRequest, "inte");
	    String cuil = ParamUtil.getString(renderRequest,"cuil");
	    String hotel = ParamUtil.getString(renderRequest,"cod_Hotel");
	    String observaciones = ParamUtil.getString(renderRequest,"observaciones");
	    
	    String fechaEstadiaDdeDia = ParamUtil.getString(renderRequest,"fechaEstadiaDdeDia");
		String fechaEstadiaDdeMes = ParamUtil.getString(renderRequest,"fechaEstadiaDdeMes");
		String fechaEstadiaDdeAnio = ParamUtil.getString(renderRequest,"fechaEstadiaDdeAnio");
		Date fechaEstadiaDde = null;
		try {
			fechaEstadiaDde = formatoDeFecha.parse(fechaEstadiaDdeDia + "/"
					+ (Integer.parseInt(fechaEstadiaDdeMes) + 1) + "/"
					+ fechaEstadiaDdeAnio);
		} catch (Exception e) {
			fechaEstadiaDde = DateUtils.getCalendarGMTMenos3().getTime();
		}
	    
		String fechaEstadiaHtaDia = ParamUtil.getString(renderRequest,"fechaEstadiaHtaDia");
		String fechaEstadiaHtaMes = ParamUtil.getString(renderRequest,"fechaEstadiaHtaMes");
		String fechaEstadiaHtaAnio = ParamUtil.getString(renderRequest,"fechaEstadiaHtaAnio");
		Date fechaEstadiaHta = null;
		try {
			fechaEstadiaHta = formatoDeFecha.parse(fechaEstadiaHtaDia + "/"
					+ (Integer.parseInt(fechaEstadiaHtaMes) + 1) + "/"
					+ fechaEstadiaHtaAnio);
		} catch (Exception e) {
			fechaEstadiaHta = DateUtils.getCalendarGMTMenos3().getTime();
		}
	    
	    Afiliado afiliado = new Afiliado(cuil,inte);
	    prestamo.setAfiliado(afiliado);
	    prestamo.setHotel(hotel);
	    prestamo.setObservaciones(observaciones);
	    prestamo.setEstadiaDesde(fechaEstadiaDde);
	    prestamo.setEstadiaHasta(fechaEstadiaHta);
	    
	    Formatter fmt =new Formatter();
	    String tipo = ParamUtil.getString(renderRequest,"fc_tipo");
		String letra = ParamUtil.getString(renderRequest,"fc_letra");
		String numero = ParamUtil.getString(renderRequest,"fc_numero");
		String sucursal = ParamUtil.getString(renderRequest,"fc_sucursal");
		String importeS = ParamUtil.getString(renderRequest,"fc_importe");
		if(numero!=null && !numero.isEmpty()) {
		   numero=fmt.format("%08d", Integer.parseInt(numero)).toString();
        }
		Double importe = 0D;
		if(importeS!=null  && !importeS.isEmpty()) {
			importeS=importeS.replace(",", ".");
			importe=Double.valueOf(importeS);
	   	}
		
		
		Factura factura = new Factura();
		factura.setTipo(tipo);
		factura.setLetra(letra);
		factura.setSucursal(sucursal);
		factura.setNumero(numero);
		factura.setTotalExento(BigDecimal.valueOf(importe));
		prestamo.setFactura(factura);
		
		String fechaAcuerdoDia = ParamUtil.getString(renderRequest,"fechaAcuerdoDia");
		String fechaAcuerdoMes = ParamUtil.getString(renderRequest,"fechaAcuerdoMes");
		String fechaAcuerdoAnio = ParamUtil.getString(renderRequest,"fechaAcuerdoAnio");
		Date fechaAcuerdo = null;
		try {
			fechaAcuerdo = formatoDeFecha.parse(fechaAcuerdoDia + "/"
					+ (Integer.parseInt(fechaAcuerdoMes) + 1) + "/"
					+ fechaAcuerdoAnio);
		} catch (Exception e) {
			fechaAcuerdo = DateUtils.getCalendarGMTMenos3().getTime();
		}
	    
	   	
	   	Double acuerdoImporte = 0D;
	   	Double acuerdoPorcentaje =0D;
	   	Double acuerdoInteres = 0D;
	   	Double acuerdoTotal = 0D;
	   	Double acuerdoMovilidad=0D;
	   	
	   	String acuerdoImporteS = ParamUtil.getString(renderRequest,"ac_importe");
	   	if(acuerdoImporteS!=null && !acuerdoImporteS.isEmpty()) {
	   		acuerdoImporteS=acuerdoImporteS.replace(",", ".");
	   		acuerdoImporte=Double.valueOf(acuerdoImporteS);
	   	}
	   	
	   	String acuerdoPorcentajeS = ParamUtil.getString(renderRequest,"ac_interes_porcentaje");
	   	if(acuerdoPorcentajeS!=null && !acuerdoPorcentajeS.isEmpty()) {
	   		acuerdoPorcentajeS=acuerdoPorcentajeS.replace(",", ".");
	   		acuerdoPorcentaje=Double.valueOf(acuerdoPorcentajeS);
	   	}
	   	
	   	String acuerdoInteresS = ParamUtil.getString(renderRequest,"ac_interes_importe");
	   	if(acuerdoInteresS!=null  && !acuerdoInteresS.isEmpty()) {
	   		acuerdoInteresS=acuerdoInteresS.replace(",", ".");
	   		acuerdoInteres=Double.valueOf(acuerdoInteresS);
	   	}
	   	
	   	String acuerdoTotalS = ParamUtil.getString(renderRequest,"ac_total");
	   	if(acuerdoTotalS!=null && !acuerdoTotalS.isEmpty()) {
	   		acuerdoTotalS=acuerdoTotalS.replace(",", ".");
	   		acuerdoTotal=Double.valueOf(acuerdoTotalS);
	   	}
	   	
	   	Integer acuerdoCuotas = ParamUtil.getInteger(renderRequest,"ac_cuotas");
	   	
	   	String fechaCuotaDia = ParamUtil.getString(renderRequest,"fechaAcuerdoCuotaDia");
		String fechaCuotaMes = ParamUtil.getString(renderRequest,"fechaAcuerdoCuotaMes");
		String fechaCuotaAnio = ParamUtil.getString(renderRequest,"fechaAcuerdoCuotaAnio");
		Date fechaCuota = null;
		try {
			fechaCuota = formatoDeFecha.parse(fechaCuotaDia + "/"
					+ (Integer.parseInt(fechaCuotaMes) + 1) + "/"
					+ fechaCuotaAnio);
		} catch (Exception e) {
			fechaCuota = DateUtils.getCalendarGMTMenos3().getTime();
		}
		
		
		String acuerdoMovilidadS = ParamUtil.getString(renderRequest,"ac_movilidad");
	   	if(acuerdoMovilidadS!=null && !acuerdoMovilidadS.isEmpty()) {
	   		acuerdoMovilidadS=acuerdoMovilidadS.replace(",", ".");
	   		acuerdoMovilidad=Double.valueOf(acuerdoMovilidadS);
	   	}
		
		prestamo.setAcuerdoFecha(fechaAcuerdo);
		prestamo.setPrimeraCuota(fechaCuota);
		prestamo.setInteresPorcentaje(acuerdoPorcentaje);
		prestamo.setInteresImporte(acuerdoInteres);
	   	prestamo.setCantidadCuotas(acuerdoCuotas);
	   	prestamo.setMonto(acuerdoImporte);
	   	prestamo.setTotal(acuerdoTotal);
	   	prestamo.setMovilidad(acuerdoMovilidad);
	   	
	   	
   }

   private long updatePrestamo(Prestamo prestamo, String user) throws Exception{
	long id = 0;
	id = HotelesServiceUtil.updatePrestamo(prestamo, user);
	return id;
   }

   private long deletePrestamo(Prestamo prestamo, String user) throws Exception{
	long id = 0;
	
//	id = HotelesServiceUtil.deleteHabitacion(habitacion, user);
	return id;
   }

   
   private void filterPrestamo(RenderRequest renderRequest,HttpSession session) throws SystemException{
		
		String cuil=ParamUtil.getString(renderRequest,"cuil",null);
		String inteParam =  ParamUtil.getString(renderRequest, "inte",null);
		String seccionalP =  ParamUtil.getString(renderRequest, "seccional",null);
		
		Integer inte = null;
		try {
			inte = Integer.parseInt(inteParam);
		} catch (Exception e) {		
		}
		
		Integer seccional=null;
		try {
			seccional = Integer.parseInt(seccionalP);
		} catch (Exception e) {}
		
		String fechaDia = ParamUtil.getString(renderRequest,"fechadesdedia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechadesdemes");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
		
		String fechaDiaH = ParamUtil.getString(renderRequest,"fechahastadia");
		String fechaMesH = ParamUtil.getString(renderRequest,"fechahastames");
		String fechaAnioH = ParamUtil.getString(renderRequest,"fechahastaanio");
		
		
		String fechaDeudaDia = ParamUtil.getString(renderRequest,"fechadeudadia");
		String fechaDeudaMes = ParamUtil.getString(renderRequest,"fechadeudames");
		String fechaDeudaAnio = ParamUtil.getString(renderRequest,"fechadeudaanio");
		
		
		Long id = ParamUtil.getLong(renderRequest, "id",0);
		String hotel = ParamUtil.getString(renderRequest,"hotel");
		
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
		
		String fechaCuotaDia = ParamUtil.getString(renderRequest,"fechadesdecuotadia");
		String fechaCuotaMes = ParamUtil.getString(renderRequest,"fechadesdecuotames");
		String fechaCuotaAnio = ParamUtil.getString(renderRequest,"fechadesdecuotaanio");
		
		String fechaCuotaDiaH = ParamUtil.getString(renderRequest,"fechahastacuotadia");
		String fechaCuotaMesH = ParamUtil.getString(renderRequest,"fechahastacuotames");
		String fechaCuotaAnioH = ParamUtil.getString(renderRequest,"fechahastacuotaanio");
		
		Date fechaCuotaD = null;
		try {
			fechaCuotaD= formatoDeFechas.parse(fechaCuotaDia + "/"
					+ (Integer.parseInt(fechaCuotaMes) + 1) + "/"
					+ fechaCuotaAnio);
		} catch (Exception e) {
			fechaCuotaD = null;
		}
		
		Date fechaCuotaH = null;
		try {
			fechaCuotaH = formatoDeFechas.parse(fechaCuotaDiaH + "/"
					+ (Integer.parseInt(fechaCuotaMesH) + 1) + "/"
					+ fechaCuotaAnioH);
		} catch (Exception e) {
			fechaCuotaH = null;
		}

		
		Date fechaDeuda = null;
		try {
			fechaDeuda = formatoDeFechas.parse(fechaDeudaDia + "/"
					+ (Integer.parseInt(fechaDeudaMes) + 1) + "/"
					+ fechaDeudaAnio);
		} catch (Exception e) {
			fechaDeuda = null;
		}
		
		session.removeAttribute(WebKeysHoteles.PRESTAMO_FILTRO);
		session.removeAttribute(WebKeysHoteles.PRESTAMOS_RESULT);
			
        Prestamo filtro = new Prestamo();
        filtro.setId(id);
        
        if(hotel!=null) {
          filtro.setHotel(hotel);	
        }
        Afiliado afiliado = new Afiliado();
        if(cuil!=null) {
        	afiliado.setCuil_titular(cuil);
        	if(inte!=null) {
        		afiliado.setInte(inte);
        	}
        	
        	filtro.setAfiliado(afiliado);
        }
        
        if(seccional!=null) {
           Seccional secc =new Seccional();
           secc.setId_seccional(seccional);
           afiliado.setSeccional(secc);
        }
        
        if(fechaD!=null) {
        	filtro.setFechaConvenioDesde(fechaD);
        }
        
        if(fechaH!=null) {
        	filtro.setFechaConvenioHasta(fechaH);
        }
        
        if(fechaCuotaD!=null) {
        	filtro.setFechaCuotaDesde(fechaCuotaD);
        }
        
        if(fechaCuotaH!=null) {
        	filtro.setFechaCuotaHasta(fechaCuotaH);
        }
        
        if(fechaDeuda!=null) {
        	filtro.setDeudaExigibleAl(fechaDeuda);
        }
        
        
        String fechaCCDiaH = ParamUtil.getString(renderRequest,"fechaccdia");
		String fechaCCMesH = ParamUtil.getString(renderRequest,"fechaccmes");
		String fechaCCAnioH = ParamUtil.getString(renderRequest,"fechaccanio");
		Date fechaDeudaCC = null;
		try {
			fechaDeudaCC = formatoDeFechas.parse(fechaCCDiaH + "/"
					+ (Integer.parseInt(fechaCCMesH) + 1) + "/"
					+ fechaCCAnioH);
		} catch (Exception e) {
			fechaDeudaCC = null;
		}
		filtro.setCorteCuentaCorriente(fechaDeudaCC);
        
        List<Prestamo> lista = HotelesServiceUtil.getListaPrestamos(filtro);
		
		session.setAttribute(WebKeysHoteles.PRESTAMO_FILTRO,filtro);
		session.setAttribute(WebKeysHoteles.PRESTAMOS_RESULT,lista);
		
	}
	
   
   private void generarCuotas(Prestamo prestamo,HttpServletRequest renderRequest) throws SystemException{
	   SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	    FeriadosServiceUtil feriadosServiceUtil=new FeriadosServiceUtil();
	   
	    Integer cantidad = ParamUtil.getInteger(renderRequest, "cantidad");
	    Double total =0D;
	    
	    String totalS = ParamUtil.getString(renderRequest,"total");
	    totalS=totalS.replace(",",".");
	    total = Double.valueOf(totalS);
	    
	    String fechaDia = ParamUtil.getString(renderRequest,"fechadia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechames");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechaanio");
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fecha = DateUtils.getCalendarGMTMenos3().getTime();
		}
	    
		List<PrestamoCuota> cuotas= new ArrayList<PrestamoCuota>();
		java.util.Calendar aux = CalendarFactoryUtil.getCalendar();
		Double valorCuotaMensual=(double)Math.round(total/cantidad * 100d) / 100d;
		Double saldo=total;
		Double valorCuota=0D; 
		for (int i = 0; i < cantidad; i++) {
		   aux.setTime(fecha);	
		   aux.add(Calendar.MONTH, i) ;	
		   
		   aux = feriadosServiceUtil.obtenerSiguienteDiaHabil(aux);
		   PrestamoCuota cuota=new PrestamoCuota();
	       cuota.setVencimiento(aux.getTime());
	       if(saldo>valorCuotaMensual && i<cantidad-1) {
	    	   saldo-=valorCuotaMensual;
	    	   valorCuota=valorCuotaMensual;
	       }else {
	    	   valorCuota=saldo;
	       }
	       cuota.setImporte(valorCuota);
	       cuota.setNumero(i+1);
	       cuotas.add(cuota);
		}	   	
	   	prestamo.setCuotas(cuotas);
   }
   
   
   private void agregarCuotas(Prestamo prestamo,HttpServletRequest renderRequest) throws SystemException{
	   SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
	   FeriadosServiceUtil feriadosServiceUtil=new FeriadosServiceUtil();
	    Integer numero = ParamUtil.getInteger(renderRequest, "numero");
	    Double total = ParamUtil.getDouble(renderRequest,"total");
	    String fechaDia = ParamUtil.getString(renderRequest,"fechadia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechames");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechaanio");
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fecha = DateUtils.getCalendarGMTMenos3().getTime();
		}
	    
		boolean existe=false;
		for(PrestamoCuota c:prestamo.getCuotas()) {
			if(c.getNumero()==numero) {
				c.setVencimiento(fecha);
				c.setImporte(total);
				c.setModificada(true);
				existe=true;
				break;
			}
		}
		
		if(!existe) {
			java.util.Calendar aux = CalendarFactoryUtil.getCalendar();
   		     aux.setTime(fecha);	
			 aux = feriadosServiceUtil.obtenerSiguienteDiaHabil(aux);
			 PrestamoCuota cuota=new PrestamoCuota();
		     cuota.setVencimiento(aux.getTime());
		     cuota.setImporte(total);
		     cuota.setNumero(numero);
		     cuota.setModificada(true);
		     prestamo.getCuotas().add(cuota);
		}
   }
   
   private void eliminarCuotas(Prestamo prestamo,HttpServletRequest renderRequest) throws SystemException{
	   Integer numero = ParamUtil.getInteger(renderRequest, "numero");
	   List<PrestamoCuota>cuotas= new ArrayList<PrestamoCuota>();
	   for(PrestamoCuota c:prestamo.getCuotas()) {
			if(c.getNumero()!=numero) {
				cuotas.add(c);
			}
		}
		prestamo.setCuotas(cuotas);
   }
   
   
   private boolean validarPrestamo (Prestamo prestamo) throws SystemException{
	   boolean ret=true;
	   Double totalCuotas=0D;
	   prestamo.setErrorMsg("");
	   if(!prestamo.getCuotas().isEmpty()) {
	     for(PrestamoCuota c:prestamo.getCuotas()) {
			totalCuotas+=c.getImporte();
	     }
	     if( Math.round(totalCuotas*100.0)/100.0!= Math.round(prestamo.getTotal()*100.0)/100.0) {
	    	 ret=false;
	    	 prestamo.setErrorMsg("No coincide el Total del Acuerdo con la sumatoria de las cuotas. ");
	     }
	   }  
		
	   return ret;
   }
   
   private void verPagosPrestamo(Long id,Integer entidad,RenderRequest renderRequest,HttpSession session) throws SystemException{
	  Prestamo pr=(Prestamo) session.getAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION); 
	  if(pr==null || pr.getId()==null || pr.getId()==0L || pr.getId()!=id || pr.getAfiliado()==null
			  ||( pr.getAfiliado()!=null && pr.getAfiliado().getApellido()==null )) {
		  pr=HotelesServiceUtil.getPrestamoById(id);
		  session.setAttribute(WebKeysHoteles.PRESTAMO_EN_EDICION,pr);
	  }
	  session.removeAttribute(WebKeysHoteles.PRESTAMO_PAGOS); 
   	  List<Recibo> recibos =HotelesServiceUtil.getPrestamoPagos(id,entidad,new Date()); 
   	  session.setAttribute(WebKeysHoteles.PRESTAMO_PAGOS,recibos);
   }
   
}
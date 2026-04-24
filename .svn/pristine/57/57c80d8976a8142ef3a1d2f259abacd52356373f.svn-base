package ar.com.ospim.tesoreria.action;


import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.WorkflowDefinition;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.CentroCosto;
import jcifs.smb.FileEntry;

public class CajaChicaEdicionAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		procesarMostrarBusquedaComprobantes(actionRequest);
		
		//HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(actionRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		String msgError = null;
		if (StringUtils.checkNotEmpty(cmd)) {
			
            UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
			ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);			
			User user = PortalUtil.getUser(actionRequest);
			DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "CajaChica"); 
			long folderId = f.getFolderId();
			File file=null;
			String filename = "";
			
			
			if(cmd.equals("addImagen") ){ 
				ComprobanteCajaChica comprobante =(ComprobanteCajaChica) session.getAttribute(WebKeysCajaChica.CAJA_CHICA_COMPROBANTE_EN_EDICION);
				if(comprobante==null) {
					comprobante=new ComprobanteCajaChica();
				}		
				String cuitAcreedor = ParamUtil.getString(actionRequest,"cuit_entidad");
				String sucuAcreedor = ParamUtil.getString(actionRequest,"sucursal_entidad");
				String tipoComprobante = ParamUtil.getString(actionRequest,"tipo_comprobante");
				String letraComprobante = ParamUtil.getString(actionRequest,"letra");
				Integer ptoVta = ParamUtil.getInteger(actionRequest,"pto_venta", 0);
				String nroComprobante = ParamUtil.getString(actionRequest,"nro_comprobante_cc");
				Integer idComprobante=ParamUtil.getInteger(actionRequest,"id_comprobante", 0);
				String razonSocial = ParamUtil.getString(actionRequest,"razon_social");
				
				String fechaDia = ParamUtil.getString(actionRequest,"fechaComprobanteCajaChicaDia");
				String fechaMes = ParamUtil.getString(actionRequest,"fechaComprobanteCajaChicaMes");
				String fechaAnio = ParamUtil.getString(actionRequest,"fechaComprobanteCajaChicaAnio");
				Integer conceptoId = ParamUtil.getInteger(actionRequest,"conceptoComprobante", 0);
				
				BigDecimal importe =new BigDecimal(ParamUtil.getDouble(actionRequest,"importe_comprobante",0));
				
                comprobante.setAcreedorEmpresa(new Empresa(cuitAcreedor,sucuAcreedor,razonSocial));
                comprobante.setTipoComprobante(tipoComprobante);
                comprobante.setLetraComprobante(letraComprobante);
                comprobante.setPtoVenta(ptoVta);
                comprobante.setNroComprobante(nroComprobante);
                comprobante.setId(idComprobante);
               
                

    			BigDecimal importe_gravado =new BigDecimal(ParamUtil.getDouble(actionRequest,"gravado",0));
    			comprobante.setGravadoIVA(importe_gravado);
    			
    			Double tasa_iva =ParamUtil.getDouble(actionRequest,"tasa_iva",0);
    			comprobante.setTasaIva(tasa_iva);
    			
    			BigDecimal importe_iva =new BigDecimal(ParamUtil.getDouble(actionRequest,"iva",0));
    			comprobante.setIva(importe_iva);
    			
    			BigDecimal importe_percep_iva =new BigDecimal(ParamUtil.getDouble(actionRequest,"percepcion_iva",0));
    			comprobante.setPercepcionIVA(importe_percep_iva);
    			
    			BigDecimal importe_percep_iibb =new BigDecimal(ParamUtil.getDouble(actionRequest,"percepcion_iibb",0));
    			comprobante.setPercepcionIIBB(importe_percep_iibb);
    			
    			Integer jurisdIIBB =ParamUtil.getInteger(actionRequest,"jurisdiccion_iibb",0);
    			comprobante.setJurisdiccionIIBB(jurisdIIBB);
    			
    			BigDecimal importe_otros_tributos =new BigDecimal(ParamUtil.getDouble(actionRequest,"otros_tributos",0));
    			comprobante.setOtrosTributos(importe_otros_tributos);
                
				
    			Date fecha = null;
    			try {
    				fecha = formatoDeFechas.parse(fechaDia + "/"
    						+ (Integer.parseInt(fechaMes) + 1) + "/"
    						+ fechaAnio);
    			} catch (Exception e) {
    				fecha = null;
    			}
    			comprobante.setFechaEmision(fecha);
    			Concepto concepto = new Concepto();
    			concepto.setId(conceptoId);
    			ComprobanteConcepto cc = new ComprobanteConcepto(concepto, importe);
    			comprobante.setConceptos(new ArrayList<ComprobanteConcepto>());
    			comprobante.getConceptos().add(cc);
    			comprobante.setImporteComprobante(importe);
    			
    			CentroCosto centro = new CentroCosto(0,"");
    			try{	
    				Integer id_centro = ParamUtil.getInteger(actionRequest,"id_centroCosto", 0);
    				centro.setId(id_centro);
    				
    		    } catch(Exception e){}
    			comprobante.setCentroCosto(centro);
    			
    			String observaciones = ParamUtil.getString(actionRequest,"observacionesComprobante");
    			Integer seccionalId=ParamUtil.getInteger(actionRequest, "seccionalCajaChica");  
    			comprobante.setObservaciones(observaciones);
    			
    			try{
    			   Seccional seccional= new Seccional(seccionalId);
    			   comprobante.setSeccional(seccional);
    			} catch(Exception e){}
    			
				
				String title="";
			    String description="";
			   	title=comprobante.getImagenNombre();
			    description=comprobante.getImagenNombre() ;
			    file = uploadReq.getFile("fc_imagen");	
				filename = uploadReq.getFileName("fc_imagen");
			    
			    String mimeType =  MimeTypesUtil.getContentType(file);
			    
			    if(!"".equalsIgnoreCase(filename)){
			      		try{
				          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, filename,
					        		filename, title, description, "", file, serviceContext);
				          
				          _log.debug("AGREGAR IMAGEN AL COMPROBANTE CAJA CHICA " + entry.getDescription());
				            String msg = "";

			                msg = "Se guardó correctamente la  imágen del comprobante. No olvide GUARDAR el comprobante. ";
	                        
         				    SessionMessages.add(actionRequest, "updateCabOk");
        				    actionRequest.setAttribute("msgCabOk", msg);
        				    
        				    session.setAttribute(WebKeysCajaChica.CAJA_CHICA_COMPROBANTE_EN_EDICION,comprobante);  
                            
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
//				setForward(actionRequest, "portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");
			    	
				}
			if(cmd.equals("deleteImagen") ){ 
				
				folderId = ParamUtil.getLong(uploadReq, "folderId");
				String name = ParamUtil.getString(uploadReq, "name");
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);				
				_log.debug("BORRAR IMAGEN comprobante Caja chica: " + folderId + " " + name);
//				setForward(actionRequest, "portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");
				
			}	

		}
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		CajaChica cajaChica = null;
		
		long idCajaChica = 0;
		String msg = "";
		
		
        int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		
		if (!StringUtils.checkEmpty(cmd)) {
			idCajaChica = ParamUtil.getInteger(renderRequest,"id_caja_chica", 0);
			if("NEW".equals(cmd) ){ // lo voy a usar como -NEW, para crear nuevas entradas en blanco
				
				cajaChica = new CajaChica();
				
				session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION , cajaChica);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.cajachica.editar_caja_chica"));
			}
			
			if(cmd.equals(Constants.EDIT) ){ 
            	cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad ); 
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
				String accion = ParamUtil.getString(renderRequest, "accion", "edit");
				if("view".equalsIgnoreCase(accion)){
					session.setAttribute("accion","view");
				}else{
					session.setAttribute("accion","edit");
				}
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id caja chica: " + idCajaChica
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.cajachica.editar_caja_chica"));		
			}
			
			if(cmd.equals("asigna") ){ 
            	cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
            	String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_EN_USO");
				WorkflowDefinition estado = new WorkflowDefinition(Integer.parseInt(estadoId),"");	
                cajaChica.setEstado(estado);
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
            	
        		asignaCajaChica(cajaChica,entidad,session,renderRequest);
            	
               	return mapping
        				.findForward("portlet.tesoreria.editar_orden_pago_ospim_entry");
			}
			
			if(cmd.equals("adduser") ){
				String usuarioDescripcion = ParamUtil.getString(renderRequest,"usuariodescripcion");
				Integer usuarioId = ParamUtil.getInteger(renderRequest,"usuarioid");
				cajaChica = (CajaChica) session.getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
				Boolean existeUser=false;
				for(User ds: cajaChica.getUsuariosHabilitados() ){
					  if(ds.getUserId()  == usuarioId){
						  existeUser=true; 
						  break;
					  }
				}
				if(!existeUser){
				  User usuario =UserLocalServiceUtil.getUser(usuarioId) ;
            	  cajaChica.getUsuariosHabilitados().add(usuario);
				}  
                
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
        		
               	return mapping
        				.findForward("portlet.tesoreria.cajachicausuarios.result");
			}
			
			if(cmd.equals("deleteuser") ){
				  Integer usuarioId= ParamUtil.getInteger(renderRequest,"usuarioid");
				  List<User> ld = new ArrayList<User>();
				  cajaChica = (CajaChica) session.getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
				  for(User d: cajaChica.getUsuariosHabilitados()){
					  if(d.getUserId()!=usuarioId){
						  ld.add(d);
					  }
				  }
				  cajaChica.setUsuariosHabilitados(ld);
				  session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
	        		
	              return mapping
	        				.findForward("portlet.tesoreria.cajachicausuarios.result");
			}
			
			if(cmd.equals("ejecuta") ){ 
            	cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
            	
            	session.removeAttribute("comprobanteTipo");
				session.removeAttribute("comprobanteLetra");
				session.removeAttribute("comprobantePtoVenta");
				session.removeAttribute("comprobanteNro");
				session.removeAttribute("comprobanteCuit");
				session.removeAttribute("comprobanteSucursal");
				session.removeAttribute("comprobanteRazonSocial");
            	
				if(entidad!=WebKeysGlobal.UOMA) {
        		   return mapping
        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica");
				} else {
					return mapping
	        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");	
				}
			}
			
			if(cmd.equals("ultimosmovimientos") ){ 
            	cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
            	
        		return mapping
        				.findForward("portlet.tesoreria.cajachicaultimosmovimientos.result");
			}
			
			
			if(cmd.equals("solicitareposicion") ){ 
				cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
				long idComprobante=solicitarReposicion(cajaChica,user.getScreenName(),entidad);
				msg = LanguageUtil.get(defaultLocale, "solicita-reposicion-caja-chica");
				msg = msg + " " +idComprobante;
				SessionMessages.add(renderRequest, "insertCabOk");
				 renderRequest.setAttribute("msgCabOk", msg);
				  _log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id Caja Chica: " + idComprobante
						);
            	
				  return mapping
	        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica");
			}
			
			
			if(cmd.equals("savecomprobante") ){ 
				ComprobanteCajaChica comprobante = new ComprobanteCajaChica();
				comprobante.setConceptos(new ArrayList<ComprobanteConcepto>());
				actualizaComprobante(comprobante,entidad,renderRequest);
				Integer idComprobanteCajaChica = ParamUtil.getInteger(renderRequest,"id_comprobante_caja_chica", 0);
				String msgError = validarDatos(comprobante,idComprobanteCajaChica,entidad);
				if("".equalsIgnoreCase(msgError) ){
				  long idComprobante=0;
				  if(idComprobanteCajaChica==0){
				     idComprobante= insertComprobanteCajaChica(idCajaChica,comprobante, user.getScreenName(),entidad);
				     msg = LanguageUtil.get(defaultLocale, "insert-comprobante-caja-chica");
				  }else{
					 idComprobante= updateComprobanteCajaChica(idComprobanteCajaChica,comprobante, user.getScreenName(),entidad);
					 msg = LanguageUtil.get(defaultLocale, "insert-comprobante-caja-chica");
				  }
				  
				  session.removeAttribute("comprobanteTipo");
				  session.removeAttribute("comprobanteLetra");
				  session.removeAttribute("comprobantePtoVenta");
				  session.removeAttribute("comprobanteNro");
				  session.removeAttribute("comprobanteCuit");
				  session.removeAttribute("comprobanteSucursal");
				  session.removeAttribute("comprobanteRazonSocial");
				  
				  session.setAttribute("comprobanteTipo", comprobante.getTipoComprobante());
				  session.setAttribute("comprobanteLetra", comprobante.getLetraComprobante());
				  session.setAttribute("comprobantePtoVenta",  String.valueOf(comprobante.getPtoVenta()));
				  session.setAttribute("comprobanteNro", comprobante.getNroComprobante());
				  
				  session.setAttribute("comprobanteCuit", comprobante.getAcreedorEmpresa().getCuit() );
				  session.setAttribute("comprobanteSucursal", comprobante.getAcreedorEmpresa().getSucursal() );
				  session.setAttribute("comprobanteRazonSocial",comprobante.getAcreedorEmpresa().getDescripcion());
				  
				  msg = msg + " " +idComprobante;
				  SessionMessages.add(renderRequest, "insertCabOk");
				  renderRequest.setAttribute("msgCabOk", msg);
				  _log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id comprobante: " + idComprobante
						);
				}else{
					msg = msgError ;
					SessionErrors.add(renderRequest, "errorValida"); 
					renderRequest.setAttribute("msgInsertError", msg);
					  
				}
				session.removeAttribute(WebKeysCajaChica.CAJA_CHICA_COMPROBANTE_EN_EDICION);  
				
				if(entidad!=WebKeysGlobal.UOMA) {
	        		   return mapping
	        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica");
				} else {
						return mapping
		        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");	
				} 
				
			}
			
//------
//------
		
			if(cmd.equals("limpiarcomprobante") ){ 
				  
			      session.removeAttribute("comprobanteTipo");
				  session.removeAttribute("comprobanteLetra");
				  session.removeAttribute("comprobantePtoVenta");
				  session.removeAttribute("comprobanteNro");
				  session.removeAttribute("comprobanteCuit");
				  session.removeAttribute("comprobanteSucursal");
				  session.removeAttribute("comprobanteRazonSocial");
				  
				  session.removeAttribute(WebKeysCajaChica.CAJA_CHICA_COMPROBANTE_EN_EDICION);  
				  
				  if(entidad!=WebKeysGlobal.UOMA) {
	        		   return mapping
	        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica");
				  } else {
						return mapping
		        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");	
				  }
				
			}
			
//------
//------			
			
			if(cmd.equals("editarcomprobante") ){ 
				Integer comprobanteId= ParamUtil.getInteger(renderRequest,"comprobanteid");
				ComprobanteCajaChica comprobante = new ComprobanteCajaChica();
				
				comprobante=CajaChicaServiceUtil.comprobantePorId(entidad, comprobanteId);
				
				session.setAttribute(WebKeysCajaChica.CAJA_CHICA_COMPROBANTE_EN_EDICION,comprobante);
				
				if(entidad!=WebKeysGlobal.UOMA) {
	        		   return mapping
	        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica");
				} else {
						return mapping
		        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");	
				}
				
			}
			
			
			if(cmd.equals("eliminarcomprobante") ){ 
				Integer idComprobanteCajaChica = ParamUtil.getInteger(renderRequest,"comprobanteid");
				
				if(entidad==WebKeysGlobal.UOMA) {
					ComprobanteCajaChica comprobante = new ComprobanteCajaChica();
					comprobante=CajaChicaServiceUtil.comprobantePorId(entidad, idComprobanteCajaChica);
					String fn =comprobante.getImagenNombreFileEntry();
					if(!"".equals(fn)) {
						DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "CajaChica"); 
						long folderId = f.getFolderId();
						DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, fn);		
					}
				}
				
				long idComprobante=0;
				idComprobante= deleteComprobanteCajaChica(idComprobanteCajaChica,entidad);
				msg = LanguageUtil.get(defaultLocale, "delete-comprobante-caja-chica");
				
				msg = msg + " " +idComprobante;
				SessionMessages.add(renderRequest, "insertCabOk");
				 renderRequest.setAttribute("msgCabOk", msg);
				  _log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id comprobante: " + idComprobante
						);
				
				  if(entidad!=WebKeysGlobal.UOMA) {
	        		   return mapping
	        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica");
				  } else {
						return mapping
		        				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");	
				  } 
				  
			}
			
			
			if(cmd.equals("controlarendicion") ){ 
            	cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
        		return mapping
        				.findForward("portlet.tesoreria.cajachicarendicionmovimientos.result");
			}
			
			
			if(cmd.equals("rechazareposicion") ){ 
				String aprobados = ParamUtil.getString(renderRequest, "aprobados");
				String rechazados= ParamUtil.getString(renderRequest, "rechazados");
				cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
				long idComprobante=0;
				idComprobante=procesaRechazados(rechazados,aprobados,cajaChica,entidad,user.getScreenName());
                msg = LanguageUtil.get(defaultLocale, "rechazo-reposicion-caja-chica");
				
				msg = msg + " " +idComprobante;
				SessionMessages.add(renderRequest, "insertCabOk");
				 renderRequest.setAttribute("msgCabOk", msg);
				  _log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id comprobante: " + idComprobante
						);
				
			    return mapping
	        				.findForward("portlet.tesoreria.cajachica.caja_chica_adm");
			    
			}
			
			if(cmd.equals("apruebareposicion") ){ 
				
				String aprobados = ParamUtil.getString(renderRequest, "aprobados");
				cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
				String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_APRUEBA_REPOSICION");
				WorkflowDefinition estado = new WorkflowDefinition(Integer.parseInt(estadoId),"");	
                cajaChica.setEstado(estado);
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
				
				long idComprobante=0;
				procesaAprobados(aprobados,cajaChica,entidad,session,renderRequest);
				if(entidad== WebKeysGlobal.OSPIM){
                   return mapping
	        				.findForward("portlet.tesoreria.editar_orden_pago_ospim_entry");
				}else if(entidad== WebKeysGlobal.UOMA){
	                   return mapping
		        				.findForward("portlet.farmacia.editar_orden_pago_entry");
				}   
			    
			}
			
            if(cmd.equals("apruebareposicionsinop") ){ 
				
				String aprobados = ParamUtil.getString(renderRequest, "aprobados");
				cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
				String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_APRUEBA_REPOSICION");
				WorkflowDefinition estado = new WorkflowDefinition(Integer.parseInt(estadoId),"");	
                cajaChica.setEstado(estado);
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
				
				long idComprobante=0;
				idComprobante=procesaAprobadosSinOP(aprobados,cajaChica,entidad,user.getScreenName());
				
                msg = LanguageUtil.get(defaultLocale, "aprueba_sin_op-reposicion-caja-chica");
				
				msg = msg + " " +idComprobante;
				SessionMessages.add(renderRequest, "insertCabOk");
				 renderRequest.setAttribute("msgCabOk", msg);
				  _log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id caja chica: " + idComprobante
						);
				
				return mapping
        				.findForward("portlet.tesoreria.cajachica.caja_chica_adm");
			    
			}
			
            if(cmd.equals("ingresareposicion") ){ 
				
				cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
				session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
				return mapping
	        				.findForward("portlet.tesoreria.caja_chica_ingresa_reposicion");
			    
			}
			
            if(cmd.equals("saveingresareposicion") ){ 
				
				cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
				String fechaDia = ParamUtil.getString(renderRequest,"fechaReposicionCajaChicaDia");
        		String fechaMes = ParamUtil.getString(renderRequest,"fechaReposicionCajaChicaMes");
        		String fechaAnio = ParamUtil.getString(renderRequest,"fechaReposicionCajaChicaAnio");
        		Date fecha = null;
        		try {
        			fecha = formatoDeFechas.parse(fechaDia + "/"
        					+ (Integer.parseInt(fechaMes) + 1) + "/"
        					+ fechaAnio);
        		} catch (Exception e) {
        			fecha = null;
        		}
				
				CajaChicaServiceUtil.ingresaReposicion(cajaChica, fecha, entidad, user.getScreenName());
				
                msg = LanguageUtil.get(defaultLocale, "ingreso-reposicion-caja-chica");
				
				msg = msg + " " +idCajaChica;
				SessionMessages.add(renderRequest, "insertCabOk");
				 renderRequest.setAttribute("msgCabOk", msg);
				  _log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id comprobante: " + idCajaChica
						);
				
				return mapping
	        				.findForward("portlet.tesoreria.cajachica.caja_chica_adm");
			    
			}
            
            if(cmd.equals("reporte") ){ 
            	cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
            	
        		return mapping
        				.findForward("portlet.tesoreria.cajachica.reporte_caja_chica");
			}
            
            if(cmd.equals("recibo") ){ 
            	cajaChica = CajaChicaServiceUtil.get((int)idCajaChica,entidad );
            	session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
            	
        		return mapping
        				.findForward("portlet.tesoreria.cajachica.recibo_caja_chica");
			}
            
            
            if(cmd.equals("marcacomprobante") ){ 
            	Integer id_seccional = ParamUtil.getInteger(renderRequest, "id_seccional");
            	
            	CajaChicaServiceUtil.updateComprobantesPendientesRecibo( entidad,(int)idCajaChica, id_seccional);
            	
        		return mapping
        				.findForward("portlet.tesoreria.cajachica.recibo_caja_chica");
			}
            
            
            cajaChica = (CajaChica) session.getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
			actualizaCajaChica(cajaChica,renderRequest);
			
			if(cmd.equals(Constants.UPDATE) ){
				if(idCajaChica == 0){
					 String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_INICIAL_CAJA_CHICA");
					 String estadoSolicitaReposicion = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_SOLICITA_REPOSICION");
                     WorkflowDefinition estado = new WorkflowDefinition(Integer.parseInt(estadoId),"");	
                     cajaChica.setEstado(estado);
                     if(estadoId.equalsIgnoreCase(estadoSolicitaReposicion) ){
                    	cajaChica.setSolicitudReposicion(new Date()); 
                     }
                     idCajaChica = insertCajaChica(cajaChica, user.getScreenName(),entidad);
					 cajaChica.setId((int)idCajaChica);
					 msg = LanguageUtil.get(defaultLocale, "insert-caja-chica");
					 msg = msg + " " +idCajaChica;
					 SessionMessages.add(renderRequest, "insertCabOk");
					  renderRequest.setAttribute("msgCabOk", msg);
					  _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id caja chica: " + idCajaChica
							);
				}else if(idCajaChica!=0){
					idCajaChica = updateCajaChica(cajaChica, user.getScreenName(),entidad);
					msg = LanguageUtil.get(defaultLocale, "update-caja-chica");
					msg = msg + idCajaChica;
					SessionMessages.add(renderRequest, "updateCabOk");
					renderRequest.setAttribute("msgCabOk", msg);
					_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id caja chica: " + idCajaChica
							);
				}
			}	
			
		}///////////
		
		session.setAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION, cajaChica);
		
		if(entidad!=WebKeysGlobal.UOMA) {
 		   return mapping
 				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica");
		} else {
				return mapping
     				.findForward("portlet.tesoreria.cajachica.ejecutar_caja_chica_extendida");	
		} 
		
		
	}
	
	
	private long insertCajaChica(CajaChica cajaChica, String user,int entidad) throws Exception{
		long id = 0;
		
		id = CajaChicaServiceUtil.add(cajaChica, user, entidad);
		return id;
	}
	
	
	private long updateCajaChica(CajaChica cajaChica, String user,int entidad) throws Exception{
		long id = 0;
		
		id = CajaChicaServiceUtil.update(cajaChica, user, entidad);
		return id;
	}
	
	private void actualizaCajaChica(CajaChica cajaChica,RenderRequest renderRequest){
		String descripcion = ParamUtil.getString(renderRequest, "descripcionCajaChica", null);
		Integer conceptoId = ParamUtil.getInteger(renderRequest,"conceptoCajaChica", 0);
		String observaciones = ParamUtil.getString(renderRequest, "observacionesCajaChica", null);
		Integer seccionalId = ParamUtil.getInteger(renderRequest,"seccionalCajaChica", 0);
		Double importeOriginal=ParamUtil.getDouble(renderRequest,"importeOriginalCajaChica",0);
		String eMailsInforme = ParamUtil.getString(renderRequest,"emailsInforme",null);
		Boolean pideSeccional = ParamUtil.getBoolean(renderRequest, "pideSeccionalCajaChica",false);
		Integer conceptoUnicoOPId = ParamUtil.getInteger(renderRequest,"conceptoCajaChicaUnicoOP", 0);
		
		Concepto concepto = new Concepto();
		concepto.setId(conceptoId);
		
		Concepto conceptoUnicoOP = new Concepto();
		conceptoUnicoOP.setId(conceptoUnicoOPId);
		
		
		Seccional seccional = new Seccional();
		seccional.setId_seccional(seccionalId);
		
		cajaChica.setDescripcion(descripcion);
		cajaChica.setConcepto(concepto);
		cajaChica.setObservaciones(observaciones);
		cajaChica.setSeccional(seccional);
		cajaChica.setImporteOriginal(importeOriginal);
		cajaChica.setEmailsController(eMailsInforme);
		cajaChica.setPideSeccionalGasto(pideSeccional);
		cajaChica.setConceptoUnicoOP(conceptoUnicoOP);
	}
	
	
	private void asignaCajaChica(CajaChica cajaChica,int entidad,HttpSession session, RenderRequest renderRequest){
		session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		session.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		session.removeAttribute(WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES);
		session.removeAttribute(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);

		String cuitAcreedor = "";
		OrdenPago op=null;
    	if(entidad==WebKeysGlobal.OSPIM){
    	   cuitAcreedor = WebKeysGlobal.CUIT_OSPIM;
    	   op = new OrdenPagoOspim();
    	}else if(entidad==WebKeysGlobal.AMTIMA){
     	   cuitAcreedor = WebKeysGlobal.CUIT_AMTIMA;
     	   op = new OrdenPagoAmtima();
     	}else if(entidad==WebKeysGlobal.UOMA){
      	   cuitAcreedor = WebKeysGlobal.CUIT_UOMA;
      	   op = new OrdenPagoUoma();
      	}
    	op.setComprobantes(new ArrayList<Comprobante>());
    	
    	List<Comprobante> comprobantes = new ArrayList<Comprobante>();
    	
    	//Genera Comprobante
    	List<Empresa> proveedores = null;
    	proveedores = TraeListasServiceUtil.getEmpleadores(cuitAcreedor,null,null);
    	Empresa acreedorEmpresa =proveedores.get(0);
    	Comprobante comp = new Comprobante();
    	List<ComprobanteConcepto>lcc = new ArrayList<ComprobanteConcepto>();
    	comp.setConceptos(lcc);
    	comp.setAcreedorEmpresa(acreedorEmpresa);
    	comp.setTipoComprobante("VAR");
    	String numero="";
		try {
			numero = ComprobanteServiceUtil.getUltimoNumeroComprobante("VAR",
					cuitAcreedor,Integer.toString(cajaChica.getSeccional().getId()), entidad);
			numero =cuitAcreedor + "-" + cajaChica.getSeccional().getId() +  "/"+ (Integer.parseInt(numero) +1);
		} catch (SystemException e) {}
		
		BigDecimal importe =new BigDecimal(cajaChica.getImporteOriginal());
		
    	comp.setNroComprobante(numero);
    	comp.setFechaEmision(new Date());
    	comp.setFechaRecepcion(new Date());
    	comp.setCuitEmisor(cuitAcreedor);
    	comp.setPtoVenta(1);
    	comp.setImporteComprobante(importe);
    	comp.setLetraComprobante("");
    	ComprobanteConcepto cc = new ComprobanteConcepto(cajaChica.getConcepto(), importe);
    	comp.getConceptos().add(cc);
    	
    	op.getComprobantes().add(comp);
    	op.setFarmacia(false);
    	op.setImporte(importe);
    	
    	op.setAcreedor(acreedorEmpresa);
    	
    	comprobantes.add(comp);
    	
    	List<OrdenPago.FormaPago> list = op.getFormaPago();
		if (list == null) {
			list = new ArrayList<OrdenPago.FormaPago>();
			op.setFormaPago(list);
		}
		
		TraeListasServiceUtil.getCtasBcrias(renderRequest);
	
		
		session.setAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION,TraeListasServiceUtil.getCtasBcrias(renderRequest));
		session.setAttribute(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES,WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
		session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,comprobantes);
    	session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
    	session.setAttribute("fromCajaChica",true);
    	
    	List<Cheque> chequesReutilizables;
		try {
			chequesReutilizables = ChequeServiceUtil
					.getChequesReutilizables(entidad);
			if (chequesReutilizables != null && chequesReutilizables.size() > 0) {
				renderRequest.setAttribute(
						WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES,
						WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES);
			}
		} catch (SystemException e) {
			
		}
		
    	
	}
	
	
	private void procesarMostrarBusquedaComprobantes(ActionRequest actionRequest) {
		String key = WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES;
		String nomostrar = actionRequest.getParameter(key);
		if (nomostrar != null && nomostrar.equals(key)) {
			actionRequest.setAttribute(key, key);
		}
	}
	
	private void actualizaComprobante(ComprobanteCajaChica comprobante,Integer entidad,RenderRequest renderRequest){
		
		String fechaDia = ParamUtil.getString(renderRequest,"fechaComprobanteCajaChicaDia");
		String fechaMes = ParamUtil.getString(renderRequest,"fechaComprobanteCajaChicaMes");
		String fechaAnio = ParamUtil.getString(renderRequest,"fechaComprobanteCajaChicaAnio");
		Integer conceptoId = ParamUtil.getInteger(renderRequest,"conceptoComprobante", 0);
		String cuitAcreedor = ParamUtil.getString(renderRequest,"cuit_entidad");
		String sucuAcreedor = ParamUtil.getString(renderRequest,"sucursal_entidad");
		BigDecimal importe =new BigDecimal(ParamUtil.getDouble(renderRequest,"importe_comprobante",0));
		String tipoComprobante = ParamUtil.getString(renderRequest,"tipo_comprobante");
		String letraComprobante = ParamUtil.getString(renderRequest,"letra");
		Integer ptoVta = ParamUtil.getInteger(renderRequest,"pto_venta", 0);
		String nroComprobante = ParamUtil.getString(renderRequest,"nro_comprobante_cc");
		String observaciones = ParamUtil.getString(renderRequest,"observacionesComprobante");
		String razonSocial = ParamUtil.getString(renderRequest,"entidad","");
		Integer seccionalId=ParamUtil.getInteger(renderRequest, "seccionalCajaChica");  
		Empresa acreedor =new Empresa(cuitAcreedor, sucuAcreedor, razonSocial);
		
		
		
        if(entidad==WebKeysGlobal.UOMA) {
			
			BigDecimal importe_gravado =new BigDecimal(ParamUtil.getDouble(renderRequest,"importe_gravado",0));
			comprobante.setGravadoIVA(importe_gravado);
			
			Double tasa_iva =ParamUtil.getDouble(renderRequest,"tasa_iva",0);
			comprobante.setTasaIva(tasa_iva);
			
			BigDecimal importe_iva =new BigDecimal(ParamUtil.getDouble(renderRequest,"importe_iva",0));
			comprobante.setIva(importe_iva);
			
			BigDecimal importe_percep_iva =new BigDecimal(ParamUtil.getDouble(renderRequest,"importe_percep_iva",0));
			comprobante.setPercepcionIVA(importe_percep_iva);
			
			BigDecimal importe_percep_iibb =new BigDecimal(ParamUtil.getDouble(renderRequest,"importe_percep_iibb",0));
			comprobante.setPercepcionIIBB(importe_percep_iibb);
			
			Integer jurisdIIBB =ParamUtil.getInteger(renderRequest,"jurisdiccion_iibb",0);
			comprobante.setJurisdiccionIIBB(jurisdIIBB);
			
			BigDecimal importe_otros_tributos =new BigDecimal(ParamUtil.getDouble(renderRequest,"importe_otros_tributos",0));
			comprobante.setOtrosTributos(importe_otros_tributos);
			
		}
		
		
		Date fecha = null;
		try {
			fecha = formatoDeFechas.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}
		
		Concepto concepto = new Concepto();
		concepto.setId(conceptoId);
		ComprobanteConcepto cc = new ComprobanteConcepto(concepto, importe);
		
        comprobante.setAcreedorEmpresa(acreedor);
		comprobante.getConceptos().add(cc);
		comprobante.setTipoComprobante(tipoComprobante);
		comprobante.setImporteComprobante(importe);
		comprobante.setLetraComprobante(letraComprobante);
		comprobante.setPtoVenta(ptoVta);
		comprobante.setNroComprobante(nroComprobante);
		comprobante.setObservaciones(observaciones);
		comprobante.setFechaEmision(fecha);
		try{
		   Seccional seccional= new Seccional(seccionalId);
		   comprobante.setSeccional(seccional);
		} catch(Exception e){}
		
		CentroCosto centro = new CentroCosto(0,"");
		try{	
			Integer id_centro = ParamUtil.getInteger(renderRequest,"id_centroCosto", 0);
			centro.setId(id_centro);
			
	    } catch(Exception e){}
		comprobante.setCentroCosto(centro);
	}
	
	private long insertComprobanteCajaChica(long idCajaChica, ComprobanteCajaChica comprobante, String user,int entidad) throws Exception{
		long id = 0;
		
		id = CajaChicaServiceUtil.addComprobante(idCajaChica,comprobante, user, entidad);
		return id;
	}
	
	private long updateComprobanteCajaChica(long idComprobanteCajaChica, ComprobanteCajaChica comprobante, String user,int entidad) throws Exception{
		long id = 0;
		
		id = CajaChicaServiceUtil.updateComprobante(idComprobanteCajaChica,comprobante, user, entidad);
		return id;
	}
	
	
	private long deleteComprobanteCajaChica(long idComprobanteCajaChica,int entidad) throws Exception{
		long id = 0;
		
		id = CajaChicaServiceUtil.deleteComprobante(idComprobanteCajaChica, entidad);
		return id;
	}
	
	private long solicitarReposicion(CajaChica caja,String user,int entidad) throws Exception{
       long id = 0;
		List<ComprobanteCajaChica> list =caja.getComprobantesPendientesRendicion();
		if(list.size()>0){
			id = CajaChicaServiceUtil.solicitaReposicion(caja,list,user,entidad);
			if(id!=0){
				List<String> direc = new ArrayList<String>();
				
				String usuarios=TraeListasServiceUtil.getSystemConfig("CAJA_CHICA_ADMINISTRADOR_EMAIL");
				String[] usuario= usuarios.split(";");
				String detalle="Se ha solicitado reposición de caja, y está pendiente de autorización";
				for(int i=0;i<usuario.length;i++){
				   DerivacionNotificacion dv = CrmServiceUtil.getNotificacionDerivacion(usuario[i]);
				   String eMail="";
				   if(dv!=null){
					eMail=dv.getDerivacionEmail();
				   }
				   if(!eMail.isEmpty()) {
					 direc.clear();
				     direc.add(eMail);
				     EnviaEmailsThread.enviarMailDesatendido("Aviso solicitud reposición Caja", detalle, direc,1);
				   }  
				}   
			}
		}
		return id;
	}
	
	private long procesaRechazados(String rechazados,String aprobados, CajaChica cajaChica,int entidad,String screenName) throws Exception{
		long id=0;
		String[] vRechazados = rechazados.split(";");
		List<Integer> lRechazados=new ArrayList<Integer>();
		for(int i=0;i<vRechazados.length;i++){
			if(!"".equalsIgnoreCase(vRechazados[i]))
			   lRechazados.add(Integer.parseInt(vRechazados[i]));
		}
		
		String[] vAprobados = aprobados.split(";");
		List<Integer> lAprobados=new ArrayList<Integer>();
		for(int i=0;i<vAprobados.length;i++){
			if(!"".equalsIgnoreCase(vAprobados[i]))
			   lAprobados.add(Integer.parseInt(vAprobados[i]));
		}
		
		id=CajaChicaServiceUtil.procesaComprobantesRechazados(lRechazados,lAprobados,cajaChica,screenName,entidad);
		List<String> direc = new ArrayList<String>();
		
		String eMail="";
		for(User u:cajaChica.getUsuariosHabilitados()){
		   DerivacionNotificacion dv = CrmServiceUtil.getNotificacionDerivacion(u.getScreenName());
		   if(dv!=null){
			  eMail=dv.getDerivacionEmail();
		   }
		   direc.add(eMail);
		}
		
		if(direc.size()>0){ 
		   EnviaEmailsThread.enviarMailDesatendido("Aviso reposición rechazada", "Mensaje a definir", direc,1);
		}
		return id;
	}
	
	private void procesaAprobados(String aprobados,CajaChica cajaChica,int entidad,HttpSession session, RenderRequest renderRequest) throws Exception{
		session.removeAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
		session.removeAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		session.removeAttribute(WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES);
		session.removeAttribute(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
		String cuitAcreedor = "";
		String sucu =null;
		OrdenPago op=null;
    	if(entidad==WebKeysGlobal.OSPIM){
    	   cuitAcreedor = WebKeysGlobal.CUIT_OSPIM;
    	   op = new OrdenPagoOspim();
    	   try {
				int proximoIdOP= OrdenPagoServiceUtil.obtenerProximoIdOrdenPago();
				renderRequest.setAttribute("PROXIMOIDORDENPAGO",proximoIdOP);
				
			} catch (SystemException e) {
				_log.error(e);
			}
    	}else if(entidad==WebKeysGlobal.AMTIMA){
     	   cuitAcreedor = WebKeysGlobal.CUIT_AMTIMA;
     	   op = new OrdenPagoAmtima();
     	}else if(entidad==WebKeysGlobal.UOMA){
      	   cuitAcreedor = WebKeysGlobal.CUIT_UOMA;
      	   op = new OrdenPagoUoma();
      	   sucu="000";
      	   if(cajaChica.getSeccional().getId()!=0){
      		 List <Seccional >seccionales = TraeListasServiceUtil.getSeccionales(cajaChica.getSeccional().getId(),
					null, cuitAcreedor);
      		 Seccional sec = new Seccional();
      		 if(seccionales.size()>0) sec= seccionales.get(0);
      		 op.setSeccional(sec);
      	   }
      	   
      	}
		
		
        op.setComprobantes(new ArrayList<Comprobante>());
    	
    	List<Comprobante> comprobantes = new ArrayList<Comprobante>();
    	
    	//Genera Comprobante
    	List<Empresa> proveedores = null;
    	proveedores = TraeListasServiceUtil.getEmpleadores(cuitAcreedor,null,sucu);
    	Empresa acreedorEmpresa =proveedores.get(0);

    	
    	if(entidad==WebKeysGlobal.UOMA){
       	   if(cajaChica.getSeccional().getId()!=0){
       		   acreedorEmpresa.setSeccional(op.getSeccional());
       		   acreedorEmpresa.setRazon_soc(op.getSeccional().getDescripcion());
       		   acreedorEmpresa.setSucursal(String.valueOf(op.getSeccional().getId()));
       	   }
       	   
       	}
    	
    	Comprobante comp = new Comprobante();
    	List<ComprobanteConcepto>lcc = new ArrayList<ComprobanteConcepto>();
    	comp.setConceptos(lcc);
    	comp.setAcreedorEmpresa(acreedorEmpresa);
    	comp.setTipoComprobante("VAR");
    	if(entidad==WebKeysGlobal.UOMA){
        	   if(cajaChica.getSeccional().getId()!=0){
        		   comp.setSeccional(op.getSeccional());
        	   }
    	}	   
    	
    	String numero="";
		try {
			numero = ComprobanteServiceUtil.getUltimoNumeroComprobante("VAR",
					cuitAcreedor,Integer.toString(cajaChica.getSeccional().getId()), entidad);
			numero =cuitAcreedor + "-" + cajaChica.getSeccional().getId() +  "/"+ (Integer.parseInt(numero) +1);
		} catch (SystemException e) {}
    	
		String[] vAprobados = aprobados.split(";");
		List<ComprobanteCajaChica> lAprobados=new ArrayList<ComprobanteCajaChica>();
		BigDecimal importe =new BigDecimal(0D);
		
		for(int i=0;i<vAprobados.length;i++){
			ComprobanteCajaChica ccc = CajaChicaServiceUtil.comprobantePorId(entidad, Integer.parseInt(vAprobados[i]));
			importe=importe.add(ccc.getImporteComprobante());
			ComprobanteConcepto cc = new ComprobanteConcepto(ccc.getConceptos().get(0).getConceptoComprobante() ,ccc.getImporteComprobante());
			Boolean existe=false;
			if(entidad==WebKeysGlobal.UOMA){
				
				    cc.setGravadoIVA(ccc.getGravadoIVA());
				    cc.setTasaIva(ccc.getTasaIva());
				    cc.setIva(ccc.getIva());
				    cc.setPercepcionIVA(ccc.getPercepcionIVA());
				    cc.setPercepcionIIBB(ccc.getPercepcionIIBB());
				    cc.setJurisdiccionIIBB(ccc.getJurisdiccionIIBB());
				    cc.setOtrosTributos(ccc.getOtrosTributos());
				
				
					Concepto cx = CajaChicaServiceUtil.getConceptoMaestro(cc.getConceptoComprobante().getId());
					if(cx.getId()!=0){
						cc.setConceptoComprobante(cx);
					}
					
					for(ComprobanteConcepto c:comp.getConceptos()){
						   if(c.getConceptoComprobante().getId()==cc.getConceptoComprobante().getId()
							  && c.getTasaIva()==cc.getTasaIva()	   
						      && c.getJurisdiccionIIBB()==cc.getJurisdiccionIIBB()){
							   BigDecimal ip=c.getImporte().add(cc.getImporte());
							   BigDecimal gr=c.getGravadoIVA().add(cc.getGravadoIVA());
							   BigDecimal iv=c.getIva().add(cc.getIva());
							   BigDecimal pi=c.getPercepcionIVA().add(cc.getPercepcionIVA());
							   BigDecimal piibb=c.getPercepcionIIBB().add(cc.getPercepcionIIBB());
							   BigDecimal ot=c.getOtrosTributos().add(cc.getOtrosTributos());
							   c.setImporte(ip);
							   c.setGravadoIVA(gr);
							   c.setIva(iv);
							   c.setPercepcionIVA(pi);
							   c.setPercepcionIIBB(piibb);
							   c.setOtrosTributos(ot);
							   
							   existe=true;
							   break;
						   }
					}	
					
			}else {
			
			   for(ComprobanteConcepto c:comp.getConceptos()){
				   if(c.getConceptoComprobante().getId()==cc.getConceptoComprobante().getId()){
					   BigDecimal ip=c.getImporte().add(cc.getImporte());
					   c.setImporte(ip);
					   existe=true;
					   break;
				   }
			   }
			}   
			if(!existe){
			   comp.getConceptos().add(cc);
			}
			   
		}
	
	    if(cajaChica.getConceptoUnicoOP()!=null && cajaChica.getConceptoUnicoOP().getId()!=0){
	    	ComprobanteConcepto ct = new ComprobanteConcepto(cajaChica.getConceptoUnicoOP(),importe);
	    	List<ComprobanteConcepto>lct = new ArrayList<ComprobanteConcepto>();
	    	lct.add(ct);
	        comp.setConceptos(lct);
	    }	
	    
		comp.setNroComprobante(numero);
    	comp.setFechaEmision(new Date());
    	comp.setFechaRecepcion(new Date());
    	comp.setCuitEmisor(cuitAcreedor);
    	comp.setPtoVenta(1);
    	comp.setSucuComprobante(1);
    	comp.setImporteComprobante(importe);
    	comp.setLetraComprobante("");
    	
    	op.getComprobantes().add(comp);
    	op.setFarmacia(false);
    	op.setImporte(importe);
    	
    	op.setAcreedor(acreedorEmpresa);
    	
    	comprobantes.add(comp);
    	
    	List<OrdenPago.FormaPago> list = op.getFormaPago();
		if (list == null) {
			list = new ArrayList<OrdenPago.FormaPago>();
			op.setFormaPago(list);
		}
		
		TraeListasServiceUtil.getCtasBcrias(renderRequest);
	
		session.setAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION,TraeListasServiceUtil.getCtasBcrias(renderRequest));
		session.setAttribute(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES,WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
		session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,comprobantes);
    	session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION, op);
    	session.setAttribute("cajachicauomacomprobantesaprobados", aprobados);
    	session.setAttribute("fromCajaChica",true);
    	
    	List<Cheque> chequesReutilizables;
		try {
			chequesReutilizables = ChequeServiceUtil
					.getChequesReutilizables(entidad);
			if (chequesReutilizables != null && chequesReutilizables.size() > 0) {
				renderRequest.setAttribute(
						WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES,
						WebKeysLiquidaciones.CHEQUES_REUTILIZABLES_DISPONIBLES);
			}
		} catch (SystemException e) {}
	}
	
	
	
	private long procesaAprobadosSinOP(String aprobados, CajaChica cajaChica,int entidad,String screenName) throws Exception{
		long id=0;
		String[] vAprobados = aprobados.split(";");
		List<Integer> lAprobados=new ArrayList<Integer>();
		for(int i=0;i<vAprobados.length;i++){
			try{
			   lAprobados.add(Integer.parseInt(vAprobados[i]));
			}catch( Exception e){}   
		}
		id=CajaChicaServiceUtil.procesaComprobantesAprobadosSinOP(lAprobados, cajaChica,screenName, entidad);
		return id;
	}
	
	private String validarDatos(ComprobanteCajaChica comprobante,Integer idComprobante,int entidad){
		String ret="";
		try {
			Empresa acreedor = EmpresaServiceUtil.getEmpleadorCompleto(comprobante.getAcreedorEmpresa().getCuit() , comprobante.getAcreedorEmpresa().getSucursal());
			if(acreedor==null){
				if (comprobante.getAcreedorEmpresa().getCuit().equals(WebKeysGlobal.CUIT_AMTIMA)
						|| comprobante.getAcreedorEmpresa().getCuit().equals(WebKeysGlobal.CUIT_OSPIM)
						|| comprobante.getAcreedorEmpresa().getCuit().equals(WebKeysGlobal.CUIT_UOMA)){
				   List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales(Integer.parseInt(comprobante.getAcreedorEmpresa().getSucursal()),null, comprobante.getAcreedorEmpresa().getCuit());
				   if(seccionales.size()==0){
					   ret="Acreedor Inexistente en Tabla";					   
				   }
				}else{
					ret="Acreedor Inexistente en Tabla";
				}
				
			}else{
				
			  if(entidad==WebKeysGlobal.UOMA && (idComprobante==null || idComprobante==0)){
				 boolean existe = CajaChicaServiceUtil.verificaComprobante(comprobante,entidad);
				 if(existe) ret="El Comprobante/Concepto Ya se encuentra cargado";
			  }
			  
			}
		} catch (Exception e) {
			ret="Error al validar Acreedor";
		}
		
		return ret;
		
	}
}

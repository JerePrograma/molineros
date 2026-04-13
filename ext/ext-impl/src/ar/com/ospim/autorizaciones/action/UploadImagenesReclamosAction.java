
package ar.com.ospim.autorizaciones.action;                                                                     

import java.io.File;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
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

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.util.StringUtils;
import jcifs.smb.FileEntry;
                                                                                                          
public class UploadImagenesReclamosAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesReclamosAction.class);                                                         
              

	
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                  

		String cmd = ParamUtil.getString(actionRequest, "imagen", null);
		
		String cmdSeccional = ParamUtil.getString(actionRequest, Constants.ACTION, null);

		String solapa = ParamUtil.getString(actionRequest,"solapa", null);
		
		User user = PortalUtil.getUser(actionRequest);
		
		String msgError = null;
		boolean validaOK = true;

		
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
		
		ReclamoPrestacional reclamoPrestacional =null;		
		
		if (StringUtils.checkNotEmpty(cmd)) {
			UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			
			reclamoPrestacional = (ReclamoPrestacional)uploadReq.getSession().getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);			
			
			
			if(cmd.equals(Constants.ADD) ){ 
				
			
				
				
				Random rnd = new Random();
				
				ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);
		
		        //Para uno solo		
				File file;
				String filename = "";
			    String description =ParamUtil.getString(uploadReq, "descripcionFile");
				file = uploadReq.getFile("importa_imagenes");	
				filename = uploadReq.getFileName("importa_imagenes");
				String mimeType =  MimeTypesUtil.getContentType(file);						
		        DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "ReclamosPrestacionales"); 
			    long folderId = f.getFolderId();
			    
			    
				if ("cta_bancaria".equalsIgnoreCase(solapa)){				
					msgError = CuentaDocumentoHelper.validaExisteImagen(reclamoPrestacional, description );
					if (msgError != null){
						SessionErrors.add(actionRequest, "errorCtaBancaria");
		      			actionRequest.setAttribute("msgCtaBancaria",msgError);
		      			validaOK = false;
					}
				}
			    
				if(validaOK){
			      	String title="";
			      	DLFileEntry dl=null;
			      	do {
			      		title=String.valueOf( reclamoPrestacional.getId_reclamo() ) +"-" +(int)(rnd.nextDouble()*100);
			      		try{
			      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
			      		} catch(Exception e){ /* no hace falta, siempre arroja NoSuchFileException cuando no existe por duplicado.*/}   
			      	} while (dl!=null);    
			      	
			      	if(!"".equalsIgnoreCase(filename)){
			      		try{
				          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, filename,
					        		filename, title, description, "", file, serviceContext);
				          
				          logger.debug("AGREGAR IMAGEN AL RECLAMO PRESTACIONAL: " + entry.getDescription());
				          
				      	if (msgError == null && "cta_bancaria".equalsIgnoreCase(solapa)){
							if(ReclamosPrestacionesServiceUtil.getReclamoPrestacionalCuenta(reclamoPrestacional.getId_reclamo()) != null){
								ReclamosPrestacionesServiceUtil.updateNombreImagen(reclamoPrestacional,  entry.getName(),  entry.getDescription(),  user.getScreenName()) ;
							}
						}
				          
			      		}catch(DuplicateFileException e){
			      			SessionErrors.add(actionRequest, "errorUploadFile");
							actionRequest.setAttribute("msgInsertError","El archivo se encuentra duplicado");
							logger.error(e);  
			      		}catch(FileSizeException e){
			      			SessionErrors.add(actionRequest, "errorUploadFile");
							actionRequest.setAttribute("msgInsertError","El archivo a subir supera el tamaño permitido");
							logger.error(e);
			      		}catch(FileNameException e){
			      			SessionErrors.add(actionRequest, "errorUploadFile");
							actionRequest.setAttribute("msgInsertError","El tipo de archivo a subir no está permitido");
							logger.error(e);
			      		}catch(Exception e){
			      			SessionErrors.add(actionRequest, "errorUploadFile");
			      			actionRequest.setAttribute("msgInsertError",e.getMessage());
			      			logger.error(e);
			      		}
			      	}
				}
			
				
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				Long folderId = ParamUtil.getLong(uploadReq, "folderid");
				String name = ParamUtil.getString(uploadReq, "filename");
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);				
				logger.debug("BORRAR IMAGEN DEL RECLAMO: " + folderId + " " + name);
				if ("cta_bancaria".equalsIgnoreCase(solapa)){
					ReclamosPrestacionesServiceUtil.deleteImagenCuenta( name,  user.getScreenName());
					session.removeAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
					reclamoPrestacional = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(reclamoPrestacional.getId_reclamo());	
					session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, reclamoPrestacional);
				}
			}			
		}	
		
		actionRequest.setAttribute("tab", solapa);
		
		actionRequest.setAttribute(Constants.CMD,Constants.MOVE );
		
		if (msgError == null && "cta_bancaria".equalsIgnoreCase(solapa)){
			//Se lo dejamos al render
		
			
		}else{			
			if (cmdSeccional!=null && WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdSeccional)){
				setForward(actionRequest,"portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry_seccionales_imagen");
			}else{				
				setForward(actionRequest, "portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry_imagen");
			}
		}	
		
	}       
	
 
	
	
	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
		
		String solapa = ParamUtil.getString(renderRequest,"solapa", null);
		String cmdSeccional = ParamUtil.getString(renderRequest, Constants.ACTION, null);
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		if ("cta_bancaria".equalsIgnoreCase(solapa)){
		
			ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
			
			String titular = ParamUtil.getString(renderRequest, "cmb_titular");
			if("0".equals(titular)){
				cuenta.setCbu(ParamUtil.getString(renderRequest, "cuenta_cbu")); 
				cuenta.setEmail(ParamUtil.getString(renderRequest, "cuenta_email"));
				cuenta.setCuil(ParamUtil.getString(renderRequest, "cuil_titular_cuenta"));
				String string = ParamUtil.getString(renderRequest, "denominacion");
				String[] parts = string.split(",");
				String part1 = parts[0];  
				String part2 = parts[1]; 
				cuenta.setApellido(part1);
				cuenta.setNombre(part2);
				cuenta.setCmbTitular(titular);
			}else if ("1".equals(titular)){
				cuenta.setCbu(ParamUtil.getString(renderRequest, "cuenta_cbu_autorizado")); 
				cuenta.setEmail(ParamUtil.getString(renderRequest, "cuenta_email_autorizado"));
				cuenta.setCuil(ParamUtil.getString(renderRequest, "cuil_autorizado"));
				cuenta.setApellido(ParamUtil.getString(renderRequest, "apellido_autorizado"));
				cuenta.setNombre(ParamUtil.getString(renderRequest, "nombre_autorizado"));
				cuenta.setCmbTitular(titular);
			}
	
			session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT, cuenta );
		}
		
			
		if (cmdSeccional!=null && WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdSeccional)){
			return mapping.findForward(getForward(renderRequest,
							"portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry_seccionales_imagen"));	
		}else{	
			return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.reclamosprestacionales.editar_reclamos_entry_imagen"));
		}
	
		
	}
	
	
}	


                                                                                                          
                                                                                                       


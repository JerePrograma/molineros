package ar.com.ospim.autorizaciones.action;                                                                     
                                                                                                          
import java.io.File;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.util.StringUtils;
import jcifs.smb.FileEntry;
                                                                                                          
public class UploadImagenesAutorizacionAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesAutorizacionAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
        
		String cmd = ParamUtil.getString(actionRequest, Constants.UPDATE, null);
		
		int idTratamiento = 0;
		
		idTratamiento =  ParamUtil.getInteger(actionRequest, "id_tratamiento", 0); 
		
		AutorizacionPrestacional tratamiento = null;
		tratamiento = AutorizacionPrestacionalServiceUtil.getAutorizacionPrestacional(idTratamiento);
	
		
		if (!StringUtils.checkEmpty(cmd)) {
			UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			
		
			
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
						
		        DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "AutorizacionesPrestacionales");
		        
			    long folderId = f.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		title=tratamiento.getNroAutorizacion() +"-" +(int)(rnd.nextDouble()*100);
		      		try{
		      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
		      		} catch(Exception e){ /* no hace falta, siempre arroja NoSuchFileException cuando no existe por duplicado.*/}   
		      	} while (dl!=null);    
		      	
		      	if(!"".equalsIgnoreCase(filename)){
		      		try{
			          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, filename,
				        		filename, title, description, "", file, serviceContext);
			          
			          logger.debug("AGREGAR IMAGEN A LA  AUTORIZACION : " + entry.getDescription());
			          
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
			
			if(cmd.equals(Constants.DELETE) ){ 
				Long folderId = ParamUtil.getLong(uploadReq, "folderid");
				String name = ParamUtil.getString(uploadReq, "filename");
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);
				
				logger.debug("BORRAR IMAGEN A LA AUTORIZACION: " + folderId + " " + name);
			}
			
		}	
		
		actionRequest.setAttribute("tab", "archivos");
		actionRequest.setAttribute("id_tratamiento", idTratamiento);
		
		
		setForward(actionRequest, "portlet.autorizaciones.editar_autorizacion_imagen");
		}
	                                                                                                      
                                                                                                          
                                                                                                          
                                                                                                   
   }                                                                                                         
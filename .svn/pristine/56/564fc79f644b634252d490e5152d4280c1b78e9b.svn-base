package ar.com.uoma.unidad_operativa.action;                                                                     
                                                                                                          
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

import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.Incidente;
import ar.com.uoma.unidad_operativa.WebKeysUnidadOperativa;
import ar.com.uoma.unidad_operativa.services.UnidadOperativaServiceUtil;
import jcifs.smb.FileEntry;
                                                                                                          
public class UploadImagenesIncidenteAfiliadoAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesIncidenteAfiliadoAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
        
		String cmd = ParamUtil.getString(actionRequest, "imagen", null);
		Integer idIncidente = ParamUtil.getInteger(actionRequest, "id_incidente");
		
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
						
		        DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "UnidadOperativa");
		        
			    long folderId = f.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		title=String.valueOf(idIncidente) +"-" +(int)(rnd.nextDouble()*100);
		      		try{
		      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
		      		} catch(Exception e){ /* no hace falta, siempre arroja NoSuchFileException cuando no existe por duplicado.*/}   
		      	} while (dl!=null);    
		      	
		      	if(!"".equalsIgnoreCase(filename)){
		      		try{
			          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, filename,
				        		filename, title, description, "", file, serviceContext);
			          
			          logger.debug("AGREGAR IMAGEN AL AFILIADO: " + entry.getDescription());
			          
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
				
				logger.debug("BORRAR IMAGEN AL AFILIADO: " + folderId + " " + name);
			}
			
		}	
		
		actionResponse.setRenderParameter("tabs1", "Imagenes");
		Incidente incidente=UnidadOperativaServiceUtil.buscarIncidente(idIncidente);
		actionRequest.setAttribute(WebKeysUnidadOperativa.INCIDENTE_EN_EDICION, incidente);
		
		setForward(actionRequest, "portlet.uoma.view_incidente_afiliado_entry");
	}                                                                                                       
                                                                                                                                                                                     
}                                                                                                         
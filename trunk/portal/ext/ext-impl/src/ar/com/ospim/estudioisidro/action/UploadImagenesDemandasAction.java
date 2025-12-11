package ar.com.ospim.estudioisidro.action;                                                                     
                                                                                                          
import java.io.File;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

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
import com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil;

import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.services.AutorizacionPrestacionalServiceUtil;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.DemandaJudicial;
import ar.com.ospim.util.StringUtils;
import jcifs.smb.FileEntry;
                                                                                                          
public class UploadImagenesDemandasAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesDemandasAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
        
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		
		HttpServletRequest req =  PortalUtil.getHttpServletRequest(actionRequest);
		DemandaJudicial demanda = (DemandaJudicial)  req.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION); 
		
		if (!StringUtils.checkEmpty(cmd)) {
			UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			
			if(cmd.equals(Constants.ADD) ){ 
				Random rnd = new Random();
				ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);
				Long id_demanda = ParamUtil.getLong(uploadReq, "id_demanda");
				File file;
				String fileName = "";
			    String description =ParamUtil.getString(uploadReq, "descripcionFile");
				file = uploadReq.getFile("importa_imagenes");	
				fileName = uploadReq.getFileName("importa_imagenes");
				String mimeType =  MimeTypesUtil.getContentType(file);
				String extension="";
				String[] vFile=fileName.split("\\.");
				if(vFile.length>1) extension=vFile[1];
		        DLFolder f = DLFolderServiceUtil.getFolder(10136, 0L, "DEMANDAS_JUDICIALES");
			    long folderId = f.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		title="DEMANDA_"+demanda.getId().toString() +"-" +(int)(rnd.nextDouble()*100);
		      		try{
		      		   dl=null;
		      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title + (extension.length()>0?".":"") + extension);
		      		} catch(Exception e){}   
		      	} while (dl!=null);    
		      	
		      	if(!"".equalsIgnoreCase(fileName)){
		               try{
					          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, fileName,
						        		fileName, title, description, "", file, serviceContext);
					          
					          logger.debug("AGREGAR IMAGEN DEMANDA: " + entry.getDescription());
					          
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
		      	setForward(actionRequest, "portlet.estudio_isidro.demanda.imagenes_demanda"); 
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				Long folderId = ParamUtil.getLong(uploadReq, "folderid");
				String name = ParamUtil.getString(uploadReq, "filename");
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);
				
				logger.debug("BORRAR IMAGEN A LA DEMANDA: " + folderId + " " + name);
			}
			
		}	
		
		actionRequest.setAttribute("tab", "archivos");
		actionRequest.setAttribute("id_demanda", demanda.getId());
		
		
		setForward(actionRequest, "portlet.estudio_isidro.demanda.imagenes_demanda");
	 }
	                                                                                                      
                                                                                                          
                                                                                                          
                                                                                                   
   }                                                                                                         
package ar.com.ospim.autorizaciones.action;                                                                     
                                                                                                          
import java.io.File;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import jcifs.smb.FileEntry;



import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil;
                                                                                                          
public class UploadImagenesSeguimientoSurAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesSeguimientoSurAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
         
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		
		if (!StringUtils.checkEmpty(cmd)) {
			
			if(cmd.equals(Constants.ADD) ){ 
				Random rnd = new Random();
				UploadPortletRequest uploadReq = PortalUtil                                                           
						.getUploadPortletRequest(actionRequest);  
				ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);
				Long id_seguimiento = ParamUtil.getLong(uploadReq, "id_seguimiento");
				
				SeguimientoSur seguimiento = (SeguimientoSur) uploadReq.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
		//Para uno solo		
				File file;
				String filename = "";
			    String description =ParamUtil.getString(uploadReq, "descripcionFile");
				file = uploadReq.getFile("importa_imagenes");	
				filename = uploadReq.getFileName("importa_imagenes");
				String mimeType =  MimeTypesUtil.getContentType(file);
						
		        DLFolder f = DLFolderServiceUtil.getFolder(
				            10136, 0L, "ExpedientesSUR");
			    long folderId = f.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		//title=seguimiento.getNro_solicitud_sur()+"_" +(int)(rnd.nextDouble()*100);
		      		title=seguimiento.getId_tipo_expediente_nro()+"-"+seguimiento.getClaseExpediente()  +"_" +(int)(rnd.nextDouble()*100);
		      		try{
		      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
		      		} catch(Exception e){}   
		      	} while (dl!=null);    
		      	
		      	if(!"".equalsIgnoreCase(filename)){
		        DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, filename,
			        		filename, title, description, "", file, serviceContext);
		      	}
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				UploadPortletRequest uploadReq = PortalUtil                                                           
						.getUploadPortletRequest(actionRequest);  
				Long folderId = ParamUtil.getLong(uploadReq, "folderid");
				String name = ParamUtil.getString(uploadReq, "filename");
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);
			}
			
		}	
	       
		setForward(actionRequest, "portlet.autorizaciones.imagenes_seguimientosur");                                                   
	}                                                                                                       
                                                                                                          
                                                                                                          
                                                                                                          
	public ActionForward render(ActionMapping mapping, ActionForm form,                                     
			PortletConfig portletConfig, RenderRequest renderRequest,                                           
			RenderResponse renderResponse) throws Exception {
                                                                                                          
		return mapping.findForward(getForward(renderRequest,                                                  
				"portlet.autorizaciones.imagenes_seguimientosur"));                                                                       
	}                                                                                                       
                                                                                                          
}                                                                                                         
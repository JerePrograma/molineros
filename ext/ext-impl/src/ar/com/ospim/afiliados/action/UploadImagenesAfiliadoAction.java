package ar.com.ospim.afiliados.action;                                                                     
                                                                                                          
import java.io.File;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import jcifs.smb.FileEntry;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.novedades.beans.PreAfiliado;
import ar.com.ospim.util.StringUtils;

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
                                                                                                          
public class UploadImagenesAfiliadoAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesAfiliadoAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
        
//		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		String cmd = ParamUtil.getString(actionRequest, "imagen", null);
		String cmdPreCarga = ParamUtil.getString(actionRequest, "cmd_pre_carga");
		
		Afiliado afiliado = null;
		PreAfiliado preAfiliado = null;
		
		if (!StringUtils.checkEmpty(cmd)) {
			UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			
			afiliado = (Afiliado)uploadReq.getSession().getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

			if(afiliado == null){
				preAfiliado = (PreAfiliado)uploadReq.getSession().getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);
				afiliado = new Afiliado(preAfiliado.getCuil_titular());
			}
			
			if(cmd.equals(Constants.ADD) ){ 
				Random rnd = new Random();
				
				ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);
				//Long id_seguimiento = ParamUtil.getLong(uploadReq, "id_seguimiento");

		//Para uno solo		
				File file;
				String filename = "";
			    String description =ParamUtil.getString(uploadReq, "descripcionFile");
				file = uploadReq.getFile("importa_imagenes");	
				filename = uploadReq.getFileName("importa_imagenes");
				String mimeType =  MimeTypesUtil.getContentType(file);
						
		        DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Afiliaciones");
		        
			    long folderId = f.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		title=afiliado.getCuil_titular() +"-" +(int)(rnd.nextDouble()*100);
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
		
		actionRequest.setAttribute("tabs1", "imagenes_afiliados");
		
		if(afiliado != null && preAfiliado==null){
			setForward(actionRequest, "portlet.afiliados.view_afiliado_entry");
		}else{
			actionRequest.setAttribute(Constants.CMD, cmdPreCarga );
			if(cmdPreCarga.equalsIgnoreCase(Constants.VIEW)){
				setForward(actionRequest, "portlet.novedades.view_pre_afiliado");
			}else{
				setForward(actionRequest, "portlet.novedades.editar_pre_afiliado");
			}
		}	
	}                                                                                                       
                                                                                                          
                                                                                                          
                                                                                                          
//	public ActionForward render(ActionMapping mapping, ActionForm form,                                     
//			PortletConfig portletConfig, RenderRequest renderRequest,                                           
//			RenderResponse renderResponse) throws Exception {
//		
//		renderRequest.setAttribute("tabs1", "imagenes_afiliados");
//	
//		return mapping.findForward(getForward(renderRequest,                                                  
//				"portlet.afiliados.view_afiliado_entry"));                                                                       
//	}                                                                                                       
                                                                                                          
}                                                                                                         
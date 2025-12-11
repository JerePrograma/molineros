package ar.com.ospim.seccional.action;                                                                     
                                                                                                          
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
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

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.novedades.beans.PreAfiliado;
import ar.com.ospim.util.StringUtils;
import jcifs.smb.FileEntry;
                      
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil;
import com.liferay.portlet.imagegallery.model.IGFolder;
import com.liferay.portlet.imagegallery.model.IGImage;
import com.liferay.portlet.imagegallery.service.IGFolderServiceUtil;
import com.liferay.portlet.imagegallery.service.IGImageLocalServiceUtil;

public class UploadImagenesSeccionalAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesSeccionalAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
        
		String cmd = ParamUtil.getString(actionRequest, "imagen", null);
		Integer idSeccional = ParamUtil.getInteger(actionRequest, "id_seccional", 0);
		
		if (!StringUtils.checkEmpty(cmd)) {
			UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			
//			afiliado = (Afiliado)uploadReq.getSession().getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
			
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
						
		        DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "Seccionales");
		        
			    long folderId = f.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		title=idSeccional +"-" +(int)(rnd.nextDouble()*100);
		      		try{
		      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
		      		} catch(Exception e){ /* no hace falta, siempre arroja NoSuchFileException cuando no existe por duplicado.*/}   
		      	} while (dl!=null);    
		      	
		      	if(!"".equalsIgnoreCase(filename)){
		      		try{
			          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, filename,
				        		filename, title, description, "", file, serviceContext);
			          
			          logger.debug("AGREGAR IMAGEN A LA SECCIONAL: " + entry.getDescription());
			          
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
				
				logger.debug("BORRAR IMAGEN A LA SECCIONAL: " + folderId + " " + name);
			}
			
		}	
		
		actionRequest.setAttribute("tabs1", "imagenes_seccional");
		
//		setForward(actionRequest, "portlet.seccional.imagenes_seccional");
		
	}                                                                                                       
                                                                                                          
                                                                                                          
                                                                                                          
	public ActionForward render(ActionMapping mapping, ActionForm form,                                     
			PortletConfig portletConfig, RenderRequest renderRequest,                                           
			RenderResponse renderResponse) throws Exception {
		
//		renderRequest.setAttribute("tabs1", "imagenes_afiliados");
	
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		Integer idSeccional = ParamUtil.getInteger(renderRequest, "id_seccional", 0);
		
		byte[] imageBytes = null;
		
		logger.info("CMD: " + cmd!=null?cmd:"-" + "Seccional "+ idSeccional);
		
		if(StringUtils.checkNotEmpty(cmd)) {
			
			if(cmd.equalsIgnoreCase(Constants.VIEW)) {
				
				logger.info("CMD: " + cmd);
				
				renderRequest.setAttribute("id_seccional", idSeccional);
				
				return mapping.findForward("portlet.seccional.galeria_seccional");
			}
			if(cmd.equalsIgnoreCase(Constants.PREVIEW)) {
			
				logger.info("CMD: " + cmd);
				
				Integer pos = ParamUtil.getInteger(renderRequest, "posicion", 0);
				
				
				DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
						DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
				
				DLFolder f = DLFolderServiceUtil.getFolder(10136, 0L, "Seccionales");
				
			    long folderId = f.getFolderId();
				
				Criterion criterion1 = null;
				criterion1 = RestrictionsFactoryUtil.eq("folderId",folderId);
				criterion1 = RestrictionsFactoryUtil.and(criterion1,RestrictionsFactoryUtil.ilike("title", idSeccional+"%" ));
				dlf.add(criterion1);
				
				List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dlf);
					
				if(results !=null && results.size() > 0) {
					if(pos>=results.size() ) {
						pos=0;
					}else if(pos<0) {
						pos=results.size()-1;
					}
					
					DLFileEntry fileEntry = (DLFileEntry) results.get(pos);
					
		            InputStream readImg = DLFileEntryLocalServiceUtil.getFileAsStream(
							fileEntry.getCompanyId(), fileEntry.getUserId(), folderId, fileEntry.getName(), fileEntry.getVersion());
		            
	
		            imageBytes = IOUtils.toByteArray(readImg);
				}else {
					imageBytes = new byte[0];
				}
	            renderRequest.setAttribute("ImgSeccional", javax.xml.bind.DatatypeConverter.printBase64Binary(imageBytes));
	            renderRequest.setAttribute("id_seccional", idSeccional);
	            renderRequest.setAttribute("posicion", pos);
		        logger.info("Fin pegando la imagen a la pagina");
		        
		       
		        
				return mapping.findForward("portlet.seccional.galeria_seccional");
				
//				mapping.findForward(getForward(renderRequest,                                                  
//						"portlet.seccional.galeria_seccional"));     
			}
		}
		
		renderRequest.setAttribute("id_seccional", idSeccional);
		
		return mapping.findForward("portlet.seccional.imagenes_seccional"); 
	}                                                                                                       
                                                                                                          
}                                                                                                         
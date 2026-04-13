package ar.com.ospim.crm.action;                                                                     
                                                                                                          
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

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.DocumentoLegalCRM;
import ar.com.ospim.util.StringUtils;

import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
                                                                                                          
public class UploadCRMFilesAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadCRMFilesAction.class);                                                         
         
	/**
	 * Referencias:
	 * 
	 * http://grepcode.com/file/repo1.maven.org/maven2/com.liferay.portal/portal-service/5.2.3/com/liferay/portlet/documentlibrary/service/DLFolderServiceUtil.java/
	 *
	 */
	
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
        
		String cmd = ParamUtil.getString(actionRequest, "imagen", null);
		String cmdCrmCarga = ParamUtil.getString(actionRequest, "cmd_crm_carga");
		
		Afiliado afiliado = null;
		DocumentoLegalCRM docLegal = null;
		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		String fileName = "";
		DLFolder carpetaBase = null;
		DLFolder carpetaReclamoNN = null;
		
		User user = PortalUtil.getUser(actionRequest);
		
		if (!StringUtils.checkEmpty(cmd)) {
			UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
			
			afiliado = (Afiliado)uploadReq.getSession().getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);

			if(afiliado == null){
				docLegal = (DocumentoLegalCRM)uploadReq.getSession().getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);
				afiliado = docLegal.getAfiliado()!=null?docLegal.getAfiliado():new Afiliado("9999999999",0,"No se encontro","No se encontro");
			}
			
			if(cmd.equals(Constants.ADD) ){
				/**
				 * Primero revisamos si existe el directorio 'Documentos_Legal' (deberia estar al mismo nivel que afiliaciones)
				 * luego le iremos agregando sub-carpetas segun numero de reclamo 
				 */
				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);
				
				try{
//					solo por la primera vez de un archivo en Documentos Legales...
					carpetaBase = DLFolderLocalServiceUtil.getFolder(10136, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, WebKeysCrm.CRM_DOCUM_LEGAL_FOLDER);
				}catch (NoSuchFolderException e) {
					carpetaBase = null;
				}
				
				
				if(carpetaBase == null){

					try{
						carpetaBase = DLFolderLocalServiceUtil.addFolder(user.getUserId(), 
//								user.getGroups().get(0).getGroupId(), 
//								UserUtil.getUserGroups(user.getUserId()).get(0).getUserGroupId(), 
								10136,
								DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, 
								WebKeysCrm.CRM_DOCUM_LEGAL_FOLDER,
								"Documentos legales de Afiliados", 
								serviceContext);
					
					}catch (PortalException e1) {
						logger.error(e1);
					}catch (SystemException e1) {	
						logger.error(e1);
					}
					
				}
				try{
					//DS - Agregado 2024-08-22 Pinchaba cuando docLegal es nulo
					if(docLegal==null) {
						docLegal = (DocumentoLegalCRM)uploadReq.getSession().getAttribute(WebKeysCrm.CRM_DOCUM_LEGAL_EN_EDICION);	
					}
					
//					solo la primera vez de un archivo para dicho reclamo...
					carpetaReclamoNN = DLFolderLocalServiceUtil.getFolder(10136, carpetaBase.getFolderId(), 
										WebKeysCrm.CRM_DOCUM_LEGAL_SUBFOLDER+docLegal.getId());
				}catch (NoSuchFolderException e) {
					carpetaReclamoNN = null;
				}
				
				if(carpetaReclamoNN == null){
//					DLFolderServiceUtil.addFolder('Guest', 'WebKeysCrm.CRM_DOCUM_LEGAL_FOLDER', name, description, serviceContext)
					try{
						carpetaReclamoNN = DLFolderLocalServiceUtil.addFolder(user.getUserId(), 
								10136, 
								carpetaBase.getFolderId(), 
								WebKeysCrm.CRM_DOCUM_LEGAL_SUBFOLDER+docLegal.getId(), 
								"Reclamo Nro " + docLegal.getId(), 
								serviceContext);
						
					}catch (PortalException e1) {
						logger.error(e1);
					}catch (SystemException e1) {	
						logger.error(e1);
					}	
				}
				
				Random rnd = new Random();
				
//				ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
				File file;
				
			    String description =ParamUtil.getString(uploadReq, "descripcionFile");
				file = uploadReq.getFile("importa_imagenes");	
				fileName = uploadReq.getFileName("importa_imagenes");
//				String mimeType =  MimeTypesUtil.getContentType(file);	
		        
			    folderId = carpetaReclamoNN.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		title=afiliado.getCuil_titular() +"-" +(int)(rnd.nextDouble()*100);
		      		try{
		      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
		      		} catch(Exception e){}   
		      	} while (dl!=null);    
		      	
		      	if(!"".equalsIgnoreCase(fileName)){
		      		try{
			          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, fileName,
				        		fileName, title, description, "", file, serviceContext);
			          
			          logger.debug("AGREGAR DOCUMENTO LEGAL AL AFILIADO: " + entry.getDescription());
			          
		      		}catch(FileSizeException e){
		      			SessionErrors.add(actionRequest, "errorUploadFile");
						actionRequest.setAttribute("msgInsertError","El archivo a subir supera el tamaño permitido");
		      		}catch(FileNameException e){
		      			SessionErrors.add(actionRequest, "errorUploadFile");
						actionRequest.setAttribute("msgInsertError","El tipo de archivo a subir no está permitido");	
		      		}catch(Exception e){
		      			SessionErrors.add(actionRequest, "errorUploadFile");
		      			actionRequest.setAttribute("msgInsertError",e.getMessage());	
		      		}
		      	}
			}
			
			if(cmd.equals(Constants.DELETE) ){ 
				folderId = ParamUtil.getLong(uploadReq, "folderid");
				fileName = ParamUtil.getString(uploadReq, "filename");
				
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, fileName);
				
				logger.debug("BORRAR DOCUMENTO LEGAL AL AFILIADO: " + folderId + " " + fileName);
			}
			
		}	
		
		actionRequest.setAttribute("tabs1", "imagenes_afiliados");
		
//		if(afiliado != null && preAfiliado==null){
//			setForward(actionRequest, "portlet.afiliados.view_afiliado_entry");
//		}else{
			actionRequest.setAttribute(Constants.CMD, cmdCrmCarga );
//			if(cmdCrmCarga.equalsIgnoreCase(Constants.VIEW)){
//				setForward(actionRequest, "portlet.novedades.view_pre_afiliado");
//			}else{
//				setForward(actionRequest, "/afiliados/editar_crm_legales_entry");
//			}
//		}
//		setForward(actionRequest, "/afiliados/editar_crm_legales_entry");
	}                                                                                                       
                                                                                                          
                                                                                                          
                                                                                                          
	public ActionForward render(ActionMapping mapping, ActionForm form,                                     
			PortletConfig portletConfig, RenderRequest renderRequest,                                           
			RenderResponse renderResponse) throws Exception {
		
//		renderRequest.setAttribute("tabs1", "imagenes_afiliados");
	
		return mapping.findForward(getForward(renderRequest,                                                  
//				"portlet.crm.imagenes_afiliado"));
				"portlet.crm.editar_docum_legal"));
	}                                                                                                       
                                                                                                          
}                                                                                                         
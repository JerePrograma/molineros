package ar.com.ospim.autorizaciones.action;                                                                     
                                                                                                          
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jcifs.smb.FileEntry;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.desarrolloAppMobile.beans.ClienteAppMobile;
import ar.com.ospim.desarrolloAppMobile.services.ClienteAppMobileServiceUtil;
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
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil;
                                                                                                          
public class UploadImagenesPreautorizacionAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadImagenesPreautorizacionAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {   
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(actionRequest).getSession();
         
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		User user = PortalUtil.getUser(actionRequest);
		
		if (!StringUtils.checkEmpty(cmd)) {
			
			HttpServletRequest req =  PortalUtil.getHttpServletRequest(actionRequest);
			PreAutorizacion preautorizacion = (PreAutorizacion)  req.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
			
			if(cmd.equals(Constants.ADD) ){ 
				Random rnd = new Random();
				UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest); 
//				PreAutorizacion preautorizacion = (PreAutorizacion) uploadReq.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);
				ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
				ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), actionRequest);
				Long id_preautorizacion = ParamUtil.getLong(uploadReq, "id_preautorizacion");

		//Para uno solo		
				File file;
				String fileName = "";
			    String description =ParamUtil.getString(uploadReq, "descripcionFile");
				file = uploadReq.getFile("importa_imagenes");	
				fileName = uploadReq.getFileName("importa_imagenes");
				String mimeType =  MimeTypesUtil.getContentType(file);
				String extension="";
				String[] vFile=fileName.split("\\.");
				if(vFile.length>1) extension=vFile[1];
		        DLFolder f = DLFolderServiceUtil.getFolder(10136, 0L, "PREAUTORIZACIONES");
			    long folderId = f.getFolderId();
		
		      	String title="";
		      	DLFileEntry dl=null;
		      	do {
		      		//title=seguimiento.getNro_solicitud_sur()+"_" +(int)(rnd.nextDouble()*100);
		      		title="PREAUT_"+preautorizacion.getId().toString() +"-" +(int)(rnd.nextDouble()*100);
		      		try{
		      		   dl=null;
		      		   dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title + (extension.length()>0?".":"") + extension);
		      		} catch(Exception e){}   
		      	} while (dl!=null);    
		      	
		      	if(!"".equalsIgnoreCase(fileName)){
		               try{
					          DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(serviceContext.getUserId(), folderId, fileName,
						        		fileName, title, description, "", file, serviceContext);
					          
					          PreAutorizacionServiceUtil.insertaSeguimientoDocumento(preautorizacion.getId(), entry.getName(), user.getScreenName());
					          
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
				UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);  
				Long folderId = ParamUtil.getLong(uploadReq, "folderid");
				String fileName = ParamUtil.getString(uploadReq, "filename");
				DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, fileName);
				
				PreAutorizacionServiceUtil.eliminaSeguimientoDocumento(preautorizacion.getId(), fileName, user.getScreenName());
			}
			
			
			if(cmd.equals("upload")){
				UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);
				String fileName = uploadReq.getFileName("archivo").toLowerCase();
				logger.info("subiendo archivo :" + fileName);
				if (fileName != null) {
					File zip = uploadReq.getFile("archivo");
					String ss ="";
					if ( fileName.endsWith(".xls")) {
						
						FileInputStream file = new FileInputStream(zip);
						HSSFWorkbook workbook = new HSSFWorkbook(file);
						
						HSSFSheet sheet = workbook.getSheetAt(0);
						Iterator<Row> rowIterator = sheet.iterator();
						
				        while (rowIterator.hasNext()) {
				        	Row currentRow = rowIterator.next();
				        	Iterator<Cell> cellIterator = currentRow.iterator();
				        	while (cellIterator.hasNext()) {
				        		Cell currentCell = cellIterator.next();
				        		int cellIndex = currentCell.getColumnIndex();
				        		Double xval;
				        		switch (cellIndex) {
								case 0:
									xval= currentCell.getNumericCellValue();
									ss +=String.valueOf(xval.longValue()) +";";
									break;
				        		}	
				        	}
				        }	
						session.setAttribute("PREAUTORIZACIONES_PROCESAR_IMAGENES", ss );	
					}
				}	
			}
			
			
		}	
	       
		setForward(actionRequest, "portlet.autorizaciones.imagenes_preautorizacion");                                                   
	}                                                                                                       
                                                                                                          
                                                                                                          
                                                                                                          
	public ActionForward render(ActionMapping mapping, ActionForm form,                                     
			PortletConfig portletConfig, RenderRequest renderRequest,                                           
			RenderResponse renderResponse) throws Exception {
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		if(!StringUtils.checkEmpty(cmd)){
		    if(cmd.equals("upload")){
			  return mapping.findForward("portlet.autorizaciones.reporte.imagenes_preautorizaciones");	
		    }
		    
		    if (cmd.equals("descargarImagenes")) {
		        try {
		            String titulo = ParamUtil.getString(renderRequest, "titulo");
		            Integer idPreaut = Integer.valueOf(titulo.replace("PREAUT_", ""));
		            Integer idPedidoApp = PreAutorizacionServiceUtil.obtenerIdPreautorizacionAPP(idPreaut);

		            if (idPedidoApp != null) {
		                String token = ClienteAppMobile.obtenerToken();
		                if (token != null) {
		                	ServiceContext serviceContext = ServiceContextFactory.getInstance(FileEntry.class.getName(), renderRequest);
			                ClienteAppMobile.procesarDocumentosDePedido(idPedidoApp, token, serviceContext);			                
		                }
		            }
		        } catch (Exception e) {
		            logger.error("Error al procesar imágenes de APP", e);
		        }

		        return mapping.findForward("portlet.autorizaciones.imagenes_preautorizacion");
		    }

		}    
                                                                                                          
		return mapping.findForward(getForward(renderRequest,                                                  
				"portlet.autorizaciones.imagenes_preautorizacion"));                                                                       
	}                                                                                                       
                                                                                                          
}                                                                                                         
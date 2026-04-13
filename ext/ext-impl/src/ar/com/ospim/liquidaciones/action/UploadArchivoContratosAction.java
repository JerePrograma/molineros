package ar.com.ospim.liquidaciones.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.procesaArchivos.ProcesaArchivosContratos;
import ar.com.ospim.procesaArchivos.exception.ErrorActualizaContratoException;
import ar.com.ospim.procesaArchivos.exception.ErrorImportaContratosException;
import ar.com.ospim.procesaArchivos.exception.ErrorProcesandoArchivosContratoException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
                                                                                                          
public class UploadArchivoContratosAction extends PortletAction {

	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadArchivoContratosAction.class);                                                         

	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);                                                          
		List<String> erroresImportaContrato = null;                                                                    
		List<String> erroresActualizaValores = null;
		String tipoArchivo = null;
		try {

			String fileNameImportaContrato = uploadReq.getFileName("importa_contrato")
					.toLowerCase();   
			String fileNameActualizaValores = uploadReq.getFileName(
					"actualiza_valores").toLowerCase();
			if (fileNameImportaContrato != null || fileNameActualizaValores != null) {
				if (fileNameImportaContrato != null && !fileNameImportaContrato.trim().equals("")) {
					tipoArchivo = "importar";                                              
					File file =uploadReq.getFile("importa_contrato");
					FileInputStream fileInput = new FileInputStream(file);
					erroresImportaContrato = procesarFile(actionRequest, fileInput,
							tipoArchivo);
				}
				if (fileNameActualizaValores != null                                                                       
						&& !fileNameActualizaValores.trim().equals("")) {
					tipoArchivo = "actualizar";                                                                 
					File file =uploadReq.getFile("actualiza_valores");                            
					FileInputStream fileInput = new FileInputStream(file);
					erroresActualizaValores = procesarFile(actionRequest, fileInput,                                      
							tipoArchivo);                                                                               
				}								
			}
		} catch (Exception e) {
			logger.error(e);
			SessionErrors.add(actionRequest, e.getClass().getName());                                           
		}
		if ((null!=erroresImportaContrato && !erroresImportaContrato.isEmpty()) || (null!= erroresActualizaValores && !erroresActualizaValores.isEmpty())) {
			if(null!=erroresImportaContrato && !erroresImportaContrato.isEmpty()){
				ErrorImportaContratosException epame= new ErrorImportaContratosException();
				SessionErrors.add(actionRequest, epame.getClass().getName());
			}else if(null!=erroresActualizaValores && !erroresActualizaValores.isEmpty()){
				ErrorActualizaContratoException epas= new ErrorActualizaContratoException();
				SessionErrors.add(actionRequest, epas.getClass().getName());
			}else {			                                 
				ErrorProcesandoArchivosContratoException e = new ErrorProcesandoArchivosContratoException();
				SessionErrors.add(actionRequest, e.getClass().getName());
			}
		}                                                                                                 
		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,                                          
					"successMessage");                                                                              
			SessionMessages.add(actionRequest, "request_processed",                                             
					successMessage);                                                                                
		}                                                                                                     
		setForward(actionRequest, "portlet.liquidaciones.importar_contratos");
		//actionRequest.setAttribute("tabs1", "importar-archivos");
	}                                                                                                       
                                                                                                          
	private List<String> procesarFile(ActionRequest actionRequest, FileInputStream file,                    
			String tipoArchivo) throws IOException {
		BufferedReader reader=null;
		List<String> errores = new ArrayList<String>();
		try {
			if (tipoArchivo.trim().equals("importar")
					|| tipoArchivo.trim().equals("actualizar")) {
				ZipInputStream in = new ZipInputStream(file);
				ZipEntry entry = in.getNextEntry();
				reader = new BufferedReader(new InputStreamReader(in,"ISO-8859-1"));
				new ProcesaArchivosContratos().procesarArchivoImportaContrato(reader);
				
			} else if (tipoArchivo.trim().equals("actualizar")) {
				reader = new BufferedReader(new InputStreamReader(file,"ISO-8859-1"));
				new ProcesaArchivosContratos().procesarArchivoListadoActualizaValores(reader);
			}
		} catch (Exception e) {
			logger.debug("Error al procesar archivo " + tipoArchivo, e);
			errores.add(tipoArchivo);
		}
		reader.close();
		return errores;
	}
                                                                                                       
                                                                                                          
	public ActionForward render(ActionMapping mapping, ActionForm form,                                     
			PortletConfig portletConfig, RenderRequest renderRequest,                                           
			RenderResponse renderResponse) throws Exception {
                                                                                                          
		return mapping.findForward(getForward(renderRequest,                                                  
				"portlet.tesoreria.view"));                                                                       
	}                                                                                                       
                                                                                                          
}                                                                                                         
package ar.com.ospim.farmacia.action;                                                                     
                                                                                                          
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Date;
import com.liferay.portal.model.User;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.procesaArchivos.ProcesaArchivosFarmacia;
import ar.com.ospim.procesaArchivos.exception.ErrorProcesandoArchivosListadoSSSaludException;
import ar.com.ospim.procesaArchivos.exception.ErrorProcesandoArchivosMedicamentosException;
import ar.com.ospim.procesaArchivos.exception.ErrorProcesandoArchivosVademecumException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
                                                                                                          
public class UploadArchivoFarmaciaAction extends PortletAction {                                          
                                                                                                          
	private static Log logger = LogFactoryUtil                                                              
			.getLog(UploadArchivoFarmaciaAction.class);                                                         
                                                                                                          
	public void processAction(ActionMapping mapping, ActionForm form,                                       
			PortletConfig portletConfig, ActionRequest actionRequest,                                           
			ActionResponse actionResponse) throws Exception {                                                   
                                                                                                          
		UploadPortletRequest uploadReq = PortalUtil                                                           
				.getUploadPortletRequest(actionRequest);                                                          
		List<String> erroresManual = null;                                                                    
		List<String> erroresListadosss = null;
		String tipoArchivo = null;
		
		User user = PortalUtil.getUser(actionRequest);

		try { 
			
			String fileNameDat = uploadReq.getFileName("archivoManualDat").toLowerCase();                                          
			String fileNameSSSalud = uploadReq.getFileName("archivoListadoSSSalud").toLowerCase();                                                         
			
			if (fileNameDat != null || fileNameSSSalud != null) {
				if (fileNameDat != null && !fileNameDat.trim().equals("")) {
					logger.debug("Levantando Manual dat...");
					tipoArchivo = "manual";                                                                         
					File file =uploadReq.getFile("archivoManualDat");                                 
					FileInputStream fileInput = new FileInputStream(file);                                          
					erroresManual = procesarFile(actionRequest, fileInput, tipoArchivo);                                                                               
				}
				if (fileNameSSSalud != null                                                                       
						&& !fileNameSSSalud.trim().equals("")) {
					logger.debug("Levantando ListadoSSSalud...");

					tipoArchivo = "listadosssalud";                                                                 
					File file =uploadReq.getFile("archivoListadoSSSalud");                            
					FileInputStream fileInput = new FileInputStream(file);
					erroresListadosss = procesarFile(actionRequest, fileInput, tipoArchivo);                                                                               
				}	
				
				Date periodoArchivo;
				String periodoMes = ParamUtil.getString(actionRequest,"periodoMes") ;
				String periodoAnio = ParamUtil.getString(actionRequest,"periodoAnio");
				SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
				periodoArchivo= null;

				
				if(erroresManual.size()==0 && erroresListadosss.size()==0) {
					
					logger.debug("Actualizando Vademecum..." + periodoMes + "/" + periodoAnio);
					
					try {
						periodoArchivo= formatoDePeriodo.parse( "01/"
								+ (Integer.parseInt(periodoMes) + 1) + "/"
								+ periodoAnio);
						new ProcesaArchivosFarmacia().actualizarVademecum(periodoArchivo,user);
					} catch (NumberFormatException e) {
						logger.error(e);
					} catch (Exception e) {
						logger.error(e);
					}
				}
				
			}
                                                                                                          
		} catch (Exception e) {
			logger.error(e);
			SessionErrors.add(actionRequest, e.getClass().getName());                                           
		}
		if ((null!=erroresManual && !erroresManual.isEmpty()) || (null!= erroresListadosss && !erroresListadosss.isEmpty())) {
			if(null!=erroresManual && !erroresManual.isEmpty()){
				ErrorProcesandoArchivosMedicamentosException epame= new ErrorProcesandoArchivosMedicamentosException();
				SessionErrors.add(actionRequest, epame.getClass().getName());
			}else if(null!=erroresListadosss && !erroresListadosss.isEmpty()){
				ErrorProcesandoArchivosListadoSSSaludException epas= new ErrorProcesandoArchivosListadoSSSaludException();
				SessionErrors.add(actionRequest, epas.getClass().getName());
			}else {			                                 
				ErrorProcesandoArchivosVademecumException e = new ErrorProcesandoArchivosVademecumException();
				SessionErrors.add(actionRequest, e.getClass().getName());
			}
		}                                                                                                 
		if (SessionErrors.isEmpty(actionRequest)) {                                                           
			String successMessage = ParamUtil.getString(actionRequest, "successMessage");                                                                              
			SessionMessages.add(actionRequest, "request_processed", successMessage);
			actionRequest.setAttribute(WebKeysFarmaciaOspim.VADEMECUM_PROCESADO , "Si");
		}                     
		setForward(actionRequest, "portlet.farmaciaospim.view");
		actionRequest.setAttribute("tabs1", "subir-archivo-vademecum");
		
		
	}                                                                                                       
                                                                                                          
	private List<String> procesarFile(ActionRequest actionRequest, FileInputStream file,                    
			String tipoArchivo) throws IOException {
		BufferedReader reader=null;
		List<String> errores = new ArrayList<String>();
		try {
			if (tipoArchivo.trim().equals("manual")
//					|| tipoArchivo.trim().equals("todos")
					) {
				ZipInputStream in = new ZipInputStream(file);
				ZipEntry entry = in.getNextEntry();
				reader = new BufferedReader(new InputStreamReader(in,"ISO-8859-1"));
				new ProcesaArchivosFarmacia().procesarArchivoManualDat(reader);                              
			} else if (tipoArchivo.trim().equals("listadosssalud")) {
//				reader = new BufferedReader(new InputStreamReader(file,"ISO-8859-1"));
				new ProcesaArchivosFarmacia().procesarArchivoListadoSSSalud(file);
			}
		} catch (Exception e) {
			logger.debug("Error al procesar archivo " + tipoArchivo, e);
			errores.add(tipoArchivo);
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
		
		return errores;
	}
                                                                                                          
                                                                                                          
	public ActionForward render(ActionMapping mapping, ActionForm form,                                     
			PortletConfig portletConfig, RenderRequest renderRequest,                                           
			RenderResponse renderResponse) throws Exception {
                                                                                                          
		return mapping.findForward(getForward(renderRequest,                                                  
				"portlet.tesoreria.view"));                                                                       
	}                                                                                                       
                                                                                                          
}                                                                                                         
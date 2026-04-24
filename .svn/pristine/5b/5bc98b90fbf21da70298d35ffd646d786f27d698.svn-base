package ar.com.ospim.novedades.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.novedades.beans.ErrorProcesandoArchivosNovedadesException;
import ar.com.ospim.novedades.exception.PeriodoArchivoDuplicadoException;
import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.util.DateUtils;

public class UploadArchivoNovedadAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoNovedadAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);
		
		Date fechaArchivo = null;
		try {
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
			String fechaArchivoDia = ParamUtil.getString(actionRequest,"fechaArchivoDia");
			String fechaArchivoMes = ParamUtil.getString(actionRequest,"fechaArchivoMes");
			String fechaArchivoAnio = ParamUtil.getString(actionRequest,"fechaArchivoAnio");
				fechaArchivo = formatoDeFechas.parse(fechaArchivoDia + "/"
						+ (Integer.parseInt(fechaArchivoMes) + 1) + "/"
						+ fechaArchivoAnio);
		} catch (Exception e) {
			fechaArchivo = null;
		}
		
		List<String> errores = null;
		try {
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
			if (fileName != null) {
				File archivo = uploadReq.getFile("archivo");
				if (fileName.endsWith(".zip")) {
					errores = procesarZip(actionRequest, archivo);
				} else if (fileName.endsWith(".csv")) {
					errores = procesarCsv(actionRequest, archivo, fechaArchivo, fileName);
				} else if (fileName.startsWith("112608")
						&& fileName.endsWith(".txt")) {
					errores = procesarTxt(actionRequest, archivo, fechaArchivo, fileName);
				} else if ((fileName.startsWith("112608")
						&& fileName.endsWith(".err")) || (fileName.toLowerCase().contains("rechazados") && fileName.endsWith(".txt")  )) {
					errores = procesarTxt(actionRequest, archivo, fechaArchivo, fileName);
				}else if (fileName.startsWith("a112608") || fileName.startsWith("am112608") || fileName.startsWith("pc112608")
						&& fileName.endsWith(".txt")) {
					errores = procesarTxt(actionRequest, archivo, fechaArchivo, fileName);				
				} else if (fileName.startsWith("novedades_sss_112608")
						&& fileName.endsWith(".txt")) {
					errores = procesarTxt(actionRequest, archivo, fechaArchivo, fileName);
				}else if (fileName.startsWith("delegaciones")
							&& fileName.endsWith(".txt")) {
					errores = procesarTxt(actionRequest, archivo, fechaArchivo, fileName);
				}else if (fileName.startsWith("bajasporopcion")
						&& fileName.endsWith(".xls")) {
					errores = procesarXLS(actionRequest, fechaArchivo, archivo, fileName);
				}
				
					
						
			}		
		} catch (Exception e) {
			
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		if (null!=errores && !errores.isEmpty()) {
			actionRequest.setAttribute("errores", errores);
			ErrorProcesandoArchivosNovedadesException e = new ErrorProcesandoArchivosNovedadesException();
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
		
		
		if (actionResponse.getNamespace().equals("_AFI_1_")){
			setForward(actionRequest, "portlet.novedades.view");
			actionRequest.setAttribute("tabs1", "subir-archivo");
		}
	}

	
	
	private List<String> procesarXLS(ActionRequest actionRequest, Date fechaArchivo, File archivo,String fileName) throws Exception {
		List<String> errores = new ArrayList<String>();
		Date FechaFinMes = null;
		User user = null;
		try {
			user = PortalUtil.getUser(actionRequest);
		} catch (PortalException e1) {
			user = null;
		} catch (SystemException e1) {
			user=null;
		}
		FechaFinMes = DateUtils.getLastDateOfMonth(fechaArchivo, true);
		
		try {
			new ProcesaArchivos().procesarXLSBajasPorOpcion(actionRequest, FechaFinMes, archivo, fileName, user,  errores);
		}catch (Exception e) {
			errores.add(archivo.getName()+" "+e.getCause()+" "+e.getLocalizedMessage()+" "+e.getMessage());
			return errores;
		}
		
		
		
		return errores;
	}
	
	
	private List<String> procesarCsv(ActionRequest actionRequest, File archivo, Date fechaArchivo, String fileName)
			throws IOException {

		User user = null;
		try {
			user = PortalUtil.getUser(actionRequest);
		} catch (PortalException e1) {
			user = null;
		} catch (SystemException e1) {
			user=null;
		}
		
		List<String> errores = new ArrayList<String>();

		FileInputStream in = new FileInputStream(archivo);
		String upperName = archivo.getName().toUpperCase();
		logger.debug(upperName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		try {
			if (fileName.toUpperCase().startsWith("OSPIM_") && fileName.endsWith(".csv")){
				new ProcesaArchivos().procesarArchivoAfiliacionPrevencion(reader, fechaArchivo, user);			
			}

		} catch (Exception e) {
			if (e instanceof PeriodoArchivoDuplicadoException) {
				SessionErrors.add(actionRequest, e.getClass().getName());
			}else{
				logger.debug("Error al procesar archivo " + archivo.getName(), e);
				errores.add(archivo.getName()+" "+e.getCause()+" "+e.getLocalizedMessage()+" "+e.getMessage());
			}	
		}

		in.close();
		return errores;
	}
	
	private List<String> procesarTxt(ActionRequest actionRequest, File archivo, Date fechaArchivo, String fileName)
			throws IOException {

		User user = null;
		try {
			user = PortalUtil.getUser(actionRequest);
		} catch (PortalException e1) {
			user = null;
		} catch (SystemException e1) {
			user=null;
		}
		
		List<String> errores = new ArrayList<String>();

		FileInputStream in = new FileInputStream(archivo);
		String upperName = archivo.getName().toUpperCase();
		logger.debug(upperName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		try {
			if(fileName.startsWith("112608") && fileName.endsWith(".err")){
				new ProcesaArchivos().procesarArchivoErroresSS(reader,0);
			}if (fileName.toLowerCase().contains("rechazados") && fileName.endsWith(".txt")){
				new ProcesaArchivos().procesarArchivoErroresSS(reader,1);				
			}else if (fileName.startsWith("a112608") && fileName.endsWith(".txt")) {
				new ProcesaArchivos().procesarArchivoOpcionesSSS(reader, fechaArchivo, user);
			}else if (fileName.startsWith("am112608") && fileName.endsWith(".txt")) {
				new ProcesaArchivos().procesarArchivoOpcionesMonotrib(reader, fechaArchivo, user);
			}else if (fileName.startsWith("novedades_sss_112608") && fileName.endsWith(".txt")) {
				new ProcesaArchivos().procesarArchivoNovedades(reader, fechaArchivo, user);
			}else if (fileName.startsWith("delegaciones") && fileName.endsWith(".txt")) {
				new ProcesaArchivos().procesarArchivoDelegaciones112608SSS(reader, fechaArchivo, user);
			}else if (fileName.startsWith("pc112608") && fileName.endsWith(".txt")) {
				new ProcesaArchivos().procesarArchivoNovedadesPadronConsolidado(reader, fechaArchivo, user);
			}

		} catch (Exception e) {
			if (e instanceof PeriodoArchivoDuplicadoException) {
				SessionErrors.add(actionRequest, e.getClass().getName());
			}else{
				logger.debug("Error al procesar archivo " + archivo.getName(), e);
				errores.add(archivo.getName()+" "+e.getCause()+" "+e.getLocalizedMessage()+" "+e.getMessage());
			}	
		}

		in.close();
		return errores;
	}

//	private List<String> procesarTxt(ActionRequest actionRequest, File zip, Date fechaArchivo, String fileName)
//			throws IOException {
//
//		User user = null;
//		try {
//			user = PortalUtil.getUser(actionRequest);
//		} catch (PortalException e1) {
//			user = null;
//		} catch (SystemException e1) {
//			user=null;
//		}
//		
//		List<String> errores = new ArrayList<String>();
//
//		FileInputStream in = new FileInputStream(zip);
//		String upperName = zip.getName().toUpperCase();
//		logger.debug(upperName);
//		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
//		try {
//			if (fileName.startsWith("novedades_sss_112608") && fileName.endsWith(".txt")) {
//				new ProcesaArchivos().procesarArchivoNovedades(reader, fechaArchivo, user);
//			}
//
//		} catch (Exception e) {
//			logger.debug("Error al procesar archivo " + zip.getName(), e);
//			errores.add(zip.getName()+" "+e.getCause()+" "+e.getLocalizedMessage()+" "+e.getMessage());
//		}
//
//		in.close();
//		return errores;
//	}
	private List<String> procesarZip(ActionRequest actionRequest, File zip)
			throws IOException {

		List<String> errores = new ArrayList<String>();
		ZipInputStream in = new ZipInputStream(new FileInputStream(zip));
		ZipEntry entry = in.getNextEntry();
		while (entry != null) {
			String upperName = entry.getName().toUpperCase();
			logger.debug(upperName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));
			try {
				if (upperName.startsWith("B2")) {
					new ProcesaArchivos()
					.procesarArchivoBajaOpciones(reader);
				} else if (upperName.startsWith("A112608")) {
					new ProcesaArchivos()
					.procesarArchivoAltasOpciones(reader);	
				} else if (upperName.startsWith("AM112608")) {
					new ProcesaArchivos()
					.procesarArchivoAltasOpcionesMonotrib(reader);	
				}
			}catch (Exception e) {
				logger.debug("Error al procesar archivo " + entry.getName(), e);
				errores.add(entry.getName());
			}
			entry = in.getNextEntry();
		}
		in.close();
		return errores;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.novedades.view"));
	}

}

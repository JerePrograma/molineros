package ar.com.ospim.estudioisidro.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class UploadArchivoEmpresasAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivoEmpresasAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);
		List<String> errores = null;
		Boolean proceso=false;
		try {
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
			if (fileName != null) {
				File zip = uploadReq.getFile("archivo");
				if ( (fileName.toUpperCase().startsWith("LOTE_EMPRESA")	) 
						&& fileName.toUpperCase().endsWith(".CSV")) {
					proceso=true;
					errores = procesarTxt(actionRequest, zip, fileName);					
				}		
			}		
		} catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		if (null!=errores && !errores.isEmpty()) {
			actionRequest.setAttribute("errores", errores);
			ErrorProcesandoArchivosAfipException e = new ErrorProcesandoArchivosAfipException();
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		
		if (SessionErrors.isEmpty(actionRequest) && !proceso) {
			errores= new ArrayList<String>();
			errores.add("No se proceso el archivo solicitado");
			actionRequest.setAttribute("errores", errores);
			ErrorProcesandoArchivosAfipException e = new ErrorProcesandoArchivosAfipException();
			SessionErrors.add(actionRequest,e.getClass().getName());
		}
		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
		
		setForward(actionRequest, "portlet.estudio_isidro.view");
		actionRequest.setAttribute("tabs1", "subir-archivo");
		
		
	}

	private List<String> procesarTxt(ActionRequest actionRequest, File zip, String fileName)
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

		FileInputStream in = new FileInputStream(zip);
		String upperName = zip.getName().toUpperCase();
		logger.debug(upperName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		try {
			if (fileName.toUpperCase().startsWith("LOTE_EMPRESA") && fileName.toUpperCase().endsWith(".CSV")) {
				if(LlamadoServiceUtil.cierreAutomaticoLotes()){
				  new ProcesaArchivos().procesarArchivoLoteEmpresa(reader,user);
				}  
			}
			
		} catch (Exception e) {
			logger.debug("Error al procesar archivo " + zip.getName(), e);
			errores.add(zip.getName()+" "+e.getMessage());
		}

		in.close();
		return errores;
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.estudioisidro.view"));
	}

}

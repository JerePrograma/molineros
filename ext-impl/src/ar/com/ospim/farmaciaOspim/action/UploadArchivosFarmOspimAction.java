package ar.com.ospim.farmaciaOspim.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.procesaArchivos.exception.ArchivoMedEsIncorrectoException;

public class UploadArchivosFarmOspimAction extends PortletAction {
	
	private static Log logger = LogFactoryUtil
			.getLog(UploadArchivosFarmOspimAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		UploadPortletRequest uploadReq = PortalUtil
				.getUploadPortletRequest(actionRequest);
		
		SessionErrors.clear(actionRequest);
		logger.debug("Subiendo Farmacia...");
		List<String> errores = null;		
		try {
			String fileName = uploadReq.getFileName("archivo").toLowerCase();
			if (fileName != null) {
				File filecsv = uploadReq.getFile("archivo");
				if (fileName.endsWith(".csv")) {										
					errores = procesarTxt(actionRequest, filecsv, fileName);															
				}else{					   
					   SessionErrors.add(actionRequest, "errormedespecialarchivonombre");
				}
			}		
		}  catch(ArchivoMedEsIncorrectoException  e1 ){
			SessionErrors.add(actionRequest, e1.getClass().getName());
		}
		   catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
		if (null!=errores && !errores.isEmpty()) {	
			SessionErrors.add(actionRequest, "error-medespecial");
		}					
		setForward(actionRequest, "portlet.farmaciaospim.view");
		actionRequest.setAttribute("tabs1", "subir-archivo");
	}

	private List<String> procesarTxt(ActionRequest actionRequest, File filecsv, String fileName)
			throws ArchivoMedEsIncorrectoException , IOException {

		User user = null;
		try {
			user = PortalUtil.getUser(actionRequest);
		} catch (PortalException e1) {
			user = null;
		} catch (SystemException e1) {
			user=null;
		}	
			
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
		
		List<String> errores = new ArrayList<String>();

		FileInputStream in = new FileInputStream(filecsv);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		try {
			
			if ((fileName.toUpperCase().startsWith("MEDESP") && fileName.toUpperCase().endsWith(".CSV"))) {
				
					new ProcesaArchivos().procesarArchivoMedEsp	(user, reader,fechaArchivo); 
						SessionErrors.clear(actionRequest);						
						SessionMessages.add(actionRequest, "request_processed" );    														
			}else{			
				SessionErrors.add(actionRequest, "errormedespecialarchivonombre");				
			}
			
		} catch (ArchivoMedEsIncorrectoException e) {
			String msg =e.getMessage() ;
			switch ( Integer.parseInt(msg) )			{
				case 1:
					SessionErrors.add(actionRequest, "errormedespecialperiodonocoincide");
					break;
				case 2:
					SessionErrors.add(actionRequest, "errormedespecialarchivonombre");
					break;
				case 3:
					SessionErrors.add(actionRequest, "errormedespecialperiodoyaprocesado");
					break;
				case 4:
					SessionErrors.add(actionRequest, "errormedespecialdatosdentrodearchivo");	
					break;
			}
			
									
			
		} catch (ParseException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally{
		
			in.close();
		}
		return errores;
	}

	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.farmaciaospim.view"));
	}

}

package ar.com.ospim.farmaciaOspim.action;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.portlet.*;

import org.apache.struts.action.*;

import com.liferay.portal.*;
import com.liferay.portal.kernel.log.*;
import com.liferay.portal.kernel.servlet.*;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.procesaArchivos.exception.ArchivoAdmifarmIncorrectoException;

public class UploadAdmifarmAction extends PortletAction {

	private static Log logger = LogFactoryUtil.getLog(UploadAdmifarmAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);

		SessionErrors.clear(actionRequest);

		try {

			String fileName = uploadReq.getFileName("archivo");

			if (fileName != null) {

				File filecsv = uploadReq.getFile("archivo");

				if (fileName.toUpperCase().endsWith(".CSV")) {

					processAdmifarm(actionRequest, filecsv, fileName);

				} else {
					SessionErrors.add(actionRequest, "erroradmifarmarchivonombre");
				}
			}

		} catch (ArchivoAdmifarmIncorrectoException ex) {

			handleAdmifarmException(actionRequest, ex);

		} catch (Exception ex) {
			logger.error(ex);
			SessionErrors.add(actionRequest, "erroradmifarmdesconocido");
		}

		setForward(actionRequest, "portlet.farmaciaospim.view");
	}

	private void processAdmifarm(ActionRequest actionRequest, File filecsv, String fileName)
	        throws Exception {

	    User user = PortalUtil.getUser(actionRequest);

	    Date fechaArchivo = parseFechaArchivo(actionRequest);

	    BufferedReader reader = new BufferedReader(new InputStreamReader(
	            new FileInputStream(filecsv), "UTF-8"));
	    
	    if (fileName.toUpperCase().startsWith("CONSUMOMONOTRIBUTO")) {

	        new ProcesaArchivos().procesarArchivoAdmifarm(user, reader, fechaArchivo);

	        SessionErrors.clear(actionRequest);
	        SessionMessages.add(actionRequest, "request_processed_admifarm");
	        SessionMessages.add(actionRequest, "sel_proceso_admifarm");

	    } else {
	        SessionErrors.add(actionRequest, "erroradmifarmarchivonombre");
	    }

	    reader.close();
	}


	private Date parseFechaArchivo(ActionRequest actionRequest) {

	    try {
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	        String d = ParamUtil.getString(actionRequest, "fechaAdmifarmDia");
	        int m = ParamUtil.getInteger(actionRequest, "fechaAdmifarmMes");
	        String y = ParamUtil.getString(actionRequest, "fechaAdmifarmAnio");
	        
	        m = m + 1;

	        return sdf.parse(d + "/" + m + "/" + y);

	    } catch (Exception e) {
	        return null;
	    }
	}


	private void handleAdmifarmException(ActionRequest actionRequest,
			ArchivoAdmifarmIncorrectoException e) {

		switch (e.getCode()) {
		case 1:
			SessionErrors.add(actionRequest, "erroradmifarmperiodonocoincide");
			break;
		case 2:
			SessionErrors.add(actionRequest, "erroradmifarmarchivonombre");
			break;
		case 3:
			SessionErrors.add(actionRequest, "erroradmifarmperiodoyaprocesado");
			break;
		case 4:
			SessionErrors.add(actionRequest, "erroradmifarmdatosdentrodearchivo");
			break;
		default:
			SessionErrors.add(actionRequest, "erroradmifarmdesconocido");
			break;
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.farmaciaospim.view"));
	}
	
	
}

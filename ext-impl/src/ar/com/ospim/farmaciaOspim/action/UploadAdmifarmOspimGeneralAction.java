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
import ar.com.ospim.procesaArchivos.exception.ArchivoAdmifarmGeneralOspimIncorrectoException;

public class UploadAdmifarmOspimGeneralAction extends PortletAction {

	private static Log logger = LogFactoryUtil.getLog(UploadAdmifarmOspimGeneralAction.class);

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

					processAdmifarmOspimGeneral(actionRequest, filecsv, fileName);

				} else {
					SessionErrors.add(actionRequest, "erroradmifarmgeneralarchivonombre");
				}
			}

		} catch (ArchivoAdmifarmGeneralOspimIncorrectoException ex) {

			handleAdmifarmException(actionRequest, ex);

		} catch (Exception ex) {
			logger.error(ex);
			SessionErrors.add(actionRequest, "erroradmifarmgeneraldesconocido");
		}

		setForward(actionRequest, "portlet.farmaciaospim.view");
	}

	private void processAdmifarmOspimGeneral(ActionRequest actionRequest, File filecsv, String fileName)
	        throws Exception {

	    User user = PortalUtil.getUser(actionRequest);

	    Date fechaArchivo = parseFechaArchivo(actionRequest);

	    BufferedReader reader = new BufferedReader(new InputStreamReader(
	            new FileInputStream(filecsv), "UTF-8"));
	    
	    if (fileName.toUpperCase().startsWith("CONSUMOOSPIMGENERAL")) {

	        new ProcesaArchivos().procesarArchivoAdmifarmOspimGeneral(
	        		user,
	        		reader,
	        		fechaArchivo
	        );
	        
	        SessionErrors.clear(actionRequest);
	        SessionMessages.add(actionRequest, "request_processed_admifarm_general");
	        SessionMessages.add(actionRequest, "sel_proceso_general");

	    } else {
	        SessionErrors.add(actionRequest, "erroradmifarmgeneralarchivonombre");
	    }

	    reader.close();
	}


	private Date parseFechaArchivo(ActionRequest actionRequest) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sdf.setLenient(false);

			String d = ParamUtil.getString(
					actionRequest,
					"fechaAdmifarmGeneralDia"
			);

			int m = ParamUtil.getInteger(
					actionRequest,
					"fechaAdmifarmGeneralMes"
			);

			String y = ParamUtil.getString(
					actionRequest,
					"fechaAdmifarmGeneralAnio"
			);

			m = m + 1;

			return sdf.parse(d + "/" + m + "/" + y);

		} catch (Exception e) {
			logger.error("Error interpretando fecha Admifarm Ospim General", e);
			return null;
		}
	}


	private void handleAdmifarmException(ActionRequest actionRequest,
			ArchivoAdmifarmGeneralOspimIncorrectoException e) {

		switch (e.getCode()) {
		case 1:
			SessionErrors.add(actionRequest, "erroradmifarmgeneralperiodonocoincide");
			break;
		case 2:
			SessionErrors.add(actionRequest, "erroradmifarmgeneralarchivonombre");
			break;
		case 3:
			SessionErrors.add(actionRequest, "erroradmifarmgeneralperiodoyaprocesado");
			break;
		case 4:
			SessionErrors.add(actionRequest, "erroradmifarmgeneraldatosdentrodearchivo");
			break;
		default:
			SessionErrors.add(actionRequest, "erroradmifarmgeneraldesconocido");
			break;
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		return mapping.findForward(getForward(renderRequest, "portlet.farmaciaospim.view"));
	}
	
	
}

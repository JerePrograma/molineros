package ar.com.uoma.action;

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

import ar.com.ospim.afip.beans.ArchivoSubidoBco;
import ar.com.ospim.afip.service.AfipServiceUtil;

import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarArchivosBcoAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.buscar.archivos.banco");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaUltimoArchivoDia = ParamUtil.getString(renderRequest,"fechaUltimoArchivoDia");
		String fechaUltimoArchivoMes = ParamUtil.getString(renderRequest,"fechaUltimoArchivoMes");
		String fechaUltimoArchivoAnio = ParamUtil.getString(renderRequest,"fechaUltimoArchivoAnio");
			Date fechaArchivo = null;
				try {
					fechaArchivo = formatoDeFechas.parse(fechaUltimoArchivoDia + "/"
							+ (Integer.parseInt(fechaUltimoArchivoMes) + 1) + "/"
							+ fechaUltimoArchivoAnio);
				} catch (Exception e) {
					fechaArchivo = null;
				}
		String porletName = renderResponse.getNamespace();
		
		if (porletName == "_FAR_1_" ){
			List<ArchivoSubidoBco> archivos = new ArrayList<ArchivoSubidoBco>();
			archivos = AfipServiceUtil.getArchivosSubidosBcoAMTIMA(fechaArchivo);	
			renderRequest.removeAttribute("archivoSubidoBco");
			renderRequest.setAttribute("archivoSubidoBco", archivos);
		}
		
		else {
			List<ArchivoSubidoBco> archivos = new ArrayList<ArchivoSubidoBco>();
			archivos = AfipServiceUtil.getArchivosSubidosBcoUOMA(fechaArchivo);	
			renderRequest.removeAttribute("archivoSubidoBco");
			renderRequest.setAttribute("archivoSubidoBco", archivos);
		}

			return mapping.findForward("portlet.buscar.archivos.banco");
		
	}

}
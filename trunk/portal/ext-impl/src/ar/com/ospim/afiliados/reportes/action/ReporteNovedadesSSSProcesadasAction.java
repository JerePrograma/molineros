package ar.com.ospim.afiliados.reportes.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.novedades.beans.ArchivoNovedad;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;

import com.liferay.portal.struts.PortletAction;

public class ReporteNovedadesSSSProcesadasAction extends PortletAction {			   
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		ArrayList<ArchivoNovedad> archivosProc = (ArrayList<ArchivoNovedad>) TraeListasServiceUtil.getFechasArchivosNovedades(WebKeysAfiliados.TIPOS_ORIGEN[3]);
		List<ReporteNovedadesSSSProcesadas> reportes = (ArrayList<ReporteNovedadesSSSProcesadas>) NovedadesServiceUtil.getInstance().getReportesNovedadesSSSProcesadas();
		
		renderRequest.setAttribute("archivosNovedades", archivosProc);
		renderRequest.setAttribute("reportesNovedSSSProc",reportes); 
		
		return mapping.findForward("portlet.afiliados.reportes.estadistica_novedades_procesadas");									 
	}
}
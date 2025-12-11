package ar.com.ospim.tesoreria.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.reportes.beans.UltimosProcesosSisOld;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class ReportePortalAction extends PortletAction {
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
				
		TraeListasServiceUtil.getConvenioNac(renderRequest);
		TraeListasServiceUtil.getCuentasNac(renderRequest);
		
		List<UltimosProcesosSisOld> archivos = new ArrayList<UltimosProcesosSisOld>();
		archivos = TraeListasServiceUtil.getUltimosProcesosSisOld(fechaArchivo);	
		renderRequest.removeAttribute("ultimosProcesosSisOld");
		renderRequest.setAttribute("ultimosProcesosSisOld", archivos);
		
		return mapping.findForward("portlet.tesoreria.boletas_portal_empleadores");
	}
}

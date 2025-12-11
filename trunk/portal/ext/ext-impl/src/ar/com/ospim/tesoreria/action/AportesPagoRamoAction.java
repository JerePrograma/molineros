package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.beans.ProcesoSQL;
import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AportesPagoRamoAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(AportesPagoRamoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		PortletSession session = renderRequest.getPortletSession();
		
		Integer idJob =Integer.parseInt(TraeListasServiceUtil.getSystemConfig("reporte.aportes_pago_ramo"));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		int fechaDesdeDia = ParamUtil.getInteger(renderRequest, "fechaDesdeDia");
		int fechaDesdeMes = ParamUtil.getInteger(renderRequest, "fechaDesdeMes")+1;
		
		String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechaDesdeAnio");
		
		int fechaHastaDia = ParamUtil.getInteger(renderRequest, "fechaHastaDia");
		int fechaHastaMes = ParamUtil.getInteger(renderRequest, "fechaHastaMes")+1;
		
		String fechaHastaAnio = ParamUtil.getString(renderRequest, "fechaHastaAnio");
		List<String>parameters = new ArrayList<String>();
		
		try {
			String fechaIni = fechaDesdeAnio+String.format("%02d", fechaDesdeMes)+String.format("%02d", fechaDesdeDia) ;
			String fechaFin = fechaHastaAnio +String.format("%02d", fechaHastaMes)+String.format("%02d", fechaHastaDia);
			parameters.add(fechaIni);
			parameters.add(fechaFin);
		}catch(Exception e){}
		
		SchedulerServiceUtil.addParameters("reporte.aportes_pago_ramo", idJob, parameters);
		
		SchedulerServiceUtil.run(idJob);
		
		return mapping
				.findForward("portlet.tesoreria.reporte.aportes_pago_ramo");
		
		//return mapping
		//		.findForward("portlet.tesoreria.reporte.aportes_pago_ramo_result");
	}

}

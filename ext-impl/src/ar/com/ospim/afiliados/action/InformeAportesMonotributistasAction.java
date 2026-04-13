package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class InformeAportesMonotributistasAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(InformeAportesMonotributistasAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		PortletSession session = renderRequest.getPortletSession();
		
		Integer idJob =Integer.parseInt(TraeListasServiceUtil.getSystemConfig("reporte.informe_aportes_monotributistas"));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		int fechaDesdeDia = ParamUtil.getInteger(renderRequest, "fechaDesdeDia");
		int fechaDesdeMes = ParamUtil.getInteger(renderRequest, "fechaDesdeMes")+1;
		
		String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechaDesdeAnio");
		
		List<String>parameters = new ArrayList<String>();
		
		Calendar calendar = new GregorianCalendar(Integer.parseInt(fechaDesdeAnio),fechaDesdeMes-1,fechaDesdeDia);
		
		Date fechaHta=DateUtils.getLastDateOfMonth(calendar.getTime(), false);
		
		try {
			String fechaIni = fechaDesdeAnio+String.format("%02d", fechaDesdeMes)+String.format("%02d", fechaDesdeDia) ;
			String fechaFin = fechaDesdeAnio +String.format("%02d", fechaDesdeMes)+String.format("%02d", fechaHta.getDate());
			parameters.add(fechaIni);
			parameters.add(fechaFin);
		}catch(Exception e){}
		
		SchedulerServiceUtil.addParameters("reporte.informe_aportes_monotributistas", idJob, parameters);
		
		SchedulerServiceUtil.run(idJob);
		
		return mapping
				.findForward("portlet.afiliados.reporte.informe_aportes_monotributo");
	}

}

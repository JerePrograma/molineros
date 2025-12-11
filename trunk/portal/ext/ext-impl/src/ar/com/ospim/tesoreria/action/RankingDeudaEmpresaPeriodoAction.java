package ar.com.ospim.tesoreria.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;

public class RankingDeudaEmpresaPeriodoAction extends PortletAction {
	
	public static final String reporte_system_config = "reporte.ranking_deuda_empresa_periodo";
	
	private static Log logger = LogFactoryUtil
			.getLog(RankingDeudaEmpresaPeriodoAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
//		PortletSession session = renderRequest.getPortletSession();
		
		Integer idJob =Integer.parseInt(TraeListasServiceUtil.getSystemConfig(reporte_system_config));
		
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
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
		
		SchedulerServiceUtil.addParameters(reporte_system_config, idJob, parameters);
		
		SchedulerServiceUtil.run(idJob);
		
		return mapping
				.findForward("portlet.tesoreria.reporte.ranking_deuda_empresa_periodo_result");
	}

}

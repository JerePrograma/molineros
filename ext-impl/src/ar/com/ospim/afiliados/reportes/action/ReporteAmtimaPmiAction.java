package ar.com.ospim.afiliados.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.reportes.ReportesAmtimaPmiServiceImpl;
import ar.com.ospim.afiliados.reportes.beans.ReporteAmtimaPMI;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class ReporteAmtimaPmiAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteAmtimaPmiAction.class);
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(renderRequest, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(renderRequest, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(renderRequest, "fechaHastaAnio");
		Boolean soloConyuges=ParamUtil.getBoolean(renderRequest, "solo_conyuges");
		
		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			ReportesAmtimaPmiServiceImpl service=new ReportesAmtimaPmiServiceImpl();
			ArrayList<ReporteAmtimaPMI> lista= (ArrayList<ReporteAmtimaPMI>)service.getReporteAmtimaPMI(fechaIni,fechaFin,soloConyuges);
			renderRequest.setAttribute(WebKeysAfiliados.REPORTE_AMTIMA_PMI, lista);
		
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}

		return mapping.findForward("portlet.afiliados.reporte_amtima_pmi");
	}
}
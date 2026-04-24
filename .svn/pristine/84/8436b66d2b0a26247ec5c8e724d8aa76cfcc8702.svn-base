package ar.com.ospim.tesoreria.reportes.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afip.beans.ReporteDeudaEmpresa;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class ReporteDeudaEmpresaPeriodoAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {

		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaPagoDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaPagoMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaPagoMes = String.valueOf(Integer.valueOf(fechaPagoMes) + 1);
		String fechaPagoAnio = ParamUtil.getString(req, "fechaHastaAnio");
		
		int ramo_desde=ParamUtil.getInteger(req, "ramo_desde");
		int ramo_hasta=ParamUtil.getInteger(req, "ramo_desde");
		
		boolean agrupar_remuneracion=ParamUtil.getBoolean(req, "agrupar_remuneracion");
		boolean sin_deuda=ParamUtil.getBoolean(req, "sin_deuda");
		
		Date fechaIni = format.parse(fechaInicioDia + "-" + fechaInicioMes
				+ "-" + fechaInicioAnio);
		Date fechaFin = format.parse(fechaPagoDia + "-" + fechaPagoMes + "-"
				+ fechaPagoAnio);

		req.setAttribute("agrupar_remuneracion",agrupar_remuneracion);
		
		List<ReporteDeudaEmpresa> reporte = AfipServiceUtil.getReporteDeudaEmpresaPeriodo(fechaIni, fechaFin, sin_deuda, ramo_desde, ramo_hasta);
		req.setAttribute(WebKeysTesoreria.REPORTE_DEUDA_EMPRESA_PERIODO,
				reporte);
		
		

		return mapping.findForward(getForward(req,
				"portlet.tesoreria.reporte.deuda.empresa.periodo.result"));
	}
}

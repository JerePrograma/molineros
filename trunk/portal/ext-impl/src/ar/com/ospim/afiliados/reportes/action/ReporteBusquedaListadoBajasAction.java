package ar.com.ospim.afiliados.reportes.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Baja;
import ar.com.ospim.afiliados.reportes.ReportesAfiliadoServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class ReporteBusquedaListadoBajasAction extends PortletAction {
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

		Date fechaIni = format.parse(fechaInicioDia + "-" + fechaInicioMes
				+ "-" + fechaInicioAnio);
		Date fechaFin = format.parse(fechaPagoDia + "-" + fechaPagoMes + "-"
				+ fechaPagoAnio);

		List<Baja> reporte = ReportesAfiliadoServiceUtil.getReporteListadoBajas(
				fechaIni, fechaFin);

		req.setAttribute(WebKeysAfiliados.AFILIADO_BAJA, reporte);

		return mapping.findForward(getForward(req,
				"portlet.afiliados.reportes.listado_bajas.result"));
	}
}
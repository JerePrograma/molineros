package ar.com.ospim.automatico.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.reportes.beans.ReporteNovedadesSSSProcesadas;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaListado;
import ar.com.ospim.automatico.service.AgendaReporteUtil;
import ar.com.ospim.automatico.service.ReportesServiceImpl;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class AgendarReporteAction extends JSONAction {

	private AgendaReporteUtil service = new AgendaReporteUtil();
	private String reporte = "";
	
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		reporte = ParamUtil.getString(req, "tipoReporte");
		int respuesta = 0;
		
		if(reporte.equalsIgnoreCase(ReporteDeudaEmpresaListado.REPORTE_DEUDA_EMPRESAS_PERIODO)){
			respuesta = agendarReporteDeudaEmpresaPeriodo(req);
		}
		 
		if(reporte.equalsIgnoreCase(ReporteNovedadesSSSProcesadas.REPORTE_NOVEDADES_SSS_PROCESADAS)){
			respuesta = agendarReporteNovedadesSSSProcesadas(req);
		}
		
		
		return "{ \"validado\" : \"" + String.valueOf(respuesta) + "\"}";
	}

	private int agendarReporteDeudaEmpresaPeriodo(HttpServletRequest req) throws ParseException, SystemException, PortalException {
		
		int idRepoAutom = 0;
		
		User user = PortalUtil.getUser(req);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaInicioDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaInicioMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaInicioMes = String.valueOf(Integer.valueOf(fechaInicioMes) + 1);
		String fechaInicioAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
		
		Integer ramo_desde=ParamUtil.getInteger(req, "ramo_desde");
		Integer ramo_hasta=ParamUtil.getInteger(req, "ramo_hasta");
		
		if(ramo_desde == 0){ ramo_desde = null;}
		if(ramo_hasta == 0){ ramo_hasta = null;}
		
		boolean agrupar_remuneracion=ParamUtil.getBoolean(req, "agrupar_remuneracion");
		boolean sin_deuda=ParamUtil.getBoolean(req, "sin_deuda");
		
		Date fechaIni = format.parse(fechaInicioDia + "-" + fechaInicioMes
				+ "-" + fechaInicioAnio);
		Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes + "-"
				+ fechaHastaAnio);

		idRepoAutom = service.agendarRepoDeudaEmpPeriodo(reporte, user.getScreenName(), fechaIni, fechaFin, 
				ramo_desde, ramo_hasta, agrupar_remuneracion, sin_deuda);

		List<ReporteDeudaEmpresaCab> lista = ReportesServiceImpl.getInstance().getReportesDeudaEmpPeriodo();
		
		req.setAttribute("reportesDeuEmpPeriodo", lista);
		
		return idRepoAutom;
	}
	
	private int agendarReporteNovedadesSSSProcesadas(HttpServletRequest req) throws ParseException, SystemException, PortalException {
		
		int idRepoAutom = 0;
		
		User user = PortalUtil.getUser(req);
				
		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
		String fechaNovedades = null;
		
		String fechaProcesoDia = ParamUtil.getString(req, "fechaProcesoDia");
		String fechaProcesoMes = ParamUtil.getString(req, "fechaProcesoMes");
		String fechaProcesoAnio = ParamUtil.getString(req, "fechaProcesoAnio");
		
		String fechaPadronDesdeDia = ParamUtil.getString(req, "fechaPadronDesdeDia");
		String fechaPadronDesdeMes = ParamUtil.getString(req, "fechaPadronDesdeMes");
		String fechaPadronDesdeAnio = ParamUtil.getString(req, "fechaPadronDesdeAnio");
		
		String fechaPadronHastaDia = ParamUtil.getString(req, "fechaPadronHastaDia");
		String fechaPadronHastaMes = ParamUtil.getString(req, "fechaPadronHastaMes");
		String fechaPadronHastaAnio = ParamUtil.getString(req, "fechaPadronHastaAnio");
		
		boolean informar = ParamUtil.getBoolean(req, "informar");
		
		Date fechaProceso = null;
		Date fechaDesde = null;
		Date fechaHasta = null;
		Calendar fechaNovedadSSS = Calendar.getInstance();
		
		try {
			fechaProceso = formatoDeFechas.parse(fechaProcesoDia + "/"
					+ (Integer.parseInt(fechaProcesoMes) + 1) + "/"
					+ fechaProcesoAnio);
			
			fechaDesde = formatoDeFechas.parse(fechaPadronDesdeDia + "/"
					+ (Integer.parseInt(fechaPadronDesdeMes) + 1) + "/"
					+ fechaPadronDesdeAnio);
			
			fechaHasta = formatoDeFechas.parse(fechaPadronHastaDia + "/"
					+ (Integer.parseInt(fechaPadronHastaMes) + 1) + "/"
					+ fechaPadronHastaAnio);
			
			if (null != req.getParameter("fechaNovedad")) {
				fechaNovedades = req.getParameter("fechaNovedad").trim().length() > 0 ? req
						.getParameter("fechaNovedad") : null;
			}
			if(fechaNovedades != null ){
				fechaNovedadSSS.setTime(formatoDeFechas.parse(fechaNovedades));
			}else{
				fechaNovedadSSS = null;
			}
			
		} catch (Exception e) {
			fechaProceso = null;
			fechaDesde = null;
			fechaHasta = null;
			fechaNovedadSSS = null;
		}
		
		idRepoAutom = service.agendarRepoProcesoNovedadesSSS(reporte, user.getScreenName(), fechaProceso, fechaNovedadSSS.getTime(), 
				fechaDesde, fechaHasta, informar);

		List<ReporteNovedadesSSSProcesadas> lista = NovedadesServiceUtil.getInstance().getReportesNovedadesSSSProcesadas();
		
		req.setAttribute("reportesNovedSSSProc", lista);
		
		return idRepoAutom;
		
	}
}


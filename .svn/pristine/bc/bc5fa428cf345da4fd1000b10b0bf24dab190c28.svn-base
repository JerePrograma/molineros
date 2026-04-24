package ar.com.ospim.tesoreria.reportes.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afip.beans.ReporteAporteContribucionesEmpresa;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ReporteAportesContribucionesAction extends PortletAction {
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
		
		Calendar fechaAcreDesde=null;
		if(ParamUtil.getInteger(req, "fechaAcreDesdeMes")>=0 && ParamUtil.getInteger(req, "fechaAcreDesdeAnio")>0 ){
			fechaAcreDesde=Calendar.getInstance();
			fechaAcreDesde.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaAcreDesdeAnio"));
			fechaAcreDesde.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaAcreDesdeMes"));
			fechaAcreDesde.set(Calendar.DATE, ParamUtil.getInteger(req, "fechaAcreDesdeDia"));
		}
		
		Calendar fechaAcreHasta=null;
		if(ParamUtil.getInteger(req, "fechaAcreHastaMes")>=0 && ParamUtil.getInteger(req, "fechaAcreHastaAnio")>0 ){
			fechaAcreHasta=Calendar.getInstance();
			fechaAcreHasta.set(Calendar.YEAR, ParamUtil.getInteger(req, "fechaAcreHastaAnio"));
			fechaAcreHasta.set(Calendar.MONTH, ParamUtil.getInteger(req, "fechaAcreHastaMes"));
			fechaAcreHasta.set(Calendar.DATE, ParamUtil.getInteger(req, "fechaAcreHastaDia"));
		}
		

		String cuit = ParamUtil.getString(req, "cuit");
		
		String cuil = ParamUtil.getString(req, "cuil");

		List<ReporteAporteContribucionesEmpresa> reporte = AfipServiceUtil
				.getReporteAportesContribucionEmpresa(cuit,cuil, fechaIni, fechaFin, fechaAcreDesde.getTime(), fechaAcreHasta.getTime());

		/*Regla para evitar que se vena aportes Ospim antes del 01/01/2013*/
		User user = PortalUtil.getUser(PortalUtil.getHttpServletRequest(req));
		boolean permiteVerAportesOOSSdesde2011 = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_VER_APORTES_OSPIM);
		
		if(!permiteVerAportesOOSSdesde2011){

			Calendar fechaRestriccion = Calendar.getInstance();
			fechaRestriccion.set(Calendar.YEAR, 2013);
			fechaRestriccion.set(Calendar.MONTH, 0);
			fechaRestriccion.set(Calendar.DATE, 1);
			fechaRestriccion.set(Calendar.HOUR_OF_DAY, 0);
			fechaRestriccion.set(Calendar.MINUTE, 0);
			fechaRestriccion.set(Calendar.SECOND, 0);
			fechaRestriccion.set(Calendar.MILLISECOND, 0);
			
			List<ReporteAporteContribucionesEmpresa> auxAportesRestringidos = new ArrayList<ReporteAporteContribucionesEmpresa>();
			for (Iterator<ReporteAporteContribucionesEmpresa> iterator = reporte.iterator(); iterator.hasNext();) {
				
				ReporteAporteContribucionesEmpresa race = iterator.next();
				if(race.getPeriodo().getTime() < fechaRestriccion.getTimeInMillis()){ // aporte_os
					auxAportesRestringidos.add(race);
				}
			}
			reporte.removeAll(auxAportesRestringidos);
		}
		/*fin regla*/
		req.setAttribute(WebKeysTesoreria.REPORTE_APORTES_CONTRIBUYENTES,
				reporte);

		return mapping.findForward(getForward(req,
				"portlet.tesoreria.reporte.aportes.contrib.result"));
	}
}

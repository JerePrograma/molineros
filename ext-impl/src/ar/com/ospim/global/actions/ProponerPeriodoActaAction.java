package ar.com.ospim.global.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.SubdiarioComprobante;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.reportes.ReporteSubdiarioIngresoExcel;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class ProponerPeriodoActaAction extends JSONAction {
	private static Log _log = LogFactoryUtil
			.getLog(ProponerPeriodoActaAction.class);
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		 
		 String resultado = "{}";
		 
		String entidad = ParamUtil.getString(req, "entidad");
		String cuit = ParamUtil.getString(req, "cuit");
		
		try {
			
			Date fecha = TraeListasServiceUtil.getMaximoPeriodoActasPorCuit(cuit,entidad);
			resultado="{}";
			
			Calendar cal = Calendar.getInstance(); 
            cal.setTime(fecha); 
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DATE,1);
            fecha = cal.getTime();
            int mes=cal.get(Calendar.MONTH) + 1;
			resultado = "{ \"dia\" : \"" 
				    + cal.get(Calendar.DAY_OF_MONTH) 
			        + "\",\"mes\" : \""
			        + mes
			        + "\",\"anio\" : \""
			        + cal.get(Calendar.YEAR)
			        + "\" }";
			
		}catch(Exception e){
			_log.error("Error al generar control ingresos-egresos", e);
		    return null;
		}	
		
		return resultado;
	}
	
	protected static Date getDesde(HttpServletRequest req) {
		return DateUtils.getFechaDesde(req);
	}

	protected static Date getHasta(HttpServletRequest req) {
		return DateUtils.getFechaHasta(req);
	}
	
}
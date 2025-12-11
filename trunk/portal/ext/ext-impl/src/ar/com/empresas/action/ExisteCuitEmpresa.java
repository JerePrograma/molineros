package ar.com.empresas.action;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
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

public class ExisteCuitEmpresa extends JSONAction {
	private static Log _log = LogFactoryUtil
			.getLog(ExisteCuitEmpresa.class);
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
	    String resultado = "{}";
		 
		String cuit= ParamUtil.getString(req, "cuit");
		Boolean existe=false;
		
		if(cuit!=null && cuit!=""){	
		  List<Empresa>e=EmpresaServiceUtil.getEmpleadores(cuit, null, null,0);
		  if(e !=null && e.size()>0) existe=true;
		}			
		resultado = "{ \"existe\" : \"" 
				    + existe 
			        + "\" }";
		
		return resultado;
	}
	
		
}
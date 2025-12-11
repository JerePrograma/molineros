package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroConcepto;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroCuenta;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;

public class EquivalenciaParametrosEspecialesAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		PortletSession portletSession = renderRequest.getPortletSession();
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Calendar validoDesde = null;
		Calendar validoHasta = null;
		
		if(entidad==WebKeysGlobal.AMTIMA){
			validoDesde = DateUtils.getDesdeEjercicioActualAmtima();
			validoHasta = DateUtils.getHastaEjercicioActualAmtima();
		}else if(entidad==WebKeysGlobal.UOMA){
			validoDesde = DateUtils.getDesdeEjercicioActualUOMA();
			validoHasta = DateUtils.getHastaEjercicioActualUOMA();
		}else{
			validoDesde = DateUtils.getDesdeEjercicioActual();
			validoHasta = DateUtils.getHastaEjercicioActual();			
		}
		
		String ejercicio = renderRequest.getParameter("ejercicio");
		String ejercicio_seleccionado= (String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
		if(!StringUtils.isNotBlank(ejercicio)){
			if(StringUtils.isNotBlank(ejercicio_seleccionado)){
				ejercicio=ejercicio_seleccionado;
			}
		}
		if (StringUtils.isNotBlank(ejercicio)) {
			String dd = ejercicio.split("-")[0];
			String hta = ejercicio.split("-")[1];
			validoDesde.set(Calendar.YEAR, Integer.valueOf(dd));
			validoHasta.set(Calendar.YEAR, Integer.valueOf(hta));
			portletSession.removeAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
			portletSession.setAttribute("ejercicio_seleccionado", ejercicio, PortletSession.PORTLET_SCOPE);
		}

		List<ParametroCuenta> parametrosCuentas = ConceptoServiceUtil
				.getParametrosCuentas(validoDesde.getTime(),
						validoHasta.getTime(), entidad);
		List<ParametroConcepto> parametrosConceptos = ConceptoServiceUtil
				.getParametrosConceptos(validoDesde.getTime(),
						validoHasta.getTime(), entidad);
		List<Concepto> conceptos = TraeListasServiceUtil
				.getConceptos(validoDesde.getTime(), entidad);
		renderRequest.setAttribute("parametrosCuentas", parametrosCuentas);
		renderRequest.setAttribute("parametrosConceptos", parametrosConceptos);
		renderRequest.setAttribute("conceptos", conceptos);

		renderRequest.setAttribute("ejercicio_desde",
				format.format(validoDesde.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(validoHasta.getTime()));

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentas(validoDesde.getTime(), entidad);
		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS, planCuentas);
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.parametros_especiales"));
	}
}

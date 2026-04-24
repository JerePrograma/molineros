package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;

public class PlanCuentasAction extends PortletAction {
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
		
		Calendar desdeEjercicio = null;
		Calendar hastaEjercicio = null;
		if(entidad==WebKeysGlobal.AMTIMA){
			desdeEjercicio = DateUtils.getDesdeEjercicioActualAmtima();
			hastaEjercicio = DateUtils.getHastaEjercicioActualAmtima();
		}else if(entidad==WebKeysGlobal.UOMA){
			desdeEjercicio = DateUtils.getDesdeEjercicioActualUOMA();
			hastaEjercicio = DateUtils.getHastaEjercicioActualUOMA();
		}else{
			desdeEjercicio = DateUtils.getDesdeEjercicioActual();
			hastaEjercicio = DateUtils.getHastaEjercicioActual();	
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
			desdeEjercicio.set(Calendar.YEAR, Integer.valueOf(dd));
			hastaEjercicio.set(Calendar.YEAR, Integer.valueOf(hta));
			portletSession.removeAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
			portletSession.setAttribute("ejercicio_seleccionado", ejercicio, PortletSession.PORTLET_SCOPE);
		}

		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeEjercicio.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaEjercicio.getTime()));

		String numero = renderRequest.getParameter("numero");
		String descripcion = renderRequest.getParameter("descripcion");

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentas(desdeEjercicio.getTime(), entidad);
		if (StringUtils.isNotBlank(numero)
				|| StringUtils.isNotBlank(descripcion)) {
			planCuentas = filtrar(planCuentas, numero, descripcion);
		}
		renderRequest.setAttribute("planCuentas", planCuentas);
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.plan_cuentas"));
	}

	private List<PlanCuentas> filtrar(List<PlanCuentas> planCuentas,
			String numero, String descripcion) {
		List<PlanCuentas> ret = new ArrayList<PlanCuentas>();
		for (PlanCuentas pc : planCuentas) {
			boolean incluir = true;
			if (StringUtils.isNotBlank(numero)
					&& !pc.getNumero().contains(numero)) {
				incluir = false;
			}
			if (StringUtils.isNotBlank(descripcion)
					&& !pc.getCuenta().toUpperCase()
							.contains(descripcion.toUpperCase())) {
				incluir = false;
			}
			if (incluir) {
				ret.add(pc);
			}
		}
		return ret;
	}
}

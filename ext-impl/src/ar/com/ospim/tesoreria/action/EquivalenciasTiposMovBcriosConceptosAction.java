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
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;

public class EquivalenciasTiposMovBcriosConceptosAction extends PortletAction {
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

		List<TipoMovBcrio> tipoMov = TraeListasServiceUtil.getTipoMovBcrio(
				desdeEjercicio.getTime(), hastaEjercicio.getTime(), entidad);

		renderRequest.setAttribute("tiposMovBcrios", tipoMov);

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentas(desdeEjercicio.getTime(), entidad);
		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS, planCuentas);

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.equivalencias_mov_bcrios"));
	}

}

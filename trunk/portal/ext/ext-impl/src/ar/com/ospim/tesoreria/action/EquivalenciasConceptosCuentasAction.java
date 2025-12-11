package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.ListaConcepto;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class EquivalenciasConceptosCuentasAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		PortletSession portletSession = renderRequest.getPortletSession();
		int entidad=WebKeysGlobal.OSPIM;
		int paginado=20;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Calendar desdeEjercicio=null;
		Calendar hastaEjercicio=null;
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

		String concepto = renderRequest.getParameter("concepto");
		String cuenta = renderRequest.getParameter("cuenta");
		String cuentaPasivo = renderRequest.getParameter("cuenta_pasivo");
		int pagina=ParamUtil.getInteger(renderRequest, "pagina");
		
		renderRequest.setAttribute("offset_reg", pagina);
		
		if (StringUtils.isBlank(cuenta)) {
			cuenta = "-1";
		}

		if (StringUtils.isBlank(cuentaPasivo)) {
			cuentaPasivo = "-1";
		}

		int cta = Integer.parseInt(cuenta);
		int ctaPasivo = Integer.parseInt(cuentaPasivo);
		ListaConcepto listaConceptos=null;
		if (StringUtils.isBlank(concepto) && cuenta.equals("-1")
				&& cuentaPasivo.equals("-1")) {
			listaConceptos= TraeListasServiceUtil
					.getConceptosValidosDentroDe(desdeEjercicio.getTime(),
							hastaEjercicio.getTime(), entidad, new Integer(pagina));			
		}else{
			listaConceptos= TraeListasServiceUtil
					.getConceptosValidosDentroDe(desdeEjercicio.getTime(),
							hastaEjercicio.getTime(), entidad, null);
		}
		
		
		
		if (StringUtils.isBlank(concepto) && cuenta.equals("-1")
				&& cuentaPasivo.equals("-1")) {
			renderRequest.getPortletSession().removeAttribute("conceptos", PortletSession.PORTLET_SCOPE);
			renderRequest.getPortletSession().removeAttribute("total_conceptos", PortletSession.PORTLET_SCOPE);
			renderRequest.getPortletSession().setAttribute("conceptos", listaConceptos.getConceptos(),PortletSession.PORTLET_SCOPE);
			renderRequest.getPortletSession().setAttribute("total_conceptos", listaConceptos.getTotalConceptos(),PortletSession.PORTLET_SCOPE);			
		} else {
			renderRequest.setAttribute("filtro_concepto", concepto);
			renderRequest.setAttribute("filtro_cuenta", cuenta);
			renderRequest.setAttribute("filtro_cuenta_pasivo", cuentaPasivo);			
			List<Concepto> ret = new ArrayList<Concepto>();
			
			for (int i=0;i<listaConceptos.getConceptos().size();i++) {
				Concepto con =listaConceptos.getConceptos().get(i);
				boolean cumpleFiltro = true;
				if (StringUtils.isNotBlank(concepto)
						&& !con.getDescripcion().toUpperCase()
								.contains(concepto.toUpperCase())) {
					cumpleFiltro = false;
				}
				if (cumpleFiltro && cta != -1
						&& con.getPlanCuentas().getId() != cta) {
					cumpleFiltro = false;
				}
				if (cumpleFiltro && ctaPasivo != -1
						&& con.getPlanCuentasPasivo().getId() != ctaPasivo) {
					cumpleFiltro = false;
				}
				if (cumpleFiltro) {
					ret.add(con);					
				}
			}
			
			int fromIndex=0;
			int toIndex=0;
			if(pagina>0){
				fromIndex=pagina*20;
				toIndex=(pagina*20)+20;
			}else if(pagina==0){
				toIndex=20;			
			}
			if(toIndex>=ret.size()){
				toIndex=ret.size();
			}
			if(entidad==WebKeysGlobal.UOMA){
				renderRequest.getPortletSession().setAttribute("conceptos", ret.subList(fromIndex, toIndex),PortletSession.PORTLET_SCOPE);
				renderRequest.getPortletSession().setAttribute("total_conceptos", ret.size(),PortletSession.PORTLET_SCOPE);
			}else{
				renderRequest.getPortletSession().setAttribute("conceptos", ret ,PortletSession.PORTLET_SCOPE);
			}
			
		}

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentasImputables(desdeEjercicio.getTime(), entidad);
		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS, planCuentas);

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.conceptos_cuentas"));
	}
}

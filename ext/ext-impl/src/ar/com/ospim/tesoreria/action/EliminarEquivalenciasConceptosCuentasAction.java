package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.ListaConcepto;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.ConceptoUtilizadoException;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.actas.action.ActasBaseAction;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class EliminarEquivalenciasConceptosCuentasAction extends
		ActasBaseAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		int pagina=ParamUtil.getInteger(renderRequest, "pagina");
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		String id = renderRequest.getParameter("id");
		String dd = renderRequest.getParameter("ejercicio_desde");
		String hta = renderRequest.getParameter("ejercicio_hasta");
		int id_secuencial=ParamUtil.getInteger(renderRequest, "id_secuencial");

		if (renderRequest.getAttribute("ejercicio_desde") != null) {
			dd = (String) renderRequest.getAttribute("ejercicio_desde");
		}
		if (renderRequest.getAttribute("ejercicio_hasta") != null) {
			hta = (String) renderRequest.getAttribute("ejercicio_hasta");
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if (StringUtils.isBlank(dd)) {
			dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());
		}
		if (StringUtils.isBlank(hta)) {
			hta = format.format(DateUtils.getHastaEjercicioActual().getTime());
		}
		Date hasta = format.parse(hta);
		Date desde = format.parse(dd);

		try {
			User user = PortalUtil.getUser(renderRequest);
			ConceptoServiceUtil.eliminar(new Concepto(Integer.parseInt(id), id_secuencial),
					desde, hasta, user, entidad);
		} catch (ConceptoUtilizadoException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}

		Calendar auxHta = Calendar.getInstance();
		auxHta.setTime(hasta);

		Calendar desdeFinal = DateUtils.getDesdeEjercicioActual();
		Calendar hastaFinal = DateUtils.getHastaEjercicioActual();
		desdeFinal.set(Calendar.YEAR, auxHta.get(Calendar.YEAR) - 1);
		hastaFinal.set(Calendar.YEAR, auxHta.get(Calendar.YEAR));
		ListaConcepto listaConceptos=TraeListasServiceUtil
				.getConceptosValidosDentroDe(desdeFinal.getTime(),
						hastaFinal.getTime(), entidad,new Integer(pagina));
				
		if(entidad==WebKeysGlobal.UOMA){
			renderRequest.getPortletSession().removeAttribute("total_conceptos", PortletSession.PORTLET_SCOPE);
			renderRequest.getPortletSession().setAttribute("total_conceptos", listaConceptos.getTotalConceptos(),PortletSession.PORTLET_SCOPE);	
		}
		renderRequest.getPortletSession().removeAttribute("conceptos", PortletSession.PORTLET_SCOPE);
		renderRequest.getPortletSession().setAttribute("conceptos", listaConceptos.getConceptos(),PortletSession.PORTLET_SCOPE);
		
		renderRequest.setAttribute("offset_reg", pagina);		
		renderRequest.setAttribute("conceptos", listaConceptos.getConceptos());

		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeFinal.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaFinal.getTime()));
		
		List<PlanCuentas> planCuentasNumero = TraeListasServiceUtil
				.getPlanCuentasImputables(format.parse(dd), entidad);
		List<PlanCuentas> planCuentas = new ArrayList<PlanCuentas>();
		planCuentas.addAll(planCuentasNumero);
		Collections.sort(planCuentas, new Comparator<PlanCuentas>() {

			public int compare(PlanCuentas pc1, PlanCuentas pc2) {
				return pc1.getCuenta().compareTo(pc2.getCuenta());
			}

		});
		renderRequest.setAttribute("cuentas_por_nombre", planCuentas);
		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS,
				planCuentasNumero);
		
		
		if(entidad==WebKeysGlobal.UOMA){
			return mapping.findForward(getForward(renderRequest,
					"portlet.uoma.equivalencia.conceptos_cuentas"));
		}else if(entidad==WebKeysGlobal.AMTIMA){
			return mapping.findForward(getForward(renderRequest,
					"portlet.farmacia.equivalencia.conceptos_cuentas"));
		}else{
			return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.conceptos_cuentas"));
		}
	}
}

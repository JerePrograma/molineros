package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;

public class EquivalenciasPrestacionesConceptosAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Calendar desdeEjercicio = DateUtils.getDesdeEjercicioActual();
		Calendar hastaEjercicio = DateUtils.getHastaEjercicioActual();
		String ejercicio = renderRequest.getParameter("ejercicio");
		if (StringUtils.isNotBlank(ejercicio)) {
			String dd = ejercicio.split("-")[0];
			String hta = ejercicio.split("-")[1];
			desdeEjercicio.set(Calendar.YEAR, Integer.valueOf(dd));
			hastaEjercicio.set(Calendar.YEAR, Integer.valueOf(hta));
		}

		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeEjercicio.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaEjercicio.getTime()));

		String descripcion = renderRequest.getParameter("descripcion");
		String codigo = renderRequest.getParameter("codigo");

		if (StringUtils.isBlank(codigo) && StringUtils.isBlank(descripcion)) {
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.equivalencia.prestaciones_conceptos"));
		}

		List<PrestacionConcepto> pcs = ConceptoServiceUtil
				.getPrestacionesConceptos(desdeEjercicio, hastaEjercicio);
		List<PrestacionConcepto> ret = new ArrayList<PrestacionConcepto>();
		for (PrestacionConcepto con : pcs) {
			boolean cumpleFiltro = true;
			if (StringUtils.isNotBlank(codigo)
					&& !con.getPrestacion().getCodigo().toUpperCase()
							.contains(codigo.toUpperCase())) {
				cumpleFiltro = false;
			}
			if (StringUtils.isNotBlank(descripcion)
					&& !con.getPrestacion().getDescripcion().toUpperCase()
							.contains(descripcion.toUpperCase())) {
				cumpleFiltro = false;
			}
			if (cumpleFiltro) {
				ret.add(con);
			}
		}
		renderRequest.setAttribute("prestacionConceptos", ret);

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentas(desdeEjercicio.getTime(), entidad);
		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS, planCuentas);

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.prestaciones_conceptos"));
	}
}

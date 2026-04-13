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

import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.beans.ConceptoAfip;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;

public class EquivalenciasConceptosAfipAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

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

		String codigo = renderRequest.getParameter("codigo");
		String concepto = renderRequest.getParameter("concepto");

		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeEjercicio.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaEjercicio.getTime()));

		List<ConceptoAfip> conceptosAfip = ConceptoServiceUtil
				.getConceptosAfip(desdeEjercicio.getTime(),
						hastaEjercicio.getTime());

		if (StringUtils.isBlank(codigo) && StringUtils.isBlank(concepto)) {
			renderRequest.setAttribute("conceptos", conceptosAfip);
		} else {
			List<ConceptoAfip> ret = new ArrayList<ConceptoAfip>();
			for (ConceptoAfip con : conceptosAfip) {
				boolean cumple = true;
				if (StringUtils.isNotBlank(codigo)
						&& !con.getCodigoConcepto().toUpperCase()
								.contains(codigo.toUpperCase())) {
					cumple = false;
				}
				if (cumple
						&& StringUtils.isNotBlank(concepto)
						&& !con.getDescripcion().toUpperCase()
								.contains(concepto.toUpperCase())) {
					cumple = false;
				}
				if (cumple) {
					ret.add(con);
				}
			}
			renderRequest.setAttribute("conceptos", ret);
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.equivalencias_conceptos_afip"));
	}
}

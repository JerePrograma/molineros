package ar.com.ospim.tesoreria.actas.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.InteresAfip;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarPeriodoActaAction extends PortletAction {

	private static Log _log = LogFactoryUtil
			.getLog(AgregarPeriodoActaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");

		String desdeM = renderRequest.getParameter("fechaDesdeMes");
		desdeM = String.valueOf(Integer.valueOf(desdeM) + 1);
		String desdeA = renderRequest.getParameter("fechaDesdeAnio");

		String hastaM = renderRequest.getParameter("fechaHastaMes");
		hastaM = String.valueOf(Integer.valueOf(hastaM) + 1);
		String hastaA = renderRequest.getParameter("fechaHastaAnio");
		String cuit = renderRequest.getParameter("cuit");

		String obligD = renderRequest.getParameter("fechaObligDia");
		String obligM = renderRequest.getParameter("fechaObligMes");
		obligM = String.valueOf(Integer.valueOf(obligM) + 1);
		String obligA = renderRequest.getParameter("fechaObligAnio");

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		Acta acta = (Acta) session
				.getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
		
		if (acta == null) {
			acta = new Acta();			
		}

		if (acta.getEmpresa() == null) {
			acta.setEmpresa(new Empresa(cuit));
		} else if (StringUtils.checkEmpty(acta.getEmpresa().getCuit())) {
			acta.getEmpresa().setCuit(cuit);
		}

		Calendar dde = Calendar.getInstance();
		Calendar hta = Calendar.getInstance();
		Calendar oblig = Calendar.getInstance();

		dde.setTime(format.parse("01-" + desdeM + "-" + desdeA));
		hta.setTime(format.parse("01-" + hastaM + "-" + hastaA));
		oblig.setTime(format.parse(obligD + "-" + obligM + "-" + obligA));
		
		List<InteresAfip> intereses = TraeListasServiceUtil
				.getInteresesAfip(renderRequest);
		
		acta=calcularDeudaActa(acta, dde, hta, oblig, intereses, renderRequest);	
		
		
		session.setAttribute(WebKeysTesoreria.ACTA_EN_EDICION, acta);
		
		

		renderRequest.setAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION,
				WebKeysTesoreria.ACTAS_ACTION_EDICION);
		_log.debug("Saliendo de reder");
		return mapping.findForward("portlet.tesoreria.actas.periodos.view");
	}
	
	
	public Acta calcularDeudaActa(Acta acta, Calendar dde, Calendar hta, Calendar oblig, List<InteresAfip> intereses, PortletRequest request) throws Exception{
		
		if (dde.before(hta) || dde.equals(hta)) {
			List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
			if (peris == null) {
				peris = new ArrayList<ActaPeriodoDeudaEmpresa>();
			}

			for (ActaPeriodoDeudaEmpresa actaPeri : peris) {
				Date peri = actaPeri.getPeriodo();
				if (peri.equals(dde.getTime())
						|| peri.equals(hta.getTime())
						|| (peri.after(dde.getTime()) && peri.before(hta
								.getTime()))) {
					actaPeri.setBorradoLogico(false);
				}
			}

			List<ReporteDeudaNominaEmpresa> list = AfipServiceUtil
					.getDeudaNominaEmpresa(acta.getEmpresa().getCuit(), dde.getTime(), hta.getTime());
			for (ReporteDeudaNominaEmpresa deuda : list) {
				ActaPeriodoDeudaEmpresa actaPeri = new ActaPeriodoDeudaEmpresa(
						deuda);
				if (!peris.contains(actaPeri)) {
					peris.add(actaPeri);
				}
			}
			acta.setPeriodos(peris);

			Collections.sort(peris, new Comparator<ActaPeriodoDeudaEmpresa>() {
				public int compare(ActaPeriodoDeudaEmpresa o1,
						ActaPeriodoDeudaEmpresa o2) {
					int compareTo = o1.getPeriodo().compareTo(o2.getPeriodo());
					if (compareTo == 0) {
						compareTo = o1.getCuil().compareTo(o2.getCuil());
					}
					return compareTo;
				}
			});

		
			acta.calcaularIntereses(acta.getEmpresa().getCuit(), intereses, oblig, request);
		}
		return acta;
	}
	
}

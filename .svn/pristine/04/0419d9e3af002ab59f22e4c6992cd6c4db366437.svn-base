package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.CuentaServiceUtil;
import ar.com.ospim.tesoreria.actas.action.ActasBaseAction;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class EliminarPlanCuentas extends ActasBaseAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		String id = renderRequest.getParameter("id");
		String dd = renderRequest.getParameter("ejercicio_desde");
		String hta = renderRequest.getParameter("ejercicio_hasta");

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
		User user = PortalUtil.getUser(renderRequest);
		try {
			CuentaServiceUtil.eliminar(new PlanCuentas(Integer.parseInt(id)),
					desde, hasta, user, entidad);
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

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentas(desdeFinal.getTime(), entidad);
		renderRequest.setAttribute("planCuentas", planCuentas);

		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeFinal.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaFinal.getTime()));

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.plan_cuentas"));
	}
}

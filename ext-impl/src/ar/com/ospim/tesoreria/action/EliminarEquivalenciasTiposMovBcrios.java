package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.actas.action.ActasBaseAction;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class EliminarEquivalenciasTiposMovBcrios extends ActasBaseAction {
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

		try {
			User user = PortalUtil.getUser(renderRequest);
			ConceptoServiceUtil.eliminar(
					new TipoMovBcrio(Integer.parseInt(id)), user, entidad);
		} catch (Exception e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Calendar desdeEjercicio = DateUtils.getDesdeEjercicioActual();
		Calendar hastaEjercicio = DateUtils.getHastaEjercicioActual();

		List<TipoMovBcrio> tipoMov = TraeListasServiceUtil.getTipoMovBcrio(
				desdeEjercicio.getTime(), hastaEjercicio.getTime(), entidad);

		renderRequest.setAttribute("tiposMovBcrios", tipoMov);

		List<PlanCuentas> planCuentas = TraeListasServiceUtil
				.getPlanCuentas(desdeEjercicio.getTime(), entidad);

		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS, planCuentas);
		renderRequest.setAttribute("ejercicio_desde",
				format.format(desdeEjercicio.getTime()));
		renderRequest.setAttribute("ejercicio_hasta",
				format.format(hastaEjercicio.getTime()));
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.equivalencias_mov_bcrios"));
	}
}

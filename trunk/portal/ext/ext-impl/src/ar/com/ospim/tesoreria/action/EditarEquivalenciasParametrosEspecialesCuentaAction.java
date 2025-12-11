package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroCuenta;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarEquivalenciasParametrosEspecialesCuentaAction extends
		PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {
		try {
			int entidad=WebKeysGlobal.OSPIM;
			
			if(actionResponse.getNamespace().equals("_FAR_1_")){
				entidad=WebKeysGlobal.AMTIMA;
			}else if(actionResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
			}
			String ddOriginal = req.getParameter("ejercicio_desde_original");
			String dd = req.getParameter("ejercicio_desde");
			String hta = req.getParameter("ejercicio_hasta");

			req.setAttribute("ejercicio_desde", dd);
			req.setAttribute("ejercicio_hasta", hta);
			req.setAttribute("ejercicio_desde_original", ddOriginal);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date desdeOriginal = format.parse(ddOriginal);

			ParametroCuenta pc = new ParametroCuenta();
			pc.setPlanCuentas(new PlanCuentas(Integer.parseInt(req
					.getParameter("cuentaId"))));
			pc.setValidoDesde(format.parse(dd));
			if(entidad==WebKeysGlobal.AMTIMA){
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActualAmtima().getTime()) == 0) {
					pc.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					pc.setValidoHasta(format.parse(hta));
				}
				
			}else{
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActual().getTime()) == 0) {
					pc.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					pc.setValidoHasta(format.parse(hta));
				}
				
			}
			
			String param = req.getParameter("parametro");
			pc.setParametro(param);

			User user = PortalUtil.getUser(req);
			ConceptoServiceUtil.update(pc, desdeOriginal, user, entidad);
			// si no fallo piso el dde original
			req.setAttribute("ejercicio_desde_original", dd);
			req.setAttribute("param", param);
		} catch (Exception e) {
			SessionErrors.add(req, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(req)) {
			String successMessage = ParamUtil.getString(req, "successMessage");
			SessionMessages.add(req, "request_processed", successMessage);
		}
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

		String ddOriginal = renderRequest
				.getParameter("ejercicio_desde_original");
		String dd = renderRequest.getParameter("ejercicio_desde");
		String hta = renderRequest.getParameter("ejercicio_hasta");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		if (renderRequest.getAttribute("ejercicio_desde_original") != null) {
			ddOriginal = (String) renderRequest
					.getAttribute("ejercicio_desde_original");
		}
		if (renderRequest.getAttribute("ejercicio_desde") != null) {
			dd = (String) renderRequest.getAttribute("ejercicio_desde");
		}
		if (renderRequest.getAttribute("ejercicio_hasta") != null) {
			hta = (String) renderRequest.getAttribute("ejercicio_hasta");
		}
		
		if(entidad==WebKeysGlobal.AMTIMA){
			if (StringUtils.isBlank(dd)) {
				dd = format.format(DateUtils.getDesdeEjercicioActualAmtima().getTime());
			}
			if (StringUtils.isBlank(hta)) {
				hta = format.format(DateUtils.getHastaEjercicioActualAmtima().getTime());
			}
			
		}else{
			if (StringUtils.isBlank(dd)) {
				dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());
			}
			if (StringUtils.isBlank(hta)) {
				hta = format.format(DateUtils.getHastaEjercicioActual().getTime());
			}			
		}

		
		renderRequest.setAttribute("ejercicio_desde", dd);
		renderRequest.setAttribute("ejercicio_hasta", hta);
		renderRequest.setAttribute("ejercicio_desde_original", ddOriginal);

		String param = renderRequest.getParameter("param");
		if (param == null) {
			param = (String) renderRequest.getAttribute("param");
		}

		List<ParametroCuenta> parametrosCuentas = ConceptoServiceUtil
				.getParametrosCuentas(format.parse(ddOriginal),
						format.parse(hta), entidad);

		ParametroCuenta pc = new ParametroCuenta(param);
		pc = parametrosCuentas.get(parametrosCuentas.indexOf(pc));
		renderRequest.setAttribute("parametroCuenta", pc);
		List<PlanCuentas> planCuentasImputables = TraeListasServiceUtil
				.getPlanCuentasImputables(pc.getValidoDesde(), entidad);
		renderRequest.setAttribute(WebKeysTesoreria.PLAN_CUENTAS,
				planCuentasImputables);

		return mapping
				.findForward(getForward(renderRequest,
						"portlet.tesoreria.equivalencia.editar_parametro_especial_cuenta"));

	}

}

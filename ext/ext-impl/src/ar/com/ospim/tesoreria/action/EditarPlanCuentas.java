package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
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
import ar.com.ospim.liquidaciones.services.CuentaServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarPlanCuentas extends PortletAction {
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {
		try {
			PortletSession portletSession = req.getPortletSession();
			int entidad=WebKeysGlobal.OSPIM;			
			if(actionResponse.getNamespace().equals("_FAR_1_")){
				entidad = WebKeysGlobal.AMTIMA;
			}if(actionResponse.getNamespace().equals("_UOM_1_")){
				entidad = WebKeysGlobal.UOMA;
			}
			
			String dd = req.getParameter("ejercicio_desde");
			String hta = req.getParameter("ejercicio_hasta");
			
			String fechaDsdDia, fechaDsdMes, fechaDsdAnio = null ;
			String fechaHtaDia, fechaHtaMes, fechaHtaAnio = null ; 
			if(entidad == WebKeysGlobal.AMTIMA){
				fechaDsdDia = ParamUtil.getString(req, "validoDesdeDia");
				fechaDsdMes = ParamUtil.getString(req, "validoDesdeMes");
				fechaDsdAnio = ParamUtil.getString(req, "validoDesdeAnio");
				
				if(fechaDsdDia != null && fechaDsdMes != null && fechaDsdAnio != null){
					dd = fechaDsdDia + "/"+ (Integer.parseInt(fechaDsdMes) + 1) + "/" + fechaDsdAnio;
				}
				fechaHtaDia = ParamUtil.getString(req, "validoHastaDia");
				fechaHtaMes = ParamUtil.getString(req, "validoHastaMes");
				fechaHtaAnio = ParamUtil.getString(req, "validoHastaAnio");
				
				if(fechaHtaDia != null && fechaHtaMes != null && fechaHtaAnio != null){
					hta = fechaHtaDia + "/"+ (Integer.parseInt(fechaHtaMes) + 1) + "/" + fechaHtaAnio;
				}	
				
			}
			
			
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			if (StringUtils.isBlank(dd) || StringUtils.isBlank(hta)
					&& !StringUtils.isBlank(req.getParameter("ejercicio"))) {
				String ejercicio = req.getParameter("ejercicio");
				portletSession.removeAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute("ejercicio_seleccionado", ejercicio, PortletSession.PORTLET_SCOPE);
				if(entidad == WebKeysGlobal.AMTIMA){
//					dd = "01/07/" + Integer.valueOf(ejercicio.split("-")[0]);
//					hta = "30/06/" + Integer.valueOf(ejercicio.split("-")[1]);
					dd = "01/07/" + fechaDsdAnio;
					hta = "30/06/" + fechaHtaAnio;
				}else{
					dd = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
					hta = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);
				}				
			}

			req.setAttribute("ejercicio_desde", dd);
			req.setAttribute("ejercicio_hasta", hta);

			String imputable = req.getParameter("imputable");
			String inflacion = req.getParameter("ajinflacion");

			PlanCuentas pCuenta = new PlanCuentas();
			if (StringUtils.isNotBlank(imputable)
					&& imputable.trim().equals("true")) {
				pCuenta.setImputable(true);
			}
			if (StringUtils.isNotBlank(inflacion)
					&& (inflacion.trim().equals("true") || inflacion.trim().equals("on") )) {
				pCuenta.setAjustaInflacion(true);
			}
			pCuenta.setId(Integer.parseInt(req.getParameter("id")));
			pCuenta.setCuenta(req.getParameter("cuenta").trim());
			pCuenta.setNumero(req.getParameter("numero").trim());
			pCuenta.setTipo(req.getParameter("tipo").trim());
			pCuenta.setValidoDesde(format.parse(dd));

			if(entidad == WebKeysGlobal.AMTIMA){
//				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
//						DateUtils.getDesdeEjercicioActualAmtima().getTime()) == 0) {
//					pCuenta.setValidoHasta(DateUtils.getInfinito().getTime());
//				} else {
					pCuenta.setValidoHasta(format.parse(hta));
//				}				
			}else if(entidad == WebKeysGlobal.UOMA){
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActualUOMA().getTime()) == 0) {
					pCuenta.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					pCuenta.setValidoHasta(format.parse(hta));
				}				
				
			}else{
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActual().getTime()) == 0) {
					pCuenta.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					pCuenta.setValidoHasta(format.parse(hta));
				}
			}

			User user = PortalUtil.getUser(req);
			if (pCuenta.getId() != 0) {
				CuentaServiceUtil.update(pCuenta, user, entidad);
			} else {
				CuentaServiceUtil.guardar(pCuenta, user, entidad);
			}

			req.setAttribute("id", pCuenta.getId());
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
			if(entidad==WebKeysGlobal.OSPIM){
				dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());	
			} else if(entidad==WebKeysGlobal.UOMA){
				dd = format.format(DateUtils.getDesdeEjercicioActualUOMA().getTime());
			}else if(entidad==WebKeysGlobal.AMTIMA){
				dd = format.format(DateUtils.getDesdeEjercicioActualAmtima().getTime());
			}
			
		}
		if (StringUtils.isBlank(hta)) {
			if(entidad==WebKeysGlobal.AMTIMA){
				hta = format.format(DateUtils.getHastaEjercicioActualAmtima().getTime());
			}else if(entidad!=WebKeysGlobal.UOMA){
				dd = format.format(DateUtils.getDesdeEjercicioActualUOMA().getTime());
			}else{
				hta = format.format(DateUtils.getHastaEjercicioActual().getTime());	
			}			
		}
		renderRequest.setAttribute("ejercicio_desde", dd);
		renderRequest.setAttribute("ejercicio_hasta", hta);

		Integer idCuenta = null;
		String id = renderRequest.getParameter("id");
		if (renderRequest.getAttribute("id") != null) {
			id = ((Integer) renderRequest.getAttribute("id")).toString();
		}
		if(StringUtils.isNotBlank(id)){
			idCuenta = Integer.parseInt(id);
		}

		 
//		nuevo comportamiento, mostrar una seleccion de los conceptos maestros historicos del mismo id para luego elegir cual editar...
		String cmd = renderRequest.getParameter(Constants.CMD);
		
		if (StringUtils.isNotBlank(id) && Integer.parseInt(id) != 0 && entidad != WebKeysGlobal.AMTIMA) {
			PlanCuentas pc = TraeListasServiceUtil.getCuentaById(idCuenta,
					format.parse(dd), entidad);
			renderRequest.setAttribute("cuenta", pc);
		}else if(StringUtils.isNotBlank(id) && Integer.parseInt(id) != 0 
				&& entidad == WebKeysGlobal.AMTIMA 
				&& cmd!=null 
				&& cmd.equalsIgnoreCase(Constants.SEARCH)){
			
			List<PlanCuentas> planCuentasList = CuentaServiceUtil.getPlanCuentas(idCuenta, format.parse(dd),entidad);
			
			renderRequest.setAttribute("cuenta",planCuentasList.get(0));
//			renderRequest.setAttribute(WebKeysTesoreria.PLANES_CUENTAS_EN_SESSION, planCuentasList);
			
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.equivalencia.editar_plan_cuentas"));
//			return mapping.findForward(getForward(renderRequest,
//					"portlet.tesoreria.equivalencia.editar_plan_cuentas_amtima"));
		}else if(StringUtils.isNotBlank(id) && Integer.parseInt(id) != 0 
				&& entidad == WebKeysGlobal.AMTIMA 
				&& cmd!=null 
				&& cmd.equalsIgnoreCase(Constants.EDIT)){
			
			PlanCuentas pc = CuentaServiceUtil.getCuentaById(idCuenta,format.parse(dd), entidad);
			
			renderRequest.setAttribute("cuenta", pc);
			
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.equivalencia.editar_plan_cuentas")); //"portlet.tesoreria.equivalencia.editar_plan_cuentas_amtima"));
		}else {
			renderRequest.setAttribute("cuenta", new PlanCuentas());
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.equivalencia.editar_plan_cuentas"));

	}

}

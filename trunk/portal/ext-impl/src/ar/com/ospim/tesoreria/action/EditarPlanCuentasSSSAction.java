package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PlanCuentasSSS;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.CuentaServiceUtil;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarPlanCuentasSSSAction extends PortletAction {
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(req);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(req);
		try {
			PortletSession portletSession = req.getPortletSession();
			int entidad=WebKeysGlobal.OSPIM;			
			if(actionResponse.getNamespace().equals("_FAR_1_")){
				entidad = WebKeysGlobal.AMTIMA;
			}if(actionResponse.getNamespace().equals("_UOM_1_")){
				entidad = WebKeysGlobal.UOMA;
			}
			
			PlanCuentasSSS pc = (PlanCuentasSSS) session.getAttribute("cuentaSSS");
            
			
            PlanCuentasSSS pCuenta = new PlanCuentasSSS();
			pCuenta.setId(Integer.parseInt(req.getParameter("id")));
			pCuenta.setCuenta(req.getParameter("cuenta").trim());
			pCuenta.setNumero(req.getParameter("numero").trim());
			pCuenta.setTipo(req.getParameter("tipo").trim());
			pCuenta.setSigno(Integer.parseInt(req.getParameter("signo")));
			pCuenta.setAcumulaSobre(req.getParameter("acumula").trim());
			
            if(pc!=null){
            	pCuenta.setEquivalencias(pc.getEquivalencias());
            }
			
            if (pCuenta.getId() != 0) {
            	ContabilidadServiceUtil.updateCuentaSSS(pCuenta, entidad, user.getScreenName());
			} else {
				ContabilidadServiceUtil.addCuentaSSS(pCuenta, entidad, user.getScreenName());
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
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		Integer idCuenta = null;
		String id = renderRequest.getParameter("id");
		if (renderRequest.getAttribute("id") != null) {
			id = ((Integer) renderRequest.getAttribute("id")).toString();
		}
		if(StringUtils.isNotBlank(id)){
			idCuenta = Integer.parseInt(id);
		}

		 
		String cmd = renderRequest.getParameter(Constants.CMD);
		
		if(cmd!=null && "asociarcuenta".equalsIgnoreCase(cmd) ){
			PlanCuentasSSS pc = (PlanCuentasSSS) session.getAttribute("cuentaSSS");
			PlanCuentas cuenta = new PlanCuentas();
			String numero =renderRequest.getParameter("cuentaid");
			String descripcion =renderRequest.getParameter("cuentadescripcion");
			cuenta.setCuenta(descripcion);
			cuenta.setNumero(numero);
			
			
			Boolean existe=false;
			if(pc.getEquivalencias()!=null){
			  for(PlanCuentas ds: pc.getEquivalencias() ){
				  if(ds.getNumero().equalsIgnoreCase(numero)){
					  existe=true; 
					  break;
				  }
			  }
			} 
			if(!existe){
			  pc.getEquivalencias().add(cuenta);
			}  
            
			session.setAttribute("cuentaSSS", pc);
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.contabilidad.plan_cuentas_sss_cuentas.result"));
		}
		
		if(cmd!=null && cmd.equals("desasociarcuenta") ){
			
			PlanCuentasSSS pc = (PlanCuentasSSS) session.getAttribute("cuentaSSS");
			String numero =renderRequest.getParameter("cuentaid");
			List<PlanCuentas> l = new ArrayList<PlanCuentas>();
			
			for(PlanCuentas ds: pc.getEquivalencias() ){
				  if(!ds.getNumero().equalsIgnoreCase(numero)){
					  l.add(ds);
				  }
			}
			pc.setEquivalencias(l);
			
			session.setAttribute("cuentaSSS", pc);
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.contabilidad.plan_cuentas_sss_cuentas.result"));
		}
		
		if (StringUtils.isNotBlank(id) && Integer.parseInt(id) != 0 && entidad != WebKeysGlobal.AMTIMA) {
			
			PlanCuentasSSS pc = TraeListasServiceUtil.getCuentaSSSById(idCuenta,entidad);
			session.setAttribute("cuentaSSS", pc);
		}else if(StringUtils.isNotBlank(id) && Integer.parseInt(id) != 0 
				&& entidad == WebKeysGlobal.AMTIMA 
				&& cmd!=null 
				&& cmd.equalsIgnoreCase(Constants.SEARCH)){
			/*
			
			List<PlanCuentas> planCuentasList = CuentaServiceUtil.getPlanCuentas(idCuenta, entidad);
			
			renderRequest.setAttribute(WebKeysTesoreria.PLANES_CUENTAS_EN_SESSION, planCuentasList);
			
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.equivalencia.editar_plan_cuentas_amtima"));
			*/		
		}else if(StringUtils.isNotBlank(id) && Integer.parseInt(id) != 0 
				&& entidad == WebKeysGlobal.AMTIMA 
				&& cmd!=null 
				&& cmd.equalsIgnoreCase(Constants.EDIT)){
		/*	
			PlanCuentas pc = CuentaServiceUtil.getCuentaById(idCuenta, entidad);
			
			renderRequest.setAttribute("cuenta", pc);
			
			return mapping.findForward(getForward(renderRequest,
					"portlet.tesoreria.equivalencia.editar_plan_cuentas")); //"portlet.tesoreria.equivalencia.editar_plan_cuentas_amtima"));
		*/			
		}else {
			session.setAttribute("cuentaSSS", new PlanCuentasSSS());
		}

		if(StringUtils.isNotBlank(id) && Integer.parseInt(id) != 0 
				&& entidad != WebKeysGlobal.AMTIMA 
				&& cmd!=null 
				&& cmd.equalsIgnoreCase(Constants.DELETE)){
			
			  PlanCuentasSSS pc = TraeListasServiceUtil.getCuentaSSSById(idCuenta,entidad);
			  ContabilidadServiceUtil.deleteCuentaSSS(pc, entidad);
			  return mapping.findForward(getForward(renderRequest,"portlet.tesoreria.contabilidad.plan_cuentas_sss"));
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.editar_plan_cuentas_sss"));

	}

}

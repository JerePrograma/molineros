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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.beans.ProcesoSQL;
import ar.com.ospim.automatico.service.SchedulerServiceUtil;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class LiquidarJubiladosAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(LiquidarJubiladosAction.class);
	
 public void processAction(ActionMapping mapping, ActionForm form,
			  PortletConfig portletConfig, ActionRequest actionRequest,
			  ActionResponse actionResponse) throws Exception {

 }

 public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		PortletSession session = renderRequest.getPortletSession();
		PortletSession portletSession = renderRequest.getPortletSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		String periodo="";
		String msg = "";
		if (!StringUtils.checkEmpty(cmd)) {
			periodo = ParamUtil.getString(renderRequest,"periodo", "");
			if(cmd.equals("liquidar")){
				
				
            	liquidarPeriodo(periodo);
            	
            	msg = "Finalizó Liquidación del periodo";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.tesoreria.liquidar.jubilados.result");
			}else if(cmd.equals("eliminar")){
				
				
            	eliminarPeriodo(periodo);
            	
            	msg = "Finalizó Eliminación del periodo";
				  
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				  
                return mapping.findForward("portlet.tesoreria.liquidar.jubilados.result");
			}

		}
		
		return mapping
				.findForward("portlet.tesoreria.liquidar.jubilados.result");
	}
 
 
 
    private void liquidarPeriodo(String periodo) throws Exception {
		LiquidaDesreguladosServiceUtil.liquidarPeriodoJubilados(periodo);
	}

    private void eliminarPeriodo(String periodo) throws Exception {
		LiquidaDesreguladosServiceUtil.eliminarPeriodoJubilados(periodo);
	}
}




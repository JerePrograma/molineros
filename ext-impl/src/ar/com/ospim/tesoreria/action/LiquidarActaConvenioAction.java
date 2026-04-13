package ar.com.ospim.tesoreria.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.tesoreria.LiquidarActaConvenioException;
import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;
import ar.com.ospim.tesoreria.services.LiquidaActaConveniosServiceUtil;

import com.liferay.portal.struts.PortletAction;

public class LiquidarActaConvenioAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try{
			LiquidaActaConveniosServiceUtil.liqActaConvenio();
		
			List<ConsolidadoLiquidaciones> liquidaciones=LiquidaActaConveniosServiceUtil.getConsolidadoLiquidaciones(null);
			renderRequest.setAttribute("consolidadoLiquidaciones", liquidaciones);
		}catch(LiquidarActaConvenioException e){
			return mapping.findForward("portlet.tesoreria.error");
		}
		return mapping.findForward("portlet.tesoreria.liquidar.acta.convenio.search.result");
	}

}

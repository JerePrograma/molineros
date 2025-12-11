package ar.com.ospim.autorizaciones.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.NomencladorPlan;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarNomencladorPlanAction extends PortletAction {
	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		List<NomencladorPlan> lista = (List<NomencladorPlan>) session
				.getAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION);

		if (lista == null) {
			lista = new ArrayList<NomencladorPlan>();
		}
		
		int id = 0;
		
		String plan="";
		String autorizacion="";
		plan=ParamUtil.getString(renderRequest,"plan");
		autorizacion=ParamUtil.getString(renderRequest, "autorizacion");

		id= (int)Math.floor((Math.random()*100)); //Asigna id ficticio, hay que reemplazarlo por el real cuando se defina
		
		Boolean isNew = true;
		for(NomencladorPlan n:lista){
			if(n.getPlan().getId()==Integer.parseInt(plan) && 
					n.getAutorizacion().getId()==Integer.parseInt(autorizacion)){
		       isNew= false;
		       break;
			}
		}
		
		if(isNew)
		   lista.add(new NomencladorPlan(id,0, Integer.parseInt(plan), Integer.parseInt(autorizacion)));
		
		renderRequest.setAttribute("esEdicion", "true");
		session.setAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION, lista);
		
		return mapping.findForward("portlet.autorizaciones.nomenclador.nomencladorplan.search.result");
		
	}
}

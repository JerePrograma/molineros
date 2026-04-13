package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarNomencladorPlanAction extends PortletAction {
	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		List<NomencladorPlan> lista = (List<NomencladorPlan>) session
				.getAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION);

		int idPlan = ParamUtil.getInteger(renderRequest, "id_plan");
		int idAutorizacion = ParamUtil.getInteger(renderRequest, "id_autorizacion");
		List<NomencladorPlan> listaNew = new ArrayList<NomencladorPlan>();
		for (NomencladorPlan m:lista){
		   if(!(m.getPlan().getId() ==idPlan && m.getAutorizacion().getId()==idAutorizacion)){
			   listaNew.add(m);	
		   }   
		}
		
		renderRequest.setAttribute("esEdicion", "true");
		session.setAttribute(
				WebKeysAutorizaciones.MODALIDAD_ATENCION, listaNew);
		
		return mapping.findForward("portlet.autorizaciones.nomenclador.nomencladorplan.search.result");
	}

}

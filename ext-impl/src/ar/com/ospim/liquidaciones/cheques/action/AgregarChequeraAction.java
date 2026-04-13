package ar.com.ospim.liquidaciones.cheques.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.beans.Chequera;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarChequeraAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(AgregarChequeraAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		
		
		setForward(actionRequest, "portlet.liquidaciones.cheques.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			int entidad=WebKeysGlobal.OSPIM;
			if(renderResponse.getNamespace().equals("_FAR_1_")){
				entidad=WebKeysGlobal.AMTIMA;
			}else if(renderResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
			}
			boolean borrar=ParamUtil.getBoolean(renderRequest, "borrar");
			int ctaBcria=ParamUtil.getInteger(renderRequest, "ctaBcria");
			int numeroDesde=ParamUtil.getInteger(renderRequest, "numeroDesde");
			int numeroHasta=ParamUtil.getInteger(renderRequest, "numeroHasta");
			
			User user = PortalUtil.getUser(renderRequest);
			
			Chequera chequera=new Chequera(ctaBcria, numeroDesde, numeroHasta);
			if(borrar){
				int idChequera=ParamUtil.getInteger(renderRequest, "id_chequera");
				ChequeServiceUtil.borrarChequera(idChequera, user.getScreenName(), entidad);
			}else{
				ChequeServiceUtil.saveChequera(chequera, user.getScreenName(), entidad);
			}
					
			List<Chequera> lista = ChequeServiceUtil.getUltimasChequeras(entidad);
			
			renderRequest.removeAttribute(WebKeysLiquidaciones.CHEQUERAS);
			
			renderRequest.setAttribute(WebKeysLiquidaciones.CHEQUERAS,
					lista);
		} catch (DuplicateNumeroChequeException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());			
		}catch (Exception e) {	
			_log.error(e);
		}
		return mapping
				.findForward("portlet.uoma.chequera.result.search");
	}
}
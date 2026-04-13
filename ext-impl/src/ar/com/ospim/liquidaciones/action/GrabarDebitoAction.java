/**
 */

package ar.com.ospim.liquidaciones.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.ComprobanteItem;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.DebitoServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="GrabarDebitosAction.java.html"><b><i>View Source</i></b></a>
 * <p> Graba los Debitos
 * @author Carlos Rivas
 *
 */
public class GrabarDebitoAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarDebitoAction.class);

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {
		
		setForward(actionRequest,"portlet.debito.view");
	}
	
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		User user = PortalUtil.getUser(renderRequest);
		int id_liquidacion = ParamUtil.getInteger(renderRequest, "id_liquidacion", 0);
		int item = ParamUtil.getInteger(renderRequest, "item", 0);						
		int motivo_debito = ParamUtil.getInteger(renderRequest, "motivo_debito", 0);
		String observaciones_debito = ParamUtil.getString(renderRequest, "observaciones_debito", "");
		String importe_debito = ParamUtil.getString(renderRequest, "importe_debito", "0.0");
		String borradoDeb = ParamUtil.getString(renderRequest, "borrarDeb", null);
		String cuit_prestador = ParamUtil.getString(renderRequest, "cuit_prestador", null);
		String sucu_prestador = ParamUtil.getString(renderRequest, "id_prestador", null);
		sucu_prestador="000"; //Agregado para subsanar prestadores no dados de alta en empresas con sucursal =id_prestador DS 20230919
		
		
		try {
			List<ComprobanteItem> debitos=null;
			if(null != borradoDeb && borradoDeb.trim().equals("true")){
				debitos=DebitoServiceUtil.borraDebitoRetornaLista(id_liquidacion, item, user);
			}			
			else if(item != 0){
				debitos= DebitoServiceUtil.editaDebitoRetornaLista(id_liquidacion, item, motivo_debito, observaciones_debito, importe_debito, cuit_prestador,  user);
			}else{
				debitos= DebitoServiceUtil.grabaDebitoRetornaLista(id_liquidacion, motivo_debito, observaciones_debito, importe_debito, cuit_prestador, user,sucu_prestador);				
			}
			renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_DEBITOS, debitos);
			String view=ParamUtil.getString(renderRequest, "view");
			
			if(null!=view && view.equals("true")){
				renderRequest.setAttribute("view", view);
			}
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest,Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed","");
		}
		return mapping.findForward("portlet.liquidaciones.debitos.result");
	}



}
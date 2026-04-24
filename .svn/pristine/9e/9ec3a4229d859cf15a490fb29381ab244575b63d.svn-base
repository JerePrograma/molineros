package ar.com.ospim.tesoreria.recibos.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AnularReciboAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String id = renderRequest.getParameter("recibo_id");
		User user = PortalUtil.getUser(renderRequest);
		
		int entidad=WebKeysGlobal.OSPIM;
		
		String path = mapping.getPath();
		renderRequest.setAttribute("recibo_id", id);
		
		boolean reactivar=ParamUtil.getBoolean(renderRequest, "reactivar");
		
		if(path.contains("anular_recibo_fecha")){			
			return mapping.findForward("portlet.tesoreria.anular.recibo");
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
					WebKeysTesoreria.IS_AMTIMA);
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		if(reactivar){
			try {
				ReciboServiceUtil.reactivarRecibo(Integer.parseInt(id), user, entidad);
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}			
		}else{
			Calendar fechaBaja=Calendar.getInstance();
			fechaBaja.set(Calendar.DATE, ParamUtil.getInteger(renderRequest, "fechaBajaDia"));
			fechaBaja.set(Calendar.MONTH, ParamUtil.getInteger(renderRequest, "fechaBajaMes"));
			fechaBaja.set(Calendar.YEAR, ParamUtil.getInteger(renderRequest, "fechaBajaAnio"));
			try {
				ReciboServiceUtil.anularRecibo(Integer.parseInt(id), user, fechaBaja.getTime(), entidad);
			} catch (Exception e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");
			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}
		return mapping.findForward("portlet.tesoreria.anular.recibo");

		
	}
}

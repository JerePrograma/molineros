package ar.com.ospim.liquidaciones.cheques.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceImpl;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AnularChequesAction extends PortletAction {

	private static Log _log = LogFactoryUtil.getLog(AnularChequesAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		TraeListasServiceUtil.getCtasBcrias(renderRequest);
//		FIXME ANALIZAR ESTA ANULACION DE CHEQUE DESDE DONDE VIENE
		_log.fatal("ANALIZAR ESTA ANULACION DE CHEQUE DESDE DONDE VIENE");
		Integer nro = ParamUtil.getInteger(renderRequest, "cheque_nro");
		Cheque ch = null;
		User user = PortalUtil.getUser(renderRequest);
		ChequeServiceUtil.anularcheque(ch, null, user, entidad);

		if (SessionErrors.isEmpty(renderRequest)) {
			String successMessage = ParamUtil.getString(renderRequest,
					"successMessage");

			SessionMessages.add(renderRequest, "request_processed",
					successMessage);
		}

		return mapping.findForward("portlet.liquidaciones.view");
	}
}

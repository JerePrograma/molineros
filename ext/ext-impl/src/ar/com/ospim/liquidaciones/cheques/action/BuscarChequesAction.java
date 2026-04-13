package ar.com.ospim.liquidaciones.cheques.action;

import java.math.BigDecimal;
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
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarChequesAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * 
 * @author Martin Moreyra
 * 
 */
public class BuscarChequesAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarChequesAction.class);

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
			
			String cuit = null;
			String numero = null;

			if (null != renderRequest.getParameter("cuit")) {
				cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
						.getParameter("cuit")
						: null;
			}

			if (null != renderRequest.getParameter("numero")) {
				numero = renderRequest.getParameter("numero").trim().length() > 0 ? renderRequest
						.getParameter("numero")
						: null;
			}
			BigDecimal numeroBigD = null;
			if (numero != null) {
				numeroBigD = new BigDecimal(numero);
			}
			
			Cheque cheque = new Cheque();
			cheque.setNumero(numeroBigD);
			cheque.setCuit(cuit);
			List<Cheque> lista = ChequeServiceUtil.getCheques(cheque, entidad);
			renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CHEQUES,
					lista);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.liquidaciones.cheques.result.search");
	}
}
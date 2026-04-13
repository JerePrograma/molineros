package ar.com.global.action;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarChequesAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * 
 * @author Martin Moreyra
 * 
 */
public class BuscarChequesCarteraAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarChequesCarteraAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		setForward(actionRequest, "portlet.liquidaciones.cheques.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			renderRequest.removeAttribute(WebKeysLiquidaciones.BUSQUEDA_CHEQUES);
			int entidad=WebKeysGlobal.OSPIM;
			if(renderResponse.getNamespace().equals("_FAR_1_")){
				entidad=WebKeysGlobal.AMTIMA;
			}else if(renderResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
			}
			
			int nroCheque=ParamUtil.getInteger(renderRequest, "nro_cheque");
			String cuit=ParamUtil.getString(renderRequest, "cuit", null);
			double imported=ParamUtil.getDouble(renderRequest, "importe");
			
			BigDecimal importe=new BigDecimal(imported,MathContext.DECIMAL64);
			List<Cheque> lista = ChequeServiceUtil.getCheques(cuit, Cheque.Estado.RECIBIDO, new BigDecimal(nroCheque), importe.setScale(2, RoundingMode.DOWN), entidad);
			renderRequest.setAttribute(WebKeysLiquidaciones.CHEQUES_CARTERA,
					lista);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.liquidaciones.ordenes_pago.pagos.cheques.recibidos");
	}
}
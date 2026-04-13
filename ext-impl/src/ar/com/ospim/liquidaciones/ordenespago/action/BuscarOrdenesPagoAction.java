package ar.com.ospim.liquidaciones.ordenespago.action;

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
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.WebKeysTesoreria;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarOrdenesPagoAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * 
 * @author Martin Moreyra
 * 
 */
public class BuscarOrdenesPagoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarOrdenesPagoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.farmacia.ordenes_pago.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			int entidad=WebKeysGlobal.OSPIM;
			
			if(renderResponse.getNamespace().equals("_FAR_1_")){
				entidad=WebKeysGlobal.AMTIMA;
				renderRequest.setAttribute(WebKeysTesoreria.IS_AMTIMA,
						WebKeysTesoreria.IS_AMTIMA);
			}else if(renderResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
			}

			String numeroCheque = null;
			String numero = null;

			if (null != renderRequest.getParameter("cheque_numero")) {
				numeroCheque = renderRequest.getParameter("cheque_numero")
						.trim().length() > 0 ? renderRequest
						.getParameter("cheque_numero") : null;
			}

			if (null != renderRequest.getParameter("numero")) {
				numero = renderRequest.getParameter("numero").trim().length() > 0 ? renderRequest
						.getParameter("numero")
						: null;
			}
			Integer numeroInt = null;
			if (numero != null) {
				numeroInt = Integer.valueOf(numero);
			}

			BigDecimal numeroChequeInt = null;
			if (numeroCheque != null) {
				numeroChequeInt = new BigDecimal(numeroCheque);
			}
			
			int desdeDia=ParamUtil.getInteger(renderRequest, "desdeDia");
			int desdeMes=ParamUtil.getInteger(renderRequest, "desdeMes");
			int desdeAnio=ParamUtil.getInteger(renderRequest, "desdeAnio");
			Calendar fechaDesde=null;
			if(desdeMes>=0 && desdeAnio>0){
				fechaDesde=Calendar.getInstance();
				fechaDesde.set(Calendar.DAY_OF_MONTH, desdeDia==0?1:desdeDia);
				fechaDesde.set(Calendar.MONTH, desdeMes);
				fechaDesde.set(Calendar.YEAR, desdeAnio);
			}
			
			int hastaDia=ParamUtil.getInteger(renderRequest, "hastaDia");
			int hastaMes=ParamUtil.getInteger(renderRequest, "hastaMes");
			int hastaAnio=ParamUtil.getInteger(renderRequest, "hastaAnio");
			Calendar fechaHasta=null;
			if(hastaMes>=0 && hastaAnio>0){
				fechaHasta=Calendar.getInstance();
				fechaHasta.set(Calendar.DAY_OF_MONTH, hastaDia==0?1:hastaDia);
				fechaHasta.set(Calendar.MONTH, hastaMes);
				fechaHasta.set(Calendar.YEAR, hastaAnio);
			}
			String cuit=ParamUtil.getString(renderRequest, "cuit");
			String sucursal=ParamUtil.getString(renderRequest, "sucu");
			int idSeccional=ParamUtil.getInteger(renderRequest, "idSeccional");
			
			List<OrdenPago> lista = OrdenPagoServiceUtil.getOrdenesPago(
					numeroChequeInt, numeroInt, cuit, idSeccional>0?null:sucursal, null!=fechaDesde?fechaDesde.getTime():null,null!=fechaHasta?fechaHasta.getTime():null, idSeccional, entidad);
			renderRequest.setAttribute(
					WebKeysLiquidaciones.BUSQUEDA_ORDENES_PAGO, lista);
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.farmacia.ordenes_pago.result.search");
	}
}
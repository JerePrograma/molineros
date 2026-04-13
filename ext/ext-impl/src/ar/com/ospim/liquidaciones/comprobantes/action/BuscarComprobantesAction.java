package ar.com.ospim.liquidaciones.comprobantes.action;

import java.util.Date;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.WebKeysPortal;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarComprobantesAction extends PortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;

		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		renderRequest.setAttribute(WebKeysPortal.FECHA_CIERRE_PERIODO_CONTABLE,
				fecha_cierre_periodo);

		Comprobante comp = EditarComprobantesAction
				.getComprobanteFromRequest(renderRequest);
		
		int pagado=ParamUtil.getInteger(renderRequest, "estado");		
		
		renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES,
				ComprobanteServiceUtil.getComprobantes(comp, entidad, pagado));

		if (entidad != WebKeysGlobal.AMTIMA) {
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.search.result");
		} else {
			return mapping
					.findForward("portlet.farmacia.comprobantes.search.result");
		}

	}
}

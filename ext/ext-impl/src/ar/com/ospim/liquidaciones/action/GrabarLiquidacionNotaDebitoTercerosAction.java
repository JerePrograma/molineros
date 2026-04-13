package ar.com.ospim.liquidaciones.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTercero;
import ar.com.ospim.liquidaciones.services.LiquidacionDebitoTerceroServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="GrabarCuotaAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Graba registro de liquidaci{on de nota débito a terceros
 * 
 * @author Carlos Rivas
 * 
 */
public class GrabarLiquidacionNotaDebitoTercerosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarLiquidacionNotaDebitoTercerosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.liquidaciones.liquidacion_debitos_terceros.result");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		User user = PortalUtil.getUser(renderRequest);
		
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(renderRequest,
				"periodo");
		Date periodo = null;			
		
		try {
			periodo = formatoDePeriodos.parse( "0" + String.valueOf((Integer.parseInt(periodoMesAnio
					.substring(0, 1))
					+ 1)) + "/" + periodoMesAnio.substring(2, 6));
		} catch (Exception e) {
			periodo = null;
		}
		if (periodo == null){
			try {
				periodo = formatoDePeriodos.parse(Integer.parseInt(periodoMesAnio
						.substring(0, 2))
						+ 1 + "/" + periodoMesAnio.substring(3, 7));
			} catch (Exception e) {
				periodo = null;
			}
		}
		if (periodo == null){
			String periodoHidden = ParamUtil.getString(renderRequest, "periodoHidden");
			try {
				periodo = formatoDePeriodos.parse(periodoHidden);
			} catch (Exception e) {
				periodo = null;
			}
		}

		int id_liquidacion = ParamUtil.getInteger(renderRequest, "id_liquidacion");
		String observaciones = ParamUtil.getString(renderRequest, "observaciones");

		LiquidacionDebitoTercero ltd = new LiquidacionDebitoTercero();
		ltd.setId_liquidacion(id_liquidacion);
		ltd.setPeriodoHasta(periodo);
		ltd.setObservaciones(observaciones);
		
		try {
			if (id_liquidacion == 0) {
					id_liquidacion = LiquidacionDebitoTerceroServiceUtil.save(ltd, user);
			} else {
				LiquidacionDebitoTerceroServiceUtil.update(ltd, user);				
			}
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		renderRequest.setAttribute(
				WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION, id_liquidacion);		
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "");
		}
		return mapping.findForward("portlet.liquidaciones.liquidacion_debitos_terceros.result");	
	}
}
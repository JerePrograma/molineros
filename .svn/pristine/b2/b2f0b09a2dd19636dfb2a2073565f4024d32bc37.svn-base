package ar.com.ospim.afiliados.action;

import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.HistoricoMovimientoAfiliado;
import ar.com.ospim.afiliados.services.HistoricoMovimientoServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class HistoricoMovimientosAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String cuil_titular = ParamUtil
				.getString(renderRequest, "cuil_titular");

		int desde_dia = ParamUtil.getInteger(renderRequest, "desde_dia");
		int desde_mes = ParamUtil.getInteger(renderRequest, "desde_mes");
		int desde_anio = ParamUtil.getInteger(renderRequest, "desde_anio");
		GregorianCalendar fecha_desde = null;
		if (desde_dia != 0 && desde_anio != 0) {
			fecha_desde = new GregorianCalendar(desde_anio, desde_mes,
					desde_dia);
		}

		int hasta_dia = ParamUtil.getInteger(renderRequest, "hasta_dia");
		int hasta_mes = ParamUtil.getInteger(renderRequest, "hasta_mes");
		int hasta_anio = ParamUtil.getInteger(renderRequest, "hasta_anio");
		GregorianCalendar fecha_hasta = null;
		if (hasta_dia != 0 && hasta_anio != 0) {
			fecha_hasta = new GregorianCalendar(hasta_anio, hasta_mes,
					hasta_dia);
		}

		try {
			List<HistoricoMovimientoAfiliado> historico = null;
			if (fecha_desde == null && fecha_hasta == null) {
				historico = HistoricoMovimientoServiceUtil.buscarHistorico(
						cuil_titular, null, null);
			} else if (fecha_hasta != null && fecha_desde == null) {
				historico = HistoricoMovimientoServiceUtil.buscarHistorico(
						cuil_titular, null, fecha_hasta.getTime());
			} else if (fecha_desde != null && fecha_hasta == null) {
				historico = HistoricoMovimientoServiceUtil.buscarHistorico(
						cuil_titular, fecha_desde.getTime(), null);
			} else {
				historico = HistoricoMovimientoServiceUtil.buscarHistorico(
						cuil_titular, fecha_desde.getTime(), fecha_hasta
								.getTime());
			}
			renderRequest.setAttribute(WebKeysAfiliados.HISTORICO_MOVIMIENTOS,
					historico);
		} catch (Exception e) {
			setForward(renderRequest, "portlet.afiliados.error");
		}

		return mapping.findForward(getForward(renderRequest,
				"portlet.historico.movimiento.result.search"));
	}
}
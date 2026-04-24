package ar.com.ospim.liquidaciones.action;

import java.math.BigDecimal;
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

import ar.com.ospim.liquidaciones.beans.DetalleCuota;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

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
 * Graba registro de Cuota
 * 
 * @author Carlos Rivas
 * 
 */
public class GrabarCuotaAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(GrabarCuotaAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.liquidaciones.cuota.result");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		
		
		User user = PortalUtil.getUser(renderRequest);

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		
		String fechaDia = ParamUtil.getString(renderRequest, "diaPer");
		String fechaMes = ParamUtil.getString(renderRequest, "mesPer");
		String fechaAnio = ParamUtil.getString(renderRequest, "anioPer");		
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(renderRequest,"periodo");
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

		int idReintegro = ParamUtil.getInteger(renderRequest, "id_reitnegro");
		int idReintegroUser = ParamUtil.getInteger(renderRequest, "id_reitnegro_user");
		int cuota = ParamUtil.getInteger(renderRequest, "cuota");
		String diagnostico = ParamUtil.getString(renderRequest, "diagnostico");
		String planTratamiento = ParamUtil.getString(renderRequest, "plan_tratamiento");
		String tiempoEstimado = ParamUtil.getString(renderRequest, "tiempo_estimado");
		String pronostico = ParamUtil.getString(renderRequest, "pronostico");
		String informe = ParamUtil.getString(renderRequest, "informe");
		String comprobanteTipo = ParamUtil.getString(renderRequest, "comprobante_tipo");
		String comprobanteLetra = ParamUtil.getString(renderRequest, "comprobante_letra");
		String comprobanteSucu = ParamUtil.getString(renderRequest, "comprobante_sucu");
		String comprobanteNro = ParamUtil.getString(renderRequest, "comprobante_nro");			
		
		int idReclamo = ParamUtil.getInteger(renderRequest, "id_reclamo");
		int idReclamoPrestaciones = ParamUtil.getInteger(renderRequest, "id_reclamo_prestaciones");
		
		int porcentaje_cuota = ParamUtil.getInteger(renderRequest, "porcentaje_cuota");
		String aux_importe = ParamUtil.getString(renderRequest, "importe_cuota");
		BigDecimal importe_cuota = new BigDecimal(aux_importe); 
		
		try {
			DetalleCuota detalleCuota = new DetalleCuota (idReintegroUser, idReintegro, cuota, fecha, periodo, 
					porcentaje_cuota, importe_cuota, 
					diagnostico, planTratamiento, tiempoEstimado, pronostico, informe, 
					comprobanteTipo, comprobanteLetra, comprobanteSucu, comprobanteNro, 0, 
					idReclamo, idReclamoPrestaciones);
			
			ReintegroServiceUtil.actualizaOrtoDetalleCuota(detalleCuota, user);

		} catch (Exception e) {
			_log.error(e);
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "Se guardó correctamente, reclamo asociado.");
		}
		return mapping.findForward("portlet.liquidaciones.cuota.result");
	
	}

}
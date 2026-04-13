package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.exceptions.AfiliadoNoEsBebeException;
import ar.com.ospim.autorizaciones.exceptions.ExcedeCantAutoException;
import ar.com.ospim.autorizaciones.exceptions.NoEsPlanMolineroException;
import ar.com.ospim.autorizaciones.exceptions.PeriodoNoConsecutivoException;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceImpl;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="GenerarAutorizacionPmiAction.java.html"><b><i>View
 * Source</i></b></a>
 * <p>
 * Generar Autorizaciones Recetas PMI
 * 
 * @author Gustavo Fernandez
 * 
 */
public class GenerarAutorizacionPmiAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(GenerarAutorizacionPmiAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String msg = "";

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

		String fechaRecetaDia = ParamUtil.getString(renderRequest,
				"fechaRecetaDia");
		String fechaRecetaMes = ParamUtil.getString(renderRequest,
				"fechaRecetaMes");
		String fechaRecetaAnio = ParamUtil.getString(renderRequest,
				"fechaRecetaAnio");
		Date fechaReceta = null;
		try {
			fechaReceta = formatoDePeriodo.parse(fechaRecetaDia + "/"
					+ (Integer.parseInt(fechaRecetaMes) + 1) + "/"
					+ fechaRecetaAnio);
		} catch (Exception e) {
			fechaReceta = null;
		}
		String tipoReceta = "PMI";
		String altaUsuario = ParamUtil.getString(renderRequest, "usuario_modi");
		int inte = ParamUtil.getInteger(renderRequest, "inte");
		String cuil = ParamUtil.getString(renderRequest, "cuil");
		int numReceta = ParamUtil.getInteger(renderRequest, "receta");
		String observaciones = ParamUtil.getString(renderRequest, "observaciones");

		@SuppressWarnings("unused")
		boolean validaAutorizaciones=false;

		try {
			validaAutorizaciones = AutorizacionesServiceUtil.getValidaAutorizacionPMI(cuil, inte, fechaReceta);
			AutorizacionesServiceUtil.getGenerarAutorizacionPmi(tipoReceta,	fechaReceta, cuil, inte, observaciones, altaUsuario);
			
		} catch (NoEsPlanMolineroException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (AfiliadoNoEsBebeException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (ExcedeCantAutoException e) {
			SessionErrors.add(renderRequest, e.getClass().getName());
		} catch (PeriodoNoConsecutivoException e) {
			
			Date ultimoPeriodo = new AutorizacionesServiceImpl().getValidaPeriodoNoConsecutivo(cuil, inte);
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			String periodo = sdf.format(ultimoPeriodo);
			
			msg = LanguageUtil.get(defaultLocale, "periodo-no-consecutivo");
			msg = msg + periodo;
			SessionErrors.add(renderRequest, "avisoPeriodoNoConsecutivo");
			renderRequest.setAttribute("periodoNoConsecutivo", msg); 
		} catch (Exception e) {
		}

		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "AutoGenerada"); // si todo sale bien
		}

		try {
			List<AutorizacionesPmi> autorizaciones = new ArrayList<AutorizacionesPmi>();
			autorizaciones = AutorizacionesServiceUtil
					.getListaAutorizacionesPmi(fechaReceta,	cuil, inte, numReceta);
			
			renderRequest.removeAttribute("AutorizacionesPmi");
			renderRequest.setAttribute("AutorizacionesPmi", autorizaciones);

		} catch (Exception e) {
			_log.error(e);
		}

		return mapping
				.findForward("portlet.autorizaciones.buscar_autorizacion_pmi");
		
	}

}
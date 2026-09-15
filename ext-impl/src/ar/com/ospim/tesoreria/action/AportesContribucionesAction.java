package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.tesoreria.beans.AportesContribuciones;
import ar.com.ospim.tesoreria.service.AportesContribucionesServiceUtil;

public class AportesContribucionesAction extends PortletAction {

	@Override
	public ActionForward render(
			ActionMapping mapping,
			ActionForm form,
			PortletConfig portletConfig,
			RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws Exception {

		String modo = ParamUtil.getString(renderRequest, "modo", "nuevo");

		if ("historico".equals(modo)) {
			String fechaDesdeStr = ParamUtil.getString(renderRequest, "fechaDesde");

			if (fechaDesdeStr != null && !fechaDesdeStr.trim().equals("")) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);

				Date fechaDesde = sdf.parse(fechaDesdeStr);
				renderRequest.setAttribute("historicoAportesContribuciones", AportesContribucionesServiceUtil.getHistoricoAportesContribuciones(fechaDesde));

				renderRequest.setAttribute("fechaDesdeHistorico", fechaDesde);
			}

			return mapping.findForward("portlet.tesoreria.aportes.contribuciones.historico");
		}

		if ("editar".equals(modo)) {

			String fechaDesdeStr = ParamUtil.getString(renderRequest, "fechaDesde");

			if (fechaDesdeStr != null && !fechaDesdeStr.trim().equals("")) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);

				Date fechaDesde = sdf.parse(fechaDesdeStr);
				AportesContribuciones tope = AportesContribucionesServiceUtil.getAportesContribuciones(fechaDesde);
				renderRequest.setAttribute("AportesContribuciones", tope);
			}
		}

		renderRequest.setAttribute("modo", modo);
		return mapping.findForward("portlet.tesoreria.aportes.contribuciones.editar");
	}

	@Override
	public void processAction(
			ActionMapping mapping,
			ActionForm form,
			PortletConfig portletConfig,
			ActionRequest actionRequest,
			ActionResponse actionResponse)
			throws Exception {

		String redirect = ParamUtil.getString(actionRequest, "redirect");
		String modo = ParamUtil.getString(actionRequest, "modo", "nuevo");
		
		if ("eliminar".equals(modo)) {

			String fechaDesdeStr =
					ParamUtil.getString(actionRequest, "fechaDesde");

			if (fechaDesdeStr == null || fechaDesdeStr.trim().equals("")) {
				throw new Exception("No se encontró el registro a eliminar");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setLenient(false);

			Date fechaDesde = sdf.parse(fechaDesdeStr);

			boolean resultado = AportesContribucionesServiceUtil.eliminarAportesContribuciones(fechaDesde);

			if (!resultado) {
				throw new Exception("No se pudo eliminar el registro");
			}

			SessionMessages.add(
					actionRequest,
					"tope-aportes-eliminado"
			);

			actionResponse.sendRedirect(redirect);

			return;
		}
		
		String remuneracionStr = ParamUtil.getString(actionRequest, "topeRemuneracion");
		String remuneracionMinimaStr = ParamUtil.getString(actionRequest, "topeAporte");
		String fechaDesdeOriginalStr = ParamUtil.getString(actionRequest, "fechaDesdeOriginal");
		
		int fechaDesdeDia = ParamUtil.getInteger(actionRequest, "fechaDesdeDia");
		int fechaDesdeMes = ParamUtil.getInteger(actionRequest, "fechaDesdeMes");
		int fechaDesdeAnio = ParamUtil.getInteger(actionRequest, "fechaDesdeAnio");
		int fechaHastaDia = ParamUtil.getInteger(actionRequest, "fechaHastaDia");
		int fechaHastaMes = ParamUtil.getInteger(actionRequest, "fechaHastaMes");
		int fechaHastaAnio = ParamUtil.getInteger(actionRequest, "fechaHastaAnio");

		try {
			
			BigDecimal remuneracion = parseImporte(remuneracionStr);

			if (remuneracion == null) {
				SessionErrors.add(actionRequest, "remuneracion-maxima-requerida");
				
				volverFormulario(
						actionResponse,
						modo,
						fechaDesdeDia,
						fechaDesdeMes,
						fechaDesdeAnio,
						fechaHastaDia,
						fechaHastaMes,
						fechaHastaAnio,
						remuneracionStr,
						remuneracionMinimaStr,
						fechaDesdeOriginalStr
				);

				return;
			}

			if (remuneracion.compareTo(BigDecimal.ZERO) <= 0) {
				SessionErrors.add(actionRequest, "remuneracion-maxima-mayor-cero");

				volverFormulario(
						actionResponse,
						modo,
						fechaDesdeDia,
						fechaDesdeMes,
						fechaDesdeAnio,
						fechaHastaDia,
						fechaHastaMes,
						fechaHastaAnio,
						remuneracionStr,
						remuneracionMinimaStr,
						fechaDesdeOriginalStr
				);

				return;
			}

			BigDecimal remuneracionMinima = parseImporte(remuneracionMinimaStr);

			if (remuneracionMinima == null) {
				SessionErrors.add(actionRequest, "remuneracion-minima-requerida");

				volverFormulario(
						actionResponse,
						modo,
						fechaDesdeDia,
						fechaDesdeMes,
						fechaDesdeAnio,
						fechaHastaDia,
						fechaHastaMes,
						fechaHastaAnio,
						remuneracionStr,
						remuneracionMinimaStr,
						fechaDesdeOriginalStr
				);

				return;
			}

			if (remuneracionMinima.compareTo(BigDecimal.ZERO) <= 0) {
				SessionErrors.add(actionRequest, "remuneracion-minima-mayor-cero");

				volverFormulario(
						actionResponse,
						modo,
						fechaDesdeDia,
						fechaDesdeMes,
						fechaDesdeAnio,
						fechaHastaDia,
						fechaHastaMes,
						fechaHastaAnio,
						remuneracionStr,
						remuneracionMinimaStr,
						fechaDesdeOriginalStr
				);

				return;
			}
			
			if (remuneracionMinima.compareTo(remuneracion) > 0) {

				SessionErrors.add(actionRequest, "remuneracion-minima-mayor-maxima");

				volverFormulario(
						actionResponse,
						modo,
						fechaDesdeDia,
						fechaDesdeMes,
						fechaDesdeAnio,
						fechaHastaDia,
						fechaHastaMes,
						fechaHastaAnio,
						remuneracionStr,
						remuneracionMinimaStr,
						fechaDesdeOriginalStr
				);

				return;
			}

			boolean resultado = false;
			
			if ("editar".equals(modo)) {
				if (fechaDesdeOriginalStr == null || fechaDesdeOriginalStr.trim().equals("")) {
					throw new Exception("No se encontró el registro a modificar");
				}

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);

				Date fechaDesdeOriginal = sdf.parse(fechaDesdeOriginalStr);
				User user = PortalUtil.getUser(actionRequest);

				String usuario = user != null ? user.getScreenName() : "";

				resultado = AportesContribucionesServiceUtil.modificarAportesContribuciones(
								fechaDesdeOriginal,
								remuneracion,
								remuneracionMinima,
								usuario
						);
			} else {

				Date fechaDesde = crearFecha(fechaDesdeDia, fechaDesdeMes, fechaDesdeAnio);
				Date fechaHasta = crearFecha(fechaHastaDia, fechaHastaMes, fechaHastaAnio);

				if (fechaHasta.before(fechaDesde)) {

					SessionErrors.add(actionRequest, "fecha-periodo-invalida");

					volverFormulario(
							actionResponse,
							modo,
							fechaDesdeDia,
							fechaDesdeMes,
							fechaDesdeAnio,
							fechaHastaDia,
							fechaHastaMes,
							fechaHastaAnio,
							remuneracionStr,
							remuneracionMinimaStr,
							fechaDesdeOriginalStr
					);

					return;
				}

				boolean periodoCargado = AportesContribucionesServiceUtil.existePeriodoSuperpuesto(fechaDesde, fechaHasta);

				if (periodoCargado) {
					SessionErrors.add(actionRequest, "periodo-ya-cargado");

					volverFormulario(
							actionResponse,
							modo,
							fechaDesdeDia,
							fechaDesdeMes,
							fechaDesdeAnio,
							fechaHastaDia,
							fechaHastaMes,
							fechaHastaAnio,
							remuneracionStr,
							remuneracionMinimaStr,
							fechaDesdeOriginalStr
					);
					return;
				}

				resultado = AportesContribucionesServiceUtil.insertarAportesContribuciones(
								fechaDesde,
								fechaHasta,
								remuneracion,
								remuneracionMinima
						);
			}

			if (!resultado) {
				throw new Exception("No se pudo guardar el registro");
			}

			/*
			 * Redirección al listado luego de guardar.
			 * Evita que al refrescar el navegador se repita el POST.
			 */
			SessionMessages.add(actionRequest, "tope-aportes-guardado");
			actionResponse.sendRedirect(redirect);

			return;

		} catch (Exception e) {
			SessionErrors.add(actionRequest, "error-guardar-tope-aportes");

			volverFormulario(
					actionResponse,
					modo,
					fechaDesdeDia,
					fechaDesdeMes,
					fechaDesdeAnio,
					fechaHastaDia,
					fechaHastaMes,
					fechaHastaAnio,
					remuneracionStr,
					remuneracionMinimaStr,
					fechaDesdeOriginalStr
			);
		}
	}

	//Regresa al formulario conservando los datos ingresados por el usuario
	private void volverFormulario(
			ActionResponse actionResponse,
			String modo,
			int fechaDesdeDia,
			int fechaDesdeMes,
			int fechaDesdeAnio,
			int fechaHastaDia,
			int fechaHastaMes,
			int fechaHastaAnio,
			String remuneracionStr,
			String remuneracionMinimaStr,
			String fechaDesdeOriginalStr) {

		actionResponse.setRenderParameter("struts_action", "/tesoreria/aportes_contribuciones");
		actionResponse.setRenderParameter("modo", modo);
		
		if (!"editar".equals(modo)) {
			
			actionResponse.setRenderParameter("fechaDesdeDia", String.valueOf(fechaDesdeDia));
			actionResponse.setRenderParameter("fechaDesdeMes", String.valueOf(fechaDesdeMes));
			actionResponse.setRenderParameter("fechaDesdeAnio", String.valueOf(fechaDesdeAnio));
			actionResponse.setRenderParameter("fechaHastaDia", String.valueOf(fechaHastaDia));
			actionResponse.setRenderParameter("fechaHastaMes", String.valueOf(fechaHastaMes));
			actionResponse.setRenderParameter("fechaHastaAnio", String.valueOf(fechaHastaAnio));
		}

		actionResponse.setRenderParameter("topeRemuneracion", remuneracionStr);
		actionResponse.setRenderParameter("topeAporte", remuneracionMinimaStr);

		if (fechaDesdeOriginalStr != null && !fechaDesdeOriginalStr.trim().equals("")) {
			actionResponse.setRenderParameter("fechaDesde", fechaDesdeOriginalStr);
		}
	}
	
	private Date crearFecha(
			int dia,
			int mes,
			int anio)
			throws Exception {

		Calendar calendario = Calendar.getInstance();

		calendario.clear();
		calendario.setLenient(false);
		calendario.set(anio, mes, dia, 0, 0, 0);

		return calendario.getTime();
	}

	/**
	 * Convierte importes ingresados a BigDecimal.
	 * Ejemplo:
	 * 4.594.798,00 -> 4594798.00
	 */
	private BigDecimal parseImporte(String valor) throws Exception {

		if (valor == null || valor.trim().equals("")) {
			return null;
		}

		String numero = valor.trim().replace("$", "").replace(" ", "");

		if (numero.contains(",") && numero.contains(".")) {
			numero = numero.replace(".", "");
			numero = numero.replace(",", ".");
		} else if (numero.contains(",")) {
			numero = numero.replace(",", ".");
		}

		return new BigDecimal(numero);
	}
}
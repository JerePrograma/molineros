package ar.com.ospim.tesoreria.action;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.SaldoDiarioCuentaBancaria;
import ar.com.ospim.tesoreria.services.SaldoDiarioCuentaBancariaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class BuscarSaldoDiarioCuentasBancariasAction extends PortletAction {

	private static Log _log = LogFactoryUtil.getLog(BuscarSaldoDiarioCuentasBancariasAction.class);

	private static final String INICIAL = "portlet.tesoreria.buscar.saldo.diario.cuentas.bancarias.inicial";

	private static final String RESULTADO = "portlet.tesoreria.buscar.saldo.diario.cuentas.bancarias.resultado";

	public void processAction(
			ActionMapping mapping,
			ActionForm form,
			PortletConfig portletConfig,
			ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, "cmd");

		try {
			if ("agregar".equals(cmd)) {
				agregarSaldo(actionRequest);

				SessionMessages.add(actionRequest, "saldo_diario_agregado");

				actionResponse.setWindowState(WindowState.MAXIMIZED);

				actionResponse.setRenderParameter(
					"struts_action",
					"/tesoreria/buscar_saldo_diario_cuentas_bancarias"
				);

				actionResponse.setRenderParameter("cmd", "inicial");
				actionResponse.setRenderParameter("buscar_luego", "true");

				actionResponse.setRenderParameter(
					"cta_bancaria_buscar",
					ParamUtil.getString(actionRequest, "cta_bancaria_buscar")
				);

				actionResponse.setRenderParameter(
					"fechaDesdeDia",
					ParamUtil.getString(actionRequest, "fechaDesdeDia")
				);

				actionResponse.setRenderParameter(
					"fechaDesdeMes",
					ParamUtil.getString(actionRequest, "fechaDesdeMes")
				);

				actionResponse.setRenderParameter(
					"fechaDesdeAnio",
					ParamUtil.getString(actionRequest, "fechaDesdeAnio")
				);

				actionResponse.setRenderParameter(
					"fechaHastaDia",
					ParamUtil.getString(actionRequest, "fechaHastaDia")
				);

				actionResponse.setRenderParameter(
					"fechaHastaMes",
					ParamUtil.getString(actionRequest, "fechaHastaMes")
				);

				actionResponse.setRenderParameter(
					"fechaHastaAnio",
					ParamUtil.getString(actionRequest, "fechaHastaAnio")
				);

				setForward(actionRequest, INICIAL);

				return;
			}

			actionResponse.setWindowState(WindowState.MAXIMIZED);

			actionResponse.setRenderParameter("struts_action","/tesoreria/buscar_saldo_diario_cuentas_bancarias");

			actionResponse.setRenderParameter("cmd", "inicial");

			setForward(actionRequest, INICIAL);
		}
		catch (Exception e) {

			String mensajeError = e.getMessage();

			if (mensajeError != null && mensajeError.indexOf(":") >= 0) {
				mensajeError = mensajeError.substring(
					mensajeError.lastIndexOf(":") + 1
				).trim();
			}

			if (mensajeError != null &&
					(mensajeError.indexOf("Ya existe un saldo cargado") >= 0 ||
					 mensajeError.indexOf("Importe o saldo inválido") >= 0)) {

				_log.debug(mensajeError);
			}
			else {
				_log.error(e);
			}		
			
			actionResponse.setWindowState(WindowState.MAXIMIZED);

			actionResponse.setRenderParameter("struts_action","/tesoreria/buscar_saldo_diario_cuentas_bancarias");

			actionResponse.setRenderParameter("cmd", "inicial");
			actionResponse.setRenderParameter("mensaje_error", mensajeError);
			actionResponse.setRenderParameter("buscar_luego", "true");

			actionResponse.setRenderParameter("cta_bancaria_buscar", ParamUtil.getString(actionRequest, "cta_bancaria_buscar"));

			actionResponse.setRenderParameter("fechaDesdeDia", ParamUtil.getString(actionRequest, "fechaDesdeDia"));

			actionResponse.setRenderParameter("fechaDesdeMes", ParamUtil.getString(actionRequest, "fechaDesdeMes"));

			actionResponse.setRenderParameter("fechaDesdeAnio", ParamUtil.getString(actionRequest, "fechaDesdeAnio"));

			actionResponse.setRenderParameter("fechaHastaDia", ParamUtil.getString(actionRequest, "fechaHastaDia"));

			actionResponse.setRenderParameter("fechaHastaMes", ParamUtil.getString(actionRequest, "fechaHastaMes"));

			actionResponse.setRenderParameter("fechaHastaAnio", ParamUtil.getString(actionRequest, "fechaHastaAnio"));

			setForward(actionRequest, INICIAL);
		}
	}

	public ActionForward render(
			ActionMapping mapping,
			ActionForm form,
			PortletConfig portletConfig,
			RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws Exception {

		try {
			String cmd = ParamUtil.getString(renderRequest, "cmd");

			if ("buscar".equals(cmd)) {
				buscarSaldos(renderRequest, renderResponse);

				return mapping.findForward(RESULTADO);
			}

			if ("borrar".equals(cmd)) {
				borrarSaldo(renderRequest);
				buscarSaldos(renderRequest, renderResponse);

				return mapping.findForward(RESULTADO);
			}

			return mapping.findForward(INICIAL);
		}
		catch (Exception e) {
			_log.error(e);

			renderRequest.setAttribute("mensaje_error", e.getMessage());

			return mapping.findForward(INICIAL);
		}
	}

	private void buscarSaldos(
			RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws Exception {

		int entidad = WebKeysGlobal.OSPIM;

		int desdeDia = ParamUtil.getInteger(renderRequest, "desde_dia");
		int desdeMes = ParamUtil.getInteger(renderRequest, "desde_mes");
		int desdeAnio = ParamUtil.getInteger(renderRequest, "desde_anio");

		GregorianCalendar fechaDesde = null;

		if (desdeDia != 0 && desdeAnio != 0) {
			fechaDesde = new GregorianCalendar(desdeAnio, desdeMes, desdeDia);
		}

		int hastaDia = ParamUtil.getInteger(renderRequest, "hasta_dia");
		int hastaMes = ParamUtil.getInteger(renderRequest, "hasta_mes");
		int hastaAnio = ParamUtil.getInteger(renderRequest, "hasta_anio");

		GregorianCalendar fechaHasta = null;

		if (hastaDia != 0 && hastaAnio != 0) {
			fechaHasta = new GregorianCalendar(hastaAnio, hastaMes, hastaDia);
		}

		int ctaBcria = ParamUtil.getInteger(renderRequest, "cta_bcria");

		List<SaldoDiarioCuentaBancaria> saldos =
			SaldoDiarioCuentaBancariaServiceUtil.buscar(
				fechaDesde != null ? fechaDesde.getTime() : null,
				fechaHasta != null ? fechaHasta.getTime() : null,
				ctaBcria,
				entidad
			);

		renderRequest.setAttribute(WebKeysTesoreria.SALDOS_DIARIOS_CUENTAS_BANCARIAS, saldos);
	}

	private void agregarSaldo(ActionRequest actionRequest)
		throws Exception {

		int idCuentaBcria = ParamUtil.getInteger(actionRequest, "cta_bancaria_agregar");

		int dia = ParamUtil.getInteger(actionRequest, "fechaAgregarDia");
		int mes = ParamUtil.getInteger(actionRequest, "fechaAgregarMes");
		int anio = ParamUtil.getInteger(actionRequest, "fechaAgregarAnio");

		GregorianCalendar fechaInicioEjercicio = null;

		if (dia != 0 && anio != 0) {
			fechaInicioEjercicio = new GregorianCalendar(anio, mes, dia);
		}

		String saldoString = ParamUtil.getString(actionRequest, "saldo");

		if (saldoString != null) {
			saldoString = saldoString.trim().replace(",", ".");
		}

		BigDecimal saldo = null;

		if (saldoString != null && !saldoString.equals("")) {
			try {
				saldo = new BigDecimal(saldoString);
			}
			catch (NumberFormatException e) {
				throw new Exception("Importe o saldo inválido.");
			}
		}

		SaldoDiarioCuentaBancariaServiceUtil.agregar(idCuentaBcria, fechaInicioEjercicio != null ? fechaInicioEjercicio.getTime() : null, saldo);
	
	}

	private void borrarSaldo(RenderRequest renderRequest)
		throws Exception {

		int idCuentaBcria = ParamUtil.getInteger(renderRequest, "id_cuenta_bcria");
		String fechaInicioEjercicioString = ParamUtil.getString(renderRequest, "fecha_inicio_ejercicio");
		Date fechaInicioEjercicio = Date.valueOf(fechaInicioEjercicioString);

		SaldoDiarioCuentaBancariaServiceUtil.borrar(idCuentaBcria, fechaInicioEjercicio);
	}	
	
}
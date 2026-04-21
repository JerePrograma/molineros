package ar.com.ospim.liquidaciones.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.BusquedaDebitosTercerizadorasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class HayReporteTercerizadoraGrabadoAction extends JSONAction {

	private static final Log _log = LogFactoryUtil.getLog(HayReporteTercerizadoraGrabadoAction.class);

	@Override
	public String getJSON(ActionMapping mapping, ActionForm form,
						  HttpServletRequest req, HttpServletResponse res) throws Exception {

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");

		String fechaDesdeMes  = ParamUtil.getString(req, "fechaDesdeMes");
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");

		// Nota: este param en tu JSP es la tercerizadora, aunque el nombre diga "tipo..."
		String idTercerizadora = ParamUtil.getString(req, "tipo_debitos_tercerizadoras");

		// tipo proceso con aliases (lo que mandás desde JS)
		String tipoProcesoRaw = ParamUtil.getString(req, "tipo_proceso");
		if (tipoProcesoRaw == null || tipoProcesoRaw.trim().length() == 0) {
			tipoProcesoRaw = ParamUtil.getString(req, "tipoProceso");
		}
		if (tipoProcesoRaw == null || tipoProcesoRaw.trim().length() == 0) {
			tipoProcesoRaw = ParamUtil.getString(req, "tipoDebito");
		}
		tipoProcesoRaw = (tipoProcesoRaw != null) ? tipoProcesoRaw.trim().toUpperCase() : "";

		// Mapeo a constante DB (DEBITOS_*) si viene alias (LI/HO/RE/PR)
		String tipoProcesoDb = resolveTipoProceso(tipoProcesoRaw);

		Date fechaDesde;
		try {
			// mes viene 0-based desde liferay input-date
			fechaDesde = formatoDeFechas.parse("01/" + (Integer.parseInt(fechaDesdeMes) + 1) + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}

		if (fechaDesde == null) {
			return "{ \"resultado\" : \"0\", \"error\": \"periodo_invalido\", " +
					"\"tipoProceso\": \"" + tipoProcesoRaw + "\", " +
					"\"tipoProcesoDb\": \"" + tipoProcesoDb + "\" }";
		}

		Date fechaHasta = DateUtils.getLastDateOfMonth(fechaDesde, false);

		// LOGS CLAVE
		try {
			_log.info("[HAY_GRABADO] mes=" + fechaDesdeMes
					+ " anio=" + fechaDesdeAnio
					+ " terc=" + idTercerizadora
					+ " tipoProcesoRaw=" + tipoProcesoRaw
					+ " tipoProcesoDb=" + tipoProcesoDb
					+ " fechaHasta=" + fechaHasta);
		} catch (Exception ignore) {}

		boolean result = BusquedaDebitosTercerizadorasServiceUtil
				.existeReporteGrabadoDebitoTercerizadoras(fechaHasta, idTercerizadora, tipoProcesoDb);

		return "{ \"resultado\" : \"" + (result ? "1" : "0") + "\"," +
				" \"tipoProceso\" : \"" + tipoProcesoRaw + "\"," +
				" \"tipoProcesoDb\" : \"" + tipoProcesoDb + "\" }";
	}

	/**
	 * Convierte aliases de UI a las constantes que usa la DB/SP.
	 * Si ya viene “DEBITOS_*”, lo devuelve tal cual.
	 */
	private String resolveTipoProceso(String t) {
		if (t == null) return "";
		t = t.trim().toUpperCase();

		if ("LI".equals(t)) return WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES;
		if ("HO".equals(t)) return WebKeysLiquidaciones.DEBITOS_HOSPITALES;
		if ("RE".equals(t)) return WebKeysLiquidaciones.DEBITOS_REINTEGROS;
		if ("PR".equals(t)) return WebKeysLiquidaciones.DEBITOS_PRESTADORES;

		return t;
	}
}

package ar.com.ospim.afiliados.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.services.LiquidaDesreguladosServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteListadosSSTxt extends ReporteXLS {
	private static Log _log = LogFactoryUtil.getLog(ReporteListadosSSTxt.class);

	public static List<String> generaReporteListadoSSAlta(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");

		String fechaDesde = ParamUtil.getString(req, "periodoDesde");
		boolean registrar = ParamUtil.getBoolean(req, "registrar");
		// String fechaHasta = ParamUtil.getString(req, "periodoHasta");

		try {
			Date fechaIni = format
					.parse(Integer.parseInt(fechaDesde.split("_")[0]) + 1 + "-"
							+ fechaDesde.split("_")[1]);
			ReportesListadosSSServiceImpl service = new ReportesListadosSSServiceImpl();

			ArrayList<String> lista = (ArrayList<String>) service
					.getReporteListadoSSAlta(fechaIni, registrar);

			return lista;
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	public static List<String> generaReporteListadoSSBaja(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		String fechaDesde = ParamUtil.getString(req, "periodoDesde");
		boolean registrar = ParamUtil.getBoolean(req, "registrar");

		try {
			Date fechaIni = format
					.parse(Integer.parseInt(fechaDesde.split("_")[0]) + 1 + "-"
							+ fechaDesde.split("_")[1]);
			ReportesListadosSSServiceImpl service = new ReportesListadosSSServiceImpl();

			ArrayList<String> lista = (ArrayList<String>) service
					.getReporteListadoSSBaja(fechaIni, registrar);

			return lista;
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	public static List<String> generaReporteListadoSSModificaciones(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
		String fechaDesde = ParamUtil.getString(req, "periodoDesde");

		boolean registrar = ParamUtil.getBoolean(req, "registrar");

		try {
			Date fechaIni = format
					.parse(Integer.parseInt(fechaDesde.split("_")[0]) + 1 + "-"
							+ fechaDesde.split("_")[1]);
			ReportesListadosSSServiceImpl service = new ReportesListadosSSServiceImpl();
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaIni);
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
			ArrayList<String> lista = (ArrayList<String>) service
					.getReporteListadoSSModificaciones(fechaIni, cal.getTime(),
							registrar);

			return lista;
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

	public static List<String> generaArchivoDerivaTerc(HttpServletRequest req,
			HttpServletResponse res) {

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("dd/MM/yyyy");
		String fechaLiq = ParamUtil.getString(req, "fechaLiq");
		String id_terc = ParamUtil.getString(req, "id_terc");
		Date fechaLiqDate = null;
		try {
			fechaLiqDate = formatoDePeriodos.parse(fechaLiq);
		} catch (Exception e) {
			fechaLiqDate = null;
		}

		try {
			ArrayList<String> lista = (ArrayList<String>) LiquidaDesreguladosServiceUtil
					.getDerivaDesregulaString(id_terc, fechaLiqDate);
			return lista;
		} catch (Exception e) {
			_log.error("Error al generar archivo Deriva", e);
			return null;
		}
	}

	public static List<String> generaArchivoAfiliadosSinAporte(HttpServletRequest req,
			HttpServletResponse res) {

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("dd/MM/yyyy");
		String fechaLiq = ParamUtil.getString(req, "fechaLiq");
		String id_terc = ParamUtil.getString(req, "id_terc");
		Date fechaLiqDate = null;
		try {
			fechaLiqDate = formatoDePeriodos.parse(fechaLiq);
		} catch (Exception e) {
			fechaLiqDate = null;
		}

		try {
			ArrayList<String> lista = (ArrayList<String>) LiquidaDesreguladosServiceUtil.
					getAfiliadosSinAporteString(id_terc, fechaLiqDate);
			return lista;
		} catch (Exception e) {
			_log.error("Error al generar archivo Afiliados sin Aportes", e);
			return null;
		}
	}

	public static List<String> generaReporteSistemaViejo(
			HttpServletRequest req, HttpServletResponse res) {

		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
		String vigenciaArchivoDia = ParamUtil.getString(req,
				"vigenciaArchivoDia");
		String vigenciaArchivoMes = ParamUtil.getString(req,
				"vigenciaArchivoMes");
		String vigenciaArchivoAnio = ParamUtil.getString(req,
				"vigenciaArchivoAnio");
		Date fechaArchivo = null;
		try {
			fechaArchivo = formatoDePeriodo.parse(vigenciaArchivoDia + "/"
					+ (Integer.parseInt(vigenciaArchivoMes)) + "/"
					+ vigenciaArchivoAnio);

			ReportesListadosSSServiceImpl service = new ReportesListadosSSServiceImpl();
			ArrayList<String> lista = (ArrayList<String>) service
					.getReporteSistemaViejoUoma(fechaArchivo);
			return lista;
		} catch (Exception e) {
			_log.error("Error al generar reporte de sistema viejo uoma", e);
			return null;
		}
	}
	
	public static List<String> generaArchivoComisionesPrevencion(HttpServletRequest req,
			HttpServletResponse res) {

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("dd/MM/yyyy");
		String fechaLiq = ParamUtil.getString(req, "fechaLiq");
		String id_terc = ParamUtil.getString(req, "id_terc");
		Date fechaLiqDate = null;
		try {
			fechaLiqDate = formatoDePeriodos.parse(fechaLiq);
		} catch (Exception e) {
			fechaLiqDate = null;
		}

		try {
			ArrayList<String> lista = (ArrayList<String>) LiquidaDesreguladosServiceUtil.getComisionesTercerizadoraString(id_terc, fechaLiqDate);
			return lista;
		} catch (Exception e) {
			_log.error("Error al generar archivo Comisiones Tercerizadoras", e);
			return null;
		}
	}
}

package ar.com.ospim.tesoreria.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteCuentasCorrientesExcel extends ReporteCuentaCorriente {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCuentasCorrientesExcel.class);

	public static HSSFWorkbook generaReporteCtasCtes(HttpServletRequest req,
			HttpServletResponse res) {

		boolean incluirReintegros = ParamUtil.getBoolean(req,
				"incluir_reintegros");
		boolean incluirReintegros_farmacia = ParamUtil.getBoolean(req,
				"incluir_reintegros_farmacia");
		boolean incluirLiquidaciones = ParamUtil.getBoolean(req,
				"incluir_liquidaciones");
		boolean incluirLiquidaciones_farmacia = ParamUtil.getBoolean(req,
				"incluir_liquidaciones_farmacia");
		boolean incluirProveedores = ParamUtil.getBoolean(req,
				"incluir_proveedores");
		
		int entidad=ParamUtil.getInteger(req, "entidad");

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");		
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
		String fechaPagoHastaDia = ParamUtil
				.getString(req, "fechaPagoHastaDia");
		String fechaPagoHastaMes = ParamUtil
				.getString(req, "fechaPagoHastaMes");
		fechaPagoHastaMes = String
				.valueOf(Integer.valueOf(fechaPagoHastaMes) + 1);
		String fechaPagoHastaAnio = ParamUtil.getString(req,
				"fechaPagoHastaAnio");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);
		boolean soloConSaldo = ParamUtil.getBoolean(req, "soloConSaldo");
		boolean mostrarSoloComprobantesConSaldo = ParamUtil.getBoolean(req,
				"mostrarSoloComprobantesConSaldo");
		boolean mostrarMasInfo = ParamUtil.getBoolean(req, "mostrarMasInfo");

		if (seccional != 0) {
			sucu = "000";
		}

		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			Date fechaPagoHasta = format.parse(fechaPagoHastaDia + "-"
					+ fechaPagoHastaMes + "-" + fechaPagoHastaAnio);

			if (!mostrarSoloComprobantesConSaldo) {
				fechaPagoHasta = null;
			}

			if (mostrarMasInfo && !mostrarSoloComprobantesConSaldo) {
				fechaPagoHasta = fechaFin;
			}
			//LUEGO DE LA PRUEBA HACERLO PARA LAS 3 ENTIDADES
			List<EstadoInicialCuentaCorriente> saldoIni = null;			
			

			if (saldoIni == null) {
				saldoIni = new ArrayList<EstadoInicialCuentaCorriente>();
			}

			List<CuentaCorriente> ctas = ContabilidadServiceUtil
					.cuentaCorrienteAcreedores(fechaIni, fechaFin, cuit, sucu,
							seccional, fechaPagoHasta, incluirProveedores,
							incluirLiquidaciones, incluirReintegros, incluirLiquidaciones_farmacia, incluirReintegros_farmacia, entidad );
			return generarReporte(fechaIni, fechaFin, ctas, entidad==WebKeysGlobal.UOMA?false:true, saldoIni,
					soloConSaldo, mostrarSoloComprobantesConSaldo,
					mostrarMasInfo,incluirProveedores,
					incluirLiquidaciones, incluirReintegros, incluirLiquidaciones_farmacia, incluirReintegros_farmacia, entidad);
		} catch (Exception e) {
			_log.error("Error al generar cuenta corriente acreedores", e);
			return null;
		}
	}

}

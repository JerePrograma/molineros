package ar.com.ospim.tesoreria.reportes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.CuentaCorriente.Informacion;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteCuentasCorrienteActasYConveniosExcel extends
		ReporteCuentaCorriente {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCuentasCorrienteActasYConveniosExcel.class);

	public static HSSFWorkbook generaReporteCtasCtes(HttpServletRequest req,
			HttpServletResponse res) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
		String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
		String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
		fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
		String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
		boolean aportesContrib = ParamUtil.getBoolean(req, "apoContrib");

		String cuit = ParamUtil.getString(req, "cuit_entidad");
		String sucu = ParamUtil.getString(req, "sucursal_entidad");
		Integer seccional = ParamUtil.getInteger(req, "id_seccional", 0);

		boolean soloConSaldo = ParamUtil.getBoolean(req, "soloConSaldo");

		int entidad = ParamUtil.getInteger(req, "entidad");

		String tipoReporte = ParamUtil.getString(req, "tipoReporte");
		int id = ParamUtil.getInteger(req, "id");

		if (seccional != 0) {
			sucu = "000";
		}

		try {
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			List<EstadoInicialCuentaCorriente> saldoIni = null;
			List<CuentaCorriente> ctas = null;
			if (aportesContrib) {
				ctas = ContabilidadServiceUtil
						.cuentaCorrienteActasYConveniosConApoContrib(fechaIni,
								fechaFin, cuit, sucu, seccional);

			} else {
				ctas = ContabilidadServiceUtil.cuentaCorrienteActasYConvenios(
						fechaIni, fechaFin, cuit, sucu, seccional, id,
						tipoReporte, entidad);
			}
			if (StringUtils.checkNotEmpty(cuit) && ctas.size() == 0) {
				CuentaCorriente e = new CuentaCorriente();
//DS - Agregado porque para las empresas sin moviviento no aparece la Razon Social en el listado 25/08/2020				
				Empresa emp = EmpresaServiceUtil.getEmpleadorCompleto(cuit, sucu);
				e.setEmpresa(emp);
				
//				e.setEmpresa(new Empresa(cuit, sucu, ""));
				e.setInfo(new ArrayList<Informacion>());
				ctas.add(e);
			}
			// si no ingresa el cuit trae saldo incial para todas las cuentas y los genera
			if (StringUtils.checkEmpty(cuit)) {
				for (CuentaCorriente cuentaCorriente : ctas) {
					ContabilidadServiceUtil
							.saldoInicialCorrienteActasYConvenios(cuentaCorriente.getEmpresa().getCuit(), cuentaCorriente.getEmpresa().getSucursal(),
									seccional, fechaIni, entidad);	
				}
				saldoIni = ContabilidadServiceUtil
						.saldoInicialCorrienteActasYConvenios(cuit, sucu,
								seccional, fechaIni, entidad);	
			}else{//Solo genera el saldo inicial para esa empresa
				saldoIni = ContabilidadServiceUtil
						.saldoInicialCorrienteActasYConvenios(cuit, sucu,
								seccional, fechaIni, entidad);				
			}
					
			return generarReporteAcCo(fechaIni, fechaFin, ctas, false,
					saldoIni, soloConSaldo, false, false, entidad);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
	}

}

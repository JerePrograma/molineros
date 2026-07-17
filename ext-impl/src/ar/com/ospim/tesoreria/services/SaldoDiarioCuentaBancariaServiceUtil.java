package ar.com.ospim.tesoreria.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.beans.SaldoDiarioCuentaBancaria;

public class SaldoDiarioCuentaBancariaServiceUtil {

	public static List<SaldoDiarioCuentaBancaria> buscar(
			Date fechaDesde,
			Date fechaHasta,
			int idCuentaBcria,
			int entidad)
		throws Exception {

		SaldoDiarioCuentaBancariaServiceImpl service = new SaldoDiarioCuentaBancariaServiceImpl();

		return service.buscar(fechaDesde, fechaHasta, idCuentaBcria, entidad);
	}

	public static void agregar(
			int idCuentaBcria,
			Date fechaInicioEjercicio,
			BigDecimal saldo)
		throws Exception {

		SaldoDiarioCuentaBancariaServiceImpl service = new SaldoDiarioCuentaBancariaServiceImpl();

		service.agregar(idCuentaBcria, fechaInicioEjercicio, saldo);
	}

	public static void borrar(
			int idCuentaBcria,
			Date fechaInicioEjercicio)
		throws Exception {

		SaldoDiarioCuentaBancariaServiceImpl service = new SaldoDiarioCuentaBancariaServiceImpl();

		service.borrar(idCuentaBcria, fechaInicioEjercicio);
	}
}
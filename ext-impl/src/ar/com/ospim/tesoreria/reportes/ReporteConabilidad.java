package ar.com.ospim.tesoreria.reportes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ar.com.ospim.liquidaciones.ordenespago.reportes.ReporteXLS;
import ar.com.ospim.tesoreria.beans.BalanceSumasYSaldos;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ReporteConabilidad extends ReporteXLS {
	protected static List<BalanceSumasYSaldos> getSaldoInicial(
			Calendar desdeReporte, Calendar desdeEjercicio,
			Calendar hastaEjercicio, boolean incluirAutomaticos,
			boolean incluirManuales, boolean incluir_asiento_inicial, int entidad) {		
		
		List<BalanceSumasYSaldos> saldosIniciales;
		if (DateUtils.compararFechasTruncarEnDia(desdeEjercicio.getTime(),
				desdeReporte.getTime()) != 0) {
			Calendar hastaSaldoInicial = Calendar.getInstance();
			hastaSaldoInicial.setTime(desdeReporte.getTime());
			hastaSaldoInicial.add(Calendar.DATE, -1);
			saldosIniciales = AsientoServiceUtil.buscarBalanceSumasYSaldos(
					desdeEjercicio.getTime(), hastaSaldoInicial.getTime(),
					incluirAutomaticos, incluirManuales,
					incluir_asiento_inicial, entidad);
		} else {
			if (!incluir_asiento_inicial) {
				return new ArrayList<BalanceSumasYSaldos>();
			}
			saldosIniciales = BalanceSumasYSaldos
					.buildBalanceFromAsientos(AsientoServiceUtil
							.buscarAsientosConDetalle(desdeEjercicio.getTime(),
									hastaEjercicio.getTime(), 1, 1, true, true, entidad));
		}
		return saldosIniciales;
	}
}

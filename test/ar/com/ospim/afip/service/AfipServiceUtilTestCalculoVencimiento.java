package ar.com.ospim.afip.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import ar.com.ospim.global.beans.Feriado;

public class AfipServiceUtilTestCalculoVencimiento extends TestCase {
	@SuppressWarnings("static-access")
	public void testDigitoFinal0y1_VencimientoAFIPDigito0y1CaeSabadoFeriadoLunes() {
		AfipServiceUtil service = getCalculadorIntereses();

		Calendar periodo = getPeriodoJunio();
		Date vencimiento0 = service.getVencimientoOriginalAFIP("XXXX0",
				periodo.getTime());
		verificarDiaIgualA(10, vencimiento0);

		Date vencimiento1 = service.getVencimientoOriginalAFIP("XXXX1",
				periodo.getTime());
		verificarDiaIgualA(10, vencimiento1);
	}

	@SuppressWarnings("static-access")
	public void testDigitoFinal2y3_VencimientoAFIPDigito0y1CaeSabadoFeriadoLunes() {
		AfipServiceUtil service = getCalculadorIntereses();

		Calendar periodo = getPeriodoJunio();
		Date vencimiento0 = service.getVencimientoOriginalAFIP("XXX2",
				periodo.getTime());
		verificarDiaIgualA(11, vencimiento0);

		Date vencimiento1 = service.getVencimientoOriginalAFIP("XXX3",
				periodo.getTime());
		verificarDiaIgualA(11, vencimiento1);
	}

	@SuppressWarnings("static-access")
	public void testDigitoFinal4y5_VencimientoAFIPDigito0y1CaeSabadoFeriadoLunes() {
		AfipServiceUtil service = getCalculadorIntereses();

		Calendar periodo = getPeriodoJunio();
		Date vencimiento0 = service.getVencimientoOriginalAFIP("XXX4",
				periodo.getTime());
		verificarDiaIgualA(12, vencimiento0);

		Date vencimiento1 = service.getVencimientoOriginalAFIP("XXX5",
				periodo.getTime());
		verificarDiaIgualA(12, vencimiento1);
	}

	@SuppressWarnings("static-access")
	public void testDigitoFinal6y7_VencimientoAFIPDigito0y1CaeSabadoFeriadoLunes() {
		AfipServiceUtil service = getCalculadorIntereses();

		Calendar periodo = getPeriodoJunio();
		Date vencimiento0 = service.getVencimientoOriginalAFIP("XXX6",
				periodo.getTime());
		verificarDiaIgualA(13, vencimiento0);

		Date vencimiento1 = service.getVencimientoOriginalAFIP("XXX7",
				periodo.getTime());
		verificarDiaIgualA(13, vencimiento1);
	}

	@SuppressWarnings("static-access")
	public void testDigitoFinal8y9_VencimientoAFIPDigito0y1CaeSabadoFeriadoLunes() {
		AfipServiceUtil service = getCalculadorIntereses();

		Calendar periodo = getPeriodoJunio();
		Date vencimiento0 = service.getVencimientoOriginalAFIP("XXX8",
				periodo.getTime());
		verificarDiaIgualA(16, vencimiento0);

		Date vencimiento1 = service.getVencimientoOriginalAFIP("XXX9",
				periodo.getTime());
		verificarDiaIgualA(16, vencimiento1);
	}

	@SuppressWarnings("static-access")
	public void testTodosLosDigitosFinales_VencimientoAFIPDigito0y1CaeMiercoles() {
		AfipServiceUtil service = getCalculadorIntereses();

		Calendar periodo = getPeriodoJulio();
		Date vencimiento0 = service.getVencimientoOriginalAFIP("XXX0",
				periodo.getTime());
		verificarDiaIgualA(7, vencimiento0);

		Date vencimiento1 = service.getVencimientoOriginalAFIP("XXX2",
				periodo.getTime());
		verificarDiaIgualA(8, vencimiento1);

		Date vencimiento2 = service.getVencimientoOriginalAFIP("XXX4",
				periodo.getTime());
		verificarDiaIgualA(9, vencimiento2);

		Date vencimiento3 = service.getVencimientoOriginalAFIP("XXX6",
				periodo.getTime());
		verificarDiaIgualA(10, vencimiento3);

		Date vencimiento4 = service.getVencimientoOriginalAFIP("XXX8",
				periodo.getTime());
		verificarDiaIgualA(13, vencimiento4);
	}

	@SuppressWarnings("static-access")
	public void testTodosLosDigitosFinales_VencimientoAFIPDigito0y1CaeLunes_FeriadoElMartes() {
		AfipServiceUtil service = getCalculadorIntereses();

		Calendar periodo = getPeriodoSeptiembre();
		Date vencimiento0 = service.getVencimientoOriginalAFIP("XXX0",
				periodo.getTime());
		verificarDiaIgualA(8, vencimiento0);

		Date vencimiento1 = service.getVencimientoOriginalAFIP("XXX2",
				periodo.getTime());
		verificarDiaIgualA(10, vencimiento1);

		Date vencimiento2 = service.getVencimientoOriginalAFIP("XXX4",
				periodo.getTime());
		verificarDiaIgualA(11, vencimiento2);

		Date vencimiento3 = service.getVencimientoOriginalAFIP("XXX6",
				periodo.getTime());
		verificarDiaIgualA(12, vencimiento3);

		Date vencimiento4 = service.getVencimientoOriginalAFIP("XXX8",
				periodo.getTime());
		verificarDiaIgualA(15, vencimiento4);
	}

	@SuppressWarnings("static-access")
	private AfipServiceUtil getCalculadorIntereses() {
		AfipServiceUtil service = new AfipServiceUtil();
		FeriadosServiceUtil feriadosService = new FeriadoServiceMock();
		service.setFeriadoService(feriadosService);
		return service;
	}

	private void verificarDiaIgualA(int dia, Date vencimiento0) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(vencimiento0);
		assertEquals(dia, aux.get(Calendar.DATE));
	}

	private Calendar getPeriodoJunio() {
		Calendar periodo = Calendar.getInstance();
		periodo.set(Calendar.DATE, 1);
		periodo.set(Calendar.MONTH, Calendar.JUNE);
		periodo.set(Calendar.YEAR, 2012);
		return periodo;
	}

	private Calendar getPeriodoJulio() {
		Calendar periodo = Calendar.getInstance();
		periodo.set(Calendar.DATE, 1);
		periodo.set(Calendar.MONTH, Calendar.JULY);
		periodo.set(Calendar.YEAR, 2012);
		return periodo;
	}

	private Calendar getPeriodoSeptiembre() {
		Calendar periodo = Calendar.getInstance();
		periodo.set(Calendar.DATE, 1);
		periodo.set(Calendar.MONTH, Calendar.SEPTEMBER);
		periodo.set(Calendar.YEAR, 2012);
		return periodo;
	}

	private static class FeriadoServiceMock extends FeriadosServiceUtil {
		public Calendar obtenerSiguienteDiaHabil(Calendar dia) {
			Calendar aux = Calendar.getInstance();
			aux.setTime(dia.getTime());
			aux.set(Calendar.MILLISECOND, 0);
			aux.set(Calendar.SECOND, 0);
			aux.set(Calendar.MINUTE, 0);
			aux.set(Calendar.HOUR, 0);
			List<Feriado> feriados = getFeriados();
			while (esFeriadoOFinde(aux, feriados)) {
				aux.add(Calendar.DATE, 1);
			}
			return aux;
		}

		private ArrayList<Feriado> getFeriados() {
			ArrayList<Feriado> feriados = new ArrayList<Feriado>();

			Calendar feriado1 = Calendar.getInstance();
			feriado1.set(Calendar.DATE, 9);
			feriado1.set(Calendar.MONTH, Calendar.JULY);
			feriado1.set(Calendar.YEAR, 2012);
			feriados.add(new Feriado(feriado1.getTime()));

			Calendar feriado2 = Calendar.getInstance();
			feriado2.set(Calendar.DATE, 9);
			feriado2.set(Calendar.MONTH, Calendar.OCTOBER);
			feriado2.set(Calendar.YEAR, 2012);
			feriados.add(new Feriado(feriado2.getTime()));
			return feriados;
		}

	}
}

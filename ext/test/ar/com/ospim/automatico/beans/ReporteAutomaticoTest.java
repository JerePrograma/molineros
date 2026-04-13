package ar.com.ospim.automatico.beans;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import ar.com.ospim.util.DateUtils;

public class ReporteAutomaticoTest extends TestCase {
	public void testNoCorrerPorHorario() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, true, false, 0,
				0, null, null);
		Calendar calendar = getFecha("19/10/2011 12:00:00");
		assertFalse(ra.ejecutar(calendar));
	}

	public void testSiCorrerPorHorario() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, true, false, 0,
				0, null, null);
		Calendar calendar = getFecha("19/10/2011 15:20:00");
		assertTrue(ra.ejecutar(calendar));
	}

	public void testNoCorrerPorDiaSemana() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, false, false,
				Calendar.MONDAY, 0, null, null);
		Calendar calendar = getFecha("19/10/2011 15:20:00");
		assertFalse(ra.ejecutar(calendar));
	}

	public void testSiCorrerPorDiaSemana() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, false, false,
				Calendar.MONDAY, 0, null, null);
		Calendar calendar = getFecha("17/10/2011 15:20:00");
		assertTrue(ra.ejecutar(calendar));
	}

	public void testNoCorrerPorFinde() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, true, false, 0,
				0, null, null);
		Calendar calendar = getFecha("16/10/2011 15:20:00");
		assertFalse(ra.ejecutar(calendar));
	}

	public void testSiCorrerPorFinde() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, true, true, 0,
				0, null, null);
		Calendar calendar = getFecha("16/10/2011 15:20:00");
		assertTrue(ra.ejecutar(calendar));
	}

	public void testCorrerFindeAPesarDeBoolean() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, false, false,
				Calendar.SUNDAY, 0, null, null);
		Calendar calendar = getFecha("16/10/2011 15:20:00");
		assertTrue(ra.ejecutar(calendar));
	}

	public void testNoCorrerPorUltimaEjecucionReciente() throws ParseException {
		Calendar ultima = getFecha("19/10/2011 15:20:00");
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, true, true, 0,
				0, null, ultima.getTime());
		Calendar calendar = getFecha("19/10/2011 15:18:00");
		assertFalse(ra.ejecutar(calendar));
	}

	public void testCorrerPorUltimaEjecucionNoReciente() throws ParseException {
		Calendar ultima = getFecha("19/10/2011 14:20:00");
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, true, true, 0,
				0, null, ultima.getTime());
		Calendar calendar = getFecha("19/10/2011 15:18:00");
		assertTrue(ra.ejecutar(calendar));
	}

	public void testSiCorrerMensual() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, false, false, 0,
				19, null, null);
		Calendar calendar = getFecha("19/10/2011 15:18:00");
		assertTrue(ra.ejecutar(calendar));
	}

	public void testNoCorrerMensual() throws ParseException {
		ReporteAutomaticoAdapter ra = getReporteAutomatico(15, false, false, 0,
				20, null, null);
		Calendar calendar = getFecha("19/10/2011 15:18:00");
		assertFalse(ra.ejecutar(calendar));
	}

	public void testSiCorrerFechaUnizaVez() throws ParseException {
		Calendar unica = getFecha("01/11/2032 14:20:00");
		ReporteAutomaticoAdapter ra = getReporteAutomatico(14, false, false, 0,
				0, unica.getTime(), null);
		Calendar calendar = getFecha("01/11/2032 14:20:00");
		assertTrue(ra.ejecutar(calendar));
	}

	public void testNoCorrerFechaUnizaVez() throws ParseException {
		Calendar unica = getFecha("01/11/2032 14:20:00");
		ReporteAutomaticoAdapter ra = getReporteAutomatico(14, false, false, 0,
				0, unica.getTime(), null);
		Calendar calendar = getFecha("01/11/2052 14:20:00");
		assertFalse(ra.ejecutar(calendar));
	}

	private Calendar getFecha(String fecha) throws ParseException {
		Date parse = DateUtils.parse(fecha, DateUtils.LONG_SEC);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse);
		return calendar;
	}

	private ReporteAutomaticoAdapter getReporteAutomatico(int hora,
			boolean diario, boolean correrFinde, int dia, int diaDelMes,
			Date fechaUnicaVez, Date ultimaEjecucion) {
		ReporteAutomaticoAdapter ra = new ReporteAutomaticoAdapter();
		ra.setHora(hora);
		ra.setDiario(diario);
		ra.setIncluirFinDeSemana(correrFinde);
		ra.setDiaDeLaSemana(dia);
		ra.setDiaDelMes(diaDelMes);
		ra.setFechaUnicaVez(fechaUnicaVez);
		ra.setUltimaEjecucion(ultimaEjecucion);
		return ra;
	}

	private static class ReporteAutomaticoAdapter extends ReporteAutomatico {
		public boolean ejecutar(Calendar calendar) {
			return super.ejecutar(calendar);
		}
	}
}

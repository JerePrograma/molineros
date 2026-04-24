package ar.com.ospim.util;

/*

 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ar.com.ospim.afip.service.FeriadosServiceImpl;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Feriado;

import com.liferay.portal.kernel.util.ParamUtil;

public class DateUtils {
	public static final int ENERO = 1;
	public static final int FEBRERO = 2;
	public static final int MARZO = 3;
	public static final int ABRIL = 4;
	public static final int MAYO = 5;
	public static final int JUNIO = 6;
	public static final int JULIO = 7;
	public static final int AGOSTO = 8;
	public static final int SEPTIEMBRE = 9;
	public static final int OCTUBRE = 10;
	public static final int NOVIEMBRE = 11;
	public static final int DICIEMBRE = 12;

	public static final String LONG = "dd/MM/yyyy HH:mm";

	public static final String LONG_SEC = "dd/MM/yyyy HH:mm:ss";

	public static final String LONG_MILI_SEC = "dd/MM/yyyy HH:mm:ss.S";

	public static final String SHORT = "dd/MM/yyyy";
	
	public static final String SHORT_MID = "dd-MM-yyyy";


	public static final String PERIODO = "MM/yyyy";

	public static final String CRYPTO = "ddMMyyyyHHmm";

	public static final String LDAP = "yyyyMMddHHmmZ";

	public static final String ORACLE = "dd/MM/yy";

	public static final String SAMCA = "dd/MM/yy HH:mm";
	public static final String SAMCA_S = "dd/MM/yy HH:mm:ss";

	public static final String FECHA4ORDENAR = "yyyy/MM/dd HH:mm";

	public static final long MILISEGUNDOS_DIA = 24 * 60 * 60 * 1000;
	public static final long MILISEGUNDOS_HORA = 1 * 60 * 60 * 1000;

	public static String format(Date date, String patron) {
		if (date == null)
			return "";
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(patron);
			return formatter.format(date);
		}
	}

	public static String format(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat();
		return formatter.format(date);
	}

	public static Date parse(String date, String patron) throws ParseException {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(patron);
		return formatter.parse(date);
	}

	public static Date parse(String date) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat();
		return formatter.parse(date);
	}

	/**
	 * Devuelve un String a partir de la fecha y formato pasados
	 * 
	 * @param fecha
	 * @param pattern
	 * @return
	 */
	public static String getDateString(Date fecha, String pattern) {
		if (!("".equals(fecha))) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			try {
				String date = simpleDateFormat.format(fecha);
				return date;
			} catch (Exception ex) {
			}
		}
		return null;
	}

	/*
	 * Anyade un dia a la fecha pasada como parametro
	 */
	public static Date anyadeDias(String fecha, int ndias) {
		Calendar calendario = Calendar.getInstance();
		calendario.set(Calendar.YEAR, Integer.parseInt(fecha.substring(6, 10)));
		calendario.set(Calendar.MONTH,
				Integer.parseInt(fecha.substring(3, 5)) - 1);
		calendario.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fecha.substring(
				0, 2)));
		calendario.set(Calendar.HOUR_OF_DAY, 0);
		calendario.set(Calendar.MINUTE, 0);
		calendario.set(Calendar.SECOND, 0);
		calendario.set(Calendar.MILLISECOND, 0);

		calendario.add(Calendar.DAY_OF_MONTH, ndias);

		return calendario.getTime();

	}

	/*
	 * Anyade un dia a la fecha pasada como parametro
	 */
	public static Date anyadeDias(Date fecha, int ndias) {
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(fecha);
		calendario.add(Calendar.DATE, ndias);
		return calendario.getTime();
	}

	/*
	 * Anyade n Meses a la fecha pasada como parametro
	 */
	public static Date anyadeMeses(Date fecha, int nmeses) {
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(fecha);
		calendario.add(Calendar.MONTH, nmeses);
		return calendario.getTime();

	}

	/**
	 * Devuelve una nueva fecha restando la cantidad especificada de dias a la
	 * fecha origen O.Naval
	 * 
	 * @param fOrigen
	 * @param dias
	 * @return
	 */
	public static Date quitaDias(Date fOrigen, int dias) {
		Calendar calend = Calendar.getInstance();
		calend.setTime(fOrigen);
		//calend.roll(Calendar.DAY_OF_YEAR, (dias * (-1)));
		calend.add(Calendar.DAY_OF_YEAR, (dias * (-1)));
		return calend.getTime();

	}

	/**
	 * Devuelve una nueva fecha restando la cantidad especificada de meses a la
	 * fecha origen O.Naval
	 * 
	 * @param fOrigen
	 * @param dias
	 * @return
	 */
	public static Date quitaMeses(Date fOrigen, int meses) {
		Calendar calend = Calendar.getInstance();
		// calend.setTime(fOrigen);
		// calend.roll(Calendar.MONTH,(meses*(-1)));
		// if (fOrigen.getMonth()<=2) calend.roll(Calendar.YEAR,(1*(-1)));
		return calend.getTime();

	}

	/**
	 * Data una fecha y hora en formato "dd-MM-yyyy hh:mm" devuelve un date
	 * 
	 * @param fechaHora
	 * @return
	 */
	public static Date fechaHoraCompleta(String fecha, String hora) {
		Calendar calendario = Calendar.getInstance();
		calendario.set(Calendar.YEAR, Integer.parseInt(fecha.trim().substring(
				6, 10)));
		calendario.set(Calendar.MONTH, Integer.parseInt(fecha.trim().substring(
				3, 5)) - 1);
		calendario.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fecha.trim()
				.substring(0, 2)));
		calendario.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora.trim()
				.substring(0, 2)));
		calendario.set(Calendar.MINUTE, Integer.parseInt(hora.trim().substring(
				3, 5)));
		calendario.set(Calendar.SECOND, Integer.parseInt(hora.trim().substring(
				6, 8)));

		// new Date(calendario.getTimeInMillis());
		return calendario.getTime();
	}

	/**
	 * Devuelve true si las dos fechas de entrada pertenecen al mismo mes.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isMismoMes(Date date1, Date date2) {
		int a1, a2;
		int m1, m2;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		m1 = cal.get(Calendar.MONTH);
		a1 = cal.get(Calendar.YEAR);
		cal.setTime(date2);
		m2 = cal.get(Calendar.MONTH);
		a2 = cal.get(Calendar.YEAR);
		boolean ret = true;
		if (a1 != a2) {
			ret = false;
		}
		if (m1 != m2) {
			ret = false;
		}
		return ret;

	}

	/**
	 * Devuelve true si las dos fechas indicadas como parï¿½metros de entrada
	 * pertenecen al mismo dï¿½a del aï¿½o
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isMismoDia(Date date1, Date date2) {
		int a1, a2;
		int m1, m2;
		int d1, d2;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		m1 = cal.get(Calendar.MONTH);
		a1 = cal.get(Calendar.YEAR);
		d1 = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(date2);
		m2 = cal.get(Calendar.MONTH);
		a2 = cal.get(Calendar.YEAR);
		d2 = cal.get(Calendar.DAY_OF_MONTH);
		boolean ret = true;
		if (a1 != a2) {
			ret = false;
		}
		if (m1 != m2) {
			ret = false;
		}
		if (d1 != d2) {
			ret = false;
		}
		return ret;
	}

	private static final String[] todosLosMeses = { "ENE", "FEB", "MAR", "ABR",
			"MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC" };
	// para que no tome en cuenta el DST
	public static final String TIME_ZONE_AR = "Etc/GMT+3";

	public static String[] obtenerSeisMesesSiguientes(Date fecha) {
		String[] meses = new String[6];
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);

		int mesInicio = c.get(Calendar.MONTH);

		for (int i = 1; i <= 6; i++) {
			meses[i - 1] = todosLosMeses[(mesInicio + i) % 12];
		}

		return meses;
	}

	public static int obtenerMesActual() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return (cal.get(Calendar.MONTH) + 1);
	}

	public static int obtenerAnyoActual() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return cal.get(Calendar.YEAR);
	}

	public static String getHora(Date fecha) {

		if (fecha != null) {

			String fechaS = format(fecha, SAMCA);
			return fechaS.substring(9, 11);
		}
		return "00";
	}

	public static String getMinuto(Date fecha) {

		if (fecha != null) {
			String fechaS = format(fecha, SAMCA);
			return fechaS.substring(12, 14);
		}
		return "00";
	}

	public static int getYear(Date d) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);
		return gc.get(GregorianCalendar.YEAR);
	}

	public static String componerFechaSAMCA(String fechaIniCarga,
			String horaIniCarga, String minIniCarga) {
		return fechaIniCarga.concat(" " + horaIniCarga + ":" + minIniCarga);
	}

	public static Date getDiaAntes_18_00(Date inicial) {
		Calendar c = Calendar.getInstance();
		c.setTime(inicial);
		c.set(Calendar.HOUR_OF_DAY, 18);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date d = new Date(c.getTimeInMillis() - MILISEGUNDOS_DIA);
		return d;
	}

	public static int getEdad(Date birthDate) {
//		long elapsed = (Calendar.getInstance().getTimeInMillis() - birthday
//				.getTime()) / 86400000;
//		// elapsed is in milliseconds so we have to convert to year
//		int years = Math.round(elapsed / 365); // leap years make it
//		return years;
		Calendar birth = new GregorianCalendar();
		Calendar today = new GregorianCalendar();
		int age=0;
		int factor=0;
		Date currentDate=new Date(); //current date
		birth.setTime(birthDate);
		today.setTime(currentDate);
		if (today.get(Calendar.MONTH) <= birth.get(Calendar.MONTH)) {
			if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH)) {
				if (today.get(Calendar.DATE) > birth.get(Calendar.DATE)) {
					factor = -1; //Aun no celebra su cumpleaños
				}
			} else {
				factor = -1; //Aun no celebra su cumpleaños
			}
		}
		age=(today.get(Calendar.YEAR)-birth.get(Calendar.YEAR))+factor;
		
		return age;
	}

	public static boolean esMayor(Date first, Date second) {
		// Devuelve true si la primer fecha es mayor que la segunda
		boolean compare = first.after(second);
		return compare;
	}

	public static int compararFechasTruncarEnDia(Date first, Date second) {
		Calendar firstCal = getFechaTruncadaEnDia(first);
		Calendar secondCal = getFechaTruncadaEnDia(second);
		return firstCal.compareTo(secondCal);
	}

	public static Date getMismoDia_00_00hs(Date inicial) {
		Calendar c = Calendar.getInstance();
		c.setTime(inicial);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date d = new Date(c.getTimeInMillis());
		return d;
	}

	public static Date getMismoDia_23_59hs(Date inicial) {
		Calendar c = Calendar.getInstance();
		c.setTime(inicial);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 59);
		Date d = new Date(c.getTimeInMillis());
		return d;
	}

	public Date updateTime(Date date, Date timeHolder) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		int hours = Integer.parseInt(new SimpleDateFormat("h")
				.format(timeHolder));
		int minutes = Integer.parseInt(new SimpleDateFormat("M")
				.format(timeHolder));
		cal.set(Calendar.HOUR, hours);
		cal.set(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	public Date updateDate(Date date, Date dateHolder) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		int month = Integer.parseInt(new SimpleDateFormat("m")
				.format(dateHolder));
		int day = Integer
				.parseInt(new SimpleDateFormat("d").format(dateHolder));
		int year = Integer.parseInt(new SimpleDateFormat("yyyy")
				.format(dateHolder));
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}

	public static Date getDatetimeFromString(String format,
			String datetimeAsString) {
		try {
			DateFormat formatter = new SimpleDateFormat(format);
			return formatter.parse(datetimeAsString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date getDateFromString(String datetimeAsString) {
		return getDatetimeFromString("yyyy-MM-dd", datetimeAsString);
	}

	public static Date getTimeFromString(String militaryTimeAsString) {
		return getDatetimeFromString("hhMM", militaryTimeAsString);
	}

	/**
	 * For exmample if you wanted to compute what day of the week is Saturday on
	 * a calendar where monday begins the week, you would pass javaDayValue =
	 * Calendar.SATURDAY and firstDayOfWeek = Calendar.MONDAY.
	 * 
	 * @param javaDayValue
	 * @param firstDayOfWeek
	 * @return converted day
	 */
	public static int dayOfWeekShifter(int javaDayValue, int firstDayOfWeek) {
		return ((javaDayValue - (6 + firstDayOfWeek)) % 7) + 7;
	}

	/**
	 * This is a version of the Calendar add() method for adding or subtracting
	 * days, months, years, etc., only this method passes Date Objects in and
	 * out.
	 * 
	 * @param oldDate
	 * @param calendarField
	 * @param amount
	 * @return Date.
	 */
	public static Date changeDate(Date oldDate, int calendarField, int amount) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(oldDate);
		cal.add(calendarField, amount);
		return cal.getTime();
	}

	public static Date getFirstDateOfMonth(Date date, boolean rollTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		if (rollTime)
			return getDayBegin(calendar.getTime());
		else
			return calendar.getTime();
	}

	public static Date getLastDateOfMonth(Date date, boolean rollTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.DATE, -1);
		if (rollTime)
			return getDayEnd(calendar.getTime());
		else
			return calendar.getTime();
	}

	public static Date getFirstDateOfWeek(Date date, boolean rollTime) {
		return getFirstDateOfWeek(date, Calendar.SUNDAY, rollTime);
	}

	public static Date getFirstDateOfWeek(Date date, int firstDayOfWeek,
			boolean rollTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeekShifter(calendar
				.get(Calendar.DAY_OF_WEEK), firstDayOfWeek) - 1));
		if (rollTime)
			return getDayBegin(calendar.getTime());
		else
			return calendar.getTime();
	}

	public static Date getLastDateOfWeek(Date date, boolean rollTime) {
		return getLastDateOfWeek(date, Calendar.SUNDAY, rollTime);
	}

	public static Date getLastDateOfWeek(Date date, int firstDayOfWeek,
			boolean rollTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		//calendar.add(Calendar.DAY_OF_WEEK, 7 - dayOfWeekShifter(calendar.get(Calendar.DAY_OF_WEEK), firstDayOfWeek) - 1);
		calendar.add(Calendar.DAY_OF_WEEK, 7 - dayOfWeekShifter(calendar.get(Calendar.DAY_OF_WEEK), firstDayOfWeek));
		if (rollTime)
			return getDayEnd(calendar.getTime());
		else
			return calendar.getTime();
	}

	public static Date getFirstDateOfYear(Date date, boolean rollTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DATE, 1);
		if (rollTime)
			return getDayBegin(calendar.getTime());
		else
			return calendar.getTime();
	}

	public static Date getLastDateOfYear(Date date, boolean rollTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, 1);
		calendar.setTime(getFirstDateOfYear(calendar.getTime(), false));
		calendar.add(Calendar.DATE, -1);
		if (rollTime)
			return getDayEnd(calendar.getTime());
		else
			return calendar.getTime();
	}

	public static Date getDayBegin(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getDayEnd(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	public static Date getFollowingMonth(Date pivotDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pivotDate);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}

	public static Date getPreceedingMonth(Date pivotDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pivotDate);
		calendar.add(Calendar.MONTH, -1);
		return calendar.getTime();
	}

	public static Date getPreceedingYear(Date pivotDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pivotDate);
		calendar.add(Calendar.YEAR, -1);
		return calendar.getTime();
	}

	public static Calendar getDesdeEjercicioActual() {
		Calendar desde = Calendar.getInstance();
		int anio = desde.get(Calendar.YEAR);
		if (desde.get(Calendar.MONTH) >= Calendar.JANUARY
				&& desde.get(Calendar.MONTH) <= Calendar.JULY) {
			anio--;
		}
		desde.set(Calendar.DATE, 1);
		desde.set(Calendar.MONTH, Calendar.AUGUST);
		desde.set(Calendar.YEAR, anio);
		desde.set(Calendar.HOUR, 0);
		desde.set(Calendar.MINUTE, 0);
		desde.set(Calendar.SECOND, 0);
		desde.set(Calendar.MILLISECOND, 0);
		return desde;
	}
	
	public static Calendar getDesdeEjercicioActualAmtima() {
		Calendar desde = Calendar.getInstance();
		int anio = desde.get(Calendar.YEAR);
		if (desde.get(Calendar.MONTH) >= Calendar.JANUARY
				&& desde.get(Calendar.MONTH) <= Calendar.JUNE) {
			anio--;
		}
		desde.set(Calendar.DATE, 1);
		desde.set(Calendar.MONTH, Calendar.JULY);
		desde.set(Calendar.YEAR, anio);
		desde.set(Calendar.HOUR, 0);
		desde.set(Calendar.MINUTE, 0);
		desde.set(Calendar.SECOND, 0);
		desde.set(Calendar.MILLISECOND, 0);
		return desde;
	}	
	
	public static Calendar getDesdeEjercicioActualUOMA() {
		Calendar desde = Calendar.getInstance();
		int anio = desde.get(Calendar.YEAR);
		if (desde.get(Calendar.MONTH) >= Calendar.JANUARY
				&& desde.get(Calendar.MONTH) <= Calendar.JULY) {
			anio--;
		}
		desde.set(Calendar.DATE, 1);
		desde.set(Calendar.MONTH, Calendar.AUGUST);
		desde.set(Calendar.YEAR, anio);
		desde.set(Calendar.HOUR, 0);
		desde.set(Calendar.MINUTE, 0);
		desde.set(Calendar.SECOND, 0);
		desde.set(Calendar.MILLISECOND, 0);
		return desde;
	}

	public static Calendar getHastaEjercicioActual() {
		Calendar hastaEjActual = Calendar.getInstance();
		if(hastaEjActual.get(Calendar.MONTH)>Calendar.JULY){
			hastaEjActual.add(Calendar.YEAR, 1);
		}
		hastaEjActual.set(Calendar.MONTH, Calendar.JULY);
		hastaEjActual.set(Calendar.DATE, hastaEjActual.getActualMaximum(Calendar.DATE));
		hastaEjActual.set(Calendar.HOUR, 0);
		hastaEjActual.set(Calendar.MINUTE, 0);
		hastaEjActual.set(Calendar.SECOND, 0);
		hastaEjActual.set(Calendar.MILLISECOND, 0);
		return hastaEjActual;
	}
	
	public static Calendar getHastaEjercicioActualAmtima() {
		Calendar hastaEjActual = Calendar.getInstance();
		if(hastaEjActual.get(Calendar.MONTH)>Calendar.JUNE){
			hastaEjActual.add(Calendar.YEAR, 1);
		}
		hastaEjActual.set(Calendar.MONTH, Calendar.JUNE);
		hastaEjActual.set(Calendar.DATE, hastaEjActual.getActualMaximum(Calendar.DATE));
		hastaEjActual.set(Calendar.HOUR, 0);
		hastaEjActual.set(Calendar.MINUTE, 0);
		hastaEjActual.set(Calendar.SECOND, 0);
		hastaEjActual.set(Calendar.MILLISECOND, 0);
		return hastaEjActual;
	}
	
	public static Calendar getHastaEjercicioActualUOMA() {
		Calendar hastaEjActual = Calendar.getInstance();		
		if(hastaEjActual.get(Calendar.MONTH)>Calendar.JULY){
			hastaEjActual.add(Calendar.YEAR, 1);
		}
		hastaEjActual.set(Calendar.MONTH, Calendar.JULY);		
		hastaEjActual.set(Calendar.DATE, hastaEjActual.getActualMaximum(Calendar.DATE));		
		hastaEjActual.set(Calendar.HOUR, 0);
		hastaEjActual.set(Calendar.MINUTE, 0);
		hastaEjActual.set(Calendar.SECOND, 0);
		hastaEjActual.set(Calendar.MILLISECOND, 0);
		return hastaEjActual;
	}

	public static Calendar getInfinito() {
		Calendar hasta = Calendar.getInstance();
		hasta.set(Calendar.DATE, 1);
		hasta.set(Calendar.MONTH, Calendar.JANUARY);
		hasta.set(Calendar.YEAR, 2999);
		return hasta;
	}

	public static Calendar getHastaInfinito() {
		return getInfinito();
	}

	public static Calendar getDesdeInfinito() {
		Calendar hasta = Calendar.getInstance();
		hasta.set(Calendar.DATE, 1);
		hasta.set(Calendar.MONTH, Calendar.JANUARY);
		hasta.set(Calendar.YEAR, 1800);
		return hasta;
	}

	public static Calendar getFechaTruncadaEnDia(Date fecha) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(fecha);
		Calendar fechaExacta = Calendar.getInstance();
		fechaExacta.set(Calendar.MONTH, aux.get(Calendar.MONTH));
		fechaExacta.set(Calendar.YEAR, aux.get(Calendar.YEAR));
		fechaExacta.set(Calendar.DATE, aux.get(Calendar.DATE));
		fechaExacta.set(Calendar.MILLISECOND, 0);
		fechaExacta.set(Calendar.SECOND, 0);
		fechaExacta.set(Calendar.HOUR, 0);
		fechaExacta.set(Calendar.MINUTE, 0);
		return fechaExacta;
	}

	public static Calendar getHastaEjercicio(RenderRequest renderRequest, int entidad) {
		String ejercicio1 = renderRequest.getParameter("ejercicio");
		return getHastaEjercicio(ejercicio1, entidad);
	}

	public static Calendar getDesdeEjercicio(RenderRequest renderRequest, int entidad) {
		String ejercicio = renderRequest.getParameter("ejercicio");
		return getDesdeEjercicio(ejercicio, entidad);
	}

	public static Calendar getHastaEjercicio(HttpServletRequest renderRequest, int entidad) {
		String ejercicio1 = renderRequest.getParameter("ejercicio");
		return getHastaEjercicio(ejercicio1, entidad);
	}

	public static Calendar getDesdeEjercicio(HttpServletRequest renderRequest, int entidad) {
		String ejercicio = renderRequest.getParameter("ejercicio");
		return getDesdeEjercicio(ejercicio, entidad);
	}

	private static Calendar getDesdeEjercicio(String ejercicio, int entidad) {
		Calendar desdeEjercicio =null;
		if(entidad==WebKeysGlobal.OSPIM){
			desdeEjercicio = DateUtils.getDesdeEjercicioActual();
		}else if(entidad==WebKeysGlobal.AMTIMA){
			desdeEjercicio = DateUtils.getDesdeEjercicioActualAmtima();
		}else if(entidad==WebKeysGlobal.UOMA){
			desdeEjercicio = DateUtils.getDesdeEjercicioActualUOMA();
		}
		if (StringUtils.isNotBlank(ejercicio)) {
			String dd = ejercicio.split("-")[0];
			desdeEjercicio.set(Calendar.YEAR, Integer.valueOf(dd));
		}
		return desdeEjercicio;
	}

	private static Calendar getHastaEjercicio(String ejercicio1, int entidad) {
		Calendar hastaEjercicio = null;
		if(entidad==WebKeysGlobal.OSPIM){
			hastaEjercicio = DateUtils.getHastaEjercicioActual();
		}else if(entidad==WebKeysGlobal.AMTIMA){
			hastaEjercicio = DateUtils.getHastaEjercicioActualAmtima();
		}else if(entidad==WebKeysGlobal.UOMA){
			hastaEjercicio = DateUtils.getHastaEjercicioActualUOMA();
		}
		if (StringUtils.isNotBlank(ejercicio1)) {
			String hta = ejercicio1.split("-")[1];
			hastaEjercicio.set(Calendar.YEAR, Integer.valueOf(hta));
		}
		return hastaEjercicio;
	}

	public static Calendar getHastaPeriodo(HttpServletRequest req, int entidad) {
		Calendar desdeC = DateUtils.getDesdeEjercicio(req, entidad);
		Calendar hastaC = DateUtils.getHastaEjercicio(req, entidad);

		String periodo = req.getParameter("periodo_hasta");
		if (StringUtils.isNotBlank(periodo) && !periodo.equals("-1")) {
			int periodoInt = Integer.parseInt(periodo);
			periodoInt--;
			desdeC.set(Calendar.MONTH, periodoInt);

			// debo setear el dia maximo para el mes elegido
			hastaC.set(Calendar.DATE, 1);
			hastaC.set(Calendar.MONTH, periodoInt);
			hastaC.set(Calendar.DATE, hastaC.getActualMaximum(Calendar.DATE));

			/*
			if (periodoInt > Calendar.JULY) {
				hastaC.set(Calendar.YEAR, desdeC.get(Calendar.YEAR));
			}
			*/
			
			// Nuevo
			if(entidad == WebKeysGlobal.AMTIMA){
				if (periodoInt > Calendar.JUNE) {
					hastaC.set(Calendar.YEAR, desdeC.get(Calendar.YEAR));
				}
			}else{
				if (periodoInt > Calendar.JULY) {
					hastaC.set(Calendar.YEAR, desdeC.get(Calendar.YEAR));
				}
			}
			
			// Fin Nuevo
			
		}
		return hastaC;
	}

	public static Calendar getDesdePeriodo(HttpServletRequest req, int entidad) {
		Calendar desdeC = DateUtils.getDesdeEjercicio(req, entidad);
		Calendar hastaC = DateUtils.getHastaEjercicio(req, entidad);

		String periodo = req.getParameter("periodo_desde");
		if (StringUtils.isNotBlank(periodo) && !periodo.equals("-1")) {
			int periodoInt = Integer.parseInt(periodo);
			periodoInt--;
			desdeC.set(Calendar.MONTH, periodoInt);

			// debo setear el dia maximo para el mes elegido
			hastaC.set(Calendar.DATE, 1);
			hastaC.set(Calendar.MONTH, periodoInt);
			hastaC.set(Calendar.DATE, hastaC.getActualMaximum(Calendar.DATE));

			if(entidad == WebKeysGlobal.AMTIMA){
				if (periodoInt <= Calendar.JUNE) {
					desdeC.set(Calendar.YEAR, hastaC.get(Calendar.YEAR));
				}
			}else{
				if (periodoInt <= Calendar.JULY) {
					desdeC.set(Calendar.YEAR, hastaC.get(Calendar.YEAR));
				}
			}
			
		}
		return desdeC;
	}

	public static Date getFechaDesde(HttpServletRequest req) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
			fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
			String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");
			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			return fechaIni;
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getFechaHasta(HttpServletRequest req) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
			fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
			String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			return fechaFin;
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getFechaDesde(RenderRequest req) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaDesdeDia = ParamUtil.getString(req, "fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(req, "fechaDesdeMes");
			fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
			String fechaDesdeAnio = ParamUtil.getString(req, "fechaDesdeAnio");

			Date fechaIni = format.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
			return fechaIni;
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getFechaHasta(RenderRequest req) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			String fechaHastaDia = ParamUtil.getString(req, "fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(req, "fechaHastaMes");
			fechaHastaMes = String.valueOf(Integer.valueOf(fechaHastaMes) + 1);
			String fechaHastaAnio = ParamUtil.getString(req, "fechaHastaAnio");
			Date fechaFin = format.parse(fechaHastaDia + "-" + fechaHastaMes
					+ "-" + fechaHastaAnio);
			return fechaFin;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean esFeriadoOFinde(Date fecha, List<Feriado> feriados) {
		Calendar dia = Calendar.getInstance();
		dia.setTime(fecha);
		if (dia.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			return true;
		} else if (dia.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return true;
		}

		for (Feriado f : feriados) {
			Calendar aux = Calendar.getInstance();
			aux.setTime(f.getFecha());
			if (aux.get(Calendar.DATE) == dia.get(Calendar.DATE)
					&& aux.get(Calendar.MONTH) == dia.get(Calendar.MONTH)
					&& aux.get(Calendar.YEAR) == dia.get(Calendar.YEAR)) {
				return true;
			}
		}
		return false;
	}

	public static String esFeriadoOFindeSemana(Date fecha, List<Feriado> feriados) {
		
		Calendar dia = Calendar.getInstance();
		dia.setTime(fecha);
		if (dia.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			return "Sabado";
		} else if (dia.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return "Domingo";
		}

		for (Feriado f : feriados) {
			Calendar aux = Calendar.getInstance();
			aux.setTime(f.getFecha());
			if (aux.get(Calendar.DATE) == dia.get(Calendar.DATE)
					&& aux.get(Calendar.MONTH) == dia.get(Calendar.MONTH)
					&& aux.get(Calendar.YEAR) == dia.get(Calendar.YEAR)) {
				return "Feriado";
			}
		}
		return "";
	}
	/*
	 * Metodo que calcula la diferencia de las horas que han pasado entre dos
	 * fechas en java
	 */
	public static long diferenciaHorasDias(Calendar fechaInicial,
			Calendar fechaFinal) {
		// Milisegundos al dï¿½a
		long diferenciaHoras = 0;
		// Restamos a la fecha final la fecha inicial y lo dividimos entre el
		// numero de milisegundos al dia
		diferenciaHoras = (fechaFinal.getTimeInMillis() - fechaInicial
				.getTimeInMillis())
				/ MILISEGUNDOS_DIA;
		if (diferenciaHoras > 0) { // Lo Multiplicaos por 24 por que estamos
			// utilizando el formato militar
			diferenciaHoras *= 24;
		}
		return diferenciaHoras;
	}

	/* Metodo que calcula la diferencia de los minutos entre dos fechas */
	public static long diferenciaMinutos(Calendar fechaInicial,
			Calendar fechaFinal) {
		long diferenciaHoras = 0;
		diferenciaHoras = (fechaFinal.get(Calendar.MINUTE) - fechaInicial
				.get(Calendar.MINUTE));
		return diferenciaHoras;
	}

	public static int diferenciaDias(Calendar fechaInicial, Calendar fechaFinal){

		int result = -9999;
		
        // conseguir la representacion de la fecha en milisegundos
        long milis1 = fechaInicial.getTimeInMillis();
        long milis2 = fechaFinal.getTimeInMillis();

        // calcular la diferencia en milisengundos
        long diff = milis2 - milis1;

//        // calcular la diferencia en segundos
//        long diffSeconds = diff / 1000;
//
//        // calcular la diferencia en minutos
//        long diffMinutes = diff / (60 * 1000);
//
//        // calcular la diferencia en horas
//        long diffHours = diff / (60 * 60 * 1000);

        // calcular la diferencia en dias
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        try{
        	result = Integer.parseInt(String.valueOf(diffDays));
        }catch (Exception e) {
        	//no se q hacer... analizar el -9999 ?
		}
        
        return result;
	}
	/*
	 * Metodo que devuelve el Numero total de minutos que hay entre las dos
	 * Fechas
	 */
	public static long cantidadTotalMinutos(Calendar fechaInicial,
			Calendar fechaFinal) {
		long totalMinutos = 0;
		totalMinutos = ((fechaFinal.getTimeInMillis() - fechaInicial
				.getTimeInMillis()) / 1000 / 60);
		return totalMinutos;
	}

	/* Metodo que devuelve el Numero total de horas que hay entre las dos Fechas */

	public static long cantidadTotalHoras(Calendar fechaInicial,
			Calendar fechaFinal) {
		long totalMinutos = 0;
		totalMinutos = ((fechaFinal.getTimeInMillis() - fechaInicial
				.getTimeInMillis()) / 1000 / 60 / 60);
		return totalMinutos;
	}

	/*
	 * Metodo que devuelve el Numero total de Segundos que hay entre las dos
	 * Fechas
	 */
	public static long cantidadTotalSegundos(Calendar fechaInicial,
			Calendar fechaFinal) {
		long totalMinutos = 0;
		totalMinutos = ((fechaFinal.getTimeInMillis() - fechaInicial
				.getTimeInMillis()) / 1000);
		return totalMinutos;
	}

	/* Metodo que calcula la diferencia de las horas entre dos fechas */
	public static long diferenciaHoras(Calendar fechaInicial,
			Calendar fechaFinal) {
		long diferenciaHoras = 0;
		diferenciaHoras = (fechaFinal.get(Calendar.HOUR_OF_DAY) - fechaInicial
				.get(Calendar.HOUR_OF_DAY));
		return diferenciaHoras;
	}

	public static String convertMSInvertido(long ms) {
		long seconds = (long) ((ms / 1000) % 60);
		long minutes = (long) (((ms / 1000) / 60) % 60);
		long hours = (long) (((ms / 1000) / 60) / 60);

		String sec, min, hrs;
		if (Math.abs(seconds) < 10)
			sec = "0" + Math.abs(seconds);
		else
			sec = "" + Math.abs(seconds);
		if (Math.abs(minutes) < 10)
			min = "0" + Math.abs(minutes);
		else
			min = "" + Math.abs(minutes);
		if (Math.abs(hours) < 10)
			hrs = "0" + Math.abs(hours);
		else
			hrs = "" + Math.abs(hours);
		if (hours == 0 && minutes == 0)
			return "";
		else if (hours == 0 && minutes > 0)
			return "-00:" + min;
		else if (hours == 0 && minutes < 0)
			return "00:" + min;
		else if (hours < 0 && minutes < 0) 
			return hrs + ":" + min;
		else if (hours < 0 && minutes == 0) 
			return hrs + ":" + min;
		else if (hours < 0 && minutes > 0) 
			return hrs + ":" + min;		
		else if (hours > 0 && minutes > 0)
			return "-" + hrs + ":" + min;
		else if (hours > 0 && minutes < 0)
			return "-" + hrs + ":" + min;
		else if (hours > 0 && minutes == 0)
			return "-" + hrs + ":" + min;
		return "";
	}
	
	
	public static String convertMS(long ms) {
		long seconds = (long) ((ms / 1000) % 60);
		long minutes = (long) (((ms / 1000) / 60) % 60);
		long hours = (long) (((ms / 1000) / 60) / 60);

		String sec, min, hrs;
		if (Math.abs(seconds) < 10)
			sec = "0" + Math.abs(seconds);
		else
			sec = "" + Math.abs(seconds);
		if (Math.abs(minutes) < 10)
			min = "0" + Math.abs(minutes);
		else
			min = "" + Math.abs(minutes);
		if (Math.abs(hours) < 10)
			hrs = "0" + Math.abs(hours);
		else
			hrs = "" + Math.abs(hours);
		if (hours == 0 && minutes == 0)
			return "";
		else if (hours == 0 && minutes > 0)
			return "00:" + min;
		else if (hours == 0 && minutes < 0)
			return "-00:" + min;
		else if (hours < 0 && minutes < 0) 
			return "-" + hrs + ":" + min;
		else if (hours < 0 && minutes == 0) 
			return "-" + hrs + ":" + min;
		else if (hours < 0 && minutes > 0) 
			return "-" + hrs + ":" + min;		
		else if (hours > 0 && minutes > 0)
			return hrs + ":" + min;
		else if (hours > 0 && minutes < 0)
			return hrs + ":" + min;
		else if (hours > 0 && minutes == 0)
			return hrs + ":" + min;
		return "";
	}
	
	/**
	 * Calcula los dias habiles entre 2 fechas, la fecha1 debe ser anterior a la fecha2
	 * Si no se pasa la lista de feriados, la va a buscar. Esto es asi por si se quiere agregar alguna lista de feriados diferentes
	 * @param fecha1: fecha_certificacion
	 * @param fecha2: fecha_eleccion
	 * @param feriados
	 * @return cantidad de dias habiles entre las fechas
	 */
	public static int calculaDiasHabilesEntreFechas(Date fecha1, Date fecha2, boolean validaFechaFutura, List<Feriado> feriados ){
		
		Logger log = Logger.getLogger(DateUtils.class);
		
//		Calendar fechAux = Calendar.getInstance();
		Calendar fechAux = DateUtils.getCalendarGMTMenos3();
		int cantDiasHabiles = 999;
		
		if(feriados==null || feriados.size()==0){
			feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
		}
		if(validaFechaFutura && (fecha1.after(fechAux.getTime()) || fecha2.after(fechAux.getTime())) ){ 
//		if(fecha1.after(Calendar.getInstance().getTime()) || fecha2.after(Calendar.getInstance().getTime()) ){
			return --cantDiasHabiles; // 998
		}
		
		if(fecha1.after(fecha2)){
			return cantDiasHabiles; // 999
		}
		
		cantDiasHabiles = 0;
		
		if(fecha1.equals(fecha2)){
			return cantDiasHabiles; // 0
		}
		
		fechAux.setTime(fecha1);
		
		while(fechAux.getTime().before(fecha2)){
			if(!esFeriadoOFinde(fechAux.getTime(), feriados)){
				cantDiasHabiles++;
			}
			fechAux.add(Calendar.DATE, 1);
		}
		
		return cantDiasHabiles;
	}
	
	public static String getNombreDiaSemana(Calendar fecha){
		
		String diaSemana = "";
		
		int weekName = fecha.get(Calendar.DAY_OF_WEEK);
		
		switch (weekName) {
		case Calendar.SUNDAY:
			diaSemana = "Domingo";
			break;
		case Calendar.MONDAY:
			diaSemana = "Lunes";
			break;
		case Calendar.TUESDAY:
			diaSemana = "Martes";
			break;
		case Calendar.WEDNESDAY :
			diaSemana = "Miércoles";
			break;
		case Calendar.THURSDAY:
			diaSemana = "Jueves";
			break;
		case Calendar.FRIDAY:
			diaSemana = "Viernes";
			break;
		case Calendar.SATURDAY:
			diaSemana = "Sábado";
			break;
		}
		
		return diaSemana;
	}
	
	public static Calendar getCalendarGMTMenos3(){
		
		TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
		Calendar gmtMenos3 = Calendar.getInstance(); // fecha de hoy
		gmtMenos3.setTimeZone(tz);
		
		return gmtMenos3;
	}
	
	
public static int calculaDiasHabilesEntreFechasHoraCero(Date fecha1Aux, Date fecha2Aux, boolean validaFechaFutura, List<Feriado> feriados ){
		
		Logger log = Logger.getLogger(DateUtils.class);
		
		Calendar fechAux = DateUtils.getCalendarGMTMenos3();
		fechAux.set(Calendar.HOUR_OF_DAY, 0);
		fechAux.set(Calendar.MINUTE, 0);
		fechAux.set(Calendar.SECOND, 0);
		fechAux.set(Calendar.MILLISECOND, 0);
				
		Calendar fecha1 =  DateUtils.getCalendarGMTMenos3() ;
		fecha1.setTime(fecha1Aux);
		fecha1.set(Calendar.HOUR_OF_DAY, 0);
		fecha1.set(Calendar.MINUTE, 0);
		fecha1.set(Calendar.SECOND, 0);
		fecha1.set(Calendar.MILLISECOND, 0);
		
		
		Calendar fecha2 =  DateUtils.getCalendarGMTMenos3() ;
		fecha2.setTime(fecha2Aux);
		fecha2.set(Calendar.HOUR_OF_DAY, 0);
		fecha2.set(Calendar.MINUTE, 0);
		fecha2.set(Calendar.SECOND, 0);
		fecha2.set(Calendar.MILLISECOND, 0);
				
		
		int cantDiasHabiles = 999;
		
		if(feriados==null || feriados.size()==0){
			feriados = FeriadosServiceImpl.getInstance().findAllFeriados();
		}
		if(validaFechaFutura && (fecha1.after(fechAux.getTime()) || fecha2.after(fechAux.getTime())) ){ 
//		if(fecha1.after(Calendar.getInstance().getTime()) || fecha2.after(Calendar.getInstance().getTime()) ){
			return --cantDiasHabiles; // 998
		}
		
		if(fecha1.after(fecha2)){
			return cantDiasHabiles; // 999
		}
		
		cantDiasHabiles = 0;
		
		if(fecha1.equals(fecha2)){
			return cantDiasHabiles; // 0
		}
		
		fechAux.setTime(fecha1.getTime());
		
		while(fechAux.getTime().before(fecha2.getTime())){
			if(!esFeriadoOFinde(fechAux.getTime(), feriados)){
				cantDiasHabiles++;
			}
			fechAux.add(Calendar.DATE, 1);
		}
		
		return cantDiasHabiles;
	}


	public static Calendar toCalendar(Date date){ 
	  Calendar cal = Calendar.getInstance();
	  cal.setTime(date);
	  return cal;
	}
	
}
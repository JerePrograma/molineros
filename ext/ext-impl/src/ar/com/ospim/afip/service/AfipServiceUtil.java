package ar.com.ospim.afip.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.afip.beans.ArchivoSubidoAfip;
import ar.com.ospim.afip.beans.ArchivoSubidoBco;
import ar.com.ospim.afip.beans.ReporteAporteContribucionesEmpresa;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresa;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaConsolidado;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaDet;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaListado;
import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa;
import ar.com.ospim.tesoreria.beans.InteresAfip;
import ar.com.ospim.tesoreria.reportes.ReporteAcreditacionesAFIPExcel.ResumenExtractoBancario;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AfipServiceUtil {
	private static AfipServiceImpl instance = null;
	private static FeriadosServiceUtil feriadoService = null;
	private static Log _log = LogFactoryUtil
			.getLog(AfipServiceUtil.class);

	private static AfipServiceImpl getInstance() {
		if (null == instance) {
			instance = new AfipServiceImpl();
		}
		return instance;
	}
	
	public static BigDecimal getRetencionGanancias(String cuit,BigDecimal importe, Date periodo, int entidad) throws SystemException {
		return getInstance().getRetencionGanancias(cuit, importe, periodo, entidad);
	}
	
	public static List<ArchivoSubidoAfip> getArchivosSubidosAfip()
			throws SystemException {
		return getInstance().getArchivosSubidosAfip();
	}

	public static List<ArchivoSubidoBco> getArchivosSubidosBcoUOMA(Date fechaArchivo)
			throws SystemException {
		return getInstance().getArchivosSubidosBcoUOMA(fechaArchivo);
	}
	
	public static List<ArchivoSubidoBco> getArchivosSubidosBcoAMTIMA(Date fechaArchivo)
			throws SystemException {
		return getInstance().getArchivosSubidosBcoAMTIMA(fechaArchivo);
	}

	public static List<ReporteDeudaEmpresa> getReporteDeudaEmpresaPeriodo(
			Date fechaIni, Date fechaFin, boolean sin_deuda, int ramo_desde, int ramo_hasta)
			throws SystemException {
		return getInstance().getReporteDeudaEmpresaPeriodo(fechaIni, fechaFin,
				sin_deuda, ramo_desde, ramo_hasta);
	}
	
	public static ReporteDeudaEmpresaListado getReporteDeudaEmpresaPeriodoConsolidado(int idRepoAgendado)
			throws SystemException {
		
		ReporteDeudaEmpresaListado listadoResultados = new ReporteDeudaEmpresaListado();
		
		ReporteDeudaEmpresaCab cabecera = new ReporteDeudaEmpresaCab();
		List<ReporteDeudaEmpresaDet> detalles = new ArrayList<ReporteDeudaEmpresaDet>();
		List<ReporteDeudaEmpresaConsolidado> consolidados = new ArrayList<ReporteDeudaEmpresaConsolidado>();
		
		cabecera = getInstance().getReporteDeudaEmpresaPeriodoCabecera(idRepoAgendado);
		detalles = getInstance().getReporteDeudaEmpresaPeriodoDetalle(idRepoAgendado);
		consolidados = getInstance().getReporteDeudaEmpresaPeriodoConsolidado(idRepoAgendado);
		
		Collections.sort(detalles, new Comparator<ReporteDeudaEmpresa>() {
			public int compare(ReporteDeudaEmpresa o1, ReporteDeudaEmpresa o2) {
				if (o1.getPeriodo().equals(o2.getPeriodo())) {
					return o1.getPeriodo().compareTo(o2.getPeriodo());
				} else {
					return o1.getPeriodo().compareTo(o2.getPeriodo());
				}
			}
		});
		
		listadoResultados.setCabecera(cabecera);
		listadoResultados.setDetalle(detalles);
		listadoResultados.setConsolidado(consolidados);
		
		return listadoResultados;
	}
	
	public static List<ReporteDeudaEmpresa> getDeudaEmpresa(String cuit,
			Date desde, Date hasta) throws SystemException {
		return getInstance().getDeudaEmpresa(cuit, desde, hasta);
	}

	public static List<ReporteDeudaNominaEmpresa> getDeudaNominaEmpresa(
			String cuit, Date desde, Date hasta) throws SystemException {
		return getInstance().getDeudaNominaEmpresa(cuit, desde, hasta);
	}

	public static List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresa(
			String cuit, String cuil, Date desde, Date hasta, Date fechaAcreDesde, Date fechaAcreHasta)
			throws SystemException {
		return getInstance().getReporteAportesContribucionEmpresa(cuit, cuil,
				desde, hasta, fechaAcreDesde, fechaAcreHasta);
	}

	public static List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresas(
			Date desde, Date hasta, int ramo, int ramo_hasta, String cuit) throws SystemException {
		return getInstance().getReporteAportesContribucionEmpresas(desde, hasta, ramo,
				ramo_hasta, cuit);
	}
	
	public static List<ReporteAporteContribucionesEmpresa> getReporteMonotributistas(
			Date desde, Date hasta, String cuil) throws SystemException {
		return getInstance().getReporteAportesMonotributistas(desde, hasta, cuil);
	}

	public static List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresaActaConv(
			String cuit, String cuil, Date desde, Date hasta)
			throws SystemException {
		return getInstance().getReporteAportesContribucionEmpresaActaConv(cuit,
				cuil, desde, hasta);
	}

	public static List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresa(
			String cuit, String cuil, Date desde, Date hasta, Connection con,
			CallableStatement stmt) throws SystemException {
		return getInstance().getReporteAportesContribucionEmpresa(cuit, cuil,
				desde, hasta, con, stmt);
	}

	public static BigDecimal calculoInteres(BigDecimal deuda, Date vencimOrig,
			Date fechaObligacion, List<InteresAfip> listaIntereses)
			throws SystemException {
		
//		_log.debug("calculoInteres");
//		_log.debug("deuda: " + deuda);
//		_log.debug("vencimOrig: " + vencimOrig);
//		_log.debug("fechaObligacion: " + fechaObligacion);
		
		Calendar vencimOrigCalendar = Calendar.getInstance();
		vencimOrigCalendar.setTime(vencimOrig);
		BigDecimal interes = new BigDecimal("0");
		if (deuda.setScale(2, RoundingMode.HALF_DOWN).equals(interes)) {
			return interes;
		}
		for (InteresAfip a : listaIntereses) {
			Calendar ini = Calendar.getInstance();
			ini.setTime(a.getIni());

			Calendar fin = Calendar.getInstance();
			if (a.getFin() != null) {
				fin.setTime(a.getFin());
			} else {
				fin.setTime(fechaObligacion);
			}

			if ((fin.after(vencimOrigCalendar) || fin
					.equals(vencimOrigCalendar))
					&& (ini.before(vencimOrigCalendar) || ini
							.equals(vencimOrigCalendar))) {
				
//				_log.debug("fin >= vencimOrigCalendar");

				Calendar inicioIntereses = Calendar.getInstance();
				inicioIntereses.setTime(vencimOrig);
				inicioIntereses.add(Calendar.DATE, 1);
				Date fechaFinInteres = fin.getTime();
				if (fin.getTime().after(fechaObligacion)) {
					fechaFinInteres = fechaObligacion;
				}
				int dias = obtenerDiasParaInteres(inicioIntereses.getTime(),
						fechaFinInteres);
//				_log.debug("dias: " + dias);

				BigDecimal interesPeriodo = deuda.multiply(a.getInteresDiario()
						.multiply(new BigDecimal(dias))
						.divide(new BigDecimal(100)));
				interes = interes.add(interesPeriodo.setScale(2,
						RoundingMode.HALF_DOWN));
			} else if (ini.after(vencimOrigCalendar)
					&& ini.getTime().before(fechaObligacion)) {
				
//				_log.debug("ini > vencimOrigCalendar || ini < fechaObligacion");

				Date fechaFinInteres = fin.getTime();
				if (fin.getTime().after(fechaObligacion)) {
					fechaFinInteres = fechaObligacion;
				}
				int dias = obtenerDiasParaInteres(ini.getTime(),
						fechaFinInteres);
//				_log.debug("dias: " + dias);
				BigDecimal interesPeriodo = deuda.multiply(a.getInteresDiario()
						.multiply(new BigDecimal(dias))
						.divide(new BigDecimal(100)));
				interes = interes.add(interesPeriodo.setScale(2,
						RoundingMode.HALF_DOWN));
			}
		}
		return interes;
	}

	public static List<InteresAfip> getInteresesAfip() throws SystemException {
		return getInstance().getIntereses();
	}

	public static int obtenerDiasParaInteres(Date ini, Date fin) {
		Calendar cIni = Calendar.getInstance();
		Calendar cFin = Calendar.getInstance();
		cIni.setTime(ini);
		cFin.setTime(fin);
		if (cIni.after(cFin)) {
			return 0;
		}
		return obtenerCantidadDiasCalendario(ini, fin) + 1;
	}

	/**
	 * NO ESTA SIENDO UTILIZADO, LO DEJO POR SI CAMBIA LA ESPECIFICACION
	 * 
	 * @param ini
	 * @param fin
	 * @return
	 */
	public static int obtenerDiasAFIPParaInteres(Date ini, Date fin) {
		Calendar cIni = Calendar.getInstance();
		cIni.setTime(ini);
		Calendar cFin = Calendar.getInstance();
		cFin.setTime(fin);

		Calendar cTemp = Calendar.getInstance();
		cTemp.setTime(fin);
		cTemp.add(Calendar.MONTH, -1);
		cTemp.add(Calendar.DATE, 1);
		int diasTot = 0;
		int mesesDif = 0;
		if (cTemp.after(cIni) || cTemp.equals(cIni)) {
			int aniosDif = cFin.get(Calendar.YEAR) - cIni.get(Calendar.YEAR);
			mesesDif = cFin.get(Calendar.MONTH) - cIni.get(Calendar.MONTH);
			int diasDif = cFin.get(Calendar.DATE) - cIni.get(Calendar.DATE);
			if (mesesDif < 0) {
				aniosDif--;
				mesesDif = 12 + mesesDif;
			}
			if (diasDif < -1) {
				mesesDif--;
			}
			mesesDif += aniosDif * 12;
			diasTot = mesesDif * 30;
		} else {
			cTemp.setTime(ini);
			cTemp.add(Calendar.DATE, -1);
			return obtenerCantidadDiasCalendario(cTemp.getTime(), fin);
		}

		cTemp.setTime(ini);
		cTemp.add(Calendar.MONTH, mesesDif);
		cTemp.add(Calendar.DATE, -1);
		diasTot += obtenerCantidadDiasCalendario(cTemp.getTime(), fin);
		return diasTot;
	}

	private static int obtenerCantidadDiasCalendario(Date ini, Date fin) {
		return (int) Math.ceil((fin.getTime() - ini.getTime()) / (1000 * 60 * 60 * 24D));
	}

	public static Map<Date, List<ResumenExtractoBancario>> getResumenOSAportes(
			Date fechaIni, Date fechaFin) throws SQLException {
		return getInstance().getResumenOSAportes(fechaIni, fechaFin);
	}

	public static Map<Date, List<ResumenExtractoBancario>> getResumenSubsidioDesempleo(
			Date fechaIni, Date fechaFin) throws SQLException {
		return getInstance().getResumenSubsidioDesempleo(fechaIni, fechaFin);
	}

	public static Date getVencimientoOriginalAFIP(String cuit, Date periodo) {
		// Calculo los dias de vencimiento, afip dicta que siempre son a partir
		// del 7, por lo tanto a partir del 7 me fijo si cae en finde o feriado
		// y
		// voy corriendo las fechas de acuerdo a esto
		int dia7 = 7;
		Calendar fechaDia7 = obtenerFechaPeriodoMas1(periodo, dia7);
		int sumarDiasAl7 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia7);
		dia7 += sumarDiasAl7;

		int dia8 = dia7 + 1;
		Calendar fechaDia8 = obtenerFechaPeriodoMas1(periodo, dia8);
		int sumarDiasAl8 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia8);
		dia8 += sumarDiasAl8;

		int dia9 = dia8 + 1;
		Calendar fechaDia9 = obtenerFechaPeriodoMas1(periodo, dia9);
		int sumarDiasAl9 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia9);
		dia9 += sumarDiasAl9;

		int dia10 = dia9 + 1;
		Calendar fechaDia10 = obtenerFechaPeriodoMas1(periodo, dia10);
		int sumarDiasAl10 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia10);
		dia10 += sumarDiasAl10;

		int dia11 = dia10 + 1;
		Calendar fechaDia11 = obtenerFechaPeriodoMas1(periodo, dia11);
		int sumarDiasAl11 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia11);
		dia11 += sumarDiasAl11;

		// Ahora segun el digito verificador del cuil/cuit obtengo el
		// vencimiento real
		int dig = Integer.parseInt(cuit.substring(cuit.length() - 1));
		int diaSegunDigito = 0;
		if (dig == 0 || dig == 1) {
			diaSegunDigito = dia7;
		} else if (dig == 2 || dig == 3) {
			diaSegunDigito = dia8;
		} else if (dig == 4 || dig == 5) {
			diaSegunDigito = dia9;
		} else if (dig == 6 || dig == 7) {
			diaSegunDigito = dia10;
		} else {
			diaSegunDigito = dia11;
		}

		return obtenerFechaPeriodoMas1(periodo, diaSegunDigito).getTime();
	}
	
	public static Date getVencimientoOriginalAFIP(String cuit, Date periodo, PortletRequest request) {
		// Calculo los dias de vencimiento, afip dicta que siempre son a partir
		// del 7, por lo tanto a partir del 7 me fijo si cae en finde o feriado
		// y
		// voy corriendo las fechas de acuerdo a esto
		int dia7 = 7;
		Calendar fechaDia7 = obtenerFechaPeriodoMas1(periodo, dia7);
		int sumarDiasAl7 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia7, request);
		dia7 += sumarDiasAl7;

		int dia8 = dia7 + 1;
		Calendar fechaDia8 = obtenerFechaPeriodoMas1(periodo, dia8);
		int sumarDiasAl8 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia8, request);
		dia8 += sumarDiasAl8;

		int dia9 = dia8 + 1;
		Calendar fechaDia9 = obtenerFechaPeriodoMas1(periodo, dia9);
		int sumarDiasAl9 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia9, request);
		dia9 += sumarDiasAl9;

		int dia10 = dia9 + 1;
		Calendar fechaDia10 = obtenerFechaPeriodoMas1(periodo, dia10);
		int sumarDiasAl10 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia10, request);
		dia10 += sumarDiasAl10;

		int dia11 = dia10 + 1;
		Calendar fechaDia11 = obtenerFechaPeriodoMas1(periodo, dia11);
		int sumarDiasAl11 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia11, request);
		dia11 += sumarDiasAl11;

		// Ahora segun el digito verificador del cuil/cuit obtengo el
		// vencimiento real
		int dig = Integer.parseInt(cuit.substring(cuit.length() - 1));
		int diaSegunDigito = 0;
		if (dig == 0 || dig == 1) {
			diaSegunDigito = dia7;
		} else if (dig == 2 || dig == 3) {
			diaSegunDigito = dia8;
		} else if (dig == 4 || dig == 5) {
			diaSegunDigito = dia9;
		} else if (dig == 6 || dig == 7) {
			diaSegunDigito = dia10;
		} else {
			diaSegunDigito = dia11;
		}

		return obtenerFechaPeriodoMas1(periodo, diaSegunDigito).getTime();
	}
	
//	public static Date getVencimientoOriginalAFIP(String cuit, Date periodo, HttpServletRequest request) {
//		// Calculo los dias de vencimiento, afip dicta que siempre son a partir
//		// del 7, por lo tanto a partir del 7 me fijo si cae en finde o feriado
//		// y
//		// voy corriendo las fechas de acuerdo a esto
//		int dia7 = 7;
//		Calendar fechaDia7 = obtenerFechaPeriodoMas1(periodo, dia7);
//		int sumarDiasAl7 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia7, request);
//		dia7 += sumarDiasAl7;
//
//		int dia8 = dia7 + 1;
//		Calendar fechaDia8 = obtenerFechaPeriodoMas1(periodo, dia8);
//		int sumarDiasAl8 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia8, request);
//		dia8 += sumarDiasAl8;
//
//		int dia9 = dia8 + 1;
//		Calendar fechaDia9 = obtenerFechaPeriodoMas1(periodo, dia9);
//		int sumarDiasAl9 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia9, request);
//		dia9 += sumarDiasAl9;
//
//		int dia10 = dia9 + 1;
//		Calendar fechaDia10 = obtenerFechaPeriodoMas1(periodo, dia10);
//		int sumarDiasAl10 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia10, request);
//		dia10 += sumarDiasAl10;
//
//		int dia11 = dia10 + 1;
//		Calendar fechaDia11 = obtenerFechaPeriodoMas1(periodo, dia11);
//		int sumarDiasAl11 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia11, request);
//		dia11 += sumarDiasAl11;
//
//		// Ahora segun el digito verificador del cuil/cuit obtengo el
//		// vencimiento real
//		int dig = Integer.parseInt(cuit.substring(cuit.length() - 1));
//		int diaSegunDigito = 0;
//		if (dig == 0 || dig == 1) {
//			diaSegunDigito = dia7;
//		} else if (dig == 2 || dig == 3) {
//			diaSegunDigito = dia8;
//		} else if (dig == 4 || dig == 5) {
//			diaSegunDigito = dia9;
//		} else if (dig == 6 || dig == 7) {
//			diaSegunDigito = dia10;
//		} else {
//			diaSegunDigito = dia11;
//		}
//
//		return obtenerFechaPeriodoMas1(periodo, diaSegunDigito).getTime();
//	}
	public static Date getVencimientoOriginalAFIP(String cuit, Date periodo, HttpServletRequest request) {
		// Calculo los dias de vencimiento, afip dicta que siempre son a partir
		// del 7, por lo tanto a partir del 7 me fijo si cae en finde o feriado
		// y
		// voy corriendo las fechas de acuerdo a esto
		Calendar corte1 = Calendar.getInstance();
		corte1.set(2017, 11, 31, 0, 0, 0);
		
		int dia7 = 7;
		
		/**
		* http://www.afip.gov.ar/noticias/20180104vencimientoEmpleadores.asp
		*/

		if(periodo.after(corte1.getTime())) {
			dia7 = 9;
		}
		
		Calendar fechaDia7 = obtenerFechaPeriodoMas1(periodo, dia7);
		int sumarDiasAl7 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia7,request);
		dia7 += sumarDiasAl7;

		int dia8 = dia7 + 1;
		Calendar fechaDia8 = obtenerFechaPeriodoMas1(periodo, dia8);
		int sumarDiasAl8 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia8,request);
		dia8 += sumarDiasAl8;

		int dia9 = dia8 + 1;
		Calendar fechaDia9 = obtenerFechaPeriodoMas1(periodo, dia9);
		int sumarDiasAl9 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia9,request);
		dia9 += sumarDiasAl9;

		int dia10 = dia9 + 1;
		Calendar fechaDia10 = obtenerFechaPeriodoMas1(periodo, dia10);
		int sumarDiasAl10 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia10,request);
		dia10 += sumarDiasAl10;

		int dia11 = dia10 + 1;
		Calendar fechaDia11 = obtenerFechaPeriodoMas1(periodo, dia11);
		int sumarDiasAl11 = obtenerCantidadDiasEntreFechaYPrimerDiaHabil(fechaDia11,request);
		dia11 += sumarDiasAl11;

		// Ahora segun el digito verificador del cuil/cuit obtengo el
		// vencimiento real
		int dig = Integer.parseInt(cuit.substring(cuit.length() - 1));
		int diaSegunDigito = 0;
		
//		Cambio que se aplicaba desde 2018. Pero lo metimos en 14/01/2019
		if(periodo.before(corte1.getTime())) {
			if (dig == 0 || dig == 1) {
				diaSegunDigito = dia7;
			} else if (dig == 2 || dig == 3) {
				diaSegunDigito = dia8;
			} else if (dig == 4 || dig == 5) {
				diaSegunDigito = dia9;
			} else if (dig == 6 || dig == 7) {
				diaSegunDigito = dia10;
			} else {
				diaSegunDigito = dia11;
			}
		}else{
			if (dig == 0 || dig == 1 || dig == 2 || dig == 3) {
				diaSegunDigito = dia7;
			} else if (dig == 4 || dig == 5 || dig == 6 ) {
				diaSegunDigito = dia8;
			} else if (dig == 7 || dig == 8 || dig == 9) {
				diaSegunDigito = dia9;
			} 
		}

		return obtenerFechaPeriodoMas1(periodo, diaSegunDigito).getTime();
	}

	private static Calendar obtenerFechaPeriodoMas1(Date periodo, int dia) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(periodo);
		fecha.add(Calendar.MONTH, 1);
		fecha.set(Calendar.DATE, dia);
		return fecha;
	}

	private static int obtenerCantidadDiasEntreFechaYPrimerDiaHabil(
			Calendar fecha) {
		// Hago esto para poder testear con un feriadosServiceMock en
		// AfipServiceUtilTestCalculoVencimiento
		if (getFeriadoService() == null) {
			setFeriadoService(new FeriadosServiceUtil());
		}
		Calendar siguienteDia = getFeriadoService().obtenerSiguienteDiaHabil(
				fecha);
		int obtenerCantidadDiasCalendario = obtenerCantidadDiasCalendario(
				fecha.getTime(), siguienteDia.getTime());
		return obtenerCantidadDiasCalendario;
	}
	
	private static int obtenerCantidadDiasEntreFechaYPrimerDiaHabil(
			Calendar fecha, PortletRequest request) {
		// Hago esto para poder testear con un feriadosServiceMock en
		// AfipServiceUtilTestCalculoVencimiento
		if (getFeriadoService() == null) {
			setFeriadoService(new FeriadosServiceUtil());
		}
		Calendar siguienteDia = getFeriadoService().obtenerSiguienteDiaHabil(
				fecha, request);
		int obtenerCantidadDiasCalendario = obtenerCantidadDiasCalendario(
				fecha.getTime(), siguienteDia.getTime());
		return obtenerCantidadDiasCalendario;
	}
	
	private static int obtenerCantidadDiasEntreFechaYPrimerDiaHabil(
			Calendar fecha, HttpServletRequest request) {
		// Hago esto para poder testear con un feriadosServiceMock en
		// AfipServiceUtilTestCalculoVencimiento
		if (getFeriadoService() == null) {
			setFeriadoService(new FeriadosServiceUtil());
		}
		Calendar siguienteDia = getFeriadoService().obtenerSiguienteDiaHabil(
				fecha, request);
		int obtenerCantidadDiasCalendario = obtenerCantidadDiasCalendario(
				fecha.getTime(), siguienteDia.getTime());
		return obtenerCantidadDiasCalendario;
	}

	public static FeriadosServiceUtil getFeriadoService() {
		return feriadoService;
	}

	public static void setFeriadoService(FeriadosServiceUtil feriadoService) {
		AfipServiceUtil.feriadoService = feriadoService;
	}
}

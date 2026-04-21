package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

import ar.com.ospim.liquidaciones.reportes.bean.DebitosLiquidacionesPendientes;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosHospitales;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaPrestadores;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaReintegros;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaTotal;

/**
 * <a href="BusquedaDebitosTercerizadorasServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 *
 * <p>
 * </p>
 *
 * @author Pablo Conde
 *
 */
public class BusquedaDebitosTercerizadorasServiceUtil {

	private static BusquedaDebitoTercerizadorasServiceImpl instance = null;

	public static BusquedaDebitoTercerizadorasServiceImpl getInstance() {
		if (null == instance) {
			instance = new BusquedaDebitoTercerizadorasServiceImpl();
		}
		return instance;
	}

	public static List<DebitosLiquidacionesPendientes> getBusquedaDebitosaLiquidacionesPendientes(
			Date fechaDesde, Date fechaHasta , DebitosaTotal debitosaTotal , String idTercerizadoras)
			throws Exception {
		return getInstance().getBusquedaDebitosaLiquidacionesPendientes(fechaDesde,fechaHasta, debitosaTotal, idTercerizadoras);
	}



	public static List<?> getBusquedaDebitosaGrabados(String tipo, Date fechaHasta ,  String idTercerizadoras)
			throws Exception {
		return getInstance().getBusquedaDebitosaGrabados(tipo,fechaHasta, idTercerizadoras);
	}


	public static List<DebitosHospitales> getBusquedaDebitosaHospitales(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal ,  String idTercerizadoras) {
		return getInstance().getBusquedaDebitosHospitales( fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras);
	}

	public static List<DebitosHospitales> getBusquedaDebitosHospitalesStatus(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras) {
		return getInstance().getBusquedaDebitosHospitalesStatus(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras);
	}

	public static List<DebitosaPrestadores> getBusquedaDebitosPrestadoresStatus(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras) {
		return getInstance().getBusquedaDebitosPrestadoresStatus(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras);
	}

	public static List<DebitosaReintegros> getBusquedaDebitosReintegrosStatus(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras) {
		return getInstance().getBusquedaDebitosReintegrosStatus(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras);
	}

	public static List<DebitosLiquidacionesPendientes> getBusquedaDebitosLiquidacionesStatus(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras) {
		return getInstance().getBusquedaDebitosLiquidacionesStatus(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras);
	}

	public static List<DebitosHospitales> getBusquedaDebitosHospitalesStatusBorrador(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras, String workKey) {
		return getInstance().getBusquedaDebitosHospitalesStatusBorrador(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras, workKey);
	}

	public static List<DebitosLiquidacionesPendientes> getBusquedaDebitosLiquidacionesStatusBorrador(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras, String workey) {
		return getInstance().getBusquedaDebitosLiquidacionesStatusBorrador(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras, workey);
	}

	public static List<DebitosaReintegros> getBusquedaDebitosReintegrosStatusBorrador(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras, String workey) {
		return getInstance().getBusquedaDebitosReintegrosStatusBorrador(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras, workey);
	}

	public static List<DebitosaPrestadores> getBusquedaDebitosPrestadoresStatusBorrador(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal, String idTercerizadoras, String workey) {
		return getInstance().getBusquedaDebitosPrestadoresStatusBorrador(fechaDesde, fechaHasta, debitosaTotal, idTercerizadoras, workey);
	}

	public static List<DebitosaReintegros> getBusquedaDebitosReintegros(Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal , String idTercerizadoras) {
		return  getInstance().getBusquedaDebitosReintegros(fechaDesde,fechaHasta,  debitosaTotal, idTercerizadoras);
	}


	public static List<DebitosaPrestadores> getBusquedaDebitosPrestadores(Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal , String idTercerizadoras) {
		return  getInstance().getBusquedaDebitosPrestadores(fechaDesde,fechaHasta, debitosaTotal, idTercerizadoras);
	}

	public static DebitosaTotal getBuscarTotalesDebitos(Date fecha, String idTercerizadora) {
		return  getInstance().getBuscarTotalesDebitos(fecha, idTercerizadora);
	}

	public static int grabarTotalesDebitos(DebitosaTotal deb, String user , Date fecha , String idTercerizadoras ) throws SystemException {
		return  getInstance().grabarTotalesDebitos(deb, user, fecha, idTercerizadoras);
	}

	public static int grabarLiquidacionesPendientesDebitos(DebitosLiquidacionesPendientes deb, String user , Date fecha , String idTercerizadoras ) throws SystemException {
		return  getInstance().grabarLiquidacionesPendientesDebitos(deb, user, fecha, idTercerizadoras);
	}

	public static int grabarHospitalesDebitos(DebitosHospitales deb, String user , Date fecha , String idTercerizadoras ) throws SystemException {
		return  getInstance().grabarHospitalesDebitos(deb, user, fecha, idTercerizadoras);
	}

	public static int grabarReintegrosDebitos(DebitosaReintegros deb, String user , Date fecha , String idTercerizadoras ) throws SystemException {
		return  getInstance().grabarReintegrosDebitos(deb, user, fecha, idTercerizadoras);
	}

	public static int grabarPrestadoresDebitos(DebitosaPrestadores deb, String user , Date fecha , String idTercerizadoras ) throws SystemException {
		return  getInstance().grabarPrestadoresDebitos(deb, user, fecha, idTercerizadoras);
	}

	public static boolean existeReporteDebitoTercerizadoras(Date fechaDesde, Date fechaHasta,String idTercerizadora ) throws SystemException {
		return  getInstance().existeReporteDebitoTercerizadoras(fechaDesde, fechaHasta, idTercerizadora);
	}

	/**
	 * Exite reporte periodo grabado
	 * @param fechaDesde
	 * @param fechaHasta
	 * @param idTercerizadora
	 * @return
	 * @throws SystemException
	 */
	public static boolean existeReporteGrabadoDebitoTercerizadoras(Date periodoHasta, String idTercerizadora, String tipoProceso)
			throws SystemException {
		return getInstance().existeReporteGrabadoDebitoTercerizadoras(periodoHasta, idTercerizadora, tipoProceso);
	}

	/**
	 * Nota de debito
	 * @param deb
	 * @param user
	 * @param fecha
	 * @param idTercerizadoras
	 * @return
	 * @throws SystemException
	 */
	public static int grabarNDB(BigDecimal totalDebitoPrestadoras , User user , Date fecha, Date periodo , String idTercerizadoras ) throws SystemException {
		return  getInstance().grabarTotalesDebitos(totalDebitoPrestadoras, user, fecha, periodo,idTercerizadoras);
	}


	public static java.util.List<DebitosaTotal> getArchivosDebitos(
			java.util.Date periodo, String idTercerizadora)
			throws com.liferay.portal.SystemException {

		return getInstance().getArchivosDebitos(periodo, idTercerizadora);
	}

	public static int grabarBorradorHospitalesDebitos(
			DebitosHospitales deb, String user, Date periodoFechaDesde, String idTercerizadora, String workKey
	) throws SystemException {
		return getInstance().grabarBorradorHospitalesDebitos(deb, user, periodoFechaDesde, idTercerizadora, workKey);
	}

	public static int grabarBorradorReintegrosDebitos(
			DebitosaReintegros deb, String user, Date periodoFechaDesde, String idTercerizadora, String workKey
	) throws SystemException {
		return getInstance().grabarBorradorReintegrosDebitos(deb, user, periodoFechaDesde, idTercerizadora, workKey);
	}

	public static int grabarBorradorPrestadoresDebitos(
			DebitosaPrestadores deb, String user, Date periodoFechaDesde, String idTercerizadora, String workKey
	) throws SystemException {
		return getInstance().grabarBorradorPrestadoresDebitos(deb, user, periodoFechaDesde, idTercerizadora, workKey);
	}

	public static int grabarBorradorLiquidacionesPendientesDebitos(
			DebitosLiquidacionesPendientes deb, String user, Date periodoFechaDesde, String idTercerizadora, String workKey
	) throws SystemException {
		return getInstance().grabarBorradorLiquidacionesPendientesDebitos(deb, user, periodoFechaDesde, idTercerizadora, workKey);
	}

	public static int borrarBorradorDebitosTercerizadorasPorTipoYPeriodo(
			String tipoSel,
			Date periodoFechaDesde,
			String idTercerizadora,
			String workKey
	) throws SystemException {
		return getInstance().borrarBorradorDebitosTercerizadorasPorTipoYPeriodo(tipoSel, periodoFechaDesde, idTercerizadora, workKey);
	}

	public static int[] reabrirDebitosTercerizadorasPeriodo(
			String tipoSel,
			Date periodoFechaDesde,
			Date periodoHasta,
			String idTercerizadora,
			String usuario
	) throws SystemException {
		return getInstance().reabrirDebitosTercerizadorasPeriodo(
				tipoSel, periodoFechaDesde, periodoHasta, idTercerizadora, usuario
		);
	}

	public static int cantidadReporteGrabadoDebitoTercerizadoras(Date fechaHasta, String tercUp, String tipoDb) {
		return getInstance().cantidadReporteGrabadoDebitoTercerizadoras(fechaHasta, tercUp, tipoDb);
	}

	public static List<Map<String, Object>> getPeriodosTrabajadosDebitosTercerizadoras(
			String idTercerizadora,
			String tipoProceso,
			Date desde,
			Date hasta,
			boolean incluirCerrados,
			boolean incluirBorradores) throws SystemException {
		return getInstance().getPeriodosTrabajadosDebitosTercerizadoras(idTercerizadora, tipoProceso, desde, hasta, incluirCerrados, incluirBorradores);
	}

	public static List<Map<String, Object>> getPeriodosPendientesDebitosTercerizadoras(
			String idTercerizadora,
			String tipoProceso,
			Date desde,
			Date hasta
    ) throws SystemException {
		return getInstance().getPeriodosPendientesDebitosTercerizadoras(idTercerizadora, tipoProceso, desde, hasta);
	}
}

package ar.com.ospim.liquidaciones.services;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.DuplicateLiquidacionIdException;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTercero;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleLiq;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleReint;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleReintOrtod;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

/**
 * <a href="BusquedaLiquidacionServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * <p>
 * </p>
 * 
 * @author Carlos Rivas
 * 
 */
public class LiquidacionDebitoTerceroServiceUtil {

	private static LiquidacionDebitoTerceroServiceImpl instance = null;

	public static LiquidacionDebitoTerceroServiceImpl getInstance() {
		if (null == instance) {
			instance = new LiquidacionDebitoTerceroServiceImpl();
		}
		return instance;
	}

	public static List<LiquidacionDebitoTercero> getLiquidacionesDebitosTerceros(
			Date periodoDesde, Date periodoHasta) throws Exception {
		return getInstance().getLiquidacionesDebitosTerceros(periodoDesde,
				periodoHasta);
	}

	public static List<LiquidacionDebitoTercero> getLiquidacionesDebitosTercerosPendientes()
			throws Exception {
		List<LiquidacionDebitoTercero> liquidas = new ArrayList<LiquidacionDebitoTercero>();
		Calendar fechaUltPer = Calendar.getInstance();
		Calendar hoy = Calendar.getInstance();
		Date fechaHoy = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(fechaHoy);
		Date today = dateFormat.parse(sDate);

		hoy.setTime(today);
		hoy.set(Calendar.DATE, 1);

		Date fechaUltimoPeriodo = getInstance()
				.getUltimoPeriodoDebitosTerceros();
		String sFechaUlt = dateFormat.format(fechaUltimoPeriodo);
		fechaUltPer.setTime(dateFormat.parse(sFechaUlt));

		while (fechaUltPer.getTime().before(hoy.getTime())) {
			fechaUltPer.add(Calendar.MONTH, 1);
			LiquidacionDebitoTercero liquidacionDebitos = new LiquidacionDebitoTercero();
			liquidacionDebitos.setPeriodoHasta(fechaUltPer.getTime());
			liquidacionDebitos.setObservaciones("");
			liquidas.add(liquidacionDebitos);
		}
		return liquidas;
	}

	public static LiquidacionDebitoTercero getLiquidacionesDebitosTerceros(
			int id_liquidacion) throws Exception {
		LiquidacionDebitoTercero liquidacionDebitos = getInstance()
				.getLiquidacionDebitosEntry(id_liquidacion);
		liquidacionDebitos
				.setDetalleLiquidacionDebitosTercerosReint(LiquidacionDebitoTerceroServiceUtil
						.getDetalleReintegrosPagosPeriodo(liquidacionDebitos
								.getPeriodoHasta()));
		liquidacionDebitos
				.setDetalleLiquidacionDebitosTercerosReintOrtod(LiquidacionDebitoTerceroServiceUtil
						.getDetalleReintegrosOrtPagosPeriodo(liquidacionDebitos
								.getPeriodoHasta()));
		liquidacionDebitos
				.setDetalleLiquidacionDebitosTercerosLiq(LiquidacionDebitoTerceroServiceUtil
						.getDetalleLiquidacionesPagasPeriodo(liquidacionDebitos
								.getPeriodoHasta()));
		liquidacionDebitos.generarImporteTotal();
		return liquidacionDebitos;
	}

	public static List<LiquidacionDebitoTerceroDetalleReint> getDetalleReintegrosPagosPeriodo(
			Date periodoHasta) {
		return getInstance().getDetalleReintegrosPagosPeriodo(periodoHasta);
	}

	public static List<LiquidacionDebitoTerceroDetalleReintOrtod> getDetalleReintegrosOrtPagosPeriodo(
			Date periodoHasta) {
		return getInstance().getDetalleReintegrosOrtPagosPeriodo(periodoHasta);
	}

	public static List<LiquidacionDebitoTerceroDetalleLiq> getDetalleLiquidacionesPagasPeriodo(
			Date periodoHasta) {
		return getInstance().getDetalleLiquidacionesPagasPeriodo(periodoHasta);
	}

	public static int save(LiquidacionDebitoTercero ldt, User user)
			throws SystemException, SQLException,
			DuplicateLiquidacionIdException {
		int id = getInstance().save(ldt, user.getScreenName());
		return id;
	}

	public static void update(LiquidacionDebitoTercero ldt, User user)
			throws SystemException, SQLException,
			DuplicateLiquidacionIdException {
		getInstance().update(ldt, user.getScreenName());
	}
}
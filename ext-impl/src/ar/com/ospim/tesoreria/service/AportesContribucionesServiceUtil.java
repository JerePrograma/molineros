package ar.com.ospim.tesoreria.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.beans.AportesContribuciones;
import ar.com.ospim.tesoreria.beans.AportesContribucionesHistorico;

public class AportesContribucionesServiceUtil {

	public static List<AportesContribuciones> getAportesContribuciones() {
		return AportesContribucionesServiceImpl.getInstance().getAportesContribuciones();
	}

	public static AportesContribuciones getAportesContribuciones(Date fechaDesde) {
		return AportesContribucionesServiceImpl.getInstance().getAportesContribuciones(fechaDesde);
	}

	public static boolean insertarAportesContribuciones(
			Date fechaDesde,
			Date fechaHasta,
			BigDecimal topeRemuneracion,
			BigDecimal topeAporte) {

		return AportesContribucionesServiceImpl
				.getInstance()
				.insertarAportesContribuciones(
						fechaDesde,
						fechaHasta,
						topeRemuneracion,
						topeAporte
				);
	}


	public static boolean modificarAportesContribuciones(
			Date fechaDesdeOriginal,
			BigDecimal topeRemuneracion,
			BigDecimal topeAporte,
			String usuario) {

		return AportesContribucionesServiceImpl
				.getInstance()
				.modificarAportesContribuciones(
						fechaDesdeOriginal,
						topeRemuneracion,
						topeAporte,
						usuario
				);
	}
	
	public static boolean existePeriodoSuperpuesto(
			Date fechaDesde,
			Date fechaHasta) {

		return AportesContribucionesServiceImpl
				.getInstance()
				.existePeriodoSuperpuesto(
						fechaDesde,
						fechaHasta
				);
	}
	
	public static List<AportesContribucionesHistorico>getHistoricoAportesContribuciones(Date fechaDesde) {

	return AportesContribucionesServiceImpl
			.getInstance()
			.getHistoricoAportesContribuciones(
					fechaDesde
			);
	}
	
	public static boolean eliminarAportesContribuciones(Date fechaDesde) {
		return AportesContribucionesServiceImpl.getInstance().eliminarAportesContribuciones(fechaDesde);
	}
}
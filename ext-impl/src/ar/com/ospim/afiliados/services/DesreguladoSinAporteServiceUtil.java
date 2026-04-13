package ar.com.ospim.afiliados.services;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.ReporteDesreguladoSinAporteBean;

public class DesreguladoSinAporteServiceUtil {
	private static DesreguladoSinAporteServiceImpl instance = null;

	public static DesreguladoSinAporteServiceImpl getInstance() {
		if (null == instance) {
			instance = new DesreguladoSinAporteServiceImpl();
		}
		return instance;
	}

	public static List<ReporteDesreguladoSinAporteBean> getReporteDesreguladoSinAporteDesreg(
			Date periodoDesdeMesAnio,
			Date periodoHastaMesAnio) throws Exception {
		return getInstance().getReporteDesreguladoSinAporteDesreg(periodoDesdeMesAnio, periodoHastaMesAnio);
	}
	
	public static List<ReporteDesreguladoSinAporteBean> getReporteDesreguladoSinAporteMonotrib(
			Date periodoDesdeMesAnio,
			Date periodoHastaMesAnio) throws Exception {
		return getInstance().getReporteDesreguladoSinAporteMonotrib(periodoDesdeMesAnio, periodoHastaMesAnio);
	}
}
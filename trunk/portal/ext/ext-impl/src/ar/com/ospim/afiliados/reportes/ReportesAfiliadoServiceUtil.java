package ar.com.ospim.afiliados.reportes;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Baja;
import ar.com.ospim.afiliados.reportes.beans.BusquedaReportePadronFiltro;
import ar.com.ospim.afiliados.reportes.beans.ReporteCredenResult;
import ar.com.ospim.afiliados.reportes.beans.ReporteLegajosCred;
import ar.com.ospim.afiliados.reportes.beans.ReportePadronTotalResult;

import com.liferay.portal.SystemException;

public class ReportesAfiliadoServiceUtil {

	private static ReportesAfiliadoServiceImpl instance;

	private static ReportesAfiliadoServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReportesAfiliadoServiceImpl();
		}
		return instance;
	}

	public static List<ReportePadronResult> getReportePadron(BusquedaReportePadronFiltro filtro)
			throws SystemException {
		
		return getInstance().getReportePadron(filtro);

	}

	public static List<ReportePadronTotalResult> getReportePadronTotalesEntidad(
			Date fechaVig, String terce, BusquedaReportePadronFiltro filtro) throws SystemException {
		
		return getInstance().getReportePadronTotalesEntidad(fechaVig, terce, filtro);
	}
	
	public static List<ReportePadronTotalResult> getReportePadronTotales(BusquedaReportePadronFiltro filtro)
			throws SystemException {
		
		return getInstance().getReportePadronTotales(filtro);
	}

	public static List<Baja> getReporteListadoBajas(Date fechaIni, Date fechaFin)
			throws SystemException {
		return getInstance().getReporteListadoBajas(fechaIni, fechaFin);
	}
	
	public static List<ReporteCredenResult> getReporteCredencialesEmitidas(Date fechaIni, Date fechaHasta, boolean informar)
			throws SystemException {
		return getInstance().getReporteCredencialesEmitidas(fechaIni,fechaHasta, informar);
	}
	
	public static List<ReporteCredenResult> getReporteCredencialesEmitidasHistorico(int idReporte)
			throws SystemException {
		return getInstance().getReporteCredencialesEmitidasHistorico(idReporte);
	}
	
	public static List<ReporteLegajosCred> getReporteLegajosCredEmitidasHistorico(int idLote) throws SystemException {
		
		return getInstance().getReporteLegajosCredEmitidasHistorico(idLote);
		
	}
}
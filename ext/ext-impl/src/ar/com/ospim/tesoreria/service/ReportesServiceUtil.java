package ar.com.ospim.tesoreria.service;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.reportes.beans.ReporteAportesMonotributistasBean;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.tesoreria.beans.ReporteAportesPagoRamoBean;
import ar.com.ospim.tesoreria.beans.ReporteIngresosDevengadosBean;
import ar.com.ospim.tesoreria.beans.ReporteRankingDeudaEmpresaBean;
import ar.com.ospim.tesoreria.beans.ReporteResumenProcesoCalcDeudaMasivoBean;

public class ReportesServiceUtil {

	
	
	public static List<ReporteRankingDeudaEmpresaBean> getRankingDeudaEmpresas() {
		return ReportesServiceImpl.getInstance().getRankingDeudaEmpresas();
	}
	
	public static List<ReporteAportesPagoRamoBean> getAportesPagoRamo() {
		return ReportesServiceImpl.getInstance().getAportesPagoRamo();
	}
	
	public static List<ReporteRankingDeudaEmpresaBean> getNuevosAfiliadosEmpresas(Date fechaDesde,Date fechaHasta) {
		return ReportesServiceImpl.getInstance().getNuevosAfiliadosEmpresas(fechaDesde,fechaHasta);
	}
	
	public static List<ReporteRankingDeudaEmpresaBean> getNuevosAfiliadosEmpresasPortalMolineros(Date fechaDesde,Date fechaHasta) {
		return ReportesServiceImpl.getInstance().getNuevosAfiliadosEmpresasPortalMolineros(fechaDesde,fechaHasta);
	}
	
	public static List<ReporteRankingDeudaEmpresaBean> getNuevosAfiliadosEmpresasPorRamo(Date fechaDesde) {
		return ReportesServiceImpl.getInstance().getNuevosAfiliadosEmpresasPorRamo(fechaDesde);
	}
	
	public static List<ReporteIngresosDevengadosBean> getIngresosDevengados() {
		return ReportesServiceImpl.getInstance().getIngresosDevengados() ;
	}
	
	public static List<ReporteAportesMonotributistasBean> getControlAportesMonotributistas() {
		return ReportesServiceImpl.getInstance().getControlAportesMonotributistas();
	}
	
	public static List<Cheque> getChequesPendientesCobro(Integer idCta) {
		return ReportesServiceImpl.getInstance().getChequesPendientesCobro(idCta);
	}
	
	public static List<ReporteResumenProcesoCalcDeudaMasivoBean> getResumenProcesoCalcDeudaMasivo(int idProceso) {
		return ReportesServiceImpl.getInstance().getResumenProcesoCalcDeudaMasivo(idProceso);
	}
}

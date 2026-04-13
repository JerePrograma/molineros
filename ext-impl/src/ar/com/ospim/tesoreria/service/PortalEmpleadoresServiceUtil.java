package ar.com.ospim.tesoreria.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;

import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;

/**
 * <a href="BusquedaLiquidacionServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Gustavo Fernandez
 */

public class PortalEmpleadoresServiceUtil {

	private static PortalEmpleadoresServiceImpl instance = null;

	public static PortalEmpleadoresServiceImpl getInstance() {
		if (null == instance) {
			instance = new PortalEmpleadoresServiceImpl();
		}
		return instance;
	}

	public static List<FichaBoletaPortal> getReporteBoletaPortal(
			Date periodoDesde, Date periodoHasta, String cuentaSuc,
			String tipoBoleta, String actaConvenio, Date fechaRecDesde,
			Date fechaRecHasta, String nroCheque, int impDesde, int impHasta,
			String estadoCheque, String cuit_entidad, Date fechaRenDesde,
			Date fechaRenHasta, int idSeccional) {

		List<FichaBoletaPortal> reportes = getInstance()
				.getReporteBoletaPortal(periodoDesde, periodoHasta, cuentaSuc,
						tipoBoleta, actaConvenio, fechaRecDesde, fechaRecHasta,
						nroCheque, impDesde, impHasta, estadoCheque,
						cuit_entidad, fechaRenDesde, fechaRenHasta, idSeccional);

		return reportes;
	}
	
	public static List<ReporteDeudaNominaEmpresa> getDeudaNominaEmpresa(
			String cuit, Date desde, Date hasta) throws SystemException {
		return getInstance().getDeudaNominaEmpresa(cuit, desde, hasta);
	}

	public static List<FichaBoletaPortal> getReporteBoletaPortalTodasEmpresas(
			Date periodoDesde, Date periodoHasta, String cuit_entidad, int idSeccional,
			boolean consolidado) {

		List<FichaBoletaPortal> reportes = getInstance()
				.getReporteBoletaPortalTodasEmpresas(periodoDesde,
						periodoHasta, cuit_entidad, idSeccional, consolidado);

		return reportes;
	}
	
	public static List<FichaBoletaPortal> getBoletaCapitalInteresPortal(
			Date periodoDesde, Date periodoHasta) {

		List<FichaBoletaPortal> reportes = getInstance()
				.getBoletaCapitalInteresPortal(periodoDesde,
						periodoHasta);

		return reportes;
	}
	
	
	public static List<FichaBoletaPortal> getBoletaCapitalSinDDJJ(
			Date periodoDesde, Date periodoHasta) {

		List<FichaBoletaPortal> reportes = getInstance()
				.getBoletaCapitalSinDDJJ(periodoDesde,
						periodoHasta);

		return reportes;
	}
	
	public static FichaBoletaPortal getReporteCantDDJJFinales(
			Date periodoDesde, Date periodoHasta) {

		FichaBoletaPortal reportes = getInstance()
				.getReporteCantDDJJFinales(periodoDesde,
						periodoHasta);

		return reportes;
	}
	public static FichaBoletaPortal getReporteCantDDJJ(
			Date periodoDesde, Date periodoHasta) {

		FichaBoletaPortal reportes = getInstance()
				.getReporteCantDDJJ(periodoDesde,
						periodoHasta);

		return reportes;
	}
	public static FichaBoletaPortal getReporteEmpresasActiva(
			Date periodoDesde, Date periodoHasta) {

		FichaBoletaPortal reportes = getInstance()
				.getReporteEmpresasActivas(periodoDesde,
						periodoHasta);

		return reportes;
	}
	
	
	
	public static Integer getBoletaNroSecuencia(
			String cuit,Integer secuenciaDDJJ, Date periodo, String tipoBoleta) throws SystemException {
		return getInstance().getBoletaNroSecuencia(cuit,secuenciaDDJJ,periodo,tipoBoleta);
	}
	
	public static List<FichaBoletaPortal> getBoletasPorSecuencia(String cuit,String sucursal,
			Integer tipoBoleta, Integer nro) {
		List<FichaBoletaPortal> reportes = getInstance()
				.getBoletasPorSecuencia(cuit,sucursal, tipoBoleta,nro);
		return reportes;
	}
	
	public static List<FichaBoletaPortal> getBoletasImpagas(String cuit,String sucursal,
			Integer tipoBoleta, Integer visualizar){
		List<FichaBoletaPortal> reportes = getInstance()
				.getBoletasImpagas(cuit,sucursal, tipoBoleta,visualizar);
		return reportes;
	}
	
	public static FichaBoletaPortal getBoletaCobranzaByCuitNroBoleta(String cuit,Integer nroBoleta) throws SystemException {
	    return   getInstance().getDatosCobranza(cuit, nroBoleta);
	}
	
	
	public static FichaBoletaPortal getBoletaCobranzaByCuitNroBoleta(String cuit,Integer nroBoleta,String nroMovimiento) throws SystemException {
	    return   getInstance().getDatosCobranza(cuit, nroBoleta,nroMovimiento);
	}
}

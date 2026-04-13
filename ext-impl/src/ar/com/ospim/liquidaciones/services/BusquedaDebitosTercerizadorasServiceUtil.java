package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
	public static boolean existeReporteGrabadoDebitoTercerizadoras(Date fechaHasta,String idTercerizadora ) throws SystemException {
		return  getInstance().existeReporteGrabadoDebitoTercerizadoras(fechaHasta, idTercerizadora);	
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
	

	public static List<DebitosaTotal> getArchivosDebitos ()
			throws SystemException {
		return getInstance().getArchivosDebitos();
	}
	
	
}

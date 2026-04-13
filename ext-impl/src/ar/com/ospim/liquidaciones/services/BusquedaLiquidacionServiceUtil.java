package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.FichaConsumo;
import ar.com.ospim.liquidaciones.beans.FichaFarmacia;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacionOdo;
import ar.com.ospim.liquidaciones.beans.TratamiendoDiscapacidad;

/**
 * <a href="BusquedaLiquidacionServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * <p>
 * </p>
 * 
 * @author Carlos Rivas
 * @modif sva
 */
public class BusquedaLiquidacionServiceUtil {

	private static BusquedaLiquidacionServiceImpl instance = null;

	public static BusquedaLiquidacionServiceImpl getInstance() {
		if (null == instance) {
			instance = new BusquedaLiquidacionServiceImpl();
		}
		return instance;
	}

	public static List<Liquidacion> getBusquedaLiquidaciones(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, int codPrestad, int id_prestador, String cuit, String prestador, int numero, 
			String tipo_compro, String letra_compro, int sucu, String nro_compro, int estado, int id_orden_compra,Integer sector)
			throws Exception {
		return getInstance().getBusquedaLiquidaciones(entidad, fechaDesde,
				fechaHasta, periodoDesde, periodoHasta, codPrestad, id_prestador, cuit, prestador,
				numero, tipo_compro, letra_compro, sucu, nro_compro, estado, id_orden_compra,sector);
	}
	
	public static List<LiquidacionPrestacionOdo> getBusquedaLiquidacionesOdo(
			String entidad, Date fechaDesde, Date fechaHasta,
			Date periodoDesde, Date periodoHasta, int nroAfi, int inte, String cuil_titular, 
			int seccional, BigDecimal presupuesto, int numero) {
		return getInstance().getBusquedaLiquidacionesOdo(entidad, fechaDesde,
				fechaHasta, periodoDesde, periodoHasta, nroAfi, inte, cuil_titular, seccional,
				presupuesto, numero);		
	}
	
	public static List<FichaConsumo> getConsumoAfiliado(String entidad, Date fechaDesde, Date fechaHasta,
			String codPrestaci, int nroAfi, int inte, String cuil_titular, String cuit, String sucu,
			String prestac, String ortop, String protesis, String  odontogeneral, String  liquidaciones, 
			String discapacidad, String farmacia, String liqFarmacia, String preAutorizaciones, String rtaPrevencion) {
		
		return getInstance().getConsumoAfiliado(entidad, fechaDesde,
				fechaHasta, codPrestaci, nroAfi, inte, cuil_titular, cuit, sucu,
				prestac, ortop, protesis, odontogeneral, liquidaciones, discapacidad, farmacia, 
				liqFarmacia, preAutorizaciones, rtaPrevencion);
	}
	
	public static List<FichaConsumo> getReporteDiscapacidad(String entidad, Date fechaDesde, Date fechaHasta, Date periodoDesde, Date periodoHasta, 
			String codPrestaci, int estado, int nroAfi, int inte, String cuil_titular, String cuit, String sucu,
			String prestac, String liquidaciones, String diagnostico, String ciex) {
		
		List<FichaConsumo> reportes = getInstance().getReporteDiscapacidad(entidad, fechaDesde,
				fechaHasta, periodoDesde, periodoHasta, codPrestaci, estado, nroAfi, inte, cuil_titular, cuit, sucu,
				prestac, liquidaciones, diagnostico, ciex);

		return reportes;
	}
	
	public static List<TratamiendoDiscapacidad> getReporteTratamiendoDiscapacidad(Date periodoDesde, Date periodoHasta, boolean sur, 
			String ciex, String codigoPrestacion, String cuitPrestador, boolean rangoEtario, String tipoDiscapacidad) {
		
		List<TratamiendoDiscapacidad> reportes = null;
		
		if (rangoEtario == false){
			reportes = getInstance().getReporteTratamiendoDiscapacidad(periodoDesde, periodoHasta, sur,
					ciex, codigoPrestacion, cuitPrestador, tipoDiscapacidad);
		}else { 
			reportes = getInstance().reporteTratamientosDiscaPorEdad(periodoDesde, periodoHasta, sur, 
					ciex, codigoPrestacion, cuitPrestador, tipoDiscapacidad) ;
		}
		
		return reportes;
	}
	
	public static List<FichaFarmacia> getLiquidacionesFarmacia(Date periodoDesde, Date periodoHasta,String troquel,
			String cuil, Integer inte, String id_farmacia,String farmacia ,int opDesde, int opHasta, boolean pmi) {
		
		List<FichaFarmacia> reportes = getInstance().getLiquidacionesFarmacia( periodoDesde,
				periodoHasta, troquel,  cuil, inte, id_farmacia, farmacia , opDesde,  opHasta, pmi);
		
		return reportes;
	}
}

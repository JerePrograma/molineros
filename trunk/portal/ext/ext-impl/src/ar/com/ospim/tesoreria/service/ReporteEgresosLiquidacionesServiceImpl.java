package ar.com.ospim.tesoreria.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.beans.EgresoLiquidacion;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteEgresosLiquidacionesServiceImpl {

	
	private static Log _log = LogFactoryUtil.getLog(ReporteEgresosLiquidacionesServiceImpl.class);

	private static ReporteEgresosLiquidacionesServiceImpl instance = null;

	public static ReporteEgresosLiquidacionesServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReporteEgresosLiquidacionesServiceImpl();
		}
		return instance;
	}


	public List<EgresoLiquidacion> getLiquidacionesConcepto(Date fechaDesde, Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
//			String sql = "{call reporte_egresos_por_concepto_cta_46(?,?)}";
			String sql = "{call reporte_egresos_por_concepto_cta_46_y_cta_44(?,?)}";
//			String sql = "{call reporte_egresos_por_concepto_cta_44(?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMapping(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Egresos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
	
	public List<EgresoLiquidacion> getLiquidacionesConceptoAgrupado(Date fechaDesde, Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
//			String sql = "{call reporte_egresos_por_concepto_cta_46_agrupados(?,?)}";
			String sql = "{call reporte_egresos_por_concepto_cta_46_y_cta_44_agrupados(?,?)}";
//			String sql = "{call reporte_egresos_por_concepto_cta_44_agrupados(?,?)}";
			
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMapping(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Egresos AGRUPADO", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
	
	public List<EgresoLiquidacion> getEgresosPrestacion(Date fechaDesde, Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
//			String sql = "{call reporte_egresos_prestaciones(?,?)}";
//			String sql = "{call reporte_egresos_prestaciones_cta_46_y_cta_44(?,?)}";
			String sql = "{call reporte_egresos_prestaciones_cta44(?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMappingEgresos(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Egresos Prestacionales cuenta 46", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
	
	public List<EgresoLiquidacion> getEgresosPrestacionOS(Date fechaDesde, Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
			String sql = "{call reporte_egresos_prestaciones_abona_os(?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMappingEgresosOS(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Egresos ABONA OS", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
	
	public List<EgresoLiquidacion> getOtrosReintegrosPrestacionOS(Date fechaDesde, Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
			String sql = "{call reporte_egresos_otros_reintegros_prestaciones(?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMappingOtrosReintegosOS(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Egresos ABONA OS", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}


}

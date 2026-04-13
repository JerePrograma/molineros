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

public class ReporteDerivacionTercerizadorasServiceImpl {

	
	private static Log _log = LogFactoryUtil.getLog(ReporteDerivacionTercerizadorasServiceImpl.class);

	private static ReporteDerivacionTercerizadorasServiceImpl instance = null;

	public static ReporteDerivacionTercerizadorasServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReporteDerivacionTercerizadorasServiceImpl();
		}
		return instance;
	}

	public List<EgresoLiquidacion> getLiquidacionesConceptoAgrupado(Date fechaLiq) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
			String sql = "{call reporte_deriv_aportes_por_concepto(?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaLiq.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMapping(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
	
	public List<EgresoLiquidacion> getLiquidacionesPeriodo(Date fechaLiq) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
			String sql = "{call reporte_deriv_aportes_por_periodo(?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaLiq.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMapping(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Períodos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
	
	public List<EgresoLiquidacion> getLiquidacionesCuilPorTercerizadora(Date fechaLiq) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
			String sql = "{call reporte_deriv_cuiles_por_tercerizadoras(?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaLiq.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<EgresoLiquidacion>();
			while (rs.next()) {
				EgresoLiquidacion ins = EgresoLiquidacion.getMapping(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Cuiles", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
	
//	
	public List<EgresoLiquidacion> getLiquidacionesConcepto(Date fechaDesde, Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
			String sql = "{call reporte_egresos_por_concepto_cta_46(?,?)}";
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
	
	
	
	public List<EgresoLiquidacion> getEgresosPrestacion(Date fechaDesde, Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<EgresoLiquidacion> listaEgresos = null;
		try {
			String sql = "{call reporte_egresos_prestaciones(?,?)}";
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
			_log.error("Error al traer reporte Egresos PRESTACI", e);
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

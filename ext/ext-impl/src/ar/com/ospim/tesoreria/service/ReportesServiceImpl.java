package ar.com.ospim.tesoreria.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.reportes.beans.ReporteAportesMonotributistasBean;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.tesoreria.beans.ReporteAportesPagoRamoBean;
import ar.com.ospim.tesoreria.beans.ReporteIngresosDevengadosBean;
import ar.com.ospim.tesoreria.beans.ReporteRankingDeudaEmpresaBean;
import ar.com.ospim.tesoreria.beans.ReporteResumenProcesoCalcDeudaMasivoBean;
import ar.com.ospim.util.ConnectionHelper;

public class ReportesServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ReportesServiceImpl.class);

	private static ReportesServiceImpl instance = null;

	public static ReportesServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReportesServiceImpl();
		}
		return instance;
	}

	
	public List<ReporteRankingDeudaEmpresaBean> getRankingDeudaEmpresas() {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteRankingDeudaEmpresaBean> reporte = null;
		try {
			String sql = "{call busca_ranking_deuda_empresa()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteRankingDeudaEmpresaBean>();
			while (rs.next()) {
				ReporteRankingDeudaEmpresaBean deuda = ReporteRankingDeudaEmpresaBean.getMapping(rs);
				reporte.add(deuda);
			}
		} catch (Exception e) {
			_log.error("Error al traer Ranking deuda empresas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}
	

	public List<ReporteAportesPagoRamoBean> getAportesPagoRamo() {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAportesPagoRamoBean> reporte = null;
		try {
			String sql = "{call busca_aportes_pago_ramo()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteAportesPagoRamoBean>();
			while (rs.next()) {
				ReporteAportesPagoRamoBean deuda = ReporteAportesPagoRamoBean.getMapping(rs);
				reporte.add(deuda);
			}
		} catch (Exception e) {
			_log.error("Error al traer Aportes Pago Ramo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}
	
	public List<ReporteRankingDeudaEmpresaBean> getNuevosAfiliadosEmpresas(Date fechaDesde,Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteRankingDeudaEmpresaBean> reporte = null;
		try {
			String sql = "{call reporte_empresas_nuevos_afiliados(?,?)}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			
			if (null != fechaDesde) {
				stmt.setDate(1,  new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != fechaHasta) {
				stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteRankingDeudaEmpresaBean>();
			while (rs.next()) {
				ReporteRankingDeudaEmpresaBean deuda = new ReporteRankingDeudaEmpresaBean();
				deuda.setCuit(rs.getString("cuit_empresa"));
				deuda.setRazonSocial(rs.getString("razon_social"));
				deuda.setTotal_calculo_deuda(rs.getBigDecimal("cantidad_afiliados"));
				deuda.setRamoEmpresaId(rs.getInt("codigo_ramo"));
				reporte.add(deuda);
			}
		} catch (Exception e) {
			_log.error("Error al traer Nuevos Afiliados Empresas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}
	
	public List<ReporteRankingDeudaEmpresaBean> getNuevosAfiliadosEmpresasPortalMolineros(Date fechaDesde,Date fechaHasta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteRankingDeudaEmpresaBean> reporte = null;
		try {
			String sql = "{call informacion_afip.reporte_empresas_nuevos_afiliados(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (null != fechaDesde) {
				stmt.setDate(1,  new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != fechaHasta) {
				stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteRankingDeudaEmpresaBean>();
			while (rs.next()) {
				ReporteRankingDeudaEmpresaBean deuda = new ReporteRankingDeudaEmpresaBean();
				deuda.setCuit(rs.getString("cuit_empresa"));
				deuda.setRazonSocial(rs.getString("razon_social"));
				deuda.setRamoEmpresaId(rs.getInt("codigo_ramo"));
				deuda.setTotal_calculo_deuda(rs.getBigDecimal("cantidad_afiliados"));
				reporte.add(deuda);
			}
		} catch (Exception e) {
			_log.error("Error al traer Nuevos Afiliados Empresas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}

	public List<ReporteRankingDeudaEmpresaBean> getNuevosAfiliadosEmpresasPorRamo(Date fechaDesde) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteRankingDeudaEmpresaBean> reporte = null;
		try {
			String sql = "{call informacion_afip.reporte_empresas_nuevos_afiliados_por_ramo(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (null != fechaDesde) {
				stmt.setDate(1,  new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteRankingDeudaEmpresaBean>();
			while (rs.next()) {
				ReporteRankingDeudaEmpresaBean deuda = new ReporteRankingDeudaEmpresaBean();
				deuda.setCuit(rs.getString("cuit_empresa"));
				deuda.setRazonSocial(rs.getString("razon_social"));
				deuda.setRamoEmpresaId(rs.getInt("codigo_ramo"));
				deuda.setTotal_calculo_deuda(rs.getBigDecimal("cantidad_afiliados"));
				reporte.add(deuda);
			}
		} catch (Exception e) {
			_log.error("Error al traer Nuevos Afiliados Empresas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}
	
	public List<ReporteIngresosDevengadosBean> getIngresosDevengados() {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteIngresosDevengadosBean> reporte = null;
		try {
			String sql = "{call informes.trae_ingresos_devengados()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteIngresosDevengadosBean>();
			while (rs.next()) {
				ReporteIngresosDevengadosBean d = ReporteIngresosDevengadosBean.getMapping(rs);
				reporte.add(d);
			}
		} catch (Exception e) {
			_log.error("Error al traer Ingresos Devengados", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}
	
	public List<ReporteAportesMonotributistasBean> getControlAportesMonotributistas() {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAportesMonotributistasBean> reporte = null;
		try {
			String sql = "{call informes.trae_control_aportes_monotributistas()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteAportesMonotributistasBean>();
			while (rs.next()) {
				ReporteAportesMonotributistasBean d = ReporteAportesMonotributistasBean.getMapping(rs);
				reporte.add(d);
			}
		} catch (Exception e) {
			_log.error("Error al traer Ingresos Devengados", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}
	
	public List<Cheque> getChequesPendientesCobro(Integer idCta) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Cheque> reporte = null;
		try {
			String sql = "{call trae_cheques_pendientes_cobro(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCta);
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<Cheque>();
			while (rs.next()) {
				Cheque d = new Cheque();
				d.setaNombreDe(rs.getString("razon_social"));
				d.setNumero(rs.getBigDecimal("nro_cheque"));
				d.setCuit(rs.getString("cuit"));
//				d.setConcepto(rs.getString("concepto"));
				d.setImporte(rs.getBigDecimal("importe").multiply(("D".equalsIgnoreCase(rs.getString("debito_credito") )?BigDecimal.valueOf(-1) :
					BigDecimal.valueOf(1))));
				d.setFecha(rs.getDate("fecha"));
				reporte.add(d);
			}
		} catch (Exception e) {
			_log.error("Error al traer Cheques Pendientes Cobro", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}
	
	public List<ReporteResumenProcesoCalcDeudaMasivoBean> getResumenProcesoCalcDeudaMasivo(int idProceso) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteResumenProcesoCalcDeudaMasivoBean> reporte = null;
		try {
			String sql = "{call trae_resumen_proceso_acta_masiva(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idProceso);
			
			ResultSet rs = stmt.executeQuery();
			reporte = new ArrayList<ReporteResumenProcesoCalcDeudaMasivoBean>();
			while (rs.next()) {
				ReporteResumenProcesoCalcDeudaMasivoBean deuda = ReporteResumenProcesoCalcDeudaMasivoBean.getMapping(rs);
				reporte.add(deuda);
			}
		} catch (Exception e) {
			_log.error("Error al traer Resumen Proceso Calc. Deuda Masivo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return reporte;
	}

}

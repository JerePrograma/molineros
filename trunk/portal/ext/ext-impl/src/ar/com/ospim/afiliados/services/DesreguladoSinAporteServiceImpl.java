package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.ReporteDesreguladoSinAporteBean;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class DesreguladoSinAporteServiceImpl {
	private static Log _log = LogFactoryUtil
			.getLog(DesreguladoSinAporteServiceImpl.class);
	
	public List<ReporteDesreguladoSinAporteBean> getReporteDesreguladoSinAporteDesreg(
			Date periodoDesdeMesAnio,
			Date periodoHastaMesAnio) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDesreguladoSinAporteBean> listaReporteDesreguladoSinAporteBean = null;

		try {
			String sql = "{call reporte_deudores_desregulados_des(?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setDate(1, new java.sql.Date(periodoDesdeMesAnio.getTime()));
			stmt.setDate(2, new java.sql.Date(periodoHastaMesAnio.getTime()));

			ResultSet rs = stmt.executeQuery();
			listaReporteDesreguladoSinAporteBean = new ArrayList<ReporteDesreguladoSinAporteBean>();
			while (rs.next()) {
				ReporteDesreguladoSinAporteBean bean = ReporteDesreguladoSinAporteBean.getMapping(rs);						
				listaReporteDesreguladoSinAporteBean.add(bean);
			}
		} catch (Exception e) {
			_log.error("Error al buscar empleadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaReporteDesreguladoSinAporteBean;
	}

	public List<ReporteDesreguladoSinAporteBean> getReporteDesreguladoSinAporteMonotrib(
			Date periodoDesdeMesAnio,
			Date periodoHastaMesAnio) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDesreguladoSinAporteBean> listaReporteDesreguladoSinAporteBean = null;

		try {
			String sql = "{call reporte_deudores_desregulados_monotrib(?,?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setDate(1, new java.sql.Date(periodoDesdeMesAnio.getTime()));
			stmt.setDate(2, new java.sql.Date(periodoHastaMesAnio.getTime()));

			ResultSet rs = stmt.executeQuery();
			listaReporteDesreguladoSinAporteBean = new ArrayList<ReporteDesreguladoSinAporteBean>();
			while (rs.next()) {
				ReporteDesreguladoSinAporteBean bean = ReporteDesreguladoSinAporteBean.getMapping(rs);						
				listaReporteDesreguladoSinAporteBean.add(bean);
			}
		} catch (Exception e) {
			_log.error("Error al buscar empleadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaReporteDesreguladoSinAporteBean;
	}
}
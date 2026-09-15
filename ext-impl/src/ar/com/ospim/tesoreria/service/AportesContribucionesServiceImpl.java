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

import ar.com.ospim.tesoreria.beans.AportesContribuciones;
import ar.com.ospim.tesoreria.beans.AportesContribucionesHistorico;
import ar.com.ospim.util.ConnectionHelper;

public class AportesContribucionesServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(AportesContribucionesServiceImpl.class);
	private static AportesContribucionesServiceImpl instance = null;

	public static AportesContribucionesServiceImpl getInstance() {

		if (null == instance) {
			instance = new AportesContribucionesServiceImpl();
		}

		return instance;
	}

	public List<AportesContribuciones> getAportesContribuciones() {

		Connection con = null;
		CallableStatement stmt = null;
		List<AportesContribuciones> lista = null;

		try {
			String sql = "{call trae_topes_aportes_os()}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			ResultSet rs = stmt.executeQuery();

			lista = new ArrayList<AportesContribuciones>();

			while (rs.next()) {

				AportesContribuciones tope = AportesContribuciones.getMapping(rs);
				lista.add(tope);
			}

		} catch (Exception e) {
			_log.error("Error al traer topes de aportes OS", e);

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return lista;
	}

	public AportesContribuciones getAportesContribuciones(Date fechaDesde) {

		Connection con = null;
		CallableStatement stmt = null;
		AportesContribuciones tope = null;

		try {

			String sql = "{call trae_tope_aportes_os(?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			if (fechaDesde != null) {
				stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				tope = AportesContribuciones.getMapping(rs);
			}

		} catch (Exception e) {
			_log.error("Error al traer tope de aportes OS", e);

		} finally {

			ConnectionHelper.cerrar(stmt, con);
		}

		return tope;
	}

	public boolean insertarAportesContribuciones(
			Date fechaDesde,
			Date fechaHasta,
			BigDecimal topeRemuneracion,
			BigDecimal topeAporte) {

		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call insertar_tope_aportes_os(?,?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			stmt.setBigDecimal(3, topeRemuneracion);
			stmt.setBigDecimal(4, topeAporte);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) == 1;
			}

		} catch (Exception e) {
			_log.error("Error al insertar tope de aportes OS",e);
			
		} finally {

			ConnectionHelper.cerrar(stmt, con);
		}

		return false;
	}

	public boolean modificarAportesContribuciones(
			Date fechaDesdeOriginal,
			BigDecimal topeRemuneracion,
			BigDecimal topeAporte,
			String usuario) {

		Connection con = null;
		CallableStatement stmt = null;

		try {

			String sql = "{call modificar_tope_aportes_os(?,?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			stmt.setDate(1, new java.sql.Date(fechaDesdeOriginal.getTime()));
			stmt.setBigDecimal(2, topeRemuneracion);
			stmt.setBigDecimal(3, topeAporte);
			stmt.setString(4, usuario);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) == 1;
			}

		} catch (Exception e) {
			_log.error("Error al modificar tope de aportes OS",e);

		} finally {

			ConnectionHelper.cerrar(stmt, con);
		}

		return false;
	}
	
	public boolean existePeriodoSuperpuesto(
			Date fechaDesde,
			Date fechaHasta) {

		Connection con = null;
		CallableStatement stmt = null;

		try {

			String sql = "{call existe_periodo_topes_aportes_os(?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getBoolean(1);
			}

		} catch (Exception e) {
			_log.error("Error al validar período de aportes OS",e);

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return false;
	}
	
	public List<AportesContribucionesHistorico> getHistoricoAportesContribuciones(
			Date fechaDesde) {

		Connection con = null;
		CallableStatement stmt = null;

		List<AportesContribucionesHistorico> lista = new ArrayList<AportesContribucionesHistorico>();

		try {
			String sql = "{call trae_historico_topes_aportes_os(?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(AportesContribucionesHistorico.getMapping(rs));
			}

		} catch (Exception e) {
			_log.error("Error al traer histórico de aportes OS", e);

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return lista;
	}
	
	public boolean eliminarAportesContribuciones(Date fechaDesde) {

		Connection con = null;
		CallableStatement stmt = null;

		try {

			String sql = "{call eliminar_tope_aportes_os(?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) == 1;
			}

		} catch (Exception e) {
			_log.error("Error al eliminar tope de aportes OS", e);

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return false;
	}
}
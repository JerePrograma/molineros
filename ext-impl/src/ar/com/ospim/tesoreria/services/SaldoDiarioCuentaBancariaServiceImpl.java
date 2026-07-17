package ar.com.ospim.tesoreria.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.beans.SaldoDiarioCuentaBancaria;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SaldoDiarioCuentaBancariaServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(
		SaldoDiarioCuentaBancariaServiceImpl.class
	);

	public List<SaldoDiarioCuentaBancaria> buscar(
			Date fechaDesde,
			Date fechaHasta,
			int idCuentaBcria,
			int entidad)
		throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		ResultSet rs = null;

		List<SaldoDiarioCuentaBancaria> saldos =
			new ArrayList<SaldoDiarioCuentaBancaria>();

		try {
			con = ConnectionHelper.getConnection();

			String sql = "{call public.buscar_saldo_diario_cuentas_bancarias(?,?,?,?)}";

			stmt = con.prepareCall(sql);

			stmt.setDate(1, fechaDesde != null ? new java.sql.Date(fechaDesde.getTime()) : null);

			stmt.setDate(2, fechaHasta != null ? new java.sql.Date(fechaHasta.getTime()) : null);

			if (idCuentaBcria > 0) {
				stmt.setInt(3, idCuentaBcria);
			}
			else {
				stmt.setNull(3, java.sql.Types.INTEGER);
			}

			stmt.setString(4, "O");

			rs = stmt.executeQuery();

			while (rs.next()) {
				SaldoDiarioCuentaBancaria saldo =
					new SaldoDiarioCuentaBancaria();

				saldo.setIdCuentaBcria(rs.getInt("id_cuenta_bcria"));
				saldo.setCuentaBancaria(rs.getString("cuenta_bancaria"));
				saldo.setFechaInicioEjercicio(rs.getDate("fecha_inicio_ejercicio"));
				saldo.setSaldo(rs.getBigDecimal("saldo"));

				saldos.add(saldo);
			}
		}
		catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		}
		finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return saldos;
	}

	public void agregar(
			int idCuentaBcria,
			Date fechaInicioEjercicio,
			BigDecimal saldo)
		throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			if (idCuentaBcria <= 0) {
				throw new SystemException("Debe seleccionar una cuenta bancaria.");
			}

			if (fechaInicioEjercicio == null) {
				throw new SystemException("Debe ingresar la fecha de inicio de ejercicio.");
			}

			if (saldo == null) {
				throw new SystemException("Debe ingresar el saldo.");
			}

			con = ConnectionHelper.getConnection();

			String sql = "{call public.agregar_saldo_diario_cuenta_bancaria(?,?,?)}";

			stmt = con.prepareCall(sql);

			stmt.setInt(1, idCuentaBcria);
			stmt.setDate(2, new java.sql.Date(fechaInicioEjercicio.getTime()));
			stmt.setBigDecimal(3, saldo);

			rs = stmt.executeQuery();

			int resultado = 0;

			if (rs.next()) {
				resultado = rs.getInt(1);
			}

			if (resultado == 0) {
				throw new SystemException(
					"Ya existe un saldo cargado para la cuenta bancaria y fecha seleccionadas."
				);
			}
		}
		catch (SystemException e) {
			_log.debug(e.getMessage());
			throw e;
		}
		catch (Exception e) {
			_log.error(e);
			throw new SystemException(e);
		}
		finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void borrar(
			int idCuentaBcria,
			Date fechaInicioEjercicio)
		throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;

		try {
			if (idCuentaBcria <= 0) {
				throw new SystemException("Debe seleccionar una cuenta bancaria.");
			}

			if (fechaInicioEjercicio == null) {
				throw new SystemException("Debe ingresar la fecha de inicio de ejercicio.");
			}

			con = ConnectionHelper.getConnection();

			String sql = "{call public.borrar_saldo_diario_cuenta_bancaria(?,?)}";

			stmt = con.prepareCall(sql);

			stmt.setInt(1, idCuentaBcria);
			stmt.setDate(2, new java.sql.Date(fechaInicioEjercicio.getTime()));

			stmt.executeQuery();
		}
		catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		}
		finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

}
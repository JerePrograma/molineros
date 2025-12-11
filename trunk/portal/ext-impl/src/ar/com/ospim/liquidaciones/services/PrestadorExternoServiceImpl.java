package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.DuplicatePrestadorExternoIdException;
import ar.com.ospim.liquidaciones.beans.PrestadorExterno;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class PrestadorExternoServiceImpl {
	private static Log _log = LogFactoryUtil
			.getLog(PrestadorExternoServiceImpl.class);

	private static PrestadorServiceImpl instance = null;

	public static PrestadorServiceImpl getInstance() {
		if (null == instance) {
			instance = new PrestadorServiceImpl();
		}
		return instance;
	}

	public List<PrestadorExterno> getPrestadores(int id, String tipo_matricula,
			String numero_matricula, String descripcion, String cuit) {
		Connection con = null;
		CallableStatement stmt = null;
		List<PrestadorExterno> listaPrestadores = null;
		try {
			String sql = "{call buscar_prestadores_externos(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (tipo_matricula != null && tipo_matricula.trim().equals("")) {
				tipo_matricula = null;
			}
			if (numero_matricula != null && numero_matricula.trim().equals("")) {
				numero_matricula = null;
			}
			if (descripcion != null && descripcion.trim().equals("")) {
				descripcion = null;
			}
			if (cuit != null && cuit.trim().equals("")) {
				cuit = null;
			}			
			stmt.setString(1, tipo_matricula);
			stmt.setInt(2, (numero_matricula != null) ? Integer
					.valueOf(numero_matricula) : 0);
			stmt.setString(3, descripcion);
			stmt.setInt(4, id);
			stmt.setString(5, cuit);

			ResultSet rs = stmt.executeQuery();
			listaPrestadores = new ArrayList<PrestadorExterno>();
			while (rs.next()) {
				PrestadorExterno emp = PrestadorExterno.getMapping(rs);
				listaPrestadores.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar prestadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestadores;
	}

	public PrestadorExterno getPrestadorExterno(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		PrestadorExterno emp = null;
		try {
			String sql = "{call buscar_prestador_externo_by_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				emp = PrestadorExterno.getMapping(rs, "prs__");
			}
		} catch (Exception e) {
			_log.error("Error al buscar prestador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return emp;
	}

	public int save(String cuit, String desc, String screenName, int iva,
			String matriculaTipo, int matriculaNro, int matriculaProvincia,
			String matriculaCategoria) throws SystemException,
			DuplicatePrestadorExternoIdException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call inserta_prestador_externo (?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, desc);
			if (iva == 0) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(3, iva);
			}
			stmt.setString(4, matriculaTipo);
			if (matriculaNro != 0) {
				stmt.setInt(5, matriculaNro);
			} else {
				stmt.setNull(5, Type.INT);
			}
			if (matriculaProvincia != 0) {
				stmt.setInt(6, matriculaProvincia);
			} else {
				stmt.setNull(6, Type.INT);
			}
			stmt.setString(7, matriculaCategoria);
			stmt.setString(8, screenName);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar prestador externo", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicatePrestadorExternoIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar prestador", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}

	public void update(int id_prestador_ext, String cuit, String desc,
			String screenName, int iva, String matriculaTipo, int matriculaNro,
			int matriculaProvincia, String matriculaCategoria)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_prestador_externo (?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, desc);
			if (iva == 0) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(3, iva);
			}
			stmt.setString(4, matriculaTipo);
			if (matriculaNro != 0) {
				stmt.setInt(5, matriculaNro);
			} else {
				stmt.setNull(5, Type.INT);
			}
			if (matriculaProvincia != 0) {
				stmt.setInt(6, matriculaProvincia);
			} else {
				stmt.setNull(40, Type.INT);
			}
			stmt.setString(7, matriculaCategoria);
			stmt.setInt(8, id_prestador_ext);
			stmt.setString(9, screenName);

			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al actualizar prestador externo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}
	//
	// public void borrar(int id, String screenName) throws SQLException,
	// ImposibleBorrarPrestadorException {
	// Connection con = null;
	// CallableStatement stmt = null;
	// try {
	// String sql = "{call borra_prestador(?, ?)}";
	// con = ConnectionHelper.getConnection();
	// stmt = con.prepareCall(sql.toString());
	// stmt.setInt(1, id);
	// stmt.setString(2, screenName);
	// ResultSet rs = stmt.executeQuery();
	// while (rs.next()) {
	// if (rs.getInt(1) == 0) {
	// throw new ImposibleBorrarPrestadorException();
	// }
	// }
	// } catch (ImposibleBorrarPrestadorException e) {
	// _log.error("Error al buscar contactos", e);
	// throw e;
	// } finally {
	// try {
	// stmt.close();
	// con.close();
	// } catch (SQLException e) {
	// _log.debug("Error al cerrar la conexion", e);
	// throw e;
	// }
	// }
	// }

}
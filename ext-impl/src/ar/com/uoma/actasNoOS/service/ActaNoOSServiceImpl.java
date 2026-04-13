package ar.com.uoma.actasNoOS.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hsqldb.Types;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.DuplicateActaIdException;
import ar.com.ospim.tesoreria.ImposibleBorrarActaException;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Acta.ActaPagoIngresado;
import ar.com.ospim.tesoreria.beans.Acta.ActaRelacionada;
import ar.com.ospim.tesoreria.beans.Acta.DetalleActaInspectores;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.Inspector;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReporteActaBean;
import ar.com.ospim.tesoreria.beans.ReporteCobranzaActaBean;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.ActasAcuerdos;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ActaNoOSServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ActaNoOSServiceImpl.class);

	private static ActaNoOSServiceImpl instance = null;

	public static ActaNoOSServiceImpl getInstance() {
		if (null == instance) {
			instance = new ActaNoOSServiceImpl();
		}
		return instance;
	}

	public int save(Acta acta, String screenName, Connection connectionParameter)
			throws DuplicateActaIdException, SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_acta_no_os(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, acta.getNumero());
			stmt.setString(2, acta.getEmpresa().getCuit());
			stmt.setString(3, acta.getEmpresa().getSucursal());
			java.sql.Date fechaIniSqlDate = new java.sql.Date(acta
					.getFechaInicio().getTime());
			stmt.setDate(4, fechaIniSqlDate);			
			java.sql.Date fechaPagoSqlDate = new java.sql.Date(acta
					.getFechaPago().getTime());
			stmt.setDate(5, fechaPagoSqlDate);
			stmt.setBigDecimal(6, acta.getOtros());
			stmt.setBigDecimal(7, acta.getInteres());
			stmt.setBigDecimal(8, acta.getCapital());
			stmt.setBigDecimal(9, acta.getDeudaActasRelacionadas());
			if (acta.getCierre_fecha() != null) {
				stmt.setDate(10, new java.sql.Date(acta.getCierre_fecha()
						.getTime()));
			} else {
				stmt.setDate(10, null);
			}
			stmt.setString(11, screenName);
			stmt.setBoolean(12, acta.isMolinera());
			stmt.setString(13, acta.getEstado());
			stmt.setString(14, acta.getEntidad());
			stmt.setDate(15, new java.sql.Date(acta.getPeriodoInicial().getTime()));
			stmt.setDate(16, new java.sql.Date(acta.getPeriodoFinal().getTime()));
			stmt.setBigDecimal(17, acta.getCapitalSindicato());
			stmt.setBigDecimal(18, acta.getInteresSindicato());
			stmt.setBigDecimal(19, acta.getCapitalSolidario());
			stmt.setBigDecimal(20, acta.getInteresSolidario());
			stmt.setBigDecimal(21, acta.getCapitalUsufructo());
			stmt.setBigDecimal(22, acta.getInteresUsufructo());
			stmt.setBigDecimal(23, acta.getCapitalArt46());
			stmt.setBigDecimal(24, acta.getInteresArt46());
			stmt.setBoolean(25, acta.isActaCerrada());
			if (acta.getEstadoSeguimiento() != null) {
				stmt.setInt(26, acta.getEstadoSeguimiento().getId());
			} else {
				stmt.setNull(26, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar acta no os", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateActaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar acta_no_os", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public void update(Acta acta, String screenName,
			Connection connectionParameter) throws DuplicateActaIdException,
			SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call actualiza_acta_no_os (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, acta.getId());
			stmt.setString(2, acta.getNumero());
			stmt.setString(3, acta.getEmpresa().getCuit());
			stmt.setString(4, "000");
			java.sql.Date fechaIniSqlDate = new java.sql.Date(acta
					.getFechaInicio().getTime());
			stmt.setDate(5, fechaIniSqlDate);
			java.sql.Date fechaPagoSqlDate = new java.sql.Date(acta
					.getFechaPago().getTime());
			stmt.setDate(6, fechaPagoSqlDate);
			stmt.setBigDecimal(7, acta.getOtros());
			stmt.setBigDecimal(8, acta.getInteres());
			stmt.setBigDecimal(9, acta.getCapital());
			stmt.setBigDecimal(10, acta.getDeudaActasRelacionadas());
			if (acta.getCierre_fecha() != null) {
				stmt.setDate(11, new java.sql.Date(acta.getCierre_fecha()
						.getTime()));
			} else {
				stmt.setDate(11, null);
			}
			stmt.setString(12, screenName);
			stmt.setBoolean(13, acta.isMolinera());
			stmt.setString(14, acta.getEstado());
			stmt.setString(15, acta.getEntidad());
			stmt.setDate(16, new java.sql.Date(acta.getPeriodoInicial().getTime()));
			stmt.setDate(17, new java.sql.Date(acta.getPeriodoFinal().getTime()));
			stmt.setBigDecimal(18, acta.getCapitalSindicato());
			stmt.setBigDecimal(19, acta.getInteresSindicato());
			stmt.setBigDecimal(20, acta.getCapitalSolidario());
			stmt.setBigDecimal(21, acta.getInteresSolidario());
			stmt.setBigDecimal(22, acta.getCapitalUsufructo());
			stmt.setBigDecimal(23, acta.getInteresUsufructo());
			stmt.setBigDecimal(24, acta.getCapitalArt46());
			stmt.setBigDecimal(25, acta.getInteresArt46());
			if (acta.getEstadoSeguimiento() != null) {
				stmt.setInt(26, acta.getEstadoSeguimiento().getId());
			} else {
				stmt.setNull(26, Types.INTEGER);
			}
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar acta", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateActaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar empresa", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public Acta get(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_acta_no_os(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Acta acta = Acta.getMappingNoOS(rs, "act__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					acta.setEmpresa(emp);
				}
				return acta;
			}
		} catch (Exception e) {
			_log.error("Error al traer inspectores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public List<Acta> getActas(String entidad, String nro, String cuit, String empresa, String estado, Connection connectionParameter) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Acta> actas = null;
		try {
			String sql = "{call buscar_actas_no_os(?,?,?,?,?)}";
			
			if(null==connectionParameter){
				con = ConnectionHelper.getConnection();	
			}else{
				con= connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, entidad);
			stmt.setString(2, nro);
			stmt.setString(3, cuit);
			stmt.setString(4, cuit!=null&&cuit.trim().length()>0?null:empresa);
			stmt.setString(5, estado);

			ResultSet rs = stmt.executeQuery();
			actas = new ArrayList<Acta>();
			while (rs.next()) {
				Acta acta = Acta.getMappingNoOS(rs, "act__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					acta.setEmpresa(emp);
				}
				Date periIni = rs.getDate("actperi__periodo_inicial");
				Date periFin = rs.getDate("actperi__periodo_final");
				acta.setPeriodoInicial(periIni);
				acta.setPeriodoFinal(periFin);
				actas.add(acta);
			}
		} catch (Exception e) {
			_log.error("Error al traer inspectores", e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return actas;
	}

	public void saveInspectorFirmante(int actaId, int inspectorId,
			Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call inserta_acta_no_os_inspector_firmante(?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaId);
			stmt.setInt(2, inspectorId);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al grabar inspector", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void borrarInspectorFirmante(int actaId, int inspectorId,
			Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_acta_no_os_inspector_firmante(?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaId);
			stmt.setInt(2, inspectorId);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al borrar acta-inspector", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void saveDetalleActa(Acta acta, DetalleActaInspectores detalle,
			Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call inserta_detalle_acta(?, ?, ?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, acta.getId());
			stmt.setDate(2, new java.sql.Date(detalle.getDesde().getTime()));
			stmt.setDate(3, new java.sql.Date(detalle.getHasta().getTime()));
			stmt.setBigDecimal(4, detalle.getCapital());
			stmt.setBigDecimal(5, detalle.getInteres());
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al grabar detalle acta", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<Inspector> getInspectoresFirmantes(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Inspector> inspectores = null;
		try {
			String sql = "{call buscar_acta_no_os_inspector_firmante(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			inspectores = new ArrayList<Inspector>();
			while (rs.next()) {
				Inspector inspector = Inspector.getMapping(rs);
				inspectores.add(inspector);
			}
		} catch (Exception e) {
			_log.error("Error al traer inspectores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return inspectores;
	}

	public List<DetalleActaInspectores> getDetallesActas(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<DetalleActaInspectores> detalles = null;
		try {
			String sql = "{call buscar_detalle_actas(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			detalles = new ArrayList<DetalleActaInspectores>();
			while (rs.next()) {
				DetalleActaInspectores detalle = DetalleActaInspectores
						.getMapping(rs);
				detalles.add(detalle);
			}
		} catch (Exception e) {
			_log.error("Error al traer detalle actas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return detalles;
	}

	public void borrar(int id, Date fechaBaja, String usr,
			Connection connectionParameter)
			throws ImposibleBorrarActaException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borra_acta_no_os(?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setDate(2, new java.sql.Date(fechaBaja.getTime()));
			stmt.setString(3, usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarActaException();
				}
			}

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (ImposibleBorrarActaException e) {
			_log.error("Error al borrar acta", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
	public void pasarACalculo(int id, Date fechaBaja, String usr,
			Connection connectionParameter)
			throws ImposibleBorrarActaException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call pasar_acta_no_os_a_calculo(?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setDate(2, new java.sql.Date(fechaBaja.getTime()));
			stmt.setString(3, usr);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarActaException();
				}
			}

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (ImposibleBorrarActaException e) {
			_log.error("Error al borrar acta", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public boolean isActaRelacionada(int actaId, Connection connectionParameter)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean relacionada = false;
		try {
			String sql = "{call acta_no_os_relacionada(?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 1) {
					relacionada = true;
				}
			}
		} catch (Exception e) {
			_log.error("Error acta_relacionada", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return relacionada;
	}

	public boolean isActaPagada(int actaId) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean paga = false;
		try {
			String sql = "{call verificar_acta_paga(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 1) {
					paga = true;
				}
			}
		} catch (Exception e) {
			_log.error("Error verificar_acta_paga", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return paga;
	}

	public List<ActaRelacionada> getActasRelacionadas(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActaRelacionada> actas = null;
		try {
			String sql = "{call buscar_actas_no_os_relacionadas(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			actas = new ArrayList<ActaRelacionada>();
			while (rs.next()) {
				ActaRelacionada actaR = ActaRelacionada.getMapping(rs, "AR__");
				actaR.getActaRelacionada().setNumero(
						rs.getString("A__acta_relacionada_nro"));
				actaR.getActaRelacionada().setFechaPago(
						rs.getDate("A__acta_relacionada_fecha_pago"));
				actas.add(actaR);
			}
		} catch (Exception e) {
			_log.error("Error al traer inspectores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return actas;
	}

	public void saveActaRelacionada(ActaRelacionada actaRel, String usr,
			Connection connectionParameter) throws SQLException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call inserta_acta_no_os_relacionada (?, ?, ?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaRel.getActa().getId());
			stmt.setInt(2, actaRel.getActaRelacionada().getId());
			stmt.setBigDecimal(3, actaRel.getImporte());
			stmt.setBigDecimal(4, actaRel.getSaldo());
			stmt.setString(5, usr);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar acta relacionada", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}

	}

	public void deleteActaRelacionada(ActaRelacionada actaRel,
			Connection connectionParameter) throws SQLException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_acta_no_os_relacionada (?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaRel.getId());
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al borrar acta relacionada", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}

	}

	public void updateActaRelacionada(ActaRelacionada actaRel, String usr,
			Connection connectionParameter) throws SQLException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_acta_no_os_relacionada (?, ?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaRel.getId());
			stmt.setBigDecimal(2, actaRel.getImporte());
			stmt.setBigDecimal(3, actaRel.getSaldo());
			stmt.setString(4, usr);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al modificar acta relacionada", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void deleteDetalleActa(DetalleActaInspectores detalle,
			Connection connectionParameter) throws SQLException,
			SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_detalle_acta_no_os(?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, detalle.getId());
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al borrar detalle acta", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}

		}
	}

	public List<ActaPeriodoDeudaEmpresa> getPeriodosActas(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActaPeriodoDeudaEmpresa> periodos = null;
		try {
			String sql = "{call buscar_actas_no_os_periodos(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			periodos = new ArrayList<ActaPeriodoDeudaEmpresa>();
			while (rs.next()) {
				ActaPeriodoDeudaEmpresa actaR = ActaPeriodoDeudaEmpresa
						.getMappingEmpleadores(rs);
				
				int indexOf = periodos.indexOf(actaR);
				if (indexOf == -1) {
					periodos.add(actaR);
				} else {
					periodos.get(indexOf).getDetalle()
							.addAll(actaR.getDetalle());
				}
			}
		} catch (Exception e) {
			_log.error("Error al traer periodos de acta", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return periodos;
	}

	public void saveActaPeriodo(Acta acta, ActaPeriodoDeudaEmpresa peri,
			String usr, int tipo_aporte, Connection connectionParameter) throws SQLException,
			SystemException {
		for (ActaPeriodoDeudaEmpresa.Detalle det : peri.getDetalle()) {
			Connection con = null;
			CallableStatement stmt = null;
			try {
				String sql = "{call inserta_acta_no_os_periodo (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
				if (connectionParameter == null) {
					con = ConnectionHelper.getConnection();
					con.setAutoCommit(false);
				} else {
					con = connectionParameter;
				}
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, acta.getId());
				stmt.setDate(2, new java.sql.Date(peri.getPeriodo().getTime()));
				stmt.setString(3, peri.getCuil().replace("-", ""));
				stmt.setBigDecimal(4, peri.getRemuneracionDeclarada());
				stmt.setBigDecimal(5, peri.getCalculado());
				stmt.setBigDecimal(6, det.getMontoPagado());
				if (det.getFechaPagado() != null) {
					stmt.setDate(7, new java.sql.Date(det.getFechaPagado()
							.getTime()));
				} else {
					stmt.setDate(7, null);
				}
				stmt.setBigDecimal(8, det.getCapital());
				stmt.setBigDecimal(9, det.getInteres());
				stmt.setString(10, peri.getApellido());
				stmt.setString(11, peri.getNombre());
				stmt.setString(12, usr);
				stmt.setBoolean(13, det.isAgregadoManual());
				stmt.setInt(14, det.getTipoAporte());
				stmt.setInt(15, det.getCantidadAfiliados());				
				stmt.setString(16, peri.getCamara());
				stmt.setDate(17, new java.sql.Date(peri.getFechaIngreso().getTime()));
				stmt.setBigDecimal(18, det.getInteresAFechaPagada());
				stmt.executeUpdate();
				if (connectionParameter == null) {
					con.commit();
				}
			} catch (SQLException e) {
				_log.error("Error al insertar periodo acta", e);
				throw e;
			} finally {
				if (connectionParameter == null) {
					ConnectionHelper.cerrar(stmt, con);
				}else {
					ConnectionHelper.cerrar(stmt);
				}
			}
		}
	}

	public void updateActaPeriodo(Acta acta, ActaPeriodoDeudaEmpresa peri,
			String screenName, Connection connectionParameter)
			throws SystemException, SQLException {
		for (ActaPeriodoDeudaEmpresa.Detalle det : peri.getDetalle()) {
			Connection con = null;
			CallableStatement stmt = null;
			try {
				String sql = "{call actualiza_acta_no_os_periodo (?, ?, ?)}";
				if (connectionParameter == null) {
					con = ConnectionHelper.getConnection();
					con.setAutoCommit(false);
				} else {
					con = connectionParameter;
				}
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, det.getId());
				stmt.setBigDecimal(2, det.getInteres());
				stmt.setString(3, screenName);
				stmt.executeUpdate();
				if (connectionParameter == null) {
					con.commit();
				}
			} catch (SQLException e) {
				_log.error("Error al insertar periodo acta", e);
				throw e;
			} finally {
				if (connectionParameter == null) {
					ConnectionHelper.cerrar(stmt, con);
				}else {
					ConnectionHelper.cerrar(stmt);
				}
			}
		}
	}

	public void deleteActaPeriodo(ActaPeriodoDeudaEmpresa peri,
			Connection connectionParameter) throws SQLException,
			SystemException {
		for (ActaPeriodoDeudaEmpresa.Detalle det : peri.getDetalle()) {
			if (det.isBorradoLogico()) {
				Connection con = null;
				CallableStatement stmt = null;
				try {
					String sql = "{call borrar_acta_no_os_periodo(?)}";
					if (connectionParameter == null) {
						con = ConnectionHelper.getConnection();
						con.setAutoCommit(false);
					} else {
						con = connectionParameter;
					}
					stmt = con.prepareCall(sql.toString());
					stmt.setInt(1, det.getId());
					stmt.executeUpdate();
					if (connectionParameter == null) {
						con.commit();
					}
				} catch (SQLException e) {
					_log.error("Error al borrar periodo de acta", e);
					throw e;
				} finally {
					if (connectionParameter == null) {
						ConnectionHelper.cerrar(stmt, con);
					}else {
						ConnectionHelper.cerrar(stmt);
					}
				}
			}
		}
	}

	public void cerrarActa(Acta acta, String usr, Connection connectionParameter)
			throws SQLException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call cerrar_acta_no_os(?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, acta.getId());
			stmt.setString(2, usr);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al cerrar acta", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<Acta> getDeuda(String cuit, String empresa, String entidad, Connection connectionParameter) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Acta> actas = null;
		try {
			String sql = "{call buscar_deuda_no_os(?, ?, ?)}";
			
			_log.debug(sql);
			_log.debug("cuit: " + cuit);
			_log.debug("empresa: " + empresa);
			_log.debug("connectionParameter: " + connectionParameter!=null);
			
			if(null==connectionParameter){
				con = ConnectionHelper.getConnection();	
			}else{
				con=connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, empresa);
			stmt.setString(3, entidad);

			ResultSet rs = stmt.executeQuery();
			actas = new ArrayList<Acta>();
			while (rs.next()) {
				Acta acta = Acta.getMappingNoOS(rs, "act__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					acta.setEmpresa(emp);
				}				
				
				actas.add(acta);
			}
		} catch (Exception e) {
			_log.error("Error al buscar deuda", e);
		} finally {
			if(null==connectionParameter){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return actas;
	}
	
	public List<Acta> getDeuda(String cuit, String empresa) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Acta> actas = null;
		try {
			String sql = "{call buscar_deuda_no_os(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, empresa);			

			ResultSet rs = stmt.executeQuery();
			actas = new ArrayList<Acta>();
			while (rs.next()) {
				Acta acta = Acta.getMappingNoOS(rs, "act__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					acta.setEmpresa(emp);
				}				
				
				actas.add(acta);
			}
		} catch (Exception e) {
			_log.error("Error al buscar deuda", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return actas;
	}

	public void saveActaPago(Acta acta, ActaPago p, String screenName,
			Connection connectionParameter) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call inserta_acta_no_os_pagos(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, acta.getId());
			stmt.setString(2, p.getTipo().getMapping());
			if(null!=p.getFechaPago()){
				stmt.setDate(3, new java.sql.Date(p.getFechaPago().getTime()));
			}else{
				stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			}
			stmt.setBigDecimal(4, p.getImporte());
			stmt.setString(5, screenName);
			if (p.getIngreso() instanceof Cheque) {
				stmt.setBigDecimal(6, ((Cheque) p.getIngreso()).getNumero());
				stmt.setInt(7, p.getIngreso().getBanco().getId_banco());
			} else {
				stmt.setBigDecimal(6, null);
				stmt.setNull(7, java.sql.Types.INTEGER);
			}
			if (p.getIngreso() instanceof Cheque) {
				stmt.setString(8, ActaPago.Forma.Cheque.getMapping());
			} else if (p.getIngreso() instanceof DepositoBancario) {
				stmt.setString(8, ActaPago.Forma.Deposito.getMapping());
			} else {
				stmt.setString(8, ActaPago.Forma.Efectivo.getMapping());
			}
			if (p.getIngreso() instanceof Cheque) {
				stmt.setInt(9, p.getIngreso().getCuentaBancaria().getId_cuenta_bcria());
			}else{
				stmt.setNull(9, Types.INTEGER);
			}
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al salvar pago acta no os", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void deleteActaPago(ActaPago p, String string,
			Connection connectionParameter) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_acta_no_os_pagos(?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, p.getId());
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al borrar pago acta", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<ActaPago> getPagosActas(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActaPago> pagos = null;
		try {
			String sql = "{call buscar_acta_no_os_pagos(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			pagos = new ArrayList<ActaPago>();
			while (rs.next()) {
				ActaPago pago = ActaPago.getMapping(rs, "AP__");
				Cheque cheque = Cheque.getMapping(rs, "CH__");
				cheque.setEstado(Cheque.Estado.getMapping(rs, "es__"));
				if (cheque.getNumero() != null) {
					pago.setIngreso(cheque);
				}
				pagos.add(pago);
			}
		} catch (Exception e) {
			_log.error("Error al buscar pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return pagos;
	}

	public void updateActaPago(Acta acta, ActaPago p, String screenName,
			Connection connectionParameter) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_acta_no_os_pagos(?, ?, ?, ?, ?, ?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, p.getId());
			stmt.setString(2, p.getTipo().getMapping());
			stmt.setDate(3, new java.sql.Date(p.getFechaPago().getTime()));
			stmt.setBigDecimal(4, p.getImporte());
			stmt.setString(5, screenName);
			if (p.getIngreso() instanceof Cheque) {
				stmt.setBigDecimal(6, ((Cheque) p.getIngreso()).getNumero());
				stmt.setInt(7, p.getIngreso().getBanco().getId_banco());
			} else {
				stmt.setBigDecimal(6, null);
				stmt.setNull(7, java.sql.Types.INTEGER);
			}
			if (p.getIngreso() instanceof Cheque) {
				stmt.setString(8, ActaPago.Forma.Cheque.getMapping());
			} else if (p.getIngreso() instanceof DepositoBancario) {
				stmt.setString(8, ActaPago.Forma.Deposito.getMapping());
			} else if (p.getIngreso() instanceof Efectivo) {
				stmt.setString(8, ActaPago.Forma.Efectivo.getMapping());
			}
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar pago acta", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<Acta> getActasSinRecibo(String cuit, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Acta> actas = null;
		try {
			String sql = "{call buscar_actas_no_os_sin_recibo_amtima(?,?)}";
			if(entidad==WebKeysGlobal.UOMA){
				sql="{call uoma.buscar_actas_no_os_sin_recibo(?,?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, entidad==WebKeysGlobal.AMTIMA?WebKeysGlobal.ENTIDAD_AMTIMA:WebKeysGlobal.ENTIDAD_UOMA);

			ResultSet rs = stmt.executeQuery();
			actas = new ArrayList<Acta>();
			while (rs.next()) {
				Acta acta = Acta.getMapping(rs, "act__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					acta.setEmpresa(emp);
				}
				Date periIni = rs.getDate("actperi__periodo_inicial");
				Date periFin = rs.getDate("actperi__periodo_final");
				acta.setPeriodoInicial(periIni);
				acta.setPeriodoFinal(periFin);
				actas.add(acta);
			}
		} catch (Exception e) {
			_log.error("Error al traer actas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return actas;
	}

	public List<ActaPagoIngresado> getPagosIngresados(int id, int reciboId, String entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActaPagoIngresado> pagos = null;
		try {
			String sql = "{call buscar_acta_no_os_pagos_ingresados(?)}";
			if(entidad.equals("A.M.T.I.M.A.")){
				sql = "{call buscar_acta_pagos_ingresados_amtima(?)}";
			}if(entidad.equals("U.O.M.A.")){
				sql = "{call uoma.buscar_acta_pagos_ingresados_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			pagos = new ArrayList<ActaPagoIngresado>();
			while (rs.next()) {
				int recibo_id = rs.getInt("recibo_id");
				if (reciboId == 0 || recibo_id == reciboId) {
					pagos.add(new ActaPagoIngresado(new Recibo(rs
							.getInt("recibo_id")), rs.getBigDecimal("importe"),
							rs.getDate("fecha_pagado"),
							rs.getBigDecimal("nro_cheque"), rs
									.getInt("id_banco")));
				}				
			}
		} catch (Exception e) {
			_log.error("Error al buscar pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return pagos;
	}

	public boolean isActaConRecibo(int actaId, Connection connectionParameter)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean relacionada = false;
		try {
			String sql = "{call verificar_acta_no_os_con_recibo(?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 1) {
					relacionada = true;
				}
			}
		} catch (Exception e) {
			_log.error("Error verificar_acta_con_recibo", e);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return relacionada;
	}

	public List<ReporteActaBean> reporteActas(Date fechaIni, Date fechaFin) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteActaBean> repo = null;
		try {
			String sql = "{call reporte_actas_no_os(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<ReporteActaBean>();
			while (rs.next()) {
				repo.add(ReporteActaBean.getMapping(rs,true));
			}
		} catch (Exception e) {
			_log.error("Error al reporte_actas pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}
	
	public List<ActasAcuerdos> reporteActasNoOS(String cuit, String sucu, Date fechaIni, Date fechaFin, Date fechaPago, int conSaldo) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActasAcuerdos> repo = null;
		try {
			String sql = "{call reporte_actas_no_os(?, ?, ?, ?, ?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);			
			stmt.setDate(3, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));
			stmt.setDate(5, new java.sql.Date(fechaPago.getTime()));
			stmt.setInt(6, conSaldo);			

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<ActasAcuerdos>();
			while (rs.next()) {
				repo.add(ActasAcuerdos.getMappingActas(rs));
			}
		} catch (Exception e) {
			_log.error("Error al reporte_actas pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}
	
	public List<ReporteCobranzaActaBean> reporteCobranzaActas(Date fechaIni, Date fechaFin) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteCobranzaActaBean> repo = null;
		try {
			String sql = "{call reporte_aplicacion_cobranzas_actas_no_os(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<ReporteCobranzaActaBean>();
			while (rs.next()) {
				repo.add(ReporteCobranzaActaBean.getMapping(rs));
			}
		} catch (Exception e) {
			_log.error("Error al reporte_actas pagos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}
}

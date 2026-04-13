package ar.com.ospim.tesoreria.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hsqldb.Types;

import ar.com.ospim.estudioisidro.beans.ActaAcuerdoSeguimiento;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.tesoreria.DuplicateActaIdException;
import ar.com.ospim.tesoreria.ImposibleBorrarActaException;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Acta.ActaPagoIngresado;
import ar.com.ospim.tesoreria.beans.Acta.ActaRelacionada;
import ar.com.ospim.tesoreria.beans.Acta.DetalleActaInspectores;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.CalculoDeudaMasivoCab;
import ar.com.ospim.tesoreria.beans.Inspector;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReporteActaBean;
import ar.com.ospim.tesoreria.beans.ReporteCobranzaActaBean;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ActaServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ActaServiceImpl.class);

	private static ActaServiceImpl instance = null;

	public static ActaServiceImpl getInstance() {
		if (null == instance) {
			instance = new ActaServiceImpl();
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
			String sql = "{call inserta_acta(?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, acta.getNumero());
			stmt.setString(2, acta.getEmpresa().getCuit());
			if(null!=acta.getEmpresa().getSucursal() && !acta.getEmpresa().getSucursal().trim().equals("") ){
				stmt.setString(3, acta.getEmpresa().getSucursal());
			}else{
				stmt.setString(3, "000");
			}			
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
			if (acta.getEstadoSeguimiento() != null) {
				stmt.setInt(11, acta.getEstadoSeguimiento().getId());
			} else {
				stmt.setNull(11, Types.INTEGER);
			}
			stmt.setString(12, screenName);			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar acta", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateActaIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar acta", e);
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
			String sql = "{call actualiza_acta (?,?,?,?,?,?,?,?,?,?,?,?,?)}";			
			stmt = con.prepareCall(sql.toString());
			int cont=1;
			stmt.setInt(cont++, acta.getId());
			stmt.setString(cont++, acta.getNumero());
			stmt.setString(cont++, acta.getEmpresa().getCuit());
			if(null!=acta.getEmpresa().getSucursal() && !acta.getEmpresa().getSucursal().trim().equals("") ){
				stmt.setString(cont++, acta.getEmpresa().getSucursal());
			}else{
				stmt.setString(cont++, "000");
			}
			java.sql.Date fechaIniSqlDate = new java.sql.Date(acta
					.getFechaInicio().getTime());
			stmt.setDate(cont++, fechaIniSqlDate);
			java.sql.Date fechaPagoSqlDate = new java.sql.Date(acta
					.getFechaPago().getTime());
			stmt.setDate(cont++, fechaPagoSqlDate);
			stmt.setBigDecimal(cont++, acta.getOtros());
			stmt.setBigDecimal(cont++, acta.getInteres());
			stmt.setBigDecimal(cont++, acta.getCapital());
			stmt.setBigDecimal(cont++, acta.getDeudaActasRelacionadas());
			if (acta.getCierre_fecha() != null) {
				stmt.setDate(cont++, new java.sql.Date(acta.getCierre_fecha()
						.getTime()));
			} else {
				stmt.setDate(cont++, null);
			}			
			if (acta.getEstadoSeguimiento() != null) {
				stmt.setInt(cont++, acta.getEstadoSeguimiento().getId());
			} else {
				stmt.setNull(cont++, Types.INTEGER);
			}
			stmt.setString(cont++, screenName);			
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar acta", e);
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
			String sql = "{call buscar_acta(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Acta acta = Acta.getMapping(rs, "act__");
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

	public List<Acta> getActas(String nro, String cuit, String empresa, Connection connectionParameter) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Acta> actas = null;
		try {
			String sql = "{call buscar_actas(?, ?, ?)}";
			if(null==connectionParameter){
				con = ConnectionHelper.getConnection();	
			}else{
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, nro);
			stmt.setString(2, cuit);
			stmt.setString(3, empresa);

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
				acta.setEntidad("OSPIM");
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
			String sql = "{call inserta_acta_inspector_firmante(?, ?)}";
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
			String sql = "{call borrar_acta_inspector_firmante(?, ?)}";
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
			String sql = "{call buscar_acta_inspector_firmante(?)}";
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
//		TODO armar el buscar_detalle_actas_amtima
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
			String sql = "{call borra_acta(?, ?, ?)}";
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
			String sql = "{call acta_relacionada(?)}";
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
			String sql = "{call buscar_actas_relacionadas(?)}";
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
			String sql = "{call inserta_acta_relacionada (?, ?, ?, ?, ?)}";
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
			String sql = "{call borrar_acta_relacionada (?)}";
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
			String sql = "{call actualiza_acta_relacionada (?, ?, ?, ?)}";
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
			String sql = "{call borrar_detalle_acta(?)}";
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
			String sql = "{call buscar_actas_periodos(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			periodos = new ArrayList<ActaPeriodoDeudaEmpresa>();
			while (rs.next()) {
				ActaPeriodoDeudaEmpresa actaR = ActaPeriodoDeudaEmpresa
						.getMapping(rs);
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
			String usr, Connection connectionParameter) throws SQLException,
			SystemException {
		for (ActaPeriodoDeudaEmpresa.Detalle det : peri.getDetalle()) {
			Connection con = null;
			CallableStatement stmt = null;
			try {
				String sql = "{call inserta_acta_periodo (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
				if (connectionParameter == null) {
					con = ConnectionHelper.getConnection();
					con.setAutoCommit(false);
				} else {
					con = connectionParameter;
				}
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, acta.getId());
				stmt.setDate(2, new java.sql.Date(peri.getPeriodo().getTime()));
				stmt.setString(3, peri.getCuil());
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
				String sql = "{call actualiza_acta_periodo (?, ?, ?)}";
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
					ConnectionHelper.cerrar(con);
				}
				ConnectionHelper.cerrar(stmt);
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
					String sql = "{call borrar_acta_periodo(?)}";
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
			String sql = "{call cerrar_acta(?, ?)}";
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

	public List<Acta> getDeuda(String cuit, String empresa, Connection connectionParameter) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Acta> actas = null;
		try {
			String sql = "{call buscar_deuda(?, ?)}";
			
			_log.debug(sql);
			_log.debug("cuit: " + cuit);
			_log.debug("empresa: " + empresa);
			_log.debug("connectionParameter: " + connectionParameter!=null);
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con=connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, empresa);

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
				acta.setEntidad("OSPIM");
				acta.setPeriodoInicial(periIni);
				acta.setPeriodoFinal(periFin);
				actas.add(acta);
			}
		} catch (Exception e) {
			_log.error("Error al buscar deuda", e);
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return actas;
	}

	public void saveActaPago(Acta acta, ActaPago p, String screenName,
			Connection connectionParameter) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call inserta_acta_pagos(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, acta.getId());
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
			}else if (p.getIngreso() instanceof Pagare) {
				stmt.setString(8, ActaPago.Forma.Pagare.getMapping());				
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
			_log.error("Error al salvar pago acta", e);
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
			String sql = "{call borrar_acta_pagos(?)}";
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
			String sql = "{call buscar_acta_pagos(?)}";
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
			String sql = "{call actualiza_acta_pagos(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
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
			} else{
				stmt.setBigDecimal(6, null);
				stmt.setNull(7, java.sql.Types.INTEGER);
			}
			if (p.getIngreso() instanceof Cheque) {
				stmt.setString(8, ActaPago.Forma.Cheque.getMapping());
			} else if (p.getIngreso() instanceof DepositoBancario) {
				stmt.setString(8, ActaPago.Forma.Deposito.getMapping());
			} else if (p.getIngreso() instanceof Efectivo) {
				stmt.setString(8, ActaPago.Forma.Efectivo.getMapping());
			} else if (p.getIngreso() instanceof Pagare) {
				stmt.setString(8, ActaPago.Forma.Pagare.getMapping());
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

	public List<Acta> getActasSinRecibo(String cuit) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Acta> actas = null;
		try {
			String sql = "{call buscar_actas_sin_recibo(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);

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

	public List<ActaPagoIngresado> getPagosIngresados(int id, int reciboId) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ActaPagoIngresado> pagos = null;
		try {
			String sql = "{call buscar_acta_pagos_ingresados(?)}";
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
			String sql = "{call verificar_acta_con_recibo(?)}";
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

	public List<ReporteActaBean> reporteActas(Date fechaIni, Date fechaFin, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteActaBean> repo = null;
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call reporte_actas_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.reporte_actas_uoma_abierto(?, ?)}";
			}else{
				sql = "{call reporte_actas(?, ?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<ReporteActaBean>();
			while (rs.next()) {
				//repo.add(ReporteActaBean.getMapping(rs,false));
				repo.add(ReporteActaBean.getMapping(rs,true));
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
			String sql = "{call reporte_aplicacion_cobranzas_actas(?, ?)}";
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
	
	public List<ActaAcuerdoSeguimiento> buscaActaAcuerdoSeguimiento(String cuit){
		Connection con = null;
		CallableStatement stmt = null;
		ActaAcuerdoSeguimiento repo=null;  
		List<ActaAcuerdoSeguimiento> lista=new ArrayList<ActaAcuerdoSeguimiento>();
		try {
			String sql = "{call busca_actas_acuerdos_seguimiento(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);			
			stmt.setString(2, "000");
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {				
				repo=ActaAcuerdoSeguimiento.getMapping(rs);		
				lista.add(repo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar reporte entidad", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
		
	}
	
	public void pasarACalculo(int id, Date fechaBaja, String usr,
			Connection connectionParameter)
			throws ImposibleBorrarActaException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call pasar_acta_a_calculo(?, ?, ?)}";
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
				ConnectionHelper.cerrar(con);
			}
			ConnectionHelper.cerrar(stmt);
		}
	}
	
	public boolean actualizaEstadoSeguimientoActa(int actaId, int estadoSegId, String usr)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean actualizoOk = false;
		try {
			String sql = "{call actualiza_estado_seguim_acta(?,?,?)}";

			con = ConnectionHelper.getConnection();

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaId);
			stmt.setInt(2, estadoSegId);	
			stmt.setString(3, usr);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 1) {
					actualizoOk = true;
				}
			}
		} catch (Exception e) {
			_log.error("Error cambio estado acta", e);
			throw e;
		} finally {

			ConnectionHelper.cerrar(stmt, con);
		}
		return actualizoOk;
	}
	
	public boolean actualizaEstadoSeguimientoActaNoOS(int actaId, int estadoSegId, String usr)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		boolean actualizoOk = false;
		try {
			String sql = "{call actualiza_estado_seguim_acta_no_os(?,?,?)}";

			con = ConnectionHelper.getConnection();

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, actaId);
			stmt.setInt(2, estadoSegId);	
			stmt.setString(3, usr);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 1) {
					actualizoOk = true;
				}
			}
		} catch (Exception e) {
			_log.error("Error cambio estado acta", e);
			throw e;
		} finally {

			ConnectionHelper.cerrar(stmt, con);
		}
		return actualizoOk;
	}
	
	public List<CalculoDeudaMasivoCab> traerProcesosCalculoDeudaMasivo(){
		Connection con = null;
		CallableStatement stmt = null;
		CalculoDeudaMasivoCab cab=null;  
		List<CalculoDeudaMasivoCab> lista=new ArrayList<CalculoDeudaMasivoCab>();
		try {
			String sql = "{call trae_procesos_actas_masiva() }";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {				
				cab=CalculoDeudaMasivoCab.getMapping(rs);		
				lista.add(cab);
			}
		} catch (Exception e) {
			_log.error("Error al buscar procesos de calculo masivo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
		
	}
	
}

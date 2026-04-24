package ar.com.uoma.recibos.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Efectivo.Estado;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.ReciboAnticipo;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboActa;
import ar.com.ospim.tesoreria.beans.ReciboCheque;
import ar.com.ospim.tesoreria.beans.ReciboConcepto;
import ar.com.ospim.tesoreria.beans.ReciboConcepto.ConceptoPago;
import ar.com.ospim.tesoreria.beans.ReciboConvenio;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposExcel.ReporteAnticipos;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReciboNoOSServiceImpl implements ReciboGlobalServiceImpl {
	private static Log _log = LogFactoryUtil
			.getLog(ReciboNoOSServiceImpl.class);

	private static ReciboNoOSServiceImpl instance = null;

	public static ReciboNoOSServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReciboNoOSServiceImpl();
		}
		return instance;
	}

	public void saveConcepto(Recibo recibo, Acta acta, Convenio convenio,
			Cheque chequeNoDepositado, Cheque chequeRechazado,
			Concepto otroConcepto, BigDecimal importePorCheques,
			BigDecimal importeAdicional, List<ConceptoPago> cpagos,
			BigDecimal importeRemunTotal, Date periodo,
			Integer cantidadEmpleados,Integer nroBoleta, Integer nroSecuenciaDDJJ, String userName,
			Connection connectionParameter, int entidad_i, String entidad)
			throws SystemException {

		// public void saveConcepto(Recibo recibo, Acta acta, Convenio convenio,
		// Cheque chequeNoDepositado, Cheque chequeRechazado,
		// Integer otroConceptoId, BigDecimal importePorCheques,
		// BigDecimal importeAdicional, List<ConceptoPago> cpagos,
		// String userName, Connection connectionParameter, boolean amtima)
		// throws SystemException {
		_log.debug("Guardando recibo-acta");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}

			String sql = "{call inserta_recibo_no_os_concepto (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, recibo.getId());
			if (null != entidad && !entidad.equals("O.S.P.I.M.")
					&& acta != null) {
				stmt.setInt(2, acta.getId());
			} else {
				stmt.setNull(2, java.sql.Types.INTEGER);
			}
			if (null != entidad && !entidad.equals("O.S.P.I.M.")
					&& convenio != null) {
				stmt.setInt(3, convenio.getId());
			} else {
				stmt.setNull(3, java.sql.Types.INTEGER);
			}

			if (chequeNoDepositado != null) {
				stmt.setBigDecimal(4, chequeNoDepositado.getNumero());
				stmt.setInt(5, chequeNoDepositado.getBanco().getId_banco());
			} else {
				stmt.setBigDecimal(4, null);
				stmt.setNull(5, java.sql.Types.INTEGER);
			}

			if (chequeRechazado != null) {
				stmt.setBigDecimal(6, chequeRechazado.getNumero());
				stmt.setInt(7, chequeRechazado.getBanco().getId_banco());
			} else {
				stmt.setBigDecimal(6, null);
				stmt.setNull(7, java.sql.Types.INTEGER);
			}
			if (otroConcepto != null) {
				stmt.setInt(8, otroConcepto.getId());
			} else {
				stmt.setNull(8, java.sql.Types.INTEGER);
			}
			stmt.setBigDecimal(9, importePorCheques);
			stmt.setBigDecimal(10, importeAdicional);
			stmt.setString(11, userName);

			if (null != entidad && entidad.equals("O.S.P.I.M.") && acta != null) {
				stmt.setInt(12, acta.getId());
			} else {
				stmt.setNull(12, java.sql.Types.INTEGER);
			}

			if (null != entidad && entidad.equals("O.S.P.I.M.")
					&& convenio != null) {
				stmt.setInt(13, convenio.getId());
			} else {
				stmt.setNull(13, java.sql.Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			int id = 0;
			while (rs.next()) {
				id = rs.getInt(1);
			}

			for (ConceptoPago cp : cpagos) {
				salvarConceptoPagos(id, cp, userName, con);
			}

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar recibo-acta", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	private void salvarConceptoPagos(int reciboConceptoId, ConceptoPago cp,
			String userName, Connection connectionParameter)
			throws SystemException {
		_log.debug("Guardando recibo concepto pagos");
		CallableStatement stmt = null;
		Connection con = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_recibo_no_os_concepto_pago (?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reciboConceptoId);
			stmt.setInt(2, cp.getIngreso().getId());
			stmt.setBigDecimal(3, cp.getImporte());
			stmt.setString(4, userName);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al insertar recibo conceptos pago", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public int save(Recibo recibo, boolean debaja, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		return save(recibo, debaja, user, null, connectionParameter);
	}

	public int save(Recibo recibo, boolean debaja, String user, String entidad,
			Connection connectionParameter) throws SystemException {

		_log.debug("Guardando recibo");
		CallableStatement stmt = null;
		Connection con = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_recibo_no_os (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, recibo.getNumero());
			stmt.setBigDecimal(2, recibo.getImporte());
			stmt.setString(3, recibo.getObservaciones());
			stmt.setString(4, StringUtils.checkEmpty(recibo.getEmpresa()
					.getCuit()) ? null : recibo.getEmpresa().getCuit());
			stmt.setString(5, StringUtils.checkEmpty(recibo.getEmpresa()
					.getSucursal()) ? null : recibo.getEmpresa().getSucursal());
			java.sql.Date fechaIniSqlDate = new java.sql.Date(recibo.getFecha()
					.getTime());
			if (recibo.getSeccional() != null
					&& recibo.getSeccional().getId() != 0) {
				stmt.setInt(6, recibo.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			stmt.setDate(7, fechaIniSqlDate);
			stmt.setString(8, user);
			stmt.setBoolean(9, debaja);
			stmt.setString(10, entidad);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al insertar recibo", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
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

	public int saveIngreso(Recibo recibo, Cheque cheque, DepositoBancario depo,
			Efectivo ef, ReciboAnticipo rAnticipo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-ingreso OBSOLETO ?");
//		Connection con = null;
//		CallableStatement stmt = null;
//		try {
//			if (connectionParameter == null) {
//				con = ConnectionHelper.getConnection();
//				con.setAutoCommit(false);
//			} else {
//				con = connectionParameter;
//			}
//
//			String sql = "{call inserta_recibo_no_os_ingreso (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
//			stmt = con.prepareCall(sql.toString());
//			stmt.setInt(1, recibo.getId());
//
//			if (cheque != null) {
//				stmt.setBigDecimal(2, cheque.getNumero());
//			} else {
//				stmt.setBigDecimal(2, null);
//			}
//
//			if (cheque != null) {
//				stmt.setInt(3, cheque.getBanco().getId_banco());
//			} else if (depo != null) {
//				stmt.setNull(3, java.sql.Types.INTEGER);
//			} else {
//				stmt.setNull(3, java.sql.Types.INTEGER);
//			}
//
//			if (depo != null) {
//				stmt.setString(4, depo.getNumero());
//			} else {
//				stmt.setString(4, null);
//			}
//
//			BigDecimal importe = null;
//			if (cheque != null) {
//				importe = cheque.getImporte();
//			} else if (depo != null) {
//				importe = depo.getImporte();
//			} else if (ef != null) {
//				importe = ef.getImporte();
//			} else {
//				importe = rAnticipo.getImporte();
//			}
//			stmt.setBigDecimal(5, importe);
//
//			Date fecha = null;
//			if (cheque != null) {
//				fecha = cheque.getFecha();
//			} else if (depo != null) {
//				fecha = depo.getFecha();
//			} else if (ef != null) {
//				fecha = ef.getFecha();
//			} else {
//				fecha = rAnticipo.getFecha();
//			}
//
//			stmt.setDate(6, new java.sql.Date(fecha.getTime()));
//
//			if (ef != null) {
//				stmt.setInt(7, ef.getEstado() != null ? ef.getEstado().getId()
//						: Efectivo.Estado.RECIBIDO);
//			} else if (rAnticipo != null) {
//				stmt.setInt(7, rAnticipo.getEstado().getId());
//			} else {
//				stmt.setNull(7, java.sql.Types.INTEGER);
//			}
//			stmt.setString(8, user);
//
//			if (depo != null) {
//				stmt.setInt(9, depo.getCuentaBancaria().getId_cuenta_bcria());
//			} else {
//				stmt.setNull(9, Types.INTEGER);
//			}
//
//			if (depo != null) {
//				stmt.setInt(10, depo.getTipoDeposito());
//			} else {
//				stmt.setNull(10, Types.INTEGER);
//			}
//
//			if (rAnticipo != null) {
//				stmt.setInt(11, rAnticipo.getAnticipo().getId());
//			} else {
//				stmt.setNull(11, Types.INTEGER);
//			}
//
//			ResultSet rs = stmt.executeQuery();
//			if (connectionParameter == null) {
//				con.commit();
//			}
//
//			while (rs.next()) {
//				return rs.getInt(1);
//			}
//
//		} catch (SQLException e) {
//			_log.error("Error al insertar recibo-ingreso", e);
//			if (connectionParameter == null) {
//				ConnectionHelper.rollback(con);
//			}
//			throw new SystemException(e);
//		} finally {
//			if (connectionParameter == null) {
//				ConnectionHelper.cerrar(stmt, con);
//			}else {
//				ConnectionHelper.cerrar(stmt);
//			}
//		}
		return 0;
	}

	public int save(Cheque cheque, Recibo recibo, String user,
			Connection connectionParameter, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		_log.debug("Guardando recibo-cheque");

		try {
			if (cheque.getBaja_fecha() != null) {
				ChequeServiceUtil.update(cheque, user, connectionParameter);
			} else {
				if (cheque.getAlta_fecha() == null) {
					ChequeServiceUtil.save(cheque, user, connectionParameter,
							entidad);
				} else {
					ChequeServiceUtil.cambiarEstadoCheque(cheque,
							cheque.getEstado(), user, connectionParameter,
							entidad);
				}
			}
			return saveIngreso(recibo, cheque, null, null, null, user,
					connectionParameter, entidad);
		} catch (DuplicateNumeroChequeException e) {
			_log.error("Duplicado!:", e);
		}
		return 0;
	}

	public int save(DepositoBancario depo, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-deposito");
		return saveIngreso(recibo, null, depo, null, null, user,
				connectionParameter, entidad);
	}

	public int save(Efectivo ef, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-efectivo");
		return saveIngreso(recibo, null, null, ef, null, user,
				connectionParameter, entidad);
	}

	public void save(Recibo recibo, ReciboActa rActa, String userName,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-acta");
		saveConcepto(recibo, rActa.getActa(), null, null, null, null,
				rActa.getImportePorCheques(), rActa.getImporteAdicional(),
				rActa.getPagos(), null, null, null,null,null, userName,
				connectionParameter, entidad, null);
	}

	public void save(Recibo recibo, ReciboActa rActa, String userName,
			Connection connectionParameter, int entidad_i, String entidad)
			throws SystemException {
		_log.debug("Guardando recibo-acta");
		saveConcepto(recibo, rActa.getActa(), null, null, null, null,
				rActa.getImportePorCheques(), rActa.getImporteAdicional(),
				rActa.getPagos(), null, null, null,null,null, userName,
				connectionParameter, entidad_i, entidad);
	}

	public void save(Recibo recibo, ReciboConvenio rConv, String userName,
			Connection connectionParameter, int entidad) throws SystemException {
		saveConcepto(recibo, null, rConv.getConvenio(), null, null, null,
				rConv.getImportePorCheques(), rConv.getImporteAdicional(),
				rConv.getPagos(), null, null, null,null,null, userName,
				connectionParameter, entidad, null);
	}

	public void save(Recibo recibo, ReciboConvenio rConv, String userName,
			Connection connectionParameter, int entidad_i, String entidad)
			throws SystemException {
		_log.debug("Guardando recibo-conv");
		saveConcepto(recibo, null, rConv.getConvenio(), null, null, null,
				rConv.getImportePorCheques(), rConv.getImporteAdicional(),
				rConv.getPagos(), null, null, null,null,null, userName,
				connectionParameter, entidad_i, entidad);
	}

	public void saveChequeRechazadoASustituir(ReciboCheque rCh, Recibo recibo,
			String userName, Connection connectionParameter, int entidad)
			throws SystemException {
		_log.debug("Guardando recibo-cheque no depositado a sustituir");
		saveConcepto(recibo, null, null, null, rCh.getChequeASustituir(), null,
				null, null, rCh.getPagos(), null, null, null,null,null,null, userName,
				connectionParameter, entidad);
		ChequeServiceUtil.cambiarEstadoCheque(rCh.getChequeASustituir(), rCh
				.getChequeASustituir().getEstado(), userName,
				connectionParameter, entidad);
	}

	public void saveChequeASustituir(ReciboCheque rCh, Recibo recibo,
			String userName, Connection connectionParameter, int entidad)
			throws SystemException {
		_log.debug("Guardando recibo-cheque rechazado a sustituir");
		saveConcepto(recibo, null, null, rCh.getChequeASustituir(), null, null,
				null, null, rCh.getPagos(), null, null, null,null,null,null, userName,
				connectionParameter, entidad);
		ChequeServiceUtil.cambiarEstadoCheque(rCh.getChequeASustituir(), rCh
				.getChequeASustituir().getEstado(), userName,
				connectionParameter, entidad);
	}

	public void save(ReciboOtroConcepto oc, Recibo recibo, String userName,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("insertando otros conceptos");
		saveConcepto(recibo, null, null, null, null, oc.getConcepto(), null,
				oc.getImporte(), oc.getPagos(), null, null, null, null,null,null,userName,
				connectionParameter, entidad);
	}

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			Connection connectionParameter, String cuil_titular, Integer inte,
			int entidad) throws SystemException {
		return get(reciboNroStr, cuit, empresa, null, null);
	}

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			String entidad) throws SystemException {
		return get(reciboNroStr, cuit, empresa, entidad, null);
	}

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			String entidad, String sacarRecibos, Date fechaDesde,
			Date fechaHasta) throws SystemException {
		return get(reciboNroStr, cuit, empresa, entidad, fechaDesde,
				fechaHasta, null);
	}

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			int entidad) throws SystemException {
		return get(reciboNroStr, cuit, empresa, null, null);
	}

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			String entidad, Date fechaDesde, Date fechaHasta,
			Connection connectionParameter) throws SystemException {
		_log.debug("Buscando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		List<Recibo> recibos = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			String sql = "{call buscar_recibos_no_os(?, ?, ?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, reciboNroStr);
			if (cuit != null) {
				stmt.setString(2, cuit);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			if (empresa != null) {
				stmt.setString(3, empresa);
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			if (entidad != null) {
				stmt.setString(4, entidad);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}

			if (fechaDesde != null) {
				stmt.setDate(5, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(5, Types.DATE);
			}

			if (fechaHasta != null) {
				stmt.setDate(6, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(6, Types.DATE);
			}

			ResultSet rs = stmt.executeQuery();
			recibos = new ArrayList<Recibo>();

			while (rs.next()) {
				Recibo recibo = Recibo.getMapping_no_os(rs, "rec__");

				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					recibo.setEmpresa(emp);
				}
				recibos.add(recibo);

			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return recibos;
	}

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			String entidad, Connection connectionParameter)
			throws SystemException {
		_log.debug("Buscando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		List<Recibo> recibos = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			String sql = "{call buscar_recibos_no_os(?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, reciboNroStr);
			stmt.setString(2, cuit);
			stmt.setString(3, empresa);
			stmt.setString(4, entidad);

			ResultSet rs = stmt.executeQuery();
			recibos = new ArrayList<Recibo>();
			while (rs.next()) {
				Recibo recibo = Recibo.getMapping_no_os(rs, "rec__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					recibo.setEmpresa(emp);
				}
				recibos.add(recibo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return recibos;
	}

	public Recibo get(int id, int entidad) throws SystemException {
		return get(id);
	}

	public Recibo get(int id) throws SystemException {

		_log.debug("Buscando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		Recibo recibo = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				recibo = Recibo.getMapping_no_os(rs, "rec__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					recibo.setEmpresa(emp);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return recibo;
	}

	public List<ReciboConcepto> getConceptos(int id, int entidad)
			throws SystemException {

		return getConceptos(id);
	}

	public List<ReciboConcepto> getConceptos(int id) throws SystemException {
		_log.debug("Buscando recibos-conceptos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReciboConcepto> conceptos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_conceptos(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			conceptos = new ArrayList<ReciboConcepto>();
			while (rs.next()) {
				ReciboConcepto rc = ReciboConcepto.getMapping(rs);
				conceptos.add(rc);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos-conceptos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return conceptos;
	}

	public List<ReciboIngreso> getIngresos(int reciboId, int entidad)
			throws SystemException {
		_log.debug("Buscando recibos-igresos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReciboIngreso> ingresos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_ingresos(?)}";
			if (entidad == WebKeysGlobal.ESTUDIO) {
				sql = "{call buscar_recibo_no_os_ingresos_estudio(?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reciboId);
			ResultSet rs = stmt.executeQuery();
			ingresos = new ArrayList<ReciboIngreso>();
			while (rs.next()) {
				ReciboIngreso ingreso = ReciboIngreso.getMapping(rs, "ri__",
						entidad);
				ingresos.add(ingreso);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos-igresos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ingresos;
	}

	public void anular(int reciboId, String user, Date bajaFecha, int entidad)
			throws SystemException {
		_log.debug("Anulando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call anular_recibo_no_os(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reciboId);
			stmt.setString(2, user);
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Anulando recibos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public List<ReciboIngreso> getEfectivosRecibidos(int estadoEfectivo,
			int entidad) throws SystemException {
		_log.debug("Buscando recibos-igresos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReciboIngreso> ingresos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_efectivo_estado(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, estadoEfectivo);
			ResultSet rs = stmt.executeQuery();
			ingresos = new ArrayList<ReciboIngreso>();
			while (rs.next()) {
				ReciboIngreso ingreso = ReciboIngreso.getMapping(rs, "ri__",
						entidad);
				ingresos.add(ingreso);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos-igresos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ingresos;
	}

	public void cambiarEstadoReciboEfectivo(ReciboIngreso ri, Estado estado,
			String screenName, Connection connectionParameter)
			throws SystemException {
		_log.debug("Cambiando estado recibos-igresos");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call cambiar_recibo_no_os_efectivo_estado(?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, ri.getId());
			stmt.setInt(2, estado.getId());
			stmt.setString(3, screenName);

			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al cambiar estado recibos-igresos", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<Recibo> get(Date fechaIni, Date fechaFin, Empresa empresa,
			int entidad) throws SystemException {
		_log.debug("Buscando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		List<Recibo> recibos = new ArrayList<Recibo>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_por_fechas(?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			stmt.setString(
					3,
					StringUtils.checkNotEmpty(empresa.getCuit()) ? empresa
							.getCuit() : null);
			stmt.setString(
					4,
					StringUtils.checkNotEmpty(empresa.getSucursal()) ? empresa
							.getSucursal() : null);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Recibo recibo = Recibo.getMapping(rs, "rec__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					recibo.setEmpresa(emp);
				}
				recibos.add(recibo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return recibos;
	}

	public Map<Recibo, List<ReciboConcepto>> getConceptos(Date fechaIni,
			Date fechaFin, int entidad) throws SystemException {
		_log.debug("Buscando recibos-conceptos");
		Connection con = null;
		CallableStatement stmt = null;
		Map<Recibo, List<ReciboConcepto>> recibos = new HashMap<Recibo, List<ReciboConcepto>>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_conceptos_por_fechas(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReciboConcepto rc = ReciboConcepto.getMapping(rs);
				Recibo rec = new Recibo(rs.getInt("recibo_id"));
				List<ReciboConcepto> rcs = recibos.get(rec);
				if (rcs == null) {
					rcs = new ArrayList<ReciboConcepto>();
				}
				rcs.add(rc);
				recibos.put(rec, rcs);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos-conceptos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return recibos;
	}

	public List<Recibo> getIngresos(Date fechaIni, Date fechaFin, int entidad)
			throws SystemException {
		_log.debug("Buscando recibos-igresos");
		Connection con = null;
		CallableStatement stmt = null;
		List<Recibo> recibos = new ArrayList<Recibo>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_ingresos_por_fechas(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReciboIngreso ingreso = ReciboIngreso.getMapping(rs, "ri__",
						entidad);
				Recibo rec = new Recibo(rs.getInt("ri__recibo_id"));
				int indexOf = recibos.indexOf(rec);
				if (indexOf == -1) {
					recibos.add(rec);
				} else {
					rec = recibos.get(indexOf);
				}

				List<ReciboIngreso> ingresos = rec.getIngresos();
				if (ingresos == null) {
					ingresos = new ArrayList<ReciboIngreso>();
					rec.setIngresos(ingresos);
				}
				ingresos.add(ingreso);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos-igresos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return recibos;
	}

	public List<ReciboIngreso> getAnticiposParaAplicar(Empresa empresa,
			String entidad) throws SystemException {
		_log.debug("Buscando recibos-anticipos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReciboIngreso> recibos = new ArrayList<ReciboIngreso>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_anticipos_para_a_aplicar(?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, empresa.getCuit());
			stmt.setString(2, empresa.getSucursal());
			stmt.setString(3, entidad);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReciboAnticipo ra = ReciboAnticipo.getMapping(rs);
				ReciboIngreso ri = new ReciboIngreso();
				ri.setIngreso(ra);
				ra.getReciboAnticipo().setEmpresa(empresa);
				recibos.add(ri);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos-anticipos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return recibos;
	}

	public List<ReciboIngreso> getAnticiposParaAplicar(Empresa empresa,
			int entidad) throws SystemException {
		_log.debug("Buscando recibos-anticipos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReciboIngreso> recibos = new ArrayList<ReciboIngreso>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_no_os_anticipos_para_a_aplicar(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, empresa.getCuit());
			stmt.setString(2, empresa.getSucursal());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReciboAnticipo ra = ReciboAnticipo.getMapping(rs);
				ReciboIngreso ri = new ReciboIngreso();
				ri.setIngreso(ra);
				ra.getReciboAnticipo().setEmpresa(empresa);
				recibos.add(ri);
			}
		} catch (Exception e) {
			_log.error("Error al buscar recibos-anticipos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return recibos;
	}

	public int save(ReciboAnticipo reciboAnticipo, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-anticipo");
		return saveIngreso(recibo, null, null, null, reciboAnticipo, user,
				connectionParameter, entidad);
	}

	public List<ReporteAnticipos> getReporteAnticipos(Date fechaIni,
			Date fechaFin, Empresa empresa) throws SystemException {
		_log.debug("Buscando reporte-anticipos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAnticipos> recibos = new ArrayList<ReporteAnticipos>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call reporte_no_os_anticipos(?, ?, ?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			stmt.setString(
					3,
					StringUtils.checkNotEmpty(empresa.getCuit()) ? empresa
							.getCuit() : null);
			stmt.setString(
					4,
					StringUtils.checkNotEmpty(empresa.getSucursal()) ? empresa
							.getSucursal() : null);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReporteAnticipos ra = ReporteAnticipos.getMapping(rs);
				recibos.add(ra);
			}
		} catch (Exception e) {
			_log.error("Error al buscar reporte-anticipos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return recibos;
	}

	public boolean verificarReciboDerivado(int reciboId, int entidad)
			throws SystemException {
		Connection con = ConnectionHelper.getConnection();
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_recibo_no_os_derivado(?)}";
			_log.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reciboId);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_recibo_derivado ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return true;
	}

	@Override
	public void cambiarEstadoReciboEfectivo(ReciboIngreso ri, Estado estado,
			String screenName, Connection connectionParameter, int entidad)
			throws SystemException {
		cambiarEstadoReciboEfectivo(ri, estado, screenName,
				connectionParameter, entidad);

	}

	@Override
	public int save(Pagare pagare, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void saveConcepto(Recibo recibo, Acta acta, Convenio convenio,
			Cheque chequeNoDepositado, Cheque chequeRechazado,
			Concepto concepto, BigDecimal importePorCheques,
			BigDecimal importeAdicional, List<ConceptoPago> cpagos,
			BigDecimal importeRemunTotal, Date periodo,
			Integer cantidadEmpleados,Integer nroBoleta, Integer nroSecuenciaDDJJ,
			BigDecimal totalBoleta,
			String userName,
			Connection connectionParameter, int entidad) throws SystemException {
		saveConcepto(recibo, acta, convenio, chequeNoDepositado,
				chequeRechazado, concepto, importePorCheques, importeAdicional,
				cpagos, importeRemunTotal, periodo, cantidadEmpleados,nroBoleta,nroSecuenciaDDJJ,
				userName, connectionParameter, entidad, null);

	}

	@Override
	public int saveIngreso(Recibo recibo, Cheque cheque, DepositoBancario depo, Efectivo ef, ReciboAnticipo rAnticipo,
			TarjetaDebitoCredito tarjeta, String user, Connection connectionParameter, int entidad)
			throws SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(TarjetaDebitoCredito depo, Recibo recibo, String user, Connection connectionParameter, int entidad)
			throws SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	
}

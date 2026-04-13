package ar.com.ospim.tesoreria.service;

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

import ar.com.ospim.afiliados.beans.Afiliado;
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
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.hoteles.beans.Prestamo;
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
import ar.com.ospim.tesoreria.beans.ReciboPrestamo;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposExcel.ReporteAnticipos;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReciboServiceImpl implements ReciboGlobalServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(ReciboServiceImpl.class);

	private static ReciboServiceImpl instance = null;

	public static ReciboServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReciboServiceImpl();
		}
		return instance;
	}

	public void saveConcepto(Recibo recibo, Acta acta, Convenio convenio,
			Cheque chequeNoDepositado, Cheque chequeRechazado,
			Concepto otroConcepto, BigDecimal importePorCheques,
			BigDecimal importeAdicional, List<ConceptoPago> cpagos,
			BigDecimal importeRemunTotal, Date periodo,
			Integer cantidadEmpleados, Integer nroBoleta,Integer nroSecuenciaDDJJ,
			BigDecimal totalBoleta, String userName,
			Connection connectionParameter, int entidad) throws SystemException {

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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call inserta_recibo_concepto_amtima (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call inserta_recibo_concepto (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.inserta_recibo_concepto_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)}";
			}
			int cont = 0;
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(++cont, recibo.getId());
			if (acta != null) {
				stmt.setInt(++cont, acta.getId());
			} else {
				stmt.setNull(++cont, java.sql.Types.INTEGER);
			}
			if (convenio != null) {
				stmt.setInt(++cont, convenio.getId());
			} else {
				stmt.setNull(++cont, java.sql.Types.INTEGER);
			}

			if (chequeNoDepositado != null) {
				stmt.setBigDecimal(++cont, chequeNoDepositado.getNumero());
				stmt.setInt(++cont, chequeNoDepositado.getBanco().getId_banco());
			} else {
				stmt.setBigDecimal(++cont, null);
				stmt.setNull(++cont, java.sql.Types.INTEGER);
			}

			if (chequeRechazado != null) {
				stmt.setBigDecimal(++cont, chequeRechazado.getNumero());
				stmt.setInt(++cont, chequeRechazado.getBanco().getId_banco());
			} else {
				stmt.setBigDecimal(++cont, null);
				stmt.setNull(++cont, java.sql.Types.INTEGER);
			}
			if (otroConcepto != null) {
				stmt.setInt(++cont, otroConcepto.getId());
			} else {
				stmt.setNull(++cont, java.sql.Types.INTEGER);
			}
			// SI ES CONVENIO PUEDE PAGARSE PARCIALMENTE...
			if (convenio != null) {
				stmt.setBigDecimal(++cont, recibo.getImporte());
			} else {
				stmt.setBigDecimal(++cont, importePorCheques);
			}

			stmt.setBigDecimal(++cont, importeAdicional);

			stmt.setString(++cont, userName);
			if (entidad != WebKeysGlobal.OSPIM) {
				if (importeRemunTotal != null) {
					stmt.setBigDecimal(++cont, importeRemunTotal);
				} else {
					stmt.setNull(++cont, java.sql.Types.INTEGER);
				}
				if (periodo != null) {
					stmt.setDate(++cont, new java.sql.Date(periodo.getTime()));
				} else {
					stmt.setNull(++cont, java.sql.Types.DATE);
				}
				if (cantidadEmpleados != null) {
					stmt.setInt(++cont, cantidadEmpleados);
				} else {
					stmt.setNull(++cont, java.sql.Types.INTEGER);
				}
			}
			if (null !=otroConcepto && null!=otroConcepto.getAnticipo()
					&& otroConcepto.getAnticipo().getAnticipo()
							.getTipoComprobante() != null) {
				stmt.setString(++cont, otroConcepto.getAnticipo().getAnticipo()
						.getTipoComprobante());
				stmt.setString(++cont, otroConcepto.getAnticipoComproNro());
				stmt.setString(++cont, otroConcepto.getAnticipo().getAnticipo()
						.getCuit());
				stmt.setInt(++cont, otroConcepto.getAnticipo().getNroCuota());
				stmt.setInt(++cont, otroConcepto.getAnticipo().getAnticipo()
						.getSeccional().getId());

			} else if (entidad == WebKeysGlobal.UOMA) {
				stmt.setNull(++cont, Types.VARCHAR);
				stmt.setNull(++cont, Types.VARCHAR);
				stmt.setNull(++cont, Types.VARCHAR);
				stmt.setNull(++cont, Types.INTEGER);
				stmt.setNull(++cont, Types.INTEGER);
			}
			if (entidad == WebKeysGlobal.UOMA || entidad==WebKeysGlobal.AMTIMA) {
				if (nroBoleta != null) {
					stmt.setInt(++cont, nroBoleta);
					stmt.setInt(++cont, nroSecuenciaDDJJ);
					stmt.setBigDecimal(++cont, totalBoleta);
				} else {
					stmt.setNull(++cont, java.sql.Types.INTEGER);
					stmt.setNull(++cont, java.sql.Types.INTEGER);
					stmt.setNull(++cont, java.sql.Types.INTEGER);
				}
			}
			
			ResultSet rs = stmt.executeQuery();
			int id = 0;
			while (rs.next()) {
				id = rs.getInt(1);
			}

			for (ConceptoPago cp : cpagos) {
				salvarConceptoPagos(id, cp, userName, con, entidad);
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
			String userName, Connection connectionParameter, int entidad)
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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call inserta_recibo_concepto_pago_amtima (?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call inserta_recibo_concepto_pago (?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.inserta_recibo_concepto_pago_uoma (?, ?, ?, ?)}";
			}

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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call inserta_recibo_amtima (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call inserta_recibo (?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.inserta_recibo_uoma (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, recibo.getNumero());
			stmt.setBigDecimal(2, recibo.getImporte());
			stmt.setString(3, recibo.getObservaciones());
			if (recibo.getEmpresa() != null) {
				stmt.setString(4, StringUtils.checkEmpty(recibo.getEmpresa()
						.getCuit()) ? null : recibo.getEmpresa().getCuit());
				stmt.setString(5, StringUtils.checkEmpty(recibo.getEmpresa()
						.getSucursal()) ? null : recibo.getEmpresa()
						.getSucursal());
			} else {
				stmt.setNull(4, Types.NULL);
				stmt.setNull(5, Types.NULL);
			}
			if (recibo.getSeccional() != null
					&& recibo.getSeccional().getId() != 0) {
				stmt.setInt(6, recibo.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			java.sql.Date fechaIniSqlDate = new java.sql.Date(recibo.getFecha()
					.getTime());
			stmt.setDate(7, fechaIniSqlDate);
			stmt.setString(8, user);
			stmt.setBoolean(9, debaja);
			if (entidad != WebKeysGlobal.OSPIM) {
				if (recibo.getAfiliado() != null) {
					stmt.setString(10, recibo.getAfiliado().getCuil_titular());
					stmt.setInt(11, recibo.getAfiliado().getInte());
				} else {
					stmt.setNull(10, Types.NULL);
					stmt.setNull(11, Types.INTEGER);
				}
			}
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

	public int update(Recibo recibo, boolean debaja, String user,
			Connection connectionParameter, int entidad) throws SystemException {

		_log.debug("Guardando recibo");
		CallableStatement stmt = null;
		Connection con = null;
		int cont = 1;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call update_recibo_amtima (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call update_recibo (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.update_recibo_uoma (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(cont++, recibo.getNumero());
			stmt.setBigDecimal(cont++, recibo.getImporte());
			stmt.setString(cont++, recibo.getObservaciones());
			if (recibo.getEmpresa() != null) {
				stmt.setString(cont++, StringUtils.checkEmpty(recibo
						.getEmpresa().getCuit()) ? null : recibo.getEmpresa()
						.getCuit());
				stmt.setString(cont++, StringUtils.checkEmpty(recibo
						.getEmpresa().getSucursal()) ? null : recibo
						.getEmpresa().getSucursal());
			} else {
				stmt.setNull(cont++, Types.NULL);
				stmt.setNull(cont++, Types.NULL);
			}
			if (recibo.getSeccional() != null
					&& recibo.getSeccional().getId() != 0) {
				stmt.setInt(cont++, recibo.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(cont++, Types.INTEGER);
			}
			java.sql.Date fechaIniSqlDate = new java.sql.Date(recibo.getFecha()
					.getTime());
			stmt.setDate(cont++, fechaIniSqlDate);
			stmt.setString(cont++, user);
			stmt.setBoolean(cont++, debaja);
			if (entidad != WebKeysGlobal.OSPIM) {
				if (recibo.getAfiliado() != null) {
					stmt.setString(cont++, recibo.getAfiliado()
							.getCuil_titular());
					stmt.setInt(cont++, recibo.getAfiliado().getInte());
				} else {
					stmt.setNull(cont++, Types.NULL);
					stmt.setNull(cont++, Types.INTEGER);
				}
			}
			stmt.setInt(cont++, recibo.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al insertar recibo a", e);
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

	public int borrarIngresos(int idRecibo, Connection connectionParameter,
			int entidad) throws SystemException {

		_log.debug("Guardando recibo");
		CallableStatement stmt = null;
		Connection con = null;
		int cont = 1;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call borra_ingresos_recibo_amtima (?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call borra_ingresos_recibo (?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.borra_ingresos_recibo_uoma (?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(cont++, idRecibo);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al borrar recibos", e);
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

	public int borrarConceptos(int idRecibo, Connection connectionParameter,
			int entidad) throws SystemException {

		_log.debug("Guardando recibo");
		CallableStatement stmt = null;
		Connection con = null;
		int cont = 1;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call borra_conceptos_recibo_amtima (?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call borra_conceptos_recibo (?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.borra_conceptos_recibo_uoma (?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(cont++, idRecibo);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al borrar conceptos recibos", e);
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
			Efectivo ef, ReciboAnticipo rAnticipo, TarjetaDebitoCredito tarjeta,String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-ingreso");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call inserta_recibo_ingreso_amtima (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call inserta_recibo_ingreso (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.inserta_recibo_ingreso_uoma (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, recibo.getId());

			if (cheque != null) {
				stmt.setBigDecimal(2, cheque.getNumero());
			} else {
				stmt.setBigDecimal(2, null);
			}

			if (cheque != null) {
				stmt.setInt(3, cheque.getBanco().getId_banco());
			} else if (depo != null) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt.setNull(3, java.sql.Types.INTEGER);
			}

			if (depo != null) {
				stmt.setString(4, depo.getNumero());
			} else {
				stmt.setString(4, null);
			}

			BigDecimal importe = null;
			if (cheque != null) {
				importe = cheque.getImporte();
			} else if (depo != null) {
				importe = depo.getImporte();
			} else if (ef != null) {
				importe = ef.getImporte();
			} else {
				importe = rAnticipo.getImporte();
			}
			stmt.setBigDecimal(5, importe);

			Date fecha = null;
			if (cheque != null) {
				fecha = cheque.getFecha();
			} else if (depo != null) {
				fecha = depo.getFecha();
			} else if (ef != null) {
				fecha = ef.getFecha();
			} else {
				fecha = rAnticipo.getFecha();
			}

			stmt.setDate(6, new java.sql.Date(fecha.getTime()));

			if (ef != null) {
				stmt.setInt(7, ef.getEstado().getId());
			} else if (rAnticipo != null) {
				stmt.setInt(7, rAnticipo.getEstado().getId());
			} else {
				stmt.setNull(7, java.sql.Types.INTEGER);
			}
			stmt.setString(8, user);

			if (depo != null) {
				stmt.setInt(9, depo.getCuentaBancaria().getId_cuenta_bcria());
			} else {
				stmt.setNull(9, Types.INTEGER);
			}

			if (depo != null) {
				stmt.setInt(10, depo.getTipoDeposito());
			} else {
				stmt.setNull(10, Types.INTEGER);
			}

			if (rAnticipo != null) {
				stmt.setInt(11, rAnticipo.getAnticipo().getId());
			} else {
				stmt.setNull(11, Types.INTEGER);
			}

			if (entidad == WebKeysGlobal.OSPIM || entidad == WebKeysGlobal.AMTIMA) {
				if(cheque!= null && cheque.getCuentaBancaria()!=null){
					stmt.setInt(12, cheque.getCuentaBancaria().getId_cuenta_bcria());
				}else{
					stmt.setNull(12, Types.INTEGER);
				}
			}else if (entidad == WebKeysGlobal.UOMA && depo != null) {
				stmt.setInt(12, depo.getSucuNacion());
			}else{
				stmt.setNull(12, Types.INTEGER);
			}
			
			if (entidad == WebKeysGlobal.UOMA) {
				if(cheque!= null && cheque.getCuentaBancaria()!=null){
					stmt.setInt(13, cheque.getCuentaBancaria().getId_cuenta_bcria());
				}else{
					stmt.setNull(13, Types.INTEGER);
				}
			} 
			

			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}

			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			_log.error("Error al insertar recibo-ingreso", e);
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
			return saveIngreso(recibo, cheque, null, null, null,null, user,
					connectionParameter, entidad);
		} catch (DuplicateNumeroChequeException e) {
			_log.error("Duplicado!:", e);
			throw e;
		}		
	}

	// Implementar Pagaré
	public int save(Pagare pagare, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		// TODO Auto-generated method stub
		return saveIngreso(pagare, recibo, user, connectionParameter, entidad);
	}

	public int saveIngreso(Pagare pagare, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-ingreso");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call inserta_recibo_ingreso_amtima (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.inserta_recibo_ingreso_uoma (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				sql = "{call inserta_recibo_ingreso(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, recibo.getId());

			stmt.setBigDecimal(2, null);

			stmt.setNull(3, java.sql.Types.INTEGER);

			stmt.setString(4, null);

			BigDecimal importe = null;
			if (pagare != null) {
				importe = pagare.getImporte();
			}

			stmt.setBigDecimal(5, importe);

			Date fecha = null;
			if (pagare != null) {
				fecha = pagare.getFecha();
			}

			stmt.setDate(6, new java.sql.Date(fecha.getTime()));

			stmt.setNull(7, java.sql.Types.INTEGER);

			stmt.setString(8, user);

			stmt.setNull(9, Types.INTEGER);

			stmt.setNull(10, Types.INTEGER);

			stmt.setNull(11, Types.INTEGER);

			if (null != pagare) {
				stmt.setBigDecimal(12, pagare.getNumero());
			} else {
				stmt.setBigDecimal(12, null);
			}

//			FIXME: ME FALTA REVISAR COMO EN LA FUNCION: 
//			saveIngreso(Recibo recibo, Cheque cheque, DepositoBancario depo,
//					Efectivo ef, ReciboAnticipo rAnticipo, String user,
//					Connection connectionParameter, int entidad)
//			los parametros 12 y 13 ?
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}

			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			_log.error("Error al insertar recibo-ingreso", e);
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

	public int save(DepositoBancario depo, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-deposito");
		return saveIngreso(recibo, null, depo, null, null, null,user,
				connectionParameter, entidad);
	}

	public int save(Efectivo ef, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-efectivo");
		return saveIngreso(recibo, null, null, ef, null,null, user,
				connectionParameter, entidad);
	}

	public void save(Recibo recibo, ReciboActa rActa, String userName,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-acta");
		saveConcepto(recibo, rActa.getActa(), null, null, null, null,
				rActa.getImportePorCheques(), rActa.getImporteAdicional(),
				rActa.getPagos(), null, null, null,null, null,null,userName,
				connectionParameter, entidad);
	}

	public void save(Recibo recibo, ReciboConvenio rConv, String userName,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-conv");
		saveConcepto(recibo, null, rConv.getConvenio(), null, null, null,
				rConv.getImportePorCheques(), rConv.getImporteAdicional(),
				rConv.getPagos(), null, null, null,null,null,null, userName,
				connectionParameter, entidad);
	}

	public void saveChequeRechazadoASustituir(ReciboCheque rCh, Recibo recibo,
			String userName, Connection connectionParameter, int entidad)
			throws SystemException {
		_log.debug("Guardando recibo-cheque no depositado a sustituir");
		saveConcepto(recibo, null, null, null, rCh.getChequeASustituir(), null,
				null, null, rCh.getPagos(), null, null, null,null, null,null,userName,
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
		
		if (oc.getConcepto().getId() == Concepto.DEVOLUCION_ANTICIPO) {
			oc.getConcepto().getAnticipo().getAnticipo()
					.setImporteComprobante(oc.getImporte());
			ComprobanteServiceUtil
					.actualizaSaldoAnticipoRecibo(recibo.getId(), oc
							.getConcepto().getAnticipo().getAnticipo(),
							userName, entidad);
			oc.getConcepto()
					.getAnticipo()
					.getAnticipo()
					.setNroAnticipo(
							oc.getConcepto().getAnticipo().getAnticipo()
									.getNroAnticipo());
		}
		saveConcepto(recibo, null, null, null, null, oc.getConcepto(), null,
				oc.getImporte(), oc.getPagos(), oc.getRemuneracionTotal(),
				oc.getPeriodo(), oc.getCantidadEmpleados(),oc.getBoletaNro(),oc.getNroSecuenciaDDJJ(),
				oc.getTotalBoleta(),userName,connectionParameter, entidad);
	}

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			String cuil_titular, Integer inte, int entidad)
			throws SystemException {
		return get(reciboNroStr, cuit, empresa, null, cuil_titular, inte,
				entidad);
	}
	
	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			String cuil_titular, Integer inte, int entidad,Integer id_amtima)
			throws SystemException {
		return get(reciboNroStr, cuit, empresa, null, cuil_titular, inte,
				entidad,id_amtima);
	}
	

	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			Connection connectionParameter, String cuil_titular, Integer inte,
			int entidad) throws SystemException {
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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibos_amtima(?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibos(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibos_uoma(?, ?, ?, ?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, reciboNroStr);
			stmt.setString(2, cuit);
			stmt.setString(3, empresa);
			if (entidad != WebKeysGlobal.OSPIM) {
				stmt.setString(4, cuil_titular);
				if (inte != null)
					stmt.setInt(5, inte);
				else
					stmt.setNull(5, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			recibos = new ArrayList<Recibo>();
			while (rs.next()) {
				Recibo recibo = Recibo.getMapping(rs, "rec__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					recibo.setEmpresa(emp);
				}
				if (entidad != WebKeysGlobal.OSPIM) {
					if (rs.getString("a__cuil_titular") != null) {
						Afiliado afi = Afiliado.getMappingDatosBasicos(rs,
								"a__");
						recibo.setAfiliado(afi);
					}					
				} 
				recibo.setEntidad(entidad==WebKeysGlobal.OSPIM?"OSPIM":entidad==WebKeysGlobal.UOMA?"UOMA":"AMTIMA");

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
			Connection connectionParameter, String cuil_titular, Integer inte,
			int entidad,Integer id_amtima) throws SystemException {
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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibos_amtima(?, ?, ?, ?, ? ,?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibos(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibos_uoma(?, ?, ?, ?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, reciboNroStr);
			stmt.setString(2, cuit);
			stmt.setString(3, empresa);
			if (entidad != WebKeysGlobal.OSPIM) {
				stmt.setString(4, cuil_titular);
				if (inte != null)
					stmt.setInt(5, inte);
				else
					stmt.setNull(5, Types.INTEGER);
			}
			
			if (entidad == WebKeysGlobal.AMTIMA) {
				if (id_amtima != null)
					stmt.setInt(6, id_amtima);
				else
					stmt.setNull(6, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			recibos = new ArrayList<Recibo>();
			while (rs.next()) {
				Recibo recibo = Recibo.getMapping(rs, "rec__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					recibo.setEmpresa(emp);
				}
				if (entidad != WebKeysGlobal.OSPIM) {
					if (rs.getString("a__cuil_titular") != null) {
						Afiliado afi = Afiliado.getMappingDatosBasicos(rs,
								"a__");
						recibo.setAfiliado(afi);
					}					
				} 
				recibo.setEntidad(entidad==WebKeysGlobal.OSPIM?"OSPIM":entidad==WebKeysGlobal.UOMA?"UOMA":"AMTIMA");

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
		_log.debug("Buscando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		Recibo recibo = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_amtima(?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_uoma(?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				recibo = Recibo.getMapping(rs, "rec__");
				if (rs.getString("emp__cuit") != null) {
					Empresa emp = Empresa.getMapping(rs, "emp__");
					recibo.setEmpresa(emp);
				}

				if (rs.getString("a__cuil_titular") != null) {
					Afiliado afi = Afiliado.getMappingDatosBasicos(rs, "a__");
					recibo.setAfiliado(afi);
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
		_log.debug("Buscando recibos-conceptos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReciboConcepto> conceptos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_recibo_conceptos(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_conceptos_amtima(?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo_conceptos(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_conceptos_uoma(?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			conceptos = new ArrayList<ReciboConcepto>();
			while (rs.next()) {
				ReciboConcepto rc = ReciboConcepto.getMapping(rs);
				conceptos.add(rc);
				if (entidad != WebKeysGlobal.OSPIM
						&& (rc instanceof ReciboOtroConcepto)) {
					((ReciboOtroConcepto) rc).setCantidadEmpleados(rs
							.getInt("cantidad_empleados"));
					((ReciboOtroConcepto) rc).setRemuneracionTotal(rs
							.getBigDecimal("importe_remuneracion_total"));
					((ReciboOtroConcepto) rc).setPeriodo(rs.getDate("periodo"));
					((ReciboOtroConcepto) rc).setBoletaNro(rs.getInt("nro_boleta_empleadores"));
					((ReciboOtroConcepto) rc).setNroSecuenciaDDJJ(rs.getInt("nro_secuencia_ddjj_empleadores"));
					
					((ReciboOtroConcepto) rc).setTotalBoleta(rs.getBigDecimal("total_boleta"));
				}
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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_ingresos_amtima(?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo_ingresos(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_ingresos_uoma(?)}";
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

	public void anular(int reciboId, String user, Date fechaBaja, int entidad)
			throws SystemException {
		_log.debug("Anulando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call anular_recibo_amtima(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call anular_recibo(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.anular_recibo_uoma(?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reciboId);
			stmt.setDate(2, new java.sql.Date(fechaBaja.getTime()));
			stmt.setString(3, user);
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Anulando recibos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public void reactivar(int reciboId, String user, int entidad)
			throws SystemException {
		_log.debug("Anulando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call reactivar_recibo_amtima(?,?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call reactivar_recibo(?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.reactivar_recibo_uoma(?, ?)}";
			}

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
		String sql = null;
		try {
			con = ConnectionHelper.getConnection();
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_efectivo_estado_amtima(?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo_efectivo_estado(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_efectivo_estado_uoma(?)}";
			}
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
			String screenName, Connection connectionParameter, int entidad)
			throws SystemException {
		_log.debug("Cambiando estado recibos-igresos");
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call cambiar_recibo_efectivo_estado_amtima(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call cambiar_recibo_efectivo_estado(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.cambiar_recibo_efectivo_estado_uoma(?, ?, ?)}";
			}

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
		return get(fechaIni, fechaFin, empresa, entidad);

	}

	public List<Recibo> get(Date fechaIni, Date fechaFin, Empresa empresa,
			boolean filtrar_0001, boolean filtrar_0002, boolean filtrar_0003,
			boolean filtrar_rend, boolean filtrar_bcap, boolean filtrar_otro,
			int entidad) throws SystemException {
		_log.debug("Buscando recibos");
		Connection con = null;
		CallableStatement stmt = null;
		List<Recibo> recibos = new ArrayList<Recibo>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_por_fechas_amtima(?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo_por_fechas(?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_por_fechas_uoma(?, ?, ?, ?, ?, ? ,?, ?, ?, ?)}";
			}
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
			if (entidad == WebKeysGlobal.UOMA) {
				stmt.setBoolean(5, filtrar_0001);
				stmt.setBoolean(6, filtrar_0002);
				stmt.setBoolean(7, filtrar_0003);
				stmt.setBoolean(8, filtrar_rend);
				stmt.setBoolean(9, filtrar_bcap);
				stmt.setBoolean(10, filtrar_otro);
			}
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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_conceptos_por_fechas_amtima	(?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo_conceptos_por_fechas(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_conceptos_por_fechas_uoma(?, ?)}";
			}
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
				
				if (entidad == WebKeysGlobal.UOMA) {
					if(rc instanceof ReciboOtroConcepto) {
					  try {	
						((ReciboOtroConcepto) rc).setBoletaNro(rs.getInt("nro_boleta_empleadores"));
						((ReciboOtroConcepto) rc).setPeriodo(rs.getDate("periodo"));
					  }catch(Exception e) {}
					}
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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_ingresos_por_fechas_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo_ingresos_por_fechas(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_ingresos_por_fechas_uoma(?, ?)}";
			}

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
			int entidad) throws SystemException {
		_log.debug("Buscando recibos-anticipos");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReciboIngreso> recibos = new ArrayList<ReciboIngreso>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_recibo_anticipos_para_a_aplicar_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call buscar_recibo_anticipos_para_a_aplicar(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_recibo_anticipos_para_a_aplicar_uoma(?, ?)}";
			}

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
		return saveIngreso(recibo, null, null, null, reciboAnticipo,null ,user,
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
			String sql = "{call reporte_anticipos(?, ?, ?, ?)}";
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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_recibo_derivado_amtima(?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call verificar_recibo_derivado(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_recibo_derivado_uoma(?)}";
			}
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

	public String getNumeroReciboSugerido(String pre, int entidad)
			throws SystemException {
		Connection con = ConnectionHelper.getConnection();
		CallableStatement stmt = null;
		String numero = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call sugerir_numero_recibo_amtima(?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call sugerir_numero_recibo(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.sugerir_numero_recibo_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, pre);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				numero = rs.getString(1);
			}
		} catch (Exception e) {
			_log.error("Error al verificar_recibo_derivado ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return numero;

	}
	
	public int save(TarjetaDebitoCredito tarjeta, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("Guardando recibo-tarjeta debito credito");
		return saveIngreso(recibo, null, null, null, null,tarjeta, user,
				connectionParameter, entidad);
	}
	
	public int borrarConceptosAportes(String idRecibo, Connection connectionParameter,
			int entidad) throws SystemException {

		_log.debug("Guardando recibo");
		CallableStatement stmt = null;
		Connection con = null;
		int cont = 1;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call borra_conceptos_recibo_amtima_aportes (?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.borra_conceptos_recibo_uoma_aportes (?)}";
			}
			
			if(sql.length()>0) {
			  stmt = con.prepareCall(sql.toString());
			  stmt.setString(cont++, idRecibo);

			  ResultSet rs = stmt.executeQuery();
			  while (rs.next()) {
				return rs.getInt(1);
			  }
			}  
		} catch (Exception e) {
			_log.error("Error al borrar conceptos recibos", e);
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

	
	public void save(ReciboPrestamo oc, Recibo recibo, String userName,
			Connection connectionParameter, int entidad) throws SystemException {
		_log.debug("insertando Beneficios aMTIMA");
		
		
		savePrestamo(recibo,  oc,userName,connectionParameter, entidad);
	}


	
	public void savePrestamo(Recibo recibo,ReciboPrestamo rp, String userName,
			Connection connectionParameter, int entidad) throws SystemException {

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
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call inserta_recibo_prestamo_amtima (?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "";
			}
			int cont = 0;
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(++cont, recibo.getId());
			if (rp.getPrestamo() != null) {
				stmt.setLong(++cont, rp.getPrestamo().getId());
			} else {
				stmt.setNull(++cont, java.sql.Types.BIGINT);
			}
			
			if (rp.getPrestamo().getAcuerdoFecha() != null) {
				stmt.setDate(++cont, new java.sql.Date(rp.getPrestamo().getAcuerdoFecha().getTime()));
			} else {
				stmt.setNull(++cont, java.sql.Types.DATE);
			}
			
			if(rp.getPrestamo().getMonto()!=null) {
				stmt.setDouble(++cont, rp.getPrestamo().getMonto());
			}else {
				stmt.setNull(++cont, java.sql.Types.DOUBLE);
			}
			
			stmt.setString(++cont, userName);
						ResultSet rs = stmt.executeQuery();
			int id = 0;
			while (rs.next()) {
				id = rs.getInt(1);
			}

			for (ConceptoPago cp : rp.getPagos()) {
				salvarConceptoPagos(id, cp, userName, con, entidad);
			}

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar recibo-prestamo", e);
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

}

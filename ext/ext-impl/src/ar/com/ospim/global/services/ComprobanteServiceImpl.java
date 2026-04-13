package ar.com.ospim.global.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.ComprobanteExistenteException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposOPExcel.ItemAnticipoOP;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ComprobanteServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(ComprobanteServiceImpl.class);

	private static ComprobanteServiceImpl instance = null;

	public static ComprobanteServiceImpl getInstance() {
		if (null == instance) {
			instance = new ComprobanteServiceImpl();
		}
		return instance;
	}

	public static void save(Comprobante comp, String user, int entidad)
			throws SystemException, ComprobanteExistenteException {
		getInstance().save(comp, user, null, entidad);
	}

	public void save(Comprobante comp, String user,
			Connection connectionParameter, int entidad)
			throws SystemException, ComprobanteExistenteException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_comprobante_amtima(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_comprobante_uoma(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			int cont = 1;
			stmt = con.prepareCall(sql.toString());
			
			if(entidad == WebKeysGlobal.OSPIM) {
				if(comp.getAlta_fecha() != null) {
					stmt.setDate(cont++, new java.sql.Date(comp.getAlta_fecha().getTime()));
				}else {
					stmt.setDate(cont++, new java.sql.Date(DateUtils.getCalendarGMTMenos3().getTimeInMillis()));
				}
			}
			stmt.setInt(cont++, comp.getPtoVenta());
			stmt.setString(cont++, comp.getTipoComprobante());
			stmt.setString(cont++, comp.getNroComprobante());
			stmt.setString(cont++, comp.getLetraComprobante());
			stmt.setInt(cont++, comp.getSucuComprobante());
			stmt.setString(cont++, comp.getCuit());

			stmt.setString(cont++, comp.getAcreedorEmpresa() != null ? comp
					.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(
					cont++,
					comp.getAcreedorEmpresa() != null
							&& !comp.getAcreedorEmpresa().getSucursal()
									.equals("") ? comp.getAcreedorEmpresa()
							.getSucursal() : "000");
			if (comp.getSeccional() != null && comp.getSeccional().getId() != 0) {
				stmt.setInt(cont++, comp.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(cont++, Types.INTEGER);
			}

			stmt.setBigDecimal(cont++, comp.getImporteComprobante());
			stmt.setDate(cont++,
					comp.getFechaEmision() != null ? new java.sql.Date(comp
							.getFechaEmision().getTime()) : null);
			stmt.setDate(cont++,
					comp.getFechaRecepcion() != null ? new java.sql.Date(comp
							.getFechaRecepcion().getTime()) : null);
			stmt.setDate(cont++,
					comp.getFechaVencimiento() != null ? new java.sql.Date(comp
							.getFechaVencimiento().getTime()) : null);
			stmt.setString(cont++, comp.getObservaciones());
			if (comp.getPeriodoPrestacion() != null) {
				stmt.setDate(cont++, new java.sql.Date(comp
						.getPeriodoPrestacion().getTime()));
			} else {
				stmt.setDate(cont++, null);
			}
			stmt.setString(cont++, user);
			stmt.setBoolean(cont++, comp.isDebitoParaEgreso());
			stmt.setBigDecimal(cont++, comp.getImporteComprobanteOriginal());

			if (entidad == WebKeysGlobal.UOMA) {
				stmt.setInt(cont++,
						comp.getCantCuotas() > 0 ? comp.getCantCuotas() : 1);
			}
			stmt.setInt(cont++, comp.getNroAnticipo());
			if (null != comp.getAfiliado()
					&& null != comp.getAfiliado().getCuil_titular()) {
				stmt.setString(cont++, comp.getAfiliado().getCuil_titular());
				stmt.setInt(cont++, comp.getAfiliado().getInte());
			} else {
				stmt.setNull(cont++, Types.VARCHAR);
				stmt.setNull(cont++, Types.INTEGER);
			}

			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new ComprobanteExistenteException(e);
			} else {
				throw new SystemException(e);
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void update(Comprobante comp, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_comprobante(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call actualizar_comprobante_amtima(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.actualizar_comprobante_uoma(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());

			stmt.setString(7, comp.getAcreedorEmpresa() != null ? comp
					.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(8, comp.getAcreedorEmpresa() != null ? comp
					.getAcreedorEmpresa().getSucursal() : null);
			if (comp.getSeccional() != null && comp.getSeccional().getId() != 0) {
				stmt.setInt(9, comp.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(9, Types.INTEGER);
			}

			stmt.setBigDecimal(10, comp.getImporteComprobante());
			stmt.setDate(11,
					comp.getFechaEmision() != null ? new java.sql.Date(comp
							.getFechaEmision().getTime()) : null);
			stmt.setDate(12,
					comp.getFechaRecepcion() != null ? new java.sql.Date(comp
							.getFechaRecepcion().getTime()) : null);
			stmt.setDate(13,
					comp.getFechaVencimiento() != null ? new java.sql.Date(comp
							.getFechaVencimiento().getTime()) : null);
			stmt.setString(14, comp.getObservaciones());
			if (comp.getPeriodoPrestacion() != null) {
				stmt.setDate(15, new java.sql.Date(comp.getPeriodoPrestacion()
						.getTime()));
			} else {
				stmt.setDate(15, null);
			}
			stmt.setString(16, user);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public int deleteComprobanteLiquidacion(Integer id, String user)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_comprobante_liquidacion(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("borrar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}

	public List<Comprobante> getComprobantes(OrdenPago ordenPago, int entidad)
			throws SystemException {
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_comprobantes_orden_pago_ospim(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_comprobantes_orden_pago_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_comprobantes_orden_pago_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, ordenPago.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = Comprobante.getMapping(rs);
				comprobantes.add(comp);
			}
			ordenPago.setComprobantes(comprobantes);
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes para la orden pago ospim",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}

	public Comprobante getComprobante(Comprobante comp, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_comprobante(?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_comprobante_amtima(?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_comprobante_uoma(?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comprobante = Comprobante.getMapping(rs);
				comprobante.setPagado(rs.getBoolean("pagado"));
				comprobante.setNroAnticipo(rs.getInt("nro_anticipo"));
				if (entidad == WebKeysGlobal.UOMA) {
					comprobante.setCantCuotas(rs.getInt("cant_cuotas"));
				}
				return comprobante;
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public Comprobante getComprobanteAnticipo(Comprobante comp, int entidad,
			int idOP) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_comprobante_anticipo(?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_comprobante_anticipo_uoma(?,?,?,?,?,?,?)}";
			}
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_comprobante_anticipo_amtima(?,?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			stmt.setInt(7, idOP);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comprobante = Comprobante.getMapping(rs);
				comprobante.setPagado(rs.getBoolean("pagado"));
				comprobante.setImporteComprobanteOriginal(rs
						.getBigDecimal("total_original"));
				comprobante.setCantCuotas(rs.getInt("cant_cuotas"));
				return comprobante;
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public Comprobante getComprobanteLiquidacionPorId(int id_liquidacion)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_comprobante_liquidacion_por_id_liq(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return Comprobante.getMapping(rs);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public Comprobante getComprobanteDebitoLiquidacionPorId(int id_liquidacion)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_comprobante_debito_liquidacion_por_id_liq(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return Comprobante.getMapping(rs);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public List<Comprobante> getComprobantesLikeNro(Comprobante comp,
			boolean isOspim) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = null;
		try {
			String sql = "{call buscar_comprobantes_like_nro(?,?,?,?,?,?)}";
			if (!isOspim) {
				sql = "{call buscar_comprobantes_amtima_like_nro(?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			ResultSet rs = stmt.executeQuery();
			comps = new ArrayList<Comprobante>();
			while (rs.next()) {
				comps.add(Comprobante.getMapping(rs));
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}

	public int getIdComprobanteLiquidacion(Comprobante comp)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_liquidacion = 0;
		try {
			String sql = "{call buscar_id_liquidacion_comprobante(?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return id_liquidacion = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_liquidacion;
	}

	public String generarSiguienteDebito(Connection connectionParameter,
			String tipo, int entidad) throws SystemException {
		CallableStatement stmt = null;
		String secuencia = "1";
		try {
			String sql = "{call generar_siguiente_debito(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call generar_siguiente_debito_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.generar_siguiente_debito_uoma(?)}";
			}
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setString(1, tipo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return secuencia = String.valueOf(rs.getInt(1));
			}
		} catch (Exception e) {
			_log.error("Error al generar siguiente debito", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return secuencia;
	}

	public List<Comprobante> getComprobantes(Comprobante comp, int entidad)
			throws SystemException {
		String sp = "buscar_comprobantes";
		if (entidad == WebKeysGlobal.AMTIMA) {
			sp = "buscar_comprobantes_amtima";
		} else if (entidad == WebKeysGlobal.UOMA) {
			sp = "uoma.buscar_comprobantes_uoma";
		}
		return getComprobantes(comp, sp, entidad);
	}

	public List<Comprobante> getComprobantesImpagosNoLiquidaciones(
			Comprobante comp, int entidad) throws SystemException {

		String sp = "buscar_comprobantes_impagos";
		if (entidad == WebKeysGlobal.AMTIMA) {
			sp = "buscar_comprobantes_amtima_impagos";
		} else if (entidad == WebKeysGlobal.UOMA) {
			sp = "uoma.buscar_comprobantes_impagos_uoma";
		}
		return getComprobantes(comp, sp, entidad);
	}

	public List<Comprobante> getComprobantes(Comprobante comp, String sp,
			int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = null;
		try {
			String sql = "{call " + sp + "(?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());

			if (comp.getPtoVenta() != 0) {
				stmt.setInt(1, comp.getPtoVenta());
			} else {
				stmt.setNull(1, Types.SMALLINT);
			}
			stmt.setString(2, StringUtils.checkNotEmpty(comp
					.getTipoComprobante()) ? comp.getTipoComprobante() : null);
			stmt.setString(3, StringUtils.checkNotEmpty(comp
					.getNroComprobante()) ? comp.getNroComprobante() : null);
			stmt.setString(4,StringUtils.checkNotEmpty(comp.getCuit()) ? comp.getCuit(): null);
//			stmt.setString(4,StringUtils.checkNotEmpty(comp.getCuitEmisor()) ? comp.getCuitEmisor(): null);
			stmt.setString(5, StringUtils.checkNotEmpty(comp
					.getLetraComprobante()) ? comp.getLetraComprobante() : null);
			if (comp.getSucuComprobante() != 0) {
				stmt.setInt(6, comp.getSucuComprobante());
			} else {
				stmt.setNull(6, Types.SMALLINT);
			}
			if (comp.getFechaEmision() != null) {
				stmt.setDate(7, new java.sql.Date(comp.getFechaEmision()
						.getTime()));
			} else {
				stmt.setDate(7, null);
			}
			if (comp.getFechaRecepcion() != null) {
				stmt.setDate(8, new java.sql.Date(comp.getFechaRecepcion()
						.getTime()));
			} else {
				stmt.setDate(8, null);
			}

			if (comp.getPeriodoPrestacion() != null) {
				stmt.setDate(9, new java.sql.Date(comp.getPeriodoPrestacion()
						.getTime()));
			} else {
				stmt.setDate(9, null);
			}
			stmt.setString(10, comp.getAcreedorEmpresa() != null ? comp
					.getAcreedorEmpresa().getCuit() : null);
			stmt.setString(11, comp.getAcreedorEmpresa() != null ? comp
					.getAcreedorEmpresa().getSucursal() : null);

			// SI VIENE 0 Y ES UOMA ELIGIERON CENTRAL
			if (comp.getSeccional() != null) {
				stmt.setInt(12, comp.getSeccional().getIdSeccional());
			} else if (entidad == WebKeysGlobal.UOMA
					&& null == comp.getSeccional()) {
				stmt.setInt(12, 0);
			} else {
				stmt.setNull(12, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			comps = new ArrayList<Comprobante>();
			while (rs.next()) {
				Comprobante c = Comprobante.getMapping(rs);
				c.setPagado(rs.getBoolean("pagado"));
				c.setOpExistente(rs.getBoolean("op_existente"));
				comps.add(c);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}

	public int saveConcepto(Comprobante comp, ComprobanteConcepto concepto,
			String usr, Connection connectionParameter, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_concepto_comprobante(?,?,?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_concepto_comprobante_amtima(?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_concepto_comprobante_uoma(?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, concepto.getConceptoComprobante().getId());
			stmt.setBigDecimal(8, concepto.getImporte());
			stmt.setString(9, usr);
			stmt.setBigDecimal(10, concepto.getImporteOriginal());
			if (entidad == WebKeysGlobal.UOMA) {
				stmt.setInt(11, concepto.getConceptoComprobante()
						.getIdSeccional());
				if(concepto.getCentroCosto()!=null && concepto.getCentroCosto().getId()!=null){
				   stmt.setInt(12,concepto.getCentroCosto().getId());	
				}else{
				   stmt.setNull(12,Types.INTEGER);	
				}
			}else if(entidad == WebKeysGlobal.OSPIM) {
				if(comp.getAlta_fecha()!=null) {
					stmt.setDate(11, new java.sql.Date(comp.getAlta_fecha().getTime()));
				}else {
					stmt.setDate(11, new java.sql.Date(DateUtils.getCalendarGMTMenos3().getTimeInMillis()));
				}
			}

			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante - rollbackeando", e);
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

	public int borrarConcepto(Comprobante comp, ComprobanteConcepto cc,
			String usr, Connection connectionParameter, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_concepto_comprobante(?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call borrar_concepto_comprobante_amtima(?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.borrar_concepto_comprobante_uoma(?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, cc.getConceptoComprobante().getId());
			stmt.setString(8, usr);

			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al borrar el comprobante - rollbackeando", e);
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

	public List<ComprobanteConcepto> getConceptos(Comprobante comp, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteConcepto> lista = new ArrayList<ComprobanteConcepto>();
		try {
			String sql = "{call buscar_concepto_comprobante(?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_concepto_comprobante_amtima(?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_concepto_comprobante_extendido_uoma(?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteConcepto ccc = ComprobanteConcepto.getMapping(rs,
						"CCC__");
				ccc.setConceptoComprobante(Concepto.getMapping(rs, "CC__"));
				if (entidad == WebKeysGlobal.UOMA) {
					
					Double tasaIva = rs.getDouble("ccc__tasa_iva");
					tasaIva=tasaIva==null?0D:tasaIva;		
					ccc.setTasaIva(tasaIva);		
					
					BigDecimal gravado = rs.getBigDecimal("ccc__gravado");
					gravado = (gravado==null?BigDecimal.ZERO:gravado);
					if(tasaIva==0D && gravado.compareTo(BigDecimal.ZERO)>0) {
						ccc.setGravadoIVA(BigDecimal.ZERO);
						ccc.setExento(gravado);
					}else {
						ccc.setGravadoIVA(gravado);
						ccc.setExento(BigDecimal.ZERO);
					}
					
					BigDecimal iva = rs.getBigDecimal("ccc__iva");		
					iva=iva==null?BigDecimal.ZERO:iva;
					ccc.setIva(iva);
							
					BigDecimal percepIIBB = rs.getBigDecimal("ccc__percepcion_iibb");
					percepIIBB=percepIIBB==null?BigDecimal.ZERO:percepIIBB;
					ccc.setPercepcionIIBB(percepIIBB);
					
					Integer jurisdIIBB = rs.getInt("ccc__jurisdiccion_iibb");
					jurisdIIBB=(jurisdIIBB==null?0:jurisdIIBB);
					ccc.setJurisdiccionIIBB(jurisdIIBB);
					
					BigDecimal percepIva = rs.getBigDecimal("ccc__percepcion_iva");
					percepIva=percepIva==null?BigDecimal.ZERO:percepIva;
					ccc.setPercepcionIVA(percepIva);
							
					BigDecimal otrosTributos = rs.getBigDecimal("ccc__otros_tributos");
					otrosTributos=otrosTributos==null?BigDecimal.ZERO:otrosTributos;
					ccc.setOtrosTributos(otrosTributos);
				}
				lista.add(ccc);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<ComprobanteConcepto> getConceptosAnticipo(Comprobante comp,
			int entidad, int idOP) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ComprobanteConcepto> lista = new ArrayList<ComprobanteConcepto>();
		try {
			String sql = "{call buscar_concepto_comprobante_anticipo(?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_concepto_comprobante_anticipo_uoma(?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_concepto_comprobante_anticipo_amtima(?,?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getCuit());
			stmt.setString(5, comp.getLetraComprobante());
			stmt.setInt(6, comp.getSucuComprobante());
			stmt.setInt(7, idOP);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteConcepto ccc = ComprobanteConcepto.getMapping(rs,
						"CCC__");
				ccc.setConceptoComprobante(Concepto.getMapping(rs, "CC__"));
				lista.add(ccc);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public String getUltimoNumeroComprobante(String tipo, String cuit,
			String sucu, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_ultimo_nro_comprobante(?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ultimo_nro_comprobante_amtima(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ultimo_nro_comprobante_uoma(?, ?, ?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, tipo);
			stmt.setString(2, cuit);
			stmt.setString(3, sucu);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String ret = rs.getString(1);
				if (ret == null) {
					return "0";
				} else {
					return ret;
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar  ultimo nro de  comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return "0";
	}

	public List<Anticipo> getAnticiposARendir(Empresa emp, int seccional,
			int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Anticipo> anticipos = null;
		try {
			String sql = "{call buscar_comprobantes_anticipos_a_rendir(?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_comprobantes_amtima_anticipos_a_rendir(?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_comprobantes_uoma_anticipos_a_rendir(?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, emp != null ? emp.getCuit() : null);
			stmt.setString(2, emp != null ? emp.getSucursal() : null);
			if (seccional != 0) {
				stmt.setInt(3, seccional);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			anticipos = new ArrayList<Anticipo>();
			while (rs.next()) {
				Comprobante comp = Comprobante.getMapping(rs);
				Anticipo anticipo = new Anticipo();
				anticipo.setOpOrigen(rs.getInt("opOrigen"));
				anticipo.setFechaOPOrigen(rs.getDate("fechaOPOrigen"));
				anticipo.setAnticipo(comp);
				if (entidad == WebKeysGlobal.UOMA) {
					anticipo.setCantCuotas(rs.getInt("cant_cuotas"));
					comp.setCantCuotas(rs.getInt("cant_cuotas"));
				}
				anticipo.setImporteOriginal(rs.getBigDecimal("total_original"));
				// SACAR AL IMPLEMENTAR DEVO ANTI
				try {
					anticipo.setNroCuota(rs.getInt("nro_anticipo"));
				} catch (Exception e) {

				}
				comp.setSaldo(rs.getBigDecimal("importe_comprobante"));
				comp.setImporteComprobanteOriginal(rs
						.getBigDecimal("total_original"));
				anticipos.add(anticipo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar anticipos a rendir", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return anticipos;
	}

	public int actualizaSaldoAnticipoRecibo(int reciboId, Comprobante comp, String user, int entidad)
			throws SystemException {
		int anticNro = 0;
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_saldo_anticipo_recibo(?, ?, ?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call actualiza_saldo_anticipo_amtima(?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.actualiza_saldo_anticipo_uoma(?, ?, ?, ?, ?, ?, ?)}";
			}
			_log.debug("obteniendo conexion");
			int cont=0;
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(++cont, reciboId);
			stmt.setString(++cont, comp.getTipoComprobante());
			stmt.setString(++cont, comp.getNroComprobante());
			stmt.setString(++cont, comp.getCuit());
			if (null != comp.getSeccional() && comp.getSeccional().getId() > 0) {
				stmt.setInt(++cont, comp.getSeccional().getId());
			} else {
				stmt.setNull(++cont, Types.INTEGER);
			}
			stmt.setBigDecimal(++cont, comp.getImporte());
			stmt.setString(++cont, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				anticNro = rs.getInt(1);
			}

		} catch (Exception e) {
			_log.error("Error al buscar  ultimo nro de  comprobantes ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return anticNro;
	}

	public List<Comprobante> getAnticiposARendir(Comprobante comp)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = null;
		try {
			String sql = "{call buscar_comprobantes_anticipos_a_rendir_por_comp(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, comp.getNroComprobante());

			ResultSet rs = stmt.executeQuery();
			comps = new ArrayList<Comprobante>();
			while (rs.next()) {
				comps.add(Comprobante.getMapping(rs));
			}
		} catch (Exception e) {
			_log.error("Error al buscar anticipos a rendir por comp", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}

	public void save(int idLiquidacion, Comprobante comp, String user)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_comprobante_liquidacion(?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, idLiquidacion);
			stmt.setString(8, user);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void save(Integer id, Comprobante comp, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_comprobante_op(?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_comprobante_op_amtima(?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_comprobante_op_uoma(?,?,?,?,?,?,?,?)}";
			}
			con = connectionParameter;
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, id);
			stmt.setString(8, user);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public List<OrdenPagoOspim> getComprobantesOP(Date fechaIni, Date fechaFin)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> ops = new ArrayList<OrdenPagoOspim>();
		try {
			String sql = "{call buscar_comprobantes_orden_pago_ospim_por_fecha(?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = Comprobante.getMapping(rs, "C__");
				comp.setFechaPrimerPago(rs.getDate("fecha_primer_op"));
				int id = rs.getInt("OP__id_orden_pago");
				comp.setIdOp(id);
				comp.setNroAnticipo(rs.getInt("nro_cuota"));
				comp.getAcreedorEmpresa().setRazon_soc(
						rs.getString("E__razon_soc"));
				OrdenPagoOspim op = new OrdenPagoOspim(id);
				if (comp.getImporte().compareTo(BigDecimal.ZERO) != 0) {
					if (ops.contains(op)) {
						ops.get(ops.indexOf(op)).getComprobantes().add(comp);
					} else {
						op.setComprobantes(new ArrayList<Comprobante>());
						op.getComprobantes().add(comp);
						ops.add(op);
					}
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes para la orden pago ospim",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ops;
	}

	public List<OrdenPago> getComprobantesOPAmtima(Date fechaIni, Date fechaFin)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> ops = new ArrayList<OrdenPago>();
		try {
			String sql = "{call buscar_comprobantes_orden_pago_amtima_por_fecha(?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = Comprobante.getMapping(rs, "C__");
				comp.setFechaPrimerPago(rs.getDate("fecha_primer_op"));
				int id = rs.getInt("OP__id_orden_pago");
				comp.getAcreedorEmpresa().setRazon_soc(
						rs.getString("E__razon_soc"));
				OrdenPagoAmtima op = new OrdenPagoAmtima(id);
				if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getComprobantes().add(comp);
				} else {
					op.setComprobantes(new ArrayList<Comprobante>());
					op.getComprobantes().add(comp);
					ops.add(op);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes para la orden pago ospim",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ops;
	}

	public List<OrdenPago> getComprobantesOP(Date fechaIni, Date fechaFin,
			int entidad, Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> ops = new ArrayList<OrdenPago>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_comprobantes_orden_pago_amtima_por_fecha(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_comprobantes_orden_pago_uoma_por_fecha(?, ?)}";
			}
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con=connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = Comprobante.getMapping(rs, "C__");
				comp.setFechaPrimerPago(rs.getDate("fecha_primer_op"));
				int id = rs.getInt("OP__id_orden_pago");
				comp.setIdOp(id);
				comp.setNroAnticipo(rs.getInt("nro_cuota"));
				comp.getAcreedorEmpresa().setRazon_soc(
						rs.getString("E__razon_soc"));
				
/*	Anterior			
				OrdenPagoAmtima op = new OrdenPagoAmtima(id);

				if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getComprobantes().add(comp);
				} else {
					op.setComprobantes(new ArrayList<Comprobante>());
					op.getComprobantes().add(comp);
					ops.add(op);
				}
*/			
//Nuevo para Devengado				
				if (entidad == WebKeysGlobal.AMTIMA) {				
				   OrdenPagoAmtima op = new OrdenPagoAmtima(id);

				   if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getComprobantes().add(comp);
				   } else {
					op.setComprobantes(new ArrayList<Comprobante>());
					op.getComprobantes().add(comp);
					ops.add(op);
				   }
				}else {
					OrdenPagoUoma op = new OrdenPagoUoma(id);
					   if (ops.contains(op)) {
						ops.get(ops.indexOf(op)).getComprobantes().add(comp);
					   } else {
						op.setComprobantes(new ArrayList<Comprobante>());
						op.getComprobantes().add(comp);
						ops.add(op);
					   }
	
				}
//Fin Nuevo
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes para la orden pago ospim",
					e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return ops;
	}

	public List<OrdenPagoOspim> getComprobantesOP(Date fechaIni, Date fechaFin,
			int id_prestador, String cuit, String sucur, String compro_tipo,
			String compro_nro, int compro_sucur, String compro_letra, Connection connection)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> ops = new ArrayList<OrdenPagoOspim>();
		try {
			String sql = "{call buscar_comprobantes_orden_pago_ospim_por_fecha(?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getReportesOspimConnection();
			}else{
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			if (null == cuit || (null != cuit && cuit.trim().equals(""))) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, cuit);
			}
			if (null == sucur || (null != sucur && sucur.trim().equals(""))) {
				stmt.setNull(4, Types.VARCHAR);
			} else {
				stmt.setString(4, sucur);
			}
			if (0 == id_prestador) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, id_prestador);
			}

			if (null == compro_tipo || null != compro_tipo
					&& compro_tipo.trim().equals("")) {
				stmt.setNull(6, Types.VARCHAR);
			} else {
				stmt.setString(6, compro_tipo);
			}

			if (null == compro_letra
					|| (null != compro_letra && compro_letra.trim().equals(""))) {
				stmt.setNull(7, Types.VARCHAR);
			} else {
				stmt.setString(7, compro_letra);
			}

			if ((0 == compro_sucur)) {
				stmt.setNull(8, Types.INTEGER);
			} else {
				stmt.setInt(8, compro_sucur);
			}

			if (null == compro_nro
					|| (null != compro_nro && compro_nro.trim().equals(""))) {
				stmt.setNull(9, Types.VARCHAR);
			} else {
				stmt.setString(9, compro_nro);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = Comprobante.getMapping(rs, "C__");
				int id = rs.getInt("OP__id_orden_pago");
				comp.setIdOp(id);
				comp.getAcreedorEmpresa().setRazon_soc(
						rs.getString("E__razon_soc"));
				OrdenPagoOspim op = new OrdenPagoOspim(id);
				if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getComprobantes().add(comp);
				} else {
					op.setComprobantes(new ArrayList<Comprobante>());
					op.getComprobantes().add(comp);
					ops.add(op);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes para la orden pago ospim",
					e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return ops;
	}

	public List<OrdenPago> getComprobantesOP(Date fechaIni, Date fechaFin,
			int id_prestador, String cuit, String sucur, String compro_tipo,
			String compro_nro, int compro_sucur, String compro_letra,
			int entidad, Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> ops = new ArrayList<OrdenPago>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_comprobantes_orden_pago_amtima_por_fecha(?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_comprobantes_orden_pago_uoma_por_fecha(?,?,?,?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con=connection;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			if (null == cuit || (null != cuit && cuit.trim().equals(""))) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, cuit);
			}
			if (null == sucur || (null != sucur && sucur.trim().equals(""))) {
				stmt.setNull(4, Types.VARCHAR);
			} else {
				stmt.setString(4, sucur);
			}
			if (0 == id_prestador) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, id_prestador);
			}

			if (null == compro_tipo || null != compro_tipo
					&& compro_tipo.trim().equals("")) {
				stmt.setNull(6, Types.VARCHAR);
			} else {
				stmt.setString(6, compro_tipo);
			}

			if (null == compro_letra
					|| (null != compro_letra && compro_letra.trim().equals(""))) {
				stmt.setNull(7, Types.VARCHAR);
			} else {
				stmt.setString(7, compro_letra);
			}

			if ((0 == compro_sucur)) {
				stmt.setNull(8, Types.INTEGER);
			} else {
				stmt.setInt(8, compro_sucur);
			}

			if (null == compro_nro
					|| (null != compro_nro && compro_nro.trim().equals(""))) {
				stmt.setNull(9, Types.VARCHAR);
			} else {
				stmt.setString(9, compro_nro);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comp = Comprobante.getMapping(rs, "C__");
				int id = rs.getInt("OP__id_orden_pago");
				comp.getAcreedorEmpresa().setRazon_soc(
						rs.getString("E__razon_soc"));

				comp.setIdOp(id);
				comp.setNroAnticipo(rs.getInt("nro_cuota"));

//				OrdenPagoAmtima op = new OrdenPagoAmtima(id);
				
				OrdenPago op = null;
				if (entidad == WebKeysGlobal.AMTIMA) {
				   op =  new OrdenPagoAmtima(id);
				} else if (entidad == WebKeysGlobal.UOMA) {
				   op =  new OrdenPagoUoma(id);
				}
				
				if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getComprobantes().add(comp);
				} else {
					op.setComprobantes(new ArrayList<Comprobante>());
					op.getComprobantes().add(comp);
					ops.add(op);
				}

			}
		} catch (Exception e) {
			_log.error(
					"Error al buscar comprobantes para la orden pago amtima", e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return ops;
	}

	public int getContadorPagoParcial(Comprobante comp,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int contador = 0;
		try {
			String sql = "{call buscar_contador_pago_parcial(?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_contador_pago_parcial_amtima(?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_contador_pago_parcial_uoma(?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			contador = rs.getInt(1);

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante - rollbackeando", e);
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
		return contador;
	}

	public List<Comprobante> getConceptosOP(Date fechaIni, Date fechafin,
			int id_prestador, String cuit, String sucur, String compro_tipo,
			String compro_nro, int compro_sucur, String compro_letra,
			int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_concepto_comprobante_por_fecha(?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_concepto_comprobante_por_fecha_uoma(?,?,?,?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechafin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				obtenerConceptoParaComprobante(comps, rs, entidad);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}

	public List<Comprobante> getConceptosOP(Date fechaIni, Date fechafin)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		try {
			String sql = "{call buscar_concepto_comprobante_por_fecha(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechafin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				obtenerConceptoParaComprobante(comps, rs, WebKeysGlobal.OSPIM);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}

	public List<Comprobante> getConceptosOP(Date fechaIni, Date fechafin,
			int entidad, Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_concepto_comprobante_por_fecha_subdiario_amtima(?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_concepto_comprobante_por_fecha_subdiario_uoma(?,?)}";
			}
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con= connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechafin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				obtenerConceptoParaComprobante(comps, rs, entidad);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return comps;
	}

	public void anular(Comprobante comp, String screenName, int entidad,
			boolean borrarTotal, Connection connectionForTransaction)
			throws SystemException, ComprobantesYaPagadosException {
		CallableStatement stmt = null;
		try {
			String sql = "{call anular_comprobante(?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call anular_comprobante_amtima(?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.anular_comprobante_uoma(?,?,?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionForTransaction.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setString(7, screenName);
			stmt.setBoolean(8, borrarTotal);
			stmt.executeUpdate();
		} catch (SQLException e) {
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_CHECK_VIOLATION)) {
				throw new ComprobantesYaPagadosException();
			} else {
				throw new SystemException(e);
			}
		} finally {
			ConnectionHelper.cerrar(stmt);
		}

	}

	public String getUltimoNroDebito(String tipo, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_ultimo_nro_debito(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ultimo_nro_debito_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ultimo_nro_debito_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, tipo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String ret = rs.getString(1);
				if (ret == null) {
					return "0";
				} else {
					return ret;
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar  ultimo nro de  debito ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return "0";
	}

	public int updateConcepto(Comprobante comp, ComprobanteConcepto concepto,
			String screenName, Connection connectionParameter, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_concepto_comprobante(?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call actualizar_concepto_comprobante_amtima(?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.actualizar_concepto_comprobante_uoma(?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, concepto.getConceptoComprobante().getId());
			stmt.setBigDecimal(8, concepto.getImporte());
			stmt.setString(9, screenName);
			if (entidad == WebKeysGlobal.UOMA) {
				if (concepto.getConceptoComprobante().getIdSeccional() > 0) {
					stmt.setInt(10, concepto.getConceptoComprobante()
							.getIdSeccional());
				} else {
					stmt.setNull(10, Types.INTEGER);
				}
			}

			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante - rollbackeando", e);
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

	public List<Comprobante> getConceptosOPSubdiario(Date fechaIni,
			Date fechaFin, Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		try {
			String sql = "{call buscar_concepto_comprobante_por_fecha_subdiario2(?,?)}";
			// String sql =
			// "{call buscar_concepto_comprobante_por_fecha_subdiario(?,?)}";
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con= connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteConcepto ccc = ComprobanteConcepto.getMapping(rs,
						"CCC__");
				ccc.setConceptoComprobante(Concepto.getMapping(rs, "CC__"));
				ccc.getConceptoComprobante().getPlanCuentas()
						.setId(rs.getInt("cc__cuenta_id"));
				PlanCuentas planCuentasPasivo = new PlanCuentas(
						rs.getString("cc_numero_pasivo"),
						rs.getString("cc_cuenta_pasivo"));
				planCuentasPasivo.setId(rs.getInt("cc_cuenta_pasivo_id"));
				ccc.getConceptoComprobante().setPlanCuentasPasivo(
						planCuentasPasivo);

				Comprobante c = new Comprobante(rs.getInt("C__id_punto_venta"),
						rs.getString("C__compro_tipo"),
						rs.getString("C__compro_nro"),
						rs.getString("C__compro_letra"),
						rs.getInt("C__compro_sucu"), rs.getString("C__cuit"));
				c.setIdOp(rs.getInt("o_id_orden_pago"));
				c.setNroAnticipo(rs.getInt("nro_cuota"));
				if (comps.contains(c)) {

					if (c.getIdOp() == comps.get(comps.indexOf(c)).getIdOp()) {
						comps.get(comps.indexOf(c)).getConceptos().add(ccc);
					}

				} else {
					c.setConceptos(new ArrayList<ComprobanteConcepto>());
					c.getConceptos().add(ccc);
					comps.add(c);
				}

			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return comps;
	}

	public List<Comprobante> getConceptosOPSubdiario(Date fechaIni,
			Date fechaFin, int entidad, Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_concepto_comprobante_por_fecha_subdiario_uoma(?,?)}";
			} else {
				sql = "{call buscar_concepto_comprobante_amtima_por_fecha_subdiario(?,?)}";
			}
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ComprobanteConcepto ccc = ComprobanteConcepto.getMapping(rs,
						"CCC__");
				ccc.setConceptoComprobante(Concepto.getMapping(rs, "CC__"));
				ccc.getConceptoComprobante().getPlanCuentas()
						.setId(rs.getInt("cc__cuenta_id"));
				PlanCuentas planCuentasPasivo = new PlanCuentas(
						rs.getString("cc_numero_pasivo"),
						rs.getString("cc_cuenta_pasivo"));
				planCuentasPasivo.setId(rs.getInt("cc_cuenta_pasivo_id"));
				ccc.getConceptoComprobante().setPlanCuentasPasivo(
						planCuentasPasivo);

				Comprobante c = new Comprobante(rs.getInt("C__id_punto_venta"),
						rs.getString("C__compro_tipo"),
						rs.getString("C__compro_nro"),
						rs.getString("C__compro_letra"),
						rs.getInt("C__compro_sucu"), rs.getString("C__cuit"));

				c.setNroAnticipo(rs.getInt("nro_cuota"));
				c.setIdOp(rs.getInt("id_orden_pago"));

				if (comps.contains(c)) {

					if (c.getIdOp() == comps.get(comps.indexOf(c)).getIdOp()) {
						comps.get(comps.indexOf(c)).getConceptos().add(ccc);
					}

				} else {
					c.setConceptos(new ArrayList<ComprobanteConcepto>());
					c.getConceptos().add(ccc);
					comps.add(c);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return comps;
	}

	public List<Comprobante> getConceptos(OrdenPago op, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		try {
			String sql = "{call buscar_concepto_comprobante_por_orden_pago_ospim(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_concepto_comprobante_por_orden_pago_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_concepto_comprobante_por_orden_pago_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, op.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				obtenerConceptoParaComprobante(comps, rs, entidad);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes conceptos comp ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}

	private void obtenerConceptoParaComprobante(List<Comprobante> comps,
			ResultSet rs, int entidad) throws SQLException {
		ComprobanteConcepto ccc = ComprobanteConcepto.getMapping(rs, "CCC__");
		ccc.setConceptoComprobante(Concepto.getMapping(rs, "CC__"));

		Comprobante c = new Comprobante(rs.getInt("C__id_punto_venta"),
				rs.getString("C__compro_tipo"), rs.getString("C__compro_nro"),
				rs.getString("C__compro_letra"), rs.getInt("C__compro_sucu"),
				rs.getString("C__cuit"));

		c.setNroAnticipo(rs.getInt("nro_cuota"));
		c.setIdOp(rs.getInt("id_orden_pago"));

		if (comps.contains(c)) {
			if (c.getIdOp() == comps.get(comps.indexOf(c)).getIdOp()) {
				comps.get(comps.indexOf(c)).getConceptos().add(ccc);
			} else {
				c.setConceptos(new ArrayList<ComprobanteConcepto>());
				c.getConceptos().add(ccc);
				comps.add(c);
			}
		} else {
			c.setConceptos(new ArrayList<ComprobanteConcepto>());
			c.getConceptos().add(ccc);
			comps.add(c);
		}

	}

	public List<ItemAnticipoOP> listadoAnticiposPagos(Date fechaIni,
			Date fechaFin, Date fechaUtil, String cuit, String sucursal,
			int id_seccional, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemAnticipoOP> comps = new ArrayList<ItemAnticipoOP>();
		try {
			String sql = "{call listado_anticipos_pagos_op(?, ?)}";

			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.listado_anticipos_pagos_op_uoma(?,?,?,?,?,?)}";
			}

			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call listado_anticipos_pagos_op_amtima(?,?,?,?,?,?)}";
			}
			_log.debug(sql + " fechas: " + fechaIni + " " +fechaFin  );
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			if (fechaUtil != null && entidad == WebKeysGlobal.UOMA) {
				stmt.setDate(3, new java.sql.Date(fechaUtil.getTime()));
			} else if (entidad == WebKeysGlobal.UOMA) {
				stmt.setNull(3, Types.DATE);
			}

			if (entidad == WebKeysGlobal.UOMA) {
				if (null != cuit && cuit.trim().length() > 0) {
					stmt.setString(4, cuit);
				} else {
					stmt.setNull(4, Types.VARCHAR);
				}
				if (null != sucursal && sucursal.trim().length() > 0) {
					stmt.setString(5, sucursal);
				} else {
					stmt.setNull(5, Types.VARCHAR);
				}
				if (id_seccional > 0) {
					stmt.setInt(6, id_seccional);
				} else {
					stmt.setNull(6, Types.INTEGER);
				}
			}
			
			
			if (entidad == WebKeysGlobal.AMTIMA) {
				stmt.setNull(3, Types.DATE);
				if (null != cuit && cuit.trim().length() > 0) {
					stmt.setString(4, cuit);
				} else {
					stmt.setNull(4, Types.VARCHAR);
				}
				stmt.setNull(5, Types.VARCHAR);
				stmt.setNull(6, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemAnticipoOP item = ItemAnticipoOP.getMapping(rs);
				if (entidad == WebKeysGlobal.UOMA) {
					item.setCantCuotas(rs.getInt("cant_cuotas"));
					item.setNroCuota(rs.getInt("nro_cuota"));
					item.setValorCuota(rs.getBigDecimal("valor_cuotas"));
					item.setSaldo(rs.getBigDecimal("saldo"));
				}
				
				if (entidad == WebKeysGlobal.AMTIMA) {
					item.setSaldo(rs.getBigDecimal("saldo"));
				}
				comps.add(item);
			}
		} catch (Exception e) {
			_log.error("Error al buscar listado_anticipos_pagos_op ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comps;
	}

	public Comprobante getUltimoComprobanteAmtimaAutomatico()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Comprobante comp = null;
		try {
			String sql = "{call buscar_ultimo_comprobante_amtima_automatico()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				comp = Comprobante.getMapping(rs);
			}
		} catch (Exception e) {
			_log.error(
					"Error al buscar buscar_ultimo_comprobante_amtima_automatico ",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comp;
	}

	public Comprobante getUltimoComprobanteOspimAutomatico()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Comprobante comp = null;
		try {
			String sql = "{call buscar_ultimo_comprobante_ospim_automatico()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				comp = Comprobante.getMapping(rs);
			}
		} catch (Exception e) {
			_log.error(
					"Error al buscar buscar_ultimo_comprobante_ospim_automatico ",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comp;
	}

	public Comprobante getUltimoComprobanteNDFOspimAutomatico()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Comprobante comp = null;
		try {
			String sql = "{call buscar_ultimo_comprobante_ospim_ndf_automatico()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				comp = Comprobante.getMapping(rs);
			}
		} catch (Exception e) {
			_log.error(
					"Error al buscar buscar_ultimo_comprobante_ndf_ospim_automatico ",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comp;
	}
	
	public int saveConceptoExtendido(Comprobante comp, ComprobanteConcepto concepto,
			String usr, Connection connectionParameter, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_concepto_comprobante(?,?,?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_concepto_comprobante_amtima(?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_concepto_comprobante_extendido_uoma(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, concepto.getConceptoComprobante().getId());
			stmt.setBigDecimal(8, concepto.getImporte());
			stmt.setString(9, usr);
			stmt.setBigDecimal(10, concepto.getImporteOriginal());
			if (entidad == WebKeysGlobal.UOMA) {
				stmt.setInt(11, concepto.getConceptoComprobante()
						.getIdSeccional());
				if(concepto.getCentroCosto()!=null && concepto.getCentroCosto().getId()!=null){
				   stmt.setInt(12,concepto.getCentroCosto().getId());	
				}else{
				   stmt.setNull(12,Types.INTEGER);	
				}
				
				BigDecimal gravado = concepto.getGravadoIVA()!=null?concepto.getGravadoIVA():BigDecimal.ZERO;
				if(gravado.compareTo(BigDecimal.ZERO)==0) {
					  gravado=concepto.getExento();
				}
				stmt.setBigDecimal(13,gravado);
				
				if(concepto.getTasaIva()!=null) {
				   stmt.setDouble(14, concepto.getTasaIva());
				}else {
				   stmt.setNull(14,Types.DOUBLE);		
				}
				
				if(concepto.getIva()!=null) {
                   stmt.setBigDecimal(15, concepto.getIva());
				}else {
				   stmt.setBigDecimal(15, BigDecimal.ZERO);	
				}
				
				if(concepto.getPercepcionIVA()!=null) {
                   stmt.setBigDecimal(16,concepto.getPercepcionIVA());
				} else {
				   stmt.setBigDecimal(16, BigDecimal.ZERO); 	
				}
				
				if(concepto.getPercepcionIIBB()!=null) {
                  stmt.setBigDecimal(17,concepto.getPercepcionIIBB());
				}else {
				  stmt.setBigDecimal(17, BigDecimal.ZERO);	
				}
                if(concepto.getJurisdiccionIIBB()!=null) {
                  stmt.setInt(18, concepto.getJurisdiccionIIBB());
                } else {
                  stmt.setNull(18,Types.INTEGER);	
                }
                
                if(concepto.getOtrosTributos()!=null) {
                   stmt.setBigDecimal(19, concepto.getOtrosTributos());
                }else {
                	stmt.setNull(19,Types.INTEGER);
                }
                	
			}else if(entidad == WebKeysGlobal.OSPIM) {
				if(comp.getAlta_fecha()!=null) {
					stmt.setDate(11, new java.sql.Date(comp.getAlta_fecha().getTime()));
				}else {
					stmt.setDate(11, new java.sql.Date(DateUtils.getCalendarGMTMenos3().getTimeInMillis()));
				}
			}

			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante extendido - rollbackeando", e);
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
	
	
	public int borrarConceptoExtendido(Comprobante comp, ComprobanteConcepto cc,
			String usr, Connection connectionParameter, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_concepto_comprobante(?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call borrar_concepto_comprobante_amtima(?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.borrar_concepto_comprobante_extendido_uoma(?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, cc.getConceptoComprobante().getId());
			stmt.setString(8, usr);
			if (entidad == WebKeysGlobal.UOMA) {
			   stmt.setInt(9, cc.getCentroCosto().getId());
			   stmt.setDouble(10,cc.getTasaIva());
			   stmt.setDouble(11,cc.getImporte().doubleValue());
			}
			
			
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al borrar el comprobante - rollbackeando", e);
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
	
	
	public int updateConceptoExtendido(Comprobante comp, ComprobanteConcepto concepto,
			String screenName, Connection connectionParameter, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_concepto_comprobante(?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call actualizar_concepto_comprobante_amtima(?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.actualizar_concepto_comprobante_extendido_uoma(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParameter == null) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, comp.getPtoVenta());
			stmt.setString(2, comp.getTipoComprobante());
			stmt.setString(3, comp.getNroComprobante());
			stmt.setString(4, comp.getLetraComprobante());
			stmt.setInt(5, comp.getSucuComprobante());
			stmt.setString(6, comp.getCuit());
			stmt.setInt(7, concepto.getConceptoComprobante().getId());
			stmt.setBigDecimal(8, concepto.getImporte());
			stmt.setString(9, screenName);
			if (entidad == WebKeysGlobal.UOMA) {
				if (concepto.getConceptoComprobante().getIdSeccional() > 0) {
					stmt.setInt(10, concepto.getConceptoComprobante()
							.getIdSeccional());
				} else {
					stmt.setNull(10, Types.INTEGER);
				}
				
				if(concepto.getCentroCosto()!=null && concepto.getCentroCosto().getId()!=null) {
					stmt.setInt(11, concepto.getCentroCosto().getId());
				} else {
					stmt.setNull(11, Types.INTEGER);
				}
				
				if(concepto.getGravadoIVA()!=null || concepto.getExento()!=null) {
				   BigDecimal gravado = concepto.getGravadoIVA()!=null?concepto.getGravadoIVA():BigDecimal.ZERO;
				   if(gravado.compareTo(BigDecimal.ZERO)==0) {
					  gravado=concepto.getExento();
				   }
				   stmt.setBigDecimal(12, gravado);
				}else {
				   stmt.setNull(12,Types.DECIMAL);	
				}
				
				if(concepto.getTasaIva()!=null) {
				   stmt.setDouble(13, concepto.getTasaIva());	
				}else {
					stmt.setNull(13, Types.DOUBLE);
				}
				
				if(concepto.getIva()!=null) {
					stmt.setBigDecimal(14, concepto.getIva());
				}else {
					stmt.setNull(14,Types.DECIMAL);	
				}
				
				if(concepto.getPercepcionIVA() !=null) {
					stmt.setBigDecimal(15, concepto.getPercepcionIVA());
				}else {
					stmt.setNull(15,Types.DECIMAL);	
				}
				
				if(concepto.getPercepcionIIBB() !=null) {
					stmt.setBigDecimal(16, concepto.getPercepcionIIBB());
				}else {
					stmt.setNull(16,Types.DECIMAL);	
				}
				
				if(concepto.getJurisdiccionIIBB()!=null) {
					stmt.setInt(17, concepto.getJurisdiccionIIBB());
				} else {
					stmt.setNull(17, Types.INTEGER);
				}
				
				if(concepto.getOtrosTributos() !=null) {
					stmt.setBigDecimal(18, concepto.getOtrosTributos());
				}else {
					stmt.setNull(18,Types.DECIMAL);	
				}
				
			}
			

			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el comprobante - rollbackeando", e);
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

	
	public List<Comprobante> getComprobantesIIBB(Date fechaIni,
			Date fechaFin, int entidad, Integer jurisdiccion,Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_percepciones_iibb_por_fecha(?,?,?)}";
			} else {
				sql = "";
			}
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			if(jurisdiccion!=null) {
			  stmt.setInt(3, jurisdiccion);
			} else {
			  stmt.setNull(3, Types.INTEGER);	
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante c = new Comprobante();
				Empresa acreedor = new Empresa(rs.getString("cuit"),"000",rs.getString("razon_social"));
				
				c.setAcreedorEmpresa(acreedor);
				c.setFechaEmision(rs.getDate("fecha_emision"));
				c.setTipoComprobante(rs.getString("comprobante_tipo"));
				c.setLetraComprobante(rs.getString("comprobante_letra"));
				c.setSucuComprobante(rs.getInt("comprobante_sucursal"));
				c.setNroComprobante(rs.getString("comprobante_nro"));
				
				ComprobanteConcepto ccc = new ComprobanteConcepto();
				
				ccc.setJurisdiccionIIBB(rs.getInt("jurisdiccion_iibb"));
				ccc.setPercepcionIIBB(rs.getBigDecimal("percepcion_iibb"));
				
				c.setConceptos(new ArrayList<ComprobanteConcepto>());
				c.getConceptos().add(ccc);
				comps.add(c);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes iibb  ", e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return comps;
	}


	public List<Comprobante> getComprobantesLibroIVACompras(Date fechaIni,
			Date fechaFin, int entidad,Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> comps = new ArrayList<Comprobante>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.libro_iva_compras_por_fecha(?,?)}";
			} else {
				sql = "";
			}
			_log.debug("obteniendo conexion");
			if(connection==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connection;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante c = new Comprobante();
				Empresa acreedor = new Empresa(rs.getString("cuit"),"000",rs.getString("razon_social"));
				acreedor.setImpIva(rs.getString("cond_iva"));
				c.setAcreedorEmpresa(acreedor);
				c.setFechaEmision(rs.getDate("fecha_emision"));
				c.setTipoComprobante(rs.getString("comprobante_tipo"));
				c.setLetraComprobante(rs.getString("comprobante_letra"));
				c.setSucuComprobante(rs.getInt("comprobante_sucursal"));
				c.setNroComprobante(rs.getString("comprobante_nro"));
				c.setGravadoIVA27(rs.getBigDecimal("gravado27"));
				c.setGravadoIVA21(rs.getBigDecimal("gravado21"));
				c.setGravadoIVA105(rs.getBigDecimal("gravado105"));
				c.setExento(rs.getBigDecimal("exento"));
				
				c.setIva27(rs.getBigDecimal("iva27"));
				c.setIva21(rs.getBigDecimal("iva21"));
				c.setIva105(rs.getBigDecimal("iva105"));
				c.setJurisdiccionIIBB(rs.getInt("jurisdiccion_iibb"));
				c.setPercepcionIIBB(rs.getBigDecimal("percepcion_iibb"));
				c.setPercepcionIVA(rs.getBigDecimal("percepcion_iva"));
				c.setOtrosTributos(rs.getBigDecimal("otros_tributos"));
				c.setImporteComprobante(rs.getBigDecimal("importe_comprobante"));
				comps.add(c);
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes libro iva compras  ", e);
			throw new SystemException(e);
		} finally {
			if(connection==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return comps;
	}

	
	public List<Comprobante> getComprobantesGlobales(Comprobante comp, int entidad,Integer offset)
			throws SystemException {
		List<Comprobante>comprobantes=new ArrayList<Comprobante>();
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call buscar_comprobantes_global(?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
//				sql = "{call buscar_comprobante_amtima(?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
//				sql = "{call uoma.buscar_comprobante_uoma(?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(comp.getAcreedorEmpresa()!=null && comp.getAcreedorEmpresa().getCuit()!=null && !"".equalsIgnoreCase(comp.getAcreedorEmpresa().getCuit())) {
			   stmt.setString(1, comp.getAcreedorEmpresa().getCuit() );
			}else{
			   stmt.setNull(1,Types.VARCHAR);	
			}   
			if(comp.getTipoComprobante()!=null && !"".equalsIgnoreCase(comp.getTipoComprobante())) {
			  stmt.setString(2, comp.getTipoComprobante());
			}else{
				   stmt.setNull(2,Types.VARCHAR);	
			}  
			if(comp.getLetraComprobante()!=null &&  !"".equalsIgnoreCase(comp.getLetraComprobante())) {
			   stmt.setString(3, comp.getLetraComprobante());
			}else{
			   stmt.setNull(3,Types.VARCHAR);	
			}
			
			if(!"0".equalsIgnoreCase(String.valueOf( comp.getPtoVenta()))) {
			    stmt.setString(4, String.valueOf( comp.getPtoVenta()));
			}else{
				stmt.setNull(4,Types.VARCHAR);	
			}   
			if(comp.getNroComprobante()!=null && !"".equalsIgnoreCase(String.valueOf( comp.getNroComprobante()))) {
			   stmt.setString(5, comp.getNroComprobante());
		    }else{
			   stmt.setNull(5,Types.VARCHAR);	
		    } 
			
			 if(offset!=null) {
			    	stmt.setInt(6, offset);
			   }else {
				   stmt.setNull(6, Types.INTEGER);	
			 }
//			stmt.setString(4, comp.getCuit());
//			stmt.setInt(6, comp.getSucuComprobante());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comprobante comprobante = new Comprobante();
				
				
				
				comprobante.setImporteComprobante(rs.getBigDecimal("total"));
				comprobante.setNroComprobante(rs.getString("numero"));
				comprobante.setTipoComprobante(rs.getString("tipo"));
				comprobante.setPtoVenta(rs.getInt("sucursal"));
				comprobante.setLetraComprobante(rs.getString("letra"));
				comprobante.setSucuComprobante(rs.getInt("sucursal"));
				comprobante.setFechaEmision(rs.getDate("fecha"));
				comprobante.setFechaPrimerPago(rs.getDate("fecha_transferencia"));

				String cuitAcreedor = rs.getString("cuit");
				comprobante.setAcreedorEmpresa(new Empresa(cuitAcreedor, null, null));
				comprobante.setOrigen(rs.getString("origen"));
				comprobante.setReclamoId(rs.getBigDecimal("reclamo"));
				comprobante.setReintegroId(rs.getBigDecimal("reintegro"));
				comprobante.setReintegroTipo(rs.getString("reintegro_tipo"));
				comprobante.setLiquidacionId(rs.getBigDecimal("liquidacion"));
				comprobante.setOrdenPagoId(rs.getBigDecimal("orden_pago"));
				comprobante.setImportePagado(rs.getDouble("pagado"));
				comprobante.setTotalRegistros(rs.getInt("total_registros"));
				comprobantes.add(comprobante);
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar comprobantes global ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return comprobantes;
	}
	
}

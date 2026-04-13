package ar.com.ospim.global.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.objectweb.asm.Type;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.beans.ReintegroFarmaciaList;
import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.farmacia.beans.ReporteOrdenPagoReintegrosFarmacia;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Caja;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.beans.Pago;
import ar.com.ospim.global.beans.PagoBancario;
import ar.com.ospim.global.beans.PagoSinSalidaDeFondos;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.beans.RetencionIIBB;
import ar.com.ospim.global.beans.RetencionIVA;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.DetalleCuota;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.PlanPrestacion;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroList;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacion;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoOrtopediaOrtodoncia;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoProtesis;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.reportes.action.ReporteOrdenesPagoAction.ReporteOrdenPagoOspim;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.WebKeysUOMA;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="OrdenPagoServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class OrdenPagoServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(OrdenPagoServiceImpl.class);

	private static OrdenPagoServiceImpl instance = null;

	public static OrdenPagoServiceImpl getInstance() {
		if (null == instance) {
			instance = new OrdenPagoServiceImpl();
		}
		return instance;
	}

	public int getLastLoteOrdenPago() {
		Connection con = null;
		CallableStatement stmt = null;
		int id_lote = 0;
		try {
			String sql = "{call buscar_ultimo_lote_op()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_lote = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al buscar lote ordenes pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_lote;

	}

	public int setFechaFirmaLoteOrdenPago(Date fecha_firma, String user) {
		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call actualizar_fecha_firma_lote_op(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha_firma.getTime()));
			stmt.setString(2, user);

			stmt.executeQuery();

		} catch (Exception e) {
			_log.error("Error al buscar lote ordenes pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;

	}

	public List<OrdenPagoAmtima> getOrdenesPagoAmtima(
			BigDecimal numeroChequeInt, Integer numeroInt) {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoAmtima> lista = null;
		try {
			String sql = "{call buscar_ordenes_pago(?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (numeroChequeInt == null) {
				stmt.setNull(1, Type.INT);
			} else {
				stmt.setBigDecimal(1, numeroChequeInt);
			}
			if (numeroInt == null) {
				stmt.setNull(2, Type.INT);
			} else {
				stmt.setInt(2, numeroInt);
			}

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPagoAmtima>();
			while (rs.next()) {
				OrdenPagoAmtima op = OrdenPagoAmtima.getMapping(rs, "op__");
				String razonSoc = rs.getString("e__razon_soc");
				op.getAcreedor().setRazon_soc(razonSoc);
				lista.add(op);
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<OrdenPago> getOrdenesPago(BigDecimal numeroChequeInt,
			Integer numeroInt, String cuit, String sucursal, Date fechaDesde,
			Date fechaHasta, int idSeccional, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> lista = null;
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ordenes_pago_amtima(?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ordenes_pago_uoma(?, ?, ?, ?, ?, ?, ?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (numeroChequeInt == null) {
				stmt.setNull(1, Type.INT);
			} else {
				stmt.setBigDecimal(1, numeroChequeInt);
			}
			if (numeroInt == null) {
				stmt.setNull(2, Type.INT);
			} else {
				stmt.setInt(2, numeroInt);
			}

			stmt.setString(3, cuit != null && cuit.trim().length() > 0 ? cuit
					: null);
			stmt.setString(4,
					sucursal != null && sucursal.trim().length() > 0 ? sucursal
							: null);

			if (null != fechaDesde) {
				stmt.setDate(5, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(5, Types.DATE);
			}

			if (null != fechaHasta) {
				stmt.setDate(6, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(6, Types.DATE);
			}

			if (idSeccional > 0) {
				stmt.setInt(7, idSeccional);
			} else {
				stmt.setNull(7, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPago>();
			while (rs.next()) {
				OrdenPago op = OrdenPago.getMapping(rs, "op__");
				String razonSoc = rs.getString("e__razon_soc");
				op.getAcreedor().setRazon_soc(razonSoc);
				lista.add(op);
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<OrdenPago> getOrdenesPago(Date ini, Date fin, int id_prestador,
			String cuit, String sucur, String compro_tipo, String compro_nro,
			int compro_sucur, String compro_letra, int entidad,
			Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> lista = null;
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ordenes_pago_amtima_reporte(?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ordenes_pago_uoma_reporte(?,?,?,?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(ini.getTime()));
			stmt.setDate(2, new java.sql.Date(fin.getTime()));
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
			if (null == compro_tipo
					|| (null != compro_tipo && compro_tipo.trim().equals(""))) {
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

			if (0 == compro_sucur) {
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
			String razonSoc ="";
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPago>();
			while (rs.next()) {
				razonSoc = rs.getString("e__razon_soc");
				if (entidad == WebKeysGlobal.AMTIMA) {
				    OrdenPagoAmtima op = OrdenPagoAmtima.getMappingRepo(rs, "OP__");
					op.getAcreedor().setRazon_soc(razonSoc);
					lista.add(op);
				} else if (entidad == WebKeysGlobal.UOMA) {
					OrdenPagoUoma op = OrdenPagoUoma.getMappingRepo(rs, "OP__");
					op.getAcreedor().setRazon_soc(razonSoc);
					lista.add(op);
				}
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago", e);
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return lista;
	}

	public OrdenPagoAmtima getOrdenPagoAmtima(Integer numeroInt) {
		List<OrdenPagoAmtima> ordenesPagoAmtima = getOrdenesPagoAmtima(null,
				numeroInt);
		OrdenPagoAmtima op = null;
		if (ordenesPagoAmtima != null && ordenesPagoAmtima.size() > 0) {
			op = ordenesPagoAmtima.get(0);
			op.setItems(getItemsOrdenPago(op.getId()));
		}
		return op;
	}

	public OrdenPago getOrdenPago(Integer numeroInt, String cuit,
			String sucursal, Date fechaDesde, Date fechaHasta, int entidad) {
		List<OrdenPago> ordenesPago = getOrdenesPago(null, numeroInt, null,
				null, null, null, 0, entidad);
		OrdenPago op = null;
		if (ordenesPago != null && ordenesPago.size() > 0) {
			op = ordenesPago.get(0);
			op.setItems(getItemsOrdenPago(op.getId()));
		}
		return op;
	}

	private List<ItemOrdenPago> getItemsOrdenPago(Integer id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemOrdenPago> items = null;
		try {
			String sql = "{call buscar_items_ordenes_pago(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			items = new ArrayList<ItemOrdenPago>();
			while (rs.next()) {
				items.add(ItemOrdenPago.getMapping(rs, "LFA__"));
			}
		} catch (Exception e) {
			_log.error("Error al buscar orden pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return items;
	}

	public void borrar(Integer numero, String screenName) {
		// Auto-generated method stub
	}

	public int save(OrdenPagoAmtima op, String user,
			Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_orden_pago(?,?,?,?,?,?,?,?,?,?)}";
			con = connectionParameter;
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, op.getImporte());
			stmt.setString(2, op.getAcreedor().getCuit());
			stmt.setString(3, op.getAcreedor().getSucursal());
			if (op.getSeccional() != null && op.getSeccional().getId() != 0) {
				stmt.setInt(4, op.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}

			stmt.setString(5, op.getObservaciones());
			stmt.setString(6, user);
			stmt.setBigDecimal(7, op.getDescuento());
			stmt.setBigDecimal(8, op.getDescuentoDrogueria());
			stmt.setDate(9, new java.sql.Date(op.getFechaDesde().getTime()));
			stmt.setDate(10, new java.sql.Date(op.getFechaHasta().getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar orden pago amtima", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}

	public void udpate(BigDecimal importe, String aFavorDe, Date fecha,
			BigDecimal descuento, BigDecimal descuentoDrogueria,
			Date fechaDesde, Date fechaHasta, BigDecimal numeroCheque,
			String usuario, String concepto, Integer id,
			String afiliadoRazonSocial, String cuitcuil, Integer idSeccional,
			Date fechaEmision, Date fechaRecepcion, String tipoComp,
			String nroComp, String cuit, BigDecimal importeComp, int ptoVenta,
			String letra, int sucu) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_orden_pago(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, importe);
			stmt.setString(2, aFavorDe);
			stmt.setDate(3, new java.sql.Date(fecha.getTime()));
			stmt.setBigDecimal(4, descuento);
			stmt.setBigDecimal(5, descuentoDrogueria);
			stmt.setDate(6, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(7, new java.sql.Date(fechaHasta.getTime()));
			stmt.setBigDecimal(8, numeroCheque);
			stmt.setString(9, usuario);
			stmt.setString(10, concepto);
			stmt.setInt(11, id);
			stmt.setString(12, afiliadoRazonSocial);
			stmt.setString(13, cuitcuil);
			if (idSeccional != null) {
				stmt.setInt(14, idSeccional);
			} else {
				stmt.setNull(14, Type.INT);
			}
			stmt.setDate(15, new java.sql.Date(fechaEmision.getTime()));
			stmt.setDate(16, new java.sql.Date(fechaRecepcion.getTime()));
			stmt.setString(17, tipoComp);
			stmt.setString(18, nroComp);
			stmt.setString(19, cuit);
			stmt.setBigDecimal(20, new BigDecimal(importeComp.toString()));
			stmt.setInt(21, ptoVenta);
			stmt.setString(22, letra);
			stmt.setInt(23, sucu);

			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar orden pago", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void saveItem(Date fecha, Date periodo, OrdenPago ordenPago,
			Integer nroLiquidacion, String nroPrestador, String prestador,
			int nroFarmacia, String farmacia, Integer idOspim,
			Integer idAmtima, Integer idUoma, int inte, String nombreApellido,
			String nroRecetario, String troquel, String medicamento,
			Integer cantidad, BigDecimal pvp, BigDecimal totalOspim,
			BigDecimal totalAmtima, String debito, BigDecimal difOspim,
			BigDecimal difAmtima, Double porcentajeOspim,
			Double porcentajeAmtima, String pmi, String user, String caja,
			String archivo, Connection connectionParameter)

	throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = null;
			if (ordenPago.getClass().getName()
					.equals(OrdenPagoAmtima.class.getName())) {
				sql = "{call insertar_item_orden_pago(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else {
				sql = "{call insertar_item_farmacia_orden_pago_ospim(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			// _log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			stmt.setDate(2, new java.sql.Date(periodo.getTime()));
			stmt.setInt(3, ordenPago.getId());
			stmt.setInt(4, nroLiquidacion);
			stmt.setString(5, nroPrestador);
			stmt.setString(6, prestador);
			stmt.setInt(7, nroFarmacia);
			stmt.setString(8, farmacia);
			if (idOspim == null) {
				stmt.setNull(9, Type.INT);
			} else {
				stmt.setInt(9, idOspim);
			}
			if (idAmtima == null) {
				stmt.setNull(10, Type.INT);
			} else {
				stmt.setInt(10, idAmtima);
			}
			if (idUoma == null) {
				stmt.setNull(11, Type.INT);
			} else {
				stmt.setInt(11, idUoma);
			}
			stmt.setInt(12, inte);
			stmt.setString(13, nombreApellido);
			stmt.setString(14, nroRecetario);
			stmt.setString(15, troquel);
			stmt.setString(16, medicamento);
			stmt.setInt(17, cantidad);
			stmt.setBigDecimal(18, pvp);
			stmt.setBigDecimal(19, totalOspim);
			stmt.setBigDecimal(20, totalAmtima);
			stmt.setString(21, debito);
			stmt.setBigDecimal(22, difOspim);
			stmt.setBigDecimal(23, difAmtima);
			if (porcentajeOspim == null) {
				stmt.setNull(24, Type.DOUBLE);
			} else {
				stmt.setDouble(24, porcentajeOspim);
			}
			if (porcentajeAmtima == null) {
				stmt.setNull(25, Type.DOUBLE);
			} else {
				stmt.setDouble(25, porcentajeAmtima);
			}
			stmt.setString(26, pmi);
			stmt.setString(27, user);
			stmt.setString(28, caja);
			stmt.setString(29, archivo);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al insertar item de orden pago", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public int save(OrdenPagoOspim op, String screenName,
			Connection connectionParameter) throws SystemException,
			DuplicateNumeroChequeException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_orden_pago_ospim(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = connectionParameter;
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, op.getId());
			stmt.setDate(2,  new java.sql.Date(op.getAlta_fecha().getTime() ) );
			stmt.setBigDecimal(3, op.getImporte());
			stmt.setBoolean(4, op.isPrestador());
			stmt.setBoolean(5, op.isFarmacia());
			stmt.setString(6, op.getAcreedor().getCuit());
			stmt.setString(7, op.getAcreedor().getSucursal());
			if (op.getSeccional() != null && op.getSeccional().getId() != 0) {
				stmt.setInt(8, op.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(8, Types.INTEGER);
			}

			stmt.setString(9, op.getObservaciones());
			stmt.setBigDecimal(10, op.getDescuento());
			stmt.setBigDecimal(11, op.getDescuentoDrogueria());
			if (op.getFechaDesde() != null) {
				stmt.setDate(12,
						new java.sql.Date(op.getFechaDesde().getTime()));
			} else {
				stmt.setNull(12, Types.NULL);
			}
			if (op.getFechaHasta() != null) {
				stmt.setDate(13,
						new java.sql.Date(op.getFechaHasta().getTime()));
			} else {
				stmt.setNull(13, Types.NULL);
			}
			stmt.setString(14, screenName);
			stmt.setInt(15, op.getIdLote());
			stmt.setString(16, op.getDestino());
			stmt.setString(17, op.getObsInterna());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar orden pago ospim", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateNumeroChequeException(e);
			} else {
				throw new SystemException(e);
			}
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}

	public int save(OrdenPago op, String screenName,
			Connection connectionParameter, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			int i = 1;
			int proximoIdOP = -1;
			
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_orden_pago_amtima(?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_orden_pago_uoma(?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else {
				sql = "{call insertar_orden_pago_ospim(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			con = connectionParameter;
			stmt = con.prepareCall(sql.toString());
			
			if (entidad == WebKeysGlobal.OSPIM) {
				proximoIdOP= OrdenPagoServiceUtil.obtenerProximoIdOrdenPago();

				stmt.setInt(i++, proximoIdOP);
				if(op.getAlta_fecha()!=null) {
					stmt.setDate(i++,  new java.sql.Date(op.getAlta_fecha().getTime() ) );
				}else {
					stmt.setDate(i++,  new java.sql.Date(DateUtils.getCalendarGMTMenos3().getTimeInMillis() ) );
				}
				
			}
			stmt.setBigDecimal(i++, op.getImporte());
			stmt.setBoolean(i++, op.isPrestador());
			stmt.setBoolean(i++, op.isFarmacia());
			stmt.setString(i++, op.getAcreedor().getCuit());
			stmt.setString(i++, op.getAcreedor().getSucursal());
			if (op.getSeccional() != null && op.getSeccional().getId() != 0) {
				stmt.setInt(i++, op.getSeccional().getIdSeccional());
			} else {
				stmt.setNull(i++, Types.INTEGER);
			}

			stmt.setString(i++, op.getObservaciones());
			stmt.setBigDecimal(i++, op.getDescuento() != null ? op.getDescuento()
					: new BigDecimal(0));
			stmt.setBigDecimal(
					i++,
					op.getDescuentoDrogueria() != null ? op
							.getDescuentoDrogueria() : new BigDecimal(0));
			if (op.getFechaDesde() != null) {
				stmt.setDate(i++,
						new java.sql.Date(op.getFechaDesde().getTime()));
			} else {
				stmt.setNull(i++, Types.NULL);
			}
			if (op.getFechaHasta() != null) {
				stmt.setDate(i++,
						new java.sql.Date(op.getFechaHasta().getTime()));
			} else {
				stmt.setNull(i++, Types.NULL);
			}
			stmt.setString(i++, screenName);
			if (entidad == WebKeysGlobal.OSPIM) {
				stmt.setInt(i++, op.getIdLote());
				stmt.setString(i++, op.getDestino());
				stmt.setString(i++, op.getObsInterna());
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if(entidad == WebKeysGlobal.OSPIM) {
					return proximoIdOP;
				}else {
					return rs.getInt(1);
				}	
			}
		} catch (SQLException e) {
			_log.error("Error al insertar orden pago", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateNumeroChequeException(e);
			} else {
				throw new SystemException(e);
			}
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}

	public List<OrdenPagoOspim> getOrdenesPagoOspim(BigDecimal numeroChequeInt,
			Integer numeroInt, String cuit, String sucursal, Date fechaDesde,
			Date fechaHasta, int idSeccional, String cbu) {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> lista = null;
		try {
			String sql = "{call buscar_ordenes_pago_ospim(?, ?, ?, ?, ?, ?, ? ,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (numeroChequeInt == null) {
				stmt.setNull(1, Type.INT);
			} else {
				stmt.setBigDecimal(1, numeroChequeInt);
			}
			if (numeroInt == null) {
				stmt.setNull(2, Type.INT);
			} else {
				stmt.setInt(2, numeroInt);
			}

			stmt.setString(3, cuit != null && cuit.trim().length() > 0 ? cuit
					: null);
			stmt.setString(4,
					sucursal != null && sucursal.trim().length() > 0 ? sucursal
							: null);

			if (null != fechaDesde) {
				stmt.setDate(5, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(5, Types.DATE);
			}

			if (null != fechaHasta) {
				stmt.setDate(6, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(6, Types.DATE);
			}

			if (idSeccional > 0) {
				stmt.setInt(7, idSeccional);
			} else {
				stmt.setNull(7, Types.INTEGER);
			}
			
			
			stmt.setString(8, cbu != null && cbu.trim().length() > 0 ? cbu : null);

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPagoOspim>();
			while (rs.next()) {
				OrdenPagoOspim op = OrdenPagoOspim.getMapping(rs, "OP__");
				String razonSoc = rs.getString("e__razon_soc");
				op.getAcreedor().setRazon_soc(razonSoc);
				lista.add(op);
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago ospim", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<OrdenPagoOspim> getOrdenesPagoOspim(Date ini, Date fin,
			boolean incluirProveedores, boolean incluirLiquidaciones,
			boolean incluirReintegros, Connection connection) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> lista = null;
		try {
			String sql = "{call buscar_ordenes_pago_ospim_reporte(?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(ini.getTime()));
			stmt.setDate(2, new java.sql.Date(fin.getTime()));
			stmt.setBoolean(3, incluirProveedores);
			stmt.setBoolean(4, incluirLiquidaciones);
			stmt.setBoolean(5, incluirReintegros);
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPagoOspim>();
			while (rs.next()) {
				OrdenPagoOspim op = OrdenPagoOspim.getMapping(rs, "OP__");
				op.setLiquidacion(rs.getBoolean("liquidacion"));
				op.setReintegro(rs.getBoolean("reintegro"));
				op.setMostrarEnCuadro(rs.getBoolean("mostrar_en_cuadro"));
				String razonSoc = rs.getString("e__razon_soc");
				op.getAcreedor().setRazon_soc(razonSoc);
				lista.add(op);
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago ospim", e);
			throw e;
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return lista;
	}

	public List<OrdenPago> getOrdenesPago(Date ini, Date fin,
			boolean incluirProveedores, boolean incluirLiquidaciones,
			boolean incluirReintegros, String cuit, String sucursal,
			int idSeccional, boolean contabilidad, int entidad,
			Connection connection) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> lista = null;
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ordenes_pago_uoma_reporte(?,?,?,?,?,?,?,?,?)}";
			} else {
				sql = "{call buscar_ordenes_pago_amtima_reporte(?,?,?,?,?,?,?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			if (connection == null) {
				con = ConnectionHelper.getReportesOspimConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(ini.getTime()));
			stmt.setDate(2, new java.sql.Date(fin.getTime()));
			stmt.setString(3, null != cuit && !cuit.trim().equals("") ? cuit
					: null);
			stmt.setString(4,
					null != sucursal && !sucursal.trim().equals("") ? sucursal
							: null);
			if (idSeccional > 0) {
				stmt.setInt(5, idSeccional);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			stmt.setNull(6, Types.VARCHAR);
			stmt.setNull(7, Types.VARCHAR);
			stmt.setNull(8, Types.INTEGER);
			stmt.setNull(9, Types.VARCHAR);
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPago>();
			while (rs.next()) {
// Nuevo para devengado 				
				if (entidad == WebKeysGlobal.UOMA) {
					OrdenPagoUoma op= OrdenPagoUoma.getMapping(rs, "OP__");
					op.setMostrarEnCuadro(rs.getBoolean("mostrar_en_cuadro"));
					String razonSoc = rs.getString("e__razon_soc");
					op.getAcreedor().setRazon_soc(razonSoc);
					if ((contabilidad && !op.getObservaciones().equals(
							"ANULADAMISMODIA"))
							|| !contabilidad) {
						lista.add(op);
					}
				}else {
					OrdenPagoAmtima op= OrdenPagoAmtima.getMapping(rs, "OP__");
					op.setMostrarEnCuadro(rs.getBoolean("mostrar_en_cuadro"));
					String razonSoc = rs.getString("e__razon_soc");
					op.getAcreedor().setRazon_soc(razonSoc);
					if ((contabilidad && !op.getObservaciones().equals(
							"ANULADAMISMODIA"))
							|| !contabilidad) {
						lista.add(op);
					}
				}
//Fin nuevo Devengado
				
/*	Anterior		
				OrdenPago op = OrdenPago.getMapping(rs, "OP__");
				op.setMostrarEnCuadro(rs.getBoolean("mostrar_en_cuadro"));
				String razonSoc = rs.getString("e__razon_soc");
				op.getAcreedor().setRazon_soc(razonSoc);
				if ((contabilidad && !op.getObservaciones().equals(
						"ANULADAMISMODIA"))
						|| !contabilidad) {
					lista.add(op);
				}
*/
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago ospim", e);
			throw e;
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return lista;
	}

	public List<OrdenPago> getOrdenesPagoOspim(Date ini, Date fin,
			int id_prestador, String cuit, String sucur, String compro_tipo,
			String compro_nro, int compro_sucur, String compro_letra,
			int nro_lote, Connection connection) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> lista = null;
		try {
			String sql = "{call buscar_ordenes_pago_ospim_reporte(?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			if (connection == null) {
				con = ConnectionHelper.getReportesOspimConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(ini.getTime()));
			stmt.setDate(2, new java.sql.Date(fin.getTime()));
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
			if (null == compro_tipo
					|| (null != compro_tipo && compro_tipo.trim().equals(""))) {
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

			if (0 == compro_sucur) {
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

			if (0 == nro_lote) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, nro_lote);
			}

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<OrdenPago>();
			while (rs.next()) {
				OrdenPagoOspim op = OrdenPagoOspim.getMapping(rs, "OP__");
				String razonSoc = rs.getString("e__razon_soc");
				op.getAcreedor().setRazon_soc(razonSoc);
				op.setAFavorDe(rs.getString("a_nombre_de"));
				lista.add(op);
			}
		} catch (Exception e) {
			_log.error("Error al buscar ordenes pago ospim", e);
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return lista;
	}

	public OrdenPagoOspim getOrdenPagoOspim(Integer numeroInt, int entidad) {
		List<OrdenPagoOspim> ops = getOrdenesPagoOspim(null, numeroInt, null,
				null, null, null, 0, null);
		if (ops == null || ops.size() == 0) {
			return null;
		}
		OrdenPagoOspim op = ops.get(0);
		getListasReintegros(op, entidad);
		getLiquidacionesPresta(op, entidad);
		return op;
	}

	public void getLiquidacionesPresta(OrdenPagoOspim op, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;

			sql = "{call buscar_ordenes_pago_ospim_liquidaciones(?)}";

			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, op.getId());

			List<Liquidacion> lista = new ArrayList<Liquidacion>();
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(new Liquidacion(rs.getInt("id_liquidacion")));
			}
			if (lista.size() > 0) {
				op.setLiquidacionesList(lista);
			}

		} catch (Exception e) {
			_log.error("Error al buscar orden pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void getListasReintegros(OrdenPago op, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;

			sql = "{call buscar_ordenes_pago_ospim_lista_reintegros(?)}";

			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ordenes_pago_amtima_lista_reintegros(?)}";
			}else if (entidad == WebKeysGlobal.UOMA) {
//				sql = "{call }"; no implementado todavia
			}
			
//			se agrega esto porque se cruzaban lso reintegros de ospi men las ops de uoma
			if(entidad == WebKeysGlobal.AMTIMA || entidad == WebKeysGlobal.OSPIM) {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, op.getId());
	
				List<ReintegroList> lista = new ArrayList<ReintegroList>();
				ResultSet rs = stmt.executeQuery();
				getReintegroList(lista, rs, "PRESTACIONAL");
	
				sql = "{call buscar_ordenes_pago_ospim_lista_reintegros_farmacia(?)}";
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, op.getId());
				rs = stmt.executeQuery();
				getReintegroList(lista, rs, "FARMACIA");
				
				if (lista != null && !lista.isEmpty()) {
					op.setReintegrosList(lista);
				}
			}
			
		} catch (Exception e) {
			_log.error("Error al buscar orden pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void udpate(OrdenPagoOspim op, String screenName)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualizar_orden_pago_ospim_secundarios(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, op.getId());
			stmt.setInt(2, op.getIdLote());
			stmt.setString(3, op.getDestino());
			stmt.setString(4, op.getObsInterna());
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar orden pago ospim", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void anularOrdenPago(Integer nro, Date fechaBaja, String usr,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call anular_orden_pago_ospim(?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call anular_orden_pago_amtima(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.anular_orden_pago_uoma(?, ?, ?)}";
			}
			if (connectionParameter != null) {
				con = connectionParameter;
			} else {
				_log.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, nro);
			stmt.setDate(2, new java.sql.Date(fechaBaja.getTime()));
			stmt.setString(3, usr);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al anular orden pago ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}

	}

	public void saveOrdenPagoOspimReintegro(Reintegro reint, int id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_orden_pago_ospim_reintegro(?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setInt(2, reint.getId_reintegro());
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al agregar orden pago - reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public List<ReporteOrdenPagoReintegros> getReintegros(int listaId)
			throws SystemException, NoSuchReintegroEntryException {
		ArrayList<ReporteOrdenPagoReintegros> list = new ArrayList<ReporteOrdenPagoReintegros>();
		int id_reintegro = getPrimerReitnegroLista(listaId);

		String tipo_reintegro = getTipoReintegroLista(listaId, id_reintegro);

		Reintegro reintegro = null;
		if (!tipo_reintegro
				.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			reintegro = ReintegroServiceUtil.getReintegroEntry(id_reintegro);
		} else {
			reintegro = ReintegroServiceUtil
					.getReintegroPorIdCuota(id_reintegro);
		}
		if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_PRE)) {
			getReintegros(listaId, list);
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			getReintegrosOdoProtesis(listaId, list);
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			getReintegrosOdoOrto(listaId, list);
		}

		Collections.sort(list, new Comparator<ReporteOrdenPagoReintegros>() {
			public int compare(ReporteOrdenPagoReintegros o1,
					ReporteOrdenPagoReintegros o2) {
				if (o1.getReintegro().getId_reintegro() == o2.getReintegro()
						.getId_reintegro()) {
					return 0;
				} else if (o1.getReintegro().getId_reintegro() < o2
						.getReintegro().getId_reintegro()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return list;
	}

	public List<ReporteOrdenPagoReintegrosFarmacia> getReintegrosFarmacia(
			int listaId) throws SystemException, NoSuchReintegroEntryException {
		ArrayList<ReporteOrdenPagoReintegrosFarmacia> list = new ArrayList<ReporteOrdenPagoReintegrosFarmacia>();

		getReintegrosFarmacia(listaId, list);

		Collections.sort(list,
				new Comparator<ReporteOrdenPagoReintegrosFarmacia>() {
					public int compare(ReporteOrdenPagoReintegrosFarmacia o1,
							ReporteOrdenPagoReintegrosFarmacia o2) {
						if (o1.getReintegro().getId_reintegro() == o2
								.getReintegro().getId_reintegro()) {
							return 0;
						} else if (o1.getReintegro().getId_reintegro() < o2
								.getReintegro().getId_reintegro()) {
							return -1;
						} else {
							return 1;
						}
					}
				});
		return list;
	}

	public List<ReporteOrdenPagoReintegrosFarmacia> getReintegrosFarmacia(
			String listas) throws SystemException,
			NoSuchReintegroEntryException {
		ArrayList<ReporteOrdenPagoReintegrosFarmacia> list = new ArrayList<ReporteOrdenPagoReintegrosFarmacia>();
		listas = listas.replaceAll("\\[", "").replaceAll("\\]", "").trim();
		String ids[] = listas.split(",");
		if (ids != null) {
			for (String id : ids) {
				getReintegrosFarmacia(Integer.parseInt(id.trim()), list);
			}
		}
		Collections.sort(list,
				new Comparator<ReporteOrdenPagoReintegrosFarmacia>() {
					public int compare(ReporteOrdenPagoReintegrosFarmacia o1,
							ReporteOrdenPagoReintegrosFarmacia o2) {
						if (o1.getReintegro().getId_reintegro() == o2
								.getReintegro().getId_reintegro()) {
							return 0;
						} else if (o1.getReintegro().getId_reintegro() < o2
								.getReintegro().getId_reintegro()) {
							return -1;
						} else {
							return 1;
						}
					}
				});
		return list;
	}

	public List<ReporteOrdenPagoReintegros> getReintegros(String listas)
			throws SystemException, NoSuchReintegroEntryException {
		ArrayList<ReporteOrdenPagoReintegros> list = new ArrayList<ReporteOrdenPagoReintegros>();
		listas = listas.replaceAll("\\[", "").replaceAll("\\]", "").trim();
		String ids[] = listas.split(",");
		if (ids != null) {
			for (String id : ids) {

				int id_reintegro = getPrimerReitnegroLista(Integer.parseInt(id
						.trim()));

				String tipo_reintegro = getInstance().getTipoReintegroLista(
						Integer.parseInt(id.trim()), id_reintegro);

				Reintegro reintegro = null;
				if (!tipo_reintegro
						.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					reintegro = ReintegroServiceUtil
							.getReintegroEntry(id_reintegro);
				} else {
					reintegro = ReintegroServiceUtil
							.getReintegroPorIdCuota(id_reintegro);
				}

				if (reintegro.getTipo_reintegro().equalsIgnoreCase(
						WebKeysLiquidaciones.REINTEGRO_PRE)) {
					getReintegros(Integer.parseInt(id.trim()), list);
				} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
						WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
					getReintegrosOdoProtesis(Integer.parseInt(id.trim()), list);
				} else if (reintegro
						.getTipo_reintegro()
						.equalsIgnoreCase(
								WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					getReintegrosOdoOrto(Integer.parseInt(id.trim()), list);
				}

			}
		}
		Collections.sort(list, new Comparator<ReporteOrdenPagoReintegros>() {
			public int compare(ReporteOrdenPagoReintegros o1,
					ReporteOrdenPagoReintegros o2) {
				if (o1.getReintegro().getId_reintegro() == o2.getReintegro()
						.getId_reintegro()) {
					return 0;
				} else if (o1.getReintegro().getId_reintegro() < o2
						.getReintegro().getId_reintegro()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return list;
	}

	private List<ReporteOrdenPagoReintegros> getReintegros(int opId,
			List<ReporteOrdenPagoReintegros> list) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		try {
			String sql = "{call buscar_orden_pago_ospim_reintegros(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroPrestacionNormal reintegroPrestacion = ReintegroPrestacionNormal
						.getMapping(rs, "rp_");
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				PlanPrestacion pp = new PlanPrestacion();
				pp.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(pp);
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegroPrestacion
						.setId_reintegro(reintegro.getId_reintegro());
				List<ReintegroPrestacionNormal> reintegroPrestacionList = new ArrayList<ReintegroPrestacionNormal>();
				reintegroPrestacionList.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(reintegroPrestacionList);
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional
						.getMappingSeccionalParaReintegros(rs, "s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);

				ReporteOrdenPagoReintegros repo = new ReporteOrdenPagoReintegros(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegros repoOriginal = list.get(indexOf);
					repoOriginal.getReintegro().getReintegroPrestacion()
							.add(reintegroPrestacion);
					Collections.sort(repoOriginal.getReintegro()
							.getReintegroPrestacion(),
							new Comparator<ReintegroPrestacion>() {
								public int compare(ReintegroPrestacion o1,
										ReintegroPrestacion o2) {
									return o1.getFecha_prestacion().compareTo(
											o2.getFecha_prestacion());
								}

							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de op", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	private List<ReporteOrdenPagoReintegrosFarmacia> getReintegrosFarmacia(
			int opId, List<ReporteOrdenPagoReintegrosFarmacia> list)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_orden_pago_amtima_reintegros_farmacia(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				ReintegroMedicamento reintegro = ReintegroMedicamento
						.getMapping(rs, "r_");
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Domicilio afiDomicilio = Domicilio.getMappingAfiDomicilio(rs,
						"ad_");
				afiliado.setDomicilioDefault(afiDomicilio);
				Seccional secc = Seccional.getMappingSeccionalParaReintegros(
						rs, "s_");
				reintegro.setSeccional(secc);
				reintegro.setAfiliado(afiliado);

				ReintegroMedicamentoItem reintegroPrestacion = ReintegroMedicamentoItem
						.getMapping(rs, "rp_");
				// el troquel viene en el medicamento
				Medicamento medicamento = Medicamento.getMapping(rs, "m_");
				reintegroPrestacion.setMedicamento(medicamento);
				List<ReintegroMedicamentoItem> listaReintegrosPrest = new ArrayList<ReintegroMedicamentoItem>();
				listaReintegrosPrest.add(reintegroPrestacion);
				reintegro.setMedicamentos(listaReintegrosPrest);

				ReporteOrdenPagoReintegrosFarmacia repo = new ReporteOrdenPagoReintegrosFarmacia(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegrosFarmacia repoOriginal = list
							.get(indexOf);
					repoOriginal.getReintegro().getMedicamentos()
							.add(reintegroPrestacion);

					Collections.sort(repoOriginal.getReintegro()
							.getMedicamentos(),
							new Comparator<ReintegroMedicamentoItem>() {
								public int compare(ReintegroMedicamentoItem o1,
										ReintegroMedicamentoItem o2) {
									return o1.getIdAsString().compareTo(
											o2.getIdAsString());
								}
							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de op", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	private List<ReporteOrdenPagoReintegros> getReintegrosOdoProtesis(int opId,
			List<ReporteOrdenPagoReintegros> list) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		try {
			String sql = "{call buscar_orden_pago_ospim_reintegros_odo_protesis(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroPrestacionOdoProtesis reintegroPrestacion = ReintegroPrestacionOdoProtesis
						.getMapping(rs, "rp_");
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				PlanPrestacion pp = new PlanPrestacion();
				pp.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(pp);
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegroPrestacion
						.setId_reintegro(reintegro.getId_reintegro());
				List<ReintegroPrestacionOdoProtesis> reintegroPrestacionList = new ArrayList<ReintegroPrestacionOdoProtesis>();
				reintegroPrestacionList.add(reintegroPrestacion);
				reintegro.setReintegroPrestacion(reintegroPrestacionList);
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional
						.getMappingSeccionalParaReintegros(rs, "s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);

				ReporteOrdenPagoReintegros repo = new ReporteOrdenPagoReintegros(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegros repoOriginal = list.get(indexOf);
					repoOriginal.getReintegro().getReintegroPrestacion()
							.add(reintegroPrestacion);
					Collections.sort(repoOriginal.getReintegro()
							.getReintegroPrestacion(),
							new Comparator<ReintegroPrestacion>() {
								public int compare(ReintegroPrestacion o1,
										ReintegroPrestacion o2) {
									return o1.getFecha_prestacion().compareTo(
											o2.getFecha_prestacion());
								}

							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de op", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	private List<ReporteOrdenPagoReintegros> getReintegrosOdoOrto(int opId,
			List<ReporteOrdenPagoReintegros> list) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Prestacion prestacion = null;
		try {
			String sql = "{call buscar_orden_pago_ospim_reintegros_odo_orto(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, opId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = ReintegroPrestacionOdoOrtopediaOrtodoncia
						.getMapping(rs, "rp_");
				prestacion = Prestacion.getMapping(rs, "n_");
				prestacion.setId_prestacion(reintegroPrestacion
						.getId_prestacion());
				PlanPrestacion pp = new PlanPrestacion();
				pp.setNomenclador(prestacion);
				reintegroPrestacion.setPlan_prestacion(pp);
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegroPrestacion
						.setId_reintegro(reintegro.getId_reintegro());
				List<ReintegroPrestacionOdoOrtopediaOrtodoncia> reintegroPrestacionList = new ArrayList<ReintegroPrestacionOdoOrtopediaOrtodoncia>();
				reintegroPrestacionList.add(reintegroPrestacion);

				reintegro.setReintegroPrestacion(reintegroPrestacionList);
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional
						.getMappingSeccionalParaReintegros(rs, "s_");
				reintegro.setSeccional(seccional);
				reintegro.setAfiliado(afiliado);
				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> detalles = new ArrayList<DetalleCuota>();
				detalles.add(detalleCuota);
				reintegro.setDetalleCuota(detalles);

				ReporteOrdenPagoReintegros repo = new ReporteOrdenPagoReintegros(
						rs.getBigDecimal("suma_por_afiliado"));
				repo.setAfiliado(afiliado);
				repo.setReintegro(reintegro);
				int indexOf = list.indexOf(repo);
				if (indexOf == -1) {
					list.add(repo);
				} else {
					ReporteOrdenPagoReintegros repoOriginal = list.get(indexOf);
					repoOriginal.getReintegro().getReintegroPrestacion()
							.add(reintegroPrestacion);
					Collections.sort(repoOriginal.getReintegro()
							.getReintegroPrestacion(),
							new Comparator<ReintegroPrestacion>() {
								public int compare(ReintegroPrestacion o1,
										ReintegroPrestacion o2) {
									return o1.getFecha_prestacion().compareTo(
											o2.getFecha_prestacion());
								}

							});
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar reintegros de op", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public int saveReintegroListParaReporte(ReintegroList reintegrosList,
			String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_reporte(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reintegrosList.getSeccional().getId());
			stmt.setString(2, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reintegrosList.setNroLista(rs.getInt(1));
				return reintegrosList.getNroLista();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar lista para reporte de reintegros", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
	}

	public void saveReintegroListParaReporteDetalle(ReintegroList reintegrosList)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_reporte_detalle(?, ?, ?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			for (Reintegro r : reintegrosList.getReintegros()) {
				if (r.getTipo_reintegro() != null
						&& r.getTipo_reintegro()
								.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
					if (r.getDetalleCuota().get(0).getId_reintegro_user() != 0) {
						stmt = con.prepareCall(sql.toString());
						stmt.setInt(1, reintegrosList.getNroLista());
						stmt.setInt(2, r.getDetalleCuota().get(0)
								.getId_reintegro_user());
						stmt.setBigDecimal(3, r.importeTotal());
						stmt.setString(4, r.getTipo_reintegro());
						stmt.executeUpdate();
					}
				} else {
					stmt = con.prepareCall(sql.toString());
					stmt.setInt(1, reintegrosList.getNroLista());
					stmt.setInt(2, r.getId_reintegro());
					stmt.setBigDecimal(3, r.importeTotal());
					stmt.setString(4, r.getTipo_reintegro());
					stmt.executeUpdate();
				}
			}

			con.commit();
		} catch (SQLException e) {
			_log.error(
					"Error al insertar item de lista para reporte de reintegros",
					e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public int saveReintegroListParaPago(ReintegroList reintegrosList,
			String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_pago(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reintegrosList.getSeccional().getId());
			stmt.setString(2, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reintegrosList.setNroLista(rs.getInt(1));
				return reintegrosList.getNroLista();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar lista para orden pago de reintegros",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
	}

	public void saveReintegroListParaPagoDetalle(ReintegroList reintegrosList)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_pago_detalle(?, ?, ?, ?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			for (Reintegro r : reintegrosList.getReintegros()) {
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, reintegrosList.getNroLista());
				stmt.setInt(2, r.getId_reintegro());
				stmt.setBigDecimal(3, r.importeTotal());
				stmt.setString(4, r.getTipo_reintegro());
				stmt.setBoolean(5, r.isTransferenciaBancaria());
				

				stmt.executeUpdate();
			}
			con.commit();
		} catch (SQLException e) {
			_log.error("Error al insertar item de lista para op", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public int saveReintegroFarmaciaListParaPago(
			ReintegroFarmaciaList reintegrosFarmaciaList, String user)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_farmacia_pago(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reintegrosFarmaciaList.getSeccional().getId());
			stmt.setString(2, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reintegrosFarmaciaList.setNroLista(rs.getInt(1));
				return reintegrosFarmaciaList.getNroLista();
			}
		} catch (SQLException e) {
			_log.error(
					"Error al insertar lista para orden pago de reintegros farmacia",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
	}

	public void saveReintegroFarmaciaListParaPagoDetalle(
			ReintegroFarmaciaList reintegrosFarmaciaList)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_farmacia_pago_detalle(?, ?, ?, ?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			for (ReintegroMedicamento r : reintegrosFarmaciaList
					.getReintegros()) {
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, reintegrosFarmaciaList.getNroLista());
				stmt.setInt(2, r.getId_reintegro());
				stmt.setBigDecimal(3, r.getImporteTotal());
				stmt.setString(4, null);
				stmt.setBoolean(5, r.isTransferenciaBancaria());
				stmt.executeUpdate();
			}
			con.commit();
		} catch (SQLException e) {
			_log.error("Error al insertar item de lista para op", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public int saveReintegroFarmaciaListParaReporte(
			ReintegroFarmaciaList reintegrosFarmaciaList, String user)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_farmacia_reporte(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, reintegrosFarmaciaList.getSeccional().getId());
			stmt.setString(2, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reintegrosFarmaciaList.setNroLista(rs.getInt(1));
				return reintegrosFarmaciaList.getNroLista();
			}
		} catch (SQLException e) {
			_log.error(
					"Error al insertar lista para reporte de reintegros de farmacia",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
	}

	public void saveReintegroFarmaciaListParaReporteDetalle(
			ReintegroFarmaciaList reintegrosList) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_lista_reintegro_farmacia_reporte_detalle(?, ?, ?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			for (ReintegroMedicamento r : reintegrosList.getReintegros()) {
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, reintegrosList.getNroLista());
				stmt.setInt(2, r.getId_reintegro());
				stmt.setBigDecimal(3, r.getImporteTotal());
				stmt.setString(4, null);
				stmt.executeUpdate();
			}

			con.commit();
		} catch (SQLException e) {
			_log.error(
					"Error al insertar item de lista para reporte de reintegros",
					e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public boolean existeOPAmtima(Date periodo, String codigoPrestador)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call existe_op_amtima(?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(periodo.getTime()));
			stmt.setString(2, codigoPrestador);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1) == 1;
			}
		} catch (SQLException e) {
			_log.error("Error al buscar OP Amtima por sus items", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return false;
	}

	public boolean existeOPFarmacia(Date periodo, String codigoPrestador)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call existe_op_farmacia(?, ?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(periodo.getTime()));
			stmt.setString(2, codigoPrestador);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1) == 1;
			}
		} catch (SQLException e) {
			_log.error("Error al buscar OP Farmacia por sus items", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return false;
	}

	public List<Reintegro> getReintegrosFromList(int nroList)
			throws SystemException {
		List<Reintegro> lista = new ArrayList<Reintegro>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_reintegros_de_lista(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, nroList);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Reintegro reintegro = new Reintegro(rs.getInt("id_reintegro"),
						rs.getBigDecimal("importe"));
				reintegro.setSeccional(new Seccional(rs.getInt("id_seccional"),
						""));
				lista.add(reintegro);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar OP Amtima por sus items", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<ReintegroList> getReintegrosLists(Integer idSeccional,
			Date fechaIni, Date fechaFin) throws SystemException {
		List<ReintegroList> lista = new ArrayList<ReintegroList>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_reintegroslist(?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (idSeccional==null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, idSeccional);
			}

			if (fechaIni==null) {
				stmt.setNull(2, Types.DATE);
			} else {
				stmt.setDate(2, new java.sql.Date(fechaIni.getTime()));
			}
			
			if (fechaFin==null) {
				stmt.setNull(3, Types.DATE);
			} else {
				stmt.setDate(3, new java.sql.Date(fechaFin.getTime()));
			}

			//stmt.setInt(1, idSeccional);
			//stmt.setDate(2, new java.sql.Date(fechaIni.getTime()));
			//stmt.setDate(3, new java.sql.Date(fechaFin.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			getReintegroList(lista, rs, "PRESTACIONAL");
		} catch (SQLException e) {
			_log.error("Error al buscar listas de reintegros para op ospim", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	public List<ReintegroList> getReintegroAgrupadoSumadoList(int idSeccional,
			Date fechaIni, Date fechaFin, String in) throws SystemException {
		List<ReintegroList> lista = new ArrayList<ReintegroList>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_reintegros_agrupado_suma_total(?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idSeccional);
			stmt.setDate(2, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(3, new java.sql.Date(fechaFin.getTime()));
			stmt.setString(4, in);
			ResultSet rs = stmt.executeQuery();
			getReintegroList(lista, rs, "PRESTACIONAL");
		} catch (SQLException e) {
			_log.error("Error al buscar listas de reintegros para op ospim", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	
	

	private void getReintegroList(List<ReintegroList> lista, ResultSet rs,
			String tipo) throws SQLException {
		while (rs.next()) {

			ReintegroList rList = new ReintegroList(
					rs.getInt("id_lista_reintegro_pago"));
			rList.setTipo(tipo);
			int indexOf = lista.indexOf(rList);
			if (indexOf != -1) {
				rList = lista.get(indexOf);
			} else {
				lista.add(rList);
			}

			List<Reintegro> reintegros = rList.getReintegros();
			if (reintegros == null) {
				reintegros = new ArrayList<Reintegro>();
				rList.setReintegros(reintegros);
				rList.setSeccional(new Seccional(rs.getInt("id_seccional"), ""));
			}

			Reintegro reintegro = new Reintegro(rs.getInt("id_reintegro"),rs.getBigDecimal("importe"));
			reintegro.setSeccional(new Seccional(rs.getInt("id_seccional"), ""));
			
			try {  			
				reintegro.setCbu(rs.getString("cbu"));
				reintegro.setCuilCuenta(rs.getString("cuil_cuenta"));
				reintegro.setEmailCuenta(rs.getString("email_cuenta"));
				reintegro.setApellidoCuenta(rs.getString("apellido_cuenta"));
				reintegro.setNombreCuenta(rs.getString("nombre_cuenta"));
				reintegro.setTransferenciaBancaria(rs.getBoolean("transferencia_bancaria"));
			} catch (Exception e) {
				_log.debug("No tiene datos de cuenta");
			}
			reintegros.add(reintegro);

			try {
				reintegro.setAfiliado(new Afiliado(rs.getString("cuil_titular")));
			} catch (Exception e) {

			}
		}
	}

	private void getReintegroList(List<ReintegroList> lista, ResultSet rs)
			throws SQLException {
		while (rs.next()) {

			ReintegroList rList = new ReintegroList(
					rs.getInt("id_lista_reintegro_pago"));
			int indexOf = lista.indexOf(rList);
			if (indexOf != -1) {
				rList = lista.get(indexOf);
			} else {
				lista.add(rList);
			}

			List<Reintegro> reintegros = rList.getReintegros();
			if (reintegros == null) {
				reintegros = new ArrayList<Reintegro>();
				rList.setReintegros(reintegros);
				rList.setSeccional(new Seccional(rs.getInt("id_seccional"), ""));
			}

			Reintegro reintegro = new Reintegro(rs.getInt("id_reintegro"),
					rs.getBigDecimal("importe"));
			reintegro
					.setSeccional(new Seccional(rs.getInt("id_seccional"), ""));
			reintegros.add(reintegro);

			try {
				reintegro
						.setAfiliado(new Afiliado(rs.getString("cuil_titular")));
			} catch (Exception e) {

			}
		}
	}

	public void saveOPReintegrosList(OrdenPagoOspim op, String user,
			Connection connectionParameter) throws SystemException {
		saveOPReintegrosList(op, user, connectionParameter, WebKeysGlobal.OSPIM);
	}

	public void saveOPReintegrosList(OrdenPago op, String user,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;

			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_op_amtima_lista(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call insertar_op_uoma_lista(?, ?, ?)}";
			}
			con = connectionParameter;
			for (ReintegroList r : op.getReintegrosList()) {
				if (r.getTipo() != null && r.getTipo().equals("FARMACIA")
						&& entidad == WebKeysGlobal.OSPIM) {
					sql = "{call insertar_op_ospim_lista_reintegros_farmacia(?, ?, ?, ?)}";
				} else if (entidad == WebKeysGlobal.OSPIM) {
					sql = "{call insertar_op_ospim_lista(?, ?, ?, ?)}";
				}
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, op.getId());
				stmt.setInt(2, r.getNroLista());
				stmt.setString(3, user);
				if (entidad == WebKeysGlobal.OSPIM) {
					stmt.setDate(4, new java.sql.Date(op.getAlta_fecha().getTime()) );
				}
				
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar op-lista", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void saveOPLiquidacionesList(OrdenPagoOspim op, String user,
			Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_op_ospim_lista_liquidaciones(?, ?, ?, ?)}";
			con = connectionParameter;
			for (Liquidacion l : op.getLiquidacionesList()) {
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, op.getId());
				stmt.setInt(2, l.getId_liquidacion());
				stmt.setString(3, user);
				stmt.setDate(4, new java.sql.Date(op.getAlta_fecha().getTime()) );
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar op-lista-liquidacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public int getPrimerReitnegroLista(int idLista) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_reintegro = 0;
		try {
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_id_primer_reintegro_lista(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idLista);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_reintegro = rs.getInt("id_reintegro");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar lista reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_reintegro;
	}
	


	public String getTipoReintegroLista(int idLista, int id_reintegro)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		String tipo_reintegro = "";
		try {
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_tipo_reintegro_lista(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idLista);
			stmt.setInt(2, id_reintegro);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tipo_reintegro = rs.getString("tipo_reintegro");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar en la lista de reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tipo_reintegro;
	}

	public List<ReporteOrdenPagoOspim> reporteOrdenPagoOspim(Date fechaInicio,
			Date fechaFin) throws SystemException {
		List<ReporteOrdenPagoOspim> lista = null;
		Connection con = null;
		CallableStatement stmt = null;
		try {
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call reporte_ordenes_pago_ospim(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ReporteOrdenPagoOspim>();
			while (rs.next()) {
				lista.add(ReporteOrdenPagoOspim.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al buscar lista reintegro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public BigDecimal getUltimoNumeroChequeOP(int idCtaBcria, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_ultimo_cheque_de_op(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ultimo_cheque_amtima_de_op(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ultimo_cheque_de_op_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCtaBcria);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				BigDecimal nro = rs.getBigDecimal(1);
				return nro != null ? nro : BigDecimal.ZERO;
			}
		} catch (Exception e) {
			_log.error("Error al buscar ultimo nro cheque", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return BigDecimal.ZERO;
	}

	public void save(Pago pago, OrdenPago ordenPago,
			Connection connectionParameter, int entidad) throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_orden_pago_ospim_pagos(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_orden_pago_amtima_pagos(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_orden_pago_uoma_pagos(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}

			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, ordenPago.getId());
			if (pago instanceof Anticipo) {
				Anticipo ant = (Anticipo) pago;
				Comprobante anticipo = ant.getAnticipo();
				stmt.setInt(2, anticipo.getPtoVenta());
				stmt.setString(3, anticipo.getTipoComprobante());
				stmt.setString(4, anticipo.getNroComprobante());
				stmt.setString(5, anticipo.getCuit());
				stmt.setString(6, anticipo.getLetraComprobante());
				stmt.setInt(7, anticipo.getSucuComprobante());
			} else {
				stmt.setNull(2, Types.SMALLINT);
				stmt.setString(3, null);
				stmt.setString(4, null);
				stmt.setString(5, null);
				stmt.setString(6, null);
				stmt.setNull(7, Types.INTEGER);
			}

			if (pago instanceof Cheque) {
				Cheque ch = (Cheque) pago;
				stmt.setBigDecimal(8, ch.getNumero());
				stmt.setInt(9, ch.getBanco().getId_banco());
				stmt.setInt(10, ch.getCuentaBancaria().getId_cuenta_bcria());
			} else {
				stmt.setBigDecimal(8, null);
				stmt.setNull(9, Types.INTEGER);
				stmt.setNull(10, Types.INTEGER);
			}

			if (pago instanceof RetencionGanancias) {
				RetencionGanancias ret = (RetencionGanancias) pago;
				stmt.setInt(11, ret.getCuentaBancaria().getId_cuenta_bcria());
				stmt.setBigDecimal(12, ret.getImporte());
			} else {
				stmt.setNull(11, Types.INTEGER);
				stmt.setBigDecimal(12, null);
			}

			if (pago instanceof PagoBancario) {
				PagoBancario deb = (PagoBancario) pago;
				stmt.setInt(13, deb.getCuentaBancaria().getId_cuenta_bcria());
				stmt.setBigDecimal(14, deb.getImporte());
				stmt.setString(15, deb.getNumero());
				stmt.setInt(16, deb.getTipo_pago());
				stmt.setString(17, deb.getCuilCuenta());
				stmt.setString(18, deb.getEmailCuenta());
				stmt.setString(19, deb.getApellidoCuenta());
				stmt.setString(20, deb.getNombreCuenta());		
			} else {
				stmt.setNull(13, Types.INTEGER);
				stmt.setBigDecimal(14, null);
				stmt.setString(15, null);
				stmt.setNull(16, Types.INTEGER);
				stmt.setString(17, null);
				stmt.setString(18,null);
				stmt.setString(19, null);
				stmt.setString(20,null);		
			}

			if (pago instanceof Caja) {
				Caja caja = (Caja) pago;
				stmt.setBigDecimal(14, caja.getImporte());
				stmt.setInt(16, caja.getTipo_pago() );
//				stmt.setInt(16, caja.get_Tipo_pago());
			}
			
			if (pago instanceof PagoSinSalidaDeFondos) {
				PagoSinSalidaDeFondos caja = (PagoSinSalidaDeFondos) pago;
				stmt.setBigDecimal(14, caja.getImporte());
				stmt.setInt(16, caja.getTipo_pago() );
			}

			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al insertar pago", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void getPagos(OrdenPago op, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		String sql = "{call buscar_orden_pago_ospim_pagos(?)}";
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_orden_pago_amtima_pagos(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_orden_pago_uoma_pagos(?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, op.getId());

			List<OrdenPago.FormaPago> lista = new ArrayList<OrdenPago.FormaPago>();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				OrdenPago.FormaPago mapping = OrdenPago.FormaPago.getMapping(rs, "OPP__", entidad);
				mapping.setOtraOpCheque(rs.getInt("cheque_otra_id_orden_pago"));
				lista.add(mapping);
			}
			if (lista != null && !lista.isEmpty()) {
				op.setFormaPago(lista);
			}
		} catch (Exception e) {
			_log.error("Error al buscar orden pago", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public String[] getUltimaRazonSocialChequeYDestinoOP(String cuit,
			String sucu, Integer seccional, int entidad) {
		String[] array = null;
		Connection con = null;
		CallableStatement stmt = null;
		
//		_log.debug("getUltimaRazonSocialChequeYDestinoOP " + cuit + " " + sucu + " " + seccional );
		try {
			String sql = "{call buscar_ultima_razon_social_cheque_op_destino_empresa(?,?,?)}";
			// String sql =
			// "{call buscar_ultima_razon_social_cheque_op_destino(?,?,?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ultima_razon_social_cheque_op_uoma_empresa(?,?,?)}";
			} else if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ultima_razon_social_cheque_op_amtima_empresa(?,?,?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setString(2, sucu);
			if (seccional == null || seccional==0) {
				stmt.setNull(3, Types.INTEGER);
			} else {
				stmt.setInt(3, seccional);
			}
			array = new String[5];
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				array[0] = rs.getString("a_nombre_de");
				array[1] = rs.getString("destino");
				array[2] = rs.getString("cbu");
				array[3] = rs.getString("email");
				array[4] = rs.getString("razon_social");
				break; // cubrir que no retorne más de 1 resultado.
//				return array;
			}
		} catch (Exception e) {
			_log.error("Error al buscar ultimo nro cheque", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return array;
	}

	public List<OrdenPagoOspim> getPagosOP(Date fechaInicio, Date fechaFin,
			Connection connection) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> ops = new ArrayList<OrdenPagoOspim>();
		try {
			String sql = "{call buscar_orden_pago_ospim_pagos_por_fechas(?, ?)}";
			_log.debug("obteniendo conexion");
			if (connection == null) {
				con = ConnectionHelper.getReportesOspimConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				OrdenPago.FormaPago fp = OrdenPago.FormaPago.getMapping(rs,
						"OPP__", WebKeysGlobal.OSPIM);
				int id = rs.getInt("OP__id_orden_pago");

				if (fp.getPago() instanceof Cheque) {
					Cheque cheque = (Cheque) fp.getPago();
					cheque.setBaja_fecha(rs.getDate("ch__baja_fecha"));
				}

				OrdenPagoOspim op = new OrdenPagoOspim(id);
				if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getFormaPago().add(fp);
				} else {
					op.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
					op.getFormaPago().add(fp);
					ops.add(op);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar orden pago", e);
			throw e;
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return ops;
	}

	public List<OrdenPago> getPagosOP(Date fechaInicio, Date fechaFin,
			int entidad, Connection connection) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPago> ops = new ArrayList<OrdenPago>();
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_orden_pago_amtima_pagos_por_fechas(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_orden_pago_uoma_pagos_por_fechas(?, ?)}";
			}
			_log.debug("obteniendo conexion");
			if (connection == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
/*Anterior				
				OrdenPagoAmtima.FormaPago fp = OrdenPagoAmtima.FormaPago
						.getMapping(rs, "OPP__", entidad);

				int id = rs.getInt("OP__id_orden_pago");

				if (fp.getPago() instanceof Cheque) {
					Cheque cheque = (Cheque) fp.getPago();
					cheque.setBaja_fecha(rs.getDate("ch__baja_fecha"));
				}

				OrdenPagoAmtima op = new OrdenPagoAmtima(id);
				if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getFormaPago().add(fp);
				} else {
					op.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
					op.getFormaPago().add(fp);
					ops.add(op);
				}
*/	
//Nuevo para Devengado				
				if (entidad == WebKeysGlobal.AMTIMA) {
				
				   OrdenPagoAmtima.FormaPago fp = OrdenPagoAmtima.FormaPago
						.getMapping(rs, "OPP__", entidad);

				   int id = rs.getInt("OP__id_orden_pago");

				   if (fp.getPago() instanceof Cheque) {
					Cheque cheque = (Cheque) fp.getPago();
					cheque.setBaja_fecha(rs.getDate("ch__baja_fecha"));
				   }

				   OrdenPagoAmtima op = new OrdenPagoAmtima(id);
				   if (ops.contains(op)) {
					ops.get(ops.indexOf(op)).getFormaPago().add(fp);
				   } else {
					op.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
					op.getFormaPago().add(fp);
					ops.add(op);
				   }
				}else {
					OrdenPagoUoma.FormaPago fp = OrdenPagoUoma.FormaPago
							.getMapping(rs, "OPP__", entidad);

					   int id = rs.getInt("OP__id_orden_pago");

					   if (fp.getPago() instanceof Cheque) {
						Cheque cheque = (Cheque) fp.getPago();
						cheque.setBaja_fecha(rs.getDate("ch__baja_fecha"));
					   }

					   OrdenPagoUoma op = new OrdenPagoUoma(id);
					   if (ops.contains(op)) {
						ops.get(ops.indexOf(op)).getFormaPago().add(fp);
					   } else {
						op.setFormaPago(new ArrayList<OrdenPago.FormaPago>());
						op.getFormaPago().add(fp);
						ops.add(op);
					   }
				}
//Fin nuevo				
			}
		} catch (Exception e) {
			_log.error("Error al buscar orden pago", e);
			throw e;
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return ops;
	}

	public void sacarAnulacionChequesReactivados(Integer nro,
			String screenName, Connection connectionParameter, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call sacar_anulacion_cheques_op(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call sacar_anulacion_cheques_amtima_op(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.sacar_anulacion_cheques_uoma_op(?)}";
			}

			_log.debug("obteniendo conexion");
			if (connectionParameter != null) {
				con = connectionParameter;
			} else {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, nro);
			stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al scar anulacion cheques de orden pago ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void reactivar(Integer nro, Connection connectionParameter,
			int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call reactivar_orden_pago_ospim(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call reactivar_orden_pago_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.reactivar_orden_pago_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, nro);
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al anular orden pago ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public boolean verificarChequesReutilizados(Integer nro,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = connectionParameter;
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_cheques_reutilizados(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_cheques_amtima_reutilizados(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_cheques_reutilizados_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, nro);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_cheques_reutilizados ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return true;
	}

	public boolean verificarComprobantesYaPagados(Integer nro,
			Connection connectionParameter, int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_comprobantes_ya_pagados(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_comprobantes_amtima_ya_pagados(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_comprobantes_uoma_ya_pagados(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, nro);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_comprobantes_ya_pagados ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return true;
	}

	public boolean verificarAnticiposUtilizados(Integer nroOpOspim,
			Connection connectionParameter, int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_anticipos_utilizados(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_anticipos_utilizados_amtima(?)}";
			} else if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call uoma.verificar_anticipos_utilizados_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, nroOpOspim);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_comprobantes_ya_pagados ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return true;
	}

	public boolean verificarComprobantesAnulados(Integer nroOpOspim,
			Connection connectionParameter, int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_comprobantes_anulados(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_comprobantes_amtima_anulados(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_comprobantes_anulados_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, nroOpOspim);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_comprobantes_anulados ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return true;
	}

	public boolean verificarAnticiposNoPagados(Integer nroOpOspim,
			Connection connectionParameter, int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_anticipos_no_pagados(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_anticipos_amtima_no_pagados(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_anticipos_no_pagados_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, nroOpOspim);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_anticipos_no_pagados ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return true;
	}

	public void saveOPReintegrosList(OrdenPagoAmtima op, String user,
			Connection con) throws SystemException {
		saveOPReintegrosList(op, user, con, WebKeysGlobal.AMTIMA);
	}

	public List<ReintegroList> getReintegrosFarmaciasLists(Integer idSeccional,
			Date fechaIni, Date fechaFin) throws SystemException {
		List<ReintegroList> lista = new ArrayList<ReintegroList>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_reintegros_farmacia_list(?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (idSeccional==null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, idSeccional);
			}

			if (fechaIni==null) {
				stmt.setNull(2, Types.DATE);
			} else {
				stmt.setDate(2, new java.sql.Date(fechaIni.getTime()));
			}
			
			if (fechaFin==null) {
				stmt.setNull(3, Types.DATE);
			} else {
				stmt.setDate(3, new java.sql.Date(fechaFin.getTime()));
			}
			
			/*
			stmt.setInt(1, idSeccional);
			stmt.setDate(2, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(3, new java.sql.Date(fechaFin.getTime()));
			*/
			ResultSet rs = stmt.executeQuery();
			getReintegroList(lista, rs, "FARMACIA");
		} catch (SQLException e) {
			_log.error("Error al buscar listas de reintegros para op ospim", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public boolean verificarAnticiposReUtilizados(Integer nro,
			Connection connectionParameter, int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_anticipos_reutilizados(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_anticipos_amtima_reutilizados(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_anticipos_reutilizados_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, nro);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_anticipos_no_pagados ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return true;
	}

	public List<Cheque> verificarOpConChequesCanjeados(Integer id,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Cheque> listaCheques = null;
		try {
			String sql = null;
			if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call verificar_op_cheques_canjeados(?)}";
			} else if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_op_cheques_canjeados_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_op_cheques_canjeados_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			con = connectionParameter;
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			listaCheques = new ArrayList<Cheque>();
			while (rs.next()) {
				Cheque chq = Cheque.getMapping(rs, "ch__");
				chq.setEstado(Cheque.Estado.getMapping(rs, "es__"));
				chq.setBanco(Banco.getMapping(rs, "ba__"));
				listaCheques.add(chq);
			}
		} catch (Exception e) {
			_log.error("Error al buscar cheques", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return listaCheques;
	}

	public boolean verificarOPCreadaEnCanje(Integer id,
			Connection connectionParameter, int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call verificar_op_creada_en_canje(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_op_creada_en_canje_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.verificar_op_creada_en_canje_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet executeQuery = stmt.executeQuery();
			while (executeQuery.next()) {
				return executeQuery.getInt(1) == 1;
			}
		} catch (Exception e) {
			_log.error("Error al verificar_op_creada_en_canje ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return true;
	}
	
	public int obtenerProximoIdOrdenPago() throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int idOP = 0;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call propone_id_orden_pago_ospim() }";
			
			_log.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());

			ResultSet executeQuery = stmt.executeQuery();
			
			while (executeQuery.next()) {
				idOP =  executeQuery.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al proponer Id Orden de Pago ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return idOP;
	}
	
	public List<OrdenPagoOspim> reporteOrdenPagoInterbanking(Date fechaInicio, Date fechaFin,Integer tipoPago,Integer ctaBcria,
			int entidad,Connection connection) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<OrdenPagoOspim> ops = new ArrayList<OrdenPagoOspim>();
		try {
			String sql = "{call buscar_orden_pago_ospim_pagos_por_fechas_interbanking(?,?,?,?)}";
			_log.debug("obteniendo conexion");
			if (connection == null) {
				con = ConnectionHelper.getReportesOspimConnection();
			} else {
				con = connection;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			stmt.setInt(3, tipoPago);
			stmt.setInt(4, ctaBcria);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				OrdenPagoOspim op = new OrdenPagoOspim();
				op.setId(rs.getInt("id_orden_pago"));
				op.setAcreedor(new Empresa(rs.getString("cuit"),""));
				ops.add(op);
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar orden pago para interbanking", e);
			throw e;
		} finally {
			if (connection == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
		return ops;
	}
	
	
	public void eliminarPagos(Integer nro, Connection connectionParameter,
			int entidad) throws SystemException {
		CallableStatement stmt = null;
		try {
			String sql = "{call eliminar_pagos_orden_pago_ospim(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call eliminar_pagos_orden_pago_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.eliminar_pagos_orden_pago_uoma(?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, nro);
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al eliminar pagos orden pago ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}
	
	
	public void saveRetencion(Pago pago, OrdenPago ordenPago,
			Connection connectionParameter, int entidad) throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "";
			String tipo="";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_orden_pago_retencion_pagos(?,?,?,?,?,?)}";
			}
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, ordenPago.getId());
			
			if (pago instanceof RetencionIIBB) {
				RetencionIIBB ret = (RetencionIIBB) pago;
				stmt.setString(2, WebKeysUOMA.RET_IIBB);
				stmt.setInt(3, ret.getCuentaBancaria().getId_cuenta_bcria());
				stmt.setBigDecimal(4, ret.getImporte());
				stmt.setInt(5, ret.getJurisdiccion());
				stmt.setDouble(6, ret.getAlicuota()!=null?ret.getAlicuota():0D);
			}else if (pago instanceof RetencionIVA) {
				RetencionIVA ret = (RetencionIVA) pago;
				stmt.setString(2, WebKeysUOMA.RET_IVA);
				stmt.setInt(3, ret.getCuentaBancaria().getId_cuenta_bcria());
				stmt.setBigDecimal(4, ret.getImporte());
				stmt.setNull(5, Types.INTEGER);
				stmt.setNull(6, Types.DOUBLE);
			}else {
				stmt.setNull(2, Types.VARCHAR);
				stmt.setNull(3, Types.INTEGER);
				stmt.setBigDecimal(4, null);
				stmt.setNull(5, Types.INTEGER);
				stmt.setNull(6, Types.DOUBLE);
			}
			
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al insertar pago Retenciones", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}


	public Double getAlicuotaARBA(String cuit,Date fecha,String tipo) {
		Connection con = null;
		CallableStatement stmt = null;
		Double alicuota = 0D;
		try {
			String sql = "{call uoma.traer_alicuota_arba(?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setString(3, tipo);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				alicuota = rs.getDouble(1);
			}
		} catch (Exception e) {
			_log.error("Error al buscar alicuota ARBA", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return alicuota;

	}

	public void saveOPsFromLiquidaciones( String lista,String ctaBcria,	Connection connectionParameter) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call genera_ordenes_pago_from_liquidaciones(?,?)}";
			if (connectionParameter == null) {
				con = ConnectionHelper.getReportesOspimConnection();
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,lista);
			stmt.setInt(2,Integer.parseInt(ctaBcria));
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al generar OPs desde liquidaciones", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			} else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
}

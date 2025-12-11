package ar.com.ospim.tesoreria.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.novedades.exception.PeriodoArchivoDuplicadoException;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.MovimientoBancarioSubdiarioEgreso;
import ar.com.ospim.tesoreria.beans.MovimientoBancoCheque;
import ar.com.ospim.tesoreria.beans.MovimientoBancoItem;
import ar.com.ospim.tesoreria.beans.MovimientoBancoReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.tesoreria.reportes.ReporteAcreditacionesAFIPExcel.ResumenExtractoBancario;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="MovimientoBancarioServiceImpl.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class MovimientoBancarioServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(MovimientoBancarioServiceImpl.class);

	public int grabaMovimientoBancario(MovimientoBancario mov, String usr,
			Connection connectionParameter, int entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;
		int id = 0;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call inserta_movimiento_banco_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call inserta_movimiento_banco(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.inserta_movimiento_banco_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}
			if (connectionParameter != null) {
				con = connectionParameter;
			} else {
				_log.debug("creando conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			}
			int cont=1;
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(cont++, new java.sql.Date(mov.getFecha_movimiento()
					.getTime()));
			stmt.setInt(cont++, mov.getTipo_mov().getId_tipo_mov());
			stmt.setInt(cont++, mov.getCta_bcria().getId_cuenta_bcria());
			stmt.setBoolean(cont++, mov.isDeb_cred());
			stmt.setNull(cont++, Types.INTEGER);
			if (mov.getChequera().getId_chequera() == 0) {
				stmt.setNull(cont++, Types.INTEGER);
			} else {
				stmt.setInt(cont++, mov.getChequera().getId_chequera() != 0 ? mov
						.getChequera().getId_chequera() : null);
			}
			if (mov.getNro_comprobante() == null
					|| mov.getNro_comprobante().trim().equals("")) {
				stmt.setNull(cont++, Types.VARCHAR);
			} else {
				stmt.setString(cont++, mov.getNro_comprobante());
			}
			stmt.setDate(cont++, new java.sql.Date(mov.getFecha_comprobante()
					.getTime()));
			stmt.setDouble(cont++, mov.getImporte().doubleValue()!=0?mov.getImporte().doubleValue():mov.getImporteCheques().doubleValue());
			stmt.setString(cont++, mov.getDescripcion());
			stmt.setBoolean(cont++, mov.isImprime_cheque());
			stmt.setBoolean(cont++, mov.isNo_a_la_orden());			
			
			if(entidad==WebKeysGlobal.OSPIM && mov.getFechaSur()!=null){
				stmt.setDate(cont++, new java.sql.Date(mov.getFechaSur().getTime()));
			}else if(entidad==WebKeysGlobal.OSPIM){
				stmt.setNull(cont++, Types.DATE);
			}
			
			if(entidad==WebKeysGlobal.OSPIM){
				stmt.setString(cont++, mov.getNroExpedienteSur());
			}
			
			stmt.setString(cont++, usr);
			
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				id = result.getInt(1);
			}

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al grabar Movimiento Bcrio", e);
			id = -1;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id;
	}

	public int editaMovimientoBancario(MovimientoBancario mov, String usr,
			Connection connectionParameter, int entidad) throws SystemException,
			SQLException {
		int result = 0;
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call edita_movimiento_banco_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call edita_movimiento_banco(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.edita_movimiento_banco_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}
			
			if (connectionParameter == null) {
				_log.debug("creando conexion");
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(mov.getFecha_movimiento()
					.getTime()));
			stmt.setInt(2, mov.getTipo_mov().getId_tipo_mov());
			stmt.setInt(3, mov.getCta_bcria().getId_cuenta_bcria());
			stmt.setBoolean(4, mov.isDeb_cred());
			stmt.setNull(5, Types.INTEGER);
			if (mov.getChequera().getId_chequera() == 0) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, mov.getChequera().getId_chequera() != 0 ? mov
						.getChequera().getId_chequera() : null);
			}
			if (mov.getNro_comprobante() == null
					|| mov.getNro_comprobante().trim().equals("")) {
				stmt.setNull(7, Types.VARCHAR);
			} else {
				stmt.setString(7, mov.getNro_comprobante());
			}
			stmt.setDate(8, new java.sql.Date(mov.getFecha_comprobante()
					.getTime()));
			stmt.setDouble(9,  mov.getImporteCheques()!=null && mov.getImporteCheques().compareTo(BigDecimal.ZERO)>0 && mov.getImporteCheques().compareTo(mov.getImporte())!=0?
					mov.getImporteCheques().doubleValue():mov.getImporte().doubleValue());
			stmt.setString(10, mov.getDescripcion());
			stmt.setBoolean(11, mov.isImprime_cheque());
			stmt.setBoolean(12, mov.isNo_a_la_orden());
			stmt.setString(13, usr);
			stmt.setInt(14, mov.getId_movimiento());

			result = stmt.executeUpdate();
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al editar Movimiento Bcrio", e);	
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}

		return result;
	}

	public List<MovimientoBancario> buscaMovimientoBcrio(Date fecha_desde,
			Date fecha_hasta, int id_cta_bcria, String descripcion, int tipoMov, int entidad)
			throws Exception {
		List<MovimientoBancario> result = null;
		Connection con = null;
		CallableStatement stmt = null;
		String sql =null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call buscar_movimientos_bcrios_amtima(?,?,?,?,?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call buscar_movimientos_bcrios(?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_movimientos_bcrios_uoma(?,?,?,?,?)}";
			}
			
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha_desde.getTime()));
			stmt.setDate(2, new java.sql.Date(fecha_hasta.getTime()));
			if (id_cta_bcria != 0) {
				stmt.setInt(3, id_cta_bcria);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (descripcion != null && !descripcion.equals("")) {
				stmt.setString(4, descripcion);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			if (tipoMov != 0) {
				stmt.setInt(5, tipoMov);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<MovimientoBancario>();
			while (rs.next()) {
				MovimientoBancario mov = new MovimientoBancario(
						rs.getInt("id_movimiento"),
						rs.getDate("fecha_movimiento"),
						rs.getString("tipo_mov"), rs.getString("cuenta_bcria"),
						rs.getDate("fecha_comprobante"),
						rs.getString("nro_compro"),
						rs.getString("mov_descripcion"),
						rs.getDouble("importe"));
				result.add(mov);
			}
		} catch (Exception e) {
			_log.error("Error al busca Movimiento Bcrio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;

	}

	public MovimientoBancario get(int id_movimiento, int entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		MovimientoBancario mov = null;
		String sql = null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call buscar_movimiento_bcrio_amtima(?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call buscar_movimiento_bcrio(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_movimiento_bcrio_uoma(?)}";
			}
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_movimiento);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				mov = new MovimientoBancario(rs.getInt("id_movimiento"),
						rs.getDate("fecha_movimiento"),
						rs.getInt("id_tipo_mov"), rs.getInt("id_cuenta_bcria"),
						rs.getBoolean("deb_cred"), rs.getInt("id_chequera"),
						rs.getString("nro_compro"),
						rs.getDate("fecha_comprobante"),
						rs.getDouble("importe"), rs.getString("descripcion"),
						rs.getBoolean("imprime_cheque"),
						rs.getBoolean("no_a_la_orden"),
						rs.getDate("concilia_fecha"),
						rs.getString("concilia_usr"), rs.getDate("alta_fecha"),
						rs.getString("alta_usr"),
						rs.getDate("fecha_pago_sur"),
						rs.getString("nro_expe_sur"));
			}
		} catch (Exception e) {
			_log.error("Error al get movimiento bcrio", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return mov;

	}

	public int borraMovimientoBcrio(int id_movimiento, String user,
			Connection connectionParameter, int entidad) throws Exception {
		int result = 0;
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call borra_movimiento_bcrio_amtima(?,?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call borra_movimiento_bcrio(?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.borra_movimiento_bcrio_uoma(?,?)}";
			}
			
			_log.debug("creando conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_movimiento);
			stmt.setString(2, user);

			result = stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al borrar Movimiento Bcrio", e);
			if (connectionParameter != null) {
				throw e;
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}

		return result;

	}

	public void save(MovimientoBancario mov, Cheque ch,
			Cheque.Estado estadoAntiguo, Cheque.Estado estadoNuevo,
			ReciboIngreso ri, String screenName, Connection connectionParameter, int entidad)
			throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call inserta_movimiento_banco_item_amtima(?, ?, ?, ?, ?, ?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call inserta_movimiento_banco_item(?, ?, ?, ?, ?, ?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.inserta_movimiento_banco_item_uoma(?, ?, ?, ?, ?, ?, ?, ?)}";
			}
			_log.debug("creando conexion");
			if (connectionParameter != null) {
				con = connectionParameter;
			} else {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, mov.getId_movimiento());
			if (ch != null) {
				stmt.setBigDecimal(2, ch.getNumero());
				stmt.setInt(3, ch.getBanco().getId_banco());
			} else {
				stmt.setBigDecimal(2, null);
				stmt.setNull(3, Types.INTEGER);
			}
			if (estadoAntiguo != null) {
				stmt.setInt(4, estadoAntiguo.getId());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			if (estadoNuevo != null) {
				stmt.setInt(5, estadoNuevo.getId());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			if (ri != null) {
				stmt.setInt(6, ri.getId());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			
			if (ch != null && ch.getCuentaBancaria() !=null) {
				stmt.setInt(7,ch.getCuentaBancaria().getId_cuenta_bcria());
			} else {
				stmt.setNull(7, Types.INTEGER);
			}
			
			stmt.setString(8, screenName);

			stmt.executeUpdate();

			if (connectionParameter == null) {
				con.commit();
			}

		} catch (Exception e){
			_log.error("Error al insertar Movimiento Bcrio", e);
			if (connectionParameter == null) {
				con.rollback();
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void saveCheque(MovimientoBancario mov, Cheque ch,
			Cheque.Estado estadoAntiguo, Cheque.Estado estadoNuevo,
			String screenName, Connection connectionParameter, int entidad)
			throws SQLException {
		save(mov, ch, estadoAntiguo, estadoNuevo, null, screenName,
				connectionParameter, entidad);
	}

	public void save(MovimientoBancario mov, ReciboIngreso ri,
			String screenName, Connection con, int entidad) throws SQLException {
		save(mov, null, null, null, ri, screenName, con, entidad);
	}

	public List<MovimientoBancoCheque> getChequesEstadoOriginal(int movId,
			Cheque.Estado estado, int entidad) throws SystemException {
		_log.debug("Buscando mov bcrio cheques estado original estado");
		Connection con = null;
		CallableStatement stmt = null;
		List<MovimientoBancoCheque> cheques = null;
		String sql =null;
		try {
			con = ConnectionHelper.getConnection();
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call buscar_movimiento_banco_cheques_estado_original_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call buscar_movimiento_banco_cheques_estado_original(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_movimiento_banco_cheques_estado_original_uoma(?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, movId);
			stmt.setInt(2, estado.getId());
			ResultSet rs = stmt.executeQuery();
			cheques = new ArrayList<MovimientoBancoCheque>();
			while (rs.next()) {
				Cheque chq = Cheque.getMapping(rs, "ch__");
				chq.setEstado(Cheque.Estado.getMapping(rs, "es__"));
				chq.setBanco(Banco.getMapping(rs, "ba__"));
				chq.setNroRecibo(rs.getString("nro_recibo"));
				chq.setFechaRecibo(rs.getDate("fecha_recibo"));
				MovimientoBancoCheque mbch = new MovimientoBancoCheque();
				mbch.setId(rs.getInt("mbi__id"));
				mbch.setCheque(chq);
				cheques.add(mbch);
			}
		} catch (Exception e) {
			_log.error("Error buscar movbcrio cheques estado original estado",e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cheques;
	}

	public List<MovimientoBancoReciboIngreso> getEfectivo(int movId, int entidad)
			throws SystemException {
		_log.debug("Buscando mov bcrio efectivo");
		Connection con = null;
		CallableStatement stmt = null;
		List<MovimientoBancoReciboIngreso> ris = null;
		String sql = null;
		try {
			con = ConnectionHelper.getConnection();
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call buscar_movimiento_banco_efectivo_amtima(?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call buscar_movimiento_banco_efectivo(?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_movimiento_banco_efectivo_uoma(?)}";
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, movId);
			ResultSet rs = stmt.executeQuery();
			ris = new ArrayList<MovimientoBancoReciboIngreso>();
			while (rs.next()) {
				ReciboIngreso ri = new ReciboIngreso();
				ri.setId(rs.getInt("id_recibo_ingreso"));
				Efectivo ef = new Efectivo();
				ef.setImporte(rs.getBigDecimal("importe"));
				ef.setFecha(rs.getDate("fecha"));
				ef.setEstado(new Efectivo.Estado(rs
						.getInt("id_estado_efectivo"), rs
						.getString("descripcion")));
				ef.setNroRecibo(rs.getString("nro_recibo"));
				ef.setFechaRecibo(rs.getDate("fecha_recibo"));
				ri.setIngreso(ef);

				MovimientoBancoReciboIngreso mbri = new MovimientoBancoReciboIngreso();
				mbri.setReciboIngreso(ri);
				mbri.setId(rs.getInt("mbi__id"));
				
				ris.add(mbri);
			}
		} catch (Exception e) {
			_log.error("Error buscando mov bcrio efectivo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ris;
	}

	public void borraMovimientoBancoItem(MovimientoBancario mov,
			MovimientoBancoItem mbi, Connection connectionParameter, int entidad)
			throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;

		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call borra_movimiento_banco_item_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call borra_movimiento_banco_item(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.borra_movimiento_banco_item_uoma(?, ?)}";
			}
			_log.debug("creando conexion");
			if (connectionParameter != null) {
				con = connectionParameter;
			} else {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, mov.getId_movimiento());
			stmt.setInt(2, mbi.getId());
			stmt.executeUpdate();

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.debug("Error al borrar items", e);
			if (connectionParameter != null) {
				throw e;
			}else{
				con.rollback();
			}

		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}

		}
	}

	public Map<Date, List<ResumenExtractoBancario>> getResumenExtractoBancario(
			Date fechaIni, Date fechaFin) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		Map<Date, List<ResumenExtractoBancario>> resultados = null;
		try {
			String sql = "{call buscar_resumen_extracto_bancario(?, ?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			resultados = new HashMap<Date, List<ResumenExtractoBancario>>();
			while (rs.next()) {
				ResumenExtractoBancario resumen = ResumenExtractoBancario.getMapping(rs);
				if (resultados.get(resumen.getFecha()) == null) {
					List<ResumenExtractoBancario> lista = new ArrayList<ResumenExtractoBancario>();
					lista.add(resumen);
					resultados.put(resumen.getFecha(), lista);
				} else {
					resultados.get(resumen.getFecha()).add(resumen);
				}
			}
		} catch (SQLException e) {
			_log.debug("Error al buscar resumen extracto bancario", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return resultados;
	}

	public List<? extends ItemSubdiarioEgreso> reporteParaSubdiario(
			Date fechaInicio, Date fechaFin, int entidad) throws Exception {
		List<MovimientoBancarioSubdiarioEgreso> result = null;
		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = null;
			if (entidad==WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_movimientos_bcrios_amtima_subdiario_egreso(?, ?)}";
			} else if (entidad==WebKeysGlobal.OSPIM) {
				sql = "{call buscar_movimientos_bcrios_subdiario_egreso(?, ?)}";
			} else if (entidad==WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_movimientos_bcrios_subdiario_egreso_uoma(?, ?)}";
			}
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<MovimientoBancarioSubdiarioEgreso>();
			while (rs.next()) {
				MovimientoBancarioSubdiarioEgreso mov = new MovimientoBancarioSubdiarioEgreso(
						rs.getInt("id_movimiento"),
						rs.getDate("fecha_movimiento"),
						rs.getString("tipo_mov"), rs.getString("cuenta_bcria"),
						rs.getDate("fecha_comprobante"),
						rs.getString("nro_compro"),
						rs.getString("mov_descripcion"),
						rs.getDouble("importe"));
				mov.setCuit(rs.getString("cuit"));
				mov.setBaja_fecha(rs.getDate("baja_fecha"));
				CuentaBancaria cta = new CuentaBancaria();
				cta.setNro_cuenta(rs.getInt("CTA__nro_cuenta"));
				cta.setSucursal(rs.getInt("CTA__sucursal"));
				cta.setDescripcion(rs.getString("CTA__descripcion"));
				cta.setCuentaAsociada(new PlanCuentas(rs
						.getString("CTA__numero_plan_cuenta_asociada"), rs
						.getString("CTA__cuenta_asociada")));
				cta.getCuentaAsociada().setId(rs.getInt("CTA__cuenta_asociada_id"));
				mov.setCta_bcria(cta);

				TipoMovBcrio tipo = new TipoMovBcrio();
				PlanCuentas cuentaAsociada = new PlanCuentas();
				cuentaAsociada.setCuenta(rs.getString("TIPO__cuenta_asociada"));
				cuentaAsociada.setNumero(rs.getString("TIPO__numero_plan_cuenta_asociada"));
				cuentaAsociada.setId(rs.getInt("TIPO__cuenta_asociada_id"));
				tipo.setCuentaAsociada(cuentaAsociada);
				tipo.setDescripcion(rs.getString("tipo_mov"));
				tipo.setId_tipo_mov(rs.getInt("TIPO__id_tipo_mov"));
				mov.setNroChequeRechazado(rs
						.getBigDecimal("nro_cheque_rechazado"));

				mov.setTipo_mov(tipo);

				if (mov.getBaja_fecha() != null) {
					MovimientoBancarioSubdiarioEgreso movBaja = new MovimientoBancarioSubdiarioEgreso(mov);
					movBaja.setFecha_movimiento(mov.getBaja_fecha());
					mov.setBaja_fecha(null);
					result.add(movBaja);
				}
				result.add(mov);
			}
		} catch (Exception e) {
			_log.error("Error al buscar_movimientos_bcrios_subdiario_egreso", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}
	
	public int grabaArchivoMovBcrios(List<MovimientoBancario> movs, String user, int entidad) throws SQLException {
     	
		int result = 0; 
		Connection con = null;
		CallableStatement stmt = null;
		String queryArch;
		try {
			
			_log.debug("Comienzo a grabar archivo Mov Bcrios");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);
			
			for (Iterator<MovimientoBancario> iterator = movs.iterator(); iterator.hasNext();) {
				MovimientoBancario mov = iterator.next();
				
				queryArch = "{call inserta_movimiento_banco(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
									            		
				stmt = con.prepareCall(queryArch.toString());
				
				int cont=1;

				stmt = con.prepareCall(queryArch.toString());
				stmt.setDate(cont++, new java.sql.Date(mov.getFecha_movimiento()
						.getTime()));
				stmt.setInt(cont++, mov.getTipo_mov().getId_tipo_mov());
				stmt.setInt(cont++, mov.getCta_bcria().getId_cuenta_bcria());
				stmt.setBoolean(cont++, mov.isDeb_cred());
				stmt.setNull(cont++, Types.INTEGER);
				if (mov.getChequera()==null || mov.getChequera().getId_chequera() == 0) {
					stmt.setNull(cont++, Types.INTEGER);
				} else {
					stmt.setInt(cont++, mov.getChequera().getId_chequera() != 0 ? mov
							.getChequera().getId_chequera() : null);
				}
				if (mov.getNro_comprobante() == null
						|| mov.getNro_comprobante().trim().equals("")) {
					stmt.setNull(cont++, Types.VARCHAR);
				} else {
					stmt.setString(cont++, mov.getNro_comprobante());
				}
				stmt.setDate(cont++, new java.sql.Date(mov.getFecha_comprobante()
						.getTime()));
				stmt.setDouble(cont++, mov.getImporte().doubleValue());
				stmt.setString(cont++, mov.getDescripcion());
				stmt.setBoolean(cont++, mov.isImprime_cheque());
				stmt.setBoolean(cont++, mov.isNo_a_la_orden());			
				
				if(entidad==WebKeysGlobal.OSPIM && mov.getFechaSur()!=null){
					stmt.setDate(cont++, new java.sql.Date(mov.getFechaSur().getTime()));
				}else if(entidad==WebKeysGlobal.OSPIM){
					stmt.setNull(cont++, Types.DATE);
				}
				
				if(entidad==WebKeysGlobal.OSPIM){
					stmt.setString(cont++, mov.getNroExpedienteSur());
				}
				
				stmt.setString(cont++, user);

				result = stmt.executeUpdate();
			
			}
		
			con.commit();
			_log.debug("archivo Mov Bcrios: commiteado");			
		} catch (SQLException e) {
			_log.error("Error al insertar archivo de Mov Bcrios ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
///////
///////
	
public int grabaArchivoMovBcriosConformados(List<MovimientoBancario> movs,CuentaBancaria ctaBcria,String titulo,String fechaConsulta, String horaConsulta,
		String usuarioConsulta,String parametros,Integer cantidadRegistros, Double saldo, String user, int entidad) throws SQLException, 
		PeriodoArchivoDuplicadoException {
     	
		int result = 0; 
		Connection con = null;
		CallableStatement stmt = null;
		String queryArch;
		try {
			
			_log.debug("Comienzo a grabar archivo Movimientos Bancarios Conformados");
			
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			
// Graba Cabecera			
			Integer id_Lote = 0;
			queryArch = "{call inserta_movimiento_bancario_conformado(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
    		
			MovimientoBancario mv = movs.get(movs.size()-1);
			
			stmt = con.prepareCall(queryArch.toString());
			stmt.setString(1, titulo);
			stmt.setString(2, fechaConsulta);
			stmt.setString(3, horaConsulta);
			stmt.setString(4, usuarioConsulta);
			stmt.setString(5, parametros);
			stmt.setInt(6, cantidadRegistros);
			stmt.setInt(7, movs.size());
			stmt.setInt(8, ctaBcria.getId_cuenta_bcria());
			stmt.setDate(9, new java.sql.Date(mv.getFecha_movimiento().getTime()) );
			stmt.setDouble(10, saldo);
			stmt.setString(11, user);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_Lote = rs.getInt(1);
			}
			
			queryArch = "{call inserta_movimiento_bancario_conformado_detalle(?, ?, ?, ?, ?, ?, ?,?) }";
			stmt = con.prepareCall(queryArch.toString());
			
			for(MovimientoBancario m:movs){
				stmt.setInt(1, id_Lote );
				stmt.setInt(2,  ctaBcria.getId_cuenta_bcria() );
				stmt.setDate(3,new java.sql.Date(m.getFecha_movimiento().getTime()));
				stmt.setDate(4, new java.sql.Date(m.getFecha_comprobante().getTime()));
				stmt.setDouble(5,m.getImporte().doubleValue());
				stmt.setString(6,m.getNro_comprobante());
				stmt.setString(7, m.getDescripcion());
				stmt.setString(8, user);
				result = stmt.executeUpdate();
				
			}
		
			con.commit();
			_log.debug("archivo Movimientos Bancarios Conformados: commiteado");			
		} catch (SQLException e) {
			if(e.getMessage().contains("duplicate key value violates unique constraint") ||
					e.getMessage().contains("llave duplicada viola restricción de unicidad")){
				throw new PeriodoArchivoDuplicadoException("Existen registros ya ingresados de este archivo");
			}
			_log.error("Error al insertar archivo de Movimientos Bancarios Conformados ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	
	
}

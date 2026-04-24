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

import org.objectweb.asm.Type;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Estado;
import ar.com.ospim.liquidaciones.ChequeSinChequeraException;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.Chequera;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="ChequeServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class ChequeServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ChequeServiceImpl.class);

	private static ChequeServiceImpl instance = null;

	public static ChequeServiceImpl getInstance() {
		if (null == instance) {
			instance = new ChequeServiceImpl();
		}
		return instance;
	}

	public List<Cheque> getCheques(String cuit, BigDecimal numero,
			Integer idBanco, BigDecimal importe, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Cheque> listaCheques = null;
		try {
			String sql = "{call buscar_cheques(?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_cheques_amtima(?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_cheques_uoma(?, ?, ?, ?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=cuit&& !cuit.trim().equals("")){
				stmt.setString(1, cuit);
			}else{
				stmt.setNull(1,Types.VARCHAR);
			}
			
			if(null!=numero && !numero.equals(BigDecimal.ZERO)){
				stmt.setBigDecimal(2, numero);
			}else{
				stmt.setNull(2, Types.NUMERIC);
			}
			if (idBanco == null) {
				stmt.setNull(3, Type.INT);
			} else {
				stmt.setInt(3, idBanco.intValue());
			}
			if(null!=importe && importe.compareTo(BigDecimal.ZERO)!=0){
				stmt.setBigDecimal(4, importe);
			}else{
				stmt.setNull(4, Types.NUMERIC);
			}

			ResultSet rs = stmt.executeQuery();
			listaCheques = new ArrayList<Cheque>();
			while (rs.next()) {
				Cheque chq = Cheque.getMapping(rs, "ch__");
				chq.setEstado(Cheque.Estado.getMapping(rs, "es__"));								
				if(entidad==WebKeysGlobal.UOMA && rs.getString("id_orden_pago") !=null){					
					CuentaBancaria cta=new CuentaBancaria(chq.getCuentaBancaria().getId_cuenta_bcria(),rs.getString("cuenta"));
					cta.setBanco(chq.getCuentaBancaria().getBanco());
					chq.setCuentaBancaria(cta);
					chq.setIdOp(rs.getInt("id_orden_pago"));
					
				}	
				chq.setNroRecibo(rs.getString("nro_recibo"));
				chq.setFechaRecibo(rs.getDate("fecha_recibo"));
				if(null==chq.getBanco()){
					chq.setBanco(Banco.getMapping(rs, "ba__"));
				}
				listaCheques.add(chq);
			}
		} catch (Exception e) {
			_log.error("Error al buscar cheques", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCheques;
	}
	
	public static Cheque getChequePorCuitBancoCtaBancariaNro(Cheque ch, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
	    Cheque result = null;
		try {
			String sql = "{call buscar_cheque(?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_cheque_amtima(?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_cheque_uoma(?, ?, ?, ?, ?)}";
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, ch.getCuit());
			stmt.setInt(2, ch.getBanco().getId_banco());
			stmt.setInt(3, ch.getCuentaBancaria().getId_cuenta_bcria());
			stmt.setString(4, ch.getCuentaBancaria().getDescripcion());
			stmt.setBigDecimal(5, ch.getNumero());

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				result = Cheque.getMapping(rs, "ch__");
				result.setEstado(Cheque.Estado.getMapping(rs, "es__"));												
				result.setCuentaBancaria(new CuentaBancaria(rs.getString("cuenta")));
//				result.setIdOp(rs.getInt("id_orden_pago"));
				result.setNroRecibo(rs.getString("nro_recibo"));
				result.setFechaRecibo(rs.getDate("fecha_recibo"));
				result.setBanco(Banco.getMapping(rs, "ba__"));
			}
		} catch (Exception e) {
			_log.error("Error al buscar cheques x cuit, banco, cta bancaria y nro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
	
	public void save(String cuit, BigDecimal numero, BigDecimal importe,
			String getaNombreDe, Date fecha, String usuario, boolean prestador,
			String concepto, Integer idCtaBcria, Cheque.Tipo debitoCredito,
			int idBanco, Cheque.Estado estado, Connection connectionParam,
			int entidad) throws DuplicateNumeroChequeException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_cheques(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_cheques_amtima(?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_cheques_uoma(?,?,?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.ESTUDIO) {
				sql = "{call insertar_cheques_estudio(?,?,?,?,?,?,?,?,?,?,?,?)}";
			}
			if (connectionParam == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParam;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, numero);
			stmt.setString(2, cuit);
			stmt.setString(3, getaNombreDe);
			stmt.setDate(4, new java.sql.Date(fecha.getTime()));
			stmt.setBigDecimal(5, importe);
			stmt.setString(6, usuario);
			stmt.setBoolean(7, prestador);
			stmt.setString(8, concepto);
			if (idCtaBcria == null) {
				stmt.setNull(9, Type.INT);
			} else {
				stmt.setInt(9, idCtaBcria.intValue());
			}
			stmt.setString(10, debitoCredito != null ? debitoCredito.toString()
					: "C");
			stmt.setInt(11, idBanco);
			stmt.setInt(12, estado.getId());
			
			if (entidad == WebKeysGlobal.OSPIM) {
				stmt.setDate(13, new java.sql.Date(fecha.getTime()));
			}
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al insertar cheque", e);
			
			if (connectionParam != null) {
				ConnectionHelper.rollback(con);
			}
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateNumeroChequeException(new Cheque(numero,
						idBanco));
			} else {
				throw new SystemException(e);
			}
		} finally {
			if (connectionParam == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void update(String cuit, BigDecimal numero, BigDecimal importe,
			String getaNombreDe, Date fecha, String usuario, boolean prestador,
			String concepto, Integer idCtaBcria, Cheque.Tipo debitoCredito,
			int idBanco, Cheque.Estado estado, Connection connectionParam,
			int entidad) throws DuplicateNumeroChequeException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call update_cheques(?,?,?,?,?,?,?,?,?,?,?)}";

			if (connectionParam == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParam;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, numero);
			stmt.setString(2, cuit);
			stmt.setString(3, getaNombreDe);
			stmt.setDate(4, new java.sql.Date(fecha.getTime()));
			stmt.setBigDecimal(5, importe);
			stmt.setString(6, usuario);
			stmt.setBoolean(7, prestador);
			stmt.setString(8, concepto);
			if (idCtaBcria == null) {
				stmt.setNull(9, Type.INT);
			} else {
				stmt.setInt(9, idCtaBcria.intValue());
			}
			stmt.setString(10, debitoCredito.toString());
			stmt.setInt(11, idBanco);
			// stmt.setInt(12,
			// estado!=null?estado.getId():Cheque.Estado.RECIBIDO);
			stmt.executeUpdate();
/*			
			if (connectionParam == null) {
				con.commit();
			}
*/			
		} catch (SQLException e) {
			_log.error("Error al insertar cheque", e);

			/*
			if (connectionParam == null) {
				ConnectionHelper.rollback(con);
			}
			*/
			
			if (connectionParam != null) {
				ConnectionHelper.rollback(con);
			}
			
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateNumeroChequeException(new Cheque(numero,
						idBanco));
			} else {
				throw new SystemException(e);
			}
		} finally {
			if (connectionParam == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public void anularcheque(Cheque ch, Date fecha, String usr,
			int entidad) throws SystemException {
		anularcheque(ch, fecha, usr, null, entidad);
	}

	public void anularcheque(Cheque ch, Date fecha, String usr,
			Connection connectionParameter, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter != null) {
				con = connectionParameter;
			} else {
				con = ConnectionHelper.getConnection();
//				con.setAutoCommit(false);
			}
			String sql = "{call anular_cheque(?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call anular_cheque_amtima(?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.anular_cheque_uoma(?, ?, ?, ?, ?)}";
			}
			_log.debug("obteniendo conexion");
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, ch.getNumero());
			stmt.setInt(2, ch.getBanco().getId_banco());
			stmt.setInt(3, ch.getCuentaBancaria().getId_cuenta_bcria());
			stmt.setDate(4, new java.sql.Date(fecha.getTime()));
			stmt.setString(5, usr);
			
			stmt.executeUpdate();
/*			
			if (connectionParameter == null) {
				con.commit();
			}
*/			
		} catch (Exception e) {
			_log.error("Error al anular cheque", e);
//			ConnectionHelper.rollback(con);
			
			if (connectionParameter != null) {
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

	public List<Cheque.Estado> getChequeEstados() {
		Connection con = null;
		CallableStatement stmt = null;
		List<Cheque.Estado> list = new ArrayList<Cheque.Estado>();
		try {
			String sql = "{call trae_cheque_estados()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Cheque.Estado.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer estados cheque", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public void cambiarEstadoCheque(Cheque cheque, Estado estado,
			String userName, Connection connectionParameter, int entidad)
			throws SystemException {
		_log.debug("Cambiando estado cheque");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
//				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call cambiar_estado_cheque_amtima (?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call cambiar_estado_cheque (?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.cambiar_estado_cheque_uoma (?, ?, ?, ?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, cheque.getNumero());
			stmt.setInt(2, cheque.getBanco().getId_banco());
			stmt.setInt(3, estado.getId());
			stmt.setString(4, userName);
			String idCtaBcria=TraeListasServiceUtil.getSystemConfig("HOTELES_BCO_CTA_BCRIA_CTA_CTE");
			if(cheque.getCuentaBancaria() !=null && (cheque.getCuentaBancaria().getId_cuenta_bcria() > 0 ||
					cheque.getCuentaBancaria().getId_cuenta_bcria()==Integer.parseInt(idCtaBcria))  //Cuenta Especial para procesar cheques recibos para cancelar Cuenta Corriente
					                                                        //Hoteles en el sistema Venica   
			   ){
				stmt.setInt(5, cheque.getCuentaBancaria().getId_cuenta_bcria());
			}else{
				stmt.setNull(5, Types.INTEGER);
			}

			stmt.executeUpdate();
/*
			if (connectionParameter == null) {
				con.commit();
			}
*/			
		} catch (SQLException e) {
			_log.error("Error al cambiar estado cheque", e);
/*			
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
*/			
			if (connectionParameter != null) {
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

	public void update(Cheque cheque, String user, Connection connectionParam)
			throws SystemException {
		_log.debug("Actualizando cheque");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_cheque(?,?,?,?,?,?,?,?,?,?,?,?)}";
			if (connectionParam == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParam;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, cheque.getNumero());
			stmt.setString(2, cheque.getCuit());
			stmt.setString(3, cheque.getANombreDe());
			stmt.setDate(4, new java.sql.Date(cheque.getFecha().getTime()));
			stmt.setBigDecimal(5, cheque.getImporte());
			stmt.setString(6, user);
			stmt.setBoolean(7, cheque.isPrestador());
			stmt.setString(8, cheque.getConcepto());
			if (cheque.getCuentaBancaria() == null
					|| cheque.getCuentaBancaria().getId_cuenta_bcria() == 0) {
				stmt.setNull(9, Type.INT);
			} else {
				stmt.setInt(9, cheque.getCuentaBancaria().getId_cuenta_bcria());
			}
			stmt.setString(10, cheque.getDebitoCredito().toString());
			stmt.setInt(11, cheque.getBanco().getId_banco());
			stmt.setInt(12, cheque.getEstado().getId());
			stmt.executeUpdate();
/*			
			if (connectionParam == null) {
				con.commit();
			}
*/			
		} catch (SQLException e) {
			_log.error("Error al actualizar cheque", e);
/*			
			if (connectionParam == null) {
				ConnectionHelper.rollback(con);
			}
*/			
			if (connectionParam != null) {
				ConnectionHelper.rollback(con);
			}
			throw new SystemException(e);
		} finally {
			if (connectionParam == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

	public List<Cheque> getChequesRecibidos(int entidad) throws SystemException {
		Estado estado = new Estado();
		estado.setId(Cheque.Estado.RECIBIDO);
		return getChequesEstado(estado, entidad);
	}

	public List<Cheque> getChequesEstado(Cheque.Estado estado, int entidad)
			throws SystemException {
		_log.debug("buscando cheques");
		Connection con = null;
		CallableStatement stmt = null;
		List<Cheque> listaCheques = null;
		String sql = null;
		try {
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_cheques_estado_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_cheques_estado_uoma(?)}";
			} else {
				sql = "{call buscar_cheques_estado(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, estado.getId());
			ResultSet rs = stmt.executeQuery();
			listaCheques = new ArrayList<Cheque>();
			while (rs.next()) {
				Cheque chq = Cheque.getMapping(rs, "ch__");
				chq.setEstado(Cheque.Estado.getMapping(rs, "es__"));
				chq.setBanco(Banco.getMapping(rs, "ba__"));
				chq.setNroRecibo(rs.getString("nro_recibo"));
				chq.setFechaRecibo(rs.getDate("fecha_recibo"));
				
				listaCheques.add(chq);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar cheques", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCheques;
	}

	public List<Cheque> getChequesDepositados(int entidad)
			throws SystemException {
		Estado estado = new Estado();
		estado.setId(Cheque.Estado.DEPOSITADO);
		return getChequesEstado(estado, entidad);
	}

	public List<Cheque> getChequesReutilizables(int entidad)
			throws SystemException {
		_log.debug("buscando cheques");
		Connection con = null;
		CallableStatement stmt = null;
		List<Cheque> listaCheques = null;
		try {
			String sql = "{call buscar_cheques_reutilizables()}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_cheques_amtima_reutilizables()}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_cheques_uoma_reutilizables()}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaCheques = new ArrayList<Cheque>();
			while (rs.next()) {
				Cheque chq = Cheque.getMapping(rs, "ch__");
				chq.setEstado(Cheque.Estado.getMapping(rs, "es__"));
				chq.setBanco(Banco.getMapping(rs, "ba__"));
				listaCheques.add(chq);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar cheques", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCheques;
	}
	
	public List<Chequera> getUltimasChequeras(int entidad)
			throws SystemException {
		_log.debug("buscando chequeras");
		Connection con = null;
		CallableStatement stmt = null;
		List<Chequera> listaCheques = null;
		try {
			String sql = "{call buscar_ultimas_chequeras()}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_ultimas_chequeras_amtima()}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_ultimas_chequeras()}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaCheques = new ArrayList<Chequera>();
			while (rs.next()) {
				Chequera chq = new Chequera(rs.getInt("id"),rs.getString("cuenta"),rs.getInt("desde"),rs.getInt("hasta"),rs.getString("alta_usr"),rs.getDate("alta_fecha"),rs.getInt("id_cta_bcria"));
				
				listaCheques.add(chq);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar chequeras", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCheques;
	}

	public void saveChequera(Chequera chequera, String user, int entidad)
			throws DuplicateNumeroChequeException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_chequera(?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_chequera_amtima(?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_chequera_uoma(?,?,?,?)}";
			}

			con = ConnectionHelper.getConnection();

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, chequera.getIdCtaBcria());
			stmt.setInt(2, chequera.getNroDesde());
			stmt.setInt(3, chequera.getNroHasta());
			stmt.setString(4, user);

			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al insertar cheque", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateNumeroChequeException();
			} else {
				throw new SystemException(e);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public void borrarChequera(int id_chequera, String user, int entidad)
			throws DuplicateNumeroChequeException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_chequera(?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call borrar_chequera_amtima(?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.borrar_chequera_uoma(?,?)}";
			}

			con = ConnectionHelper.getConnection();

			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_chequera);
			stmt.setString(2, user);			
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al borrar cheque", e);			
				throw new SystemException(e);
			
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public boolean validarCheque(Cheque cheque, int entidad)
			throws DuplicateNumeroChequeException, ChequeSinChequeraException, SystemException {
		_log.debug("buscando chequeras");
		Connection con = null;
		CallableStatement stmt = null;		
		try {
			String sql = "{call validar_cheque(?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call validar_cheque_amtima(?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.validar_cheque_uoma(?,?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, cheque.getCuentaBancaria().getId_cuenta_bcria());
			stmt.setBigDecimal(2, cheque.getNumero());
			stmt.executeQuery();		
			
		} catch (SQLException e) {
			_log.error("Error al buscar chequeras", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateNumeroChequeException();
			}else if(e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_CHECK_VIOLATION)){
				throw new ChequeSinChequeraException();
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return true;
	}
	
	
	public void updateDatos(Cheque cheque, String usuario, Connection connectionParam,
			int entidad) throws DuplicateNumeroChequeException, SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			
			String sql =null ;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call update_cheques_datos_amtima(?,?,?,?,?,?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.update_cheques_datos_uoma(?,?,?,?,?,?,?,?,?,?,?)}";
			} else {
				sql = "{call update_cheques_datos(?,?,?,?,?,?,?,?,?,?,?)}";
			}
			

			if (connectionParam == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParam;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1,cheque.getNumero());
			stmt.setString(2,cheque.getCuit());
			stmt.setString(3,cheque.getANombreDe());
			stmt.setDate(4, new java.sql.Date(cheque.getFecha().getTime()));
			stmt.setBigDecimal(5, cheque.getImporte());
			stmt.setString(6, usuario);
			stmt.setBoolean(7,cheque.isPrestador());
			stmt.setString(8,cheque.getConcepto());
			if (cheque.getCuentaBancaria() == null) {
				stmt.setNull(9, Type.INT);
			} else {
				stmt.setInt(9,cheque.getCuentaBancaria().getId_cuenta_bcria());
			}
			stmt.setString(10,cheque.getDebitoCredito().toString());
			stmt.setInt(11, cheque.getBanco().getId_banco());
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al modificar datos cheque", e);

			
			if (connectionParam != null) {
				ConnectionHelper.rollback(con);
			}
			
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateNumeroChequeException(new Cheque(cheque.getNumero(),
						cheque.getBanco().getId_banco()));
			} else {
				throw new SystemException(e);
			}
		} finally {
			if (connectionParam == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else {
				ConnectionHelper.cerrar(stmt);
			}
		}
	}

}

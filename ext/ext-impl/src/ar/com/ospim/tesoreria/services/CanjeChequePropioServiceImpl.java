package ar.com.ospim.tesoreria.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class CanjeChequePropioServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(CanjeChequePropioServiceImpl.class);

	public int save(CanjeChequePropio cpp, User user,
			Connection connectionParameter, int entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call insertar_canje_cheque_propio_amtima(?,?,?,?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call insertar_canje_cheque_propio(?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.insertar_canje_cheque_propio_uoma(?,?,?,?)}";
			}
			
			con = connectionParameter;
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, cpp.getOrdenPago().getId());
			stmt.setInt(2, cpp.getOrdenPagoNueva().getId());
			stmt.setInt(3, cpp.getIdMovimientoBancario());
			stmt.setString(4, user.getScreenName());
			ResultSet executeQuery = stmt.executeQuery();
			executeQuery.next();
			return executeQuery.getInt(1);
		} catch (Exception e) {
			logger.error("Error al insertar canje cheque", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public CanjeChequePropio get(int id, int entidad) throws Exception {
		Connection con = ConnectionHelper.getConnection();
		CallableStatement stmt = null;
		String sql = null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call buscar_canje_cheques_propios_amtima(?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call buscar_canje_cheques_propios(?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_canje_cheques_propios_uoma(?)}";
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet executeQuery = stmt.executeQuery();
			if (executeQuery.next()) {
				return CanjeChequePropio.getMapping(executeQuery);
			}
		} catch (Exception e) {
			logger.error("Error al insertar canje cheque", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public void guardarAsociacionCheque(Cheque cheque, boolean nuevo,
			CanjeChequePropio cpp, Connection connectionParameter, int entidad)
			throws Exception {
		String sp = "insertar_canje_cheque_propio_cheque_viejo";
		if (nuevo) {
			sp = "insertar_canje_cheque_propio_cheque_nuevo";
		}
		if(entidad==WebKeysGlobal.AMTIMA){
			sp=sp+"_amtima";
		}else if(entidad==WebKeysGlobal.UOMA){
			sp="uoma."+sp+"_uoma";
		}
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call " + sp + "(?, ?, ?, ?)}";
			con = connectionParameter;
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, cpp.getId());
			stmt.setBigDecimal(2, cheque.getNumero());
			stmt.setInt(3, cheque.getBanco().getId_banco());
			stmt.setInt(4, cheque.getCuentaBancaria().getId_cuenta_bcria());  
			stmt.executeUpdate();
			
		} catch (Exception e) {
			logger.error("Error al asociar cheque", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public List<Cheque> getChequesNuevos(int id, int entidad) throws SystemException {
		return getCheques(id, "buscar_canje_cheques_nuevos", entidad);
	}

	public List<Cheque> getChequesViejos(int id, int entidad) throws SystemException {
		return getCheques(id, "buscar_canje_cheques_viejos", entidad);
	}

	public List<Cheque> getCheques(int id, String sp, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Cheque> listaCheques = null;
		String sql =null;
		try {			
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call " + sp+"_amtima"+ "(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma." + sp+"_uoma"+ "(?)}";
			}else{
				sql = "{call " + sp+"(?)}";
			}
			
			con = ConnectionHelper.getConnection();
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
			logger.error("error al buscar Cheques de canje", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCheques;
	}

	public List<CanjeChequePropio> buscar(Date fechaIni, Date fechaFin,
			BigDecimal chequeNuevo, BigDecimal chequeCanjeado,
			Integer op_generada, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<CanjeChequePropio> listaCheques = null;
		String sql =  "{call buscar_canje_cheques_propios_params(?, ?, ?, ?, ?)}";
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call buscar_canje_cheques_propios_params_amtima(?, ?, ?, ?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_canje_cheques_propios_params_uoma(?, ?, ?, ?, ?)}";	
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (fechaIni == null) {
				stmt.setDate(1, null);
			} else {
				stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			}
			if (fechaFin == null) {
				stmt.setDate(2, null);
			} else {
				stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			}
			stmt.setBigDecimal(3, chequeNuevo);
			stmt.setBigDecimal(4, chequeCanjeado);
			if (op_generada == null) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, op_generada);
			}
			ResultSet rs = stmt.executeQuery();
			listaCheques = new ArrayList<CanjeChequePropio>();
			while (rs.next()) {
				listaCheques.add(CanjeChequePropio.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("error al buscar Cheques de canje", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCheques;
	}

	public void anular(Integer id, String usuario,
			Connection connectionParameter, int entidad) throws SystemException {
		CallableStatement stmt = null;
		String sql = null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call anular_canje_cheques_propios_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call anular_canje_cheques_propios(?, ?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.anular_canje_cheques_propios_uoma(?, ?)}";
			}
			
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, usuario);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.error("error al anular canje", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}
}

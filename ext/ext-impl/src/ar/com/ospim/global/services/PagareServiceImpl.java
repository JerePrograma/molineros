package ar.com.ospim.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Estado;
import ar.com.ospim.global.beans.Pagare;
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
public class PagareServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(PagareServiceImpl.class);

	private static PagareServiceImpl instance = null;

	public static PagareServiceImpl getInstance() {
		if (null == instance) {
			instance = new PagareServiceImpl();
		}
		return instance;
	}
	
	public List<Pagare.Estado> getPagareEstados() {
		Connection con = null;
		CallableStatement stmt = null;
		List<Pagare.Estado> list = new ArrayList<Pagare.Estado>();
		try {
			String sql = "{call trae_cheque_estados()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Pagare.Estado.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer estados cheque", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
}

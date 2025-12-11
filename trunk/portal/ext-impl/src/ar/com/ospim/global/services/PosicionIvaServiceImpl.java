package ar.com.ospim.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.global.beans.PosicionIva;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="PosicionIvaImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class PosicionIvaServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(PosicionIvaServiceImpl.class);

	private static PosicionIvaServiceImpl instance = null;

	public static PosicionIvaServiceImpl getInstance() {
		if (null == instance) {
			instance = new PosicionIvaServiceImpl();
		}
		return instance;
	}

	public List<PosicionIva> getPosicionesIva() throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<PosicionIva> listaPosiciones = null;
		try {
			String sql = "{call trae_posicion_iva()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			listaPosiciones = new ArrayList<PosicionIva>();
			while (rs.next()) {
				PosicionIva pos = PosicionIva.getMapping(rs);
				listaPosiciones.add(pos);
			}
		} catch (Exception e) {
			_log.error("Error al buscar posiciones iva", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPosiciones;
	}
}

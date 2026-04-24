package ar.com.ospim.login.coordenadas.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import ar.com.ospim.login.coordenadas.beans.TarjetaCoordenadas;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class CoordenadasServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(CoordenadasServiceImpl.class);

	public TarjetaCoordenadas getTarjetaCoordenadasUsuario(long userid)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		String sql = null;
		TarjetaCoordenadas tarjeta = null;
		try {

			sql = "{call ingreso_externo.buscarTarjetaCoordenadasUsuario(?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setLong(1, userid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tarjeta = new TarjetaCoordenadas(rs.getString("coordenadas"),
						rs.getInt("id"), rs.getString("ip_sin_coord"));
			}

			return tarjeta;
		} catch (Exception e) {
			logger.error("Error al buscar tarjeta", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
	}

}

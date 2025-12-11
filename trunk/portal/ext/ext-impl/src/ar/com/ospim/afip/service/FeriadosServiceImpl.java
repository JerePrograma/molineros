package ar.com.ospim.afip.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.global.beans.Feriado;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class FeriadosServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(FeriadosServiceImpl.class);

	private static FeriadosServiceImpl instance = null;

	public static FeriadosServiceImpl getInstance() {
		if (null == instance) {
			instance = new FeriadosServiceImpl();
		}
		return instance;
	}

	public List<Feriado> findAllFeriados() {
		Connection con = null;
		CallableStatement stmt = null;
		List<Feriado> repo = null;
		try {
			String sql = "{call buscar_feriados()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			repo = new ArrayList<Feriado>();
			while (rs.next()) {
				repo.add(new Feriado(rs.getDate("feriado")));
			}
		} catch (Exception e) {
			_log.error("Error al buscar_feriados", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return repo;
	}
}

package ar.com.ospim.tesoreria.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.tesoreria.beans.Inspector;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class InspectorServiceImpl {

	
	private static Log _log = LogFactoryUtil.getLog(InspectorServiceImpl.class);

	private static InspectorServiceImpl instance = null;

	public static InspectorServiceImpl getInstance() {
		if (null == instance) {
			instance = new InspectorServiceImpl();
		}
		return instance;
	}


	public List<Inspector> getInspectores() {
		Connection con = null;
		CallableStatement stmt = null;
		List<Inspector> listaInspectores = null;
		try {
			String sql = "{call trae_inspectores()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaInspectores = new ArrayList<Inspector>();
			while (rs.next()) {
				Inspector ins = Inspector.getMapping(rs);
				listaInspectores.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer inspectores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaInspectores;
	}


}

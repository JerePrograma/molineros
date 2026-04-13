package ar.com.cgt.ddhh.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ar.com.cgt.ddhh.beans.TemasNormasDDHH;
import ar.com.cgt.ddhh.beans.TiposNormasDDHH;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


public class TraeListasServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(TraeListasServiceImpl.class);
	
	public List<TemasNormasDDHH> getTemasNormasDDHH() {
		Connection con = null;
		List<TemasNormasDDHH> listaTemas = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_temas_normas_ddhh()}";
			con = ConnectionHelper.getConnectionCGT();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaTemas = new ArrayList<TemasNormasDDHH>();
			while (rs.next()) {
				TemasNormasDDHH tndh = TemasNormasDDHH.getMapping(rs);
				listaTemas.add( tndh );
			}
		} catch (Exception e) {
			_log.debug("error al traer Temas Normas DDHH", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTemas;
	}
	
	public List<TiposNormasDDHH> getTiposNormasDDHH(String sistema) {
		Connection con = null;
		List<TiposNormasDDHH> listaTipos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tipos_normas_ddhh(?)}";
			con = ConnectionHelper.getConnectionCGT();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, sistema);
			ResultSet rs = stmt.executeQuery();
			listaTipos = new ArrayList<TiposNormasDDHH>();
			while (rs.next()) {
				TiposNormasDDHH tndh = TiposNormasDDHH.getMapping(rs);
				listaTipos.add( tndh );
			}
		} catch (Exception e) {
			_log.debug("error al traer Tipos Normas DDHH", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTipos;
	}
	
}
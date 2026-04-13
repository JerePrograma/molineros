package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.liquidaciones.beans.Catastro;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="CatastroServiceImpl"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 *
 */
public class CatastroServiceImpl {
	
	private static Log _log = LogFactoryUtil.getLog(CatastroServiceImpl.class);
	
	public List<Catastro> buscaCatastro(String cuil_titular, int inte) throws SQLException{
		Connection con = null;
		CallableStatement stmt=null;
		List<Catastro> catastros=null;
		try {		
			
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			String sqlList = "{call trae_catastro_cuil_inte(?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			catastros = new ArrayList<Catastro>();
			while (rs.next()) {
				Catastro catastro = Catastro.getMapping(rs, "c_");
				catastros.add(catastro);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return catastros;
	}
	
	public int save(Catastro catastro, String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_catastro_item(?,?,?,?,?,?,?,?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, catastro.getAfiliado().getCuil_titular());
			stmt.setInt(2, catastro.getAfiliado().getInte());
			stmt.setDate(3, new java.sql.Date(catastro.getFecha_prestacion().getTime()));			
			stmt.setInt(4, catastro.getPlan_prestacion().getNomenclador().getId_prestacion());
			stmt.setString(5, catastro.getCodigo());
			stmt.setString(6, catastro.getPieza());
			stmt.setString(7, catastro.getCara());
			stmt.setString(8, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el item de catastro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public int delete(int id, String user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {			 
			String sql = "{call borrar_catastro_item(?,?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, user);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("borrar el item de catastro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
}

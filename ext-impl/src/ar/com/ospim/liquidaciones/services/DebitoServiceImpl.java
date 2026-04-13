package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.global.beans.ComprobanteItem;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="DebitoServiceImpl"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 *
 */
public class DebitoServiceImpl {
	
	private static Log _log = LogFactoryUtil.getLog(DebitoServiceImpl.class);
		
	public List<ComprobanteItem> buscaDebitos(int id_liquidacion) throws SQLException{
		Connection con = null;		
		CallableStatement stmt=null;
		List<ComprobanteItem> debitos=null;
		try {			
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();			
			String sqlList = "{call trae_debitos_id_liquidacion(?)}";
			stmt = con.prepareCall(sqlList.toString());					
			stmt.setInt(1, id_liquidacion);
			ResultSet rs =stmt.executeQuery();
			debitos=new ArrayList<ComprobanteItem>();
			while (rs.next()) {
				ComprobanteItem comp = ComprobanteItem.getMapping(rs, "ci_");
				debitos.add(comp);
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return debitos;
	}
}

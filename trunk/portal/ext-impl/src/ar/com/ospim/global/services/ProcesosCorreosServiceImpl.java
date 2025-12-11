package ar.com.ospim.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.global.beans.DestinatarioPorProceso;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * 
 * @author SVA
 * 
 */

public class ProcesosCorreosServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(ProcesosCorreosServiceImpl.class);

	public List<DestinatarioPorProceso> getDestinatariosInformadosPorProceso(int idProceso) {
		Connection con = null;
		List<DestinatarioPorProceso> destinatarios = new ArrayList<DestinatarioPorProceso>();
		DestinatarioPorProceso dp = null;
		
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_emails_por_proceso(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idProceso);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				dp = DestinatarioPorProceso.getMapping("",rs);
				destinatarios.add(dp);
			}
		} catch (Exception e) {
			_log.debug("error al correos por proceso", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return destinatarios;
	}
}
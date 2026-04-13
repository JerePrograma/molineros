package ar.com.global.services;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLXML;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.global.beans.MensajeXMLBase;
import ar.com.ospim.util.ConnectionHelper;

public class MensajeriaXMLServiceImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6479557173792209854L;
	private static Log _log = LogFactoryUtil.getLog(MensajeriaXMLServiceImpl.class);

	public void guardarMensajeXML(MensajeXMLBase mensaje) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;

		try {

			con = ConnectionHelper.getConnection();
			
			_log.debug("Guardando mensaje xml " + mensaje.getServicio() + " id: " + mensaje.getIdReferencia());
			
			String sql = "{call public.guardar_mensaje_xml(?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, mensaje.getServicio());
			stmt.setInt(2, mensaje.getIdReferencia());
			stmt.setString(3, mensaje.getMensaje());
				
			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error("Error al guardar mensaje xml", e);
			throw new SystemException(e);
		} finally {

			ConnectionHelper.cerrar(stmt, con);

		}
	}
	
}
